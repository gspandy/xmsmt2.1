/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantSellConditionDao.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月8日
 */
package net.wit.dao;


import java.util.List;

import net.wit.entity.TenantTicket;



/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月8日
 */
public interface TenantTicketDao extends BaseDao<TenantTicket, Long>{

	 public TenantTicket findByTanentId(Long tanentId);

	 public List<TenantTicket> getTenantTicketByTenantId(Long tenantId);
	 
}
