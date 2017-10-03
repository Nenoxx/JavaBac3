#include "tcplib.h"
#include "csvlib.h"
#include "cimp.h"

#include <signal.h>

void handlerSIGINT(int sig);
void clear_screen();

int SocketClient;

int main()
{
	struct sockaddr_in SocketAddress; // contient port et ip de la socket
	struct hostent *infosHost; // infos sur la machine
	struct in_addr adrIP;
	struct sigaction act;
	
	char msg[TAILLE_MSG] = {0}, rcv[TAILLE_MSG]={0}, login[30], password[30], *separator, *separator2;
	int req=0, choix;
	int retour=0, loginok = 0, deco = 0;
	
	
	separator = getProperty("separateur_CIMP");
	separator2 = getProperty("separateur_fichier");
	
	//armement de SIGINT
	act.sa_handler = handlerSIGINT;
	sigemptyset(&act.sa_mask);
	act.sa_flags = 0;
	sigaction(SIGINT, &act, 0);
	

	//1) Création de la socket
	SocketClient = CreateSocket(SocketClient);
	
	//2) Informations sur l'ordinateur DISTANT
	if((infosHost = gethostbyname(getProperty("hostname")))==0){
		printf("CLI> erreur acquisition infos sur le host distant\n");
		exit(1);
	}
	memcpy(&adrIP, infosHost->h_addr, infosHost->h_length);
	printf("CLI> hote distant: ip: %s\n", inet_ntoa(adrIP));
	
	
	//3) Préparation de la struct sockaddr_in
	SocketAddress = initSocketAddress(SocketAddress, infosHost, "port_service");
	

	//4) Connexion
	ClientConnect(SocketClient, SocketAddress);
	printf("\nCLI> connexion au serveur...\n");
		
	//si réception NOK
	SocketRcvEOM(SocketClient, rcv, TAILLE_MSG);
	req = getRequest(rcv);
	if(req == NOK){
		CloseSocket(SocketClient);
		printf("Serveur plein, retenter plus tard\n");
		exit(1);
	}
	printf("CLI> Connecté au serveur\n");
	
	do{
		clear_screen();
		int cpt = 0;
		printf("#### APPLICATION CHECK-IN ####\n\n\n");		
		do{
			printf("Entrez votre login:");
			scanf("%s", login);
			printf("Entrez votre password:");
			scanf("%s", password);
		
			strcpy(msg, login);
			strcat(msg, separator2);
			strcat(msg, password);
			SocketSendReqEOM(SocketClient, LOGIN_OFFICER, separator, msg);
			SocketRcvEOM(SocketClient, msg, TAILLE_MSG);
			req = getRequest(msg);
			if(req == 10){
				loginok = 1;
				deco = 0;
			}
		
			cpt++;
		}while(cpt < 5 && loginok == 0);
		if(cpt == 5){
			CloseSocket(SocketClient);
			printf("Trop de tentative infructueuses\n");
			exit(1);
		}
		printf("CLI> connexion autorisée\n");
		
		// boucle debut
			// envoi LOGIN_OFFICER avec login, pwd
			// réception LOGIN_OFFICER avec OK ou NOK
			// si NOK -> nouvel essai
				// si cpt essai == 5 -> envoi EOC
				// CloseSocket(SocketClient);
				// printf("Trop de tentative infructueuses\n");
				// exit(1);
		
		// boucle fin
	
		// connexion autorisée:
		//boucle debut
			// afficher menu:
			//do{
			//		clear_screen();
			//printf("#### APPLICATION CHECK-IN ####\n");
			//printf("        MENU PRINCIPAL\n1) Enregistrer des billets\n2) Déconnexion\n\nVotre choix: ");
			//}while(choix != '1' && choix != '2');
		//boucle fin
			
			while(deco == 0){
				char r;
				do{
					clear_screen();
					printf("MENU PRINCIPAL\n\n\t1) Enregistrer des billets\n\t2) Déconnexion\n\n\tVotre choix : ");
		
					r = getchar();
				}
				while(r != '1' && r != '2');

				switch(r){
				case '1':
					{ //mettre le bloc (les crochets) sinon le compilateur râle.
					char billet[20], sep[5], msg[TAILLE_MSG] = "", rcv[TAILLE_MSG], tmp[10], c;
					int nbPersonnes = 0, prix_exces = 0;
					float poids = 0, poids_tot = 0;
				
					strcpy(sep, getProperty("separateur_fichier"));
					printf("\nNuméro de billet : ");
					fgets(billet, 20, stdin);
					printf("Nompre d'accompagnants : ");
					scanf("%d", &nbPersonnes); //Bon, si l'utilisateur commence à venir casser les couilles je renforcerai la sécurité sur les INPUT mais là, non.
					for(int i = 0; i < nbPersonnes; i++)
					{
						printf("\nPoids de la valise num. %d : ", i);
						scanf("%f", &poids);
						poids_tot += poids;
						sprintf(tmp, "%f", poids);
						strcat(msg, tmp);
						if(i != nbPersonnes - 1) strcat(msg, separator2); //pour ne pas mettre un séparateur en fin de chaine qui foutra la merde dans la lecture de la requête.
					}
					printf("Verification des valises, veuillez patienter SVP...\n");
					SocketSendReqEOM(SocketClient, CHECK_LUGGAGE, separator, msg);	
					SocketRcvEOM(SocketClient, rcv, TAILLE_MSG);
					req = getRequest(rcv);
					if(req == 10){ //OK
						char *pPoidsEx, *pPrixEx;
						float poids_ex;
						pPoidsEx = strtok(rcv, separator2);
						sscanf(pPoidsEx, "%f", poids_ex);
						if(poids_ex > 0){ //Cas ou il y a un supplément à payer
							pPrixEx = strtok(NULL, separator2);
							sscanf(pPrixEx, "%d", prix_exces);
							printf("\n-----------\nPoids total des baggages : %f\nPoids excessif : %f\nCout supplémentaire : %d euros\n-----------\n", poids_tot, poids_ex, prix_exces);
						}
						
						do
						{	printf("Paiement effectué? (o/n) : ");					
							c = getchar();
						}
						while(c != 'o' && c!= 'n');
						
						if(c == 'o'){
							//Création du log temporaire
							CreateLuggageLog(billet, "Valise");
						}
					}
					break;
					}
				case '2': deco = 1; break;
				default: break;
				}
			}
		}
		while(deco == 1);
	//5) Envoi d'un message
	//6) Réception de la réponse
	//7)
		
	CloseSocket(SocketClient);
	return 0;
}

//---------------------------------------------------------------------------------------
/*
Réception d'un SIGINT
*/
void handlerSIGINT(int sig)
{
	printf("CTRL+C détecté\n");
	SocketSend(SocketClient, "CLIENT INTERRUPTED<EOM>");	
	close(SocketClient);
	exit(1);
}

void clear_screen()
{
	printf("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"); //han ouais
	fflush(stdout);
} 

