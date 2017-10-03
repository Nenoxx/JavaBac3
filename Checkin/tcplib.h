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
#include <netinet/tcp.h>
#include <netinet/in.h>
#include <arpa/inet.h> 


/*----TO DO LIST----

1) Corriger les derniers petits bugs (genre le putain de fgets qui veut pas lire stdin)
2) Threader tout le bordel
3) Tester sous Unix et prier Jésus que ça fonctionne
4) Ne pas céder à la panique
999999) Faire des gestionnaires d'erreurs en fonction de errno (en gros, des switch(errno) case...: case...:)
   histoire de pouvoir fournir des erreurs plus explicites (voir p55 bouquin TCP/IP)
*/

#define TAILLE_MSG 2000
// Ports 26020 à 26029
#define PORT_CHCK 26020

//---------------------------------------------------------------------------------------
/*
Crée le fichier de config si celui-ci n'existe pas déjà
*/

/*
Permet de séparer le numéro de type de requête du message en lui-même et de récupérer les deux valeurs
OUTPUT : Le numéro correspondant au type de la requête
INPUT : L'adresse du message entier, reçu par la socket.
*/

void CreateCheckinConfig()
{
	FILE* fp;
	char Content[1024] = {""};
	
	char hostname[1024]={0}; //Question portabilité, on va directement intégrer le bon hostname de la machine dans le fichier de config.
	hostname[1023] = '\0';
	gethostname(hostname, 1023); // récup le nom de la machine
	//printf("Hostname : %s\n", hostname);
	strcpy(Content, "###CONFIG FILE###\nport_service = 26020\nport_admin = 26029\nhostname = ");
	strcat(Content, hostname);
	strcat(Content, "\nseparateur_CIMP = |\nseparateur_fichier = ;\nnb_threads = 5\n###EOF###");
	//printf("CONTENT : %s\n", Content); // <-- OK
	
	if((fp = fopen("checkin.config", "r")) == NULL){ // ! Check si le fichier n'existe pas, pas question d'écraser l'ancien
		printf("checkin config n'existe pas, tentive de création...\n");
		if((fp = fopen("checkin.config", "a+")) != NULL){
			printf("création OK\n");
			fwrite(Content, sizeof(char), strlen(Content), fp);
			printf("Initialisation du fichier OK\n");
			fclose(fp);
		}	
	}
	else
	{
		printf("Le fichier config existe déjà\n");
		fclose(fp);
	}
}

//---------------------------------------------------------------------------------------
/*
Ouvre le fichier de config et recherche le hostname
*/
char* getProperty(char* propertyName)
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
				//printf("\nLINE : %s\n", line);
				sscanf(line, "%s = %s", property, value);

			
				//DEBUG
				//printf("\n%s = %s", property, value);
			
				if(strcmp(property, propertyName) == 0){
					char* returnvalue = NULL;
					if(returnvalue != NULL) 
						free(returnvalue);
					returnvalue = (char*)malloc(sizeof(strlen(value)));
					strcpy(returnvalue, value);
					//printf("\nVALUE = %s", value);
					return returnvalue;
				}
				else{
					if(strcmp(property, "###EOF###") == 0)
						stop = 1;
				}
			}
			else{
				printf("Fin du fichier\nLINE : %s", line);
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

//---------------------------------------------------------------------------------------
/*
Ouvre le fichier de config et cherche le port
*/
unsigned int getPort(char* nomPort)
{
	int port = atoi(getProperty(nomPort));
	if(port > 0)
		return port;
	else
		printf("Erreur : INVALID PORT NUMBER\n");
}

//---------------------------------------------------------------------------------------
/*
Sépare le contenu de la requête de son type et retourne les deux valeurs.
*/
int getRequest(char* request)
{
	char sep[5], *content;
	int requestType, tailleSep = strlen(sep);
	strcpy(sep, getProperty("separateur_CIMP"));
	//exemple de message de connexion : 1`aaa`bbb<EOM>
	printf("[%s]\n", request);
	content = strtok(request, sep);
	sscanf(content, "%d", &requestType); //Le premier paramètre étant le type de requête, on le converti en int.
	content = strtok(NULL, sep);
	strcpy(request, content);
	return requestType;
}

//---------------------------------------------------------------------------------------
/*
Création d'une socket de protocole TCP en mode connecté fiable
*/
int CreateSocket(int SocketHandle)
{

	SocketHandle = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if(SocketHandle == -1){
		printf("Erreur de création de la socket %d\n", errno);
	}
	else{
		printf("Création socket %d OK\n", SocketHandle);
	}
	return SocketHandle;
}

//---------------------------------------------------------------------------------------
/*
Effectue un shutdown sur la connexion
*/
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

//---------------------------------------------------------------------------------------
/*
Termine une socket
*/
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

//---------------------------------------------------------------------------------------
/*
Lie la socket à une adresse ip et un port
*/
int ConnectSocket(int SocketHandle, struct sockaddr_in SocketAddress, struct hostent *CurrentHost)
{
	//printf("SocketEcoute = %d\n", SocketHandle);
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

//---------------------------------------------------------------------------------------
/*
Mise en attente d'une connexion par un client
*/
int SocketWait(int SocketHandle)
{
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

//---------------------------------------------------------------------------------------
/*
Prise en charge d'un client par la socket d'écoute (BLOQUANT)
*/
int GetClient(int SocketHandle, struct sockaddr_in SocketAddress)
{
	socklen_t tailleSocketAddress = sizeof(struct sockaddr_in);
	int hTemp;

	if((hTemp = accept(SocketHandle, (struct sockaddr*)&SocketAddress, &tailleSocketAddress)) == -1){
		printf("Erreur lors de la prise en charge d'un client par la socket %d\n", errno);
		ShutConnection(SocketHandle);
		CloseSocket(SocketHandle);
		exit(1);
	}
	else{
		printf("Client prit en charge par la socket d'écoute\n");
		return hTemp; //Valeur retournée = Handle de socket dédié à la connexion acceptée (duplication de socket)
	}
}

//---------------------------------------------------------------------------------------
/*
Permet à un client d'initier une connexion à une socket serveur
*/
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

//---------------------------------------------------------------------------------------
/*
Envoi d'un message sur une socket
*/
int SocketSend(int SocketHandle, char* msg)
{
	if(send(SocketHandle, msg, TAILLE_MSG, 0) == -1){
		printf("\nErreur sur l'envoi du message %s de la socket %d\n", msg, errno);
		return 0; //Pas d'exit ici, il faut clore toutes les sockets avant	
	}
	else{
		printf("\nEnvoi OK [%s]\n", msg);
		return 1;
	}
}

int SocketSendReqEOM(int SocketHandle, int requete, char *separator, char *msg)
{
	char new_msg[TAILLE_MSG];
	sprintf(new_msg,"%d", requete);
	//itoa(requete, new_msg, 10);
	strcat(new_msg, separator);
	strcat(new_msg, msg);
	strcat(new_msg, "<EOM>");
	SocketSend(SocketHandle, new_msg);
}

//---------------------------------------------------------------------------------------
/*
Récupère le MTU d'une socket
*/
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

//---------------------------------------------------------------------------------------
/*
Recherche de '<EOM>' dans une chaine
*/
int EndOfMessage(char* msg, int taille)
{
	int i, trouve = 0;

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

//---------------------------------------------------------------------------------------
/*
Supprime le <EOM> à la fin d'une chaine
*/
int deleteEOM(char* msg, int taille)
{
	int i, trouve = 0;

	//Recherche de '<EOM>'

	for(i = 0; i<taille-5 && !trouve; i++) //-5 parce que pas besoin de check les 4 derniers caractères si le celui d'avant n'est pas '<'
	{
		if(msg[i] == '<'){
			if(msg[++i] == 'E'){
				if(msg[++i] == 'O'){
					if(msg[++i] == 'M'){
						if(msg[++i] == '>'){
							trouve = 1;
							msg[i-4] = '\0'; //on arrete la chaîne au <
						}
					}
				}
			}
		}
	}

	return trouve;
}

//---------------------------------------------------------------------------------------
/*
Méthode 1 de receive : tant qu'il y a encore des bytes à lire.
*/
char* SocketRcvFull(int SocketHandle, int taille)
{
	int tailleMsgRecu = 0, nbBytes = 0, fin;
	char buf[TAILLE_MSG];
	char* MsgRecu = (char*)malloc(sizeof(char)*TAILLE_MSG);

	memset(buf,0 ,sizeof(buf));
	//printf("\nRéception d'un message...\n");
	do
	{
			if((nbBytes = (recv(SocketHandle, buf, taille, 0))) != -1){
				memcpy((char*)MsgRecu + tailleMsgRecu, buf, nbBytes);
				tailleMsgRecu += nbBytes;
			}
			else
			{
				printf("\nErreur de réception du message !");
				close(SocketHandle);
			}
	}
	while(nbBytes != 0 && nbBytes != -1); //Tant qu'il y a des bytes à recevoir (et pas d'erreur)

	if(nbBytes != 0 && nbBytes != -1){
		MsgRecu[nbBytes]= '\0';
		printf("Message reçu : %s\n", MsgRecu);
		return MsgRecu; 
	}
}

//---------------------------------------------------------------------------------------
/*
Méthode 2 de receive : Caractère (ou chaine de caractères) de fin de séquence. Proposition : <EOM> (pour End of message) ?
//Attention à ne pas afficher le <EOM> -> deleteEOM
*/
int SocketRcvEOM(int SocketHandle, char *rcv, int taille)
{
	int nbBytes = 0;
		
	if((nbBytes = (recv(SocketHandle, rcv, TAILLE_MSG, 0))) != -1){
		deleteEOM(rcv, nbBytes);
	}
	else
	{
		printf("Erreur de recv\n");
		
	}
	return nbBytes;
}


//---------------------------------------------------------------------------------------
/*
Acquisition des infos sur l'ordinateur local
*/
struct hostent* getLocalHost()
{
	struct hostent* CurrentHost;

	if((CurrentHost = gethostbyname( getProperty("hostname") )) == 0){//récupère le hostname dans le fichier de config puis cherche les infos dessus
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
		struct in_addr adresseIP;
		memcpy(&adresseIP, CurrentHost->h_addr, CurrentHost->h_length);
		printf("\ngetLocalHost -> OK -- IP Address : %s\n", inet_ntoa(adresseIP));
		return CurrentHost;
	}
}

//---------------------------------------------------------------------------------------
/*
Préparation de la structure sockaddr_in

--- LA PREPARATION EST A FAIRE DANS LE FICHIER .C(PP) PRINCIPAL ! La structure doit être "préparée" avant d'être donnée en argument !
(l'ennui de la repréparer à chaque fois c'est que 1) perte de performances 2) On écrase les valeurs précédentes qui auraient pu être simplement réutilisées
*/
struct sockaddr_in initSocketAddress(struct sockaddr_in SocketAddress, struct hostent *CurrentHost, char* nomPort)
{
	memset(&SocketAddress, 0, sizeof(struct sockaddr_in));
	SocketAddress.sin_family = AF_INET; //domaine
	SocketAddress.sin_port = htons(getPort(nomPort)); //conversion du numéro de port au format réseau
	memcpy(&SocketAddress.sin_addr, CurrentHost->h_addr, CurrentHost->h_length);
	//printf("APRES MEMCPY : %s\n", inet_ntoa(SocketAddress.sin_addr));
	return SocketAddress;
}

#endif
