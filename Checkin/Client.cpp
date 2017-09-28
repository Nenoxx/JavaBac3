#include "tcplib.h"

int main()
{
	int SocketClient;
	struct sockaddr_in SocketAddress;
	struct hostent *CurrentHost;
	char r;
	
	
	CurrentHost = getLocalHost();
	initSocketAddress(SocketAddress, CurrentHost);
	SocketClient = CreateSocket(SocketClient);
	
	//ConnectSocket(SocketClient, SocketAddress, CurrentHost);
	printf("Adresse CurrentHost : %s\n", CurrentHost->h_addr);
	printf("sin_family = %d\n", SocketAddress.sin_family);
	do{
		
		printf("\nCLI> Se connecter? (O/N) : ");
		fflush(stdin);
		r = getchar();
	}
	while(r != 'O' && r != 'N');
	ClientConnect(SocketClient, SocketAddress);
	
	SocketSend(SocketClient, "COUCOU <EOM>");
	
	return 0;
}

