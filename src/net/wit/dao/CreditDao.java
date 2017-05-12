/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Credit;
import net.wit.entity.Member;

/**
 * Dao - 付款单
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface CreditDao extends BaseDao<Credit, Long> {

	/**
	 * 根据编号查找付款单
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 付款单，若不存在则返回null
	 */
	Credit findBySn(String sn);

	Page<Credit> findPage(Member member, Pageable pageable, Credit.Type type );
}