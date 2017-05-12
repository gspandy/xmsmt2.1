package net.wit.interceptor;

import java.net.URLEncoder;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.wit.Principal;
import net.wit.entity.Member;
import net.wit.service.MemberService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class MemberInterceptor extends HandlerInterceptorAdapter {
	private static final String REDIRECT_VIEW_NAME_PREFIX = "redirect:";

	private static final String REDIRECT_URL_PARAMETER_NAME = "redirectUrl";

	private static final String MEMBER_ATTRIBUTE_NAME = "member";

	private static final String DEFAULT_LOGIN_URL = "/login.jhtml";

	private String loginUrl = "/login.jhtml";

	@Value("${url_escaping_charset}")
	private String urlEscapingCharset;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();
		Principal principal = (Principal) session.getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
		if (principal != null) {
			return true;
		}
		String requestType = request.getHeader("X-Requested-With");
		if ((requestType != null) && (requestType.equalsIgnoreCase("XMLHttpRequest"))) {
			response.addHeader("loginStatus", "accessDenied");
			response.sendError(403);
			return false;
		}
		if (request.getMethod().equalsIgnoreCase("GET")) {
			String redirectUrl = request.getQueryString() != null ? request.getRequestURI() + "?" + request.getQueryString() : request.getRequestURI();
			String path = redirectUrl.substring(request.getContextPath().length() + 1);
			String[] split = path.split("/");
			String base = "/" + split[0];
			if ((base.equals("/vbox")) || (base.equals("/pad")) || (base.equals("/vu")) || (base.equals("/tenant")) || (base.equals("/box")) || (base.equals("/b2c")) || (base.equals("/b2b")))
				response.sendRedirect(request.getContextPath() + base + this.loginUrl + "?" + "redirectUrl" + "=" + URLEncoder.encode(redirectUrl, this.urlEscapingCharset));
			else
				response.sendRedirect(request.getContextPath() + this.loginUrl + "?" + "redirectUrl" + "=" + URLEncoder.encode(redirectUrl, this.urlEscapingCharset));
		} else {
			String redirectUrl = request.getRequestURI().toString();
			String path = redirectUrl.substring(request.getContextPath().length() + 1);
			String[] split = path.split("/");
			String base = "/" + split[0];
			if ((base.equals("/vbox")) || (base.equals("/pad")) || (base.equals("/vu")) || (base.equals("/tenant")) || (base.equals("/box")) || (base.equals("/b2c")) || (base.equals("/b2b")))
				response.sendRedirect(request.getContextPath() + base + this.loginUrl);
			else {
				response.sendRedirect(request.getContextPath() + this.loginUrl);
			}
		}
		return false;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if (modelAndView != null) {
			String viewName = modelAndView.getViewName();
			if (!StringUtils.startsWith(viewName, "redirect:"))
				modelAndView.addObject("member", this.memberService.getCurrent());
		}
	}

	public String getLoginUrl() {
		return this.loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}
}