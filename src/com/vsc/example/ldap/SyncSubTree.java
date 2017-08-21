package com.vsc.example.ldap;
/*
 * Thực hiện đồng bộ SubTree from OpenLDAP System into Tivoli System and against
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
public class SyncSubTree 
{
	public SyncSubTree() 
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
   	   // env.put(Context.SECURITY_AUTHENTICATION, "CRAM-MD5"); 
   	    env.put(Context.SECURITY_AUTHENTICATION, "simple");	    
   	  //DirContext ctx = new InitialDirContext(env);
   	    return env;
   	}
	
	//Khai bao searchControls - kết quả của thao tác search
	private static SearchControls constraints() { 
	    SearchControls searchControls = new SearchControls(); 
	    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE); 
	    searchControls.setTimeLimit(30000); 
	    //Chon lua thuoc tinh se lay ra
	   // String[] attrIDs = {"objectclass"}; 
	   // searchControls.setReturningAttributes(attrIDs); 
	    return searchControls; 
	}
	
	//Ham get entry
	
	private void displayIBM(String nameS)
	{
	    //Search entry
	    try {	  
	    	String baseDN="dc=ibm,dc=com";
	    	countIBM=0;
	    	ctxIBM = new InitialDirContext(getHashtableIBM());	  
	    	NamingEnumeration results;
	  	    if(nameS.equals("dc=ibm"))
	  	    	 results = ctxIBM.search(baseDN, "(objectclass=*)", constraints());
	  	    else
	             results= ctxIBM.search(nameS+","+baseDN, "(objectclass=*)", constraints());
	  	    if(results.hasMore()==false)
	  	    System.out.println("Error!...Không tồn tại Entry với name đã nhập");
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
	    	System.out.println("Error!..Lỗi name");
	    }
	}
	
	private void displayOLDAP(String nameS)
	{
	    //Search entry
	    try {	  
	    	countOLDAP=0;
	    	ctxOLDAP = new InitialDirContext(getHashtableOpenLDAP());
	    	String baseDN="dc=maxcrc,dc=com";
	    	//Tìm kiếm tất cả các thực thể có tên "Smith" và trả lại gia trị theo constrains().	    	
	    	NamingEnumeration results;
	    	if(nameS.equals("dc=maxcrc"))
	    	results = ctxOLDAP.search(baseDN, "(objectclass=*)", constraints());
	        else
	        results = ctxOLDAP.search(nameS+","+baseDN, "(objectclass=*)", constraints());
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
	    	System.out.println("Error!..Lỗi name");
	    }
	}


	private boolean checkExistNameIBM(String test,String nameS)
	{
		try{
			countIBM=0;		
			boolean check=false; //bien kiem tra name da ton tai			
	  	    String baseDN="dc=ibm,dc=com";
	    	countIBM=0;
	    	ctxIBM = new InitialDirContext(getHashtableIBM());	  
	    	NamingEnumeration resultsIBM;
	  	    if(nameS.equals("dc=maxcrc"))
	  	    	resultsIBM = ctxIBM.search(baseDN, "(objectclass=*)", constraints());
	  	    else
	  	    	resultsIBM= ctxIBM.search(nameS+","+baseDN, "(objectclass=*)", constraints());
	    	String ArrNames[]= new String[100];
	    	ArrayList al= new ArrayList();
	  	   // lay ra tung Entry	  	   
	  	    while (resultsIBM.hasMore()) {
	  	    	//  countOLDAP++;
		  	       SearchResult si =(SearchResult)resultsIBM.next();		    	       	  
			       al.add(si.getName());
	  	    }
	  	    //	System.out.println(al);
	  	    
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
			//e.printStackTrace();
			return false;
		}
	}
	
	private boolean checkExistNameOLDAP(String test,String nameS)
	{
		try{
			countOLDAP=0;		
			boolean check=false; //bien kiem tra name da ton tai
			ctxOLDAP = new InitialDirContext(getHashtableOpenLDAP());
			//Lấy ra các name của OpenLDAP theo filter
	  	    String baseDN="dc=maxcrc,dc=com";
	    	//Tìm kiếm tất cả các thực thể có tên "Smith" và trả lại gia trị theo constrains().	    	
	    	NamingEnumeration resultsOLDAP;
	    	if(nameS.equals("dc=ibm"))
	    		resultsOLDAP = ctxOLDAP.search(baseDN, "(objectclass=*)", constraints());
	        else
	        	resultsOLDAP = ctxOLDAP.search(nameS+","+baseDN, "(objectclass=*)", constraints());
	    	String ArrNames[]= new String[100];
	    	ArrayList al= new ArrayList();
	  	   // lay ra tung Entry	  	   
	  	    while (resultsOLDAP.hasMore()) {
	  	    	//  countOLDAP++;
		  	       SearchResult si =(SearchResult)resultsOLDAP.next();		    	       	  
			       al.add(si.getName());
	  	    }
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
		//	e.printStackTrace();
			return false;
		}
	}
	private void syncIBMOLDAP(String nameS){
		try{	
			
			String baseDN="dc=ibm,dc=com";
			String baseDNOLDAP="dc=maxcrc,dc=com";
	    	countIBM=0;
	    	ctxIBM = new InitialDirContext(getHashtableIBM());	  
	    	NamingEnumeration results;
	  	    if(nameS.equals("dc=ibm"))
	  	    	 results = ctxIBM.search(baseDN, "(objectclass=*)", constraints());
	  	    else
	             results= ctxIBM.search(nameS+","+baseDN, "(objectclass=*)", constraints());
	  	    if(results.hasMore()==false)
	  	    System.out.println("Error!...Không tồn tại Entry với name đã nhập");
	  	    // lay ra tung Entry
	  	    while (results.hasMore()) {
	  	    	SearchResult si =(SearchResult)results.next();
	    	    countIBM++;
		    		name=si.getName();
			    	   //Cac thuoc tinh cua moi Entry
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
		    	   // System.out.println(checkExistNameOLDAP(name, nameS));
		    	    if((checkExistNameOLDAP(name, nameS)==false))
		    	    {
		    	    	if(nameS.equals("dc=ibm"))
		    	               ctxOLDAP.createSubcontext(name+","+baseDNOLDAP, attrs1);
		    	    	else
    	    	        	   if(name.equals(""))
    	    	        		  ctxOLDAP.createSubcontext(nameS+","+baseDNOLDAP, attrs1);
    	    	        	   else
    	    	        		  ctxOLDAP.createSubcontext(name+","+nameS+","+baseDNOLDAP, attrs1);
    	    }
		    	    ae.close();
		    	}     	  
		 
		}catch(Exception ex)
		{	
			System.out.println("Error!...Tên ko tồn tại");
		}
	}

	
	private void syncOLDAPIBM(String nameS){
		try{
			String baseDN="dc=maxcrc,dc=com";
			String baseDNIBM="dc=ibm,dc=com";
	    	countOLDAP=0;
	    	ctxOLDAP = new InitialDirContext(getHashtableOpenLDAP());	  
	    	NamingEnumeration results;
	  	    if(nameS.equals("dc=maxcrc"))
	  	    	 results = ctxOLDAP.search(baseDN, "(objectclass=*)", constraints());
	  	    else
	             results= ctxOLDAP.search(nameS+","+baseDN, "(objectclass=*)", constraints());
	  	    if(results.hasMore()==false)
	  	    System.out.println("Error!...Không tồn tại Entry với name đã nhập");
	  	    // lay ra tung Entry
	  	    while (results.hasMore()) {
	  	    	SearchResult si =(SearchResult)results.next();
	    	    countOLDAP++;
		    		name=si.getName();
			    	   //Cac thuoc tinh cua moi Entry
		    	    Attributes attrs = si.getAttributes();
		    	    if (attrs == null) {
		    	        System.out.println("   No attributes");
		    	        continue;
		    	    }
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
		    	 
		    	    if((checkExistNameIBM(name, nameS)==false))
		    	    {
		    	    	if(nameS.equals("dc=maxcrc"))
		    	               ctxIBM.createSubcontext(name+","+baseDNIBM, attrs1);
		    	    
		    	    	else
    	    	        	   if(name.equals(""))
    	    	        		  ctxIBM.createSubcontext(nameS+","+baseDNIBM, attrs1);
    	    	        	   else
    	    	        		  ctxIBM.createSubcontext(name+","+nameS+","+baseDNIBM, attrs1);
    	    }
		    	    ae.close();
		    	}     	  
		 
		}catch(Exception ex)
		{	
			System.out.println("Error!...Tên ko tồn tại");
		}
	}
	
	
//Ham main
	
	public static void main(String[] args) {
	      int tmp=0;
	      SyncSubTree t=new SyncSubTree();
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
	                		System.out.print("Nhập name:");
	            			String nameS=scan1.nextLine();
	                		t.displayIBM(nameS);break;
	                	}
	                case 2: 
                	{
                		System.out.print("Nhập filter OLDAP:");
            			String filter=scan1.nextLine();
                		t.displayOLDAP(filter);break;
                	}
	                case 3: 
	                	{
	                		System.out.print("Nhập name cần đồng bộ:");
	            			String filter=scan1.nextLine();
	                		t.syncIBMOLDAP(filter);
	                		break;
	                	}
	                case 4: 
                	{
                		System.out.print("Nhập name cần đồng bộ:");
            			String name=scan1.nextLine();
                		t.syncOLDAPIBM(name);
                		break;
                	}
                	
	                case 0: System.exit(0);
	                default: break;
	            }
	        }
	}
}
	
