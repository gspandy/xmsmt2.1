/**
 *====================================================
 * 文件名称: StockDomain.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年5月14日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.domain;

import net.wit.entity.DeliveryCenter;
import net.wit.entity.Order;
import net.wit.entity.OrderItem;
import net.wit.entity.ShippingItem;
import net.wit.entity.Trade;

/**
 * @ClassName: StockDomain
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年5月14日 下午3:54:01
 */
public interface StockStrategy {

	/**
	 * 商家库存锁定
	 * @param order 订单信息
	 */
	public void lockAllocatedOrder(Order order);

	/** 释放订单库存 */
	public void releaseAllocatedOrder(Order order);

	/** 发货减库存 */
	public void subtractForShipping(Order order, OrderItem orderItem, ShippingItem shippingItem, DeliveryCenter delivery);

	/** 发货减库存 */
	public void adjustForShipping(Order order, OrderItem orderItem, ShippingItem shippingItem, DeliveryCenter fromDelivery);

	/** 统一减库存 */
	public void adjustForShipping(Trade trade);

	/**
	 * 订单更新-商家库存调整
	 * @param prefixOrder 原订单信息
	 * @param suffixOrder 修改订单信息
	 */
	public void adjustForOrderUpdate(Order prefixOrder, Order suffixOrder);

	

}
