/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.Principal;
import net.wit.dao.AdminDao;
import net.wit.dao.RoleDao;
import net.wit.dao.TenantDao;
import net.wit.entity.Admin;
import net.wit.entity.Authority;
import net.wit.entity.Role;
import net.wit.entity.Tenant;
import net.wit.exception.TenantException;
import net.wit.service.AdminService;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 管理员
 * @author rsico Team
 * @version 3.0
 */
@Service("adminServiceImpl")
public class AdminServiceImpl extends BaseServiceImpl<Admin, Long>implements AdminService {

	@Resource(name = "adminDaoImpl")
	private AdminDao adminDao;

	@Resource(name = "roleDaoImpl")
	private RoleDao roleDao;

	@Resource(name = "tenantDaoImpl")
	private TenantDao tenantDao;

	@Resource(name = "adminDaoImpl")
	public void setBaseDao(AdminDao adminDao) {
		super.setBaseDao(adminDao);
	}

	@Transactional(readOnly = true)
	public boolean usernameExists(String username) {
		return adminDao.usernameExists(username);
	}

	@Transactional(readOnly = true)
	public Admin findByUsername(String username) {
		return adminDao.findByUsername(username);
	}

	@Transactional(readOnly = true)
	public List<String> findAuthorities(Long id) {
		List<String> authorities = new ArrayList<String>();
		Admin admin = adminDao.find(id);
		if (admin != null) {
			for (Role role : admin.getRoles()) {
				for (Authority at : role.getAuthorities()) {
					authorities.add(at.getAuthority());
				}
			}
		}
		return authorities;
	}

	@Transactional(readOnly = true)
	public boolean isAuthenticated() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			return subject.isAuthenticated();
		}
		return false;
	}

	@Transactional(readOnly = true)
	public Admin getCurrent() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			Principal principal = (Principal) subject.getPrincipal();
			if (principal != null) {
				return adminDao.find(principal.getId());
			}
		}
		return null;
	}

	@Transactional(readOnly = true)
	public String getCurrentUsername() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			Principal principal = (Principal) subject.getPrincipal();
			if (principal != null) {
				return principal.getUsername();
			}
		}
		return null;
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public void save(Admin admin) {
		super.save(admin);
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public Admin update(Admin admin) {
		return super.update(admin);
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public Admin update(Admin admin, String... ignoreProperties) {
		return super.update(admin, ignoreProperties);
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
	public void delete(Admin admin) {
		super.delete(admin);
	}

	@Transactional(readOnly = true)
	public Page<Admin> findPage(Tenant tenant, Pageable pageable) {
		return adminDao.findPage(tenant, pageable);
	}

	@Transactional
	public void initSuperAdmin(Tenant tenant, String username, String password) throws TenantException {
		if (tenant == null)
			throw new TenantException("商家id为空");
		
		Admin defaultAdmin=adminDao.findByUsername("default");
		List<Authority> authorities = new ArrayList<Authority>();
		if (defaultAdmin != null) {
			for (Role defaultRole : defaultAdmin.getRoles()) {
				for(Authority authority:defaultRole.getAuthorities()){
					Authority aut= new Authority();
					aut.setId(authority.getId());
					authorities.add(aut);
				}
				
			}
		}
		Role role = new Role();
		role.setName(tenant.getName() + "超级管理员");
		role.setIsSuper(true);
		role.setTenant(tenant);
		role.setAuthorities(authorities);
		role.setDescription(tenant.getName() + "超级管理员");
		roleDao.persist(role);
		
		
		

		Admin admin = new Admin();
		admin.setDepartment(tenant.getName());
		admin.setUsername(username);
		admin.setName(tenant.getName() + "超级管理员");
		admin.setPassword(DigestUtils.md5Hex(password));
		admin.setEmail(username + "@163.com");
		admin.setIsEnabled(true);
		admin.setIsLocked(false);
		admin.setLoginFailureCount(0);
		admin.getRoles().add(role);
		admin.setTenant(tenant);
		adminDao.persist(admin);
	}

	@Override
	public String  settingCashPwd(Admin admin, String cashPwd)
			throws TenantException {
		
	    if(admin.getPassword().equals(DigestUtils.md5Hex(cashPwd)))
	    {
	    	return "2";//登录密码与支付密码一直请重新输入
	    }else{
	    	admin.setCashPwd(DigestUtils.md5Hex(cashPwd));
	    	this.update(admin);
	    	return "1";//支付密码设置成功
	    }
	
		
	}

	@Override
	public String  verifyCashPwd(String cashPwd) throws TenantException {
		String verifyCashPwd=DigestUtils.md5Hex(cashPwd);
		if(verifyCashPwd.equals(this.getCurrent().getCashPwd())){
			return "1";//支付密码输入正确
		}
		return "0";//支付密码输入错误
	}

}