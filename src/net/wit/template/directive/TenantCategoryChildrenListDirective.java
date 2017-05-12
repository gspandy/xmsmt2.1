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

import net.wit.entity.TenantCategory;
import net.wit.service.TenantCategoryService;
import net.wit.util.FreemarkerUtils;

import org.springframework.stereotype.Component;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 下级企业分类列表
 * 
 * @author rsico Team
 * @version 3.0
 */
@Component("tenantCategoryChildrenListDirective")
public class TenantCategoryChildrenListDirective extends BaseDirective {

	/** "文章企业ID"参数名称 */
	private static final String TENANT_CATEGORY_ID_PARAMETER_NAME = "tenantCategoryId";

	/** 变量名称 */
	private static final String VARIABLE_NAME = "tenantCategories";

	@Resource(name = "tenantCategoryServiceImpl")
	private TenantCategoryService tenantCategoryService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		Long tenantCategoryId = (Long) FreemarkerUtils.getParameter(TENANT_CATEGORY_ID_PARAMETER_NAME, Long.class, params);

		TenantCategory tenantCategory = tenantCategoryService.find(tenantCategoryId);

		List<TenantCategory> tenantCategories;
		if (tenantCategoryId != null && tenantCategory == null) {
			tenantCategories = new ArrayList<TenantCategory>();
		} else {
			boolean useCache = useCache(env, params);
			String cacheRegion = getCacheRegion(env, params);
			Integer count = getCount(params);
			if (useCache) {
				tenantCategories = tenantCategoryService.findChildren(tenantCategory, count, cacheRegion);
			} else {
				tenantCategories = tenantCategoryService.findChildren(tenantCategory, count);
			}
		}
		setLocalVariable(VARIABLE_NAME, tenantCategories, env, body);
	}

}