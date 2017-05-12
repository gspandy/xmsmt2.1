/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.template.directive;

import java.io.IOException;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import net.wit.entity.Area;
import net.wit.service.AreaService;

import org.springframework.stereotype.Component;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 当前会员
 * 
 * @author rsico Team
 * @version 3.0
 */
@Component("currentSessionAreaDirective")
public class CurrentSessionAreaDirective extends BaseDirective {

	/** 变量名称 */
	private static final String VARIABLE_NAME = "currentArea";
	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	@SuppressWarnings("rawtypes")
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		if (body != null) {
			setLocalVariable(VARIABLE_NAME, areaService.getCurrent(), env, body);
		}
	}

}