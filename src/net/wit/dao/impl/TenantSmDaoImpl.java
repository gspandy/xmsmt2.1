/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSetDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.dao.impl;

import net.wit.entity.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import net.wit.dao.TenantSmDao;
import net.wit.dao.TicketSetDao;

import javax.persistence.FlushModeType;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月7日
 */
@Repository("tenantSmDaoImpl")
public class TenantSmDaoImpl extends BaseDaoImpl<TenantSm, Long> implements TenantSmDao{

    public List<TenantSm> getSmLeftByTenantId(Long tenantId) {
        List<TenantSm> tenantSm = new ArrayList<TenantSm>();
        try {
            String jpql = "select tenantSm from TenantSm tenantSm where lower(tenantSm.tenant) = lower(:tenant)";
            tenantSm = entityManager.createQuery(jpql, TenantSm.class).setFlushMode(FlushModeType.COMMIT).setParameter("tenant", new Tenant(tenantId)).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tenantSm;
    }
}
