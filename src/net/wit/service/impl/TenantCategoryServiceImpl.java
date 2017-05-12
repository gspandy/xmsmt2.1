/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.dao.TenantCategoryDao;
import net.wit.entity.ProductChannel;
import net.wit.entity.TenantCategory;
import net.wit.service.TenantCategoryService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 企业分类
 * @author rsico Team
 * @version 3.0
 */
@Service("tenantCategoryServiceImpl")
public class TenantCategoryServiceImpl extends BaseServiceImpl<TenantCategory, Long> implements TenantCategoryService {

	@Resource(name = "tenantCategoryDaoImpl")
	private TenantCategoryDao tenantCategoryDao;

	@Resource(name = "tenantCategoryDaoImpl")
	public void setBaseDao(TenantCategoryDao tenantCategoryDao) {
		super.setBaseDao(tenantCategoryDao);
	}

	@Transactional(readOnly = true)
	public List<TenantCategory> findRoots() {
		return tenantCategoryDao.findRoots(null);
	}

	@Transactional(readOnly = true)
	public List<TenantCategory> findRoots(Integer count) {
		return tenantCategoryDao.findRoots(count);
	}

	@Transactional(readOnly = true)
	@Cacheable("tenantCategory")
	public List<TenantCategory> findRoots(Integer count, String cacheRegion) {
		return tenantCategoryDao.findRoots(count);
	}

	@Transactional(readOnly = true)
	public List<TenantCategory> findParents(TenantCategory tenantCategory) {
		return tenantCategoryDao.findParents(null, tenantCategory, null);
	}

	@Transactional(readOnly = true)
	public List<TenantCategory> findParents(ProductChannel productChannel, TenantCategory tenantCategory, Integer count) {
		return tenantCategoryDao.findParents(productChannel, tenantCategory, count);
	}

	@Transactional(readOnly = true)
	@Cacheable("tenantCategory")
	public List<TenantCategory> findParents(ProductChannel productChannel, TenantCategory tenantCategory, Integer count, String cacheRegion) {
		return tenantCategoryDao.findParents(productChannel, tenantCategory, count);
	}

	@Transactional(readOnly = true)
	public List<TenantCategory> findTree() {
		return tenantCategoryDao.findChildren(null, null);
	}

	@Transactional(readOnly = true)
	public List<TenantCategory> findChildren(TenantCategory tenantCategory) {
		return tenantCategoryDao.findChildren(tenantCategory, null);
	}

	@Transactional(readOnly = true)
	public List<TenantCategory> findChildren(TenantCategory tenantCategory, Integer count) {
		return tenantCategoryDao.findChildren(tenantCategory, count);
	}

	@Transactional(readOnly = true)
	@Cacheable("tenantCategory")
	public List<TenantCategory> findChildren(TenantCategory tenantCategory, Integer count, String cacheRegion) {
		return tenantCategoryDao.findChildren(tenantCategory, count);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "tenantCategory" }, allEntries = true)
	public void save(TenantCategory tenantCategory) {
		super.save(tenantCategory);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "tenantCategory" }, allEntries = true)
	public TenantCategory update(TenantCategory tenantCategory) {
		return super.update(tenantCategory);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "tenantCategory" }, allEntries = true)
	public TenantCategory update(TenantCategory tenantCategory, String... ignoreProperties) {
		return super.update(tenantCategory, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "tenantCategory" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "tenantCategory" }, allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "tenantCategory" }, allEntries = true)
	public void delete(TenantCategory tenantCategory) {
		super.delete(tenantCategory);
	}

	@Transactional(readOnly = true)
	public List<TenantCategory> findParents(TenantCategory tenantCategory, Integer count) {
		return tenantCategoryDao.findParents(tenantCategory, count);
	}

}