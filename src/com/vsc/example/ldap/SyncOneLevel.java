package com.vsc.example.ldap;
/*
 * Thực hiện đồng bộ oneLevel from OpenLDAP System into Tivoli System and against
 * Created by Trienvu
 * Date 09/05/2012
 * */
import java.awt.List;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.omg.CORBA.CTX_RESTRICT_SCOPE;

import java.util.*;
public class SyncOneLevel 
{
	public SyncOneLevel() 
	{
	}
	//bien toan cuc
	static DirContext ctxIBM,ctxOLDAP;
    static  Scanner scan=new Scanner(System.in);
    static  Scanner scan1=new Scanner(System.in);
    static int countIBM,countOLDAP;
    //cac thuoc tinh name
    static String name,nameOLDAP;
    
	//Tao kết nối and binding user
    private static Hashtable getHashtableIBM()
	{
		Hashtable env = new Hashtable();
		String url="ldap://Trienvu-PC:1389";   	   
	    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	    env.put(Context.PROVIDER_URL, url);
	    env.put("java.naming.ldap.version","3");
	    //Binding user
	    env.put(Context.SECURITY_PRINCIPAL, "cn=root");//dn
	    env.put(Context.SECURITY_CREDENTIALS, "123456");//password
	   // env.put(Context.SECURITY_AUTHENTICATION, "CRAM-MD5"); 
	    env.put(Context.SECURITY_AUTHENTICATION, "simple");	    
	  //DirContext ctx = new InitialDirContext(env);
	    return env;
	}
    
    private static Hashtable getHashtableOpenLDAP()
   	{
   		Hashtable env = new Hashtable();
   		String url="ldap://Trienvu-PC:389";   	   
   	    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
   	    env.put(Context.PROVIDER_URL, url);
   	    env.put("java.naming.ldap.version","3");
   	    //Binding user
   	    env.put(Context.SECURITY_PRINCIPAL, "cn=Manager,dc=maxcrc,dc=com");//dn
   	    env.put(Context.SECURITY_CREDENTIALS, "123456");//password
   	    env.put(Context.SECURITY_AUTHENTICATION, "simple");	    
   	    return env;
   	}
	
	//Khai bao searchControls - kết quả của thao tác search
	private static SearchControls constraints() { 
	    SearchControls searchControls = new SearchControls(); 
	    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE); 
	    searchControls.setTimeLimit(30000); 
	    return searchControls; 
	}
	
	//Ham get entry
	
	private void displayIBM(String filter)
	{
	    //Search entry
	    try {	  
	    	countIBM=0;
	    	ctxIBM = new InitialDirContext(getHashtableIBM());
	    	//Tìm kiếm tất cả các thực thể có tên "Smith" và trả lại gia trị theo constrains().	    	
	  	    NamingEnumeration results = ctxIBM.search("", filter, constraints());
	  	    if(results.hasMore()==false)
	  	    System.out.println("Error!...Không tồn tại Entry với filter đã nhập");
	  	    // lay ra tung Entry
	  	    while (results.hasMore()) {
	  	    	SearchResult si =(SearchResult)results.next();
	    	    countIBM++;
	    	   System.out.println(countIBM+"."+si.getName());	    	  
		    	   //Cac thuoc tinh cua moi Entry
	    	    Attributes attrs = si.getAttributes();
	    	    if (attrs == null) {
	    	        System.out.println("   No attributes");
	    	        continue;
	    	    }
	    	    //Duyet tung thuoc tinh
	    	    NamingEnumeration ae = attrs.getAll(); 
	    	    while (ae.hasMoreElements()) {
	    	        Attribute attr =(Attribute)ae.next();
	    	        //ten thuoc tinh
	    	        String id = attr.getID();	    	       
	    	        //cac gia tri cua thuoc tinh
	    	        Enumeration vals = attr.getAll();
	    	        while (vals.hasMoreElements())
	    	            System.out.println("   "+id + ": " + vals.nextElement());
	    	    }
	    	    ae.close();
	    	}   
	    }catch(Exception e) {	    	
	    	System.out.println("Error!..Lỗi filter");
	    }
	}
	
	private void displayOLDAP(String filter)
	{
	    //Search entry
	    try {	  
	    	countOLDAP=0;
	    	ctxOLDAP = new InitialDirContext(getHashtableOpenLDAP());
	    	//Tìm kiếm tất cả các thực thể có tên "Smith" và trả lại gia trị theo constrains().	    	
	  	    NamingEnumeration results = ctxOLDAP.search("dc=maxcrc,dc=com", filter, constraints());
	  	    if(results.hasMore()==false)
		  	    System.out.println("Error!...Không tồn tại Entry với filter đã nhập");
	  	    // lay ra tung Entry
	  	    while (results.hasMore()) {
	  	    	SearchResult si =(SearchResult)results.next();
	    	   countOLDAP++;
	    	   System.out.println(countOLDAP+"."+si.getName());	    	  
		    	   //Cac thuoc tinh cua moi Entry
	    	    Attributes attrs = si.getAttributes();
	    	    if (attrs == null) {
	    	        System.out.println("   No attributes");
	    	        continue;
	    	    }
	    	    //Duyet tung thuoc tinh
	    	    NamingEnumeration ae = attrs.getAll(); 
	    	    while (ae.hasMoreElements()) {
	    	        Attribute attr =(Attribute)ae.next();
	    	        //ten thuoc tinh
	    	        String id = attr.getID();	    	       
	    	        //cac gia tri cua thuoc tinh
	    	        Enumeration vals = attr.getAll();
	    	        while (vals.hasMoreElements())
	    	            System.out.println("   "+id + ": " + vals.nextElement());
	    	            
	    	            //System.out.println(vals.nextElement());
	    	    }
	    	    if(countOLDAP==0)
	    	    	System.out.println("Error!..Ko tồn tại Entry cần tìm");
	    	    ae.close();
	    	}   
	    }catch(Exception e) {
	    	System.out.println("Error!..Lỗi filter");
	    }
	}


	private boolean checkExistNameIBM(String test,String filter)
	{
		try{
			countIBM=0;		
			boolean check=false; //bien kiem tra name da ton tai
			ctxIBM = new InitialDirContext(getHashtableIBM());
			//Lấy ra các name của OpenLDAP theo filter
	  	    NamingEnumeration resultsIBM = ctxIBM.search("",filter, constraints());	   	
	    	String ArrNames[]= new String[100];
	    	ArrayList al= new ArrayList();
	  	   // lay ra tung Entry	  	   
	  	    while (resultsIBM.hasMore()) {
	  	    	//  countOLDAP++;
		  	       SearchResult si =(SearchResult)resultsIBM.next();		    	       	  
			       al.add(si.getName());
	  	    }
	  	 
	  	   // String test="cn=hong anh,ou=people";
	  	    String temp;
	  	    for(int i=0;i<al.toString().length()-test.length();i++)
	  	    {
	  	    	temp=al.toString().substring(i,i+test.length());
	  	    	if(temp.equals(test))
	  	    		check=true;	  	   
	  	    }
	  	    	return check;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean checkExistNameOLDAP(String test,String filter)
	{
		try{
			countOLDAP=0;		
			boolean check=false; //bien kiem tra name da ton tai
			ctxOLDAP = new InitialDirContext(getHashtableOpenLDAP());
			//Lấy ra các name của OpenLDAP theo filter
	  	    NamingEnumeration resultsOLDAP = ctxOLDAP.search("dc=maxcrc,dc=com",filter, constraints());	   	
	    	String ArrNames[]= new String[100];
	    	ArrayList al= new ArrayList();
	  	   // lay ra tung Entry	  	   
	  	    while (resultsOLDAP.hasMore()) {
	  	    	//  countOLDAP++;
		  	       SearchResult si =(SearchResult)resultsOLDAP.next();		    	       	  
			       al.add(si.getName());
	  	    }
	  	   // String test="cn=hong anh,ou=people";
	  	    String temp;
	  	    for(int i=0;i<al.toString().length()-test.length();i++)
	  	    {
	  	    	temp=al.toString().substring(i,i+test.length());
	  	    	if(temp.equals(test))
	  	    		check=true;	  	   
	  	    }
	  	    	return check;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	private void syncIBMOLDAP(String filter){
		try{	
			//this.displayIBM(filter);
			countIBM=0;
	    	ctxIBM = new InitialDirContext(getHashtableIBM());	    	
	  	    NamingEnumeration results = ctxIBM.search("",filter, constraints());
	    	// lay ra tung Entry
	  	    while (results.hasMore()) {
		  	        SearchResult si =(SearchResult)results.next();
		    	    countIBM++;   	  
		    		name=si.getName();
				//	System.out.println(name);
					StringTokenizer st= new StringTokenizer(name,",");
					String cut="";
					String rename="";
					while(st.hasMoreTokens())
					{
						cut=st.nextToken();
					//	System.out.println(cut);
						if(cut.equalsIgnoreCase("dc=ibm"))
						{
							cut="dc=maxcrc";
						}
						rename=rename+","+cut;
					}
				
					rename=rename.substring(1);
				   
		    	    Attributes attrs = si.getAttributes();
		    	    if (attrs == null) {
		    	        System.out.println("   No attributes");
		    	        continue;
		    	    }
		    	    ctxOLDAP = new InitialDirContext(getHashtableOpenLDAP());
		    	    //ADD Entry
		    	    BasicAttributes attrs1 = new BasicAttributes();
		    	   
		    	    //Duyet tung thuoc tinh
		    	    NamingEnumeration ae = attrs.getAll(); 
		    	    while (ae.hasMoreElements()) {
		    	        Attribute attr =(Attribute)ae.next();
		    	        //ten thuoc tinh
		    	        BasicAttribute id1=new BasicAttribute(attr.getID());
		    	        //cac gia tri cua thuoc tinh
		    	        Enumeration vals = attr.getAll();
		    	        while (vals.hasMoreElements())
		    	        {
		    	            id1.add(vals.nextElement());
		    	        }
		    	        attrs1.put(id1);
		    	    }
		    	    if(filter.equals("dc=ibm"))
		    	    	filter="dc=maxcrc";
		    	     if((checkExistNameOLDAP(rename, filter)==false))
		    	    {
		    	      System.out.println(rename);
		    	      ctxOLDAP.createSubcontext(rename, attrs1);		
		    	    }
		    	    ae.close();
		    	}     	  
		 
		}catch(Exception ex)
		{	
			
		}
	}

	private void syncOLDAPIBM(String filter){
		try{	
			//this.displayOLDAP(filter);
			countOLDAP=0;
	    	ctxOLDAP = new InitialDirContext(getHashtableOpenLDAP());	
	    	NamingEnumeration results = ctxOLDAP.search("dc=maxcrc,dc=com", filter, constraints());
	  	    while (results.hasMore()) {
		  	        SearchResult si =(SearchResult)results.next();
		    	    countOLDAP++;   	  
		    		name=si.getName();
					StringTokenizer st= new StringTokenizer(name,",");
					String cut="";
					String rename="";
					while(st.hasMoreTokens())
					{
						cut=st.nextToken();
						if(cut.equalsIgnoreCase("dc=maxcrc"))
						{
							cut="dc=ibm";
						}
						rename=rename+","+cut;
					}
					rename=rename.substring(1);
			
			    	//Cac thuoc tinh cua moi Entry
		    	    Attributes attrs = si.getAttributes();
		    	    ctxIBM = new InitialDirContext(getHashtableIBM());
		    	    //ADD Entry
		    	    BasicAttributes attrs1 = new BasicAttributes();
		    	    //Duyet tung thuoc tinh
		    	    NamingEnumeration ae = attrs.getAll(); 
		    	    while (ae.hasMoreElements()) {
		    	        Attribute attr =(Attribute)ae.next();
		    	        //ten thuoc tinh
		    	        BasicAttribute id1=new BasicAttribute(attr.getID());
		    	        //cac gia tri cua thuoc tinh
		    	        Enumeration vals = attr.getAll();
		    	        while (vals.hasMoreElements())
		    	        {
		    	            id1.add(vals.nextElement());
		    	        }
		    	        attrs1.put(id1);
		    	    }
		    	    if(filter.equals("dc=maxcrc"))
		    	    	filter="dc=ibm";
		    	 
		    	    if((checkExistNameIBM(rename, filter)==false) && rename.equals(filter+",dc=ibm,dc=com")==false)
		    	    {
		    	      ctxIBM.createSubcontext(rename+",dc=ibm,dc=com", attrs1);		
		    	    }
		    	    ae.close();
		    	}     	  
		 
		}catch(Exception ex)
		{	
			ex.printStackTrace();
		}
	}
	
//Ham main
	
	public static void main(String[] args) {
	      int tmp=0;
	      SyncOneLevel t=new SyncOneLevel();
		   while(true)
	        {
	            System.out.println("\t\t\t ==========MENU===========");
	            System.out.println("\t\t 1. Display IBM");
	            System.out.println("\t\t 2. Display OpenLDAP");
	            System.out.println("\t\t 3. Sync from IBM into OpenLDAP");
	            System.out.println("\t\t 4. Sync from OpenLDAP into IBM");
	            System.out.println("\t\t 0. thoat");
	            System.out.print("\t\t  ---Ban chon :");	            
	            tmp=scan.nextInt();
	           // scan.nextLine();
	            switch(tmp)
	            {
	                case 1: 
	                	{
	                		System.out.print("Nhập filter IBM:");
	            			String filter=scan1.nextLine();
	                		t.displayIBM(filter);break;
	                	}
	                case 2: 
                	{
                		System.out.print("Nhập filter OLDAP:");
            			String filter=scan1.nextLine();
                		t.displayOLDAP(filter);break;
                	}
	                case 3: 
	                	{
	                		System.out.print("Nhập filter IBM:");
	            			String filter=scan1.nextLine();
	            		//	String filter="objectclass=inetOrgPerson";
	                		t.syncIBMOLDAP(filter);
	                		break;
	                	}
	                case 4: 
                	{
                		System.out.print("Nhập filter IBM:");
            			String filter=scan1.nextLine();
            		//	String filter="objectclass=inetOrgPerson";
                		t.syncOLDAPIBM(filter);
                		break;
                	}
                	
	                case 0: System.exit(0);
	                default: break;
	            }
	        }
	}
}
	
