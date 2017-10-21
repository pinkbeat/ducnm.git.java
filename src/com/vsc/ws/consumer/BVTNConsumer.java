package com.vsc.ws.consumer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.json.JSONObject;
import org.w3c.dom.Document;
import com.vsc.dto.SOAPResult;
import com.vsc.util.BOMDetector;
import com.vsc.util.FileUtil;
import com.vsc.util.WsUtil;
import com.vsc.util.XmlUtil;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class BVTNConsumer {
//	static String ws_url = "http://bvtn.org.vn/hPortal/services/BVTNServices";
	static String ws_url = "http://localhost:8080/hPortal/services/BVTNServices";
//	static String ws_url = "http://congdulieuyte.vn/hPortal/services/WSPortal";
	static String sec_url=null;
	SOAPResult result=null; 
	public static void main(String[] asd){
//		System.out.println(Charset.defaultCharset());
		BVTNConsumer consumer = new BVTNConsumer();
		File file = new File("C:\\xmldata\\BVTN\\BYT\\checkout\\0a021636-69a3-4cb1-9fbf-05506f4c06bf_ws.xml");
//		consumer.sendWSGW_guiTTXV("ducnm","e197e5b54a61133f87056e6e7fad0d8a", FileUtil.readFile(file), "ABCXYZ.xml");
		consumer.sendWSguiHS("ducnm","e197e5b54a61133f87056e6e7fad0d8a", FileUtil.readFile(file), "M0002","0");
//		consumer.sendWSguiHSEncoded("ducnm","e197e5b54a61133f87056e6e7fad0d8a", consumer.getBase64FromFile(file), "M0002","0");
//		consumer.sendWSguiHS("ducnm","e197e5b54a61133f87056e6e7fad0d8a", consumer.getBase64FromFile("C:/xmldata/checkout/KCB_31291_201703171458_1700004701.xml"), "M0002","0");
	}
	private void sendWSguiHS(String user,String pass,String content,String msgType, String actionType){		
		String postData = 
		 "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws\">"+
		    "<soapenv:Header/>"+
		    	"<soapenv:Body>"+
		    	"<ws:guiHS soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"+
		          "<_usr xsi:type=\"soapenc:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">"+user+"</_usr>"+
		          "<_pwd xsi:type=\"soapenc:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">"+pass+"</_pwd>"+
		          "<xmlData xsi:type=\"soapenc:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\"><![CDATA["+content+"]]></xmlData>"+
		          "<msgType xsi:type=\"soapenc:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">"+msgType+"</msgType>"+
		          "<actionType xsi:type=\"soapenc:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">"+actionType+"</actionType>"+
		       "</ws:guiHS>"+
		    "</soapenv:Body>"+
		 "</soapenv:Envelope>";	
		
		result = WsUtil.doSendWS(sec_url, ws_url, postData);					
		System.out.println(result.getERROR_CODE());
//		System.out.println(result.getSoapResult());
		WsUtil.showSoapResult(result.getSoapResult());
	}
	private void sendWSguiHSEncoded(String user,String pass,String content,String msgType, String actionType){		
		String postData = 
		 "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws\">"+
		    "<soapenv:Header/>"+
		    	"<soapenv:Body>"+
		    	"<ws:guiHSMaHoa soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"+
		          "<_usr xsi:type=\"soapenc:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">"+user+"</_usr>"+
		          "<_pwd xsi:type=\"soapenc:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">"+pass+"</_pwd>"+
		          "<xmlData xsi:type=\"soapenc:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">"+content+"</xmlData>"+
		          "<msgType xsi:type=\"soapenc:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">"+msgType+"</msgType>"+
		          "<actionType xsi:type=\"soapenc:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">"+actionType+"</actionType>"+
		       "</ws:guiHSMaHoa>"+
		    "</soapenv:Body>"+
		 "</soapenv:Envelope>";	
		
		result = WsUtil.doSendWS(sec_url, ws_url, postData);					
		System.out.println(result.getERROR_CODE());
//		System.out.println(result.getSoapResult());
		WsUtil.showSoapResult(result.getSoapResult());
	}
	private void sendWSGW_guiTTXV(String user,String pass,String content,String filename){		
		String postData = 
		"<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws\">"+
		   "<soapenv:Header/>"+
		   "<soapenv:Body>"+
		      "<ws:guiHSXV soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"+
		         "<_usr xsi:type=\"soapenc:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">"+user+"</_usr>"+
		         "<_pwd xsi:type=\"soapenc:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">"+pass+"</_pwd>"+
		         "<xmlData xsi:type=\"soapenc:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\"><![CDATA["+content+"]]></xmlData>"+
		      "</ws:guiHSXV>"+
		   "</soapenv:Body>"+
		"</soapenv:Envelope>";			
		result = WsUtil.doSendWS(sec_url, ws_url, postData);					
		System.out.println(result.getERROR_CODE());
		System.out.println(result.getSoapResult());
//		WsUtil.showSoapResult(result.getSoapResult());
//        "<nameofFile xsi:type=\"soapenc:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">"+filename+"</nameofFile>"+

	}
	
	private String getBase64FromFile(File file) {
		// TODO Auto-generated method stub		
		BASE64Encoder encoder = new BASE64Encoder();
		String rs=null;
		try {
//			rs = encoder.encode(read(file));FileUtil.readFileBytes(file)
			rs = encoder.encode(FileUtil.readFile(file,"UTF-16LE").getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;		
	}
	private String extractDataFromSOAPMsg(String soapMsg, String xPath) {
		// TODO Auto-generated method stub		
		Document doc = XmlUtil.parse(soapMsg);		
		return XmlUtil.getValueByPath(doc.getDocumentElement(), xPath);		
	}
	private void saveFileFormBase64(String soapContent, String filename ){
		System.out.println(soapContent);
		FileOutputStream outputStream = null;
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] tmp;
		try {
			tmp = decoder.decodeBuffer(soapContent);
			File file = new File(filename);
			outputStream = new FileOutputStream(file);
			outputStream.write(tmp);
			outputStream.close();
			System.out.println("Saving file have done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}
	private byte[] read(File file) throws IOException {	    
	    ByteArrayOutputStream ous = null;
	    InputStream ios = null;
	    try {
	        byte[] buffer = new byte[4096];
	        ous = new ByteArrayOutputStream();
	        ios = new FileInputStream(file);
	        int read = 0;
	        while ((read = ios.read(buffer)) != -1) {
	            ous.write(buffer, 0, read);
	        }
	    }finally {
	        try {
	            if (ous != null)
	                ous.close();
	        } catch (IOException e) {
	        }

	        try {
	            if (ios != null)
	                ios.close();
	        } catch (IOException e) {
	        }
	    }
	    return ous.toByteArray();
	}
}
