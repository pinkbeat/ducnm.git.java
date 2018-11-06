package com.vsc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpHost;
import org.json.JSONException;
import org.json.JSONObject;

public class DHIS2RestfulHelper {
	static HttpHost targetHost;
	static HttpURLConnection conn;
	static URL url;
//	static String basepath = "http://dhis2.yte.gov.vn/dhis227";
//	static String clientID = "vnpt";
//	static String clientSecret = "9e7d2169d-0d53-d762-75f1-316351cc207";
//	static String username = "camau";
//	static String password = "123456Aa@";
	static String basepath = "http://dhis2.yte.gov.vn/dhis2";
	static String clientID = "vnpt";
	static String clientSecret = "4909deeee-45df-5e85-6dff-2af35d53586";
	static String username = "vnpt";
	static String password = "123456Aa@";
	static String oAuth2Path="/uaa/oauth/token";
	static String dataValuePath="/api/26/dataValues?";
	
	public static JSONObject doOAuth2Authentication(String uri) {
		oAuth2Path=uri;
		return doOAuth2Authentication();
	}
	public static JSONObject doOAuth2Authentication() {
		JSONObject jsonObject=null;
		String token = null;
		try {
			url = new URL(basepath+oAuth2Path);
			conn = (HttpURLConnection) url.openConnection();			
			conn.setRequestMethod("POST");
			String encode = "Basic " + new String(CryptoUtil.BASE64_encode(clientID+":"+clientSecret));
			System.out.println(encode);
		    conn.setRequestProperty("Authorization", encode);
		    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		    conn.setRequestProperty("Accept", "application/json");
		    conn.setDoOutput(true);
			String urlEncoded = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
			           URLEncoder.encode("password", "UTF-8") +"="+ URLEncoder.encode(password, "UTF-8") + "&" +
			           URLEncoder.encode("grant_type", "UTF-8") + "=" + URLEncoder.encode("password", "UTF-8");
			System.out.println(urlEncoded);
			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
		    osw.write(urlEncoded);
		    osw.flush();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
			}
			jsonObject = new JSONObject(getResponseMsg(conn));
		} catch (MalformedURLException e) {	
			e.printStackTrace();	
		} catch (JSONException e) {	
			e.printStackTrace();	
		}catch (Exception e) {	
			e.printStackTrace();	
		}
			return jsonObject;
	}
//	public static JSONObject doBasicAuthentication(String _url,String userName, String password) {
//		JSONObject jsonObject=null;
//		  try {
//			url = new URL(_url);
//			conn = (HttpURLConnection) url.openConnection();			
//			conn.setRequestMethod("GET");
//			String encode = "Basic " + new String(CryptoUtil.BASE64_encode(userName+":"+password));
//			System.out.println(encode);
//		    conn.setRequestProperty("Authorization", encode);
//		    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//		    conn.setRequestProperty("Accept", "application/json");
//		    jsonObject = new JSONObject(getResponseMsg(conn));
//		  } catch (MalformedURLException e) {	
//				e.printStackTrace();	
//			} catch (Exception e) {	
//				e.printStackTrace();	
//			}
//			return jsonObject;
//	}
//	public static JSONObject getOUId(String _url, String token){
//		JSONObject jsonObject=null;
//		try {
//			url = new URL(_url);
//			conn = (HttpURLConnection) url.openConnection();			
//			conn.setRequestMethod("GET");
//			System.out.println(token);
//		    conn.setRequestProperty("Authorization", token);
//		    conn.setRequestProperty("Content-type", "application/json");
//		    conn.setDoOutput(true);
//		    jsonObject = new JSONObject(getResponseMsg(conn));
//		} catch (MalformedURLException e) {	
//			e.printStackTrace();	
//		} catch (Exception e) {	
//			e.printStackTrace();	
//		}
//		return jsonObject;
//	}
//	public static void doSendData(String _url, String token){
//		  try {
//			url = new URL(_url);
//			conn = (HttpURLConnection) url.openConnection();			
//			conn.setRequestMethod("POST");
//			System.out.println(token);
//		    conn.setRequestProperty("Authorization", token);
//		    conn.setRequestProperty("Content-type", "application/json");
//		    conn.setDoOutput(true);
//			System.out.println(getResponseMsg(conn));			
//		  }catch (Exception e) {	
//			e.printStackTrace();	
//		  }
//	}
	public static void doSend(String uri, String queryString, String token, String method){
		dataValuePath=uri;
		doSend(queryString, token, method);
	}
	public static void doSend(String queryString, String token, String method){
		  try {
			System.out.println(basepath+dataValuePath+queryString);
			url = new URL(basepath+dataValuePath+queryString);
			conn = (HttpURLConnection) url.openConnection();			
			conn.setRequestMethod(method.toUpperCase());
			System.out.println(token);
		    conn.setRequestProperty("Authorization", token);
		    conn.setRequestProperty("Content-type", "application/json");
		    conn.setDoOutput(true);
			System.out.println(getResponseMsg(conn));			
		  }catch (Exception e) {	
			e.printStackTrace();	
		  }
	}
	private static String getResponseMsg(HttpURLConnection conn){
		String rst=null;
		BufferedReader br = null;
		try {
			String output;			
			System.out.println("ResponceCode = "+conn.getResponseCode());
			br = new BufferedReader(new InputStreamReader((conn.getInputStream())));				
			StringBuffer messBody = new StringBuffer();
			while ((output = br.readLine()) != null) {
				messBody.append(output);
			}
			System.out.println(output);
			rst = messBody.toString();			
		  } catch (IOException e) {	
			e.printStackTrace();	
		  }finally{
			  conn.disconnect();
			  if(br!=null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }
		  return rst;
	   }
}

class Indicator{
	protected String bcx2 = "{\r\n \"rpcode\":\"27X02\",\r\n \"dhis2\":\"m9ErSxUe1n3\",\r\n \"inds\":[\"tg3tGzIThbA\",\r\n        \"U39J6OmtUVF\",\r\n        \"bikzAN6GhaG\",\r\n        \"gzUAQXSJ5fL\",\r\n        \"gnVBQoDg094\",\r\n        \"HPTnpkxE6v8\",\r\n        \"oDgpQNnX1c1\",\r\n        \"Asqu9CyZLcG\",\r\n        \"GJOyeFi2C86\"\r\n        ],\r\n \"inds_map\":[\r\n            {\"1.1.1\":\"tg3tGzIThbA\"},\r\n            {\"1.1.2\":\"U39J6OmtUVF\"},\r\n            {\"1.2\":\"bikzAN6GhaG\"},\r\n            {\"1.3\":\"gzUAQXSJ5fL\"},\r\n            {\"1.6\":\"gnVBQoDg094\"},\r\n            {\"2.2\":\"HPTnpkxE6v8\"},\r\n            {\"2.3\":\"oDgpQNnX1c1\"},\r\n            {\"2.4\":\"Asqu9CyZLcG\"},\r\n            {\"2.5\":\"GJOyeFi2C86\"}\r\n        ]\r\n}";
	protected String bcx5 = "{\r\n \"rpcode\":\"27X05\",\r\n \"dhis2\":\"m9ErSxUe1n3\",\r\n \"inds\":[\"i5yXKoAcSpb\",\r\n        \"QfSJtngOK7c\",\r\n        \"kiO7ZFe67bt\",\r\n        \"rrQkixe4Vqc\",\r\n        \"BxwLDew2XkX\",\r\n        \"KfJOeNWNzuB\",\r\n        \"SJ3MWLZS4C7\",\r\n        \"umHMm1zLFF9\",\r\n        \"fgL3hP3B3Nc\",\r\n        \"XprPxm6c373\"\r\n        ],\r\n \"inds_map\":[\r\n            {\"1\":\"i5yXKoAcSpb\"},\r\n            {\"2\":\"QfSJtngOK7c\"},\r\n            {\"2.1\":\"kiO7ZFe67bt\"},\r\n            {\"2.2\":\"rrQkixe4Vqc\"},\r\n            {\"2.3\":\"BxwLDew2XkX\"},\r\n            {\"2.4\":\"KfJOeNWNzuB\"},\r\n            {\"3\":\"SJ3MWLZS4C7\"},\r\n            {\"4\":\"umHMm1zLFF9\"},\r\n            {\"5\":\"fgL3hP3B3Nc\"},\r\n            {\"6\":\"XprPxm6c373\"}\r\n        ]\r\n}";
	
	public JSONObject getBcx2() {
		return new JSONObject(bcx2);
	}

	public void setBcx2(String bcx2) {
		this.bcx2 = bcx2;
	}

	public JSONObject getBcx5() {
		return new JSONObject(bcx5);
	}

	public void setBcx5(String bcx5) {
		this.bcx5 = bcx5;
	}	
}