Modifs avant de continuer:
au lieu de passer des sockets dans le serveur passer directement les flux oos et ois ?
passer également la connexion à la base de données puisque nécessaire dans la plupart des cas

réparer les requêtes test avant d'en créer des nouvelles


---> Déplacer le readObject du ThreadServeur dans le ThreadClient (sinon, il ne le fait qu'une seule fois, pour le login et puis on ne fait plus aucune attente sur un readObject pour savoir quand le client envoie des requêtes)
