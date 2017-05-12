package net.wit.service;

import java.util.Date;
import java.util.List;

import net.wit.entity.Order;


/**
 * @version 
 */
public interface BatchJobService  {
	/**
	 * 内购券发放-每月定额发放
	 * @param batchDate
	 */
	public void batchSendTicket(Date batchDate);
	/**
	 * 内购券到期失效处理（可重复运行）
	 * @param batchDate
	 */
	public void releaseTicket(Date batchDate);
	/**
	 * 订单结算（可重复运行）
	 * @param batchDate
	 */
	public void orderSettle(Date batchDate);
	/**
	 * 租金生成（可重复运行）
	 * @param batchDate
	 */
	public void calculateRent(Date batchDate);
	/**
	 * 自动收货
	 * @param date
	 */
	public void orderAccept(Date date);
	/**
	 * 订单完成
	 * @param date
	 */
	public void orderComplete(Date date);
	/**
	 * 订单退货取消
	 * @param date
	 */
	public void returnCancel(Date date);
	/**
	 * 订单支付过期
	 * @param date
	 */
	public void orderExpire(Date date);
	
	public List<Order> findOrderAccepted(Date dueDate);
}