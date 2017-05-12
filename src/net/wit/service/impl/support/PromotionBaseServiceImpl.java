/**
 *====================================================
 * 文件名称: PromotionBaseServiceImpl.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年4月29日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.service.impl.support;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.persistence.LockModeType;

import net.wit.Setting;
import net.wit.constant.SettingConstant;
import net.wit.dao.CouponCodeDao;
import net.wit.dao.OrderDao;
import net.wit.dao.OrderLogDao;
import net.wit.dao.ProductDao;
import net.wit.dao.PromotionDao;
import net.wit.dao.PromotionMemberDao;
import net.wit.dao.SnDao;
import net.wit.entity.Admin;
import net.wit.entity.Coupon;
import net.wit.entity.CouponCode;
import net.wit.entity.Deposit;
import net.wit.entity.Member;
import net.wit.entity.Order;
import net.wit.entity.Order.ShippingStatus;
import net.wit.entity.OrderItem;
import net.wit.entity.OrderLog;
import net.wit.entity.OrderLog.Type;
import net.wit.entity.Payment;
import net.wit.entity.PaymentMethod;
import net.wit.entity.Product;
import net.wit.entity.Promotion;
import net.wit.entity.PromotionMember;
import net.wit.entity.PromotionProduct;
import net.wit.entity.Receiver;
import net.wit.entity.SafeKey;
import net.wit.entity.ShippingMethod;
import net.wit.entity.Sn;
import net.wit.entity.Trade;
import net.wit.service.AppointmentService;
import net.wit.service.MemberService;
import net.wit.service.PaymentMethodService;
import net.wit.service.SmsSendService;
import net.wit.service.StaticService;
import net.wit.service.impl.BaseServiceImpl;
import net.wit.support.EntitySupport;
import net.wit.util.RandomUtil;
import net.wit.util.SettingUtils;
import net.wit.util.SpringUtils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName: PromotionBaseServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年4月29日 上午10:39:48
 */
public abstract class PromotionBaseServiceImpl extends BaseServiceImpl<Promotion, Long> {

	protected static final Logger logger = LoggerFactory.getLogger(PromotionBaseServiceImpl.class);

	@Resource(name = "promotionDaoImpl")
	protected PromotionDao promotionDao;

	@Resource
	protected PromotionMemberDao promotionMemberDao;

	@Resource(name = "productDaoImpl")
	protected ProductDao productDao;

	@Resource(name = "snDaoImpl")
	protected SnDao snDao;

	@Resource(name = "orderDaoImpl")
	private OrderDao orderDao;

	@Resource(name = "orderLogDaoImpl")
	private OrderLogDao orderLogDao;

	@Resource(name = "couponCodeDaoImpl")
	private CouponCodeDao couponCodeDao;

	@Resource(name = "appointmentServiceImpl")
	protected AppointmentService appointmentService;

	@Resource(name = "paymentMethodServiceImpl")
	protected PaymentMethodService paymentMethodService;

	@Resource(name = "staticServiceImpl")
	protected StaticService staticService;

	@Resource(name = "smsSendServiceImpl")
	private SmsSendService smsSendService;

	@Resource(name = "memberServiceImpl")
	protected MemberService memberService;

	protected Order createPromotionOrder(ShippingMethod shippingMethod, Promotion promotion, PromotionMember promotionMember, Deposit deposit, Admin operator) {
		Member member = promotionMember.getMember();
		Receiver receiver = promotionMember.getReceiver();
		Order order = new Order();
		order.setSn(snDao.generate(Sn.Type.order));
		order.setOrderStatus(Order.OrderStatus.confirmed);
		order.setPaymentStatus(Order.PaymentStatus.paid);
		order.setShippingStatus(Order.ShippingStatus.unshipped);
		order.setFee(BigDecimal.ZERO);
		order.setFreight(BigDecimal.ZERO);
		order.setPromotionDiscount(BigDecimal.ZERO);

		order.setCouponDiscount(BigDecimal.ZERO);
		order.setCouponCode(null);
		CouponCode couponCode = promotionMember.getCouponCode();
		if (couponCode != null && Coupon.Type.point == couponCode.getCoupon().getType()) { // 积分兑换券
			BigDecimal couponDiscount = couponCode.getCoupon().calculatePrice(couponCode.getPoint());
			order.setCouponDiscount(couponDiscount);
			order.setCouponCode(couponCode);
		}

		order.setOffsetAmount(BigDecimal.ZERO);
		order.setPoint(promotion.getPromotionPoint());
		order.setAmountPaid(deposit.getDebit());
		order.setConsignee(receiver.getConsignee());
		order.setAddress(receiver.getAddress());
		order.setZipCode(receiver.getZipCode());
		order.setPhone(receiver.getPhone());
		order.setIsInvoice(false);
		order.setInvoiceTitle(null);
		order.setTax(BigDecimal.ZERO);
		order.setMemo(promotion.getName() + "订单.");
		order.setAppointment(appointmentService.findDefault());
		order.setPromotion(promotion.getName());
		order.setExpire(null);
		order.setLockExpire(null);

		PaymentMethod paymentMethod = paymentMethodService.findFirst();
		order.setPaymentMethod(paymentMethod);
		order.setShippingMethod(shippingMethod);
		order.setArea(receiver.getArea());
		order.setOperator(null);
		order.setMember(member);
		order.setCoupons(null);
		order.getDeposits().add(deposit);

		Payment payment = new Payment();
		payment.setSn(snDao.generate(Sn.Type.payment));
		payment.setType(Payment.Type.payment);
		payment.setMethod(Payment.Method.deposit);
		payment.setStatus(Payment.Status.success);
		payment.setPaymentMethod(order.getPaymentMethodName());
		payment.setBank(null);
		payment.setAccount(null);
		payment.setFee(BigDecimal.ZERO);
		payment.setAmount(order.getAmountPaid());
		payment.setPayer(member.getName());
		payment.setOperator(null);
		payment.setPaymentDate(new Date());
		payment.setMemo(promotion.getName() + "-订单支付");
		payment.setPaymentPluginId(null);
		payment.setExpire(null);
		payment.setDeposit(deposit);
		payment.setMember(member);
		payment.setOrder(order);
		order.getPayments().add(payment);

		if (Promotion.Type.auction == promotion.getType()) { // 拍卖
			BigDecimal sharePaidAmount = order.getAmountPaid();
			BigDecimal minimumAmount = BigDecimal.ZERO;
			for (PromotionProduct pp : promotion.getPromotionProducts()) {
				minimumAmount = minimumAmount.add(pp.getPrice());
			}
			Iterator<PromotionProduct> iterator = promotion.getPromotionProducts().iterator();
			for (int i = 0; iterator.hasNext(); i++) {
				PromotionProduct next = iterator.next();
				if (i == promotion.getPromotionProducts().size()) {
					next.setTempPrice(sharePaidAmount);
				} else {
					if (minimumAmount.compareTo(BigDecimal.ZERO) > 0) {
						next.setTempPrice(order.getAmountPaid().multiply(next.getPrice().divide(minimumAmount)).setScale(2, BigDecimal.ROUND_HALF_UP));
					} else {
						next.setTempPrice(order.getAmountPaid().divide(new BigDecimal(promotion.getPromotionProducts().size())).setScale(2, BigDecimal.ROUND_HALF_UP));
					}
					sharePaidAmount = sharePaidAmount.subtract(next.getTempPrice());
				}
			}
		} else {
			for (PromotionProduct pp : promotion.getPromotionProducts()) {
				pp.setTempPrice(pp.getPrice());
			}
		}

		// 子订单/订单项、是否已分配库存
		order.setIsAllocatedStock(true);
		for (PromotionProduct pp : promotion.getPromotionProducts()) {
			Product product = pp.getProduct();
			productDao.lock(product, LockModeType.PESSIMISTIC_WRITE);
			OrderItem orderItem = new OrderItem();
			Trade trade = order.getTrade(product.getTenant());
			if (trade == null) {
				trade = EntitySupport.createInitTrade();
				trade.setSn(RandomUtil.encryptRandom(Long.parseLong(snDao.generate(Sn.Type.trade))));
				trade.setShippingStatus(ShippingStatus.unshipped);
				SafeKey safeKey = new SafeKey(String.valueOf(SpringUtils.getIdentifyingCode()), null);
				trade.setSafeKey(safeKey);
				trade.setTenant(product.getTenant());
				trade.setOrder(order);
				trade.setLockExpire(null);
				order.getTrades().add(trade);
			}
			orderItem.setSn(product.getSn());
			orderItem.setName(product.getName());
			orderItem.setFullName(product.getFullName());
			orderItem.setQuantity(pp.getQuantity() * promotionMember.getQuantity());
			orderItem.setPrice(pp.getTempPrice());
			orderItem.setThumbnail(product.getThumbnail());
			orderItem.setIsGift(false);
			orderItem.setWeight(product.getWeight());
			orderItem.setPackagUnitName(product.getUnit());
			orderItem.setCalculatePackagUnit(product, null);
			orderItem.setShippedQuantity(0);
			orderItem.setReturnQuantity(0);
			orderItem.setProduct(product);
			orderItem.setTrade(trade);
			orderItem.setOrder(order);
			trade.getOrderItems().add(orderItem);
			if (product.getStock() != null) {
				product.setAllocatedStock(product.getAllocatedStock() + orderItem.calculateQuantityIntValue());
				productDao.merge(product);
			}
		}
		// 订单类型
		if (order.getTrades().size() > 1) {
			order.setOrderType(Order.OrderType.composite);
		} else {
			order.setOrderType(Order.OrderType.single);
		}
		order.setPromotionScheme(promotion);
		orderDao.persist(order);
		orderDao.refresh(order);

		member.setPoint(member.getPoint() + order.getPoint());
		member.setAmount(member.getAmount().add(order.getAmountPaid()));
		member.setPrivilege(member.getPrivilege() + order.getAmountPaid().multiply(SettingConstant.amountPrivilegeScale).intValue());
		memberService.save(member);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.create);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		ResourceBundle bundle = PropertyResourceBundle.getBundle("config");
		Setting setting = SettingUtils.get();
		Map<String, String> model = new HashMap<String, String>();
		for (Trade t : order.getTrades()) {
			model.put("siteName", setting.getSiteName());
			model.put("validatecode", t.getSn());
			model.put("signature", bundle.getString("signature"));
			String sendPhone = order.getPhone();
			if(sendPhone!=null&&StringUtils.isNotEmpty(sendPhone)){
				smsSendService.sendTemplateNoticePool(sendPhone, "memberTPLNotice", model);
			}else{
				smsSendService.sendTemplateNoticePool(order.getMember().getMobile(), "memberTPLNotice", model);
			}
			model.clear();
		}
		return order;
	}

	@Transactional
	@CacheEvict(value = { "promotion", "product" }, allEntries = true)
	public void delete(Long... ids) {
		for (Long id : ids) {
			Promotion promotion = promotionDao.find(id);
			if (Promotion.Status.submit == promotion.getStatus() || Promotion.Status.cancel == promotion.getStatus()) {
				super.delete(id);
			}
		}
	}

	public PromotionMember findPromotionMember(Long id) {
		return promotionMemberDao.find(id);
	}

	@Transactional
	public void updatePromotionMember(PromotionMember promotionMember) {
		promotionMemberDao.merge(promotionMember);
	}

}
