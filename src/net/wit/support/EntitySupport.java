/**
 *====================================================
 * 文件名称: EntitySupport.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年8月18日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.support;

import java.math.BigDecimal;
import java.util.Date;

import net.wit.entity.Member;
import net.wit.entity.Member.BindStatus;
import net.wit.entity.Order;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.Order.ShippingStatus;
import net.wit.entity.Refunds;
import net.wit.entity.Tenant;
import net.wit.entity.Trade;

/**
 * @ClassName: EntitySupport
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年8月18日 上午11:21:04
 */
public class EntitySupport {

	public static Order createInitOrder() {
		Order order = new Order();
		order.setOrderStatus(OrderStatus.unconfirmed);
		order.setPaymentStatus(PaymentStatus.unpaid);
		order.setShippingStatus(ShippingStatus.unshipped);
		order.setFee(BigDecimal.ZERO);
		order.setFreight(BigDecimal.ZERO);
		order.setPromotionDiscount(BigDecimal.ZERO);
		order.setCouponDiscount(BigDecimal.ZERO);
		order.setOffsetAmount(BigDecimal.ZERO);
		order.setPoint(0L);
		order.setIsExtend(false);
		return order;
	}

	public static Trade createInitTrade() {
		Trade trade = new Trade();
		trade.setShippingStatus(ShippingStatus.unshipped);
		trade.setTax(BigDecimal.ZERO);
		trade.setFreight(BigDecimal.ZERO);
		trade.setOffsetAmount(BigDecimal.ZERO);
		return trade;
	}

	public static Member createInitMember() {
		Member member = new Member();
		member.setCreateDate(new Date());
		member.setModifyDate(new Date());
		member.setAmount(BigDecimal.ZERO);
		member.setBalance(BigDecimal.ZERO);
		member.setFreezeBalance(BigDecimal.ZERO);
		member.setPrivilege(0);
		member.setTotalScore(0L);
		member.setIsEnabled(true);
		member.setIsLocked(false);
		member.setLoginFailureCount(0);
		member.setPoint(0l);
		member.setRegisterIp("0.0.0.0");
		member.setBindMobile(BindStatus.none);
		member.setBindEmail(BindStatus.none);
		member.setRebateAmount(BigDecimal.ZERO);
		member.setProfitAmount(BigDecimal.ZERO);
		member.setPrivilege(0);
		member.setTotalScore(0L);
		return member;
	}
	public static Tenant createInitTenant() {
		Tenant tenant = new Tenant();
		tenant = new Tenant();
		tenant.setCode("1");
		tenant.setScore(0F);
		tenant.setTotalScore(0L);
		tenant.setScoreCount(0L);
		tenant.setHits(0L);
		tenant.setWeekHits(0L);
		tenant.setMonthHits(0L);
		tenant.setTotalAssistant(0L);
		return tenant;
	}
	public static Refunds createInitRefunds() {
		Refunds refunds = new Refunds();
		return refunds;
	}
}
