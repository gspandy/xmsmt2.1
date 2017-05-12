/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantSellConditionDao.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月8日
 */
package net.wit.dao;

import java.util.Date;
import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Member;
import net.wit.entity.TicketCache;


/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月8日
 */
public interface TicketCacheDao extends BaseDao<TicketCache, Long>{

	public List<TicketCache> getTicketCacheByTenantId(Long memberId,String receiveStatus);
	
	public Long getshopkeeperNoUseCount(Long memberId,String receiveStatus);

	public boolean updateTicketCache(Long memberId);

	public List<TicketCache> getTicketCacheByMember(Long memberId);
/**
 * 页面查询ticketCache
 * @param tenantId
 * @param startDate
 * @param endDate
 * @param sendModel
 * @param name
 * @param pageable
 * @return
 */
	public Page<TicketCache> findTickeCacheByPage(Long tenantId, Date startDate,
			Date endDate, String sendModel, List<Member> members, Pageable pageable);
	/**
	 * 页面查询ticketCache
	 * @param tenantId
	 * @param startDate
	 * @param endDate
	 * @param sendModel
	 * @param name
	 * @param pageable
	 * @return
	 */
	public List<TicketCache> findTickeCacheByList(Long tenantId, Date startDate,
			Date endDate, String sendModel, List<Member> members);
}
