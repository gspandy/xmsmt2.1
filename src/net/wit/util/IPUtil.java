package net.wit.util;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.util.Assert;

/**
 * 判断IP地址所属城市
 */
public class IPUtil {

	public static String getIPInfo(String ip) {
		Assert.hasText(ip);
		String result = null;
		@SuppressWarnings("resource")
		HttpClient httpClient = new DefaultHttpClient();
		try {
			HttpGet httpGet = new HttpGet("http://ip.taobao.com/service/getIpInfo.php?ip=" + ip);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			String json = httpResponse.toString();
			JSONObject jsonObject = JSONObject.fromObject(json);
			JSONObject jsonObject1 = JSONObject.fromObject(jsonObject.getString("data"));
			result = jsonObject1.getString("city_id");
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
