/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.LockModeType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.wit.Filter;
import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.ProductDao;
import net.wit.entity.Area;
import net.wit.entity.Attribute;
import net.wit.entity.Brand;
import net.wit.entity.Community;
import net.wit.entity.Location;
import net.wit.entity.Member;
import net.wit.entity.Product;
import net.wit.entity.Product.OrderType;
import net.wit.entity.ProductCategory;
import net.wit.entity.ProductCategoryTenant;
import net.wit.entity.Promotion;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.service.ProductService;
import net.wit.service.StaticService;

/**
 * Service - 商品
 * @author rsico Team
 * @version 3.0
 */
@Service("productServiceImpl")
public class ProductServiceImpl extends BaseServiceImpl<Product, Long>implements ProductService, DisposableBean {

	/** 查看点击数时间 */
	private long viewHitsTime = System.currentTimeMillis();

	@Resource(name = "ehCacheManager")
	private CacheManager cacheManager;

	@Resource(name = "productDaoImpl")
	private ProductDao productDao;

	@Resource(name = "staticServiceImpl")
	private StaticService staticService;

	@Resource(name = "productDaoImpl")
	public void setBaseDao(ProductDao productDao) {
		super.setBaseDao(productDao);
	}

	@Transactional(readOnly = true)
	public boolean snExists(String sn) {
		return productDao.snExists(sn);
	}

	@Transactional(readOnly = true)
	public Product findBySn(String sn) {
		return productDao.findBySn(sn);
	}

	@Transactional(readOnly = true)
	public boolean snUnique(String previousSn, String currentSn) {
		if (StringUtils.equalsIgnoreCase(previousSn, currentSn)) {
			return true;
		} else {
			if (productDao.snExists(currentSn)) {
				return false;
			} else {
				return true;
			}
		}
	}

	@Transactional(readOnly = true)
	public List<Product> search(String keyword, Boolean isGift, Integer count) {
		return productDao.search(keyword, isGift, count);
	}

	@Transactional(readOnly = true)
	public List<Product> findList(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType, Integer count, List<Filter> filters, List<Order> orders) {
		return productDao.findList(productCategory, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, orderType, count, filters, orders);
	}

	@Transactional(readOnly = true)
	public List<Product> findList(Set<ProductCategory> productCategories, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Tenant tenant, Area area, Boolean periferal, OrderType orderType, Integer count, List<Filter> filters, List<Order> orders) {
		return productDao.findList(productCategories, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, community, tenant, area, periferal, orderType, count, filters, orders);
	}

	@Transactional(readOnly = true)
	@Cacheable("product")
	public List<Product> findList(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion) {
		return productDao.findList(productCategory, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, orderType, count, filters, orders);
	}

	@Transactional(readOnly = true)
	@Cacheable("product")
	public List<Product> findList(Set<ProductCategory> productCategories, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Tenant tenant, Area area, Boolean periferal, OrderType orderType, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion) {
		return productDao.findList(productCategories, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, community, tenant, area, periferal, orderType, count, filters, orders);
	}

	@Transactional(readOnly = true)
	public List<Product> findList(ProductCategory productCategory, Date beginDate, Date endDate, Integer first, Integer count) {
		return productDao.findList(productCategory, beginDate, endDate, first, count);
	}

	@Transactional(readOnly = true)
	public List<Object[]> findSalesList(Date beginDate, Date endDate, Integer count) {
		return productDao.findSalesList(beginDate, endDate, count);
	}
	@Override
	public Page<Object[]> findSalesPage(Pageable pageable,Date beginDate,Date endDate,Tenant tenant){
		return productDao.findSalesPage(pageable,beginDate,endDate,tenant);
	}

	@Transactional(readOnly = true)
	public Page<Product> findPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, Location location, BigDecimal distatce, OrderType orderType, Pageable pageable) {
		return productDao.findPage(productCategory, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, community, area, periferal, location, distatce, orderType, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Product> findPage(Long tenantId, String searchValue, Pageable pageable) {
		return productDao.findPage(tenantId, searchValue, pageable);
		// return null;
	}

	@Transactional(readOnly = true)
	public Page<Product> findPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, OrderType orderType, String phonetic, String keyword, Pageable pageable) {
		return productDao.findPage(productCategory, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, community, area, periferal, orderType, phonetic, keyword, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Product> searchPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, OrderType orderType, String phonetic, String keyword, Pageable pageable) {
		return productDao.findPage(productCategory, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, community, area, periferal, orderType, phonetic, keyword, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Product> findPageByChannel(Set<ProductCategory> productCategorys, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, OrderType orderType, String phonetic, String keyword, Pageable pageable) {
		return productDao.findPageByChannel(productCategorys, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, community, area, periferal, orderType, phonetic, keyword,
				pageable);
	}

	@Transactional(readOnly = true)
	public Page<Product> findPage(Member member, Pageable pageable) {
		return productDao.findPage(member, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Product> findPage(Member member, ProductCategory productCategory, Brand brand, Pageable pageable) {
		return productDao.findPage(member, productCategory, brand, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Product> findPage(Member member, Integer days, ProductCategory productCategory, Pageable pageable) {
		return productDao.findPage(member, days, productCategory, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Product> findMyPage(Tenant tenant, ProductCategory productCategory, ProductCategoryTenant productCategoryTenant, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice,
			BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType, Pageable pageable) {
		return productDao.findMyPage(tenant, productCategory, productCategoryTenant, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, orderType, pageable);
	}

	@Transactional(readOnly = true)
	public Long count(Tenant tenant, Member favoriteMember, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert) {
		return productDao.count(tenant, favoriteMember, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert);
	}

	@Transactional(readOnly = true)
	public boolean isPurchased(Member member, Product product) {
		return productDao.isPurchased(member, product);
	}

	public long viewHits(Long id) {
		Ehcache cache = cacheManager.getEhcache(Product.HITS_CACHE_NAME);
		Element element = cache.get(id);
		Long hits;
		if (element != null) {
			hits = (Long) element.getObjectValue();
		} else {
			Product product = productDao.find(id);
			if (product == null) {
				return 0L;
			}
			hits = product.getHits();
		}
		hits++;
		cache.put(new Element(id, hits));
		long time = System.currentTimeMillis();
		if (time > viewHitsTime + Product.HITS_CACHE_INTERVAL) {
			viewHitsTime = time;
			updateHits();
			cache.removeAll();
		}
		return hits;
	}

	public void destroy() throws Exception {
		updateHits();
	}

	/**
	 * 更新点击数
	 */
	@SuppressWarnings("unchecked")
	private void updateHits() {
		Ehcache cache = cacheManager.getEhcache(Product.HITS_CACHE_NAME);
		List<Long> ids = cache.getKeys();
		for (Long id : ids) {
			Product product = productDao.find(id);
			if (product != null) {
				productDao.lock(product, LockModeType.PESSIMISTIC_WRITE);
				Element element = cache.get(id);
				long hits = (Long) element.getObjectValue();
				long increment = hits - product.getHits();
				Calendar nowCalendar = Calendar.getInstance();
				Calendar weekHitsCalendar = DateUtils.toCalendar(product.getWeekHitsDate());
				Calendar monthHitsCalendar = DateUtils.toCalendar(product.getMonthHitsDate());
				if (nowCalendar.get(Calendar.YEAR) != weekHitsCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.WEEK_OF_YEAR) > weekHitsCalendar.get(Calendar.WEEK_OF_YEAR)) {
					product.setWeekHits(increment);
				} else {
					product.setWeekHits(product.getWeekHits() + increment);
				}
				if (nowCalendar.get(Calendar.YEAR) != monthHitsCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.MONTH) > monthHitsCalendar.get(Calendar.MONTH)) {
					product.setMonthHits(increment);
				} else {
					product.setMonthHits(product.getMonthHits() + increment);
				}
				product.setHits(hits);
				product.setWeekHitsDate(new Date());
				product.setMonthHitsDate(new Date());
				productDao.merge(product);
			}
		}
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review", "consultation" }, allEntries = true)
	public void save(Product product) {
		Assert.notNull(product);

		super.save(product);
		productDao.flush();
		// staticService.build(product);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review", "consultation" }, allEntries = true)
	public Product update(Product product) {
		Assert.notNull(product);

		Product pProduct = super.update(product);
		productDao.flush();
		// staticService.build(pProduct);
		return pProduct;
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review", "consultation" }, allEntries = true)
	public Product update(Product product, String... ignoreProperties) {
		return super.update(product, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review", "consultation" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review", "consultation" }, allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory", "review", "consultation" }, allEntries = true)
	public void delete(Product product) {
		if (product != null) {
			staticService.delete(product);
		}
		super.delete(product);
	}

	public Page<Product> search(String keyword, String phonetic, BigDecimal startPrice, BigDecimal endPrice, OrderType orderType, Pageable pageable) {
		return productDao.search(keyword, phonetic, startPrice, endPrice, orderType, pageable);
	}

	public Page<Product> mobileFindPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, Location location, BigDecimal distatce, OrderType orderType, Pageable pageable) {
		return productDao.mobileFindPage(productCategory, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, community, area, periferal, location, distatce, orderType, pageable);
	}

	public Page<Product> searchPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, OrderType orderType, String phonetic, String keyword, Location location, BigDecimal distatce, Pageable pageable) {
		return productDao.findPage(productCategory, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, community, area, periferal, orderType, phonetic, keyword, location, distatce,
				pageable);
	}

	public Page<Product> mobileSearchPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, OrderType orderType, String phonetic, String keyword, Location location, BigDecimal distatce, Pageable pageable) {
		return productDao.mobileFindPage(productCategory, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, community, area, periferal, orderType, phonetic, keyword, location,
				distatce, pageable);
	}

	public List<Product> findList(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Tenant tenant, Area area, Boolean periferal, OrderType orderType, Integer count, List<Filter> filters, List<Order> orders) {
		return productDao.findList(productCategory, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, community, tenant, area, periferal, orderType, count, filters, orders);
	}

	public Page<Product> findPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType, Pageable pageable) {
		return productDao.findPage(productCategory, brand, promotion, tags, attributeValue, startPrice, endPrice, true, true, null, false, null, null, null, null, null, null, null, orderType, pageable);
	}

	public Page<Product> searchPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, List<Tag> unionTags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable,
			Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, OrderType orderType, String phonetic, String keyword, Pageable pageable) {
		return productDao.findPage(productCategory, brand, promotion, tags, unionTags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, community, area, periferal, orderType, phonetic, keyword,
				pageable);
	}

	public Page<Product> findUnionPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, List<Tag> unionTags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable,
			Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, Location location, BigDecimal distatce, String phonetic, String keyword, OrderType orderType,
			Pageable pageable) {
		return productDao.findUnionPage(productCategory, brand, promotion, tags, unionTags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, community, area, periferal, location, distatce, phonetic,
				keyword, orderType, pageable);
	}

	@Transactional(readOnly = true)
	public List<Product> productTenantSelect(String q, Long tenantId, Boolean b, int i) {
		return productDao.productTenantSelect(q, tenantId, b, i);
	}

	@Override
	public List<Product> findList(Tenant tenant, Boolean isList) {
		return productDao.findList(tenant, isList);
	}

}