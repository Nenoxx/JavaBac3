#include "tcplib.h"
#include "csvlib.h"
#include <signal.h>

void handlerSIGINT(int sig);
void clear_screen();

int SocketClient;

int main()
{
	struct sockaddr_in SocketAddress; // contient port et ip de la socket
	struct hostent *CurrentHost; // infos sur la machine
	char msg[TAILLE_MSG] = "", rcv[TAILLE_MSG], value[30], loginpwd[65];
	struct sigaction act;
	int retour=0, loginok = 0, deco = 0;
	
	//armement de SIGINT
	act.sa_handler = handlerSIGINT;
	sigemptyset(&act.sa_mask);
	act.sa_flags = 0;
	sigaction(SIGINT, &act, 0);
	

	//1) Création de la socket
	SocketClient = CreateSocket(SocketClient);
	
	//2) Informations sur l'ordinateur DISTANT
	CurrentHost = getLocalHost(); // à modif car on récup les infos de l'ordi local
	
	//3) Préparation de la struct sockaddr_in
	SocketAddress = initSocketAddress(SocketAddress, CurrentHost, "port_service");
	while(deco == 0){
		//4) Connexion
		printf("#### APPLICATION CHECK-IN ####\n\n\n");
		do{
			//init
			RowList *rlist = NULL;
			strcpy(value, "");
			strcpy(loginpwd, "");
			//get login
			printf("\nLOGIN : ");
			fgets(value, 30, stdin);
			value[strlen(value)-1] = '\0';
			strcpy(loginpwd, value);
			rlist = FetchRows(value, "login.csv"); //tant qu'on a encore le login dans value.. Oui, c'est dégueulasse comme code.
							       //!!! Le login est UNIQUE, il ne doit y avoir qu'une seule valeur dans rlist !
			strcat(loginpwd, ";");
			printf("MOT DE PASSE : ");
			fgets(value, 30, stdin);
			value[strlen(value)-1] = '\0';
			strcat(loginpwd, value);
			printf("row? %s\n", rlist->first->rowElem);
			//verification
			if(strcmp(rlist->first->rowElem, loginpwd) != 0)
				printf("Login ou mot de passe erronné..\n\n");
			else{
				printf("Login OK\n\n");
				loginok = 1;
			}
		}
		while(loginok == 0);
	
		ClientConnect(SocketClient, SocketAddress);
		
		//5) Envoi d'un message de connexion
		strcpy(msg, "Hello, demande de connexion");
		msg[strcspn(msg, "\n")] = '\0'; //On remplace le \n du fgets par un \0
		strcat(msg, "<EOM>");
		SocketSend(SocketClient, msg);
		printf("Message de connexion envoyé: [%s]\n", msg);
	
		//6) Réception réponse
		retour = SocketRcvEOM(SocketClient, rcv, TAILLE_MSG);
		if(retour > 0){
			printf("Réponse du serveur: [%s]\n", rcv);
		}
		else
			printf("Erreur de receive\n");
	
			
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
				char billet[20], sep[5], msg[TAILLE_MSG], rcv[TAILLE_MSG];
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
					if(poids > 23.0)
						prix_exces += 15;
				}
				printf("\n\nPoids total : %f\nSupplément à payer : %f", poids_tot, prix_exces);
				printf("\nVERIFICATION DES VALISES, VEUILLEZ PATIENTER... ");
				strcpy(msg, "?"); //Changer ! C'est le numéro de la requête !
				strcat(msg, sep);
				strcat(msg, "CHECK_LUGGAGE");
				SocketSend(SocketClient, msg);
				SocketRcvEOM(SocketClient, rcv, TAILLE_MSG); // VALISE_ACK ou VALISE_NACK
				if(strcmp(rcv, "VALISE_ACK") == 0) printf("VALISE OK ! BON VOYAGE !\n\n\n");
				else printf("VALISE NON OK !");
				break;
				}
			case '2': deco = 1; break;
			default: break;
			}
		}
	}
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

