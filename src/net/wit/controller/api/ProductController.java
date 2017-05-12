/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.controller.api;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.wit.Filter;
import net.wit.Order;
import net.wit.entity.Area;
import net.wit.entity.Brand;
import net.wit.entity.Community;
import net.wit.entity.Product;
import net.wit.entity.ProductCategory;
import net.wit.entity.Promotion;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.service.AreaService;
import net.wit.service.BrandService;
import net.wit.service.CommunityService;
import net.wit.service.ProductCategoryService;
import net.wit.service.ProductService;
import net.wit.service.PromotionService;
import net.wit.service.TagService;
import net.wit.service.TenantService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller - 商品API
 * @author rsico Team
 * @version 3.0
 */
@Controller("apiProductController")
@RequestMapping("/api/product")
public class ProductController extends BaseController {

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;

	@Resource(name = "promotionServiceImpl")
	private PromotionService promotionService;

	@Resource(name = "tagServiceImpl")
	private TagService tagService;

	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	@Resource(name = "brandServiceImpl")
	private BrandService brandService;

	@Resource(name = "communityServiceImpl")
	private CommunityService communityService;

	@Resource(name = "tenantServiceImpl")
	private TenantService tenantService;

	/**
	 * 获取商品分类根结点
	 */
	@RequestMapping(value = "/getProductCategoryRoots", method = RequestMethod.GET)
	public @ResponseBody
	List<ProductCategory> getProductCategoryRoots(Integer count) {
		List<ProductCategory> productCategories;
		productCategories = productCategoryService.findRoots(null, count);
		return productCategories;
	}

	/**
	 * 获取商品分类子结点
	 */
	@RequestMapping(value = "/getProductCategoryChildrens", method = RequestMethod.GET)
	public @ResponseBody
	List<ProductCategory> getProductCategoryChildrens(Long id, Integer count) {
		List<ProductCategory> productCategories;
		ProductCategory productCategory = productCategoryService.find(id);
		productCategories = productCategoryService.findChildren(productCategory, count, null);
		return productCategories;
	}

	/**
	 * 获取商品分类父结点
	 */
	@RequestMapping(value = "/getProductCategoryParents", method = RequestMethod.GET)
	public @ResponseBody
	List<ProductCategory> getProductCategoryParents(Long id, Integer count) {
		List<ProductCategory> productCategories;
		ProductCategory productCategory = productCategoryService.find(id);
		productCategories = productCategoryService.findParents(productCategory, count);
		return productCategories;
	}

	/**
	 * 获取商品
	 */
	@RequestMapping(value = "/getProducts", method = RequestMethod.GET)
	public @ResponseBody
	List<Product> getProducts(Long brandId, Long productCategoryId, Long promotionId, Long tenantId, String areaCode, String communityCode, Long[] tagIds, Boolean periferal, Integer count) {
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		Brand brand = brandService.find(brandId);
		Promotion promotion = promotionService.find(promotionId);
		List<Tag> tags = tagService.findList(tagIds);
		Area area = areaService.findByCode(areaCode);
		Tenant tenant = tenantService.find(tenantId);
		Community community = communityService.findbyCode(communityCode);
		List<Filter> filters = new ArrayList<Filter>();
		List<Order> orders = new ArrayList<Order>();
		List<Product> products;
		if ((productCategoryId != null && productCategory == null) || (brandId != null && brand == null) || (promotionId != null && promotion == null) || (tagIds != null && tags.isEmpty()) || (areaCode != null && area == null)) {
			products = new ArrayList<Product>();
		} else {
			products = productService.findList(productCategory, brand, promotion, tags, null, null, null, true, true, null, false, null, null, community, tenant, area, periferal, null, count, filters, orders);
		}
		return products;
	}

}