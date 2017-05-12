/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.plugin.chinapay.mobile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import net.wit.Setting;
import net.wit.entity.Payment;
import net.wit.entity.PluginConfig;
import net.wit.plugin.PaymentPlugin;
import net.wit.plugin.PaymentPlugin.NotifyMethod;
import net.wit.util.SettingUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

/**
 * Plugin - 银联手机支付
 * 
 * @author rsico Team
 * @version 3.0
 */
@Component("chinapayMobilePlugin")
public class ChinapayMobilePlugin extends PaymentPlugin {

	/** 货币 */
	private static final String CURRENCY = "156";

	@Override
	public String getName() {
		return "ChinaPayMobile";
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
		return "chinapay/mobile/install.jhtml";
	}

	@Override
	public String getUninstallUrl() {
		return "chinapay/mobile/uninstall.jhtml";
	}

	@Override
	public String getSettingUrl() {
		return "chinapay/mobile/setting.jhtml";
	}

	@Override
	public String getRequestUrl() {
//		return "http://121.199.29.26:9980/qyapi/trans/getTn";
		return "http://www.sinoqy.com:8080/qyapi/trans/getTn";
	}

	@Override
	public RequestMethod getRequestMethod() {
		return RequestMethod.get;
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
		parameterMap.put("merchantNO", pluginConfig.getAttribute("partner"));
		parameterMap.put("orderNO", sn);
 		parameterMap.put("orderAmount", payment.getAmount().multiply(new BigDecimal(100)).setScale(0).toString());
		parameterMap.put("backUrl", getNotifyUrl(sn, NotifyMethod.async,root));
		parameterMap.put("sign",DigestUtils.md5Hex(pluginConfig.getAttribute("partner")+"&"+sn+"&"+payment.getAmount().multiply(new BigDecimal(100)).setScale(0).toString()+"&"+getNotifyUrl(sn, NotifyMethod.async,root)+"&"+pluginConfig.getAttribute("key")));
		return parameterMap;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean verifyNotify(String sn, NotifyMethod notifyMethod, HttpServletRequest request) {
		PluginConfig pluginConfig = getPluginConfig();
		Payment payment = getPayment(sn);
		String resp = request.getParameter("resp");
		try {
			System.out.println(resp);
		    Document document = DocumentHelper.parseText(resp);   
		    
		    Element root = document.getRootElement();
		    String qyOrderNO = ((Element)root.selectNodes("qyOrderNO").get(0)).getText();
		    String orderAmount = ((Element)root.selectNodes("orderAmount").get(0)).getText();
		    String orderNO = ((Element)root.selectNodes("orderNO").get(0)).getText();
		    String qyResultCode = ((Element)root.selectNodes("qyResultCode").get(0)).getText();
		    String sign = ((Element)root.selectNodes("sign").get(0)).getText();
		    
		    String mysign = DigestUtils.md5Hex(pluginConfig.getAttribute("partner")+"&"+qyOrderNO+"&"+pluginConfig.getAttribute("key")+"&"+qyResultCode);
		    
			if (mysign.equals(sign) && sn.equals(orderNO) && "000000000".equals(qyResultCode) && payment.getAmount().multiply(new BigDecimal(100)).compareTo(new BigDecimal(orderAmount)) == 0) {
				return true;
			}

		} catch (Exception e) {
	    	return false;
        }
		return false;
	}

	@Override
	public String getNotifyMessage(String sn, NotifyMethod notifyMethod, HttpServletRequest request) {
		return "0000";
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
        return DigestUtils.md5Hex(joinValue(new TreeMap<String, Object>(parameterMap), null,"&"+pluginConfig.getAttribute("key"), "&", true, "sign"));
	}

}