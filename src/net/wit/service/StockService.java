package net.wit.service;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.Product;
import net.wit.entity.Stock;
import net.wit.entity.Tenant;

/**
 * 库存量
 * @author Administrator
 */
public interface StockService extends BaseService<Stock, Long> {

	/**
	 * 分页
	 * @param tenant
	 * @param pageable
	 * @return
	 */
	Page<Stock> findPage(Tenant tenant, Pageable pageable);

	public Stock findStock(Tenant tenant, Product product, DeliveryCenter deliveryCenter);

	/**
	 * 库存消耗
	 * @param tenant 商家信息
	 * @param product 商品信息
	 * @param deliveryCenter 库存货点
	 * @param quantity 消耗数量
	 */
	public void consume(Tenant tenant, Product product, DeliveryCenter deliveryCenter, Integer quantity);
}
