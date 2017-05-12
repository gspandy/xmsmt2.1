package net.wit.service;

import java.util.List;

import net.wit.entity.TenantTicket;

/**
 * @ClassName：TicketService
 * @Description：
 * @author：yangjj
 * @date：2015年9月11日 下午6:22:12 
 */
public interface TenantTicketService extends BaseService<TenantTicket,Long> {
	
	public TenantTicket findTenantTicketByTenantId(Long tanentId);
	
}
