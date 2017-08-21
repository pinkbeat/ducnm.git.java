package com.vsc.sync.openldap;
import java.sql.*;
import java.util.*;
import oracle.jdbc.*;
import oracle.jdbc.dcn.*;
import oracle.jdbc.dcn.RowChangeDescription.*;
 
public class UsersChangeNotify {
  String URL = "jdbc:oracle:thin:his_esb/123@123.31.27.51:1521:db01";
  Properties prop;
 
  public static void main(String[] argv) {
 
    UsersChangeNotify dcn = new UsersChangeNotify();
    try {
      dcn.prop = new Properties();
      dcn.run();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
 
  void run() throws SQLException {
    OracleConnection conn = (OracleConnection)DriverManager.getConnection(URL,prop);
 
    Properties prop = new Properties();
    prop.setProperty(OracleConnection.DCN_NOTIFY_ROWIDS,"true");
    DatabaseChangeRegistration dcr = conn.registerDatabaseChangeNotification(prop);
 
    try {
      dcnListener list = new dcnListener(this);
      dcr.addListener(list);
 
      Statement stmt = conn.createStatement();
      ((OracleStatement)stmt).setDatabaseChangeRegistration(dcr);
      ResultSet rs = stmt.executeQuery("select * from adm_user where 1 = 2");
      rs.close();
      stmt.close();
    }
    catch(Exception e) {
      //clean up our registration
      if(conn != null)
        conn.unregisterDatabaseChangeNotification(dcr);
      e.printStackTrace();
    }
    finally {
      try {
        conn.close();
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }
 
    try {
      Thread.currentThread().join();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
 
    finally {
      OracleConnection conn3 = (OracleConnection)DriverManager.getConnection(URL,prop);
      conn3.unregisterDatabaseChangeNotification(dcr);
      conn3.close();
    }
  }
}
 
class dcnListener implements DatabaseChangeListener {
  UsersChangeNotify dcn;
  dcnListener(UsersChangeNotify dem) {
    dcn = dem;
  }
 
  public void onDatabaseChangeNotification(DatabaseChangeEvent e) {
    TableChangeDescription[] tc = e.getTableChangeDescription();
 
    for (int i = 0; i < tc.length; i++) {
      RowChangeDescription[] rcds = tc[i].getRowChangeDescription();
      for (int j = 0; j < rcds.length; j++) {
    	System.out.println(rcds[j].getRowOperation() + " " + rcds[j].getRowid().stringValue());
	  }
	}
    synchronized( dcn ){
      dcn.notify();
    }
  }
}