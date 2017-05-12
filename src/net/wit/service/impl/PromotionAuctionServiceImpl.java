/**
 *====================================================
 * 文件名称: PromotionAuctionServiceImpl.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年4月25日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.LockModeType;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.PromotionDao;
import net.wit.dao.PromotionProductDao;
import net.wit.entity.Area;
import net.wit.entity.Attribute;
import net.wit.entity.Brand;
import net.wit.entity.Community;
import net.wit.entity.Deposit;
import net.wit.entity.Member;
import net.wit.entity.Product.OrderType;
import net.wit.entity.ProductCategory;
import net.wit.entity.Promotion;
import net.wit.entity.Promotion.Classify;
import net.wit.entity.Promotion.Type;
import net.wit.entity.PromotionMember;
import net.wit.entity.PromotionMember.Status;
import net.wit.entity.PromotionProduct;
import net.wit.entity.Receiver;
import net.wit.entity.ShippingMethod;
import net.wit.entity.Tag;
import net.wit.service.PromotionAuctionService;
import net.wit.service.impl.support.PromotionBaseServiceImpl;
import net.wit.util.DateUtil;
import net.wit.util.SpringUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName: PromotionAuctionServiceImpl
 * @Description: 拍卖促销
 * @author Administrator
 * @date 2014年4月25日 下午3:02:18
 */
@Service("promotionAuctionService")
public class PromotionAuctionServiceImpl extends PromotionBaseServiceImpl implements PromotionAuctionService {

	@Resource(name = "promotionDaoImpl")
	public void setBaseDao(PromotionDao promotionDao) {
		super.setBaseDao(promotionDao);
	}

	@Resource(name = "promotionProductDaoImpl")
	private PromotionProductDao promotionProductDao;

	private void createPromotionAuctionWinnerOrder(ShippingMethod shippingMethod, Promotion promotion, PromotionMember promotionMember) throws Exception {
		// 生成支付记录
		Deposit deposit = memberService.update(promotionMember.getMember(), null, promotionMember.getOfferPrice().negate(), "拍卖活动结束,竞拍成功", null);
		createPromotionOrder(shippingMethod, promotion, promotionMember, deposit, null);
	}

	/** 标准拍 */
	private void submitStandard(Member member, Promotion promotion, Receiver receiver, BigDecimal currentProxyPrice) throws Exception {
		Deposit deposit = null;
		if (promotion.getBail().compareTo(BigDecimal.ZERO) > 0) { // 需要保证金
			if (!promotion.isMemberPartake(member)) { // 未参与竞拍
				deposit = memberService.update(member, null, promotion.getBail().negate(), "拍卖保证金", null);
			} else {
				for (PromotionMember pm : promotion.getPromotionMembers()) {
					if (pm.getMember().getId().equals(member.getId()) && pm.getDeposit() != null) {
						deposit = pm.getDeposit();
						pm.setDeposit(null);
						break;
					}
				}
			}
		}
		Date currentDate = new Date();
		if (((promotion.getEndDate().getTime() - currentDate.getTime()) / 180000) < 3) {
			promotion.setEndDate(DateUtil.transpositionDate(currentDate, Calendar.MINUTE, 3));
		}

		PromotionMember promotionMember = new PromotionMember();
		promotionMember.setMember(member);
		promotionMember.setPromotion(promotion);
		promotionMember.setStatus(PromotionMember.Status.partake);
		promotionMember.setReceiver(receiver);
		int challege = SpringUtils.getIdentifyingCode();
		promotionMember.setRandom(String.valueOf(challege));
		promotionMember.setDeposit(deposit);
		// 代理出价
		PromotionMember proxyPromotionMember = null;
		for (PromotionMember temp : promotion.getPromotionMembers()) {
			if (temp.getProxyPrice() == null) {
				continue;
			}
			proxyPromotionMember = temp;
			break;
		}
		if (proxyPromotionMember == null) {
			promotion.setCurrentPrice(promotion.getCurrentPrice().add(promotion.getStepPrice()));
			promotionMember.setOfferPrice(promotion.getCurrentPrice());
			promotionMember.setProxyPrice(currentProxyPrice);
			promotion.getPromotionMembers().add(promotionMember);
		} else {
			BigDecimal proxyPrice = proxyPromotionMember.getProxyPrice();
			if (currentProxyPrice.compareTo(proxyPrice) > 0) {
				if (proxyPrice.compareTo(promotion.getCurrentPrice().add(promotion.getStepPrice())) == 1) {
					promotion.setCurrentPrice(proxyPrice);
				} else if (currentProxyPrice.compareTo(promotion.getCurrentPrice().add(promotion.getStepPrice())) == 1) {
					promotion.setCurrentPrice(promotion.getCurrentPrice().add(promotion.getStepPrice()));
				} else {
					promotion.setCurrentPrice(currentProxyPrice);
				}
				promotionMember.setOfferPrice(promotion.getCurrentPrice());
				promotionMember.setProxyPrice(currentProxyPrice);
				proxyPromotionMember.setProxyPrice(null);
				promotion.getPromotionMembers().add(promotionMember);
			} else {
				promotionMember.setOfferPrice(currentProxyPrice);
				promotion.getPromotionMembers().add(promotionMember);

				proxyPromotionMember.setProxyPrice(null);
				proxyPromotionMember.setDeposit(null);
				PromotionMember proxyMember = new PromotionMember();
				proxyMember.setMember(proxyPromotionMember.getMember());
				proxyMember.setPromotion(promotion);
				proxyMember.setStatus(PromotionMember.Status.partake);
				proxyMember.setReceiver(proxyPromotionMember.getReceiver());
				challege = SpringUtils.getIdentifyingCode();
				proxyMember.setRandom(String.valueOf(challege));
				proxyMember.setDeposit(proxyPromotionMember.getDeposit());
				if (proxyPrice.compareTo(currentProxyPrice.add(promotion.getStepPrice())) > 0) {
					promotion.setCurrentPrice(currentProxyPrice.add(promotion.getStepPrice()));
				} else {
					promotion.setCurrentPrice(currentProxyPrice);
				}
				proxyMember.setOfferPrice(promotion.getCurrentPrice());
				proxyMember.setProxyPrice(proxyPrice);
				proxyMember.setMemo("代理出价");
				promotion.getPromotionMembers().add(proxyMember);
			}
		}
	}

	/** 特权拍 */
	private void submitPrivilege(Member member, Promotion promotion, Receiver receiver) throws Exception {
		promotion.setCurrentPrice(promotion.getCurrentPrice().add(promotion.getStepPrice()));

		PromotionMember promotionMember = new PromotionMember();
		promotionMember.setMember(member);
		promotionMember.setPromotion(promotion);
		promotionMember.setStatus(PromotionMember.Status.partake);
		promotionMember.setReceiver(receiver);
		int challege = SpringUtils.getIdentifyingCode();
		promotionMember.setRandom(String.valueOf(challege));
		promotionMember.setOfferPrice(promotion.getCurrentPrice());
		if (member.getPrivilege() <= 0) {
			throw new java.lang.IllegalArgumentException("特权数不足,无法参与该竞拍的活动!");
		}
		member.setPrivilege(member.getPrivilege() - 1);
		Deposit deposit = memberService.update(member, null, promotion.getBail().negate(), "拍卖保证金", null);
		promotionMember.setDeposit(deposit);

		promotion.getPromotionMembers().add(promotionMember);
	}

	@Transactional
	public synchronized void submitAuction(Member member, Promotion promotion, BigDecimal currentPrice, BigDecimal proxyPrice, Receiver receiver) throws Exception {
		Calendar calendar = Calendar.getInstance();
		if (promotion.getEndDate().getTime() - calendar.getTime().getTime() < 0) {
			throw new java.lang.IllegalArgumentException("该拍卖活动已经结束!");
		}
		if (promotion.getBeginDate().getTime() - calendar.getTime().getTime() > 0) {
			throw new java.lang.IllegalArgumentException("该拍卖活动尚未开始!");
		}
		if (promotion.getCurrentPrice().compareTo(currentPrice) != 0) {
			throw new java.lang.IllegalArgumentException("竞拍价格已经刷新!");
		}
		if (Promotion.Status.submit != promotion.getStatus() && Promotion.Status.underway != promotion.getStatus()) {
			throw new java.lang.IllegalArgumentException("该拍卖活动已经结束!");
		}
		promotionDao.lock(promotion, LockModeType.PESSIMISTIC_WRITE);

		promotion.setStatus(Promotion.Status.underway);
		if (Promotion.Classify.privilege == promotion.getClassify()) {
			submitPrivilege(member, promotion, receiver);
		} else {
			submitStandard(member, promotion, receiver, proxyPrice);
		}
		promotionDao.persist(promotion);
	}

	@Transactional
	public void cancel(Promotion promotion, Long[] promotionMemberIds) throws Exception {
		if (promotionMemberIds == null || promotionMemberIds.length < 1) {
			throw new java.lang.IllegalArgumentException("未获取参与人员!");
		}
		if (Promotion.Status.finished == promotion.getStatus() || Promotion.Status.cancel == promotion.getStatus()) {
			throw new java.lang.IllegalArgumentException("该拍卖活动已经结束!");
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
			if (promotionMember.getDeposit() != null) { // 返还保证金
				promotionMember.setDeposit(null);
				memberService.update(promotionMember.getMember(), null, promotion.getBail(), "拍卖撤销,保证金返还", null);
			}
			promotionMember.setStatus(PromotionMember.Status.cancel); // 取消状态
		}
		promotionDao.merge(promotion);
	}

	@Transactional
	public void confirm(Promotion promotion, Long[] promotionMemberIds, ShippingMethod shippingMethod) throws Exception {
		try {
			if (promotionMemberIds == null || promotionMemberIds.length < 1) {
				throw new java.lang.IllegalArgumentException("未获取参与人员!");
			}
			if (Promotion.Status.finished == promotion.getStatus() || Promotion.Status.cancel == promotion.getStatus()) {
				throw new java.lang.IllegalArgumentException("该拍卖活动已经结束!");
			}
			promotionDao.lock(promotion, LockModeType.PESSIMISTIC_WRITE);
			Set<Long> submitPromotionMemberIds = new HashSet<Long>();
			submitPromotionMemberIds.addAll(Arrays.asList(promotionMemberIds));
			for (PromotionMember promotionMember : promotion.getPromotionMembers()) {
				if (!submitPromotionMemberIds.contains(promotionMember.getId())) {
					continue;
				}
				if (PromotionMember.Status.partake != promotionMember.getStatus()) {
					continue;
				}
				if (promotionMember.getDeposit() != null) {
					promotionMember.setDeposit(null);
					memberService.update(promotionMember.getMember(), null, promotion.getBail(), "拍卖活动结束,保证金返还", null);
				}
				createPromotionAuctionWinnerOrder(shippingMethod, promotion, promotionMember);
				promotionMember.setStatus(PromotionMember.Status.finished);
			}
			promotionDao.merge(promotion);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Transactional
	public void finished(Promotion promotion, ShippingMethod shippingMethod) throws Exception {
		if (Promotion.Status.submit == promotion.getStatus()) {
			throw new java.lang.IllegalArgumentException("该拍卖活动未有人参与!");
		}
		if (Promotion.Status.finished == promotion.getStatus() || Promotion.Status.cancel == promotion.getStatus()) {
			throw new java.lang.IllegalArgumentException("该拍卖活动已经结束!");
		}
		promotionDao.lock(promotion, LockModeType.PESSIMISTIC_WRITE);
		PromotionMember auctionWinner = null;
		for (PromotionMember promotionMember : promotion.getPromotionMembers()) {
			if (PromotionMember.Status.partake != promotionMember.getStatus()) {
				continue;
			}
			if (auctionWinner == null) {
				auctionWinner = promotionMember;
				continue;
			}
			PromotionMember failPromotionMember = null;
			if (auctionWinner.getOfferPrice().compareTo(promotionMember.getOfferPrice()) < 0) {
				failPromotionMember = auctionWinner;
				auctionWinner = promotionMember;
			} else {
				failPromotionMember = promotionMember;
			}
			if (failPromotionMember.getDeposit() != null) {
				failPromotionMember.setDeposit(null);
				memberService.update(failPromotionMember.getMember(), null, promotion.getBail(), "拍卖活动结束,保证金返还", null);
			}
			failPromotionMember.setStatus(PromotionMember.Status.cancel);
		}
		if (auctionWinner != null) {
			if (auctionWinner.getDeposit() != null) {
				auctionWinner.setDeposit(null);
				memberService.update(auctionWinner.getMember(), null, promotion.getBail(), "拍卖活动结束,保证金返还", null);
			}
			createPromotionAuctionWinnerOrder(shippingMethod, promotion, auctionWinner);
			auctionWinner.setStatus(PromotionMember.Status.finished);
		}
		promotion.setStatus(Promotion.Status.finished);
		promotionDao.merge(promotion);
	}

	@Transactional
	public void rescind(Promotion promotion) throws Exception {
		if (Promotion.Status.finished == promotion.getStatus() || Promotion.Status.cancel == promotion.getStatus()) {
			throw new java.lang.IllegalArgumentException("该拍卖活动已经结束!");
		}
		promotionDao.lock(promotion, LockModeType.PESSIMISTIC_WRITE);
		for (PromotionMember promotionMember : promotion.getPromotionMembers()) {
			if (PromotionMember.Status.finished == promotionMember.getStatus()) {
				throw new java.lang.IllegalArgumentException("该拍卖活动已经有对参与人员进行确认并生成订单,无法撤销!");
			}
			if (PromotionMember.Status.cancel == promotionMember.getStatus()) {
				continue;
			}
			if (promotionMember.getDeposit() != null) {
				promotionMember.setDeposit(null);
				memberService.update(promotionMember.getMember(), null, promotion.getBail(), "拍卖活动结束,保证金返还", null);
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

	@Transactional(readOnly = true)
	public Page<PromotionMember> findJoinAuctionPage(Pageable pageable, Member member, Classify classify) {
		return promotionMemberDao.findJoinAuctionPage(pageable, member, classify);
	}
}
