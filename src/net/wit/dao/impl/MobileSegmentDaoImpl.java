/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import net.wit.dao.MobileSegmentDao;
import net.wit.entity.MobileSegment;

import org.springframework.stereotype.Repository;

/**
 * Dao - 手机快充价格
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("mobileSegmentDaoImpl")
public class MobileSegmentDaoImpl extends BaseDaoImpl<MobileSegment, Long> implements MobileSegmentDao {

	public MobileSegment findByMobile(String mobile) {
		if (mobile == null) {
			return null;
		}
		String jpql = "select mobileSegment from MobileSegment mobileSegment where mobileSegment.segment = :segment";
		try {
			int segment = Integer.parseInt( mobile.substring(0,7) );
			return entityManager.createQuery(jpql, MobileSegment.class).setFlushMode(FlushModeType.COMMIT).setParameter("segment",segment).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}