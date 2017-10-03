#ifndef CSVLIB_H
#define CSVLIB_H

#include "tcplib.h"

int CreateLoginFile()
{
	FILE* fp = NULL;
	char Content[1024] = "", sep[5];
	strcpy(sep, getProperty("separateur_fichier"));

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

int CreateTicketFile()
{
	FILE* fp = NULL;
	
	char Content[1024] = "", sep[5];
	strcpy(sep, getProperty("separateur_fichier"));
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

int FetchRow(char* cle, char* valeur, char* file)
{
	FILE* fp = NULL;
	int stop = 0;
	char row[100], *property, *password, sep[5];
	strcpy(sep, getProperty("separateur_fichier"));

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



#endif
