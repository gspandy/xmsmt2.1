/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.dao.MemberAttributeDao;
import net.wit.entity.MemberAttribute;
import net.wit.service.MemberAttributeService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 会员注册项
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("memberAttributeServiceImpl")
public class MemberAttributeServiceImpl extends BaseServiceImpl<MemberAttribute, Long> implements MemberAttributeService {

	@Resource(name = "memberAttributeDaoImpl")
	private MemberAttributeDao memberAttributeDao;

	@Resource(name = "memberAttributeDaoImpl")
	public void setBaseDao(MemberAttributeDao memberAttributeDao) {
		super.setBaseDao(memberAttributeDao);
	}

	@Transactional(readOnly = true)
	public Integer findUnusedPropertyIndex() {
		return memberAttributeDao.findUnusedPropertyIndex();
	}

	@Transactional(readOnly = true)
	public List<MemberAttribute> findList() {
		return memberAttributeDao.findList();
	}

	@Transactional(readOnly = true)
	@Cacheable("memberAttribute")
	public List<MemberAttribute> findList(String cacheRegion) {
		return memberAttributeDao.findList();
	}

	@Override
	@Transactional
	@CacheEvict(value = "memberAttribute", allEntries = true)
	public void save(MemberAttribute memberAttribute) {
		super.save(memberAttribute);
	}

	@Override
	@Transactional
	@CacheEvict(value = "memberAttribute", allEntries = true)
	public MemberAttribute update(MemberAttribute memberAttribute) {
		return super.update(memberAttribute);
	}

	@Override
	@Transactional
	@CacheEvict(value = "memberAttribute", allEntries = true)
	public MemberAttribute update(MemberAttribute memberAttribute, String... ignoreProperties) {
		return super.update(memberAttribute, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = "memberAttribute", allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = "memberAttribute", allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = "memberAttribute", allEntries = true)
	public void delete(MemberAttribute memberAttribute) {
		super.delete(memberAttribute);
	}

}