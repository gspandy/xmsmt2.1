/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.Filter;
import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.MemberDao;
import net.wit.dao.ProductDao;
import net.wit.dao.ReviewDao;
import net.wit.dao.TenantDao;
import net.wit.dao.TradeDao;
import net.wit.entity.Member;
import net.wit.entity.Product;
import net.wit.entity.Review;
import net.wit.entity.Review.Flag;
import net.wit.entity.Review.Type;
import net.wit.entity.Tenant;
import net.wit.entity.Trade;
import net.wit.service.ReviewService;
import net.wit.service.StaticService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 评论
 * @author rsico Team
 * @version 3.0
 */
@Service("reviewServiceImpl")
public class ReviewServiceImpl extends BaseServiceImpl<Review, Long> implements ReviewService {

	@Resource(name = "reviewDaoImpl")
	private ReviewDao reviewDao;

	@Resource(name = "productDaoImpl")
	private ProductDao productDao;

	@Resource(name = "tenantDaoImpl")
	private TenantDao tenantDao;

	@Resource(name = "memberDaoImpl")
	private MemberDao memberDao;

	@Resource(name = "staticServiceImpl")
	private StaticService staticService;
	@Resource(name = "tradeDaoImpl")
	private TradeDao tradeDao;

	@Resource(name = "reviewDaoImpl")
	public void setBaseDao(ReviewDao reviewDao) {
		super.setBaseDao(reviewDao);
	}

	@Transactional(readOnly = true)
	public List<Review> findList(Member member, Product product, Type type, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders) {
		return reviewDao.findList(member, product, type, isShow, count, filters, orders);
	}

	@Transactional(readOnly = true)
	@Cacheable("review")
	public List<Review> findList(Member member, Product product, Type type, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion) {
		return reviewDao.findList(member, product, type, isShow, count, filters, orders);
	}

	@Transactional(readOnly = true)
	public Page<Review> findPage(Member member, Product product, Type type, Boolean isShow, Pageable pageable) {
		return reviewDao.findPage(member, product, type, isShow, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Review> findTenantPage(Tenant tenant, Type type, Boolean isShow, Pageable pageable) {
		return reviewDao.findTenantPage(tenant, type, isShow, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Review> findMyPage(Member member, Product product, Type type, Boolean isShow, Pageable pageable) {
		return reviewDao.findMyPage(member, product, type, isShow, pageable);
	}

	@Transactional(readOnly = true)
	public Long count(Member member, Product product, Type type, Boolean isShow) {
		return reviewDao.count(member, product, type, isShow);
	}

	@Transactional(readOnly = true)
	public boolean isReviewed(Member member, Product product) {
		return reviewDao.isReviewed(member, product);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review", "consultation" }, allEntries = true)
	public void save(Review review) {
		super.save(review);
		if (review.getFlag().equals(Flag.product)) {
			Product product = review.getProduct();
			if (product != null) {
				reviewDao.flush();
				long totalScore = reviewDao.calculateTotalScore(product);
				long scoreCount = reviewDao.calculateScoreCount(product);
				product.setTotalScore(totalScore);
				product.setScoreCount(scoreCount);
				productDao.merge(product);
				reviewDao.flush();
				staticService.build(product);
			}
		} else if (review.getFlag().equals(Flag.tenant)) {
			Tenant tenant = review.getTenant();
			if (tenant != null) {
				reviewDao.flush();
				long totalScore = reviewDao.calculateTotalScore(tenant);
				long scoreCount = reviewDao.calculateScoreCount(tenant);
				tenant.setTotalScore(totalScore);
				tenant.setScoreCount(scoreCount);
				tenantDao.merge(tenant);
				reviewDao.flush();
			}
		} else if (review.getFlag().equals(Flag.trade)) {
			if (review.getOrderItem() != null) { // 订单商品评价
				reviewDao.flush();
				Product product = review.getOrderItem().getProduct();
				product.setTotalScore(product.getTotalScore() + review.getScore());
				product.setScoreCount(product.getScoreCount() + 1);
				productDao.merge(product);
				reviewDao.flush();
				staticService.build(product);
			} else if (review.getMemberTrade() != null) { // 买家订单评价
				Tenant tenant = review.getMemberTrade().getTenant();
				review.setTenant(tenant);

				tenant.setTotalScore(tenant.getTotalScore() + review.getScore());
				tenant.setTotalAssistant(tenant.getTotalAssistant() + review.getAssistant());
				tenant.setScoreCount(tenant.getScoreCount() + 1);
				tenantDao.merge(tenant);
				reviewDao.flush();
			} else if (review.getTenantTrade() != null) { // 卖家订单评价
				Member member = review.getTenantTrade().getOrder().getMember();
				review.setMember(member);
				member.setTotalScore(member.getTotalScore() + review.getScore());
				memberDao.merge(member);
				reviewDao.flush();
			}
		}
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review", "consultation" }, allEntries = true)
	public Review update(Review review) {
		Review pReview = super.update(review);
		if (review.getFlag().equals(Flag.product)) {
			Product product = review.getProduct();
			if (product != null) {
				reviewDao.flush();
				long totalScore = reviewDao.calculateTotalScore(product);
				long scoreCount = reviewDao.calculateScoreCount(product);
				product.setTotalScore(totalScore);
				product.setScoreCount(scoreCount);
				productDao.merge(product);
				reviewDao.flush();
				staticService.build(product);
			}
		} else {
			Tenant tenant = review.getTenant();
			if (tenant != null) {
				reviewDao.flush();
				long totalScore = reviewDao.calculateTotalScore(tenant);
				long scoreCount = reviewDao.calculateScoreCount(tenant);
				tenant.setTotalScore(totalScore);
				tenant.setScoreCount(scoreCount);
				tenantDao.merge(tenant);
				reviewDao.flush();
			}
		}
		return pReview;
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review", "consultation" }, allEntries = true)
	public Review update(Review review, String... ignoreProperties) {
		return super.update(review, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review", "consultation" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review", "consultation" }, allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review", "consultation" }, allEntries = true)
	public void delete(Review review) {
		if (review != null) {
			super.delete(review);
			if (review.getFlag().equals(Flag.product)) {
				Product product = review.getProduct();
				if (product != null) {
					reviewDao.flush();
					long totalScore = reviewDao.calculateTotalScore(product);
					long scoreCount = reviewDao.calculateScoreCount(product);
					product.setTotalScore(totalScore);
					product.setScoreCount(scoreCount);
					productDao.merge(product);
					reviewDao.flush();
					staticService.build(product);
				}
			} else {
				Tenant tenant = review.getTenant();
				if (tenant != null) {
					reviewDao.flush();
					long totalScore = reviewDao.calculateTotalScore(tenant);
					long scoreCount = reviewDao.calculateScoreCount(tenant);
					tenant.setTotalScore(totalScore);
					tenant.setScoreCount(scoreCount);
					tenantDao.merge(tenant);
					reviewDao.flush();
				}
			}
		}
	}

	@Transactional
	public void tenantReview(Trade trade, Review review) {
		reviewDao.persist(review);
		trade.setTenantReview(review);
		tradeDao.persist(trade);
	}

}