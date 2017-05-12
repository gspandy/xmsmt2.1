package net.wit.service;

import java.util.Date;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Tenant;
import net.wit.entity.VipReport;

/**
 * Service - Vip统计报表
 * @author Teddy
 * @version 1.0
 */
public interface VipReportService extends BaseService<VipReport, Long> {

	/**
	 * 分页统计企业vip销量
	 * @param tenant 企业
	 * @param beginDate 开始日期
	 * @param endDate 结束日期
	 * @param pageable 分页信息
	 * @return vip销量分页
	 */
	public Page<VipReport> findVolumePageTenant(Tenant tenant, Date beginDate, Date endDate, Pageable pageable);
	
	/**
	 * 分页统计企业vip销售额
	 * @param tenant 企业
	 * @param beginDate 开始日期
	 * @param endDate 结束日期
	 * @param pageable 分页信息
	 * @return vip销售额分页
	 */
	public Page<VipReport> findAmountPageTenant(Tenant tenant, Date beginDate, Date endDate, Pageable pageable);
	
}