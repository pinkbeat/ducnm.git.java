package com.vsc.sync.openldap;
/*
 * author ducnm
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

public class SyncUser2LDAP {
	static String db_name = "jdbc:oracle:thin:his_esb/123@123.31.27.51:1521:db01";
	static Connection connection = null;
	static ResultSet resultSet = null;
	static boolean isUpdateIfExists = true;
	static String sql = "select * from (SELECT rownum r, a.user_name, b.ten_bv, a.full_name, a.USER_PWD "+
			" FROM ADM_USER a, DM_COSO_KCB b WHERE a.user_name = b.ma_bv(+) order by a.user_name) where r between ? and ?";
	static int page = 500;
	static LDAPHelper ldapHelper = null;
	static LdapContext lctx = null;
	static PreparedStatement statement = null;
	
	public SyncUser2LDAP()		
	{
		ldapHelper = new LDAPHelper();
	}
	private static Connection getConnection(){
		Connection connection = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");			
			connection = DriverManager.getConnection(
				"jdbc:oracle:thin:@123.31.27.51:1521:db01","his_esb","123");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}
	private void insertAllUser2Ldap(Object[][] arg ){
		if(arg!=null){
			int i=0;
			for(Object[] obj : arg ){
				sql = "select * from (SELECT rownum r, a.user_name, b.ten_bv, a.full_name, a.USER_PWD "+
						" FROM ADM_USER a, DM_COSO_KCB b WHERE a.user_name = b.ma_bv(+) and user_group_id = "+obj[i+1]+" order by a.user_name) where r between ? and ?";
				insertAllUser2Ldap((String)obj[i]);
			}
		}		
	}
	private void insertAllUser2Ldap(String dN){
		EntryDTO entryDTO = null;
		try{
			if(connection == null)
				connection = getConnection();
			int i=1,j = page,k=1;						
			lctx = ldapHelper.authenticate();		
			long time = System.currentTimeMillis();
			statement = connection.prepareCall(sql);
			do{
//				System.out.println("i va j ="+i+"-"+j);
				k=1;				
				statement.setInt(1, i);
				statement.setInt(2, j);
				resultSet = statement.executeQuery();
				if(resultSet != null){								
					while(resultSet.next()){
						if(resultSet.getString(2) !=null){
							entryDTO = new EntryDTO(resultSet.getString(2), resultSet.getString(3),"congdulieuboyte@gmail.com", resultSet.getString(4), resultSet.getString(5));
							ldapHelper.addEntry(dN, entryDTO, 1 ,lctx);
						}else{
							System.out.println("entry null");
						}
						k++;						
					}
					i=i+page;
					j=j+page;
					System.out.println("Synchronized="+k);					
				}				
			}while (k==page);
			System.out.println("ESTIMATED TIME: " + (System.currentTimeMillis()-time));
			statement.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if (connection!= null){
				try {
					statement.close();
					connection.close();
					connection=null;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(lctx != null){
				try {
					lctx.close();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("Success");
	}
	private static void delLdap(String dN){
		lctx = ldapHelper.authenticate();
		//			lctx.destroySubcontext(dN);
		ldapHelper.deleteEntry(dN, "", lctx);
	}
	private void scanUser2Ldap(Object[][] arg ){
		if(arg!=null){
			int i=0;
			for(Object[] obj : arg ){
				sql = "select * from (SELECT rownum r, a.user_name, b.ten_bv, a.full_name, a.USER_PWD "+
						" FROM ADM_USER a, DM_COSO_KCB b WHERE a.user_name = b.ma_bv(+) and user_group_id = "+obj[i+1]+" order by a.user_name) where r between ? and ?";
				scanUser2Ldap((String)obj[i]);
			}
		}		
	}
	private static void scanUser2Ldap(String dN){
		EntryDTO entryDTO = null;
		try{
			if(connection == null)
				connection = getConnection();
			
			int i=1,j = page,k=1;
			boolean exists = false;
			lctx = ldapHelper.authenticate();		
			long time = System.currentTimeMillis();
			statement = connection.prepareCall(sql);
			do{
//				System.out.println("i va j ="+i+"-"+j);
				k=1;				
				statement.setInt(1, i);
				statement.setInt(2, j);
				resultSet = statement.executeQuery();
				if(resultSet != null){	
					int rs=0;
					while(resultSet.next()){
						if(resultSet.getString(2)!=null){
							entryDTO = new EntryDTO(resultSet.getString(2), resultSet.getString(3),"congdulieuboyte@gmail.com", resultSet.getString(4), resultSet.getString(5));
							exists = ldapHelper.checkEntryExists(entryDTO.getUid(), null, "(uid=*)", lctx);
							k++;
							if(exists){
								System.out.println("entry "+entryDTO.getUid()+" is existing");
								if(isUpdateIfExists){
									ldapHelper.updateEntry(dN, entryDTO, 1, lctx);
								}else
									continue;
							}else{
								rs = ldapHelper.addEntry(dN, entryDTO, 1 ,lctx);
								if(rs==1)System.out.println("Added "+entryDTO.getUid()+" successfully");
								//else if(rs==2)System.out.println("entry "+entryDTO.getUid()+" is exists");							
							}
						}
					}
					i=i+page;
					j=j+page;
					System.out.println("synchronized = "+k);					
				}				
			}while (k==page);
			System.out.println("ESTIMATED TIME: " + (System.currentTimeMillis()-time)); //700538 ms
			statement.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if (connection!= null){
				try {
					statement.close();
					connection.close();
					connection=null;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(lctx != null){
				try {
					lctx.close();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("Success");
	}
	private static void upgradeUser(String baseDn,EntryDTO entryDTO,LdapContext context){
		ldapHelper.updateEntry(baseDn, entryDTO, 1, context);
	}
	private static void insertUser(String baseDn,EntryDTO entryDTO,LdapContext context){
		ldapHelper.addEntry(baseDn, entryDTO, 2, context);
	}
	public static void main(String[] asd){
		SyncUser2LDAP syncUser2LDAP = new SyncUser2LDAP();
//		String dN = "ou=hPortal";
		//insert new
//		String[][] dN = new String[][]{
//					{"ou=hpt_admin,ou=hPortal","1"}
//					,{"ou=hpt_hportal,ou=hPortal","2"}
//					,{"ou=hpt_byt,ou=hPortal","3"}
//					,{"ou=hpt_syt,ou=hPortal","4"}
//					,{"ou=hpt_cs_kcb,ou=hPortal","5"}
//					,{"ou=hpt_vsc,ou=hPortal","6"}
//					,{"ou=hpt_tt_yt,ou=hPortal","7"}};
//					
//		syncUser2LDAP.insertAllUser2Ldap(dN);
		//scan 
		String[][] dN = new String[][]{
			{"ou=hpt_admin,ou=hPortal","1"}
			,{"ou=hpt_hportal,ou=hPortal","2"}
			,{"ou=hpt_byt,ou=hPortal","3"}
			,{"ou=hpt_syt,ou=hPortal","4"}
			,{"ou=hpt_cs_kcb,ou=hPortal","5"}
			,{"ou=hpt_vsc,ou=hPortal","6"}
			,{"ou=hpt_tt_yt,ou=hPortal","7"}};
			
		syncUser2LDAP.scanUser2Ldap(dN);
	}
}
