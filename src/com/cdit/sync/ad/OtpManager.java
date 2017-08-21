package com.cdit.sync.ad;

import java.util.Date;

import javax.print.attribute.standard.DateTimeAtProcessing;
import javax.swing.JFrame;

import com.cdit.test.portal.CheckUserMobile;

public class OtpManager {	
	public static void main(String asd[]){
		TransferToDB db = null;
		Date today = new Date();
		String version = "5.1.13";
//		  Date myDate = new Date(113,04,20);
//		  System.out.println("My Date is "+myDate);    
//		  System.out.println("Today Date is "+today);
//		  if (today.compareTo(myDate)<0)
//		      System.out.println("Today Date is Lesser than my Date");
//		  else if (today.compareTo(myDate)>0)
//		      System.out.println("Today Date is Greater than my date"); 
//		  else
//		      System.out.println("Both Dates are equal");
//		if (today.compareTo(myDate)<0){	  
			if (asd .length > 0) {			
				if(asd[0] != null && asd[0].equals("-h")){
					System.out.println("type -a username [start date] [finish date] (date format yyyy-mm-dd) to addnew user to ABROAD");
					System.out.println("type -d username to delete user go to ABROAD");
					System.out.println("type -l to see list user go to ABROAD");
					System.out.println("type -s to sync user from AD to Portal");
					System.out.println("type -s -d to sync user from AD to Danh ba Portal");
					System.out.println("type -m username to see user mobile");
					System.out.println("type -exit to finish your work, PLEASE DON'T FORGET ^!*");
				}else if(asd[0] != null && asd[0].equals("-s")){
					if(asd.length > 1 && (asd[1]!= null && asd[1].equals("-d")))
						(new SyncUserAD2Dirtel()).connectToSyncData();
					else
						(new SyncUserAD2DB(getConnection(db))).connectToSyncData();
//					System.out.println("Funnction is disabled by ducnm.");
				}else if(asd[0] != null && asd[0].equals("-l")){
	//				(new TransferToDB()).listUserGoAbroad(true);
					getConnection(db).listUserGoAbroad(true);
				}else if(asd[0] != null && asd[0].equals("-d")){
					if(asd.length == 2 ){
	//					int a = (new TransferToDB()).delUserGoAbroad(asd[1],true);
						int a = getConnection(db).delUserGoAbroad(asd[1],true);
						if(a==-1)
							System.out.println("DELETE USER SUCCESSFUL");
						else
							System.out.println("ERROR DURING DELETE USER");
					}else{
						System.out.println("PLEASE INPUT correct syntax: -d username");
					}
				}else if(asd[0] != null && asd[0].equals("-a")){
					if(asd.length > 1 && asd.length ==4){
	//					int a = (new TransferToDB()).addUserGoAbroad(asd, true);
						int a = getConnection(db).addUserGoAbroad(asd, true);
						if(a==1){
							System.out.println("ADD USER GO TO ABROAD SUCCESSFUL");
						}else if(a==0){
							System.out.println("USER ALREADY EXIST OR ERROR, PLEASE CONTACT DUCNM");
						}
					}else{
						System.out.println("PLEASE INPUT correct syntax: -a username | start_date | finish_date (YYYY-MM-dd)");
					}
				}else if(asd[0] != null && asd[0].equals("-m")){
					if(asd.length == 2){
						if(!asd[1].equals("more"))
	//						System.out.println((new TransferToDB()).getUserMobile(asd[1], true));
							System.out.println(getConnection(db).getUserMobile(asd[1], true));
						else{
							CheckUserMobile test = new CheckUserMobile();
							test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						}
					}else{
						System.out.println("PLEASE INPUT correct syntax: -m username OR -m more IF YOU CHECK MORE THAN ONE USER");
					}
				}else if(asd[0].equals("-exit")){
					getConnection(db).closeConnection();
					for (String s: asd) {
			            System.out.println(s);
			        }
				}else{
					System.out.println("Your parameter in correct, please type -h to help you");
				}
			}else{
				System.out.println("Please input parameter or type -h to help you");			
			}
		}
//		else if (today.compareTo(myDate)>0){
//			System.out.println("Today, this program is expired, pls contact with ducnm for more information");			
//		}
//	}
	public static TransferToDB getConnection(TransferToDB db){
		if(db == null){
			db = new TransferToDB();
		}
		return db;
	}
}
