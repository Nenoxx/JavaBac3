#include "tcplib.h"
#include "csvlib.h"
#include "cimp.h"

#include <signal.h>

void handlerSIGINT(int sig);
void clear_screen();

int SocketClient;

int main()
{
	struct sockaddr_in SocketAddress; // contient port et ip de la socket
	struct hostent *infosHost; // infos sur la machine
	struct in_addr adrIP;
	struct sigaction act;
	
	char fCli[]="client.config";
	char msg[TAILLE_MSG] = {0}, rcv[TAILLE_MSG]={0}, login[30], password[30], *separator, *separator2, session[5]="N/A";
	int req=0, choix;
	int retour=0, loginok = 0, deco = 0;
	
	separator = getProperty(fCli, "separateur_CIMP");
	separator2 = getProperty(fCli, "separateur_fichier");
	
	//armement de SIGINT
	act.sa_handler = handlerSIGINT;
	sigemptyset(&act.sa_mask);
	act.sa_flags = 0;
	sigaction(SIGINT, &act, 0);
	

	//1) Création de la socket
	SocketClient = CreateSocket(SocketClient);
	
	//2) Informations sur l'ordinateur DISTANT
	if((infosHost = gethostbyname(getProperty(fCli, "hostname")))==0){
		printf("CLI> erreur acquisition infos sur le host distant\n");
		exit(1);
	}
	memcpy(&adrIP, infosHost->h_addr, infosHost->h_length);
	printf("CLI> hote distant: ip: %s\n", inet_ntoa(adrIP));
	
	
	//3) Préparation de la struct sockaddr_in
	SocketAddress = initSocketAddress(SocketAddress, infosHost, "port_serveur", fCli);
	

	//4) Connexion
	ClientConnect(SocketClient, SocketAddress);
	printf("\nCLI> connexion au serveur...\n");
		
	//si réception NOK
	SocketRcvEOM(SocketClient, rcv, TAILLE_MSG);
	req = getRequest(separator, rcv);
	if(req == NOK){
		CloseSocket(SocketClient);
		printf("CLI> Serveur plein, retenter plus tard\n");
		exit(1);
	}
	printf("CLI> Connecté au serveur\n");
	
	do{
		clear_screen();
		int cpt = 0;
		printf("#### APPLICATION CHECK-IN ####\n\n\n");		
		do{
			printf("Entrez votre login:");
			scanf("%s", login);
			printf("Entrez votre password:");
			scanf("%s", password);
		
			strcpy(msg, login);
			strcat(msg, separator2);
			strcat(msg, password);
			SocketSendReqEOM(SocketClient, LOGIN_OFFICER, separator, msg);
			SocketRcvEOM(SocketClient, msg, TAILLE_MSG);
			req = getRequest(separator, msg);
			strcpy(session, msg);
			if(req == 10){
				loginok = 1;
				deco = 0;
			}
		
			cpt++;
		}while(cpt < 5 && loginok == 0);
		if(cpt == 5){
			CloseSocket(SocketClient);
			printf("CLI> Trop de tentative infructueuses\n");
			exit(1);
		}
		printf("CLI> connexion autorisée\n");
		
			
		while(deco == 0){
			char r;
			do{
				clear_screen();
				printf("MENU PRINCIPAL\nNumero de session: %s\n\n\t1) Enregistrer des billets\n\t2) Déconnexion\n\n\tVotre choix : ", session);

				r = getchar();
			}while(r != '1' && r != '2');

			switch(r){
			case '1':
				{ //mettre le bloc (les crochets) sinon le compilateur râle.
				char billet[20], sep[5], msg[TAILLE_MSG] = "", rcv[TAILLE_MSG], tmp[10], numvol[5], ArrayValise[TAILLE_MSG] = "", TypeValise[10], c;
				int nbPersonnes = 0, prix_exces = 0, typeRequete = -1;
				float poids = 0, poids_tot = 0;
				short volOK = 1, billetOK = 1, nbPassOK = 1, LugOK = 1;//Booleens de protocole

				//init
				strcpy(sep, separator2);
		
				do{
					//1) Encodage du numéro de vol
					printf("\nNuméro de vol : ");
					scanf("%s", numvol);
					//1.1) Envoi de la requête au serveur
					SocketSendReqEOM(SocketClient, NUM_VOL, separator, numvol);
					//1.2) Réception de la réponse du serveur
					SocketRcvEOM(SocketClient, rcv, TAILLE_MSG);
					typeRequete = getRequest(separator, rcv);
					if(typeRequete == OK){
						volOK = 1;
						printf("\nCLI> Vol confirmé\n");
						do{
							//2) Encodage du numéro du billet
							printf("\nNuméro de billet : ");
							scanf("%s", billet);
							//2.1) Envoi de la requête au serveur
							SocketSendReqEOM(SocketClient, NUM_BILLET, separator, billet);
							//2.2) Réception de la réponse du serveur
							SocketRcvEOM(SocketClient, rcv, TAILLE_MSG);
							typeRequete = getRequest(separator, rcv);
							if(typeRequete == OK){
								printf("\nCLI> Billet confirmé\n");
								billetOK = 1;
								do{
									//3) Encodage du nombre de personnes
									printf("\nNombre d'accompagnants : ");
									scanf("%d", &nbPersonnes);
									sprintf(msg, "%d", nbPersonnes);
									//3.1) Envoi de la requête au serveur
									SocketSendReqEOM(SocketClient, NBR_PASS, separator, msg);
									//3.2) Réception de la réponse du serveur
									SocketRcvEOM(SocketClient, rcv, TAILLE_MSG);
									typeRequete = getRequest(separator, rcv);
									if(typeRequete == OK){
										nbPassOK = 1;
										printf("\nCLI> Nombre d'accompagnants confirmé\n");
										//4) Encodage des différents bagages
										strcpy(msg, "\0"); // vide msg
										strcpy(ArrayValise, "\0");
										for(int i = 0; i < nbPersonnes; i++)
										{
											printf("\nPoids du baggage num. %d : ", i+1);
											scanf("%f", &poids);
											poids_tot += poids;
											sprintf(tmp, "%.2f", poids);
											strcat(msg, tmp);
										
											printf("\nType de baggage num. %d : ", i+1);
											scanf("%s", TypeValise);
											strcat(ArrayValise, TypeValise);

											if(i != nbPersonnes - 1){
												strcat(msg, separator2); //pour ne pas mettre un séparateur en fin de chaine qui foutra la merde dans la lecture de la requête.
												strcat(ArrayValise, separator2);
											}
										}
										//4.1) Envoi de la requête au serveur
										SocketSendReqEOM(SocketClient, CHECK_LUGGAGE, separator, msg);	
										//4.2) Réception de la réponse du serveur
										SocketRcvEOM(SocketClient, rcv, TAILLE_MSG);
										typeRequete = getRequest(separator, rcv);
										if(typeRequete == OK){ 
											char *p;
											float poids_ex;
											p = strtok(rcv, separator2);
											poids_ex = atof(p);
											if(poids_ex > 0){ //Cas ou il y a un supplément à payer
												p = strtok(NULL, separator2);
												prix_exces = atof(p);
												printf("\n-----------\nPoids total des baggages : %.2f\nPoids excessif : %.2f\nCout supplémentaire : %.2f euros\n-----------\n", poids_tot, poids_ex, prix_exces);
												do//6) Vérification du paiement
												{	
													printf("Paiement effectué? (o/n): ");					
													c = getchar();
												}
												while(c != 'o' && c != 'O' && c!= 'n' && c != 'N');
												if(c == 'o' || c == 'O'){
													printf("CLI> Le client a payé\n");
													//6.1) Envoie au serveur que le clien a payé
													SocketSendReqEOM(SocketClient, PAIEMENT, separator, "Paiement en ordre");
													SocketSendReqEOM(SocketClient, BAGAGES, separator, ArrayValise);
													SocketRcvEOM(SocketClient, rcv, TAILLE_MSG);
													typeRequete = getRequest(separator, rcv);
													if(typeRequete == OK){
														printf("CLI> Tickets validés, bon voyage !\n");
														printf("CLI> Appuyez sur Enter...");
														getchar();getchar();
													}
													else{
														printf("CLI> Erreur de validation des tickets, recommencez\n");
														printf("CLI> Appuyez sur Enter...");
														getchar();getchar();
													}					
												}
												else
												{
													SocketSendReqEOM(SocketClient, PAIEMENT, separator, "Paiement pas en ordre");
													printf("CLI> Le client n'a pas payé, enregistrement annulé\n");
													printf("CLI> Appuyez sur Enter...");
													getchar();getchar();
												}
											}
											else{// pas de surpoids
												SocketSendReqEOM(SocketClient, BAGAGES, separator, ArrayValise);
												SocketRcvEOM(SocketClient, rcv, TAILLE_MSG);
												typeRequete = getRequest(separator, rcv);
												if(typeRequete == OK){
													printf("CLI> Tickets validés, bon voyage !\n");
													printf("CLI> Appuyez sur Enter...");
													getchar();getchar();
												}
												else{
													printf("CLI> Erreur de validation des tickets, recommencez\n");
												}		
											}
										}
									}
									else{
										printf("CLI> Erreur nombre d'accompagnants\n");
										nbPassOK = 0;
									}
								}while(nbPassOK == 0);
							}else{
								printf("CLI> Billet non-existant\n");
								billetOK = 0;
							}
						}while(billetOK == 0);
					}
					else{
						printf("CLI> Vol non-existant\n");
						volOK = 0;
					}
				}while(volOK == 0);

				break;
				}
			case '2':
				{
				 SocketSendReqEOM(SocketClient, LOGOUT_OFFICER, separator, "Deconnexion");	
				 deco = 1; 
				 break;
				}
			default: break;
			}
		}
	}while(deco == 1);
		
	CloseSocket(SocketClient);
	return 0;
}

//---------------------------------------------------------------------------------------
/*
Réception d'un SIGINT
*/
void handlerSIGINT(int sig)
{
	char msg[TAILLE_MSG];
	int req;
	printf("\nCLI> CTRL+C détecté\n");
	SocketSendReqEOM(SocketClient, LOGOUT_OFFICER, getProperty("client.config", "separateur_CIMP"), "Deconnexion");
	SocketRcvEOM(SocketClient, msg, TAILLE_MSG);
	req = getRequest(getProperty("client.config", "separateur_CIMP"), msg);
	if(req == OK)
		printf("CLI> Deconnexion autorisée\n");
	CloseSocket(SocketClient);
	exit(1);
}

void clear_screen()
{
	printf("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
	fflush(stdout);
} 

