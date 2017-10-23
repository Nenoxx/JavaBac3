/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientpoolthreads;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import protocoleLUGAP.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static myutils.MyPropUtils.myGetProperty;
import static myutils.MyCrypto.*;
/**
 *
 * @author Arnaud
 */

public class FenAppClient extends javax.swing.JFrame {
    Connection con;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket cliSocket;
    
    /**
     * Creates new form FenAppCLient
     */
    public FenAppClient() {
        initComponents();
    }
    
    public FenAppClient(String login, String password){
        initComponents();
        
        //infos sur le serveur du fichier config
        ois = null;
        oos = null;
        cliSocket = null;
        String adresse = null;
        int port = 0;
        try{
            port = Integer.parseInt(myGetProperty("config.properties", "PORT_SERVEUR"));
            adresse = myGetProperty("config.properties", "IP_SERVEUR");
        }catch(FileNotFoundException e){
            JOptionPane.showMessageDialog(this, "Fichier de config du client non trouvé", "Warning", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }catch(IOException e){
            JOptionPane.showMessageDialog(this, "Erreur d'IO: " + e.getLocalizedMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
        System.out.println("Récupérations d'infos sur le serveur OK");
        
        // connection au serveur
        try{
            cliSocket = new Socket(adresse, port);
            System.out.println(cliSocket.getInetAddress().toString());
            oos = new ObjectOutputStream(cliSocket.getOutputStream());
            
        }catch(UnknownHostException e){
            System.err.println("Erreur: host non trouvé");
            System.exit(0);
        }catch(IOException e){
            System.err.println("Erreur d'IO: "+ e.getMessage());
            System.exit(0);
        }
        System.out.println("Connecté au serveur");
        
        // envoi du login + digest salé
        RequeteLUGAP req = null;
        //showProviders();
        try{
            req = new RequeteLUGAP(RequeteLUGAP.LOGIN, createDigest(login, password));
            oos.writeObject(req);
            oos.flush();
        }catch(NoSuchAlgorithmException e){
            System.err.println("Erreur, l'algorithme de digest est introuvable");
            System.exit(0);
        }catch(NoSuchProviderException e){
            System.err.println("Erreur, le security provider est introuvable");
            System.exit(0);
        }catch(IOException e){
            System.err.println("Erreur d'IO: "+ e.getMessage());
            System.exit(0);
        }
        System.out.println("Login envoié");
        
        // réponse du serveur
        ReponseLUGAP rep = null;
        try{
            ois = new ObjectInputStream(cliSocket.getInputStream());
            rep = (ReponseLUGAP)ois.readObject();
            System.out.println("Reponse recue: "+ rep.getChargeUtile());
        }catch(ClassNotFoundException e){
            System.err.println("Erreur sur la classe" + e.getMessage());
            System.exit(0);
        }catch(IOException e){
            System.err.println("Erreur d'IO" + e.getMessage());
            System.exit(0);
        }
        // Si OK
        if(rep.getCode() == ReponseLUGAP.OK){
            LConnecte.setText("Connecté en tant que: " + login);
        }
        else{
            JOptionPane.showMessageDialog(this, "Impossible de se connecter à la base de données", "Warning", JOptionPane.WARNING_MESSAGE);
        }  
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TFRequete = new javax.swing.JTextField();
        BEnvoyer = new javax.swing.JButton();
        LReponse = new javax.swing.JLabel();
        LConnecte = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Application_Bagages");

        BEnvoyer.setText("Envoyer");
        BEnvoyer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BEnvoyerActionPerformed(evt);
            }
        });

        LConnecte.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(LReponse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(LConnecte, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(BEnvoyer)
                        .addGap(18, 18, 18)
                        .addComponent(TFRequete, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LConnecte)
                .addGap(80, 80, 80)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TFRequete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BEnvoyer))
                .addGap(71, 71, 71)
                .addComponent(LReponse)
                .addContainerGap(101, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BEnvoyerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BEnvoyerActionPerformed
        String chargeUtile = TFRequete.getText();
        RequeteLUGAP req = null;
        
        req = new RequeteLUGAP(RequeteLUGAP.TEST, chargeUtile); 
        
        //envoi de la requete
        try{
            //oos = new ObjectOutputStream(cliSocket.getOutputStream());
            oos.writeObject(req);
            oos.flush();
        }catch(IOException e){
            System.err.println("Erreur réseau ? "+ e.getMessage());
        }
        System.out.println("Requete envoiée");
        
        //lecture de la reponse
        ReponseLUGAP rep = null;
        try{
            rep = (ReponseLUGAP)ois.readObject();
            System.out.println("Reponse recue: "+ rep.getChargeUtile());
        }catch(ClassNotFoundException e){
            System.err.println("Erreur sur la classe" + e.getMessage());
        }catch(IOException e){
            System.err.println("Erreur d'IO" + e.getMessage());
        }
        LReponse.setText(rep.getChargeUtile());
    }//GEN-LAST:event_BEnvoyerActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FenAppClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FenAppClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FenAppClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FenAppClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FenAppClient().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BEnvoyer;
    private javax.swing.JLabel LConnecte;
    private javax.swing.JLabel LReponse;
    private javax.swing.JTextField TFRequete;
    // End of variables declaration//GEN-END:variables
}
