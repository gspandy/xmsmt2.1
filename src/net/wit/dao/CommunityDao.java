/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.List;

import net.wit.entity.Area;
import net.wit.entity.Community;
import net.wit.entity.Tag;

/**
 * Dao - 社区
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface CommunityDao extends BaseDao<Community, Long> {
	
	List<Community> findList(Area area);
	Community findbyCode(String code);

	List<Community> findHot(Area area, List<Tag> tags);
}