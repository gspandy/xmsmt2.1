/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Attribute;
import net.wit.entity.Brand;
import net.wit.entity.Goods;
import net.wit.entity.Product;
import net.wit.entity.ProductCategory;
import net.wit.entity.ProductCategoryTenant;
import net.wit.entity.Promotion;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.entity.Product.OrderType;

/**
 * Service - 货品
 * @author rsico Team
 * @version 3.0
 */
public interface GoodsService extends BaseService<Goods, Long> {

	/**
	 * @Title：findMyPage @Description：店铺经营商品
	 * @param tenant 商家
	 * @param productCategory 平台商品分类
	 * @param productCategoryTenan 商家自定义分类
	 * @param brand 品牌
	 * @param promotion 促销
	 * @param tags 标签
	 * @param attributeValue 属性
	 * @param startPrice 最低价
	 * @param endPrice 最高价
	 * @param isMarketable 是否上架
	 * @param isList 是否列出
	 * @param isTop 是否置顶
	 * @param isGift 是否为赠品
	 * @param isOutOfStock 是否超出库存
	 * @param isStockAlert 库存警告
	 * @param orderType 排序类型
	 * @param pageable 分页信息
	 * @return Page<Product>
	 */
	Page<Product> findMyPage(Tenant tenant, ProductCategory productCategory, ProductCategoryTenant productCategoryTenant, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice,
			Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType, Pageable pageable);

}