package net.wit.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
import net.wit.dao.PromotionProductDao;
import net.wit.entity.Area;
import net.wit.entity.Attribute;
import net.wit.entity.Brand;
import net.wit.entity.Community;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.Location;
import net.wit.entity.Product;
import net.wit.entity.Product.OrderType;
import net.wit.entity.ProductCategory;
import net.wit.entity.Promotion;
import net.wit.entity.Promotion.Classify;
import net.wit.entity.Promotion.Status;
import net.wit.entity.Promotion.Type;
import net.wit.entity.PromotionProduct;
import net.wit.entity.PromotionProduct.TimeRegion;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.util.SettingUtils;

import org.springframework.stereotype.Repository;

@Repository("promotionProductDaoImpl")
public class PromotionProductDaoImpl extends BaseDaoImpl<PromotionProduct, Long> implements PromotionProductDao {
	@Resource(name = "deliveryCenterDaoImpl")
	private DeliveryCenterDao deliveryCenterDao;

	public Page<PromotionProduct> findPage(Type type, Promotion promotion, ProductCategory productCategory, Area area, Brand brand, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable,
			Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType, Boolean periferal, Community community, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<PromotionProduct> criteriaQuery = criteriaBuilder.createQuery(PromotionProduct.class);
		Root<PromotionProduct> root = criteriaQuery.from(PromotionProduct.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (productCategory != null) {
			restrictions = criteriaBuilder.and(
					restrictions,
					criteriaBuilder.or(criteriaBuilder.equal(root.get("product").get("productCategory"), productCategory),
							criteriaBuilder.like(root.get("product").get("productCategory").<String> get("treePath"), "%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%")));
		}
		if (brand != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("product").get("brand"), brand));
		}
		if (type != null) {
			if (promotion != null) {
				Subquery<PromotionProduct> subquery1 = criteriaQuery.subquery(PromotionProduct.class);
				Root<PromotionProduct> subqueryRoot1 = subquery1.from(PromotionProduct.class);
				subquery1.select(subqueryRoot1);
				subquery1.where(criteriaBuilder.equal(subqueryRoot1, root), criteriaBuilder.and(criteriaBuilder.equal(subqueryRoot1.get("promotion"), promotion), criteriaBuilder.equal(subqueryRoot1.get("promotion").get("type"), type)));

				Subquery<PromotionProduct> subquery2 = criteriaQuery.subquery(PromotionProduct.class);
				Root<PromotionProduct> subqueryRoot2 = subquery2.from(PromotionProduct.class);
				subquery2.select(subqueryRoot2);
				subquery2.where(criteriaBuilder.equal(subqueryRoot2, root), criteriaBuilder.equal(subqueryRoot2.get("product").get("productCategory").get("promotions"), promotion));

				Subquery<PromotionProduct> subquery3 = criteriaQuery.subquery(PromotionProduct.class);
				Root<PromotionProduct> subqueryRoot3 = subquery3.from(PromotionProduct.class);
				subquery3.select(subqueryRoot3);
				subquery3.where(criteriaBuilder.equal(subqueryRoot3, root), criteriaBuilder.equal(subqueryRoot3.join("product").get("brand").get("promotions"), promotion));

				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.exists(subquery1), criteriaBuilder.exists(subquery2), criteriaBuilder.exists(subquery3)));
			} else {
				Subquery<PromotionProduct> subquery1 = criteriaQuery.subquery(PromotionProduct.class);
				Root<PromotionProduct> subqueryRoot1 = subquery1.from(PromotionProduct.class);
				subquery1.select(subqueryRoot1);
				subquery1.where(criteriaBuilder.equal(subqueryRoot1, root), criteriaBuilder.equal(subqueryRoot1.get("promotion").get("type"), type));

				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery1));

			}
		}
		if (attributeValue != null) {
			for (Entry<Attribute, String> entry : attributeValue.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("product").get(propertyName), entry.getValue()));
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
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("product").get("isMarketable"), isMarketable));
		}
		if (isList != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("product").get("isList"), isList));
		}
		if (isTop != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("product").get("isTop"), isTop));
		}
		if (isGift != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("product").get("isGift"), isGift));
		}
		Path<Integer> stock = root.get("product").get("stock");
		Path<Integer> allocatedStock = root.get("product").get("allocatedStock");
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
		if (area != null) {
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
				return new Page<PromotionProduct>(Collections.<PromotionProduct> emptyList(), 0, pageable);
			}
			restrictions = criteriaBuilder.and(restrictions, root.get("product").<Tenant> get("tenant").in(tenants));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<PromotionProduct> findPage(Type type, Promotion promotion, Area area, TimeRegion region, List<Status> status, Classify classify, Boolean periferal, Community community, Location location, BigDecimal distatce,
			ProductCategory productCategory, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<PromotionProduct> criteriaQuery = criteriaBuilder.createQuery(PromotionProduct.class);
		Root<PromotionProduct> root = criteriaQuery.from(PromotionProduct.class);
		Predicate restrictions = criteriaBuilder.conjunction();
		criteriaQuery.select(root);
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("promotion").get("type"), type));
		if (promotion != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("promotion"), promotion));
		}
		if (productCategory != null) {
			restrictions = criteriaBuilder.and(
					restrictions,
					criteriaBuilder.or(criteriaBuilder.equal(root.get("product").get("productCategory"), productCategory),
							criteriaBuilder.like(root.get("product").get("productCategory").<String> get("treePath"), "%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%")));
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
			return new Page<PromotionProduct>(Collections.<PromotionProduct> emptyList(), 0, pageable);
		}
		restrictions = criteriaBuilder.and(restrictions, root.<Tenant> get("product").get("tenant").in(tenants));
		// restrictions = criteriaBuilder.and(restrictions, root.<Tenant> get("tenant").in(tenants));

		if (region != null) {
			Calendar calendar = Calendar.getInstance();
			if (TimeRegion.notstart == region) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThan(root.get("promotion").<Date> get("beginDate"), calendar.getTime()));
			} else if (TimeRegion.progress == region) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThan(root.get("promotion").<Date> get("endDate"), calendar.getTime()));
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThan(root.get("promotion").<Date> get("beginDate"), calendar.getTime()));
			} else if (TimeRegion.past == region) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThan(root.get("promotion").<Date> get("endDate"), calendar.getTime()));
			}
		}
		if (classify != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("promotion").get("classify"), classify));
		}
		if (status != null && status.size() > 0) {
			restrictions = criteriaBuilder.and(restrictions, root.get("promotion").get("status").in(status));
		}

		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<PromotionProduct> findPageNormal(Type type, Promotion promotion, Area area, TimeRegion region, List<Status> status, Classify classify, Boolean periferal, Community community, Location location, BigDecimal distatce,
			ProductCategory productCategory, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<PromotionProduct> criteriaQuery = criteriaBuilder.createQuery(PromotionProduct.class);
		Root<PromotionProduct> root = criteriaQuery.from(PromotionProduct.class);
		Predicate restrictions = criteriaBuilder.conjunction();
		criteriaQuery.select(root);
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("promotion").get("type"), type));
		if (promotion != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("promotion"), promotion));
		}
		if (productCategory != null) {
			restrictions = criteriaBuilder.and(
					restrictions,
					criteriaBuilder.or(criteriaBuilder.equal(root.get("product").get("productCategory"), productCategory),
							criteriaBuilder.like(root.get("product").get("productCategory").<String> get("treePath"), "%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%")));
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
			// restrictions = criteriaBuilder.and(restrictions, root.<Tenant> get("tenant").in(tenants));
			restrictions = criteriaBuilder.and(restrictions, root.<Tenant> get("product").get("tenant").in(tenants));
		}
		if (region != null) {
			Calendar calendar = Calendar.getInstance();
			if (TimeRegion.notstart == region) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThan(root.get("promotion").<Date> get("beginDate"), calendar.getTime()));
			} else if (TimeRegion.progress == region) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThan(root.get("promotion").<Date> get("endDate"), calendar.getTime()));
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThan(root.get("promotion").<Date> get("beginDate"), calendar.getTime()));
			} else if (TimeRegion.past == region) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThan(root.get("promotion").<Date> get("endDate"), calendar.getTime()));
			}
		}
		if (classify != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("promotion").get("classify"), classify));
		}
		if (status != null && status.size() > 0) {
			restrictions = criteriaBuilder.and(restrictions, root.get("promotion").get("status").in(status));
		}

		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<PromotionProduct> findPage(Type type, Promotion promotion, Area area, TimeRegion region, List<Status> status, Classify classify, Boolean periferal, Community community, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<PromotionProduct> criteriaQuery = criteriaBuilder.createQuery(PromotionProduct.class);
		Root<PromotionProduct> root = criteriaQuery.from(PromotionProduct.class);
		Predicate restrictions = criteriaBuilder.conjunction();
		criteriaQuery.select(root);
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("promotion").get("type"), type));
		if (promotion != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("promotion"), promotion));
		}
		if (area != null) {
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
				return new Page<PromotionProduct>(Collections.<PromotionProduct> emptyList(), 0, pageable);
			}
			restrictions = criteriaBuilder.and(restrictions, root.get("product").<Tenant> get("tenant").in(tenants));
		}
		if (region != null) {
			Calendar calendar = Calendar.getInstance();
			if (TimeRegion.notstart == region) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThan(root.get("promotion").<Date> get("beginDate"), calendar.getTime()));
			} else if (TimeRegion.progress == region) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThan(root.get("promotion").<Date> get("endDate"), calendar.getTime()));
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThan(root.get("promotion").<Date> get("beginDate"), calendar.getTime()));
			} else if (TimeRegion.past == region) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThan(root.get("promotion").<Date> get("endDate"), calendar.getTime()));
			}
		}
		if (classify != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("promotion").get("classify"), classify));
		}
		if (status != null && status.size() > 0) {
			restrictions = criteriaBuilder.and(restrictions, root.get("promotion").get("status").in(status));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<PromotionProduct> findProductPage(Pageable pageable, Tenant tenant, Status status) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<PromotionProduct> criteriaQuery = criteriaBuilder.createQuery(PromotionProduct.class);
		Root<PromotionProduct> root = criteriaQuery.from(PromotionProduct.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("product").get("tenant").get("id"), tenant.getId()));
		restrictions = criteriaBuilder.and(restrictions, root.get("promotion").get("status").in(status));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public List<PromotionProduct> findList(Promotion promotion, Type type,  Area area,Integer count, List<Filter> filters, List<Order> orders) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<PromotionProduct> criteriaQuery = criteriaBuilder.createQuery(PromotionProduct.class);
		Root<PromotionProduct> root = criteriaQuery.from(PromotionProduct.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (promotion != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("promotion"), promotion.getId()));
		}
		if (type != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("promotion").get("type"), type));
		}
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(root.get("promotion").<Date> get("endDate")));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(root.get("promotion").<Date> get("beginDate")));
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery, null, count, filters, orders);
	}
}
