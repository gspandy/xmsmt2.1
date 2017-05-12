package net.wit.mobile.controller;


import net.wit.util.CacheUtil;
import net.wit.mobile.service.INTokenBS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping(value = "/ntoken")
public class NTokenController extends BaseController {

	@Autowired
	private INTokenBS inTokenBS;

	

	// 判断token是否有效,打开应用是调用。后台自动延后过期时间
	@RequestMapping(value = "/isVaild")
	public void isVaild(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
                this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				this.handleJsonResponse(response, true, "");
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
		}
	}
	// //更新token过期时间.
	// @RequestMapping(value = "/renewExpiredDate")
	// public void renewExpiredDate(HttpServletResponse response,
	// @RequestParam("token") String token) throws Exception {
	// try {
	// if(!tokenBS.isExpiredByToken(token)){
	// //判断token是否有效
	// if(tokenBS.renewExpireTimeByToken(token)){
	// this.handleJsonResponse(response, true, "");
	// }else{
	// throw new Exception(errJsonMessage(TOKEN_RENEW_FAILED_CODE,
	// TOKEN_RENEW_FAILED_MESSAGE));
	// }
	// }else{
	// throw new Exception(errJsonMessage(TOKEN_EXPIRED_CODE,
	// TOKEN_EXPIRED_MESSAGE));
	// }
	// } catch (Exception e) {
	// this.handleJsonResponse(response, false, e.getMessage());
	// }
	// }

}
