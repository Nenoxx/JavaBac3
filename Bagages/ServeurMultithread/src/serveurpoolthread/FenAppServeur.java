/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurpoolthread;

import java.util.*;
import javax.swing.table.*;
import java.io.*;
import requetepoolthreads.ConsoleServeur;
import static myutils.MyPropUtils.myGetProperty;

/**
 *
 * @author Arnaud
 */
public class FenAppServeur extends javax.swing.JFrame implements ConsoleServeur {

    private int port;
    /**
     * Creates new form FenAppServeur
     */
    public FenAppServeur() {
        initComponents();
        TraceEvenements("Serveur#Initialisation#Main");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ServeurLabel = new javax.swing.JLabel();
        ScrollPaneTableauEvenements = new javax.swing.JScrollPane();
        TableauEvenements = new javax.swing.JTable();
        BStart = new javax.swing.JButton();
        BStop = new javax.swing.JButton();
        PortLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ServeurLabel.setText("Serveur");

        TableauEvenements.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Qui ?", "Quoi ?", "Ou ?"
            }
        ));
        ScrollPaneTableauEvenements.setViewportView(TableauEvenements);

        BStart.setText("Start");
        BStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BStartActionPerformed(evt);
            }
        });

        BStop.setText("Stop");
        BStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BStopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ScrollPaneTableauEvenements, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BStart)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BStop)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(ServeurLabel)
                        .addGap(76, 76, 76)
                        .addComponent(PortLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(270, 270, 270)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ServeurLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PortLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BStart)
                    .addComponent(BStop))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ScrollPaneTableauEvenements, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BStartActionPerformed
        try{
            port = Integer.parseInt(myGetProperty("config.properties", "PORT_BAGAGES"));
        }catch(FileNotFoundException e){
            System.err.println("Erreur: Impossible d'ouvrir le fichier de config");
            System.exit(1);
        }catch(IOException e){
            System.err.println("Erreur d'IO: "+ e.getMessage());
            System.exit(1);
        }
        
        PortLabel.setText("Port: " + port);
        TraceEvenements("Serveur#Port obtenu#Main");
        ThreadServeur ts = new ThreadServeur(port, new ListeTaches(), this);
        ts.start();        
    }//GEN-LAST:event_BStartActionPerformed

    private void BStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BStopActionPerformed
        System.exit(0);
    }//GEN-LAST:event_BStopActionPerformed

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
            java.util.logging.Logger.getLogger(FenAppServeur.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FenAppServeur.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FenAppServeur.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FenAppServeur.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FenAppServeur().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BStart;
    private javax.swing.JButton BStop;
    private javax.swing.JLabel PortLabel;
    private javax.swing.JScrollPane ScrollPaneTableauEvenements;
    private javax.swing.JLabel ServeurLabel;
    private javax.swing.JTable TableauEvenements;
    // End of variables declaration//GEN-END:variables

    @Override
    public void TraceEvenements(String commentaire) {
        Vector ligne = new Vector();
        StringTokenizer parser = new StringTokenizer(commentaire, "#");
        while(parser.hasMoreTokens()){
            ligne.add(parser.nextToken());
        }
        DefaultTableModel dtm = (DefaultTableModel) TableauEvenements.getModel();
        dtm.insertRow(dtm.getRowCount(), ligne);
    }
}
