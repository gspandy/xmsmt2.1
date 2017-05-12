/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import javax.persistence.FlushModeType;

import net.wit.dao.LogDao;
import net.wit.entity.Log;

import org.springframework.stereotype.Repository;

/**
 * Dao - 日志
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("logDaoImpl")
public class LogDaoImpl extends BaseDaoImpl<Log, Long> implements LogDao {

	public void removeAll() {
		String jpql = "delete from Log log";
		entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).executeUpdate();
	}

}