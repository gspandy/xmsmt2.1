/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Attribute;
import net.wit.entity.Brand;
import net.wit.entity.Goods;
import net.wit.entity.Product.OrderType;
import net.wit.entity.ProductCategory;
import net.wit.entity.ProductCategoryTenant;
import net.wit.entity.Promotion;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;

/**
 * Dao - 货品
 * @author rsico Team
 * @version 3.0
 */
public interface GoodsDao extends BaseDao<Goods, Long> {

	/**
	 * @Title：findMyPage @Description：
	 * @param tenant
	 * @param productCategory
	 * @param productCategoryTenant
	 * @param brand
	 * @param promotion
	 * @param tags
	 * @param attributeValue
	 * @param startPrice
	 * @param endPrice
	 * @param isMarketable
	 * @param isList
	 * @param isTop
	 * @param isGift
	 * @param isOutOfStock
	 * @param isStockAlert
	 * @param orderType
	 * @param pageable
	 * @return Page<Goods>
	 */
	Page<Goods> findMyPage(Tenant tenant, ProductCategory productCategory, ProductCategoryTenant productCategoryTenant, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice,
			Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType, Pageable pageable);

}