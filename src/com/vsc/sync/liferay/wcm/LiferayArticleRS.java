package com.vsc.sync.liferay.wcm;

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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.poi.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.vsc.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class LiferayArticleRS {
	static HttpHost targetHost;
	static DefaultHttpClient httpclient;
	static String logfile = "e:\\log_create_article_js.log";
	static String errorlogfile = "e:\\error_create_article_js.log";
	static String contentfile = "E:\\jobs\\docs\\2017\\skype\\convertDB(1).xlsx";
	static String errorcontentfile = "E:\\error_convertDB(1).xlsx";
	static String jswsurl = "/api/jsonws/journalarticle/add-article";
	static String ext = "xlsx";
	public static String[] mapVNPArticle = {"NEWS_CAT_ID","VN_TITLE","PREVIEW_IMG_URL", "VN_CONTENT", "VN_PREVIEW", "TYPE", "STRUCTUREID","TEMPLATEID", "LAYOUTUUID", "FOLDERID","CATEGORYID","GROUPID"};
	
	private static void initConnection() {
		// targetHost = new HttpHost("123.31.40.162", 10080, "http");
//		targetHost = new HttpHost("14.225.6.70", 80, "http");
		targetHost = new HttpHost("123.31.40.181", 80, "http");
		httpclient = new DefaultHttpClient();
		httpclient.getCredentialsProvider().setCredentials(
				new AuthScope(targetHost.getHostName(), targetHost.getPort()),
				new UsernamePasswordCredentials("trungnv@vnpt.vn", "123456a@"));
	}
	//
	public static JSONObject doSendJSWS(List<NameValuePair> params, String url) throws Exception {
		initConnection();
		AuthCache authCache = new BasicAuthCache();
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(targetHost, basicAuth);
		BasicHttpContext ctx = new BasicHttpContext();
		ctx.setAttribute(ClientContext.AUTH_CACHE, authCache);
		HttpPost post = new HttpPost(url);
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
		post.setEntity(entity);
		HttpResponse resp = httpclient.execute(targetHost, post, ctx);
//		System.out.println(resp.getStatusLine());
		String json = new String(IOUtils.toByteArray(resp.getEntity().getContent()), "UTF-8");
		JSONObject object=new JSONObject(json);
		httpclient.getConnectionManager().shutdown();
		return object;
	}
	private static List<ArticleDTO> sendJSWS(List<ArticleDTO> list, LiferayArticleUtil articleUtil){
		List<ArticleDTO> newlist = new ArrayList<>();
		List<ArticleDTO> errorlist = new ArrayList<>();
		JSONObject jsonObject = null;
		ArticleDTO dto = null;
		if (list != null) {			
			for (int i = 0; i < list.size(); i++) {
				initConnection();
				dto = list.get(i);				
				try {
					jsonObject = doSendJSWS(articleUtil.buildArticleMessage(dto),jswsurl);
					dto.setArticleId(jsonObject.getString("articleId"));
					newlist.add(dto);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					errorlist.add(dto);
				}							
			}
		}
		if(errorlist.size()!=0){
			System.out.println("Error: "+errorlist.size());
			articleUtil.writeJSonLogError(errorlist, errorlogfile);
		}
		return newlist;
	}
	
	public static void removeArticle(String groupid,String articleid) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("serviceClassName",
				"com.liferay.portlet.journal.service.JournalArticleServiceUtil"));
		params.add(new BasicNameValuePair("serviceMethodName", "deleteArticle"));
		params.add(new BasicNameValuePair("serviceParameterTypes",
				"[long,java.lang.String,java.lang.String,com.liferay.portal.service.ServiceContext]"));
		params.add(new BasicNameValuePair("serviceParameters", "[groupId,articleId,articleURL,serviceContext]"));
		params.add(new BasicNameValuePair("groupId", groupid));
		params.add(new BasicNameValuePair("articleId", articleid));
		params.add(new BasicNameValuePair("articleURL", "articleURL"));
		params.add(new BasicNameValuePair("serviceContext", "{}"));
		doSendJSWS(params, "/api/jsonws/journalarticle/delete-article");
	}
	public static void getArticle(String articleId) throws Exception {
		// create Liferay API parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("serviceClassName",
				"com.liferay.portlet.journal.service.JournalArticleServiceUtil"));
		params.add(new BasicNameValuePair("serviceMethodName", "getArticle"));
		// params.add(new BasicNameValuePair("serviceParameters",
		// "[groupId,articleId]"));
		params.add(new BasicNameValuePair("serviceParameters", "id"));
		// params.add(new BasicNameValuePair("groupId", "10156"));
		params.add(new BasicNameValuePair("id", articleId));
		doSendJSWS(params, "/api/jsonws/journalarticle/get-article");
	}
	public static void getCategory(String cateId) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("serviceClassName","com.liferay.portlet.asset.service.impl.AssetCategoryServiceImpl"));
		params.add(new BasicNameValuePair("serviceMethodName","getCategory"));
		params.add(new BasicNameValuePair("serviceParameters","categoryId"));
		params.add(new BasicNameValuePair("categoryId", cateId));
		doSendJSWS(params, "/api/jsonws/assetcategory/get-category");
	}
	public static void testJsonWS() throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("serviceClassName", "com.liferay.portal.service.CountryServiceUtil"));
		params.add(new BasicNameValuePair("serviceMethodName", "getCountries"));
		doSendJSWS(params, "/api/jsonws/country/get-countries");
	}
	
	public static void createArticleViaJSWS() throws Exception {
		LiferayArticleUtil articleUtil = new LiferayArticleUtil();
		int r=0;
		// get content
		List<ArticleDTO> list = ExcelHelper.extractContent(contentfile, mapVNPArticle, ext);
		System.out.println("Getted: "+list.size());
		// send to webservice
		List<ArticleDTO> newlist = sendJSWS(list,articleUtil);
		System.out.println("Sent: "+newlist.size());
		// send to the back door
		System.out.println("Inserting: "+newlist.size());
		System.out.println(articleUtil.insertLinkArticleWithCategory(newlist)==1?"success":"failure");		
		// write to log
		System.out.println("Logging: "+newlist.size());
		System.out.println(articleUtil.writeJSonLog(newlist,logfile)==1?"success":"failure");		
	}
	
	public static void deleteArticleViaJSWS(){		
		String fileTmp = FileUtil.readFile(new File(logfile));
		JSONArray array = new JSONArray(fileTmp);
		JSONObject tmp;
		StringBuilder rangeArticle=new StringBuilder();
		int size = array.length();
		try{
			for(int i=0;i<size;i++){
				tmp=array.getJSONObject(i);
				removeArticle(tmp.getString("groupId"), tmp.getString("articleId")+"");
				if(i==size-1) rangeArticle.append(tmp.getString("articleId")); 
				else rangeArticle.append(tmp.getString("articleId")).append(",");
				System.out.println("deleted: "+tmp.getString("articleId"));	
			}
//			System.out.println(rangeArticle);
			LiferayArticleUtil articleUtil = new LiferayArticleUtil();
			articleUtil.deleteLinkArticleWithCategory(rangeArticle.toString());
			System.out.println("delete total: " +array.length());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void unlinkArticleWithCategory(){		
		String fileTmp = FileUtil.readFile(new File(logfile));
		JSONArray array = new JSONArray(fileTmp);
		JSONObject tmp;
		StringBuilder rangeArticle=new StringBuilder();
		int size = array.length();
		try{
			for(int i=0;i<size;i++){
				tmp=array.getJSONObject(i);				
				if(i==size-1) rangeArticle.append(tmp.getString("articleId")); 
				else rangeArticle.append(tmp.getString("articleId")).append(",");
			}
			System.out.println(rangeArticle);
			LiferayArticleUtil articleUtil = new LiferayArticleUtil();
			articleUtil.deleteLinkArticleWithCategory(rangeArticle.toString());
			System.out.println("Unlinked : " +array.length());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void linkArticleWithCategory(){		
		String fileTmp = FileUtil.readFile(new File(logfile));
		JSONArray array = new JSONArray(fileTmp);
		JSONObject tmp;
		int size = array.length();
		ArticleDTO articleDTO=null;
		List<ArticleDTO> list = new ArrayList<>();
		try{
			for(int i=0;i<size;i++){
				tmp=array.getJSONObject(i);
				articleDTO = new ArticleDTO();
				articleDTO.setArticleId(tmp.getString("articleId"));
				articleDTO.setCat_id(tmp.getString("cateId"));
				list.add(articleDTO);				
			}
			System.out.println("Added: "+list.size());
			LiferayArticleUtil articleUtil = new LiferayArticleUtil();
			articleUtil.insertLinkArticleWithCategory(list);
			System.out.println("Linked: " +list.size());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
//		ExcelUtil.generateErrorFile(errorlogfile, contentfile, errorcontentfile, ext);
		createArticleViaJSWS();
//		unlinkArticleWithCategory();
//		linkArticleWithCategory();
//		deleteArticleViaJSWS();
		
	
		//for testing
		// initConnection();
		// addArticle(LiferayArticleUtil.buildArticleMessage(new
		// ExcelUtil().mapExampleContent()));
		// testJsonWS();
		// getArticle();
		// addArticle();
		// addEntry();
		// removeArticle();
	}

}
