/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import net.wit.entity.MobileSegment;

/**
 * Dao - 手机快充价格
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface MobileSegmentDao extends BaseDao<MobileSegment, Long> {

	/**
	 * 查找手机号码段
	 * 
	 * @param mobile
	 *            手机号
	 * @return 号码段
	 */
	
	MobileSegment findByMobile(String mobile);

}