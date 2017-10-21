package com.vsc.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ddf.EscherColorRef.SysIndexSource;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.vsc.dto.SOAPResult;

public class WsUtil {
	static String filename = "F:\\soap_result.xml";	
	static HttpURLConnection urlConn = null;
    static URL myURL = null;
    static PrintWriter writer;
	public static SOAPResult doSendWS(String url, String postMsg){		
	    SOAPResult result = new SOAPResult();
		try {
			myURL = new URL(url);
			urlConn = (HttpURLConnection) myURL.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			urlConn.setRequestProperty("Content-Length",Integer.toString(postMsg.length()));
			urlConn.setRequestProperty("SOAPAction", ""); // hportal required
			writer = new PrintWriter(urlConn.getOutputStream());
			writer.print(postMsg);
			writer.flush();
			urlConn.connect();
			if(urlConn.getResponseCode() == 200){
				System.out.println(urlConn.getResponseCode());				
							   
			}else{
				System.out.println("ResponseCode = "+urlConn.getResponseCode());
				result.setRESPONSE_CODE(urlConn.getResponseCode());
				InputStream resStream = urlConn.getErrorStream();
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			    byte[] byteBuf = new byte[10240];
			    int nRead = 0;
			    while ((nRead = resStream.read(byteBuf, 0, byteBuf.length)) != -1) {
	        	    buffer.write(byteBuf, 0, nRead);
	            }
				result.setSoapResult(buildXmlResult(buffer.toString()));
				result.setERROR_CODE(-2);				
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setERROR_CODE(-1);
		}catch(ConnectException e){
			e.printStackTrace();
			result.setERROR_CODE(-1);
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setERROR_CODE(-3);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setERROR_CODE(-4);
		}
		return result;
	}
	public static SOAPResult doSendWS(String sec_url, String wsurl, String postMsg){	
		SOAPResult result = new SOAPResult();
		try {
			String sesionId = null;
			if(sec_url!=null) sesionId = doLoginWS(sec_url);
			HttpURLConnection.setFollowRedirects(false);		

			myURL = new URL(wsurl);
			urlConn = (HttpURLConnection) myURL.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			urlConn.setRequestProperty("Content-Length",Integer.toString(postMsg.length()));
			urlConn.setRequestProperty("SOAPAction", ""); // hportal required
			if(sesionId!=null)	urlConn.setRequestProperty("Cookie", sesionId);
			writer = new PrintWriter(urlConn.getOutputStream());
			writer.print(postMsg);
			writer.flush();
			urlConn.connect();
			String rs = null;
			if(urlConn.getResponseCode() == 200){
				rs = FileUtil.readInputStream(urlConn.getInputStream());						   
			}else{
				System.out.println("ResponseCode = "+urlConn.getResponseCode());
				rs = FileUtil.readInputStream(urlConn.getErrorStream());					
				result.setERROR_CODE(-2);
				result.setRESPONSE_CODE(urlConn.getResponseCode());
			}
//			result.setSoapResult(buildXmlResult(rs));
			result.setSoapResult(XmlUtil.transformNode((XmlUtil.parse(rs)).getDocumentElement()));
			writeFile(result.getSoapResult(), filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setERROR_CODE(-1);
		}catch(ConnectException e){
			e.printStackTrace();
			result.setERROR_CODE(-1);
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setERROR_CODE(-3);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setERROR_CODE(-4);
		}
		return result;
	}
	private static String doLoginWS(String sec_url){
		    String cookie = "";
		    String criteria = "Set-Cookie";
		try {
			HttpURLConnection.setFollowRedirects(false);		

		    myURL = new URL(sec_url);
		    urlConn = (HttpURLConnection) myURL.openConnection();
		    urlConn.setDoOutput(true);
		    urlConn.setDoInput(true);
		    urlConn.setAllowUserInteraction(true);
		    urlConn.connect();
			
			Map headers = urlConn.getHeaderFields();
			Set keys = headers.keySet();
			Iterator keysit = keys.iterator();
			ListIterator keyvals;
			String key;
			String tmp;
			while(keysit.hasNext()) {			
			    key = (String)keysit.next();
			    keyvals = ((List)headers.get(key)).listIterator(); 			
				while(keyvals.hasNext()) {				
				    tmp = (String)keyvals.next();
				    if (key != null && key.equals(criteria)) {
				    	cookie = tmp.substring(0, tmp.indexOf(";"));
				    } 
				}
			}
			System.out.println(cookie);
		}catch(Exception e){
			e.printStackTrace();
		}
		return cookie;
	}
	private static void writeFile(String content, String filename){
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(new File(filename));
			outputStream.write(XmlUtil.transformNode((XmlUtil.parse(content)).getDocumentElement()).getBytes());
//		    outputStream.write(buildXmlResult(content).getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		   
	}
	public static void showSoapResult(String content){
		try {
			Document document = XmlUtil.parse(content);
			Element root = document.getDocumentElement();
			Element body = XmlUtil.getElementByPath(root, "soapenv:Body/soapenv:Fault");
			if(body != null) {
				System.out.println("faultcode = "+XmlUtil.getValueByPath(body, "faultcode"));
				System.out.println("faultstring = "+XmlUtil.getValueByPath(body, "faultstring"));
			}else {
				System.out.println(content);
			}
			
//		    outputStream.write(buildXmlResult(content).getBytes());
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		   
	}
	private static String buildXmlResult(String xmldata) throws Exception{
		if(xmldata == null) return xmldata;
		xmldata = xmldata.replaceAll("&lt;", "<");
		xmldata = xmldata.replaceAll("&gt;", ">");
	    return xmldata;
	}
	public static JSONObject doSendJSWS(String _url, JSONObject content) {
		JSONObject jsonObject=null;
		  try {
			String output;
			String tmp = "";
			String key;
			myURL = new URL(_url);
			urlConn = (HttpURLConnection) myURL.openConnection();
			urlConn.setDoOutput(true);
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("Content-Type", "application/json");
			urlConn.setRequestProperty("Content-Length", "" + content.length());
			Iterator<String> iterator = content.keys();
			while(iterator.hasNext()){
				key = iterator.next();
				urlConn.setRequestProperty(key, content.getString(key));
			}
//			OutputStream os = urlConn.getOutputStream();
	//		os.write(content.getBytes());
	//		os.flush();
	
			if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : "
					+ urlConn.getResponseCode());
			}
	
			BufferedReader br = new BufferedReader(new InputStreamReader((urlConn.getInputStream())));
			
			System.out.println("Output from Server ...");
			while ((output = br.readLine()) != null) {
				tmp+=output;
			}
			jsonObject = new JSONObject(tmp);
			urlConn.disconnect();
		  } catch (MalformedURLException e) {
	
			e.printStackTrace();
	
		  } catch (IOException e) {
	
			e.printStackTrace();
	
		  }
		  return jsonObject;
	}
	public static JSONObject doSendJSWS(String _url, String content) {
		JSONObject jsonObject=null;
		  try {
			String output;
			String tmp = "";
			String key;
			myURL = new URL(_url);
			urlConn = (HttpURLConnection) myURL.openConnection();
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("Content-Type", "application/json");
			urlConn.setRequestProperty("Content-Length", "" + content.length());
			
			OutputStream os = urlConn.getOutputStream();
			os.write(content.getBytes());
			os.flush();
	
			if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : "
					+ urlConn.getResponseCode());
			}
			
			System.out.println("Output from Server ...");
			tmp = IOUtils.toString(urlConn.getInputStream(), urlConn.getContentEncoding() == null ? "UTF-8"
                    : urlConn.getContentEncoding());
			
//			BufferedReader br = new BufferedReader(new InputStreamReader((urlConn.getInputStream())));
//			while ((output = br.readLine()) != null) {
//				tmp+=output;
//			}
			jsonObject = new JSONObject(tmp);
			urlConn.disconnect();
		  } catch (MalformedURLException e) {
	
			e.printStackTrace();
	
		  } catch (IOException e) {
	
			e.printStackTrace();
	
		  }
		  return jsonObject;
	}
}
