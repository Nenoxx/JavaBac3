/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.utilities;
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
        return rs;
    }
    
    //Retourne le nombre de lignes mises à jour
    public static synchronized int MyUpdate(String query, Connection con) throws SQLException{
        Statement st = con.createStatement();
        return st.executeUpdate(query);
    }
    
    
    
}
