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
*/

#define TAILLE_MSG 2000

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
	else
		printf("Le fichier config existe déjà");
}

unsigned int getPort()
{
	FILE* fp;
	unsigned int Port;
	
	if((fp = fopen("checkin.config", "r")) != NULL)
	{
		char line[50], property[30], value[30];
		int stop = 0;

		while(!stop){
			//Réinitialisation des valeurs
			strcpy(line, "");
			strcpy(property, "");
			strcpy(value, "");
	
			//On lit une ligne du fichier config et on la segmente
			if(fgets(line, 50, fp) != NULL){
				line[strlen(line)-1] = '\0'; // <--- Pour enlever le '\n' mit par le fgets
				//printf("LINE : %s\n", line);
				sscanf(line, "%s = %s", property, value);

			
				//DEBUG
				//printf("\n%s = %s", property, value);
			
				if(strcmp(property, "port") == 0){
					if(strcmp(value, "INSERT_PORT_HERE") != 0){
						sscanf(value, "%d", &Port); //On converti la chaine de caractère en int	
						//printf("\nVALUE = %d", Port);
						fclose(fp);
						return Port;
					}
				}
				else{
					if(strcmp(property, "###EOF###") == 0)
						stop = 1;
				}
			}
			else{
				printf("Erreur de lecture dans le fichier\n");
				exit(1);
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
		int stop = 0;

		while(!stop){
			//Réinitialisation des valeurs
			strcpy(line, "");
			strcpy(property, "");
			strcpy(value, "");
	
			//On lit une ligne du fichier config et on la segmente
			if(fgets(line, 50, fp) != NULL){
				line[strlen(line)-1] = '\0'; // <--- Pour enlever le '\n' mit par le fgets
				//printf("LINE : %s\n", line);
				sscanf(line, "%s = %s", property, value);

			
				//DEBUG
				//printf("\n%s = %s", property, value);
			
				if(strcmp(property, "hostname") == 0){
					if(strcmp(value, "INSERT_HOSTNAME_HERE") != 0){
						char* returnvalue = (char*)malloc(sizeof(strlen(value)));
						strcpy(returnvalue, value);
						//printf("\nVALUE = %s", value);
						return returnvalue;
					}
				}
				else{
					if(strcmp(property, "###EOF###") == 0)
						stop = 1;
				}
			}
			else{
				printf("Erreur de lecture dans le fichier\n");
				exit(1);
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

int ConnectSocket(int SocketHandle, struct sockaddr_in SocketAddress, struct hostent *CurrentHost)
{
	//Connexion de la socket à l'internet du cul
	printf("SocketEcoute = %d\n", SocketHandle);
	if(bind(SocketHandle, (struct sockaddr*)&SocketAddress, sizeof(struct sockaddr_in)) == -1){
		printf("Erreur de bind sur la socket %d\n", SocketHandle);
		switch(errno){
		case EBADF: printf("Descripteur invalide\n"); break;
		case ENOTSOCK: printf("Descripteur non associé à une socket\n"); break;
		case EADDRNOTAVAIL: printf("Adresse spécifiée non-accessible\n"); break;
		case EADDRINUSE: printf("Adresse déjà utilisée\n"); break;
		case EINVAL: printf("Socket déjà liée à une adresse\n"); break;
		case EACCES: printf("Permission refusée\n"); break;
		case EFAULT: printf("Adresse non-accessible en lecture\n"); break;
		case ELOOP: printf("Trop de liens symboliques\n"); break;
		case ENAMETOOLONG: printf("Adresse trop longue\n"); break;
		case ENOENT: printf("File does not exist\n"); break;
		//case ENONEM: printf("Plus assez d'espace mémoire kernel de disponible\n"); break;
		case ENOTDIR: printf("Un composant n'est pas un répertoire\n"); break;
		case EROFS: printf("L'inode du socket réside dans un système read-only\n"); break;
		
		default: printf("Hé moi j'comprend rien\n");break;
		
		}
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
	socklen_t tailleSocketAddress = sizeof(struct sockaddr_in);


	//Prise en charge d'un client par la socket d'écoute
	//BLOQUANT
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
	socklen_t tailleSocketAddress = sizeof(struct sockaddr_in);
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
		printf("Erreur lors de la connexion de la socket Client %d\n", SocketHandle);
		switch(errno){
		case EBADF: printf("--> Descripteur invalide\n"); break;
		case ENOTSOCK: printf("Descripteur non associé à une socket\n"); break;
		case EOPNOTSUPP: printf("Type de socket invalide\n"); break;
		case EISCONN: printf("Socket déjà connectée\n"); break;
		case ETIMEDOUT: printf("Client timed out\n"); break;
		case ECONNREFUSED: printf("Le serveur a refusé la connexion\n"); break;
		case EADDRINUSE: printf("Socket serveur déjà connectée\n"); break;
		case EFAULT: printf("Adresse sockaddr_in incorrecte\n"); break;
		case EINTR: printf("Fonction interrompue\n"); break;
		case EACCES: printf("Permission refusée\n"); break;
		case EADDRNOTAVAIL: printf("Adresse spécifiée non-accessible\n"); break;
		case EAFNOSUPPORT: printf("Famille d'adresse incorrecte (sa_family)\n"); break;
		case EAGAIN: printf("Entrées insuffisantes dans le cache de routing\n"); break;
		case EINPROGRESS: printf("La connexion ne peut être complétée immédiatement (EINPROGRESS)\n"); break;
		case ENETUNREACH: printf("Réseau inaccessible\n");
		//case : printf("\n"); break;
		
		default: printf("Hé moi j'comprend rien\n");break;
		}

		CloseSocket(SocketHandle);
		exit(1);
	}
	else{
		printf("\nConnexion de la socket Client OK");
		return 1;
	}
}

int SocketSend(int SocketHandle, char* msg)
{
	if(send(SocketHandle, msg, TAILLE_MSG, 0) == -1){
		printf("\nErreur sur l'envoi du message %s de la socket %d", msg, errno);
		return 0; //Pas d'exit ici, il faut clore toutes les sockets avant	
	}
	else{
		printf("\nEnvoi OK");
		return 1;
	}
}

int getMTU(int SocketHandle)
{
	socklen_t optlen;
	int taille;

	optlen = sizeof(int);

	if((getsockopt(SocketHandle, IPPROTO_TCP, 2, &taille, &optlen)) == -1){
		printf("\nErreur sur le getsockopt de la socket %d", errno);
		exit(1);
	}
	else{
		printf("MTU = %d\n", taille);
		return taille;
	}
}

int EndOfMessage(char* msg, int taille)
{
	int i, trouve = 0;

	//Recherche de '<EOM>'

	for(i = 0; i<taille-5 && !trouve; i++) //-5 parce que pas besoin de check les 4 derniers caractères si le celui d'avant n'est pas '<'
	{
		if(msg[i] == '<'){
			if(msg[++i] == 'E'){
				if(msg[++i] == 'O'){
					if(msg[++i] == 'M'){
						if(msg[++i] == '>')
							trouve = 1;
					}
				}
			}
		}
	}

	return trouve;
}

//Méthode 1 de receive : tant qu'il y a encore des bytes à lire.
char* SocketRcvFull(int SocketHandle, int taille)
{
	int tailleMsgRecu = 0, nbBytes, fin;
	char buf[TAILLE_MSG*2];
	char MsgRecu[TAILLE_MSG];

	memset(buf,0 ,sizeof(buf));
	do
	{
			printf("\nRéception d'un message...");
			if(nbBytes = (recv(SocketHandle, buf, taille, 0)) != -1){
				memcpy((char*)MsgRecu + tailleMsgRecu, buf, nbBytes);
				tailleMsgRecu += nbBytes;
				printf("\nNombre de bytes reçus : %d", nbBytes);
				printf("\nTaille totale : %d", tailleMsgRecu);
			}
			else
			{
				printf("\nErreur de réception du message !");
			}
	}
	while(nbBytes != 0 && nbBytes != -1); //Tant qu'il y a des bytes à recevoir (et pas d'erreur)
}

//Méthode 2 de receive : Caractère (ou chaine de caractères) de fin de séquence. Proposition : <EOM> (pour End of message) ?
//Attention à ne pas afficher le <EOM>.

int SocketRcvEOM(int SocketHandle, int taille)
{
	int tailleMsgRecu = 0, nbBytes, fin;
	char buf[TAILLE_MSG*2];
	char MsgRecu[TAILLE_MSG];
	int finDetectee = 0;

	memset(buf,0 ,sizeof(buf));
	do
	{
			printf("\nRéception d'un message...");
			if(nbBytes = (recv(SocketHandle, buf, taille, 0)) != -1){
				finDetectee = EndOfMessage(buf, nbBytes);
				memcpy((char*)MsgRecu + tailleMsgRecu, buf, nbBytes);
				tailleMsgRecu += nbBytes;
				printf("\nNombre de bytes reçus : %d", nbBytes);
				printf("\nTaille totale : %d", tailleMsgRecu);
				if(finDetectee)
					printf("\nFin de message détectée");
			}
			else
			{
				printf("\nErreur de réception du message !");
				return 0;
			}
	}
	while(!finDetectee && nbBytes != -1);
}

struct hostent* getLocalHost()
{
	struct hostent* CurrentHost;
	//Acquisition des infos sur l'ordinateur local

	if((CurrentHost = gethostbyname( getHostname() )) == 0){
		printf("\nErreur d'acquisition d'infos sur le host %d\n", errno);
		switch(errno){
			case HOST_NOT_FOUND: printf("l'hote spécifié n'existe pas\n"); break;
			case NO_DATA: printf("Aucune adresse IP associée à l'hôte\n"); break;
			case TRY_AGAIN: printf("Erreur temporaire, réessayez.\n"); break;
			case NO_RECOVERY: printf("A nonrecoverable name server error occurred.\n"); break;
			default: printf("Han ouais\n");
		}
		exit(1);
	}
	else{
		printf("\ngetLocalHost -> OK");
		return CurrentHost;
	}
}


int initSocketAddress(struct sockaddr_in SocketAddress, struct hostent *CurrentHost)
{
	/*--- LA PREPARATION EST A FAIRE DANS LE FICHIER .C(PP) PRINCIPAL ! La structure doit être "préparée" avant d'être donnée en argument !
       	(l'ennui de la repréparer à chaque fois c'est que 1) perte de performances 2) On écrase les valeurs précédentes qui auraient pu être 		simplement réutilisées*/

	//Préparation de la structure sockaddr_in
	memset(&SocketAddress, 0, sizeof(struct sockaddr_in));
	SocketAddress.sin_family = AF_INET;
	SocketAddress.sin_port = htons(getPort()); //conversion du numéro de port au format réseau
	memcpy(&SocketAddress.sin_addr, CurrentHost->h_addr, CurrentHost->h_length);
}

#endif
