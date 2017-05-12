/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantRenovationServiceImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月10日
 */
package net.wit.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.wit.dao.TenantDao;
import net.wit.dao.TenantRenovationDao;
import net.wit.entity.Tenant;
import net.wit.entity.TenantRenovation;
import net.wit.service.TenantRenovationService;

import java.util.List;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月10日
 */
@Service("tenantRenovationServiceImpl")
public class TenantRenovationServiceImpl extends BaseServiceImpl<TenantRenovation, Long> implements TenantRenovationService{

    @Autowired
    private  TenantRenovationDao tenantRenovationDao;

	@Resource(name = "tenantRenovationDaoImpl")
	public void setBaseDao(TenantRenovationDao tenantRenovationDao) {
		super.setBaseDao(tenantRenovationDao);
	}

    public List<TenantRenovation> getTenantRenovationByTenantId(Long tenantId) {
        return tenantRenovationDao.getTenantRenovationByTenantId(tenantId);
    }
    
    
    public List<TenantRenovation> getTenantRenovationByTenant(Long tenantId) {
        return tenantRenovationDao.getTenantRenovationByTenant(tenantId);
    }
}
