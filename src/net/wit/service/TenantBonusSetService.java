/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantBonusSetService.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月8日
 */
package net.wit.service;

import java.math.BigDecimal;

import net.wit.entity.TenantBonusSet;
import net.wit.entity.TenantSellCondition;
import net.wit.entity.VipLevel;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月8日
 */
public interface TenantBonusSetService extends BaseService<TenantBonusSet, Long>{
	public TenantBonusSet getRegularTenantBonusSetByTenantId(Long tenantId);

	public String getBonusPercent(Long tenantId);
	
	public BigDecimal calcReBonus(BigDecimal amount,TenantBonusSet set);
	
	public BigDecimal calcBonus(BigDecimal amount,TenantBonusSet set);

	/**
	 * 使用vip等级来计算奖金
	 * @param amount
	 * @param vipLevel
	 * @return
	 */
	public BigDecimal calcReBonus(BigDecimal amount, VipLevel vipLevel);
	/**
	 * 根据vip等级获取奖金比列显示；
	 * @param vipLevel
	 * @return
	 */
	public String getBonusPercent(VipLevel vipLevel);
	
}
