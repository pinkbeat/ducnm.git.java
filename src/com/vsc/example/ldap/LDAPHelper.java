package com.vsc.example.ldap;
/*
 * author ducnm
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cdit.sync.ad.ContactDTO;
import com.cdit.sync.ad.SyncUtility;
import com.cdit.sync.ad.TotalDTO;

public class LDAPHelper  {
	private static Hashtable env = null;
	private static String provider = null;
	private static String svr = null;
	private static String url = null;
	private static String baseDN = null;
	private static String principal = null;
	private static String credential = null;
	private static String lport = null;
	private static String type_auth = "simple";
	private static String search_query = "(objectclass=$)";
	private static int time_limit = 30000;
	private static int paging = 1000;
	private static DirContext ctx = null;
	private static LdapContext lctx = null;
	private static boolean isInit = false;
	NamingEnumeration ne = null;
	NamingEnumeration results = null;
	private static String ltype_ou = null;
	private static String ltype_user = null;
	private static String lacc = null;
	private static String lou = "ou";
	private boolean isClose = true;
	
	String entryDN = null;
	Attributes entry = null;
	Attribute userCn = null;
	Attribute userSn = null;
	Attribute userMail = null;
	Attribute org = null;
	Attribute userGivename = null;
	Attribute userPassword = null;
	Attribute oc = null;
	
	public static void main(String[] asd){
		LDAPHelper helper = new LDAPHelper();
		int i = 0;
//		helper.displayAllEntries(null);
//		helper.findEntryFormLDAP("(uid=ducnm)");
//		helper.findEntryFormLDAP("uid=BYT");
		LdapContext context = helper.authenticate();
//		System.out.println(helper.checkEntryExists("31291",null,"uid=*",null));
		List<String> a= (List<String>)helper.findEntryFormLDAP("uid=ducnm");
		System.out.println(a.get(0));
		helper.updateEntry(helper.getNewBranch("uid=ducnm", a.get(0)),new EntryDTO("ducnm", "ducnm", "ducnm@vnpt.vn", "unknown", "123123"),1,context);
		try {
			context.close();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	public LDAPHelper(){
		url=LDAPConfig.getLdapURL();
		svr = LDAPConfig.getLdapServer();
		provider = LDAPConfig.getLdapProvider();
		baseDN = LDAPConfig.getLdapBaseDN();
		principal = LDAPConfig.getLdapUserName();
		credential = LDAPConfig.getLdapPassword();
		type_auth = LDAPConfig.getLdapTypeAuth();
		lport = LDAPConfig.getLdapServerPort();
		ltype_ou = LDAPConfig.getLdapTypeOU();
		ltype_user = LDAPConfig.getLdapTypeUser();
		lacc = LDAPConfig.getLdapAccount();
		time_limit = new Integer(LDAPConfig.getLdapTimeout());
		paging = new Integer(LDAPConfig.getLdapPaging());
	}
	public LDAPHelper(String ldap_provider,String adr,String dn, String cn, String secret){
		provider = ldap_provider;
		url = adr;
		baseDN = dn;
		principal = cn;
		credential = secret;
	}
	private void setCloseConnection(boolean yORn){
		isClose = yORn;
	}
	private String getNewBranch(String strNeedCut,String dnSrc){
		StringBuilder builder = null;
		if(dnSrc != null && strNeedCut!= null){
			builder = new StringBuilder(dnSrc);
			int j=strNeedCut.length();
			int i=builder.indexOf(strNeedCut);
			builder = builder.delete(i, i+j+1);
			if(builder.lastIndexOf(",")==builder.length()-1)
				builder = builder.deleteCharAt(builder.lastIndexOf(","));
			return builder.toString();
		}else{
			return dnSrc;
		}
	}
	public LdapContext authenticate(){
		try {
			init();			
		    this.lctx= new InitialLdapContext(env,null);
		    System.out.println("authenticated");
//		    System.out.println(lctx.getEnvironment());		     		    		
		} catch (AuthenticationNotSupportedException ex) {
		    System.out.println("The authentication is not supported by the server");
		} catch (AuthenticationException ex) {
		    System.out.println("incorrect password or username");
		} catch (NamingException ex) {
		    System.out.println("error when trying to create the context");
		}
		return lctx;
	}
	public void close(){	
		if(isClose){
			try {
				if(ctx != null){
					ctx.close();
					ctx = null;
				}
				if(lctx != null){
					lctx.close();
					this.lctx=null;
				}
				if(ne != null){
					ne.close();
					ne=null;
				}
				if(results != null){
					results.close();
					results=null;
				}						
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				System.out.println("error when trying to close the context");
				e.printStackTrace();
			}
		}
	}
	private void init() {
		// TODO Auto-generated method stub
		if(env == null){
			env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, url);
			env.put(Context.SECURITY_AUTHENTICATION, type_auth);
			env.put(Context.SECURITY_PRINCIPAL, principal);
			env.put(Context.SECURITY_CREDENTIALS, credential);
			env.put("java.naming.ldap.version", "3");
			env.put("java.naming.ldap.attributes.binary","objectGUID");
		}
	}
	public int deleteEntry(String uid,String queryString){
		int r = -1;				
		try {			
//			setCloseConnection(true);			
			if(lctx== null){
				lctx= authenticate();
			}			
			List<String> list = findEntryFormLDAP(uid, null, queryString);
			System.out.println(list.size());
			String tmp=null;
			if(list!=null){
				for(int i=0;i<list.size();i++){
					tmp = list.get(i)+","+baseDN;
					System.out.println(tmp);
					lctx.destroySubcontext(tmp);
				}					
			}				
			r=1;
//			setCloseConnection(true);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			close();
		}
		return r;
	}
	public int updateEntry(String baseDn, EntryDTO entryDTO,int typeEntry,LdapContext context){
		makeAttribute(baseDn, entryDTO, typeEntry);
		return updateEntry(entryDTO, context);
	}
	public int updateEntry(EntryDTO entryDTO,LdapContext context){
		if(lctx==null){			
			lctx = context;
		}			
		return updateEntry(entryDTO);
	}
	private int updateEntry(EntryDTO entryDTO){
		int flag = -1;//-1 error,1 success
		try {
//			if(lctx==null){
				lctx = authenticate();
//			}
			lctx.modifyAttributes(entryDTO.getDn(), DirContext.REPLACE_ATTRIBUTE, entryDTO.getAtt());
			flag=1;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}
	public int addEntry(String baseDn, EntryDTO entryDTO,int typeEntry,LdapContext context){
		int flag = -1;//-1 error,1 success,2 exists
		try{			
//			lctx.bind(entryDN, entry);//for javaContainer, requires 'cn'
			entryDTO = makeAttribute(baseDn, entryDTO, typeEntry);
			context.createSubcontext(entryDTO.getDn(), entryDTO.getAtt());
			flag = 1;
		}catch(Exception e){
			System.out.println("error: " + e.getMessage());
			if(e.getMessage().indexOf("code 68")!=-1){
				
				//return 2;
			}
			if(e.getMessage().indexOf("code 32")!=-1){				
				return 3; // ko ton tai
			}
		}
		return flag;
	}
	public int addEntry(String baseDn, EntryDTO entryDTO,int typeEntry){
		int flag = -1;//-1 error,1 success,2 exists
		Attribute oc = new BasicAttribute("objectClass");
		if(baseDn != null)
			if(!checkEntryExists(baseDn,null,"(ou=*)",null))
				addEntry(null, new EntryDTO(null, null, null, baseDn, null), 2);
		if(typeEntry == 1){
			userCn = new BasicAttribute("cn", entryDTO.getUserCn());
			userSn = new BasicAttribute("sn", entryDTO.getUserSn());
			userMail = new BasicAttribute("mail", entryDTO.getUserMail());
			org = new BasicAttribute("o", entryDTO.getOrg());
			userGivename = new BasicAttribute("givenName", entryDTO.getUserGivename());
			userPassword = new BasicAttribute("userPassword", entryDTO.getUserPassword());
			//ObjectClass attributes
			oc = new BasicAttribute("objectClass");
			oc.add("person");
			oc.add("organizationalPerson");
			oc.add("inetOrgPerson");
			oc.add("top");
		 
			entry = new BasicAttributes();
			entry.put(userCn);
			entry.put(userSn);
			entry.put(userMail);
			entry.put(userGivename);
			entry.put(userPassword);
			entry.put(oc);
			if(baseDn != null){
				if(entryDTO.getOrg()!=null){					
					if(!checkEntryExists(entryDTO.getOrg(),baseDn,"(ou=*)",null))
						addEntry(baseDn, new EntryDTO(null, null, null, entryDTO.getOrg(), null), 2);
					entryDN = "uid="+entryDTO.getUid()+",ou="+entryDTO.getOrg()+","+baseDn+","+baseDN;
				}
				else {
					entryDN = "uid="+entryDTO.getUid()+","+baseDn+","+baseDN;
				}
			}else{
				if(entryDTO.getOrg()!=null){					
					if(!checkEntryExists(entryDTO.getOrg(),null,"(ou=*)",null))
						addEntry(baseDn, new EntryDTO(null, null, null, entryDTO.getOrg(), null), 2);
					entryDN = "uid="+entryDTO.getUid()+",ou="+entryDTO.getOrg()+","+baseDN;
				}
				else {
					entryDN = "uid="+entryDTO.getUid()+","+baseDN;
				}				
			}
		}else if(typeEntry ==2){
			entry = new BasicAttributes(true); // case-ignore
	        Attribute objclass = new BasicAttribute("objectclass");
	        objclass.add("top");
	        objclass.add("organizationalUnit");
	        entry.put(objclass);
	        if(baseDn != null)
	        	entryDN = "ou="+entryDTO.getOrg()+","+baseDn+","+baseDN;
	        else
	        	entryDN = "ou="+entryDTO.getOrg()+","+baseDN;
		}		
		System.out.println("entryDN :" + entryDN);
		try{			
//			if(this.lctx== null)
//				this.lctx= authenticate();
//			lctx.bind(entryDN, entry);//for javaContainer, requires 'cn'
			entryDTO = makeAttribute(baseDn, entryDTO, typeEntry);
			lctx.createSubcontext(entryDTO.getDn(), entryDTO.getAtt());
			flag = 1;
		}catch(Exception e){
			System.out.println("error: " + e.getMessage());
			if(e.getMessage().indexOf("code 68")!=-1){				
				return 2;
			}
		}
		return flag;
	}
	public EntryDTO makeAttribute(String baseDn,EntryDTO entryDTO,int typeEntry){
		
		if(typeEntry == 1){
			userCn = new BasicAttribute("cn", entryDTO.getUserCn());
			userSn = new BasicAttribute("sn", entryDTO.getUserSn());
			userMail = new BasicAttribute("mail", entryDTO.getUserMail());
			org = new BasicAttribute("o", entryDTO.getOrg());
			userGivename = new BasicAttribute("givenName", entryDTO.getUserGivename());
			userPassword = new BasicAttribute("userPassword", entryDTO.getUserPassword());
			//ObjectClass attributes
			oc = new BasicAttribute("objectClass");
			oc.add("person");
			oc.add("organizationalPerson");
			oc.add("inetOrgPerson");
			oc.add("top");
			
			entry = new BasicAttributes();
			entry.put(userCn);
			entry.put(userSn);
			entry.put(userMail);
			entry.put(userGivename);
			entry.put(userPassword);
			entry.put(oc);
			if(baseDn != null){
				entryDN = "uid="+entryDTO.getUid()+","+baseDn+","+baseDN;
			}else{
				entryDN = "uid="+entryDTO.getUid()+","+baseDN;				
			}
		}else if(typeEntry ==2){
			entry = new BasicAttributes(true); // case-ignore
	        oc = new BasicAttribute("objectclass");
	        oc.add("top");
	        oc.add("organizationalUnit");
	        entry.put(oc);
	        if(baseDn != null)
	        	entryDN = "ou="+entryDTO.getOrg()+","+baseDn+","+baseDN;
	        else
	        	entryDN = "ou="+entryDTO.getOrg()+","+baseDN;
		}
		
//		System.out.println("entryDN :" + entryDN);
		entryDTO.setAtt(entry);
		entryDTO.setDn(entryDN);
		return entryDTO;
	}
	
	private static SearchControls constraints() { 
	    SearchControls searchControls = new SearchControls(); 
	    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE); 
	    searchControls.setTimeLimit(time_limit); 
	    return searchControls; 
	}
	public void displayAllEntries(String nameS)
	{		
	    //Search entry
	    try {	  
	    	ctx = (DirContext)authenticate();
	    	if(nameS== null)
	    		results = ctx.search(baseDN, search_query, constraints());
	        else
	        	results = ctx.search(nameS+","+baseDN, search_query, constraints());
	  	    if(results.hasMore()==false)
		  	    System.out.println("Error!... The Input Entry does not exists");
	  	    // lay ra tung Entry
	  	    int countOLDAP=0;
	  	    while (results.hasMore()) {
	  	    	SearchResult searchResult =(SearchResult)results.next();
	  	    	countOLDAP++;
	  	    	System.out.println(countOLDAP+"."+searchResult.getName());	    	  
		    	   //Cac thuoc tinh cua moi Entry
	    	    Attributes attrs = searchResult.getAttributes();
	    	    if (attrs == null) {
	    	        System.out.println("No attributes");
	    	        continue;
	    	    }
	    	    //Duyet tung thuoc tinh
	    	    ne = attrs.getAll(); 
	    	    while (ne.hasMoreElements()) {
	    	        Attribute attr =(Attribute)ne.next();
	    	        //ten thuoc tinh
	    	        String id = attr.getID();	    	       
	    	        //cac gia tri cua thuoc tinh
	    	        Enumeration vals = attr.getAll();
	    	        while (vals.hasMoreElements())
	    	            System.out.println("   "+id + ": " + vals.nextElement());	    	            
	    	            //System.out.println(vals.nextElement());
	    	    }
	    	}   
	    } catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
	    	close();
		}
	}
	private void reportEntryFormLDAP(String dn,String searchType){		
		byte[] cookie = null;
		List listTotal = null;
		int total = 0;
		int countOU=0;
		int countuser=0;
		int countobj=0;
		String tmp = "";
		String id = "";
		try {
			if(this.lctx== null)
				this.lctx= authenticate();
			lctx.setRequestControls(new Control[] { new PagedResultsControl(
					paging, Control.CRITICAL) });
			System.out.println("START TIME: " + System.currentTimeMillis());

			do {						
				try{
					//results = ctx.search(entry, "(|(objectClass=organizationalUnit)(objectClass=user))", constraints());
					if(dn== null)
						results = lctx.search(dn, searchType, constraints());
			        else
			        	results = lctx.search(dn+","+baseDN, searchType, constraints());
					
					while (results!=null && results.hasMoreElements()) {
						SearchResult searchResult = (SearchResult) results.next();
						Attributes attributes = searchResult.getAttributes();
						//Attribute att = attributes.get( "objectClass" );
						try{
							ne = attributes.getAll();
							while (ne.hasMoreElements()){
								Attribute attr  = (Attribute)ne.next();
								id =  attr.getID();
								NamingEnumeration vals = attr.getAll();
								while (vals.hasMoreElements()){
									tmp = vals.nextElement().toString();
									if(tmp.equals(ltype_ou)){										
//										if(!tmp.equals("Computer")){
										//System.out.println("OU = "+(String)attributes.get(lou).get());
										countOU++;
//										}
									}else if(tmp.equals(ltype_user)){
										//System.out.println("User = "+(String)attributes.get(lacc).get());
										countuser++;
									}
								}								
							}															
						}catch(Exception e){
							System.out.println("Error :"+e.getMessage());
						}			
						countobj++;
					}

					Control[] control = lctx.getResponseControls();
					if (control != null) {
						for (int i = 0; i < control.length; i++) {
							if (control[i] instanceof PagedResultsResponseControl) {
								PagedResultsResponseControl prrc = (PagedResultsResponseControl) control[i];
								total = prrc.getResultSize();
								cookie = prrc.getCookie();
								System.out.println(cookie);
							} else {
								// Handle other response controls (if any)
							}
						}
					}	
					// Re-activate paged results
					lctx.setRequestControls(new Control[] { new PagedResultsControl(
							paging, cookie, Control.CRITICAL) });
				
				}catch (Exception e){
					e.printStackTrace();
				}
			} while (cookie != null);

			if(results != null && !results.hasMoreElements()){								
				System.out.println("Finded "+countobj+" objects");
				System.out.println("With "+countuser+" users");
				System.out.println("And "+countOU+" OU");
			}
			}catch(Exception e){
				e.printStackTrace();
			}finally {
				close();
			}		
		System.out.println("FINISH TIME: " + System.currentTimeMillis());		
	}
	public boolean checkEntryExists(String entry){
		return checkEntryExists(entry, null, search_query,null);
	}
	public boolean checkEntryExists(String entry, String queryString){
		return checkEntryExists(entry, null, queryString,null);
	}
	public boolean checkEntryExists(String entry, String dn ,String queryString, LdapContext context){
		String attr = null;	
		SearchResult searchResult=null;
		Attributes attributes=null;	
		boolean exists = false;
		try {
			if(context!= null)
				lctx = context;
			else
				lctx= authenticate();			
			if(entry!=null && entry.length()!=0){
				queryString = buildQueryString(entry, queryString);
			}
			if(dn== null)
				results = lctx.search(baseDN, queryString, constraints());
	        else
	        	results = lctx.search(dn+","+baseDN, queryString, constraints());							
			exists = results.hasMoreElements();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
//			close();
		}		
		return exists;
	}
	public List findEntryFormLDAP(String searchContent, String searchType){
		if(searchType != null){
			search_query = searchType;			
		}
		return findEntryFormLDAP(searchContent);
	}
	public List findEntryFormLDAP(String searchContent, String dn, String searchType){
		if(dn != null && dn.indexOf("-")!=-1){
			baseDN = dn.concat(",").concat(baseDN);
		}
		if(searchType != null){
			search_query = searchType;			
		}
		return findEntryFormLDAP(searchContent);
	}
//	private List findEntryFormLDAP(String searchContent, String dn,String searchType){		
//		byte[] cookie = null;
//		int total = 0;
//		int countobj=0;
//		String attr = null;	
//		SearchResult searchResult=null;
//		Attributes attributes=null;	
//		int count=0;
//		List<String> result = null;
//		String tmp = "";
//		try {
//			if(this.lctx== null)
//				this.lctx= authenticate();
//			lctx.setRequestControls(new Control[] { new PagedResultsControl(
//					paging, Control.CRITICAL) });
//			System.out.println("START TIME: " + System.currentTimeMillis());			
//			do {						
//				try{
//					//results = ctx.search(entry, "(|(objectClass=organizationalUnit)(objectClass=user))", constraints());		
//					if(searchContent!=null && searchContent.length()!=0){
//						searchType = buildQueryString(searchContent, searchType);
//					}
//					if(dn== null)
//						results = lctx.search(baseDN, searchType, constraints());
//			        else
//			        	results = lctx.search(dn+","+baseDN, searchType, constraints());
//									
//					attr = searchType.substring(1, searchType.indexOf("="));
////					System.out.println(attr);		
//					result = new ArrayList<String>();
//					while (results!=null && results.hasMoreElements()) {						
//						searchResult = (SearchResult) results.next();
//						attributes = searchResult.getAttributes();																	
//						try{
//								Attribute att = attributes.get(attr);
//								ne = att.getAll();									
//								while (ne.hasMoreElements()){
//									tmp = ne.next().toString();
//									if(searchContent!= null && searchContent.length()!=0){
//										if(searchContent.indexOf(tmp)!= -1){
//											System.out.println("Content "+tmp+" found in object: "+searchResult.getName());
//											result.add(searchResult.getName());
//											count++;
//										}																
//									}else{
//										System.out.println("Content "+tmp+" found in object: "+searchResult.getName());
//										result.add(searchResult.getName());
//										count++;
//									}
//									
//								}								
//						}catch(Exception e){
//							System.out.println("Error while getting attributes of the entry: "+e.getMessage());
//						}			
//						countobj++;
//					}					
//					Control[] control = lctx.getResponseControls();
//					if (control != null) {
//						for (int i = 0; i < control.length; i++) {
//							if (control[i] instanceof PagedResultsResponseControl) {
//								PagedResultsResponseControl prrc = (PagedResultsResponseControl) control[i];
//								total = prrc.getResultSize();
//								cookie = prrc.getCookie();
////								System.out.println(cookie);
//							} else {
//								// Handle other response controls (if any)
//							}
//						}
//					}	
//					// Re-activate paged results
//					lctx.setRequestControls(new Control[] { new PagedResultsControl(
//							paging, cookie, Control.CRITICAL) });
//				
//				}catch (Exception e){
//					e.printStackTrace();
//				}
//			} while (cookie != null);
//			if(count==0){
//				System.out.println("Can not find the entry: "+((searchContent==null||searchContent.length()==0)?searchType:searchContent));
//			}else{
//				System.out.println("Found "+count+" entries(y) with input content: "+((searchContent==null||searchContent.length()==0)?searchType:searchContent));
//			}
//			if(results != null && !results.hasMoreElements()){								
//				System.out.println("Finded on "+countobj+" objects");
//			}
//			}catch(Exception e){
//				e.printStackTrace();
//			}finally {
//				close();
//			}		
//		System.out.println("FINISH TIME: " + System.currentTimeMillis());		
//		return result;
//	}
	private List findEntryFormLDAP(String searchContent){		
		byte[] cookie = null;
		int total = 0;
		int countobj=0;
		String attr = null;	
		SearchResult searchResult=null;
		Attributes attributes=null;	
		int count=0;
		List<String> result = null;
		String tmp = "";
		try {
			if(lctx== null)
				lctx= authenticate();
			lctx.setRequestControls(new Control[] { new PagedResultsControl(
					paging, Control.CRITICAL) });
			System.out.println("START TIME: " + System.currentTimeMillis());			
			do {						
				try{
					results = lctx.search(baseDN, buildQueryString(searchContent, search_query), constraints());
					result = new ArrayList<String>();
					while (results!=null && results.hasMoreElements()) {						
						searchResult = (SearchResult) results.next();
						System.out.println("Content \""+searchContent+"\" has found in the entry: "+searchResult.getName());
						result.add(searchResult.getName());
						count++;													
						countobj++;
					}					
					Control[] control = lctx.getResponseControls();
					if (control != null) {
						for (int i = 0; i < control.length; i++) {
							if (control[i] instanceof PagedResultsResponseControl) {
								PagedResultsResponseControl prrc = (PagedResultsResponseControl) control[i];
								total = prrc.getResultSize();
								cookie = prrc.getCookie();
							} else {
								// Handle other response controls (if any)
							}
						}
					}	
					// Re-activate paged results
					lctx.setRequestControls(new Control[] { new PagedResultsControl(
							paging, cookie, Control.CRITICAL) });
				
				}catch (Exception e){
					e.printStackTrace();
				}
			} while (cookie != null);
//			if(count==0){
//				System.out.println("Can not find the entry: "+searchContent);
//			}else{
//				System.out.println("Found "+result.size()+" entries(y) with input content: "+searchContent);
//			}
//				System.out.println("Finded on "+countobj+" objects");
			}catch(Exception e){
				e.printStackTrace();
			}finally {
				close();
			}		
		System.out.println("FINISH TIME: " + System.currentTimeMillis());		
		return result;
	}
	private static String buildQueryString(String searchContent, String queryString){
		StringBuilder builder = null;
		if(queryString == null || searchContent.indexOf("=")!=-1){
			if(searchContent.indexOf("(")!=-1){
				return searchContent;
			}
			else{
				builder = new StringBuilder("("+searchContent+")");
			}
		}else{
			builder = new StringBuilder(queryString);
			if(queryString.indexOf("objectclass")!=-1){				
				builder.replace(queryString.indexOf("$"), queryString.indexOf("$")+1, searchContent);
			}else{
				if(queryString.indexOf("*") == -1)
					builder = replaceString(builder,searchContent.concat("*"),"$");
				else
					builder.replace(queryString.indexOf("*"), queryString.indexOf("*")+1, searchContent.concat("*"));
			}
		}			
//		System.out.println(builder.toString());
		return builder.toString();
	}
	private static StringBuilder replaceString(StringBuilder src, String des, String token){
		if(src!= null && token != null){	
			int i = src.indexOf(token);
			src = src.replace(i, i+1, des);
			if(src.indexOf(token)!=-1){
				src = replaceString(src, des, token);
			}			
		}
		return src;
	}
}

class LDAPConfig {
    private static Log log = LogFactory.getLog(LDAPConfig.class);
    private static Properties prop = null;
    private static String filename = "ldap.properties";
    private static String ldapServer = null;
    private static String ldapBaseDN = null;
    private static String ldapServerPort = null;
    private static String ldapUserName = null;
    private static String ldapPassword = null;
    private static String ldapProvider = null;
    private static String ldapTypeAuth = null;
    private static String ldapPaging = null;
    private static String ldapTimeout = null;
    private static String ldapURL = null;
    private static String ldapTypeUser = null;
    private static String ldapTypeOU = null;
    private static String ldapAccount = null;
    private static String ldapDisplayName = null;
    
    public static String getLdapServer() {
        return ldapServer;
    }
    public static String getLdapServerPort() {
        return ldapServerPort;
    }
    public static String getLdapBaseDN() {
        return ldapBaseDN;
    }
    public static String getLdapUserName() {
        return ldapUserName;
    }
    public static String getLdapPassword() {
        return ldapPassword;
    }
    public static String getLdapProvider() {
        return ldapProvider;
    }
    public static String getLdapTypeAuth() {
        return ldapTypeAuth;
    }
    public static String getLdapPaging() {
        return ldapPaging;
    }
    public static String getLdapTimeout() {
        return ldapTimeout;
    }
    public static String getLdapURL() {
        return ldapURL;
    }
    public static String getLdapTypeUser() {
        return ldapTypeUser;
    }
    public static String getLdapTypeOU() {
        return ldapTypeOU;
    }
    public static String getLdapAccount() {
        return ldapAccount;
    }
    public static String getLdapDisplayName() {
        return ldapDisplayName;
    }
    
    static {
        load();
    }

    public static void load() {
        reload();
    }
    
    private static Properties getResource() {
    	prop = new Properties();
    	InputStream input = null;    
    	try{
			input = LDAPConfig.class.getResourceAsStream(filename);	
			prop.load(input);
    	} catch (IOException e) {
            String message = "Can't read the configuration file: '" + filename + "'. Make sure the file is in your CLASSPATH";    		          
    		log.error(message, e);
        }		
		return prop;
    }

    private static void reload() {
            /* <LDAP Options> */
        	prop = getResource();
            ldapServer = prop.getProperty("ldap.oldap.server");
            ldapBaseDN = prop.getProperty("ldap.oldap.baseDN");
            ldapUserName = prop.getProperty("ldap.oldap.username");
            ldapPassword = prop.getProperty("ldap.oldap.password");
            ldapServerPort = prop.getProperty("ldap.oldap.port");
            ldapProvider= prop.getProperty("ldap.oldap.provider");
            ldapTypeAuth= prop.getProperty("ldap.oldap.type_auth");
            ldapPaging= prop.getProperty("ldap.oldap.paging");
            ldapTimeout= prop.getProperty("ldap.oldap.timeout");
            ldapURL = prop.getProperty("ldap.oldap.url");
            ldapTypeUser = prop.getProperty("ldap.oldap.type_user");
            ldapTypeOU = prop.getProperty("ldap.oldap.type_ou");
            ldapAccount = prop.getProperty("ldap.oldap.unique_acc");
            ldapDisplayName = prop.getProperty("ldap.oldap.display_name");
    }
}
class LDAPHelperException extends Exception{
	public LDAPHelperException(Exception e){
		super(e);
		if(e.getMessage().indexOf("code 32")!=-1){
    		System.out.println("The entry does not exists");
    	}else if(e.getMessage().indexOf("code 34")!=-1){
    		System.out.println("The entry is invalid DN");
    	}
	}
}
class EntryDTO{
	protected String uid = null;
	protected String userCn = null;
	protected String userSn = null;
	protected String userMail = null;
	protected String org = null;
	protected String userGivename = null;
	protected String userPassword = null;
	protected String dn = null;
	protected Attributes att = null;
	
	public EntryDTO(String u_id,String u_name,String u_mail, String orgName, String password) {
		// TODO Auto-generated constructor stub
		uid= u_id;
		userCn=u_name==null?"n/a":u_name;
		userSn=u_id;
		userMail=u_mail==null?"n/a":u_mail;
		userGivename=u_name==null?"n/a":u_name;
		org = orgName==null?"n/a":orgName;
		userPassword=password;		
	}

	protected String getDn() {
		return dn;
	}

	protected void setDn(String dn) {
		this.dn = dn;
	}

	protected Attributes getAtt() {
		return att;
	}

	protected void setAtt(Attributes att) {
		this.att = att;
	}

	protected String getUid() {
		return uid;
	}

	protected void setUid(String uid) {
		this.uid = uid;
	}

	protected String getUserCn() {
		return userCn;
	}

	protected void setUserCn(String userCn) {
		this.userCn = userCn;
	}

	protected String getUserSn() {
		return userSn;
	}

	protected void setUserSn(String userSn) {
		this.userSn = userSn;
	}

	protected String getUserMail() {
		return userMail;
	}

	protected void setUserMail(String userMail) {
		this.userMail = userMail;
	}

	protected String getOrg() {
		return org;
	}

	protected void setOrg(String org) {
		this.org = org;
	}

	protected String getUserGivename() {
		return userGivename;
	}

	protected void setUserGivename(String userGivename) {
		this.userGivename = userGivename;
	}

	protected String getUserPassword() {
		return userPassword;
	}

	protected void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	
}

















