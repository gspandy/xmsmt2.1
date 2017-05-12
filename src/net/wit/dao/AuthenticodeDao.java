/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Authenticode;
import net.wit.entity.Authenticode.Status;
import net.wit.entity.Member;
import net.wit.entity.Tenant;

/**
 * Dao - 验证码
 * @author rsico Team
 * @version 3.0
 */
public interface AuthenticodeDao extends BaseDao<Authenticode, Long> {

	/**
	 * 验证码查找
	 * @param sn- 验证码
	 * @return
	 */
	Authenticode findBySn(String sn);

	/**
	 * 通过店铺查找
	 * @param tenant
	 * @return
	 */

	Authenticode findByTenant(Tenant tenant);

	/**
	 * 分页
	 * @param tenant
	 * @param pageable
	 * @return
	 */
	Page<Authenticode> findPage(Tenant tenant, Pageable pageable);

	Page<Authenticode> findPage(Member member, List<Status> status, Pageable pageable);

	/**
	 * 通过店铺查找
	 * @param tenant
	 * @return
	 */
	List<Authenticode> findByMember(Member member, Tenant tenant, List<Status> status);
	List<Authenticode> findByMember(Member member);


}