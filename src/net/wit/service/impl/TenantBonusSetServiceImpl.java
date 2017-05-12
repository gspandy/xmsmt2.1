/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantBonusSetServiceImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月8日
 */
package net.wit.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.wit.dao.TenantBonusSetDao;
import net.wit.dao.TenantSellConditionDao;
import net.wit.entity.TenantBonusSet;
import net.wit.entity.TenantSellCondition;
import net.wit.entity.VipLevel;
import net.wit.service.TenantBonusSetService;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月8日
 */
@Service("tenantBonusSetServiceImpl")
public class TenantBonusSetServiceImpl extends BaseServiceImpl<TenantBonusSet, Long> implements TenantBonusSetService{
	@Resource(name = "tenantBonusSetDaoImpl")
	private TenantBonusSetDao tenantBonusSetDaoImpl;

	@Resource(name = "tenantBonusSetDaoImpl")
	public void setBaseDao(TenantBonusSetDao tenantBonusSetDaoImpl) {
		super.setBaseDao(tenantBonusSetDaoImpl);
	}

	@Override
	public TenantBonusSet getRegularTenantBonusSetByTenantId(Long tenantId) {
		List<TenantBonusSet> list = tenantBonusSetDaoImpl.getTenantBonusSetByTenantId(tenantId);
		if(list.size()!=0){
			return list.get(0);
		}
		return new TenantBonusSet();
	}
	@Override
	public String getBonusPercent(Long tenantId){  
		   String result="";//接受百分比的值  
		   TenantBonusSet tenantBonusSet = this.getRegularTenantBonusSetByTenantId(tenantId);
		   double x_double=tenantBonusSet.getRelativeSellBonusRate(); 
		   result = x_double+"%";
		   return result;  
		}

	@Override
	public String getBonusPercent(VipLevel vipLevel){  
		   String result="";//接受百分比的值  
		   double x_double=vipLevel.getBonusLevel();
		   result = x_double+"%";
		   return result;  
		}
	@Override
	public BigDecimal calcBonus(BigDecimal amount, TenantBonusSet set) {
		BigDecimal bonus = BigDecimal.ZERO;
		BigDecimal pecent  = new BigDecimal("100");
		if(set.getTenantSellBonusRate()!=null && set.getTenantSellBonusRate()>0){
			BigDecimal rate = new BigDecimal(String.valueOf(set.getTenantSellBonusRate()));
			bonus = amount.multiply(rate).divide(pecent, 2, BigDecimal.ROUND_HALF_UP);
		}
		return bonus;
	}

	@Override
	public BigDecimal calcReBonus(BigDecimal amount, TenantBonusSet set) {
		BigDecimal bonus = BigDecimal.ZERO;
		BigDecimal pecent  = new BigDecimal("100");
		if(set.getRelativeSellBonusRate()!=null && set.getRelativeSellBonusRate()>0){
			BigDecimal rate = new BigDecimal(String.valueOf(set.getRelativeSellBonusRate()));
			bonus = amount.multiply(rate).divide(pecent, 2, BigDecimal.ROUND_HALF_UP);
		}
		return bonus;
	} 
	
	@Override
	public BigDecimal calcReBonus(BigDecimal amount, VipLevel vipLevel) {
		BigDecimal bonus = BigDecimal.ZERO;
		BigDecimal pecent  = new BigDecimal("100");
		if(vipLevel==null) return bonus;
		Integer bounsLevel = vipLevel.getBonusLevel();
		if(vipLevel.getBonusLevel()!=null && vipLevel.getBonusLevel()>0){
			BigDecimal rate = new BigDecimal(String.valueOf(bounsLevel));
			bonus = amount.multiply(rate).divide(pecent, 2, BigDecimal.ROUND_HALF_UP);
		}
		return bonus;
	}  
}
