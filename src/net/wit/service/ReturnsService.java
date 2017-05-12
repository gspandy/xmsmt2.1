/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Order;
import net.wit.entity.Returns;
import net.wit.entity.Returns.ReturnStatus;
import net.wit.entity.Tenant;
import net.wit.exception.OrderException;
import net.wit.support.ReturnItemVo;

/**
 * Service - 退货单
 * @author rsico Team
 * @version 3.0
 */
public interface ReturnsService extends BaseService<Returns, Long> {

	/**
	 * @Title：findPage @Description：
	 * @param tenant
	 * @param pageable
	 * @return Object
	 */
	Page<Returns> findPage(Tenant tenant, ReturnStatus returnStatus, Pageable pageable);

	/**
	 * @Title：reject @Description：
	 * @param returns void
	 */
	void reject(Returns returns);

	/**
	 * @Title：agreeReturn @Description：
	 * @param returns void
	 */
	void agreeReturn(Returns returns) throws OrderException;

	/**
	 * 
	 * @Title：apply
	 * @Description：申请退货
	 * @param returnVos 
	 * @param order 订单
	 * @param memo 备注
	 * @param reason 退货理由
	 * @param img1 图片1
	 * @param img2 图片2
	 * @param img3 图片3
	 * @return
	 * @throws OrderException  Returns 退货单信息（包含 商家收货信息)
	 */
	Returns apply(List<ReturnItemVo> returnVos, Order order, String memo, String reason, String img1, String img2, String img3) throws OrderException;

	/**
	 * 
	 * @Title：edit
	 * @Description：填写物流单号
	 * @param returns 退货单
	 * @param trackingNo 运单号
	 * @throws OrderException  void
	 */
	void edit(Returns returns, String trackingNo) throws OrderException;

	/**
	 * 
	 * @Title：returns
	 * @Description： 卖家收到货 退货
	 * @param returns 退货单
	 * @param username 操作人
	 * @throws OrderException  void
	 */
	void returns(Returns returns, String username) throws OrderException;

}