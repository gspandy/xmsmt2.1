/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantShopkeeperController.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月20日
 */
package net.wit.controller.admin;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.Member;
import net.wit.entity.MemberBank;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.Tenant;
import net.wit.entity.TenantShopkeeper;
import net.wit.entity.TenantShopkeeper.InvitedType;
import net.wit.entity.TenantShopkeeper.IsShopkeeper;
import net.wit.plugin.alipayMobile.sign.MD5;
import net.wit.service.AdminService;
import net.wit.service.MemberService;
import net.wit.service.OrderService;
import net.wit.service.TenantShopkeeperService;
import net.wit.vo.TenantShopkeeperVO;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月20日
 */
@Controller("tenantShopkeeperController")
@RequestMapping({"/admin/tenant_shopkeeper"})
public class TenantShopkeeperController extends BaseController{
	
	 @Resource(name="tenantShopkeeperServiceImpl")
	 private TenantShopkeeperService tenantShopkeeperService;
	 
	  @Resource(name="adminServiceImpl")
	  private AdminService adminService;
	  
	  @Resource(name="orderServiceImpl")
	  private OrderService orderService;
	  
	  @Resource(name="memberServiceImpl")
	  private MemberService memberService;

	 @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String list(Pageable pageable, ModelMap model)
	  {
		 String searchValue = null;
			try {
				searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
				pageable.setSearchValue(searchValue);
			} catch (Exception localException) {
			}
		 Admin admin = adminService.getCurrent();
		 Page<TenantShopkeeper> page=this.tenantShopkeeperService.findPage(admin.getTenant(),IsShopkeeper.yes, null , pageable);
		 model.addAttribute("tenant", admin.getTenant());
	     model.addAttribute("page",page);
	    return "/admin/tenant_shopkeeper/list";
	  }
	 @RequestMapping(value={"/vipInviteList"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	 public String vipInviteList(Long [] ids,Boolean isPass,Pageable pageable, ModelMap model)
	 {
		 if(ids!=null && isPass!=null){
			 tenantShopkeeperService.processApply(ids, isPass);
		 }
		 String searchValue = null;
		 try {
			 searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
			 pageable.setSearchValue(searchValue);
		 } catch (Exception localException) {
		 }
		 List<TenantShopkeeperVO> list=new ArrayList<TenantShopkeeperVO>();
		 Admin admin = adminService.getCurrent();
		 Tenant tenant = admin.getTenant();
		 Page<TenantShopkeeper> page=this.tenantShopkeeperService.findPage(tenant,IsShopkeeper.capable, InvitedType.tenant , pageable);
		 if(page.getContent()!=null && page.getContent().size()>0){
			 for (TenantShopkeeper tenantShopkeeper : page.getContent()) {
				 TenantShopkeeperVO tenantShopkeeperVO = new TenantShopkeeperVO();
				 Member member = tenantShopkeeper.getMember();
				 tenantShopkeeperVO.setId(BigInteger.valueOf(tenantShopkeeper.getId()));
				 if(member!=null && !"".equals(member)){
					    List<OrderStatus>  orderStatuses = new ArrayList<OrderStatus>();
						orderStatuses.add(OrderStatus.confirmed);
						orderStatuses.add(OrderStatus.completed);
						List<PaymentStatus> paymentStatuses = new ArrayList<PaymentStatus>();
						paymentStatuses.add(PaymentStatus.paid);

					 BigDecimal amount = orderService.getHistoryOrderAmtByTenant(tenantShopkeeper.getTenant(), member, orderStatuses, paymentStatuses);
					 tenantShopkeeperVO.setAmount(amount);
					 tenantShopkeeperVO.setName(member.getNickName());
					 tenantShopkeeperVO.setMobile(member.getMobile());
					 list.add(tenantShopkeeperVO);
				 }
				 
			}
		 }
		 
		 model.addAttribute("tenant", tenant);
		 model.addAttribute("page",new Page<>(list, page.getTotal(), page.getPageable()));
		 return "/admin/tenant_shopkeeper/vip_invite";
	 }
	 //查询
	 @RequestMapping(value={"/search"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String search(String memberName,Pageable pageable, ModelMap model)
	  {
//		 String searchValue = null;
		 String mobile = "";
			try {
				if("mobile".equals(pageable.getSearchProperty())){
					mobile = pageable.getSearchValue();
				}else if("name".equals(pageable.getSearchProperty())){
					memberName = pageable.getSearchValue();
				}
				pageable.setSearchProperty(null);
			} catch (Exception localException) {
			}
		 Admin admin = adminService.getCurrent();
	    model.addAttribute("page", this.tenantShopkeeperService.findPageSearch(memberName, admin.getTenant(),pageable,mobile));
	    model.addAttribute("memberName", memberName);
	    return "/admin/tenant_shopkeeper/list";
	  }
	 
	 /**
	  * 查看VIP详情
	  */
	 @RequestMapping(value={"/view"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	 public String view(Long id, Model model) {
		 
		 TenantShopkeeper tenantShopkeeper = tenantShopkeeperService.find(id);
		 String cardNo = "";
		 Set<MemberBank> set = tenantShopkeeper.getMember().getMemberBanks();
			if (set != null && set.iterator() != null
					&& set.iterator().hasNext()) {
				cardNo = set.iterator().next().getCardNo();
			}
		 model.addAttribute("tenantShopkeeper", tenantShopkeeper);
		 model.addAttribute("cardNo", cardNo);
		 return "/admin/tenant_shopkeeper/view";
		 
	 }
	 
	 /**
	  * 密码重置
	  */
	 @RequestMapping(value={"/reset"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	 public String reset(Long id, Model model,RedirectAttributes redirectAttributes) {
		 
		 TenantShopkeeper tenantShopkeeper = tenantShopkeeperService.find(id);
		 Member	member = tenantShopkeeper.getMember();
		 member.setCashPwd(MD5.getMD5Str("123456"));
		 memberService.update(member);
		 
		 addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		 return "redirect:list.jhtml";
	 }
	 
}
