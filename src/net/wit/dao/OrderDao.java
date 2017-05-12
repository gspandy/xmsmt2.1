/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import net.wit.Filter;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Member;
import net.wit.entity.MemberRank;
import net.wit.entity.Order;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.Order.ShippingStatus;
import net.wit.entity.Tenant;
import net.wit.enums.OrderSearchStatus;
import net.wit.service.OrderService;

/**
 * Dao - 订单
 * @author rsico Team
 * @version 3.0
 */
public interface OrderDao extends BaseDao<Order, Long> {

	/**
	 * 根据订单编号查找订单
	 * @param sn 订单编号(忽略大小写)
	 * @return 订单，若不存在则返回null
	 */
	Order findBySn(String sn);

	/**
	 * 查找订单
	 * @param member 会员
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @return 订单
	 */
	List<Order> findList(Member member, Integer count, List<Filter> filters, List<net.wit.Order> orders);

	/**
	 * 查找订单分页
	 * @param member 会员
	 * @param pageable 分页信息
	 * @return 订单分页
	 */
	Page<Order> findPage(Member member, Pageable pageable);

	/**
	 * 查找订单分页
	 * @param orderStatus 订单状态
	 * @param paymentStatus 支付状态
	 * @param shippingStatus 配送状态
	 * @param hasExpired 是否已过期
	 * @param pageable 分页信息
	 * @return 商品分页
	 */
	Page<Order> findPage(OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable);

	/**
	 * 查询订单数量
	 * @param orderStatus 订单状态
	 * @param paymentStatus 支付状态
	 * @param shippingStatus 配送状态
	 * @param hasExpired 是否已过期
	 * @return 订单数量
	 */
	Long count(OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired);

	/**
	 * 查询等待支付订单数量
	 * @param tenant
	 * @param member 会员
	 * @return 等待支付订单数量
	 */
	Long waitingPaymentCount(Tenant tenant, Member member);

	/**
	 * 查询等待发货订单数量
	 * @param tenant
	 * @param member 会员
	 * @return 等待发货订单数量
	 */
	Long waitingShippingCount(Tenant tenant, Member member);

	/**
	 * 获取销售额
	 * @param beginDate 起始日期
	 * @param endDate 结束日期
	 * @return 销售额
	 */
	BigDecimal getSalesAmount(Date beginDate, Date endDate);

	/**
	 * 获取销售量
	 * @param beginDate 起始日期
	 * @param endDate 结束日期
	 * @return 销售量
	 */
	Integer getSalesVolume(Date beginDate, Date endDate);

	/**
	 * 释放过期订单库存
	 */
	void releaseStock();

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
	Page<Order> findPage(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable);

	/**
	 * 查找店铺订单分页
	 * @param member 会员
	 * @param beginDate 开始时间
	 * @param endDate 结束时间
	 * @param pageable 分页信息
	 * @return 商品分页
	 */
	Page<Order> findPage(Member member, Date beginDate, Date endDate, Pageable pageable);

	/**
	 * 根据企业id筛选信息
	 * @param tenantId
	 * @param pageable
	 * @return
	 */
	Page<Order> findPage(List<Tenant> tenant, Pageable pageable);

	/**
	 * 查找我的订单分页
	 * @param member 会员
	 * @param beginDate 开始时间
	 * @param endDate 结束时间
	 * @param pageable 分页信息
	 * @return 商品分页
	 */
	Page<Order> findPageMember(Member member, Date beginDate, Date endDate, Pageable pageable);

	/**
	 * 查找全部订单分页
	 * @param member 会员
	 * @param beginDate 开始时间
	 * @param endDate 结束时间
	 * @param pageable 分页信息
	 * @return 商品分页
	 */
	Page<Order> findPageAll(Member member, Date beginDate, Date endDate, Pageable pageable);

	Long countAll(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired);

	long countmy(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired);

	/**
	 * @see OrderService#findList(Tenant, Date, Date, OrderStatus, PaymentStatus, ShippingStatus)
	 */
	List<Order> findList(Tenant tenant, Date startDate, Date endDate, List<OrderStatus> orderStatuses, List<ShippingStatus> shippingStatuses);

	/**
	 * 查找店铺订单分页
	 * @param member 会员
	 * @param beginDate 开始时间
	 * @param endDate 结束时间
	 * @param pageable 分页信息
	 * @return 商品分页
	 */
	public Page<Order> findPageTenant(Member member, Date beginDate, Date endDate, Pageable pageable);

	/**
	 * 查找会员代付款订单
	 * @param member 会员
	 * @param hasExpired 是否已过期
	 * @param pageable 分页信息
	 * @return 商品分页
	 */
	public Page<Order> findWaitPay(Member member, Boolean hasExpired, Pageable pageable);

	public Page<Order> findWaitShipping(Member member, Boolean hasExpired, Pageable pageable);

	public Page<Order> findPageWithoutStatus(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Pageable pageable);

	/**
	 * @Title：findPageByTenant @Description：
	 * @param tenant
	 * @param orderStatus
	 * @param paymentStatus
	 * @param shippingStatus
	 * @param hasExpired
	 * @param pageable
	 * @return Page<Order>
	 */
	Page<Order> findPageByTenant(Tenant tenant, List<OrderStatus> orderStatuses, List<PaymentStatus> paymentStatuses, List<ShippingStatus> shippingStatuses, Boolean hasExpired, Pageable pageable);
	
    Page<Order> findPageByTenant(Tenant tenant,
			List<OrderStatus> orderStatuses,
			List<PaymentStatus> paymentStatuses,
			List<ShippingStatus> shippingStatuses, String productName,
			String username, String sn, Date startTime, Date endTime,
			Boolean hasExpired, Pageable pageable);

	/**
	 * @Title：findPageByTenant @Description：
	 * @param tenant
	 * @param orderStatus
	 * @param paymentStatus
	 * @param shippingStatus
	 * @param hasExpired
	 * @param pageable
	 * @return Page<Order>
	 */
	Page<Order> findPageByTenant(Tenant tenant, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable);

	/**
	 * @Title：findMemberAndOwer @Description：
	 * @param member
	 * @param owner
	 * @param orderStatus
	 * @param paymentStatus
	 * @param shippingStatus
	 * @param pageable
	 * @return Page<Order>
	 */
	Page<Order> findMemberAndOwer(Member member, Member owner, Boolean hasExpired, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Pageable pageable);

	/**
	 * @Title：findMemberAndOwer @Description：
	 * @param member
	 * @param owner
	 * @param orderStatus
	 * @param paymentStatus
	 * @param shippingStatus
	 * @param pageable
	 * @return Page<Order>
	 */
	Page<Order> findMemberAndOwer(Member member, Member owner, List<OrderStatus> orderStatuses, List<PaymentStatus> paymentStatuses, List<ShippingStatus> shippingStatuses, Pageable pageable);

	/**
	 * 根据订单状态、支付状态、物流状态组合获取商家退款（货）单
	 * @param tenant
	 * @param orderStatuses
	 * @param paymentStatuses
	 * @param shippingStatuses
	 * @param pageable
	 * @return
	 */
	public Page<Order> findReturnPage(Tenant tenant, List<PaymentStatus> paymentStatuses, List<ShippingStatus> shippingStatuses, Pageable pageable);

	/**
	 * @param tenant
	 * @Title：findForExport @Description：
	 * @param name
	 * @param beginDate
	 * @param endDate
	 * @param owerRank
	 * @param status
	 * @param sn
	 * @param pageable
	 * @return Page<Order>
	 */
	Page<Order> findForExport(Tenant tenant, String name, Date beginDate, Date endDate, MemberRank owerRank, OrderSearchStatus status, String sn, Pageable pageable);

	/**
	 * @Title：findForExport @Description：
	 * @param tenant
	 * @param name
	 * @param beginDate
	 * @param endDate
	 * @param owerRank
	 * @param status
	 * @param sn
	 * @param count
	 * @return List<Order>
	 */
	List<Order> findForExport(Tenant tenant, String name, Date beginDate, Date endDate, MemberRank owerRank, OrderSearchStatus status, String sn, Integer count,String memberNam);

	/**
	 * 
	 * @param owner
	 * @param beginDate
	 * @param endDate
	 * @param status
	 * @return
	 */
	List<Order> findOrderByOwner(Member owner, Date beginDate, Date endDate,
			PaymentStatus status);
	/**
	 * 根据企业 买家和订单状态 支付状态查找订单
	 * @param tenant
	 * @param orderStatuses
	 * @param paymentStatuses
	 * @return
	 */
	public List<Order> findOrdersByTenant(Tenant tenant,Member member,
			List<OrderStatus> orderStatuses, List<PaymentStatus> paymentStatuses);

}