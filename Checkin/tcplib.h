#ifndef TCPLIB_INPRES_H
#define TCPLIB_INPRES_H

#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <errno.h>
#include <netinet/in.h>
#include <unistd.h>
#include <netdb.h>
#include "string.h"
/*----TO DO LIST----

1) Faire des gestionnaires d'erreurs en fonction de errno (en gros, des switch(errno) case...: case...:)
   histoire de pouvoir fournir des erreurs plus explicites (voir p55 bouquin TCP/IP)

2) Fonctions de receive

*/

#define TAILLE_MSG 100

void CreateCheckinConfig()
{
	FILE* fp;
	char Content[] = "###CONFIG FILE###\nport = INSERT_PORT_HERE\nhostname = INSERT_HOSTNAME_HERE\n###EOF###\n"; //à modifier si ajout de nouvelles propriétés
	if((fp = fopen("checkin.config", "r")) == NULL){ // ! Check si le fichier n'existe pas, pas question d'écraser l'ancien
		printf("checkin config n'existe pas, tentive de création...\n");
		if((fp = fopen("checkin.config", "a+")) != NULL){
			printf("création OK\n");
			fwrite(Content, sizeof(char), sizeof(Content)-1, fp);
			printf("Initialisation du fichier OK\n");
			fclose(fp);
		}	
	}
}

unsigned int getPort()
{
	FILE* fp;
	unsigned int Port;
	
	if((fp = fopen("checkin.config", "r")) != NULL)
	{
		char line[50], property[30], value[30]; 

		while(!feof(fp)){
			//Réinitialisation des valeurs
			strcpy(line, "");
			strcpy(property, "");
			strcpy(value, "");
			
			//On lit une ligne du fichier config et on la segmente
			fgets(line, 50, fp);
			line[strlen(line-1)] = '\0'; // <--- Pour enlever le '\n' mit par le fgets
			sscanf(line, "%s = %s", property, value);
			
			/*DEBUG*/
			printf("%s = %s", property, value);
			
			if(!strcmp(property, "port")){
				sscanf(value, "%d", &Port); //On converti la chaine de caractère en int	
				fclose(fp);
				return Port;
			}
		}
		printf("Port non-trouvé, veuillez vérifier le fichier de configuration.\n"); //Si on sort du while c'est qu'on a atteint la fin du fichier sans trouver
		fclose(fp);	
		return 0;
	}
	else{
		printf("Erreur lors de l'ouverture du fichier checkin.config\n");
		exit(1);
	}
}

char* getHostname()
{
	FILE* fp;
	
	if((fp = fopen("checkin.config", "r")) != NULL)
	{
		char line[50], property[30], value[30]; 

		while(!feof(fp)){
			//Réinitialisation des valeurs
			strcpy(line, "");
			strcpy(property, "");
			strcpy(value, "");
			
			//On lit une ligne du fichier config et on la segmente
			fgets(line, 50, fp);
			line[strlen(line-1)] = '\0'; // <--- Pour enlever le '\n' mit par le fgets
			sscanf(line, "%s = %s", property, value);
			
			/*DEBUG*/
			printf("%s = %s", property, value);
			
			if(!strcmp(property, "hostname")){
				fclose(fp);
				return value;
			}
		}
		printf("Hostname non-trouvé, veuillez vérifier le fichier de configuration.\n"); //Si on sort du while c'est qu'on a atteint la fin du fichier sans trouver
		fclose(fp);
		return NULL;
	}
	else{
		printf("Erreur lors de l'ouverture du fichier checkin.config\n");		
		exit(1);
	}
}

int CreateSocket(int SocketHandle)
{
	//Création d'une socket de protocole TCP en mode connecté fiable, possibilité de donner une option à l'appel
	//de fonction pour pouvoir créer une socket IP en mode bas niveau? Besoin?
	SocketHandle = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if(SocketHandle == -1){
		printf("Erreur de création de la socket %d\n", errno);
		return 0;
	}
	else{
		printf("Création socket %d OK\n", SocketHandle);
		return SocketHandle;
	}

	
}

int ShutConnection(int SocketHandle)
{
	//Bloque la connexion en écriture, permet de terminer les lectures
	if((shutdown(SocketHandle, 1)) == -1){
		printf("Erreur lors de la fermeture de la socket %d en écriture\n", errno);
		exit(1);
	}
	else{
		printf("Socket correctement fermée en écriture\n");	
		return 1;
	}
}

int CloseSocket(int SocketHandle)
{
	//Termine une socket, appeler cette fonction après ShutConnection.
	if((close(SocketHandle)) == -1){
		printf("Erreur lors de l'arrêt de la socket %d\n", errno);
		exit(1);
	}

	else{
		printf("Socket correctement terminée\n");
		return 1;
	}
}

int ConnectSocket(int SocketHandle, struct sockaddr_in SocketAddress)
{
	struct hostent *CurrentHost;
	struct in_addr IPAddress;
	unsigned int PORT;
	
	//Acquisition des infos sur l'ordinateur local

	if((CurrentHost = gethostbyname( getHostname() )) == 0){
		printf("Erreur d'acquisition d'infos sur le host %d\n", errno);
		exit(1);
	}

/* --- LA PREPARATION EST A FAIRE DANS LE FICHIER .C(PP) PRINCIPAL ! La structure doit être "préparée" avant d'être donnée en argument !
       (l'ennui de la repréparer à chaque fois c'est que 1) perte de performances 2) On écrase les valeurs précédentes qui auraient pu être simplement réutilisées

	//Préparation de la structure sockaddr_in
	memset(&SocketAddress, 0, sizeof(struct sockaddr_in));
	SocketAddress.sin_family = AF_INET;
	SocketAddress.sin_port = htons(getPort()); //conversion du numéro de port au format réseau
	memcpy(&SocketAddress.sin_addr, CurrentHost->h_addr, CurrentHost->h_length);

*/
	//Connexion de la socket à l'internet du cul
	if(bind(SocketHandle, (struct sockaddr*)&SocketAddress, sizeof(struct sockaddr_in)) == -1){
		printf("Erreur de bind sur la socket %d\n", errno);
		exit(1);
	}
	else{
		printf("Bind adresse et port socket OK\n");
		return 1;
	}
	
}

int SocketWait(int SocketHandle)
{
	//Mise en attente d'une connexion par un client
	if(listen(SocketHandle, SOMAXCONN) == -1){
		printf("Erreur de mise en écoute de la socket %d sur le réseau\n", errno);
		CloseSocket(SocketHandle); //pas besoin de shutdown avant ici, pour le coup
		exit(1);
	}
	else{
		printf("Socket en attente d'une connexion...\n");
		return 1;
	}
}

int GetClient(int SocketHandle, struct sockaddr_in SocketAddress)
{
	int tailleSocketAddress = sizeof(struct sockaddr_in);


	//Prise en charge d'un client par la socket d'écoute
	if((accept(SocketHandle, (struct sockaddr*)&SocketAddress, &tailleSocketAddress)) == -1){
		printf("Erreur lors de la prise en charge d'un client par la socket %d\n", errno);
		ShutConnection(SocketHandle);
		CloseSocket(SocketHandle);
		exit(1);
	}
	else{
		printf("Client prit en charge par la socket d'écoute\n");
		return SocketHandle; //Valeur retournée = Handle de socket dédié à la connexion acceptée (duplication de socket)
	}
}

//SocketConnect permet de prendre connaissance de la socket cliente qui est en partial connection
//-> Donc la socket du serveur accepte la connexion d'un client
int SocketConnect(int SocketHandle, struct sockaddr_in SocketAddress) //Peut-être dûe à un changement de nom?
{
	int tailleSocketAddress = sizeof(struct sockaddr_in);
	if((accept(SocketHandle, (struct sockaddr*)&SocketAddress, &tailleSocketAddress)) == -1){
		printf("Erreur lors de la connexion de la socket Serveur%d\n", errno);
		CloseSocket(SocketHandle);
		exit(1);
	}
	else{
		printf("Connexion de la socket Serveur OK\n");
		return 1;
	}
}

//Permet à un client d'initier une connexion à une socket serveur
int ClientConnect(int SocketHandle, struct sockaddr_in SocketAddress)
{
	socklen_t tailleSocketAddress = sizeof(struct sockaddr_in);
	
	if((connect(SocketHandle, (struct sockaddr*)&SocketAddress, tailleSocketAddress)) == -1){
		printf("Erreur lors de la connexion de la socket Client %d\n", errno);
		CloseSocket(SocketHandle);
		exit(1);
	}
	else{
		printf("Connexion de la socket Client OK\n");
		return 1;
	}
}

int SocketSend(int SocketHandle, char* msg)
{
	if(send(SocketHandle, msg, TAILLE_MSG, 0) == -1){
		printf("Erreur sur l'envoi du message %s de la socket %d\n", msg, errno);
		return 0; //Pas d'exit ici, il faut clore toutes les sockets avant	
	}
	else{
		printf("Envoi OK\n");
		return 1;
	}
}

//Méthode 1 de receive : tant qu'il y a encore des bytes à lire.
int SocketRcvFull();

//Méthode 2 de receive : Caractère (ou chaine de caractères) de fin de séquence. Proposition : <EOM> (pour End of message) ?
//Attention à ne pas afficher le <EOM>.

int SocketRcvEOM();

#endif
