#ifndef CSVLIB_H
#define CSVLIB_H

#include "tcplib.h"

int CreateLoginFile(char *sep)
{
	FILE* fp = NULL;
	char Content[1024] = "";

	strcpy(Content, "LOGIN");
	strcat(Content, sep);
	strcat(Content, "PASSWORD\nadmin");
	strcat(Content, sep);
	strcat(Content, "admin\n");
	
	if((fp = fopen("login.csv", "r")) == NULL){ // ! Check si le fichier n'existe pas, pas question d'écraser l'ancien
		printf("login.csv n'existe pas, tentive de création...\n");
		if((fp = fopen("login.csv", "a+")) != NULL){
			printf("création OK\n");
			fwrite(Content, sizeof(char), strlen(Content), fp);
			printf("Initialisation du fichier OK\n");
			fclose(fp);
		}	
	}
	else
	{
		printf("Le fichier login.csv existe déjà\n");
		fclose(fp);
	}
}

int CreateTicketFile(char *sep)
{
	FILE* fp = NULL;
	
	char Content[1024] = "";
	strcpy(Content, "IDVOL");
	strcat(Content, sep);
	strcat(Content, "NUMBILLET\n");
	
	if((fp = fopen("ticket.csv", "r")) == NULL){ // ! Check si le fichier n'existe pas, pas question d'écraser l'ancien
		printf("ticket.csv n'existe pas, tentive de création...\n");
		if((fp = fopen("ticket.csv", "a+")) != NULL){
			printf("création OK\n");
			fwrite(Content, sizeof(char), strlen(Content), fp);
			printf("Initialisation du fichier OK\n");
			fclose(fp);
		}	
	}
	else
	{
		printf("Le fichier ticket.csv existe déjà\n");
		fclose(fp);
	}
}

int FetchRow(char* cle, char* valeur, char* file, char* sep)
{
	FILE* fp = NULL;
	int stop = 0;
	char row[100], *property, *password;

	if((fp = fopen(file, "r")) != NULL){
		do{
			if(fgets(row, 65, fp) == NULL){ //On parcoure le fichier ligne par ligne
				//printf("Erreur de lecture dans le fichier %s\n", file);
				stop = 1;
			}
			else{
				row[strlen(row)-1] = '\0';
				//printf("Après fgets : [%s]\n", row);
				property = strtok(row, sep); // On récupère juste la toute première valeur
				//printf("property : [%s]\ncle : [%s]\n", property, cle);
				password = strtok(NULL, sep);
				//printf("value : [%s]\n", password);
				if(strcmp(property, cle) == 0){
					printf("property trouvé\n");			
					strcpy(valeur, password);
					fclose(fp);
					return 1;
				}
			}
		}
		while(!feof(fp) && row != NULL && stop != 1);
		
		//On sort du while -> fin de fichier (en tout cas, j'espère)
	}
	else{
		printf("Impossible d'ouvrir le fichier spécifié\n");
		exit(1);
	}
	fclose(fp);
	return 0;
}

int EcrireCsv(char *file, char *cle, char*valeur, char *separator)
{
	FILE* fp = NULL;
	char ligne [1000];
	
	if((fp = fopen(file, "a")) != NULL){
		fputs(cle, fp);
		fputs(separator, fp);
		fputs(valeur, fp);
		fputs("\n", fp);
	}
	else{
		printf("Impossible d'ouvrir le fichier spécifié\n");
		fclose(fp);
		return 0;
	}
	fclose(fp);
	return 1;
}

int CreateLuggageLog(char* IDLuggage, char* TypeLuggage, char* sep)
{

	FILE *fp;
	int taille = strlen(IDLuggage) + 10;
	char nomFichier[taille];

	strcpy(nomFichier, IDLuggage);
	strcat(nomFichier, "_lug.csv");

	if((fp = fopen(nomFichier, "w")) != NULL){
		int tailleRow = (strlen(IDLuggage) + strlen(TypeLuggage) + strlen(sep) + 1);
		char row[tailleRow];
		strcpy(row, IDLuggage);
		strcat(row, sep);
		strcat(row, TypeLuggage);
		fwrite(row, sizeof(char), tailleRow, fp);
		printf("\nCréation du log %s OK\n", nomFichier);
		return 1;
	}
	else{
		printf("\nErreur de création de fichier log\n");
		return 0;
	}
}


#endif
