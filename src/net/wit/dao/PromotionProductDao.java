package net.wit.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import net.wit.Filter;
import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Area;
import net.wit.entity.Attribute;
import net.wit.entity.Brand;
import net.wit.entity.Community;
import net.wit.entity.Location;
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

public interface PromotionProductDao extends BaseDao<PromotionProduct, Long> {

	Page<PromotionProduct> findPage(Type type, Promotion promotion, ProductCategory productCategory, Area area, Brand brand, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable,
			Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType, Boolean periferal, Community community, Pageable pageable);

	Page<PromotionProduct> findPage(Type type, Promotion promotion, Area area, TimeRegion region, List<Status> status, Classify classify, Boolean periferal, Community community, Location location, BigDecimal distatce, ProductCategory productCategory,
			Pageable pageable);

	Page<PromotionProduct> findPageNormal(Type type, Promotion promotion, Area area, TimeRegion region, List<Status> status, Classify classify, Boolean periferal, Community community, Location location, BigDecimal distatce,
			ProductCategory productCategory, Pageable pageable);

	Page<PromotionProduct> findProductPage(Pageable pageable, Tenant tenant, net.wit.entity.Promotion.Status status);

	Page<PromotionProduct> findPage(Type type, Promotion promotion, Area area, TimeRegion region, List<Status> status, Classify classify, Boolean periferal, Community community, Pageable pageable);

	List<PromotionProduct> findList(Promotion promotion, Type type, Area area, Integer count, List<Filter> filters, List<Order> orders);

}
