#include <pthread.h>

#include "tcplib.h"

int SocketEcoute;
int SocketService;
int pool = 5;

void ThreadTraitementMsg(char*);

int main()
{
	struct sockaddr_in SocketAddress; // contient port et ip de la socket
	struct hostent *CurrentHost; // infos sur la machine
	struct in_addr adrIP; // addresse ip de la machine
	int retour=0;
	char rcv[TAILLE_MSG];

	//Vérifie si le fichier de config existe, le crée sinon
	CreateCheckinConfig();
	
	// Création du pool de threads
	pthread_t *thread;
	thread = malloc(pool * sizeof(int));
	for(int i=0; i<pool; i++){
		pthread_create((thread+i), NULL, 
	}
	
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
	
	//3) Préparation de la struct sockaddr_in
	SocketAddress = initSocketAddress(SocketAddress, CurrentHost);		
	printf("SER> Struct SocketAddress initialisée\n");
	
	//4) Bind
	ConnectSocket(SocketEcoute, SocketAddress, CurrentHost);
	printf("SER> Bind ok\n");
	
	//while(1){
		//5) Listen
		SocketWait(SocketEcoute);
		printf("SER> Listen ok\n");
	
		//6) Accept
		SocketService = GetClient(SocketEcoute, SocketAddress); //Duplication de socket
		printf("SER> Accept ok\n");
		
		//7) fournir SocketService à un thread libre
		
	//}

	//test de quelques messages
	for(int i=0; i<10; i++){
		//7) Réception d'un message
		retour = SocketRcvEOM(SocketService, rcv, TAILLE_MSG);
		if(retour > 0){
			
		}
		else
			break;
	}
	
	CloseSocket(SocketEcoute);
	CloseSocket(SocketService);
	return 0;
}


/*
Effectue un traitement en fonction du message recu
*/
void ThreadTraitementMsg(char* msg)
{
	//récup socket service libre
	//while(1)
		//recup message
		//traitement message
	printf("SER> message recu: [%s], taille %d\n", msg, strlen(msg));
		
	if(strcmp(msg, "EOC") == 0 || strcmp(msg, "CLIENT INTERRUPTED") == 0){
		printf("---Fin de connexion---\n");
	}
	else{
		SocketSend(SocketService, "SER> ACK Message bien reçu<EOM>\n");
	}
	
	// connexion =1
	// deconnexion =2 
	// num vol
	// num billet
	// nbr passagers (vérifier si existe meme nombre billets à la suite que passagers)
	// bagages
	//
	//
}

