/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.plugin.chinapay;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.wit.entity.Payment;
import net.wit.entity.PluginConfig;
import net.wit.plugin.PaymentPlugin;

import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.stereotype.Component;

import chinapay.PrivateKey;
import chinapay.SecureLink;

/**
 * Plugin - 银联在线支付
 * 
 * @author rsico Team
 * @version 3.0
 */
@Component("chinapayPlugin")
public class ChinapayPlugin extends PaymentPlugin {

	/** 货币 */
	private static final String CURRENCY = "156";

	@Override
	public String getName() {
		return "chinaPay";
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
		return "chinapay/install.jhtml";
	}

	@Override
	public String getUninstallUrl() {
		return "chinapay/uninstall.jhtml";
	}

	@Override
	public String getSettingUrl() {
		return "chinapay/setting.jhtml";
	}

	@Override
	public String getRequestUrl() {
		return "https://payment.chinapay.com/pay/TransGet";
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
		parameterMap.put("MerId", pluginConfig.getAttribute("partner"));
		parameterMap.put("OrdId", org.apache.commons.lang.StringUtils.leftPad(sn, 16, "0"));
		parameterMap.put("TransDate", new SimpleDateFormat("yyyyMMdd").format(new Date()));
		parameterMap.put("TransAmt", org.apache.commons.lang.StringUtils.leftPad(payment.getAmount().multiply(new BigDecimal(100)).setScale(0).toString(), 12, "0"));
		parameterMap.put("CuryId", CURRENCY);
		parameterMap.put("TransType", "0001");
		parameterMap.put("Version", "20070129");
		parameterMap.put("GateId", "");
		parameterMap.put("PageRetUrl", getNotifyUrl(sn, NotifyMethod.sync,root));
		parameterMap.put("BgRetUrl", getNotifyUrl(sn, NotifyMethod.async,root));
		parameterMap.put("Priv1",sn);
		parameterMap.put("ChkValue",generateSign(parameterMap));
		return parameterMap;
	}

	@Override
	public boolean verifyNotify(String sn, NotifyMethod notifyMethod, HttpServletRequest request) {
		PluginConfig pluginConfig = getPluginConfig();
		Payment payment = getPayment(sn);
		String Version = request.getParameter("version");
		String MerId = request.getParameter("merid");
		String OrdId = request.getParameter("orderno");
		String TransAmt = request.getParameter("amount");// 12
		String CuryId = request.getParameter("currencycode");// 3
		String TransDate = request.getParameter("transdate");// 8
		String TransType = request.getParameter("transtype");// 4
		String Status = request.getParameter("status");
		String BgRetUrl = request.getParameter("BgRetUrl");
		String PageRetUrl = request.getParameter("PageRetUrl");
		String GateId = request.getParameter("GateId");
		String Priv1 = request.getParameter("Priv1");
		String ChkValue = request.getParameter("checkvalue");
		
		//System.out.println(MerId+OrdId+TransAmt+CuryId+TransDate+TransType+Status+ChkValue);
		
		boolean buildOK = false;
		boolean res = false;
		int KeyUsage = 0;
		PrivateKey key = new PrivateKey();
		try {
			//System.out.println(pluginConfig.getAttribute("partner"));
			//System.out.println(pluginConfig.getAttribute("key")+"PgPubk.key");
			buildOK = key.buildKey("999999999999999", KeyUsage, pluginConfig.getAttribute("key")+"PgPubk.key");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (!buildOK) {
			System.out.println("build error!");
			return false;
		}
		try {
			SecureLink sl = new SecureLink(key);
			res=sl.verifyTransResponse(MerId, OrdId, TransAmt, CuryId, TransDate, TransType, Status, ChkValue);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		//System.out.println("buildKeyfinish");
		
		if (res && pluginConfig.getAttribute("partner").equals(MerId) && sn.equals(Priv1) && "1001".equals(Status)
				&& org.apache.commons.lang.StringUtils.leftPad(payment.getAmount().multiply(new BigDecimal(100)).setScale(0).toString(), 12, "0").equals(TransAmt)) {
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
	private String generateSign(Map<String, ?> parameterMap) {
		PluginConfig pluginConfig = getPluginConfig();
		boolean buildOK = false;
		int KeyUsage = 0;
		String chkvalue="";
		PrivateKey key = new PrivateKey();
		try {
			System.out.println(pluginConfig.getAttribute("partner"));
			System.out.println(pluginConfig.getAttribute("key")+"MerPrK.key");
			buildOK = key.buildKey(pluginConfig.getAttribute("partner"), KeyUsage, pluginConfig.getAttribute("key")+"MerPrK.key");
		} catch (Exception e) {
			e.printStackTrace();
			return chkvalue;
		}
		if (!buildOK) {
			System.out.println("build error!");
			return chkvalue;
		}
		try {
			SecureLink sl = new SecureLink(key);

			chkvalue = sl.Sign(
					ConvertUtils.convert(parameterMap.get("MerId")) + 
					ConvertUtils.convert(parameterMap.get("OrdId")) + 
					ConvertUtils.convert(parameterMap.get("TransAmt")) + 
					ConvertUtils.convert(parameterMap.get("CuryId")) + 
					ConvertUtils.convert(parameterMap.get("TransDate")) + 
					ConvertUtils.convert(parameterMap.get("TransType")) + 
					ConvertUtils.convert(parameterMap.get("Priv1")) );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return chkvalue;
	}

}