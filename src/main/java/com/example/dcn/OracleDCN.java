/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.dcn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;
import oracle.jdbc.OracleStatement;
import oracle.jdbc.dcn.DatabaseChangeEvent;
import oracle.jdbc.dcn.DatabaseChangeListener;
import oracle.jdbc.dcn.DatabaseChangeRegistration;

/**
 *
 * @author wjirawong
 */
public class OracleDCN {
    static final String USERNAME = "sti_common";
    static final String PASSWORD = "MkzcxbAm7ayMXlSfWpQh";
    static String URL = "jdbc:oracle:thin:@172.16.73.16:1521:stingdev";
    
    public static void main(String[] args) {
        OracleDCN oracleDCN = new OracleDCN();
        try {
            oracleDCN.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void run() throws Exception{
        OracleConnection conn = connect();
        Properties prop = new Properties();
        prop.setProperty(OracleConnection.DCN_NOTIFY_ROWIDS, "true");
        DatabaseChangeRegistration dcr = conn.registerDatabaseChangeNotification(prop);
        
        try{
            dcr.addListener(new DatabaseChangeListener() {

                public void onDatabaseChangeNotification(DatabaseChangeEvent dce) {
                    System.out.println("Changed row id : "+dce.getTableChangeDescription()[0].getRowChangeDescription()[0].getRowid().stringValue());
                }
            });
            
            Statement stmt = conn.createStatement();
            ((OracleStatement) stmt).setDatabaseChangeRegistration(dcr);
            ResultSet rs = stmt.executeQuery("select * from AIR_TEST where I_AIR_BOOKING1_ID=1");
            while (rs.next()) {
            }
            rs.close();
            stmt.close();
        }catch(SQLException ex){
            if (conn != null)
            {
                conn.unregisterDatabaseChangeNotification(dcr);
                conn.close();
            }
            throw ex;
        }
    }

    OracleConnection connect() throws SQLException {
        OracleDriver dr = new OracleDriver();
        Properties prop = new Properties();
        prop.setProperty("user", OracleDCN.USERNAME);
        prop.setProperty("password", OracleDCN.PASSWORD);
        return (OracleConnection) dr.connect(OracleDCN.URL, prop);
    }
}
