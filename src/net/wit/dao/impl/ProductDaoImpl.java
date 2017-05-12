/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import net.wit.Filter;
import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.Setting;
import net.wit.dao.DeliveryCenterDao;
import net.wit.dao.GoodsDao;
import net.wit.dao.ProductDao;
import net.wit.dao.SnDao;
import net.wit.entity.Area;
import net.wit.entity.Attribute;
import net.wit.entity.Brand;
import net.wit.entity.Community;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.Goods;
import net.wit.entity.Location;
import net.wit.entity.Member;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.OrderItem;
import net.wit.entity.Product;
import net.wit.entity.Product.OrderType;
import net.wit.entity.ProductCategory;
import net.wit.entity.ProductCategoryTenant;
import net.wit.entity.Promotion;
import net.wit.entity.Sn.Type;
import net.wit.entity.SpecificationValue;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.util.DateUtil;
import net.wit.util.SettingUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Dao - 商品
 * @author rsico Team
 * @version 3.0
 */
@Repository("productDaoImpl")
public class ProductDaoImpl extends BaseDaoImpl<Product, Long>implements ProductDao {

	private static final Pattern pattern = Pattern.compile("\\d*");

	@Resource(name = "goodsDaoImpl")
	private GoodsDao goodsDao;

	@Resource(name = "snDaoImpl")
	private SnDao snDao;

	@Resource(name = "deliveryCenterDaoImpl")
	private DeliveryCenterDao deliveryCenterDao;

	private CriteriaQuery<Product> criteriaQuery(Set<ProductCategory> productCategorys, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable,
			Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, String phonetic, String keyword) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (productCategorys != null && productCategorys.size() > 0) { // 商品分类
			Predicate categoryPredicate = criteriaBuilder.conjunction();
			Predicate categoryPredicate1 = null;
			for (ProductCategory productCategory : productCategorys) {
				String categoryTreePath = ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR;
				if (categoryPredicate1 == null) {
					categoryPredicate = criteriaBuilder.or(criteriaBuilder.equal(root.get("productCategory"), productCategory), criteriaBuilder.like(root.get("productCategory").<String> get("treePath"), "%" + categoryTreePath + "%"));
					categoryPredicate1 = criteriaBuilder.or(criteriaBuilder.equal(root.get("productCategory"), productCategory), criteriaBuilder.like(root.get("productCategory").<String> get("treePath"), "%" + categoryTreePath + "%"));
				} else {
					categoryPredicate = criteriaBuilder.or(categoryPredicate1,
							criteriaBuilder.or(criteriaBuilder.equal(root.get("productCategory"), productCategory), criteriaBuilder.like(root.get("productCategory").<String> get("treePath"), "%" + categoryTreePath + "%")));
				}
			}
			restrictions = criteriaBuilder.and(restrictions, categoryPredicate);
		}
		if (brand != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("brand"), brand));
		}
		if (promotion != null) { // 促销信息
			Subquery<Product> promotionProductSubquery = criteriaQuery.subquery(Product.class);
			Root<Product> promotionProductSubqueryRoot = promotionProductSubquery.from(Product.class);
			promotionProductSubquery.select(promotionProductSubqueryRoot);
			promotionProductSubquery.where(criteriaBuilder.equal(promotionProductSubqueryRoot, root), criteriaBuilder.equal(promotionProductSubqueryRoot.join("promotionProducts").get("promotion"), promotion));

			Subquery<Product> productCategorySubquery = criteriaQuery.subquery(Product.class);
			Root<Product> productCategorySubqueryRoot = productCategorySubquery.from(Product.class);
			productCategorySubquery.select(productCategorySubqueryRoot);
			productCategorySubquery.where(criteriaBuilder.equal(productCategorySubqueryRoot, root), criteriaBuilder.equal(productCategorySubqueryRoot.join("productCategory").join("promotions"), promotion));

			Subquery<Product> brandSubQuery = criteriaQuery.subquery(Product.class);
			Root<Product> brandSubQueryRoot = brandSubQuery.from(Product.class);
			brandSubQuery.select(brandSubQueryRoot);
			brandSubQuery.where(criteriaBuilder.equal(brandSubQueryRoot, root), criteriaBuilder.equal(brandSubQueryRoot.join("brand").join("promotions"), promotion));

			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.exists(promotionProductSubquery), criteriaBuilder.exists(productCategorySubquery), criteriaBuilder.exists(brandSubQuery)));
		}
		if (tags != null && !tags.isEmpty()) { // 标签
			Subquery<Product> tagsSubquery = criteriaQuery.subquery(Product.class);
			Root<Product> tagsSubqueryRoot = tagsSubquery.from(Product.class);
			tagsSubquery.select(tagsSubqueryRoot);
			tagsSubquery.where(criteriaBuilder.equal(tagsSubqueryRoot, root), tagsSubqueryRoot.join("tags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(tagsSubquery));
		}
		if (attributeValue != null) { // 属性
			for (Entry<Attribute, String> entry : attributeValue.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get(propertyName), entry.getValue()));
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.ge(root.<Number> get("price"), startPrice));
		}
		if (endPrice != null && endPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.le(root.<Number> get("price"), endPrice));
		}
		if (isMarketable != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"), isMarketable));
		}
		if (isList != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isList"), isList));
		}
		if (isTop != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isTop"), isTop));
		}
		if (isGift != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isGift"), isGift));
		}
		Path<Integer> stock = root.get("stock");
		Path<Integer> allocatedStock = root.get("allocatedStock");
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, allocatedStock));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, allocatedStock)));
			}
		}
		if (isStockAlert != null) {
			Setting setting = SettingUtils.get();
			if (isStockAlert) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount()))));
			}
		}

		List<Tenant> tenants = new ArrayList<Tenant>();
		List<DeliveryCenter> dlvs = new ArrayList<DeliveryCenter>();
		if ((periferal != null) && (periferal)) {
			dlvs = deliveryCenterDao.findList(area, community.getLocation(), new BigDecimal(6));
		} else {
			dlvs = deliveryCenterDao.findList(area, community);
		}
		for (Iterator<DeliveryCenter> it = dlvs.iterator(); it.hasNext();) {
			DeliveryCenter dc = it.next();
			tenants.add(dc.getTenant());
		}
		if (tenants.size() == 0) {
			return null;
		}
		restrictions = criteriaBuilder.and(restrictions, root.<Tenant> get("tenant").in(tenants));

		if (StringUtils.isNotBlank(phonetic)) { // 拼音条件
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.like(root.<String> get("phonetic"), "%" + phonetic + "%"));
		}
		if (StringUtils.isNotBlank(keyword)) { // 关键字
			Predicate snPredicate = criteriaBuilder.like(root.<String> get("sn"), "%" + keyword + "%");
			Predicate fullNamePredicate = criteriaBuilder.like(root.<String> get("fullName"), "%" + keyword + "%");
			if (pattern.matcher(keyword).matches()) {
				Predicate idPredicate = criteriaBuilder.equal(root.get("id"), Long.valueOf(keyword));
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(idPredicate, snPredicate, fullNamePredicate));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(snPredicate, fullNamePredicate));
			}
		}
		return criteriaQuery.where(restrictions);
	}

	private CriteriaQuery<Product> criteriaQuery(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, String phonetic, String keyword) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (productCategory != null) { // 商品分类
			String categoryTreePath = ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR;
			Predicate categoryPredicate = criteriaBuilder.or(criteriaBuilder.equal(root.get("productCategory"), productCategory), criteriaBuilder.like(root.get("productCategory").<String> get("treePath"), "%" + categoryTreePath + "%"));
			restrictions = criteriaBuilder.and(restrictions, categoryPredicate);
		}
		if (brand != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("brand"), brand));
		}
		if (promotion != null) { // 促销信息
			Subquery<Product> promotionProductSubquery = criteriaQuery.subquery(Product.class);
			Root<Product> promotionProductSubqueryRoot = promotionProductSubquery.from(Product.class);
			promotionProductSubquery.select(promotionProductSubqueryRoot);
			promotionProductSubquery.where(criteriaBuilder.equal(promotionProductSubqueryRoot, root), criteriaBuilder.equal(promotionProductSubqueryRoot.join("promotionProducts").get("promotion"), promotion));

			Subquery<Product> productCategorySubquery = criteriaQuery.subquery(Product.class);
			Root<Product> productCategorySubqueryRoot = productCategorySubquery.from(Product.class);
			productCategorySubquery.select(productCategorySubqueryRoot);
			productCategorySubquery.where(criteriaBuilder.equal(productCategorySubqueryRoot, root), criteriaBuilder.equal(productCategorySubqueryRoot.join("productCategory").join("promotions"), promotion));

			Subquery<Product> brandSubQuery = criteriaQuery.subquery(Product.class);
			Root<Product> brandSubQueryRoot = brandSubQuery.from(Product.class);
			brandSubQuery.select(brandSubQueryRoot);
			brandSubQuery.where(criteriaBuilder.equal(brandSubQueryRoot, root), criteriaBuilder.equal(brandSubQueryRoot.join("brand").join("promotions"), promotion));

			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.exists(promotionProductSubquery), criteriaBuilder.exists(productCategorySubquery), criteriaBuilder.exists(brandSubQuery)));
		}
		if (tags != null && !tags.isEmpty()) { // 标签
			Subquery<Product> tagsSubquery = criteriaQuery.subquery(Product.class);
			Root<Product> tagsSubqueryRoot = tagsSubquery.from(Product.class);
			tagsSubquery.select(tagsSubqueryRoot);
			tagsSubquery.where(criteriaBuilder.equal(tagsSubqueryRoot, root), tagsSubqueryRoot.join("tags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(tagsSubquery));
		}
		if (attributeValue != null) { // 属性
			for (Entry<Attribute, String> entry : attributeValue.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get(propertyName), entry.getValue()));
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.ge(root.<Number> get("price"), startPrice));
		}
		if (endPrice != null && endPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.le(root.<Number> get("price"), endPrice));
		}
		if (isMarketable != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"), isMarketable));
		}
		if (isList != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isList"), isList));
		}
		if (isTop != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isTop"), isTop));
		}
		if (isGift != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isGift"), isGift));
		}
		Path<Integer> stock = root.get("stock");
		Path<Integer> allocatedStock = root.get("allocatedStock");
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, allocatedStock));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, allocatedStock)));
			}
		}
		if (isStockAlert != null) {
			Setting setting = SettingUtils.get();
			if (isStockAlert) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount()))));
			}
		}

		if (community != null) { // 寻找周边商家
			List<Tenant> tenants = new ArrayList<Tenant>();
			List<DeliveryCenter> dlvs = new ArrayList<DeliveryCenter>();
			if ((periferal != null) && (periferal)) {
				dlvs = deliveryCenterDao.findList(area, community.getLocation(), new BigDecimal(6));
			} else {
				dlvs = deliveryCenterDao.findList(area, community);
			}
			for (Iterator<DeliveryCenter> it = dlvs.iterator(); it.hasNext();) {
				DeliveryCenter dc = it.next();
				tenants.add(dc.getTenant());
			}
			if (tenants.size() == 0) {
				return null;
			}
			restrictions = criteriaBuilder.and(restrictions, root.<Tenant> get("tenant").in(tenants));
		} else if (area != null) {
			Subquery<Product> areaSubquery = criteriaQuery.subquery(Product.class);
			Root<Product> tagsSubqueryRoot = areaSubquery.from(Product.class);
			areaSubquery.select(tagsSubqueryRoot);
			areaSubquery.where(criteriaBuilder.equal(tagsSubqueryRoot, root), criteriaBuilder.equal(tagsSubqueryRoot.join("tenant").join("deliveryCenters").get("area"), area));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(areaSubquery));
		}

		if (StringUtils.isNotBlank(phonetic)) { // 拼音条件
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.like(root.<String> get("phonetic"), "%" + phonetic + "%"));
		}
		if (StringUtils.isNotBlank(keyword)) { // 关键字
			Predicate snPredicate = criteriaBuilder.like(root.<String> get("sn"), "%" + keyword + "%");
			Predicate fullNamePredicate = criteriaBuilder.like(root.<String> get("fullName"), "%" + keyword + "%");
			if (pattern.matcher(keyword).matches()) {
				Predicate idPredicate = criteriaBuilder.equal(root.get("id"), Long.valueOf(keyword));
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(idPredicate, snPredicate, fullNamePredicate));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(snPredicate, fullNamePredicate));
			}
		}
		return criteriaQuery.where(restrictions);
	}

	public boolean snExists(String sn) {
		if (sn == null) {
			return false;
		}
		String jpql = "select count(*) from Product product where lower(product.sn) = lower(:sn)";
		Long count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT).setParameter("sn", sn).getSingleResult();
		return count > 0;
	}

	public Product findBySn(String sn) {
		if (sn == null) {
			return null;
		}
		String jpql = "select product from Product product where lower(product.sn) = lower(:sn)";
		try {
			return entityManager.createQuery(jpql, Product.class).setFlushMode(FlushModeType.COMMIT).setParameter("sn", sn).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Page<Product> findPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, OrderType orderType, String phonetic, String keyword, Pageable pageable) {
		CriteriaQuery<Product> criteriaQuery = criteriaQuery(productCategory, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, community, area, periferal, phonetic, keyword);
		List<Order> orders = pageable.getOrders();
		if (orderType == OrderType.priceAsc) {
			orders.add(Order.asc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.weight) {
			orders.add(Order.desc("priority"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.priceDesc) {
			orders.add(Order.desc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.salesDesc) {
			orders.add(Order.desc("sales"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.scoreDesc) {
			orders.add(Order.desc("score"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.dateDesc) {
			orders.add(Order.desc("createDate"));
		} else {
			orders.add(Order.desc("isTop"));
			orders.add(Order.desc("modifyDate"));
		}
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Product> findPageByChannel(Set<ProductCategory> productCategorys, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, OrderType orderType, String phonetic, String keyword, Pageable pageable) {
		CriteriaQuery<Product> criteriaQuery = criteriaQuery(productCategorys, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, community, area, periferal, phonetic, keyword);
		List<Order> orders = pageable.getOrders();
		if (orderType == OrderType.priceAsc) {
			orders.add(Order.asc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.weight) {
			orders.add(Order.desc("priority"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.priceDesc) {
			orders.add(Order.desc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.salesDesc) {
			orders.add(Order.desc("sales"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.scoreDesc) {
			orders.add(Order.desc("score"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.dateDesc) {
			orders.add(Order.desc("createDate"));
		} else {
			orders.add(Order.desc("isTop"));
			orders.add(Order.desc("modifyDate"));
		}
		try {
			return super.findPage(criteriaQuery, pageable);
		} catch (Exception e) {
			e.printStackTrace();
			return new Page<Product>();
		}
	}

	public List<Product> findList(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, String phonetic, String keyword, OrderType orderType, Integer first, Integer count, List<Filter> filters, List<Order> orders) {
		CriteriaQuery<Product> criteriaQuery = criteriaQuery(productCategory, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, community, area, periferal, phonetic, keyword);
		if (orderType == OrderType.priceAsc) {
			orders.add(Order.asc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.weight) {
			orders.add(Order.desc("priority"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.priceDesc) {
			orders.add(Order.desc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.salesDesc) {
			orders.add(Order.desc("sales"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.scoreDesc) {
			orders.add(Order.desc("score"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.dateDesc) {
			orders.add(Order.desc("createDate"));
		} else {
			orders.add(Order.desc("isTop"));
			orders.add(Order.desc("modifyDate"));
		}
		return super.findList(criteriaQuery, first, count, filters, orders);
	}

	public List<Product> search(String keyword, Boolean isGift, Integer count) {
		if (StringUtils.isEmpty(keyword)) {
			return Collections.<Product> emptyList();
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (pattern.matcher(keyword).matches()) {
			restrictions = criteriaBuilder.and(restrictions,
					criteriaBuilder.or(criteriaBuilder.equal(root.get("id"), Long.valueOf(keyword)), criteriaBuilder.like(root.<String> get("sn"), "%" + keyword + "%"), criteriaBuilder.like(root.<String> get("fullName"), "%" + keyword + "%")));
		} else {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.like(root.<String> get("sn"), "%" + keyword + "%"), criteriaBuilder.like(root.<String> get("fullName"), "%" + keyword + "%")));
		}
		if (isGift != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isGift"), isGift));
		}
		criteriaQuery.where(restrictions);
		criteriaQuery.orderBy(criteriaBuilder.desc(root.get("isTop")));
		return super.findList(criteriaQuery, null, count, null, null);
	}

	public List<Product> findList(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType, Integer count, List<Filter> filters, List<Order> orders) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (productCategory != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.equal(root.get("productCategory"), productCategory),
					criteriaBuilder.like(root.get("productCategory").<String> get("treePath"), "%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%")));
		}
		if (brand != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("brand"), brand));
		}
		if (promotion != null) {
			Subquery<Product> subquery1 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot1 = subquery1.from(Product.class);
			subquery1.select(subqueryRoot1);
			subquery1.where(criteriaBuilder.equal(subqueryRoot1, root), criteriaBuilder.equal(subqueryRoot1.join("promotions"), promotion));

			Subquery<Product> subquery2 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot2 = subquery2.from(Product.class);
			subquery2.select(subqueryRoot2);
			subquery2.where(criteriaBuilder.equal(subqueryRoot2, root), criteriaBuilder.equal(subqueryRoot2.join("productCategory").join("promotions"), promotion));

			Subquery<Product> subquery3 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot3 = subquery3.from(Product.class);
			subquery3.select(subqueryRoot3);
			subquery3.where(criteriaBuilder.equal(subqueryRoot3, root), criteriaBuilder.equal(subqueryRoot3.join("brand").join("promotions"), promotion));

			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.exists(subquery1), criteriaBuilder.exists(subquery2), criteriaBuilder.exists(subquery3)));
		}
		if (tags != null && !tags.isEmpty()) {
			Subquery<Product> subquery = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot = subquery.from(Product.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("tags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		if (attributeValue != null) {
			for (Entry<Attribute, String> entry : attributeValue.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get(propertyName), entry.getValue()));
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.ge(root.<Number> get("price"), startPrice));
		}
		if (endPrice != null && endPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.le(root.<Number> get("price"), endPrice));
		}
		if (isMarketable != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"), isMarketable));
		}
		if (isList != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isList"), isList));
		}
		if (isTop != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isTop"), isTop));
		}
		if (isGift != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isGift"), isGift));
		}
		Path<Integer> stock = root.get("stock");
		Path<Integer> allocatedStock = root.get("allocatedStock");
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, allocatedStock));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, allocatedStock)));
			}
		}
		if (isStockAlert != null) {
			Setting setting = SettingUtils.get();
			if (isStockAlert) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount()))));
			}
		}
		criteriaQuery.where(restrictions);
		if (orderType == OrderType.priceAsc) {
			orders.add(Order.asc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.priceDesc) {
			orders.add(Order.desc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.salesDesc) {
			orders.add(Order.desc("sales"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.scoreDesc) {
			orders.add(Order.desc("score"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.dateDesc) {
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.rate) {
			Session session = (Session) entityManager.getDelegate();
			Query query = session.createQuery("from Product p where p.isList=" + Boolean.TRUE + " and p.isMarketable=" + Boolean.TRUE + " ORDER BY p.wholePrice/p.price*10 asc");
			if (count == null) {
				query.setFirstResult(0);
			} else {
				query.setFirstResult(0).setFetchSize(count);
			}
			return query.list();
		} else {
			orders.add(Order.desc("isTop"));
			orders.add(Order.desc("modifyDate"));
		}
		return super.findList(criteriaQuery, null, count, filters, orders);
	}

	public List<Product> findList(Set<ProductCategory> productCategories, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Tenant tenant, Area area, Boolean periferal, OrderType orderType, Integer count, List<Filter> filters, List<Order> orders) {

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (productCategories != null && productCategories.size() > 0) { // 商品分类
			Predicate categoryPredicate = criteriaBuilder.conjunction();
			Predicate categoryPredicate1 = null;
			for (ProductCategory productCategory : productCategories) {
				String categoryTreePath = ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR;
				if (categoryPredicate1 == null) {
					categoryPredicate = criteriaBuilder.or(criteriaBuilder.equal(root.get("productCategory"), productCategory), criteriaBuilder.like(root.get("productCategory").<String> get("treePath"), "%" + categoryTreePath + "%"));
					categoryPredicate1 = criteriaBuilder.or(criteriaBuilder.equal(root.get("productCategory"), productCategory), criteriaBuilder.like(root.get("productCategory").<String> get("treePath"), "%" + categoryTreePath + "%"));
				} else {
					categoryPredicate = criteriaBuilder.or(categoryPredicate1,
							criteriaBuilder.or(criteriaBuilder.equal(root.get("productCategory"), productCategory), criteriaBuilder.like(root.get("productCategory").<String> get("treePath"), "%" + categoryTreePath + "%")));
				}
			}
			restrictions = criteriaBuilder.and(restrictions, categoryPredicate);
		}

		if (brand != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("brand"), brand));
		}
		if (promotion != null) {
			Subquery<Product> subquery1 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot1 = subquery1.from(Product.class);
			subquery1.select(subqueryRoot1);
			subquery1.where(criteriaBuilder.equal(subqueryRoot1, root), criteriaBuilder.equal(subqueryRoot1.join("promotionProducts").get("promotion"), promotion));

			Subquery<Product> subquery2 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot2 = subquery2.from(Product.class);
			subquery2.select(subqueryRoot2);
			subquery2.where(criteriaBuilder.equal(subqueryRoot2, root), criteriaBuilder.equal(subqueryRoot2.join("productCategory").join("promotions"), promotion));

			Subquery<Product> subquery3 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot3 = subquery3.from(Product.class);
			subquery3.select(subqueryRoot3);
			subquery3.where(criteriaBuilder.equal(subqueryRoot3, root), criteriaBuilder.equal(subqueryRoot3.join("brand").join("promotions"), promotion));

			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.exists(subquery1), criteriaBuilder.exists(subquery2), criteriaBuilder.exists(subquery3)));
		}
		if (tags != null && !tags.isEmpty()) {
			Subquery<Product> subquery = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot = subquery.from(Product.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("tags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		if (attributeValue != null) {
			for (Entry<Attribute, String> entry : attributeValue.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get(propertyName), entry.getValue()));
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.ge(root.<Number> get("price"), startPrice));
		}
		if (endPrice != null && endPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.le(root.<Number> get("price"), endPrice));
		}
		if (isMarketable != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"), isMarketable));
		}
		if (isList != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isList"), isList));
		}
		if (isTop != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isTop"), isTop));
		}
		if (isGift != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isGift"), isGift));
		}
		Path<Integer> stock = root.get("stock");
		Path<Integer> allocatedStock = root.get("allocatedStock");
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, allocatedStock));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, allocatedStock)));
			}
		}
		if (isStockAlert != null) {
			Setting setting = SettingUtils.get();
			if (isStockAlert) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount()))));
			}
		}

		List<Tenant> tenants = new ArrayList<Tenant>();
		if (tenant == null) {
			List<DeliveryCenter> dlvs = new ArrayList<DeliveryCenter>();
			if ((periferal != null) && (periferal)) {
				dlvs = deliveryCenterDao.findList(area, community.getLocation(), new BigDecimal(6));
			} else {
				dlvs = deliveryCenterDao.findList(area, community);
			}

			for (Iterator<DeliveryCenter> it = dlvs.iterator(); it.hasNext();) {
				DeliveryCenter dc = it.next();
				tenants.add(dc.getTenant());
			}
		} else {
			tenants.add(tenant);
		}
		if (tenants.size() == 0) {
			return new ArrayList<Product>(Collections.<Product> emptyList());
		}
		restrictions = criteriaBuilder.and(restrictions, root.get("tenant").in(tenants));

		criteriaQuery.where(restrictions);
		if (orderType == OrderType.priceAsc) {
			orders.add(Order.asc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.priceDesc) {
			orders.add(Order.desc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.salesDesc) {
			orders.add(Order.desc("sales"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.scoreDesc) {
			orders.add(Order.desc("score"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.dateDesc) {
			orders.add(Order.desc("createDate"));
		} else if (OrderType.rate == orderType) {
			Session session = (Session) entityManager.getDelegate();
			Query query = session.createQuery("from Product p where p.isList=" + Boolean.TRUE + " and p.isMarketable=" + Boolean.TRUE + " ORDER BY p.wholePrice/p.price*10 asc");
			if (count == null) {
				query.setFirstResult(0);
			} else {
				query.setFirstResult(0).setMaxResults(count);
			}
			return query.list();
		} else {
			orders.add(Order.desc("isTop"));
			orders.add(Order.desc("modifyDate"));
		}
		return super.findList(criteriaQuery, null, count, filters, orders);
	}

	public List<Product> findList(ProductCategory productCategory, Date beginDate, Date endDate, Integer first, Integer count) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"), true));
		if (productCategory != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.equal(root.get("productCategory"), productCategory),
					criteriaBuilder.like(root.get("productCategory").<String> get("treePath"), "%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%")));
		}
		if (beginDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), beginDate));
		}
		if (endDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<Date> get("createDate"), endDate));
		}
		criteriaQuery.where(restrictions);
		criteriaQuery.orderBy(criteriaBuilder.desc(root.get("isTop")));
		return super.findList(criteriaQuery, first, count, null, null);
	}

	public List<Product> findList(Goods goods, Set<Product> excludes) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (goods != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("goods"), goods));
		}
		if (excludes != null && !excludes.isEmpty()) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.not(root.in(excludes)));
		}
		criteriaQuery.where(restrictions);
		return entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT).getResultList();
	}

	public List<Object[]> findSalesList(Date beginDate, Date endDate, Integer count) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);
		Root<Product> product = criteriaQuery.from(Product.class);
		Join<Product, OrderItem> orderItems = product.join("orderItems");
		Join<Product, net.wit.entity.Order> order = orderItems.join("order");
		criteriaQuery.multiselect(product.get("id"), product.get("sn"), product.get("name"), product.get("fullName"), product.get("price"), criteriaBuilder.sum(orderItems.<Integer> get("quantity")),
				criteriaBuilder.sum(criteriaBuilder.prod(orderItems.<Integer> get("quantity"), orderItems.<BigDecimal> get("price"))));
		Predicate restrictions = criteriaBuilder.conjunction();
		if (beginDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(order.<Date> get("createDate"), beginDate));
		}
		if (endDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(order.<Date> get("createDate"), endDate));
		}
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(order.get("orderStatus"), OrderStatus.completed), criteriaBuilder.equal(order.get("paymentStatus"), PaymentStatus.paid));
		criteriaQuery.where(restrictions);
		criteriaQuery.groupBy(product.get("id"), product.get("sn"), product.get("name"), product.get("fullName"), product.get("price"));
		criteriaQuery.orderBy(criteriaBuilder.desc(criteriaBuilder.sum(criteriaBuilder.prod(orderItems.<Integer> get("quantity"), orderItems.<BigDecimal> get("price")))));
		TypedQuery<Object[]> query = entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT);
		if (count != null && count >= 0) {
			query.setMaxResults(count);
		}
		return query.getResultList();
	}
	@Override
	public Page<Object[]> findSalesPage(Pageable pageable,Date beginDate,Date endDate,Tenant tenant) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);
		Root<Product> product = criteriaQuery.from(Product.class);
		Join<Product, OrderItem> orderItems = product.join("orderItems");
		Join<Product, net.wit.entity.Order> order = orderItems.join("order");
		criteriaQuery.multiselect(product.get("name"), product.get("fullName"), product.get("unit"),product.get("cost"), orderItems.get("price"),
				criteriaBuilder.sum(orderItems.<Integer> get("quantity")),product.get("stock"),
				criteriaBuilder.sum(criteriaBuilder.prod(orderItems.<Integer> get("quantity"), orderItems.<BigDecimal> get("price"))));
		Predicate restrictions = criteriaBuilder.conjunction();
		if (beginDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(order.<Date> get("createDate"), beginDate));
		}
		if (endDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(order.<Date> get("createDate"), endDate));
		}
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(order.get("orderStatus"), OrderStatus.completed), criteriaBuilder.equal(order.get("paymentStatus"), PaymentStatus.paid));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(order.get("tenant"), tenant));
		criteriaQuery.where(restrictions);
		criteriaQuery.groupBy( product.get("id"),orderItems.get("price"));
		criteriaQuery.orderBy(criteriaBuilder.desc(criteriaBuilder.sum(criteriaBuilder.prod(orderItems.<Integer> get("quantity"), orderItems.<BigDecimal> get("price")))));
		
		int total =  entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT).getResultList().size();
		TypedQuery<Object[]> query = entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT);
		if (pageable != null) {
			int totalPages = (int) Math.ceil((double) total / (double) pageable.getPageSize());
			if (totalPages < pageable.getPageNumber()) {
				pageable.setPageNumber(totalPages);
			}
			query.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}
		
		return new Page<Object[]>(query.getResultList(), total, pageable);
	}

	public Page<Product> findPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (productCategory != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.equal(root.get("productCategory"), productCategory),
					criteriaBuilder.like(root.get("productCategory").<String> get("treePath"), "%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%")));
		}
		if (brand != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("brand"), brand));
		}
		if (promotion != null) {
			Subquery<Product> subquery1 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot1 = subquery1.from(Product.class);
			subquery1.select(subqueryRoot1);
			subquery1.where(criteriaBuilder.equal(subqueryRoot1, root), criteriaBuilder.equal(subqueryRoot1.join("promotions"), promotion));

			Subquery<Product> subquery2 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot2 = subquery2.from(Product.class);
			subquery2.select(subqueryRoot2);
			subquery2.where(criteriaBuilder.equal(subqueryRoot2, root), criteriaBuilder.equal(subqueryRoot2.join("productCategory").join("promotions"), promotion));

			Subquery<Product> subquery3 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot3 = subquery3.from(Product.class);
			subquery3.select(subqueryRoot3);
			subquery3.where(criteriaBuilder.equal(subqueryRoot3, root), criteriaBuilder.equal(subqueryRoot3.join("brand").join("promotions"), promotion));

			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.exists(subquery1), criteriaBuilder.exists(subquery2), criteriaBuilder.exists(subquery3)));
		}
		if (tags != null && !tags.isEmpty()) {
			Subquery<Product> subquery = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot = subquery.from(Product.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("tags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		if (attributeValue != null) {
			for (Entry<Attribute, String> entry : attributeValue.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get(propertyName), entry.getValue()));
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.ge(root.<Number> get("price"), startPrice));
		}
		if (endPrice != null && endPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.le(root.<Number> get("price"), endPrice));
		}
		if (isMarketable != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"), isMarketable));
		}
		if (isList != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isList"), isList));
		}
		if (isTop != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isTop"), isTop));
		}
		if (isGift != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isGift"), isGift));
		}
		Path<Integer> stock = root.get("stock");
		Path<Integer> allocatedStock = root.get("allocatedStock");
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, allocatedStock));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, allocatedStock)));
			}
		}
		if (isStockAlert != null) {
			Setting setting = SettingUtils.get();
			if (isStockAlert) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount()))));
			}
		}
		criteriaQuery.where(restrictions);
		List<Order> orders = pageable.getOrders();
		if (orderType == OrderType.priceAsc) {
			orders.add(Order.asc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.priceDesc) {
			orders.add(Order.desc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.salesDesc) {
			orders.add(Order.desc("sales"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.scoreDesc) {
			orders.add(Order.desc("score"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.dateDesc) {
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.rate) {
			try {
				Session session = (Session) entityManager.getDelegate();
				String hql = "from Product p where p.isList=" + Boolean.TRUE + " and p.isMarketable=" + Boolean.TRUE + " ORDER BY p.wholePrice/p.price*10 asc";
				Query query = session.createQuery(hql);
				query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize()).setFetchSize(pageable.getPageSize());
				List<Product> products = query.list();
				query = session.createQuery("select count(*) " + hql);
				Long count = (Long) query.list().get(0);
				return new Page<Product>(products, count, pageable);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			orders.add(Order.desc("isTop"));
			orders.add(Order.desc("modifyDate"));
		}
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Product> findPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, Location location, BigDecimal distatce, OrderType orderType, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (productCategory != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.equal(root.get("productCategory"), productCategory),
					criteriaBuilder.like(root.get("productCategory").<String> get("treePath"), "%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%")));
		}
		if (brand != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("brand"), brand));
		}
		if (promotion != null) {
			Subquery<Product> subquery1 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot1 = subquery1.from(Product.class);
			subquery1.select(subqueryRoot1);
			subquery1.where(criteriaBuilder.equal(subqueryRoot1, root), criteriaBuilder.equal(subqueryRoot1.join("promotions"), promotion));

			Subquery<Product> subquery2 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot2 = subquery2.from(Product.class);
			subquery2.select(subqueryRoot2);
			subquery2.where(criteriaBuilder.equal(subqueryRoot2, root), criteriaBuilder.equal(subqueryRoot2.join("productCategory").join("promotions"), promotion));

			Subquery<Product> subquery3 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot3 = subquery3.from(Product.class);
			subquery3.select(subqueryRoot3);
			subquery3.where(criteriaBuilder.equal(subqueryRoot3, root), criteriaBuilder.equal(subqueryRoot3.join("brand").join("promotions"), promotion));

			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.exists(subquery1), criteriaBuilder.exists(subquery2), criteriaBuilder.exists(subquery3)));
		}
		if (tags != null && !tags.isEmpty()) {
			Subquery<Product> subquery = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot = subquery.from(Product.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("tags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		if (attributeValue != null) {
			for (Entry<Attribute, String> entry : attributeValue.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get(propertyName), entry.getValue()));
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.ge(root.<Number> get("price"), startPrice));
		}
		if (endPrice != null && endPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.le(root.<Number> get("price"), endPrice));
		}
		if (isMarketable != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"), isMarketable));
		}
		if (isList != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isList"), isList));
		}
		if (isTop != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isTop"), isTop));
		}
		if (isGift != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isGift"), isGift));
		}
		Path<Integer> stock = root.get("stock");
		Path<Integer> allocatedStock = root.get("allocatedStock");
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, allocatedStock));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, allocatedStock)));
			}
		}
		if (isStockAlert != null) {
			Setting setting = SettingUtils.get();
			if (isStockAlert) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount()))));
			}
		}

		List<Tenant> tenants = new ArrayList<Tenant>();
		List<DeliveryCenter> dlvs = new ArrayList<DeliveryCenter>();
		if ((periferal != null) && periferal) {
			if ((location == null || !location.isExists()) && community != null) {
				dlvs = deliveryCenterDao.findList(area, community.getLocation(), new BigDecimal(6));
			} else if (location != null) {
				if (distatce == null) {
					distatce = new BigDecimal(6);
				}
				dlvs = deliveryCenterDao.findList(area, location, distatce);
			}
		} else {
			dlvs = deliveryCenterDao.findList(area, community);
		}

		for (Iterator<DeliveryCenter> it = dlvs.iterator(); it.hasNext();) {
			DeliveryCenter dc = it.next();
			tenants.add(dc.getTenant());
		}
		if (tenants.size() > 0) {
			restrictions = criteriaBuilder.and(restrictions, root.<Tenant> get("tenant").in(tenants));
		}

		criteriaQuery.where(restrictions);
		List<Order> orders = pageable.getOrders();
		if (orderType == OrderType.priceAsc) {
			orders.add(Order.asc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.weight) {
			orders.add(Order.desc("priority"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.priceDesc) {
			orders.add(Order.desc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.salesDesc) {
			orders.add(Order.desc("sales"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.scoreDesc) {
			orders.add(Order.desc("score"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.dateDesc) {
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.rate) {
			try {
				Session session = (Session) entityManager.getDelegate();
				String hql = "from Product p where p.isList=" + Boolean.TRUE + " and p.isMarketable=" + Boolean.TRUE + " ORDER BY p.wholePrice/p.price*10 asc";
				Query query = session.createQuery(hql);
				query.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize()).setMaxResults(pageable.getPageSize());
				List<Product> products = query.list();
				query = session.createQuery("select count(*) " + hql);
				Long count = (Long) query.list().get(0);
				return new Page<Product>(products, count, pageable);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			orders.add(Order.desc("isTop"));
			orders.add(Order.desc("modifyDate"));
		}
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Product> findPage(Member member, Pageable pageable) {
		if (member == null) {
			return new Page<Product>(Collections.<Product> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.join("favoriteMembers"), member));

		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Product> findPage(Member member, ProductCategory productCategory, Brand brand, Pageable pageable) {
		if (member == null) {
			return new Page<Product>(Collections.<Product> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		// criteriaQuery.where(criteriaBuilder.equal(root.join("favoriteMembers"),
		// member));
		Subquery<Product> subquery = criteriaQuery.subquery(Product.class);
		Root<Product> subqueryRoot = subquery.from(Product.class);
		subquery.select(subqueryRoot);
		subquery.where(criteriaBuilder.equal(subqueryRoot, root), criteriaBuilder.equal(subqueryRoot.join("favoriteMembers"), member));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.exists(subquery)));
		if (productCategory != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("productCategory"), productCategory));
		}
		if (brand != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("brand"), brand));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Product> findPage(Member member, Integer days, ProductCategory productCategory, Pageable pageable) {
		if (member == null && days > 0) {
			return new Page<Product>(Collections.<Product> emptyList(), 0, pageable);
		}
		Date latestDate = DateUtil.transpositionDate(new Date(), Calendar.DATE, days);
		String hql = "from Product p where exists(from OrderItem oi where oi.product = p and oi.order.createDate > ? and oi.order.member = ?)";
		if (productCategory != null) {
			hql += " and (p.productCategory.id = " + productCategory.getId() + " or p.productCategory.treePath like '" + productCategory.getTreePath() + productCategory.getId() + "%')";
		}
		Session session = (Session) entityManager.getDelegate();
		Query query = session.createQuery(hql);
		query.setParameter(0, latestDate);
		query.setParameter(1, member);
		query.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize()).setMaxResults(pageable.getPageSize());
		@SuppressWarnings("unchecked")
		List<Product> products = query.list();
		query = session.createQuery("select count(*) " + hql);
		query.setParameter(0, latestDate);
		query.setParameter(1, member);
		Long count = (Long) query.list().get(0);
		return new Page<Product>(products, count, pageable);
	}

	public Page<Product> findMyPage(Tenant tenant, ProductCategory productCategory, ProductCategoryTenant productCategoryTenant, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice,
			BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType, Pageable pageable) {
		if (tenant == null) {
			return new Page<Product>(Collections.<Product> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (productCategory != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.equal(root.get("productCategory"), productCategory),
					criteriaBuilder.like(root.get("productCategory").<String> get("treePath"), "%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%")));
		}
		if (productCategoryTenant != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.equal(root.get("productCategoryTenant"), productCategoryTenant),
					criteriaBuilder.like(root.get("productCategoryTenant").<String> get("treePath"), "%" + ProductCategoryTenant.TREE_PATH_SEPARATOR + productCategoryTenant.getId() + ProductCategoryTenant.TREE_PATH_SEPARATOR + "%")));
		}
		if (brand != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("brand"), brand));
		}
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		if (promotion != null) {
			Subquery<Product> subquery1 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot1 = subquery1.from(Product.class);
			subquery1.select(subqueryRoot1);
			subquery1.where(criteriaBuilder.equal(subqueryRoot1, root), criteriaBuilder.equal(subqueryRoot1.join("promotions"), promotion));

			Subquery<Product> subquery2 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot2 = subquery2.from(Product.class);
			subquery2.select(subqueryRoot2);
			subquery2.where(criteriaBuilder.equal(subqueryRoot2, root), criteriaBuilder.equal(subqueryRoot2.join("productCategory").join("promotions"), promotion));

			Subquery<Product> subquery3 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot3 = subquery3.from(Product.class);
			subquery3.select(subqueryRoot3);
			subquery3.where(criteriaBuilder.equal(subqueryRoot3, root), criteriaBuilder.equal(subqueryRoot3.join("brand").join("promotions"), promotion));

			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.exists(subquery1), criteriaBuilder.exists(subquery2), criteriaBuilder.exists(subquery3)));
		}
		if (tags != null && !tags.isEmpty()) {
			Subquery<Product> subquery = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot = subquery.from(Product.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("tags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		if (attributeValue != null) {
			for (Entry<Attribute, String> entry : attributeValue.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get(propertyName), entry.getValue()));
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.ge(root.<Number> get("price"), startPrice));
		}
		if (endPrice != null && endPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.le(root.<Number> get("price"), endPrice));
		}
		if (isMarketable != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"), isMarketable));
		}
		if (isList != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isList"), isList));
		}
		if (isTop != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isTop"), isTop));
		}
		if (isGift != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isGift"), isGift));
		}
		Path<Integer> stock = root.get("stock");
		Path<Integer> allocatedStock = root.get("allocatedStock");
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, allocatedStock));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, allocatedStock)));
			}
		}
		if (isStockAlert != null) {
			Setting setting = SettingUtils.get();
			if (isStockAlert) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount()))));
			}
		}
		criteriaQuery.where(restrictions);
		List<Order> orders = pageable.getOrders();
		if (orderType == OrderType.priceAsc) {
			orders.add(Order.asc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.priceDesc) {
			orders.add(Order.desc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.salesDesc) {
			orders.add(Order.desc("sales"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.scoreDesc) {
			orders.add(Order.desc("score"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.dateDesc) {
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.sortAsc) {
			orders.add(Order.asc("sort"));
			orders.add(Order.asc("createDate"));
		} else {
			orders.add(Order.desc("isTop"));
			orders.add(Order.desc("modifyDate"));
		}
		return super.findPage(criteriaQuery, pageable);
	}

	public Long count(Tenant tenant, Member favoriteMember, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (favoriteMember != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.join("favoriteMembers"), favoriteMember));
		}
		if (isMarketable != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"), isMarketable));
		}
		if (isList != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isList"), isList));
		}
		if (isTop != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isTop"), isTop));
		}
		if (isGift != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isGift"), isGift));
		}
		if (tenant != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		Path<Integer> stock = root.get("stock");
		Path<Integer> allocatedStock = root.get("allocatedStock");
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, allocatedStock));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, allocatedStock)));
			}
		}
		if (isStockAlert != null) {
			Setting setting = SettingUtils.get();
			if (isStockAlert) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount()))));
			}
		}
		criteriaQuery.where(restrictions);
		return super.count(criteriaQuery, null);
	}

	public boolean isPurchased(Member member, Product product) {
		if (member == null || product == null) {
			return false;
		}
		String jqpl = "select count(*) from OrderItem orderItem where orderItem.product = :product and orderItem.order.member = :member and orderItem.order.orderStatus = :orderStatus";
		Long count = entityManager.createQuery(jqpl, Long.class).setFlushMode(FlushModeType.COMMIT).setParameter("product", product).setParameter("member", member).setParameter("orderStatus", OrderStatus.completed).getSingleResult();
		return count > 0;
	}

	/**
	 * 设置值并保存
	 * @param product 商品
	 */
	@Override
	public void persist(Product product) {
		Assert.notNull(product);

		setValue(product);
		super.persist(product);
	}

	/**
	 * 设置值并更新
	 * @param product 商品
	 * @return 商品
	 */
	@Override
	public Product merge(Product product) {
		Assert.notNull(product);

		if (!product.getIsGift()) {
			String jpql = "delete from GiftItem giftItem where giftItem.gift = :product";
			entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).setParameter("product", product).executeUpdate();
		}
		if (!product.getIsMarketable() || product.getIsGift()) {
			String jpql = "delete from CartItem cartItem where cartItem.product = :product";
			entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).setParameter("product", product).executeUpdate();
		}
		setValue(product);
		return super.merge(product);
	}

	@Override
	public void remove(Product product) {
		if (product != null) {
			Goods goods = product.getGoods();
			if (goods != null && goods.getProducts() != null) {
				goods.getProducts().remove(product);
				if (goods.getProducts().isEmpty()) {
					goodsDao.remove(goods);
				}
			}
		}
		super.remove(product);
	}

	/**
	 * 设置值
	 * @param product 商品
	 */
	private void setValue(Product product) {
		if (product == null) {
			return;
		}
		if (StringUtils.isEmpty(product.getSn())) {
			String sn;
			do {
				sn = snDao.generate(Type.product);
			} while (snExists(sn));
			product.setSn(sn);
		}
		StringBuffer fullName = new StringBuffer(product.getName());
		if (product.getSpecificationValues() != null && !product.getSpecificationValues().isEmpty()) {
			List<SpecificationValue> specificationValues = new ArrayList<SpecificationValue>(product.getSpecificationValues());
			Collections.sort(specificationValues, new Comparator<SpecificationValue>() {
				public int compare(SpecificationValue a1, SpecificationValue a2) {
					return new CompareToBuilder().append(a1.getSpecification(), a2.getSpecification()).toComparison();
				}
			});
			fullName.append(Product.FULL_NAME_SPECIFICATION_PREFIX);
			int i = 0;
			for (Iterator<SpecificationValue> iterator = specificationValues.iterator(); iterator.hasNext(); i++) {
				if (i != 0) {
					fullName.append(Product.FULL_NAME_SPECIFICATION_SEPARATOR);
				}
				fullName.append(iterator.next().getName());
			}
			fullName.append(Product.FULL_NAME_SPECIFICATION_SUFFIX);
		}
		product.setFullName(fullName.toString());
	}

	@SuppressWarnings("unchecked")
	public Page<Product> search(String keyword, String phonetic, BigDecimal startPrice, BigDecimal endPrice, OrderType orderType, Pageable pageable) {
		if (StringUtils.isEmpty(keyword)) {
			return (Page<Product>) Collections.<Product> emptyList();
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (pattern.matcher(keyword).matches()) {
			restrictions = criteriaBuilder.and(restrictions,
					criteriaBuilder.or(criteriaBuilder.equal(root.get("id"), Long.valueOf(keyword)), criteriaBuilder.like(root.<String> get("sn"), "%" + keyword + "%"), criteriaBuilder.like(root.<String> get("fullName"), "%" + keyword + "%")),
					criteriaBuilder.like(root.get("productCategory").<String> get("name"), "%" + keyword + "%"));
		} else {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.like(root.<String> get("sn"), "%" + keyword + "%"), criteriaBuilder.like(root.<String> get("fullName"), "%" + keyword + "%"),
					criteriaBuilder.like(root.get("productCategory").<String> get("name"), "%" + keyword + "%")));
		}
		if (StringUtils.isNotBlank(phonetic)) { // 拼音条件
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.like(root.<String> get("phonetic"), "%" + phonetic + "%"));
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.ge(root.<Number> get("price"), startPrice));
		}
		if (endPrice != null && endPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.le(root.<Number> get("price"), endPrice));
		}
		List<Order> orders = pageable.getOrders();
		if (orderType == OrderType.priceAsc) {
			orders.add(Order.asc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.weight) {
			orders.add(Order.desc("priority"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.priceDesc) {
			orders.add(Order.desc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.salesDesc) {
			orders.add(Order.desc("sales"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.scoreDesc) {
			orders.add(Order.desc("score"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.dateDesc) {
			orders.add(Order.desc("createDate"));
		} else {
			orders.add(Order.desc("isTop"));
			orders.add(Order.desc("modifyDate"));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Product> mobileFindPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, Location location, BigDecimal distatce, OrderType orderType, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (productCategory != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.equal(root.get("productCategory"), productCategory),
					criteriaBuilder.like(root.get("productCategory").<String> get("treePath"), "%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%")));
		}
		if (brand != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("brand"), brand));
		}
		if (promotion != null) {
			Subquery<Product> subquery1 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot1 = subquery1.from(Product.class);
			subquery1.select(subqueryRoot1);
			subquery1.where(criteriaBuilder.equal(subqueryRoot1, root), criteriaBuilder.equal(subqueryRoot1.join("promotions"), promotion));

			Subquery<Product> subquery2 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot2 = subquery2.from(Product.class);
			subquery2.select(subqueryRoot2);
			subquery2.where(criteriaBuilder.equal(subqueryRoot2, root), criteriaBuilder.equal(subqueryRoot2.join("productCategory").join("promotions"), promotion));

			Subquery<Product> subquery3 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot3 = subquery3.from(Product.class);
			subquery3.select(subqueryRoot3);
			subquery3.where(criteriaBuilder.equal(subqueryRoot3, root), criteriaBuilder.equal(subqueryRoot3.join("brand").join("promotions"), promotion));

			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.exists(subquery1), criteriaBuilder.exists(subquery2), criteriaBuilder.exists(subquery3)));
		}
		if (tags != null && !tags.isEmpty()) {
			Subquery<Product> subquery = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot = subquery.from(Product.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("tags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		if (attributeValue != null) {
			for (Entry<Attribute, String> entry : attributeValue.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get(propertyName), entry.getValue()));
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.ge(root.<Number> get("price"), startPrice));
		}
		if (endPrice != null && endPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.le(root.<Number> get("price"), endPrice));
		}
		if (isMarketable != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"), isMarketable));
		}
		if (isList != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isList"), isList));
		}
		if (isTop != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isTop"), isTop));
		}
		if (isGift != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isGift"), isGift));
		}
		Path<Integer> stock = root.get("stock");
		Path<Integer> allocatedStock = root.get("allocatedStock");
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, allocatedStock));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, allocatedStock)));
			}
		}
		if (isStockAlert != null) {
			Setting setting = SettingUtils.get();
			if (isStockAlert) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount()))));
			}
		}

		List<Tenant> tenants = new ArrayList<Tenant>();
		List<DeliveryCenter> dlvs = new ArrayList<DeliveryCenter>();
		if ((periferal != null) && periferal) {
			if ((location == null || !location.isExists()) && community != null) {
				dlvs = deliveryCenterDao.findList(area, community.getLocation(), new BigDecimal(6));
			} else if (location.isExists()) {
				if (distatce == null) {
					distatce = new BigDecimal(6);
				}
				dlvs = deliveryCenterDao.findList(area, location, distatce);
			}
		} else {
			dlvs = deliveryCenterDao.findList(area, community);
		}

		for (Iterator<DeliveryCenter> it = dlvs.iterator(); it.hasNext();) {
			DeliveryCenter dc = it.next();
			tenants.add(dc.getTenant());
		}
		if (tenants.size() == 0) {
			return new Page<Product>(Collections.<Product> emptyList(), 0, pageable);
		}
		restrictions = criteriaBuilder.and(restrictions, root.<Tenant> get("tenant").in(tenants));

		criteriaQuery.where(restrictions);
		List<Order> orders = pageable.getOrders();
		if (orderType == OrderType.priceAsc) {
			orders.add(Order.asc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.weight) {
			orders.add(Order.desc("priority"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.priceDesc) {
			orders.add(Order.desc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.salesDesc) {
			orders.add(Order.desc("sales"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.scoreDesc) {
			orders.add(Order.desc("score"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.dateDesc) {
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.rate) {
			try {
				Session session = (Session) entityManager.getDelegate();
				String hql = "from Product p where p.isList=" + Boolean.TRUE + " and p.isMarketable=" + Boolean.TRUE + " ORDER BY p.wholePrice/p.price*10 asc";
				Query query = session.createQuery(hql);
				query.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize()).setMaxResults(pageable.getPageSize());
				List<Product> products = query.list();
				query = session.createQuery("select count(*) " + hql);
				Long count = (Long) query.list().get(0);
				return new Page<Product>(products, count, pageable);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			orders.add(Order.desc("isTop"));
			orders.add(Order.desc("modifyDate"));
		}
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Product> findPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, OrderType orderType, String phonetic, String keyword, Location location, BigDecimal distatce, Pageable pageable) {
		CriteriaQuery<Product> criteriaQuery = criteriaQuery(productCategory, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, community, area, periferal, phonetic, keyword,
				location, distatce);
		List<Order> orders = pageable.getOrders();
		if (orderType == OrderType.priceAsc) {
			orders.add(Order.asc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.weight) {
			orders.add(Order.desc("priority"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.priceDesc) {
			orders.add(Order.desc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.salesDesc) {
			orders.add(Order.desc("sales"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.scoreDesc) {
			orders.add(Order.desc("score"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.dateDesc) {
			orders.add(Order.desc("createDate"));
		} else {
			orders.add(Order.desc("isTop"));
			orders.add(Order.desc("modifyDate"));
		}
		return super.findPage(criteriaQuery, pageable);
	}

	private CriteriaQuery<Product> criteriaQuery(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, String phonetic, String keyword, Location location, BigDecimal distatce) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (productCategory != null) { // 商品分类
			String categoryTreePath = ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR;
			Predicate categoryPredicate = criteriaBuilder.or(criteriaBuilder.equal(root.get("productCategory"), productCategory), criteriaBuilder.like(root.get("productCategory").<String> get("treePath"), "%" + categoryTreePath + "%"));
			restrictions = criteriaBuilder.and(restrictions, categoryPredicate);
		}
		if (brand != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("brand"), brand));
		}
		if (promotion != null) { // 促销信息
			Subquery<Product> promotionProductSubquery = criteriaQuery.subquery(Product.class);
			Root<Product> promotionProductSubqueryRoot = promotionProductSubquery.from(Product.class);
			promotionProductSubquery.select(promotionProductSubqueryRoot);
			promotionProductSubquery.where(criteriaBuilder.equal(promotionProductSubqueryRoot, root), criteriaBuilder.equal(promotionProductSubqueryRoot.join("promotionProducts").get("promotion"), promotion));

			Subquery<Product> productCategorySubquery = criteriaQuery.subquery(Product.class);
			Root<Product> productCategorySubqueryRoot = productCategorySubquery.from(Product.class);
			productCategorySubquery.select(productCategorySubqueryRoot);
			productCategorySubquery.where(criteriaBuilder.equal(productCategorySubqueryRoot, root), criteriaBuilder.equal(productCategorySubqueryRoot.join("productCategory").join("promotions"), promotion));

			Subquery<Product> brandSubQuery = criteriaQuery.subquery(Product.class);
			Root<Product> brandSubQueryRoot = brandSubQuery.from(Product.class);
			brandSubQuery.select(brandSubQueryRoot);
			brandSubQuery.where(criteriaBuilder.equal(brandSubQueryRoot, root), criteriaBuilder.equal(brandSubQueryRoot.join("brand").join("promotions"), promotion));

			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.exists(promotionProductSubquery), criteriaBuilder.exists(productCategorySubquery), criteriaBuilder.exists(brandSubQuery)));
		}
		if (tags != null && !tags.isEmpty()) { // 标签
			Subquery<Product> tagsSubquery = criteriaQuery.subquery(Product.class);
			Root<Product> tagsSubqueryRoot = tagsSubquery.from(Product.class);
			tagsSubquery.select(tagsSubqueryRoot);
			tagsSubquery.where(criteriaBuilder.equal(tagsSubqueryRoot, root), tagsSubqueryRoot.join("tags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(tagsSubquery));
		}
		if (attributeValue != null) { // 属性
			for (Entry<Attribute, String> entry : attributeValue.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get(propertyName), entry.getValue()));
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.ge(root.<Number> get("price"), startPrice));
		}
		if (endPrice != null && endPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.le(root.<Number> get("price"), endPrice));
		}
		if (isMarketable != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"), isMarketable));
		}
		if (isList != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isList"), isList));
		}
		if (isTop != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isTop"), isTop));
		}
		if (isGift != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isGift"), isGift));
		}
		Path<Integer> stock = root.get("stock");
		Path<Integer> allocatedStock = root.get("allocatedStock");
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, allocatedStock));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, allocatedStock)));
			}
		}
		if (isStockAlert != null) {
			Setting setting = SettingUtils.get();
			if (isStockAlert) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount()))));
			}
		}

		List<Tenant> tenants = new ArrayList<Tenant>();
		List<DeliveryCenter> dlvs = new ArrayList<DeliveryCenter>();
		if ((periferal != null) && periferal) {
			if ((location == null || !location.isExists()) && community != null) {
				dlvs = deliveryCenterDao.findList(area, community.getLocation(), new BigDecimal(6));
			} else if (location.isExists()) {
				if (distatce == null) {
					distatce = new BigDecimal(6);
				}
				dlvs = deliveryCenterDao.findList(area, location, distatce);
			}
		} else {
			dlvs = deliveryCenterDao.findList(area, community);
		}

		for (Iterator<DeliveryCenter> it = dlvs.iterator(); it.hasNext();) {
			DeliveryCenter dc = it.next();
			tenants.add(dc.getTenant());
		}
		if (tenants.size() > 0) {
			restrictions = criteriaBuilder.and(restrictions, root.<Tenant> get("tenant").in(tenants));
		}

		if (StringUtils.isNotBlank(phonetic)) { // 拼音条件
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.like(root.<String> get("phonetic"), "%" + phonetic + "%"));
		}
		if (StringUtils.isNotBlank(keyword)) { // 关键字
			Predicate snPredicate = criteriaBuilder.like(root.<String> get("sn"), "%" + keyword + "%");
			Predicate fullNamePredicate = criteriaBuilder.like(root.<String> get("fullName"), "%" + keyword + "%");
			if (pattern.matcher(keyword).matches()) {
				Predicate idPredicate = criteriaBuilder.equal(root.get("id"), Long.valueOf(keyword));
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(idPredicate, snPredicate, fullNamePredicate));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(snPredicate, fullNamePredicate));
			}
		}
		return criteriaQuery.where(restrictions);
	}

	public Page<Product> mobileFindPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList,
			Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, OrderType orderType, String phonetic, String keyword, Location location, BigDecimal distatce, Pageable pageable) {
		CriteriaQuery<Product> criteriaQuery = criteriaQueryMobile(productCategory, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, community, area, periferal, phonetic,
				keyword, location, distatce);
		if (criteriaQuery == null) {
			return new Page<Product>(Collections.<Product> emptyList(), 0, pageable);
		}
		List<Order> orders = pageable.getOrders();
		if (orderType == OrderType.priceAsc) {
			orders.add(Order.asc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.weight) {
			orders.add(Order.desc("priority"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.priceDesc) {
			orders.add(Order.desc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.salesDesc) {
			orders.add(Order.desc("sales"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.scoreDesc) {
			orders.add(Order.desc("score"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.dateDesc) {
			orders.add(Order.desc("createDate"));
		} else {
			orders.add(Order.desc("isTop"));
			orders.add(Order.desc("modifyDate"));
		}
		return super.findPage(criteriaQuery, pageable);
	}

	private CriteriaQuery<Product> criteriaQueryMobile(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable,
			Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, String phonetic, String keyword, Location location, BigDecimal distatce) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (productCategory != null) { // 商品分类
			String categoryTreePath = ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR;
			Predicate categoryPredicate = criteriaBuilder.or(criteriaBuilder.equal(root.get("productCategory"), productCategory), criteriaBuilder.like(root.get("productCategory").<String> get("treePath"), "%" + categoryTreePath + "%"));
			restrictions = criteriaBuilder.and(restrictions, categoryPredicate);
		}
		if (brand != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("brand"), brand));
		}
		if (promotion != null) { // 促销信息
			Subquery<Product> promotionProductSubquery = criteriaQuery.subquery(Product.class);
			Root<Product> promotionProductSubqueryRoot = promotionProductSubquery.from(Product.class);
			promotionProductSubquery.select(promotionProductSubqueryRoot);
			promotionProductSubquery.where(criteriaBuilder.equal(promotionProductSubqueryRoot, root), criteriaBuilder.equal(promotionProductSubqueryRoot.join("promotionProducts").get("promotion"), promotion));

			Subquery<Product> productCategorySubquery = criteriaQuery.subquery(Product.class);
			Root<Product> productCategorySubqueryRoot = productCategorySubquery.from(Product.class);
			productCategorySubquery.select(productCategorySubqueryRoot);
			productCategorySubquery.where(criteriaBuilder.equal(productCategorySubqueryRoot, root), criteriaBuilder.equal(productCategorySubqueryRoot.join("productCategory").join("promotions"), promotion));

			Subquery<Product> brandSubQuery = criteriaQuery.subquery(Product.class);
			Root<Product> brandSubQueryRoot = brandSubQuery.from(Product.class);
			brandSubQuery.select(brandSubQueryRoot);
			brandSubQuery.where(criteriaBuilder.equal(brandSubQueryRoot, root), criteriaBuilder.equal(brandSubQueryRoot.join("brand").join("promotions"), promotion));

			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.exists(promotionProductSubquery), criteriaBuilder.exists(productCategorySubquery), criteriaBuilder.exists(brandSubQuery)));
		}
		if (tags != null && !tags.isEmpty()) { // 标签
			Subquery<Product> tagsSubquery = criteriaQuery.subquery(Product.class);
			Root<Product> tagsSubqueryRoot = tagsSubquery.from(Product.class);
			tagsSubquery.select(tagsSubqueryRoot);
			tagsSubquery.where(criteriaBuilder.equal(tagsSubqueryRoot, root), tagsSubqueryRoot.join("tags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(tagsSubquery));
		}
		if (attributeValue != null) { // 属性
			for (Entry<Attribute, String> entry : attributeValue.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get(propertyName), entry.getValue()));
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.ge(root.<Number> get("price"), startPrice));
		}
		if (endPrice != null && endPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.le(root.<Number> get("price"), endPrice));
		}
		if (isMarketable != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"), isMarketable));
		}
		if (isList != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isList"), isList));
		}
		if (isTop != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isTop"), isTop));
		}
		if (isGift != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isGift"), isGift));
		}
		Path<Integer> stock = root.get("stock");
		Path<Integer> allocatedStock = root.get("allocatedStock");
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, allocatedStock));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, allocatedStock)));
			}
		}
		if (isStockAlert != null) {
			Setting setting = SettingUtils.get();
			if (isStockAlert) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount()))));
			}
		}

		List<Tenant> tenants = new ArrayList<Tenant>();
		List<DeliveryCenter> dlvs = new ArrayList<DeliveryCenter>();
		if ((periferal != null) && periferal) {
			if ((location == null || !location.isExists()) && community != null) {
				dlvs = deliveryCenterDao.findList(area, community.getLocation(), new BigDecimal(6));
			} else if (location.isExists()) {
				if (distatce == null) {
					distatce = new BigDecimal(6);
				}
				dlvs = deliveryCenterDao.findList(area, location, distatce);
			}
		} else {
			dlvs = deliveryCenterDao.findList(area, community);
		}

		for (Iterator<DeliveryCenter> it = dlvs.iterator(); it.hasNext();) {
			DeliveryCenter dc = it.next();
			tenants.add(dc.getTenant());
		}
		if (tenants.size() == 0) {
			return null;
		}

		if (StringUtils.isNotBlank(phonetic)) { // 拼音条件
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.like(root.<String> get("phonetic"), "%" + phonetic + "%"));
		}
		if (StringUtils.isNotBlank(keyword)) { // 关键字
			Predicate snPredicate = criteriaBuilder.like(root.<String> get("sn"), "%" + keyword + "%");
			Predicate fullNamePredicate = criteriaBuilder.like(root.<String> get("fullName"), "%" + keyword + "%");
			if (pattern.matcher(keyword).matches()) {
				Predicate idPredicate = criteriaBuilder.equal(root.get("id"), Long.valueOf(keyword));
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(idPredicate, snPredicate, fullNamePredicate));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(snPredicate, fullNamePredicate));
			}
		}
		return criteriaQuery.where(restrictions);
	}

	public List<Product> findList(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Tenant tenant, Area area, Boolean periferal, OrderType orderType, Integer count, List<Filter> filters, List<Order> orders) {
		CriteriaQuery<Product> criteriaQuery = criteriaQuery(productCategory, brand, promotion, tags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, community, area, periferal, null, null);
		if (orderType == OrderType.priceAsc) {
			orders.add(Order.asc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.weight) {
			orders.add(Order.desc("priority"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.priceDesc) {
			orders.add(Order.desc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.salesDesc) {
			orders.add(Order.desc("sales"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.scoreDesc) {
			orders.add(Order.desc("score"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.dateDesc) {
			orders.add(Order.desc("createDate"));
		} else {
			orders.add(Order.desc("isTop"));
			orders.add(Order.desc("modifyDate"));
		}
		return super.findList(criteriaQuery, 0, count, filters, orders);
	}

	public Page<Product> findPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, List<Tag> unionTags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable,
			Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, OrderType orderType, String phonetic, String keyword, Pageable pageable) {
		CriteriaQuery<Product> criteriaQuery = criteriaQuery(productCategory, brand, promotion, tags, unionTags, attributeValue, startPrice, endPrice, isMarketable, isList, isTop, isGift, isOutOfStock, isStockAlert, community, area, periferal, phonetic,
				keyword);
		List<Order> orders = pageable.getOrders();
		if (orderType == OrderType.priceAsc) {
			orders.add(Order.asc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.weight) {
			orders.add(Order.desc("priority"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.priceDesc) {
			orders.add(Order.desc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.salesDesc) {
			orders.add(Order.desc("sales"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.scoreDesc) {
			orders.add(Order.desc("score"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.dateDesc) {
			orders.add(Order.desc("createDate"));
		} else {
			orders.add(Order.desc("isTop"));
			orders.add(Order.desc("modifyDate"));
		}
		return super.findPage(criteriaQuery, pageable);
	}

	private CriteriaQuery<Product> criteriaQuery(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, List<Tag> unionTags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice,
			Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, String phonetic, String keyword) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (productCategory != null) { // 鍟嗗搧鍒嗙被
			String categoryTreePath = ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR;
			Predicate categoryPredicate = criteriaBuilder.or(criteriaBuilder.equal(root.get("productCategory"), productCategory), criteriaBuilder.like(root.get("productCategory").<String> get("treePath"), "%" + categoryTreePath + "%"));
			restrictions = criteriaBuilder.and(restrictions, categoryPredicate);
		}
		if (brand != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("brand"), brand));
		}
		if (promotion != null) { // 淇冮攢淇℃伅
			Subquery<Product> promotionProductSubquery = criteriaQuery.subquery(Product.class);
			Root<Product> promotionProductSubqueryRoot = promotionProductSubquery.from(Product.class);
			promotionProductSubquery.select(promotionProductSubqueryRoot);
			promotionProductSubquery.where(criteriaBuilder.equal(promotionProductSubqueryRoot, root), criteriaBuilder.equal(promotionProductSubqueryRoot.join("promotionProducts").get("promotion"), promotion));

			Subquery<Product> productCategorySubquery = criteriaQuery.subquery(Product.class);
			Root<Product> productCategorySubqueryRoot = productCategorySubquery.from(Product.class);
			productCategorySubquery.select(productCategorySubqueryRoot);
			productCategorySubquery.where(criteriaBuilder.equal(productCategorySubqueryRoot, root), criteriaBuilder.equal(productCategorySubqueryRoot.join("productCategory").join("promotions"), promotion));

			Subquery<Product> brandSubQuery = criteriaQuery.subquery(Product.class);
			Root<Product> brandSubQueryRoot = brandSubQuery.from(Product.class);
			brandSubQuery.select(brandSubQueryRoot);
			brandSubQuery.where(criteriaBuilder.equal(brandSubQueryRoot, root), criteriaBuilder.equal(brandSubQueryRoot.join("brand").join("promotions"), promotion));

			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.exists(promotionProductSubquery), criteriaBuilder.exists(productCategorySubquery), criteriaBuilder.exists(brandSubQuery)));
		}
		if (tags != null && !tags.isEmpty()) { // 鏍囩
			Subquery<Product> tagsSubquery = criteriaQuery.subquery(Product.class);
			Root<Product> tagsSubqueryRoot = tagsSubquery.from(Product.class);
			tagsSubquery.select(tagsSubqueryRoot);
			tagsSubquery.where(criteriaBuilder.equal(tagsSubqueryRoot, root), tagsSubqueryRoot.join("tags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(tagsSubquery));
		}
		if (unionTags != null && !unionTags.isEmpty()) { // 鏍囩
			Subquery<Tenant> tagsSubquery = criteriaQuery.subquery(Tenant.class);
			Root<Tenant> tagsSubqueryRoot = tagsSubquery.from(Tenant.class);
			tagsSubquery.select(tagsSubqueryRoot);
			tagsSubquery.where(criteriaBuilder.equal(tagsSubqueryRoot, root.get("tenant")), tagsSubqueryRoot.join("unionTags").in(unionTags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(tagsSubquery));
		} else {
			Subquery<Tenant> subquery = criteriaQuery.subquery(Tenant.class);
			Root<Tenant> subqueryRoot = subquery.from(Tenant.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.and(criteriaBuilder.equal(subqueryRoot, root.get("tenant")), criteriaBuilder.isNotEmpty(subqueryRoot.<Set<Tag>> get("unionTags"))));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		if (attributeValue != null) { // 灞炴�
			for (Entry<Attribute, String> entry : attributeValue.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get(propertyName), entry.getValue()));
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.ge(root.<Number> get("price"), startPrice));
		}
		if (endPrice != null && endPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.le(root.<Number> get("price"), endPrice));
		}
		if (isMarketable != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"), isMarketable));
		}
		if (isList != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isList"), isList));
		}
		if (isTop != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isTop"), isTop));
		}
		if (isGift != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isGift"), isGift));
		}
		Path<Integer> stock = root.get("stock");
		Path<Integer> allocatedStock = root.get("allocatedStock");
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, allocatedStock));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, allocatedStock)));
			}
		}
		if (isStockAlert != null) {
			Setting setting = SettingUtils.get();
			if (isStockAlert) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount()))));
			}
		}

		if (community != null) { // 瀵绘壘鍛ㄨ竟鍟嗗
			List<Tenant> tenants = new ArrayList<Tenant>();
			List<DeliveryCenter> dlvs = new ArrayList<DeliveryCenter>();
			if ((periferal != null) && (periferal)) {
				dlvs = deliveryCenterDao.findList(area, community.getLocation(), new BigDecimal(6));
			} else {
				dlvs = deliveryCenterDao.findList(area, community);
			}
			for (Iterator<DeliveryCenter> it = dlvs.iterator(); it.hasNext();) {
				DeliveryCenter dc = it.next();
				tenants.add(dc.getTenant());
			}
			if (tenants.size() == 0) {
				return null;
			}
			restrictions = criteriaBuilder.and(restrictions, root.<Tenant> get("tenant").in(tenants));
		} else if (area != null) {
			Subquery<Product> areaSubquery = criteriaQuery.subquery(Product.class);
			Root<Product> tagsSubqueryRoot = areaSubquery.from(Product.class);
			areaSubquery.select(tagsSubqueryRoot);

			areaSubquery.where(criteriaBuilder.equal(tagsSubqueryRoot, root), criteriaBuilder.like(tagsSubqueryRoot.join("tenant").join("deliveryCenters").get("area").<String> get("treePath"), "%" + area.getTreePath() + "%"));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(areaSubquery));
		}

		if (StringUtils.isNotBlank(phonetic)) { // 鎷奸煶鏉′欢
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.like(root.<String> get("phonetic"), "%" + phonetic + "%"));
		}
		if (StringUtils.isNotBlank(keyword)) { // 鍏抽敭瀛�
			Predicate snPredicate = criteriaBuilder.like(root.<String> get("sn"), "%" + keyword + "%");
			Predicate fullNamePredicate = criteriaBuilder.like(root.<String> get("fullName"), "%" + keyword + "%");
			if (pattern.matcher(keyword).matches()) {
				Predicate idPredicate = criteriaBuilder.equal(root.get("id"), Long.valueOf(keyword));
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(idPredicate, snPredicate, fullNamePredicate));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(snPredicate, fullNamePredicate));
			}
		}
		return criteriaQuery.where(restrictions);
	}

	public Page<Product> findUnionPage(ProductCategory productCategory, Brand brand, Promotion promotion, List<Tag> tags, List<Tag> unionTags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable,
			Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Community community, Area area, Boolean periferal, Location location, BigDecimal distatce, String phonetic, String keyword, OrderType orderType,
			Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (productCategory != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.equal(root.get("productCategory"), productCategory),
					criteriaBuilder.like(root.get("productCategory").<String> get("treePath"), "%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%")));
		}
		if (brand != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("brand"), brand));
		}
		if (promotion != null) {
			Subquery<Product> subquery1 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot1 = subquery1.from(Product.class);
			subquery1.select(subqueryRoot1);
			subquery1.where(criteriaBuilder.equal(subqueryRoot1, root), criteriaBuilder.equal(subqueryRoot1.join("promotions"), promotion));

			Subquery<Product> subquery2 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot2 = subquery2.from(Product.class);
			subquery2.select(subqueryRoot2);
			subquery2.where(criteriaBuilder.equal(subqueryRoot2, root), criteriaBuilder.equal(subqueryRoot2.join("productCategory").join("promotions"), promotion));

			Subquery<Product> subquery3 = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot3 = subquery3.from(Product.class);
			subquery3.select(subqueryRoot3);
			subquery3.where(criteriaBuilder.equal(subqueryRoot3, root), criteriaBuilder.equal(subqueryRoot3.join("brand").join("promotions"), promotion));

			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.exists(subquery1), criteriaBuilder.exists(subquery2), criteriaBuilder.exists(subquery3)));
		}
		if (tags != null && !tags.isEmpty()) {
			Subquery<Product> subquery = criteriaQuery.subquery(Product.class);
			Root<Product> subqueryRoot = subquery.from(Product.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("tags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		if (unionTags != null && !unionTags.isEmpty()) {
			Subquery<Tenant> subquery = criteriaQuery.subquery(Tenant.class);
			Root<Tenant> subqueryRoot = subquery.from(Tenant.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root.get("tenant")), subqueryRoot.join("unionTags").in(unionTags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		} else {
			Subquery<Tenant> subquery = criteriaQuery.subquery(Tenant.class);
			Root<Tenant> subqueryRoot = subquery.from(Tenant.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.and(criteriaBuilder.equal(subqueryRoot, root.get("tenant")), criteriaBuilder.isNotEmpty(subqueryRoot.<Set<Tag>> get("unionTags"))));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		if (attributeValue != null) {
			for (Entry<Attribute, String> entry : attributeValue.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get(propertyName), entry.getValue()));
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.ge(root.<Number> get("price"), startPrice));
		}
		if (endPrice != null && endPrice.compareTo(new BigDecimal(0)) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.le(root.<Number> get("price"), endPrice));
		}
		if (isMarketable != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"), isMarketable));
		}
		if (isList != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isList"), isList));
		}
		if (isTop != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isTop"), isTop));
		}
		if (isGift != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isGift"), isGift));
		}
		Path<Integer> stock = root.get("stock");
		Path<Integer> allocatedStock = root.get("allocatedStock");
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, allocatedStock));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, allocatedStock)));
			}
		}
		if (isStockAlert != null) {
			Setting setting = SettingUtils.get();
			if (isStockAlert) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount()))));
			}
		}

		List<Tenant> tenants = new ArrayList<Tenant>();
		List<DeliveryCenter> dlvs = new ArrayList<DeliveryCenter>();
		if ((periferal != null) && periferal) {
			if ((location == null || !location.isExists()) && community != null) {
				dlvs = deliveryCenterDao.findList(area, community.getLocation(), new BigDecimal(6));
			} else if (location.isExists()) {
				if (distatce == null) {
					distatce = new BigDecimal(6);
				}
				dlvs = deliveryCenterDao.findList(area, location, distatce);
			}
		} else {
			dlvs = deliveryCenterDao.findList(area, community);
		}

		for (Iterator<DeliveryCenter> it = dlvs.iterator(); it.hasNext();) {
			DeliveryCenter dc = it.next();
			tenants.add(dc.getTenant());
		}
		if (tenants.size() > 0) {
			restrictions = criteriaBuilder.and(restrictions, root.<Tenant> get("tenant").in(tenants));
		}
		if (StringUtils.isNotBlank(phonetic)) { // 拼音条件
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.like(root.<String> get("phonetic"), "%" + phonetic + "%"));
		}
		if (StringUtils.isNotBlank(keyword)) { // 关键字
			Predicate snPredicate = criteriaBuilder.like(root.<String> get("sn"), "%" + keyword + "%");
			Predicate fullNamePredicate = criteriaBuilder.like(root.<String> get("fullName"), "%" + keyword + "%");
			if (pattern.matcher(keyword).matches()) {
				Predicate idPredicate = criteriaBuilder.equal(root.get("id"), Long.valueOf(keyword));
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(idPredicate, snPredicate, fullNamePredicate));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(snPredicate, fullNamePredicate));
			}
		}
		criteriaQuery.where(restrictions);
		List<Order> orders = pageable.getOrders();
		if (orderType == OrderType.priceAsc) {
			orders.add(Order.asc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.weight) {
			orders.add(Order.desc("priority"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.priceDesc) {
			orders.add(Order.desc("price"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.salesDesc) {
			orders.add(Order.desc("sales"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.scoreDesc) {
			orders.add(Order.desc("score"));
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.dateDesc) {
			orders.add(Order.desc("createDate"));
		} else if (orderType == OrderType.rate) {
			try {
				Session session = (Session) entityManager.getDelegate();
				String hql = "from Product p where p.isList=" + Boolean.TRUE + " and p.isMarketable=" + Boolean.TRUE + " ORDER BY p.wholePrice/p.price*10 asc";
				Query query = session.createQuery(hql);
				query.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize()).setMaxResults(pageable.getPageSize());
				List<Product> products = query.list();
				query = session.createQuery("select count(*) " + hql);
				Long count = (Long) query.list().get(0);
				return new Page<Product>(products, count, pageable);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			orders.add(Order.desc("isTop"));
			orders.add(Order.desc("modifyDate"));
		}
		return super.findPage(criteriaQuery, pageable);
	}

	public List<Product> productTenantSelect(String keyword, Long tenantId, Boolean isGift, int count) {
		if (StringUtils.isEmpty(keyword)) {
			return Collections.<Product> emptyList();
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenantId));
		if (pattern.matcher(keyword).matches()) {
			restrictions = criteriaBuilder.and(restrictions,
					criteriaBuilder.or(criteriaBuilder.equal(root.get("id"), Long.valueOf(keyword)), criteriaBuilder.like(root.<String> get("sn"), "%" + keyword + "%"), criteriaBuilder.like(root.<String> get("fullName"), "%" + keyword + "%")));
		} else {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.like(root.<String> get("sn"), "%" + keyword + "%"), criteriaBuilder.like(root.<String> get("fullName"), "%" + keyword + "%")));
		}
		if (isGift != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isGift"), isGift));
		}
		criteriaQuery.where(restrictions);
		criteriaQuery.orderBy(criteriaBuilder.desc(root.get("isTop")));
		return super.findList(criteriaQuery, null, count, null, null);
	}

	@Override
	public Page<Product> findPage(Long tenantId, String searchValue, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenantId));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"),true));
		criteriaQuery.where(restrictions);
		criteriaQuery.orderBy(criteriaBuilder.desc(root.get("isTop")));
		return super.findPage(criteriaQuery, pageable);
	}

	public List findListByGroup() {
		String hql = "select p.productCategory from Product p group by p.productCategory";
		List list = entityManager.createQuery(hql).setFlushMode(FlushModeType.COMMIT).getResultList();
		return list;
	}

	@Override
	public List<Product> findList(Tenant tenant, Boolean isList) {
		if (tenant==null) {
			return Collections.<Product> emptyList();
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		
		if (isList != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isList"), isList));
		}
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery, null, null, null, null);
	}
}