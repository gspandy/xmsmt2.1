/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.template.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.wit.entity.TenantCategory;
import net.wit.service.TenantCategoryService;

import org.springframework.stereotype.Component;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 顶级企业分类列表
 * 
 * @author rsico Team
 * @version 3.0
 */
@Component("tenantCategoryRootListDirective")
public class TenantCategoryRootListDirective extends BaseDirective {

	/** 变量名称 */
	private static final String VARIABLE_NAME = "tenantCategories";

	@Resource(name = "tenantCategoryServiceImpl")
	private TenantCategoryService tenantCategoryService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		List<TenantCategory> tenantCategories;
		boolean useCache = useCache(env, params);
		String cacheRegion = getCacheRegion(env, params);
		Integer count = getCount(params);
		if (useCache) {
			tenantCategories = tenantCategoryService.findRoots(count, cacheRegion);
		} else {
			tenantCategories = tenantCategoryService.findRoots(count);
		}
		setLocalVariable(VARIABLE_NAME, tenantCategories, env, body);
	}

}