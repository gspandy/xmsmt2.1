/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: VersionUpdateService.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月11日
 */
package net.wit.service;

import java.util.Date;
import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.VersionUpdate;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月11日
 */
public interface VersionUpdateService extends BaseService<VersionUpdate, Long>{
	/**
	 *  返回版本跟新信息
	 */
	VersionUpdate versionUpdate(String versionType);
	/**
	 *根据时间查询客户端版本
	 */
	Page<VersionUpdate> timeSearch(Date startTime,Date endTime, Pageable pageable);

}
