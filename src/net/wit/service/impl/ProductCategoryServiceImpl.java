/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.dao.ProductCategoryDao;
import net.wit.entity.ProductCategory;
import net.wit.entity.ProductChannel;
import net.wit.entity.Tag;
import net.wit.service.ProductCategoryService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 商品分类
 * @author rsico Team
 * @version 3.0
 */
@Service("productCategoryServiceImpl")
public class ProductCategoryServiceImpl extends BaseServiceImpl<ProductCategory, Long> implements ProductCategoryService {

	@Resource(name = "productCategoryDaoImpl")
	private ProductCategoryDao productCategoryDao;

	@Resource(name = "productCategoryDaoImpl")
	public void setBaseDao(ProductCategoryDao productCategoryDao) {
		super.setBaseDao(productCategoryDao);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findRoots() {
		return productCategoryDao.findRoots(null);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findRoots(Integer count) {
		return productCategoryDao.findRoots(count);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findRoots(Tag tag, Integer count) {
		return productCategoryDao.findRoots(tag, count);
	}

	@Transactional(readOnly = true)
	@Cacheable("productCategory")
	public List<ProductCategory> findRoots(Integer count, String cacheRegion) {
		return productCategoryDao.findRoots(count);
	}

	@Transactional(readOnly = true)
	@Cacheable("productCategory")
	public List<ProductCategory> findRoots(Tag tag, Integer count, String cacheRegion) {
		return productCategoryDao.findRoots(tag, count);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findParents(ProductCategory productCategory) {
		return productCategoryDao.findParents(productCategory, null);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findParents(ProductCategory productCategory, Integer count) {
		return productCategoryDao.findParents(productCategory, count);
	}

	@Transactional(readOnly = true)
	@Cacheable("productCategory")
	public List<ProductCategory> findParents(ProductCategory productCategory, Integer count, String cacheRegion) {
		return productCategoryDao.findParents(productCategory, count);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findTree() {
		return productCategoryDao.findChildren(null, null, null);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findChildren(ProductCategory productCategory) {
		return productCategoryDao.findChildren(productCategory, null, null);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findChildren(ProductCategory productCategory, Integer count, List<Tag> tags) {
		return productCategoryDao.findChildren(productCategory, count, tags);
	}

	@Transactional(readOnly = true)
	@Cacheable("productCategory")
	public List<ProductCategory> findChildren(ProductCategory productCategory, Integer count, List<Tag> tags, String cacheRegion) {
		return productCategoryDao.findChildren(productCategory, count, tags);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review", "consultation" }, allEntries = true)
	public void save(ProductCategory productCategory) {
		super.save(productCategory);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review", "consultation" }, allEntries = true)
	public ProductCategory update(ProductCategory productCategory) {
		return super.update(productCategory);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review", "consultation" }, allEntries = true)
	public ProductCategory update(ProductCategory productCategory, String... ignoreProperties) {
		return super.update(productCategory, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review", "consultation" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review", "consultation" }, allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review", "consultation" }, allEntries = true)
	public void delete(ProductCategory productCategory) {
		super.delete(productCategory);
	}

	@Transactional(readOnly = true)
	@Cacheable("productCategory")
	public List<ProductCategory> findParentsByChannel(ProductChannel productChannel, ProductCategory productCategory, Integer count, String cacheRegion) {
		return productCategoryDao.findParentsByChannel(productChannel, productCategory, count);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findParentsByChannel(ProductChannel productChannel, ProductCategory productCategory, Integer count) {
		return productCategoryDao.findParentsByChannel(productChannel, productCategory, count);
	}

	@Transactional(readOnly = true)
	public List<ProductCategory> findRootsByChannel(ProductChannel productChannel, Integer count) {
		return productCategoryDao.findRootsByChannel(productChannel, count);
	}

	@Transactional(readOnly = true)
	@Cacheable("productCategory")
	public List<ProductCategory> findRootsByChannel(ProductChannel productChannel, Integer count, String cacheRegion) {
		return productCategoryDao.findRootsByChannel(productChannel, count);
	}
	@Transactional(readOnly = true)
	@Cacheable("productCategory")
	public List<ProductCategory> findRootsByChannel(ProductChannel productChannel, Tag tags, Integer count, String cacheRegion) {
		return productCategoryDao.findRootsByChannel(productChannel, tags, count);
	}
	
	@Transactional(readOnly = true)
	public List<ProductCategory> findRootsByChannel(ProductChannel productChannel, Tag tags, Integer count) {
		return productCategoryDao.findRootsByChannel(productChannel, tags, count);
	}
}