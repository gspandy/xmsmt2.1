/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: OrderStatusConfigDao.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月18日
 */
package net.wit.dao;

import java.util.List;

import net.wit.entity.OrderStatusConfig;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月18日
 */
public interface OrderStatusDao extends BaseDao<OrderStatusConfig, Long>{
	
	 List<OrderStatusConfig> findList(String orderStatus, String paymentStatus,String shippingStatus);

}
