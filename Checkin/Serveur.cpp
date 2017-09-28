#include "tcplib.h"

int main()
{
	struct sockaddr_in SocketAddress;
	struct hostent *CurrentHost;
	int SocketEcoute;
	int SocketService;
	char defaultIP[] = "0.0.0.0";
	//TEST CONNEXION MONOCLIENT
	CreateCheckinConfig();
	//printf("\nPORT = %d\nHOSTNAME = %s\n", getPort(), getHostname());
	
	CurrentHost = getLocalHost();
	//CurrentHost->h_addr = defaultIP;
	if(initSocketAddress(SocketAddress, CurrentHost)){
		printf("\nSER> Init SocketAddress OK\n");
	}
	
	//printf("sin_family = %d\n", SocketAddress.sin_family);
	//printf("Adresse CurrentHost : %s\n", CurrentHost->h_addr);
	printf("\nHostname: %s\n", CurrentHost->h_name);
	printf("Address type #: %d\n", CurrentHost->h_addrtype);
	printf("Address length: %d\n\n", CurrentHost->h_length);
	
	//Création de la socket
	SocketEcoute = CreateSocket(SocketEcoute);
	printf("SER> SocketEcoute = %d\n", SocketEcoute);
	//Attachement de la socket à une adresse
	ConnectSocket(SocketEcoute, SocketAddress, CurrentHost);
	//Mise à l'écoute d'une requête de connexion
	SocketWait(SocketEcoute);
	SocketService = GetClient(SocketEcoute, SocketAddress); //Duplication de socket
	SocketRcvEOM(SocketService, TAILLE_MSG);

	return 0;
}



