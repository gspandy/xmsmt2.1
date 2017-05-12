package net.wit.service.impl;


import java.util.List;

import net.wit.entity.ShareSet;
import net.wit.entity.ShareSet.Type;
import net.wit.entity.Tenant;
import net.wit.service.BaseService;

public interface ShareSetService extends BaseService<ShareSet, Long> {

	public List<ShareSet> getShareSetByTenant(Tenant tenant);

	public List<ShareSet> getShareSetByTenant(Tenant tenant, Type type);

	public ShareSet getTicketShareTitleByTenant(Tenant tenant);

	public ShareSet getInvitaionShareTitleByTenant(Tenant tenant);

	public ShareSet getTicketShareContentByTenant(Tenant tenant);

	public ShareSet getInvitaionShareContentByTenant(Tenant tenant);

}
