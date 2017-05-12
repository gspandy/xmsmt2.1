/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.plugin.unionpay.mobile;

import java.io.Console;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import net.wit.entity.Payment;
import net.wit.entity.PluginConfig;
import net.wit.plugin.PaymentPlugin;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Plugin - 银联手机支付
 * 
 * @author rsico Team
 * @version 3.0
 */
@Component("unionpayMobilePlugin")
public class UnionpayMobilePlugin extends PaymentPlugin {

	/** 货币 */
	private static final String CURRENCY = "156";

	@Override
	public String getName() {
		return "银联手机支付";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getAuthor() {
		return "rsico";
	}

	@Override
	public String getSiteUrl() {
		return "http://www.rsico.cn";
	}

	@Override
	public String getInstallUrl() {
		return "unionpay/mobile/install.jhtml";
	}

	@Override
	public String getUninstallUrl() {
		return "unionpay/mobile/uninstall.jhtml";
	}

	@Override
	public String getSettingUrl() {
		return "unionpay/mobile/setting.jhtml";
	}

	@Override
	public String getRequestUrl() {
		return "https://mgate.unionpay.com/gateway/merchant/trade";
	}

	@Override
	public RequestMethod getRequestMethod() {
		return RequestMethod.post;
	}

	@Override
	public String getRequestCharset() {
		return "UTF-8";
	}

	@Override
	public Map<String, Object> getParameterMap(String sn, String description, HttpServletRequest request, String root) {
		PluginConfig pluginConfig = getPluginConfig();
		Payment payment = getPayment(sn);
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("backEndUrl", getNotifyUrl(sn, NotifyMethod.async,root));
		parameterMap.put("charset", "UTF-8");
		parameterMap.put("frontEndUrl", getNotifyUrl(sn, NotifyMethod.sync,root));
		parameterMap.put("merId", pluginConfig.getAttribute("partner"));
 		parameterMap.put("orderAmount", payment.getAmount().multiply(new BigDecimal(100)).setScale(0).toString());
		parameterMap.put("orderCurrency", CURRENCY);
		parameterMap.put("orderDescription", "vsstoo");
		parameterMap.put("orderNumber", sn);
		parameterMap.put("orderTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, 1);
		date=calendar.getTime(); 
		parameterMap.put("orderTimeout",new SimpleDateFormat("yyyyMMddHHmmss").format(date));
		parameterMap.put("transType", "01");
		parameterMap.put("version", "1.0.0");
		parameterMap.put("signMethod", "MD5");
		parameterMap.put("signature", generateSign(parameterMap));
		return parameterMap;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean verifyNotify(String sn, NotifyMethod notifyMethod, HttpServletRequest request) {
		PluginConfig pluginConfig = getPluginConfig();
		Payment payment = getPayment(sn);
		/**		Logger logger = Logger.getLogger("verifyNotity");  
		String info="";
		Iterator it=request.getParameterMap().entrySet().iterator();
		Map.Entry entry;
		String value="";
		while (it.hasNext()) 
		{ entry = (Map.Entry) it.next();
		String[] values = (String[])entry.getValue();
	    for(int i=0;i<values.length;i++){
	     value = values[i] + ",";
	    }
	    value = value.substring(0, value.length()-1);

			info += ","+entry.getKey()+"="+value;
		}
		logger.warn(info);*/
		if (generateSign(request.getParameterMap()).equals(request.getParameter("signature")) && pluginConfig.getAttribute("partner").equals(request.getParameter("merId")) && sn.equals(request.getParameter("orderNumber")) && "00".equals(request.getParameter("transStatus")) 
				&& payment.getAmount().multiply(new BigDecimal(100)).compareTo(new BigDecimal(request.getParameter("settleAmount"))) == 0) {
			/*Map<String, Object> parameterMap = new HashMap<String, Object>();
			parameterMap.put("version", "1.0.0");
			parameterMap.put("charset", "UTF-8");
			parameterMap.put("transType", "01");
			parameterMap.put("merId", pluginConfig.getAttribute("partner"));
			parameterMap.put("orderNumber", sn);
			parameterMap.put("orderTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			parameterMap.put("signMethod", "MD5");
			parameterMap.put("signature", generateSign(parameterMap));
			//logger.warn(parameterMap.toString());
			String result = post("http://222.66.233.198:8080/gateway/merchant/query", parameterMap);
			//logger.warn(result);
			if (ArrayUtils.contains(result.split("&"), "respCode=00")) {
				return true;
			}*/
			return true;
		}
		return false;
	}

	@Override
	public String getNotifyMessage(String sn, NotifyMethod notifyMethod, HttpServletRequest request) {
		return null;
	}

	@Override
	public Integer getTimeout() {
		return 21600;
	}

 	/**
	 * 生成签名
	 * 
	 * @param parameterMap
	 *            参数
	 * @return 签名
	 */
	public String generateSign(Map<String, ?> parameterMap) {
		PluginConfig pluginConfig = getPluginConfig();
        return DigestUtils.md5Hex(joinKeyValue(new TreeMap<String, Object>(parameterMap), null,"&"+DigestUtils.md5Hex(pluginConfig.getAttribute("key")), "&", true, "signMethod", "signature"));
	}

}