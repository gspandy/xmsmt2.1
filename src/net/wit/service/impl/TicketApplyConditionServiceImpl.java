package net.wit.service.impl;



import javax.annotation.Resource;

import net.wit.entity.Tenant;
import net.wit.entity.TicketApplyCondition;


import org.springframework.stereotype.Service;


import net.wit.dao.TicketApplyConditionDao;

import net.wit.service.TicketApplyConditionService;


/**
 * @ClassName：TicketServiceImpl @Description：
 * @author：Chenlf
 * @date：2015年9月11日 下午6:22:39
 */
@Service("ticketApplyConditionServiceImpl")
public class TicketApplyConditionServiceImpl extends BaseServiceImpl<TicketApplyCondition, Long>implements TicketApplyConditionService {


	@Resource(name = "ticketApplyConditionDaoImpl")
	private TicketApplyConditionDao ticketApplyConditionDao;
	
	@Resource(name = "ticketApplyConditionDaoImpl")
	public void setBaseDao(TicketApplyConditionDao ticketApplyConditionDao) {
		super.setBaseDao(ticketApplyConditionDao);
	}
	
	@Override
	public TicketApplyCondition getTicketApplyConditionByTenant(Tenant tenant){
		return this.ticketApplyConditionDao.getConditionByTenant(tenant);
	}
	
}
