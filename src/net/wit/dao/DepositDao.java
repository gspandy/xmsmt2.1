/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.Date;
import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Deposit;
import net.wit.entity.Member;

/**
 * Dao - 预存款
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface DepositDao extends BaseDao<Deposit, Long> {

	/**
	 * 查找预存款分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 预存款分页
	 */
	Page<Deposit> findPage(Member member, Pageable pageable);

	List<Deposit> findList(Member member, Date beginTime, Date endTime);

}