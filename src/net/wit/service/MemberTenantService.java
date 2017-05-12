/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Member;
import net.wit.entity.MemberTenant;
import net.wit.entity.Tenant;

import java.util.List;


/**
 * Service - 管理员
 * @author rsico Team
 * @version 3.0
 */
public interface MemberTenantService extends BaseService<MemberTenant, Long> {

	/**
	 * 生成会员企业关系关系
	 */
	public void addMemberTenant(Member member, Long tenantId);
	 
	public 	Page<Member> getMemberByTenant(Tenant tenant, Pageable pageable,Boolean isRegister);

    public int addMembers(List<Object[]> addMembers, String comId);
    
    public void importSubmit(Object[] objects,String comId);
}