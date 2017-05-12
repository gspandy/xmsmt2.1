package net.wit.service;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.CompanyNotice;
import net.wit.entity.Tenant;

public interface CompanyNoticeService extends BaseService<CompanyNotice, Long>{
	
	Page<CompanyNotice> PageALL(Tenant tenant,Pageable pageable);

}
