package net.wit.dao;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Area;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.Product;
import net.wit.entity.Stock;
import net.wit.entity.Tenant;

/**
 * 库存量
 * @author Administrator
 */
public interface StockDao extends BaseDao<Stock, Long> {

	/**
	 * 分页
	 * @param tenant
	 * @param pageable
	 * @return
	 */
	Page<Stock> findPage(Tenant tenant, Pageable pageable);

	/**
	 * 获取商家商品的库存信息
	 * @param tenant
	 * @param product
	 * @param deliveryCenter
	 * @return
	 */
	public Stock findStock(Tenant tenant, Product product, DeliveryCenter deliveryCenter);

	/**
	 * 分配销售商
	 * 
	 * @param area
	 *            城市
	 * @param product
	 *            产品
	 * @param quantity
	 *            数量
	 * @return 库存
	 */
	Stock find(Area area, Product product, Integer quantity);
}
