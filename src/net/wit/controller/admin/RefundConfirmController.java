/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: RefundConfirmController.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月14日
 */
package net.wit.controller.admin;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.MemberBank;
import net.wit.entity.Payment;
import net.wit.entity.Refunds;
import net.wit.entity.Tenant;
import net.wit.exception.TenantException;
import net.wit.plugin.PaymentPlugin;
import net.wit.plugin.alipayMobile.config.AlipayConfig;
import net.wit.plugin.alipayMobile.util.AlipayRefund;
import net.wit.service.AdminService;
import net.wit.service.DeliveryCorpService;
import net.wit.service.OrderService;
import net.wit.service.PaymentMethodService;
import net.wit.service.PluginService;
import net.wit.service.RefundConfirmService;
import net.wit.service.ShippingMethodService;
import net.wit.service.YeePayService;
import net.wit.util.BizException;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月14日
 */
@Controller("refundConfirmController")
@RequestMapping({"/admin/refund_confirm"})
public class RefundConfirmController extends BaseController{
	  @Resource(name="refundConfirmServiceImpl")
	  private RefundConfirmService refundConfirmService;
	  
	  @Resource(name = "adminServiceImpl")
	  private AdminService adminService;
	  
	  @Resource(name = "yeePayServiceImpl")
	  private YeePayService yeePayService;
	  
	  @Resource(name = "orderServiceImpl")
	  private OrderService orderService;
	  
	  @Resource(name = "paymentMethodServiceImpl")
	  private PaymentMethodService paymentMethodService;
	  
	  @Resource(name = "shippingMethodServiceImpl")
	  private ShippingMethodService shippingMethodService;
	  
	  @Resource(name = "deliveryCorpServiceImpl")
	  private DeliveryCorpService deliveryCorpService;
	  
	  @Autowired
	  private PluginService pluginService;
	  

	  @RequestMapping(value={"/view"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String view(Long id, ModelMap model)
	  {
	    model.addAttribute("refunds", this.refundConfirmService.find(id));
	    return "/admin/refund_confirm/view";
	  }

	  @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String list(Pageable pageable, ModelMap model)
	  {
		  String searchValue = null;
			try {
				searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
				pageable.setSearchValue(searchValue);
			} catch (Exception localException) {
			}
		  model.addAttribute("page", this.refundConfirmService.findPage(adminService.getCurrent().getTenant(), pageable));
			return "/admin/refund_confirm/list";
	  }
	  
	  /**
		 * 查看退款（货）中订单详情
		 * @param id
		 * @param model
		 * @return
		 */
		@RequestMapping(value = { "/viewRefunds" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
		public String viewRefunds(Long id, ModelMap model) {
			model.addAttribute("methods", Payment.Method.values());
			model.addAttribute("refundsMethods", Refunds.Method.values());
			model.addAttribute("paymentMethods", this.paymentMethodService.findAll());
			model.addAttribute("shippingMethods", this.shippingMethodService.findAll());
			model.addAttribute("deliveryCorps", this.deliveryCorpService.findAll());
			model.addAttribute("order", this.orderService.find(id));
			return "/admin/refund_confirm/viewNew";
		}

	  @RequestMapping(value={"/confirm"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String confirm(String cashPwd,Long[] ids, RedirectAttributes redirectAttributes, HttpServletResponse response,
			  HttpServletRequest request, ModelMap model) throws BizException, TenantException
	  {
////		  String verifyCashPwd=adminService.verifyCashPwd(cashPwd);
//		  if(verifyCashPwd.equals("1")){
//			  if(ids==null)
//			  {
//				  addFlashMessage(redirectAttributes, NOT_SELECTED);
//				  return "redirect:list.jhtml";
//			  }
//			  List<Refunds> refundList= refundConfirmService.findList(ids);
//			  Map<String, String> params=new HashMap<String, String>();
//			  for(Refunds refunds:refundList){
//				  yeePayService.refund(params,refunds);
//			  }
//			  addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
//			  return "redirect:list.jhtml";
//		  }else if(verifyCashPwd.equals("0")){
//			  addFlashMessage(redirectAttributes, NOT_CASH_PWD);
//			  return "redirect:list.jhtml";
//		  }
		Admin admin = this.adminService.getCurrent();
		Tenant tenant = admin.getTenant();
		Set<MemberBank> memberBankSet = tenant.getMemberBanks();
		MemberBank memberBank = new MemberBank();
		if (memberBankSet != null && memberBankSet.iterator() != null && memberBankSet.iterator().hasNext()) {
			memberBank = memberBankSet.iterator().next();
		}
		// 调用支付宝接口
//		ServletContext context = request.getSession().getServletContext();
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin("alipayMobilePlugin");
		String refundNotifyUrl = paymentPlugin.getAttribute("refundNotifyUrl");
//		String refundNotifyUrl = context.getContextPath() + paymentPlugin.getAttribute("refundNotifyUrl");
		  
		  
		// 退款当天日期
		String refund_date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
		// 必填，格式：年[4位]-月[2位]-日[2位] 小时[2位 24小时制]:分[2位]:秒[2位]，如：2007-10-01
		// 13:13:13
		// 批次号
		String batch_no = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		// 必填，格式：当天日期[8位]+序列号[3至24位]，如：201008010000001
		// 退款笔数
		String batch_num = String.valueOf(ids.length);
		// 必填，参数detail_data的值中，“#”字符出现的数量加1，最大支持1000笔（即“#”字符出现的数量999个）
		// 退款详细数据
		List<Refunds> refundList = refundConfirmService.findList(ids);
		Refunds refunds = null;
		String detail_data = "";
		for (int i = 0, size = refundList.size(); i < size; i++) {
			refunds = refundList.get(i);
			Set<Payment> paymentSet = refunds.getOrder().getPayments();
			Payment payment = new Payment();
			if (paymentSet != null && paymentSet.iterator() != null && paymentSet.iterator().hasNext()) {
				payment = paymentSet.iterator().next();
			}
			if (i == 0) {// 第一条记录前面不需要“#”
				detail_data = detail_data + payment.getSn() + "^"
//						+ refunds.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP) + "^" + "订单协商退款";
						+ refunds.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP) + "^" + "退款";
			} else {
				detail_data = detail_data + "#" + payment.getSn() + "^"
//						+ refunds.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP) + "^" + "订单协商退款";
						+ refunds.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP) + "^" + "退款";
			}
			// 必填，具体格式请参见接口技术文档
		}

		// 把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		try {
			sParaTemp.put("service", new String("refund_fastpay_by_platform_pwd".getBytes("ISO-8859-1"),"UTF-8"));
			sParaTemp.put("partner", new String(memberBank.getBankProvince().getBytes("ISO-8859-1"),"UTF-8"));
			sParaTemp.put("_input_charset", new String(AlipayConfig.input_charset.getBytes("ISO-8859-1"),"UTF-8"));
			sParaTemp.put("notify_url", new String(refundNotifyUrl.getBytes("ISO-8859-1"),"UTF-8"));
			sParaTemp.put("seller_email", new String(memberBank.getCardNo().getBytes("ISO-8859-1"),"UTF-8"));
			sParaTemp.put("refund_date", new String(refund_date.getBytes("ISO-8859-1"),"UTF-8"));
			sParaTemp.put("batch_no", new String(batch_no.getBytes("ISO-8859-1"),"UTF-8"));
			sParaTemp.put("batch_num", new String(batch_num.getBytes("ISO-8859-1"),"UTF-8"));
			sParaTemp.put("detail_data", detail_data);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		// 建立请求
		String sHtmlText = AlipayRefund.buildRequest(sParaTemp, "get", "submit", memberBank.getDepositBank());
		model.addAttribute("sHtmlText", sHtmlText);
//		sHtmlText = AlipayRefund.buildRequest("","",sParaTemp, memberBank.getDepositBank());
//		try {
//			response.getOutputStream().println(sHtmlText);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return "/admin/refund_confirm/payment";
	  }
	  
	  @RequestMapping(value={"/confirmAll"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String  confirmAll(String cashPwd, RedirectAttributes redirectAttributes) throws BizException, TenantException
	  {
		  String verifyCashPwd=adminService.verifyCashPwd(cashPwd);
		  if(verifyCashPwd.equals("1")){
		        List<Refunds> refundList= refundConfirmService.listAll(adminService.getCurrent().getTenant());
		        Map<String, String> params=new HashMap<String, String>();
		        for(Refunds refunds:refundList){
			    yeePayService.refund(params,refunds);
		  }
		        addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		        return "redirect:list.jhtml";
		  }else if(verifyCashPwd.equals("0")){
			  addFlashMessage(redirectAttributes, NOT_CASH_PWD);
			  return "redirect:list.jhtml";
		  }
		return "redirect:list.jhtml";
	  }
	  /**
	   * 判断是否设置支付密码
	   * @param request
	   * @return
	   */
	  @RequestMapping(value={"/pwdIsNull"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  @ResponseBody
	  public String pwdIsNull(HttpServletRequest request)
	  {
		  Admin admin = adminService.getCurrent();
		  if(admin.getCashPwd()!=null&&!(admin.getCashPwd().equals(""))){
			  return "0";//支付密码不为空
		  }
		  return "1";//支付密码为空
	  }
	  /**
	   * 设置支付密码
	   * @param request
	   * @return
	 * @throws TenantException 
	   */
	  @RequestMapping(value={"/savePwd"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  @ResponseBody
	  public String savePwd(String setPwd,HttpServletRequest request) throws TenantException
	  {
		  Admin admin = adminService.getCurrent();
		  try{
			  String setting=adminService.settingCashPwd(admin, setPwd);
			  return setting;
		  }catch(Exception e){
			  return "0";//支付密码设置失败
		  }
	  }
	  
}
