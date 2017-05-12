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

import net.wit.Filter;
import net.wit.Order;
import net.wit.entity.Area;
import net.wit.entity.Promotion;
import net.wit.entity.Promotion.Type;
import net.wit.service.AreaService;
import net.wit.service.PromotionService;
import net.wit.util.FreemarkerUtils;

import org.springframework.stereotype.Component;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 促销列表
 * 
 * @author rsico Team
 * @version 3.0
 */
@Component("promotionListDirective")
public class PromotionListDirective extends BaseDirective {

	private static final String RAWTYPES = "rawtypes";

	/** "是否已开始"参数名称 */
	private static final String HAS_BEGUN_PARAMETER_NAME = "hasBegun";

	/** "是否已结束"参数名称 */
	private static final String HAS_ENDED_PARAMETER_NAME = "hasEnded";
	
	/** "区域"参数名称 */
	private static final String AREA_ID_PARAMETER_NAME = "areaId";
	/** "是否已开始"参数名称 */
	private static final String PROMOTION_TYPE = "promotionType";

	/** 变量名称 */
	private static final String VARIABLE_NAME = "promotions";

	@Resource(name = "promotionServiceImpl")
	private PromotionService promotionService;
	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	@SuppressWarnings({ "unchecked", RAWTYPES })
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		Boolean hasBegun = (Boolean) FreemarkerUtils.getParameter(HAS_BEGUN_PARAMETER_NAME, Boolean.class, params);
		Boolean hasEnded = (Boolean) FreemarkerUtils.getParameter(HAS_ENDED_PARAMETER_NAME, Boolean.class, params);
		Long areaId = (Long) FreemarkerUtils.getParameter(AREA_ID_PARAMETER_NAME, Long.class, params);
		Type type = (Type) FreemarkerUtils.getParameter(PROMOTION_TYPE, Type.class, params);

		Area area = areaService.find(areaId);
		List<Promotion> promotions;
		boolean useCache = useCache(env, params);
		String cacheRegion = getCacheRegion(env, params);
		Integer count = getCount(params);
		List<Filter> filters = getFilters(params, Promotion.class);
		List<Order> orders = getOrders(params);
		if (useCache) {
			promotions = promotionService.findList(type,hasBegun, hasEnded, area, count, filters, orders, cacheRegion);
		} else {
			promotions = promotionService.findList(type,hasBegun, hasEnded, area, count, filters, orders);
		}
		setLocalVariable(VARIABLE_NAME, promotions, env, body);
	}

}