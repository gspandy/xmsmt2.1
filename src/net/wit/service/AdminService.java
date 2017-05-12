/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import java.util.List;

import jodd.jtx.meta.Transaction;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.Tenant;
import net.wit.exception.TenantException;

/**
 * Service - 管理员
 * @author rsico Team
 * @version 3.0
 */
public interface AdminService extends BaseService<Admin, Long> {

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
	 * 根据ID查找权限
	 * @param id ID
	 * @return 权限,若不存在则返回null
	 */
	List<String> findAuthorities(Long id);

	/**
	 * 判断管理员是否登录
	 * @return 管理员是否登录
	 */
	boolean isAuthenticated();

	/**
	 * 获取当前登录管理员
	 * @return 当前登录管理员,若不存在则返回null
	 */
	Admin getCurrent();

	/**
	 * 获取当前登录用户名
	 * @return 当前登录用户名,若不存在则返回null
	 */
	String getCurrentUsername();

	/**
	 * @Title：findPage @Description：
	 * @param isSuper
	 * @param tenant
	 * @param pageable
	 * @return Object
	 */
	Page<Admin> findPage(Tenant tenant, Pageable pageable);

	void initSuperAdmin(Tenant tenant, String username, String password) throws TenantException;
	/**
	 * 设置支付密码
	 * @param admin
	 * @param cashPwd
	 * @throws TenantException
	 */
	String  settingCashPwd(Admin admin, String cashPwd) throws TenantException;
	/**
	 * 验证密码是否正确
	 * @param admin
	 * @param cashPwd
	 * @throws TenantException
	 */
	String  verifyCashPwd(String cashPwd) throws TenantException;

}