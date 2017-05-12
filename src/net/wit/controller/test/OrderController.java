package net.wit.controller.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import net.wit.CancelExcelView;
import net.wit.Message;
import net.wit.RefundIngExcelView;
import net.wit.RefundedExcelView;
import net.wit.ReturnIngExcelView;
import net.wit.ShippedExcelView;
import net.wit.SignedExcelView;
import net.wit.WaitPayExcelView;
import net.wit.WaitShippingExcelView;
import net.wit.controller.shop.BaseController;
import net.wit.dao.OrderItemDao;
import net.wit.entity.Member;
import net.wit.entity.MemberRank;
import net.wit.entity.Order;
import net.wit.entity.OrderItem;
import net.wit.entity.Payment;
import net.wit.entity.PaymentMethod;
import net.wit.entity.Product;
import net.wit.entity.Receiver;
import net.wit.entity.Returns;
import net.wit.entity.ShippingMethod;
import net.wit.entity.Sn;
import net.wit.entity.Tenant;
import net.wit.entity.Ticket;
import net.wit.enums.OrderSearchStatus;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.Order.ShippingStatus;
import net.wit.entity.Payment.Method;
import net.wit.entity.Payment.Status;
import net.wit.entity.Payment.Type;
import net.wit.exception.OrderException;
import net.wit.plugin.PaymentPlugin;
import net.wit.service.MemberService;
import net.wit.service.OrderService;
import net.wit.service.PaymentMethodService;
import net.wit.service.PaymentService;
import net.wit.service.PluginService;
import net.wit.service.ProductService;
import net.wit.service.ReceiverService;
import net.wit.service.ShippingMethodService;
import net.wit.service.SnService;
import net.wit.service.TenantService;
import net.wit.service.TicketService;
import net.wit.util.DateUtil;

/**
 * @ClassName：OrderController @Description：
 * @author：Chenlf
 * @date：2015年9月11日 下午5:03:21
 */
@Controller
@RequestMapping(value = "/test")
public class OrderController extends BaseController {

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;

	@Resource(name = "paymentServiceImpl")
	private PaymentService paymentService;

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "shippingMethodServiceImpl")
	private ShippingMethodService shippingMethodService;

	@Resource(name = "paymentMethodServiceImpl")
	private PaymentMethodService paymentMethodService;

	@Resource(name = "receiverServiceImpl")
	private ReceiverService receiverService;

	@Resource(name = "ticketServiceImpl")
	private TicketService ticketService;

	@Resource(name = "snServiceImpl")
	private SnService snService;

	@Resource(name = "pluginServiceImpl")
	private PluginService pluginService;

	@Resource(name = "orderItemDaoImpl")
	private OrderItemDao orderItemDao;

	@Resource(name = "tenantServiceImpl")
	private TenantService tenantService;

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String testIndex() {
		return "/test/index";

	}

	@RequestMapping(value = "/payemnt", method = RequestMethod.GET)
	public String payemnt(String sn, String paymentPluginId, Model model) {
		Payment payment = new Payment();
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		Order order = orderService.findBySn(sn);
		if (order == null || order.isExpired()) {
			return ERROR_VIEW;
		}
		if (order.getPaymentMethod() == null || order.getPaymentMethod().getMethod() != PaymentMethod.Method.online) {
			return ERROR_VIEW;
		}
		if (order.getPaymentStatus() != PaymentStatus.unpaid && order.getPaymentStatus() != PaymentStatus.partialPayment) {
			return ERROR_VIEW;
		}
		if (order.getAmountPaid().compareTo(new BigDecimal(0)) <= 0) {
			return ERROR_VIEW;
		}
		payment.setMember(order.getMember());
		payment.setSn(snService.generate(Sn.Type.payment));
		payment.setType(Type.payment);
		payment.setMethod(Method.online);
		payment.setStatus(Status.wait);
		payment.setPaymentMethod(order.getPaymentMethodName() + Payment.PAYMENT_METHOD_SEPARATOR + paymentPlugin.getPaymentName());
		// payment.setFee(new BigDecimal(0));
		payment.setFee(paymentPlugin.calculateFee(order.getAmountPaid()));
		// payment.setAmount(order.getAmountPayable());
		payment.setAmount(paymentPlugin.calculateAmount(order.getAmountPaid()));
		payment.setPaymentPluginId(paymentPluginId);
		payment.setExpire(paymentPlugin.getTimeout() != null ? DateUtils.addMinutes(new Date(), paymentPlugin.getTimeout()) : null);
		payment.setOrder(order);
		paymentService.save(payment);
		model.addAttribute("sn", payment.getSn());
		return "/test/payment";

	}

	@RequestMapping(value = "/notify", method = RequestMethod.GET)
	public String notify(String sn) {
		Payment payment = paymentService.findBySn(sn);
		try {
			paymentService.handle(payment);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/test/notify";

	}

	@RequestMapping(value = "/sign", method = RequestMethod.GET)
	public String sign(String sn) {
		Order order = orderService.findBySn(sn);
		try {
			orderService.sign(order);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/test/notify";

	}

	@RequestMapping(value = "/apply_return", method = RequestMethod.GET)
	public String applyReturn(Long orderItemId, Integer quantity, String trackingNo, String memo, String reason) {
		OrderItem orderItem = orderItemDao.find(orderItemId);
		// orderService.applyReturn(orderItem, quantity, trackingNo, memo, reason);
		return "/test/notify";

	}

	@RequestMapping(value = "/order/create", method = RequestMethod.POST)
	@ResponseBody
	public Message create(BuyArray buyArray) {
		if (buyArray == null || buyArray.getBuys().length <= 0) {
			return Message.warn("请选择商品");
		}
		BuyVo[] buys = buyArray.getBuys();
		Member member = memberService.findByUsername("chenlf");
		ShippingMethod shippingMethod = shippingMethodService.find(1l);
		PaymentMethod paymentMethod = paymentMethodService.find(1l);
		Receiver receiver = receiverService.find(16l);
		Ticket ticket = ticketService.find(1l);
		Member owner = memberService.find(16l);
		try {
			Order order = orderService.createImmediately(buys, member, owner, ticket, receiver, paymentMethod, shippingMethod);
			return Message.success(order.getSn());
		} catch (OrderException e) {
			return Message.error(e.getMessage());
		}
	}

	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public ModelAndView export(Long tenantId, OrderSearchStatus status) {
		Tenant tenant = tenantService.find(tenantId);
		String sn = null;
		MemberRank owerRank = null;
		Date endDate = null;
		Date beginDate = null;
		String name = null;
		List<Order> data = orderService.findForExport(tenant, name, beginDate, endDate, owerRank, status, sn, 100,null);
		
		// 等待付款
		if (OrderSearchStatus.waitPay.equals(status)) {
			return new ModelAndView(new WaitPayExcelView("待付款" + DateUtil.changeDateToStr(new Date(), DateUtil.DB_STORE_DATE_FULL) + ".xls", data));
			// 等待发货
		} else if (OrderSearchStatus.waitShipping.equals(status)) {
			return new ModelAndView(new WaitShippingExcelView("待发货" + DateUtil.changeDateToStr(new Date(), DateUtil.DB_STORE_DATE_FULL) + ".xls", data));
			// 已发货
		} else if (OrderSearchStatus.shippinged.equals(status)) {
			return new ModelAndView(new ShippedExcelView("已发货" + DateUtil.changeDateToStr(new Date(), DateUtil.DB_STORE_DATE_FULL) + ".xls", data));
			// 退货申请中
		} else if (OrderSearchStatus.returning.equals(status)) {
			return new ModelAndView(new ReturnIngExcelView("退货申请中" + DateUtil.changeDateToStr(new Date(), DateUtil.DB_STORE_DATE_FULL) + ".xls", data));
			// 退款申请中
		} else if (OrderSearchStatus.refunding.equals(status)) {
			return new ModelAndView(new RefundIngExcelView("退款申请中" + DateUtil.changeDateToStr(new Date(), DateUtil.DB_STORE_DATE_FULL) + ".xls", data));
			// 已退款
		} else if (OrderSearchStatus.refunded.equals(status)) {
			return new ModelAndView(new RefundedExcelView("已退款" + DateUtil.changeDateToStr(new Date(), DateUtil.DB_STORE_DATE_FULL) + ".xls", data));
			// 已签收
		} else if (OrderSearchStatus.signed.equals(status)) {
			return new ModelAndView(new SignedExcelView("已签收" + DateUtil.changeDateToStr(new Date(), DateUtil.DB_STORE_DATE_FULL) + ".xls", data));
			// 已关闭
		} else if (OrderSearchStatus.cancel.equals(status)) {
			return new ModelAndView(new CancelExcelView("待发货" + DateUtil.changeDateToStr(new Date(), DateUtil.DB_STORE_DATE_FULL) + ".xls", data));
			// 交易成功
		} else if (OrderSearchStatus.complete.equals(status)) {
			// return new ModelAndView(new complete("待发货" + DateUtil.changeDateToStr(new Date(), DateUtil.DB_STORE_DATE_FULL) + ".xls", data));
		}
		return new ModelAndView("");

	}

}
