package com.vsc.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Date;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

public class FileUtil {
	static String filterExt="";
	public static FileFilter getFilter(String ext) {
		filterExt=ext;
		FileFilter filter = new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				
				//boolean b=pathname.getName().toLowerCase().endsWith("."+filterExt);
				//System.out.println("getFilter="+pathname+" filterExt="+filterExt+" return="+b);
				return pathname.getName().toLowerCase().endsWith("."+filterExt);
			}
		};
		return filter;
	}
	public static void copyFile(File file,String dir)
	{
		copyFile(file,dir,null);
	}
	public static void copyFile(File file,String dir,String fmt)
	{
		Date now = new Date();
		File dirFile=null;
		try {
			if(fmt != null){
				String yyyyMM=DateUtil.formatDate(now,fmt);
				dirFile = new File(dir+"/"+yyyyMM);				
			}else{
				dirFile = new File(dir);	
			}
			if(!dirFile.exists()) {
				dirFile.mkdirs();
			}
//			FileUtils.copyFile(file,new File(dirFile.getAbsolutePath()+"/"+file.getName()));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void moveFile(File tmpFile,String path_backup)
	{
		moveFile(tmpFile, path_backup, null);
	}
	public static void moveFile(File tmpFile,String path_backup,String fmt)
	{
		Date now = new Date();
		File file = null;
		File bkpFile= null;
		if(fmt != null){
			String yyyyMM=DateUtil.formatDate(now,fmt);
		    file = new File(path_backup+"/"+yyyyMM);
		    bkpFile = new File(path_backup+"/"+yyyyMM+"/"+tmpFile.getName());
		}else{
			file = new File(path_backup);
			bkpFile = new File(path_backup+"/"+tmpFile.getName());
		}	    
		if(!file.exists()){
			file.mkdirs();
		}		
		if(bkpFile.exists()){
			bkpFile.delete();
		}		
	    tmpFile.renameTo(bkpFile);
	}
	
	public static void deleteFile(File file) {
//		file.deleteOnExit();
		file.delete();
	}
    
	public static byte[] readFileBytes(File file) {
	  byte fileContent[]=null;
	  
	          //File file = new File(fileName);
	          FileInputStream fin = null;
	          try {
	              fin = new FileInputStream(file);
	              fileContent = new byte[(int)file.length()];
	              fin.read(fileContent);
	          }
	          catch (Exception e) {
	              System.out.println("File not found" + e);
	          }
	          
	          finally {
	              try {
	                  if (fin != null) {
	                      fin.close();
	                  }
	              }
	              catch (Exception ioe) {
	                  System.out.println("Error while closing stream: " + ioe);
	              }
	          }
	          return fileContent;
	      }
	public static String readFile(File file)
	{
		StringBuffer sb=null;
		BufferedReader br=null;
		try 
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			//br=new BufferedReader(new FileReader(file));
			String line;
			sb=new StringBuffer();
			while ((line=br.readLine())!=null) {
				sb.append(line+"\n");
		    }
	    }
		catch (Exception e) 
		{
		 	e.printStackTrace();
		} 
		finally 
		{
			try 
			{
			  if (br !=null) br.close();
			}
			catch (Exception ex){}
	    }
		return sb.toString();
	}
	public static String readFile(File file, String chartset)
	{
		StringBuffer sb=null;
		BufferedReader br=null;
		try 
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),chartset));
			String line;
			sb=new StringBuffer();
			while ((line=br.readLine())!=null) {
				sb.append(line+"\n");
		    }
	    }
		catch (Exception e) 
		{
		 	e.printStackTrace();
		} 
		finally 
		{
			try 
			{
			  if (br !=null) br.close();
			}
			catch (Exception ex){}
	    }
		return sb.toString();
	}
	public static void writeFile(File file,String content)
	{
		FileOutputStream fo=null;
		try 
		{
			if(!file.exists()) {
				file.createNewFile();
			}
			fo=new FileOutputStream(file,false);
			
			byte [] bContent=content.getBytes("UTF-8");
			fo.write(bContent);
			fo.flush();
			fo.close();
			
	    }
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			try 
			{
			  if (fo !=null) fo.close();
			}
			catch (Exception ex){}
	    }
	}	
	public static boolean validateXMLSchema(String xsdPath, String xmlPath){
	      try {
	         SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	         Schema schema = factory.newSchema(new File(xsdPath));
	            Validator validator = schema.newValidator();
	            validator.validate(new StreamSource(new File(xmlPath)));
	      } catch (Exception e){    
	         System.out.println("Exception: "+e.getMessage());
	         return false;
	      }
	      return true;
	 }
	public static boolean validateXMLSchema(File xsdFile, File xmlFile){
	    boolean ret = false;  
	    
	    Reader reader = null;
		BufferedReader buff = null;
		try {
			  reader = new java.io.BufferedReader(new FileReader(xmlFile));
			  buff = new BufferedReader(reader);
	    	  if(xsdFile.exists()) {
	    		  SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	    		  Schema schema = factory.newSchema(xsdFile);
	    		  Validator validator = schema.newValidator();
	    		  validator.validate(new StreamSource(buff));
	    		  buff.close();
	    		  ret = true;
	    	  }
	    	  else {
	    		  ret = true;//ignore
	    	  }
	      } catch (Exception e){
	    	  e.printStackTrace();
	    	  ret = false;
	      }finally{
	    	  try {
				buff.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      }
	      return ret;
	 }
	//ducnm added
		public static void copy(File sourceLocation, File targetLocation) throws IOException {
		    if (sourceLocation.isDirectory()) {
		        copyDirectory(sourceLocation, targetLocation);	    	
		    } else {
		        copyFile(sourceLocation, targetLocation);
		    }
		}

		private static void copyDirectory(File source, File target) throws IOException {
//	    	System.out.println(source.getAbsolutePath());
		    if (!target.exists()) {
//		    	System.out.println(target.getName());
		        target.mkdir();
		    }
		    for (String f : source.list()) {
		        copy(new File(source, f), new File(target, f));
		    }
		}

		private static void copyFile(File source, File target) throws IOException {        
		    try (
		            InputStream in = new FileInputStream(source);
		            OutputStream out = new FileOutputStream(target)
		    ) {
		        byte[] buf = new byte[4056];
		        int length;
		        while ((length = in.read(buf)) > 0) {
		            out.write(buf, 0, length);
		        }
		    }
		}
	
		public static void clearFolder(String _folder){
			File directory = null;
			directory = new File(_folder);	
	    	//make sure directory exists
	    	if(!directory.exists()){	
	           System.out.println("Directory does not exist.");
//		           System.exit(0);	
	        }else{	
	           try{
	               delete(directory);
	           }catch(IOException e){
	               e.printStackTrace();
//		               System.exit(0);
	           }
	        }
		}
		private static void delete(File file) throws IOException{				
	    	if(file.isDirectory()){
	    		//directory is empty, then delete it
	    		if(file.list().length==0){
	    		   file.delete();
//		    		   System.out.println("Directory is deleted : " + file.getAbsolutePath());
	    		}else{
	    		   //list all the directory contents
	        	   String files[] = file.list();
	        	   for (String temp : files) {
	        	      //construct the file structure
	        	      File fileDelete = new File(file, temp);
	        	      //recursive delete
	        	     delete(fileDelete);
	        	   }
	        	   //check the directory again, if empty then delete it
	        	   if(file.list().length==0){
	           	     file.delete();
//		        	     System.out.println("Directory is deleted : "+ file.getAbsolutePath());
	        	   }
	    		}
	    	}else{
	    		//if file, then delete it
	    		file.delete();
	    		System.out.println("File is deleted : " + file.getAbsolutePath());
	    	}
	    }
		public static String readInputStream(InputStream resStream) throws IOException {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		    byte[] byteBuf = new byte[10240];
		    int nRead = 0;
		    while ((nRead = resStream.read(byteBuf, 0, byteBuf.length)) != -1) {
        	    buffer.write(byteBuf, 0, nRead);
            }
			return buffer.toString("UTF-8");	
		}
	 public static void main(String args[])
	 {
//		 boolean rt=validateXMLSchema("C:\\xmldata\\checkout1.xsd","C:\\xmldata\\CheckOut.xml");
//		 if(rt) {
//			 System.out.println("xml valid");
//		 }
//		 else {
//			 System.out.println("xml invalid");
//		 }
//		 removeFiles(new String[]{"c:\\xmldata\\backup - Copy","c:\\xmldata\\error - Copy"});
	 }
	 
}
