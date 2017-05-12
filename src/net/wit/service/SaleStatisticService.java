package net.wit.service;

import net.wit.entity.Order;

/**
 * @ClassName：SaleStatisticService
 * @Description：
 * @author：Chenlf 
 * @date：2015年9月13日 上午11:24:26 
 */
public interface SaleStatisticService {

	/**
	 * @Title：adjustForOrder
	 * @Description： 销量
	 * @param order  void
	 */
	void adjustForOrder(Order order);

}
