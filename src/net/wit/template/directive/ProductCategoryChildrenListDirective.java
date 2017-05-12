/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.template.directive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.wit.entity.ProductCategory;
import net.wit.entity.ProductCategoryTenant;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.service.ProductCategoryService;
import net.wit.service.ProductCategoryTenantService;
import net.wit.service.TagService;
import net.wit.service.TenantService;
import net.wit.util.FreemarkerUtils;

import org.springframework.stereotype.Component;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 下级商品分类列表
 * 
 * @author rsico Team
 * @version 3.0
 */
@Component("productCategoryChildrenListDirective")
public class ProductCategoryChildrenListDirective extends BaseDirective {

	/** "商品分类ID"参数名称 */
	private static final String PRODUCT_CATEGORY_ID_PARAMETER_NAME = "productCategoryId";
	
	/** "区域ID"参数名称 */
	private static final String TENANT_ID_PARAMETER_NAME = "tenantId";
	
	/** "标签ID"参数名称 */
	private static final String TAG_ID_PARAMETER_NAME = "tagIds";
	/** 变量名称 */
	private static final String VARIABLE_NAME = "productCategories";
	/** 变量名称 */
	private static final String VARIABLE_NAME_TENANT = "productCategoryTenants";

	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;
	@Resource(name="tenantServiceImpl")
	private TenantService tenantService;
	@Resource(name="productCategoryTenantServiceImpl")
	private ProductCategoryTenantService productCategoryTenantService ;
	@Resource(name = "tagServiceImpl")
	private TagService tagService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		Long productCategoryId = FreemarkerUtils.getParameter(PRODUCT_CATEGORY_ID_PARAMETER_NAME, Long.class, params);
		Long tenantId = FreemarkerUtils.getParameter(TENANT_ID_PARAMETER_NAME, Long.class, params);
		Long[] tagIds = FreemarkerUtils.getParameter(TAG_ID_PARAMETER_NAME, Long[].class, params);
		Tenant tenant = tenantService.find(tenantId);
		List<Tag> tags = tagService.findList(tagIds);
		if(tenant==null){
			ProductCategory productCategory = productCategoryService.find(productCategoryId);
			List<ProductCategory> productCategories;
			if (productCategoryId != null && productCategory == null) {
				productCategories = new ArrayList<ProductCategory>();
			} else {
				boolean useCache = useCache(env, params);
				String cacheRegion = getCacheRegion(env, params);
				Integer count = getCount(params);
				if (useCache) {
					productCategories = productCategoryService.findChildren(productCategory, count,tags, cacheRegion);
				} else {
					productCategories = productCategoryService.findChildren(productCategory, count,tags);
				}
			}
			setLocalVariable(VARIABLE_NAME, productCategories, env, body);
		}else{
			ProductCategoryTenant productCategoryTenant = productCategoryTenantService.find(productCategoryId);
			List<ProductCategoryTenant> productCategoryTenants;
			if (productCategoryId != null && productCategoryTenant == null) {
				productCategoryTenants = new ArrayList<ProductCategoryTenant>();
			} else {
				boolean useCache = useCache(env, params);
				String cacheRegion = getCacheRegion(env, params);
				Integer count = getCount(params);
				if (useCache) {
					productCategoryTenants = productCategoryTenantService.findChildren(productCategoryTenant, count,tenant, cacheRegion);
				} else {
					productCategoryTenants = productCategoryTenantService.findChildren(productCategoryTenant,count,tenant);
				}
			}
			setLocalVariable(VARIABLE_NAME_TENANT, productCategoryTenants, env, body);
		}
	}

}