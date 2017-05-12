/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: OwnerCashDetailServiceImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月17日
 */
package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.dao.OwnerCashDetailDao;
import net.wit.entity.OwnerCashDetail;
import net.wit.service.OwnerCashDetailService;

import org.springframework.stereotype.Service;
/**
 * Service - 提现流水
 */
@Service("ownerCashDetailServiceImpl")
public class OwnerCashDetailServiceImpl extends BaseServiceImpl<OwnerCashDetail, Long>implements OwnerCashDetailService{

	@Resource(name = "ownerCashDetailDaoImpl")
	private OwnerCashDetailDao ownerCashDetailDao;
	
	@Resource(name = "ownerCashDetailDaoImpl")
	public void setBaseDao(OwnerCashDetailDao ownerCashDetailDao) {
		super.setBaseDao(ownerCashDetailDao);
	}
	@Override
	public List<OwnerCashDetail> getOwnerCashDetailByMemberId(Long memberId) {
	   return ownerCashDetailDao.getOwnerCashDetailByMemberId(memberId);
	}

	@Override
	public OwnerCashDetail findByCashRequestId(String Ccashrequestid) {
		
		return ownerCashDetailDao.findByCashRequestId(Ccashrequestid);
	}
	

}
