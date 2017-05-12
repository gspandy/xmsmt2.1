/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import javax.annotation.Resource;

import net.wit.dao.BarcodeCategoryDao;
import net.wit.entity.BarcodeCategory;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Dao - 条码库
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("barcodeCategoryDaoImpl")
public class BarcodeCategoryDaoImpl extends BaseDaoImpl<BarcodeCategory, Long> implements BarcodeCategoryDao {

	@Resource(name = "barcodeCategoryDaoImpl")
	private BarcodeCategoryDao barcodeCatetoryDao;

	/**
	 * 设置值并保存
	 * 
	 * @param barcode
	 *            条码库
	 */
	@Override
	public void persist(BarcodeCategory barcodeCategory) {
		Assert.notNull(barcodeCategory);

		super.persist(barcodeCategory);
	}

	/**
	 * 设置值并更新
	 * 
	 * @param barcode
	 *            条码库
	 * @return 条码库
	 */
	@Override
	public BarcodeCategory merge(BarcodeCategory barcodeCategory) {
		Assert.notNull(barcodeCategory);

		return super.merge(barcodeCategory);
	}

}