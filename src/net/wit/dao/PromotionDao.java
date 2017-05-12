/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

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
import net.wit.entity.Promotion.Classify;
import net.wit.entity.Promotion.Status;
import net.wit.entity.Promotion.Type;

/**
 * Dao - 促销
 * @author rsico Team
 * @version 3.0
 */
public interface PromotionDao extends BaseDao<Promotion, Long> {

	/**
	 * 查找促销
	 * @param type 
	 * @param hasBegun 是否已开始
	 * @param hasEnded 是否已结束
	 * @param area 
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @return 促销
	 */
	List<Promotion> findList(Type type, Boolean hasBegun, Boolean hasEnded, Area area, Integer count, List<Filter> filters, List<Order> orders);
	Page<Promotion> findPage(Type type, Area area, Boolean hasBegun, Boolean hasEnded, List<Status> status, Classify classify, Boolean periferal, Community community,Location location, BigDecimal distance, ProductCategory productCategory, Pageable pageable);

}