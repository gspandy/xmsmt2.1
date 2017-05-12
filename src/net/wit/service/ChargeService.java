/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.BonusCalc;
import net.wit.entity.Charge;
import net.wit.entity.Member;
import net.wit.entity.Owner;
import net.wit.entity.TenantBonusSet;
import net.wit.util.BizException;
import net.wit.vo.OrderSettlementVO;

/**
 * 
 */
public interface ChargeService extends BaseService<Charge, Long> {
	/**
	 * 分页查询
	 * @param charge
	 * @param pageable
	 * @return
	 */
	public Page<Charge> findPage(Charge charge,Pageable pageable);
	/**
	 * 提现
	 * @param id
	 * @throws BizException
	 */
	public void chargeCash(Long id)throws BizException;
	/**
	 * 提现审批通过
	 * @param list
	 * @throws BizException
	 */
	public void cashAgree(List<Long> list)throws BizException;
	/**
	 * 未提现记录查询
	 * @param memberId
	 * @param pageable
	 * @return
	 */
	public Page<Charge> findPageNotReceive(Long memberId,Pageable pageable);
	/**
	 * 已提现记录查询
	 * @param memberId
	 * @param pageable
	 * @return
	 */
	public Page<Charge> findPageReceived(Long memberId,Pageable pageable);
	/**
	 * 查询提现列表
	 * @param memberId
	 * @param status
	 * @return
	 */
	public List<Charge> findChargeInfo(Long memberId,Charge.Status status);
	/**
	 * 查询未提现列表
	 * @param memberId
	 * @return
	 */
	public List<Charge> findNotReceiveList(Long memberId);
	/**
	 * 统计已发放总额
	 * @param memberId
	 * @return
	 */
	public BigDecimal sumReceived(Long memberId) ;
	/**
	 * 提现回调
	 * @param result
	 * @return
	 * @throws BizException
	 */
	public String cashAdviceBack(String result)throws BizException;
	/**
	 * charge更新为失效
	 * @param shopkeeper
	 * @return
	 */
	public boolean updateTicketInvalid(Member shopkeeper);
	
	public void orderSettleSubmit(OrderSettlementVO vo,Date date,TenantBonusSet set,Owner owner) throws Exception;
	
	/**
	 * 
	 * @param owner
	 * @param charge
	 * @param boncalcList
	 */
	public void submitBonusCharge(Owner owner, Charge charge,
			List<BonusCalc> boncalcList);
}