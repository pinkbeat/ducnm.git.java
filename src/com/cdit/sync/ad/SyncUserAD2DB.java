package com.cdit.sync.ad;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

public class SyncUserAD2DB {
	public static List<String> listUrl = null;
	public TransferToDB db = null;
	public boolean isTrans = true;
	public boolean isDebug = false;

	public SyncUserAD2DB() {
		if(isTrans)
			db = new TransferToDB();
	}
//	public SyncUserAD2DB(TransferToDB dbobj) {
//		db = dbobj;
//	}
	public SyncUserAD2DB(TransferToDB dbobj) {
		if(isTrans)
			db = dbobj;
	}
	public static void main(String[] args) throws ClassNotFoundException {
		(new SyncUserAD2DB()).connectToSyncData();
	}

	public void connectToSyncData() {
		Hashtable env1 = new Hashtable();

		env1.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env1.put(Context.PROVIDER_URL, "ldap://10.1.3.4:389/");
		env1.put(Context.SECURITY_AUTHENTICATION, "simple");
		env1.put(Context.SECURITY_PRINCIPAL, "fimadmin");
		env1.put(Context.SECURITY_CREDENTIALS, "3k$L46t");
		env1.put("java.naming.ldap.version", "3");
		//specify attributes to be returned in binary format
		//env1.put("java.naming.ldap.attributes.binary","objectSid");
		env1.put("java.naming.ldap.attributes.binary","objectGUID");
		LdapContext ctx = null;

		byte[] cookie = null;
		int total = 0;
		int tongacc = 0;
		int pageSize = 6000; // 5000 entries per page
		NamingEnumeration results = null;
		List listTotal = null; // new ArrayList();
		ContactDTO contactDTO = null;
		TotalDTO totalDTO = new TotalDTO();

		String url = "dc=cqtd,dc=vnpt,dc=vn";

		try {
			ctx = new InitialLdapContext(env1, null);

			SearchControls controls = new SearchControls();

			ctx.setRequestControls(new Control[] { new PagedResultsControl(
					pageSize, Control.CRITICAL) });
//			26241 = 33s
			String[] tenKhoi = { 
					"Khối Cơ quan Tập đoàn",
					"Khối hạch toán phụ thuộc",
					"Các Công ty Cổ phần",
					"VNPT Tỉnh - Thành phố",
					"Khối Hành chính Sự nghiệp",
//					"Khối chuyên trách Công đoàn Tập đoàn",
					"Các Công ty dọc",
					"Ngoài Tập đoàn",
					"Portal",
					"Quản trị và Hỗ trợ",
					"Test",
					"Guest - VNPT"//,"AcounttobeDeleted"
				};
			String khoiCQ = "";
			System.out.println("START TIME: " + SyncUtility.getCurrentDateTime());
			for (int z = 0; z < tenKhoi.length; z++) {
				if(z == 0){
					int i = db.delOldDataBeforeSync(false);
					if(i==-1)
						System.out.println("DELETE TEMP DATA SUCCESSFULLY");
					else
						System.out.println("DELETE TEMP DATA UNSUCCESSFULLY");
				}
				khoiCQ = tenKhoi[z];				
//				System.out.println("Start with: " + khoiCQ);
				String temp = "OU=" + khoiCQ + ",";
				boolean lg = false;
				String url2 = temp + url;
				String tenBan = "";
				listTotal = new LinkedList();
				results = ctx.search(url2, "(objectclass=user)", controls);

				listUrl = new LinkedList();
				listUrl.add(url2);
				if (results.hasMore()) {
					try {
						totalDTO = this.getUser(listUrl, ctx, results,
								controls, khoiCQ, khoiCQ, url2, cookie, total,
								pageSize);
						listTotal.add(totalDTO);
					} catch (Exception e) {
						System.out.println("Exception tai day: "
								+ (listUrl.get(0)).toString());
						e.printStackTrace();
					}
				}
				results = ctx.search(url2, "(objectclass=organizationalUnit)",
						controls);
				while (results.hasMore()) {
					listUrl = new LinkedList();

					try {
						SearchResult searchResult = (SearchResult) results
								.next();
						Attributes attributes = searchResult.getAttributes();

						Attribute attr10 = attributes.get("name");
						Attribute attr5 = attributes.get("distinguishedName");
						String getDN = (String) attr5.get();

						if (!((String) attr10.get()).equals("Computer")) {
							tenBan = (String) attr10.get();
//							System.out.println("quet den ban:"+tenBan);
							lg = checkOU(getDN, ctx, env1, controls, results);
							//listUrl.add(getDN);
							if (lg == true) {
								// khi co ban thi vao
								listUrl = getSubOU(listUrl, getDN, ctx, env1,
										controls, results);
//							} else {
//								System.out.println("OU la', ko co OU con:"+getDN);
							}
							listUrl.add(getDN);
						} else {
							// System.out.println("********** DINH MOT CHU
							// COMPUTER NHE");
						}
						totalDTO = getUser(listUrl, ctx, results, controls,
								khoiCQ, tenBan, getDN, cookie, total, pageSize);

					} catch (Exception e) {

						e.printStackTrace();
					}
					listTotal.add(totalDTO);
				}
				List listShow = new ArrayList();
				int inserBan = 0;
				int tranferUserBan = 0;
				int user = 0;
				if (listTotal != null) {
					for (int i = 0; i < listTotal.size(); i++) {
						totalDTO = (TotalDTO) listTotal.get(i);
						if (totalDTO != null) {
							listShow = (ArrayList) totalDTO.getDanhSachBan();
							int userBan = 0;
							db.callProc();
							if (listShow != null) {
								for (int j = 0; j < listShow.size(); j++) {
									user += 1;
									tongacc +=1;
									userBan += 1;
									contactDTO = (ContactDTO) listShow.get(j);
									tranferUserBan = db
											.tranferUSER_ADToDirtelDB(
													contactDTO.getFullName(),
													contactDTO.getEmail(),
													contactDTO.getMobile(),
													contactDTO.getAccount(),
													contactDTO.getDN(),
													contactDTO.getObjGUID(),
													contactDTO.getObjSID(),
													false);
								}
							}
							userBan = 0;
						}
					}
					
//					System.out.println("DUOC TAT CA : " + (listTotal.size()-1)
//							+ " Donvi VA : " + user + " USERS");
					
				} else {
					System.out.println("DUOC TAT CA : 0 Donvi");
					System.out.println("DUOC TAT CA : 0 USERS");
				}
				if(z == tenKhoi.length-1){					
					int k = db.check_user_exist_AD(false);
					int i = db.syncUser_to_db(true);
					if(i==-1)
						System.out.println("SYNC USER OTP SUCCESSFULLY");
					else
						System.out.println("SYNC USER OTP UNSUCCESSFULLY");
				}
			}
			
		} catch (SizeLimitExceededException se) {
			System.out.println("###### SizeLimitExceededException !!!!!!!!! ");
			se.printStackTrace();
		} catch (NamingException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			db.closeConnection();
			if (results != null) {
				try {
					results.close();
				} catch (Exception e) {
				}
			}
			if (ctx != null) {
				try {
					ctx.close();
				} catch (Exception e) {
				}
			}
		}
		System.out.println("TONG SO USER SYNC: "+tongacc);
		System.out.println("FINISH TIME: " + SyncUtility.getCurrentDateTime());
	}

	public TotalDTO getUser(List listUrl, LdapContext ctx,
			NamingEnumeration results, SearchControls controls,
			String khoiDonVi, String tenBan, String urlBan, byte[] cookie,
			int total, int pageSize) {
		ContactDTO contactDTO = null;
		TotalDTO totalDTO = null;
		List listCV = new ArrayList();

		try {
			// System.out.println("%%%%%%%%%%% get xong OU con va bat dau get du
			// lieu "+listUrl.size());
			if (listUrl != null && listUrl.size() != 0) {
				for (int dem = 0; dem < listUrl.size(); dem++) {					
					String urlCon = String.valueOf(listUrl.get(dem));
					try {

						listCV = processGetUser(controls, urlCon, results,
								listCV, ctx, cookie, total, pageSize);

					} catch (SizeLimitExceededException se) {
						System.out
								.println("###### SizeLimitExceededException !!!!!!!!! ");
						se.printStackTrace();
					} catch (Exception ne) {
						throw new RuntimeException(ne);
					}

				}
			}

			// System.out.println("%%%%%%%%%% So user cua " + tenBan + " la: "+
			// listCV.size());
			totalDTO = new TotalDTO(khoiDonVi, tenBan, listCV);

		} catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println("!!!!!!!!!!!!! Da get xong user");
		return totalDTO;
	}

	public List processGetUser(SearchControls controls, String urlCon,
			NamingEnumeration results, List listCV, LdapContext ctx,
			byte[] cookie, int total, int pageSize) throws Exception {
		ContactDTO contactDTO = null;

//		String department = "";
//		String company = "";
		String account = "";
		String fullTen = "";
//		String ngaySinh = "";
		String mobile = "";
		String mobile1 = "";
		String email = "";
//		String chucvu = "";
//		String offPhone = "";
		String dN = "";
		String objGUID = "";
		String objSID = "";
		Attribute attr0 = null;
		Attribute attr1 = null;
//		Attribute attr2 = null;
		Attribute attr3 = null;
		Attribute attr4 = null;
		Attribute attr5 = null;
//		Attribute attr6 = null;
//		Attribute attr7 = null;
//		Attribute attr8 = null;
		Attribute attr9 = null;
		Attribute attr10 = null;
		Attribute attr11 = null;
		try {
			do {
				results = ctx.search(urlCon, "(objectclass=user)", controls);
			    
				// Iterate over a batch of search results
				while (results != null && results.hasMore()) {
					// Display an entry
					SearchResult searchResult1 = (SearchResult) results.next();
					Attributes attributes1 = searchResult1.getAttributes();

					attr0 = attributes1.get("cn");
//					System.err.println((String)attr0.get());
//					attr1 = attributes1.get("extensionAttribute1");
//					attr2 = attributes1.get("department");
					attr3 = attributes1.get("displayName");
					attr4 = attributes1.get("mail");
					attr5 = attributes1.get("mobile");
//					attr6 = attributes1.get("title");
//					attr7 = attributes1.get("telephoneNumber");
//					attr8 = attributes1.get("company");
					attr9 = attributes1.get("distinguishedName");
					attr10 = attributes1.get("objectGUID");
					attr11 = attributes1.get("objectSid");
					attr1 = attributes1.get("extensionAttribute3");

					contactDTO = new ContactDTO();

					if (attr0 == null || attr0.equals("")) {
						account = "";
					} else {
						account = (String) attr0.get();
					}
//					if (attr1 == null || attr1.equals("null")
//							|| attr1.equals("")) {
//						ngaySinh = null;
//					} else {
//						ngaySinh = (String) attr1.get();
//						Date date = SyncUtility.stringToDate(ngaySinh);
//						ngaySinh = SyncUtility.dateToString(date, "yyyy-MM-dd");
//					}
					if (attr1 == null || attr1.equals("null")
							|| attr1.equals("")) {
						mobile1 = null;
					} else {
						mobile1 = (String) attr1.get();
					}
					if (attr3 == null || attr3.equals("")) {
						fullTen = "";
					} else {
						fullTen = (String) attr3.get();
					}
//					if (attr2 == null || attr2.equals("null")
//							|| attr2.equals("")) {
//						department = "";
//					} else {
//						department = (String) attr2.get();
//					}
//					if (attr8 == null || attr8.equals("null")
//							|| attr8.equals("")) {
//						company = "";
//					} else {
//						company = (String) attr8.get();
//					}
					if (attr4 == null || attr4.equals("")) {
						email = "";
					} else {
						email = (String) attr4.get();
					}

//					if (attr6 == null || attr6.equals("null")
//							|| attr6.equals("")) {
//						chucvu = "";// .concat(" - ").concat(department);
//						if (!department.equals("")) {
//							chucvu = chucvu.concat(department);
//						}
//						if (!company.equals("")) {
//							chucvu = chucvu.concat(company);
//						}
//					} else {
//						chucvu = ((String) attr6.get());
//						if (!department.equals("")) {
//							chucvu = chucvu.concat(" - ").concat(department);
//						}
//						if (!company.equals("")) {
//							chucvu = chucvu.concat(" - ").concat(company);
//						}
//
//					}
					if (attr5 == null || attr5.equals("null")
							|| attr5.equals("")) {
						mobile = "";
					} else {
						mobile = (String) attr5.get();
						if(mobile1 != null && mobile1.length()!= 0 && !mobile1.equals("null")){
							mobile=mobile.concat(";").concat(mobile1);
						}
					}
//					if (attr7 == null || attr7.equals("null")
//							|| attr7.equals("")) {
//						offPhone = "";
//					} else {
//						offPhone = (String) attr7.get();
//					}
					if (attr9 == null || attr9.equals("")) {
						dN = "n/a";
					} else {
						dN = (String) attr9.get();
//						System.err.println(dN);
					}
					if (attr10 == null || attr10.equals("")) {
						objGUID = "cdit_"+SyncUtility.genRandomNumber();
					} else {
						objGUID = SyncUtility.convertGuidToString((byte[])attr10.get());
						//System.err.println("cdit_"+SyncUtility.genRandomNumber());
					}
					if (attr11 == null || attr11.equals("")) {
						objSID = "cdit_"+SyncUtility.genRandomNumber();
					} else {
						//objSID = SyncUtility.convertSIDToString((byte[])attr11.get());
						objSID = "cdit_"+SyncUtility.genRandomNumber();
//						System.err.println(objSID);
					}
//					System.err.println("cdit_"+SyncUtility.genRandomNumber());
//					System.err.println("cdit_"+SyncUtility.genRandomNumber());
					contactDTO.setAccount(account);
//					contactDTO.setNgaySinh(ngaySinh);
					contactDTO.setMobile(mobile);
					contactDTO.setFullName(fullTen);
					contactDTO.setEmail(email);
//					contactDTO.setChucvu(chucvu);
//					contactDTO.setOffPhone(offPhone);
					contactDTO.setDN(dN);
					contactDTO.setObjGUID(objGUID);
					contactDTO.setObjSID(objSID);

					listCV.add(contactDTO);

					// Handle the entry's response controls (if any)
//					if (searchResult1 instanceof HasControls) {
//						// ((HasControls)entry).getControls();
//					}
				}
				// Examine the paged results control response
				Control[] control = ctx.getResponseControls();
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
				ctx.setRequestControls(new Control[] { new PagedResultsControl(
						pageSize, cookie, Control.CRITICAL) });
			} while (cookie != null);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listCV;
	}

	public static boolean checkOU(String url, DirContext ctx, Hashtable env1,
			SearchControls controls, NamingEnumeration results) {
		boolean b = false;
		try {
			// System.out.println("CHECK OU_------------"+url);
			results = ctx.search(url, "(objectclass=organizationalUnit)",
					controls);

			while (results.hasMore()) {
				SearchResult searchResult = (SearchResult) results.next();
				Attributes attributes = searchResult.getAttributes();

				Attribute attr5 = attributes.get("distinguishedName");
				String getSubDN = (String) attr5.get();

				Attribute attr11 = attributes.get("name");
				String OC = (String) attr11.get();
				// System.out.println("!!!!!!!!!! ou To :"+getSubDN);
				if (!OC.equals("Computer")) {
					b = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return b;
		}
		return b;
	}

	public List getSubOU(List listUrl, String url, DirContext ctx,
			Hashtable env1, SearchControls controls, NamingEnumeration results) {

		boolean sublg = false;
		int r = 0;
		try {
			// System.out.println("$$$$$$$$$$ getSubUsers");
			ctx = new InitialDirContext(env1);
			results = ctx.search(url, "(objectclass=organizationalUnit)",
					controls);

			while (results.hasMore()) {

				SearchResult searchResult = (SearchResult) results.next();
				Attributes attributes = searchResult.getAttributes();

				Attribute attrDN = attributes.get("distinguishedName");
				String getDN1 = (String) attrDN.get();
				Attribute attr11 = attributes.get("name");
				if (!((String) attr11.get()).equals("Computer")) {
//					System.out.println("subOU1: "+getDN1);
					listUrl.add(getDN1);
					sublg = checkOU(getDN1, ctx, env1, controls, results);
					if (sublg == true) {
						// System.out.println("sublg==true");
						listUrl = getSubOU(listUrl, getDN1, ctx, env1,
								controls, results);
					} else {
//						System.out.println("subOU: "+getDN1);
//						listUrl.add(getDN1);
						// System.out.println("DN con cua ban : "+getDN1);
					}
				}
			}

		} catch (NamingException e) {
			throw new RuntimeException(e);
		}

		return listUrl;
	}
}
