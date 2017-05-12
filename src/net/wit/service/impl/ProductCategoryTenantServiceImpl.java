/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.dao.ProductCategoryTenantDao;
import net.wit.entity.ProductCategoryTenant;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.service.ProductCategoryTenantService;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 商家商品分类
 * @author rsico Team
 * @version 3.0
 */
@Service("productCategoryTenantServiceImpl")
public class ProductCategoryTenantServiceImpl extends BaseServiceImpl<ProductCategoryTenant, Long>implements ProductCategoryTenantService {

	@Resource(name = "productCategoryTenantDaoImpl")
	private ProductCategoryTenantDao productCategoryTenantDao;

	@Resource(name = "productCategoryTenantDaoImpl")
	public void setBaseDao(ProductCategoryTenantDao ProductCategoryTenantDao) {
		super.setBaseDao(ProductCategoryTenantDao);
	}

	@Transactional(readOnly = true)
	public List<ProductCategoryTenant> findRoots(Tenant tenant) {
		return productCategoryTenantDao.findRoots(null, tenant);
	}

	@Transactional(readOnly = true)
	public List<ProductCategoryTenant> findRoots(Integer count, Tenant tenant) {
		return productCategoryTenantDao.findRoots(count, tenant);
	}

	@Transactional(readOnly = true)
	public List<ProductCategoryTenant> findRoots(Tag tag, Integer count, Tenant tenant) {
		return productCategoryTenantDao.findRoots(tag, count, tenant);
	}

	@Transactional(readOnly = true)
	@Cacheable("ProductCategoryTenant")
	public List<ProductCategoryTenant> findRoots(Integer count, Tenant tenant, String cacheRegion) {
		return productCategoryTenantDao.findRoots(count, tenant);
	}

	@Transactional(readOnly = true)
	@Cacheable("ProductCategoryTenant")
	public List<ProductCategoryTenant> findRoots(Tag tag, Integer count, Tenant tenant, String cacheRegion) {
		return productCategoryTenantDao.findRoots(tag, count, tenant);
	}

	@Transactional(readOnly = true)
	public List<ProductCategoryTenant> findParents(ProductCategoryTenant ProductCategoryTenant, Tenant tenant) {
		return productCategoryTenantDao.findParents(ProductCategoryTenant, null, tenant);
	}

	@Transactional(readOnly = true)
	public List<ProductCategoryTenant> findParents(ProductCategoryTenant ProductCategoryTenant, Integer count, Tenant tenant) {
		return productCategoryTenantDao.findParents(ProductCategoryTenant, count, tenant);
	}

	@Transactional(readOnly = true)
	@Cacheable("ProductCategoryTenant")
	public List<ProductCategoryTenant> findParents(ProductCategoryTenant ProductCategoryTenant, Integer count, Tenant tenant, String cacheRegion) {
		return productCategoryTenantDao.findParents(ProductCategoryTenant, count, tenant);
	}

	@Transactional(readOnly = true)
	public List<ProductCategoryTenant> findTree(Tenant tenant) {
		return productCategoryTenantDao.findChildren(null, null, tenant);
	}

	@Transactional(readOnly = true)
	public List<ProductCategoryTenant> findChildren(ProductCategoryTenant ProductCategoryTenant, Tenant tenant) {
		return productCategoryTenantDao.findChildren(ProductCategoryTenant, null, tenant);
	}

	@Transactional(readOnly = true)
	public List<ProductCategoryTenant> findChildren(ProductCategoryTenant ProductCategoryTenant, Integer count, Tenant tenant) {
		return productCategoryTenantDao.findChildren(ProductCategoryTenant, count, tenant);
	}

	@Transactional(readOnly = true)
	@Cacheable("ProductCategoryTenant")
	public List<ProductCategoryTenant> findChildren(ProductCategoryTenant ProductCategoryTenant, Integer count, Tenant tenant, String cacheRegion) {
		return productCategoryTenantDao.findChildren(ProductCategoryTenant, count, tenant);
	}

	@Override
	@Transactional
	public void save(ProductCategoryTenant ProductCategoryTenant) {
		super.save(ProductCategoryTenant);
	}

	@Override
	@Transactional
	public ProductCategoryTenant update(ProductCategoryTenant ProductCategoryTenant) {
		return super.update(ProductCategoryTenant);
	}

	@Override
	@Transactional
	public ProductCategoryTenant update(ProductCategoryTenant ProductCategoryTenant, String... ignoreProperties) {
		return super.update(ProductCategoryTenant, ignoreProperties);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	public void delete(ProductCategoryTenant ProductCategoryTenant) {
		super.delete(ProductCategoryTenant);
	}

	public List<ProductCategoryTenant> findList(Tenant tenant, Integer count) {
		return productCategoryTenantDao.findList(tenant, count);
	}

}