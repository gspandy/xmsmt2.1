/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import net.wit.Filter;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.controller.test.BuyVo;
import net.wit.entity.Admin;
import net.wit.entity.Appointment;
import net.wit.entity.Cart;
import net.wit.entity.CouponCode;
import net.wit.entity.Member;
import net.wit.entity.MemberRank;
import net.wit.entity.Order;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.Order.ShippingStatus;
import net.wit.entity.OrderItem;
import net.wit.entity.Payment;
import net.wit.entity.PaymentMethod;
import net.wit.entity.Receiver;
import net.wit.entity.Refunds;
import net.wit.entity.Returns;
import net.wit.entity.Shipping;
import net.wit.entity.ShippingMethod;
import net.wit.entity.Stock;
import net.wit.entity.Tenant;
import net.wit.entity.Ticket;
import net.wit.entity.Trade;
import net.wit.enums.OrderSearchStatus;
import net.wit.exception.OrderException;
import net.wit.util.BizException;

/**
 * Service - 订单
 * @author rsico Team
 * @version 3.0
 */
public interface OrderService extends BaseService<Order, Long> {

	/** 释放过期订单库存 */
	public void releaseStock();

	/**
	 * 生成订单
	 * @param cart 购物车
	 * @param receiver 收货地址
	 * @param paymentMethod 支付方式
	 * @param shippingMethod 配送方式
	 * @param couponCode 优惠码
	 * @param isInvoice 是否开据发票
	 * @param invoiceTitle 发票抬头
	 * @param useBalance 是否使用余额
	 * @param memo 附言
	 * @return 订单
	 */
	public Order build(Cart cart, Receiver receiver, PaymentMethod paymentMethod, ShippingMethod shippingMethod, CouponCode couponCode, boolean isInvoice, String invoiceTitle, boolean useBalance, String memo);

	/**
	 * @see OrderService#build(Cart, Receiver, PaymentMethod, ShippingMethod, CouponCode, boolean, String, boolean, String)
	 */
	public Order create(Cart cart, Receiver receiver, PaymentMethod paymentMethod, ShippingMethod shippingMethod, CouponCode couponCode, boolean isInvoice, String invoiceTitle, boolean useBalance, String memo, Admin operator, Appointment appointment);

	/**
	 * 积分兑换订单
	 * @see OrderService#build(Cart, Receiver, PaymentMethod, ShippingMethod, CouponCode, boolean, String, boolean, String)
	 */
	public Order createPointExchangeOrder(Cart cart, Receiver receiver, PaymentMethod paymentMethod, ShippingMethod shippingMethod, Long point, boolean isInvoice, String invoiceTitle, boolean useBalance, String memo, Admin operator,
			Appointment appointment);

	/**
	 * 更新订单
	 * @param order 订单
	 * @param operator 操作员
	 */
	public void update(Order order, String operator);

	/**
	 * 订单确认
	 * @param order 订单
	 * @param operator 操作员
	 */
	public void confirm(Order order, Admin operator);

	/**
	 * 订单完成
	 * @param order 订单
	 * @param operator 操作员
	 */
	public void complete(Order order, Admin operator)throws BizException ;

	/**
	 * 订单取消
	 * @param order 订单
	 * @param operator 操作员
	 */
	public void cancel(Order order, Admin operator);

	/**
	 * 订单支付
	 * @param order 订单
	 * @param payment 收款单
	 * @param operator 操作员
	 */
	public void payment(Order order, Payment payment, Admin operator);

	/**
	 * 订单帐户支付
	 * @param order 订单
	 * @param operator 操作员
	 */
	public void paymentByDeposit(Order order, Admin operator);

	/**
	 * 订单退款-并立即退款至会员帐户
	 * @param order 订单
	 * @param refunds 退款单
	 * @param operator 操作员
	 */
	public void refunds(Order order, Refunds refunds, Admin operator);

	/**
	 * 订单发货
	 * @param order 订单
	 * @param shipping 发货单
	 * @param operator 操作员
	 */
	public void shipping(Order order, Shipping shipping, Admin operator);

	/**
	 * 订单发货
	 * @param order 订单
	 * @param shipping 发货单
	 * @param operator 操作员
	 */
	public void shippingByMember(Order order, Shipping shipping, Member operator);

	/**
	 * 线下订单发货
	 * @param order 订单
	 * @param trade 发货子订单
	 */
	public void offlineShipping(Order order, Trade trade);

	/**
	 * 自动发货
	 * @param order
	 */
	public void autoShipping(Member member, Order order, OrderItem orderItem, Stock stock, Integer quantity);

	/**
	 * 订单退货
	 * @param order 订单
	 * @param returns 退货单
	 * @param operator 操作员
	 */
	public void returns(Order order, Returns returns, Admin operator);

	/**
	 * 查找店铺订单分页
	 * @param member 会员
	 * @param beginDate 开始日期
	 * @param endDate 结束日期
	 * @param pageable 分页信息
	 * @return 订单分页
	 */
	public Page<Order> findPage(Member member, Date beginDate, Date endDate, Pageable pageable);

	/**
	 * 查找我的订单分页
	 * @param member 会员
	 * @param beginDate 开始日期
	 * @param endDate 结束日期
	 * @param pageable 分页信息
	 * @return 订单分页
	 */
	public Page<Order> findPageMember(Member member, Date beginDate, Date endDate, Pageable pageable);

	/**
	 * 查找我的订单分页
	 * @param member 会员
	 * @param beginDate 开始日期
	 * @param endDate 结束日期
	 * @param pageable 分页信息
	 * @return 订单分页
	 */
	public Page<Order> findPageAll(Member member, Date beginDate, Date endDate, Pageable pageable);

	/**
	 * 查询订单数量
	 * @param member 业务员
	 * @param orderStatus 订单状态
	 * @param paymentStatus 支付状态
	 * @param shippingStatus 配送状态
	 * @param hasExpired 是否已过期
	 * @return 订单数量
	 */
	public long countmy(Member member, OrderStatus OrderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired);

	public Page<Order> findPageTenant(Member member, Date beginDate, Date endDate, Pageable pageable);

	/**
	 * 根据订单编号查找订单
	 * @param sn 订单编号(忽略大小写)
	 * @return 若不存在则返回null
	 */
	public Order findBySn(String sn);

	/**
	 * 查找订单
	 * @param member 会员
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @return 订单
	 */
	public List<Order> findList(Member member, Integer count, List<Filter> filters, List<net.wit.Order> orders);

	/**
	 * 查询订单
	 * @param tenant 子订单商家
	 * @param startDate 订单生成时间范围
	 * @param endDate 订单生成时间范围
	 * @param orderStatus 订单状态
	 * @param paymentStatus 支付状态
	 * @param shippingStatus 发货状态
	 * @return
	 */
	public List<Order> findList(Tenant tenant, Date startDate, Date endDate, List<OrderStatus> orderStatuses, List<ShippingStatus> shippingStatuses);

	/**
	 * 查找订单分页
	 * @param member 会员
	 * @param pageable 分页信息
	 * @return 订单分页
	 */
	public Page<Order> findPage(Member member, Pageable pageable);

	public Page<Order> findPage(List<Tenant> tenant, Pageable pageable);

	/**
	 * 查找订单分页
	 * @param member 会员
	 * @param orderStatus 订单状态
	 * @param paymentStatus 支付状态
	 * @param shippingStatus 配送状态
	 * @param hasExpired 是否已过期
	 * @param pageable 分页信息
	 * @return 商品分页
	 */
	public Page<Order> findPage(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable);

	/**
	 * 查找订单分页
	 * @param orderStatus 订单状态
	 * @param paymentStatus 支付状态
	 * @param shippingStatus 配送状态
	 * @param hasExpired 是否已过期
	 * @param pageable 分页信息
	 * @return 商品分页
	 */
	public Page<Order> findPage(OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable);

	/**
	 * 查找会员代付款订单
	 * @param member 会员
	 * @param hasExpired 是否已过期
	 * @param pageable 分页信息
	 * @return 商品分页
	 */
	public Page<Order> findWaitPay(Member member, Boolean hasExpired, Pageable pageable);

	/**
	 * 查找会员待发货订单
	 * @param member 会员
	 * @param hasExpired 是否已过期
	 * @param pageable 分页信息
	 * @return 商品分页
	 */
	public Page<Order> findWaitShipping(Member member, Boolean hasExpired, Pageable pageable);

	/**
	 * 查询订单数量
	 * @param orderStatus 订单状态
	 * @param paymentStatus 支付状态
	 * @param shippingStatus 配送状态
	 * @param hasExpired 是否已过期
	 * @return 订单数量
	 */
	public Long count(OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired);

	/**
	 * 查询订单数量
	 * @param member 业务员
	 * @param orderStatus 订单状态
	 * @param paymentStatus 支付状态
	 * @param shippingStatus 配送状态
	 * @param hasExpired 是否已过期
	 * @return 订单数量
	 */
	public Long countAll(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired);

	/**
	 * 查询等待支付订单数量
	 * @param tenant
	 * @param member 会员
	 * @return 等待支付订单数量
	 */
	public Long waitingPaymentCount(Tenant tenant, Member member);

	/**
	 * 查询等待发货订单数量
	 * @param tenant
	 * @param member 会员
	 * @return 等待发货订单数量
	 */
	public Long waitingShippingCount(Tenant tenant, Member member);

	/**
	 * 获取销售额
	 * @param beginDate 起始日期
	 * @param endDate 结束日期
	 * @return 销售额
	 */
	public BigDecimal getSalesAmount(Date beginDate, Date endDate);

	/**
	 * 获取销售量
	 * @param beginDate 起始日期
	 * @param endDate 结束日期
	 * @return 销售量
	 */
	public Integer getSalesVolume(Date beginDate, Date endDate);

	/**
	 * 根据状态过滤订单分页
	 * @param member 会员
	 * @param orderStatus
	 * @param paymentStatus
	 * @param shippingStatus
	 * @param pageable
	 * @return
	 */
	public Page<Order> findPageWithoutStatus(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Pageable pageable);

	/**
	 * @Title：createImmediately @Description： 买家下单
	 * @param buys 商品信息 商品id 商品数量
	 * @param member 下单会员
	 * @param owner 店主
	 * @param ticket 内购券
	 * @param receiver 收货地址
	 * @param paymentMethod 支付方式
	 * @param shippingMethod 配送方式
	 * @return
	 * @throws OrderException Order
	 */
	public Order createImmediately(BuyVo[] buys, Member member, Member owner, Ticket ticket, Receiver receiver, PaymentMethod paymentMethod, ShippingMethod shippingMethod) throws OrderException;

	/**
	 * @Title：findPageByTenant @Description： 商家的订单
	 * @param tenant 商家
	 * @param orderStatus 订单状态
	 * @param paymentStatus 支付状态
	 * @param shippingStatus 配送状态
	 * @param hasExpired 是否已经过期
	 * @param pageable 分页信息
	 * @return Page<Order>
	 */
	public Page<Order> findPageByTenant(Tenant tenant, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable);

	/**
	 * @Title：findPageByTenant @Description： 商家的订单
	 * @param tenant 商家
	 * @param orderStatus 订单状态
	 * @param paymentStatus 支付状态
	 * @param shippingStatus 配送状态
	 * @param hasExpired 是否已经过期
	 * @param pageable 分页信息
	 * @return Page<Order>
	 */
	public Page<Order> findPageByTenant(Tenant tenant, List<OrderStatus> orderStatuses, List<PaymentStatus> paymentStatuses, List<ShippingStatus> shippingStatuses, Boolean hasExpired, Pageable pageable);
	
	public Page<Order> findPageByTenant(Tenant tenant, List<OrderStatus> orderStatuses, List<PaymentStatus> paymentStatuses, List<ShippingStatus> shippingStatuses, String productName,String username,String sn,Date startTime,Date endTime,Boolean hasExpired, Pageable pageable);

	/**
	 * 签收
	 * @Title：sign @Description：
	 * @param order void
	 */
	public void sign(Order order);

	/**
	 * @Title：findMemberAndOwer @Description： 订单列表 买家与店主的
	 * @param member 买家
	 * @param owner 店主
	 * @param hasExpired 是否过期
	 * @param orderStatus 订单状态
	 * @param paymentStatus 支付状态
	 * @param shippingStatus 配送状态
	 * @param pageable 分页信息
	 * @return Page<Order>
	 */
	public Page<Order> findMemberAndOwer(Member member, Member owner, Boolean hasExpired, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Pageable pageable);

	/**
	 * 延长收货
	 * @param order
	 */
	public void extendAccept(Order order);

	/**
	 * 根据订单状态、支付状态、物流状态组合获取商家订单信息
	 * @param tenant
	 * @param paymentStatuses
	 * @param shippingStatuses
	 * @param pageable
	 * @return
	 */
	public Page<Order> findReturnPage(Tenant tenant, List<PaymentStatus> paymentStatuses, List<ShippingStatus> shippingStatuses, Pageable pageable);

	public Page<Order> findMemberAndOwer(Member member, Member owner, List<OrderStatus> orderStatuses, List<PaymentStatus> paymentStatuses, List<ShippingStatus> shippingStatuses, Pageable pageable);

	/**
	 * @Title：findForExport @Description：
	 * @param name 商品名
	 * @param beginDate 开始时间
	 * @param endDate 结算时间
	 * @param owerRank 店主等级
	 * @param status 订单状态 枚举类OrderSearchStatus
	 * @param sn 订单编号
	 * @param pageable 分页
	 * @return Page<Order>
	 */
	public Page<Order> findForExport(Tenant tenant, String name, Date beginDate, Date endDate, MemberRank owerRank, OrderSearchStatus status, String sn, Pageable pageable);

	/**
	 * @Title：findForExport @Description：
	 * @param name 商品名
	 * @param beginDate 开始时间
	 * @param endDate 结算时间
	 * @param owerRank 店主等级
	 * @param status 订单状态 枚举类OrderSearchStatus
	 * @param sn 订单编号
	 * @return Page<Order>
	 */
	public List<Order> findForExport(Tenant tenant, String name, Date beginDate, Date endDate, MemberRank owerRank, OrderSearchStatus status, String sn, Integer count,String memberNam);
	
	/**
	 * 获取某个月中店主的交易订单根据支付状态查询
	 * @param owner
	 * @param date
	 * @param status
	 * @return
	 */
	public List<Order> orderTimesWithMonth(Member owner, Order.PaymentStatus status);
	
	/**
	 * 根据企业,会员,订单状态和订单支付情况获取订单记录
	 * @param tenant
	 * @param member
	 * @param orderStatuses
	 * @param paymentStatuses
	 * @return
	 */
	public List<Order> getHistoryOrderByTenant(Tenant tenant,Member member ,List<OrderStatus> orderStatuses, List<PaymentStatus> paymentStatuses);
	/**
	 * 获取历史订单总金额;
	 * @param tenant
	 * @param member
	 * @param orderStatuses
	 * @param paymentStatuses
	 * @return
	 */
	public BigDecimal getHistoryOrderAmtByTenant(Tenant tenant, Member member,
			List<OrderStatus> orderStatuses, List<PaymentStatus> paymentStatuses);
	
}