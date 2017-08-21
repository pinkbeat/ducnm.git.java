package com.vsc.ws.consumer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;
import org.w3c.dom.Document;
import com.vsc.dto.SOAPResult;
import com.vsc.util.WsUtil;
import com.vsc.util.XmlUtil;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class VptwConsumer {
//	static String ws_url = "http://10.100.1.33/vptw0112.nsf/WSJS_ATTACHMENTS?OpenWebService";
//	static String sec_url = "http://10.100.1.33/names.nsf?login&username=dang minh chinh&password=123456";
	static String ws_url = "http://10.145.37.252/dev/dvptw.nsf/WSJS_ATTACHMENTS?OpenWebService";
	static String sec_url = "http://10.145.37.252/names.nsf?login&username=Administrator&password=123456a@";
	SOAPResult result=null; 
	public static void main(String[] asd){
		VptwConsumer consumer = new VptwConsumer();
//		JSONObject jsonObject = new JSONObject("{\"modname\":\"congvanttcntt\",\"status\":\"signed\"}");
//		System.out.println(jsonObject.toString());
//		System.out.println(jsonObject.get("status"));
//		consumer.sendWS(1, "ED2AA009EB7518E74725808A00349CF6", "mohinh.pdf", "congvanttcntt","getAttachByNoteIDReturn");
		consumer.sendWS(2, "7ED09AD963CDC779472580F8002EB719", "e:\\DocUtil.java", "{\"modname\":\"congvantw\",\"status\":\"signed\"}","uploadAttachByNoteIDReturn");
//		consumer.sendWS(1, "FA8EE35DD8E1D2DC472580830014470B", "Tailieu_Config_phanmem.doc", "congvantw","getAttachByNoteIDReturn");
//		consumer.sendWS(5, null, "Thành uỷ Đà Nẵng", "congvantw","getCertificateReturn");
//		consumer.sendWS(6, null, "Trung tam CNTT", "congvanttcntt","getCertificateReturn");
	}
	private void sendWS(int order,String docId,String filename,String sessionId,String tagReturnData){		
		String postData = null;
		if(order == 1){
			postData=   
				"<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wsp.vptw.org\">"+
				   "<soapenv:Header/>"+
				   "<soapenv:Body>"+
				      "<urn:getAttachByNoteID soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"+
				         "<docId xsi:type=\"xsd:string\">"+docId+"</docId>"+
				         "<fileName xsi:type=\"xsd:string\">"+filename+"</fileName>"+
				         "<sessionId xsi:type=\"xsd:string\">"+sessionId+"</sessionId>"+
				      "</urn:getAttachByNoteID>"+
				   "</soapenv:Body>"+
				"</soapenv:Envelope>";
			filename = "F:\\"+filename;
			result = WsUtil.doSendWS(sec_url, ws_url, postData);
			saveFileFormBase64(extractDataFromSOAPMsg(result.getSoapResult(), tagReturnData), filename);
		}
		else if(order == 2){
			postData=   "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wsp.vptw.org\">"+
				   "<soapenv:Header/>"+
				   "<soapenv:Body>"+
				      "<urn:uploadAttachByNoteID soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"+
				      	 "<docId xsi:type=\"xsd:string\">"+docId+"</docId>"+
				      	 "<fileName xsi:type=\"xsd:string\">"+filename+"</fileName>"+
				         "<content xsi:type=\"xsd:string\">"+getBase64FromFile(filename)+"</content>"+
				         "<sessionId xsi:type=\"xsd:string\">"+sessionId+"</sessionId>"+
				      "</urn:uploadAttachByNoteID>"+
				   "</soapenv:Body>"+
				"</soapenv:Envelope>";
			result = WsUtil.doSendWS(sec_url, ws_url, postData);	
		}
		else if(order == 3){
			postData=   "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wsp.vptw.org\">"+
				   "<soapenv:Header/>"+
				   "<soapenv:Body>"+
				      "<urn:getAttachToEncryptByNoteID soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"+
				      	 "<docId xsi:type=\"xsd:string\">"+docId+"</docId>"+
				      	 "<sessionId xsi:type=\"xsd:string\">"+sessionId+"</sessionId>"+
				      "</urn:getAttachToEncryptByNoteID>"+
				   "</soapenv:Body>"+
				"</soapenv:Envelope>";
			filename = "F:\\"+filename;
			result = WsUtil.doSendWS(sec_url, ws_url, postData);
			saveFileFormBase64(extractDataFromSOAPMsg(result.getSoapResult(), tagReturnData), filename);
		}
		else if(order == 4){	
			postData=   
				"<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wsp.vptw.org\">"+
				   "<soapenv:Header/>"+
				   "<soapenv:Body>"+
				      "<urn:uploadToEncryptByNoteID soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"+
				         "<docId xsi:type=\"xsd:string\">"+docId+"</docId>"+
				      	 "<sessionId xsi:type=\"xsd:string\">"+sessionId+"</sessionId>"+
				         "<content xsi:type=\"xsd:string\">"+getBase64FromFile(filename)+"</content>"+
				      "</urn:uploadToEncryptByNoteID>"+
				   "</soapenv:Body>"+
				"</soapenv:Envelope>";			
			 result = WsUtil.doSendWS(sec_url, ws_url, postData);		
		}else if(order == 5){	
			postData=   "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wsp.vptw.org\">"+
					   "<soapenv:Header/>"+
					   "<soapenv:Body>"+
					      "<urn:checkValidSession soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"+					         
					      	 "<sessionId xsi:type=\"xsd:string\">"+sessionId+"</sessionId>"+					         
					      "</urn:checkValidSession>"+
					   "</soapenv:Body>"+
					"</soapenv:Envelope>";
			 result = WsUtil.doSendWS(sec_url, ws_url, postData);
		}
		else if(order == 6){
			postData = 
					   "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wsp.vptw.org\">"+
					   "<soapenv:Header/>"+
					   "<soapenv:Body>"+
					      "<urn:getCertificate soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"+
					         "<sessionId xsi:type=\"xsd:string\">"+sessionId+"</sessionId>"+
					         "<fileCertName xsi:type=\"xsd:string\">"+filename+"</fileCertName>"+
					      "</urn:getCertificate>"+
					   "</soapenv:Body>"+
					"</soapenv:Envelope>";
			 result = WsUtil.doSendWS(sec_url, ws_url, postData);			
		}
		System.out.println(result.getERROR_CODE());
		System.out.println(result.getSoapResult());
	}
	private String getBase64FromFile(String filepath) {
		// TODO Auto-generated method stub		
		File file = new File(filepath);
		BASE64Encoder encoder = new BASE64Encoder();
		String rs=null;
		try {
			rs = encoder.encode(read(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(rs);
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
