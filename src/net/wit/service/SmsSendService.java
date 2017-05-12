package net.wit.service;

import java.util.Map;

import net.wit.entity.SmsSend;

/**
 * @ClassName: SmsSendService
 * @Description: Service接口, </br> 版权所有 2008-2010 rspcn.com,并保留所有权利。</br> 提示：在未取得SHOPUNION商业授权之前,您不能将本软件应用于商业用途,否则SHOPUNION将保留追究的权力。 </br>
 *               官方网站：http://www.shopunion.com </br> KEY: SHOPUNIONFCA2469C0D8EC758618D14A3D8EA6A05
 * @author Administrator
 * @date 2014年4月21日 下午5:16:50
 */
public interface SmsSendService extends BaseService<SmsSend, String> {

	/** 按模版内容发送 */
	public void sendTemplateNoticePool(String mobile, String tempalte, Map<String, String> model);

	/** 系统类短信发送-同步发送 */
	public String sysSend(final String mobile, final String content);

	/** 系统类短信发送-异步发送 */
	public void putSystemSendPool(final String mobile, final String content);

}