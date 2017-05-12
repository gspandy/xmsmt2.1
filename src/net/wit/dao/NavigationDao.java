/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.List;

import net.wit.entity.Navigation;
import net.wit.entity.Navigation.Position;

/**
 * Dao - 导航
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface NavigationDao extends BaseDao<Navigation, Long> {

	/**
	 * 查找导航
	 * 
	 * @param position
	 *            位置
	 * @return 导航
	 */
	List<Navigation> findList(Position position);

}