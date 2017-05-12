package net.wit.service;

import java.util.Date;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Member;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.Order.ShippingStatus;
import net.wit.entity.Trade;

/**
 * @ClassName: TradeApply
 * @Description: 申请信息
 * @author Administrator
 * @date 2014年8月15日 上午10:16:06
 */
public interface TradeService extends BaseService<Trade, Long> {

	/**
	 * 根据验证码查找订单
	 * @param code 验证码(忽略大小写)
	 * @return 若不存在则返回null
	 */
	public Trade findBySn(String code);

	/**
	 * 查找订单分页
	 * @param orderStatus 订单状态
	 * @param paymentStatus 支付状态
	 * @param shippingStatus 配送状态
	 * @param hasExpired 是否已过期
	 * @param pageable 分页信息
	 * @return 商品分页
	 */
	public Page<Trade> findPage(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable);
	
	/**
	 * 根据会员查找子订单
	 * @param member	会员
	 * @param pageable	分页信息
	 * @return
	 */
	public Page<Trade> findPage(Member member, Pageable pageable);
	
	/**
	 * 查找订单分页
	 * @param member 会员
	 * @param beginDate 开始日期
	 * @param endDate 结束日期
	 * @param pageable 分页信息
	 * @return 订单分页
	 */
	public Page<Trade> findPage(Member member, Date beginDate, Date endDate, Pageable pageable);

	public long countTenant(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired);

}
