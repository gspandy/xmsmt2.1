/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: ServiceController.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	12 Sep,2015
 */
package net.wit.mobile.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.wit.entity.Admin;
import net.wit.entity.CustomService;
import net.wit.entity.Member;
import net.wit.mobile.service.impl.PushService;
import net.wit.service.AdminService;
import net.wit.service.CustomServiceService;
import net.wit.service.MemberService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author: weihuang.peng
 * @version Revision: 0.0.1
 * @Date: 12 Sep,2015
 */
@Controller("customerServerController")
@RequestMapping("/admin/customerServer")
public class CustomerServerController extends BaseController {

	@Resource(name="adminServiceImpl")
	private AdminService adminService;
	@Resource(name="pushService")
	private PushService pushService;
	
	@Autowired
	private CustomServiceService customServiceService;
	
	@Autowired
	private MemberService memberService;
	
	@RequestMapping("/server")
	public String server(HttpServletResponse response, HttpServletRequest request, Model model) throws Exception {
		Admin admin = adminService.getCurrent();
		CustomService cs = customServiceService.findCustomServiceByAdmin(admin);
		String userId = ServiceController.SERVICE_PREFIX+admin.getId();
		String userName = cs.getServiceName();
		String headImage = cs.getServiceImg();
		model.addAttribute("userId", userId);
		model.addAttribute("username", userName);
		model.addAttribute("headImage", headImage);
		pushService.refreshToken(userId, userName, headImage);
		String token = cs.getToken();
		if (token == null) {
			
		}
		model.addAttribute("token", token);
		return "/admin/common/server";
	}
	
    @RequestMapping(value={"/getMember"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Map  ajaxAdd(String memberId,HttpServletRequest request)
    {
	    Member member = memberService.find(new Long(memberId));
	    Map map= new HashMap();
	    map.put("nickName", member.getNickName());
	    map.put("mobile", member.getMobile());
	  	 return map;
    }
	
	
	
}