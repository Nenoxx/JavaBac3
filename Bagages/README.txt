Modifs avant de continuer:
au lieu de passer des sockets dans le serveur passer directement les flux oos et ois ?
passer �galement la connexion � la base de donn�es puisque n�cessaire dans la plupart des cas

r�parer les requ�tes test avant d'en cr�er des nouvelles


---> D�placer le readObject du ThreadServeur dans le ThreadClient (sinon, il ne le fait qu'une seule fois, pour le login et puis on ne fait plus aucune attente sur un readObject pour savoir quand le client envoie des requ�tes)
