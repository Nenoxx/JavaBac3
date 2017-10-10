/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airport_app;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;
import database.utilities.MyDBUtils;

/* A FAIRE 
-Faire en sorte que la JTable s'adapte aux colonnes de chaque table de chaque DB
-Se connecter à telle ou telle DB selon les paramètres passés au constructeur
*/

/**
 *
 * @author nenoxx
 */
public class Airport_GUI extends javax.swing.JFrame {
    Connection con;
    int TypeDB;
    /**
     * Creates new form Airport_GUI
     */
    
    public Airport_GUI(){
        initComponents();
    }
    
    public Airport_GUI(int DBType, String login, String pwd) {
        initComponents();
        TableCB.removeAllItems();
        try{
            if(DBType == 1){ //1 = MySQL
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BD_AIRPORT", login, pwd);
                System.out.println("Connexion établie à la BDD MySQL");
                DatabaseMetaData m = con.getMetaData();
                ResultSet tables = m.getTables(con.getCatalog(), null, "%", null);
                while(tables.next()){
                    TableCB.addItem(tables.getString("TABLE_NAME"));
                }
                TypeDB = 1;
            }
            else if(DBType == 2){
                con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", login, pwd);
                System.out.println("Connexion établie à la BDD Oracle");
                Statement st = con.createStatement();
                DatabaseMetaData m = con.getMetaData();
                String query = "select object_name from user_objects where object_type = 'TABLE'";
                ResultSet tables = MyDBUtils.MySelect(query, con);
                while(tables.next()){
                    TableCB.addItem(tables.getString(1));
                }
                TypeDB = 2;
            }
            
            
        }
        catch(SQLException ex)
        {
            //System.out.println("Han ouais : " + ex.getLocalizedMessage());
            JOptionPane.showMessageDialog(new JFrame(),
                            ex.getLocalizedMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
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

        CategorieLabel = new javax.swing.JLabel();
        ListButton = new javax.swing.JButton();
        AnnulerButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table = new javax.swing.JTable();
        TableCB = new javax.swing.JComboBox<>();
        ModifierButton = new javax.swing.JButton();
        CompterButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        CategorieLabel.setText("Catégorie :");

        ListButton.setText("Lister");
        ListButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ListButtonActionPerformed(evt);
            }
        });

        AnnulerButton.setText("Annuler");
        AnnulerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AnnulerButtonActionPerformed(evt);
            }
        });

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
        jScrollPane1.setViewportView(Table);

        TableCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        ModifierButton.setText("Modifier");
        ModifierButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModifierButtonActionPerformed(evt);
            }
        });

        CompterButton.setText("Compter");
        CompterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CompterButtonActionPerformed(evt);
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
                        .addComponent(CategorieLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(TableCB, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ListButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AnnulerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ModifierButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CompterButton, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CategorieLabel)
                    .addComponent(ListButton)
                    .addComponent(AnnulerButton)
                    .addComponent(ModifierButton)
                    .addComponent(TableCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CompterButton))
                .addGap(30, 30, 30)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ListButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ListButtonActionPerformed
        try {
            //Simplement une requête SELECT
            Statement st = con.createStatement();
            String query = "select * from " + (String)TableCB.getSelectedItem();
            ResultSet rs = MyDBUtils.MySelect(query, con);
            //On modifie la JTable pour qu'elle se mette directement à jour en fonction
            //du nombre de colonnes, des noms, valeurs, etc...
            Table.setModel(DbUtils.resultSetToTableModel(rs));
            //-> marche pour MySQL, pas pour Oracle :(
            
        } catch (SQLException ex) {
            System.out.println("Han ouais : " + ex.getLocalizedMessage());
        }
    }//GEN-LAST:event_ListButtonActionPerformed

    private void ModifierButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModifierButtonActionPerformed
        UpdateGUI g = new UpdateGUI(this, true, con, TypeDB);  
        g.setVisible(true); //C'est modal, pas besoin d'attendre une valeur de retour
    }//GEN-LAST:event_ModifierButtonActionPerformed

    private void AnnulerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnnulerButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_AnnulerButtonActionPerformed

    private void CompterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CompterButtonActionPerformed
        try {
            Statement st = con.createStatement();
            String query = "select count(*) as total from " + (String)TableCB.getSelectedItem();
            if(TypeDB == 1) query += ";";
            ResultSet rs = MyDBUtils.MySelect(query, con);
            rs.next();
            JOptionPane.showMessageDialog(new JFrame(),
                            "Nombre de tuples : " + rs.getInt("total"),
                            "Résultat de la requête",
                            JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(new JFrame(),
                            ex.getLocalizedMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_CompterButtonActionPerformed

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
            java.util.logging.Logger.getLogger(Airport_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Airport_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Airport_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Airport_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Airport_GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AnnulerButton;
    private javax.swing.JLabel CategorieLabel;
    private javax.swing.JButton CompterButton;
    private javax.swing.JButton ListButton;
    private javax.swing.JButton ModifierButton;
    private javax.swing.JTable Table;
    private javax.swing.JComboBox<String> TableCB;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
