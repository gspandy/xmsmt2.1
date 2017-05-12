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

import net.wit.entity.ProductChannel;
import net.wit.entity.TenantCategory;
import net.wit.service.ProductChannelService;
import net.wit.service.TenantCategoryService;
import net.wit.util.FreemarkerUtils;

import org.springframework.stereotype.Component;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 上级企业分类列表
 * @author rsico Team
 * @version 3.0
 */
@Component("tenantCategoryParentListDirective")
public class TenantCategoryParentListDirective extends BaseDirective {
	/** "文章企业ID"参数名称 */
	private static final String tenant_CATEGORY_ID_PARAMETER_NAME = "tenantCategoryId";

	/** "频道ID"参数名称 */
	private static final String PRODUCTCHANNEL_ID_PARAMETER_NAME = "productChannelId";

	/** 变量名称 */
	private static final String VARIABLE_NAME = "tenantCategories";

	@Resource(name = "productChannelServiceImpl")
	private ProductChannelService productChannelService;

	@Resource(name = "tenantCategoryServiceImpl")
	private TenantCategoryService tenantCategoryService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		Long tenantCategoryId = (Long) FreemarkerUtils.getParameter(tenant_CATEGORY_ID_PARAMETER_NAME, Long.class, params);
		Long productChannelId = FreemarkerUtils.getParameter(PRODUCTCHANNEL_ID_PARAMETER_NAME, Long.class, params);
		TenantCategory tenantCategory = tenantCategoryService.find(tenantCategoryId);
		ProductChannel productChannel = productChannelService.find(productChannelId);
		List<TenantCategory> tenantCategories;
		if (tenantCategoryId != null && tenantCategory == null) {
			tenantCategories = new ArrayList<TenantCategory>();
		} else {
			boolean useCache = useCache(env, params);
			String cacheRegion = getCacheRegion(env, params);
			Integer count = getCount(params);
			if (useCache) {
				tenantCategories = tenantCategoryService.findParents(productChannel, tenantCategory, count, cacheRegion);
			} else {
				tenantCategories = tenantCategoryService.findParents(productChannel, tenantCategory, count);
			}
		}
		setLocalVariable(VARIABLE_NAME, tenantCategories, env, body);
	}

}