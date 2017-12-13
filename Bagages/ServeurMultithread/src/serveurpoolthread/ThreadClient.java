/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurpoolthread;

import ProtocoleLUGAP.RequeteLUGAP;
import java.io.*;
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
    private int num;
    private int numReq;
    private boolean connecte = false;
    
    private Socket CSocket = null;
    
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;
    
    private Connection con = null;
    private ConsoleServeur cs = null;
        
    public ThreadClient(SourceTaches st, int n){
        tachesAExecuter = st;
        num = n;
    }
    
    @Override
    public void run(){
        while(!isInterrupted()){
            
            try{
                System.out.println("Thread Client " + num + " avant get");
                CSocket = tachesAExecuter.getTache();
            }catch(InterruptedException e){
                System.err.println("Interruption: " + e.getMessage());
                System.exit(1);
            }
            System.out.println("Thread client " + num + ": prise en charge d'un client");
            
            // récupère les flux
            try{
                ois = new ObjectInputStream(CSocket.getInputStream());
                oos = new ObjectOutputStream(CSocket.getOutputStream());
            }catch(IOException e){
                System.err.println("Erreur: "+ e.getMessage());
                System.exit(1);
            }
            System.out.println("Thread client " + num + ": Flux ouverts");
            Requete req = null;
            connecte = true;
            numReq = 0;
            
            // Tant que le client ne se déconnecte pas
            do{
                // récupère une requête
                try{
                    req = (Requete)ois.readObject();
                    numReq++;
                }catch(ClassNotFoundException e){
                    System.err.println("Erreur de definition de classe: "+ e.getMessage());
                    System.exit(1);
                }catch(IOException e){
                    System.err.println("Erreur: "+ e.getMessage());
                    connecte = false;
                }
                
                if(connecte){
                System.out.println("Thread client " + num + ": nouvelle requete " + numReq + " recue");
                cs.TraceEvenements(CSocket.getRemoteSocketAddress().toString()+"#Nouvelle requête(" + numReq + ")#Thread Client " + num);

                // crée un runnable travail
                RequeteLUGAP tmp = (RequeteLUGAP)req;
                System.out.println("Type requête : " + tmp.getType());
                Runnable travail = req.createRunnable(oos, ois, con, cs);
                // demande à travail de faire le job de gestion de la requête
                travail.run();
                System.out.println("Thread client " + num + ": requete " + numReq + " traitée");
                }else{
                    cs.TraceEvenements(CSocket.getRemoteSocketAddress().toString()+"#Client parti#Thread Client " + num);
                }
                
            }while(connecte);
        }
    }
    
    public void setParameters(Connection con, ConsoleServeur cs){
        this.con = con;
        this.cs = cs;
    }
}
