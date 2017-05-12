/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import javax.annotation.Resource;

import net.wit.dao.ParameterDao;
import net.wit.entity.Parameter;
import net.wit.service.ParameterService;

import org.springframework.stereotype.Service;

/**
 * Service - 参数
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("parameterServiceImpl")
public class ParameterServiceImpl extends BaseServiceImpl<Parameter, Long> implements ParameterService {

	@Resource(name = "parameterDaoImpl")
	public void setBaseDao(ParameterDao parameterDao) {
		super.setBaseDao(parameterDao);
	}

}