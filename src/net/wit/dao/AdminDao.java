/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.Tenant;

/**
 * Dao - 管理员
 * @author rsico Team
 * @version 3.0
 */
public interface AdminDao extends BaseDao<Admin, Long> {

	/**
	 * 判断用户名是否存在
	 * @param username 用户名(忽略大小写)
	 * @return 用户名是否存在
	 */
	boolean usernameExists(String username);

	/**
	 * 根据用户名查找管理员
	 * @param username 用户名(忽略大小写)
	 * @return 管理员，若不存在则返回null
	 */
	Admin findByUsername(String username);

	/**
	 * @Title：findPage @Description：
	 * @param isSuper
	 * @param tenant
	 * @param pageable
	 * @return Page<Role>
	 */
	Page<Admin> findPage(Tenant tenant, Pageable pageable);

}