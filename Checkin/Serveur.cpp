#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>

#include "tcplib.h"

int SocketEcoute;
int SocketService[100];
int pool = 5;

void* ThreadTraitementMsg(void*);

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
	
	for(int j=0; j<100; j++)
	{
		SocketService[j] = -1;
	}
	
	// Création du pool de threads
	//!!récup nbr de threads dans pool
	pthread_t *thread;
	thread =(pthread_t *) malloc(pool * sizeof(int));
	for(int i=0; i<pool; i++){
		pthread_create((thread+i), NULL, ThreadTraitementMsg, NULL);
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
	
	//3) Préparation de la struct sockaddr_in
	SocketAddress = initSocketAddress(SocketAddress, CurrentHost, "26020");		
	printf("SER> Struct SocketAddress initialisée\n");
	
	//4) Bind
	ConnectSocket(SocketEcoute, SocketAddress, CurrentHost);
	printf("SER> Bind ok\n");
	
	while(1){
		i=0;
		
		//5) Listen
		SocketWait(SocketEcoute);
		printf("SER> Listen ok\n");
		
		while(SocketService[i] != -1){
			i++;
		}
		printf("SER> Socket num:%d libre\n", i);
		//6) Accept
		SocketService[i] = GetClient(SocketEcoute, SocketAddress);
		//Duplication de socket
		
		printf("SER> Accept ok\n");
		
		//7) signaler un thread d'une nouvelle connexion
		pthread_mutex_lock(&mutexConnexion);
		connexionDispo++;
		pthread_mutex_unlock(&mutexConnexion);
		pthread_cond_signal(&condConnexion);
		
	}
	return 0;
}

//-----------------------------------------------------------------------------
/*
Thread de traitement de requêtes
*/
void* ThreadTraitementMsg(void * p)
{
	int Socket, i, taille=2000;
	printf("SER(th)> thread créé\n");
	
	pthread_mutex_lock(&mutexConnexion);
	while(connexionDispo == 0){
		pthread_cond_wait(&condConnexion, &mutexConnexion);
	}
	connexionDispo--; // le thread "prend" la connexion en charge
	
	i=0;
	while(SocketService[i] == -1){
		i++;
	}
	Socket = SocketService[i];
	SocketService[i]=-1;
	
	pthread_mutex_unlock(&mutexConnexion);
	
	while(1){
		char msg[2000]={0}; // à modifier
		SocketRcvEOM(Socket, msg, taille);
		
		printf("SER(th)> msg recu: [%s]\n", msg);
		
		if(strcmp(msg, "EOC") == 0 || strcmp(msg, "CLIENT INTERRUPTED") == 0){
			printf("---Fin de connexion---\n");
			CloseSocket(Socket);
		}
		else{
			SocketSend(Socket, "SER> ACK Message bien reçu<EOM>\n");
		}
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

