package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.dao.TenantTicketDao;
import net.wit.entity.TenantTicket;
import net.wit.service.TenantTicketService;

import org.springframework.stereotype.Service;

/**
 * @ClassName：TicketServiceImpl @Description：
 * @author：Chenlf
 * @date：2015年9月11日 下午6:22:39
 */
@Service("tenantTicketServiceImpl")
public class TenantTicketServiceImpl extends BaseServiceImpl<TenantTicket, Long>implements TenantTicketService {

	@Resource(name = "tenantTicketDaoImpl")
	private TenantTicketDao tenantTicketDao;
	
	@Resource(name = "tenantTicketDaoImpl")
	public void setBaseDao(TenantTicketDao tenantTicketDao) {
		super.setBaseDao(tenantTicketDao);
	}
	
	public TenantTicket findTenantTicketByTenantId(Long tanentId){
		TenantTicket tenantTicket=tenantTicketDao.findByTanentId(tanentId);
		if(tenantTicket==null)
		{
			return new TenantTicket();
		}
		return tenantTicket;
	}
	
}
