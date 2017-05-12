/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.controller.shop;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.wit.Message;
import net.wit.Principal;
import net.wit.Setting;
import net.wit.Setting.AccountLockType;
import net.wit.Setting.CaptchaType;
import net.wit.entity.Area;
import net.wit.entity.Cart;
import net.wit.entity.Member;
import net.wit.service.AreaService;
import net.wit.service.BindUserService;
import net.wit.service.CaptchaService;
import net.wit.service.CartService;
import net.wit.service.MemberRankService;
import net.wit.service.MemberService;
import net.wit.service.RSAService;
import net.wit.service.SmsSendService;
import net.wit.util.SettingUtils;
import net.wit.util.WebUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller - 会员登录
 * @author rsico Team
 * @version 3.0
 */
@Controller("shopIndexController")
@RequestMapping("/index")
public class IndexController extends BaseController {

	@Resource(name = "captchaServiceImpl")
	private CaptchaService captchaService;

	@Resource(name = "rsaServiceImpl")
	private RSAService rsaService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "bindUserServiceImpl")
	private BindUserService bindUserService;

	@Resource(name = "cartServiceImpl")
	private CartService cartService;

	@Resource(name = "memberRankServiceImpl")
	private MemberRankService memberRankService;

	@Resource(name = "smsSendServiceImpl")
	private SmsSendService smsSendService;

	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	/**
	 * 登录检测
	 */
	@RequestMapping(value = "/check", method = RequestMethod.GET)
	public @ResponseBody Boolean check() {
		return memberService.isAuthenticated();
	}

	/**
	 * 登录页面
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index(Long areaId, String redirectUrl, HttpServletRequest request, ModelMap model) {
		if (areaId != null) {
			request.getSession().setAttribute(Member.AREA_ATTRIBUTE_NAME, areaService.find(areaId));
		}
		Setting setting = SettingUtils.get();
		if (redirectUrl != null && !redirectUrl.equalsIgnoreCase(setting.getSiteUrl()) && !redirectUrl.startsWith(request.getContextPath() + "/") && !redirectUrl.startsWith(setting.getSiteUrl() + "/")) {
			redirectUrl = null;
		}
		Member member = memberService.getCurrent();
		Area area = areaService.getCurrent();
		model.addAttribute("areas", areaService.findOpenList());
		model.addAttribute("area", area);
		model.addAttribute("redirectUrl", redirectUrl);
		model.addAttribute("member", member);
		model.addAttribute("captchaId", UUID.randomUUID().toString());
		return "/shop/login/index";
	}

	@RequestMapping(value = "/getUUID", method = RequestMethod.GET)
	public @ResponseBody Message getUUID(HttpServletRequest request) {
		return Message.success(UUID.randomUUID().toString());
	}

	/**
	 * 登录提交
	 */
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public @ResponseBody Message submit(String captchaId, String captcha, String username, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		String password = rsaService.decryptParameter("enPassword", request);
		rsaService.removePrivateKey(request);

		if (!captchaService.isValid(CaptchaType.memberLogin, captchaId, captcha)) {
			return Message.error("shop.captcha.invalid");
		}
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			return Message.error("shop.common.invalid");
		}
		Member member = null;
		Setting setting = SettingUtils.get();
		ResourceBundle bundle = PropertyResourceBundle.getBundle("config");

		Map<String, Object> attributes = new HashMap<String, Object>();
		if (setting.getIsEmailLogin() && username.contains("@")) {
			List<Member> members = memberService.findListByEmail(username);
			if (members.isEmpty()) {
			} else if (members.size() == 1) {
				member = members.get(0);
			} else {
				return Message.error("shop.login.unsupportedAccount");
			}
		} else {
			member = memberService.findByUsername(username);
		}

		if (member == null) {
			return Message.error("shop.login.unknownAccount");
		}
		if (!member.getIsEnabled()) {
			return Message.error("shop.login.disabledAccount");
		}
		if (member.getIsLocked()) {
			if (ArrayUtils.contains(setting.getAccountLockTypes(), AccountLockType.member)) {
				int loginFailureLockTime = setting.getAccountLockTime();
				if (loginFailureLockTime == 0) {
					return Message.error("shop.login.lockedAccount");
				}
				Date lockedDate = member.getLockedDate();
				Date unlockDate = DateUtils.addMinutes(lockedDate, loginFailureLockTime);
				if (new Date().after(unlockDate)) {
					member.setLoginFailureCount(0);
					member.setIsLocked(false);
					member.setLockedDate(null);
					memberService.update(member);
				} else {
					return Message.error("shop.login.lockedAccount");
				}
			} else {
				member.setLoginFailureCount(0);
				member.setIsLocked(false);
				member.setLockedDate(null);
				memberService.update(member);
			}
		}

		if (!DigestUtils.md5Hex(password).equals(member.getPassword())) {
			int loginFailureCount = member.getLoginFailureCount() + 1;
			if (loginFailureCount >= setting.getAccountLockCount()) {
				member.setIsLocked(true);
				member.setLockedDate(new Date());
				if (member.getMobile() != null) {
					smsSendService.sysSend(member.getMobile(), "您在【" + setting.getSiteName() + "】的账号,密码输入错误，累计超过" + member.getLoginFailureCount().toString() + "次，为保证您的账户安全，您的账号已经被锁定。【" + bundle.getString("signature") + "】");
				}
			}

			member.setLoginFailureCount(loginFailureCount);
			memberService.update(member);
			if (ArrayUtils.contains(setting.getAccountLockTypes(), AccountLockType.member)) {
				return Message.error("shop.login.accountLockCount", setting.getAccountLockCount());
			} else {
				return Message.error("shop.login.incorrectCredentials");
			}
		}
		member.setLoginIp(request.getRemoteAddr());
		member.setLoginDate(new Date());
		member.setLoginFailureCount(0);
		memberService.update(member);

		Cart cart = cartService.getCurrent();
		if (cart != null) {
			if (cart.getMember() == null) {
				cartService.merge(member, cart);
				WebUtils.removeCookie(request, response, Cart.ID_COOKIE_NAME);
				WebUtils.removeCookie(request, response, Cart.KEY_COOKIE_NAME);
			}
		}

		Enumeration<?> keys = session.getAttributeNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			attributes.put(key, session.getAttribute(key));
		}
		session.invalidate();
		session = request.getSession();
		for (Entry<String, Object> entry : attributes.entrySet()) {
			session.setAttribute(entry.getKey(), entry.getValue());
		}

		session.setAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME, new Principal(member.getId(), username));
		WebUtils.addCookie(request, response, Member.USERNAME_COOKIE_NAME, member.getUsername());
		return Message.success(memberService.getToken(member));
	}

	/**
	 * 验证新商盟账号
	 * @param username
	 * @param password
	 * @return "netError" 网络故障 "userOrPwdError" 用户名或密码错误 "success" 新商盟验证登录成功
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws DocumentException
	 */
	/**
	 * public String xsmAuthor(String username,String password,Map<String, Object> attributes) throws ClientProtocolException, IOException, DocumentException{
	 * HttpClient client = new DefaultHttpClient(); try { HttpGet getMethod = new HttpGet("http://center.xinshangmeng.com/st/"); HttpContext context = new
	 * BasicHttpContext(); CookieStore cookieStore = new BasicCookieStore(); context.setAttribute(ClientContext.COOKIE_STORE, cookieStore); HttpResponse res =
	 * null; res= client.execute(getMethod,context); if(res==null){ return "netError"; } if (res.getStatusLine().getStatusCode()!=HttpStatus.SC_OK) { return
	 * "netError"; } //返回正常 HttpEntity entity = res.getEntity(); String xmlStr = new
	 * String(EntityUtils.toByteArray(entity),EntityUtils.getContentCharSet(entity)); Document dom=DocumentHelper.parseText(xmlStr); Element
	 * root=dom.getRootElement(); String codeString = root.attributeValue("code"); if(!"0000".equals(codeString)){ return "netError"; } String
	 * challenge=root.element("challenge").getText(); String dec="{1#2$3%4(5)6@7!poeeww$3%4(5)djjkkldss}"; String xsmpassword =
	 * MD5Utils.getMD5Str(MD5Utils.getMD5Str(password+dec)+challenge+dec); getMethod = new
	 * HttpGet("http://center.xinshangmeng.com/st/users/dologin/up?j_username="+username+"&j_password="+xsmpassword); res= client.execute(getMethod,context);
	 * if(res==null){ return "netError"; } if (res.getStatusLine().getStatusCode()!=HttpStatus.SC_OK) { return "netError"; } HttpEntity entity2 =
	 * res.getEntity(); String xmlStr2 = new String(EntityUtils.toByteArray(entity2),EntityUtils.getContentCharSet(entity2));
	 * dom=DocumentHelper.parseText(xmlStr2); root=dom.getRootElement(); codeString = root.attributeValue("code"); if("0001".equals(codeString)){ return
	 * "userOrPwdError"; } if("0000".equals(codeString)){ String areaCode=root.element("comId").getText(); attributes.put("areaCode", areaCode.substring(2,7));
	 * List cookies = cookieStore.getCookies(); if (!cookies.isEmpty()) { for (int i = cookies.size(); i > 0; i --) { Cookie cookie = (Cookie) cookies.get(i -
	 * 1); attributes.put(cookie.getName(),cookie.getValue()); //WebUtils.addCookie(request, response, cookie.getName(), cookie.getValue(), 120,
	 * "/",".xinshangmeng.com", cookie.isSecure()); } } return "success"; } } finally { client.getConnectionManager().shutdown(); } return "netError"; }
	 */

}