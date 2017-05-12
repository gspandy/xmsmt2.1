/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSetDao.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.dao;

import java.util.Date;
import java.util.List;

import net.wit.entity.Order;
import net.wit.entity.OrderSettlement;
import net.wit.entity.TenantShopkeeper;
import net.wit.vo.OrderSettlementVO;


/**
 * 
 */
public interface BatchJobDao extends BaseDao<Object, Long>{
	/**
	 * 已过期，状态未使用券置为失效
	 * @param date
	 * @throws Exception
	 */
	public void batchUpateTicket(Date date)throws Exception;
	/**
	 * 已过期，状态领取券置为失效
	 * @param date
	 * @throws Exception
	 */
	public void batchUpateTicketCache(Date date)throws Exception;
	
	public List<OrderSettlement> findPlanFinishOrder(Date date);
	/**
	 * 按月份获取已完成订单结算信息
	 * @param date
	 * @return
	 */
	public List<OrderSettlementVO> getSettlementCharge(Date date);
	
	public List<Long> getOwnerIdList(Date date);
	
	public List<Order> findExpireOrder(Date date);
	
	public List<Order> findOrderAccepted(Date dueDate);
	
	public List<Order> findOrderReturn(Date dueDate);
	/**
	 * 查询已发货的订单
	 * @param dueDate
	 * @return
	 */
	public List<Order> findOrderShipped(Date dueDate);
	/**
	 * 订单结算-更新状态已完成的订单
	 * @throws Exception
	 */
	public void batchUpateSettlenment()throws Exception;
	
	public List<TenantShopkeeper> findShopKeeper(Date date);
	
	public void batchUpateChargeId(Long chargeId,Long ownerId,Date date)throws Exception;
	public List<Order> findOrderShippedExtend(Date dueDate);

}
