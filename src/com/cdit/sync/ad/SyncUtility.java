package com.cdit.sync.ad;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SyncUtility {
	public static Date stringToDate(String strDate) {
		try {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

			if (strDate == null) {
				return null;
			} else {

				// Date today = df.parse("20/12/2005");
				Date today = df.parse(strDate);
				return today;

			}
		} catch (Exception e) {
			return null;
		}
	}

	public static String dateToString(Date date, String strFormat) {
		try {
			if (date == null) {
				return null;
			} else {
				SimpleDateFormat fm = new SimpleDateFormat(strFormat);
				String dateString = fm.format(date);
				return dateString;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static Connection getConnectionFromURL()
			throws ClassNotFoundException {
		// ResultSet rs = null;
		Connection connection = null;

		// Load database driver if not already loaded

		// Establish network connection to database
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			connection = DriverManager.getConnection(
					"jdbc:db2://10.1.2.201:50000/wpsapps", "otpmanager",
					"OTP!@#123");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("Loi connect DB");
			e.printStackTrace();
		}
		return connection;
	}
	public static Object[] getMultiConnectionFromURL(boolean isOTP,boolean isDirtel)
			throws ClassNotFoundException {
		// ResultSet rs = null;
		Connection connection = null;
		Object object[] = new Object[2];
		// Load database driver if not already loaded
		
		// Establish network connection to database
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			if(isOTP)
				connection = DriverManager.getConnection(
						"jdbc:db2://10.1.2.201:50000/WPSAPPS", "otpmanager",
						"OTP!@#123");
			object[0] = connection;
			if(isDirtel)
				connection = DriverManager.getConnection(
						"jdbc:db2://10.1.2.201:50000/WPSAPPS", "dirtel",
						"DIR!@#123");
			object[1] = connection;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("Loi connect DB ");
			e.printStackTrace();
		}
		return object;
	}
	public static String getCurrentDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		// get current date time with Date()
		// Date date = new Date();
		// System.out.println(dateFormat.format(date));

		// get current date time with Calendar()
		Calendar cal = Calendar.getInstance();
		// System.out.println(dateFormat.format(cal.getTime()));
		return dateFormat.format(cal.getTime());
	}

	public static String genRandomNumber() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}
	public static String convertHexToString(String hex){
		 
		  StringBuilder sb = new StringBuilder();
		  StringBuilder temp = new StringBuilder();
	 
		  //49204c6f7665204a617661 split into two characters 49, 20, 4c...
		  for( int i=0; i<hex.length()-1; i+=2 ){
	 
		      //grab the hex in pairs
		      String output = hex.substring(i, (i + 2));
		      //convert hex to decimal
		      int decimal = Integer.parseInt(output, 16);
		      //convert the decimal to character
		      sb.append((char)decimal);
	 
		      temp.append(decimal);
		  }
		  System.out.println("Decimal : " + temp.toString());
	 
		  return sb.toString();
	  }
	static String AddLeadingZero(int k) {
		return (k<0xF)?"0" + Integer.toHexString(k):Integer.toHexString(k);
	}
	public static String convertGuidToString(byte[] GUID){
		//byte[] GUID = (byte[]) objGUID;
		String strGUID = "";
		String byteGUID = "";
		//Convert the GUID into string using the byte format
		for (int c=0;c<GUID.length;c++) {
			byteGUID = byteGUID + "\\" + AddLeadingZero((int)GUID[c] & 0xFF);
		}
		//convert the GUID into string format
		//strGUID = "{";
		strGUID = strGUID + AddLeadingZero((int)GUID[3] & 0xFF);
		strGUID = strGUID + AddLeadingZero((int)GUID[2] & 0xFF);
		strGUID = strGUID + AddLeadingZero((int)GUID[1] & 0xFF); 
		strGUID = strGUID + AddLeadingZero((int)GUID[0] & 0xFF);
		strGUID = strGUID + "-";
		strGUID = strGUID + AddLeadingZero((int)GUID[5] & 0xFF);
		strGUID = strGUID + AddLeadingZero((int)GUID[4] & 0xFF);
		strGUID = strGUID + "-";
		strGUID = strGUID + AddLeadingZero((int)GUID[7] & 0xFF);
		strGUID = strGUID + AddLeadingZero((int)GUID[6] & 0xFF);
		strGUID = strGUID + "-";
		strGUID = strGUID + AddLeadingZero((int)GUID[8] & 0xFF);
		strGUID = strGUID + AddLeadingZero((int)GUID[9] & 0xFF);
		strGUID = strGUID + "-";
		strGUID = strGUID + AddLeadingZero((int)GUID[10] & 0xFF);
		strGUID = strGUID + AddLeadingZero((int)GUID[11] & 0xFF);
		strGUID = strGUID + AddLeadingZero((int)GUID[12] & 0xFF);
		strGUID = strGUID + AddLeadingZero((int)GUID[13] & 0xFF);
		strGUID = strGUID + AddLeadingZero((int)GUID[14] & 0xFF);
		strGUID = strGUID + AddLeadingZero((int)GUID[15] & 0xFF);
		//strGUID = strGUID + "}";
//		System.out.println("GUID (String format): " + strGUID);
//		System.out.println("GUID (Byte format): " + byteGUID);
		return strGUID;

	}
	public static String convertSIDToString(byte[] SID) {
		   // Add the 'S' prefix
		   StringBuilder strSID = new StringBuilder("S-");

		   // bytes[0] : in the array is the version (must be 1 but might 
		   // change in the future)
		   strSID.append(SID[0]).append('-');

		   // bytes[2..7] : the Authority
		   StringBuilder tmpBuff = new StringBuilder();
		   for (int t=2; t<=7; t++) {
		      String hexString = Integer.toHexString(SID[t] & 0xFF);
		      tmpBuff.append(hexString);
		   }
		   strSID.append(Long.parseLong(tmpBuff.toString(),16));

		   // bytes[1] : the sub authorities count
		   int count = SID[1];

		   // bytes[8..end] : the sub authorities (these are Integers - notice
		   // the endian)
		   for (int i = 0; i < count; i++) {
		      int currSubAuthOffset = i*4;
		      tmpBuff.setLength(0);
		      tmpBuff.append(String.format("%02X%02X%02X%02X", 
		        (SID[11 + currSubAuthOffset]& 0xFF),
		        (SID[10 + currSubAuthOffset]& 0xFF),
		        (SID[9 + currSubAuthOffset]& 0xFF),
		        (SID[8 + currSubAuthOffset]& 0xFF)));

		      strSID.append('-').append(Long.parseLong(tmpBuff.toString(), 16));
		   }

		   // That's it - we have the SID
		   System.out.println(strSID.toString());
		   return strSID.toString();
		}


}
