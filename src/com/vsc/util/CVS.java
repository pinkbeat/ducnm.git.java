package com.vsc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CVS {
	//Delimiter used in CSV file
		private static final String COMMA_DELIMITER = ",";
		private static final String FILE_EXT = ".csv";
		private static final String NEW_LINE_SEPARATOR = "\n";
//		private static final String FILE_HEADER = "";
		
		public static List<String> read(String fileName) {
			List<String> list = new ArrayList<String>();				    
	        try (BufferedReader fileReader = new BufferedReader(new FileReader(fileName));){            	            
	        	String line = "";            	            
	            while ((line = fileReader.readLine()) != null) {
	                if (line.length() > 0) {
	                	list.add(line);
					}
	            }
	        } 
	        catch (Exception e) {
	        	System.out.println("Error in Reading CVS !!!");
	            e.printStackTrace();
	        }
	        return list; 
		}
		public static void write(String fileName, String[] content) {
	        try (FileWriter fileWriter = new FileWriter(fileName,true);){            	            
	        	for (int i=0;i<content.length;i++) {
					fileWriter.append(content[i]);
					fileWriter.append(COMMA_DELIMITER);									
				}
	        	fileWriter.append(NEW_LINE_SEPARATOR);
	        } 
	        catch (Exception e) {
	        	System.out.println("Error in Writing CVS !!!");
	            e.printStackTrace();
	        }
		}
		public static void write(String fileName, String content) {
	        try (FileWriter fileWriter = new FileWriter(fileName,true);){            	            
	        	fileWriter.append(content);
	        	fileWriter.append(NEW_LINE_SEPARATOR);
	        } 
	        catch (Exception e) {
	        	System.out.println("Error in Writing CVS !!!");
	            e.printStackTrace();
	        }
		}		
		public static void writeCVS(String path, String content){			 
		 	Date currentDate 	= new Date();
		 	String realTime		= null;
		 	String yyyymmdd		= null;		 	
		 	String yyyymm=null;
		 	SimpleDateFormat formatter	= null;
		 	String tempStr	= null;
		 	File file = null;
		 	String fileName=null;
		 	FileWriter fileWriter =null;
		    try 
		     {
				formatter =new SimpleDateFormat ("yyyyMMdd HH:mm:ss");
				formatter.setTimeZone(TimeZone.getTimeZone("VST"));
				realTime = formatter.format(currentDate);
				yyyymmdd = realTime.substring(0,8);
				yyyymm=realTime.substring(0,6);

				tempStr = realTime+"," +content;
				fileName = yyyymmdd+FILE_EXT;
				file = new File(path); 
				if(!file.exists()){
					file.mkdir();
				}		    		
				file = new File(path+"/"+yyyymm);					
				if(!file.exists()){
					file.mkdir();
				}
//					inputString =new String(tempStr.getBytes("UTF-8"), "8859_1");
				fileWriter = new FileWriter(path+"/"+yyyymm+"/"+fileName,true);
				fileWriter.append(tempStr);
	        	fileWriter.append(NEW_LINE_SEPARATOR);
				
			} catch(FileNotFoundException e) {
			    System.out.println("CVS.java :"+e.toString());
			} catch(IOException e) {
			    System.out.println("CVS.java :"+e.toString());
			} catch(Exception e) {
			    System.out.println("CVS.java :"+e.toString());
			} finally{
				try{ if(fileWriter !=null) fileWriter.close();} catch(Exception exf){}
			}
		 }
		public static void main(String[] asd){
			write("c:\\xmldata\\log", "22,33,555,222,22,11");
			write("c:\\xmldata\\log", "22,33,555,222,22,11");
			write("c:\\xmldata\\log", "22,33,555,222,22,11");
//			write("c:\\xmldata\\log", new String[]{"22,33,555,222,22,11"});
			List<String> list = read("c:\\xmldata\\lostfiles.log.out");
			for(String s:list){
				System.out.println(s);
			}
		}
}
