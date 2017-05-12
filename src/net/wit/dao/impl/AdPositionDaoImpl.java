/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.FlushModeType;

import net.wit.dao.AdPositionDao;
import net.wit.entity.Ad;
import net.wit.entity.AdPosition;
import net.wit.entity.AdPositionTenant;
import net.wit.entity.Tenant;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dao - 广告位
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("adPositionDaoImpl")
public class AdPositionDaoImpl extends BaseDaoImpl<AdPosition, Long> implements AdPositionDao {

	public AdPosition find(Long id, Tenant tenant,Integer count) {
		Session session = (Session) entityManager.getDelegate();
		session.enableFilter("tenantFilter").setParameter("tenantId", tenant.getId());
		Query query = session.createQuery("from AdPosition where id="+id);
		query.setFirstResult(0).setFetchSize(10);
		@SuppressWarnings("unchecked")
		List<AdPosition> result = query.list();
		session.disableFilter("tenantFilter");
		if(result.isEmpty()){
			return null;
		}
		AdPosition adPosition = result.get(0);
		Set<Ad> ads = new HashSet<Ad>();
		if(count==null){
			if(!adPosition.getAds().isEmpty()){
				for(Ad ad :adPosition.getAds()){
					ads.add(ad);
				}
			}
		}else{
			if(!adPosition.getAds().isEmpty()){
				int tmpCount=0;
				while (adPosition.getAds().iterator().hasNext()) {
					ads.add(adPosition.getAds().iterator().next());
					tmpCount++;
					if(tmpCount==count){
						break;
					};
				}
			}
		}
		adPosition.setAds(ads);
		return adPosition;
	}

	@Transactional
	public AdPositionTenant findTenant(Long id) {
		String jpql = "select AdPositionTenant from AdPositionTenant AdPositionTenant where AdPositionTenant.id = :id";
		return entityManager.createQuery(jpql, AdPositionTenant.class).setFlushMode(FlushModeType.COMMIT).setParameter("id", id).getSingleResult();
	}

	@Transactional
	public void saveTenant(AdPositionTenant adPositionTenant) {
		entityManager.merge(adPositionTenant);
	}

}