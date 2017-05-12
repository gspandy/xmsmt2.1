/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Credit;
import net.wit.entity.Member;

/**
 * Service - 收款单
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface CreditService extends BaseService<Credit, Long> {

	/**
	 * 根据编号查找收款单
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 收款单，若不存在则返回null
	 */
	Credit findBySn(String sn);

	Page<Credit> findPage(Member member, Pageable pageable, Credit.Type type);
	
	void saveAndUpdate(Credit credit) throws Exception;
	void saveAndRefunds(Credit credit) throws Exception;
	
	void checkPayment();
}