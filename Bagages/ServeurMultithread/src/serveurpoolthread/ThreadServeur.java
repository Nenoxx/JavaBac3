/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurpoolthread;

import java.net.*;
import java.io.*;
import java.sql.*;
import static Utils.MyDBUtils.MyConnection;
import requetepoolthreads.ConsoleServeur;
import static Utils.MyPropUtils.myGetProperty;

/**
 *
 * @author Arnaud
 */
public class ThreadServeur extends Thread {
    private int port;
    private SourceTaches tachesAExecuter;
    private ConsoleServeur guiApplication;
    private ServerSocket SSocket = null;
    String loginBD;
    String pwdBD;
    
    public ThreadServeur(int p, SourceTaches st, ConsoleServeur cs){
        port = p;
        tachesAExecuter = st;
        guiApplication = cs;
    }
    
    @Override
    public void run(){
        
        // Crée socket
        try{
            SSocket = new ServerSocket(port);
        }catch(IOException e){
            System.err.println("Thread Serveur: Erreur de port d'écoute : " + e);
            System.exit(1);
        }
        
        // Récupère nbThreads, login et password de connexion à la BD
        int nbThreads = 0;
        try{
            nbThreads = Integer.parseInt(myGetProperty("config.properties", "NB_THREADS"));
            loginBD = myGetProperty("config.properties", "LOGIN");
            pwdBD = myGetProperty("config.properties", "PASSWORD");
        }catch(FileNotFoundException e){
            System.err.println("Thread Serveur: Erreur: fichier properties non trouvé");
            System.exit(1);
        }catch(IOException e){
            System.err.println("Thread Serveur: Erreur d'IO: "+ e.getMessage());
            System.exit(1);
        }
        
        // Connexion à la base de données
        Connection con = null;
        try{
            con = MyConnection(1, loginBD , pwdBD);
        }catch(SQLException e){
            System.err.println("Thread Serveur: Erreur de connexion à la base de données: "+ e.getMessage());
            System.exit(1);
        }
        
        // Crée les threads
        for(int i=0; i < nbThreads; i++){
            ThreadClient thr = new ThreadClient (tachesAExecuter, i+1);
            thr.setParameters(con, guiApplication);
            thr.start();
        }
        
        // Attente d'une connexion
        Socket CSocket = null;
        while(!isInterrupted()){
            try{
                System.out.println("------- Serveur en attente -------");
                CSocket = SSocket.accept();
                guiApplication.TraceEvenements(CSocket.getRemoteSocketAddress().toString() + "#Nouveau client accepté#Thread Serveur");
            }catch(IOException e){
                System.err.println("Thread Serveur: Erreur d'accept : " + e.getMessage());
                System.exit(1);
            }
            
            // signale qu'une nouvelle tâche est disponible
            tachesAExecuter.recordTache(CSocket);
        }
    }
}
