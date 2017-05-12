/**
 *====================================================
 * 文件名称: PromotionGrouponServiceImpl.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年4月25日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.persistence.LockModeType;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.CouponCodeDao;
import net.wit.dao.CouponDao;
import net.wit.dao.MemberDao;
import net.wit.dao.PromotionDao;
import net.wit.dao.PromotionProductDao;
import net.wit.entity.Area;
import net.wit.entity.Attribute;
import net.wit.entity.Brand;
import net.wit.entity.Community;
import net.wit.entity.Coupon;
import net.wit.entity.CouponCode;
import net.wit.entity.Deposit;
import net.wit.entity.Location;
import net.wit.entity.Member;
import net.wit.entity.Product.OrderType;
import net.wit.entity.ProductCategory;
import net.wit.entity.Promotion;
import net.wit.entity.Promotion.Classify;
import net.wit.entity.Promotion.Type;
import net.wit.entity.PromotionMember;
import net.wit.entity.PromotionMember.Status;
import net.wit.entity.PromotionProduct;
import net.wit.entity.PromotionProduct.TimeRegion;
import net.wit.entity.Receiver;
import net.wit.entity.ShippingMethod;
import net.wit.entity.Tag;
import net.wit.service.PromotionGrouponService;
import net.wit.service.impl.support.PromotionBaseServiceImpl;
import net.wit.util.SpringUtils;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName: PromotionGrouponServiceImpl
 * @Description: 团购促销
 * @author Administrator
 * @date 2014年4月25日 下午2:58:43
 */
@Service("promotionGrouponService")
public class PromotionGrouponServiceImpl extends PromotionBaseServiceImpl implements PromotionGrouponService {

	@Resource(name = "promotionDaoImpl")
	public void setBaseDao(PromotionDao promotionDao) {
		super.setBaseDao(promotionDao);
	}

	@Resource(name = "promotionProductDaoImpl")
	private PromotionProductDao promotionProductDao;

	@Resource(name = "memberDaoImpl")
	private MemberDao memberDao;

	@Resource(name = "couponDaoImpl")
	private CouponDao couponDao;

	@Resource(name = "couponCodeDaoImpl")
	private CouponCodeDao couponCodeDao;

	private void createPromotionGrouponOrder(ShippingMethod shippingMethod, Promotion promotion, PromotionMember promotionMember) {
		createPromotionOrder(shippingMethod, promotion, promotionMember, promotionMember.getDeposit(), null);
		promotionMember.setStatus(PromotionMember.Status.finished);
	}

	@Transactional
	@CacheEvict(value = { "promotion" }, allEntries = true)
	public void submitAuction(Member member, Promotion promotion, Integer quantity, Receiver receiver, Long point) throws Exception {
		promotionDao.lock(promotion, LockModeType.PESSIMISTIC_WRITE);
		if (Promotion.Status.submit != promotion.getStatus() && Promotion.Status.underway != promotion.getStatus()) {
			throw new java.lang.IllegalArgumentException("该团购活动已经完成!");
		}
		Calendar calendar = Calendar.getInstance();
		if (promotion.getEndDate().getTime() - calendar.getTime().getTime() < 0) {
			throw new java.lang.IllegalArgumentException("该团购活动已经结束!");
		}
		if (promotion.getBeginDate().getTime() - calendar.getTime().getTime() > 0) {
			throw new java.lang.IllegalArgumentException("该团购活动尚未开始!");
		}
		promotion.setStatus(Promotion.Status.underway);
		BigDecimal promotionPrice = promotion.getPromotionPrice();
		PromotionMember pm = new PromotionMember();
		pm.setMember(member);
		pm.setPromotion(promotion);
		pm.setQuantity(quantity);
		BigDecimal couponDiscount = BigDecimal.ZERO;
		if (point != null && 0L < point) {
			Coupon coupon = couponDao.findSystemPointExchange();
			CouponCode couponCode = new CouponCode();
			String uuid = UUID.randomUUID().toString().toUpperCase();
			couponCode.setCode(coupon.getPrefix() + uuid.substring(0, 8) + uuid.substring(9, 13) + uuid.substring(14, 18) + uuid.substring(19, 23) + uuid.substring(24));
			couponCode.setPoint(point);
			couponCode.setIsUsed(true);
			couponCode.setCoupon(coupon);
			couponCode.setMember(member);
			couponCodeDao.persist(couponCode);

			couponDiscount = couponCode.getCoupon().calculatePrice(couponCode.getPoint());
			pm.setCouponCode(couponCode);
		}
		pm.setOfferPrice(promotionPrice.multiply(new BigDecimal(quantity)).subtract(couponDiscount));
		pm.setStatus(PromotionMember.Status.partake);
		pm.setReceiver(receiver);
		int challege = SpringUtils.getIdentifyingCode();
		pm.setRandom(String.valueOf(challege));

		Deposit deposit = memberService.update(member, null, pm.getOfferPrice().negate(), "团购预付款", null);
		pm.setDeposit(deposit);
		promotion.getPromotionMembers().add(pm);
		promotionDao.persist(promotion);

		if (promotion.getShippingMethod() != null && (promotion.getMinimumQuantity() == null || promotion.getPromotionMembers().size() >= promotion.getMinimumQuantity())) {
			List<Long> promotionMembers = new ArrayList<Long>();
			for (PromotionMember promotionMember : promotion.getPromotionMembers()) {
				if (PromotionMember.Status.partake == promotionMember.getStatus()) {
					promotionMembers.add(promotionMember.getId());
				}
			}
			confirm(promotion, promotionMembers.toArray(new Long[promotionMembers.size()]), promotion.getShippingMethod());
		}
		if (promotion.getPromotionMembers().size() >= promotion.getMaximumQuantity()) {
			if (promotion.getShippingMethod() != null) {
				finished(promotion.getShippingMethod(), promotion);
			}
		}
	}

	@Transactional
	public void cancel(Promotion promotion, Long[] promotionMemberIds) throws Exception {
		if (promotionMemberIds == null || promotionMemberIds.length < 1) {
			throw new java.lang.IllegalArgumentException("未获取参与人员!");
		}
		if (Promotion.Status.finished == promotion.getStatus() || Promotion.Status.cancel == promotion.getStatus()) {
			throw new java.lang.IllegalArgumentException("该团购活动已经结束!");
		}
		promotionDao.lock(promotion, LockModeType.PESSIMISTIC_WRITE);
		Set<Long> submitPromotionMemberIds = new HashSet<Long>();
		submitPromotionMemberIds.addAll(Arrays.asList(promotionMemberIds));
		for (PromotionMember promotionMember : promotion.getPromotionMembers()) {
			if (!submitPromotionMemberIds.contains(promotionMember.getId())) { // 不在提交参与会员ID中
				continue;
			}
			if (PromotionMember.Status.partake != promotionMember.getStatus()) { // 参与会员已经不在参与状态
				continue;
			}
			if (promotionMember.getDeposit() != null) { // 返还预付款
				promotionMember.setDeposit(null);
				memberService.update(promotionMember.getMember(), null, promotionMember.getOfferPrice(), "团购活动结束,预付款返还", null);
				CouponCode couponCode = promotionMember.getCouponCode();
				if (couponCode != null) {
					if (Coupon.Type.point == couponCode.getCoupon().getType()) { // 积分兑换券
						Member member = promotionMember.getMember();
						member.setPoint(member.getPoint() + couponCode.getPoint());
						memberDao.persist(member);
					}
				}
			}
			promotionMember.setStatus(PromotionMember.Status.cancel); // 取消状态
		}
		promotionDao.merge(promotion);
	}

	@Transactional
	public void confirm(Promotion promotion, Long[] promotionMemberIds, ShippingMethod shippingMethod) throws Exception {
		if (promotionMemberIds == null || promotionMemberIds.length < 1) {
			throw new java.lang.IllegalArgumentException("未获取参与人员!");
		}
		if (Promotion.Status.finished == promotion.getStatus() || Promotion.Status.cancel == promotion.getStatus()) {
			throw new java.lang.IllegalArgumentException("该团购活动已经结束!");
		}
		promotionDao.lock(promotion, LockModeType.PESSIMISTIC_WRITE);
		Set<Long> submitPromotionMemberIds = new HashSet<Long>();
		submitPromotionMemberIds.addAll(Arrays.asList(promotionMemberIds));
		for (PromotionMember promotionMember : promotion.getPromotionMembers()) {
			if (!submitPromotionMemberIds.contains(promotionMember.getId())) { // 不在提交参与会员ID中
				continue;
			}
			if (PromotionMember.Status.partake != promotionMember.getStatus()) { // 参与会员已经不在参与状态
				continue;
			}
			createPromotionGrouponOrder(shippingMethod, promotion, promotionMember);
			promotionMember.setStatus(PromotionMember.Status.finished); // 完成订单状态
		}
		promotionDao.merge(promotion);
	}

	@Transactional
	public void finished(ShippingMethod shippingMethod, Promotion promotion) throws Exception {
		if (Promotion.Status.submit == promotion.getStatus()) {
			throw new java.lang.IllegalArgumentException("该团购活动未有人参与!");
		}
		if (Promotion.Status.finished == promotion.getStatus() || Promotion.Status.cancel == promotion.getStatus()) {
			throw new java.lang.IllegalArgumentException("该团购活动已经结束!");
		}
		promotionDao.lock(promotion, LockModeType.PESSIMISTIC_WRITE);
		for (PromotionMember promotionMember : promotion.getPromotionMembers()) {
			if (PromotionMember.Status.partake != promotionMember.getStatus()) { // 参与会员已经不在参与状态
				continue;
			}
			createPromotionGrouponOrder(shippingMethod, promotion, promotionMember);
			promotionMember.setStatus(PromotionMember.Status.finished); // 完成订单状态
		}
		promotion.setStatus(Promotion.Status.finished);
		promotionDao.merge(promotion);
	}

	@Transactional
	public void rescind(Promotion promotion) throws Exception {
		if (Promotion.Status.finished == promotion.getStatus() || Promotion.Status.cancel == promotion.getStatus()) {
			throw new java.lang.IllegalArgumentException("该团购活动已经结束!");
		}
		promotionDao.lock(promotion, LockModeType.PESSIMISTIC_WRITE);
		for (PromotionMember promotionMember : promotion.getPromotionMembers()) {
			if (PromotionMember.Status.finished == promotionMember.getStatus()) {
				throw new java.lang.IllegalArgumentException("该团购活动已经有对参与人员进行确认并生成订单,无法撤销!");
			}
			promotionMember.setDeposit(null);
			memberService.update(promotionMember.getMember(), null, promotionMember.getOfferPrice(), "团购撤销,预付款返还", null);
			CouponCode couponCode = promotionMember.getCouponCode();
			if (couponCode != null) {
				if (Coupon.Type.point == couponCode.getCoupon().getType()) { // 积分兑换券
					Member member = promotionMember.getMember();
					member.setPoint(member.getPoint() + couponCode.getPoint());
					memberDao.persist(member);
				}
			}
			promotionMember.setStatus(PromotionMember.Status.cancel);
		}
		promotion.setStatus(Promotion.Status.cancel);
		promotionDao.merge(promotion);
	}

	public Page<PromotionMember> findPage(Promotion promotion, Status status, Pageable pageable) {
		return promotionMemberDao.findPage(promotion, status, pageable);
	}

	@Transactional(readOnly = true)
	public Page<PromotionProduct> findPage(Type type, Promotion promotion, ProductCategory productCategory, Area area, Brand brand, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType,
			Boolean periferal, Community community, Pageable pageable) {
		return promotionProductDao.findPage(type, promotion, productCategory, area, brand, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, orderType, periferal, community, pageable);
	}

	public Page<PromotionProduct> findPage(Type type, Promotion promotion, Area area, TimeRegion region, List<net.wit.entity.Promotion.Status> status, Classify classify, Boolean periferal, Community community, Location location, BigDecimal distatce, ProductCategory productCategory, Pageable pageable) {
		return promotionProductDao.findPage(type, promotion, area, region, status, classify, periferal, community, location, distatce, productCategory, pageable);
	}

	public Page<PromotionProduct> findPageNormal(Type type, Promotion promotion, Area area, TimeRegion region, List<net.wit.entity.Promotion.Status> status, Classify classify, Boolean periferal, Community community, Location location, BigDecimal distatce, ProductCategory productCategory, Pageable pageable) {
		return promotionProductDao.findPageNormal(type, promotion, area, region, status, classify, periferal, community, location, distatce, productCategory, pageable);
	}
}
