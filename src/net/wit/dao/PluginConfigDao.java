/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import net.wit.entity.PluginConfig;

/**
 * Dao - 插件配置
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface PluginConfigDao extends BaseDao<PluginConfig, Long> {

	/**
	 * 判断插件ID是否存在
	 * 
	 * @param pluginId
	 *            插件ID
	 * @return 插件ID是否存在
	 */
	boolean pluginIdExists(String pluginId);

	/**
	 * 根据插件ID查找插件配置
	 * 
	 * @param pluginId
	 *            插件ID
	 * @return 插件配置，若不存在则返回null
	 */
	PluginConfig findByPluginId(String pluginId);

}