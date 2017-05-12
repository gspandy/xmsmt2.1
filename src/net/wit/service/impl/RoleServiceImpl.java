/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.RoleDao;
import net.wit.entity.Role;
import net.wit.entity.Tenant;
import net.wit.service.RoleService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 角色
 * @author rsico Team
 * @version 3.0
 */
@Service("roleServiceImpl")
public class RoleServiceImpl extends BaseServiceImpl<Role, Long>implements RoleService {

	@Resource(name = "roleDaoImpl")
	private RoleDao roleDao;

	@Resource(name = "roleDaoImpl")
	public void setBaseDao(RoleDao roleDao) {
		super.setBaseDao(roleDao);
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public void save(Role role) {
		super.save(role);
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public Role update(Role role) {
		return super.update(role);
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public Role update(Role role, String... ignoreProperties) {
		return super.update(role, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public void delete(Role role) {
		super.delete(role);
	}

	@Transactional(readOnly = true)
	public Page<Role> findPage(Tenant tenant, Pageable pageable) {
		return roleDao.findPage(tenant, pageable);
	}

	@Transactional(readOnly = true)
	public List<Role> findList(Tenant tenant) {
		return roleDao.findList(tenant);
	}

	@Override
	public Role findCustomService(Tenant tenant, String name) {
		// TODO Auto-generated method stub
		return roleDao.findCustomService(tenant,name);
	}

}