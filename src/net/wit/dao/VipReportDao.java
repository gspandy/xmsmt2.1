package net.wit.dao;

import java.util.Date;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Tenant;
import net.wit.entity.VipReport;

/**
 * Dao - vip统计
 * @author Teddy
 * @version 1.0
 */
public interface VipReportDao extends BaseDao<VipReport, Long> {

	/**
	 * 分页查找企业vip销售量
	 * @param tenant 企业
	 * @param beginDate 开始时间
	 * @param endDate 结束时间
	 * @param pageable 分页信息
	 * @return vip销售量分页
	 */
	public Page<VipReport> findVolumePageTenant(Tenant tenant, Date beginDate, Date endDate, Pageable pageable);

	/**
	 * 分页查找企业vip销售额
	 * @param tenant 企业
	 * @param beginDate 开始时间
	 * @param endDate 结束时间
	 * @param pageable 分页信息
	 * @return VIP销售额分页
	 */
	public Page<VipReport> findAmountPageTenant(Tenant tenant, Date beginDate, Date endDate, Pageable pageable);
	
}