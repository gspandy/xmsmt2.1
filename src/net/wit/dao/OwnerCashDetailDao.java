/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantSellConditionDao.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月8日
 */
package net.wit.dao;

import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import net.wit.entity.OwnerCashDetail;
import net.wit.entity.PayBank;


/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月8日
 */
public interface OwnerCashDetailDao extends BaseDao<OwnerCashDetail, Long>{
	
	 List<OwnerCashDetail> getOwnerCashDetailByMemberId(Long memberId);
	 
	 OwnerCashDetail findByCashRequestId(String Ccashrequestid);
}
