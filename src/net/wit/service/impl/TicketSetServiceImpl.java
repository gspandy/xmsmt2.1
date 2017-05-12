/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSetServiceImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.wit.dao.TenantCategoryDao;
import net.wit.dao.TicketSetDao;
import net.wit.entity.TicketCache;
import net.wit.entity.TicketSet;
import net.wit.service.TicketSetService;
import net.wit.util.TicketUtil;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月7日
 */
@Service("ticketSetServiceImpl")
public class TicketSetServiceImpl extends BaseServiceImpl<TicketSet, Long> implements TicketSetService{


	
	@Resource(name = "ticketSetDaoImpl")
	private TicketSetDao ticketSetDao;

	@Resource(name = "ticketSetDaoImpl")
	public void setBaseDao(TicketSetDao ticketSetDao) {
		super.setBaseDao(ticketSetDao);
	}
//	获取企业内购券发放设置
	private  TicketSet getTickSetByTenantId(Long tenantId ,String sendType ){
		List<TicketSet> list = ticketSetDao.getTicketSetByTenantId(tenantId);
		for (TicketSet ticketSet : list) {
				if(ticketSet.getSendType().equals(sendType)){
					return ticketSet;
				}
		}
		return null;
	}
	
	public TicketSet getNewShopKeeperTicketSetByTenantId(Long tenantId){
		return getTickSetByTenantId(tenantId,TicketSet.NEW_SEND_TYPE);
	}
	@Override
	public TicketSet getApplyTicketSetByTenantId(Long tenantId){
		return getTickSetByTenantId(tenantId,TicketSet.APPLY_SEND_TYPE);
	}
	
	public TicketSet getRegularTicketSetByTenantId(Long tenantId){
		return getTickSetByTenantId(tenantId,TicketSet.SYSTEM_SEND_TYPE);
	}
	
	public List<TicketSet> getTicketSet(Long tenantId,String sendType,String useFlag){
		return ticketSetDao.getTicketSet(tenantId, sendType, useFlag);
	}
	
	
}
