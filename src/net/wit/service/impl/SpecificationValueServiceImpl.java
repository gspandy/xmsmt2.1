/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import javax.annotation.Resource;

import net.wit.dao.SpecificationValueDao;
import net.wit.entity.SpecificationValue;
import net.wit.service.SpecificationValueService;

import org.springframework.stereotype.Service;

/**
 * Service - 规格值
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("specificationValueServiceImpl")
public class SpecificationValueServiceImpl extends BaseServiceImpl<SpecificationValue, Long> implements SpecificationValueService {

	@Resource(name = "specificationValueDaoImpl")
	public void setBaseDao(SpecificationValueDao specificationValueDao) {
		super.setBaseDao(specificationValueDao);
	}

}