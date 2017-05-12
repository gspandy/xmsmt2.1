/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: RefundConfirmServiceImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月14日
 */
package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.RefundConfirmDao;
import net.wit.entity.Refunds;
import net.wit.entity.Tenant;
import net.wit.service.RefundConfirmService;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月14日
 */
@Service("refundConfirmServiceImpl")
public class RefundConfirmServiceImpl extends BaseServiceImpl<Refunds, Long> implements RefundConfirmService{

	@Resource(name = "refundConfirmDaoImpl")
	RefundConfirmDao refundConfirmDao;
	
	@Resource(name = "refundConfirmDaoImpl")
	public void setBaseDao(RefundConfirmDao refundConfirmDao) {
		super.setBaseDao(refundConfirmDao);
	}

	public Page<Refunds> findPage(Tenant tenant, Pageable pageable) {
		return refundConfirmDao.findPage(tenant, pageable);
	}

	@Override
	public List<Refunds> listAll(Tenant tenant) {
	   return refundConfirmDao.listAll(tenant);
	}
}
