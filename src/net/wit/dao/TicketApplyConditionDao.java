package net.wit.dao;

import net.wit.entity.Tenant;
import net.wit.entity.TicketApplyCondition;

public interface TicketApplyConditionDao extends BaseDao<TicketApplyCondition, Long>{

	public TicketApplyCondition getConditionByTenant(Tenant tenant);

	
}
