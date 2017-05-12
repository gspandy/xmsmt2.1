/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.plugin.unionpay;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import net.wit.Setting;
import net.wit.entity.Payment;
import net.wit.entity.PluginConfig;
import net.wit.mobile.controller.AccountController;
import net.wit.plugin.PaymentPlugin;
import net.wit.service.impl.ZGTService;
import net.wit.util.SettingUtils;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.ld.slf4j.Logger;
import cn.ld.slf4j.LoggerFactory;

/**
 * Plugin - 银联在线支付
 * @author rsico Team
 * @version 3.0
 */
@Component("unionpayPlugin")
public class UnionpayPlugin extends PaymentPlugin {
	
	private Logger log = LoggerFactory.getLogger(AccountController.class);

	/** 货币 */
	private static final String CURRENCY = "156";

	@Override
	public String getName() {
		return "银联在线支付";
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
		return "unionpay/install.jhtml";
	}

	@Override
	public String getUninstallUrl() {
		return "unionpay/uninstall.jhtml";
	}

	@Override
	public String getSettingUrl() {
		return "unionpay/setting.jhtml";
	}

	@Override
	public String getRequestUrl() {
		return "https://unionpaysecure.com/api/Pay.action";
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
		Setting setting = SettingUtils.get();
		PluginConfig pluginConfig = getPluginConfig();
		Payment payment = getPayment(sn);
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("version", "1.0.0");
		parameterMap.put("charset", "UTF-8");
		parameterMap.put("transType", "01");
		parameterMap.put("merId", pluginConfig.getAttribute("partner"));
		parameterMap.put("merAbbr", StringUtils.abbreviate("tohola".replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", ""), 40));
		parameterMap.put("commodityUrl", setting.getSiteUrl());
		parameterMap.put("commodityName", StringUtils.abbreviate("buy".replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", ""), 200));
		parameterMap.put("orderNumber", sn);
		parameterMap.put("orderAmount", payment.getAmount().multiply(new BigDecimal(100)).setScale(0).toString());
		parameterMap.put("orderCurrency", CURRENCY);
		parameterMap.put("orderTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		parameterMap.put("customerIp", request.getLocalAddr());
		parameterMap.put("transTimeout", getTimeout() * 60000);
		parameterMap.put("frontEndUrl", getNotifyUrl(sn, NotifyMethod.sync, root));
		parameterMap.put("backEndUrl", getNotifyUrl(sn, NotifyMethod.async, root));
		parameterMap.put("signMethod", "MD5");
		parameterMap.put("signature", generateSign(parameterMap));
		return parameterMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean verifyNotify(String sn, NotifyMethod notifyMethod, HttpServletRequest request) {
		PluginConfig pluginConfig = getPluginConfig();
		Payment payment = getPayment(sn);
		if (generateSign(request.getParameterMap()).equals(request.getParameter("signature")) && pluginConfig.getAttribute("partner").equals(request.getParameter("merId")) && sn.equals(request.getParameter("orderNumber"))
				&& CURRENCY.equals(request.getParameter("orderCurrency")) && "00".equals(request.getParameter("respCode")) && payment.getAmount().multiply(new BigDecimal(100)).compareTo(new BigDecimal(request.getParameter("orderAmount"))) == 0) {
			/*
			 * Map<String, Object> parameterMap = new HashMap<String, Object>(); parameterMap.put("version", "1.0.0"); parameterMap.put("charset", "UTF-8");
			 * parameterMap.put("transType", "01"); parameterMap.put("merId", pluginConfig.getAttribute("partner")); parameterMap.put("orderNumber", sn);
			 * parameterMap.put("orderTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())); parameterMap.put("signMethod", "MD5");
			 * parameterMap.put("signature", generateSign(parameterMap)); String result = post("https://unionpaysecure.com/api/Query.action", parameterMap); if
			 * (ArrayUtils.contains(result.split("&"), "respCode=00")) { return true; }
			 */
			return true;
		}
		return false;
	}
	
	public boolean BankVerifyNotify(NotifyMethod notifyMethod,
			HttpServletRequest request) {
		String data				   = request.getParameter("data");
		
		log.info("易宝后台通知数据data : " + data);
		
		Map<String, String> result = ZGTService.decryptPayCallbackData(data);
		String customernumber      = result.get("customernumber");
		String requestid           = result.get("requestid");
		String code                = result.get("code");
		String hmacYeePay          = result.get("hmac");
		
		String keyForHmac 			= ZGTService.getKeyValue(); 									

		String[] stringArray		= {customernumber, requestid, code};
		String hmac					= ZGTService.getHmac(stringArray, keyForHmac);
		
		log.info("解密后的数据为： [customernumber:" + customernumber + "|requestid:" + requestid
				+ "|code:" + code +  "|hmac:" + hmac);
		
		if (hmacYeePay.equals(hmac)    // 加密密钥
				) { 
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

	protected String joinKeyValue(Map<String, Object> map, String prefix, String suffix, String separator, boolean ignoreEmptyValue, String... ignoreKeys) {
		List<String> keys = new ArrayList<String>(map.keySet());

		List<String> list = new ArrayList<String>();
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = ConvertUtils.convert(map.get(key));
			if (StringUtils.isNotEmpty(key) && !ArrayUtils.contains(ignoreKeys, key) && (!ignoreEmptyValue || StringUtils.isNotEmpty(value))) {
				list.add(key + "=" + (value != null ? value : ""));
			}
		}

		return (prefix != null ? prefix : "") + StringUtils.join(list, separator) + (suffix != null ? suffix : "");
	}

	/**
	 * 生成签名
	 * @param parameterMap 参数
	 * @return 签名
	 */
	private String generateSign(Map<String, ?> parameterMap) {
		PluginConfig pluginConfig = getPluginConfig();
		return DigestUtils.md5Hex(joinKeyValue(new TreeMap<String, Object>(parameterMap), null, "&" + DigestUtils.md5Hex(pluginConfig.getAttribute("key")), "&", false, "signMethod", "signature"));
	}

}