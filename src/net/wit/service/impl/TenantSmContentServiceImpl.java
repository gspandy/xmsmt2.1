package net.wit.service.impl;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.RoleDao;
import net.wit.dao.TenantSmContentDao;
import net.wit.entity.Role;
import net.wit.entity.Tenant;
import net.wit.entity.TenantSmContent;
import net.wit.service.RoleService;
import net.wit.service.TenantSmContentService;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-8
 * Time: 下午11:50
 * To change this template use File | Settings | File Templates.
 */
@Service("tenantSmContentServiceImpl")
public class TenantSmContentServiceImpl extends BaseServiceImpl<TenantSmContent, Long> implements TenantSmContentService {
    
	  @Resource(name = "tenantSmContentDaoImpl")
	  TenantSmContentDao tenantSmContentDao;
	  
    @Resource(name = "tenantSmContentDaoImpl")
    public void setBaseDao(TenantSmContentDao tenantSmContentDao) {
        super.setBaseDao(tenantSmContentDao);
    }

    public void save(TenantSmContent tenantSmContent) {
        super.save(tenantSmContent);
    }

	@Override
	public Page<TenantSmContent> findPage(Tenant tenant, Pageable pageable) {
		return tenantSmContentDao.findPage(tenant, pageable);
	}
}
