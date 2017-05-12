/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.List;

import net.wit.entity.Area;

/**
 * Dao - 地区
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface AreaDao extends BaseDao<Area, Long> {

	/**
	 * 查找顶级地区
	 * 
	 * @param count
	 *            数量
	 * @return 顶级地区
	 */
	List<Area> findRoots(Integer count);
	
	/**
	 * 查找次级地区
	 * 
	 * 
	 *           
	 * @return 次级地区
	 */
	List<Area> findLevel(Long id);

	Area findByCode(String code);

	Area findByTelCode(String code);

	Area findByZipCode(String zipCode);

	Area findByAreaName(String areaName);

	List<Area> findOpenList();

}