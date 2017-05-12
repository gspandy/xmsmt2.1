/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import javax.annotation.Resource;

import net.wit.dao.AdPositionDao;
import net.wit.entity.AdPosition;
import net.wit.entity.AdPositionTenant;
import net.wit.entity.Tenant;
import net.wit.service.AdPositionService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 广告位
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("adPositionServiceImpl")
public class AdPositionServiceImpl extends BaseServiceImpl<AdPosition, Long> implements AdPositionService {

	@Resource(name = "adPositionDaoImpl")
	private AdPositionDao adPositionDao;

	@Resource(name = "adPositionDaoImpl")
	public void setBaseDao(AdPositionDao adPositionDao) {
		super.setBaseDao(adPositionDao);
	}
	
	@Transactional(readOnly = true)
	public AdPosition find(Long id, Tenant tenant,Integer count) {
		if(tenant==null){
			return adPositionDao.find(id);
		}else{
			return adPositionDao.find(id,tenant,count);
		}
	}

	@Transactional(readOnly = true)
	@Cacheable("adPosition")
	public AdPosition find(Long id,Tenant tenant,Integer count, String cacheRegion) {
		if(tenant==null){
			return adPositionDao.find(id);
		}else{
			return adPositionDao.find(id,tenant,count);
		}
	}

	@Override
	@Transactional
	@CacheEvict(value = "adPosition", allEntries = true)
	public void save(AdPosition adPosition) {
		super.save(adPosition);
	}

	@Override
	@Transactional
	@CacheEvict(value = "adPosition", allEntries = true)
	public AdPosition update(AdPosition adPosition) {
		return super.update(adPosition);
	}

	@Override
	@Transactional
	@CacheEvict(value = "adPosition", allEntries = true)
	public AdPosition update(AdPosition adPosition, String... ignoreProperties) {
		return super.update(adPosition, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = "adPosition", allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = "adPosition", allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = "adPosition", allEntries = true)
	public void delete(AdPosition adPosition) {
		super.delete(adPosition);
	}

	@Transactional
	public AdPositionTenant findTenant(Long id) {
		return adPositionDao.findTenant(id);
	}

	@Transactional
	public void saveTenant(AdPositionTenant adPositionTenant) {
		adPositionDao.saveTenant(adPositionTenant);
	}
}