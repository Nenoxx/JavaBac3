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
char fSer[]="checkin.config";
float prixSuppl;
float poidsMax;

void* ThreadTraitementMsg(void* param);

pthread_cond_t condConnexion;
int connexionDispo=-1;
pthread_mutex_t mutexConnexion, mutexFichier, mutexPoidsPrix;

int main()
{
	struct sockaddr_in SocketAddress; // contient port et ip de la socket
	struct hostent *CurrentHost; // infos sur la machine
	struct in_addr adrIP; // addresse ip de la machine
	int retour=0, i=0;
	char *tmp;

	//Vérifie si le fichier de config existe, le crée sinon
	CreateCheckinConfig();
	sepReq = getProperty(fSer, "separateur_CIMP");
	sepData = getProperty(fSer, "separateur_fichier");
	tmp = getProperty(fSer, "poids_max");
	poidsMax = atof(tmp);
	tmp = getProperty(fSer, "prix_supplement");
	prixSuppl = atof(tmp);
	CreateLoginFile(sepData);
	
	
	// Création du pool de threads
	pool = atoi(getProperty(fSer, "nb_threads")); // récup du fichier de config
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
	pthread_mutex_init(&mutexFichier, NULL);
	pthread_mutex_init(&mutexPoidsPrix, NULL);
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
	CurrentHost = getLocalHost(fSer);
	memcpy(&adrIP, CurrentHost->h_addr, CurrentHost->h_length); // récup l'adresse ip de l'hote
	printf("SER> getLocalHost: OK -- IP Address : %s\n", inet_ntoa(adrIP));
	
	//3) Préparation de la struct sockaddr_in
	SocketAddress = initSocketAddress(SocketAddress, CurrentHost, "port_service", fSer);		
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
		printf("SER> socket[%d]=%d\n",i, SocketService[i]);
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
			connexionDispo=i;
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
	int Socket, connecte=0, requete, payementOk;
	int numTh = *((int*)(&param)), numConn;
	int loginOk, trouve, volEnCours, nbrPass, cpt;
	char msg[TAILLE_MSG], numeroVol[10], fichierVol[100], numeroBillet[100], fichierVolVal[100];
	char poids[10], *p, *valise;
	float total;
	printf("SER[th%d]> thread créé\n", numTh);
	
	while(1){
	
		loginOk = 0; //pas de client authentifié
		volEnCours = 0;
		nbrPass = 0;
		payementOk = 0;
	
		// Attente d'un client à traiter
		pthread_mutex_lock(&mutexConnexion);
		while(connexionDispo == -1){
			pthread_cond_wait(&condConnexion, &mutexConnexion); // attend d'être réveillé
		}
	
		numConn = connexionDispo;
		Socket = SocketService[numConn]; // le thread travaille sur Socket
		connecte = 1; // un client est connecté, on traite ses requetes
		connexionDispo=-1; // le thread "prend" la connexion en charge
		pthread_mutex_unlock(&mutexConnexion);
		
		printf("SER[th%d]> Nouveau client sur socket %d\n", numTh,Socket);
		
		while(connecte){
			strcpy(msg, "\0");
			SocketRcvEOM(Socket, msg, TAILLE_MSG);// recoit un message et enlève car de fin de chaine
			printf("SER[th%d]> recu [%s]\n",numTh, msg);
			requete = getRequest(sepReq, msg); // récupère le type de requête
		
			switch(requete)
			{	
				case LOGIN_OFFICER: //[LOGIN_OFFICER|login;password]
					{
						printf("SER[th%d]> Requête LOGIN_OFFICER\n", numTh);
						char login[30], password[30], tmp[30], *pLogin, *pPassword;
						//Extraction des données du message reçu.
						pLogin = strtok(msg, sepData);
						pPassword = strtok(NULL, sepData);
						strcpy(login, pLogin);
						strcpy(password,pPassword);

						//Vérification dans le fichier
						pthread_mutex_lock(&mutexFichier);
						trouve = FetchRow(login, tmp, "login.csv", sepData);
						pthread_mutex_unlock(&mutexFichier);

						if(trouve && (strcmp(password, tmp)==0)){
							sprintf(msg, "%d", numTh+1000); // numero de session
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
						printf("SER[th%d]> Requête LOGOUT_OFFICER\n", numTh);
						if(loginOk){
							loginOk = 0;
							SocketSendReqEOM(Socket, OK, sepReq, "okokok");
						}
						else
							printf("SER[th%d]> Erreur: recu logout sans login\n", numTh);	
						break;
					}
				
				case NUM_VOL: //[NUM_VOL|numeroVol]
					{
						printf("SER[th%d]> Requête NUM_VOL\n", numTh);
						// chercher dans fichier vols.csv pour numeroVol
						strcpy(numeroVol, msg);
						char nomVol[100];
						trouve = FetchRow(msg, nomVol, getProperty(fSer, "fichier_vols"), sepData);
						if(trouve)
						{
							SocketSendReqEOM(Socket, OK, sepReq, nomVol);
							volEnCours = atoi(numeroVol);

							strcpy(fichierVol, "VOL");
							strcat(fichierVol, numeroVol);
							strcpy(fichierVolVal, fichierVol);
							strcat(fichierVolVal, "LUG.csv");
							strcat(fichierVol, ".csv");
							fichierVol[strlen(numeroVol)+7] = '\0';
							fichierVolVal[strlen(numeroVol)+10] = '\0';
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
						printf("SER[th%d]> Requête NUM_BILLET\n", numTh);
						if(volEnCours != 0)
						{
							printf("SER[th%d]> recherche du billet dans %s\n", numTh, fichierVol);
							trouve = FetchRow(msg, numeroBillet, fichierVol, sepData);
							if(trouve)
							{
								SocketSendReqEOM(Socket, OK, sepReq, numeroBillet);
							}
							else // billet n'existe pas
							{
								SocketSendReqEOM(Socket, NOK, sepReq, numeroBillet);
							}
						}
						else
						{
							printf("SER[th%d]> Aucun vol en cours\n", numTh);
							SocketSendReqEOM(Socket, NOK, sepReq, "");
						}
						break;
					}
					
				case NBR_PASS: //[NBR_PASS|nbrpassagers]
					{
						printf("SER[th%d]> Requête NUM_PASS\n", numTh);
						nbrPass = atoi(msg);
						if(nbrPass > 0)
						{
							SocketSendReqEOM(Socket, OK, sepReq, "Ok");
						}
						else
						{
							SocketSendReqEOM(Socket, NOK, sepReq, "Nok");
						}
						break;
					}
					
					
				case CHECK_LUGGAGE: //[CHECK_LUGGAGE|poid1;poids2;...]
					{
						printf("SER[th%d]> Requête CHECK_LUGGAGE\n", numTh);
						total = 0;
						cpt = 1;
						p = strtok(msg, sepData);
						
						total += atof(p);
						while(cpt < nbrPass)
						{
							p = strtok(NULL, sepData);
							total += atof(p);
							cpt++;
						}
						printf("SER[th%d]> Poids total: %.2fkg pour %d bagages\n", numTh, total, cpt);
						pthread_mutex_lock(&mutexPoidsPrix);
						float surpoids;
						surpoids = total - poidsMax*cpt;
						pthread_mutex_unlock(&mutexPoidsPrix);
						if(surpoids > 0) // surpoids
						{
							char rep[TAILLE_MSG];
							strcpy(rep, "\0");
							
							printf("SER[th%d]> Surpoids de %.2fkg\n", numTh, surpoids);

							sprintf(rep, "%.2f", surpoids);
							strcat(rep, sepData);
							pthread_mutex_lock(&mutexPoidsPrix);
							sprintf(&rep[strlen(rep)], "%.2f", (surpoids*prixSuppl));
							pthread_mutex_unlock(&mutexPoidsPrix);
							SocketSendReqEOM(Socket, OK, sepReq, rep); //[OK|surpois;prixapayer]
						}
						else //pas de surpoids
						{
							printf("SER[th%d]> Pas de surpoids\n");
							SocketSendReqEOM(Socket, OK, sepReq, "0;0");
							payementOk = 1; // pas de surpoids donc pas de payement de supplément
						}
						break;
					}
										
				case BAGAGES: // [BAGAGES|valise;autre;...]
					{
						printf("SER[th%d]> Requête BAGAGES\n", numTh);
						if(payementOk)
						{							
							// découpe du message et pour chaque élément écrire dans le fichier du vol
							valise = strtok(msg, sepData);
							EcrireCsv(fichierVolVal, numeroBillet, valise, sepData); // ajoute chaque valise
							cpt = 1;
							while(cpt < nbrPass)
							{
								valise = strtok(NULL, sepData);
								EcrireCsv(fichierVolVal, numeroBillet, valise, sepData);
								cpt++;
							}
							SocketSendReqEOM(Socket, OK, sepReq, "Enregistré");
						}
						else
						{
							SocketSendReqEOM(Socket, NOK, sepReq, "Annulation");
						}
						break;
					}
				case PAIEMENT:
					{
						printf("SER[th%d]> Requête PAIEMENT\n", numTh);
						if(strcmp(msg, "Paiement en ordre") == 0)
						{
							printf("SER[th%d]> Le client a payé\n", numTh);
							payementOk = 1;
						}
						else
						{
							printf("SER[th%d]> Le client n'a payé\n", numTh);
							payementOk = 0;
						}
						break;
					}					
				default: 
					CloseSocket(Socket);
					connecte = 0;
			}
		}// boucle tant que connecté
		
		//Fin du traitement
		pthread_mutex_lock(&mutexConnexion);
		SocketService[numConn]=-1; // libère le tableau de sockets
		pthread_mutex_unlock(&mutexConnexion);
		
	}// boucle infinie thread
}//fin thread

