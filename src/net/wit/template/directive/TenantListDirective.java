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

import net.wit.Filter;
import net.wit.Order;
import net.wit.entity.Area;
import net.wit.entity.Community;
import net.wit.entity.Tenant;
import net.wit.entity.TenantCategory;
import net.wit.entity.Tag;
import net.wit.service.AreaService;
import net.wit.service.CommunityService;
import net.wit.service.TenantCategoryService;
import net.wit.service.TenantService;
import net.wit.service.TagService;
import net.wit.util.FreemarkerUtils;

import org.springframework.stereotype.Component;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 文章列表
 * 
 * @author rsico Team
 * @version 3.0
 */
@Component("tenantListDirective")
public class TenantListDirective extends BaseDirective {

	/** "文章分类ID"参数名称 */
	private static final String tenant_CATEGORY_ID_PARAMETER_NAME = "tenantCategoryId";

	/** "标签ID"参数名称 */
	private static final String TAG_IDS_PARAMETER_NAME = "tagIds";
	
	/** "区域"参数名称 */
	private static final String AREA_ID_PARAMETER_NAME = "areaId";
	
	/** "社区商圈"参数名称 */
	private static final String COMMUNITY_ID_PARAMETER_NAME = "communityId";
	
	/** "是否周边"参数名称 */
	private static final String PERIFERAL_PARAMETER_NAME = "periferal";

	/** 变量名称 */
	private static final String VARIABLE_NAME = "tenants";

	@Resource(name = "tenantServiceImpl")
	private TenantService tenantService;
	@Resource(name = "tenantCategoryServiceImpl")
	private TenantCategoryService tenantCategoryService;
	@Resource(name = "tagServiceImpl")
	private TagService tagService;
	@Resource(name = "areaServiceImpl")
	private AreaService areaService;
	@Resource(name = "communityServiceImpl")
	private CommunityService communityService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		Long tenantCategoryId = (Long) FreemarkerUtils.getParameter(tenant_CATEGORY_ID_PARAMETER_NAME, Long.class, params);
		Long[] tagIds = (Long[]) FreemarkerUtils.getParameter(TAG_IDS_PARAMETER_NAME, Long[].class, params);
		Long areaId = (Long) FreemarkerUtils.getParameter(AREA_ID_PARAMETER_NAME, Long.class, params);
		Long communityId = (Long) FreemarkerUtils.getParameter(COMMUNITY_ID_PARAMETER_NAME, Long.class, params);
		Boolean periferal = (Boolean) FreemarkerUtils.getParameter(PERIFERAL_PARAMETER_NAME, Boolean.class, params);

		TenantCategory tenantCategory = tenantCategoryService.find(tenantCategoryId);
		List<Tag> tags = tagService.findList(tagIds);
		Area area = areaService.find(areaId);
		Community community = communityService.find(communityId);

		List<Tenant> tenants;
		if ((tenantCategoryId != null && tenantCategory == null) || (tagIds != null && tags.isEmpty()) || (areaId != null && area == null) || (communityId != null && community == null) ) {
			tenants = new ArrayList<Tenant>();
		} else {
			boolean useCache = useCache(env, params);
			String cacheRegion = getCacheRegion(env, params);
			Integer count = getCount(params);
			List<Filter> filters = getFilters(params, Tenant.class);
			List<Order> orders = getOrders(params);
			if (useCache) {
				tenants = tenantService.findList(tenantCategory, tags,area,community,periferal, count, filters, orders, cacheRegion);
			} else {
				tenants = tenantService.findList(tenantCategory, tags,area,community,periferal, count, filters, orders);
			}
		}
		setLocalVariable(VARIABLE_NAME, tenants, env, body);
	}

}