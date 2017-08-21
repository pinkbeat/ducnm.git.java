package com.cdit.test.portal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestCode {
	static File file;
public static void main(String[] args) {
		
		List<String> names = new ArrayList<>();
		names.add("Java"); names.add("PHP");names.add("SQL");names.add("Angular 2");
		
		List<String> first2Names = names.subList(0, 2);
		
		System.out.println(names +" , "+first2Names);
		
		names.set(1, "JavaScript");
		//check the output below. 🙂
		System.out.println(names +" , "+first2Names);
		
		//Let's modify the list size and get ConcurrentModificationException
		names.add("NodeJS");
		System.out.println(names +" , "+first2Names); //this line throws exception


	}
	public static void main2(String args[]){
		
		List<String> myList = new ArrayList<String>();
//		List<String> myList = new CopyOnWriteArrayList<String>();
		myList.add("1");
		myList.add("2");
		myList.add("3");
		myList.add("4");
		myList.add("5");
		for(int i = 0; i<myList.size(); i++){
			System.out.println(myList.get(i));
			if(myList.get(i).equals("3")){
				myList.remove(i);
				i--;
//				myList.add("6");
//				myList.add("7");
			}
		}		
//		Iterator<String> it = myList.iterator();
////		String[] ar = (String[])myList.toArray();
//		while(it.hasNext()){
//			String value = it.next();
//			if(value.equals("3")) myList.remove("5");
//			System.out.println("Iterator Value:"+value);						
//		}
//		
		for(String a:myList){
			System.out.println("List Value:"+a);
		}
//		
////		Map<String,String> myMap = new HashMap<String,String>();
//		Map<String,String> myMap = new ConcurrentHashMap<String,String>();
//		myMap.put("1", "1");
//		myMap.put("2", "2");
//		myMap.put("3", "3");
//
//		Iterator<String> it1 = myMap.keySet().iterator();
//		while(it1.hasNext()){
//			String key = it1.next();
//			System.out.println("Map Value:"+myMap.get(key));
//			if(key.equals("2")){
//				myMap.put("1","4");
//				myMap.remove("2");
//				myMap.put("4", "4");
//			}
//		}		
//		Iterator<String> it2=myMap.keySet().iterator();
//		while (it2.hasNext()){
//			System.out.println(it2.next());
//		}
	}
	public static void main21(String[] asd) throws IOException{
		file = new File("d:\\224.xml");
		setAttribute(file, "user:ducnm","hehehehe");
//		System.out.println(getAttribute(file, "ducnm"));
//		System.out.println(file.toPath().toUri());
//		Object object = getAttribute(file, "user:ducnm");
//		basicAttribute();
//		dosAttribute();
//		showProperties(file);		
	}
	private static void setAttribute(File file, String atrr, ByteBuffer content ) throws IOException{
		Path path = file.toPath();
		UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
//		view.write(atrr,Charset.defaultCharset().encode("text/plan1"));
		view.write(atrr,content);
	}
	private static Object getAttribute(File file, String atrr) throws IOException{
		Path path = file.toPath();
		UserDefinedFileAttributeView view = Files.getFileAttributeView(path,UserDefinedFileAttributeView.class);
		ByteBuffer buf = ByteBuffer.allocate(view.size(atrr));
		view.read(atrr, buf);	
		return new String(buf.array(), 0, buf.position());
	}
	private static void setAttribute(File file, String atrr,String value) throws IOException{
		Files.setAttribute(file.toPath(), atrr, Charset.defaultCharset().encode(value));
	}
//	private static Object getAttribute(File file, String atrr) throws IOException{
//		return Files.getAttribute(file.toPath(), atrr);
//	}
	static void basicAttribute()throws IOException
    {
		Path path = file.toPath();
		BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
	
		System.out.println("creationTime: " + attr.creationTime());
		System.out.println("lastAccessTime: " + attr.lastAccessTime());
		System.out.println("lastModifiedTime: " + attr.lastModifiedTime());
		System.out.println("isDirectory: " + attr.isDirectory());
		System.out.println("isOther: " + attr.isOther());
		System.out.println("isRegularFile: " + attr.isRegularFile());
		System.out.println("isSymbolicLink: " + attr.isSymbolicLink());
		System.out.println("size: " + attr.size());		
    }
	static void dosAttribute()throws IOException
    {
		try {
		    DosFileAttributes attr =
		        Files.readAttributes(file.toPath(), DosFileAttributes.class);
		    System.out.println("isReadOnly is " + attr.isReadOnly());
		    System.out.println("isHidden is " + attr.isHidden());
		    System.out.println("isArchive is " + attr.isArchive());
		    System.out.println("isSystem is " + attr.isSystem());
		} catch (UnsupportedOperationException x) {
		    System.err.println("DOS file" +
		        " attributes not supported:" + x);
		}
    }
    static void saveProperties(Properties p)throws IOException
    {
            FileOutputStream fr=new FileOutputStream(file);
            p.store(fr,"Properties");
            fr.close();
            System.out.println("After saving properties:"+p);
    }
    static void loadProperties(Properties p)throws IOException
    {
            FileInputStream fi=new FileInputStream(file);
            p.load(fi);
            fi.close();
            System.out.println("After Loading properties:"+p);
}
    public static void showProperties(File file) throws IOException
    {
            Properties table=new Properties();
            table.setProperty("Shivam","Bane");
            table.setProperty("CS","Maverick");
            System.out.println("Properties has been set in HashTable:"+table);
            //saving the properties in file
            saveProperties(table);
            //changing the property
            table.setProperty("Shivam","Swagger");
            System.out.println("After the change in HashTable:"+table);
            //saving the properties in file
            saveProperties(table);
            //Loading the saved properties
            loadProperties(table);
    }
}
