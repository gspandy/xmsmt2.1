/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Coupon;
import net.wit.entity.Order;

/**
 * Dao - 优惠券
 * @author rsico Team
 * @version 3.0
 */
public interface CouponDao extends BaseDao<Coupon, Long> {

	/**
	 * 查找优惠券分页
	 * @param isEnabled 是否启用
	 * @param isExchange 是否允许积分兑换
	 * @param hasExpired 是否已过期
	 * @param pageable 分页信息
	 * @return 优惠券分页
	 */
	public Page<Coupon> findPage(Boolean isEnabled, Boolean isExchange, Boolean hasExpired, Pageable pageable);

	/** 获取系统内置的积分抵扣券 */
	public Coupon findSystemPointExchange();

	List<Coupon> findByOrder(List<Order> order);

}