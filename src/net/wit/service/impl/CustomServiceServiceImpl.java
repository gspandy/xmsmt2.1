package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.CreditDao;
import net.wit.dao.CustomServiceDao;
import net.wit.entity.Admin;
import net.wit.entity.CustomService;
import net.wit.entity.Tenant;
import net.wit.service.CustomServiceService;
import net.wit.service.MemberService;

import org.springframework.stereotype.Service;

@Service("customServiceServiceImpl")
public class CustomServiceServiceImpl extends BaseServiceImpl<CustomService, Long> implements CustomServiceService{

	@Resource(name = "customServiceDaoImpl")
	private CustomServiceDao customServiceDao;


	@Resource(name = "customServiceDaoImpl")
	public void setBaseDao(CustomServiceDao customServiceDao) {
		super.setBaseDao(customServiceDao);
	}


	@Override
	public List<CustomService> findALL(long tenantId) {
		 return customServiceDao.findALL(tenantId);
	}


	@Override
	public CustomService findCustomServiceByAdmin(Admin admin) {
		return customServiceDao.getCustomServiceByAdmin(admin);
	}


	@Override
	public Page<CustomService> findPage(Tenant tenant, Pageable pageable) {
		return customServiceDao.findPage(tenant,pageable);
	}
}
