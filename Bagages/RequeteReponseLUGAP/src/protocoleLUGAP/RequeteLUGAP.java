/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package protocoleLUGAP;

import java.io.*;
import java.util.*;
import java.net.*;
import java.security.*;
import requetepoolthreads.ConsoleServeur;
import requetepoolthreads.Requete;
import static myutils.MyCrypto.*;

/**
 *
 * @author Arnaud
 */
public class RequeteLUGAP implements Requete, Serializable{

    public static int LOGIN = 1;
    public static int TEST = 0;
    
    private int type;
    private String chargeUtile;
    private Socket socketClient;
    
    public RequeteLUGAP(int t, String charge){
        type = t;
        chargeUtile = charge;
    }
    
    public RequeteLUGAP(int t, String charge, Socket s){
        type = t;
        chargeUtile = charge;
        socketClient = s;
    }
    
    @Override
    public Runnable createRunnable(Socket s, ConsoleServeur cs) {
        if(type == TEST){
            return new Runnable(){
                public void run(){
                    traiteRequeteTest(s,cs);
                }
            };
        }
        else if(type == LOGIN){
            return new Runnable(){
                public void run(){
                    traiteRequeteLogin(s,cs);
                }
            };

        }else
            return null;
    }
    
    private void traiteRequeteTest(Socket s, ConsoleServeur cs){
        System.out.println("Debut de traiteRequeteTest");
        System.out.println("Recu: [" + getChargeUtile() + "]");
        ReponseLUGAP rep = new ReponseLUGAP(ReponseLUGAP.OK, getChargeUtile() + " OK");
        
        ObjectOutputStream oos;
        try{
            oos = new ObjectOutputStream(s.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }catch(IOException e){
            System.out.println("Erreur d'accès au flux d'output: "+ e.getMessage());
        }
    }
    
    private void traiteRequeteLogin(Socket s, ConsoleServeur cs){
        boolean bool = false;
        ObjectOutputStream oos;
        String digest = getChargeUtile();
        ReponseLUGAP rep = null;
        
        System.out.println("traiteRequeteLogin: recu: [" + digest + "]");
        
        StringTokenizer parser = new StringTokenizer(digest, "|");
        String login = parser.nextToken();
        long temps = Long.parseLong(parser.nextToken());
        double alea = Double.parseDouble(parser.nextToken());
        
        try{
            String test = createDigestFull(login, "pwd", temps, alea); // à remplacer par une connection au serveur, récup le mot de passe sur base du login
            bool = compareDigest(digest, test);
        }catch(NoSuchAlgorithmException | NoSuchProviderException | IOException e){
            System.out.println("Erreur !");
        }

        if(bool){
            System.out.println("Digest OK");
            rep = new ReponseLUGAP(ReponseLUGAP.OK, "OK");
        }else{
            System.out.println("Digest NOT OK");
            rep = new ReponseLUGAP(ReponseLUGAP.NOK, "NOK");
        }

        try{
            oos = new ObjectOutputStream(s.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }catch(IOException e){
            System.out.println("Erreur d'accès au flux d'output: "+ e.getMessage());
        }
    }
    
    public String getChargeUtile(){
        return chargeUtile;
    }
    public void setChargeUtile(String charge){
        chargeUtile = charge;
    }    
}
