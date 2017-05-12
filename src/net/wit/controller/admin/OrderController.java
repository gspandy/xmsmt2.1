package net.wit.controller.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.wit.CancelExcelView;
import net.wit.CompleteExcelView;
import net.wit.Message;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.RefundIngExcelView;
import net.wit.RefundedExcelView;
import net.wit.ReturnIngExcelView;
import net.wit.SMTExcelView;
import net.wit.ShippedExcelView;
import net.wit.SignedExcelView;
import net.wit.UnShippedExcelView;
import net.wit.WaitPayExcelView;
import net.wit.WaitShippingExcelView;
import net.wit.entity.Admin;
import net.wit.entity.Area;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.DeliveryCorp;
import net.wit.entity.Member;
import net.wit.entity.MemberRank;
import net.wit.entity.Order;
import net.wit.entity.OrderItem;
import net.wit.entity.Payment;
import net.wit.entity.PaymentMethod;
import net.wit.entity.Product;
import net.wit.entity.Refunds;
import net.wit.entity.Refunds.RefurnsStatus;
import net.wit.entity.Returns;
import net.wit.entity.ReturnsItem;
import net.wit.entity.Shipping;
import net.wit.entity.ShippingItem;
import net.wit.entity.ShippingMethod;
import net.wit.entity.Sn;
import net.wit.entity.Tenant;
import net.wit.enums.OrderSearchStatus;
import net.wit.exception.OrderException;
import net.wit.mobile.service.impl.PushService;
import net.wit.mobile.util.rong.models.SystemMessage;
import net.wit.service.AdminService;
import net.wit.service.AreaService;
import net.wit.service.DeliveryCenterService;
import net.wit.service.DeliveryCorpService;
import net.wit.service.OrderItemService;
import net.wit.service.OrderService;
import net.wit.service.PaymentMethodService;
import net.wit.service.ProductService;
import net.wit.service.ReceiverService;
import net.wit.service.RefundsService;
import net.wit.service.ReturnsService;
import net.wit.service.ShippingMethodService;
import net.wit.service.ShippingService;
import net.wit.service.SnService;
import net.wit.service.TenantService;
import net.wit.support.EntitySupport;
import net.wit.util.BizException;
import net.wit.util.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminOrderController")
@RequestMapping({ "/admin/order" })
public class OrderController extends BaseController {

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;

	@Resource(name = "orderItemServiceImpl")
	private OrderItemService orderItemService;

	@Resource(name = "shippingMethodServiceImpl")
	private ShippingMethodService shippingMethodService;

	@Resource(name = "deliveryCorpServiceImpl")
	private DeliveryCorpService deliveryCorpService;

	@Resource(name = "paymentMethodServiceImpl")
	private PaymentMethodService paymentMethodService;

	@Resource(name = "snServiceImpl")
	private SnService snService;

	@Resource(name = "deliveryCenterServiceImpl")
	private DeliveryCenterService deliveryCenterService;

	@Resource(name = "receiverServiceImpl")
	private ReceiverService receiverService;

	@Resource(name = "shippingServiceImpl")
	private ShippingService shippingService;
	
	@Resource(name = "tenantServiceImpl")
	private TenantService tenantService;
	
	@Resource(name = "returnsServiceImpl")
	private ReturnsService returnsService;
	
	@Resource(name = "refundsServiceImpl")
	private RefundsService refundsService;
	
	@Autowired
	private PushService pushService;

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
				return Message.warn("admin.order.adminLocked",
						new Object[] { order.getOperator().getUsername() });
			}
			return Message.warn("admin.order.memberLocked", new Object[0]);
		}

		order.setLockExpire(DateUtils.addSeconds(new Date(), 20));
		order.setOperator(admin);
		this.orderService.update(order);
		return SUCCESS_MESSAGE;
	}

	@RequestMapping(value = { "/view" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String view(String flag, Long id, ModelMap model) {
		model.addAttribute("methods", Payment.Method.values());
		model.addAttribute("refundsMethods", Refunds.Method.values());
		model.addAttribute("paymentMethods",
				this.paymentMethodService.findAll());
		model.addAttribute("shippingMethods",
				this.shippingMethodService.findAll());
		model.addAttribute("deliveryCorps", this.deliveryCorpService.findAll());
		model.addAttribute("order", this.orderService.find(id));
		model.addAttribute("flag", flag);
		return "/admin/order/view";
	}

	@RequestMapping(value = { "/confirm" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String confirm(Long id, RedirectAttributes redirectAttributes) {
		Order order = (Order) this.orderService.find(id);
		Admin admin = this.adminService.getCurrent();
		if ((order != null) && (!(order.isExpired()))
				&& (order.getOrderStatus() == Order.OrderStatus.unconfirmed)
				&& (!(order.isLocked(admin)))) {
			this.orderService.confirm(order, admin);
			addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		} else {
			addFlashMessage(redirectAttributes,
					Message.warn("admin.common.invalid", new Object[0]));
		}
		return "redirect:view.jhtml?id=" + id;
	}

	@RequestMapping(value = { "/complete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String complete(Long id, RedirectAttributes redirectAttributes) {
		Order order = (Order) this.orderService.find(id);
		Admin admin = this.adminService.getCurrent();
		if ((order != null) && (!(order.isExpired()))
				&& (order.getOrderStatus() == Order.OrderStatus.confirmed)
				&& (!(order.isLocked(admin)))) {
			try {
				this.orderService.complete(order, admin);
			} catch (BizException e) {
				// TODO Auto-generated catch block
			}
			addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		} else {
			addFlashMessage(redirectAttributes,
					Message.warn("admin.common.invalid", new Object[0]));
		}
		return "redirect:view.jhtml?id=" + id;
	}

	@RequestMapping(value = { "/cancel" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String cancel(Long id, RedirectAttributes redirectAttributes) {
		Order order = (Order) this.orderService.find(id);
		Admin admin = this.adminService.getCurrent();
		if ((order != null) && (!(order.isExpired()))
				&& (order.getOrderStatus() == Order.OrderStatus.unconfirmed)
				&& (!(order.isLocked(admin)))) {
			this.orderService.cancel(order, admin);
			addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		} else {
			addFlashMessage(redirectAttributes,
					Message.warn("admin.common.invalid", new Object[0]));
		}
		return "redirect:view.jhtml?id=" + id;
	}

	@RequestMapping(value = { "/payment" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String payment(Long orderId, Long paymentMethodId, Payment payment,
			RedirectAttributes redirectAttributes) {
		Order order = (Order) this.orderService.find(orderId);
		payment.setOrder(order);
		PaymentMethod paymentMethod = (PaymentMethod) this.paymentMethodService
				.find(paymentMethodId);
		payment.setPaymentMethod((paymentMethod != null) ? paymentMethod
				.getName() : null);
		if (!(isValid(payment, new Class[0]))) {
			return "/admin/common/error";
		}
		if ((order.isExpired())
				|| (order.getOrderStatus() != Order.OrderStatus.confirmed)) {
			return "/admin/common/error";
		}
		if ((order.getPaymentStatus() != Order.PaymentStatus.unpaid)
				&& (order.getPaymentStatus() != Order.PaymentStatus.partialPayment)) {
			return "/admin/common/error";
		}
		if ((payment.getAmount().compareTo(new BigDecimal(0)) <= 0)
				|| (payment.getAmount().compareTo(order.getAmountPayable()) > 0)) {
			return "/admin/common/error";
		}
		Member member = order.getMember();
		if ((payment.getMethod() == Payment.Method.deposit)
				&& (payment.getAmount().compareTo(member.getBalance()) > 0)) {
			return "/admin/common/error";
		}
		Admin admin = this.adminService.getCurrent();
		if (order.isLocked(admin)) {
			return "/admin/common/error";
		}
		payment.setSn(this.snService.generate(Sn.Type.payment));
		payment.setType(Payment.Type.payment);
		payment.setStatus(Payment.Status.success);
		payment.setFee(new BigDecimal(0));
		payment.setOperator(admin.getUsername());
		payment.setPaymentDate(new Date());
		payment.setPaymentPluginId(null);
		payment.setExpire(null);
		payment.setDeposit(null);
		payment.setMember(null);
		this.orderService.payment(order, payment, admin);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:view.jhtml?id=" + orderId;
	}

	@RequestMapping(value = { "/refunds" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String refunds(Long orderId, Long paymentMethodId, Refunds refunds,
			RedirectAttributes redirectAttributes) {
		Order order = (Order) this.orderService.find(orderId);
		refunds.setOrder(order);
		PaymentMethod paymentMethod = (PaymentMethod) this.paymentMethodService
				.find(paymentMethodId);
		refunds.setPaymentMethod((paymentMethod != null) ? paymentMethod
				.getName() : null);
		if (!(isValid(refunds, new Class[0]))) {
			return "/admin/common/error";
		}
		if ((order.isExpired())
				|| (order.getOrderStatus() != Order.OrderStatus.confirmed)) {
			return "/admin/common/error";
		}
		if ((order.getPaymentStatus() != Order.PaymentStatus.paid)
				&& (order.getPaymentStatus() != Order.PaymentStatus.partialPayment)
				&& (order.getPaymentStatus() != Order.PaymentStatus.partialRefunds)
				&& (order.getPaymentStatus() != Order.PaymentStatus.refundapply)) {
			return "/admin/common/error";
		}
		if ((refunds.getAmount().compareTo(new BigDecimal(0)) <= 0)
				|| (refunds.getAmount().compareTo(order.getAmountPaid()) > 0)) {
			return "/admin/common/error";
		}
		Admin admin = this.adminService.getCurrent();
		if (order.isLocked(admin)) {
			return "/admin/common/error";
		}
		refunds.setSn(this.snService.generate(Sn.Type.refunds));
		refunds.setOperator(admin.getUsername());
		this.orderService.refunds(order, refunds, admin);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:view.jhtml?id=" + orderId;
	}

	@RequestMapping(value = { "/confirmRefundapply" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String confirmRefundapply(Long id,
			RedirectAttributes redirectAttributes) {
		Order order = (Order) this.orderService.find(id);
		Admin admin = this.adminService.getCurrent();
		if (order.isLocked(admin)) {
			return "/admin/common/error";
		}
		if (order.getPaymentStatus() != Order.PaymentStatus.refundapply) {
			return "/admin/common/error";
		}

		Refunds refunds = EntitySupport.createInitRefunds();
		refunds.setSn(this.snService.generate(Sn.Type.refunds));
		refunds.setMethod(Refunds.Method.online);
		refunds.setStatus(RefurnsStatus.agree);
		refunds.setAmount(order.getAmountPaid());
		refunds.setOperator(admin.getUsername());
		refunds.setPayee(order.getMember().getUsername());
		refunds.setOrder(order);
		this.orderService.refunds(order, refunds, admin);
		return "redirect:view.jhtml?id=" + id;
	}

	@RequestMapping(value = { "/shipping" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String shipping(Long orderId, Long shippingMethodId,
			Long deliveryCorpId, String areaName, Shipping shipping,
			RedirectAttributes redirectAttributes) {
		Order order = this.orderService.find(orderId);
		if (order == null) {
			return "/admin/common/error";
		}
		for (Iterator iterator = shipping.getShippingItems().iterator(); iterator
				.hasNext();) {
			ShippingItem shippingItem = (ShippingItem) iterator.next();
			if ((shippingItem == null)
					|| (StringUtils.isEmpty(shippingItem.getSn()))
					|| (shippingItem.getQuantity() == null)
					|| (shippingItem.getQuantity().intValue() <= 0)) {
				iterator.remove();
			} else {
				OrderItem orderItem = order.getOrderItem(shippingItem.getSn());
				if ((orderItem == null)
						|| (shippingItem.getQuantity().intValue() > orderItem
								.getQuantity().intValue()
								- orderItem.getShippedQuantity().intValue())) {
					return "/admin/common/error";
				}
				if ((orderItem.getProduct() != null)
						&& (orderItem.getProduct().getStock() != null)
						&& (shippingItem.getQuantity().intValue() > orderItem
								.getProduct().getStock().intValue())) {
					return "/admin/common/error";
				}
				shippingItem.setName(orderItem.getFullName());
				shippingItem.setShipping(shipping);

			}
		}
		shipping.setOrder(order);
		ShippingMethod shippingMethod = this.shippingMethodService
				.find(shippingMethodId);
		shipping.setShippingMethod((shippingMethod != null) ? shippingMethod
				.getName() : null);
		DeliveryCorp deliveryCorp = this.deliveryCorpService
				.find(deliveryCorpId);
		shipping.setDeliveryCorp((deliveryCorp != null) ? deliveryCorp
				.getName() : null);
		shipping.setDeliveryCorpUrl((deliveryCorp != null) ? deliveryCorp
				.getUrl() : null);
		shipping.setDeliveryCorpCode((deliveryCorp != null) ? deliveryCorp
				.getCode() : null);
		// Area area = new Area();
		// shipping.setArea((area != null) ? area.getFullName() : null);
		shipping.setArea(areaName);
		DeliveryCenter deliveryCenter = this.deliveryCenterService
				.findDefault(adminService.getCurrent().getTenant());
		if (deliveryCenter == null) {
			addFlashMessage(redirectAttributes, Message.warn("请选添加一个收发货地址"));
			return "redirect:view.jhtml?id=" + orderId;
		}
		shipping.setDeliveryCenter(deliveryCenter);
		if (!(isValid(shipping, new Class[0]))) {
			return "/admin/common/error";
		}
		if ((order.isExpired())
				|| (order.getOrderStatus() != Order.OrderStatus.confirmed)) {
			return "/admin/common/error";
		}
		if ((order.getShippingStatus() != Order.ShippingStatus.unshipped)
				&& (order.getShippingStatus() != Order.ShippingStatus.partialShipment)) {
			return "/admin/common/error";
		}
		Admin admin = this.adminService.getCurrent();
		if (order.isLocked(admin)) {
			return "/admin/common/error";
		}
		shipping.setSn(this.snService.generate(Sn.Type.shipping));
		shipping.setOperator(admin.getUsername());
		this.orderService.shipping(order, shipping, admin);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:view.jhtml?id=" + orderId;
	}

	@RequestMapping(value = { "/returns" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String returns(Long orderId, Long shippingMethodId,
			Long deliveryCorpId, Long areaId, Returns returns,
			RedirectAttributes redirectAttributes) {
		Order order = this.orderService.find(orderId);
		if (order == null) {
			return "/admin/common/error";
		}

		List<ReturnsItem> returnsItems = returns.getReturnsItems();
		returns.setReturnsItems(new ArrayList());
		for (ReturnsItem returnsItem : returnsItems) {
			if ((returnsItem == null)
					|| (StringUtils.isEmpty(returnsItem.getSn()))
					|| (returnsItem.getQuantity() == null))
				continue;
			if (returnsItem.getQuantity().intValue() <= 0) {
				continue;
			}
			OrderItem orderItem = order.getOrderItem(returnsItem.getSn());
			if ((orderItem == null)
					|| (returnsItem.getQuantity().intValue() > orderItem
							.getShippedQuantity().intValue()
							- orderItem.getReturnQuantity().intValue())) {
				return "/admin/common/error";
			}
			if (returns.getTrade() == null) {
				returns.setTrade(orderItem.getTrade());
				returns.getReturnsItems().add(returnsItem);
			} else {
				if (returns.getTrade() != orderItem.getTrade())
					continue;
			}
			returnsItem.setName(orderItem.getFullName());
			returnsItem.setReturns(returns);
		}
		returns.setOrder(order);
		returns.setDeliveryCenter(this.deliveryCenterService.findDefault());

		ShippingMethod shippingMethod = this.shippingMethodService
				.find(shippingMethodId);
		returns.setShippingMethod((shippingMethod != null) ? shippingMethod
				.getName() : null);
		DeliveryCorp deliveryCorp = this.deliveryCorpService
				.find(deliveryCorpId);
		returns.setDeliveryCorp((deliveryCorp != null) ? deliveryCorp.getName()
				: null);
		Area area = this.areaService.find(areaId);
		returns.setArea((area != null) ? area.getFullName() : null);
		if (!(isValid(returns, new Class[0]))) {
			return "/admin/common/error";
		}
		if ((order.isExpired())
				|| (order.getOrderStatus() != Order.OrderStatus.confirmed)) {
			return "/admin/common/error";
		}
		if ((order.getShippingStatus() != Order.ShippingStatus.shipped)
				&& (order.getShippingStatus() != Order.ShippingStatus.partialShipment)
				&& (order.getShippingStatus() != Order.ShippingStatus.partialReturns)) {
			return "/admin/common/error";
		}
		Admin admin = this.adminService.getCurrent();
		if (order.isLocked(admin)) {
			return "/admin/common/error";
		}
		returns.setSn(this.snService.generate(Sn.Type.returns));
		returns.setOperator(admin.getUsername());
		this.orderService.returns(order, returns, admin);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:view.jhtml?id=" + orderId;
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		model.addAttribute("paymentMethods",
				this.paymentMethodService.findAll());
		model.addAttribute("shippingMethods",
				this.shippingMethodService.findAll());
		model.addAttribute("order", this.orderService.find(id));
		return "/admin/order/edit";
	}

	@RequestMapping(value = { "/order_item_add" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> orderItemAdd(String productSn) {
		Map data = new HashMap();
		Product product = this.productService.findBySn(productSn);
		if (product == null) {
			data.put("message",
					Message.warn("admin.order.productNotExist", new Object[0]));
			return data;
		}
		if (!(product.getIsMarketable().booleanValue())) {
			data.put("message", Message.warn(
					"admin.order.productNotMarketable", new Object[0]));
			return data;
		}
		if (product.getIsOutOfStock().booleanValue()) {
			data.put("message", Message.warn("admin.order.productOutOfStock",
					new Object[0]));
			return data;
		}
		data.put("sn", product.getSn());
		data.put("fullName", product.getFullName());
		data.put("price", product.getPrice());
		data.put("weight", product.getWeight());
		data.put("isGift", product.getIsGift());
		data.put("message", SUCCESS_MESSAGE);
		return data;
	}

	@RequestMapping(value = { "/calculate" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> calculate(Order order, Long areaId,
			Long paymentMethodId, Long shippingMethodId) {
		OrderItem orderItem;
		Product product;
		Map data = new HashMap();
		for (Iterator iterator = order.getOrderItems().iterator(); iterator
				.hasNext();) {
			orderItem = (OrderItem) iterator.next();
			if ((orderItem == null) || (StringUtils.isEmpty(orderItem.getSn()))) {
				iterator.remove();
			}
		}
		order.setArea((Area) this.areaService.find(areaId));
		order.setPaymentMethod((PaymentMethod) this.paymentMethodService
				.find(paymentMethodId));
		order.setShippingMethod((ShippingMethod) this.shippingMethodService
				.find(shippingMethodId));
		if (!(isValid(order, new Class[0]))) {
			data.put("message",
					Message.warn("admin.common.invalid", new Object[0]));
			return data;
		}
		Order pOrder = (Order) this.orderService.find(order.getId());
		if (pOrder == null) {
			data.put("message",
					Message.error("admin.common.invalid", new Object[0]));
			return data;
		}
		for (Iterator localIterator1 = order.getOrderItems().iterator(); localIterator1
				.hasNext();) {
			orderItem = (OrderItem) localIterator1.next();
			if (orderItem.getId() != null) {
				OrderItem pOrderItem = (OrderItem) this.orderItemService
						.find(orderItem.getId());
				if ((pOrderItem == null)
						|| (!(pOrder.equals(pOrderItem.getOrder())))) {
					data.put("message", Message.error("admin.common.invalid",
							new Object[0]));
					return data;
				}
				// Product product = pOrderItem.getProduct();// TODO
				product = pOrderItem.getProduct();
				if ((product == null) || (product.getStock() == null))
					continue;
				if (pOrder.getIsAllocatedStock().booleanValue()) {
					if (orderItem.calculateQuantityIntValue() <= product
							.getAvailableStock().intValue()
							+ pOrderItem.calculateQuantityIntValue())
						continue;
					data.put("message",
							Message.warn("admin.order.lowStock", new Object[0]));
					return data;
				}

				if (orderItem.calculateQuantityIntValue() <= product
						.getAvailableStock().intValue())
					continue;
				data.put("message",
						Message.warn("admin.order.lowStock", new Object[0]));
				return data;
			}

			product = this.productService.findBySn(orderItem.getSn());
			if (product == null) {
				data.put("message",
						Message.error("admin.common.invalid", new Object[0]));
				return data;
			}
			if ((product.getStock() != null)
					&& (orderItem.calculateQuantityIntValue() > product
							.getAvailableStock().intValue())) {
				data.put("message",
						Message.warn("admin.order.lowStock", new Object[0]));
				return data;
			}
		}

		Map orderItems = new HashMap();
		for (OrderItem orderItemTemp : order.getOrderItems()) {
			orderItems.put(orderItemTemp.getSn(), orderItemTemp);
		}
		order.setFee(pOrder.getFee());
		order.setPromotionDiscount(pOrder.getPromotionDiscount());
		order.setCouponDiscount(pOrder.getCouponDiscount());
		order.setAmountPaid(pOrder.getAmountPaid());
		data.put("weight", Integer.valueOf(order.getWeight()));
		data.put("price", order.getPrice());
		data.put("quantity", Integer.valueOf(order.getQuantity()));
		data.put("amount", order.getAmount());
		data.put("orderItems", orderItems);
		data.put("message", SUCCESS_MESSAGE);
		return data;
	}
	@RequestMapping(value = { "/updat" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String updat(Long orderId, String memo,String remark, RedirectAttributes redirectAttributes) {
		Order order=this.orderService.find(orderId);
		order.setMemo(memo);
		order.getInvoiceManagement().setRemark(remark);
		this.orderService.update(order);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(Order order, Long areaId, Long paymentMethodId,
			Long shippingMethodId, RedirectAttributes redirectAttributes) {
		for (Iterator iterator = order.getOrderItems().iterator(); iterator
				.hasNext();) {
			OrderItem orderItem = (OrderItem) iterator.next();
			if ((orderItem == null) || (StringUtils.isEmpty(orderItem.getSn()))) {
				iterator.remove();
			}
		}
		order.setArea((Area) this.areaService.find(areaId));
		order.setPaymentMethod((PaymentMethod) this.paymentMethodService
				.find(paymentMethodId));
		order.setShippingMethod((ShippingMethod) this.shippingMethodService
				.find(shippingMethodId));
		if (!(isValid(order, new Class[0]))) {
			return "/admin/common/error";
		}
		Order pOrder = (Order) this.orderService.find(order.getId());
		if (pOrder == null) {
			return "/admin/common/error";
		}
		if ((pOrder.isExpired())
				|| (pOrder.getOrderStatus() != Order.OrderStatus.unconfirmed)) {
			return "/admin/common/error";
		}
		Admin admin = this.adminService.getCurrent();
		if (pOrder.isLocked(admin)) {
			return "/admin/common/error";
		}
		if (!(order.getIsInvoice().booleanValue())) {
			order.setInvoiceTitle(null);
			order.setTax(new BigDecimal(0));
		}
		label381: for (OrderItem orderItem : order.getOrderItems()) {
			if (orderItem.getId() != null) {
				OrderItem pOrderItem = (OrderItem) this.orderItemService
						.find(orderItem.getId());
				if ((pOrderItem == null)
						|| (!(pOrder.equals(pOrderItem.getOrder())))) {
					return "/admin/common/error";
				}
				Product product = pOrderItem.getProduct();
				if ((product != null) && (product.getStock() != null)) {
					if (pOrder.getIsAllocatedStock().booleanValue()) {
						if (orderItem.calculateQuantityIntValue() <= product
								.getAvailableStock().intValue()
								+ pOrderItem.calculateQuantityIntValue())
							break label381;
						return "/admin/common/error";
					}

					if (orderItem.calculateQuantityIntValue() > product
							.getAvailableStock().intValue()) {
						return "/admin/common/error";
					}
				}

				BeanUtils.copyProperties(pOrderItem, orderItem, new String[] {
						"price", "quantity" });
				if (pOrderItem.getIsGift().booleanValue())
					orderItem.setPrice(new BigDecimal(0));
			} else {
				Product product = this.productService.findBySn(orderItem
						.getSn());
				if (product == null) {
					return "/admin/common/error";
				}
				if ((product.getStock() != null)
						&& (orderItem.calculateQuantityIntValue() > product
								.getAvailableStock().intValue())) {
					return "/admin/common/error";
				}
				orderItem.setName(product.getName());
				orderItem.setFullName(product.getFullName());
				if (product.getIsGift().booleanValue()) {
					orderItem.setPrice(new BigDecimal(0));
				}
				orderItem.setWeight(product.getWeight());
				orderItem.setThumbnail(product.getThumbnail());
				orderItem.setIsGift(product.getIsGift());
				orderItem.setShippedQuantity(Integer.valueOf(0));
				orderItem.setReturnQuantity(Integer.valueOf(0));
				orderItem.setProduct(product);
				orderItem.setOrder(pOrder);
			}
		}
		order.setSn(pOrder.getSn());
		order.setOrderStatus(pOrder.getOrderStatus());
		order.setPaymentStatus(pOrder.getPaymentStatus());
		order.setShippingStatus(pOrder.getShippingStatus());
		order.setFee(pOrder.getFee());
		order.setPromotionDiscount(pOrder.getPromotionDiscount());
		order.setCouponDiscount(pOrder.getCouponDiscount());
		order.setAmountPaid(pOrder.getAmountPaid());
		order.setPromotion(pOrder.getPromotion());
		order.setExpire(pOrder.getExpire());
		order.setLockExpire(null);
		order.setIsAllocatedStock(pOrder.getIsAllocatedStock());
		order.setOperator(null);
		order.setMember(pOrder.getMember());
		order.setCouponCode(pOrder.getCouponCode());
		order.setCoupons(pOrder.getCoupons());
		order.setOrderLogs(pOrder.getOrderLogs());
		order.setDeposits(pOrder.getDeposits());
		order.setPayments(pOrder.getPayments());
		order.setRefunds(pOrder.getRefunds());
		order.setShippings(pOrder.getShippings());
		order.setReturns(pOrder.getReturns());

		this.orderService.update(order, admin.getUsername());
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
//	public String list(Order.OrderStatus orderStatus,
//			Order.PaymentStatus paymentStatus,
//			Order.ShippingStatus shippingStatus,
	public String list(String flag, String orderStatusParam,String productName,String name,String sn,Date startTime,Date endTime,
			Boolean hasExpired, Pageable pageable, ModelMap model) {
		
		if ("2".equals(flag)) {
			return "redirect:/admin/invoice/invoice_management_find.jhtml";
		}
		String searchValue = null;
		Tenant tenantSearch = null;
		try {
			searchValue = new String(pageable.getSearchValue().getBytes(
					"ISO-8859-1"), "UTF-8");
			pageable.setSearchValue(searchValue);
			if (pageable.getSearchProperty().equals("tenant")) {
				pageable.setSearchProperty(null);
				tenantSearch = new Tenant();
				tenantSearch.setShortName(pageable.getSearchValue());
			}

		} catch (Exception localException) {}
		try {
			productName = new String(productName.getBytes("ISO-8859-1"),
					"UTF-8");
		} catch (Exception localException) {}
		try {
			name = new String(name.getBytes("ISO-8859-1"), "UTF-8");
		} catch (Exception localException) {}
		Tenant tenant = adminService.getCurrent().getTenant();
		// 不支持复选
		// model.addAttribute("orderStatus", orderStatus);
		// model.addAttribute("paymentStatus", paymentStatus);
		// model.addAttribute("shippingStatus", shippingStatus);
		model.addAttribute("orderStatusParam", orderStatusParam);
		model.addAttribute("hasExpired", hasExpired);
		model.addAttribute("productName", productName);
		model.addAttribute("name", name);
		model.addAttribute("sn", sn);
		model.addAttribute("startTime", DateUtil.changeDateToStr(startTime,DateUtil.LINK_DISPLAY_DATE_FULL));
		model.addAttribute("endTime", DateUtil.changeDateToStr(endTime,DateUtil.LINK_DISPLAY_DATE_FULL));
		try {
			model.addAttribute("tenant",tenant);
			if(tenantSearch==null){
//				model.addAttribute("page", this.orderService.findPageByTenant(
//						tenant, orderStatus, paymentStatus, shippingStatus,
//						hasExpired, pageable));
				List<Order.OrderStatus> orderStatuses = null;
				List<Order.PaymentStatus> paymentStatuses = null;
				List<Order.ShippingStatus> shippingStatuses = null;
				if (orderStatusParam != null && !"".equals(orderStatusParam)) {
					orderStatuses = new ArrayList<Order.OrderStatus>();
					paymentStatuses = new ArrayList<Order.PaymentStatus>();
					shippingStatuses = new ArrayList<Order.ShippingStatus>();
				}
				this.convertStatus(orderStatusParam, orderStatuses, paymentStatuses, shippingStatuses);
				model.addAttribute("page", orderService.findPageByTenant(tenant, orderStatuses, paymentStatuses, shippingStatuses, productName, name, sn, startTime, endTime, hasExpired, pageable));
			}
//			if(tenantSearch!=null){
//				Pageable pageTenant=new Pageable();
//				pageTenant.setSearchProperty("shortName");
//				pageTenant.setSearchValue(pageable.getSearchValue());
//				Page<Tenant> page=tenantService.findPage(pageTenant);
//				model.addAttribute("page", this.orderService.findPage(page.getContent(), pageable));
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/admin/order/list";
	}

	
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public ModelAndView export(Long tenantId, String orderStatusParam,String productName,String name,String sn,Date startTime,Date endTime) {
		OrderSearchStatus status=null;
		if (orderStatusParam.equals("unpaid")) {// 待支付
			status=OrderSearchStatus.waitPay;
		}
		if (orderStatusParam.equals("unshipped")) {// 待发货
			status=OrderSearchStatus.waitShipping;
		}
		if (orderStatusParam.equals("shipped")) {// 已发货
			status=OrderSearchStatus.shippinged;
		}
		if (orderStatusParam.equals("apply")) {// 退货中
			status=OrderSearchStatus.returning;
		}
		if (orderStatusParam.equals("refundapply")) {// 退款中
			status=OrderSearchStatus.refunding;
		}
		if (orderStatusParam.equals("refunded")) {// 已退款
			status=OrderSearchStatus.refunded;
		}
		if (orderStatusParam.equals("accept")) {// 已签收
			status=OrderSearchStatus.signed;
		}
		if (orderStatusParam.equals("cancelled")) {// 交易关闭
			status=OrderSearchStatus.cancel;
		}
		if (orderStatusParam.equals("completed")) {// 交易成功
			status=OrderSearchStatus.complete;
		}
		Tenant tenant = adminService.getCurrent().getTenant();
		MemberRank owerRank = null;
		Date endDate = endTime;
		Date beginDate = startTime;
		try {
			productName = new String(productName.getBytes("ISO-8859-1"),
					"UTF-8");
		} catch (Exception localException) {}
		try {
			name = new String(name.getBytes("ISO-8859-1"), "UTF-8");
		} catch (Exception localException) {}
		List<Order> data = orderService.findForExport(tenant, productName, beginDate, endDate, owerRank, status, sn, 100,name);
		
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
			return new ModelAndView(new CancelExcelView("交易关闭" + DateUtil.changeDateToStr(new Date(), DateUtil.DB_STORE_DATE_FULL) + ".xls", data));
			// 交易成功
		} else if (OrderSearchStatus.complete.equals(status)) {
			return new ModelAndView(new CompleteExcelView("交易成功" + DateUtil.changeDateToStr(new Date(), DateUtil.DB_STORE_DATE_FULL) + ".xls", data));
		}
		return new ModelAndView("");

	}
	/**
	 * 待发货列表
	 * 
	 * @param orderStatus
	 * @param paymentStatus
	 * @param pageable
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/unshippedList" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String unshippedList(Order.OrderStatus orderStatus,
			Order.PaymentStatus paymentStatus, Pageable pageable, ModelMap model) {
		 String searchValue = null;
			try {
				searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
				pageable.setSearchValue(searchValue);
			} catch (Exception localException) {
			}
		Tenant tenant = adminService.getCurrent().getTenant();
		model.addAttribute("orderStatus", orderStatus);
		model.addAttribute("paymentStatus", paymentStatus);
		model.addAttribute("shippingStatus", Order.ShippingStatus.unshipped);
		try {
			model.addAttribute("page", this.orderService.findPageByTenant(
					tenant, orderStatus, Order.PaymentStatus.paid,
					Order.ShippingStatus.unshipped, null, pageable));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/admin/unshipped/list";
	}

	/**
	 * 查看待发货订单详情
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/viewUnshipped" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String viewUnshipped(Long id, ModelMap model) {
		model.addAttribute("methods", Payment.Method.values());
		model.addAttribute("refundsMethods", Refunds.Method.values());
		model.addAttribute("paymentMethods",
				this.paymentMethodService.findAll());
		model.addAttribute("shippingMethods",
				this.shippingMethodService.findAll());
		model.addAttribute("deliveryCorps", this.deliveryCorpService.findAll());
		model.addAttribute("order", this.orderService.find(id));
		return "/admin/unshipped/view";
	}

	/**
	 * 已发货订单列表
	 * 
	 * @param orderStatus
	 * @param paymentStatus
	 * @param pageable
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/shippedList" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String shippedList(Order.OrderStatus orderStatus,
			Order.PaymentStatus paymentStatus, Pageable pageable, ModelMap model) {
		 String searchValue = null;
			try {
				searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
				pageable.setSearchValue(searchValue);
			} catch (Exception localException) {
			}
		Tenant tenant = adminService.getCurrent().getTenant();
		model.addAttribute("orderStatus", orderStatus);
		model.addAttribute("paymentStatus", paymentStatus);
		model.addAttribute("shippingStatus", Order.ShippingStatus.shipped);
		try {
			model.addAttribute("page", this.orderService.findPageByTenant(
					tenant, orderStatus, paymentStatus,
					Order.ShippingStatus.shipped, null, pageable));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/admin/shipped/list";
	}

	/**
	 * 查看已发货订单详情
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/viewShipped" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String viewShipped(Long id, ModelMap model) {
		model.addAttribute("methods", Payment.Method.values());
		model.addAttribute("refundsMethods", Refunds.Method.values());
		model.addAttribute("paymentMethods",
				this.paymentMethodService.findAll());
		model.addAttribute("shippingMethods",
				this.shippingMethodService.findAll());
		model.addAttribute("deliveryCorps", this.deliveryCorpService.findAll());
		Order order = this.orderService.find(id);
		Set<Shipping> shippings = order.getShippings();
		for (Shipping shipping : shippings) {
			Map<String, Object> deliveryTrackings = this.shippingService
					.query(shipping);
			if (deliveryTrackings.containsValue("ok")) {
				model.addAttribute("deliveryTrackings", deliveryTrackings.get("data"));
			} else if (deliveryTrackings.isEmpty()) {
				deliveryTrackings.put("message", "您所要查询的运单不存在");
				model.addAttribute("deliveryTrackings", deliveryTrackings);
			} else if ("3".equals(deliveryTrackings.get("status"))) {// 返回url的查询方式
				model.addAttribute("deliveryTrackings", deliveryTrackings.get("data"));
			} else {
				model.addAttribute("deliveryTrackings", deliveryTrackings);
			}
			model.addAttribute("status", deliveryTrackings.get("status"));
		}

		model.addAttribute("order", this.orderService.find(id));
		return "/admin/shipped/view";
	}

	/**
	 * 退款（货）中的订单列表
	 * 
	 * @param orderStatus
	 * @param paymentStatus
	 * @param pageable
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/returnOrRefundsList" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String returnOrRefundsList(Pageable pageable, ModelMap model) {
		 String searchValue = null;
			try {
				searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
				pageable.setSearchValue(searchValue);
			} catch (Exception localException) {
			}
		Tenant tenant = adminService.getCurrent().getTenant();
		List<Order.PaymentStatus> paymentStatuses = new ArrayList<Order.PaymentStatus>();// 支付状态列表
		List<Order.ShippingStatus> shippingStatuses = new ArrayList<Order.ShippingStatus>();// 发货状态列表

		shippingStatuses.add(Order.ShippingStatus.apply);// 退货中
		paymentStatuses.add(Order.PaymentStatus.refundapply);// 退款中的状态

		try {
			Page<Order> page = this.orderService.findReturnPage(tenant,
					paymentStatuses, shippingStatuses, pageable);
			List<Order> content = page.getContent();
//			for (Order order : content) {
//				System.out.println(order.getShippings().size());
//			}
			model.addAttribute("page", page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/admin/returnsOrRefunds/list";
	}

	/**
	 * 查看退款（货）中订单详情
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/viewReturnsOrRefunds" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String viewReturnsOrRefunds(Long id, ModelMap model) {
		model.addAttribute("methods", Payment.Method.values());
		model.addAttribute("refundsMethods", Refunds.Method.values());
		model.addAttribute("paymentMethods",
				this.paymentMethodService.findAll());
		model.addAttribute("shippingMethods",
				this.shippingMethodService.findAll());
		model.addAttribute("deliveryCorps", this.receiverService.findAll());
		Order order = this.orderService.find(id);
		Set<Returns> returnSet = order.getReturns();
		Shipping shipping = new Shipping();
		for (Returns returns : returnSet) {
			shipping.setTrackingNo(returns.getTrackingNo());
			shipping.setDeliveryCorp(returns.getDeliveryCorp());
			shipping.setDeliveryCorpCode(returns.getDeliveryCorp());
			Map<String, Object> deliveryTrackings = this.shippingService
					.query(shipping);
			if (deliveryTrackings.containsValue("ok")) {
				model.addAttribute("deliveryTrackings",
						deliveryTrackings.get("data"));
			} else if (deliveryTrackings.isEmpty()) {
				deliveryTrackings.put("message", "您所要查询的运单不存在");
				model.addAttribute("deliveryTrackings", deliveryTrackings);
			} else {
				model.addAttribute("deliveryTrackings", deliveryTrackings);
			}
		}
		if (returnSet.isEmpty()) {
			Map<String, Object> deliveryTrackings=new HashMap<>();
			deliveryTrackings.put("message", "您所要查询的运单不存在");
			model.addAttribute("deliveryTrackings", deliveryTrackings);
		}
		model.addAttribute("order", order);
		return "/admin/returnsOrRefunds/view";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		if (ids != null) {
			Admin admin = this.adminService.getCurrent();
			for (Long id : ids) {
				Order order = this.orderService.find(id);
				if ((order != null) && (order.isLocked(admin))) {
					return Message.error("admin.order.deleteLockedNotAllowed",
							new Object[] { order.getSn() });
				}
			}
			this.orderService.delete(ids);
		}
		return SUCCESS_MESSAGE;
	}

	/**
	 * 批量导出
	 * 
	 * @Title：exportExcel @Description：
	 * @param ids
	 * @param model
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/exportExcel", method = RequestMethod.POST)
	public ModelAndView exportExcel(Long[] ids, HttpServletRequest request,
			HttpServletResponse response) {
		List<Order> orders = orderService.findList(ids);
		List<Order> data = new ArrayList<Order>();
		Tenant tenant = adminService.getCurrent().getTenant();
		for (Order order : orders) {
			if (order.getTenant().equals(tenant)) {
				data.add(order);
			}
		}
		return new ModelAndView(new SMTExcelView("订单"
				+ DateUtil.changeDateToStr(new Date(),
						DateUtil.DB_STORE_DATE_FULL) + ".xls", data));
	}
	
	/**
	 * 查看退货单
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/viewReturns" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String comfirmReturns(Long id, ModelMap model) {
		model.addAttribute("returns", returnsService.find(id));
		return "/admin/returns/view";
	}
	
	/**
	 * 退货单确认收货
	 * @param id
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = { "/confirmReturns" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String confirmReturns(Long id, RedirectAttributes redirectAttributes) {
		Returns returns = this.returnsService.find(id);
		try {
			this.returnsService.returns(returns, null);
			
			// 给会员发送消息
			pushService.publishSystemMessage(returns.getOrder().getTenant(), returns.getOrder().getMember(), 
					SystemMessage.buyerOrderReturnSignMsg(returns.getOrder().getTenant().getShortName(), returns.getOrder().getSn()));
			addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		} catch (OrderException e) {
			e.printStackTrace();
			addFlashMessage(redirectAttributes, ERROR_MESSAGE);
		}
		return "redirect:returnOrRefundsList.jhtml";
	}
	
	/**
	 * 退款单确认退款
	 * @param id
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = { "/confirmRefunds" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String confirmRefunds(Long id, RedirectAttributes redirectAttributes) {
		Refunds refunds = this.refundsService.find(id);
		try {
			this.refundsService.agree(refunds, "", null);
			addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		} catch (OrderException e) {
			e.printStackTrace();
			addFlashMessage(redirectAttributes, ERROR_MESSAGE);
		}
		return "redirect:returnOrRefundsList.jhtml";
	}
	
	
	/**
	 * 打印发货单
	 * 
	 * @Title：exportExcel
	 * @Description：
	 * @param ids
	 * @param model
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/exportUnshipped", method = RequestMethod.GET)
	public ModelAndView exportUnshipped(Long[] ids, HttpServletRequest request,
			HttpServletResponse response) {
		Tenant tenant = adminService.getCurrent().getTenant();
		List<Order> data = orderService.findList(ids);
		return new ModelAndView(new UnShippedExcelView("发货单"
				+ DateUtil.changeDateToStr(new Date(),
						DateUtil.DB_STORE_DATE_FULL) + ".xls", data));
	}
	
	
	/*
	 * 订单转换
	 */
	private void convertStatus(String orderStatusParam,
			List<Order.OrderStatus> orderStatuses,
			List<Order.PaymentStatus> paymentStatuses,
			List<Order.ShippingStatus> shippingStatuses) {
		if (orderStatusParam != null && !"".equals(orderStatusParam)) {
			if ("unpaid".equals(orderStatusParam)) {// 待付款
				orderStatuses.add(Order.OrderStatus.confirmed);
				paymentStatuses.add(Order.PaymentStatus.unpaid);
				shippingStatuses.add(Order.ShippingStatus.unshipped);
			} else if ("unshipped".equals(orderStatusParam)) {// 待发货
				orderStatuses.add(Order.OrderStatus.confirmed);
				paymentStatuses.add(Order.PaymentStatus.paid);
				shippingStatuses.add(Order.ShippingStatus.unshipped);
			} else if ("shipped".equals(orderStatusParam)) {// 已发货
				orderStatuses.add(Order.OrderStatus.confirmed);
				paymentStatuses.add(Order.PaymentStatus.paid);
				shippingStatuses.add(Order.ShippingStatus.shipped);
			} else if ("completed".equals(orderStatusParam)) {// 交易成功
				orderStatuses.add(Order.OrderStatus.completed);
				paymentStatuses.add(Order.PaymentStatus.paid);
				shippingStatuses.add(Order.ShippingStatus.accept);
			} else if ("apply".equals(orderStatusParam)) {// 退货中
				orderStatuses.add(Order.OrderStatus.confirmed);
				paymentStatuses.add(Order.PaymentStatus.paid);
				shippingStatuses.add(Order.ShippingStatus.apply);
			} else if ("refundapply".equals(orderStatusParam)) {// 退款中
				orderStatuses.add(Order.OrderStatus.confirmed);
				paymentStatuses.add(Order.PaymentStatus.refundapply);
				shippingStatuses.add(Order.ShippingStatus.unshipped);
			} else if ("refunded".equals(orderStatusParam)) {// 已退款
				orderStatuses.add(Order.OrderStatus.confirmed);
				orderStatuses.add(Order.OrderStatus.completed);
				paymentStatuses.add(Order.PaymentStatus.refunded);
				
				shippingStatuses.add(Order.ShippingStatus.unshipped);
				shippingStatuses.add(Order.ShippingStatus.returned);
			} else if ("cancelled".equals(orderStatusParam)) {// 交易关闭
				orderStatuses.add(Order.OrderStatus.cancelled);
				paymentStatuses.add(Order.PaymentStatus.unpaid);
				shippingStatuses.add(Order.ShippingStatus.unshipped);
			}else if("accept".equals(orderStatusParam)){
				orderStatuses.add(Order.OrderStatus.confirmed);
				paymentStatuses.add(Order.PaymentStatus.paid);
				shippingStatuses.add(Order.ShippingStatus.accept);
			}
		}
	}

}