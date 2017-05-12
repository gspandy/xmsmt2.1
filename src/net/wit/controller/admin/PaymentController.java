package net.wit.controller.admin;

import java.util.Date;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.Order;
import net.wit.entity.Payment;
import net.wit.entity.Refunds;
import net.wit.service.AdminService;
import net.wit.service.DeliveryCorpService;
import net.wit.service.OrderItemService;
import net.wit.service.OrderService;
import net.wit.service.PaymentMethodService;
import net.wit.service.PaymentService;
import net.wit.service.ShippingMethodService;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("adminPaymentController")
@RequestMapping({ "/admin/payment" })
public class PaymentController extends BaseController {

	@Resource(name = "paymentServiceImpl")
	private PaymentService paymentService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;
	
	@Resource(name = "orderItemServiceImpl")
	private OrderItemService orderItemService;

	@Resource(name = "shippingMethodServiceImpl")
	private ShippingMethodService shippingMethodService;

	@Resource(name = "deliveryCorpServiceImpl")
	private DeliveryCorpService deliveryCorpService;

	@Resource(name = "paymentMethodServiceImpl")
	private PaymentMethodService paymentMethodService;
	
	@Resource(name = "orderServiceImpl")
	private OrderService orderService;

	@RequestMapping(value = { "/view" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String view(Long id, ModelMap model) {
		model.addAttribute("payment", this.paymentService.find(id));
		return "/admin/payment/view";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		 String searchValue = null;
			try {
				searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
				pageable.setSearchValue(searchValue);
			} catch (Exception localException) {
			}
		model.addAttribute("page", this.paymentService.findPage(adminService.getCurrent().getTenant(), pageable));
		return "/admin/payment/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		if (ids != null) {
			for (Long id : ids) {
				Payment payment = (Payment) this.paymentService.find(id);
				if ((payment != null) && (payment.getExpire() != null) && (!(payment.hasExpired()))) {
					return Message.error("admin.payment.deleteUnexpiredNotAllowed", new Object[0]);
				}
			}
			this.paymentService.delete(ids);
		}
		return SUCCESS_MESSAGE;
	}
	@RequestMapping(value = { "/orderView" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String orderView(Long id, ModelMap model) {
		model.addAttribute("methods", Payment.Method.values());
		model.addAttribute("refundsMethods", Refunds.Method.values());
		model.addAttribute("paymentMethods", this.paymentMethodService.findAll());
		model.addAttribute("shippingMethods", this.shippingMethodService.findAll());
		model.addAttribute("deliveryCorps", this.deliveryCorpService.findAll());
		model.addAttribute("order", this.orderService.find(id));
		return "/admin/payment/orderView";
	}
	@RequestMapping(value = { "/check_lock" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message checkLock(Long id) {
		Order order = this.orderService.find(id);
		if (order == null) {
			return Message.warn("admin.common.invalid", new Object[0]);
		}
		Admin admin = this.adminService.getCurrent();
		if (order.isLocked(admin)) {
			if (order.getOperator() != null) {
				return Message.warn("admin.order.adminLocked", new Object[] { order.getOperator().getUsername() });
			}
			return Message.warn("admin.order.memberLocked", new Object[0]);
		}

		order.setLockExpire(DateUtils.addSeconds(new Date(), 20));
		order.setOperator(admin);
		this.orderService.update(order);
		return SUCCESS_MESSAGE;
	}

}