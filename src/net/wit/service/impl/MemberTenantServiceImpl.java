/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.Principal;
import net.wit.dao.AdminDao;
import net.wit.dao.MemberDao;
import net.wit.dao.MemberTenantDao;
import net.wit.dao.impl.MemberTenantDaoImpl;
import net.wit.entity.Admin;
import net.wit.entity.Authority;
import net.wit.entity.Member;
import net.wit.entity.MemberTenant;
import net.wit.entity.Role;
import net.wit.entity.Tenant;
import net.wit.service.AdminService;
import net.wit.service.MemberService;
import net.wit.service.MemberTenantService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 管理员
 * @author rsico Team
 * @version 3.0
 */
@Service("memberTenantServiceImpl")
public class MemberTenantServiceImpl extends BaseServiceImpl<MemberTenant, Long>implements MemberTenantService {

	@Resource(name = "memberTanentImpl")
	private MemberTenantDao memberTenantDao;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	
	   @Autowired
	    private MemberDao memberDao;
	   
	@Resource(name = "memberTanentImpl")
	public void setBaseDao(MemberTenantDao memberTenantDao) {
		super.setBaseDao(memberTenantDao);
	}

	@Override
	public void addMemberTenant(Member member, Long tenantId) {
		if(!memberTenantDao.getExistByTenantAndMember(tenantId, member.getId())){
			MemberTenant memberTanent = new MemberTenant( member.getId(), tenantId, member.getMobile());
			memberTenantDao.addMemberTanent(memberTanent);
		}
	}

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int addMembers(List<Object[]> addMembers, String comId) {
        int cnt = 0;
        for (Object[] objects : addMembers) {
            Member member = new Member();
            member.setMobile((String)objects[0]);
            member.setName((String)objects[1]);
            member.setIdcarNo((String) objects[2]);
            member.setComId(new Long(comId));
            member.setEmail((String) objects[3]);
//            memberTenantDao.addMember(member);
            memberDao.persist(member);

            MemberTenant memberTanent = new MemberTenant();
            memberTanent.setMemberId(member.getId());
            memberTanent.setTenantId(new Long(comId));
            memberTanent.setRelativeDate(new Date());
            memberTanent.setPhone(member.getMobile());
//            memberTenantDao.addMemberTanent(memberTanent);
            memberTenantDao.persist(memberTanent);

            cnt ++;
        }

        return cnt;
    }
    
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public void importSubmit(Object[] objects,String comId){
    	Member member = memberDao.findByTel((String)objects[0]);
		if(member == null){
			member = new Member();
			member.setComId(new Long(comId));
	   		member.setMobile((String)objects[0]);
//	   		if(objects.length > 1){
//	   			  member.setName((String)objects[1]);
//	   		}
	   		if(objects.length > 1){
	   			 member.setIdcarNo((String) objects[1]);
	   		}
	   		if(objects.length > 2){
	   			 member.setEmail((String) objects[2]);
	   		}
    		memberDao.addMember(member);

		 }
		
        MemberTenant memberTanent = new MemberTenant();
        memberTanent.setMemberId(member.getId());
        memberTanent.setTenantId(new Long(comId));
        memberTanent.setRelativeDate(new Date());
        memberTanent.setPhone(member.getMobile());
        memberTenantDao.addMemberTanent(memberTanent);
	  }

	@Override
	public Page<Member> getMemberByTenant(Tenant tenant, Pageable pageable,Boolean isRegister) {
		List<MemberTenant> list=memberTenantDao.getMemberTenantByTenant(tenant.getId());
		List<Long> memberIdList=new ArrayList<Long>();
		for(MemberTenant memberTenant:list){
			memberIdList.add(memberTenant.getMemberId());
		}
		List<Member> memberList=new ArrayList<Member>();
		return memberService.findPage(memberIdList, pageable, isRegister);
	}
}