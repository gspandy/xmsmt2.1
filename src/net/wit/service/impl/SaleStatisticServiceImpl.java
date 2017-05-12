package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.wit.dao.ProductDao;
import net.wit.entity.Order;
import net.wit.entity.OrderItem;
import net.wit.entity.Product;
import net.wit.service.SaleStatisticService;

/**
 * @ClassName：SaleStatisticServiceImpl @Description：
 * @author：Chenlf
 * @date：2015年9月13日 上午11:24:46
 */
@Service("saleStatisticService")
public class SaleStatisticServiceImpl implements SaleStatisticService {

	@Resource(name = "productDaoImpl")
	private ProductDao productDao;

	public void adjustForOrder(Order order) {
		List<OrderItem> orderItems = order.getOrderItems();
		for (OrderItem ot : orderItems) {
			Product product = ot.getProduct();
			if (product.getSales() != null) {
				product.setSales(product.getSales() + ot.getQuantity().longValue());
			} else {
				product.setSales(ot.getQuantity().longValue());
			}
			productDao.merge(product);
		}
	}

}
