#include "tcplib.h"

int SocketEcoute;
int SocketService;

void TraitementMessage(char*);

int main()
{
	struct sockaddr_in SocketAddress;
	struct hostent *CurrentHost;
	struct in_addr adrIP;



	//TEST CONNEXION MONOCLIENT
	CreateCheckinConfig();
	
	CurrentHost = getLocalHost();
	memcpy(&adrIP, CurrentHost->h_addr, CurrentHost->h_length);
	SocketAddress = initSocketAddress(SocketAddress, CurrentHost);
	
	//Création de la socket
	SocketEcoute = CreateSocket(SocketEcoute);	
	
	printf("SER> SocketEcoute = %d\n", SocketEcoute);
	//Attachement de la socket à une adresse
	ConnectSocket(SocketEcoute, SocketAddress, CurrentHost);
	//Mise à l'écoute d'une requête de connexion
	while(1){
		SocketWait(SocketEcoute);
		SocketService = GetClient(SocketEcoute, SocketAddress); //Duplication de socket
		TraitementMessage(SocketRcvEOM(SocketService, TAILLE_MSG));
	}
	
	close(SocketEcoute);
	close(SocketService);
	return 0;
}

void TraitementMessage(char* msg)
{
	if(strcmp(msg, "EOC") == 0 || strcmp(msg, "CLIENT INTERRUPTED") == 0){
		printf("---Fin de connexion---");
		close(SocketEcoute);
		close(SocketService);
	}
	else{
		SocketSend(SocketService, "SER> ACK Message bien reçu <EOM>\n");
	}
}

