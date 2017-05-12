/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: RefundConfirmDao.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月14日
 */
package net.wit.dao;

import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Refunds;
import net.wit.entity.Tenant;

/**
 * 订单确认
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月14日
 */
public interface RefundConfirmDao extends BaseDao<Refunds, Long>{
	
	/**
	 * @Title：findPage
	 * @Description：
	 * @param tenant
	 * @param pageable
	 * @return  Page<Refunds>
	 */
	Page<Refunds> findPage(Tenant tenant, Pageable pageable);
	/**
	 * 查询所有记录
	 * @param tenant
	 * @return
	 */
     List<Refunds> listAll(Tenant tenant);
}
