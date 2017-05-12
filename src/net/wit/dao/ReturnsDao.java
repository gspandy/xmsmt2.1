/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Returns;
import net.wit.entity.Tenant;
import net.wit.entity.Returns.ReturnStatus;

/**
 * Dao - 退货单
 * @author rsico Team
 * @version 3.0
 */
public interface ReturnsDao extends BaseDao<Returns, Long> {

	/**
	 * @Title：findPage @Description：
	 * @param tenant
	 * @param pageable
	 * @return Page<Returns>
	 */
	Page<Returns> findPage(Tenant tenant, ReturnStatus returnStatus, Pageable pageable);

}