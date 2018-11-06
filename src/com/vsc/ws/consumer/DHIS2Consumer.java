package com.vsc.ws.consumer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vsc.util.DHIS2RestfulHelper;
import com.vsc.util.JsWSUtil;

public class DHIS2Consumer {		
	static String dhis2_bcx2 = "{\r\n \"rpcode\":\"27X02\",\r\n \"cateOpt\":\"m9ErSxUe1n3\",\r\n \"inds\":[\"tg3tGzIThbA\",\r\n        \"U39J6OmtUVF\",\r\n        \"bikzAN6GhaG\",\r\n        \"gzUAQXSJ5fL\",\r\n        \"gnVBQoDg094\",\r\n        \"HPTnpkxE6v8\",\r\n        \"oDgpQNnX1c1\",\r\n        \"Asqu9CyZLcG\",\r\n        \"GJOyeFi2C86\"\r\n        ],\r\n \"inds_map\":[\r\n            {\"1.1.1\":\"tg3tGzIThbA\"},\r\n            {\"1.1.2\":\"U39J6OmtUVF\"},\r\n            {\"1.2\":\"bikzAN6GhaG\"},\r\n            {\"1.3\":\"gzUAQXSJ5fL\"},\r\n            {\"1.6\":\"gnVBQoDg094\"},\r\n            {\"2.2\":\"HPTnpkxE6v8\"},\r\n            {\"2.3\":\"oDgpQNnX1c1\"},\r\n            {\"2.4\":\"Asqu9CyZLcG\"},\r\n            {\"2.5\":\"GJOyeFi2C86\"}\r\n        ]\r\n}";
	static String dhis2_bcx5 = "{\r\n \"rpcode\":\"27X05\",\r\n \"cateOpt\":\"m9ErSxUe1n3\",\r\n \"inds\":[\"i5yXKoAcSpb\",\r\n        \"QfSJtngOK7c\",\r\n        \"kiO7ZFe67bt\",\r\n        \"rrQkixe4Vqc\",\r\n        \"BxwLDew2XkX\",\r\n        \"KfJOeNWNzuB\",\r\n        \"SJ3MWLZS4C7\",\r\n        \"umHMm1zLFF9\",\r\n        \"fgL3hP3B3Nc\",\r\n        \"XprPxm6c373\"\r\n        ],\r\n \"inds_map\":[\r\n            {\"1\":\"i5yXKoAcSpb\"},\r\n            {\"2\":\"QfSJtngOK7c\"},\r\n            {\"2.1\":\"kiO7ZFe67bt\"},\r\n            {\"2.2\":\"rrQkixe4Vqc\"},\r\n            {\"2.3\":\"BxwLDew2XkX\"},\r\n            {\"2.4\":\"KfJOeNWNzuB\"},\r\n            {\"3\":\"SJ3MWLZS4C7\"},\r\n            {\"4\":\"umHMm1zLFF9\"},\r\n            {\"5\":\"fgL3hP3B3Nc\"},\r\n            {\"6\":\"XprPxm6c373\"}\r\n        ]\r\n}";
	static String dhis2_bcx1 = "{\r\n \"rpcode\":\"27X01\",\r\n \"cateOpt\":\"m9ErSxUe1n3\",\r\n \"inds\":[\"vlrH1xWQNTn\",\r\n        \"S117HX9INOg\",\r\n        \"KaNOeN9lTVb\",\r\n        \"c74rVPtTJRc\",\r\n        \"ZYDqKZDP7Rz\",\r\n        \"JfiaydnXRZE\",\r\n        \"xcvRHk5OWIY\",\r\n        \"o2lbJLDJ0YX\",\r\n        \"BUYJMCnJun7\",\r\n        \"wAGJ1QaefzQ\",\r\n        \"hEl6k7pDUtQ\",\r\n        \"oWknxXwK9Ul\",\r\n        \"wAGJ1QaefzQ\",\r\n        \"QXfRyr5ESa8\",\r\n        \"IhuWjwLbziP\",\r\n        \"ubzfe0kSGiN\",\r\n        \"v6D7y9a4V53\",\r\n        \"wPlQmAEhVXa\",\r\n        \"JeC84Z9TdnP\",\r\n        \"Uz0R3QwHrje\",\r\n        \"xwJLUAjawe8\"\r\n        ],\r\n \"inds_map\":[\r\n            {\"1\":\"vlrH1xWQNTn\"},\r\n            {\"2\":\"S117HX9INOg\"},\r\n            {\"3\":\"KaNOeN9lTVb\"},\r\n            {\"4\":\"c74rVPtTJRc\"},\r\n            {\"5\":\"ZYDqKZDP7Rz\"},\r\n            {\"5.1\":\"JfiaydnXRZE\"},\r\n            {\"5.3\":\"xcvRHk5OWIY\"},\r\n            {\"5.4\":\"o2lbJLDJ0YX\"},\r\n            {\"5.5\":\"BUYJMCnJun7\"},\r\n            {\"6\":\"wAGJ1QaefzQ\"},\r\n            {\"6.1\":\"hEl6k7pDUtQ\"},\r\n            {\"7\":\"oWknxXwK9Ul\"},\r\n            {\"7.1\":\"QXfRyr5ESa8\"},\r\n            {\"7.2.1\":\"IhuWjwLbziP\"},\r\n            {\"7.2.2\":\"ubzfe0kSGiN\"},\r\n            {\"7.2.3\":\"v6D7y9a4V53\"},\r\n            {\"7.3\":\"wPlQmAEhVXa\"},\r\n            {\"7.3.1\":\"JeC84Z9TdnP\"},\r\n            {\"7.4\":\"Uz0R3QwHrje\"},\r\n            {\"7.4.1\":\"xwJLUAjawe8\"}\r\n        ]\r\n}";
	static String bcx1_KH = "{\r\n\t\"rid\":\"27X01\",\r\n\t\"period\":\"012018\",\r\n\t\"oid\":\"20148\",\r\n\t\"data\": \r\n\t\t[{\"ind_code\":\"1\",\"ind_val\":[\"63\"]},\r\n\t\t{\"ind_code\":\"2\",\"ind_val\":[\"15\"]},\r\n\t\t{\"ind_code\":\"3\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"4\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"5\",\"ind_val\":[\"21355\"]},\r\n\t\t{\"ind_code\":\"5.1\",\"ind_val\":[\"10712\"]},\r\n\t\t{\"ind_code\":\"5.2\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"5.3\",\"ind_val\":[\"821\"]},\r\n\t\t{\"ind_code\":\"5.4\",\"ind_val\":[\"2931\"]},\r\n\t\t{\"ind_code\":\"5.5\",\"ind_val\":[\"6256\"]},\r\n\t\t{\"ind_code\":\"6\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"6.1\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"7\",\"ind_val\":[\"20\"]},\r\n\t\t{\"ind_code\":\"7.1\",\"ind_val\":[\"12\"]},\r\n\t\t{\"ind_code\":\"7.2.1\",\"ind_val\":[\"20\"]},\r\n\t\t{\"ind_code\":\"7.2.2\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"7.2.3\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"7.3\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"7.3.1\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"7.4\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"7.4.1\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"8\",\"ind_val\":[\"\",\"\"]},\r\n\t\t{\"ind_code\":\"9\",\"ind_val\":[\"\",\"\",\"\",\"\"]}]}";
	static String bcx1_KBT = "{\r\n\t\"rid\":\"27X01\",\r\n\t\"period\":\"012018\",\r\n\t\"oid\":\"20148\",\r\n\t\"data\": \r\n\t\t[{\"ind_code\":\"1\",\"ind_val\":[\"50\"]},\r\n\t\t{\"ind_code\":\"2\",\"ind_val\":[\"12\"]},\r\n\t\t{\"ind_code\":\"3\",\"ind_val\":[\"12\"]},\r\n\t\t{\"ind_code\":\"4\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"5\",\"ind_val\":[\"13371\"]},\r\n\t\t{\"ind_code\":\"5.1\",\"ind_val\":[\"6707\"]},\r\n\t\t{\"ind_code\":\"5.2\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"5.3\",\"ind_val\":[\"300\"]},\r\n\t\t{\"ind_code\":\"5.4\",\"ind_val\":[\"1640\"]},\r\n\t\t{\"ind_code\":\"5.5\",\"ind_val\":[\"4051\"]},\r\n\t\t{\"ind_code\":\"6\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"6.1\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"7\",\"ind_val\":[\"46\"]},\r\n\t\t{\"ind_code\":\"7.1\",\"ind_val\":[\"15\"]},\r\n\t\t{\"ind_code\":\"7.2.1\",\"ind_val\":[\"21\"]},\r\n\t\t{\"ind_code\":\"7.2.2\",\"ind_val\":[\"19\"]},\r\n\t\t{\"ind_code\":\"7.2.3\",\"ind_val\":[\"3\"]},\r\n\t\t{\"ind_code\":\"7.3\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"7.3.1\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"7.4\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"7.4.1\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"8\",\"ind_val\":[\"\",\"\"]},\r\n\t\t{\"ind_code\":\"9\",\"ind_val\":[\"\",\"\",\"\",\"\"]}]}";
	static String bcx5_KH = "{\r\n\t\"rid\":\"27X05\",\r\n\t\"period\":\"012018\",\r\n\t\"oid\":\"20148\",\r\n\t\"data\":\r\n\t\t[{\"ind_code\":\"1\",\"ind_val\":[\"0\",\"\"]},\r\n\t\t{\"ind_code\":\"2\",\"ind_val\":[\"745\"]},\r\n\t\t{\"ind_code\":\"2.1\",\"ind_val\":[\"513\"]},\r\n\t\t{\"ind_code\":\"2.2\",\"ind_val\":[\"523\"]},\r\n\t\t{\"ind_code\":\"2.3\",\"ind_val\":[\"0\"]},\r\n\t\t{\"ind_code\":\"2.4\",\"ind_val\":[\"126\"]},\r\n\t\t{\"ind_code\":\"3\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"4\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"5\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"6\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"7\",\"ind_val\":[\"\",\"\",\"\",\"\",\"\"]},\r\n\t\t{\"ind_code\":\"8\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"9\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"10\",\"ind_val\":[\"\",\"\",\"\"]},\r\n\t\t{\"ind_code\":\"11\",\"ind_val\":[\"\",\"\",\"\"]},\r\n\t\t{\"ind_code\":\"12\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"13\",\"ind_val\":[\"\"]}]}";
	static String bcx5_KBT = "{\r\n\t\"rid\":\"27X05\",\r\n\t\"period\":\"012018\",\r\n\t\"oid\":\"20148\",\r\n\t\"data\":\r\n\t\t[{\"ind_code\":\"1\",\"ind_val\":[\"11\",\"\"]},\r\n\t\t{\"ind_code\":\"2\",\"ind_val\":[\"1093\"]},\r\n\t\t{\"ind_code\":\"2.1\",\"ind_val\":[\"634\"]},\r\n\t\t{\"ind_code\":\"2.2\",\"ind_val\":[\"808\"]},\r\n\t\t{\"ind_code\":\"2.3\",\"ind_val\":[\"7\"]},\r\n\t\t{\"ind_code\":\"2.4\",\"ind_val\":[\"240\"]},\r\n\t\t{\"ind_code\":\"3\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"4\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"5\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"6\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"7\",\"ind_val\":[\"7\",\"\",\"\",\"\",\"\"]},\r\n\t\t{\"ind_code\":\"8\",\"ind_val\":[\"8\"]},\r\n\t\t{\"ind_code\":\"9\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"10\",\"ind_val\":[\"\",\"\",\"\"]},\r\n\t\t{\"ind_code\":\"11\",\"ind_val\":[\"\",\"\",\"\"]},\r\n\t\t{\"ind_code\":\"12\",\"ind_val\":[\"\"]},\r\n\t\t{\"ind_code\":\"13\",\"ind_val\":[\"\"]}]}";
	static JSONObject ou_demo = new JSONObject("{\"96016\":\"WtOD0oGliTn\",\"96017\":\"S2MC4Lm8lWu\"}");
	static JSONObject period_Q = new JSONObject("{\"20181\":\"2018Q1\",\"20182\":\"2018Q2\",\"20183\":\"2018Q3\",\"20184\":\"2018Q4\",\"2018\":\"2018\",\"2017\":\"2017\"}");
	public static void main(String[] asd) throws UnsupportedEncodingException, JSONException{
				
//		JSONObject jsonObject = DHIS2RestfulHelper.doBasicAuthentication("http://dhis2.yte.gov.vn/dhis227/api/26/me", "camau", "123456Aa@");
//		JSONObject jsonObject = DHIS2RestfulHelper.doOAuth2Authentication("/uaa/oauth/token");
//		DHIS2RestfulHelper.doSend("/api/organisationUnits.json?filter=name:eq:"+URLEncoder.encode("Xã Hòa Mỹ","UTF-8")+"&fields=id","Bearer "+jsonObject.getString("access_token"),"GET");
//		String urlParameters = "de=" +"U39J6OmtUVF"+ //+ "tg3tGzIThbA" +
//	                       "&co=" + "m9ErSxUe1n3" +
//	                       "&ou=" + "nsi7LoEjw86" +
//	                       "&pe=" + "2018"+
//	                       "&value="+"400";
//		String urlParameters = null;
//		String code = null;
//		JSONObject object = new JSONObject(dhis2_bcx5);
//		JSONArray array = object.getJSONArray("inds");
//		for(int i=0;i<array.length();i++){
//			code = (String)array.get(i);
//			System.out.println(code);
//			urlParameters = "de=" +code+ //+ "tg3tGzIThbA" +
//                    "&co=" + "m9ErSxUe1n3" +
//                    "&ou=" + "nsi7LoEjw86" +
//                    "&pe=" + "2018"+
//                    "&value="+200+i;
//			DHIS2RestfulHelper.doSend("/api/26/dataValues?"+urlParameters,"Bearer "+jsonObject.getString("access_token"),"POST");
//		}
//		for(int i=0;i<array.length();i++){
//			code = (String)array.get(i);
//			System.out.println(code);
//			urlParameters = "de=" +code+ //+ "tg3tGzIThbA" +
//                    "&co=" + "m9ErSxUe1n3" +
//                    "&ou=" + ou_KH +
//                    "&pe=" + "2018Q1"+
//                    "&value="+(200-2*i);
//			DHIS2RestfulHelper.doSend("/api/26/dataValues?",urlParameters,"Bearer "+jsonObject.getString("access_token"),"POST");
//		}	
		sendtoDHIS2(bcx5_KH, dhis2_bcx5);
		sendtoDHIS2(bcx5_KBT, dhis2_bcx5);
		sendtoDHIS2(bcx1_KH, dhis2_bcx1);
		sendtoDHIS2(bcx1_KBT, dhis2_bcx1);
	}
	public static int sendtoDHIS2(String dr, String dhis2){
		if(dr == null || dhis2 == null){
			return 11;
		}
		JSONObject jdr = null;
		JSONObject jdhis2 = null;		
		JSONObject objK1 = null;
		JSONObject objK2= null;
		jdr = new JSONObject(dr);
		jdhis2 = new JSONObject(dhis2);
		String ou = jdr.getString("oid");
		String period = jdr.getString("period");
		try{
			ou_demo.getString(ou);
		}catch(JSONException e){
			System.out.println("OU demo does not supported. "+ e.getMessage());
			return 11;
		}
		try{
			period_Q.getString(period);
		}catch(JSONException e){
			System.out.println("Period does not supported. "+ e.getMessage());
			return 11;
		}
		String urlParameters = "";
	    Iterator i= null;
	    String key = "";
	    String value = "";
		JSONArray arr1 = jdr.getJSONArray("data");
		JSONArray arr2 = jdhis2.getJSONArray("inds_map");
		JSONObject jsonObject = DHIS2RestfulHelper.doOAuth2Authentication();
		for(int j=0;j<arr2.length();j++){		
			objK2 = arr2.getJSONObject(j);
			i= objK2.keySet().iterator();
			if(i.hasNext()){
				key = (String)i.next();
				System.out.println("key = "+key);
				for(int k=0;k<arr1.length();k++){
					try{
						objK1 = arr1.getJSONObject(k);
						if(objK1.getString("ind_code").equals(key)){
//							System.out.println(jdhis2.getString("cateOpt"));
//							System.out.println(ou_KH);
							System.out.println("dhis2code = "+objK2.getString(key));							
							value = ((JSONArray)objK1.get("ind_val")).getString(0);
							if(value.equals("")) value="0";
							System.out.println("dhis2value = "+value);
							urlParameters = "de=" +objK2.getString(key)+ //+ "tg3tGzIThbA" +
			                    "&co=" + jdhis2.getString("cateOpt") +
			                    "&ou=" + ou_demo.getString(ou) +
			                    "&pe=" + period_Q.getString(period)+
			                    "&value="+value;
//							System.out.println(urlParameters);
							DHIS2RestfulHelper.doSend(urlParameters,"Bearer "+jsonObject.getString("access_token"),"POST");
							break;
						}						
					}catch(JSONException e){
						System.out.println(e.getMessage());
						continue;
					}	
				}	
			}
		}
		return 0;
	}
}
