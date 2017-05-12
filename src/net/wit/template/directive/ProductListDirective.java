/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.template.directive;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import net.wit.Filter;
import net.wit.Order;
import net.wit.entity.Area;
import net.wit.entity.Article;
import net.wit.entity.Attribute;
import net.wit.entity.Brand;
import net.wit.entity.Community;
import net.wit.entity.Product;
import net.wit.entity.Product.OrderType;
import net.wit.entity.ProductCategory;
import net.wit.entity.ProductChannel;
import net.wit.entity.Promotion;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.service.AreaService;
import net.wit.service.AttributeService;
import net.wit.service.BrandService;
import net.wit.service.CommunityService;
import net.wit.service.ProductCategoryService;
import net.wit.service.ProductChannelService;
import net.wit.service.ProductService;
import net.wit.service.PromotionService;
import net.wit.service.TagService;
import net.wit.service.TenantService;
import net.wit.util.FreemarkerUtils;

import org.springframework.stereotype.Component;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 商品列表
 * 
 * @author rsico Team
 * @version 3.0
 */
@Component("productListDirective")
public class ProductListDirective extends BaseDirective {

	/** "商品分类ID"参数名称 */
	private static final String PRODUCT_CATEGORY_ID_PARAMETER_NAME = "productCategoryId";

	/** "品牌ID"参数名称 */
	private static final String BRAND_ID_PARAMETER_NAME = "brandId";

	/** "区域ID"参数名称 */
	private static final String TENANT_ID_PARAMETER_NAME = "tenantId";

	/** "区域"参数名称 */
	private static final String AREA_ID_PARAMETER_NAME = "areaId";

	/** "社区商圈"参数名称 */
	private static final String COMMUNITY_ID_PARAMETER_NAME = "communityId";

	/** "是否周边"参数名称 */
	private static final String PERIFERAL_PARAMETER_NAME = "periferal";

	/** "促销ID"参数名称 */
	private static final String PROMOTION_ID_PARAMETER_NAME = "promotionId";
	/** "频道ID"参数名称 */
	private static final String CHANNEL_ID_PARAMETER_NAME = "channelId";

	/** "标签ID"参数名称 */
	private static final String TAG_IDS_PARAMETER_NAME = "tagIds";

	/** "属性值"参数名称 */
	private static final String ATTRIBUTE_VALUE_PARAMETER_NAME = "attributeValue";

	/** "最低价格"参数名称 */
	private static final String START_PRICE_PARAMETER_NAME = "startPrice";

	/** "最高价格"参数名称 */
	private static final String END_PRICE_PARAMETER_NAME = "endPrice";

	/** "排序类型"参数名称 */
	private static final String ORDER_TYPE_PARAMETER_NAME = "orderType";

	/** 变量名称 */
	private static final String VARIABLE_NAME = "products";

	@Resource(name = "productServiceImpl")
	private ProductService productService;
	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;
	@Resource(name = "brandServiceImpl")
	private BrandService brandService;
	@Resource(name = "tenantServiceImpl")
	private TenantService tenantService;
	@Resource(name = "promotionServiceImpl")
	private PromotionService promotionService;
	@Resource(name = "attributeServiceImpl")
	private AttributeService attributeService;
	@Resource(name = "tagServiceImpl")
	private TagService tagService;
	@Resource(name = "areaServiceImpl")
	private AreaService areaService;
	@Resource(name = "communityServiceImpl")
	private CommunityService communityService;
	@Resource(name = "productChannelServiceImpl")
	private ProductChannelService productChannelService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		Long productCategoryId = FreemarkerUtils.getParameter(PRODUCT_CATEGORY_ID_PARAMETER_NAME, Long.class, params);
		Long brandId = FreemarkerUtils.getParameter(BRAND_ID_PARAMETER_NAME, Long.class, params);
		Long tenantId = FreemarkerUtils.getParameter(TENANT_ID_PARAMETER_NAME, Long.class, params);
		Long promotionId = FreemarkerUtils.getParameter(PROMOTION_ID_PARAMETER_NAME, Long.class, params);
		Long channelId = FreemarkerUtils.getParameter(CHANNEL_ID_PARAMETER_NAME, Long.class, params);
		Long areaId = FreemarkerUtils.getParameter(AREA_ID_PARAMETER_NAME, Long.class, params);
		Long communityId = FreemarkerUtils.getParameter(COMMUNITY_ID_PARAMETER_NAME, Long.class, params);
		Boolean periferal = FreemarkerUtils.getParameter(PERIFERAL_PARAMETER_NAME, Boolean.class, params);
		Long[] tagIds = FreemarkerUtils.getParameter(TAG_IDS_PARAMETER_NAME, Long[].class, params);
		Map<Long, String> attributeValue = FreemarkerUtils.getParameter(ATTRIBUTE_VALUE_PARAMETER_NAME, Map.class, params);
		BigDecimal startPrice = FreemarkerUtils.getParameter(START_PRICE_PARAMETER_NAME, BigDecimal.class, params);
		BigDecimal endPrice = FreemarkerUtils.getParameter(END_PRICE_PARAMETER_NAME, BigDecimal.class, params);
		OrderType orderType = FreemarkerUtils.getParameter(ORDER_TYPE_PARAMETER_NAME, OrderType.class, params);

		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		Brand brand = brandService.find(brandId);
		Tenant tenant = tenantService.find(tenantId);
		Promotion promotion = promotionService.find(promotionId);
		Area area = areaService.find(areaId);
		Community community = communityService.find(communityId);
		List<Tag> tags = tagService.findList(tagIds);
		Map<Attribute, String> attributeValueMap = new HashMap<Attribute, String>();
		if (attributeValue != null) {
			for (Entry<Long, String> entry : attributeValue.entrySet()) {
				Attribute attribute = attributeService.find(entry.getKey());
				if (attribute != null) {
					attributeValueMap.put(attribute, entry.getValue());
				}
			}
		}
		
		List<Product> products;
		ProductChannel channel = productChannelService.find(channelId);
		if ((channelId != null && channel == null)||(productCategoryId != null && productCategory == null) || (brandId != null && brand == null) || (areaId != null && area == null) || (communityId != null && community == null) || (promotionId != null && promotion == null) || (tagIds != null && tags.isEmpty()) || (attributeValue != null && attributeValueMap.isEmpty())) {
			products = new ArrayList<Product>();
		} else {
			boolean useCache = useCache(env, params);
			String cacheRegion = getCacheRegion(env, params);
			Integer count = getCount(params);
			List<Filter> filters = getFilters(params, Article.class);
			List<Order> orders = getOrders(params);
			Set<ProductCategory> productCategories = new HashSet<ProductCategory>();
			if(productCategory==null&&channel!=null){
				productCategories=channel.getProductCategorys();
			}else if(productCategory!=null){
				productCategories.add(productCategory);
			}
			if (useCache) {
				products = productService.findList(productCategories, brand, promotion, tags, attributeValueMap, startPrice, endPrice, true, true, null, false, null, null,community,tenant,area,periferal, orderType, count, filters, orders, cacheRegion);
			} else {
				products = productService.findList(productCategories, brand, promotion, tags, attributeValueMap, startPrice, endPrice, true, true, null, false, null, null,community,tenant,area,periferal, orderType, count, filters, orders);
			}
		}
		setLocalVariable(VARIABLE_NAME, products, env, body);
	}
}