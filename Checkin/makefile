.SILENT :

ALL: Client Serveur

Client:	Client.cpp tcplib.h
	echo Creation de client...
	g++ -w Client.cpp -o Client

Serveur: Serveur.cpp tcplib.h
	echo Creation de serveur...
	g++ -w Serveur.cpp -lpthread -o Serveur
