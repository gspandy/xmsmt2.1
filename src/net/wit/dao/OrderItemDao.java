/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.List;

import net.wit.entity.Order;
import net.wit.entity.OrderItem;

/**
 * Dao - 订单项
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface OrderItemDao extends BaseDao<OrderItem, Long> {

	List<OrderItem> findByOrder(Order order);

}