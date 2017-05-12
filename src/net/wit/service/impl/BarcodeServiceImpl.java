/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import javax.annotation.Resource;


import net.wit.dao.BarcodeDao;
import net.wit.entity.Barcode;
import net.wit.service.BarcodeService;

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
@Service("barcodeServiceImpl")
public class BarcodeServiceImpl extends BaseServiceImpl<Barcode, Long> implements BarcodeService {

	@Resource(name = "barcodeDaoImpl")
	private BarcodeDao barcodeDao;

	@Resource(name = "barcodeDaoImpl")
	public void setBaseDao(BarcodeDao barcodeDao) {
		super.setBaseDao(barcodeDao);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "barcode" }, allEntries = true)
	public void save(Barcode barcode) {
		Assert.notNull(barcode);

		super.save(barcode);
		barcodeDao.flush();
	}

	@Override
	@Transactional
	@CacheEvict(value = { "barcode" }, allEntries = true)
	public Barcode update(Barcode barcode) {
		Assert.notNull(barcode);

		Barcode pGoods = super.update(barcode);
		barcodeDao.flush();
		return pGoods;
	}

	@Override
	@Transactional
	@CacheEvict(value = { "barcode" }, allEntries = true)
	public Barcode update(Barcode barcode, String... ignoreProperties) {
		return super.update(barcode, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "barcode" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "barcode" }, allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "barcode" }, allEntries = true)
	public void delete(Barcode barcode) {
		super.delete(barcode);
	}

	public Barcode findByBarcode(String barcode) {
		return barcodeDao.findByBarcode(barcode);
	}
	
}