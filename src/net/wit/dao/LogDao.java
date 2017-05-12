/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import net.wit.entity.Log;

/**
 * Dao - 日志
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface LogDao extends BaseDao<Log, Long> {

	/**
	 * 删除所有日志
	 */
	void removeAll();

}