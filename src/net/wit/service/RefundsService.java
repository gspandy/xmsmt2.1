/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Order;
import net.wit.entity.Refunds;
import net.wit.entity.Tenant;
import net.wit.exception.OrderException;

/**
 * Service - 退款单
 * @author rsico Team
 * @version 3.0
 */
public interface RefundsService extends BaseService<Refunds, Long> {

	/**
	 * @Title：findPage @Description：
	 * @param tenant
	 * @param pageable
	 * @return Object
	 */
	Page<Refunds> findPage(Tenant tenant, Pageable pageable);

	/**
	 * @Title：apply 
	 * @Description： 退款申请
	 * @param order
	 * @throws OrderException void
	 */
	void apply(Order order, String memo) throws OrderException;

	/**
	 * @Title：reject @Description： 拒绝
	 * @param fRefund void
	 * @param string
	 */
	void reject(Refunds fRefund, String username, String operateMemo) throws OrderException;

	/**
	 * @Title：agree @Description：
	 * @param fRefund 同意
	 * @param operateMemo
	 * @param currentUsername void
	 */
	void agree(Refunds fRefund, String operateMemo, String currentUsername) throws OrderException;

	/**
	 * @Title：agree @Description：
	 * @param fRefund 取消
	 * @param operateMemo
	 * @param currentUsername void
	 */
	void cancel(Refunds fRefund, String operateMemo, String currentUsername) throws OrderException;

	/**
	 * 
	 * @Title：refurns
	 * @Description：打款
	 * @param refunds 退款单
	 * @param username 操作人
	 * @throws OrderException  void
	 */
	void refurns(Refunds refunds,String username) throws OrderException;

}