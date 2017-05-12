/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.OrderDao;
import net.wit.dao.OrderLogDao;
import net.wit.dao.OrderSettlementDao;
import net.wit.dao.RefundsDao;
import net.wit.entity.MessageDetails.MessageType;
import net.wit.entity.Order;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.OrderLog;
import net.wit.entity.OrderLog.Type;
import net.wit.entity.OrderSettlement;
import net.wit.entity.OrderSettlement.SettlementStatus;
import net.wit.entity.Refunds;
import net.wit.entity.Refunds.RefurnsStatus;
import net.wit.entity.Tenant;
import net.wit.exception.OrderException;
import net.wit.service.MessageDetailsService;
import net.wit.service.RefundsService;
import net.wit.service.SnService;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Service - 退款单
 * @author rsico Team
 * @version 3.0
 */
@Service("refundsServiceImpl")
public class RefundsServiceImpl extends BaseServiceImpl<Refunds, Long>implements RefundsService {

	@Resource(name = "refundsDaoImpl")
	public void setBaseDao(RefundsDao refundsDao) {
		super.setBaseDao(refundsDao);
	}

	@Resource(name = "orderDaoImpl")
	private OrderDao orderDao;

	@Resource(name = "refundsDaoImpl")
	private RefundsDao refundsDao;

	@Resource(name = "orderLogDaoImpl")
	private OrderLogDao orderLogDao;

	@Resource(name = "snServiceImpl")
	private SnService snService;

	@Resource(name = "messageDetailsServiceImpl")
	private MessageDetailsService messageDetailsService;

	@Resource(name = "orderSettlementDaoImpl")
	private OrderSettlementDao orderSettlementDao;

	public Page<Refunds> findPage(Tenant tenant, Pageable pageable) {
		return refundsDao.findPage(tenant, pageable);
	}

	public void reject(Refunds fRefund, String operateMemo, String username) throws OrderException {
		Order order = fRefund.getOrder();
		if (order == null) {
			throw new OrderException("退款订单不存在");
		}
		fRefund.setStatus(RefurnsStatus.reject);
		order.setPaymentStatus(PaymentStatus.rejected);
		orderDao.merge(order);
		refundsDao.merge(fRefund);
		OrderLog orderLog = new OrderLog();
		orderLog.setContent(username + "拒绝了退款单:" + fRefund.getSn());
		orderLog.setOrderStatus(OrderStatus.confirmed);
		orderLogDao.persist(orderLog);
	}

	public void agree(Refunds fRefund, String operateMemo, String currentUsername) throws OrderException {
		Order order = fRefund.getOrder();
		if (order == null) {
			throw new OrderException("退款订单不存在");
		}
		fRefund.setStatus(RefurnsStatus.agree);
		refundsDao.merge(fRefund);
		
		this.orderDao.merge(order);
		
		OrderLog orderLog = new OrderLog();
		orderLog.setContent(currentUsername + "同意了退款单:" + fRefund.getSn());
		orderLog.setOrderStatus(OrderStatus.confirmed);
		orderLog.setOrder(order);
		orderLog.setType(OrderLog.Type.refunds);
		orderLogDao.persist(orderLog);
	}

	public void cancel(Refunds fRefund, String operateMemo, String currentUsername) throws OrderException {
		Order order = fRefund.getOrder();
		if (order == null) {
			throw new OrderException("退款订单不存在");
		}
		fRefund.setStatus(RefurnsStatus.cancel);
		refundsDao.merge(fRefund);
		OrderLog orderLog = new OrderLog();
		orderLog.setContent(currentUsername + "同意了退款单:" + fRefund.getSn());
		orderLog.setOrderStatus(OrderStatus.confirmed);
		orderLogDao.persist(orderLog);
	}

	/**
	 * 申请退款
	 */
	public void apply(Order order, String memo) throws OrderException {
		Assert.notNull(order);
		if (!order.isRefundAllowed())
			throw new OrderException("订单不允许申请退款");

		Refunds refunds = new Refunds();
		refunds.setOrder(order);
		refunds.setSn(snService.generate(net.wit.entity.Sn.Type.refunds));
//		refunds.setStatus(net.wit.entity.Refunds.RefurnsStatus.agree);
		refunds.setStatus(net.wit.entity.Refunds.RefurnsStatus.apply);
		refunds.setAmount(order.getAmountPaid());
		refunds.setOperator(order.getMember().getUsername());
		refunds.setMethod(net.wit.entity.Refunds.Method.online);
		refunds.setMemo(memo);
		refundsDao.persist(refunds);

		OrderSettlement orderSettlement = order.getOrderSettlement();
		orderSettlement.setReturnCharge(order.getChargeAmt());
		orderSettlement.setOrderReturnAmt(order.getAmountPaid());
		orderSettlement.setPlanFinishDate(null);
		orderSettlementDao.merge(orderSettlement);

		// 订单状态
		order.setPaymentStatus(PaymentStatus.refundapply);
		orderDao.merge(order);
		// 推送退款消息
		messageDetailsService.pushMessage(MessageType.order, true, order.getTenant(), order.getMember().getMobile() + "申请退款，订单:" + order.getSn(), order, false);

	}

	/**
	 * 打款
	 */
	public void refurns(Refunds refunds, String username) throws OrderException {
		Assert.notNull(refunds);
		Order order = refunds.getOrder();
		refunds.setStatus(RefurnsStatus.complete);
		order.setPaymentStatus(PaymentStatus.refunded);
		order.setOrderStatus(OrderStatus.completed);
		orderDao.merge(order);
		refundsDao.merge(refunds);

		OrderSettlement orderSettlement = order.getOrderSettlement();
		orderSettlement.setStatus(SettlementStatus.complete);
		orderSettlement.setFinishDate(new Date());
		orderSettlementDao.merge(orderSettlement);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.refunds);
		orderLog.setOperator(username);
		orderLog.setOrder(order);
		orderLog.setOrderStatus(OrderStatus.completed);
		orderLogDao.persist(orderLog);
	}
}