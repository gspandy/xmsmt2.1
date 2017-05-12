/**
 *====================================================
 * 文件名称: VstStockStrategy.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年5月29日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.domain.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.persistence.LockModeType;

import net.wit.dao.DeliveryCenterDao;
import net.wit.dao.ProductDao;
import net.wit.dao.StockDao;
import net.wit.domain.StockStrategy;
import net.wit.domain.TenantDomain;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.Order;
import net.wit.entity.OrderItem;
import net.wit.entity.Product;
import net.wit.entity.ShippingItem;
import net.wit.entity.Trade;
import net.wit.service.DeliveryCenterService;

import org.springframework.stereotype.Service;

/**
 * @ClassName: VstStockStrategy
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年5月29日 下午1:28:07
 */
@Service("stockStrategy")
public class ToholaStockStrategy implements StockStrategy {

	@Resource(name = "deliveryCenterDaoImpl")
	private DeliveryCenterDao deliveryCenterDao;

	@Resource(name = "stockDaoImpl")
	private StockDao stockDao;

	@Resource(name = "productDaoImpl")
	private ProductDao productDao;

	@Resource(name = "deliveryCenterServiceImpl")
	private DeliveryCenterService deliveryCenterService;

	@Resource
	private TenantDomain tenantDomain;

	public void lockAllocatedOrder(Order order) {

		for (OrderItem orderItem : order.getOrderItems()) {
			Product product = orderItem.getProduct();
			BigDecimal quantity = orderItem.calculateQuantity().subtract(orderItem.calculateShippedQuantity());
			if (product.getStock() != null) {
				productDao.lock(product, LockModeType.PESSIMISTIC_WRITE);
				product.setAllocatedStock(product.getAllocatedStock() + quantity.intValue());
				productDao.merge(product);
			}
		}

	}

	public void releaseAllocatedOrder(Order order) {
		for (OrderItem orderItem : order.getOrderItems()) {
			Product product = orderItem.getProduct();
			BigDecimal quantity = orderItem.calculateQuantity().subtract(orderItem.calculateShippedQuantity());
			if (product.getStock() != null) {
				productDao.lock(product, LockModeType.PESSIMISTIC_WRITE);
				product.setAllocatedStock(product.getAllocatedStock() - quantity.intValue());
				productDao.merge(product);
			}
		}
	}

	public void subtractForShipping(Order order, OrderItem orderItem, ShippingItem shippingItem, DeliveryCenter delivery) {
		Product product = orderItem.getProduct();
		BigDecimal quantity = new BigDecimal(shippingItem.getQuantity()).multiply(orderItem.getCoefficient());
		if (product.getStock() != null) {
			productDao.lock(product, LockModeType.PESSIMISTIC_WRITE);
			product.setStock(product.getStock() - quantity.intValue());
			if (order.getIsAllocatedStock()) {
				product.setAllocatedStock(product.getAllocatedStock() - quantity.intValue());
			}
			productDao.merge(product);
		}
	}

	public void adjustForShipping(Order order, OrderItem orderItem, ShippingItem shippingItem, DeliveryCenter fromDelivery) {
		if (fromDelivery == null) {
			fromDelivery = deliveryCenterDao.findDefault(order.getTenant());
		}
		subtractForShipping(order, orderItem, shippingItem, fromDelivery);
	}

	public void adjustForOrderUpdate(Order prefixOrder, Order suffixOrder) {
		releaseAllocatedOrder(prefixOrder);
		lockAllocatedOrder(suffixOrder);
	}

	public void adjustForShipping(Trade trade) {
		for (OrderItem orderItem : trade.getOrderItems()) {
			Product product = orderItem.getProduct();
			BigDecimal quantity = new BigDecimal(orderItem.getQuantity() - orderItem.getShippedQuantity()).multiply(orderItem.getCoefficient());
			if (product.getStock() != null) {
				productDao.lock(product, LockModeType.PESSIMISTIC_WRITE);
				product.setStock(product.getStock() - quantity.intValue());
				productDao.merge(product);
			}
		}
	}

}
