/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serIAChat;

import IACOP.LoginGroup;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 *
 * @author Arnaud
 */
public class ThreadTCP extends Thread{

    private boolean running = true;
    private int portTCP;
    private int portUDP;
    private String ipUDP;
    private ServerSocket socketEcoute;
    private Socket socketService;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean authorized= false;
    JLabel label;
    
    private Connection con = null;
    
    public ThreadTCP(int portTCP, int portUDP, String ipUDP, JLabel label) {
        this.portTCP = portTCP;
        this.portUDP = portUDP;
        this.ipUDP = ipUDP;
        this.label = label;
        try {
            socketEcoute = new ServerSocket(portTCP);
        } catch (IOException ex) {
            System.out.println("ERREUR THREAD TCP constructeur: " + ex);
            System.exit(1);
        }
        
        // se connecter à la BD
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BD_AIRPORT", "arnaud", "arnaud");
        }catch (SQLException ex) {
            System.out.println("Impossible de se connecter à la BDD: " + ex);
            System.exit(1);
        }
        System.out.println("Connexion établie à la BDD MySQL");
    }
    
    @Override
    public void run() {
        while(running){
            authorized = false;
            try {
                System.out.println("En attente d'un client...");
                socketService = socketEcoute.accept();
                System.out.println("Nouveau client");
                oos = new ObjectOutputStream(socketService.getOutputStream());
                oos.flush();
                ois = new ObjectInputStream(socketService.getInputStream());
                
            } catch (IOException ex) {
                System.out.println("ERREUR THREAD TCP 1: " + ex);
            }
            
            try {
                LoginGroup lg = (LoginGroup)ois.readObject();
                if(lg.askConnection()){ // le client demande à se connecter
                    System.out.println("Demande de connexion");
                    
                    PreparedStatement pst;
                    ResultSet rs;
                    
                    if(lg.getPwd() == null){// client -> num billet
                        System.out.println("Il s'agit d'un client");

                        try {
                            pst = con.prepareStatement("Select * from BILLETS where numBillet=?"); // TODO
                            pst.setString(1, lg.getUser());
                            rs = pst.executeQuery();
                            if(rs.next()){
                                lg.setUser(rs.getString("prenom") + " " + rs.getString("nom"));
                                authorized = true;
                            }  
                        } catch (SQLException ex) {
                            Logger.getLogger(ThreadTCP.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }else{// sinon -> agent
                        System.out.println("Il s'agit d'un agent");                        
                        // récup mot de passe
                        try {
                            pst = con.prepareStatement("Select password from AGENTS where login=?");
                            pst.setString(1, lg.getUser());
                            rs = pst.executeQuery();
                            if (rs.next()) {
                                String pass = rs.getString("password");
                                authorized = lg.comparePwd(pass.getBytes());
                            }else{
                                 authorized = false;
                            }  
                        } catch (SQLException ex) {
                            Logger.getLogger(ThreadTCP.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if(authorized){ // fabrique le login group
                        System.out.println("Autorisé");
                        
                        //mise à jour du compteur
                        String l = label.getText();
                        int num = Integer.parseInt(l) +1;
                        label.setText(Integer.toString(num));
                        
                        lg.setAuthorized(true);
                        lg.setAskConnection(false);
                        lg.setIp(ipUDP);
                        lg.setPort(portUDP);
                        oos.writeObject(lg);
                        oos.flush();
                    }
                    else{
                        System.out.println("Pas autorisé");
                        lg.setAuthorized(false);
                        lg.setAskConnection(true);
                        oos.writeObject(lg);
                        oos.flush();
                    }
                }
                else{
                    System.out.println("Demande de déconnexion");
                }
                    
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(ThreadTCP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Fin du thread TCP");
        try {
            socketEcoute.close();
            socketService.close();
        } catch (IOException ex) {
            System.out.println("ERREUR THREAD TCP: socket.close():" + ex);
        }
    }
    
    public void setRunning(boolean running){
        this.running = running;
    }
}
