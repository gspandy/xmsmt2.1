/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.Date;
import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Member;
import net.wit.entity.Order;
import net.wit.entity.Trade;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.Order.ShippingStatus;

/**
 * Dao - 子订单
 * @author rsico Team
 * @version 3.0
 */
public interface TradeDao extends BaseDao<Trade, Long> {

	/**
	 * 验证码查找
	 * @param sn- 验证码
	 * @return 子订单
	 */
	Trade findBySn(String sn);

	/**
	 * 查找订单分页
	 * @param orderStatus 订单状态
	 * @param paymentStatus 支付状态
	 * @param shippingStatus 配送状态
	 * @param hasExpired 是否已过期
	 * @param pageable 分页信息
	 * @return 商品分页
	 */
	Page<Trade> findPage(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable);

	/**
	 * 查找订单分页
	 * @param member 订单状态
	 * @param listType 支付状态
	 * @param beginDate 配送状态
	 * @param endDate 是否已过期
	 * @param pageable 分页信息
	 * @return 商品分页
	 */
	public Page<Trade> findPage(Member member, Date beginDate, Date endDate, Pageable pageable);

	/**
	 * 
	 * @param member	会员
	 * @param pageable	分页信息
	 * @return
	 */
	Page<Trade> findPage(Member member, Pageable pageable);
	
	long countTenant(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired);

	List<Trade> findByOrder(Order order);
}