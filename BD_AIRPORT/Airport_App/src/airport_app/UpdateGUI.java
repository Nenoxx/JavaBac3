/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airport_app;
import database.utilities.MyDBUtils;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author nenoxx
 */
public class UpdateGUI extends javax.swing.JDialog {
    Connection conn;
    int TypeDB;
    /**
     * Creates new form UpdateGUI
     */
    
    public UpdateGUI(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
    }
    
    public UpdateGUI(java.awt.Frame parent, boolean modal, Connection con, int type) {
        super(parent, modal);
        initComponents();
        TypeDB = type;
        TableCB.removeAllItems();
        ColonneCB.removeAllItems();
        ValeurCB.removeAllItems();
        conn = con;
        
        try{
            //On récupère toutes les tables
            if(TypeDB == 1){
                DatabaseMetaData m = conn.getMetaData();
                ResultSet tables = m.getTables(conn.getCatalog(), null, "%", null);
                while(tables.next()){
                    TableCB.addItem(tables.getString("TABLE_NAME"));
                }
            }
            else{
                Statement st = con.createStatement();
                DatabaseMetaData m = con.getMetaData();
                String query = "select object_name from user_objects where object_type = 'TABLE'";
                ResultSet tables = MyDBUtils.MySelect(query, conn);
                while(tables.next()){
                    TableCB.addItem(tables.getString(1));
                }
            }
        }
        catch(SQLException ex)
        {
            System.out.println("Han ouais : " + ex.getLocalizedMessage());
        }
        
        TableCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                try {
                    Statement st = conn.createStatement();
                    ResultSet rs = null;
                    String query = "";
                    //On récupère le nom des colonnes pour la table sélectionnée
                    if(TypeDB == 1) {
                        query = "select * from information_schema.COLUMNS where table_name like '" + (String)TableCB.getSelectedItem() + "';";
                        rs = MyDBUtils.MySelect(query, conn);
                    }
                    else{
                        query = "select column_name from user_tab_cols where table_name = '" + (String)TableCB.getSelectedItem() + "'";
                        rs = MyDBUtils.MySelect(query, conn);
                    }
                    ColonneCB.removeAllItems();
                    while(rs.next())
                    {
                        ColonneCB.addItem(rs.getString("COLUMN_NAME"));
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getLocalizedMessage());
                }
            }
        });
        
        ColonneCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                try{
                    //On va lister toutes les valeurs de la colonne de la table à chaque fois
                    //que la colonne est changée (dans la combobox)
                    Statement st = conn.createStatement();
                    ResultSet rs = null;
                    String query = "";
                    if(TypeDB == 1){
                        query = "select " + (String)ColonneCB.getSelectedItem() + " from " + (String)TableCB.getSelectedItem() + ";";
                        rs = MyDBUtils.MySelect(query, conn);
                        ValeurCB.removeAllItems();
                        while(rs.next())
                        {
                            System.out.println(rs.getString(1));
                            ValeurCB.addItem((String)rs.getString(1));
                        }
                    }
                    else{
                        query = "select " + (String)ColonneCB.getSelectedItem() + " from " + (String)TableCB.getSelectedItem();
                        rs = MyDBUtils.MySelect(query, conn);
                        ValeurCB.removeAllItems();
                        int i = 0;
                        while(rs.next())
                        {
                            //System.out.println(rs.getObject(i));
                            ValeurCB.addItem((String)rs.getString(1));
                            i++;
                        }
                    }
                    System.out.println("Query executed");
                    
                }
                catch(SQLException ex)
                {
                    System.out.println(ex.getLocalizedMessage());
                }
            }
        });
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        TableCB = new javax.swing.JComboBox<>();
        ColonneCB = new javax.swing.JComboBox<>();
        ValeurCB = new javax.swing.JComboBox<>();
        NouvelleValeurTF = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Table à éditer  :");

        jLabel2.setText("Colonne à éditer :");

        jLabel3.setText("Valeur à remplacer : ");

        jLabel4.setText("Nouvelle valeur :");

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Annuler");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        TableCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        ColonneCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        ValeurCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(NouvelleValeurTF, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(ValeurCB, javax.swing.GroupLayout.Alignment.TRAILING, 0, 225, Short.MAX_VALUE)
                        .addComponent(ColonneCB, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(TableCB, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(TableCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(ColonneCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(ValeurCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NouvelleValeurTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(47, 47, 47)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(NouvelleValeurTF.getText().isEmpty()) //HELA CAMARADE ! T'ESSAIES DE NIQUER MA BDD?
        {
            JOptionPane.showMessageDialog(new JFrame(),
                            "La valeur à remplacer ne peut pas être vide !",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
        }
        else{
            try {
                //On crée une requête update avec les différents composants des ComboBox
                String query = "update " + (String)TableCB.getSelectedItem() + " set " + (String)ColonneCB.getSelectedItem() + " = '"
                                + NouvelleValeurTF.getText() + "' where " + (String)ColonneCB.getSelectedItem() + " = '" + (String)ValeurCB.getSelectedItem() +"'";
                if(TypeDB == 1) query += ";";
                if(MyDBUtils.MyUpdate(query, conn) > 0){
                    System.out.println("Requête exécutée avec succes");
                    this.setVisible(false);
                }
            } catch (SQLException ex) {
                System.out.println("Gloups : " + ex.getLocalizedMessage());
            }
            
        }
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(UpdateGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UpdateGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UpdateGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UpdateGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                UpdateGUI dialog = new UpdateGUI(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ColonneCB;
    private javax.swing.JTextField NouvelleValeurTF;
    private javax.swing.JComboBox<String> TableCB;
    private javax.swing.JComboBox<String> ValeurCB;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    // End of variables declaration//GEN-END:variables
}
