/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientpoolthreads;

import ProtocoleLUGAP.ReponseLUGAP;
import ProtocoleLUGAP.RequeteLUGAP;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import static Utils.MyPropUtils.myGetProperty;
import static Utils.MyCrypto.*;
import Utils.MyDBUtils;
import net.proteanit.sql.DbUtils;
/**
 *
 * @author Arnaud
 */

public class FenAppClient extends javax.swing.JFrame {
    Connection con;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket cliSocket;
    FenAppClient thisGUI = this; //Besoin pour le passer en param dans l'évent MousePressed
    
    /**
     * Creates new form FenAppCLient
     */
    public FenAppClient() {
        initComponents();
    }
    
    public FenAppClient(String login, String password){
        initComponents();
        
        /*
        ((DefaultTableModel)Table.getModel()).setRowCount(0);
        Table.addMouseListener(new MouseAdapter() {
             public void MouseClicked(java.awt.event.MouseEvent evt){
                 EventMouseClicked(evt);
            }
        });
        */
        
        Table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                // do some actions here, for example
                // print first column value from selected row
                System.out.println("Selected row : " + Table.getSelectedRow());
            }
        });
        
        Table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        Table.getTableHeader().setReorderingAllowed(false);
        Table.getTableHeader().setResizingAllowed(false);
        Table.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        Table.getColumnModel().setColumnSelectionAllowed(false);
        
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

        LReponse = new javax.swing.JLabel();
        LConnecte = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table = new javax.swing.JTable();
        AfficherVolButton = new javax.swing.JButton();
        QuitterButton = new javax.swing.JButton();
        BagagesButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Application_Bagages");

        Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        Table.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Table.setRowSelectionAllowed(true);
        Table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(Table);

        AfficherVolButton.setText("Afficher les vols");
        AfficherVolButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AfficherVolButtonActionPerformed(evt);
            }
        });

        QuitterButton.setText("Quitter");
        QuitterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                QuitterButtonActionPerformed(evt);
            }
        });

        BagagesButton.setText("Bagages");
        BagagesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BagagesButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(AfficherVolButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BagagesButton)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(QuitterButton))
                            .addComponent(LConnecte, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 574, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LReponse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(QuitterButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LConnecte)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(180, 180, 180)
                        .addComponent(LReponse))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(AfficherVolButton)
                            .addComponent(BagagesButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void AfficherVolButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AfficherVolButtonActionPerformed
        String query = "select * from VOLS";
        try {
            //1) Envoi de la demande au serveur d'exécuter la requête SQL
            RequeteLUGAP req = new RequeteLUGAP(RequeteLUGAP.SQLQUERY, query);
            
            oos.writeObject(req);
            oos.flush();
            
            //2) Attente d'une réponse
            DefaultTableModel dtm = (DefaultTableModel)ois.readObject();
            Table.setModel(dtm);
        } catch (Exception ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }//GEN-LAST:event_AfficherVolButtonActionPerformed

    private void TableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableMouseClicked
       if(evt.getClickCount() == 2)
        {
            System.out.println("Doouble-click détecté");
            if(Table.getSelectedRow() != -1){
                int numVol;
                String row = (String)Table.getValueAt(Table.getSelectedRow(), 0);
                if(row.length() < 3)
                    System.out.println("Erreur dans la récupération des valeurs de la table");
                else{
                    numVol = ((Integer)Table.getValueAt(Table.getSelectedRow(), 5));
                    System.out.println(numVol);
                    FenLugage fen = new FenLugage(thisGUI, true, numVol, oos, ois);
                    fen.setVisible(true);
                }
            }
            else
                System.out.println("Erreur de détection");
        }
    }//GEN-LAST:event_TableMouseClicked

    private void QuitterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_QuitterButtonActionPerformed
        try {
            //1) Envoi de la demande au serveur d'exécuter la requête SQL
            RequeteLUGAP req = new RequeteLUGAP(RequeteLUGAP.LOGOUT, "Déconnexion");
            
            oos.writeObject(req);
            oos.flush();
            
            //2) Attente d'une réponse
            ReponseLUGAP rep = (ReponseLUGAP) ois.readObject();
        
            // Si OK
            if(rep.getCode() == ReponseLUGAP.OK){
                JOptionPane.showMessageDialog(this, "Vous êtes déconnecté", "Info", JOptionPane.INFORMATION_MESSAGE);            }
            else{
                JOptionPane.showMessageDialog(this, "Erreur lors de la déconnection", "Erreur", JOptionPane.ERROR_MESSAGE);
            } 
        }catch(IOException e){
            System.err.println("Erreur d'IO: "+ e.getMessage());
        }catch(ClassNotFoundException e){
            System.err.println("Classe non trouvée: "+ e.getMessage());
        }
        System.exit(0);
    }//GEN-LAST:event_QuitterButtonActionPerformed

    private void BagagesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BagagesButtonActionPerformed
        if(Table.getSelectedRow() != -1){
            int numVol;
            String row = (String)Table.getValueAt(Table.getSelectedRow(), 0);
            if(row.length() < 3)
                System.out.println("Erreur dans la récupération des valeurs de la table");
            else{
                numVol = ((Integer)Table.getValueAt(Table.getSelectedRow(), 5));
                System.out.println(numVol);
                FenLugage fen = new FenLugage(thisGUI, true, numVol, oos, ois);
                fen.setVisible(true);
            }
        }
        else
            JOptionPane.showMessageDialog(this, "Un vol doit être sélectionné", "Info", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_BagagesButtonActionPerformed
    
    private void EventMouseClicked(java.awt.event.MouseEvent evt){
        
    }
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
    private javax.swing.JButton AfficherVolButton;
    private javax.swing.JButton BagagesButton;
    private javax.swing.JLabel LConnecte;
    private javax.swing.JLabel LReponse;
    private javax.swing.JButton QuitterButton;
    private javax.swing.JTable Table;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
