/*
 * 
 * 
 * 
 */
package net.wit.plugin.yeepay;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.ld.slf4j.Logger;
import cn.ld.slf4j.LoggerFactory;
import net.wit.entity.Payment;
import net.wit.entity.PluginConfig;
import net.wit.plugin.PaymentPlugin;
import net.wit.service.impl.ZGTService;
import net.wit.util.SettingUtils;
import net.wit.util.WebUtils;

/**
 * Plugin - 易宝支付
 */
@Component("yeepayPlugin")
public class YeepayPlugin extends PaymentPlugin {
	
	private Logger log = LoggerFactory.getLogger(YeepayPlugin.class);

	@Override
	public String getName() {
		return "易宝支付";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getAuthor() {
		return SettingUtils.get().getSiteName();
	}

	@Override
	public String getSiteUrl() {
		return SettingUtils.get().getSiteUrl();
	}

	@Override
	public String getInstallUrl() {
		return "yeepay/install.jhtml";
	}

	@Override
	public String getUninstallUrl() {
		return "yeepay/uninstall.jhtml";
	}

	@Override
	public String getSettingUrl() {
		return "yeepay/setting.jhtml";
	}

	@Override
	public String getRequestUrl() {
		return "https://www.yeepay.com/app-merchant-proxy/node";
	}

	@Override
	public RequestMethod getRequestMethod() {
		return RequestMethod.get;
	}

	@Override
	public String getRequestCharset() {
		return "GBK";
	}

	@Override
	public Map<String, Object> getParameterMap(String sn, String description, HttpServletRequest request, String root) {
		PluginConfig pluginConfig = getPluginConfig();
		Payment payment = getPayment(sn);
		Map<String, Object> parameterMap = new LinkedHashMap<String, Object>();
		parameterMap.put("p0_Cmd", "Buy");
		parameterMap.put("p1_MerId", pluginConfig.getAttribute("partner"));
		parameterMap.put("p2_Order", sn);
		parameterMap.put("p3_Amt", payment.getAmount().setScale(2).toString());
		parameterMap.put("p4_Cur", "CNY");
		parameterMap.put("p5_Pid", StringUtils.abbreviate(description.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", ""), 20));
		parameterMap.put("p7_Pdesc", StringUtils.abbreviate(description.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", ""), 20));
		parameterMap.put("p8_Url", getNotifyUrl(sn, NotifyMethod.general, root));
		parameterMap.put("p9_SAF", "0");
		parameterMap.put("pa_MP", "aok");
		parameterMap.put("pr_NeedResponse", "1");
		parameterMap.put("hmac", generateSign(parameterMap));
		return parameterMap;
	}

	@Override
	public boolean verifyNotify(String sn, NotifyMethod notifyMethod, HttpServletRequest request) {
		PluginConfig pluginConfig = getPluginConfig();
		String data				   = request.getParameter("data");
		
		log.info("易宝后台通知数据data : " + data);
		
		Map<String, String> result = ZGTService.decryptPayCallbackData(data);
		String customernumber      = result.get("customernumber");
		String requestid           = result.get("requestid");
		String code                = result.get("code");
		String notifytype          = result.get("notifytype");
		String externalid          = result.get("externalid");
		String amount              = result.get("amount");
		String cardno              = result.get("cardno");
		String bankcode            = result.get("bankcode");
		String cardtype            = result.get("cardtype");
		String hmacYeePay          = result.get("hmac");
		
		String keyForHmac 			= ZGTService.getKeyValue(); 									

		String[] stringArray		= {customernumber, requestid, code, notifytype, externalid, amount, 
				cardno};
		String hmac					= ZGTService.getHmac(stringArray, keyForHmac);
		
		log.info("解密后的数据为： [customernumber:" + customernumber + "|requestid:" + requestid
				+ "|code:" + code + "|notifytype:" + notifytype + "|externalid:" + externalid
				+ "|amount:" + amount + "|cardno:" + cardno + "|bankcode:" + bankcode 
				+ "|cardtype" + cardtype + "|hmac:" + hmac);
		
		if (hmacYeePay.equals(hmac)    // 加密密钥
				&& pluginConfig.getAttribute("partner").equals(customernumber) // 商户编号
//				&& new BigDecimal(amount).compareTo(payment.getAmount()) == 0 // 订单金额
				&& sn.equals(requestid)  // 订单编号
				&& "1".equals(code) ) { // 返回状态
			return true;
		}
		return false;
	}

	@Override
	public String getNotifyMessage(String sn, NotifyMethod notifyMethod, HttpServletRequest request) {
		if ("2".equals(WebUtils.getParameter(request.getQueryString(), "GBK", "r9_BType"))) {
			return "success";
		}
		return null;
	}

	@Override
	public Integer getTimeout() {
		return 21600;
	}

	/**
	 * 生成签名
	 * @param parameterMap 参数
	 * @return 签名
	 */
	private String generateSign(Map<String, Object> parameterMap) {
		PluginConfig pluginConfig = getPluginConfig();
		return hmacDigest(joinValue(parameterMap, null, null, null, false, "hmac"), pluginConfig.getAttribute("key"));
	}

	/**
	 * Hmac加密
	 * @param value 值
	 * @param key 密钥
	 * @return 密文
	 */
	private String hmacDigest(String value, String key) {
		try {
			Mac mac = Mac.getInstance("HmacMD5");
			mac.init(new SecretKeySpec(key.getBytes("UTF-8"), "HmacMD5"));
			byte[] bytes = mac.doFinal(value.getBytes("UTF-8"));

			StringBuffer digest = new StringBuffer();
			for (int i = 0; i < bytes.length; i++) {
				String hex = Integer.toHexString(0xFF & bytes[i]);
				if (hex.length() == 1) {
					digest.append("0");
				}
				digest.append(hex);
			}
			return digest.toString();
		} catch (Exception e) {
			return null;
		}
	}

}