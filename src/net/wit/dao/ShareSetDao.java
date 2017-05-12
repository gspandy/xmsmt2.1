package net.wit.dao;

import java.util.List;

import net.wit.entity.ShareSet;
import net.wit.entity.ShareSet.Type;
import net.wit.entity.Tenant;

public interface ShareSetDao extends BaseDao<ShareSet,Long>{

	public List<ShareSet> queryShareSetByTenant(Tenant tenant, Type type);

}
