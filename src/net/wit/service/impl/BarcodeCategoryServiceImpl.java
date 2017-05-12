/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import javax.annotation.Resource;


import net.wit.dao.BarcodeCategoryDao;
import net.wit.entity.BarcodeCategory;
import net.wit.service.BarcodeCategoryService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Service - 货品
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("barcodeCategoryServiceImpl")
public class BarcodeCategoryServiceImpl extends BaseServiceImpl<BarcodeCategory, Long> implements BarcodeCategoryService {

	@Resource(name = "barcodeCategoryDaoImpl")
	private BarcodeCategoryDao barcodeCategoryDao;

	@Resource(name = "barcodeCategoryDaoImpl")
	public void setBaseDao(BarcodeCategoryDao barcodeCategoryDao) {
		super.setBaseDao(barcodeCategoryDao);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "barcodeCategory" }, allEntries = true)
	public void save(BarcodeCategory barcodeCategory) {
		Assert.notNull(barcodeCategory);

		super.save(barcodeCategory);
		barcodeCategoryDao.flush();
	}

	@Override
	@Transactional
	@CacheEvict(value = { "barcodeCategory" }, allEntries = true)
	public BarcodeCategory update(BarcodeCategory barcodeCategory) {
		Assert.notNull(barcodeCategory);

		BarcodeCategory pGoods = super.update(barcodeCategory);
		barcodeCategoryDao.flush();
		return pGoods;
	}

	@Override
	@Transactional
	@CacheEvict(value = { "barcodeCategory" }, allEntries = true)
	public BarcodeCategory update(BarcodeCategory barcodeCategory, String... ignoreProperties) {
		return super.update(barcodeCategory, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "barcodeCategory" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "barcodeCategory" }, allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "barcodeCategory" }, allEntries = true)
	public void delete(BarcodeCategory barcodeCategory) {
		super.delete(barcodeCategory);
	}

}