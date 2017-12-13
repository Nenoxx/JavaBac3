/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProtocoleLUGAP;

import java.io.*;
import java.util.*;
import java.net.*;
import java.security.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import requetepoolthreads.*;
import static Utils.MyCrypto.*;
import Utils.MyDBUtils;
import requetepoolthreads.ConsoleServeur;

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
    public static int LOGINNS = 5;

    //Pour android
    public static int GETVOL = 6;
    public static int GETBAGAGE = 7;
    public static int UPDATE_BAGAGE = 8;


    private int type;
    private String chargeUtile;
    private Socket socketClient;
    private static final long serialVersionUID = 124L;

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
    public Runnable createRunnable(final ObjectOutputStream oos, final ObjectInputStream ois, final Connection con, final ConsoleServeur cs) {

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
        else if (type == LOGINNS){
            return new Runnable(){
                public void run(){
                    System.out.println("Traitement de requête Login non-securisé");
                    traiteRequeteLoginNS(oos, ois, con);
                }
            };
        }
        else if (type == GETVOL){
            return new Runnable(){
                public void run(){
                    System.out.println("Traitement de requête GETVOL (Android)");
                    getVol(oos, ois, con, null);
                }
            };
        }
        else if (type == GETBAGAGE){
            return new Runnable(){
                public void run(){
                    System.out.println("Traitement de requête GETBAGAGE(Android)");
                    getBagage(oos, ois, con, null);
                }
            };
        }
        else if (type == UPDATE_BAGAGE){
            return new Runnable(){
                public void run(){
                    System.out.println("Traitement de requête GETBAGAGE(Android)");
                    updateBagage(oos, ois, con, null);
                }
            };
        }

        return null;
    }

    public Runnable createRunnable (final ObjectOutputStream oos, final ObjectInputStream ois, final Connection con){
        if(type == TEST){
            return new Runnable(){
                public void run(){
                    System.out.println("Traitement requête Test");
                    traiteRequeteTest(oos, ois, con, null);
                }
            };
        }
        else if(type == LOGIN){
            return new Runnable(){
                public void run(){
                    System.out.println("Traitement requête Login");
                    traiteRequeteLogin(oos, ois, con, null);
                }
            };

        }
        else if (type == LOGOUT){
            return new Runnable(){
                public void run(){
                    System.out.println("Traitement de requête Logout");
                    traiteRequeteLogout(oos, ois, con, null);
                }
            };
        }
        else if (type == SQLQUERY){
            return new Runnable(){
                public void run(){
                    System.out.println("Traitement de requête SQL");
                    traiteRequeteSQL(oos, ois, con, null);
                }
            };
        }
        else if (type == UPDATE){
            return new Runnable(){
                public void run(){
                    System.out.println("Traitement de requête Update");
                    traiteRequeteUpdate(oos, ois, con, null);
                }
            };
        }
        else if (type == LOGINNS){
            return new Runnable(){
                public void run(){
                    System.out.println("Traitement de requête Login non-securisé");
                    traiteRequeteLoginNS(oos, ois, con);
                }
            };
        }
        else if (type == GETVOL){
            return new Runnable(){
                public void run(){
                    System.out.println("Traitement de requête GETVOL (Android)");
                    getVol(oos, ois, con, null);
                }
            };
        }
        else if (type == GETBAGAGE){
            return new Runnable(){
                public void run(){
                    System.out.println("Traitement de requête GETBAGAGE(Android)");
                    getBagage(oos, ois, con, null);
                }
            };
        }
        else if (type == UPDATE_BAGAGE){
            return new Runnable(){
                public void run(){
                    System.out.println("Traitement de requête GETBAGAGE(Android)");
                    updateBagage(oos, ois, con, null);
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

    private void traiteRequeteLoginNS(ObjectOutputStream oos, ObjectInputStream ois, Connection con){
        String[] credentials = getChargeUtile().split(";");
        ReponseLUGAP rep = null;
        String query = "select password from AGENTS where login like ?";
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, credentials[0]);
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                if(credentials[1].equals(rs.getString("password"))){
                    rep = new ReponseLUGAP(ReponseLUGAP.OK, "OK");
                }
                else{
                    rep = new ReponseLUGAP(ReponseLUGAP.NOK, "NOK");
                }
            }
            else{
                rep = new ReponseLUGAP(ReponseLUGAP.NOK, "NOK");
            }

            oos.writeObject(rep);
            oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
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
            /*ResultSet res = MyDBUtils.MySelect(query, con);
            //On build une JTable temporaire contenant le résultat du ResultSet
            DefaultTableModel dtm = MyDBUtils.buildTableModel(res);
            oos.writeObject(dtm);
            oos.flush();*/
        }catch(Exception e){
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

    public void getVol(ObjectOutputStream oos, ObjectInputStream ois, Connection con, ConsoleServeur cs){
        System.out.println("traiteRequeteSQL: recu: [" + getChargeUtile() + "]");
        ReponseLUGAP rep = new ReponseLUGAP(ReponseLUGAP.OK, getChargeUtile() + " OK");
        String query = getChargeUtile();

        try{
            ResultSet res = MyDBUtils.MySelect(query, con);

            ArrayList<String> ListeVols = new ArrayList<String>();
            while(res.next()){
                ListeVols.add(res.getString("numVol"));
                System.out.println("Element ajouté : " + res.getString("numVol"));
            }
            oos.writeObject(ListeVols);
            oos.flush();
            System.out.println("Liste de vols envoyée correctement");
        }catch(Exception e){
            System.out.println("Erreur d'accès au flux d'output: "+ e.getMessage());
        }
    }

    public void getBagage(ObjectOutputStream oos, ObjectInputStream ois, Connection con, ConsoleServeur cs)
    {
        System.out.println("traiteRequeteSQL: recu: [" + getChargeUtile() + "]");
        ReponseLUGAP rep = new ReponseLUGAP(ReponseLUGAP.OK, getChargeUtile() + " OK");
        String query = getChargeUtile();

        try{
            ResultSet res = MyDBUtils.MySelect(query, con);

            ArrayList<String> Liste = new ArrayList<String>();
            while(res.next()){
                Liste.add(res.getString("numBagage") + ";" + res.getString("charge"));
                System.out.println("Element ajouté : " + res.getString("numBagage"));
            }
            oos.writeObject(Liste);
            oos.flush();
            System.out.println("Liste de bagages envoyée correctement");
        }catch(Exception e){
            System.out.println("Erreur d'accès au flux d'output: "+ e.getMessage());
        }
    }

    public void updateBagage(ObjectOutputStream oos, ObjectInputStream ois, Connection con, ConsoleServeur cs){
        System.out.println("traiteRequeteSQL: recu: [" + getChargeUtile() + "]");
        String query = getChargeUtile();

        try{
            int res = MyDBUtils.MyUpdate(query, con);

            if(res > 0){
                oos.writeObject(new ReponseLUGAP(ReponseLUGAP.OK, "OK"));
                oos.flush();
            }
            else{
                oos.writeObject(new ReponseLUGAP(ReponseLUGAP.NOK, "NOK"));
                oos.flush();
            }

            System.out.println("Bagage OK");
        }catch(Exception e){
            System.out.println("Erreur d'accès au flux d'output: "+ e.getMessage());
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
