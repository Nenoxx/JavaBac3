/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseUtils;
import java.sql.*;

/**
 *
 * @author nenoxx
 */
public class MyDBUtils {
    
    //Retourne un ResultSet contenant le résultat du SELECT.
    public static synchronized ResultSet MySelect(String query, Connection con) throws SQLException{
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        System.out.println("Requête exécutée : " + query);
        return rs;
    }
    
    //Retourne le nombre de lignes mises à jour
    public static synchronized int MyUpdate(String query, Connection con) throws SQLException{
        Statement st = con.createStatement();
        System.out.println("Requête exécutée : " + query);
        return st.executeUpdate(query);
    }
    
    public static synchronized Connection MyConnection(int DBType, String login, String pwd) throws SQLException
    {
        Connection con = null;
        if(DBType == 1){ //1 = MySQL
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BD_AIRPORT", login, pwd);
            System.out.println("Connexion établie à la BDD MySQL");
        }
        else if(DBType == 2){
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", login, pwd);
            System.out.println("Connexion établie à la BDD Oracle");
            Statement st = con.createStatement();
        }
        
        return con;
    }
    
    
    
    
}
