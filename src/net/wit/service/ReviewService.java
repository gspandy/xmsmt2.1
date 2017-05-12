/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import java.util.List;

import net.wit.Filter;
import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Member;
import net.wit.entity.Product;
import net.wit.entity.Review;
import net.wit.entity.Tenant;
import net.wit.entity.Review.Type;
import net.wit.entity.Trade;

/**
 * Service - 评论
 * @author rsico Team
 * @version 3.0
 */
public interface ReviewService extends BaseService<Review, Long> {

	/**
	 * 查找评论
	 * @param member 会员
	 * @param product 商品
	 * @param type 类型
	 * @param isShow 是否显示
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @return 评论
	 */
	public List<Review> findList(Member member, Product product, Type type, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders);

	/**
	 * 查找评论(缓存)
	 * @param member 会员
	 * @param product 商品
	 * @param type 类型
	 * @param isShow 是否显示
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @param cacheRegion 缓存区域
	 * @return 评论(缓存)
	 */
	public List<Review> findList(Member member, Product product, Type type, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion);

	/**
	 * 查找评论分页
	 * @param member 会员
	 * @param product 商品
	 * @param type 类型
	 * @param isShow 是否显示
	 * @param pageable 分页信息
	 * @return 评论分页
	 */
	public Page<Review> findPage(Member member, Product product, Type type, Boolean isShow, Pageable pageable);

	/**
	 * 查找评论分页
	 * @param tenant 商家
	 * @param type 类型
	 * @param isShow 是否显示
	 * @param pageable 分页信息
	 * @return 评论分页
	 */
	public Page<Review> findTenantPage(Tenant tenant, Type type, Boolean isShow, Pageable pageable);

	/**
	 * 查找评论分页
	 * @param member 会员
	 * @param product 商品
	 * @param type 类型
	 * @param isShow 是否显示
	 * @param pageable 分页信息
	 * @return 评论分页
	 */
	public Page<Review> findMyPage(Member member, Product product, Type type, Boolean isShow, Pageable pageable);

	/**
	 * 查找评论数量
	 * @param member 会员
	 * @param product 商品
	 * @param type 类型
	 * @param isShow 是否显示
	 * @return 评论数量
	 */
	public Long count(Member member, Product product, Type type, Boolean isShow);

	/**
	 * 判断会员是否已评论该商品
	 * @param member 会员
	 * @param product 商品
	 * @return 是否已评论该商品
	 */
	public boolean isReviewed(Member member, Product product);

	/**
	 * 商家评价买家
	 * @param trade 子订单
	 * @param review 评价
	 */
	public void tenantReview(Trade trade, Review review);
}