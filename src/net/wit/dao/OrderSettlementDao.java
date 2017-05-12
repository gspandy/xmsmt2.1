/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantSellConditionDao.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月8日
 */
package net.wit.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Charge;
import net.wit.entity.Member;
import net.wit.entity.OrderSettlement;
import net.wit.entity.OrderSettlement.SettlementStatus;
import net.wit.vo.OrderSettlementAdapter;
import net.wit.vo.OrderSettlementVO;
import net.wit.vo.ShareOrderSettlementVO;

/**
 * @author: yangyang.wu
 * @version Revision: 0.0.1 @Date：2015年9月8日
 */
public interface OrderSettlementDao extends BaseDao<OrderSettlement, Long> {

	/**
	 * 查找已完成的订单
	 * @return
	 */
	public List<OrderSettlement> getSettlementDone();

	/**
	 * 查找已完成订单的结算佣金
	 * @return
	 */
	public BigDecimal getSettlementCharge(Member owner);

	public List<OrderSettlement> getSettlementByParas(OrderSettlement orderSettlement);

	public Page<OrderSettlement> getOrderSettlementStream(Member owner, Pageable pageable);

	public BigDecimal getOwnerSettleCharge(Member owner);

	public Page<OrderSettlement> getOrderSettlementUncomplete(Member owner, Pageable pageable);

	/**
	 * 通过提现id查询列表
	 * @param chargeId
	 * @param pageable
	 * @return
	 */
	public Page<OrderSettlement> findByChargeId(Long chargeId, Pageable pageable);

	/**
	 * 解绑店主
	 * @param member
	 * @return
	 */
	boolean setOrderSettleInvalid(Member member);

	/**
	 * 店主待结算订单金额(订单未完成与已完成,并且有效)
	 * @param member
	 * @return
	 */

	public BigDecimal getPendingorderSettleAmt(Member member);

	/**
	 * 店主待结算订单(订单未完成与已完成,并且有效)
	 * @param member
	 * @return
	 */
	public List<OrderSettlement> getPendingOrderSettlements(Member member);

	/**
	 * @param month
	 * @param status
	 * @param ids
	 * @param owner
	 * @param isCharged
	 * @param listStatus
	 * @return
	 */
	public OrderSettlementAdapter queryOrderSettlementsByCondition(String time, String userName, String status, Pageable pageable, String ids,Long tenantId);
	public OrderSettlementAdapter queryOrderRecommonSettlementsByCondition(String time, String userName, String status, Pageable pageable, String ids,Long tenantId);
	/**
	 * @param month
	 * @param owner
	 * @param isCharged
	 * @param listStatus
	 * @return
	 */
	List<ShareOrderSettlementVO> queryOrderSettlementsByCondition(Date month, Member owner, Boolean isCharged, List<SettlementStatus> listStatus);

	Page<ShareOrderSettlementVO> queryOrderSettlementsByCondition(Date month, Member owner, Boolean isCharged, List<SettlementStatus> listStatus, Pageable pageable);

	List<ShareOrderSettlementVO> queryInviteOrderSettlementsByCondition(Date month, Member owner, Boolean isCharged, List<SettlementStatus> listStatus);
	
	/**
	 * 修改orderSettlement为发放状态
	 * @param list
	 * @throws Exception
	 */
	public void batchUpateOrderSettlementRecevied(List<Charge> list) throws Exception;

	public OrderSettlementAdapter queryShareChargeMonthReportByCondition(
			String time, String username, Pageable pageable, String moblie, String status,Long tenantId);

	public OrderSettlementAdapter recommendChargeListMonthReport(String time,
			String username, Pageable pageable, String moblie, String status,Long tenantId);

}
