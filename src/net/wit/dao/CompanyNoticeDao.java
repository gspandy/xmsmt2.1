package net.wit.dao;

import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.CompanyNotice;
import net.wit.entity.Tenant;

public interface CompanyNoticeDao extends BaseDao<CompanyNotice, Long>{
	
	Page<CompanyNotice> PageALL(Tenant tenant,Pageable pageable);

}
