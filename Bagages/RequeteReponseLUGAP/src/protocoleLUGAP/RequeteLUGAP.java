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
import java.sql.*;
import requetepoolthreads.*;
import static myutils.MyCrypto.*;
import myutils.MyDBUtils;

/**
 *
 * @author Arnaud
 */
public class RequeteLUGAP implements Requete, Serializable{

    public static int LOGIN = 1;
    public static int TEST = 0;
    public static int SQLQUERY = 2;
    public static int LOGOUT = 10;
    
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
    public Runnable createRunnable(Socket s, ObjectOutputStream oos, ObjectInputStream ois, Connection con, ConsoleServeur cs) {
        do
        {
            if(type == TEST){
                return new Runnable(){
                    public void run(){
                        System.out.println("Traitement requête Test");
                        traiteRequeteTest(oos, ois, con, cs);
                    }
                };
            }
            else if(type == LOGIN){
                return new Runnable(){
                    public void run(){
                        System.out.println("Traitement requête Login");
                        traiteRequeteLogin(oos, ois, con, cs);
                    }
                };

            }
            else if (type == SQLQUERY){
                return new Runnable(){
                    public void run(){
                        System.out.println("Traitement de requête SQL");
                        traiteRequeteSQL(oos, ois, con, cs);
                    }
                };
            }
        }
        while(type != LOGOUT);
        
        return null;
    }
    
    private void traiteRequeteTest(ObjectOutputStream oos, ObjectInputStream ois, Connection con, ConsoleServeur cs){
        System.out.println("Debut de traiteRequeteTest");
        System.out.println("Recu: [" + getChargeUtile() + "]");
        ReponseLUGAP rep = new ReponseLUGAP(ReponseLUGAP.OK, getChargeUtile() + " OK");
        
        //ObjectOutputStream oos;
        try{
            //oos = new ObjectOutputStream(s.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }catch(IOException e){
            System.out.println("Erreur d'accès au flux d'output: "+ e.getMessage());
        }
    }
    
    private void traiteRequeteLogin(ObjectOutputStream oos, ObjectInputStream ois, Connection con, ConsoleServeur cs){
        boolean bool = false;
        //ObjectOutputStream oos;
        String digest = getChargeUtile();
        ReponseLUGAP rep = null;
        
        System.out.println("traiteRequeteLogin: recu: [" + digest + "]");
        
        StringTokenizer parser = new StringTokenizer(digest, "|");
        String login = parser.nextToken();
        long temps = Long.parseLong(parser.nextToken());
        double alea = Double.parseDouble(parser.nextToken());
        
        try{
            String query = "select password from AUTHENTICATION where login like '" + login +"';";
            ResultSet rs = MyDBUtils.MySelect(query, con);
            rs.next();
            System.out.println("Mot de passe récupéré : " + rs.getString("password"));
            String test = createDigestFull(login, rs.getString(1), temps, alea); // à remplacer par une connection au serveur, récup le mot de passe sur base du login
            bool = compareDigest(digest, test);
        }catch(NoSuchAlgorithmException | NoSuchProviderException | IOException e){
            System.out.println("Erreur !");
        }catch(SQLException ex){
            System.out.println("Erreur SQL ! :" + ex.getLocalizedMessage());
        }

        if(bool){
            System.out.println("Digest OK");
            rep = new ReponseLUGAP(ReponseLUGAP.OK, "OK");
        }else{
            System.out.println("Digest NOT OK");
            rep = new ReponseLUGAP(ReponseLUGAP.NOK, "NOK");
        }

        try{
            //oos = new ObjectOutputStream(s.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }catch(IOException e){
            System.out.println("Erreur d'accès au flux d'output: "+ e.getMessage());
        }
    }
    
    private void traiteRequeteSQL(ObjectOutputStream oos, ObjectInputStream ois, Connection con, ConsoleServeur cs){
        System.out.println("Debut de traiteRequeteTest");
        System.out.println("Recu: [" + getChargeUtile() + "]");
        ReponseLUGAP rep = new ReponseLUGAP(ReponseLUGAP.OK, getChargeUtile() + " OK");
        String query = getChargeUtile();
        
        
        //ObjectOutputStream oos;
        try{
            ResultSet rs = MyDBUtils.MySelect(query, con);
            //oos = new ObjectOutputStream(s.getOutputStream());
            oos.writeObject(rs);
            oos.flush();
        }catch(IOException e){
            System.out.println("Erreur d'accès au flux d'output: "+ e.getMessage());
        }catch(SQLException e){
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
