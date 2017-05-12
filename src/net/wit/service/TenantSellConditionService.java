/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantSellConditionService.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月8日
 */
package net.wit.service;

import net.wit.entity.TenantSellCondition;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月8日
 */
public interface TenantSellConditionService extends BaseService<TenantSellCondition, Long>{
	public TenantSellCondition getRegularTenantSellConditionByTenantId(Long tenantId);
}
