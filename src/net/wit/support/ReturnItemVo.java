package net.wit.support;

import net.wit.entity.OrderItem;

/**
 * @ClassName：ReturnItemVo @Description：
 * @author：Chenlf
 * @date：2015年9月19日 下午6:03:00
 */
public class ReturnItemVo {

	private OrderItem orderItem;

	private Integer quantity;

	/**
	 * 获取 orderItem
	 * @return orderItem
	 */
	public OrderItem getOrderItem() {
		return orderItem;
	}

	/**
	 * 设置 orderItem
	 * @param orderItem orderItem
	 */
	public void setOrderItem(OrderItem orderItem) {
		this.orderItem = orderItem;
	}

	/**
	 * 获取 quantity
	 * @return quantity
	 */
	public Integer getQuantity() {
		return quantity;
	}

	/**
	 * 设置 quantity
	 * @param quantity quantity
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
