/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import java.math.BigDecimal;
import java.util.List;

import net.wit.Filter;
import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Area;
import net.wit.entity.Community;
import net.wit.entity.Location;
import net.wit.entity.ProductCategory;
import net.wit.entity.Promotion;
import net.wit.entity.PromotionMember;
import net.wit.entity.PromotionProduct;
import net.wit.entity.Promotion.Classify;
import net.wit.entity.Promotion.Status;
import net.wit.entity.Promotion.Type;
import net.wit.entity.PromotionProduct.TimeRegion;

/**
 * Service - 促销
 * @author rsico Team
 * @version 3.0
 */
public interface PromotionService extends BaseService<Promotion, Long> {

	public PromotionMember findPromotionMember(Long id);

	public void updatePromotionMember(PromotionMember promotionMember);

	/**
	 * 查找促销
	 * @param hasBegun 是否已开始
	 * @param hasEnded 是否已结束
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @return 促销
	 */
	List<Promotion> findList(Type type, Boolean hasBegun, Boolean hasEnded, Area area, Integer count, List<Filter> filters, List<Order> orders);

	/**
	 * 查找促销(缓存)
	 * @param type
	 * @param hasBegun 是否已开始
	 * @param hasEnded 是否已结束
	 * @param area
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @param cacheRegion 缓存区域
	 * @return 促销(缓存)
	 */
	List<Promotion> findList(Type type, Boolean hasBegun, Boolean hasEnded, Area area, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion);

	public Page<PromotionProduct> findPage(Type type, Promotion promotion, Area area, TimeRegion region, List<net.wit.entity.Promotion.Status> status, Classify classify, Boolean periferal, Community community, Location location, BigDecimal distatce,
			ProductCategory productCategory, Pageable pageable);

	public Page<PromotionProduct> findPageNormal(Type type, Promotion promotion, Area area, TimeRegion region, List<net.wit.entity.Promotion.Status> status, Classify classify, Boolean periferal, Community community, Location location, BigDecimal distatce,
			ProductCategory productCategory, Pageable pageable);

	public Page<PromotionProduct> findPage(Type type, Promotion promotion, Area area, TimeRegion region, List<net.wit.entity.Promotion.Status> status, Classify classify, Boolean periferal, Community community, Pageable pageable);

	public Page<Promotion> findPage(Type type, Area area, Boolean hasBegun, Boolean hasEnded, List<Status> status, Classify classify, Boolean periferal, Community community, Location location, BigDecimal distance, ProductCategory productCategory,
			Pageable pageable);
}