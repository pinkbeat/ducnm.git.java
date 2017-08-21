package com.cdit.sync.ad;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class SyncUserAD {

	// public void ldapClient() {
	public List listUrl = null;
	public TransferToDB db = null;

	public SyncUserAD() {
		db = new TransferToDB();
	}

	public static void main(String[] args) throws ClassNotFoundException {
		(new SyncUserAD()).connectToSyncData();
	}

	public void connectToSyncData() {
		Hashtable env1 = new Hashtable();

		env1.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env1.put(Context.PROVIDER_URL, "ldap://10.1.3.3:389/");
		env1.put(Context.SECURITY_AUTHENTICATION, "simple");
		env1.put(Context.SECURITY_PRINCIPAL, "fimadmin");
		env1.put(Context.SECURITY_CREDENTIALS, "3k$L46t");

		DirContext ctx = null;
		NamingEnumeration results = null;
		List listTotal = null; // new ArrayList();
		ContactDTO contactDTO = null;
		TotalDTO totalDTO = new TotalDTO();

		String url = "dc=cqtd,dc=vnpt,dc=vn";

		try {
			ctx = new InitialDirContext(env1);
			SearchControls controls = new SearchControls();
			controls.setSearchScope(1);// do sau cua muc tim kiem

			String[] tenKhoi = { "Khối Cơ quan Tập đoàn",
					"Khối hạch toán phụ thuộc", "Các Công ty Cổ phần",
					"VNPT Tỉnh - Thành phố", "Khối Hành chính Sự nghiệp",
					"Khối chuyên trách Công đoàn Tập đoàn", "Các Công ty dọc",
					"Ngoài Tập đoàn", "Portal", "Quản trị và Hỗ trợ", "Test",
					"Guest - VNPT" };
			String khoiCQ = "";
			for (int z = 0; z < tenKhoi.length; z++) {
				khoiCQ = tenKhoi[z];
				System.out.println("start voi khoi:" + khoiCQ);
				String temp = "OU=" + khoiCQ + ",";
				boolean lg = false;
				String url2 = temp + url;
				String tenBan = "";
				// System.out.println("start voi url:"+url2);
				listTotal = new LinkedList();

				results = ctx.search(url2, "(objectclass=user)", controls);
				listUrl = new LinkedList();
				listUrl.add(url2);
				if (results.hasMore()) {
					try {
						totalDTO = getUser(listUrl, ctx, results, controls,
								khoiCQ, khoiCQ, url2);
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
							lg = checkOU(getDN, ctx, env1, controls, results);

							if (lg == true) {
								// khi co ban thi vao
								int k = getSubOU(listUrl, getDN, ctx, env1,
										controls, results);

							} else {
								// System.out.println("parentOU:"+getDN);

							}

							listUrl.add(getDN);

						} else {
							// System.out.println("********** DINH MOT CHU COMPUTER NHE");
						}

						totalDTO = getUser(listUrl, ctx, results, controls,
								khoiCQ, tenBan, getDN);

					} catch (Exception e) {

						e.printStackTrace();
					}
					listTotal.add(totalDTO);
				}
				List listShow = new ArrayList();
				int inserBan = 0;
				int tranferUserBan = 0;
				int user = 1;
				if (listTotal != null) {
					for (int i = 0; i < listTotal.size(); i++) {

						totalDTO = (TotalDTO) listTotal.get(i);
						if (totalDTO != null) {
							listShow = (ArrayList) totalDTO.getDanhSachBan();
							//
							// inserBan = db.tranferBAN_ADToDirtelDB(
							// totalDTO.getTenKhoi(), totalDTO.getTenBan());

							// System.out.println("KKKKKKKKKKKKKKKKKKKKKKKKKK ["
							// + i + "]: " + totalDTO.getTenKhoi());
							// System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBB ["+i+"]: "+totalDTO.getTenBan());
							int userBan = 0;
							db.callProc();
							if (listShow != null) {
								for (int j = 0; j < listShow.size(); j++) {
									user += 1;
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
							System.out.println("Ban : " + totalDTO.getTenBan()
									+ " duoc : " + userBan + " users");
							userBan = 0;
						}
					}
					db.closeConnection();
					System.out.println("DUOC TAT CA : " + listTotal.size()
							+ " OU");
					System.out.println("DUOC TAT CA : " + user + " USERS");
				} else {
					System.out.println("DUOC TAT CA : 0 OU");
					System.out.println("DUOC TAT CA : 0 USERS");
				}
			}
		} catch (NamingException e) {
			// closeConnection();
			throw new RuntimeException(e);
		} catch (Exception e) {
			e.printStackTrace();
			// closeConnection();
		} finally {
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
	}

	public int getSubOU(List listUrl, String url, DirContext ctx,
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

					sublg = checkOU(getDN1, ctx, env1, controls, results);
					if (sublg == true) {
						// System.out.println("sublg==true");
						r = (int) getSubOU(listUrl, getDN1, ctx, env1,
								controls, results);
					} else {
						// System.out.println("subOU:"+getDN);
						listUrl.add(getDN1);
						// System.out.println("DN con cua ban : "+getDN1);
					}
				}

			}

		} catch (NamingException e) {
			throw new RuntimeException(e);
		}

		return r;
	}

	public boolean checkOU(String url, DirContext ctx, Hashtable env1,
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

	public static TotalDTO getUser(List listUrl, DirContext ctx,
			NamingEnumeration results, SearchControls controls,
			String khoiDonVi, String tenBan, String urlBan) {
		ContactDTO contactDTO = null;
		TotalDTO totalDTO = null;
		List listCV = new ArrayList();

		try {
			// System.out.println("%%%%%%%%%%% get xong OU con va bat dau get du lieu "+listUrl.size());
			if (listUrl != null && listUrl.size() != 0) {
				for (int dem = 0; dem < listUrl.size(); dem++) {
					try {
						String urlCon = String.valueOf(listUrl.get(dem));
						// System.out.println("%%%%%%%%%%% urlCon:"+urlCon);
						results = ctx.search(urlCon, "(objectclass=user)",
								controls);
						while (results.hasMore()) {
							SearchResult searchResult1 = (SearchResult) results
									.next();
							Attributes attributes1 = searchResult1
									.getAttributes();

							Attribute attr0 = attributes1.get("cn");
							Attribute attr1 = attributes1
									.get("extensionAttribute1");
							Attribute attr2 = attributes1.get("department");
							Attribute attr3 = attributes1.get("displayName");
							Attribute attr4 = attributes1.get("mail");
							Attribute attr5 = attributes1.get("mobile");
							Attribute attr6 = attributes1.get("title");
							Attribute attr7 = attributes1
									.get("telephoneNumber");
							Attribute attr8 = attributes1.get("company");
							Attribute attr9 = attributes1
									.get("distinguishedName");
							// System.out.println("ten hien thi-"+attr3+";email-"+attr4);
							String department = "";
							String company = "";
							String account = "";
							String fullTen = "";
							String ngaySinh = "";
							String mobile = "";
							String email = "";
							String chucvu = "";
							String offPhone = "";
							String dN = "";
							// String tenHienThi = null;
							contactDTO = new ContactDTO();
							try {
								if (attr0 == null || attr0.equals("null")
										|| attr0.equals("")) {
									account = "";
								} else {
									account = (String) attr0.get();
								}
								if (attr1 == null || attr1.equals("null")
										|| attr1.equals("")) {
									ngaySinh = null;
								} else {
									ngaySinh = (String) attr1.get();
									Date date = SyncUtility
											.stringToDate(ngaySinh);
									ngaySinh = SyncUtility.dateToString(date,
											"MM/dd/yyyy");

								}

								if (attr3 == null || attr3.equals("null")
										|| attr3.equals("")) {
									fullTen = "";
								} else {
									fullTen = (String) attr3.get();
								}
								if (attr2 == null || attr2.equals("null")
										|| attr2.equals("")) {
									department = "";
								} else {
									department = (String) attr2.get();
								}
								if (attr8 == null || attr8.equals("null")
										|| attr8.equals("")) {
									company = "";
								} else {
									company = (String) attr8.get();
								}
								if (attr4 == null || attr4.equals("null")
										|| attr4.equals("")) {
									email = "";
								} else {
									email = (String) attr4.get();
								}

								if (attr6 == null || attr6.equals("null")
										|| attr6.equals("")) {
									chucvu = "";// .concat(" - ").concat(department);
									if (!department.equals("")) {
										chucvu = chucvu.concat(department);
									}
									if (!company.equals("")) {
										chucvu = chucvu.concat(company);
									}
								} else {
									chucvu = ((String) attr6.get());
									if (!department.equals("")) {
										chucvu = chucvu.concat(" - ").concat(
												department);
									}
									if (!company.equals("")) {
										chucvu = chucvu.concat(" - ").concat(
												company);
									}
									// if(attr2 != null && !attr2.equals("null")
									// && !attr2.equals("")){
									// chucvu = chucvu.concat((String)
									// attr2.get());//.concat(" - ").concat((String)
									// attr8.get());
									// }
									// if(attr8 != null && !attr8.equals("null")
									// && !attr8.equals("")){
									// chucvu =
									// chucvu.concat(" - ").concat((String)
									// attr8.get());
									// }
								}
								if (attr5 == null || attr5.equals("null")
										|| attr5.equals("")) {
									mobile = "";
								} else {
									mobile = (String) attr5.get();
								}
								if (attr7 == null || attr7.equals("null")
										|| attr7.equals("")) {
									offPhone = "";
								} else {
									offPhone = (String) attr7.get();
								}
								// if(email.equals("luongnt@vnpt.vn")){
								// System.out.println("++++++++ chuc vu cá»§a "+fullTen+" la :"+chucvu);
								// }
								if (attr9 == null || attr9.equals("")) {
									dN = "n/a";
								} else {
									dN = (String) attr9.get();
								}
								contactDTO.setAccount(account);
								contactDTO.setNgaySinh(ngaySinh);
								contactDTO.setMobile(mobile);
								contactDTO.setFullName(fullTen);
								contactDTO.setEmail(email);
								contactDTO.setChucvu(chucvu);
								contactDTO.setOffPhone(offPhone);
								contactDTO.setDN(dN);
								// contactDTO.setTenHienThi(tenHienThi);

							} catch (Exception e) {
								e.printStackTrace();
								System.err.println(e.getMessage());
							}
							// System.out.println("!!!!!!!!!!!!!! fullll:"+contactDTO);
							listCV.add(contactDTO);

						}
					} catch (Exception ne) {
						throw new RuntimeException(ne);
					}
				}
			}

			// System.out.println("so user la: "+listCV.size());
			totalDTO = new TotalDTO(khoiDonVi, tenBan, listCV);

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("!!!!!!!!!!!!! Da get xong user in OU");
		return totalDTO;
	}

	public void getDirectUserBan() {

		List listTotal = null; // new ArrayList();
		ContactDTO contactDTO = null;
		TotalDTO totalDTO = null;

		try {

			totalDTO = new TotalDTO();
			listTotal = new LinkedList();

			totalDTO = getDirectUser("OU=VNPT TPHCM,OU=VNPT Tỉnh - Thành phố",
					"VNPT TPHCM");
			listTotal.add(totalDTO);

			List listShow = new ArrayList();
			int inserBan = 0;
			int tranferUserBan = 0;
			int user = 1;
			if (listTotal != null) {
				for (int i = 0; i < listTotal.size(); i++) {

					totalDTO = (TotalDTO) listTotal.get(i);
					listShow = (ArrayList) totalDTO.getDanhSachBan();

					inserBan = db.tranferBAN_ADToDirtelDB(
							totalDTO.getTenKhoi(), totalDTO.getTenBan());

					System.out.println("KKKKKKKKKKKKKKKKKKKKKKKKKK [" + i
							+ "]: " + totalDTO.getTenKhoi());
					// System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBB ["+i+"]: "+totalDTO.getTenBan());
					int userBan = 0;
					int kq = 0;
					for (int j = 0; j < listShow.size(); j++) {
						user += 1;
						userBan += 1;
						contactDTO = (ContactDTO) listShow.get(j);
						kq = tranferUserBan = db.tranferUSER_ADToDirtelDB(
								contactDTO.getFullName(),
								contactDTO.getEmail(), contactDTO.getMobile(),
								contactDTO.getOffPhone(), totalDTO.getTenBan(),
								contactDTO.getChucvu(),
								contactDTO.getNgaySinh(),
								contactDTO.getAccount());
						System.out.println(kq);

					}
					System.out.println("Ban : " + totalDTO.getTenBan()
							+ " duoc : " + userBan + " users");
					userBan = 0;
				}
				// closeConnection();
				System.out.println("DUOC TAT CA : " + listTotal.size() + " OU");
				System.out.println("DUOC TAT CA : " + user + " USERS");
			} else {
				System.out.println("DUOC TAT CA : 0 OU");
				System.out.println("DUOC TAT CA : 0 USERS");
			}

		} catch (Exception e) {
			e.printStackTrace();
			// closeConnection();
		}

	}

	public static TotalDTO getDirectUser(String donVi, String tenDonVi) {
		ContactDTO contactDTO = null;
		TotalDTO totalDTO = null;
		List listCV = new ArrayList();
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://10.1.3.3:389/");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "fimadmin");
		env.put(Context.SECURITY_CREDENTIALS, "inv1sibl3");

		DirContext ctx;
		try {

			ctx = new InitialDirContext(env);
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}

		NamingEnumeration results = null;
		try {
			SearchControls controls = new SearchControls();
			controls.setSearchScope(1);

			results = ctx.search(donVi + ",DC=cqtd,DC=vnpt,DC=vn",
					"(objectclass=user)", controls);

			while (results.hasMore()) {
				SearchResult searchResult1 = (SearchResult) results.next();
				Attributes attributes1 = searchResult1.getAttributes();

				Attribute attr0 = attributes1.get("cn");
				Attribute attr1 = attributes1.get("extensionAttribute1");
				Attribute attr2 = attributes1.get("department");
				Attribute attr3 = attributes1.get("displayName");
				Attribute attr4 = attributes1.get("mail");
				Attribute attr5 = attributes1.get("mobile");
				Attribute attr6 = attributes1.get("title");
				Attribute attr7 = attributes1.get("telephoneNumber");
				Attribute attr8 = attributes1.get("company");
				// System.out.println("ten hien thi-"+attr3+";email-"+attr4);
				String department = "";
				String company = "";
				String account = "";
				String fullTen = "";
				String ngaySinh = "";
				String mobile = "";
				String email = "";
				String chucvu = "";
				String offPhone = "";
				// String tenHienThi = null;
				contactDTO = new ContactDTO();
				try {
					if (attr0 == null || attr0.equals("null")
							|| attr0.equals("")) {
						account = "";
					} else {
						account = (String) attr0.get();
					}
					if (attr1 == null || attr1.equals("null")
							|| attr1.equals("")) {
						ngaySinh = null;
					} else {
						ngaySinh = (String) attr1.get();
						Date date = SyncUtility.stringToDate(ngaySinh);
						ngaySinh = SyncUtility.dateToString(date, "MM/dd/yyyy");

					}

					if (attr3 == null || attr3.equals("null")
							|| attr3.equals("")) {
						fullTen = "";
					} else {
						fullTen = (String) attr3.get();
					}
					if (attr2 == null || attr2.equals("null")
							|| attr2.equals("")) {
						department = "";
					} else {
						department = (String) attr2.get();
					}
					if (attr8 == null || attr8.equals("null")
							|| attr8.equals("")) {
						company = "";
					} else {
						company = (String) attr8.get();
					}
					if (attr4 == null || attr4.equals("null")
							|| attr4.equals("")) {
						email = "";
					} else {
						email = (String) attr4.get();
					}

					if (attr6 == null || attr6.equals("null")
							|| attr6.equals("")) {
						chucvu = "";// .concat(" - ").concat(department);
						if (!department.equals("")) {
							chucvu = chucvu.concat(department);
						}
						if (!company.equals("")) {
							chucvu = chucvu.concat(company);
						}
					} else {
						chucvu = ((String) attr6.get());
						if (!department.equals("")) {
							chucvu = chucvu.concat(" - ").concat(department);
						}
						if (!company.equals("")) {
							chucvu = chucvu.concat(" - ").concat(company);
						}
						// if(attr2 != null && !attr2.equals("null") &&
						// !attr2.equals("")){
						// chucvu = chucvu.concat((String)
						// attr2.get());//.concat(" - ").concat((String)
						// attr8.get());
						// }
						// if(attr8 != null && !attr8.equals("null") &&
						// !attr8.equals("")){
						// chucvu = chucvu.concat(" - ").concat((String)
						// attr8.get());
						// }
					}
					if (attr5 == null || attr5.equals("null")
							|| attr5.equals("")) {
						mobile = "";
					} else {
						mobile = (String) attr5.get();
					}
					if (attr7 == null || attr7.equals("null")
							|| attr7.equals("")) {
						offPhone = "";
					} else {
						offPhone = (String) attr7.get();
					}
					// if(email.equals("luongnt@vnpt.vn")){
					// System.out.println("++++++++ chuc vu cá»§a "+fullTen+" la :"+chucvu);
					// }

					contactDTO.setAccount(account);
					contactDTO.setNgaySinh(ngaySinh);
					contactDTO.setMobile(mobile);
					contactDTO.setFullName(fullTen);
					contactDTO.setEmail(email);
					contactDTO.setChucvu(chucvu);
					contactDTO.setOffPhone(offPhone);

				} catch (Exception e) {
					e.printStackTrace();
					System.err.println(e.getMessage());
				}
				// System.out.println("!!!!!!!!!!!!!! fullll:"+contactDTO);
				listCV.add(contactDTO);

			}

			totalDTO = new TotalDTO(donVi, tenDonVi, listCV);
			// System.out.println("!!!!!!!!!!!!!! fullll:");
			// } catch (NameNotFoundException fe) {
			// fe.printStackTrace();
			// throw new RuntimeException(fe);
		} catch (NamingException ne) {
			ne.printStackTrace();
			throw new RuntimeException(ne);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (results != null) {
				try {
					results.close();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			if (ctx != null) {
				try {
					ctx.close();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		return totalDTO;
	}
}
