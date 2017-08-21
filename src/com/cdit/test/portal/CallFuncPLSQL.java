package com.cdit.test.portal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;

public class CallFuncPLSQL {
	public static void main(String asd[]){
		try {
			(new CallFuncPLSQL()).update_cash_mgr(0, 0, 42472, "13123", 1);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void update_cash_mgr(int f1,int f2,int cash_id,String acc,int status) throws ClassNotFoundException, SQLException{
		Class.forName("oracle.jdbc.driver.OracleDriver");
		  Connection con=DriverManager.getConnection("jdbc:oracle:thin:@10.1.3.163:1521:bivnpt","support","123");
		  System.out.println("Connection created..............");
		  String call = "{ ? = call support.update_cash_mgr(?,?,?,?,?) }";
		  CallableStatement cstmt = con.prepareCall(call);
		  cstmt.setQueryTimeout(1800);
		  cstmt.registerOutParameter(1, Types.NUMERIC);
		  cstmt.setInt(2, f1);
		  cstmt.setInt(3, f2);
		  cstmt.setInt(4, cash_id);
		  cstmt.setString(5, acc);
		  cstmt.setInt(6, status);
		  cstmt.executeUpdate();
		  int val = cstmt.getInt(1);
		  cstmt.close();
		  con.close();
		  System.out.println(val);
	}
}
