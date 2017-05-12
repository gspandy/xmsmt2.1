/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import javax.annotation.Resource;

import net.wit.dao.AttributeDao;
import net.wit.entity.Attribute;
import net.wit.service.AttributeService;

import org.springframework.stereotype.Service;

/**
 * Service - 属性
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("attributeServiceImpl")
public class AttributeServiceImpl extends BaseServiceImpl<Attribute, Long> implements AttributeService {

	@Resource(name = "attributeDaoImpl")
	public void setBaseDao(AttributeDao attributeDao) {
		super.setBaseDao(attributeDao);
	}

}