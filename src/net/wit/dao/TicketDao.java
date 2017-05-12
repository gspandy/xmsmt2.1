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
import net.wit.entity.Ticket;


/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月8日
 */
public interface TicketDao extends BaseDao<Ticket, Long>{

	public List<Ticket> getTicketListByMemberId(Long memberId,int pageSize,int pageNo);
	
	public boolean isExistTicket(Long memberId);
	
	public Long countTicketByMemberId(Long memberId,Long tenantId,Ticket.Status status);
	
	public List<Ticket> countTicketByMemberIdAndShopKeeperId(Long memberId,Long shopKeeperId,Long tenantId,Ticket.Status status);
	
	public List<Ticket> getTicketByShopkeeperId(Long shopkeeperId,Ticket.Status status) ;
	
	public List<Ticket> getTicketByTenant(Long memberId,Long tenantId,Ticket.Status status);

	public List<Ticket> queryTenantByTickets(Member member);

	public boolean updateTicketInvalid(Member shopkeeper);
	
	/**条件分页查询
	 * @param member	会员
	 * @param ticketStatusParam   内购券状态
	 * @param ticketModifyDate	内购券领取时间 
	 * @param pageable
	 * @return
	 */
	public Page<Ticket> findPage(Long tenantId,Member member, String ticketStatusParam,  Date firstDayOfMonth, Date lastDayOfMonth, Pageable pageable);
	
	/**条件查询
	 * @param member	会员
	 * @param ticketStatusParam   内购券状态
	 * @param ticketModifyDate	内购券领取时间 
	 * @return
	 */
	public List<Ticket> findList(Long tenantId,Member member, String ticketStatusParam,  Date firstDayOfMonth, Date lastDayOfMonth);
}
