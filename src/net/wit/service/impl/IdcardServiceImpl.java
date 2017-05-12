/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.IdcardDao;
import net.wit.dao.MemberDao;
import net.wit.entity.Idcard;
import net.wit.entity.Member;
import net.wit.service.IdcardService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 实名认证
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("idcardServiceImpl")
public class IdcardServiceImpl extends BaseServiceImpl<Idcard, Long> implements IdcardService {

	@Resource(name = "memberDaoImpl")
	private MemberDao memberDao;

	@Resource(name = "idcardDaoImpl")
	public void setBaseDao(IdcardDao idcardDao) {
		super.setBaseDao(idcardDao);
	}


	public void update(Idcard idcard,Member member) {
		member.setIdcard(idcard);
		super.save(idcard);
		memberDao.merge(member);
	}

	@Override
	@Transactional
	public Idcard update(Idcard idcard) {
		return super.update(idcard);
	}
	
	public Page<Member> findMemberPage(Pageable pageable){
		return memberDao.findRealnameMemberPage(pageable);
	}
}

