package net.wit.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.CompanyNoticeDao;
import net.wit.entity.CompanyNotice;
import net.wit.entity.Tenant;
import net.wit.service.CompanyNoticeService;

@Service("companyNoticeServiceImpl")
public class CompanyNoticeServiceImpl extends BaseServiceImpl<CompanyNotice, Long> implements CompanyNoticeService{

	
	@Resource(name = "companyNoticeDaoImpl")
	private CompanyNoticeDao companyNoticeDao;


	@Resource(name = "companyNoticeDaoImpl")
	public void setBaseDao(CompanyNoticeDao companyNoticeDao) {
		super.setBaseDao(companyNoticeDao);
	}

	@Override
	public Page<CompanyNotice> PageALL(Tenant tenant,Pageable pageable) {
		return companyNoticeDao.PageALL(tenant,pageable);
	}

}
