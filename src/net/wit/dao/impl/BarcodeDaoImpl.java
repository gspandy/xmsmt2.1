/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import javax.annotation.Resource;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import net.wit.dao.BarcodeDao;
import net.wit.entity.Barcode;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Dao - 条码库
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("barcodeDaoImpl")
public class BarcodeDaoImpl extends BaseDaoImpl<Barcode, Long> implements BarcodeDao {

	@Resource(name = "barcodeDaoImpl")
	private BarcodeDao barcodeDao;

	/**
	 * 设置值并保存
	 * 
	 * @param barcode
	 *            条码库
	 */
	@Override
	public void persist(Barcode barcode) {
		Assert.notNull(barcode);

		super.persist(barcode);
	}

	/**
	 * 设置值并更新
	 * 
	 * @param barcode
	 *            条码库
	 * @return 条码库
	 */
	@Override
	public Barcode merge(Barcode barcode) {
		Assert.notNull(barcode);

		return super.merge(barcode);
	}
	
	public Barcode findByBarcode(String barcode) {
		if (barcode == null) {
			return null;
		}
		String jpql = "select barcodes from Barcode barcodes where barcodes.barcode = :barcode";
		try {
			return entityManager.createQuery(jpql, Barcode.class).setFlushMode(FlushModeType.COMMIT).setParameter("barcode", barcode).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	

}