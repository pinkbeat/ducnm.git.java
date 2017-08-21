package com.vsc.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XmlUtil{
	

    
    
	public static void main(String[] args) {
		File file=new File("C:\\xmldata\\backup\\CheckOut.xml");
		//String xmlData=readFile(file);
		//int p1=xmlData.indexOf("<");
		//xmlData=xmlData.substring(p1);
		//System.out.println("xmlData=\n"+xmlData);
		//Element element=parse(file);
		Document doc=parse(file);
		Element element=doc.getDocumentElement();
		try {
			transform(doc,element);
			writeXmlFile(file,doc);
		}
		catch(Exception e) {
			
		}
    }
	public static void transfromFile(File file) {
		try {
			Document doc=parse(file);
			Element element=doc.getDocumentElement();
			transform(doc,element);
			writeXmlFile(file,doc);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
    }
	public static String transfromFile(File file,String dir){
		File fileDir=new File(dir);
		if(!fileDir.exists()) fileDir.mkdirs();
		String newFile=dir+file.getName();
		
//		System.out.println("transfromFile "+file.getName());
		try {
			Document doc=parse(file);
			Element element=doc.getDocumentElement();
			transform(doc,element);
			writeXmlFile(new File(newFile),doc);
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return newFile;
	}
	public static Document parse(File file) {
		Document doc=null;
		try {
			DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
			DocumentBuilder db=dbf.newDocumentBuilder();
			 	/*
				InputStream inputStream= new FileInputStream(file);
		        Reader reader = new InputStreamReader(inputStream,"UTF-8");
		        InputSource is = new InputSource(reader);
		        is.setEncoding("UTF-8");
			 	 */
				String xml=FileUtil.readFile(file);
		        doc = parse(xml);
		        
			//doc=db.parse(file);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return doc;
	}
	public static Document parse(String xmlData) {
		Document doc=null;
		try {
			DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
			DocumentBuilder db=dbf.newDocumentBuilder();
			doc=db.parse(new ByteArrayInputStream(xmlData.getBytes("UTF-8")));
		}catch(Exception e) {
			e.printStackTrace();
		}
		return doc;
	}
	public static Element parseElement(String xmlData) {
		Element element=null;
		try {
			DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
			DocumentBuilder db=dbf.newDocumentBuilder();
			Document doc=db.parse(new ByteArrayInputStream(xmlData.getBytes("UTF-8")));
			element=doc.getDocumentElement();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return element;
	}
	public static Document transfromStr(String xmlContent) {
		try {
			Document doc = parse(xmlContent);
			Element element=doc.getDocumentElement();
			transform(doc,element);
			return doc;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
    }
	public static void transform(Document doc,Element element) throws Exception{
		String nodeName=element.getNodeName();
		doc.renameNode(element, null, nodeName.toUpperCase());
		NodeList nodes=element.getChildNodes();
		if(nodes!=null && nodes.getLength()>0) {
			for(int i=0;i<nodes.getLength();i++) {
				Node node=nodes.item(i);
				if(node instanceof Element) {
					//Node n1=node.getFirstChild();
					Element childEL=(Element)node;
					transform(doc,(Element)node);
					//childs.add(node.getNodeName());
				}
			}
		}
	}
	public static void writeXmlFile(File filepath, Document doc){
		try {
		// write the content into xml file
		if(filepath.exists()) {
			filepath.createNewFile();
		}
		TransformerFactory transformerFactory = TransformerFactory.newInstance();		
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(filepath);
		transformer.transform(source, result);
		}catch(Exception e) {
			e.printStackTrace();
		}
//		String xmlString = result.getWriter().toString();
//		System.out.println(xmlString);
	}
	public static Element getElementByPath(Element elem,String xpath) {
		Element el=elem;
		try {
			String[] path_ar= xpath.split("/");
			//System.out.println("root el.getNodeName()="+el.getNodeName());
			for(int i=0;i<path_ar.length;i++) {
				NodeList nodes=el.getElementsByTagName(path_ar[i]);
				if(nodes!=null && nodes.getLength()>0) {
					el=(Element)nodes.item(0);
				}
				else {
//					System.out.println("not found="+path_ar[i]);
					el=null;
					break;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return el;
	}
	public static NodeList getListByPath(Element elem,String xpath) {
		NodeList nl=null;
		Element el=elem;
		try {
			String[] path_ar= xpath.split("/");
			//System.out.println("root el.getNodeName()="+el.getNodeName());
			for(int i=0;i<path_ar.length;i++) {
				NodeList nodes=el.getElementsByTagName(path_ar[i]);
				if(nodes!=null && nodes.getLength()>0) {
					if(i==path_ar.length-1) {
						nl=nodes;
					}
					else {
						el=(Element)nodes.item(0);
					}
				}
				else {
//					System.out.println("not found="+path_ar[i]);
					nl=null;
					break;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return nl;
	}
	public static String getValueByPath(Element elem,String xpath) {
		Element el=elem;
		try {
			String[] path_ar= xpath.split("/");
			for(int i=0;i<path_ar.length;i++) {				
				NodeList nodes=el.getElementsByTagName(path_ar[i]);
				if(nodes!=null && nodes.getLength()>0) {
					el=(Element)nodes.item(0);
				}
				else {
//					System.out.println("not found="+path_ar[i]);
					el=null;
					break;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		if(el==null) {
			return "";
		}
		else {
			return el.getTextContent();
		}
	}
	public static String transformNode(Node node) {
		StreamResult result = new StreamResult(new StringWriter());
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(node);
			transformer.transform(source, result);
		}catch(Exception e) {
			e.printStackTrace();
		}

		String xmlString = result.getWriter().toString();
		return xmlString;
	}
	
}
