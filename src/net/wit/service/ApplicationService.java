/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import java.util.List;

import net.wit.entity.Application;
import net.wit.entity.Member;

/**
 * Service - 应用
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface ApplicationService extends BaseService<Application, Long> {

	Application findByCode(Member member,String code);
	/**
	 * 根据用户查找应用
	 * @param member
	 * @return
	 */
	List<Application> findByMember(Member member);
}