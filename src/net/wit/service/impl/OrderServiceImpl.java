/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.persistence.LockModeType;

import net.wit.Filter;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.Setting;
import net.wit.Setting.StockAllocationTime;
import net.wit.constant.SettingConstant;
import net.wit.controller.test.BuyVo;
import net.wit.dao.CartDao;
import net.wit.dao.CouponCodeDao;
import net.wit.dao.CouponDao;
import net.wit.dao.DepositDao;
import net.wit.dao.MemberDao;
import net.wit.dao.MemberRankDao;
import net.wit.dao.OrderDao;
import net.wit.dao.OrderItemDao;
import net.wit.dao.OrderLogDao;
import net.wit.dao.OrderSettlementDao;
import net.wit.dao.PaymentDao;
import net.wit.dao.ProductDao;
import net.wit.dao.RefundsDao;
import net.wit.dao.ReturnsDao;
import net.wit.dao.ShippingDao;
import net.wit.dao.SnDao;
import net.wit.dao.StockDao;
import net.wit.dao.TicketDao;
import net.wit.dao.TradeDao;
import net.wit.domain.DistributionStrategy;
import net.wit.domain.StockStrategy;
import net.wit.domain.TradeStrategy;
import net.wit.entity.Admin;
import net.wit.entity.Appointment;
import net.wit.entity.Authenticode;
import net.wit.entity.Cart;
import net.wit.entity.CartItem;
import net.wit.entity.Coupon;
import net.wit.entity.CouponCode;
import net.wit.entity.Deposit;
import net.wit.entity.GiftItem;
import net.wit.entity.Member;
import net.wit.entity.MemberRank;
import net.wit.entity.MessageDetails.MessageType;
import net.wit.entity.Order;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.OrderType;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.Order.ShippingStatus;
import net.wit.entity.OrderItem;
import net.wit.entity.OrderLog;
import net.wit.entity.OrderLog.Type;
import net.wit.entity.OrderSettlement;
import net.wit.entity.OrderSettlement.SettlementStatus;
import net.wit.entity.Payment;
import net.wit.entity.Payment.Method;
import net.wit.entity.Payment.Status;
import net.wit.entity.PaymentMethod;
import net.wit.entity.Product;
import net.wit.entity.Promotion;
import net.wit.entity.Receiver;
import net.wit.entity.Refunds;
import net.wit.entity.Returns;
import net.wit.entity.ReturnsItem;
import net.wit.entity.SafeKey;
import net.wit.entity.Shipping;
import net.wit.entity.ShippingItem;
import net.wit.entity.ShippingMethod;
import net.wit.entity.Sn;
import net.wit.entity.Stock;
import net.wit.entity.Tenant;
import net.wit.entity.Ticket;
import net.wit.entity.Trade;
import net.wit.enums.OrderSearchStatus;
import net.wit.exception.OrderException;
import net.wit.mobile.cache.CacheUtil;
import net.wit.mobile.controller.BatchJobController;
import net.wit.mobile.service.impl.PushService;
import net.wit.mobile.util.rong.models.SystemMessage;
import net.wit.service.AdminService;
import net.wit.service.AppointmentService;
import net.wit.service.MessageDetailsService;
import net.wit.service.OrderService;
import net.wit.service.OrderSettlementService;
import net.wit.service.SaleStatisticService;
import net.wit.service.SmsSendService;
import net.wit.service.SnService;
import net.wit.service.StaticService;
import net.wit.service.StockService;
import net.wit.service.TenantShopkeeperService;
import net.wit.support.EntitySupport;
import net.wit.util.BizException;
import net.wit.util.DateUtil;
import net.wit.util.RandomUtil;
import net.wit.util.SettingUtils;
import net.wit.util.SpringUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Service - 订单
 * @author rsico Team
 * @version 3.0
 */
@Service("orderServiceImpl")
public class OrderServiceImpl extends BaseServiceImpl<Order, Long>implements OrderService {
	
	  private Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Resource(name = "orderDaoImpl")
	private OrderDao orderDao;

	@Resource(name = "ticketDaoImpl")
	private TicketDao ticketDao;

	@Resource(name = "orderItemDaoImpl")
	private OrderItemDao orderItemDao;

	@Resource(name = "orderLogDaoImpl")
	private OrderLogDao orderLogDao;

	@Resource(name = "cartDaoImpl")
	private CartDao cartDao;

	@Resource(name = "couponCodeDaoImpl")
	private CouponCodeDao couponCodeDao;

	@Resource(name = "couponDaoImpl")
	private CouponDao couponDao;

	@Resource(name = "snDaoImpl")
	private SnDao snDao;

	@Resource(name = "memberDaoImpl")
	private MemberDao memberDao;

	@Resource(name = "memberRankDaoImpl")
	private MemberRankDao memberRankDao;

	@Resource(name = "tradeDaoImpl")
	private TradeDao tradeDao;

	@Resource(name = "orderSettlementServiceImpl")
	private OrderSettlementService orderSettlementService;

	@Resource(name = "productDaoImpl")
	private ProductDao productDao;

	@Resource(name = "depositDaoImpl")
	private DepositDao depositDao;

	@Resource(name = "paymentDaoImpl")
	private PaymentDao paymentDao;

	@Resource(name = "refundsDaoImpl")
	private RefundsDao refundsDao;

	@Resource(name = "shippingDaoImpl")
	private ShippingDao shippingDao;

	@Resource(name = "returnsDaoImpl")
	private ReturnsDao returnsDao;

	@Resource(name = "staticServiceImpl")
	private StaticService staticService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@Resource(name = "smsSendServiceImpl")
	private SmsSendService smsSendService;

	@Resource(name = "snServiceImpl")
	private SnService snService;

	@Resource(name = "stockServiceImpl")
	private StockService stockService;

	@Resource(name = "appointmentServiceImpl")
	private AppointmentService appointmentService;

	@Resource(name = "stockDaoImpl")
	private StockDao stockDao;

	@Resource(name = "orderSettlementDaoImpl")
	private OrderSettlementDao orderSettlementDao;

	@Resource(name = "messageDetailsServiceImpl")
	private MessageDetailsService messageDetailsService;

	@Resource(name = "tenantShopkeeperServiceImpl")
	private TenantShopkeeperService tenantShopkeeperService;

	@Resource
	private TradeStrategy tradeStrategy;

	@Resource
	private StockStrategy stockStrategy;

	@Resource(name = "saleStatisticService")
	private SaleStatisticService saleStatisticService;

	@Resource
	private DistributionStrategy distributionStrategy;
	
	@Autowired
	private PushService pushService;

	@Resource(name = "orderDaoImpl")
	public void setBaseDao(OrderDao orderDao) {
		super.setBaseDao(orderDao);
	}

	public void releaseStock() {
		orderDao.releaseStock();
	}

	@Transactional(readOnly = true)
	public Order build(Cart cart, Receiver receiver, PaymentMethod paymentMethod, ShippingMethod shippingMethod, CouponCode couponCode, boolean isInvoice, String invoiceTitle, boolean useBalance, String memo) {
		Assert.notNull(cart);
		Assert.notNull(cart.getMember());
		Assert.notEmpty(cart.getCartItems());
		Order order = EntitySupport.createInitOrder();
		if (receiver != null) {
			order.setMember(receiver.getMember());
			order.setConsignee(receiver.getConsignee());
			order.setAreaName(receiver.getAreaName());
			order.setAddress(receiver.getAddress());
			order.setZipCode(receiver.getZipCode());
			order.setPhone(receiver.getPhone());
			order.setArea(receiver.getArea());
		} else {
			order.setMember(cart.getMember());
		}
		if (!cart.getPromotions().isEmpty()) {
			StringBuffer promotionName = new StringBuffer();
			for (Promotion promotion : cart.getPromotions()) {
				if (promotion != null && promotion.getName() != null) {
					promotionName.append(" " + promotion.getName());
				}
			}
			if (promotionName.length() > 0) {
				promotionName.deleteCharAt(0);
			}
			order.setPromotion(promotionName.toString());
		}
		order.setPoint(cart.getEffectivePoint());
		order.setPromotionDiscount(cart.getDiscount());
		order.setMemo(memo);
		order.setPaymentMethod(paymentMethod);
		// 配送方式及运费
		if (shippingMethod != null && paymentMethod != null && paymentMethod.getShippingMethods().contains(shippingMethod)) {
			BigDecimal freight = shippingMethod.calculateFreight(cart.getWeight());
			for (Promotion promotion : cart.getPromotions()) {
				if (promotion.getIsFreeShipping()) {
					freight = BigDecimal.ZERO;
					break;
				}
			}
			order.setFreight(freight);
			order.setShippingMethod(shippingMethod);
		} else {
			order.setFreight(BigDecimal.ZERO);
		}
		if (couponCode != null) {
			if (Coupon.Type.point == couponCode.getCoupon().getType()) { // 积分兑换券
				couponCodeDao.lock(couponCode, LockModeType.PESSIMISTIC_WRITE);
				if (!couponCode.getIsUsed() && couponCode.getCoupon() != null) {
					BigDecimal couponDiscount = couponCode.getCoupon().calculatePrice(couponCode.getPoint());
					order.setCouponDiscount(couponDiscount);
					order.setCouponCode(couponCode);
				}
			} else if (Coupon.Type.promotion == couponCode.getCoupon().getType()) { // 促销兑换券
				if (cart.isCouponAllowed()) {
					couponCodeDao.lock(couponCode, LockModeType.PESSIMISTIC_WRITE);
					if (!couponCode.getIsUsed() && couponCode.getCoupon() != null && cart.isValid(couponCode.getCoupon())) {
						BigDecimal couponDiscount = cart.getEffectivePrice().subtract(couponCode.getCoupon().calculatePrice(cart.getQuantity(), cart.getEffectivePrice()));
						couponDiscount = couponDiscount.compareTo(BigDecimal.ZERO) > 0 ? couponDiscount : BigDecimal.ZERO;
						order.setCouponDiscount(couponDiscount);
						order.setCouponCode(couponCode);
					}
				}
			}
		}
		List<Trade> trades = order.getTrades();
		for (CartItem cartItem : cart.getCartItems()) {
			if (cartItem != null && cartItem.getProduct() != null) {
				Product product = cartItem.getProduct();
				OrderItem orderItem = new OrderItem();
				Trade trade = null;
				Tenant tenant = null;
				if (receiver != null) {
					tenant = tradeStrategy.distribution(cart.getMember(), receiver.getArea(), product, cartItem.calculateQuantityIntValue(), product.getTenant());
				} else {
					tenant = product.getTenant();
				}
				trade = order.getTrade(tenant);
				if (trade == null) {
					trade = EntitySupport.createInitTrade();
					trade.setTenant(tenant);
					int challege = SpringUtils.getIdentifyingCode();
					String securityCode = String.valueOf(challege);
					SafeKey safeKey = new SafeKey();
					safeKey.setValue(securityCode);
					trade.setSafeKey(safeKey);
					trade.setOrder(order);
					trades.add(trade);
				}

				List<OrderItem> tradeItems = trade.getOrderItems();
				List<OrderItem> orderItems = order.getOrderItems();
				orderItem.setTrade(trade);
				orderItem.setSn(product.getSn());
				orderItem.setName(product.getName());
				orderItem.setFullName(product.getFullName());
				orderItem.setThumbnail(product.getThumbnail());
				orderItem.setIsGift(false);
				orderItem.setPrice(cartItem.getPrice());
				orderItem.setWeight(cartItem.getWeight());
				orderItem.setQuantity(cartItem.getQuantity());
				orderItem.setCalculatePackagUnit(product, cartItem.getPackagUnit());
				orderItem.setShippedQuantity(0);
				orderItem.setReturnQuantity(0);
				orderItem.setProduct(product);
				orderItem.setTrade(trade);
				orderItem.setOrder(order);
				tradeItems.add(orderItem);
				orderItems.add(orderItem);
			}
		}

		for (GiftItem giftItem : cart.getGiftItems()) {
			if (giftItem != null && giftItem.getGift() != null) {
				Product gift = giftItem.getGift();
				OrderItem orderItem = new OrderItem();
				Trade trade = null;
				Tenant tenant = null;
				if (receiver != null) {
					tenant = tradeStrategy.distribution(cart.getMember(), receiver.getArea(), gift, giftItem.getQuantity(), gift.getTenant());
				} else {
					tenant = gift.getTenant();
				}
				trade = order.getTrade(tenant);
				if (trade == null) {
					trade = EntitySupport.createInitTrade();
					int challege = SpringUtils.getIdentifyingCode();
					String securityCode = String.valueOf(challege);
					SafeKey safeKey = new SafeKey();
					safeKey.setValue(securityCode);
					trade.setSafeKey(safeKey);
					trade.setOrder(order);
					trades.add(trade);
				}
				List<OrderItem> tradeItems = trade.getOrderItems();
				List<OrderItem> orderItems = order.getOrderItems();
				orderItem.setSn(gift.getSn());
				orderItem.setName(gift.getName());
				orderItem.setFullName(gift.getFullName());
				orderItem.setPrice(BigDecimal.ZERO);
				orderItem.setWeight(gift.getWeight());
				orderItem.setThumbnail(gift.getThumbnail());
				orderItem.setIsGift(true);
				orderItem.setQuantity(giftItem.getQuantity());
				orderItem.setShippedQuantity(0);
				orderItem.setReturnQuantity(0);
				orderItem.setProduct(gift);
				orderItem.setTrade(trade);
				orderItem.setOrder(order);
				tradeItems.add(orderItem);
				orderItems.add(orderItem);
			}
		}

		Setting setting = SettingUtils.get();
		if (setting.getIsInvoiceEnabled() && isInvoice && StringUtils.isNotEmpty(invoiceTitle)) {
			order.setIsInvoice(true);
			order.setInvoiceTitle(invoiceTitle);
			order.setTax(order.calculateTax());
		} else {
			order.setIsInvoice(false);
			order.setTax(BigDecimal.ZERO);
		}

		if (useBalance) {
			Member member = order.getMember();
			if (member.getBalance().compareTo(order.getAmount()) >= 0) {
				order.setAmountPaid(order.getAmount());
			} else {
				order.setAmountPaid(member.getBalance());
			}
		} else {
			order.setAmountPaid(BigDecimal.ZERO);
		}
		return order;
	}

	@Transactional
	public Order create(Cart cart, Receiver receiver, PaymentMethod paymentMethod, ShippingMethod shippingMethod, CouponCode couponCode, boolean isInvoice, String invoiceTitle, boolean useBalance, String memo, Admin operator, Appointment appointment) {
		try {
			Assert.notNull(cart);
			Assert.notNull(cart.getMember());
			Assert.notEmpty(cart.getCartItems());
			Assert.notNull(receiver);
			Assert.notNull(paymentMethod);
			Assert.notNull(shippingMethod);
			Order order = build(cart, receiver, paymentMethod, shippingMethod, couponCode, isInvoice, invoiceTitle, useBalance, memo);
			// 订单状态
			if (order.getAmountPayable().compareTo(BigDecimal.ZERO) == 0) {
				order.setOrderStatus(OrderStatus.confirmed);
				order.setPaymentStatus(PaymentStatus.paid);
			} else if (order.getAmountPayable().compareTo(BigDecimal.ZERO) > 0 && order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
				order.setOrderStatus(OrderStatus.confirmed);
				order.setPaymentStatus(PaymentStatus.partialPayment);
			} else {
				order.setOrderStatus(OrderStatus.unconfirmed);
				order.setPaymentStatus(PaymentStatus.unpaid);
			}
			if (paymentMethod != null && paymentMethod.getTimeout() != null && order.getPaymentStatus() == PaymentStatus.unpaid) {
				int releaseStockDays=3;
				try{
					releaseStockDays = Integer.valueOf(CacheUtil.getParamValueByName("RELEASE_STOCK_DAYS"));
				}catch(Exception e){
					log.error("获取待支付过期时间失败,使用默认3天");
				}
				order.setExpire(DateUtils.addMinutes(new Date(), paymentMethod.getTimeout()*releaseStockDays));
			}
			if (paymentMethod.getMethod() == PaymentMethod.Method.online) {
				order.setLockExpire(DateUtils.addSeconds(new Date(), 20));
				order.setOperator(operator);
			}
			if (order.getCouponCode() != null) {
				couponCode.setIsUsed(true);
				couponCode.setUsedDate(new Date());
			}
			for (Promotion promotion : cart.getPromotions()) {
				for (Coupon coupon : promotion.getCoupons()) {
					order.getCoupons().add(coupon);
				}
			}
			if (order.getTrades().size() > 1) {
				order.setOrderType(OrderType.composite);
			} else {
				order.setOrderType(OrderType.single);
			}
			if (appointment == null) {
				appointment = appointmentService.findDefault();
			}
			order.setAppointment(appointment);
			order.setSn(snDao.generate(Sn.Type.order));
			for (Trade trade : order.getTrades()) {
				trade.setSn(RandomUtil.encryptRandom(Long.parseLong(snDao.generate(Sn.Type.trade))));
			}

			if (order.getCouponCode() != null && couponCode != null) {
				couponCodeDao.merge(couponCode);
			}

			Setting setting = SettingUtils.get();
			if (setting.getStockAllocationTime() == StockAllocationTime.order
					|| (setting.getStockAllocationTime() == StockAllocationTime.payment && (order.getPaymentStatus() == PaymentStatus.partialPayment || order.getPaymentStatus() == PaymentStatus.paid))) {
				order.setIsAllocatedStock(true);
				stockStrategy.lockAllocatedOrder(order);
			} else {
				order.setIsAllocatedStock(false);
			}
			orderDao.persist(order);
			if (useBalance) {
				Payment payment = new Payment();
				payment.setSn(snService.generate(Sn.Type.payment));
				payment.setType(Payment.Type.recharge);
				payment.setMethod(Method.online);
				if (order.getAmountPayable().compareTo(BigDecimal.ZERO) == 0) {
					payment.setStatus(Status.success);
				} else {
					payment.setStatus(Status.wait);
				}
				payment.setPaymentMethod(order.getPaymentMethodName());
				payment.setFee(new BigDecimal(0));
				payment.setAmount(order.getAmountPaid());
				payment.setPaymentDate(new Date());
				payment.setPaymentPluginId(null);
				payment.setExpire(null);
				payment.setMember(null);
				payment(order, payment, null);
			}
			Member member = order.getMember();
			if (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
				memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);
				member.setBalance(member.getBalance().subtract(order.getAmountPaid()));
				memberDao.merge(member);

				Deposit deposit = new Deposit();
				deposit.setType(Deposit.Type.Payment);
				deposit.setCredit(BigDecimal.ZERO);
				deposit.setDebit(order.getAmountPaid());
				deposit.setBalance(member.getBalance());
				deposit.setOperator(operator != null ? operator.getUsername() : null);
				deposit.setMember(member);
				deposit.setOrder(order);
				depositDao.persist(deposit);
				distributionStrategy.distribution(order, operator);
			}
			orderDao.persist(order);

			OrderLog orderLog = new OrderLog();
			orderLog.setType(Type.create);
			orderLog.setOperator(operator != null ? operator.getUsername() : null);
			orderLog.setOrder(order);
			orderLogDao.persist(orderLog);
			cartDao.remove(cart);
			tradeStrategy.smsOrder(order);

			return order;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Transactional
	public Order createPointExchangeOrder(Cart cart, Receiver receiver, PaymentMethod paymentMethod, ShippingMethod shippingMethod, Long point, boolean isInvoice, String invoiceTitle, boolean useBalance, String memo, Admin operator,
			Appointment appointment) {
		if (point == null || point < 0) {
			return create(cart, receiver, paymentMethod, shippingMethod, null, isInvoice, invoiceTitle, useBalance, memo, operator, appointment);
		}
		CouponCode couponCode = null;
		Setting setting = SettingUtils.get();
		Double defaultPointScale = setting.getDefaultPointScale();
		if (defaultPointScale > 0) {
			Coupon coupon = couponDao.findSystemPointExchange();
			couponCode = new CouponCode();
			String uuid = UUID.randomUUID().toString().toUpperCase();
			couponCode.setCode(coupon.getPrefix() + uuid.substring(0, 8) + uuid.substring(9, 13) + uuid.substring(14, 18) + uuid.substring(19, 23) + uuid.substring(24));
			couponCode.setPoint(point);
			couponCode.setIsUsed(false);
			couponCode.setCoupon(coupon);
			couponCode.setMember(cart.getMember());
			couponCodeDao.persist(couponCode);
		}
		return create(cart, receiver, paymentMethod, shippingMethod, couponCode, isInvoice, invoiceTitle, useBalance, memo, operator, appointment);
	}

	@Transactional
	public void update(Order order, String operator) {
		Assert.notNull(order);
		Order prefixOrder = orderDao.find(order.getId());
		if (prefixOrder.getIsAllocatedStock()) {
			order.setIsAllocatedStock(true);
			stockStrategy.adjustForOrderUpdate(prefixOrder, order);
		}
		orderDao.merge(order);
		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.modify);
		orderLog.setOperator(operator != null ? operator : null);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);
	}

	public void confirm(Order order, Admin operator) {
		Assert.notNull(order);
		order.setOrderStatus(OrderStatus.confirmed);
		if (PaymentMethod.Method.offline.equals(order.getPaymentMethod())) {
		}
		orderDao.merge(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.confirm);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);
	}

	public void cancel(Order order, Admin operator) {
		Assert.notNull(order);
		if (order.getIsAllocatedStock()) {
			stockStrategy.releaseAllocatedOrder(order);
			order.setIsAllocatedStock(false);
		}
		order.setOrderStatus(OrderStatus.cancelled);
		order.setExpire(null);
		orderDao.merge(order);

		OrderSettlement orderSettlement = order.getOrderSettlement();
		orderSettlement.setReturnCharge(order.getChargeAmt());
		orderSettlement.setOrderReturnAmt(order.getAmountPaid());
		orderSettlement.setStatus(SettlementStatus.cancel);
		orderSettlementDao.merge(orderSettlement);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.cancel);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);
	}

	@Transactional
	public void payment(Order order, Payment payment, Admin operator) {
		Assert.notNull(order);
		Assert.notNull(payment);
		orderDao.lock(order, LockModeType.PESSIMISTIC_WRITE);
		payment.setOrder(order);
		paymentDao.merge(payment);

		if (order.getOrderSettlement() == null) {
			OrderSettlement orderSettlement = new OrderSettlement();
			orderSettlement.setOrder(order);
			orderSettlement.setMember(order.getMember());
			orderSettlement.setOwner(order.getOwner());
			orderSettlement.setOrderAmount(order.getAmountPaid());
			orderSettlement.setOrderReturnAmt(new BigDecimal(0));
			orderSettlement.setOrderCharge(order.getChargeAmt());
			orderSettlement.setReturnCharge(new BigDecimal(0));
			orderSettlementDao.persist(orderSettlement);
		} else {
			OrderSettlement orderSettlement = order.getOrderSettlement();
			orderSettlement.setOrder(order);
			orderSettlement.setMember(order.getMember());
			orderSettlement.setOwner(order.getOwner());
			orderSettlement.setOrderAmount(order.getAmountPaid());
			orderSettlement.setOrderReturnAmt(new BigDecimal(0));
			orderSettlement.setOrderCharge(order.getChargeAmt());
			orderSettlement.setReturnCharge(new BigDecimal(0));
			orderSettlementDao.merge(orderSettlement);
		}

		Member member = order.getMember();
		if (payment.getMethod() == Payment.Method.deposit) {
			memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);
			member.setBalance(member.getBalance().subtract(payment.getAmount()));
			memberDao.merge(member);

			Deposit deposit = new Deposit();
			deposit.setType(Deposit.Type.Payment);
			deposit.setCredit(BigDecimal.ZERO);
			deposit.setDebit(payment.getAmount());
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername() : null);
			deposit.setMember(member);
			deposit.setOrder(order);
			depositDao.persist(deposit);
		} else {
			// Member member = order.getMember();
			// memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);
			// member.setBalance(member.getBalance().add(payment.getAmount()));
			// memberDao.merge(member);
			// Deposit deposit1 = new Deposit();
			// deposit1.setType(Deposit.Type.Recharge);
			// deposit1.setCredit(payment.getAmount());
			// deposit1.setDebit(BigDecimal.ZERO);
			// deposit1.setBalance(member.getBalance());
			// deposit1.setOperator(operator != null ? operator.getUsername() : null);
			// deposit1.setMember(member);
			// deposit1.setOrder(order);
			// deposit1.setMemo("订单支付-充值");
			// depositDao.persist(deposit1);
			// member.setBalance(member.getBalance().subtract(payment.getAmount()));
			// memberDao.merge(member);
			// Deposit deposit2 = new Deposit();
			// deposit2.setType(Deposit.Type.Payment);
			// deposit2.setCredit(BigDecimal.ZERO);
			// deposit2.setDebit(payment.getAmount());
			// deposit2.setBalance(member.getBalance());
			// deposit2.setOperator(operator != null ? operator.getUsername() : null);
			// deposit2.setMember(member);
			// deposit2.setMemo("订单支付-付款");
			// deposit2.setOrder(order);
			// depositDao.persist(deposit2);
		}
		Setting setting = SettingUtils.get();
		if (!order.getIsAllocatedStock() && setting.getStockAllocationTime() == StockAllocationTime.payment) {
			order.setIsAllocatedStock(true);
			stockStrategy.lockAllocatedOrder(order);
		}

		// order.setAmountPaid(order.getAmountPaid().add(payment.getAmount()));
		order.setFee(payment.getFee());
		order.setExpire(null);
		order.setLockExpire(null);
		order.setPaymentStatus(PaymentStatus.paid);
		// if (order.getAmountPaid().compareTo(order.getAmount()) >= 0) {
		// order.setOrderStatus(OrderStatus.confirmed);
		// order.setPaymentStatus(PaymentStatus.paid);
		// authenticodeStrategy.createAuthentiCode(order);
		// authenticodeStrategy.sendNotify(order);
		// distributionStrategy.distribution(order, operator);
		// } else if (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
		// order.setOrderStatus(OrderStatus.confirmed);
		// }
		orderDao.merge(order);

		messageDetailsService.pushMessage(MessageType.order, true, order.getTenant(), member.getMobile() + "支付订单:" + order.getSn(), order, false);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.payment);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);
		orderLog.setContent("支付成功");
		orderLogDao.persist(orderLog);

	}

	public void paymentByDeposit(Order order, Admin operator) {
		Payment payment = new Payment();
		payment.setSn(snService.generate(Sn.Type.payment));
		payment.setMethod(Payment.Method.deposit);
		payment.setType(Payment.Type.payment);
		payment.setStatus(Status.success);
		payment.setMember(order.getMember());
		payment.setFee(BigDecimal.ZERO);
		payment.setPaymentMethod("帐户支付");
		payment.setAmount(order.getAmountPayable());
		payment.setPayer(order.getMember().getUsername());
		payment.setOperator(operator != null ? operator.getUsername() : null);
		payment.setPaymentDate(new Date());
		payment.setPaymentPluginId(null);
		payment.setExpire(null);
		payment.setDeposit(null);
		payment(order, payment, operator);
	}

	@Transactional
	public void autoShipping(Member member, Order order, OrderItem orderItem, Stock stock, Integer quantity) {
		Product product = stock.getProduct();
		// 锁库存
		stock.setAllocatedStock(stock.getAllocatedStock().add(new BigDecimal(quantity)));
		stockDao.merge(stock);
		// 自动发货
		Shipping shipping = new Shipping();
		shipping.setSn(snService.generate(Sn.Type.shipping));
		shipping.setDeliveryCorp("自提");
		shipping.setShippingMethod(order.getShippingMethodName());
		shipping.setFreight(order.getFreight());
		shipping.setConsignee(order.getConsignee());
		shipping.setArea(order.getArea().getName());
		shipping.setAddress(order.getAddress());
		shipping.setZipCode(order.getZipCode());
		shipping.setPhone(order.getPhone());
		shipping.setOrder(order);
		shipping.setTrade(order.getTrade(member.getTenant()));
		shipping.setDeliveryCenter(stock.getDeliveryCenter());
		shipping.setOperator(member.getTenant() == null ? member.getUsername() : member.getTenant().getName());
		shipping.setShippingMethod(order.getShippingMethod().getName());

		ShippingItem shippingItem = new ShippingItem();
		shippingItem.setSn(product.getSn());
		shippingItem.setName(product.getName());
		shippingItem.setQuantity(quantity);
		shippingItem.setShipping(shipping);
		shipping.getShippingItems().add(shippingItem);

		shipping.getTrade().setShippingStatus(ShippingStatus.shipped);
		order.setShippingStatus(ShippingStatus.shipped);
		order.setIsAllocatedStock(false);

		order.setAppointment(appointmentService.findDefault());
		order.setOrderItems(new ArrayList<OrderItem>());
		order.setOperator(null);
		orderDao.persist(order);
		shippingDao.persist(shipping);

		stockStrategy.subtractForShipping(order, orderItem, shippingItem, stock.getDeliveryCenter());
	}

	@Transactional
	public void shippingByMember(Order order, Shipping shipping, Member operator) {
		Assert.notNull(order);
		Assert.notNull(shipping);
		Assert.notEmpty(shipping.getShippingItems());
		orderDao.lock(order, LockModeType.PESSIMISTIC_WRITE);

		Setting setting = SettingUtils.get();
		if (!order.getIsAllocatedStock() && setting.getStockAllocationTime() == StockAllocationTime.ship) {
			stockStrategy.lockAllocatedOrder(order);
			order.setIsAllocatedStock(true);
		}

		shipping.setOrder(order);
		shippingDao.persist(shipping);
		for (ShippingItem shippingItem : shipping.getShippingItems()) {
			OrderItem orderItem = order.getOrderItem(shippingItem.getSn());
			if (orderItem == null) {
				continue;
			}
			stockStrategy.adjustForShipping(order, orderItem, shippingItem, shipping.getDeliveryCenter());
			orderItemDao.lock(orderItem, LockModeType.PESSIMISTIC_WRITE);
			orderItem.setShippedQuantity(orderItem.getShippedQuantity() + shippingItem.getQuantity());
			if (orderItem.getShippedQuantity() >= orderItem.getQuantity()) {
				for (Authenticode auth : orderItem.getAuthenticodes()) {
					auth.setStatus(Authenticode.Status.shipped);
				}
			}
		}
		if (order.getShippedQuantity() >= order.getQuantity()) {
			order.setShippingStatus(ShippingStatus.shipped);
			order.setIsAllocatedStock(false);
		} else if (order.getShippedQuantity() > 0) {
			order.setShippingStatus(ShippingStatus.partialShipment);
		}
		Trade trade = tradeDao.find(shipping.getTrade().getId());
		if (trade.getShippedQuantity() >= trade.getQuantity()) {
			trade.setShippingStatus(ShippingStatus.shipped);
		} else if (trade.getShippedQuantity() > 0) {
			trade.setShippingStatus(ShippingStatus.partialShipment);
		}

		order.setExpire(null);
		orderDao.merge(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.shipping);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);
	}

	@Transactional
	public void shipping(Order order, Shipping shipping, Admin operator) {
		Assert.notNull(order);
		Assert.notNull(shipping);
		Assert.notEmpty(shipping.getShippingItems());
		orderDao.lock(order, LockModeType.PESSIMISTIC_WRITE);

		Setting setting = SettingUtils.get();
		if ((order.getIsAllocatedStock() == null || !order.getIsAllocatedStock()) && setting.getStockAllocationTime() == StockAllocationTime.ship) {
			stockStrategy.lockAllocatedOrder(order);
			order.setIsAllocatedStock(true);
		}

		shipping.setOrder(order);
		shippingDao.persist(shipping);
		for (ShippingItem shippingItem : shipping.getShippingItems()) {
			OrderItem orderItem = order.getOrderItem(shippingItem.getSn());
			if (orderItem == null) {
				continue;
			}
			stockStrategy.adjustForShipping(order, orderItem, shippingItem, shipping.getDeliveryCenter());
			orderItemDao.lock(orderItem, LockModeType.PESSIMISTIC_WRITE);
			orderItem.setShippedQuantity(orderItem.getShippedQuantity() + shippingItem.getQuantity());
			if (orderItem.getShippedQuantity() >= orderItem.getQuantity()) {
				for (Authenticode auth : orderItem.getAuthenticodes()) {
					auth.setStatus(Authenticode.Status.shipped);
				}
			}
		}
		if (order.getShippedQuantity() >= order.getQuantity()) {
			order.setShippingStatus(ShippingStatus.shipped);
			order.setIsAllocatedStock(false);
		} else if (order.getShippedQuantity() > 0) {
			order.setShippingStatus(ShippingStatus.partialShipment);
		}

		order.setExpire(null);
		orderDao.merge(order);

		OrderSettlement orderSettlement = order.getOrderSettlement();
		orderSettlement.setPlanFinishDate(DateUtil.transpositionDate(new Date(), Calendar.DAY_OF_YEAR,
				Integer.valueOf(net.wit.mobile.cache.CacheUtil.getParamValueByName("AUTO_RECEIPT_DATE")) + Integer.valueOf(net.wit.mobile.cache.CacheUtil.getParamValueByName("FORCE_RETURN_DATE"))));
		orderSettlementDao.merge(orderSettlement);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.shipping);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);
		orderLog.setContent("订单号:" + order.getSn() + "已发货,操作者:" + (operator != null ? operator.getUsername() : null));
		orderLogDao.persist(orderLog);

	}

	@Transactional
	public void refunds(Order order, Refunds refunds, Admin operator) {
		Assert.notNull(order);
		Assert.notNull(refunds);
		orderDao.lock(order, LockModeType.PESSIMISTIC_WRITE);
		refunds.setOrder(order);
		refundsDao.persist(refunds);
		if (refunds.getMethod() == Refunds.Method.deposit) {
			Member member = order.getMember();
			memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);
			member.setBalance(member.getBalance().add(refunds.getAmount()));
			memberDao.merge(member);

			Deposit deposit = new Deposit();
			deposit.setType(Deposit.Type.Refunds);
			deposit.setCredit(refunds.getAmount());
			deposit.setDebit(BigDecimal.ZERO);
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername() : null);
			deposit.setMember(member);
			deposit.setOrder(order);
			depositDao.persist(deposit);
		}

		order.setAmountPaid(order.getAmountPaid().subtract(refunds.getAmount()));
		order.setExpire(null);
		if (order.getAmountPaid().compareTo(BigDecimal.ZERO) == 0) {
			order.setPaymentStatus(PaymentStatus.refunded);
		} else if (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
			order.setPaymentStatus(PaymentStatus.partialRefunds);
		}
		orderDao.merge(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.refunds);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);
	}

	public void returns(Order order, Returns returns, Admin operator) {
		Assert.notNull(order);
		Assert.notNull(returns);
		Assert.notEmpty(returns.getReturnsItems());
		orderDao.lock(order, LockModeType.PESSIMISTIC_WRITE);
		returns.setOrder(order);
		returnsDao.persist(returns);
		for (ReturnsItem returnsItem : returns.getReturnsItems()) {
			OrderItem orderItem = order.getOrderItem(returnsItem.getSn());
			if (orderItem != null) {
				orderItemDao.lock(orderItem, LockModeType.PESSIMISTIC_WRITE);
				orderItem.setReturnQuantity(orderItem.getReturnQuantity() + returnsItem.getQuantity());
			}
		}
		if (order.getReturnQuantity() >= order.getShippedQuantity()) {
			order.setShippingStatus(ShippingStatus.returned);
		} else if (order.getReturnQuantity() > 0) {
			order.setShippingStatus(ShippingStatus.partialReturns);
		}
		Trade trade = tradeDao.find(returns.getTrade().getId());
		if (trade.getReturnQuantity() >= trade.getShippedQuantity()) {
			trade.setShippingStatus(ShippingStatus.returned);
		} else if (trade.getReturnQuantity() > 0) {
			trade.setShippingStatus(ShippingStatus.partialReturns);
		}
		order.setExpire(null);
		orderDao.merge(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.returns);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);
	}
	
	@Transactional
	public void complete(Order order, Admin operator) throws BizException {
		Assert.notNull(order);
		Member member = order.getMember();
		memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);
		for (Refunds refund : order.getRefunds()) {
			refundsDao.persist(refund);
			if (refund.getMethod() == Refunds.Method.deposit) {
				memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);
				member.setBalance(member.getBalance().add(refund.getAmount()));
				memberDao.merge(member);

				Deposit deposit = new Deposit();
				deposit.setType(Deposit.Type.Refunds);
				deposit.setCredit(refund.getAmount());
				deposit.setDebit(BigDecimal.ZERO);
				deposit.setBalance(member.getBalance());
				deposit.setOperator(operator != null ? operator.getUsername() : null);
				deposit.setMember(member);
				deposit.setOrder(order);
				depositDao.persist(deposit);
			}
			order.setAmountPaid(order.getAmountPaid().subtract(refund.getAmount()));
		}
		if (!order.getRefunds().isEmpty()) {
			if (order.getAmountPaid().compareTo(BigDecimal.ZERO) == 0) {
				order.setPaymentStatus(PaymentStatus.refunded);
			} else if (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
				order.setPaymentStatus(PaymentStatus.partialRefunds);
			}
		}

		if (PaymentStatus.paid == order.getPaymentStatus() || PaymentStatus.partialPayment == order.getPaymentStatus() || PaymentStatus.partialRefunds == order.getPaymentStatus()) {
			// member.setPoint(member.getPoint() + order.getPoint());
			for (Coupon coupon : order.getCoupons()) {
				couponCodeDao.build(coupon, member);
			}
		}
		if (PaymentStatus.unpaid == order.getPaymentStatus() || PaymentStatus.refunded == order.getPaymentStatus()) {
			CouponCode couponCode = order.getCouponCode();
			if (couponCode != null) {
				if (Coupon.Type.point == couponCode.getCoupon().getType()) { // 积分兑换券
					member = order.getMember();
					member.setPoint(member.getPoint() + couponCode.getPoint());
					memberDao.persist(member);
				} else {
					couponCode.setIsUsed(false);
					couponCode.setUsedDate(null);
					couponCodeDao.merge(couponCode);
				}
				order.setCouponCode(null);
				orderDao.merge(order);
			}
		}

		member.setAmount(member.getAmount().add(order.getAmountPaid()));
		member.setPrivilege(member.getPrivilege() + order.getAmountPaid().multiply(SettingConstant.amountPrivilegeScale).intValue());
		/*
		 * if (!member.getMemberRank().getIsSpecial()) { MemberRank memberRank = memberRankDao.findByAmount(member.getAmount()); if (memberRank != null &&
		 * memberRank.getAmount().compareTo(member.getMemberRank().getAmount()) > 0) { member.setMemberRank(memberRank); } }
		 */
		memberDao.merge(member);

		if (order.getIsAllocatedStock()) {
			stockStrategy.releaseAllocatedOrder(order);
			order.setIsAllocatedStock(false);
		}
		for (OrderItem orderItem : order.getOrderItems()) {
			if (orderItem != null) {
				Product product = orderItem.getProduct();
				productDao.lock(product, LockModeType.PESSIMISTIC_WRITE);
				if (product != null) {
					Integer quantity = orderItem.calculateQuantityIntValue();
					Calendar nowCalendar = Calendar.getInstance();
					Calendar weekSalesCalendar = DateUtils.toCalendar(product.getWeekSalesDate());
					Calendar monthSalesCalendar = DateUtils.toCalendar(product.getMonthSalesDate());
					if (nowCalendar.get(Calendar.YEAR) != weekSalesCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.WEEK_OF_YEAR) > weekSalesCalendar.get(Calendar.WEEK_OF_YEAR)) {
						product.setWeekSales((long) quantity);
					} else {
						product.setWeekSales(product.getWeekSales() + quantity);
					}
					if (nowCalendar.get(Calendar.YEAR) != monthSalesCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.MONTH) > monthSalesCalendar.get(Calendar.MONTH)) {
						product.setMonthSales((long) quantity);
					} else {
						product.setMonthSales(product.getMonthSales() + quantity);
					}
					product.setSales(product.getSales() + quantity);
					product.setWeekSalesDate(new Date());
					product.setMonthSalesDate(new Date());
					productDao.merge(product);
					orderDao.flush();
					staticService.build(product);
				}
			}
		}

		order.setOrderStatus(OrderStatus.completed);
		order.setShippingStatus(ShippingStatus.accept);
		order.setExpire(null);
		orderDao.merge(order);
		
		OrderSettlement os = order.getOrderSettlement();
		if(os  == null ){
			log.error("订单结算异常:============="+order.getId());
			throw new BizException("订单完成失败");
		}
		os.setStatus(SettlementStatus.complete);
		os.setFinishDate(new Date());
		orderSettlementDao.merge(os);

		// 商家帐户打款
		for (Trade trade : order.getTrades()) {
			Tenant tenant = trade.getTenant();
			BigDecimal realHairAmount = trade.getPrice().subtract(trade.getRefundAmount());
			BigDecimal tenantBrokerage = (tenant.getBrokerage() == null || tenant.getBrokerage().compareTo(BigDecimal.ZERO) <= 0) ? BigDecimal.ZERO : tenant.getBrokerage();
			if (realHairAmount.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			BigDecimal memberRebate = BigDecimal.ZERO;
			if (order.getPromotionScheme() != null) {
				Promotion promotion = order.getPromotionScheme();
				if (Promotion.Type.groupon == promotion.getType() && Promotion.Classify.puzzle == promotion.getClassify()) { // 拼拼团
					Member schemeMember = promotion.getMember();
					BigDecimal memberBrokerage = promotion.getBrokerage();
					memberRebate = realHairAmount.multiply(memberBrokerage).divide(new BigDecimal(100));

					memberDao.lock(schemeMember, LockModeType.PESSIMISTIC_WRITE);
					schemeMember.setBalance(schemeMember.getBalance().add(memberRebate));
					memberDao.merge(schemeMember);

					Deposit deposit = new Deposit();
					deposit.setType(Deposit.Type.Credit);
					deposit.setCredit(memberRebate);
					deposit.setDebit(BigDecimal.ZERO);
					deposit.setBalance(schemeMember.getBalance());
					deposit.setOperator(operator != null ? operator.getUsername() : null);
					deposit.setMember(schemeMember);
					deposit.setOrder(order);
					depositDao.persist(deposit);
				}
			}

			realHairAmount = realHairAmount.subtract(realHairAmount.multiply(tenantBrokerage)).subtract(memberRebate);
			Member tenantMember = tenant.getMember();
			memberDao.lock(tenantMember, LockModeType.PESSIMISTIC_WRITE);
			tenantMember.setBalance(member.getBalance().add(realHairAmount));
			memberDao.merge(tenantMember);

			Deposit deposit = new Deposit();
			deposit.setType(Deposit.Type.Credit);
			deposit.setCredit(realHairAmount);
			deposit.setDebit(BigDecimal.ZERO);
			deposit.setBalance(tenantMember.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername() : null);
			deposit.setMember(tenantMember);
			deposit.setOrder(order);
			depositDao.persist(deposit);
		}

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.complete);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);
		//发送订单结束消息
		pushService.publishSystemMessage(order.getTenant(), member, SystemMessage.buyerOrderEndMsg(order.getTenant().getShortName(), order.getSn()));
	}

	// 签收
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void sign(Order order) {
		order.setShippingStatus(ShippingStatus.accept);
		Member member = order.getMember();

		OrderSettlement settlement = order.getOrderSettlement();
		settlement.setPlanFinishDate(DateUtil.transpositionDate(new Date(), Calendar.DAY_OF_YEAR, Integer.valueOf(net.wit.mobile.cache.CacheUtil.getParamValueByName("FORCE_RETURN_DATE"))));
		settlement.setRemark(order.getConsignee() + "已签收");
		orderSettlementDao.merge(settlement);

		orderDao.merge(order);
		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.complete);
		orderLog.setOperator(member.getUsername());
		orderLog.setContent(member.getUsername() + "签收订单号:" + order.getSn());
		orderLog.setOrder(order);
		orderLog.setOrderStatus(OrderStatus.completed);
		orderLogDao.persist(orderLog);
	}

	public void extendAccept(Order order) {
		order.setIsExtend(true);
		Member member = order.getMember();

		orderDao.merge(order);
		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.extend);
		orderLog.setOperator(member.getUsername());
		orderLog.setContent(member.getUsername() + "延长收货订单号:" + order.getSn());
		orderLog.setOrder(order);
		orderLog.setOrderStatus(OrderStatus.confirmed);
		orderLogDao.persist(orderLog);
	}

	public void delete(Order order) {
		if (order.getIsAllocatedStock()) {
			stockStrategy.releaseAllocatedOrder(order);
		}
		super.delete(order);
	}

	@Transactional(readOnly = true)
	public Order findBySn(String sn) {
		return orderDao.findBySn(sn);
	}

	@Transactional(readOnly = true)
	public List<Order> findList(Member member, Integer count, List<Filter> filters, List<net.wit.Order> orders) {
		return orderDao.findList(member, count, filters, orders);
	}

	@Transactional(readOnly = true)
	public List<Order> findList(Tenant tenant, Date startDate, Date endDate, List<OrderStatus> orderStatuses, List<ShippingStatus> shippingStatuses) {
		return orderDao.findList(tenant, startDate, endDate, orderStatuses, shippingStatuses);
	}

	@Transactional(readOnly = true)
	public Page<Order> findPage(Member member, Pageable pageable) {
		return orderDao.findPage(member, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Order> findPage(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable) {
		return orderDao.findPage(member, orderStatus, paymentStatus, shippingStatus, hasExpired, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Order> findPage(OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable) {
		return orderDao.findPage(orderStatus, paymentStatus, shippingStatus, hasExpired, pageable);
	}

	@Transactional(readOnly = true)
	public Long count(OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired) {
		return orderDao.count(orderStatus, paymentStatus, shippingStatus, hasExpired);
	}

	@Transactional(readOnly = true)
	public Long waitingPaymentCount(Tenant tenant, Member member) {
		return orderDao.waitingPaymentCount(tenant, member);
	}

	@Transactional(readOnly = true)
	public Long waitingShippingCount(Tenant tenant, Member member) {
		return orderDao.waitingShippingCount(tenant, member);
	}

	@Transactional(readOnly = true)
	public BigDecimal getSalesAmount(Date beginDate, Date endDate) {
		return orderDao.getSalesAmount(beginDate, endDate);
	}

	@Transactional(readOnly = true)
	public Integer getSalesVolume(Date beginDate, Date endDate) {
		return orderDao.getSalesVolume(beginDate, endDate);
	}

	@Transactional(readOnly = true)
	public Page<Order> findPage(Member member, Date beginDate, Date endDate, Pageable pageable) {
		return orderDao.findPage(member, beginDate, endDate, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Order> findPageTenant(Member member, Date beginDate, Date endDate, Pageable pageable) {
		return orderDao.findPageTenant(member, beginDate, endDate, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Order> findPageMember(Member member, Date beginDate, Date endDate, Pageable pageable) {
		return orderDao.findPageMember(member, beginDate, endDate, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Order> findPageAll(Member member, Date beginDate, Date endDate, Pageable pageable) {
		return orderDao.findPageAll(member, beginDate, endDate, pageable);
	}

	@Transactional(readOnly = true)
	public Long countAll(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired) {
		return orderDao.countAll(member, orderStatus, paymentStatus, shippingStatus, hasExpired);
	}

	@Transactional(readOnly = true)
	public long countmy(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired) {
		return orderDao.countmy(member, orderStatus, paymentStatus, shippingStatus, hasExpired);
	}

	@Transactional
	public void offlineShipping(Order order, Trade trade) {
		Setting setting = SettingUtils.get();
		if (!order.getIsAllocatedStock() && setting.getStockAllocationTime() == StockAllocationTime.ship) {
			stockStrategy.lockAllocatedOrder(order);
			order.setIsAllocatedStock(true);
		}
		stockStrategy.adjustForShipping(trade);
		if (OrderStatus.completed == order.getOrderStatus()) {
			if (order.getIsAllocatedStock()) {
				stockStrategy.releaseAllocatedOrder(order);
				order.setIsAllocatedStock(false);
			}
		}
	}

	/** 查找会员代付款订单 */
	public Page<Order> findWaitPay(Member member, Boolean hasExpired, Pageable pageable) {
		return orderDao.findWaitPay(member, hasExpired, pageable);
	}

	/** 查找会员待发货订单 */
	public Page<Order> findWaitShipping(Member member, Boolean hasExpired, Pageable pageable) {
		return orderDao.findWaitShipping(member, hasExpired, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Order> findPageWithoutStatus(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Pageable pageable) {
		return orderDao.findPageWithoutStatus(member, orderStatus, paymentStatus, shippingStatus, pageable);
	}

	@Transactional
	public Order createImmediately(BuyVo[] buys, Member member, Member owner, Ticket ticket, Receiver receiver, PaymentMethod paymentMethod, ShippingMethod shippingMethod) throws OrderException {
		Assert.notNull(buys);
		Assert.notNull(member);
		Assert.notNull(owner);
		Assert.notNull(ticket);

		Order order = new Order();
		order.setOrderStatus(OrderStatus.confirmed);
		order.setPaymentStatus(PaymentStatus.unpaid);
		order.setShippingStatus(ShippingStatus.unshipped);
		order.setFee(BigDecimal.ZERO);
		order.setFreight(BigDecimal.ZERO);
		order.setPromotionDiscount(BigDecimal.ZERO);
		order.setCouponDiscount(BigDecimal.ZERO);
		order.setOffsetAmount(BigDecimal.ZERO);
		order.setPoint(0L);

		if (!member.equals(ticket.getMember())) {
			throw new OrderException("非法内购券");
		}
		if (!net.wit.entity.Ticket.Status.recevied.equals(ticket.getStatus()))
			throw new OrderException("内购券无效");
		if ((new Date()).after(DateUtil.addDay(ticket.getExpiryDate(), 1)))
			throw new OrderException("内购券已经失效");
		Tenant tanent = ticket.getTenant();
		if (tanent == null) {
			throw new OrderException("非法内购券");
		}
		order.setTicket(ticket);
		order.setTenant(tanent);
		// 处理订单项
		List<OrderItem> orderItems = order.getOrderItems();
		for (BuyVo buy : buys) {
			Product product = productDao.find(buy.getProductId());
			if (product != null && buy.getQuantity() != null) {
				if (!tanent.equals(product.getTenant())) {
					throw new OrderException("优惠券只能购买【" + tanent.getName() + "】的宝贝!");
				}
				//库存校验
				if(product.getAvailableStock() - buy.getQuantity() < 0){
					throw new OrderException("商品："+product.getName()+"库存不足，无法下单");
				}
				OrderItem orderItem = new OrderItem();
				orderItem.setProduct(product);
				orderItem.setQuantity(buy.getQuantity());
				orderItem.setPrice(product.getPrice());
				orderItem.setFullName(product.getFullName());
				orderItem.setName(product.getName());
				orderItem.setSn(snService.generate(Sn.Type.trade));
				orderItem.setIsGift(false);
				orderItem.setReturnQuantity(0);
				orderItem.setShippedQuantity(0);
				orderItem.setThumbnail(product.getThumbnail());
				if (product.getRent() != null) {
					orderItem.setProductRent(product.getRent());
				} else {
					orderItem.setProductRent(new BigDecimal(0));
				}
				orderItem.setOrder(order);
				orderItems.add(orderItem);
			}
		}

		if (receiver != null) {
			order.setArea(receiver.getArea());
			order.setAreaName(receiver.getAreaName());
			order.setAddress(receiver.getAddress());
			order.setConsignee(receiver.getConsignee());
			order.setPhone(receiver.getPhone());
			order.setZipCode(receiver.getZipCode());
		}
		order.setSn(snService.generate(Sn.Type.order));
		order.setMember(member);
		order.setOwner(owner);
		order.setPaymentMethod(paymentMethod);
		order.setShippingMethod(shippingMethod);
		// 分配库存
		Setting setting = SettingUtils.get();
		if (setting.getStockAllocationTime() == StockAllocationTime.order
				|| (setting.getStockAllocationTime() == StockAllocationTime.payment && (order.getPaymentStatus() == PaymentStatus.partialPayment || order.getPaymentStatus() == PaymentStatus.paid))) {
			order.setIsAllocatedStock(true);
			stockStrategy.lockAllocatedOrder(order);
		} else {
			order.setIsAllocatedStock(false);
		}
		saleStatisticService.adjustForOrder(order);

		ticket.setStatus(net.wit.entity.Ticket.Status.used);
		// ticket.setExpiryDate(null);
		if (paymentMethod != null && paymentMethod.getTimeout() != null && order.getPaymentStatus() == PaymentStatus.unpaid) {
			int releaseStockDays=3;
			try{
				releaseStockDays = Integer.valueOf(CacheUtil.getParamValueByName("RELEASE_STOCK_DAYS"));
			}catch(Exception e){
				log.error("获取待支付过期时间失败,使用默认3天");
			}
			order.setExpire(DateUtils.addMinutes(new Date(), paymentMethod.getTimeout()*releaseStockDays));
		}
		ticketDao.merge(ticket);
		orderDao.persist(order);

		OrderSettlement orderSettlement = new OrderSettlement();
		orderSettlement.setOrder(order);
		orderSettlement.setMember(member);
		orderSettlement.setOwner(owner);
		orderSettlement.setStatus(SettlementStatus.uncomplete);
		orderSettlement.setOrderAmount(order.getAmountPaid());
		orderSettlement.setOrderReturnAmt(new BigDecimal(0));
		orderSettlement.setOrderCharge(order.getChargeAmt());
		orderSettlement.setReturnCharge(new BigDecimal(0));
		orderSettlement.setPlanFinishDate(DateUtil.transpositionDate(new Date(), Calendar.DAY_OF_YEAR, 15));
		orderSettlementDao.persist(orderSettlement);

		OrderLog log = new OrderLog();
		log.setContent(member.getMobile() + SpringUtils.getMessage("OrderLog.Type.create") + "订单sn为" + order.getSn() + ",状态调整为:" + SpringUtils.getMessage("Order.OrderStatus.confirmed"));
		log.setOperator(adminService.getCurrentUsername());
		log.setOrderStatus(Order.OrderStatus.confirmed);
		log.setType(OrderLog.Type.create);
		log.setOrder(order);
		orderLogDao.persist(log);
		String orderUser = "";
		String name = member.getName();
		String mobile = member.getMobile();
		String nickName = member.getNickName();
		if (!StringUtils.isEmpty(name)) {
			orderUser = name;
		} else if (StringUtils.isEmpty(mobile)) {
			orderUser = mobile;
		} else {
			orderUser = nickName;
		}
		messageDetailsService.pushMessage(MessageType.order, true, order.getTenant(), orderUser + "下了订单:" + order.getSn(), order, false);
		return order;

	}

	public Page<Order> findPageByTenant(Tenant tenant, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable) {
		return orderDao.findPageByTenant(tenant, orderStatus, paymentStatus, shippingStatus, hasExpired, pageable);
	}

	public Page<Order> findPageByTenant(Tenant tenant, List<OrderStatus> orderStatuses, List<PaymentStatus> paymentStatuses, List<ShippingStatus> shippingStatuses, Boolean hasExpired, Pageable pageable) {
		return orderDao.findPageByTenant(tenant, orderStatuses, paymentStatuses, shippingStatuses, hasExpired, pageable);
	}

	public Page<Order> findMemberAndOwer(Member member, Member owner, Boolean hasExpired, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Pageable pageable) {
		return orderDao.findMemberAndOwer(member, owner, hasExpired, orderStatus, paymentStatus, shippingStatus, pageable);
	}

	public Page<Order> findMemberAndOwer(Member member, Member owner, List<OrderStatus> orderStatuses, List<PaymentStatus> paymentStatuses, List<ShippingStatus> shippingStatuses, Pageable pageable) {
		return orderDao.findMemberAndOwer(member, owner, orderStatuses, paymentStatuses, shippingStatuses, pageable);
	}

	public Page<Order> findReturnPage(Tenant tenant, List<PaymentStatus> paymentStatuses, List<ShippingStatus> shippingStatuses, Pageable pageable) {
		return orderDao.findReturnPage(tenant, paymentStatuses, shippingStatuses, pageable);
	}

	@Override
	public Page<Order> findPage(List<Tenant> tenant, Pageable pageable) {
		// List<Long> list=new ArrayList<Long>();
		// for(Tenant tenant:tenants){
		// list.add(tenant.getId());
		// }
		return orderDao.findPage(tenant, pageable);
	}

	public Page<Order> findForExport(Tenant tenant, String name, Date beginDate, Date endDate, MemberRank owerRank, OrderSearchStatus status, String sn, Pageable pageable) {
		Page<Order> page = orderDao.findForExport(tenant, name, beginDate, endDate, owerRank, status, sn, pageable);
		for (Order o : page.getContent()) {
			o.setShopkeeper(tenantShopkeeperService.findShopKeeperByMemberId(o.getOwner().getId()));
			o.setBonus(orderSettlementService.getOrderBonus(o.getOrderSettlement(), o.getTenant()));
		}
		return page;
	}

	public List<Order> findForExport(Tenant tenant, String name, Date beginDate, Date endDate, MemberRank owerRank, OrderSearchStatus status, String sn, Integer count,String memberNam) {
		List<Order> orders = orderDao.findForExport(tenant, name, beginDate, endDate, owerRank, status, sn, count,memberNam);
		for (Order o : orders) {
			o.setShopkeeper(tenantShopkeeperService.findShopKeeperByMemberId(o.getOwner().getId()));
			o.setBonus(orderSettlementService.getOrderBonus(o.getOrderSettlement(), o.getTenant()));
		}
		return orders;
	}

	@Override
	public Page<Order> findPageByTenant(Tenant tenant,
			List<OrderStatus> orderStatuses,
			List<PaymentStatus> paymentStatuses,
			List<ShippingStatus> shippingStatuses, String productName,
			String username, String sn, Date startTime, Date endTime,
			Boolean hasExpired, Pageable pageable) {
		return orderDao.findPageByTenant(tenant, orderStatuses, paymentStatuses, shippingStatuses, productName, username, sn, startTime, endTime, hasExpired, pageable);
	}
	
	@Override
	public List<Order> orderTimesWithMonth(Member owner ,PaymentStatus status){
		return orderDao.findOrderByOwner(owner, DateUtil.getFirstDateOfMonth(), DateUtil.getNextMonthFirstDate(), PaymentStatus.paid);
	}

	@Override
	public List<Order> getHistoryOrderByTenant(Tenant tenant,Member member,
			List<OrderStatus> orderStatuses, List<PaymentStatus> paymentStatuses) {
		return this.orderDao.findOrdersByTenant(tenant, member, orderStatuses, paymentStatuses);
	}
	
	@Override
	public BigDecimal getHistoryOrderAmtByTenant(Tenant tenant,Member member,
			List<OrderStatus> orderStatuses, List<PaymentStatus> paymentStatuses){
		List<Order> list = this.orderDao.findOrdersByTenant(tenant, member, orderStatuses, paymentStatuses);
		BigDecimal orderAmt = BigDecimal.ZERO;
		for (Order order : list) {
			orderAmt  = orderAmt.add(order.getAmountPaid());
		}
		return orderAmt;
				 
	}
	
}