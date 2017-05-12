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
import net.wit.entity.Article;
import net.wit.entity.ArticleCategory;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.service.AreaService;
import net.wit.service.ArticleCategoryService;
import net.wit.service.ArticleService;
import net.wit.service.TagService;
import net.wit.service.TenantService;
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
@Component("articleListDirective")
public class ArticleListDirective extends BaseDirective {

	/** "文章分类ID"参数名称 */
	private static final String ARTICLE_CATEGORY_ID_PARAMETER_NAME = "articleCategoryId";

	/** "标签ID"参数名称 */
	private static final String TAG_IDS_PARAMETER_NAME = "tagIds";
	
	/** "区域"参数名称 */
	private static final String AREA_ID_PARAMETER_NAME = "areaId";
	
	/** "商家"参数名称 */
	private static final String TENANT_ID_PARAMETER_NAME = "tenantId";

	/** 变量名称 */
	private static final String VARIABLE_NAME = "articles";

	@Resource(name = "articleServiceImpl")
	private ArticleService articleService;
	@Resource(name = "articleCategoryServiceImpl")
	private ArticleCategoryService articleCategoryService;
	@Resource(name = "tagServiceImpl")
	private TagService tagService;
	@Resource(name = "areaServiceImpl")
	private AreaService areaService;
	@Resource(name="tenantServiceImpl")
	private TenantService tenantService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		Long articleCategoryId = FreemarkerUtils.getParameter(ARTICLE_CATEGORY_ID_PARAMETER_NAME, Long.class, params);
		Long[] tagIds = FreemarkerUtils.getParameter(TAG_IDS_PARAMETER_NAME, Long[].class, params);
		Long areaId = FreemarkerUtils.getParameter(AREA_ID_PARAMETER_NAME, Long.class, params);
		Long tenantId = FreemarkerUtils.getParameter(TENANT_ID_PARAMETER_NAME, Long.class, params);

		ArticleCategory articleCategory = articleCategoryService.find(articleCategoryId);
		List<Tag> tags = tagService.findList(tagIds);
		Area area = areaService.find(areaId);
		Tenant tenant = tenantService.find(tenantId);

		List<Article> articles;
		if ((articleCategoryId != null && articleCategory == null) || (tagIds != null && tags.isEmpty())) {
			articles = new ArrayList<Article>();
		} else {
			boolean useCache = useCache(env, params);
			String cacheRegion = getCacheRegion(env, params);
			Integer count = getCount(params);
			List<Filter> filters = getFilters(params, Article.class);
			List<Order> orders = getOrders(params);
			if (useCache) {
				articles = articleService.findList(articleCategory, tags,tenant,area, count, filters, orders, cacheRegion);
			} else {
				articles = articleService.findList(articleCategory, tags,tenant,area, count, filters, orders);
			}
		}
		setLocalVariable(VARIABLE_NAME, articles, env, body);
	}

}