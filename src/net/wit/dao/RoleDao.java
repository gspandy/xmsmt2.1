/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Role;
import net.wit.entity.Tenant;

/**
 * Dao - 角色
 * @author rsico Team
 * @version 3.0
 */
public interface RoleDao extends BaseDao<Role, Long> {

	/**
	 * @Title：findPage @Description：
	 * @param tenant
	 * @param pageable
	 * @return Page<Role>
	 */
	Page<Role> findPage(Tenant tenant, Pageable pageable);

	/**
	 * @Title：findList @Description：
	 * @param tenant
	 * @return List<Role>
	 */
	List<Role> findList(Tenant tenant);
	
	Role findCustomService(Tenant tenant, String name);

}