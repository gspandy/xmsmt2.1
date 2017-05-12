/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.dao.CommunityDao;
import net.wit.entity.Area;
import net.wit.entity.Community;
import net.wit.entity.Tag;
import net.wit.service.CommunityService;

import org.springframework.stereotype.Service;

/**
 * Service - 实名认证
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("communityServiceImpl")
public class CommunityServiceImpl extends BaseServiceImpl<Community, Long> implements CommunityService {

	@Resource(name = "communityDaoImpl")
	private CommunityDao communityDao;
	
	@Resource(name = "communityDaoImpl")
	public void setBaseDao(CommunityDao communityDao) {
		super.setBaseDao(communityDao);
	}

	public List<Community> findList(Area area) {
		return communityDao.findList(area);
	}
	public Community findbyCode(String code) {
		return communityDao.findbyCode(code);
	}

	public List<Community> findHot(Area area, List<Tag> tags) {
		return communityDao.findHot(area,tags);
	}
}

