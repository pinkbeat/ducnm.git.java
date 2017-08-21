package com.cdit.test.portal;

import org.json.JSONObject;
import com.vsc.util.WsUtil;

public class callJsWS {
	public static void main(String[] asd) throws Exception {
		JSONObject object = new JSONObject();
		object.put("tendangnhap", "admin");
		object.put("matkhau", "4b80fc873657d01d6e0e06b3e282926b");
		object.put("ma_dk_online", "ABC0011");		
		object = WsUtil.doSendJSWS("http://123.31.25.15:9080/portal-servciepublish-portlet/services/kiemtraMa_BN_KCB_OL",object);
		System.out.println(object.get("thong_diep"));
	}
}
