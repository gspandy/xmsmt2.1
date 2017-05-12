package net.wit.mobile.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.wit.entity.MemberBank;
import net.wit.entity.Order;
import net.wit.entity.Payment;
import net.wit.entity.Payment.Status;
import net.wit.entity.Refunds;
import net.wit.mobile.bo.NToken;
import net.wit.mobile.cache.CacheUtil;
import net.wit.mobile.service.INTokenBS;
import net.wit.mobile.service.impl.PushService;
import net.wit.mobile.util.rong.models.SystemMessage;
import net.wit.plugin.PaymentPlugin;
import net.wit.plugin.alipayMobile.util.AlipayNotify;
import net.wit.service.ChargeService;
import net.wit.service.OrderService;
import net.wit.service.OwnerServerice;
import net.wit.service.PaymentService;
import net.wit.service.PluginService;
import net.wit.service.RefundsService;
import net.wit.service.RentService;
import net.wit.service.TenantShopkeeperService;
import net.wit.service.YeePayService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.ld.slf4j.Logger;
import cn.ld.slf4j.LoggerFactory;

/**
 * 资金管理（处理第三方支付回调的接口）
 * @author Teddy
 */
@Controller
@RequestMapping(value = "/fund")
public class FundController extends BaseController {

	private Logger log = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private INTokenBS inTokenBS;
	@Autowired
	private YeePayService yeePayService;
	@Autowired
	private OwnerServerice ownerServerice;
	@Autowired
	private PluginService pluginService;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private RentService rentService;
	@Autowired
	private ChargeService chargeService;
	@Autowired
	private RefundsService refundsService;
	@Autowired
	private PushService pushService;
	@Autowired
	private TenantShopkeeperService tenantShopkeeperService;
	

	/**
	 * 订单支付后台通知URL-支付宝
	 */
	@RequestMapping(value = "/paymentNotify")
	public void paymentNotify(HttpServletResponse response, HttpServletRequest request) throws Exception {
		// 获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		// 商户订单号
		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
		Order order = this.orderService.findBySn(out_trade_no);
		Set<MemberBank> memberBankSet = order.getTenant().getMemberBanks();
		MemberBank memberBank = new MemberBank();
		if (memberBankSet != null && memberBankSet.iterator() != null && memberBankSet.iterator().hasNext()) {
			memberBank = memberBankSet.iterator().next();
//			params.put("partner", memberBank.getBankProvince());
		}
//		// 支付宝交易号
		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
		// 交易状态
		String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
		if (AlipayNotify.verify(params, memberBank.getBankProvince())) {// 验证成功
			// 请在这里加上商户的业务逻辑程序代码
			// ——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
			Payment payment = null;
			Set<Payment> paymentSet = order.getPayments();
			if (paymentSet != null && paymentSet.iterator() != null && paymentSet.iterator().hasNext()) {
				payment = paymentSet.iterator().next();
			}
			if (trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")) {
				if (payment != null && !Status.success.equals(payment.getStatus())) {
					PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(payment.getPaymentPluginId());
					if (paymentPlugin != null) {
						try {
							// 进行分账支付单处理(无需分账)
							payment.setSn(trade_no);
							payment.setMemo(trade_no);
//							paymentService.handle(payment);
							paymentService.alipayHandle(payment, trade_no);
						} catch (Exception e) {
							// 不做处理
							e.printStackTrace();
						}
					}
				}
				response.getOutputStream().println("success");
				// 给会员发送消息
				try {
					pushService.publishSystemMessage(order.getTenant(), order.getMember(), 
							SystemMessage.buyerOrderPaymentMsg(order.getTenant().getShortName(), order.getSn()));
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

				try { // 签收成功后获取邀请函资格
					tenantShopkeeperService.beCapableShopkeeper(order.getTenant(), order.getMember());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
//				response.getOutputStream().println("fail");
//				payment.setStatus(Status.failure);
//				payment.setPaymentDate(new Date());
//				paymentService.update(payment);
			}
		}
	}

	/**
	 * 提现后台通知URL(转账)
	 */
	@RequestMapping(value = "/cashNotify")
	public void cashNotify(HttpServletResponse response, HttpServletRequest request) throws Exception {
		try {
			Map<String, String> params = new HashMap<String, String>();
			Map requestParams = request.getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
				}
				// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
				// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
				params.put(name, valueStr);
			}
			// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
			// 商户订单号
//			Set<MemberBank> memberBankSet = order.getTenant().getMemberBanks();
//			MemberBank memberBank = new MemberBank();
//			if (memberBankSet != null && memberBankSet.iterator() != null && memberBankSet.iterator().hasNext()) {
//				memberBank = memberBankSet.iterator().next();
//			}
			if(request.getParameter("success_details") != null){
//				String success_details = new String(request.getParameter("success_details").getBytes("ISO-8859-1"), "UTF-8");
				String success_details = new String(params.get("success_details"));
//				String fail_details = new String(request.getParameter("fail_details").getBytes("ISO-8859-1"), "UTF-8");
//				String result = "FAIL";

//				if (AlipayTransNotify.verify(params, memberBank.getDepositBank(), memberBank.getBankProvince())) {// 验证成功
					// 调用提现的功能 TODO
					this.chargeService.cashAdviceBack(success_details);
					response.getOutputStream().println("success");
//				}
			}
			
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 确认退款后台通知URL
	 */
	@RequestMapping(value = "/refundNotify")
	public void refundNotify(HttpServletResponse response, HttpServletRequest request) throws Exception {
		try {
			Map<String, String> params = new HashMap<String, String>();
			Map requestParams = request.getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
				}
				// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
				// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
				params.put(name, valueStr);
			}
			// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
			// 商户订单号
			String result_details = params.get("result_details");
			
//			if (AlipayRefundNotify.verify(params, memberBank.getDepositBank(), memberBank.getBankProvince())) {// 验证成功
				// 调用退款的功能
				if(StringUtils.isNotBlank(result_details)){
					String[] resultDetail = result_details.split("\\#");
					for(int i = 0;i < resultDetail.length ; i++){
						String detail = resultDetail[i];
						String[] s = detail.split("\\^");
						String txNo = s[0];// 支付宝原交易流水号，与payment.sn匹配
						String status = s[2];
						if("SUCCESS".equals(status) || "success".equals(status)) {
							Payment payment = this.paymentService.findBySn(txNo);
							Set<Refunds> refundsSet = payment.getOrder().getRefunds();
							Refunds refunds = null;
							if (refundsSet != null && refundsSet.iterator() != null && refundsSet.iterator().hasNext()) {
								refunds = refundsSet.iterator().next();
							}
							this.refundsService.refurns(refunds, payment.getOrder().getMember().getUsername());
							
							// 给会员发送消息
							pushService.publishSystemMessage(refunds.getOrder().getTenant(), refunds.getOrder().getMember(), 
									SystemMessage.buyerOrderMoneyReturnMsg(refunds.getOrder().getTenant().getShortName(),
											refunds.getOrder().getSn(), refunds.getOrder().getAmountPaid().toString()));
						}
					}
				}
				response.getOutputStream().println("success");
//			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 店主- 交租后台通知
	 * 
	 * @param response
	 * @param token
	 * @param rent
	 * @param amount
	 * @throws Exception
	 */
	@RequestMapping(value = "/payRentNotify")
	public void payRentNotify(HttpServletResponse response, HttpServletRequest request) throws Exception {
		try {
			Map<String, String> params = new HashMap<String, String>();
			Map requestParams = request.getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
				}
				// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
				// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
				params.put(name, valueStr);
			}
			// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
			// 商户订单号
			String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
			String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");

			if (AlipayNotify.verify(params, CacheUtil.getParamValueByName("PARTNER"))) {// 验证成功
				if (trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")) {
					// 更新交租记录的状态为“已支付”
					rentService.adviceBack(out_trade_no);
					response.getOutputStream().println("success");
				}
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, "交租失败! 请联系平台客服咨询");
		}
	}
	
	/**
	 * 店主提现（弃用）
	 */
	@RequestMapping(value = "/cashTransfer")
	public void cashTransfer(HttpServletResponse response, @RequestParam("token") String token,
			@RequestParam("amount") String amount, @RequestParam("idcard") String idcard,
			@RequestParam("bankname") String bankname, @RequestParam("bankaccountnumber") String bankaccountnumber
	) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();
				String ledgerno = nToken.getLedgerno();

				// 客户端传来的数据
				Map<String, String> params = new HashMap<String, String>();
				params.put("memberId", memberId);// 会员ID
				params.put("ledgerno", ledgerno);// 子账户编号
				params.put("amount", amount);// 省份

				PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin("yeepayPlugin");
				String cashNotifyUrl = paymentPlugin.getAttribute("cashNotifyUrl");
				params.put("callbackurl", cashNotifyUrl);// 提现后台地址,从插件参数中获取
				yeePayService.cashTransfer(params);

				// 判断提现金额是否低于或等于账户余额
				// Owner owner=ownerServerice.getOwner(new Long(memberId));
				// BigDecimal newAmount=new BigDecimal(amount);
				// int
				// amountType=owner.getAccountBalance().compareTo(newAmount);
				// if(amountType==0||amountType==1){
				//
				// this.handleJsonResponse(response, true, "提现成功");
				// }else if(amountType==-1){
				// this.handleJsonResponse(response, true, "提现金额不足");
				// }

			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}

}
