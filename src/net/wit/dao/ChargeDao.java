/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.math.BigDecimal;
import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Charge;
import net.wit.entity.Member;
import net.wit.entity.Tenant;

/**
 * 
 */
public interface ChargeDao extends BaseDao<Charge, Long> {
	/**
	 * 
	 * @param charge
	 * @param member
	 * @param tenant
	 * @param pageable
	 * @return
	 */
	public Page<Charge> findPage(Charge charge,Member member,Tenant tenant,Pageable pageable);
	/**
	 * 多参数分页查询
	 * @param memberId
	 * @param status
	 * @param type
	 * @param pageable
	 * @return
	 */
	public Page<Charge> findPageByParas(Long memberId,List<Charge.Status> status,Charge.Type type, Pageable pageable);
	/**
	 * 查询提现列表
	 * @param memberId
	 * @param status
	 * @return
	 */
	public List<Charge> findChargeInfo(Long memberId,List<Charge.Status> status);
	/**
	 * 统计已发放总额
	 * @param memberId
	 * @return
	 */
	public BigDecimal sumReceived(Long memberId);
	/**
	 * 通过交易编号查询列表
	 * @param txNo
	 * @return
	 */
	public List<Charge> findChargeInfo(String txNo);
	/**
	 * charge更新为失效
	 * @param shopkeeper
	 * @return
	 */
	public boolean updateTicketInvalid(Member shopkeeper);
	
}