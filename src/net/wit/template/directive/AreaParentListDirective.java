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

import net.wit.entity.Area;
import net.wit.service.AreaService;
import net.wit.util.FreemarkerUtils;

import org.springframework.stereotype.Component;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 上级区域列表
 * 
 * @author rsico Team
 * @version 3.0
 */
@Component("areaParentListDirective")
public class AreaParentListDirective extends BaseDirective {
	/** "文章分类ID"参数名称 */
	private static final String ARTICLE_CATEGORY_ID_PARAMETER_NAME = "areaId";

	/** 变量名称 */
	private static final String VARIABLE_NAME = "areas";

	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		Long areaId = (Long) FreemarkerUtils.getParameter(ARTICLE_CATEGORY_ID_PARAMETER_NAME, Long.class, params);

		Area area = areaService.find(areaId).getParent();
		List<Area> areas;
		if (area != null) {
			Area parea = area.getParent();
			if (parea != null) {
		        areas = new ArrayList<Area>(parea.getChildren());
			} else {
				areas = areaService.findRoots();
			}
		} else {
			areas = areaService.findRoots();
		}
		setLocalVariable(VARIABLE_NAME, areas, env, body);
	}

}