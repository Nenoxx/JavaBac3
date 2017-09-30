#include "tcplib.h"
#include <signal.h>

void handlerSIGINT(int sig);

int SocketClient;

int main()
{
	struct sockaddr_in SocketAddress;
	struct hostent *CurrentHost;
	char r, msg[TAILLE_MSG] = "";
	struct sigaction act;
	
	CurrentHost = getLocalHost();
	SocketAddress = initSocketAddress(SocketAddress, CurrentHost);
	SocketClient = CreateSocket(SocketClient);

	//armement de SIGINT
	act.sa_handler = handlerSIGINT;
	sigemptyset(&act.sa_mask);
	act.sa_flags = 0;
	sigaction(SIGINT, &act, 0);
	
	
	do{
	
		printf("\nCLI> Se connecter? (O/N) : ");
		fflush(stdin);
		r = getchar();
	}
	while(r != 'O' && r != 'N');

	if(r == 'O'){
		ClientConnect(SocketClient, SocketAddress);
		while(1){
			//init
			strcpy(msg, "");
			//input
			printf("\nMessage a envoyer : ");
			fgets(msg, TAILLE_MSG, stdin); //ARNAUD, FAIS MOI FONCTIONNER CETTE FONCTION DE MERDE STP
			msg[strcspn(msg, "\n")] = '\0'; //On remplace le \n du fgets par un \0
			strcat(msg, "<EOM>");
			//send
			SocketSend(SocketClient, msg); 
			printf("Message envoyé\nAttente d'un ACK...\n");
			SocketRcvEOM(SocketClient, TAILLE_MSG);
		}
	}
	else{
		close(SocketClient);
		exit(0);
	}
	
	close(SocketClient);
	return 0;
}


void handlerSIGINT(int sig)
{
	printf("CTRL+C détecté\n");
	SocketSend(SocketClient, "CLIENT INTERRUPTED");	
	close(SocketClient);
	exit(1);
}

