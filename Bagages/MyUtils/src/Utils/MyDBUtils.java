/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
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
    
    public static DefaultTableModel buildTableModel(ResultSet rs)
        throws SQLException {

    ResultSetMetaData metaData = rs.getMetaData();

    // names of columns
    Vector<String> columnNames = new Vector<String>();
    int columnCount = metaData.getColumnCount();
    for (int column = 1; column <= columnCount; column++) {
        columnNames.add(metaData.getColumnName(column));
    }

    // data of the table
    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
    while (rs.next()) {
        Vector<Object> vector = new Vector<Object>();
        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
            vector.add(rs.getObject(columnIndex));
        }
        data.add(vector);
    }

    return new DefaultTableModel(data, columnNames);

}
}
