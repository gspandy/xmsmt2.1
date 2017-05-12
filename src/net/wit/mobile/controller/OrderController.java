package net.wit.mobile.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.ld.slf4j.Logger;
import cn.ld.slf4j.LoggerFactory;
import jodd.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.controller.test.BuyVo;
import net.wit.entity.InvoiceContent;
import net.wit.entity.InvoiceManagement;
import net.wit.entity.InvoiceManagement.InvoiceStat;
import net.wit.entity.Member;
import net.wit.entity.MemberBank;
import net.wit.entity.Order;
import net.wit.entity.InvoiceManagement.InvoiceStat;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.Order.ShippingStatus;
import net.wit.entity.OrderItem;
import net.wit.entity.OrderStatusConfig;
import net.wit.entity.Payment;
import net.wit.entity.Payment.Method;
import net.wit.entity.Payment.Status;
import net.wit.entity.Payment.Type;
import net.wit.entity.PaymentMethod;
import net.wit.entity.Receiver;
import net.wit.entity.Shipping;
import net.wit.entity.ShippingMethod;
import net.wit.entity.Sn;
import net.wit.entity.Tenant;
import net.wit.entity.Ticket;
import net.wit.mobile.bo.NToken;
import net.wit.mobile.service.INTokenBS;
import net.wit.mobile.service.impl.PushService;
import net.wit.mobile.util.rong.models.SystemMessage;
import net.wit.plugin.PaymentPlugin;
import net.wit.plugin.alipayMobile.util.AlipaySubmit;
import net.wit.service.InvoiceContentService;
import net.wit.service.InvoiceManagementService;
import net.wit.service.MemberBankService;
import net.wit.service.MemberService;
import net.wit.service.OrderService;
import net.wit.service.OrderStatusService;
import net.wit.service.PaymentMethodService;
import net.wit.service.PaymentService;
import net.wit.service.PluginService;
import net.wit.service.ReceiverService;
import net.wit.service.ShippingMethodService;
import net.wit.service.ShippingService;
import net.wit.service.SnService;
import net.wit.service.TicketService;
import net.wit.service.YeePayService;
import net.wit.service.impl.ZGTService;
import net.wit.util.BizException;
import net.wit.util.CacheUtil;
import net.wit.util.DateUtil;
import net.wit.util.ExcelUtil;
import net.wit.util.JsonUtils;

/**
 * Created with IntelliJ IDEA. User: ab Date: 15-9-13 Time: 下午1:56 To change this template use File | Settings | File Templates.
 */
@Controller("nOrderController")
@RequestMapping(value = "/order")
public class OrderController extends BaseController {

	private Logger log = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private INTokenBS inTokenBS;

	@Autowired
	private YeePayService yeePayService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private ShippingMethodService shippingMethodService;

	@Autowired
	private PaymentMethodService paymentMethodService;

	@Autowired
	private ReceiverService receiverService;

	@Autowired
	private TicketService ticketService;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private ShippingService shippingService;

	@Autowired
	private PluginService pluginService;

	@Autowired
	private SnService snService;

	@Autowired
	private OrderStatusService orderStatusService;

	@Autowired
	private InvoiceContentService invoiceContentService;

	@Autowired
	private InvoiceManagementService invoiceManagementService;

	@Autowired
	private MemberBankService memberBankService;
	
	@Autowired
	private PushService pushService;

	private static final String splitChar = "\\|";

	/**
	 * @param token
	 * @param response
	 * @param productId
	 * @param quantity
	 * @param receiverId
	 * @param ownerId
	 * @param ticketId
	 * @param paymentMethodId
	 * @param shippingMethodId
	 * @param invoiceTitle
	 * @param invoiceContentId
	 * @throws Exception
	 */
	@RequestMapping(value = "/placeOrder")
	public void placeOrder(@RequestParam("token") String token, HttpServletResponse response, @RequestParam("productId") String productId, @RequestParam("quantity") String quantity, @RequestParam("receiverId") String receiverId,
			@RequestParam("ownerId") String ownerId, @RequestParam("ticketId") String ticketId, @RequestParam("paymentMethodId") String paymentMethodId, @RequestParam("shippingMethodId") String shippingMethodId,
			@RequestParam("invoiceTitle") String invoiceTitle, @RequestParam("invoiceContentId") String invoiceContentId) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				log.info(
						"请求参数为：" + ",productId:" + productId + ",quantity:" + quantity + "[receiverId:" + receiverId + ",ownerId:" + ownerId + ",ticketId:" + ticketId + ",paymentMethodId:" + paymentMethodId + ",shippingMethodId:" + shippingMethodId + "]");

				String[] productIds = productId.split(splitChar);
				String[] quantitys = quantity.split(splitChar);

				BuyVo[] buys = new BuyVo[productIds.length];
				for (int i = 0, length = productIds.length; i < length; i++) {
					BuyVo buyVo = new BuyVo();
					buyVo.setProductId(new Long(productIds[i]));
					buyVo.setQuantity(new Integer(quantitys[i]));
					buys[i] = buyVo;
				}
				JSONObject resultValue = new JSONObject();
				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();

				Member member = memberService.find(new Long(memberId));
				ShippingMethod shippingMethod = shippingMethodService.find(new Long(shippingMethodId));// 配送方式
				PaymentMethod paymentMethod = paymentMethodService.find(new Long(paymentMethodId));// 支付方式ID
				Receiver receiver = receiverService.find(new Long(receiverId));// 收货地址ID
				Ticket ticket = ticketService.find(new Long(ticketId));// 内购券ID
				Member owner = memberService.find(new Long(ownerId));// 所属店主ID
				Order order = this.orderService.createImmediately(buys, member, owner, ticket, receiver, paymentMethod, shippingMethod);
				if (!StringUtil.isEmpty(invoiceContentId) && !StringUtil.isEmpty(invoiceTitle)) { // 发票抬头和发票内容同时不为空判断为要开发票
					InvoiceManagement invoiceManagement = new InvoiceManagement();
					InvoiceContent invoiceContent = new InvoiceContent();
					invoiceContent.setId(Long.parseLong(invoiceContentId));
					invoiceManagement.setInvoiceContent(invoiceContent);// 发票内容
					invoiceManagement.setInvoiceTitle(invoiceTitle);// 发票抬头
					invoiceManagement.setInvoiceValue(order.getAmountPaid());// 发票金额
					invoiceManagement.setInvoiceStat(InvoiceStat.no); // 发票未开具
					invoiceManagement.setOrder(order); // 发票关联订单
					invoiceManagement.setTenant(order.getTenant()); // 发票关联企业
					invoiceManagementService.save(invoiceManagement); // 保存发票信息
					order.setIsInvoice(true); // 修改发票是否开具发票
					order.setInvoiceTitle(invoiceTitle);
					orderService.update(order); // 修改订单信息,改为开具发票
				}
				resultValue.put("orderNo", ExcelUtil.convertNull(order.getSn()));// 订单号码
				resultValue.put("orderId", ExcelUtil.convertNull(order.getId()));// 订单ID
				resultValue.put("orderAmountPaid", ExcelUtil.convertNull(order.getAmountPaid()));// 订单金额
				this.handleJsonResponse(response, true, "下单成功", resultValue);
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * 会员下订单后进行支付
	 */
	@RequestMapping(value = "/paymentYeepay")
	public void paymentYeepay(@RequestParam("token") String token, HttpServletResponse response, @RequestParam("orderNo") String orderNo, String amount, String idcard, String bankname, String bankaccountnumber) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();
				String ledgerno = nToken.getLedgerno();

				PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin("yeepayPlugin");
				String notifyUrl = paymentPlugin.getAttribute("notifyUrl");

				JSONObject resultValue = new JSONObject();
				Order order = this.orderService.findBySn(orderNo);
				if (order == null || order.isExpired()) {
					throw new Exception("订单已到期，不能支付");
				}
				if (order.getPaymentMethod() == null || order.getPaymentMethod().getMethod() != PaymentMethod.Method.online) {
					throw new Exception("支付方式有误，不能支付");
				}
				if (order.getPaymentStatus() != PaymentStatus.unpaid && order.getPaymentStatus() != PaymentStatus.partialPayment) {
					throw new Exception("订单已支付，不能重复支付");
				}
				if (order.getAmountPaid().compareTo(new BigDecimal(0)) <= 0) {
					throw new Exception("订单金额有误，不能支付");
				}
				if (order.getPayments() != null) {
					Iterator<Payment> it = order.getPayments().iterator();
					Payment paymentHis = null;
					while (it.hasNext()) {
						paymentHis = (Payment) it.next();
						this.paymentService.delete(paymentHis);
					}

				}

				Payment payment = new Payment();
				payment.setSn(snService.generate(Sn.Type.payment));
				payment.setType(Type.payment);
				payment.setMethod(Method.online);
				payment.setStatus(Payment.Status.wait);
				payment.setPaymentMethod(order.getPaymentMethodName() + Payment.PAYMENT_METHOD_SEPARATOR + paymentPlugin.getPaymentName());
				payment.setFee(new BigDecimal(0));
				// payment.setFee(paymentPlugin.calculateFee(order.getAmountPayable()));
				// payment.setAmount(order.getAmountPayable());
				// payment.setAmount(paymentPlugin.calculateAmount(order.getAmountPayable()));
				payment.setAmount(order.getAmountPaid());
				payment.setPaymentPluginId("yeepayPlugin");// 默认使用易宝支付插件
				payment.setExpire(paymentPlugin.getTimeout() != null ? DateUtils.addMinutes(new Date(), paymentPlugin.getTimeout()) : null);
				payment.setOrder(order);
				paymentService.save(payment);

				Map<String, String> params = new HashMap<String, String>();

				params.put("memberId", memberId);// 会员ID
				params.put("ledgerno", ledgerno);// 子账户编号
				params.put("amount", amount);// 金额

				params.put("callbackurl", notifyUrl);// 后台通知地址
				params.put("requestid", payment.getSn());// 订单号
				params.put("orderNo", orderNo);// 订单号
				Map<String, String> result = yeePayService.orderPayment(params);// 调用易宝插件进行支付

				if ("1".equals(result.get("code"))) {// 支付成功, 订单状态改为未“已支付，未到账”
					// payment.setStatus(Payment.Status.unreached);
					// paymentService.update(payment);
					// order.setPaymentStatus(Order.PaymentStatus.unreached);
					// this.orderService.update(order);
				} else {
					throw new BizException("当前无法支付，请重试");
				}
				resultValue.put("payurl", ExcelUtil.convertNull(result.get("payurl")));
				log.info("payurl:" + result.get("payurl"));
				this.handleJsonResponse(response, true, "订单支付跳转成功", resultValue);
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 订单支付后台通知URL（暂时弃用）
	 */
	@RequestMapping(value = "/paymentYeepayNotify")
	public void paymentYeepayNotify(HttpServletResponse response, HttpServletRequest request) throws Exception {
		String resultStr = null;
		try {
			String data = request.getParameter("data");
			log.info("易宝后台通知数据data : " + data);
			Map<String, String> result = ZGTService.decryptPayCallbackData(data);
			String requestid = result.get("requestid");
			String code = result.get("code");
			// 进行自己内部逻辑的处理后，返回success,
			Payment payment = paymentService.findBySn(requestid);
			if (payment != null && !Status.success.equals(payment.getStatus())) {
				PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(payment.getPaymentPluginId());
				if (paymentPlugin != null) {
					if (paymentPlugin.verifyNotify(requestid, null, request)) {
						try {
							// 进行分账支付单处理及分账
							paymentService.handle(payment);
							resultStr = "SUCCESS";
							response.getWriter().write("SUCCESS");
						} catch (Exception e) {
							// 不做处理
							e.printStackTrace();
						}
					} else {
						response.getWriter().write("FAILURE");
						payment.setStatus(Status.failure);
						payment.setPaymentDate(new Date());
						paymentService.update(payment);
						// Order order = payment.getOrder();
						// order.setPaymentStatus(Order.PaymentStatus.);// 订单状态改为支付失败？
					}
				}
			}
		} catch (Exception e) {
			response.getWriter().write("EXCEPTION");
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
		// return resultStr;
	}

	@RequestMapping(value = "/tenantGetOrders")
	public void tenateGetOrders(HttpServletResponse response, String orderStatus, @RequestParam("userType") String userType, @RequestParam("pageNumber") String pageNumber, @RequestParam("pageSize") String pageSize, @RequestParam("token") String token)
			throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				log.info("请求参数为：" + "[pageNumber:" + pageNumber + ",pageSize:" + pageSize + ",userType:" + userType + ",token:" + token + ",orderStatus:" + orderStatus + "]");

				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();
				// String nickName = nToken.getNickName();
				JSONArray jsonArray = new JSONArray();
				JSONObject resultValue;
				Member member = null;
				Member owner = null;
				if ("0".equals(userType)) {// 会员查找订单列表
					member = new Member();
					member.setId(new Long(memberId));
				} else if ("1".equals(userType)) {// 店主查找订单列表
					owner = new Member();
					owner.setId(new Long(memberId));
				}

				Pageable pageable = new Pageable();
				List<net.wit.Order> orders = new ArrayList<net.wit.Order>();
				orders.add(net.wit.Order.desc("modifyDate"));

				pageable.setPageNumber(Integer.parseInt(pageNumber));
				pageable.setPageSize(Integer.parseInt(pageSize));
				pageable.setOrders(orders);
				Page<Order> orderPage = null;
				if ("unpaid".equals(orderStatus)) {// 待支付
					orderPage = orderService.findMemberAndOwer(member, owner, null, Order.OrderStatus.confirmed, Order.PaymentStatus.unpaid, null, pageable);
				} else if ("shipped".equals(orderStatus)) {// 待收货
					// orderPage = orderService.findMemberAndOwer(member, owner, null, null, null, ShippingStatus.shipped, pageable);
					List<PaymentStatus> paymentStatuses = new ArrayList<PaymentStatus>();
					List<ShippingStatus> shippingStatuses = new ArrayList<ShippingStatus>();
					paymentStatuses.add(PaymentStatus.paid);
					shippingStatuses.add(ShippingStatus.unshipped);
					shippingStatuses.add(ShippingStatus.shipped);
					orderPage = orderService.findMemberAndOwer(member, owner, null, paymentStatuses, shippingStatuses, pageable);
				} else if (StringUtils.isEmpty(orderStatus)) {
					orderPage = orderService.findMemberAndOwer(member, owner, null, null, null, null, pageable);
				}
				if (orderPage != null & orderPage.getContent() != null && orderPage.getContent().size() > 0) {
					List<Order> orderList = orderPage.getContent();
					if (CollectionUtils.isNotEmpty(orderList)) {
						for (Order order : orderList) {
							this.convertStatus(order);
							resultValue = new JSONObject();
							resultValue.put("orderStatusName", ExcelUtil.convertNull(order.getOrderStatusName()));
							resultValue.put("orderMobileStatus", ExcelUtil.convertNull(order.getOrderMobileStatus()));
							resultValue.put("createDate", ExcelUtil.convertNull(DateUtil.changeDateToStr(order.getCreateDate(), DateUtil.LINK_DISPLAY_DATE)));
							resultValue.put("orderStatus", ExcelUtil.convertNull(order.getOrderStatus().name()));
							resultValue.put("amountPaid", ExcelUtil.convertNull(order.getAmountPaid().setScale(2, BigDecimal.ROUND_HALF_UP)));
							List<OrderItem> orderItemList = order.getOrderItems();
							Integer quantity = 0;
							if (CollectionUtils.isNotEmpty(orderItemList)) {
								for (OrderItem orderItem : orderItemList) {
									quantity = quantity + orderItem.getQuantity();
								}
							}
							List<OrderItem> orderItems = order.getOrderItems();
							String thumbnail = orderItems.get(0).getThumbnail();

							Set<Shipping> shippings = order.getShippings();
							Shipping shipping = new Shipping();
							if (shippings != null) {
								Iterator<Shipping> it = shippings.iterator();
								if (it.hasNext()) {
									shipping = (Shipping) it.next();
								}
							}

							resultValue.put("quantity", ExcelUtil.convertNull(quantity));
							resultValue.put("nickName", ExcelUtil.convertNull(order.getMember().getNickName()));
							resultValue.put("chargeAmt", ExcelUtil.convertNull(order.getChargeAmt().setScale(2, BigDecimal.ROUND_HALF_UP)));
							resultValue.put("thumbnail", ExcelUtil.convertNull(thumbnail));
							resultValue.put("orderId", ExcelUtil.convertNull(order.getId()));
							resultValue.put("orderNo", ExcelUtil.convertNull(order.getSn()));
							resultValue.put("paymentMethodName", ExcelUtil.convertNull(order.getPaymentMethodName()));// 支付方式
							resultValue.put("trackingNo", ExcelUtil.convertNull(shipping.getTrackingNo()));
							resultValue.put("deliveryCorp", ExcelUtil.convertNull(shipping.getDeliveryCorp()));
							String isExtend = "false";
							if (order.getIsExtend() != null)
								isExtend = order.getIsExtend().toString();
							resultValue.put("isExtend", isExtend);

							jsonArray.add(resultValue);
						}
					}
				}
				this.handleJsonArrayResponse(response, true, "", jsonArray);
			}

		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/orderDetail")
	public void orderDetail(HttpServletResponse response, @RequestParam("token") String token, @RequestParam("orderId") String orderId) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));

			} else {
				NToken nToken = inTokenBS.get(token);

				JSONObject resultValue = new JSONObject();

				Order order = orderService.find(new Long(orderId));
				this.convertStatus(order);
				Set<Shipping> shippings = order.getShippings();
				Shipping shipping = new Shipping();
				if (shippings != null) {
					Iterator<Shipping> it = shippings.iterator();
					if (it.hasNext()) {
						shipping = (Shipping) it.next();
					}
				}
				resultValue.put("chargeAmt", ExcelUtil.convertNull(order.getChargeAmt()));// 佣金
				resultValue.put("sn", ExcelUtil.convertNull(order.getSn()));// 订单号
				resultValue.put("createDate", ExcelUtil.convertNull(DateUtil.changeDateToStr(order.getCreateDate(), DateUtil.LINK_DISPLAY_DATE)));
				resultValue.put("payType", ExcelUtil.convertNull(order.getPaymentMethod().getName()));
				resultValue.put("orderStatus", ExcelUtil.convertNull(order.getOrderStatus().name()));
				resultValue.put("orderStatusName", ExcelUtil.convertNull(order.getOrderStatusName()));
				resultValue.put("orderMobileStatus", ExcelUtil.convertNull(order.getOrderMobileStatus()));
				resultValue.put("orderId", ExcelUtil.convertNull(order.getId()));

				JSONArray jsonArray = new JSONArray();
				List<OrderItem> orderItemList = order.getOrderItems();

				BigDecimal totalMoney = BigDecimal.ZERO;
				BigDecimal markerAmount;// 商品市场价
				Integer quantity;
				if (CollectionUtils.isNotEmpty(orderItemList)) {
					JSONObject productInfo;
					for (OrderItem orderItem : orderItemList) {
						productInfo = new JSONObject();
						productInfo.put("thumbnail", ExcelUtil.convertNull(orderItem.getThumbnail()));// 商品缩略图
						productInfo.put("fullName", ExcelUtil.convertNull(orderItem.getFullName()));// 商品全名
						productInfo.put("packagUnitName", ExcelUtil.convertNull(orderItem.getPackagUnitName()));// 打包单位名称
						quantity = orderItem.getQuantity();// 数量
						if (quantity == null) {
							quantity = 0;
						}
						productInfo.put("quantity", ExcelUtil.convertNull(quantity));
						markerAmount = orderItem.getMarkerAmount();
						if (markerAmount == null) {
							markerAmount = BigDecimal.ZERO;
						}
						productInfo.put("price", ExcelUtil.convertNull(markerAmount.setScale(2, BigDecimal.ROUND_HALF_UP)));

						totalMoney = totalMoney.add(markerAmount.multiply(new BigDecimal(quantity)));

						productInfo.put("specification", orderItem.getProduct().getSpecification_value());
						productInfo.put("productId", orderItem.getProduct().getId());
						productInfo.put("orderItemId", orderItem.getId().toString());
						productInfo.put("finalPrice", ExcelUtil.convertNull(orderItem.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP)));

						jsonArray.add(productInfo);
					}
				}

				BigDecimal amountPaid = order.getAmountPaid();// 订单价格
				BigDecimal couponDiscount = totalMoney.subtract(amountPaid);// 优惠的价格
				resultValue.put("productInfo", jsonArray);

				resultValue.put("consignee", ExcelUtil.convertNull(order.getConsignee()));// 收货人
				resultValue.put("tel", ExcelUtil.convertNull(order.getPhone()));// 电话

				resultValue.put("totalMoney", ExcelUtil.convertNull(totalMoney.setScale(2, BigDecimal.ROUND_HALF_UP)));// 总金额
				resultValue.put("freight", ExcelUtil.convertNull(order.getFreight().setScale(2, BigDecimal.ROUND_HALF_UP)));// 运费
				resultValue.put("couponDiscount", ExcelUtil.convertNull(couponDiscount.setScale(2, BigDecimal.ROUND_HALF_UP)));// 内购优惠
				resultValue.put("amountPaid", ExcelUtil.convertNull(amountPaid.setScale(2, BigDecimal.ROUND_HALF_UP)));// 支付金额
				resultValue.put("address", ExcelUtil.convertNull(order.getAreaName() + order.getAddress()));// 收货地址
				resultValue.put("trackingNo", ExcelUtil.convertNull(shipping.getTrackingNo()));
				resultValue.put("deliveryCorp", ExcelUtil.convertNull(shipping.getDeliveryCorp()));
				this.handleJsonResponse(response, true, "", resultValue);
			}

		} catch (Exception e) {
			this.handleJsonResponse(response, false, "获取订单详情失败");
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/goodsDetails")
	public void goodsDetails(HttpServletResponse response, @RequestParam("orderId") String orderId) throws Exception {
		try {
			JSONObject resultValue = new JSONObject();
			Order order = orderService.find(new Long(orderId));
			if (order != null) {
				resultValue.put("sn", ExcelUtil.convertNull(order.getSn()));
				resultValue.put("createDate", ExcelUtil.convertNull(DateUtil.changeDateToStr(order.getCreateDate(), DateUtil.LINK_DISPLAY_DATE_FULL)));

				Map<String, Object> map = new HashMap<String, Object>();
				Object deliveryCorp = null;
				Object trackingNo = null;
				Set<Shipping> shippingSet = order.getShippings();
				if (CollectionUtils.isNotEmpty(shippingSet)) {
					Iterator iterator = shippingSet.iterator();
					Shipping shipping = null;
					if (iterator.hasNext()) {
						shipping = (Shipping) iterator.next();
					}
					if (shipping == null) {
						shipping = new Shipping();
					}
					deliveryCorp = ExcelUtil.convertNull(shipping.getDeliveryCorp());
					trackingNo = ExcelUtil.convertNull(shipping.getTrackingNo());
					map = shippingService.query(shipping);

				}
				resultValue.put("deliveryCorp", ExcelUtil.convertNull(deliveryCorp));
				resultValue.put("trackingNo", ExcelUtil.convertNull(trackingNo));
				if (MapUtils.isEmpty(map)) {
					map = new HashMap<String, Object>();
				}
				resultValue.put("shipping", map);
			}
			this.handleJsonResponse(response, true, "", resultValue);

		} catch (Exception e) {
			this.handleJsonResponse(response, false, "货品详情获取失败");
			e.printStackTrace();
		}
	}

	// /**
	// * 返回买家默认收货地址
	// */
	// @RequestMapping(value = "/memberReceiver")
	// public void memberReceiver(
	//// @RequestParam("token") String token,
	// HttpServletResponse response,@RequestParam("memberId") String memberId
	// ) throws Exception {
	// try {
	//// if (!inTokenBS.isVaild(token)) {
	//// this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
	//// } else {
	// JSONObject resultValue = new JSONObject();
	// Member member=new Member();
	// member.setId(Long.parseLong(memberId));
	// resultValue.put("receiver", JsonUtils.toJson(receiverService.findDefault(member)));
	// this.handleJsonResponse(response, true,"",resultValue );
	// ;
	//
	//// }
	// } catch (Exception e) {
	// this.handleJsonResponse(response, false, e.getMessage());
	// e.printStackTrace();
	// }
	// }
	/**
	 * 返回买家收货地址列表
	 */
	@RequestMapping(value = "/memberReceiverList")
	public void memberReceiverList(@RequestParam("token") String token, HttpServletResponse response) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				JSONObject resultValue = new JSONObject();
				NToken nToken = inTokenBS.get(token);
				Member member = new Member();
				member.setId(Long.parseLong(nToken.getMemberId()));
				resultValue.put("receiverList", JsonUtils.toJson(receiverService.findList(member)));
				this.handleJsonResponse(response, true, "", resultValue);

			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, "获取买家收货地址列表失败");
			e.printStackTrace();
		}
	}

	/**
	 * 获取配送方式列表
	 */
	@RequestMapping(value = "/shippingMethodList")
	public void shippingMethodList(@RequestParam("token") String token, HttpServletResponse response) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				JSONObject resultValue = new JSONObject();
				resultValue.put("receiverList", JsonUtils.toJson(shippingMethodService.findAll()));
				this.handleJsonResponse(response, true, "", resultValue);
				;
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, "获取配送方式列表失败");
			e.printStackTrace();
		}
	}

	/**
	 * 获取支付方式列表
	 */
	@RequestMapping(value = "/paymentMethodList")
	public void paymentMethodList(@RequestParam("token") String token, HttpServletResponse response) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				JSONObject resultValue = new JSONObject();
				resultValue.put("receiverList", JsonUtils.toJson(paymentMethodService.findAll()));
				this.handleJsonResponse(response, true, "", resultValue);
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, "获取配送方式列表失败");
			e.printStackTrace();
		}
	}

	/**
	 * 新增收货地址
	 */
	@RequestMapping(value = "/receiverAdd")
	public void receiverAdd(@RequestParam("token") String token, HttpServletResponse response, @RequestParam("address") String address, @RequestParam("areaName") String areaName, @RequestParam("consignee") String consignee,
			@RequestParam("isDefault") String isDefault, @RequestParam("phone") String phone, @RequestParam("zipCode") String zipCode) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				NToken nToken = inTokenBS.get(token);
				Receiver receiver = new Receiver();
				Member member = new Member();
				member.setId(Long.parseLong(nToken.getMemberId()));
				receiver.setMember(member);
				receiver.setAddress(address);
				receiver.setAreaName(areaName);
				receiver.setConsignee(consignee);
				if (isDefault.equals("1")) {
					receiver.setIsDefault(true);
				} else {
					receiver.setIsDefault(false);
				}
				receiver.setPhone(phone);
				receiver.setZipCode(zipCode);
				receiverService.addAddress(receiver);
				this.handleJsonResponse(response, true, "地址保存成功！");

			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, "地址保存失败");
			e.printStackTrace();
		}
	}

	/**
	 * 修改收货地址
	 */
	@RequestMapping(value = "/updateReceiver")
	public void updateReceiver(@RequestParam("token") String token, HttpServletResponse response, String address, String areaName, String consignee, @RequestParam("isDefault") String isDefault, String phone, String zipCode, @RequestParam("id") String id)
			throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				NToken nToken = inTokenBS.get(token);
				Receiver receiver = new Receiver();
				Member member = new Member();
				member.setId(Long.parseLong(nToken.getMemberId()));
				if (id != null && !(id.equals(""))) {
					receiver = receiverService.find(new Long(id));
				}
				if (address != null && !(address.equals(""))) {
					receiver.setAddress(address);
				}
				if (areaName != null && !(areaName.equals(""))) {
					receiver.setAreaName(areaName);
				}
				if (consignee != null && !(consignee.equals(""))) {
					receiver.setConsignee(consignee);
				}
				if (isDefault.equals("1")) {
					receiver.setIsDefault(true);
				} else {
					receiver.setIsDefault(false);
				}
				if (phone != null && !(phone.equals(""))) {
					receiver.setPhone(phone);
				}
				if (zipCode != null && !(zipCode.equals(""))) {
					receiver.setZipCode(zipCode);
				}
				receiverService.addAddress(receiver);
				this.handleJsonResponse(response, true, "地址修改成功！");

			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, "地址修改失败");
			e.printStackTrace();
		}
	}

	/**
	 * 删除地址
	 */
	@RequestMapping(value = "/receiverDelete")
	public void receiverDelete(HttpServletResponse response, @RequestParam("receiverId") String receiverId) throws Exception {
		try {
			if (receiverId != null) {
				receiverService.delete(new Long(receiverId));
				this.handleJsonResponse(response, true, "删除成功");
			} else {
				this.handleJsonResponse(response, false, "未选中要删除的地址");
			}

		} catch (Exception e) {
			this.handleJsonResponse(response, false, "删除地址失败");
			e.printStackTrace();
		}
	}

	/**
	 * 确认收货
	 */
	@RequestMapping(value = "/signOrder")
	public void signOrder(HttpServletResponse response, @RequestParam("token") String token, @RequestParam("orderNo") String orderNo) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				log.info("请求参数为：" + "[token:" + token + ",orderNo:" + orderNo + "]");
				if (orderNo != null && !"".equals(orderNo)) {
					Order order = this.orderService.findBySn(orderNo);
					this.orderService.sign(order);
					
					// 给会员发送消息
					pushService.publishSystemMessage(order.getTenant(), order.getMember(), 
							SystemMessage.buyerOrderSignMsg(order.getTenant().getShortName(), order.getSn()));
					this.handleJsonResponse(response, true, "收货成功");
				}
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, "无法收货");
			e.printStackTrace();
		}
	}

	/**
	 * 延长收货
	 */
	@RequestMapping(value = "/extendOrder")
	public void extendOrder(HttpServletResponse response, @RequestParam("token") String token, @RequestParam("orderNo") String orderNo) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				log.info("请求参数为：" + "[token:" + token + ",orderNo:" + orderNo + "]");
				if (orderNo != null && !"".equals(orderNo)) {
					Order order = this.orderService.findBySn(orderNo);
					boolean isExtend = order.getIsExtend() == null ? false : order.getIsExtend();
					if (isExtend) {
						this.handleJsonResponse(response, false, "系统只允许延长收货一次");
					} else {
						this.orderService.extendAccept(order);
						this.handleJsonResponse(response, true, "延长收货成功");
					}
				}
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 取消订单
	 */
	@RequestMapping(value = "/cancel")
	public void cancel(@RequestParam("token") String token, HttpServletResponse response, @RequestParam("orderId") String orderId) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				log.info("请求参数为：" + "[orderId:" + orderId + "]");
				Order order = this.orderService.find(new Long(orderId));
				this.orderService.cancel(order, null);
				this.handleJsonResponse(response, true, "订单取消成功");
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}

	}

	private void convertStatus(Order order) {
		OrderStatusConfig orderStatusConfig = new OrderStatusConfig();
		List<OrderStatusConfig> list = this.orderStatusService.findList(order.getOrderStatus().toString(), order.getPaymentStatus().toString(), order.getShippingStatus().toString());
		if (list != null && list.size() > 0) {
			orderStatusConfig = (OrderStatusConfig) list.get(0);
			order.setOrderStatusName(orderStatusConfig.getMobileShow());
			order.setOrderMobileStatus(orderStatusConfig.getMobileStatus());
		}
		log.info("getMobileButton : " + orderStatusConfig.getMobileButton());
		log.info("getMobileShow : " + orderStatusConfig.getMobileShow());
		log.info("getPcButton : " + orderStatusConfig.getPcButton());
		log.info("getPcButton : " + orderStatusConfig.getPcButton());

	}

	/**
	 * 会员下订单后进行支付
	 */
	@RequestMapping(value = "/payment")
	public void payment(@RequestParam("token") String token, HttpServletResponse response, @RequestParam("orderNo") String orderNo) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				NToken nToken = inTokenBS.get(token);
				PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin("alipayMobilePlugin");
				String notifyUrl = paymentPlugin.getAttribute("notifyUrl");

				JSONObject resultValue = new JSONObject();
				Order order = this.orderService.findBySn(orderNo);
				if (order == null) {
					throw new Exception("订单不存在，不能支付");
				}
				Tenant tenant = order.getTenant();
				// Set<MemberBank> memberBankSet = tenant.getMemberBanks();
				// MemberBank memberBank = new MemberBank();
				// if (memberBankSet != null && memberBankSet.iterator() != null && memberBankSet.iterator().hasNext()) {
				// memberBank = memberBankSet.iterator().next();
				// }
				MemberBank memberBank = this.memberBankService.getMemberBankByTenantId(tenant.getId(), MemberBank.Type.debit);// 获取收款支付宝账号
				if (order.isExpired()) {
					throw new Exception("订单已到期，不能支付");
				}
				if (order.getPaymentMethod() == null || order.getPaymentMethod().getMethod() != PaymentMethod.Method.online) {
					throw new Exception("支付方式有误，不能支付");
				}
				if (order.getPaymentStatus() != PaymentStatus.unpaid && order.getPaymentStatus() != PaymentStatus.partialPayment) {
					throw new Exception("订单已支付，不能重复支付");
				}
				if (order.getAmountPaid().compareTo(new BigDecimal(0)) <= 0) {
					throw new Exception("订单金额有误，不能支付");
				}
				if (order.getPayments() != null) {
					Iterator<Payment> it = order.getPayments().iterator();
					Payment paymentHis = null;
					while (it.hasNext()) {
						paymentHis = (Payment) it.next();
						this.paymentService.delete(paymentHis);
					}
				}
				Payment payment = new Payment();
				payment.setSn(snService.generate(Sn.Type.payment));
				payment.setType(Type.payment);
				payment.setMethod(Method.online);
				payment.setStatus(Payment.Status.wait);
				payment.setPaymentMethod(order.getPaymentMethodName() + Payment.PAYMENT_METHOD_SEPARATOR + paymentPlugin.getPaymentName());
				payment.setFee(new BigDecimal(0));
				payment.setAmount(order.getAmountPaid());
				payment.setPaymentPluginId("alipayMobilePlugin");// 默认使用易宝支付插件
				payment.setExpire(paymentPlugin.getTimeout() != null ? DateUtils.addMinutes(new Date(), paymentPlugin.getTimeout()) : null);
				payment.setOrder(order);
				paymentService.save(payment);

				Map<String, String> params = new HashMap<String, String>();

				params.put("amount", order.getAmountPaid().setScale(2, BigDecimal.ROUND_HALF_UP).toString());// 订单金额
				params.put("notify_url", notifyUrl);// 后台通知地址
				params.put("requestid", payment.getSn());// 订单号
				params.put("orderNo", orderNo);// 订单号
				params.put("subject", "速卖通商品");// 商品名称
				params.put("partner", memberBank.getBankProvince());// 获取企业的支付宝PID,存于银行卡省份字段中
				params.put("privateKey", memberBank.getBankCity());// 获取企业的支付宝私钥,存于银行卡城市字段中
				params.put("seller_id", memberBank.getCardNo());// 支付宝账号（邮箱）

				Map<String, String> result = AlipaySubmit.buildRequest(params);// 调用支付宝进行支付

				resultValue.put("resultStr", ExcelUtil.convertNull(result.get("resultStr")));
				// 返回给客户端的值
				log.info("resultStr:" + result.get("resultStr"));
				this.handleJsonResponse(response, true, "订单支付跳转成功", resultValue);
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/getInvoiceContent")
	public void payment(HttpServletResponse response, @RequestParam("tenantId") String tenantId) throws Exception {
		try {
			Tenant tenant = new Tenant();
			tenant.setId(Long.parseLong(tenantId));
			Pageable pageable = new Pageable();
			pageable.setPageNumber(0);
			pageable.setPageSize(50);
			Page<InvoiceContent> page = invoiceContentService.findInvoiceContentsPage(tenant, pageable);
			List<InvoiceContent> list = page.getContent();
			JSONArray jsa = new JSONArray();
			for (InvoiceContent invoiceContent : list) {
				JSONObject js = new JSONObject();
				js.put("invoiceContent", invoiceContent.getContent());
				js.put("invoiceId", invoiceContent.getId() + "");
				jsa.add(js);
			}
			this.handleJsonResponse(response, true, "", jsa);
		} catch (Exception e) {
			this.handleJsonResponse(response, false, "发票内容查询失败! 请联系平台客服");
			log.error(e.getMessage());
		}
	}
	
	@RequestMapping(value = "/receiverEdit")
	public void receiverEdit(@RequestParam("token") String token, HttpServletResponse response, @RequestParam("address") String address, @RequestParam("areaName") String areaName, @RequestParam("consignee") String consignee,
			@RequestParam("isDefault") String isDefault, @RequestParam("phone") String phone, @RequestParam("zipCode") String zipCode,@RequestParam("id") Long id) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				NToken nToken = inTokenBS.get(token);
				Receiver receiver = new Receiver();
				Member member = new Member();
				member.setId(Long.parseLong(nToken.getMemberId()));
				receiver.setMember(member);
				receiver.setId(id);
				receiver.setAddress(address);
				receiver.setAreaName(areaName);
				receiver.setConsignee(consignee);
				receiver.setPhone(phone);
				receiver.setZipCode(zipCode);
				if (isDefault.equals("1")) {
					receiver.setIsDefault(true);
				} else {
					receiver.setIsDefault(false);
				}
				receiverService.update(receiver);
				this.handleJsonResponse(response, true, "地址修改成功！");

			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, "地址修改失败");
			e.printStackTrace();
		}
	}
	
}
