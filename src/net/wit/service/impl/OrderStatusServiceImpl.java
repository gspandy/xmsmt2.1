/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: OrderStatusConfigServiceImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月18日
 */
package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.dao.OrderStatusDao;
import net.wit.entity.OrderStatusConfig;
import net.wit.service.OrderStatusService;

import org.springframework.stereotype.Service;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月18日
 */
@Service("orderStatusServiceImpl")
public class OrderStatusServiceImpl extends BaseServiceImpl<OrderStatusConfig, Long>implements OrderStatusService{
	
	@Resource(name = "orderStatusDaoImpl")
	OrderStatusDao orderStatusDao;
	
	@Resource(name = "orderStatusDaoImpl")
	public void setBaseDao(OrderStatusDao orderStatusDao) {
		super.setBaseDao(orderStatusDao);
	}
	@Override
	public List<OrderStatusConfig> findList(String orderStatus,
			String paymentStatus, String shippingStatus) {
		return orderStatusDao.findList(orderStatus, paymentStatus, shippingStatus);
	}

}
