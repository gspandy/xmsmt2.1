/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Member;
import net.wit.entity.Rebate;

/**
 * Service - 收款单
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface RebateService extends BaseService<Rebate, Long> {


	Page<Rebate> findPage(Member member, Pageable pageable);
	
	void calcRebate();
	
}