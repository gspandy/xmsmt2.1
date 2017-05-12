/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Member;
import net.wit.entity.Payment;
import net.wit.entity.Tenant;

/**
 * Service - 收款单
 * @author rsico Team
 * @version 3.0
 */
public interface PaymentService extends BaseService<Payment, Long> {

	/**
	 * 根据编号查找收款单
	 * @param sn 编号(忽略大小写)
	 * @return 收款单，若不存在则返回null
	 */
	Payment findBySn(String sn);

	/**
	 * 支付处理
	 * @param payment 收款单
	 */
	void handle(Payment payment) throws Exception;
	
	void alipayHandle(Payment payment, String sn) throws Exception;

	Page<Payment> findPage(Member member, Pageable pageable, Payment.Type type);

	/**
	 * @Title：findPage @Description：
	 * @param tenant
	 * @param pageable
	 * @return Object
	 */
	Page<Payment> findPage(Tenant tenant, Pageable pageable);
}