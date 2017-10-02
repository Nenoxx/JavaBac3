#include "tcplib.h"
#include <signal.h>

void handlerSIGINT(int sig);

int SocketClient;

int main()
{
	struct sockaddr_in SocketAddress; // contient port et ip de la socket
	struct hostent *CurrentHost; // infos sur la machine
	char r, msg[TAILLE_MSG] = "";
	char rcv[TAILLE_MSG];
	struct sigaction act;
	int retour=0;
	
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
	SocketAddress = initSocketAddress(SocketAddress, CurrentHost, "26020");
	
	//4) Connexion
	do{
	
		printf("\nCLI> Se connecter? (O/N) : ");
		fflush(stdin);
		r = getchar();
	}while(r != 'O' && r != 'N' && r != 'o' && r != 'n');
	
	
	if(r == 'O' || r == 'o'){
		ClientConnect(SocketClient, SocketAddress);
		
		//5) Envoi d'un message de connexion
		strcpy(msg, "Hello, demande de connexion");
		msg[strcspn(msg, "\n")] = '\0'; //On remplace le \n du fgets par un \0
		strcat(msg, "<EOM>");
		SocketSend(SocketClient, msg);
		printf("Message de connexion envoié: [%s]\n", msg);
		
		//6) Réception réponse
		retour = SocketRcvEOM(SocketClient, rcv, TAILLE_MSG);
		if(retour > 0){
			printf("Réponse du serveur: [%s]\n", rcv);
		}
		else
			printf("Erreur de receive\n");
		
				
		while(1){
			//init
			strcpy(msg, "");
			//input
			printf("\nMessage a envoyer : ");
			fgets(msg, TAILLE_MSG, stdin); //ARNAUD, FAIS MOI FONCTIONNER CETTE FONCTION DE MERDE STP!
			msg[strcspn(msg, "\n")] = '\0'; //On remplace le \n du fgets par un \0
			printf("message entré: [%s], taille %d\n", msg, strlen(msg));
			strcat(msg, "<EOM>");
			//send
			SocketSend(SocketClient, msg); 
			printf("Message envoyé: [%s]\nAttente d'un ACK...\n", msg);
			SocketRcvEOM(SocketClient,rcv, TAILLE_MSG);
			printf("Réponse du serveur: [%s]\n", rcv);
		}
	}
	else{
		CloseSocket(SocketClient);
		exit(0);
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

