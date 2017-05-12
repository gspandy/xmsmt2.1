package net.wit.service;

import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.CustomService;
import net.wit.entity.Tenant;
import net.wit.entity.TenantShopkeeper;

public interface CustomServiceService extends BaseService<CustomService, Long>{
	
	List<CustomService> findALL(long tenantId);
	
	public CustomService  findCustomServiceByAdmin(Admin admin);
	
	public Page<CustomService> findPage(Tenant tenant,Pageable pageable);
}
