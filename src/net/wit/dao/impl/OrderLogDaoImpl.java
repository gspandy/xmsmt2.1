/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import net.wit.dao.OrderLogDao;
import net.wit.entity.OrderLog;

import org.springframework.stereotype.Repository;

/**
 * Dao - 订单日志
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("orderLogDaoImpl")
public class OrderLogDaoImpl extends BaseDaoImpl<OrderLog, Long> implements OrderLogDao {

}