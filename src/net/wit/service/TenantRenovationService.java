/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantRenovationService.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月10日
 */
package net.wit.service;

import net.wit.entity.Tenant;
import net.wit.entity.TenantRenovation;

import java.util.List;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月10日
 */
public interface TenantRenovationService  extends BaseService<TenantRenovation, Long>{
    public List<TenantRenovation> getTenantRenovationByTenantId(Long tenantId);
    
    public List<TenantRenovation> getTenantRenovationByTenant(Long tenantId);
}
