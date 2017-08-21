package com.cdit.sync.ad;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class TransferToDB {
	Connection connection = null;
	CallableStatement callableStatement = null;
	CallableStatement callableStatement2 = null;
	PreparedStatement preparedStatement = null;
	Object obj[] = null;
	ResultSet resultSet = null;
	public TransferToDB (){
		try {
			connection = SyncUtility.getConnectionFromURL();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.getMessage();
			e.printStackTrace();
		}
	}
	public TransferToDB (boolean isOTP, boolean isDirtel){
		try {
			obj = SyncUtility.getMultiConnectionFromURL(isOTP, isDirtel);
			connection = (Connection)obj[1];
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.getMessage();
			e.printStackTrace();
		}
	}

	// protected ArrayList listCVBan = new ArrayList();
	
	public int tranferBAN_ADToDirtelDB(String khoi, String donvi) {
		// TODO Auto-generated method stub

		int rowCount = 0;
		// Connection connection =null;
		CallableStatement callableStatement;

		try {

			if (connection == null || connection.isClosed())
				connection = SyncUtility.getConnectionFromURL();

			callableStatement = connection
					.prepareCall("CALL DIRTEL.PROC_INS_DEPT(?,?)");

			callableStatement.setString(1, khoi.trim());

			callableStatement.setString(2, donvi.trim());

			callableStatement.executeUpdate();

			callableStatement.close();

			connection.close();
			rowCount = 1;
		} catch (Exception e) {
			e.printStackTrace();
			closeConnection();
			// TODO: handle exception
		}
		return rowCount;
	}

	public int tranferUSER_ADToDirtelDB(String fullName, String email,
			String mobile, String offPhone, String donVi, String chucVu,
			String ngaySinh, String acc) {
		return tranferUSER_ADToDirtelDB(fullName, email,
				mobile, offPhone, donVi, chucVu,
				ngaySinh, acc, null, true);
	}	
	//Giai quyet error: DB2 SQL Error: SQLCODE=-805, SQLSTATE=51002, SQLERRMC=NULLID.SYSLH203 0X5359534C564C3031, DRIVER=3.53.70
	public void callProc() throws ClassNotFoundException, SQLException{
		if (connection == null || connection.isClosed())
			connection = SyncUtility.getConnectionFromURL();

		callableStatement = connection
				.prepareCall("CALL DIRTEL.SYNC_FROM_AD(?,?,?,?,?,?,?)");
		//return callableStatement;
	}
	public void callProc(boolean isOTP, boolean isDirtel) throws ClassNotFoundException, SQLException{
		
		if(isOTP && !isDirtel){
			callProc();
		}else if(!isOTP && isDirtel){
			if (connection == null || connection.isClosed()){
				obj = SyncUtility.getMultiConnectionFromURL(isOTP,isDirtel);				
				connection = (Connection)obj[1];
			}
			callableStatement2 = connection
					.prepareCall("CALL DIRTEL.INST_DATA_AD_2TEMP(?,?,?,?,?,?,?,?,?,?,?,?,?)");
		}	
		else if(isOTP && isDirtel){
			if (connection == null || connection.isClosed()){
				obj = SyncUtility.getMultiConnectionFromURL(isOTP,isDirtel);
			}
			callableStatement = ((Connection)obj[0]).prepareCall("CALL DIRTEL.SYNC_FROM_AD(?,?,?,?,?,?,?)");
			callableStatement2 = ((Connection)obj[1]).prepareCall("CALL DIRTEL.INST_DATA_AD_2TEMP(?,?,?,?,?,?,?,?,?,?,?,?,?)");
		}
	}
	public int tranferUSER_ADToDirtelDB(String fullName, String email,
			String mobile, String acc, String dn, String objGUID, String objSID, boolean isclose) {
		// TODO Auto-generated method stub

		int rowCount = 0;
		// Connection connection =null;		
		try {
			callableStatement.setString(1, email);
			callableStatement.setString(2, mobile);			
			callableStatement.setString(3, acc);
			callableStatement.setString(4, fullName);
			callableStatement.setString(5, dn);
			callableStatement.setString(6, objGUID);
			callableStatement.setString(7, objSID);
			rowCount = callableStatement.executeUpdate();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("User error = "+email+"-"+mobile+"-"+acc+"-"+fullName+"-"+dn);
			try {
				callableStatement.executeUpdate();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
			}
			if(isclose)
				closeConnection();
			// TODO: handle exception
		}
		 finally{
			 if(isclose){
				 try {
					 if( connection!=null)
					 connection.close();
				 if( callableStatement!= null)
					 callableStatement.close();
				 }
				 catch (Exception e) {
				 // TODO: handle exception
					 e.printStackTrace();
				 }
			 }		
		 }
		return rowCount;
	}
	public int tranferUSER_ADToDirtelDB(String tenkhoi, String objkhoi,
			String tenBan, String objBan, ContactDTO contactDTO, boolean isclose,boolean isMultiDB) {
		
			int rowCount = 0;	
		try {
			callableStatement2.setString(1, tenkhoi);
			callableStatement2.setString(2, objkhoi);
			callableStatement2.setString(3, tenBan);
			callableStatement2.setString(4, objBan);
			callableStatement2.setString(5, contactDTO.getAccount());
			callableStatement2.setString(6, contactDTO.getFullName());
			callableStatement2.setString(7, contactDTO.getMobile());
			callableStatement2.setString(8, contactDTO.getOffPhone());			
			callableStatement2.setString(9, contactDTO.getNgaySinh());
			callableStatement2.setString(10, contactDTO.getChucvu());
			callableStatement2.setString(11, contactDTO.getEmail());
			callableStatement2.setString(12, contactDTO.getDN());
			callableStatement2.setString(13, contactDTO.getObjGUID());
			rowCount = callableStatement2.executeUpdate();
//			System.out.println("Parameter: :"+tenkhoi+","+objkhoi+","
//					+tenBan+","+objBan+","+contactDTO.getAccount()+","+contactDTO.getFullName()+","+contactDTO.getMobile()+","+contactDTO.getOffPhone()
//					+","+contactDTO.getNgaySinh()+","+contactDTO.getChucvu()+","+contactDTO.getEmail()+","+contactDTO.getDN()+","+contactDTO.getObjGUID());
//			System.out.println(rowCount);
		} catch (Exception e) {
			System.out.println(e.getMessage());			
			try {
				callableStatement2.executeUpdate();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
			}
			if(isclose)
				closeConnection();
			// TODO: handle exception
		}
		 finally{
			 if(isclose){
				 try {
					 if(isMultiDB){
						 for(int i=0;i<obj.length;i++){
							if(((Connection)obj[i]) != null) 
							 ((Connection)obj[i]).close();
						 }
					 }						 
					 if( connection!=null && !isMultiDB)
						 connection.close();
				 if( callableStatement2!= null)
					 callableStatement2.close();
				 
				 if( callableStatement!= null)
					 callableStatement.close();
				 }
				 catch (Exception e) {
				 // TODO: handle exception
					 e.printStackTrace();
				 }
			 }		
		 }
		return rowCount;
	}
	public int syncUser_to_db(boolean isclose){
		int rowCount = 0;
		// Connection connection =null;		
		try {

			if (connection == null || connection.isClosed())
				connection = SyncUtility.getConnectionFromURL();

			callableStatement = connection
					.prepareCall("{ CALL DIRTEL.SYNC_TO_DB_2(?) }");

			callableStatement.registerOutParameter(1, Types.INTEGER);			
			rowCount = callableStatement.executeUpdate();
//			ResultSet resultSet = callableStatement.getResultSet();
//			while(resultSet.next()){
//				int i = resultSet.getInt(1);
//				rowCount = i;
//			}

		} catch (Exception e) {
			System.out.println("DIRTEL.SYNC_TO_DB_2 ERROR = "+e.getMessage());
			try {
				callableStatement.executeUpdate();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
				isclose = true;
			}
//			if(isclose)
//				closeConnection();
			// TODO: handle exception
		}
		 finally{
			 if(isclose){
				 try {
					 if( connection!=null)
					 connection.close();
				 if( callableStatement!= null)
					 callableStatement.close();
				 }
				 catch (Exception e) {
				 // TODO: handle exception
					 e.printStackTrace();
				 }
			 }		
		 }
		return rowCount;
	}
	public int syncUser_to_db(boolean isclose, boolean isMultiDB,boolean isOTP, boolean isDirtel){
		int rowCount = 0;
		// Connection connection =null;		
		try {

			if(isOTP && !isDirtel){
				if (connection == null || connection.isClosed())
					connection = SyncUtility.getConnectionFromURL();
				callableStatement = connection
						.prepareCall("{ CALL DIRTEL.SYNC_TO_DB_2(?) }");
				callableStatement.registerOutParameter(1, Types.INTEGER);							
			}
			else if(!isOTP && isDirtel){
				if (connection == null || connection.isClosed()){
					obj = SyncUtility.getMultiConnectionFromURL(isOTP, isDirtel);
					connection = (Connection)obj[1];
				}
				callableStatement = connection
						.prepareCall("{ CALL DIRTEL.UPGRADE_TEMP_2_EPTUSER() }");
//				System.out.println("CALL DIRTEL.UPGRADE_TEMP_2_EPTUSER()");
			}
			else if(isOTP && isDirtel){
				if (connection == null || connection.isClosed()){
					obj = SyncUtility.getMultiConnectionFromURL(isOTP, isDirtel);
					connection = (Connection)obj[1];
				}
				callableStatement = ((Connection)obj[0])
					.prepareCall("{ CALL DIRTEL.DIRTEL.SYNC_TO_DB_2(?) }");
				callableStatement2 = ((Connection)obj[1])
					.prepareCall("{ CALL DIRTEL.UPGRADE_TEMP_2_EPTUSER() }");
				callableStatement.registerOutParameter(1, Types.INTEGER);			
				rowCount = callableStatement2.executeUpdate();
			}
			rowCount = callableStatement.executeUpdate();

			
//			ResultSet resultSet = callableStatement.getResultSet();
//			while(resultSet.next()){
//				int i = resultSet.getInt(1);
//				rowCount = i;
//			}

		} catch (Exception e) {			
			try {
				if(callableStatement!= null)
					rowCount =callableStatement.executeUpdate();
				if(callableStatement2!=null)
					rowCount =callableStatement2.executeUpdate();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				System.out.println("syncUser_to_db ERROR = "+e.getMessage());
				System.out.println(e1.getMessage());				
				isclose = true;
			}
//			if(isclose)
//				closeConnection();
			// TODO: handle exception
		}
		 finally{
			 if(isclose){
				 try {
					 if(isMultiDB){
						 for(int i=0;i<obj.length;i++){
							if(((Connection)obj[i]) != null) 
							 ((Connection)obj[i]).close();
						 }
					 }						 
					 if( connection!=null && !isMultiDB)
						 connection.close();
					 if( callableStatement2!= null)
						 callableStatement2.close();
					 
					 if( callableStatement!= null)
						 callableStatement.close();
				 }
				 catch (Exception e) {
				 // TODO: handle exception
					 e.printStackTrace();
				 }
			 }		
		 }
		return rowCount;
	}
	public int check_user_exist_AD(boolean isclose){
		int rowCount = 0;
		// Connection connection =null;		
		try {

			if (connection == null || connection.isClosed())
				connection = SyncUtility.getConnectionFromURL();

			callableStatement = connection
					.prepareCall("{ CALL DIRTEL.CHECK_EXIST_IN_AD() }");
		
			rowCount = callableStatement.executeUpdate();
//			ResultSet resultSet = callableStatement.getResultSet();
//			while(resultSet.next()){
//				int i = resultSet.getInt(1);
//				rowCount = i;
//			}

		} catch (Exception e) {
			System.out.println("dirtel.check_user_exist_AD ERROR = "+e.getMessage());
			try {
				rowCount = callableStatement.executeUpdate();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
				isclose = true;
			}
//			if(isclose)
//				closeConnection();
			// TODO: handle exception
		}
		 finally{
			 if(isclose){
				 try {
					 if( connection!=null)
					 connection.close();
				 if( callableStatement!= null)
					 callableStatement.close();
				 }
				 catch (Exception e) {
				 // TODO: handle exception
					 e.printStackTrace();
				 }
			 }		
		 }
		return rowCount;
	}
	public int check_user_exist_AD(boolean isclose, boolean isMultiDB,boolean isOTP, boolean isDirtel){
		int rowCount = 0;
		// Connection connection =null;		
		try {

			
			if(isOTP && !isDirtel){
				if (connection == null || connection.isClosed())
					connection = SyncUtility.getConnectionFromURL();
				callableStatement = connection
						.prepareCall("{ CALL DIRTEL.CHECK_EXIST_IN_AD() }");
				
			}
			else if(!isOTP && isDirtel){
				if (connection == null || connection.isClosed()){
					obj = SyncUtility.getMultiConnectionFromURL(isOTP, isDirtel);
					connection = (Connection)obj[1];
				}
				callableStatement = connection
						.prepareCall("{ CALL DIRTEL.CHECK_EXISTS_USER() }");
//				System.out.println("CALL DIRTEL.CHECK_EXISTS_USER()");
			}
			else if(isOTP && isDirtel){
				if (connection == null || connection.isClosed()){
					obj = SyncUtility.getMultiConnectionFromURL(isOTP, isDirtel);
					connection = (Connection)obj[1];
				}
				callableStatement = ((Connection)obj[0])
					.prepareCall("{ CALL DIRTEL.CHECK_EXIST_IN_AD() }");
				callableStatement2 = ((Connection)obj[1])
					.prepareCall("{ CALL DIRTEL.CHECK_EXISTS_USER() }");
				rowCount = callableStatement2.executeUpdate();

			}
			rowCount = callableStatement.executeUpdate();
			
//			ResultSet resultSet = callableStatement.getResultSet();
//			while(resultSet.next()){
//				int i = resultSet.getInt(1);
//				rowCount = i;
//			}

		} catch (Exception e) {
			
			try {
				if(callableStatement != null)
					rowCount = callableStatement.executeUpdate();
				if(callableStatement2 != null)
					rowCount = callableStatement2.executeUpdate();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				System.out.println("check_user_exist_AD ERROR = "+e.getMessage());
				System.out.println(e1.getMessage());
				isclose = true;
			}
//			if(isclose)
//				closeConnection();
			// TODO: handle exception
		}
		 finally{
			 if(isclose){
				 try {
					 if(isMultiDB){
						 for(int i=0;i<obj.length;i++){
							if(((Connection)obj[i]) != null) 
							 ((Connection)obj[i]).close();
						 }
					 }						 
					 if( connection!=null && !isMultiDB)
						 connection.close();
				 if( callableStatement2!= null)
					 callableStatement2.close();
				 
				 if( callableStatement!= null)
					 callableStatement.close();
				 }
				 catch (Exception e) {
				 // TODO: handle exception
					 e.printStackTrace();
				 }
			 }		
		 }
		return rowCount;
	}
	public int delOldDataBeforeSync(boolean isclose){
		int rowCount = 0;
		// Connection connection =null;		
		try {

			if (connection == null || connection.isClosed())
				connection = SyncUtility.getConnectionFromURL();

			callableStatement = connection
					.prepareCall("CALL DIRTEL.DEL_OTPTEMPUSER()");						
			rowCount = callableStatement.executeUpdate();
		} catch (Exception e) {
			System.out.println("DIRTEL.DEL_OTPTEMPUSER ERROR = "+e.getMessage());
			try {
				callableStatement.executeUpdate();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
			}
			if(isclose)
				closeConnection();
			// TODO: handle exception
		}
		 finally{
			 if(isclose){
				 try {
					 if( connection!=null)
					 connection.close();
				 if( callableStatement!= null)
					 callableStatement.close();
				 }
				 catch (Exception e) {
				 // TODO: handle exception
					 e.printStackTrace();
				 }
			 }		
		 }
		return rowCount;
	}
	public int delOldDataBeforeSync(boolean isclose,boolean isMultiDB,boolean isOTP,boolean isDirtel){
		int rowCount = 0;
		// Connection connection =null;		
		try {

			if (connection == null || connection.isClosed())
				connection = (Connection)(SyncUtility.getMultiConnectionFromURL(isOTP, isDirtel))[1];

			callableStatement = connection
					.prepareCall("CALL DIRTEL.DEL_EPTTEMPDATA()");						
			rowCount = callableStatement.executeUpdate();
		} catch (Exception e) {
			System.out.println("DIRTEL.DEL_EPTTEMPDATA ERROR = "+e.getMessage());
			try {
				callableStatement.executeUpdate();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
			}
			if(isclose)
				closeConnection();
			// TODO: handle exception
		}
		 finally{
			 if(isclose){
				 try {
					 if(isMultiDB){
						 for(int i=0;i<obj.length;i++){
							if(((Connection)obj[i]) != null) 
							 ((Connection)obj[i]).close();
						 }
					 }						 
					 if( connection!=null && !isMultiDB)
						 connection.close();
					 if( callableStatement2!= null)
						 callableStatement2.close();
					 
					 if( callableStatement!= null)
						 callableStatement.close();
				 }
				 catch (Exception e) {
				 // TODO: handle exception
					 e.printStackTrace();
				 }
			 }		
		 }
		return rowCount;
	}
	public void listUserGoAbroad(boolean isclose){
//		int rowCount = 0;
		// Connection connection =null;
		System.out.println("[USERNAME]   [START DATE]   [FINISH DATE]");
		try {
			if (connection == null || connection.isClosed())
				connection = SyncUtility.getConnectionFromURL();
			preparedStatement = connection.prepareStatement("SELECT user_name,start_date,finish_date FROM DIRTEL.OTP_SPECIAL_LOGIN_3");
			resultSet = preparedStatement.executeQuery();
			if(resultSet != null){
				while (resultSet.next()){					
					System.out.println(resultSet.getString(1)+"   "+resultSet.getDate(2)+"   "+resultSet.getDate(3));
				}
			}else{
				System.out.println("NO RESULT  ");
			}
		} catch (Exception e) {
			System.out.println("listUserGoAbroad ERROR = "+e.getMessage());
			if(isclose)
				closeConnection();
			// TODO: handle exception
		}
		 finally{
			 if(isclose){
				 try {
					 if( connection!=null)
						 connection.close();
				 if( preparedStatement!= null)
					 preparedStatement.close();
				 }
				 catch (Exception e) {
				 // TODO: handle exception
					 e.printStackTrace();
				 }
			 }		
		 }
	}
	public int addUserGoAbroad(String[] asd,boolean isclose){
		int rowCount = 0;
		// Connection connection =null;		
		try {
//			System.out.println(asd[1]+asd[2]+asd[3]);
			if (connection == null || connection.isClosed())
				connection = SyncUtility.getConnectionFromURL();

			callableStatement = connection
					.prepareCall("CALL DIRTEL.UPDATE_USER_DI_CONG_TAC(?,?,?,?)");
			callableStatement.setString(1, asd[1]);
			callableStatement.setString(2, asd[2]);
			callableStatement.setString(3, asd[3]);
//			callableStatement.setDate(2, (asd[2] != null) ? (java.sql.Date)SyncUtility.stringToDate(asd[2]):null);
//			callableStatement.setDate(3, (asd[3] != null) ? (java.sql.Date)SyncUtility.stringToDate(asd[3]):null);
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			callableStatement.executeUpdate();
			rowCount = callableStatement.getInt(4);
		} catch (Exception e) {
//			System.out.println("addUserGoAbroad ERROR = "+e.getMessage());
			try {
				callableStatement.executeUpdate();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
			}
			if(isclose)
				closeConnection();
			// TODO: handle exception
		}
		 finally{
			 if(isclose){
				 try {
					 if( connection!=null)
					 connection.close();
				 if( callableStatement!= null)
					 callableStatement.close();
				 }
				 catch (Exception e) {
				 // TODO: handle exception
					 e.printStackTrace();
				 }
			 }		
		 }
		return rowCount;
	}
	public int delUserGoAbroad(String username,boolean isclose){
		int rowCount = 0;
		// Connection connection =null;		
		try {

			if (connection == null || connection.isClosed())
				connection = SyncUtility.getConnectionFromURL();

			callableStatement = connection
					.prepareCall("CALL DIRTEL.DEL_USER_GO_ABROAD(?)");
			callableStatement.setString(1, username);
			rowCount = callableStatement.executeUpdate();
		} catch (Exception e) {
//			System.out.println("addUserGoAbroad ERROR = "+e.getMessage());
			try {
				rowCount = callableStatement.executeUpdate();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
			}
			if(isclose)
				closeConnection();
			// TODO: handle exception
		}
		 finally{
			 if(isclose){
				 try {
					 if( connection!=null)
					 connection.close();
				 if( callableStatement!= null)
					 callableStatement.close();
				 }
				 catch (Exception e) {
				 // TODO: handle exception
					 e.printStackTrace();
				 }
			 }		
		 }
		return rowCount;
	}
	public int tranferUSER_ADToDirtelDB(String fullName, String email,
			String mobile, String offPhone, String donVi, String chucVu,
			String ngaySinh, String acc, String dn, boolean isclose) {
		// TODO Auto-generated method stub

		int rowCount = 0;
		try {

			if (connection == null || connection.isClosed())
				connection = SyncUtility.getConnectionFromURL();

			callableStatement = connection
					.prepareCall("CALL DIRTEL.SYNC_FROM_AD(?,?,?,?,?,?,?,?)");
			callableStatement.setString(1, fullName);
			// System.out.println("  CALL DIRTEL.UPGRADE_FROM_AD: fullName="+fullName);
			callableStatement.setString(2, email);
			// System.out.println("  CALL DIRTEL.UPGRADE_FROM_AD: email="+email);
			callableStatement.setString(3, mobile);
			// System.out.println("  CALL DIRTEL.UPGRADE_FROM_AD: mobile="+mobile);
			callableStatement.setString(4, offPhone);
			// System.out.println("  CALL DIRTEL.UPGRADE_FROM_AD: offPhone="+offPhone);
			callableStatement.setString(5, donVi);
			// System.out.println("  CALL DIRTEL.UPGRADE_FROM_AD: donVi="+donVi);
			callableStatement.setString(6, chucVu);
			// System.out.println("  CALL DIRTEL.UPGRADE_FROM_AD: chucVu="+chucVu);
			callableStatement.setString(7, ngaySinh);
			// System.out.println("  CALL DIRTEL.UPGRADE_FROM_AD: ngaySinh="+ngaySinh);
			callableStatement.setString(8, acc);
			// System.out.println("  CALL DIRTEL.UPGRADE_FROM_AD: acc="+acc);
			callableStatement.setString(9, dn);

			rowCount = callableStatement.executeUpdate();

			// callableStatement.close();

			// connection.close();
			// connection.createStatement();

		} catch (Exception e) {
			e.printStackTrace();
			if(isclose)
				closeConnection();
			// TODO: handle exception
		}
		 finally{
			 if(isclose){
				 try {
					 if( connection!=null)
					 connection.close();
				 if( callableStatement!= null)
					 callableStatement.close();
				 }
				 catch (Exception e) {
				 // TODO: handle exception
					 e.printStackTrace();
				 }
			 }		
		 }
		return rowCount;
	}
	public void closeConnection(){
		 try {
			 if( callableStatement!= null)
				 callableStatement.close();
			 
				if( connection!=null)
					connection.close();				 				 
			 }
			 catch (Exception e) {
				// TODO: handle exception
				 e.printStackTrace();
			}
	}
	public String getUserMobile(String username, boolean isclose){
		int rowCount = 0;
		String mobi = "";
		// Connection connection =null;		
		try {

			if (connection == null || connection.isClosed())
				connection = SyncUtility.getConnectionFromURL();

			callableStatement = connection
					.prepareCall("CALL DIRTEL.CHECK_OTP(?)");
			callableStatement.setString(1, username);
			resultSet =  callableStatement.executeQuery();
			if(resultSet.next()){
				mobi = resultSet.getString(1);
			}

		} catch (Exception e) {
			System.out.println("DIRTEL.DEL_OTPTEMPUSER ERROR = "+e.getMessage());
			try {
				callableStatement.executeUpdate();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
			}
			if(isclose)
				closeConnection();
			// TODO: handle exception
		}
		 finally{
			 if(isclose){
				 try {
					 if( connection!=null)
					 connection.close();
				 if( callableStatement!= null)
					 callableStatement.close();
				 }
				 catch (Exception e) {
				 // TODO: handle exception
					 e.printStackTrace();
				 }
			 }		
		 }
		return mobi;
	}
	public static void main(String[] asd){
//		System.out.println((new TransferToDB()).syncUser_to_db(true));
		System.out.println((new TransferToDB()).getUserMobile("trungnv", true));
	}
}
