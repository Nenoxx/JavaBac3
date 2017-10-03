#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>

#include "tcplib.h"
#include "csvlib.h"
#include "cimp.h"

int SocketEcoute;
int SocketService[20];
int pool = 0;
char *sepReq;
char *sepData;

void* ThreadTraitementMsg(void* param);

pthread_cond_t condConnexion;
int connexionDispo=0;
pthread_mutex_t mutexConnexion;

int main()
{
	struct sockaddr_in SocketAddress; // contient port et ip de la socket
	struct hostent *CurrentHost; // infos sur la machine
	struct in_addr adrIP; // addresse ip de la machine
	int retour=0, i=0;

	//Vérifie si le fichier de config existe, le crée sinon
	CreateCheckinConfig();
	CreateLoginFile();
	sepReq = getProperty("separateur_CIMP");
	sepData = getProperty("separateur_fichier");
	
	// Création du pool de threads
	pool = atoi(getProperty("nb_threads")); // récup du fichier de config
	pthread_t *thread;
	thread =(pthread_t *) malloc(pool * sizeof(int));
	for(int i=0; i<pool; i++){
		pthread_create((thread+i), NULL, ThreadTraitementMsg, (void *)i+1);
		pthread_detach(*(thread+i));
	}
	
	for(int j=0; j<pool; j++)
	{
		SocketService[j] = -1;
	}
	//initialisation mutex et var de condition
	pthread_mutex_init(&mutexConnexion, NULL);
	pthread_cond_init(&condConnexion, NULL);
	
	//1) Création de la socket
	SocketEcoute = CreateSocket(SocketEcoute);
	if(SocketEcoute <= 0)
	{
		printf("SER-> Pas de socket\n");
		exit(1);
	}
	printf("SER> SocketEcoute = %d\n", SocketEcoute);
	
	//2) Informations sur l'ordinateut local
	CurrentHost = getLocalHost();
	memcpy(&adrIP, CurrentHost->h_addr, CurrentHost->h_length); // récup l'adresse ip de l'hote
	printf("SER> getLocalHost: OK -- IP Address : %s\n", inet_ntoa(adrIP));
	
	//3) Préparation de la struct sockaddr_in
	SocketAddress = initSocketAddress(SocketAddress, CurrentHost, "port_service");		
	printf("SER> Struct SocketAddress initialisée (ip: %s port: %d)\n", inet_ntoa(adrIP), SocketAddress.sin_port);
	
	//4) Bind
	ConnectSocket(SocketEcoute, SocketAddress, CurrentHost);
	printf("SER> Bind ok\n");
	
	while(1){

		//5) Listen
		SocketWait(SocketEcoute);
		printf("SER> Listen ok\n");
		
		i=0;
		while(SocketService[i] != -1){
			i++;
		}
		
		//6) Accept
		SocketService[i] = GetClient(SocketEcoute, SocketAddress); //Duplication de socket
		printf("SER> Accept ok\n");
		
		if(i == pool){
			printf("SER> Plus de connexions disponibles\n");
			SocketSendReqEOM(SocketService[i], NOK, sepReq, "Trop de clients connectés");
			CloseSocket(SocketService[i]);
		}
		else{
			printf("SER> Socket num:%d libre\n", i);
			SocketSendReqEOM(SocketService[i], OK, sepReq, "vous êtes connecté");
			//7) signaler un thread d'une nouvelle connexion
			pthread_mutex_lock(&mutexConnexion);
			connexionDispo++;
			pthread_mutex_unlock(&mutexConnexion);
			pthread_cond_signal(&condConnexion);
		}
	}
	return 0;
}

//-----------------------------------------------------------------------------
/*
Thread de traitement de requêtes
*/
void* ThreadTraitementMsg(void * param)
{
	int Socket, i, taille=2000, connecte=0, requete;
	int numTh = *((int*)(&param));
	int loginOk, trouve, volEnCours;
	char msg[TAILLE_MSG], numeroVol[10];
	printf("SER(th%d)> thread créé\n", numTh);
	
	while(1){
	
		loginOk = 0; //pas de client authentifié
		volEnCours = 0;
	
		// Attente d'un client à traiter
		pthread_mutex_lock(&mutexConnexion);
		while(connexionDispo == 0){
			pthread_cond_wait(&condConnexion, &mutexConnexion); // attend d'être réveillé
		}
		connexionDispo--; // le thread "prend" la connexion en charge
		i=0; // cherche socket libre
		while(SocketService[i] == -1){
			i++;
		}
		Socket = SocketService[i]; // le thread travaille sur Socket
		connecte = 1; // un client est connecté, on traite ses requetes
		pthread_mutex_unlock(&mutexConnexion);
		
		printf("SER(th%d)> Nouveau client\n", numTh);
		
		while(connecte){
			
			SocketRcvEOM(Socket, msg, taille);// recoit un message et enlève car de fin de chaine

			requete = getRequest(msg); // récupère le type de requête
		
			switch(requete)
			{	
				case LOGIN_OFFICER: //[LOGIN_OFFICER|login;password]
					{
						//printf("Entrée login officer\n");
						char login[30], password[30], tmp[30], *pLogin, *pPassword;
						//Extraction des données du message reçu.
						pLogin = strtok(msg, sepData);
						pPassword = strtok(NULL, sepData);
						strcpy(login, pLogin);
						strcpy(password,pPassword);

						//Vérification dans le fichier
						trouve = FetchRow(login, tmp, "login.csv");

						if(trouve){
							SocketSendReqEOM(Socket, OK, sepReq, msg);
							loginOk = 1;
						}
						else{
							SocketSendReqEOM(Socket, NOK, sepReq, msg);
							loginOk = 0;
						}
						break;
					}
				
				case LOGOUT_OFFICER: //[LOGOUT_OFFICER|data]
					{
						if(loginOk){
							SocketSendReqEOM(Socket, OK, sepReq, "");
							CloseSocket(Socket);
							connecte = 0;
							loginOk = 0;	
						}
						else
							printf("SER(th%d)> Erreur: recu logout sans login\n", numTh);	
						break;
					}
				
				case NUM_VOL: //[NUM_VOL|numeroVol]
					{
						// chercher dans fichier vols.csv pour numeroVol
						trouve = FetchRow(msg, numeroVol, getProperty("fichier_vols"));
						if(trouve)
						{
							SocketSendReqEOM(Socket, OK, sepReq, numeroVol);
							volEnCours = atoi(numeroVol);
						}
						else
						{
							SocketSendReqEOM(Socket, NOK, sepReq, numeroVol);
							volEnCours = 0;
						}
						break;
					}
				
				case NUM_BILLET: //[NUM_BILLET|numerodebillet(=numvol+numbillet);nbAccompagnants]
					{
						//si vol != 0
							// chercher dans fichier VOL[numeroVol].csv pour numeroBillet
							// si trouvé -> [OK|numerodebillet]
							
							// sinon -> [NOK|numerodebillet]
							
						//sinon -> [NOK|"pas de vol sélectionné"]
						break;
					}
					
				case NBR_PASS: //[NBR_PASS|nbrpassagers]
					{
						break;
					}
					
				case BAGAGES: //
					{
						break;
					}
					
				case CHECK_LUGGAGE: //[CHECK_LUGGAGE|poid1;poids2;...]
					{
						
						break;
					}
					
				default: 
					SocketSendReqEOM(Socket, EOC, sepReq, "requete inconnue");
			}
		}// boucle tant que connecté
		
		//Fin du traitement
		pthread_mutex_lock(&mutexConnexion);
		SocketService[i]=-1; // libère le tableau de sockets
		pthread_mutex_unlock(&mutexConnexion);
		
	}// boucle infinie thread
}//fin thread

