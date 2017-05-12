/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.DepositDao;
import net.wit.entity.Deposit;
import net.wit.entity.Member;
import net.wit.service.DepositService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 预存款
 * @author rsico Team
 * @version 3.0
 */
@Service("depositServiceImpl")
public class DepositServiceImpl extends BaseServiceImpl<Deposit, Long> implements DepositService {

	@Resource(name = "depositDaoImpl")
	private DepositDao depositDao;

	@Resource(name = "depositDaoImpl")
	public void setBaseDao(DepositDao depositDao) {
		super.setBaseDao(depositDao);
	}

	@Transactional(readOnly = true)
	public Page<Deposit> findPage(Member member, Pageable pageable) {
		return depositDao.findPage(member, pageable);
	}

	@Transactional(readOnly = true)
	public List<Deposit> findList(Member member, Date beginTime, Date endTime) {
		return depositDao.findList(member, beginTime, endTime);
	}

}