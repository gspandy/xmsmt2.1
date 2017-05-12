/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSetDao.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.dao;

import java.util.List;

import net.wit.entity.TicketSet;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月7日
 */
public interface TicketSetDao extends BaseDao<TicketSet, Long>{

	public List<TicketSet> getTicketSetByTenantId(Long tenantId);
	
	public List<TicketSet> getTicketSet(Long tenantId,String sendType,String useFlag);
}
