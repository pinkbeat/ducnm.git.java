package com.cdit.test.portal;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 
import java.util.Locale;

public class LiferayRestServiceUtil {
	 static HttpHost targetHost;
	 static DefaultHttpClient httpclient;
	 public static void initConnection(){
//	     	targetHost = new HttpHost("123.31.40.162", 10080, "http");
		 	targetHost = new HttpHost("bvtn.org.vn", 80, "http");
	        httpclient = new DefaultHttpClient();
	        httpclient.getCredentialsProvider().setCredentials(
		        new AuthScope(targetHost.getHostName(), targetHost.getPort()),
//		        new UsernamePasswordCredentials("congthongtinboyte@gmail.com", "18273645a@"));
		        new UsernamePasswordCredentials("ducnm", "123"));
	 }
	 public static void addArticle() throws Exception {
	        // Create AuthCache instance
	        AuthCache authCache = new BasicAuthCache();
	        Map<Locale, String> titleMap = null;
	        Map<Locale, String> descriptionMap = null;
	        Map images = null;
	        
	        // Generate BASIC scheme object and add it to the local
	        // auth cache
	        BasicScheme basicAuth = new BasicScheme();
	        authCache.put(targetHost, basicAuth);

	        // Add AuthCache to the execution context
	        BasicHttpContext ctx = new BasicHttpContext();
	        ctx.setAttribute(ClientContext.AUTH_CACHE, authCache);

	        HttpPost post = new HttpPost("/api/jsonws/journalarticle/add-article");
	        Calendar yesterday = Calendar.getInstance();
	        yesterday.add(Calendar.DAY_OF_YEAR, -1);
	        Calendar nextWeek = Calendar.getInstance();
	        nextWeek.add(Calendar.WEEK_OF_YEAR, 1);
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
//	        params.add(new BasicNameValuePair("serviceClassName", "com.liferay.portlet.journal.service.JournalArticleServiceUtil"));
	        params.add(new BasicNameValuePair("serviceClassName", "com.liferay.portlet.journal.service.impl.JournalArticleServiceImpl"));
	        params.add(new BasicNameValuePair("serviceMethodName", "addArticle"));
//	        params.add(new BasicNameValuePair("serviceParameters", "[groupId,articleId,autoArticleId,title,description,content,type,structureId,templateId,displayDateMonth,displayDateDay,displayDateYear,displayDateHour,displayDateMinute,expirationDateMonth,expirationDateDay,expirationDateYear,expirationDateHour,expirationDateMinute,neverExpire,reviewDateMonth,reviewDateDay,reviewDateYear,reviewDateHour,reviewDateMinute,neverReview,indexable,articleURL,serviceContext]"));
//	        params.add(new BasicNameValuePair("serviceParameterTypes", "[long,java.lang.String,boolean,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,int,int,int,int,int,int,int,int,int,int,boolean,int,int,int,int,int,boolean,boolean,java.lang.String,com.liferay.portal.service.ServiceContext]"));
	        params.add(new BasicNameValuePair("serviceParameters", "[groupId,folderId,classNameId,classPK,articleId,autoArticleId,titleMap,descriptionMap,content,type,ddmStructureKey,ddmTemplateKey,layoutUuid,displayDateMonth,displayDateDay,displayDateYear,displayDateHour,displayDateMinute,expirationDateMonth,expirationDateDay,expirationDateYear,expirationDateHour,expirationDateMinute,neverExpire,reviewDateMonth,reviewDateDay,reviewDateYear,reviewDateHour,reviewDateMinute,neverReview,indexable,smallImage,smallImageURL,smallFile,images,articleURL,serviceContext]"));
	        params.add(new BasicNameValuePair("serviceParameterTypes", "[long,long,long,long,java.lang.String,boolean,java.util.Map,java.util.Map,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,int,int,int,int,int,int,int,int,int,int,boolean,int,int,int,int,int,boolean,boolean,boolean,java.lang.String,java.io.File,java.util.Map,java.lang.String,com.liferay.portal.service.ServiceContext]"));
	        params.add(new BasicNameValuePair("groupId", "112015"));
	        params.add(new BasicNameValuePair("folderId", "0"));
	        params.add(new BasicNameValuePair("classNameId", "0"));
	        params.add(new BasicNameValuePair("classPK", "0"));	        
	        params.add(new BasicNameValuePair("articleId", ""));
	        params.add(new BasicNameValuePair("autoArticleId", "true"));
	        params.add(new BasicNameValuePair("titleMap", "{\"en_US\":\"Test JSON Article\"}"));
	        params.add(new BasicNameValuePair("descriptionMap","{\"en_US\":\"Test JSON Description\"}" ));
	        params.add(new BasicNameValuePair("content", "<?xml version='1.0' encoding='UTF-8'?><root available-locales=\"en_US\" default-locale=\"en_US\"><static-content language-id=\"en_US\"><![CDATA[<p>\n" +
	                "\test content</p>]]></static-content></root>"));
	        params.add(new BasicNameValuePair("type", "general"));
	        params.add(new BasicNameValuePair("ddmStructureKey", ""));
	        params.add(new BasicNameValuePair("ddmTemplateKey", ""));
	        params.add(new BasicNameValuePair("layoutUuid", ""));
	        params.add(new BasicNameValuePair("displayDateMonth", "" + (1 + yesterday.get(Calendar.MONTH))));
	        params.add(new BasicNameValuePair("displayDateDay", "" + yesterday.get(Calendar.DAY_OF_MONTH)));
	        params.add(new BasicNameValuePair("displayDateYear", "" + yesterday.get(Calendar.YEAR)));
	        params.add(new BasicNameValuePair("displayDateHour", "" + yesterday.get(Calendar.HOUR_OF_DAY)));
	        params.add(new BasicNameValuePair("displayDateMinute", "" + yesterday.get(Calendar.MINUTE)));
	        params.add(new BasicNameValuePair("expirationDateMonth", "" + (1 + nextWeek.get(Calendar.MONTH))));
	        params.add(new BasicNameValuePair("expirationDateDay", "" + nextWeek.get(Calendar.DAY_OF_MONTH)));
	        params.add(new BasicNameValuePair("expirationDateYear", "" + nextWeek.get(Calendar.YEAR)));
	        params.add(new BasicNameValuePair("expirationDateHour", "" + nextWeek.get(Calendar.HOUR_OF_DAY)));
	        params.add(new BasicNameValuePair("expirationDateMinute", "" + nextWeek.get(Calendar.MINUTE)));
	        params.add(new BasicNameValuePair("neverExpire", "true"));
	        params.add(new BasicNameValuePair("reviewDateMonth", "" + (1 + nextWeek.get(Calendar.MONTH))));
	        params.add(new BasicNameValuePair("reviewDateDay", "" + nextWeek.get(Calendar.DAY_OF_MONTH)));
	        params.add(new BasicNameValuePair("reviewDateYear", "" + nextWeek.get(Calendar.YEAR)));
	        params.add(new BasicNameValuePair("reviewDateHour", "" + nextWeek.get(Calendar.HOUR_OF_DAY)));
	        params.add(new BasicNameValuePair("reviewDateMinute", "" + nextWeek.get(Calendar.MINUTE)));
	        params.add(new BasicNameValuePair("neverReview", "true"));
	        params.add(new BasicNameValuePair("indexable", "true"));	        
//	        params.add(new BasicNameValuePair("smallImage", "false"));
//	        params.add(new BasicNameValuePair("smallImageURL", ""));
//	        params.add(new BasicNameValuePair("smallFile", ""));
//	        params.add(new BasicNameValuePair("images", ""));
	        params.add(new BasicNameValuePair("articleURL", "articleURL"));
	        params.add(new BasicNameValuePair("serviceContext", "{}"));
	        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
	        post.setEntity(entity);
	        HttpResponse resp = httpclient.execute(targetHost, post, ctx);
	        System.out.println(resp.getStatusLine());
	        resp.getEntity().writeTo(System.out);
	        httpclient.getConnectionManager().shutdown();

	    }

	    public static void removeArticle() throws Exception {
	    	
	        // Create AuthCache instance
	        AuthCache authCache = new BasicAuthCache();
	        // Generate BASIC scheme object and add it to the local
	        // auth cache
	        BasicScheme basicAuth = new BasicScheme();
	        authCache.put(targetHost, basicAuth);

	        // Add AuthCache to the execution context
	        BasicHttpContext ctx = new BasicHttpContext();
	        ctx.setAttribute(ClientContext.AUTH_CACHE, authCache);

	        HttpPost post = new HttpPost("/api/jsonws/journalarticle/delete-article");
	        Calendar now = Calendar.getInstance();
	        Calendar nextWeek = Calendar.getInstance();
	        nextWeek.add(Calendar.WEEK_OF_YEAR, 1);
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("serviceClassName", "com.liferay.portlet.journal.service.JournalArticleServiceUtil"));
	        params.add(new BasicNameValuePair("serviceMethodName", "deleteArticle"));
	        params.add(new BasicNameValuePair("serviceParameterTypes", "[long,java.lang.String,java.lang.String,com.liferay.portal.service.ServiceContext]"));
	        params.add(new BasicNameValuePair("serviceParameters", "[groupId,articleId,articleURL,serviceContext]"));
	        params.add(new BasicNameValuePair("groupId", "20181"));
	        params.add(new BasicNameValuePair("articleId", "580378"));
	        params.add(new BasicNameValuePair("articleURL", "articleURL"));
	        params.add(new BasicNameValuePair("serviceContext", "{}"));

	        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
	        post.setEntity(entity);
	        HttpResponse resp = httpclient.execute(targetHost, post, ctx);
	        System.out.println(resp.getStatusLine());
	        resp.getEntity().writeTo(System.out);
	        httpclient.getConnectionManager().shutdown();
	    }
	    public static void getArticle() throws Exception {

	        // Create AuthCache instance
	        AuthCache authCache = new BasicAuthCache();
	        // Generate BASIC scheme object and add it to the local
	        // auth cache
	        BasicScheme basicAuth = new BasicScheme();
	        authCache.put(targetHost, basicAuth);

	        // Add AuthCache to the execution context
	        BasicHttpContext ctx = new BasicHttpContext();
	        ctx.setAttribute(ClientContext.AUTH_CACHE, authCache);

	        HttpPost post = new HttpPost("/api/jsonws/journalarticle/get-article");

	        // create Liferay API parameters
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("serviceClassName", "com.liferay.portlet.journal.service.JournalArticleServiceUtil"));
	        params.add(new BasicNameValuePair("serviceMethodName", "getArticle"));
//	        params.add(new BasicNameValuePair("serviceParameters", "[groupId,articleId]"));
	        params.add(new BasicNameValuePair("serviceParameters", "id"));
//	        params.add(new BasicNameValuePair("groupId", "10156"));
	        params.add(new BasicNameValuePair("id", "578378"));
	        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
	        post.setEntity(entity);

	        // make actual HTTP request and print results to System.out
	        HttpResponse resp = httpclient.execute(targetHost, post, ctx);
	        resp.getEntity().writeTo(System.out);
	        httpclient.getConnectionManager().shutdown();

	    }
	    public static void testJsonWS() throws ClientProtocolException, IOException{
	    	AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local
            // auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost, basicAuth);

            // Add AuthCache to the execution context
            BasicHttpContext ctx = new BasicHttpContext();
            ctx.setAttribute(ClientContext.AUTH_CACHE, authCache);

            HttpPost post = new HttpPost("/api/jsonws/country/get-countries");

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("serviceClassName", "com.liferay.portal.service.CountryServiceUtil"));
            params.add(new BasicNameValuePair("serviceMethodName", "getCountries"));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            post.setEntity(entity);

            HttpResponse resp = httpclient.execute(targetHost, post, ctx);
            resp.getEntity().writeTo(System.out);
            System.out.println();
            httpclient.getConnectionManager().shutdown();
	    }
        public static void main(String[] args) throws Exception {
        	initConnection();
            // Create AuthCache instance
//            testJsonWS();
//        	getArticle();
//        	addArticle();
        	removeArticle();
        }
	    
}
