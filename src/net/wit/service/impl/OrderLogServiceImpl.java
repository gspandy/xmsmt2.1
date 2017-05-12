/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import javax.annotation.Resource;

import net.wit.dao.OrderLogDao;
import net.wit.entity.OrderLog;
import net.wit.service.OrderLogService;

import org.springframework.stereotype.Service;

/**
 * Service - 订单日志
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("orderLogServiceImpl")
public class OrderLogServiceImpl extends BaseServiceImpl<OrderLog, Long> implements OrderLogService {

	@Resource(name = "orderLogDaoImpl")
	public void setBaseDao(OrderLogDao orderLogDao) {
		super.setBaseDao(orderLogDao);
	}

}