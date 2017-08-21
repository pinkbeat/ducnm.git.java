package com.vsc.sync.liferay.wcm;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.poi.ddf.EscherColorRef.SysIndexSource;
import org.json.JSONArray;
import org.json.JSONObject;


public class LiferayArticleUtil {
	static String articleTemplate1 = "<root available-locales=\"vi_VN\" default-locale=\"vi_VN\">"
			+ "<dynamic-element name=\"mo_ta\" type=\"text_box\" index-type=\"keyword\" index=\"0\">"
			+ "<dynamic-content language-id=\"vi_VN\"><![CDATA[{0}]]></dynamic-content></dynamic-element>"
			+ "<dynamic-element name=\"image\" type=\"image\" index-type=\"keyword\" index=\"0\">"
			+ "<dynamic-element name=\"imagelink\" index=\"1\" type=\"text\" index-type=\"keyword\">"
			+ "<dynamic-content language-id=\"vi_VN\"><![CDATA[{1}]]></dynamic-content>"
			+ "</dynamic-element></dynamic-element>"
			+ "<dynamic-element name=\"noi_dung\" type=\"text_area\" index-type=\"keyword\" index=\"0\">"
			+ "<dynamic-content language-id=\"vi_VN\"><![CDATA[{2}]]></dynamic-content></dynamic-element></root>";
	StringBuilder builder;
	Map<String, String> contentMap;
	Connection connection = null;
	CallableStatement cstmt = null;
	List<NameValuePair> params = null;
	
	int page = 500;
	
	public LiferayArticleUtil(){
		
	}
	
	private Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection("jdbc:oracle:thin:@10.252.102.6:1521/pdb3", "vnptweb", "vnptweb");
			System.out.println("get connection db successfully");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}
	public void deleteLinkArticleWithCategory(String listArticle) {
		try {
			if(connection == null) connection = getConnection();
			long time = System.currentTimeMillis();
			String call = "{ ? = call unlinkArticle_Category(?) }";
			cstmt = connection.prepareCall(call);
			cstmt.setQueryTimeout(1800);
			cstmt.registerOutParameter(1, oracle.jdbc.OracleTypes.NUMBER);			
			cstmt.setString(2, listArticle);
			int i = cstmt.executeUpdate();
			System.out.println(i);
			System.out.println("ESTIMATED TIME: " + (System.currentTimeMillis() - time));
			cstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					cstmt.close();
					connection.close();
					connection = null;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public void insertLinkArticleWithCategory(String articleid, String categoryId) {
		try {
			connection = getConnection();
			long time = System.currentTimeMillis();
			String call = "{ ? = call linkArticle_Category(?,?) }";
			cstmt = connection.prepareCall(call);
			cstmt.registerOutParameter(1, oracle.jdbc.OracleTypes.NUMBER);			
			cstmt.setString(2, articleid);
			cstmt.setString(3, categoryId);
			int i = cstmt.executeUpdate();
			System.out.println(i);
			System.out.println("ESTIMATED TIME: " + (System.currentTimeMillis() - time));
			cstmt.close();
			System.out.println("Success");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					cstmt.close();
					connection.close();
					connection = null;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public int insertLinkArticleWithCategory(List<ArticleDTO> list) {
		if(list==null || list.size()==0) return 0;
		List<ArticleDTO> sublist=null;			
		int k,i=0;
		ArticleDTO dto = null;
		try {
			if(connection==null) connection = getConnection();
			long time = System.currentTimeMillis();
			String call = "{ ? = call linkArticle_Category(?,?) }";
			int lsize= list.size();			
			k=page;
			cstmt = connection.prepareCall(call);
			cstmt.registerOutParameter(1, oracle.jdbc.OracleTypes.NUMBER);
			do{
				if(k>lsize) sublist=list;
				else if(k<=lsize && (k+page)<=lsize) sublist=list.subList(i, k);
				else sublist=list.subList(i, lsize);				
				for(int j=0;j<sublist.size();j++){
					dto = sublist.get(j);										
					cstmt.setString(2, dto.getArticleId());
					cstmt.setString(3, dto.getCat_id());
					cstmt.executeUpdate();
				}
				i=k;
				k+=page;
			}while(k<=lsize);
//			if(lsize-i>0){
//				sublist=list.subList(i, lsize);
//				for(int j=0;j<sublist.size();j++){
//					dto = sublist.get(j);										
//					cstmt.setString(2, dto.getArticleId());
//					cstmt.setString(3, dto.getCat_id());
//					cstmt.executeUpdate();
//				}
//			}
			System.out.println("ESTIMATED TIME: " + (System.currentTimeMillis() - time));			
			cstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {			
			sublist=null;
			if (connection != null) {
				try {
					cstmt.close();
					connection.close();
					connection = null;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return 1;
	}
	public int writeLog(List<ArticleDTO> list, String logfile){
//		Path out = Paths.get("e:\\log_create_article.log");
//		Files.write(out,list,Charset.defaultCharset());		
        int size = list.size();
        String tmp;
        try(
        	FileWriter writer = new FileWriter(logfile);
        ){
	        for (int i=0;i<size;i++) {
	        	tmp = list.get(i).getArticleId();
	            writer.write(tmp);
	            if(i < size-1)//This prevent creating a blank like at the end of the file**
	                writer.write("\n");
	        }
        }catch(IOException e){
        	e.printStackTrace();
        	return -1;
        }
        return 1;
	}
	public int writeJSonLog(List<ArticleDTO> list,String logfile){
//		Path out = Paths.get("e:\\log_create_article.log");
//		Files.write(out,list,Charset.defaultCharset());		
        int size = list.size();
        JSONArray array = new JSONArray();
        JSONObject object = null;
        try(
        	FileWriter writer = new FileWriter(logfile);
        ){
	        for (int i=0;i<size;i++) {
	        	object = new JSONObject("{articleId:\""+list.get(i).getArticleId()+"\",cateId:\""+list.get(i).getCat_id()+"\",groupId:\""+list.get(i).getGroupId()+"\"}");
	        	array.put(object);
	        }
	        writer.write(array.toString());
            writer.write("\n");
        }catch(IOException e){
        	e.printStackTrace();
        	return -1;
        }
        return 1;
	}
	public int writeLogError(List<ArticleDTO> list, String errorlogfile){
        int size = list.size();
        String tmp;
        try(
        	FileWriter writer = new FileWriter(errorlogfile);
        ){
	        for (int i=0;i<size;i++) {
	        	tmp = list.get(i).getTitleMap();
	            writer.write(tmp);
	            if(i < size-1)//This prevent creating a blank like at the end of the file**
	                writer.write("\n");
	        }
        }catch(IOException e){
        	e.printStackTrace();
        	return -1;
        }
        return 1;
	}
	public int writeJSonLogError(List<ArticleDTO> list, String errorlogfile){
        int size = list.size();
        JSONArray array = new JSONArray();
        JSONObject object = null;
        try(
        	FileWriter writer = new FileWriter(errorlogfile);
        ){
	        for (int i=0;i<size;i++) {
	        	object = new JSONObject("{rownum:\""+list.get(i).getRownum()+"\",groupId:\""+list.get(i).getGroupId()+"\"}");
	        	array.put(object);
	        }
	        writer.write(array.toString());
            writer.write("\n");
        }catch(Exception e){
        	e.printStackTrace();
        	return -1;
        }
        return 1;
	}
	public List<NameValuePair> buildArticleMessage(ArticleDTO dto) {
		params = new ArrayList<NameValuePair>();
		contentMap = new HashMap<String, String>();
		contentMap.put("{0}", dto.getMota());
		contentMap.put("{1}", dto.getImages());
		contentMap.put("{2}", dto.getContent());
		// params.add(new BasicNameValuePair("serviceClassName",
		// "com.liferay.portlet.journal.service.JournalArticleServiceUtil"));
		params.add(new BasicNameValuePair("serviceClassName", dto.getServiceClassName()));
		params.add(new BasicNameValuePair("serviceMethodName", dto.getServiceMethodName()));
		params.add(new BasicNameValuePair("serviceParameters", dto.getServiceParameters()));
		params.add(new BasicNameValuePair("serviceParameterTypes", dto.getServiceParameterTypes()));
		params.add(new BasicNameValuePair("groupId", dto.getGroupId()));
		params.add(new BasicNameValuePair("folderId", dto.getFolderId()));
		params.add(new BasicNameValuePair("classNameId", dto.getClassNameId()));
		params.add(new BasicNameValuePair("classPK", dto.getClassPK()));
		params.add(new BasicNameValuePair("articleId", dto.getArticleId()));
		params.add(new BasicNameValuePair("autoArticleId", dto.getAutoArticleId()));
		params.add(new BasicNameValuePair("titleMap", buildJsonObject("vi_VN", dto.getTitleMap())));
		params.add(new BasicNameValuePair("descriptionMap", buildJsonObject("vi_VN", dto.getDescriptionMap())));
		params.add(new BasicNameValuePair("content", buildArticle(contentMap)));
		params.add(new BasicNameValuePair("type", dto.getType()));
		params.add(new BasicNameValuePair("ddmStructureKey", dto.getDdmStructureKey()));
		params.add(new BasicNameValuePair("ddmTemplateKey", dto.getDdmTemplateKey()));
		params.add(new BasicNameValuePair("layoutUuid", dto.getLayoutUuid()));
		params.add(new BasicNameValuePair("displayDateMonth", "" + dto.getDisplayDateMonth()));
		params.add(new BasicNameValuePair("displayDateDay", "" + dto.getDisplayDateDay()));
		params.add(new BasicNameValuePair("displayDateYear", "" + dto.getDisplayDateYear()));
		params.add(new BasicNameValuePair("displayDateHour", "" + dto.getDisplayDateHour()));
		params.add(new BasicNameValuePair("displayDateMinute", "" + dto.getDisplayDateMinute()));
		params.add(new BasicNameValuePair("expirationDateMonth", "" + dto.getExpirationDateMonth()));
		params.add(new BasicNameValuePair("expirationDateDay", "" + dto.getExpirationDateDay()));
		params.add(new BasicNameValuePair("expirationDateYear", "" + dto.getExpirationDateYear()));
		params.add(new BasicNameValuePair("expirationDateHour", "" + dto.getExpirationDateHour()));
		params.add(new BasicNameValuePair("expirationDateMinute", "" + dto.getExpirationDateMinute()));
		params.add(new BasicNameValuePair("neverExpire", dto.getNeverExpire()));
		params.add(new BasicNameValuePair("reviewDateMonth", "" + dto.getReviewDateMonth()));
		params.add(new BasicNameValuePair("reviewDateDay", "" + dto.getReviewDateDay()));
		params.add(new BasicNameValuePair("reviewDateYear", "" + dto.getReviewDateYear()));
		params.add(new BasicNameValuePair("reviewDateHour", "" + dto.getReviewDateHour()));
		params.add(new BasicNameValuePair("reviewDateMinute", "" + dto.getReviewDateMinute()));
		params.add(new BasicNameValuePair("neverReview", dto.getNeverExpire()));
		params.add(new BasicNameValuePair("indexable", dto.getIndexable()));
		// params.add(new BasicNameValuePair("smallImage", "false"));
		// params.add(new BasicNameValuePair("smallImageURL", ""));
		// params.add(new BasicNameValuePair("smallFile", ""));
		// params.add(new BasicNameValuePair("images", ""));
		params.add(new BasicNameValuePair("articleURL", dto.getArticleURL()));
		params.add(new BasicNameValuePair("serviceContext", dto.getServiceContext()));
		return params;
	}

	private String buildArticle(Map<String, String> mapContent) {
		builder = new StringBuilder(articleTemplate1);
		String tmpKey, tmpContent;
		int i = -1;
		Set<String> keysit = mapContent.keySet();
		Iterator<String> keys = keysit.iterator();
		while (keys.hasNext()) {
			tmpKey = (String) keys.next();
			i = builder.indexOf(tmpKey);
			if (i > -1) {
				tmpContent = (String) mapContent.get(tmpKey);
				if (tmpContent == null)
					tmpContent = "";
				builder = builder.replace(i, i + tmpKey.length(), tmpContent);
			}
		}
		// System.out.println(builder.toString());
		return builder.toString();
	}

	private String buildJsonObject(String locale, String content) {
		return "{\"" + locale + "\":\"" + content + "\"}";
	}
	public static void main(String[] ass) throws SQLException{
		LiferayArticleUtil articleUtil = new LiferayArticleUtil();
		articleUtil.getConnection().close();
	}
}

class ArticleDTO {
	// configuration properties
	String serviceClassName = "com.liferay.portlet.journal.service.impl.JournalArticleServiceImpl";
	String serviceMethodName = "addArticle";
	String serviceParameters = "[groupId,folderId,classNameId,classPK,articleId,autoArticleId,titleMap,descriptionMap,content,type,ddmStructureKey,ddmTemplateKey,layoutUuid,displayDateMonth,displayDateDay,displayDateYear,displayDateHour,displayDateMinute,expirationDateMonth,expirationDateDay,expirationDateYear,expirationDateHour,expirationDateMinute,neverExpire,reviewDateMonth,reviewDateDay,reviewDateYear,reviewDateHour,reviewDateMinute,neverReview,indexable,smallImage,smallImageURL,smallFile,images,articleURL,serviceContext]";
	String serviceParameterTypes = "[long,long,long,long,java.lang.String,boolean,java.util.Map,java.util.Map,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,int,int,int,int,int,int,int,int,int,int,boolean,int,int,int,int,int,boolean,boolean,boolean,java.lang.String,java.io.File,java.util.Map,java.lang.String,com.liferay.portal.service.ServiceContext]";

	// content properties
	String groupId;
	String folderId;
	String classNameId;
	String classPK;
	String articleId;
	String autoArticleId;
	String titleMap;
	String descriptionMap;
	String content;
	String type;
	String ddmStructureKey;
	String ddmTemplateKey;
	String layoutUuid;
	int displayDateMonth;
	int displayDateDay;
	int displayDateYear;
	int displayDateHour;
	int displayDateMinute;
	int expirationDateMonth;
	int expirationDateDay;
	int expirationDateYear;
	int expirationDateHour;
	int expirationDateMinute;
	String neverExpire;
	int reviewDateMonth;
	int reviewDateDay;
	int reviewDateYear;
	int reviewDateHour;
	int reviewDateMinute;
	String neverReview;
	String indexable;
	String smallImage;
	String smallImageURL;
	String smallFile;
	String images;
	String articleURL;
	String serviceContext;

	// extened properties
	String cat_id;
	String mota;
	String imgurl;
	int rownum;

	
	public int getRownum() {
		return rownum;
	}

	public void setRownum(int rownum) {
		this.rownum = rownum;
	}

	public String getCat_id() {
		return cat_id;
	}

	public void setCat_id(String cat_id) {
		this.cat_id = cat_id;
	}

	public String getServiceClassName() {
		return serviceClassName;
	}

	public void setServiceClassName(String serviceClassName) {
		this.serviceClassName = serviceClassName;
	}

	public String getServiceMethodName() {
		return serviceMethodName;
	}

	public void setServiceMethodName(String serviceMethodName) {
		this.serviceMethodName = serviceMethodName;
	}

	public String getServiceParameters() {
		return serviceParameters;
	}

	public void setServiceParameters(String serviceParameters) {
		this.serviceParameters = serviceParameters;
	}

	public String getServiceParameterTypes() {
		return serviceParameterTypes;
	}

	public void setServiceParameterTypes(String serviceParameterTypes) {
		this.serviceParameterTypes = serviceParameterTypes;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getFolderId() {
		return folderId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public String getClassNameId() {
		return classNameId;
	}

	public void setClassNameId(String classNameId) {
		this.classNameId = classNameId;
	}

	public String getClassPK() {
		return classPK;
	}

	public void setClassPK(String classPK) {
		this.classPK = classPK;
	}

	public String getArticleId() {
		return articleId;
	}

	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}

	public String getAutoArticleId() {
		return autoArticleId;
	}

	public void setAutoArticleId(String autoArticleId) {
		this.autoArticleId = autoArticleId;
	}

	public String getTitleMap() {
		return titleMap;
	}

	public void setTitleMap(String titleMap) {
		this.titleMap = titleMap;
	}

	public String getDescriptionMap() {
		return descriptionMap;
	}

	public void setDescriptionMap(String descriptionMap) {
		this.descriptionMap = descriptionMap;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDdmStructureKey() {
		return ddmStructureKey;
	}

	public void setDdmStructureKey(String ddmStructureKey) {
		this.ddmStructureKey = ddmStructureKey;
	}

	public String getDdmTemplateKey() {
		return ddmTemplateKey;
	}

	public void setDdmTemplateKey(String ddmTemplateKey) {
		this.ddmTemplateKey = ddmTemplateKey;
	}

	public String getLayoutUuid() {
		return layoutUuid;
	}

	public void setLayoutUuid(String layoutUuid) {
		this.layoutUuid = layoutUuid;
	}

	public String getNeverExpire() {
		return neverExpire;
	}

	public void setNeverExpire(String neverExpire) {
		this.neverExpire = neverExpire;
	}

	public String getNeverReview() {
		return neverReview;
	}

	public void setNeverReview(String neverReview) {
		this.neverReview = neverReview;
	}

	public String getIndexable() {
		return indexable;
	}

	public void setIndexable(String indexable) {
		this.indexable = indexable;
	}

	public String getSmallImage() {
		return smallImage;
	}

	public void setSmallImage(String smallImage) {
		this.smallImage = smallImage;
	}

	public String getSmallImageURL() {
		return smallImageURL;
	}

	public void setSmallImageURL(String smallImageURL) {
		this.smallImageURL = smallImageURL;
	}

	public String getSmallFile() {
		return smallFile;
	}

	public void setSmallFile(String smallFile) {
		this.smallFile = smallFile;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public String getArticleURL() {
		return articleURL;
	}

	public void setArticleURL(String articleURL) {
		this.articleURL = articleURL;
	}

	public String getServiceContext() {
		return serviceContext;
	}

	public void setServiceContext(String serviceContext) {
		this.serviceContext = serviceContext;
	}

	public String getMota() {
		return mota;
	}

	public void setMota(String mota) {
		this.mota = mota;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public int getDisplayDateMonth() {
		return displayDateMonth;
	}

	public void setDisplayDateMonth(int displayDateMonth) {
		this.displayDateMonth = displayDateMonth;
	}

	public int getDisplayDateDay() {
		return displayDateDay;
	}

	public void setDisplayDateDay(int displayDateDay) {
		this.displayDateDay = displayDateDay;
	}

	public int getDisplayDateYear() {
		return displayDateYear;
	}

	public void setDisplayDateYear(int displayDateYear) {
		this.displayDateYear = displayDateYear;
	}

	public int getDisplayDateHour() {
		return displayDateHour;
	}

	public void setDisplayDateHour(int displayDateHour) {
		this.displayDateHour = displayDateHour;
	}

	public int getDisplayDateMinute() {
		return displayDateMinute;
	}

	public void setDisplayDateMinute(int displayDateMinute) {
		this.displayDateMinute = displayDateMinute;
	}

	public int getExpirationDateMonth() {
		return expirationDateMonth;
	}

	public void setExpirationDateMonth(int expirationDateMonth) {
		this.expirationDateMonth = expirationDateMonth;
	}

	public int getExpirationDateDay() {
		return expirationDateDay;
	}

	public void setExpirationDateDay(int expirationDateDay) {
		this.expirationDateDay = expirationDateDay;
	}

	public int getExpirationDateYear() {
		return expirationDateYear;
	}

	public void setExpirationDateYear(int expirationDateYear) {
		this.expirationDateYear = expirationDateYear;
	}

	public int getExpirationDateHour() {
		return expirationDateHour;
	}

	public void setExpirationDateHour(int expirationDateHour) {
		this.expirationDateHour = expirationDateHour;
	}

	public int getExpirationDateMinute() {
		return expirationDateMinute;
	}

	public void setExpirationDateMinute(int expirationDateMinute) {
		this.expirationDateMinute = expirationDateMinute;
	}

	public int getReviewDateMonth() {
		return reviewDateMonth;
	}

	public void setReviewDateMonth(int reviewDateMonth) {
		this.reviewDateMonth = reviewDateMonth;
	}

	public int getReviewDateDay() {
		return reviewDateDay;
	}

	public void setReviewDateDay(int reviewDateDay) {
		this.reviewDateDay = reviewDateDay;
	}

	public int getReviewDateYear() {
		return reviewDateYear;
	}

	public void setReviewDateYear(int reviewDateYear) {
		this.reviewDateYear = reviewDateYear;
	}

	public int getReviewDateHour() {
		return reviewDateHour;
	}

	public void setReviewDateHour(int reviewDateHour) {
		this.reviewDateHour = reviewDateHour;
	}

	public int getReviewDateMinute() {
		return reviewDateMinute;
	}

	public void setReviewDateMinute(int reviewDateMinute) {
		this.reviewDateMinute = reviewDateMinute;
	}
}
