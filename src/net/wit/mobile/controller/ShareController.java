/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: ShareController.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	17 Sep,2015
 */
package net.wit.mobile.controller;

import java.text.ParseException;
import java.util.Date;

import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.entity.TenantTicket;
import net.wit.entity.Ticket;
import net.wit.service.MemberService;
import net.wit.service.PicService;
import net.wit.service.TenantService;
import net.wit.service.TenantTicketService;
import net.wit.service.TicketService;
import net.wit.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 分享控制层
 * @author: weihuang.peng
 * @version Revision: 0.0.1
 * @Date: 17 Sep,2015
 */
@Controller
@RequestMapping("/share")
public class ShareController {

	@Autowired
	private TenantService tenantService;
	@Autowired
	private TicketService ticketService;
	@Autowired
	private PicService picService;
	@Autowired
	private TenantTicketService tenantTicketService;
	
	@Autowired
	private MemberService memberService;
	
	
	/**
	 * 跳转至产品列表
	 * @author: weihuang.peng
	 * @param tenantId 企业ID
	 * @return 产品列表页面
	 */
	@RequestMapping("/product")
	public String product(@RequestParam Long tenantId, Model model) {
		Tenant tenant = tenantService.find(tenantId);
		model.addAttribute("tenantId", tenantId);
		model.addAttribute("tenantName", tenant.getShortName());
		return "mobile/share/product";
	}
	
	/**
	 * 分享券
	 * @author: weihuang.peng
	 * @param ticketId
	 * @param model
	 * @return
	 */
	@RequestMapping("/ticket")
	public String ticket(@RequestParam Long ticketId, Model model) {
		Ticket ticket = ticketService.find(ticketId);
		Tenant tenant =ticket.getTenant();
		String imageUrl = picService.getTickShareImageByTenant(tenant);  //邀请页图片
		String headImage = ticket.getShopkeeper().getHeadImg()==null?  
				picService.getDefaultHeadImage(ticket.getShopkeeper()):
					ticket.getShopkeeper().getHeadImg();   //分享人头像
	    String shopKeeperName =ticket.getShopkeeper().getName()==null?ticket.getShopkeeper().getNickName():
	    	ticket.getShopkeeper().getName(); 			//分享人名称
	    if(StringUtils.isEmpty(shopKeeperName)){
	    	shopKeeperName = "指帮用户";
	    }
	    String tenantName = ticket.getTenant().getShortName(); //企业名称
	    String ticketLogo ;    //券logo;
	    TenantTicket tenantTicket = tenantTicketService.findTenantTicketByTenantId(ticket.getTenant().getId());
	    if(ticket.getStatus()==Ticket.Status.recevied)
	    	ticketLogo = tenantTicket.getExpiryImage();
	    else ticketLogo = tenantTicket.getEffectiveImage();
	    String ticketContent = tenantTicket.getContent();  //券内容 :乔丹专属内购券......
	    int remmaingDays = 0;   //剩余天数;
	    try {
			 remmaingDays= DateUtil.differDay(ticket.getExpiryDate(), new Date());
		} catch (ParseException e) {
			remmaingDays = 0;
			e.printStackTrace();
		}
	    
	    model.addAttribute("ticketId", ticketId);//券ID
	    model.addAttribute("imageUrl", imageUrl);//邀请页图片
	    model.addAttribute("headImage", headImage);//分享人头像
	    model.addAttribute("shopKeeperName", shopKeeperName);//分享人名称
	    model.addAttribute("tenantName", tenantName);//企业名称
	    model.addAttribute("tenantId", tenant.getId());//企业名称
	    model.addAttribute("ticketLogo", ticketLogo);//券logo;
	    model.addAttribute("ticketContent", ticketContent);//券内容 :乔丹专属内购券......
	    model.addAttribute("remmaingDays", remmaingDays);//剩余天数;
	    model.addAttribute("ticketStatus", ticket.getStatus() == Ticket.Status.nouse);//状态
	    /**
	     * ticket.getStatus() == Ticket.Status.nouse;  可以领取
	     * 
	     * ticket.getStatus() == Ticket.Status.recevied 已经领取;
	     * 
	     */
		return "mobile/share/ticket";
	}
	
	@RequestMapping("/tenantInvite")
	public String tenantInvite(@RequestParam Long tenantId ,Model model){
		Tenant tenant = tenantService.find(new Long(tenantId));
		String inviationImage = picService.getTenantInviteImage(tenant);
		String tenantLogo = tenant.getLogo();
		String tenantName = tenant.getShortName();
		
		model.addAttribute("inviationImage", inviationImage);
		model.addAttribute("tenantLogo", tenantLogo);
		model.addAttribute("tenantName", tenantName);
		model.addAttribute("tenantId", tenantId);
		return "mobile/share/tenantInvite";
	}
	
	@RequestMapping("/shopKeeperInvite")
	public String tenantInvite(@RequestParam Long shopKeeperId ,Long tenantId,Model model){
		Member shopKeeper = memberService.find(new Long(shopKeeperId));
		Tenant tenant = tenantService.find(tenantId);
		String inviationImage = picService.getShopKeeperInviteImage(tenant);
		String tenantLogo = tenant.getLogo();
		String tenantName = tenant.getShortName();
		model.addAttribute("shopKeeper", shopKeeper);
		model.addAttribute("inviationImage", inviationImage);
		model.addAttribute("tenantLogo", tenantLogo);
		model.addAttribute("tenantName", tenantName);
		return "mobile/share/shopKeeperInvite";
	}
	
	@RequestMapping("/download")
	public String download() {
		return "mobile/share/download";
	}
}
