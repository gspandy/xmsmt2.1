/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.controller.shop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.wit.entity.Member;
import net.wit.util.WebUtils;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller - 会员注销
 * 
 * @author rsico Team
 * @version 3.0
 */
@Controller("shopLogoutController")
@RequestMapping("/")
public class LogoutController extends BaseController {

	/**
	 * 注销
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String execute(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		session.removeAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
		session.removeAttribute(Member.INTERFACE_ATTRIBUTE_NAME);
		WebUtils.removeCookie(request, response, Member.USERNAME_COOKIE_NAME);
		return "redirect:/";
	}

}