/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import java.util.List;

import net.wit.entity.Area;

/**
 * Service - 地区
 * @author rsico Team
 * @version 3.0
 */
public interface AreaService extends BaseService<Area, Long> {

	/**
	 * 查找顶级地区
	 * @return 顶级地区
	 */
	List<Area> findRoots();

	/**
	 * 查找顶级地区
	 * @param count 数量
	 * @return 顶级地区
	 */
	List<Area> findRoots(Integer count);

	Area findByCode(String code);

	Area findByTelCode(String code);

	Area findByZipCode(String zipCode);

	/**
	 * 获取当前登录会员
	 * @return 当前登录会员，若不存在则返回null
	 */
	Area getCurrent();

	Area findByAreaName(String areaName);

	List<Area> findOpenList();
}