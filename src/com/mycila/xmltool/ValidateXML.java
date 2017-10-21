package com.mycila.xmltool;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.util.ErrorHandlerWrapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;



public class ValidateXML {
	public static void main(String asd[]) {
		String[] errarr;
		ValidationResult result =null;
//		ValidateXML.validateXerces();	
		String xml = "<TONG_HOP xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"> "
				+ " <MA_LK>70713140349506316817920701201707051425201707131403GD48302019071042017062020180619020170801</MA_LK>  <STT>1</STT>  <MA_BN>17920701</MA_BN>  <HO_TEN><![CDATA[NGUYỄN THÀNH LẬP]]></HO_TEN>  <NGAY_SINH>1966</NGAY_SINH>  <GIOI_TINH>1sdsdsdsdsdssds</GIOI_TINH>  <DIA_CHI><![CDATA[bhyt trái tuyến]]></DIA_CHI>  <MA_THE>GD4830201907104</MA_THE>  <MA_DKBD>83301</MA_DKBD>  <GT_THE_TU>20170620</GT_THE_TU>  <GT_THE_DEN>20180619</GT_THE_DEN>  <TEN_BENH><![CDATA[BỆNH MẠCH VÀNH ĐÃ PCI- TĂNG HUYẾT ÁP]]></TEN_BENH>  <MA_BENH>I20</MA_BENH>  <MA_BENHKHAC/>  <MA_LYDO_VVIEN>2</MA_LYDO_VVIEN>  <MA_NOI_CHUYEN>83301</MA_NOI_CHUYEN>  <MA_TAI_NAN>0</MA_TAI_NAN>  <NGAY_VAO>201707051425</NGAY_VAO>  <NGAY_RA>201707131403</NGAY_RA>  <SO_NGAY_DTRI>9</SO_NGAY_DTRI>  <KET_QUA_DTRI>2</KET_QUA_DTRI>  <TINH_TRANG_RV>1</TINH_TRANG_RV>  <NGAY_TTOAN>201708010856</NGAY_TTOAN>  <MUC_HUONG>39</MUC_HUONG>  <T_THUOC>1832589</T_THUOC>  <T_VTYT>59040969</T_VTYT>  <T_TONGCHI>130205658.00</T_TONGCHI>  <T_BNTT>79015531.60</T_BNTT>  <T_BHTT>51190126.40</T_BHTT>  <T_NGUONKHAC>0.00</T_NGUONKHAC>  <T_NGOAIDS>0</T_NGOAIDS>  <NAM_QT>2017</NAM_QT>  <THANG_QT>08</THANG_QT>  <MA_LOAI_KCB>3</MA_LOAI_KCB>  <MA_KHOA>K04</MA_KHOA>  <MA_CSKCB>79025</MA_CSKCB>  <MA_KHUVUC/>  <MA_PTTT_QT/>  <CAN_NANG/></TONG_HOP>";
//		result = validateAgainstSchema(new File("C:\\xmldata\\checkout\\testTonghop.xml"), new File("C:\\xmldata\\xsd\\TongHop.xsd"));
//		result = validateAgainstSchema(new File("C:\\xmldata\\checkout\\testCTT.xml"), new File("C:\\xmldata\\xsd\\ChiTietThuoc.xsd"));
//		result = validateAgainstSchema(new File("C:\\xmldata\\checkout\\testDVKT.xml"), new File("C:\\xmldata\\xsd\\ChiTietDVKT.xsd"));
//		result = validateAgainstSchema(new File("C:\\xmldata\\checkout\\testCLS.xml"), new File("C:\\xmldata\\xsd\\ChiTietCLS.xsd"));
//		result = validateAgainstSchema(new File("C:\\xmldata\\checkout\\testDBCLS.xml"), new File("C:\\xmldata\\xsd\\ChiTietDienBienCLS.xsd"));
//		result = validateAgainstSchema(xml, new File("C:\\xmldata\\xsd\\TongHop.xsd"));
//		result = validateXerces();
//		if(result.hasError()) {
//			errarr = result.getErrorMessages();
//			for(int i=0;i<errarr.length;i++) {
//				System.out.println(errarr[i]);
//			}
//		}else if(result.hasWarning()){
//			errarr = result.getWarningMessages();
//			for(int i=0;i<errarr.length;i++) {
//				System.out.println(errarr[i]);
//			}
//		}
		testValidateXML();
	}
	
	public static void testValidateXML() {
		Map<String,String> map = new HashMap<>();
		map.put("C:\\xmldata\\xsd\\TongHop.xsd", "C:\\xmldata\\checkout\\testTonghop.xml");
		map.put("C:\\xmldata\\xsd\\ChiTietThuoc.xsd", "C:\\xmldata\\checkout\\testCTT.xml");
		map.put("C:\\xmldata\\xsd\\ChiTietDVKT.xsd", "C:\\xmldata\\checkout\\testDVKT.xml");
//		map.put("C:\\xmldata\\xsd\\testCLS.xsd", "C:\\xmldata\\checkout\\ChiTietCLS.xml");
//		map.put("C:\\xmldata\\xsd\\testDBCLS.xsd", "C:\\xmldata\\checkout\\ChiTietDienBienCLS.xml");
		JSONArray jsonArray = validateAgainstSchema(map);
		JSONArray result=null;
		if(jsonArray!= null && jsonArray.length()>0) {
			System.out.println("[Error]=");
			result = jsonArray.getJSONArray(0);
			System.out.println(result);
//			for (int i = 0; i < result.length(); i++) {
//				System.out.println(result.getJSONObject(i).toString());
//			}
			result = jsonArray.getJSONArray(1);
			System.out.println("[Warrning]=");
			for (int i = 0; i < result.length(); i++) {
				System.out.println(result.getJSONObject(i).toString());
			}
		}
		
	}
  public static ValidationResult validateXerces() {
	  XMLErrorHandler errors = new XMLErrorHandler();
      try {
        DOMParser parser = new DOMParser();
        parser.setFeature("http://xml.org/sax/features/validation", true);
        parser.setProperty(
             "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", 
             "C:\\xmldata\\xsd\\TongHop.xsd");        
        parser.setErrorHandler(errors);
        parser.parse("C:\\xmldata\\checkout\\testTonghop.xml");        
        
     } catch (SAXException e) {
        System.out.print("Problem parsing the file: "+e.getMessage());
     } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      return errors;
  }
  public static JSONArray validateAgainstSchema(Map<String,String> map) { 
//	  Source schemaFile = null;	  
//	  DocumentBuilderFactory builderFactory = null;
//	  DocumentBuilder parser = null;
//	  SchemaFactory factory = null;
//	  Document document = null;
//	  Schema schema = null;
//	  Validator validator = null;
	  String xsd=null;
	  String xml=null;
	  File tmpxsd = null;
	  File tmpxml = null;
	  JSONArray array = new JSONArray();
	  JSONArray jsonArrayErr = new JSONArray();
	  JSONObject jsonObjectErr = null;
	  JSONArray jsonArrayWar = new JSONArray();
	  JSONObject jsonObjectWar = null;
	  Set<String> keys = map.keySet();
	  ValidationResult result = null;
	  Iterator<String> xmlIte = keys.iterator();
	  if(map != null && map.size()>0) {
		  try {			  
//			  builderFactory =  DocumentBuilderFactory.newInstance();
//	    	  parser = builderFactory.newDocumentBuilder();
//	    	  builderFactory.setNamespaceAware(true);
//			  factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			  int i=1;
			  while (xmlIte.hasNext()){
				  try{					  					  
					  xsd = xmlIte.next();
					  xml = map.get(xsd);						  
					  tmpxml = new File(xml);
					  tmpxsd = new File(xsd);
					  DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance(); 
			          builderFactory.setNamespaceAware(true); 
			          DocumentBuilder parser = builderFactory.newDocumentBuilder(); 	           
			          SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
					  Document document = parser.parse(tmpxml); 			            
					  Source schemaFile = new StreamSource(tmpxsd);
			          Schema schema = factory.newSchema(schemaFile); 
			          Validator validator = schema.newValidator();
			          XMLErrorHandler errorHandler = new XMLErrorHandler();
			          validator.setErrorHandler(errorHandler);
			          validator.validate(new DOMSource(document));			          			         
			          if(errorHandler.hasError()) {
			        	  jsonObjectErr = new JSONObject();
			        	  jsonObjectErr.put(tmpxml.getName(), errorHandler.getErrorMessages());
			        	  jsonArrayErr.put(jsonObjectErr);
			          }
			          if(errorHandler.hasWarning()) {
			        	  jsonObjectWar = new JSONObject();
			        	  jsonObjectWar.put(tmpxml.getName(), errorHandler.getWarningMessages());
			        	  jsonArrayWar.put(jsonObjectWar);
			          }			          
			      } catch (FileNotFoundException ex) { 
//			          throw new OpenClinicaSystemException("File was not found", ex.getCause());
			    	  System.out.println("File was not found "+ ex.getCause());
			      } catch (IOException ioe) { 
//			          throw new OpenClinicaSystemException("IO Exception", ioe.getCause()); 
			    	  System.out.println("IO Exception "+ ioe.getCause());
			      } catch (SAXParseException spe) {           
//			          throw new OpenClinicaSystemException("Line : " + spe.getLineNumber() + " - " + spe.getMessage(), spe.getCause()); 
			          System.out.println("Line : " + spe.getLineNumber() + " - " + spe.getMessage());
	//		    	  System.out.println(schemaFile.getSystemId() + " is NOT valid reason:" + spe);
			      } catch (SAXException e) { 
//			          throw new OpenClinicaSystemException(e.getMessage(), e.getCause()); 
			          System.out.println(e.getMessage()+"---"+ e.getCause());
			      }
			  } 
			  array.put(jsonArrayErr);
	          array.put(jsonArrayWar);
		  }catch (ParserConfigurationException pce) { 
	          throw new OpenClinicaSystemException(pce.getMessage(), pce.getCause());
//		    	  System.out.println(pce.getMessage()+"---"+pce.getCause());
	      }
	  }else {
		  System.out.println("Nothing to do");		  
	  }		      	      	  
	  return array;      
  }
  public static ValidationResult validateAgainstSchema(File xml, File xsdFile) { 
	  Source schemaFile = null;
	  XMLErrorHandler errorHandler = new XMLErrorHandler();
      try { 
          // parse an XML document into a DOM tree 
          DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance(); 
          builderFactory.setNamespaceAware(true); 
          DocumentBuilder parser = builderFactory.newDocumentBuilder(); 
          Document document = parser.parse(xml); 
          // create a SchemaFactory capable of understanding WXS schemas 
          SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
          // load a WXS schema, represented by a Schema instance 
           schemaFile = new StreamSource(xsdFile);
          Schema schema = factory.newSchema(schemaFile); 
          // create a Validator instance, which can be used to validate an 
          // instance document 
          Validator validator = schema.newValidator();
//          // validate the DOM tree 
          validator.setErrorHandler(errorHandler);
          validator.validate(new DOMSource(document));                 
//          System.out.println(schemaFile.getSystemId() + " is valid");
      } catch (FileNotFoundException ex) { 
          throw new OpenClinicaSystemException("File was not found", ex.getCause());
//    	  System.out.println("File was not found "+ ex.getCause());
      } catch (IOException ioe) { 
          throw new OpenClinicaSystemException("IO Exception", ioe.getCause()); 
//    	  System.out.println("IO Exception "+ ioe.getCause());
      } catch (SAXParseException spe) {           
          throw new OpenClinicaSystemException("Line : " + spe.getLineNumber() + " - " + spe.getMessage(), spe.getCause()); 
//          System.out.println("Line : " + spe.getLineNumber() + " - " + spe.getMessage());
//    	  System.out.println(schemaFile.getSystemId() + " is NOT valid reason:" + spe);
      } catch (SAXException e) { 
//          throw new OpenClinicaSystemException(e.getMessage(), e.getCause()); 
          System.out.println(e.getMessage()+"---"+ e.getCause());
      } catch (ParserConfigurationException pce) { 
//          throw new OpenClinicaSystemException(pce.getMessage(), pce.getCause());
    	  System.out.println(pce.getMessage()+"---"+pce.getCause());
      }    
      return errorHandler;
  }
  public static ValidationResult validateAgainstSchema(String xmlString, File xsdFile) { 
	  Source schemaFile = null;
//	  XMLStreamReader reader = null;
	  XMLEventReader reader = null;
	  XMLErrorHandler errorHandler = new XMLErrorHandler();
      try { 
    	  DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance(); 
          builderFactory.setNamespaceAware(true); 
          DocumentBuilder parser = builderFactory.newDocumentBuilder(); 
    	  Document document = parser.parse(new InputSource(new StringReader(xmlString.trim()))); 
    	  
          // create a SchemaFactory capable of understanding WXS schemas 
          SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 

          // load a WXS schema, represented by a Schema instance 
          schemaFile = new StreamSource(xsdFile); 
          Schema schema = factory.newSchema(schemaFile); 

          // create a Validator instance, which can be used to validate an 
          // instance document 
          Validator validator = schema.newValidator(); 
          
          // validate the DOM tree
          
          validator.setErrorHandler(errorHandler);
          validator.validate(new DOMSource(document)); 
//          System.out.println(schemaFile.getSystemId() + " is valid");
          // parse an XML document into a DOM tree                            	
//	      Validator validator = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new StreamSource(xsdFile)).newValidator();
////	      reader =  XMLInputFactory.newFactory().createXMLStreamReader(new StreamSource(new StringReader(xmlString.trim())));
//	      reader =  XMLInputFactory.newFactory().createXMLEventReader(new StringReader(xmlString.trim()));
////	      validator.setErrorHandler(new XsdErrorHandler(reader));
//	      validator.validate(new StAXSource(reader));                     
      } catch (FileNotFoundException ex) { 
          throw new OpenClinicaSystemException("File was not found", ex.getCause());
//    	  System.out.println("File was not found "+ ex.getCause());
      } catch (IOException ioe) { 
          throw new OpenClinicaSystemException("IO Exception", ioe.getCause()); 
//    	  System.out.println("IO Exception "+ ioe.getCause());
      } catch (SAXParseException spe) { 
          throw new OpenClinicaSystemException("Line : " + spe.getLineNumber() + " - " + spe.getMessage(), spe.getCause()); 
//          System.out.println("Line : " + spe.getLineNumber() + " - " + spe.getMessage());
//    	  System.out.println(schemaFile.getSystemId() + " is NOT valid reason:" + spe);
      } catch (SAXException e) { 
          throw new OpenClinicaSystemException(e.getMessage(), e.getCause()); 
//          System.out.println(e.getMessage()+"---"+ e.getCause());
      
	} catch (FactoryConfigurationError e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}        
   return errorHandler;   
  } 
}


/* 
 * OpenClinica is distributed under the 
 * GNU Lesser General Public License (GNU LGPL). 
 * For details see: http://www.openclinica.org/license 
 * 
 * Copyright 2003-2008 Akaza Research  
 */
 
/**
 * @author Krikor Krumlian 
 */ 
//@SuppressWarnings("serial") 
class OpenClinicaSystemException extends RuntimeException { 
    private String errorCode; 
    private Object[] errorParams; 
 
    public OpenClinicaSystemException(String code, String message) { 
        this(message); 
        this.errorCode = code; 
    } 
 
    public OpenClinicaSystemException(String code, String message, Throwable cause) { 
        this(message, cause); 
        this.errorCode = code; 
    } 
 
    public OpenClinicaSystemException(String message, Throwable cause) { 
        super(message, cause); 
    } 
 
    public OpenClinicaSystemException(Throwable cause) { 
        super(cause); 
    } 
 
    public OpenClinicaSystemException(String message) { 
        super(message); 
        this.errorCode = message; 
    } 
 
    public OpenClinicaSystemException(String code, Object[] errorParams) { 
        this.errorCode = code; 
        this.errorParams = errorParams; 
    } 
 
    public OpenClinicaSystemException(String code, Object[] errorParams, String message) { 
        this(message); 
        this.errorCode = code; 
        this.errorParams = errorParams; 
    } 
 
    public String getErrorCode() { 
        return errorCode; 
    } 
 
    public Object[] getErrorParams() { 
        return errorParams; 
    } 
 
    public void setErrorParams(Object[] errorParams) { 
        this.errorParams = errorParams; 
    } 
}



class XsdErrorHandler implements ErrorHandler {

    private XMLStreamReader reader;

    public XsdErrorHandler(XMLStreamReader reader) {
        this.reader = reader; }

    @Override
    public void error(SAXParseException e) throws SAXException {
          throw new SAXException(reader.getLocalName());}

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        throw new SAXException(reader.getLocalName());}

    @Override
    public void warning(SAXParseException sAXParseException) throws SAXException {}
}