/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.template.directive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.wit.entity.ProductCategory;
import net.wit.entity.ProductCategoryTenant;
import net.wit.entity.ProductChannel;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.service.ProductCategoryService;
import net.wit.service.ProductCategoryTenantService;
import net.wit.service.ProductChannelService;
import net.wit.service.TagService;
import net.wit.service.TenantService;
import net.wit.util.FreemarkerUtils;

import org.springframework.stereotype.Component;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 顶级商品分类列表
 * @author rsico Team
 * @version 3.0
 */
@Component("productCategoryRootListDirective")
public class ProductCategoryRootListDirective extends BaseDirective {

	/** 变量名称 */
	private static final String VARIABLE_NAME = "productCategories";

	/** "区域ID"参数名称 */
	private static final String TENANT_ID_PARAMETER_NAME = "tenantId";

	/** "区域ID"参数名称 */
	private static final String CHANNE_ID_PARAMETER_NAME = "channelId";

	/** "标签ID"参数名称 */
	private static final String TAG_ID_PARAMETER_NAME = "productCategoryTagId";

	/** 变量名称 */
	private static final String VARIABLE_NAME_TENANT = "productCategoryTenants";

	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;

	@Resource(name = "tagServiceImpl")
	private TagService tagService;

	@Resource(name = "tenantServiceImpl")
	private TenantService tenantService;

	@Resource(name = "productCategoryTenantServiceImpl")
	private ProductCategoryTenantService productCategoryTenantService;

	@Resource(name = "productChannelServiceImpl")
	private ProductChannelService productChannelService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		Long productCategoryTagId = FreemarkerUtils.getParameter(TAG_ID_PARAMETER_NAME, Long.class, params);
		Tag tag = tagService.find(productCategoryTagId);
		Long tenantId = FreemarkerUtils.getParameter(TENANT_ID_PARAMETER_NAME, Long.class, params);
		Long channelId = FreemarkerUtils.getParameter(CHANNE_ID_PARAMETER_NAME, Long.class, params);
		ProductChannel channel = productChannelService.find(channelId);
		Tenant tenant = tenantService.find(tenantId);
		if (channel != null) {
			List<ProductCategory> productCategories;
			boolean useCache = useCache(env, params);
			String cacheRegion = getCacheRegion(env, params);
			Integer count = getCount(params);
			if (useCache) {
				productCategories = productCategoryService.findRootsByChannel(channel, tag, count, cacheRegion);
			} else {
				productCategories = productCategoryService.findRootsByChannel(channel, tag, count);
			}
			setLocalVariable(VARIABLE_NAME, productCategories, env, body);
		} else {
			if (tenant == null) {
				List<ProductCategory> productCategories;
				boolean useCache = useCache(env, params);
				String cacheRegion = getCacheRegion(env, params);
				Integer count = getCount(params);
				if (tag == null) {
					if (useCache) {
						productCategories = productCategoryService.findRoots(count, cacheRegion);
					} else {
						productCategories = productCategoryService.findRoots(count);
					}
				} else {
					if (useCache) {
						productCategories = productCategoryService.findRoots(tag, count, cacheRegion);
					} else {
						productCategories = productCategoryService.findRoots(tag, count);
					}
				}
				setLocalVariable(VARIABLE_NAME, productCategories, env, body);
			} else {
				List<ProductCategoryTenant> productCategoryTenants;
				boolean useCache = useCache(env, params);
				String cacheRegion = getCacheRegion(env, params);
				Integer count = getCount(params);
				if (tag == null) {
					if (useCache) {
						productCategoryTenants = productCategoryTenantService.findRoots(count, tenant, cacheRegion);
					} else {
						productCategoryTenants = productCategoryTenantService.findRoots(count, tenant);
					}
				} else {
					if (useCache) {
						productCategoryTenants = productCategoryTenantService.findRoots(tag, count, tenant, cacheRegion);
					} else {
						productCategoryTenants = productCategoryTenantService.findRoots(tag, count, tenant);
					}
				}
				setLocalVariable(VARIABLE_NAME_TENANT, productCategoryTenants, env, body);
			}
		}
	}

}