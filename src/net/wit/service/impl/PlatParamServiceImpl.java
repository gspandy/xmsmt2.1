/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: PlatParamServicImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月10日
 */
package net.wit.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.wit.dao.PicDao;
import net.wit.dao.PlatParamDao;
import net.wit.entity.Pic;
import net.wit.entity.PlatParam;
import net.wit.service.PlatParamService;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月10日
 */
@Service("platParamServiceImpl")
public class PlatParamServiceImpl extends BaseServiceImpl<PlatParam, Long> implements PlatParamService{
	@Resource(name = "platParamDaoImpl")
	public void setBaseDao(PlatParamDao platParamDao) {
		super.setBaseDao(platParamDao);
	}
}
