/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurpoolthread;

import java.net.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.*;
import static myutils.MyCrypto.*;
import static myutils.MyDBUtils.MyConnection;
import requetepoolthreads.ConsoleServeur;
import requetepoolthreads.Requete;
import static myutils.MyPropUtils.myGetProperty;

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
    
    public void run(){
        // Crée socket
        try{
            SSocket = new ServerSocket(port);
        }catch(IOException e){
            System.err.println("Erreur de port d'écoute : " + e);
            System.exit(1);
        }
        
        // Récupère nbThreads, login et password de connexion à la BD
        int nbThreads = 0;
        try{
            nbThreads = Integer.parseInt(myGetProperty("config.properties", "NB_THREADS"));
            loginBD = myGetProperty("config.properties", "LOGIN");
            pwdBD = myGetProperty("config.properties", "PASSWORD");
        }catch(FileNotFoundException e){
            System.err.println("Erreur: fichier properties non trouvé");
            System.exit(1);
        }catch(IOException e){
            System.err.println("Erreur d'IO: "+ e.getMessage());
            System.exit(1);
        }
        
        // Crée les threads
        for(int i=0; i < nbThreads; i++){
            ThreadClient thr = new ThreadClient (tachesAExecuter, "Thread du pool n°" + String.valueOf(i));
            thr.start();
        }
        
        // Connexion à la base de données
        Connection con = null;
        try{
            con = MyConnection(1, loginBD , pwdBD);
        }catch(SQLException e){
            System.err.println("Erreur de connexion à la base de données: "+ e.getMessage());
            System.exit(1);
        }
        
        // Attente d'une connexion
        Socket CSocket = null;
        while(!isInterrupted()){
            try{
                System.out.println("------- Serveur en attente");
                CSocket = SSocket.accept();
                guiApplication.TraceEvenements(CSocket.getRemoteSocketAddress().toString() + "#accept#Thread Serveur");
            }catch(IOException e){
                System.err.println("Erreur d'accept : " + e.getMessage());
                System.exit(1);
            }
            
            // Client connecté
            System.out.println("Client Accepté");
            ObjectInputStream ois = null;
            ObjectOutputStream oos = null;
            try{
                ois = new ObjectInputStream(CSocket.getInputStream());
                oos = new ObjectOutputStream(CSocket.getOutputStream());
            }catch(IOException e){
                System.err.println("Erreur: "+ e.getMessage());
            }
            
            Requete req = null;
            try{
                req = (Requete)ois.readObject();
                System.out.println("Requete lue par le serveur, instance de " + req.getClass().getName());
            }catch(ClassNotFoundException e){
                System.err.println("Erreur de definition de classe: "+ e.getMessage());
            }catch(IOException e){
                System.err.println("Erreur: "+ e.getMessage());
            }
            
            Runnable travail = req.createRunnable(CSocket, oos, ois, con, guiApplication);
            if(travail != null){
                tachesAExecuter.recordTache(travail);
                System.out.println("Travail mis en file d'attente");
            }
            else
                System.out.println("Pas de mise en file");
        }
    }
}
