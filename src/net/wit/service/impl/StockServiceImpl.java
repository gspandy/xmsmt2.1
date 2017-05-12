package net.wit.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.StockDao;
import net.wit.domain.TenantDomain;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.Product;
import net.wit.entity.Stock;
import net.wit.entity.Tenant;
import net.wit.service.DeliveryCenterService;
import net.wit.service.StockService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("stockServiceImpl")
public class StockServiceImpl extends BaseServiceImpl<Stock, Long> implements StockService {

	@Resource(name = "stockDaoImpl")
	private StockDao stockDao;

	@Resource(name = "deliveryCenterServiceImpl")
	private DeliveryCenterService deliveryCenterService;

	@Resource
	private TenantDomain tenantDomain;

	@Resource(name = "stockDaoImpl")
	public void setBaseDao(StockDao stockDao) {
		super.setBaseDao(stockDao);
	}

	public Stock findStock(Tenant tenant, Product product, DeliveryCenter deliveryCenter) {
		if (deliveryCenter == null) {
			deliveryCenter = deliveryCenterService.findDefault(tenant);
		}
		return stockDao.findStock(tenant, product, deliveryCenter);
	}

	@Transactional(readOnly = true)
	public Page<Stock> findPage(Tenant tenant, Pageable pageable) {
		return stockDao.findPage(tenant, pageable);
	}

	public void consume(Tenant tenant, Product product, DeliveryCenter deliveryCenter, Integer quantity) {
		Stock stock = stockDao.findStock(tenant, product, deliveryCenter);
		if (stock == null) {
			return;
		}
		stock.setAllocatedStock(stock.getAllocatedStock().subtract(new BigDecimal(quantity)));
		stock.setStock(stock.getStock().subtract(new BigDecimal(quantity)));
		stockDao.merge(stock);
	}
}
