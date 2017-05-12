package net.wit.plugin.alipayMobile.util;

import java.util.HashMap;
import java.util.Map;

import net.wit.plugin.alipayMobile.config.AlipayConfig;
import net.wit.plugin.alipayMobile.sign.RSA;

/* *
 *类名：AlipaySubmit
 *功能：支付宝各接口请求提交类
 *详细：构造支付宝各接口表单HTML文本，获取远程HTTP数据
 *版本：3.3
 *日期：2012-08-13
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipaySubmit {

	/**
	 * 生成签名结果
	 * @param sPara 要签名的数组
	 * @return 签名结果字符串
	 */
	public static String buildRequestMysign(Map<String, String> sPara, String privateKey) {
		String prestr = AlipayCore.createLinkString(sPara); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String mysign = "";
		if (AlipayConfig.sign_type.equals("RSA")) {
//			mysign = RSA.sign(prestr, AlipayConfig.private_key, AlipayConfig.input_charset);
			mysign = RSA.sign(prestr, privateKey, AlipayConfig.input_charset);
		}
		return mysign;
	}

	/**
	 * 生成要请求给支付宝的参数数组
	 * @param sParaTemp 请求前的参数数组
	 * @return 要请求的参数数组
	 */
	private static Map<String, String> buildRequestPara(Map<String, String> sParaTemp, String privateKey) {
		// 除去数组中的空值和签名参数
		Map<String, String> sPara = AlipayCore.paraFilter(sParaTemp);
		// 生成签名结果
		String mysign = buildRequestMysign(sPara, privateKey);

		// 签名结果与签名方式加入请求提交参数组中
		sPara.put("sign", mysign);
		sPara.put("sign_type", AlipayConfig.sign_type);

		return sPara;
	}

	/**
	 * 组装付款请求数据
	 * @return Map
	 */
	public static Map<String,String> buildRequest(Map<String,String> params) {
		// 把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();

		String service = "mobile.securitypay.pay";// 接口名称
		String partner = params.get("partner");// 合作者身 份ID
		String _input_charset = AlipayConfig.input_charset;// 参数编码 字符集
//		String sign_type = AlipayConfig.sign_type;// 签名方式
//		String sign = "";// 签名
		String notify_url = params.get("notify_url");// 服务器异步通知页面路径
		// app_id// 客户端号 可空
		// appenv// 客户端来源 可空
		String out_trade_no = params.get("orderNo");// 商户网站唯一订单号
		String subject = params.get("subject");// 商品名称
		String payment_type = "1";// 支付类型 默认1-商品购买
		String seller_id = params.get("seller_id");// 卖家支付 宝账号
		String total_fee = params.get("amount");// 总金额
		String body = "速卖通商品的商品详情";// 商品详情
		String privateKey = params.get("privateKey");
		
		sParaTemp.put("service", service);
		sParaTemp.put("partner", partner);
		sParaTemp.put("_input_charset", _input_charset);
		sParaTemp.put("notify_url", notify_url);
		sParaTemp.put("out_trade_no", out_trade_no);
		sParaTemp.put("subject", subject);
		sParaTemp.put("payment_type", payment_type);
		sParaTemp.put("seller_id", seller_id);
		sParaTemp.put("total_fee", total_fee);
		sParaTemp.put("body", body);

		// 待请求参数数组
		Map<String, String> sPara = buildRequestPara(sParaTemp, privateKey);

		String resultStr = "partner=" + sPara.get("partner")
				+ "&seller_id=" + sPara.get("seller_id")
				+ "&out_trade_no=" + sPara.get("out_trade_no")
				+ "&subject=" + sPara.get("subject")
				+ "&body=" + sPara.get("body")
				+ "&total_fee=" + sPara.get("total_fee")
				+ "&notify_url=" + sPara.get("notify_url")
				+ "&service=" + sPara.get("service")
				+ "&payment_type=" + sPara.get("payment_type")
				+ "&_input_charset=" + sPara.get("_input_charset")
				+ "&sign=" + sPara.get("sign")
				+ "&sign_type=" + sPara.get("sign_type");
		
		sPara.put("resultStr", resultStr);
		
		return sPara;
	}
	
	
	/**
	 * 组装退款请求数据
	 */
	
	
	/**
	 * 组装转账请求数据
	 */
	
	
	
}
