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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import requetepoolthreads.*;
import static myutils.MyCrypto.*;
import myutils.MyDBUtils;

/**
 *
 * @author Arnaud
 */
public class RequeteLUGAP implements Requete, Serializable{

    public static int TEST = 0;
    public static int LOGIN = 1;
    public static int LOGOUT = 2;
    public static int SQLQUERY = 3;
    public static int UPDATE = 4;
    
    
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
    public Runnable createRunnable(ObjectOutputStream oos, ObjectInputStream ois, Connection con, ConsoleServeur cs) {
       
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
        else if (type == LOGOUT){
            return new Runnable(){
                public void run(){
                    System.out.println("Traitement de requête Logout");
                    traiteRequeteLogout(oos, ois, con, cs);
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
        else if (type == UPDATE){
            return new Runnable(){
                public void run(){
                    System.out.println("Traitement de requête Update");
                    traiteRequeteUpdate(oos, ois, con, cs);
                }
            };
        }

        return null;
    }
    
    private void traiteRequeteTest(ObjectOutputStream oos, ObjectInputStream ois, Connection con, ConsoleServeur cs){
        System.out.println("traiteRequeteTest: recu: [" + getChargeUtile() + "]");
        ReponseLUGAP rep = new ReponseLUGAP(ReponseLUGAP.OK, getChargeUtile() + " OK");
        
        try{
            oos.writeObject(rep);
            oos.flush();
        }catch(IOException e){
            System.out.println("Erreur d'accès au flux d'output: "+ e.getMessage());
        }
    }
    
    private void traiteRequeteLogin(ObjectOutputStream oos, ObjectInputStream ois, Connection con, ConsoleServeur cs){
        boolean bool = false;
        
        String digest = getChargeUtile();
        ReponseLUGAP rep = null;
        
        System.out.println("traiteRequeteLogin: recu: [" + digest + "]");
        
        StringTokenizer parser = new StringTokenizer(digest, "|");
        String login = parser.nextToken();
        long temps = Long.parseLong(parser.nextToken());
        double alea = Double.parseDouble(parser.nextToken());
        
        try{
            String query = "select password from AGENTS where login like '" + login +"' and role = 'Bagagiste';";
            ResultSet rs = MyDBUtils.MySelect(query, con);
            rs.next();
            System.out.println("Mot de passe récupéré : " + rs.getString("password"));
            String test = createDigestFull(login, rs.getString(1), temps, alea);
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
            oos.writeObject(rep);
            oos.flush();
        }catch(IOException e){
            System.out.println("Erreur d'accès au flux d'output: "+ e.getMessage());
        }
    }
    
    private void traiteRequeteLogout(ObjectOutputStream oos, ObjectInputStream ois, Connection con, ConsoleServeur cs){
        System.out.println("traiteRequeteLogout");
        ReponseLUGAP rep = new ReponseLUGAP(ReponseLUGAP.OK, "Au revoir");     
        
        try{
            oos.writeObject(rep);
            oos.flush();
        }catch(IOException e){
            System.out.println("Erreur d'accès au flux d'output: "+ e.getMessage());
        }
    }
    
    private void traiteRequeteSQL(ObjectOutputStream oos, ObjectInputStream ois, Connection con, ConsoleServeur cs){
        System.out.println("traiteRequeteSQL: recu: [" + getChargeUtile() + "]");
        ReponseLUGAP rep = new ReponseLUGAP(ReponseLUGAP.OK, getChargeUtile() + " OK");
        String query = getChargeUtile();
        
        try{
            ResultSet res = MyDBUtils.MySelect(query, con);
            //On build une JTable temporaire contenant le résultat du ResultSet
            DefaultTableModel dtm = MyDBUtils.buildTableModel(res);
            oos.writeObject(dtm);
            oos.flush();
        }catch(IOException e){
            System.out.println("Erreur d'accès au flux d'output: "+ e.getMessage());
        }catch(SQLException e){
            System.out.println("Erreur d'accès au flux d'output: "+ e.getMessage());
        }
        
    }
    
    private void traiteRequeteUpdate(ObjectOutputStream oos, ObjectInputStream ois, Connection con, ConsoleServeur cs){
        PreparedStatement pStmt;
        
        // récup champs recus
        StringTokenizer parser = new StringTokenizer(getChargeUtile(), "|");
        String numBag = parser.nextToken();
        String recep = parser.nextToken();
        String charge = parser.nextToken();
        String verif = parser.nextToken();
        String rem = parser.nextToken();
        ReponseLUGAP rep = null;
        
        
        try {
            pStmt = con.prepareStatement("update BAGAGES set reception = ?, charge = ?, verifie = ?, remarque = ? where numBagage = ?;");
        
            pStmt.setString(1, recep);
            pStmt.setString(2, charge);
            pStmt.setString(3, verif);
            pStmt.setString(4, rem);
            pStmt.setString(5, numBag);
            pStmt.executeUpdate();
            rep = new ReponseLUGAP(ReponseLUGAP.OK, "OK");
        } catch (SQLException ex) {
            Logger.getLogger(RequeteLUGAP.class.getName()).log(Level.SEVERE, null, ex);
            rep = new ReponseLUGAP(ReponseLUGAP.NOK, "NOK");
          }
        
        try{
            oos.writeObject(rep);
            oos.flush();
        }
        catch(IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
        
        
        
    }
    
    
    public String getChargeUtile(){
        return chargeUtile;
    }
    public void setChargeUtile(String charge){
        chargeUtile = charge;
    } 
    public int getType(){
        return type;
    }
}
