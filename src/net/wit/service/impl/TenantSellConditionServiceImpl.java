/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantSellConditionServiceImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月8日
 */
package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.wit.dao.TenantSellConditionDao;
import net.wit.dao.TicketSetDao;
import net.wit.entity.TenantSellCondition;
import net.wit.entity.TicketSet;
import net.wit.service.TenantSellConditionService;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月8日
 */
@Service("tenantSellConditionServiceImpl")
public class TenantSellConditionServiceImpl extends BaseServiceImpl<TenantSellCondition, Long> implements TenantSellConditionService{
	@Resource(name = "tenantSellConditionDaoImpl")
	private TenantSellConditionDao tenantSellConditionDaoImpl;

	@Resource(name = "tenantSellConditionDaoImpl")
	public void setBaseDao(TenantSellConditionDao tenantSellConditionDaoImpl) {
		super.setBaseDao(tenantSellConditionDaoImpl);
	}

	@Override
	public TenantSellCondition getRegularTenantSellConditionByTenantId(
			Long tenantId) {
		List<TenantSellCondition> list = tenantSellConditionDaoImpl.getTenantSellConditionByTenantId(tenantId);
		if(list.size()!=0){
			return list.get(0);
		}
		return new TenantSellCondition();
	}
}
