/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.persistence.FlushModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.Setting;
import net.wit.dao.GoodsDao;
import net.wit.dao.ProductDao;
import net.wit.dao.SnDao;
import net.wit.entity.Attribute;
import net.wit.entity.Brand;
import net.wit.entity.Goods;
import net.wit.entity.Product;
import net.wit.entity.Product.OrderType;
import net.wit.entity.ProductCategory;
import net.wit.entity.ProductCategoryTenant;
import net.wit.entity.Promotion;
import net.wit.entity.SpecificationValue;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.entity.Sn.Type;
import net.wit.util.SettingUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Dao - 货品
 * @author rsico Team
 * @version 3.0
 */
@Repository("goodsDaoImpl")
public class GoodsDaoImpl extends BaseDaoImpl<Goods, Long>implements GoodsDao {

	@Resource(name = "productDaoImpl")
	private ProductDao productDao;

	@Resource(name = "snDaoImpl")
	private SnDao snDao;

	/**
	 * 设置值并保存
	 * @param goods 货品
	 */
	@Override
	public void persist(Goods goods) {
		Assert.notNull(goods);

		if (goods.getProducts() != null) {
			for (Product product : goods.getProducts()) {
				setValue(product);
			}
		}
		super.persist(goods);
	}

	/**
	 * 设置值并更新
	 * @param goods 货品
	 * @return 货品
	 */
	@Override
	public Goods merge(Goods goods) {
		Assert.notNull(goods);

		if (goods.getProducts() != null) {
			for (Product product : goods.getProducts()) {
				if (product.getId() != null) {
					if (!product.getIsGift()) {
						String jpql = "delete from GiftItem giftItem where giftItem.gift = :product";
						entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).setParameter("product", product).executeUpdate();
					}
					if (!product.getIsMarketable() || product.getIsGift()) {
						String jpql = "delete from CartItem cartItem where cartItem.product = :product";
						entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).setParameter("product", product).executeUpdate();
					}
				}
				setValue(product);
			}
		}
		return super.merge(goods);
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
			} while (productDao.snExists(sn));
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

	public Page<Goods> findMyPage(Tenant tenant, ProductCategory productCategory, ProductCategoryTenant productCategoryTenant, Brand brand, Promotion promotion, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice,
			BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType, Pageable pageable) {
		if (tenant == null) {
			return new Page<Goods>(Collections.<Goods> emptyList(), 0, pageable);
		}

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Goods> createQuery = criteriaBuilder.createQuery(Goods.class);
		Root<Goods> root = createQuery.from(Goods.class);
		Predicate restrictions = criteriaBuilder.conjunction();
		createQuery.select(root);

		Subquery<Product> productSubquery = createQuery.subquery(Product.class);
		Root<Product> productRoot = productSubquery.from(Product.class);
		productSubquery.select(productRoot);
		Predicate suPredicates = criteriaBuilder.conjunction();
		suPredicates = criteriaBuilder.equal(suPredicates, criteriaBuilder.equal(productRoot.get("goods"), root));
		if (productCategory != null) {
			suPredicates = criteriaBuilder.and(suPredicates, criteriaBuilder.or(criteriaBuilder.equal(productRoot.get("productCategory"), productCategory),
					criteriaBuilder.like(productRoot.get("productCategory").<String> get("treePath"), "%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%")));
		}
		if (productCategoryTenant != null) {
			suPredicates = criteriaBuilder.and(suPredicates, criteriaBuilder.or(criteriaBuilder.equal(productRoot.get("productCategoryTenant"), productCategoryTenant),
					criteriaBuilder.like(productRoot.get("productCategoryTenant").<String> get("treePath"), "%" + ProductCategoryTenant.TREE_PATH_SEPARATOR + productCategoryTenant.getId() + ProductCategoryTenant.TREE_PATH_SEPARATOR + "%")));
		}
		if (brand != null) {
			suPredicates = criteriaBuilder.and(suPredicates, criteriaBuilder.equal(productRoot.get("brand"), brand));
		}
		suPredicates = criteriaBuilder.and(restrictions, criteriaBuilder.equal(productRoot.get("tenant"), tenant));
		if (attributeValue != null) {
			for (Entry<Attribute, String> entry : attributeValue.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				suPredicates = criteriaBuilder.and(suPredicates, criteriaBuilder.equal(productRoot.get(propertyName), entry.getValue()));
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(new BigDecimal(0)) >= 0) {
			suPredicates = criteriaBuilder.and(suPredicates, criteriaBuilder.ge(productRoot.<Number> get("price"), startPrice));
		}
		if (endPrice != null && endPrice.compareTo(new BigDecimal(0)) >= 0) {
			suPredicates = criteriaBuilder.and(suPredicates, criteriaBuilder.le(productRoot.<Number> get("price"), endPrice));
		}
		if (isMarketable != null) {
			suPredicates = criteriaBuilder.and(suPredicates, criteriaBuilder.equal(productRoot.get("isMarketable"), isMarketable));
		}
		if (isList != null) {
			suPredicates = criteriaBuilder.and(suPredicates, criteriaBuilder.equal(productRoot.get("isList"), isList));
		}
		if (isTop != null) {
			suPredicates = criteriaBuilder.and(suPredicates, criteriaBuilder.equal(productRoot.get("isTop"), isTop));
		}
		if (isGift != null) {
			suPredicates = criteriaBuilder.and(suPredicates, criteriaBuilder.equal(productRoot.get("isGift"), isGift));
		}
		Path<Integer> stock = productRoot.get("stock");
		Path<Integer> allocatedStock = productRoot.get("allocatedStock");
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				suPredicates = criteriaBuilder.and(suPredicates, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, allocatedStock));
			} else {
				suPredicates = criteriaBuilder.and(suPredicates, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, allocatedStock)));
			}
		}
		if (isStockAlert != null) {
			Setting setting = SettingUtils.get();
			if (isStockAlert) {
				suPredicates = criteriaBuilder.and(suPredicates, criteriaBuilder.isNotNull(stock), criteriaBuilder.lessThanOrEqualTo(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			} else {
				suPredicates = criteriaBuilder.and(suPredicates, criteriaBuilder.or(criteriaBuilder.isNull(stock), criteriaBuilder.greaterThan(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount()))));
			}
		}

		if (tags != null && !tags.isEmpty()) {
			Subquery<Product> subquery = productSubquery.subquery(Product.class);
			Root<Product> subqueryRoot = subquery.from(Product.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(productSubquery, subqueryRoot), subqueryRoot.join("tags").in(tags));
			suPredicates = criteriaBuilder.and(suPredicates, criteriaBuilder.exists(subquery));
		}
		productSubquery.where(suPredicates);
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(productSubquery));
		createQuery.where(restrictions);

		return super.findPage(createQuery, pageable);
	}

}