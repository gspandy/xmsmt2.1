/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.OrderDao;
import net.wit.dao.OrderItemDao;
import net.wit.dao.OrderLogDao;
import net.wit.dao.OrderSettlementDao;
import net.wit.dao.RefundsDao;
import net.wit.dao.ReturnsDao;
import net.wit.entity.Member;
import net.wit.entity.MessageDetails.MessageType;
import net.wit.entity.Order;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.Order.ShippingStatus;
import net.wit.entity.OrderItem;
import net.wit.entity.OrderLog;
import net.wit.entity.OrderSettlement;
import net.wit.entity.Pic;
import net.wit.entity.Refunds;
import net.wit.entity.Returns;
import net.wit.entity.Returns.ReturnStatus;
import net.wit.entity.ReturnsItem;
import net.wit.entity.Sn;
import net.wit.entity.Tenant;
import net.wit.entity.Trade;
import net.wit.exception.OrderException;
import net.wit.service.MessageDetailsService;
import net.wit.service.OrderService;
import net.wit.service.ReturnsService;
import net.wit.service.SnService;
import net.wit.support.ReturnItemVo;
import net.wit.util.CacheUtil;
import net.wit.util.DateUtil;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Service - 退货单
 * @author rsico Team
 * @version 3.0
 */
@Service("returnsServiceImpl")
public class ReturnsServiceImpl extends BaseServiceImpl<Returns, Long>implements ReturnsService {

	@Resource(name = "returnsDaoImpl")
	public void setBaseDao(ReturnsDao returnsDao) {
		super.setBaseDao(returnsDao);
	}

	@Resource(name = "orderLogDaoImpl")
	private OrderLogDao orderLogDao;

	@Resource(name = "returnsDaoImpl")
	private ReturnsDao returnsDao;

	@Resource(name = "orderDaoImpl")
	private OrderDao orderDao;

	@Resource(name = "messageDetailsServiceImpl")
	private MessageDetailsService messageDetailsService;

	@Resource(name = "orderItemDaoImpl")
	private OrderItemDao orderItemDao;

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;

	@Resource(name = "orderSettlementDaoImpl")
	private OrderSettlementDao orderSettlementDao;

	@Resource(name = "snServiceImpl")
	private SnService snService;

	@Resource(name = "refundsDaoImpl")
	private RefundsDao refundsDao;

	public Page<Returns> findPage(Tenant tenant, ReturnStatus returnStatus, Pageable pageable) {
		return returnsDao.findPage(tenant, returnStatus, pageable);
	}

	public void reject(Returns returns) {
		Order order = returns.getOrder();
		OrderSettlement orderSettlement = order.getOrderSettlement();
		orderSettlement.setOrderSettleAmt(orderSettlement.getOrderSettleAmt().add(orderSettlement.getOrderReturnAmt())); //回加退货金额
		orderSettlement.setSettleCharge(orderSettlement.getSettleCharge().add(orderSettlement.getReturnCharge())); //回加退货佣金
		orderSettlement.setReturnCharge(new BigDecimal(0));
		orderSettlement.setOrderReturnAmt(new BigDecimal(0));
		orderSettlementDao.merge(orderSettlement);
		returns.setOrderStat(ReturnStatus.reject);
		returnsDao.merge(returns);
	}

	public void agreeReturn(Returns returns) throws OrderException {
		returns.setOrderStat(ReturnStatus.agree);
		returnsDao.merge(returns);
	}

	/**
	 * 申请退货
	 */
	public Returns apply(List<ReturnItemVo> returnVos, Order order, String memo, String reason, String img1, String img2, String img3) throws OrderException {
		if (order == null)
			throw new OrderException("订单为空");
		if (!order.isReturnsAllowed())
			throw new OrderException("不允许退货");
		if (order.isExpired() || !order.getPaymentStatus().equals(PaymentStatus.paid) || order.getOrderStatus().equals(OrderStatus.cancelled)
				|| (!(order.getShippingStatus().equals(ShippingStatus.shipped) || order.getShippingStatus().equals(ShippingStatus.accept))))
			throw new OrderException("订单无法退货");

		Returns returns = new Returns();
		Tenant tenant = order.getTenant();
		Member member = order.getMember();

		returns.setOrder(order);
		returns.setSn(snService.generate(Sn.Type.returns));
		returns.setOperator(order.getMember().getUsername());
		returns.setMemo(memo);
		returns.setReason(reason);
		Pic pic1=null;
		Pic pic2 =null;
		Pic pic3 =null;
		if (img1 != null && !"".equals(img1)) {
			pic1= new Pic();
			pic1.setId(new Long(img1));
			returns.setImg1(pic1);
		}
		if (img2 != null && !"".equals(img2)) {
			pic2 = new Pic();
			pic2.setId(new Long(img1));
			returns.setImg2(pic2);
		}
		if (img3 != null && !"".equals(img3)) {
			pic3 = new Pic();
			pic3.setId(new Long(img1));
			returns.setImg3(pic3);
		}

		if (order.getShippingStatus().equals(Order.ShippingStatus.accept)) {
			Date modifyDate = order.getModifyDate();
			if ((new Date()).after(DateUtil.transpositionDate(modifyDate, Calendar.DAY_OF_YEAR, Integer.valueOf(CacheUtil.getParamValueByName("FORCE_RETURN_DATE")) + Integer.valueOf(CacheUtil.getParamValueByName("AUTO_RECEIPT_DATE"))))) {
				throw new OrderException("订单已经过了退货期,无法退货");
			} else if ((new Date()).before(DateUtil.transpositionDate(modifyDate, Calendar.DAY_OF_YEAR, Integer.valueOf(CacheUtil.getParamValueByName("FORCE_RETURN_DATE"))))) {
				returns.setOrderStat(ReturnStatus.agree);
			} else {
				returns.setOrderStat(ReturnStatus.apply);
			}
		}
		returns.setDeliveryCenter(tenant.getDefaultDeliveryCenter());
		returns.setDeliveryCorp(tenant.getDefaultDeliveryCenter().getName());
		returns.setAddress(tenant.getAddress());
		returns.setPhone(tenant.getTelephone());
		returns.setFreight(new BigDecimal(0));

		BigDecimal orderReturnAmt = new BigDecimal(0);
		BigDecimal returnCharge = new BigDecimal(0);
		for (ReturnItemVo vo : returnVos) {
			Integer quantity = vo.getQuantity();
			OrderItem orderItem = vo.getOrderItem();
			if (quantity == null || quantity > orderItem.getQuantity()) {
				throw new OrderException(orderItem.getFullName() + "退货数量为空或者超出订单数量");
			}
			ReturnsItem returnsItem = new ReturnsItem();
			returnsItem.setName(orderItem.getFullName());
			returnsItem.setQuantity(quantity);
			returnsItem.setPrice(orderItem.getPrice());
			returnsItem.setAmount(orderItem.getPrice().multiply(new BigDecimal(quantity)));
			returnsItem.setSn(orderItem.getSn());
			returnsItem.setReturns(returns);
			returns.getReturnsItems().add(returnsItem);
//			returns.setTrade(new Trade());

			orderItem.setReturnQuantity(quantity);
			orderItemDao.merge(orderItem);

			orderReturnAmt = orderReturnAmt.add(orderItem.getPrice().multiply(new BigDecimal(quantity)));
			returnCharge = returnCharge.add(orderItem.getProductRent().multiply(new BigDecimal(quantity)));

		}
		// 处理结算表
		OrderSettlement orderSettlement = order.getOrderSettlement();
		orderSettlement.setOrderReturnAmt(orderReturnAmt);
		orderSettlement.setReturnCharge(returnCharge);
		orderSettlement.setPlanFinishDate(null);
		orderSettlementDao.merge(orderSettlement);

		order.setShippingStatus(ShippingStatus.apply);
		orderDao.merge(order);

		OrderLog log = new OrderLog();
		log.setContent(member.getUsername() + "发起退货，订单sn为" + order.getSn());
		log.setOperator(member.getUsername());
		log.setType(OrderLog.Type.returns);
		log.setOrder(order);
		orderLogDao.persist(log);
		returnsDao.persist(returns);
		// 推送退货消息
		messageDetailsService.pushMessage(MessageType.order, true, order.getTenant(), order.getMember().getMobile() + "申请退款，订单:" + order.getSn(), order, false);

		return returns;
	}

	/**
	 * 填写物流单号
	 */
	public void edit(Returns returns, String trackingNo) throws OrderException {
		Assert.notNull(returns);
		Order order = returns.getOrder();
		returns.setTrackingNo(trackingNo);
		returns.setOrderStat(ReturnStatus.return_ing);
		returnsDao.persist(returns);
		// 推送退货消息
		messageDetailsService.pushMessage(MessageType.order, true, order.getTenant(), order.getMember().getMobile() + "填写物流单，订单:" + order.getSn(), order, false);

	}


	public void returns(Returns returns, String username) throws OrderException {
		Assert.notNull(returns);
		Order order = returns.getOrder();
		order.setShippingStatus(ShippingStatus.returned);
		orderDao.merge(order);

		returns.setOrderStat(ReturnStatus.complete);
		returnsDao.persist(returns);

		Refunds refunds = new Refunds();
		refunds.setOrder(order);
		refunds.setSn(snService.generate(net.wit.entity.Sn.Type.refunds));
		refunds.setStatus(net.wit.entity.Refunds.RefurnsStatus.agree);
		refunds.setAmount(returns.getAmount());
		refunds.setOperator(order.getMember().getUsername());
		refunds.setMethod(net.wit.entity.Refunds.Method.online);
		refunds.setMemo("退货单:" + returns.getSn() + "产生");
		refundsDao.persist(refunds);

		// 推送退款消息
		messageDetailsService.pushMessage(MessageType.order, true, order.getTenant(), order.getMember().getMobile() + "退款单,由退货单:" + returns.getSn(), order, false);

	}

}