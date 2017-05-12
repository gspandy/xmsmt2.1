/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: VersionUpdateDao.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月11日
 */
package net.wit.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Area;
import net.wit.entity.Community;
import net.wit.entity.Location;
import net.wit.entity.Tag;
import net.wit.entity.TenantCategory;
import net.wit.entity.VersionUpdate;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月11日
 */
public interface VersionUpdateDao extends BaseDao<VersionUpdate, Long>{
	
	/**
	 * 根据时间查找客户端版本信息
	 *
	 */
	Page<VersionUpdate> timeSearch(Date startTime, Date endTime,  Pageable pageable);
	
	/**
	 *返回版本跟新
	 *
	 */
	 List<VersionUpdate> versionUpdate(String versionType);
}
