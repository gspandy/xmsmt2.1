/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import net.wit.Filter;
import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.PromotionDao;
import net.wit.dao.PromotionProductDao;
import net.wit.entity.Area;
import net.wit.entity.Community;
import net.wit.entity.Location;
import net.wit.entity.ProductCategory;
import net.wit.entity.Promotion;
import net.wit.entity.Promotion.Classify;
import net.wit.entity.Promotion.Status;
import net.wit.entity.Promotion.Type;
import net.wit.entity.PromotionProduct;
import net.wit.entity.PromotionProduct.TimeRegion;
import net.wit.service.PromotionService;
import net.wit.service.impl.support.PromotionBaseServiceImpl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 促销
 * @author rsico Team
 * @version 3.0
 */
@Service("promotionServiceImpl")
public class PromotionServiceImpl extends PromotionBaseServiceImpl implements PromotionService {

	@Resource(name = "promotionDaoImpl")
	private PromotionDao promotionDao;

	@Resource
	private PromotionProductDao promotionProductDao;

	@Resource(name = "promotionDaoImpl")
	public void setBaseDao(PromotionDao promotionDao) {
		super.setBaseDao(promotionDao);
	}

	@Transactional(readOnly = true)
	public List<Promotion> findList(Type type, Boolean hasBegun, Boolean hasEnded, Area area, Integer count, List<Filter> filters, List<Order> orders) {
		return promotionDao.findList(type, hasBegun, hasEnded, area, count, filters, orders);
	}

	@Transactional(readOnly = true)
	@Cacheable("promotion")
	public List<Promotion> findList(Type type, Boolean hasBegun, Boolean hasEnded, Area area, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion) {
		return promotionDao.findList(type, hasBegun, hasEnded, area, count, filters, orders);
	}

	@Transactional
	@CacheEvict(value = { "promotion", "product" }, allEntries = true)
	public void save(Promotion promotion) {
		super.save(promotion);
	}

	@Transactional
	@CacheEvict(value = { "promotion", "product" }, allEntries = true)
	public Promotion update(Promotion promotion) {
		return super.update(promotion);
	}

	@Transactional
	@CacheEvict(value = { "promotion", "product" }, allEntries = true)
	public Promotion update(Promotion promotion, String... ignoreProperties) {
		return super.update(promotion, ignoreProperties);
	}

	@Transactional
	@CacheEvict(value = { "promotion", "product" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Transactional
	@CacheEvict(value = { "promotion", "product" }, allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Transactional
	@CacheEvict(value = { "promotion", "product" }, allEntries = true)
	public void delete(Promotion promotion) {
		super.delete(promotion);
	}

	public Page<PromotionProduct> findPage(Type type, Promotion promotion, Area area, TimeRegion region, List<net.wit.entity.Promotion.Status> status, Classify classify, Boolean periferal, Community community, Location location, BigDecimal distatce,
			ProductCategory productCategory, Pageable pageable) {
		return promotionProductDao.findPage(type, promotion, area, region, status, classify, periferal, community, location, distatce, productCategory, pageable);
	}

	public Page<PromotionProduct> findPageNormal(Type type, Promotion promotion, Area area, TimeRegion region, List<net.wit.entity.Promotion.Status> status, Classify classify, Boolean periferal, Community community, Location location, BigDecimal distatce,
			ProductCategory productCategory, Pageable pageable) {
		return promotionProductDao.findPageNormal(type, promotion, area, region, status, classify, periferal, community, location, distatce, productCategory, pageable);
	}

	public Page<PromotionProduct> findPage(Type type, Promotion promotion, Area area, TimeRegion region, List<net.wit.entity.Promotion.Status> status, Classify classify, Boolean periferal, Community community, Pageable pageable) {
		return promotionProductDao.findPage(type, promotion, area, region, status, classify, periferal, community, pageable);
	}

	public Page<Promotion> findPage(Type type, Area area, Boolean hasBegun, Boolean hasEnded, List<Status> status, Classify classify, Boolean periferal, Community community, Location location, BigDecimal distance, ProductCategory productCategory,
			Pageable pageable) {
		return promotionDao.findPage(type, area, hasBegun, hasEnded, status, classify, periferal, community, location, distance, productCategory, pageable);
	}
}