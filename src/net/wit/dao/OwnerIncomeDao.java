/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantSellConditionDao.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月8日
 */
package net.wit.dao;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.OwnerIncome;



/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月8日
 */
public interface OwnerIncomeDao extends BaseDao<OwnerIncome, Long>{

    public Page<OwnerIncome> getOwnerIncomeByMemberId(Long memberId, Pageable pageable);
}
