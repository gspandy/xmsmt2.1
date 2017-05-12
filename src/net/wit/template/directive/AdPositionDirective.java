/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.template.directive;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.wit.entity.AdPosition;
import net.wit.entity.Tenant;
import net.wit.service.AdPositionService;
import net.wit.service.TenantService;
import net.wit.util.FreemarkerUtils;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.core.Environment;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 广告位
 * 
 * @author rsico Team
 * @version 3.0
 */
@Component("adPositionDirective")
public class AdPositionDirective extends BaseDirective {
	
	/** "商家"参数名称 */
	private static final String TENANT_ID_PARAMETER_NAME = "tenantId";
	/** 变量名称 */
	private static final String VARIABLE_NAME = "adPosition";
	
	

	@Resource(name = "freeMarkerConfigurer")
	private FreeMarkerConfigurer freeMarkerConfigurer;
	@Resource(name = "adPositionServiceImpl")
	private AdPositionService adPositionService;
	@Resource(name="tenantServiceImpl")
	private TenantService tenantService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		AdPosition adPosition;
		Long id = getId(params);
		Long tenantId = FreemarkerUtils.getParameter(TENANT_ID_PARAMETER_NAME, Long.class, params);
		Tenant tenant = tenantService.find(tenantId);
		boolean useCache = useCache(env, params);
		String cacheRegion = getCacheRegion(env, params);
		Integer count = getCount(params);
		if (useCache) {
			adPosition = adPositionService.find(id,tenant,count, cacheRegion);
		} else {
			adPosition = adPositionService.find(id,tenant,count);
		}
		if (body != null) {
			setLocalVariable(VARIABLE_NAME, adPosition, env, body);
		} else {
			if (adPosition != null && adPosition.getTemplate() != null) {
				try {
					Map<String, Object> model = new HashMap<String, Object>();
					model.put(VARIABLE_NAME, adPosition);
					Writer out = env.getOut();
					new Template("adTemplate", new StringReader(adPosition.getTemplate()), freeMarkerConfigurer.getConfiguration()).process(model, out);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}