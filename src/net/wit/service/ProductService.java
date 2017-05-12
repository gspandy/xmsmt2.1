/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.wit.Filter;
import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Area;
import net.wit.entity.Attribute;
import net.wit.entity.Brand;
import net.wit.entity.Community;
import net.wit.entity.Location;
import net.wit.entity.Member;
import net.wit.entity.Product;
import net.wit.entity.Product.OrderType;
import net.wit.entity.ProductCategory;
import net.wit.entity.ProductCategoryTenant;
import net.wit.entity.Promotion;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;

/**
 * Service - 商品
 * @author rsico Team
 * @version 3.0
 */
public interface ProductService extends BaseService<Product, Long> {

	/**
	 * 判断商品编号是否存在
	 * @param sn 商品编号(忽略大小写)
	 * @return 商品编号是否存在
	 */
	boolean snExists(String sn);

	/**
	 * 根据商品编号查找商品
	 * @param sn 商品编号(忽略大小写)
	 * @return 商品，若不存在则返回null
	 */
	Product findBySn(String sn);

	/**
	 * 判断商品编号是否唯一
	 * @param previousSn 修改前商品编号(忽略大小写)
	 * @param currentSn 当前商品编号(忽略大小写)
	 * @return 商品编号是否唯一
	 */
	boolean snUnique(String previousSn, String currentSn);

	/**
	 * 通过ID、编号、全称查找商品
	 * @param keyword 关键词
	 * @param isGift 是否为赠品
	 * @param count 数量
	 * @return 商品
	 */
	List<Product> search(String keyword, Boolean isGift, Integer count);

	/**
	 * 查找商品
	 * @param keyword 关键词
	 * @param phonetic 拼音
	 * @param orderType 排序类型
	 * @return 商品
	 */
	Page<Product> search(String keyword, String phonetic, BigDecimal startPrice, BigDecimal endPrice, OrderType orderType, Pageable pageable);

	/**
	 * 查找商品
	 * @param productCategory 商品分类
	 * @param brand 品牌
	 * @param promotion 促销
	 * @param tags 标签
	 * @param attributeValue 属性值
	 * @param startPrice 最低价格
	 * @param endPrice 最高价格
	 * @param isMarketable 是否上架
	 * @param isList 是否列出
	 * @param isTop 是否置顶
	 * @param isGift 是否为赠品
	 * @param isOutOfStock 是否缺货
	 * @param isStockAlert 是否库存警告
	 * @param orderType 排序类型
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @return 商品
	 */
	List<Product> findList(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType, Integer count, List<Filter> filters, List<Order> orders);

	/**
	 * 查找商品
	 * @param productCategories 商品分类
	 * @param brand 品牌
	 * @param promotion 促销
	 * @param tags 标签
	 * @param attributeValue 属性值
	 * @param startPrice 最低价格
	 * @param endPrice 最高价格
	 * @param isMarketable 是否上架
	 * @param isList 是否列出
	 * @param isTop 是否置顶
	 * @param isGift 是否为赠品
	 * @param isOutOfStock 是否缺货
	 * @param isStockAlert 所属地区
	 * @param area 小区
	 * @param community 是否查询周边
	 * @param tenant 企业
	 * @param periferal 是否库存警告
	 * @param orderType 排序类型
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @return 商品
	 */
	List<Product> findList(Set<ProductCategory> productCategories, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Tenant tenant, Area area, Boolean periferal, OrderType orderType, Integer count, List<Filter> filters, List<Order> orders);
	/**
	 * 查找一列出商品
	 * @param tenant
	 * @param isList
	 * @return
	 */
	List<Product> findList(Tenant tenant, Boolean isList);

	/**
	 * 查找商品(缓存)
	 * @param productCategory 商品分类
	 * @param brand 品牌
	 * @param promotion 促销
	 * @param tags 标签
	 * @param attributeValue 属性值
	 * @param startPrice 最低价格
	 * @param endPrice 最高价格
	 * @param isMarketable 是否上架
	 * @param isList 是否列出
	 * @param isTop 是否置顶
	 * @param isGift 是否为赠品
	 * @param isOutOfStock 是否缺货
	 * @param isStockAlert 是否库存警告
	 * @param orderType 排序类型
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @return 商品(缓存)
	 */
	List<Product> findList(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion);

	/**
	 * 查找商品(缓存)
	 * @param productCategories 商品分类
	 * @param brand 品牌
	 * @param promotion 促销
	 * @param tags 标签
	 * @param attributeValue 属性值
	 * @param startPrice 最低价格
	 * @param endPrice 最高价格
	 * @param isMarketable 是否上架
	 * @param isList 是否列出
	 * @param isTop 是否置顶
	 * @param isGift 是否为赠品
	 * @param isOutOfStock 是否缺货
	 * @param isStockAlert 是否库存警告
	 * @param area 所属地区
	 * @param community 小区
	 * @param tenant 企业
	 * @param periferal 是否查询周边
	 * @param orderType 排序类型
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @return 商品(缓存)
	 */
	List<Product> findList(Set<ProductCategory> productCategories, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Tenant tenant, Area area, Boolean periferal, OrderType orderType, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion);

	/**
	 * 查找已上架商品
	 * @param productCategory 商品分类
	 * @param beginDate 起始日期
	 * @param endDate 结束日期
	 * @param first 起始记录
	 * @param count 数量
	 * @return 已上架商品
	 */
	List<Product> findList(ProductCategory productCategory, Date beginDate, Date endDate, Integer first, Integer count);

	/**
	 * 查找商品销售信息
	 * @param beginDate 起始日期
	 * @param endDate 结束日期
	 * @param count 数量
	 * @return 商品销售信息
	 */
	List<Object[]> findSalesList(Date beginDate, Date endDate, Integer count);

	/**
	 * 查找商品分页
	 * @param productCategory 商品分类
	 * @param brand 品牌
	 * @param promotion 促销
	 * @param tags 标签
	 * @param attributeValue 属性值
	 * @param startPrice 最低价格
	 * @param endPrice 最高价格
	 * @param isMarketable 是否上架
	 * @param isList 是否列出
	 * @param isTop 是否置顶
	 * @param isGift 是否为赠品
	 * @param isOutOfStock 是否缺货
	 * @param isStockAlert 是否库存警告
	 * @param orderType 排序类型
	 * @param pageable 分页信息
	 * @return 商品分页
	 */
	Page<Product> findPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, Location location, BigDecimal distatce, OrderType orderType, Pageable pageable);

	Page<Product> findPage(Long tenantId, String searchValue, Pageable pageable);

	Page<Product> mobileFindPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, Location location, BigDecimal distatce, OrderType orderType, Pageable pageable);

	/**
	 * 查找商品分页
	 * @param productCategory 商品分类
	 * @param brand 品牌
	 * @param promotion 促销
	 * @param tags 标签
	 * @param attributeValue 属性值
	 * @param startPrice 最低价格
	 * @param endPrice 最高价格
	 * @param isMarketable 是否上架
	 * @param isList 是否列出
	 * @param isTop 是否置顶
	 * @param isGift 是否为赠品
	 * @param isOutOfStock 是否缺货
	 * @param isStockAlert 所属地区
	 * @param area 小区
	 * @param community 是否查询周边
	 * @param periferal 是否库存警告
	 * @param orderType 排序类型
	 * @param pageable 分页信息
	 * @return 商品分页
	 */
	Page<Product> findPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, OrderType orderType, String phonetic, String keyword, Pageable pageable);

	/**
	 * 查找商品分页
	 * @param productCategory 商品分类
	 * @param brand 品牌
	 * @param promotion 促销
	 * @param tags 标签
	 * @param attributeValue 属性值
	 * @param startPrice 最低价格
	 * @param endPrice 最高价格
	 * @param isMarketable 是否上架
	 * @param isList 是否列出
	 * @param isTop 是否置顶
	 * @param isGift 是否为赠品
	 * @param isOutOfStock 是否缺货
	 * @param isStockAlert 所属地区
	 * @param area 小区
	 * @param community 是否查询周边
	 * @param periferal 是否库存警告
	 * @param orderType 排序类型
	 * @param pageable 分页信息
	 * @return 商品分页
	 */
	Page<Product> searchPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, OrderType orderType, String phonetic, String keyword, Pageable pageable);

	Page<Product> searchPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, OrderType orderType, String phonetic, String keyword, Location location, BigDecimal distatce, Pageable pageable);

	/**
	 * 查找商品分页
	 * @param productCategory 商品分类
	 * @param brand 品牌
	 * @param promotion 促销
	 * @param tags 标签
	 * @param attributeValue 属性值
	 * @param startPrice 最低价格
	 * @param endPrice 最高价格
	 * @param isMarketable 是否上架
	 * @param isList 是否列出
	 * @param isTop 是否置顶
	 * @param isGift 是否为赠品
	 * @param isOutOfStock 是否缺货
	 * @param isStockAlert 所属地区
	 * @param area 小区
	 * @param community 是否查询周边
	 * @param periferal 是否库存警告
	 * @param orderType 排序类型
	 * @param pageable 分页信息
	 * @return 商品分页
	 */
	public Page<Product> findPageByChannel(Set<ProductCategory> productCategorys, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, OrderType orderType, String phonetic, String keyword, Pageable pageable);

	/**
	 * 查找收藏商品分页
	 * @param member 会员
	 * @param pageable 分页信息
	 * @return 收藏商品分页
	 */
	Page<Product> findPage(Member member, Pageable pageable);

	/**
	 * 查找收藏商品分页(过滤条件:商品分类、品牌)
	 * @param member 会员
	 * @param pageable 分页信息
	 * @return 收藏商品分页
	 */
	Page<Product> findPage(Member member, ProductCategory productCategory, Brand brand, Pageable pageable);

	/**
	 * 查找近多少天内购买的商品
	 * @param member 会员
	 * @param days 天数
	 * @param pageable 分页信息
	 * @return 商品分页
	 */
	Page<Product> findPage(Member member, Integer days, ProductCategory productCategory, Pageable pageable);

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

	/**
	 * 查询商品数量
	 * @param tenant
	 * @param favoriteMember 收藏会员
	 * @param isMarketable 是否上架
	 * @param isList 是否列出
	 * @param isTop 是否置顶
	 * @param isGift 是否为赠品
	 * @param isOutOfStock 是否缺货
	 * @param isStockAlert 是否库存警告
	 * @return 商品数量
	 */
	Long count(Tenant tenant, Member favoriteMember, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert);

	/**
	 * 判断会员是否已购买该商品
	 * @param member 会员
	 * @param product 商品
	 * @return 是否已购买该商品
	 */
	boolean isPurchased(Member member, Product product);

	/**
	 * 查看并更新点击数
	 * @param id ID
	 * @return 点击数
	 */
	long viewHits(Long id);

	Page<Product> mobileSearchPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, OrderType orderType, String phonetic, String keyword, Location location, BigDecimal distatce, Pageable pageable);

	List<Product> findList(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Tenant tenant, Area area, Boolean periferal, OrderType orderType, Integer count, List<Filter> filters, List<Order> orders);

	Page<Product> findPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType, Pageable pageable);

	Page<Product> searchPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, List<Tag> unionTags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, OrderType orderType, String phonetic, String keyword, Pageable pageable);

	Page<Product> findUnionPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, List<Tag> unionTags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, Location location, BigDecimal distatce, String phonetic, String keyword, OrderType orderType, Pageable pageable);

	List<Product> productTenantSelect(String q, Long tenantId, Boolean b, int i);

	Page<Object[]> findSalesPage(Pageable pageable,Date beginDate,Date endDate,Tenant tenant);

}