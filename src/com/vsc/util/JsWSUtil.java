package com.vsc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.poi.util.IOUtils;
import org.json.JSONObject;

public class JsWSUtil {
	static HttpHost targetHost;
	static DefaultHttpClient httpclient;
	static HttpURLConnection conn;
	static URL url;

	 
	private static void initConnection() {
		// targetHost = new HttpHost("123.31.40.162", 10080, "http");
//		targetHost = new HttpHost("14.225.6.70", 80, "http");
		targetHost = new HttpHost("123.31.25.15", 9080, "http");
		httpclient = new DefaultHttpClient();
//		httpclient.getCredentialsProvider().setCredentials(
//				new AuthScope(targetHost.getHostName(), targetHost.getPort()),
//				new UsernamePasswordCredentials("trungnv@vnpt.vn", "123456a@"));
	}
	private static void initConnection(Properties properties) {
		// targetHost = new HttpHost("123.31.40.162", 10080, "http");
		targetHost = new HttpHost(properties.getProperty("host"), Integer.parseInt(properties.getProperty("post")), properties.getProperty("http"));
		httpclient = new DefaultHttpClient();
		httpclient.getCredentialsProvider().setCredentials(
			new AuthScope(targetHost.getHostName(), targetHost.getPort()),
			new UsernamePasswordCredentials(properties.getProperty("username"), properties.getProperty("password")));
	}
	//
	public static JSONObject doSendJSWS(List<NameValuePair> params, Properties properties) throws Exception {
		initConnection(properties);
		AuthCache authCache = new BasicAuthCache();
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(targetHost, basicAuth);
		BasicHttpContext ctx = new BasicHttpContext();
		ctx.setAttribute(ClientContext.AUTH_CACHE, authCache);
		HttpPost post = new HttpPost(properties.getProperty("jsurl"));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
		post.setEntity(entity);
		HttpResponse resp = httpclient.execute(targetHost, post, ctx);
//		System.out.println(resp.getStatusLine());
		String json = new String(IOUtils.toByteArray(resp.getEntity().getContent()), "UTF-8");
		JSONObject object=new JSONObject(json);
		httpclient.getConnectionManager().shutdown();
		return object;
	}
	public static JSONObject doSendJSWS(List<NameValuePair> params, String jsurl) throws Exception {
		initConnection();
		AuthCache authCache = new BasicAuthCache();
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(targetHost, basicAuth);
		BasicHttpContext ctx = new BasicHttpContext();
		ctx.setAttribute(ClientContext.AUTH_CACHE, authCache);
		HttpPost post = new HttpPost(jsurl);
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
		post.setEntity(entity);
		HttpResponse resp = httpclient.execute(targetHost, post, ctx);
		System.out.println(resp.getStatusLine());
		String json = new String(IOUtils.toByteArray(resp.getEntity().getContent()), "UTF-8");
//		resp.getEntity().writeTo(System.out);
		System.out.println(json);
		JSONObject object=new JSONObject(json);
		httpclient.getConnectionManager().shutdown();
		return object;
	}
	public static JSONObject doSendJSWS(String _url, JSONObject content) {
		JSONObject jsonObject=null;
		  try {
			String output;
			url = new URL(_url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Content-Length", "" + content.length());
			conn.setRequestProperty("tendangnhap",content.getString("tendangnhap"));
			conn.setRequestProperty("matkhau", content.getString("matkhau"));
			conn.setRequestProperty("ma_dk_online", content.getString("ma_dk_online"));
			OutputStream os = conn.getOutputStream();
	//		os.write(content.getBytes());
	//		os.flush();
	
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
			}
	
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
	//			System.out.println(output);
			}
			jsonObject = new JSONObject(output);
			conn.disconnect();
		  } catch (MalformedURLException e) {
	
			e.printStackTrace();
	
		  } catch (IOException e) {
	
			e.printStackTrace();
	
		  }
		  return jsonObject;
	}
	
}
