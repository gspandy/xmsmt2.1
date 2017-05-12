package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.dao.AdDao;
import net.wit.dao.ApplicationDao;
import net.wit.entity.Ad;
import net.wit.entity.Application;
import net.wit.entity.Member;
import net.wit.service.ApplicationService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 应用
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("applicationServiceImpl")
public class ApplicationServiceImpl extends BaseServiceImpl<Application, Long> implements ApplicationService {
	
	@Resource(name = "applicationDaoImpl")
	private ApplicationDao applicationDao;
	
	public Application findByCode(Member member,String code){
		return applicationDao.findByCode(member, code);
	}
	
	public List<Application> findByMember(Member member){
		return applicationDao.findByMember(member);
	}

	@Resource(name = "applicationDaoImpl")
	public void setBaseDao(ApplicationDao applicationDao) {
		super.setBaseDao(applicationDao);
	}
	
	@Override
	@Transactional
	public void save(Application application) {
		super.save(application);
	}


	@Override
	@Transactional
	public Application update(Application application) {
		return super.update(application);
	}
}