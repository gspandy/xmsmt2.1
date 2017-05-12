package net.wit.dao;

import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.CustomService;
import net.wit.entity.Tenant;
import net.wit.entity.TenantShopkeeper;

public interface CustomServiceDao extends BaseDao<CustomService, Long>{
	
	
	/**
	 * 获取企业的所有客服信息
	 * @param tenantId
	 * @return
	 */
	List<CustomService> findALL(long tenantId);
	
	
	
	public CustomService  getCustomServiceByAdmin(Admin admin);
	
	
	Page<CustomService> findPage(Tenant tenant,Pageable pageable);

}
