package net.wit.controller.shop;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.wit.controller.shop.BaseController;
import net.wit.entity.Member;
import net.wit.service.MemberService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("shopIncludeController")
@RequestMapping("/")
public class IncludeController extends BaseController{

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	/**
	 * 脚头
	 */
	@RequestMapping(value = "/header",method = RequestMethod.GET)
	public String header(String redirectUrl, HttpServletRequest request, ModelMap model) {
		Member member = memberService.getCurrent();
		model.addAttribute("member", member);
		return "/shop/include/singleheader";
	}
	/**
	 * 脚脚
	 */
	@RequestMapping(value = "/footer",method = RequestMethod.GET)
	public String foolter(String redirectUrl, HttpServletRequest request, ModelMap model) {
		Member member = memberService.getCurrent();
		model.addAttribute("member", member);
		return "/shop/include/singlefooter";
	}
}
