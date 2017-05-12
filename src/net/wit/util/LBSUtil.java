package net.wit.util;

import java.io.IOException;
import java.math.BigDecimal;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 判断IP地址所属城市
 */
public class LBSUtil {
	
	public static String getAreaFromLocation(BigDecimal lat,BigDecimal lng) {
		String result = null;
		@SuppressWarnings("resource")
		HttpClient httpClient = new DefaultHttpClient();
		try {
			HttpGet httpGet = new HttpGet("http://api.map.baidu.com/geocoder?location="+lat.toString()+","+lng.toString()+"&output=xml&key=28bcdd84fae25699606ffad27f8da77b" );
			HttpResponse httpResponse = httpClient.execute(httpGet);
			String json = httpResponse.toString();
			JSONObject jsonObject = JSONObject.fromObject(json);
			if (jsonObject.getString("status").equals("OK")) {
			   JSONObject jsonObject1 = JSONObject.fromObject(jsonObject.getString("result"));
			   JSONObject jsonObject2 = JSONObject.fromObject(jsonObject.getString("addressComponent"));
			   String city = jsonObject2.getString("city");
			   String province = jsonObject2.getString("province");
			   result = province+city;
		    } 
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}
	

}
