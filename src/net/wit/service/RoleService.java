/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Role;
import net.wit.entity.Tenant;

/**
 * Service - 角色
 * @author rsico Team
 * @version 3.0
 */
public interface RoleService extends BaseService<Role, Long> {

	/**
	 * @Title：findPage @Description：
	 * @param tenant
	 * @param pageable
	 * @return Object
	 */
	Page<Role> findPage(Tenant tenant, Pageable pageable);

	/**
	 * @Title：findList
	 * @Description：
	 * @param tenant
	 * @return  Object
	 */
	List<Role> findList(Tenant tenant);
	
	Role findCustomService(Tenant tenant,String name);

}