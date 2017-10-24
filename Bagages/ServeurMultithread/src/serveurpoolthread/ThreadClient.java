/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurpoolthread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import requetepoolthreads.ConsoleServeur;
import requetepoolthreads.Requete;

/**
 *
 * @author Arnaud
 */
public class ThreadClient extends Thread{
    private SourceTaches tachesAExecuter;
    private String nom;
    private Socket CSocket = null;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;
    private Connection con = null;
    private ConsoleServeur cs = null;
    
    private Runnable tacheEnCours;
    
    public ThreadClient(SourceTaches st, String n){
        tachesAExecuter = st;
        nom = n;
    }
    
    public void run(){
        while(!isInterrupted()){
            
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
            
            Runnable travail = req.createRunnable(CSocket, oos, ois, con, cs);
            if(travail != null){
                tachesAExecuter.recordTache(travail);
                System.out.println("Travail mis en file d'attente");
            }
            else
                System.out.println("Pas de mise en file");
            
            try{
                System.out.println("Thread client avant get");
                tacheEnCours = tachesAExecuter.getTache();
            }catch(InterruptedException e){
                System.out.println("Interruption: " + e.getMessage());
            }
            System.out.println("Run de TacheEnCours");
            tacheEnCours.run();
        }
    }
    
    public void setParameters(Socket s, Connection con, ConsoleServeur cs){
        CSocket = s;
        this.con = con;
        this.cs = cs;
    }
}
