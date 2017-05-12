/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: OwnerCashDetailService.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月17日
 */
package net.wit.service;

import java.util.List;

import net.wit.entity.OwnerCashDetail;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月17日
 */
public interface OwnerCashDetailService extends BaseService<OwnerCashDetail, Long>{
	
	List<OwnerCashDetail> getOwnerCashDetailByMemberId(Long memberId);
	
	OwnerCashDetail findByCashRequestId(String  Ccashrequestid);
}
