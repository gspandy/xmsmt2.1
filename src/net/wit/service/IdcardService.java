/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Idcard;
import net.wit.entity.Member;

/**
 * Service - 实名认证
 * @author rsico Team
 * @version 3.0
 */
public interface IdcardService extends BaseService<Idcard, Long> {

	void update(Idcard idcard, Member member);

	Page<Member> findMemberPage(Pageable pageable);

}