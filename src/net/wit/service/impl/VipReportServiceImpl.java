package net.wit.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.VipReportDao;
import net.wit.entity.Tenant;
import net.wit.entity.VipReport;
import net.wit.service.VipReportService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - Vip统计
 * @author Teddy
 * @version 1.0
 */
@Service("vipReportServiceImpl")
public class VipReportServiceImpl extends BaseServiceImpl<VipReport, Long>implements VipReportService {

	@Resource(name = "vipReportDaoImpl")
	private VipReportDao vipReportDao;

	@Resource(name = "vipReportDaoImpl")
	public void setBaseDao(VipReportDao vipReportDao) {
		super.setBaseDao(vipReportDao);
	}

	@Transactional(readOnly = true)
	public Page<VipReport> findVolumePageTenant(Tenant tenant, Date beginDate, Date endDate, Pageable pageable) {
		return vipReportDao.findVolumePageTenant(tenant, beginDate, endDate, pageable);
	}
	
	public Page<VipReport> findAmountPageTenant(Tenant tenant, Date beginDate, Date endDate, Pageable pageable) {
		return vipReportDao.findAmountPageTenant(tenant, beginDate, endDate, pageable);
	}

}