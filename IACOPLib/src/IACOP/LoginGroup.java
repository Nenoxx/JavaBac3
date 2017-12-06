package IACOP;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Arnaud
 */
public class LoginGroup implements Serializable{
    boolean authorized;
    String user;
    byte[] pwd;
    boolean connexion;
    private String ip;
    private int port;
    long temps;
    double alea;

    public LoginGroup()
    {
        this.user = "";
        this.connexion = true;
        this.authorized = false;
        this.pwd = null;
    }

    public String getIp(){ return ip; }

    public void setIp(String ip){ this.ip = ip; }

    public int getPort(){ return port; }

    public void setPort(int port){ this.port = port; }
    
    public boolean askConnection(){ return connexion; }

    public void setAskConnection(boolean connexion){ this.connexion = connexion; }

    public String getUser(){ return user; }

    public void setUser(String user){ this.user = user; }

    public byte[] getPwd(){ return pwd; }

    public void setPwd(byte[] pwd) {
        try{            
            MessageDigest md = MessageDigest.getInstance("SHA-1", "BC");
            md.update(pwd); // ajoute le password
            temps = (new Date()).getTime();
            alea = Math.random();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream bdos = new DataOutputStream(baos);
            bdos.writeLong(temps); 
            bdos.writeDouble(alea); 
            md.update(baos.toByteArray());// ajoute date et nbr aléatoire
            this.pwd = md.digest(); // calcul du digest
            System.out.println("Digest: " +  Arrays.toString(this.pwd));
            
        }catch (NoSuchAlgorithmException | NoSuchProviderException | IOException ex){
            System.out.println("Erreur de création d'un salty digest:");
            System.out.println(ex);
            this.pwd = null;
        }
    }
    //TODO fonction comparaison digest
    public boolean comparePwd(byte[] pwd2){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1", "BC");
            md.update(pwd2); // ajoute le password
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream bdos = new DataOutputStream(baos);
            bdos.writeLong(temps); 
            bdos.writeDouble(alea); 
            md.update(baos.toByteArray());// ajoute date et nbr aléatoire
            pwd2 = md.digest(); // calcul du digest
            System.out.println("Digest à comparer: " +  Arrays.toString(pwd2));
            System.out.println("Digest original: " +  Arrays.toString(this.pwd));
            if(MessageDigest.isEqual(pwd, pwd2)){
                System.out.println("Ce sont les mêmes");
                return true;
            }
        } catch (NoSuchAlgorithmException | NoSuchProviderException | IOException ex) {
            System.out.println("Erreur de comparaison de password:");
            System.out.println(ex);
        }
        System.out.println("Ce ne sont pas les mêmes");
        return false;
    }
    
    public boolean isAuthorized(){ return authorized; }

    public void setAuthorized(boolean authorized){ this.authorized = authorized;}
}
