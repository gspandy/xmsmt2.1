/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.controller.shop;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
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
import net.wit.Setting.CaptchaType;
import net.wit.entity.Area;
import net.wit.entity.Cart;
import net.wit.entity.Member;
import net.wit.entity.SafeKey;
import net.wit.entity.BaseEntity.Save;
import net.wit.entity.Member.Gender;
import net.wit.service.AreaService;
import net.wit.service.CaptchaService;
import net.wit.service.CartService;
import net.wit.service.CommunityService;
import net.wit.service.MailService;
import net.wit.service.MemberRankService;
import net.wit.service.MemberService;
import net.wit.service.RSAService;
import net.wit.service.SmsSendService;
import net.wit.service.TenantCategoryService;
import net.wit.service.TenantService;
import net.wit.util.SettingUtils;
import net.wit.util.SpringUtils;
import net.wit.util.WebUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller - 会员注册
 * @author rsico Team
 * @version 3.0
 */
@Controller("shopRegisterController")
@RequestMapping("/register")
public class RegisterController extends BaseController {

	@Resource(name = "captchaServiceImpl")
	private CaptchaService captchaService;

	@Resource(name = "rsaServiceImpl")
	private RSAService rsaService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "memberRankServiceImpl")
	private MemberRankService memberRankService;

	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	@Resource(name = "cartServiceImpl")
	private CartService cartService;

	@Resource(name = "smsSendServiceImpl")
	private SmsSendService smsSendService;

	@Resource(name = "mailServiceImpl")
	private MailService mailService;

	@Resource(name = "tenantCategoryServiceImpl")
	private TenantCategoryService tenantCategoryService;

	@Resource(name = "tenantServiceImpl")
	private TenantService tenantService;

	@Resource(name = "communityServiceImpl")
	private CommunityService communityService;

	public static final String REGISTER_SECURITYCODE_SESSION = "register_securityCode_session";

	public static final String REGISTER_MOBILE_SESSION = "register_mobile_session";

	public static final String REGISTER_EMAIL_SESSION = "register_email_session";

	/**
	 * 检查用户名是否被禁用或已存在
	 */
	@RequestMapping(value = "/check_username", method = RequestMethod.GET)
	public @ResponseBody boolean checkUsername(String username) {
		if (StringUtils.isEmpty(username)) {
			return false;
		}
		if (memberService.usernameDisabled(username) || memberService.usernameExists(username)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 检查E-mail是否存在
	 */
	@RequestMapping(value = "/check_email", method = RequestMethod.GET)
	public @ResponseBody boolean checkEmail(String email) {
		if (StringUtils.isEmpty(email)) {
			return false;
		}
		if (memberService.usernameDisabled(email) || memberService.usernameExists(email) || memberService.emailExists(email)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 检查mobile是否存在
	 */
	@RequestMapping(value = "/check_mobile", method = RequestMethod.GET)
	public @ResponseBody boolean checkMobile(String mobile) {
		if (StringUtils.isEmpty(mobile)) {
			return false;
		}
		if (memberService.usernameDisabled(mobile) || memberService.usernameExists(mobile) || memberService.mobileExists(mobile)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 注册页面
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Area area = areaService.getCurrent();
		model.addAttribute("area", area);
		model.addAttribute("genders", Gender.values());
		model.addAttribute("captchaId", UUID.randomUUID().toString());
		return "/shop/register/index";
	}

	/**
	 * 注册页面
	 */
	@RequestMapping(value = "indexEmail", method = RequestMethod.GET)
	public String index1(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Area area = areaService.getCurrent();
		model.addAttribute("area", area);
		model.addAttribute("genders", Gender.values());
		model.addAttribute("captchaId", UUID.randomUUID().toString());
		return "/shop/register/indexEmail";
	}

	/**
	 * 注册提交(手机/邮箱)
	 */
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	@ResponseBody
	public Message submit(String captchaId, String captcha, String username, String mobile, String email, String checkCode, Long areaId, String password, HttpServletRequest request, HttpServletResponse response, HttpSession session,
			RedirectAttributes redirectAttributes) {
		// 用户名注册时 获取的密码
		if (StringUtils.isEmpty(password)) {
			password = rsaService.decryptParameter("enPassword", request);
		}
		rsaService.removePrivateKey(request);
		Setting setting = SettingUtils.get();
		ResourceBundle bundle = PropertyResourceBundle.getBundle("config");
		if (StringUtils.isNotEmpty(mobile)) {
			username = mobile;
			if (!isValid(Member.class, "username", username, Save.class) || !isValid(Member.class, "password", password, Save.class)) {
				return Message.error("shop.common.invalid");
			}
		}
		if (StringUtils.isNotEmpty(email)) {
			username = email;
			if (!isValid(Member.class, "username", username, Save.class) || !isValid(Member.class, "password", password, Save.class)) {
				return Message.error("shop.common.invalid");
			}
		}
		if (StringUtils.isNotEmpty(username)) {
			if (!isValid(Member.class, "username", username, Save.class) || !isValid(Member.class, "password", password, Save.class)) {
				return Message.error("shop.common.invalid");
			}
		}
		if (!setting.getIsRegisterEnabled()) {
			return Message.error("shop.register.disabled");
		}

		if (username.length() < setting.getUsernameMinLength() || username.length() > setting.getUsernameMaxLength()) {
			return Message.error("shop.username.length");
		}
		if (password.length() < setting.getPasswordMinLength() || password.length() > setting.getPasswordMaxLength()) {
			return Message.error("shop.password.length");
		}
		if (memberService.usernameDisabled(username) || memberService.usernameExists(username)) {
			return Message.error("shop.register.disabledExist");
		}
		if (areaId == null) {
			return Message.error("shop.register.area");
		}
		Area area = areaService.find(areaId);
		Member member = new Member();
		member.setArea(area);
		member.setUsername(username);
		member.setPassword(DigestUtils.md5Hex(password));
		member.setPoint(setting.getRegisterPoint());
		member.setAmount(new BigDecimal(0));
		member.setBalance(new BigDecimal(0));
		member.setFreezeBalance(BigDecimal.ZERO);
		member.setPrivilege(0);
		member.setTotalScore(0L);
		member.setIsEnabled(true);
		member.setIsLocked(false);
		member.setLoginFailureCount(0);
		member.setLockedDate(null);
		member.setRegisterIp(request.getRemoteAddr());
		member.setLoginIp(request.getRemoteAddr());
		member.setLoginDate(new Date());
		member.setSafeKey(null);
		member.setPaymentPassword(DigestUtils.md5Hex(password));
		member.setRebateAmount(new BigDecimal(0));
		member.setProfitAmount(new BigDecimal(0));
		member.setMemberRank(memberRankService.findDefault());
		member.setFavoriteProducts(null);
		SafeKey safeKey = (SafeKey) session.getAttribute(REGISTER_SECURITYCODE_SESSION);
		if (mobile != null && email == null) {
			String mobile_session = (String) session.getAttribute(REGISTER_MOBILE_SESSION);
			if (safeKey == null && mobile_session == null) {
				return Message.error("shop.captcha.invalid");
			}
			if (!safeKey.getValue().equals(checkCode) && !mobile_session.equals(mobile)) {
				return Message.error("shop.captcha.invalid");
			}
			member.setMobile(username);
			member.setBindMobile(Member.BindStatus.binded);
			member.setBindEmail(Member.BindStatus.none);
			member.setEmail(username + "@139.com");
			memberService.save(member);

			Cart cart = cartService.getCurrent();
			if (cart != null && cart.getMember() == null) {
				cartService.merge(member, cart);
				WebUtils.removeCookie(request, response, Cart.ID_COOKIE_NAME);
				WebUtils.removeCookie(request, response, Cart.KEY_COOKIE_NAME);
			}
			Map<String, Object> attributes = new HashMap<String, Object>();
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

			session.setAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME, new Principal(member.getId(), member.getUsername()));
			WebUtils.addCookie(request, response, Member.USERNAME_COOKIE_NAME, member.getUsername());
			smsSendService.sysSend(member.getUsername(), "欢迎您注册【" + setting.getSiteName() + "】,账号:" + member.getUsername() + ",密码:" + password + ",网址:【" + setting.getSiteUrl() + "】,为了您的账户安全请不要转发他人【" + bundle.getString("signature") + "】");

		}
		if (email != null && mobile == null) {
			String email_session = (String) session.getAttribute(REGISTER_EMAIL_SESSION);
			if (safeKey == null && email_session == null) {
				return Message.error("shop.captcha.invalid");
			}
			if (!safeKey.getValue().equals(checkCode) && !email.equals(email_session)) {
				addFlashMessage(redirectAttributes, Message.error("shop.captcha.invalid"));
				return Message.error("shop.captcha.invalid");
			}
			member.setMobile(null);
			member.setEmail(username);
			member.setBindEmail(Member.BindStatus.binded);
			member.setBindMobile(Member.BindStatus.none);
			memberService.save(member);
			Cart cart = cartService.getCurrent();
			if (cart != null && cart.getMember() == null) {
				cartService.merge(member, cart);
				WebUtils.removeCookie(request, response, Cart.ID_COOKIE_NAME);
				WebUtils.removeCookie(request, response, Cart.KEY_COOKIE_NAME);
			}
			Map<String, Object> attributes = new HashMap<String, Object>();
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

			session.setAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME, new Principal(member.getId(), member.getUsername()));
			WebUtils.addCookie(request, response, Member.USERNAME_COOKIE_NAME, member.getUsername());
			SafeKey safeKey1 = new SafeKey();
			safeKey1.setValue("账号:" + username + "," + "密码:" + password);
			mailService.sendRegisterSucessMail(username, username, safeKey1);
		}
		if (email == null && mobile == null) {
			if (!captchaService.isValid(CaptchaType.memberRegister, captchaId, captcha)) {
				return Message.error("shop.captcha.invalid");
			}
			member.setEmail(username + "@vsstoo.com");
			member.setBindEmail(Member.BindStatus.none);
			member.setBindMobile(Member.BindStatus.none);
			memberService.save(member);
			Cart cart = cartService.getCurrent();
			if (cart != null && cart.getMember() == null) {
				cartService.merge(member, cart);
				WebUtils.removeCookie(request, response, Cart.ID_COOKIE_NAME);
				WebUtils.removeCookie(request, response, Cart.KEY_COOKIE_NAME);
			}
			Map<String, Object> attributes = new HashMap<String, Object>();
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
			session.setAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME, new Principal(member.getId(), member.getUsername()));
			WebUtils.addCookie(request, response, Member.USERNAME_COOKIE_NAME, member.getUsername());
		}
		return Message.success("shop.register.success");
	}

	/**
	 * 获取验证码
	 */
	@RequestMapping(value = "/getCheckCode", method = RequestMethod.POST)
	@ResponseBody
	public Message getCheckCode(String mobile, HttpServletRequest request) {
		Setting setting = SettingUtils.get();
		ResourceBundle bundle = PropertyResourceBundle.getBundle("config");
		int challege = SpringUtils.getIdentifyingCode();
		String securityCode = String.valueOf(challege);
		SafeKey safeKey = new SafeKey();
		safeKey.setValue(securityCode);
		safeKey.setExpire(setting.getSafeKeyExpiryTime() != 0 ? DateUtils.addMinutes(new Date(), setting.getSafeKeyExpiryTime()) : null);
		HttpSession session = request.getSession();
		session.setAttribute(REGISTER_SECURITYCODE_SESSION, safeKey);
		session.setAttribute(REGISTER_MOBILE_SESSION, mobile);
		smsSendService.sysSend(mobile, "欢迎您注册" + setting.getSiteName() + ",验证码为 :" + securityCode + ",网址:" + setting.getSiteUrl() + ",为了您的账户安全请不要转发他人【" + bundle.getString("signature") + "】");
		return Message.success("" + securityCode);
	}

	/**
	 * 发送邮件
	 */
	@RequestMapping(value = "/send_email", method = RequestMethod.POST)
	@ResponseBody
	public Message sendEmail(String email, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		if (StringUtils.isEmpty(email)) {
			return Message.error("邮箱不能为空");
		}
		Setting setting = SettingUtils.get();
		int challege = SpringUtils.getIdentifyingCode();
		String securityCode = String.valueOf(challege);
		SafeKey safeKey = new SafeKey();
		safeKey.setValue(securityCode);
		safeKey.setExpire(setting.getSafeKeyExpiryTime() != 0 ? DateUtils.addMinutes(new Date(), setting.getSafeKeyExpiryTime()) : null);
		HttpSession session = request.getSession();
		session.setAttribute(REGISTER_SECURITYCODE_SESSION, safeKey);
		session.setAttribute(REGISTER_EMAIL_SESSION, email);
		mailService.sendRegisterMail(email, email, safeKey);
		return Message.success("" + securityCode);
	}

}