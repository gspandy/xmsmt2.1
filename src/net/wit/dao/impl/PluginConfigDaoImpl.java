/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import net.wit.dao.PluginConfigDao;
import net.wit.entity.PluginConfig;

import org.springframework.stereotype.Repository;

/**
 * Dao - 插件配置
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("pluginConfigDaoImpl")
public class PluginConfigDaoImpl extends BaseDaoImpl<PluginConfig, Long> implements PluginConfigDao {

	public boolean pluginIdExists(String pluginId) {
		if (pluginId == null) {
			return false;
		}
		String jpql = "select count(*) from PluginConfig pluginConfig where pluginConfig.pluginId = :pluginId";
		Long count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT).setParameter("pluginId", pluginId).getSingleResult();
		return count > 0;
	}

	public PluginConfig findByPluginId(String pluginId) {
		if (pluginId == null) {
			return null;
		}
		try {
			String jpql = "select pluginConfig from PluginConfig pluginConfig where pluginConfig.pluginId = :pluginId";
			return entityManager.createQuery(jpql, PluginConfig.class).setFlushMode(FlushModeType.COMMIT).setParameter("pluginId", pluginId).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}