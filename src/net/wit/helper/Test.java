package net.wit.helper;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.wit.Setting;
import net.wit.entity.Shipping;
import net.wit.util.SettingUtils;

/**
 * @ClassName：Test @Description：
 * @author：Chenlf
 * @date：2015年9月12日 下午4:55:51
 */
public class Test {
	
	@org.junit.Test
	public void t1(Shipping shipping) {
		Setting setting = SettingUtils.get();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			URL url = new URL("http://api.kuaidi100.com/api?id=" + setting.getKuaidi100Key() + "&com=" + shipping.getDeliveryCorpCode() + "&nu=" + shipping.getTrackingNo() + "&show=0&muti=1&order=desc");
			data = mapper.readValue(url, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
