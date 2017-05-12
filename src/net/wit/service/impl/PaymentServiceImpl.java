/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.LockModeType;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.PaymentDao;
import net.wit.entity.Member;
import net.wit.entity.Order;
import net.wit.entity.Payment;
import net.wit.entity.Payment.Status;
import net.wit.entity.Payment.Type;
import net.wit.entity.Tenant;
import net.wit.service.MemberService;
import net.wit.service.OrderService;
import net.wit.service.PaymentService;
import net.wit.service.YeePayService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 收款单
 * @author rsico Team
 * @version 3.0
 */
@Service("paymentServiceImpl")
public class PaymentServiceImpl extends BaseServiceImpl<Payment, Long>implements PaymentService {

	@Resource(name = "paymentDaoImpl")
	private PaymentDao paymentDao;

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "yeePayServiceImpl")
	private YeePayService yeePayService;

	@Resource(name = "paymentDaoImpl")
	public void setBaseDao(PaymentDao paymentDao) {
		super.setBaseDao(paymentDao);
	}

	@Transactional(readOnly = true)
	public Payment findBySn(String sn) {
		return paymentDao.findBySn(sn);
	}

	public void handle(Payment payment) throws Exception {
		paymentDao.refresh(payment, LockModeType.PESSIMISTIC_WRITE);
		if (payment != null && payment.getStatus() == Status.wait) {
			if (payment.getType() == Type.payment) {
				Order order = payment.getOrder();
				if (order != null) {
					orderService.payment(order, payment, null);
				}
			} else if (payment.getType() == Type.recharge) {
				Member member = payment.getMember();
				if (member != null) {
					memberService.Recharge(member, null, payment.getEffectiveAmount(), null, null);
				}
			}
			payment.setStatus(Status.success);
			payment.setPaymentDate(new Date());
			paymentDao.merge(payment);
			Map<String, String> params = new HashMap<String, String>();
			params.put("orderNo", payment.getOrder().getSn());
			params.put("paymentSn", payment.getSn());
//			this.yeePayService.divide(params);
		}
	}
	
	public void alipayHandle(Payment payment, String sn) throws Exception {
		paymentDao.refresh(payment, LockModeType.PESSIMISTIC_WRITE);
		if (payment != null && payment.getStatus() == Status.wait) {
			if (payment.getType() == Type.payment) {
				Order order = payment.getOrder();
				if (order != null) {
					orderService.payment(order, payment, null);
				}
			} else if (payment.getType() == Type.recharge) {
				Member member = payment.getMember();
				if (member != null) {
					memberService.Recharge(member, null, payment.getEffectiveAmount(), null, null);
				}
			}
			payment.setStatus(Status.success);
			payment.setPaymentDate(new Date());
			payment.setSn(sn);// 更新支付单号为支付宝交易号
			paymentDao.merge(payment);
			Map<String, String> params = new HashMap<String, String>();
			params.put("orderNo", payment.getOrder().getSn());
			params.put("paymentSn", payment.getSn());
//			this.yeePayService.divide(params);
		}
	}

	@Transactional(readOnly = true)
	public Page<Payment> findPage(Member member, Pageable pageable, Payment.Type type) {
		return paymentDao.findPage(member, pageable, type);
	}

	@Transactional(readOnly = true)
	public Page<Payment> findPage(Tenant tenant, Pageable pageable) {
		return paymentDao.findPage(tenant, pageable);
	}
}