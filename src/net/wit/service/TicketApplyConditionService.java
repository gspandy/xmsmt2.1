package net.wit.service;

import net.wit.entity.Tenant;
import net.wit.entity.TicketApplyCondition;

public interface TicketApplyConditionService extends BaseService<TicketApplyCondition, Long>{

	public TicketApplyCondition getTicketApplyConditionByTenant(Tenant tenant);

}
