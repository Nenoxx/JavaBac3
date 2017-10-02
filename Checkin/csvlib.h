#ifndef CSVLIB_H
#define CSVLIB_H

#include "tcplib.h"

/*Liste permettant de manipuler des chaines de caractères*/
typedef struct RowArray{
	char* rowElem;
	struct RowArray* psuiv;
}RowArray;

typedef struct RowList{
	RowArray *first;
	RowArray *list;
}RowList;

/*Fonctions permettant de gérer une liste de Row : */

/*Fonction permettant d'initialiser une liste NULL passée en paramètre.*/
RowList* ListInit(RowList* rlist)
{
	if(rlist == NULL){
		rlist = (RowList*)malloc(sizeof(RowList));
		if(rlist){

			rlist->first = NULL;
			rlist->list = NULL;

		}
		else{
			printf("Erreur lors de la création de la liste : Mémoire insuffisante\n");
			exit(1);
		}
	}	
}

void ListInsert(RowList* rlist, RowArray* elem)
{
	if(rlist != NULL){
		printf("1\n");
		if(rlist->first == NULL){
			printf("2\n");
			rlist->first = elem;
			printf("2.5\n");
			elem->psuiv = NULL;
		}
		else{
			printf("3\n");
			//Insertion en fin de liste
			RowArray *tmp = rlist->first;
			while(tmp->psuiv != NULL)
				tmp = tmp->psuiv;
			tmp->psuiv = elem;
			elem->psuiv = NULL;
		}
	}
	else
		printf("Liste nulle, gros naze\n");
}

//Supprime l'élément suivant de la position où on se trouve dans la liste (pas de pointeur vers l'arrière)
void ListRemove(RowList *rlist)
{
	if(rlist != NULL){
		if(rlist->list != NULL){ // deprecated, à changer
			RowArray *CurrentList = rlist->list;
			RowArray *NextItem = CurrentList->psuiv;
			CurrentList->psuiv = NextItem->psuiv;
			free(NextItem);
		}
		else{ //L'élément à supprimer est simplement le pointeur first
			if(rlist->first != NULL){
				free(rlist->first);
				rlist->first = NULL;
			}
			else
				printf("Rien à supprimer...\n");
		}
	}
}

//Permet de compter le nombre d'éléments..
int ListCount(RowList *rlist)
{
	if(rlist != NULL)
	{
		int i = 0;
		
		while(rlist->list->psuiv != NULL){
			rlist->list = rlist->list->psuiv;
			i++;
		}
		return i;
	}
}

RowArray* ElemInit(RowArray *elem, char* value)
{
	if(elem == NULL && value != NULL){
		elem = (RowArray*)malloc(sizeof(RowArray));
		elem->rowElem = (char*)malloc(strlen(value));
		strcpy(elem->rowElem, value);
		elem->psuiv = NULL;
		return elem;
	}
}

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

RowList* FetchRows(char* ID, char* file)
{
	FILE* fp = NULL;
	int stop = 0;
	char row[100], *property, sep[5];
	RowList* rlist = NULL;
	strcpy(sep, getProperty("separateur_fichier"));
	rlist = ListInit(rlist);

	if((fp = fopen(file, "r")) != NULL){
		do{
			if(fgets(row, 65, fp) == NULL){ //On parcoure le fichier ligne par ligne
				//printf("Erreur de lecture dans le fichier %s\n", file);
				stop = 1;
			}
			else{
				property = strtok(row, sep); // On récupère juste la toute première valeur
				//printf("\n\nID : [%s] -- property : [%s]\n\n", ID, property);
				if(strcmp(property, ID) == 0){
					//printf("coucou\n");
					RowArray *elem = NULL;
					elem = ElemInit(elem, row);
					printf("row = %s\n", row);
					ListInsert(rlist, elem);
				}
			}
		}
		while(!feof(fp) && row != NULL && stop != 1);
		
		//On sort du while -> fin de fichier (en tout cas, j'espère)
		if(ListCount(rlist) != 0) return rlist;
		else{ 
			printf("Aucune ligne n'a été sélectionnée\n");
			return NULL;
		}
	}
	else{
		printf("Impossible d'ouvrir le fichier spécifié\n");
		exit(1);
	}
}



#endif
