/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: VersionUpdateServiceImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月11日
 */
package net.wit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.VersionUpdateDao;
import net.wit.entity.VersionUpdate;
import net.wit.service.VersionUpdateService;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月11日
 */
@Service("versionUpdateServiceImpl")
public class VersionUpdateServiceImpl extends BaseServiceImpl<VersionUpdate, Long> implements VersionUpdateService{
	
	@Resource(name = "versionUpdateDaoImpl")
	private VersionUpdateDao versionUpdateDao;
	
	@Resource(name = "versionUpdateDaoImpl")
	public void setBaseDao(VersionUpdateDao versionUpdateDao) {
		super.setBaseDao(versionUpdateDao);;
	}
	@Override
	public Page<VersionUpdate> timeSearch(Date startTime, Date endTime,
			Pageable pageable) {
		
		return this.versionUpdateDao.timeSearch(startTime, endTime, pageable);
	}
	@Override
	public VersionUpdate versionUpdate(String versionType) {
		List<VersionUpdate> list=versionUpdateDao.versionUpdate(versionType);
		if(list.size()!=0){
			return list.get(0);
		}
		return null;
	}
}
