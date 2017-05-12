package net.wit.dao.impl;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Query;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.VipReportDao;
import net.wit.entity.Tenant;
import net.wit.entity.VipReport;
import net.wit.util.DateUtil;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

/**
 * Dao - Vip统计
 * @author Teddy
 * @version 1.0
 */
@Repository("vipReportDaoImpl")
public class VipReportDaoImpl extends BaseDaoImpl<VipReport, Long> implements VipReportDao {

	@SuppressWarnings("unchecked")
	public Page<VipReport> findVolumePageTenant(Tenant tenant, Date beginDate, Date endDate, Pageable pageable) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SUM(oi.quantity) volume, m.name vipName, m.id vipId");
		sb.append("  FROM xx_order o, t_ticket t, xx_member m, xx_order_item oi ,t_tenant_shopkeeper ts");
		sb.append(" WHERE o.payment_status > 0 ");		
		sb.append(" and o.ticket = t.id ");
		sb.append(" and t.shopkeeper_id = m.id ");
		sb.append(" and m.id = ts.member_id and ts.is_shopkeeper =1");
		sb.append(" and oi.orders = o.id ");
		if (tenant != null) {
			sb.append(" and o.tenant = ").append(tenant.getId());
		}
		if (beginDate != null && !"".equals(beginDate)) {
			String strBeginDate = DateUtil.changeDateToStr(beginDate, DateUtil.LINK_DISPLAY_DATE);
			sb.append(" and o.create_date >= '" + strBeginDate + "'");// 大于开始日期
		}
		if (endDate != null && !"".equals(endDate)) {
			String strEndDate = DateUtil.changeDateToStr(DateUtil.addDay(endDate, 1), DateUtil.LINK_DISPLAY_DATE);
			sb.append(" and o.create_date <= '" + strEndDate + "'");// 小于结束日期
		}
		sb.append(" group by m.id ");
		
		
		StringBuffer sbCount = new StringBuffer();
		sbCount.append("SELECT COUNT(*) cou from (");
		sbCount.append(sb.toString());
		sbCount.append(") c ");
		Query queryCount = entityManager.createNativeQuery(sbCount.toString());
		BigInteger cou = (BigInteger)queryCount.getResultList().get(0);
		
		sb.append(" order by volume desc ");
	
		Query query = entityManager.createNativeQuery(sb.toString());
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(VipReport.class));
		query.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize());
		query.setMaxResults(pageable.getPageSize());
		return new Page<VipReport>(query.getResultList(), new Long(cou.longValue()), pageable);
	}

	@SuppressWarnings("unchecked")
	public Page<VipReport> findAmountPageTenant(Tenant tenant, Date beginDate, Date endDate, Pageable pageable) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SUM(o.amount_paid) amount, m.name vipName, m.id vipId");
		sb.append("  FROM xx_order o, t_ticket t, xx_member m ,t_tenant_shopkeeper ts ");
		sb.append(" WHERE o.payment_status > 0 ");		
		sb.append(" and o.ticket = t.id ");
		sb.append(" and t.shopkeeper_id = m.id ");
		sb.append(" and m.id = ts.member_id and ts.is_shopkeeper =1");
		if (tenant != null) {
			sb.append(" and o.tenant = ").append(tenant.getId());
		}
		if (beginDate != null && !"".equals(beginDate)) {
			String strBeginDate = DateUtil.changeDateToStr(beginDate, DateUtil.LINK_DISPLAY_DATE);
			sb.append(" and o.create_date >= '" + strBeginDate + "'");// 大于开始日期
		}
		if (endDate != null && !"".equals(endDate)) {
			String strEndDate = DateUtil.changeDateToStr(DateUtil.addDay(endDate, 1), DateUtil.LINK_DISPLAY_DATE);
			sb.append(" and o.create_date <= '" + strEndDate + "'");// 小于结束日期
		}
		sb.append(" group by m.id ");
		
		
		StringBuffer sbCount = new StringBuffer();
		sbCount.append("SELECT COUNT(*) cou from (");
		sbCount.append(sb.toString());
		sbCount.append(") c ");
		Query queryCount = entityManager.createNativeQuery(sbCount.toString());
		BigInteger cou = (BigInteger)queryCount.getResultList().get(0);
		
		sb.append(" order by amount desc ");
	
		Query query = entityManager.createNativeQuery(sb.toString());
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(VipReport.class));
		query.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize());
		query.setMaxResults(pageable.getPageSize());
		return new Page<VipReport>(query.getResultList(), new Long(cou.longValue()), pageable);
		
	}

}