/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: MemberBankController.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月14日
 */
package net.wit.controller.admin;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.MemberBank;
import net.wit.entity.Tenant;
import net.wit.plugin.PaymentPlugin;
import net.wit.service.AdminService;
import net.wit.service.MemberBankService;
import net.wit.service.PluginService;
import net.wit.service.YeePayService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月14日
 */
@Controller("memberBankController")
@RequestMapping({"/admin/member_bank"})
public class MemberBankController extends BaseController{
	
	 @Resource(name="adminServiceImpl")
	  private AdminService adminService;
	 
	 @Resource(name="memberBankServiceImpl")
	  private MemberBankService memberBankService;
	 
	 @Resource(name="yeePayServiceImpl")
	  private YeePayService yeePayService; 
	 
	 @Autowired
	  private PluginService pluginService;
	 
	@RequestMapping(value = { "/memberBankEdits" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String memberBankEdits(Pageable pageable, ModelMap model) {
		 String searchValue = null;
			try {
				searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
				pageable.setSearchValue(searchValue);
			} catch (Exception localException) {
			}
		  Admin admin = adminService.getCurrent();
		model.addAttribute("memberBank", memberBankService.getMemberBankByTenantId(admin.getTenant().getId(), MemberBank.Type.debit));// 收款支付宝账号
		model.addAttribute("memberBankPay", memberBankService.getMemberBankByTenantId(admin.getTenant().getId(), MemberBank.Type.credit));// 付款支付宝账号
		return "/admin/member_bank/memberBankEdits";
	}
	
    @RequestMapping(value={"/saveMemberBankEdits"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	 public String saveMemberBankEdits(MemberBank memberBank,RedirectAttributes redirectAttributes,
			 Long idPay, String depositUserPay, String cardNoPay, String bankProvincePay, String depositBankPay, String bankCityPay)
	 {
    	try {
    		Admin admin = adminService.getCurrent();
    		Tenant tenant = admin.getTenant();
   		  
//    		Map<String, String> params = new HashMap<String, String>();
//    		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin("yeepayPlugin");
//          String accountNotify = paymentPlugin.getAttribute("accountNotify");
//        	params.put("callbackurl", accountNotify);// 后台通知地址
//    		params.put("tenantId", tenant.getId().toString());// 会员ID
//    		params.put("customertype", "ENTERPRISE");// 类型为企业
//    		params.put("name", memberBank.getDepositUser());// 真实姓名
//    		params.put("legalperson", tenant.getLegalRepr());// 法人代表
//    		params.put("businesslicence", tenant.getLicenseCode());// 营业执照号码
//         	params.put("bankname", memberBank.getDepositBank());// 开户银行
//         	params.put("bankaccountnumber", memberBank.getCardNo());// 银行卡号（储蓄卡）
//         	params.put("bankprovince", memberBank.getBankProvince());// 省份
//         	params.put("bankcity", memberBank.getBankCity());// 城市
//         	
//         	if (memberBank.getId() == null) {
//         		this.yeePayService.registerAccount(params);
//         		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
//         	} else {
//         		params.put("bankId", memberBank.getId().toString());
//         		this.yeePayService.updateAccount(params);
//         		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
//         	}
         	
         	if (memberBank.getId() != null) {
         		memberBank.setTenant(tenant);
         		this.memberBankService.update(memberBank);
         		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
         	} else {
         		// 保存收款支付宝账号
//             	memberBank.setCardNo(bankaccountnumber);
//    			memberBank.setDepositBank(bankname);// 开户行
//    			memberBank.setDepositUser(accountname);// 开户名
//    			memberBank.setMember(member);
//    			memberBank.setRepaymentDay(new Integer(riskreserveday));// 提现周期??
    			memberBank.setTenant(tenant);
    			memberBank.setType(MemberBank.Type.debit);// 保存为借记卡(收款支付宝)
    			memberBank.setValidity(new Date());
//    			memberBank.setBankCity(bankcity);// 开户市
//    			memberBank.setBankProvince(bankprovince);// 开户省
    			this.memberBankService.save(memberBank);
    			
         		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
         	}
         	if (idPay != null) {
         		MemberBank memberBankPay = this.memberBankService.find(idPay);
         		memberBankPay.setTenant(tenant);
         		memberBankPay.setDepositUser(depositUserPay);
         		memberBankPay.setCardNo(cardNoPay);
         		memberBankPay.setBankProvince(bankProvincePay);
         		memberBankPay.setDepositBank(depositBankPay);
         		memberBankPay.setBankCity(bankCityPay);
         		this.memberBankService.update(memberBankPay);
         		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
         	} else {
    			// 保存付款支付宝账号
    			MemberBank memberBankPay = new MemberBank();
         		memberBankPay.setTenant(tenant);
         		memberBankPay.setDepositUser(depositUserPay);
         		memberBankPay.setCardNo(cardNoPay);
         		memberBankPay.setBankProvince(bankProvincePay);
         		memberBankPay.setDepositBank(depositBankPay);
         		memberBankPay.setBankCity(bankCityPay);
         		memberBankPay.setValidity(new Date());
         		memberBankPay.setTenant(tenant);
         		memberBankPay.setType(MemberBank.Type.credit);// 保存为信用卡（付款支付宝）
         		this.memberBankService.save(memberBankPay);
         		
         		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
         	}
         	
         	
    	} catch (Exception e) {
    		System.out.print(e.getMessage());
//    		addFlashMessage(redirectAttributes, NOT_BANK);
    		addFlashMessage(redirectAttributes, Message.error("支付宝绑定失败！", new Object[0]));
    		
    	}
    	return "redirect:memberBankEdits.jhtml";
	  }
    
}
