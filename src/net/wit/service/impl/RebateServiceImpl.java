/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;


import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.RebateDao;
import net.wit.entity.Member;
import net.wit.entity.Rebate;
import net.wit.service.CreditService;
import net.wit.service.MemberService;
import net.wit.service.RebateService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 代理商返利
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("rebateServiceImpl")
public class RebateServiceImpl extends BaseServiceImpl<Rebate, Long> implements RebateService {

	@Resource(name = "rebateDaoImpl")
	private RebateDao rebateDao;
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "creditServiceImpl")
	private CreditService creditService;

	@Resource(name = "rebateDaoImpl")
	public void setBaseDao(RebateDao rebateDao) {
		super.setBaseDao(rebateDao);
	}

	@Transactional(readOnly = true)
	public Page<Rebate> findPage(Member member, Pageable pageable) {
		return rebateDao.findPage(member, pageable);
	}
	
	
	public void calcRebate() {
		
		rebateDao.calcRebate();
	}

}

