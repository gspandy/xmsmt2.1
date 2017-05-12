/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSetDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.wit.vo.OrderSettlementAdapter;
import net.wit.vo.OrderSettlementVO;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.OrderSettlementDao;
import net.wit.entity.Charge;
import net.wit.entity.Member;
import net.wit.entity.OrderSettlement;
import net.wit.entity.OrderSettlement.SettlementStatus;
import net.wit.entity.Owner;
import net.wit.entity.TenantShopkeeper;
import net.wit.entity.Ticket;
import net.wit.util.DateUtil;
import net.wit.vo.ShareOrderSettlementVO;

/**
 * @author: yangyang.wu
 * @version Revision: 0.0.1 @Date：2015年9月7日
 */
@Repository("orderSettlementDaoImpl")
public class OrderSettlementDaoImpl extends BaseDaoImpl<OrderSettlement, Long>implements OrderSettlementDao {

	public List<OrderSettlement> getSettlementDone() {
		String jpql = "select os from OrderSettlement os where os.status = :status";
		try {
			return entityManager.createQuery(jpql, OrderSettlement.class).setFlushMode(FlushModeType.COMMIT).setParameter("status", OrderSettlement.SettlementStatus.complete).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<OrderSettlement> getSettlementByParas(OrderSettlement orderSettlement) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OrderSettlement> criteriaQuery = criteriaBuilder.createQuery(OrderSettlement.class);
		Root<OrderSettlement> root = criteriaQuery.from(OrderSettlement.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();

		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), orderSettlement.getStatus()));
		if (orderSettlement.getMember() != null && orderSettlement.getMember().getId() != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), orderSettlement.getMember()));
		}
		if (orderSettlement.getOrder() != null && orderSettlement.getOrder().getId() != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("order"), orderSettlement.getOrder()));
		}
		if (orderSettlement.getOwner() != null && orderSettlement.getOwner().getId() != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("owner"), orderSettlement.getOwner()));
		}
		criteriaQuery.where(restrictions);
		List<OrderSettlement> list = super.findList(criteriaQuery);
		return list;
	}

	public BigDecimal getSettlementCharge(Member owner) {
		BigDecimal charge = null;
		String jpql = "select sum(os.settleCharge) from OrderSettlement os where os.owner =:owner and os.status = :status";
		try {
			charge = entityManager.createQuery(jpql, BigDecimal.class).setFlushMode(FlushModeType.COMMIT).setParameter("owner", owner).setParameter("status", OrderSettlement.SettlementStatus.complete).getSingleResult();
		} catch (NoResultException e) {

		}
		return charge;
	}

	@Override
	public Page<OrderSettlement> getOrderSettlementStream(Member owner, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OrderSettlement> criteriaQuery = criteriaBuilder.createQuery(OrderSettlement.class);
		Root<OrderSettlement> root = criteriaQuery.from(OrderSettlement.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.or(criteriaBuilder.equal(root.get("status"), OrderSettlement.SettlementStatus.complete), criteriaBuilder.equal(root.get("status"), OrderSettlement.SettlementStatus.settlement),criteriaBuilder.equal(root.get("status"), OrderSettlement.SettlementStatus.recevied));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("owner"), owner));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
	@Override
	public void batchUpateOrderSettlementRecevied(List<Charge> list)throws Exception{
		StringBuffer sb = new StringBuffer();
		StringBuffer inSblist = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if(i == list.size()-1){
				inSblist.append(list.get(i).getId());
			}else{
				inSblist.append(list.get(i).getId()+",");
			}
			
		}
		sb.append(" update t_order_settlement o set ");
		sb.append(" o.status = '").append(OrderSettlement.SettlementStatus.recevied.ordinal()).append("'");
		sb.append(" where o.charge_id in( ").append(inSblist.toString()+")");
		Query query = entityManager.createNativeQuery(sb.toString());
		query.executeUpdate();
	}
	

	@Override
	public BigDecimal getOwnerSettleCharge(Member owner) {
		BigDecimal charge = null;
		String jpql = "select sum(os.settleCharge) from OrderSettlement os where os.owner =:owner and (os.status = :status or os.status =:status1) and os.invalid=:invalid";
		try {
			charge = entityManager.createQuery(jpql, BigDecimal.class).setFlushMode(FlushModeType.COMMIT).setParameter("owner", owner).setParameter("status1", OrderSettlement.SettlementStatus.complete)
					.setParameter("status", OrderSettlement.SettlementStatus.uncomplete).setParameter("invalid", false).getSingleResult();
		} catch (NoResultException e) {

		}
		return charge;
	}

	@Override
	public Page<OrderSettlement> getOrderSettlementUncomplete(Member owner, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OrderSettlement> criteriaQuery = criteriaBuilder.createQuery(OrderSettlement.class);
		Root<OrderSettlement> root = criteriaQuery.from(OrderSettlement.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("owner"), owner));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), OrderSettlement.SettlementStatus.uncomplete));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<OrderSettlement> findByChargeId(Long chargeId, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OrderSettlement> criteriaQuery = criteriaBuilder.createQuery(OrderSettlement.class);
		Root<OrderSettlement> root = criteriaQuery.from(OrderSettlement.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("chargeId"), chargeId));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	@Override
	public boolean setOrderSettleInvalid(Member member) {
		if (member == null) {
			return false;
		}
		String jpql = "update OrderSettlement os set os.invalid = :invalid where os.owner =:owner and  os.status <=:status1";
		try {
			entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).setParameter("owner", member).setParameter("invalid", true).setParameter("status1", OrderSettlement.SettlementStatus.complete).executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public BigDecimal getPendingorderSettleAmt(Member member) {
		if (member == null) {
			return BigDecimal.ZERO;
		}

		String jpql = "select sum(os.orderSettleAmt)  from OrderSettlement os, TenantShopkeeper  ts where os.invalid = :invalid and ts.isShopkeeper=:isShopkeeper and   os.owner =ts.member  and  os.status <=:status1 and ts.recommendMember=:member";
		return entityManager.createQuery(jpql, BigDecimal.class).setFlushMode(FlushModeType.COMMIT).setParameter("member", member).setParameter("invalid", false).setParameter("status1", OrderSettlement.SettlementStatus.complete)
				.setParameter("isShopkeeper", TenantShopkeeper.IsShopkeeper.yes).getSingleResult();
	}

	@Override
	public List<OrderSettlement> getPendingOrderSettlements(Member member) {
		if (member == null) {
			return null;
		}
		String jpql = "select os from OrderSettlement os, TenantShopkeeper  ts , Order o where o=os.order and  os.invalid = :invalid and  os.owner =ts.member and ts.isShopkeeper=:isShopkeeper  and  os.status <=:status1 and ts.recommendMember=:member order by o.modifyDate desc";
		return entityManager.createQuery(jpql, OrderSettlement.class).setFlushMode(FlushModeType.COMMIT).setParameter("member", member).setParameter("invalid", false).setParameter("status1", OrderSettlement.SettlementStatus.complete)
				.setParameter("isShopkeeper", TenantShopkeeper.IsShopkeeper.yes).getResultList();
	}

	@Override
	public OrderSettlementAdapter queryOrderRecommonSettlementsByCondition(String time, String userName, String status, Pageable pageable, String ids,Long tenantId) {
		StringBuffer sb = new StringBuffer();
		String sqlString = "";
		sb.append(" SELECT");
		sb.append(" ts1.tenant_id tenant,o.id orderId,m1.`name` recommendName,m.`name` ownerName,m.mobile,o.sn,");
		sb.append(" p.payment_date paymentDate,os.order_amount orderAmount,os.order_settle_amt orderSettleAmt,");
		sb.append(" os.settle_charge settleCharge,os.order_settle_amt * vip.bonus_level / 100 AS recommonAmount,os.`status`,");
		sb.append(" (CASE os.`status` WHEN '0' THEN '未结算' WHEN '1' THEN '可结算' WHEN '3' THEN '已取消' END) AS statusName");
		sb.append(" FROM");
		sb.append(" t_order_settlement os,xx_member m,xx_member m1,xx_order o,t_tenant_shopkeeper ts,t_tenant_shopkeeper ts1,xx_payment p,t_vip_level vip");
		sb.append(" WHERE 1=1");
		sb.append(" AND os.owner_id = ts.member_id AND ts.recommend_member_id = ts1.member_id AND ts.member_id = m.id");
		sb.append(" AND ts1.member_id = m1.id AND os.order_id = o.id AND p.orders = o.id AND vip.id = ts1.vip_level");
		sb.append(" AND os.`status` IN ('"+SettlementStatus.uncomplete.ordinal()+"','"+SettlementStatus.complete.ordinal()+"','"+SettlementStatus.cancel.ordinal()+"')");
		if (StringUtils.isNotBlank(time)) {
			sb.append(" and date_format(p.payment_date,'%Y-%m') ='").append(time).append("'");
		}
		if (StringUtils.isNotBlank(userName)) {
			sb.append(" AND m1.name like '%").append(userName).append("%'");
		}
		if ("0".equals(status) || "1".equals(status) || "3".equals(status)) { 
			sb.append(" AND os.`status` = '"+status+"'");
		}
//		if (StringUtils.isNotBlank(ids)) {
//			sb.append(" and t1.id in(").append(ids).append(")");
//		}
		if(tenantId != null){
			sb.append(" and ts1.tenant_id = ").append(tenantId);
		}
//		sb.append(" UNION");
		StringBuffer sb1 = new StringBuffer();
		sb1.append(" SELECT");
		sb1.append(" bc.tenant_id tenant,o.id orderId,m.`name` recommendName,mb.`name` ownerName,mb.mobile,o.sn,");
		sb1.append(" p.payment_date paymentDate,os.order_amount orderAmount,os.order_settle_amt orderSettleAmt,");
		sb1.append(" os.settle_charge settleCharge,os.order_settle_amt * bc.bonus_percent / 100 recommonAmount,os.`status`,");
		sb1.append("(CASE WHEN os.`status` IN ('0', '1', '4') THEN '已结算' WHEN os.`status` = '2' THEN '已发放' END ) AS statusName");
		sb1.append(" FROM");
		sb1.append(" t_order_settlement os,xx_order AS o,t_charge tc,t_bonus_calc bc,xx_member m,xx_member mb,xx_payment AS p");
		sb1.append(" WHERE 1=1");
		sb1.append(" AND o.id = os.order_id AND os.charge_id = tc.id AND tc.type = '1' AND tc.member_id = bc.be_recommend_id");
		sb1.append(" AND p.orders = o.id AND m.id = bc.member_id AND mb.id = bc.be_recommend_id");
		if (StringUtils.isNotBlank(time)) {
			sb1.append(" and date_format(p.payment_date,'%Y-%m') ='").append(time).append("'");
		}
		if (StringUtils.isNotBlank(userName)) {
			sb1.append(" AND m.name like '%").append(userName).append("%'");
		}
		if ("2".equals(status)) { 
			sb1.append(" AND os.`status` IN ('0', '1', '4')");
		}
		if ("4".equals(status)) { 
			sb1.append(" AND os.`status` = '2'");
		}
		if(tenantId != null){
			sb1.append(" and bc.tenant_id = ").append(tenantId);
		}
		
		if(StringUtils.isNotBlank(status)){
			if ("2".equals(status) || "4".equals(status)) {  //可结算
				sqlString = sb1.toString();
			}else if("0".equals(status) || "1".equals(status) || "3".equals(status)){
				sqlString = sb.toString();
			}
		}else{
			sqlString = sb.toString() + " UNION" + sb1.toString();
		}
		sqlString = "SELECT * FROM ("+sqlString+") t ORDER BY paymentDate DESC";
		OrderSettlementVO amountVo = queryOrderRecommonSettlementsTotalAmount(time, userName, status, pageable, ids,tenantId);
		int total = entityManager.createNativeQuery(sqlString).setFlushMode(FlushModeType.COMMIT).getResultList().size();
		Query query = entityManager.createNativeQuery(sqlString).setFlushMode(FlushModeType.COMMIT);
		if (pageable != null) {
			int totalPages = (int) Math.ceil((double) total / (double) pageable.getPageSize());
			if (totalPages < pageable.getPageNumber()) {
				pageable.setPageNumber(totalPages);
			}
			query.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(OrderSettlementVO.class));
		List<OrderSettlementVO> list = query.getResultList();
		OrderSettlementAdapter orderSettlementAdapter = new OrderSettlementAdapter();
		orderSettlementAdapter.setPage( new Page<OrderSettlementVO>(list, total, pageable));
		orderSettlementAdapter.setTotalAmount(amountVo.getTotalAmount());
		orderSettlementAdapter.setTotalSettleCharge(amountVo.getTotalSettleCharge());
		return orderSettlementAdapter;
	}
	
	
	public OrderSettlementVO queryOrderRecommonSettlementsTotalAmount(String time, String userName, String status, Pageable pageable, String ids,Long tenantId){
		StringBuffer sb = new StringBuffer();
		String sqlString = "";
		/*sb.append(" select sum(t1.orderAmount)totalAmount,sum(t1.orderAmount)/(select relative_sell_bonus_rate from t_tenant_bonus_set where tenant_id=t1.tenant)totalSettleCharge from (select o.tenant,o.id orderId,	m.name ownerName,m.mobile, m.id,o.sn, ts.order_amount orderAmount, ");
		sb.append(" ts.order_settle_amt orderSettleAmt, ts.settle_charge settleCharge,  ts.status, p.payment_date paymentDate  ");
		sb.append(" from t_order_settlement as ts, xx_member as m, xx_order as o, xx_payment as p ");
		sb.append(" where ts.order_id = o.id and ts.owner_id = m.id and p.orders = o.id");*/
		sb.append(" SELECT SUM(os.order_settle_amt * vip.bonus_level / 100) AS tot");
		sb.append(" FROM t_order_settlement os,t_tenant_shopkeeper ts,t_tenant_shopkeeper ts1,xx_member m1,xx_payment p,t_vip_level vip");
		sb.append(" WHERE 1=1");
		sb.append(" AND os.owner_id = ts.member_id AND ts.recommend_member_id = ts1.member_id AND vip.id = ts1.vip_level");
		sb.append(" AND p.orders = os.order_id AND ts1.member_id = m1.id AND os.`status` IN ('0', '1', '3')");
		if (StringUtils.isNotBlank(time)) {
			sb.append(" and date_format(p.payment_date,'%Y-%m') ='").append(time).append("'");
		}
//		sb.append(" )t1, t_tenant_shopkeeper t,xx_member tm where t.member_id=t1.id and  t.is_shopkeeper=1 and t.recommend_member_id=tm.id  ");
		if (StringUtils.isNotBlank(userName)) {
			sb.append(" and m1.name like '%").append(userName).append("%'");
		}
		if ("0".equals(status) || "1".equals(status) || "3".equals(status)) {  //可结算
			sb.append(" AND os.`status` = '"+status+"'");
		}
		if(tenantId != null){
			sb.append(" and ts1.tenant_id = ").append(tenantId);
		}
//		sb.append(" UNION");
		StringBuffer sb1 = new StringBuffer();
		sb1.append(" SELECT SUM(os.order_settle_amt * bc.bonus_percent / 100) AS tot");
		sb1.append(" FROM t_order_settlement os,t_charge tc,xx_member m,xx_payment p,t_bonus_calc bc");
		sb1.append(" WHERE 1=1 AND p.orders = os.order_id");
		sb1.append(" AND m.id = bc.member_id AND os.charge_id = tc.id AND tc.type = '1' AND tc.member_id = bc.be_recommend_id");
		if (StringUtils.isNotBlank(time)) {
			sb1.append(" and date_format(p.payment_date,'%Y-%m') ='").append(time).append("'");
		}
		if (StringUtils.isNotBlank(userName)) {
			sb1.append(" and m.name like '%").append(userName).append("%'");
		}
		if ("2".equals(status)) { 
			sb1.append(" AND os.`status` IN ('0', '1', '4')");
		}
		if ("4".equals(status)) { 
			sb1.append(" AND os.`status` = '2'");
		}
		if(tenantId != null){
			sb1.append(" and bc.tenant_id = ").append(tenantId);
		}
		if(StringUtils.isNotBlank(status)){
			if ("2".equals(status) || "4".equals(status)) {  //可结算
				sqlString = sb1.toString();
			}else if("0".equals(status) || "1".equals(status) || "3".equals(status)){
				sqlString = sb.toString();
			}
		}else{
			sqlString = sb.toString() + " UNION" + sb1.toString();
		}
		sqlString = " SELECT SUM(t.tot) totalAmount FROM ("+sqlString+" ) t";
		Query query = entityManager.createNativeQuery(sqlString).setFlushMode(FlushModeType.COMMIT);
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(OrderSettlementVO.class));
		return (OrderSettlementVO) query.getResultList().get(0);
	}
	
	
	
	@Override
	public OrderSettlementAdapter queryOrderSettlementsByCondition(String time, String userName,
			String status, Pageable pageable, String ids,Long tenantId) {
		StringBuffer sb = new StringBuffer();
		sb.append(
				" select o.tenant,o.id orderId,	m.name ownerName,m.mobile, o.sn, ts.order_amount as orderAmount, ts.order_settle_amt orderSettleAmt, ts.settle_charge settleCharge,  ts.status, p.payment_date paymentDate");
		sb.append(" from t_order_settlement as ts, xx_member as m, xx_order as o, xx_payment as p,t_tenant_shopkeeper t ");
		sb.append(" where ts.order_id = o.id and ts.owner_id = m.id and p.orders = o.id and m.id = t.member_id ");
		if (StringUtils.isNotBlank(userName)) {
			sb.append(" and m.name like '%").append(userName).append("%'");
		}
		if (StringUtils.isNotBlank(status)) {

//			if ("1".equals(status)) {  //可结算
//				sb.append(" and ts.status in("+OrderSettlement.SettlementStatus.settlement.ordinal()+","+OrderSettlement.SettlementStatus.complete.ordinal()+")");
//			}else if("2".equals(status)){  //已发放
//				sb.append(" and ts.status =" + OrderSettlement.SettlementStatus.recevied.ordinal());
//			} else {
				sb.append(" and ts.status = ").append(status);
//			}
		}
		if (StringUtils.isNotBlank(time)) {
			sb.append(" and date_format(p.payment_date,'%Y-%m') ='").append(time).append("'");
		}

		if (StringUtils.isNotBlank(ids)) {
			sb.append(" and o.id in(").append(ids).append(")");
		}
		if(tenantId != null){
			sb.append(" and t.tenant_id = ").append(tenantId);
		}
		sb.append(" ORDER BY p.payment_Date DESC ");

		OrderSettlementVO amountVo = queryOrderSettlementTotalAmount(time, userName, status, pageable, ids,tenantId);
		
		
		//int total = entityManager.createNativeQuery(sb.toString()).setFlushMode(FlushModeType.COMMIT).getResultList().size();
		

		int total = entityManager.createNativeQuery(sb.toString()).setFlushMode(FlushModeType.COMMIT).getResultList().size();
		
		
		Query query = entityManager.createNativeQuery(sb.toString()).setFlushMode(FlushModeType.COMMIT);
		if (pageable != null) {
			int totalPages = (int) Math.ceil((double) total / (double) pageable.getPageSize());
			if (totalPages < pageable.getPageNumber()) {
				pageable.setPageNumber(totalPages);
			}
			query.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(OrderSettlementVO.class));
		List<OrderSettlementVO> list = query.getResultList();
		OrderSettlementAdapter orderSettlementAdapter = new OrderSettlementAdapter();
		orderSettlementAdapter.setPage( new Page<OrderSettlementVO>(list, total, pageable));
		orderSettlementAdapter.setTotalAmount(amountVo.getTotalAmount());
		orderSettlementAdapter.setTotalSettleCharge(amountVo.getTotalSettleCharge());
		return orderSettlementAdapter;
	}
	
	
	public OrderSettlementVO queryOrderSettlementTotalAmount(String time, String userName, String status, Pageable pageable, String ids,Long tenantId){
		StringBuffer sb = new StringBuffer();
		// sum(t1.orderAmount)totalAmount
		sb.append(
				" select sum(ts.order_amount) totalAmount,sum(ts.settle_charge) totalSettleCharge");
		sb.append(" from t_order_settlement as ts, xx_member as m, xx_order as o, xx_payment as p,t_tenant_shopkeeper s ");
		sb.append(" where ts.order_id = o.id and ts.owner_id = m.id and p.orders = o.id and s.member_id = m.id");
		if (StringUtils.isNotBlank(userName)) {
			sb.append(" and m.name like '%").append(userName).append("%'");
		}
		if (StringUtils.isNotBlank(status)) {

			if ("1".equals(status)) {  //可结算
				sb.append(" and ts.status in("+OrderSettlement.SettlementStatus.settlement.ordinal()+","+OrderSettlement.SettlementStatus.complete.ordinal()+")");
			}else if("2".equals(status)){  //已发放
				sb.append(" and ts.status =" + OrderSettlement.SettlementStatus.recevied.ordinal());
			} else {
				sb.append(" and ts.status = ").append(status);
			}
		}
		if (StringUtils.isNotBlank(time)) {
			sb.append(" and date_format(p.payment_date,'%Y-%m') ='").append(time).append("'");
		}

		if (StringUtils.isNotBlank(ids)) {
			sb.append(" and o.id in(").append(ids).append(")");
		}
		if(tenantId != null){
			sb.append(" and s.tenant_id = ").append(tenantId);
		}
		sb.append(" ORDER BY p.payment_Date DESC ");
		
		Query query = entityManager.createNativeQuery(sb.toString()).setFlushMode(FlushModeType.COMMIT);
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(OrderSettlementVO.class));
		return (OrderSettlementVO) query.getResultList().get(0);
	}
	
	public List<ShareOrderSettlementVO> queryOrderSettlementsByCondition(Date month, Member owner, Boolean isCharged, List<OrderSettlement.SettlementStatus> listStatus) {
		Date nextMonth = null;
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> createQuery = criteriaBuilder.createTupleQuery();
		Root<OrderSettlement> root = createQuery.from(OrderSettlement.class);
		Predicate conjunctions = createQuery(month, owner, isCharged, listStatus, criteriaBuilder, createQuery, root);
		createQuery.where(conjunctions);
		TypedQuery query = entityManager.createQuery(createQuery).setFlushMode(FlushModeType.COMMIT);
		List<ShareOrderSettlementVO> list = query.getResultList();
		return list;

	}

	public Page<ShareOrderSettlementVO> queryOrderSettlementsByCondition(Date month, Member owner, Boolean isCharged, List<OrderSettlement.SettlementStatus> listStatus, Pageable pageable) {
		Date nextMonth = null;

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> createQuery = criteriaBuilder.createTupleQuery();
		Root<OrderSettlement> root = createQuery.from(OrderSettlement.class);
		Predicate conjunctions = createQuery(month, owner, isCharged, listStatus, criteriaBuilder, createQuery, root);
		createQuery.where(conjunctions);

		if (pageable == null) {
			pageable = new Pageable();
		}
		long total = count(criteriaBuilder, createQuery, root);

		int totalPages = (int) Math.ceil((double) total / (double) pageable.getPageSize());
		if (totalPages < pageable.getPageNumber()) {
			pageable.setPageNumber(totalPages);
		}
		TypedQuery query = entityManager.createQuery(createQuery).setFlushMode(FlushModeType.COMMIT);
		query.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize());
		query.setMaxResults(pageable.getPageSize());
		List<ShareOrderSettlementVO> list = query.getResultList();
		return new Page<ShareOrderSettlementVO>(list, total, pageable);

	}

	/**
	 * @Title：count @Description：
	 * @param criteriaBuilder
	 * @param createQuery
	 * @param root
	 * @return long
	 */
	protected long count(CriteriaBuilder criteriaBuilder, CriteriaQuery<Tuple> createQuery, Root<OrderSettlement> root) {
		CriteriaQuery<Long> countCriteriaQuery = criteriaBuilder.createQuery(Long.class);
		countCriteriaQuery.select(criteriaBuilder.count(root));
		if (createQuery.getGroupList() != null) {
			countCriteriaQuery.groupBy(createQuery.getGroupList());
		}
		if (createQuery.getGroupRestriction() != null) {
			countCriteriaQuery.having(createQuery.getGroupRestriction());
		}
		if (createQuery.getRestriction() != null) {
			countCriteriaQuery.where(createQuery.getRestriction());
		}
		long total = entityManager.createQuery(countCriteriaQuery).setFlushMode(FlushModeType.COMMIT).getSingleResult();
		return total;
	}

	/**
	 * @Title：createQuery @Description：
	 * @param month
	 * @param owner
	 * @param isCharged
	 * @param listStatus
	 * @param criteriaBuilder
	 * @param createQuery
	 * @param root
	 * @return Predicate
	 */
	protected Predicate createQuery(Date month, Member owner, Boolean isCharged, List<OrderSettlement.SettlementStatus> listStatus, CriteriaBuilder criteriaBuilder, CriteriaQuery<Tuple> createQuery, Root<OrderSettlement> root) {
		Date nextMonth;
		Predicate conjunctions = criteriaBuilder.conjunction();
		Path<Object> path = root.join("order").join("payments").get("paymentDate");
		createQuery.multiselect(root.get("owner").get("name").alias("ownerName"), root.get("owner").get("mobile").alias("ownerMobile"), root.get("order").get("sn").alias("orderSn"), root.get("orderAmount"), root.get("orderSettleAmt"), root.get("status"),
				root.get("status"), path, root.get("chargeId"));
		if (month != null) {
			nextMonth = DateUtil.addMonth(month, 1);
			conjunctions = criteriaBuilder.and(conjunctions, criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), month), criteriaBuilder.lessThanOrEqualTo(root.<Date> get("createDate"), nextMonth)));
		}
		if (listStatus != null && listStatus.size() > 0) {
			conjunctions = criteriaBuilder.and(conjunctions, root.get("status").in(listStatus));
		}
		if (owner != null) {
			conjunctions = criteriaBuilder.and(conjunctions, criteriaBuilder.equal(root.get("owner").get("name"), owner.getName()));
		}
		if (isCharged != null && isCharged) {
			conjunctions = criteriaBuilder.and(conjunctions, criteriaBuilder.isNotNull(root.get("chargeId")));
		} else {
			conjunctions = criteriaBuilder.and(conjunctions, criteriaBuilder.isNull(root.get("chargeId")));
		}
		List<Order> orders = new ArrayList<Order>();
		orders.add(criteriaBuilder.desc(path));
		createQuery.orderBy(orders);
		return conjunctions;
	}

	@Override
	public List<ShareOrderSettlementVO> queryInviteOrderSettlementsByCondition(Date month, Member owner, Boolean isCharged, List<OrderSettlement.SettlementStatus> listStatus) {
		StringBuffer sb = new StringBuffer();
		Date nextMonth = null;
		sb.append(
				"SELECT tm.name as invitName, ttt.name as ownerName,ttt.sn as orderSn,ttt.paymentDate as payDate,ttt.orderAmount as orderAmount,ttt.orderSettleAmt as orderSettleAmt,ttt.settleCharge as settleCharge,ttt.status as status,ttt.chargeId as chargeId");
		sb.append(" FROM ( SELECT m.id,m.name,m.mobile,o.sn,ts.orderAmount,ts.orderSettleAmt,ts.settleCharge,ts.status,p.paymentDate,ts.chargeId FROM");
		sb.append(" OrderSettlement AS ts,Member AS m,Order AS o,Payment AS p");
		sb.append(" WHERE ts.order = o ");
		sb.append(" AND ts.owner = m ");
		sb.append(" AND p.orders = o ");
		if (month != null) {
			nextMonth = DateUtil.addMonth(month, 1);
			sb.append(" and ts.createDate >=:month and ts.createDate<:nextMonth");
		}
		if (listStatus.size() > 0) {
			for (int i = 0; i < listStatus.size(); i++) {
				if (i == 0) {
					sb.append(" and ts.status =:status ");
				} else {
					sb.append(" or ts.status =:status" + i + " ");
				}
			}
		}
		if (isCharged != null) {
			if (isCharged) {
				sb.append(" and ts.chargeId is not null  ");
			} else {
				sb.append(" and ts.chargeId is null  ");
			}
		}
		sb.append(") AS ttt,TenantShopkeeper tts,Member tm ");
		sb.append("WHERE ttt.id = tts.memberId ");
		sb.append("AND tts.isShopkeeper = 1 ");
		sb.append("AND tts.recommendMemberId = tm.id ");
		if (owner != null) {
			sb.append(" and tm.name =:owner  ");
		}
		sb.append("ORDER BY tm.id ASC,ttt.paymentDate DESC ");
		Query query = entityManager.createQuery(sb.toString()).setFlushMode(FlushModeType.COMMIT);
		if (month != null) {
			query.setParameter("month", month).setParameter("nextMonth", nextMonth);
		}
		if (listStatus.size() > 0) {
			for (int i = 0; i < listStatus.size(); i++) {
				if (i == 0) {
					query.setParameter("status", listStatus.get(i));
				} else {
					query.setParameter("status" + i, listStatus.get(i));
				}
			}
		}
		if (owner != null) {
			query.setParameter("owner", owner.getName());
		}
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(ShareOrderSettlementVO.class));
		@SuppressWarnings("unchecked")
		List<ShareOrderSettlementVO> list = query.getResultList();
		return list;
	}

	@Override
	public OrderSettlementAdapter queryShareChargeMonthReportByCondition(
			String time, String userName, Pageable pageable, String moblie, String status,Long tenantId) {
		StringBuffer sb = new StringBuffer();
		String totalSql = "SELECT SUM(OS.order_settle_amt)  totalAmount,sum(T.charge) totalSettleCharge ";
		String sql=" SELECT T.charge_date chargeDate,M.`name` ownerName ,M.mobile,SUM(OS.order_settle_amt)  orderAmount,T.charge settleCharge,case T.status WHEN 0 THEN '未申请' WHEN 1 THEN '提现审核中' WHEN 2 THEN '已提现' else 'ss' END shareStatus";
		sb.append(" FROM T_CHARGE AS T,t_order_settlement AS OS,xx_member AS M ");
		sb.append(" WHERE T.member_id = M.id AND T.id = OS.charge_id and t.type =1	");
		if (StringUtils.isNotBlank(userName)) {
			sb.append(" and m.name like '%").append(userName).append("%'");
		}
		if (StringUtils.isNotBlank(status)) {
			sb.append(" and t.status =").append(status).append("");
		}
		if (StringUtils.isNotBlank(moblie)) {
			sb.append(" and m.mobile like '%").append(moblie).append("%'");
		}
		
		if (StringUtils.isNotBlank(time)) {
			sb.append(" and T.charge_date ='").append(time).append("'");
		}
		if(tenantId != null){
			sb.append(" and T.tenant_id ='").append(tenantId).append("'");
		}
		
		
		StringBuffer totalSqls = new StringBuffer(totalSql);
		totalSqls.append(sb.toString());
		Query query = entityManager.createNativeQuery(totalSqls.toString()).setFlushMode(FlushModeType.COMMIT);
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(OrderSettlementVO.class));
		
		OrderSettlementVO amountVo = (OrderSettlementVO) query.getResultList().get(0);
		sb.append(" GROUP BY t.member_id ");
		sb.insert(0, sql);
		//OrderSettlementVO amountVo = queryOrderSettlementTotalAmount(time, userName, pageable);
		
		
		//int total = entityManager.createNativeQuery(sb.toString()).setFlushMode(FlushModeType.COMMIT).getResultList().size();
		

		int total = entityManager.createNativeQuery(sb.toString()).setFlushMode(FlushModeType.COMMIT).getResultList().size();
		
		
		 query = entityManager.createNativeQuery(sb.toString()).setFlushMode(FlushModeType.COMMIT);
		if (pageable != null) {
			int totalPages = (int) Math.ceil((double) total / (double) pageable.getPageSize());
			if (totalPages < pageable.getPageNumber()) {
				pageable.setPageNumber(totalPages);
			}
			query.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(OrderSettlementVO.class));
		List<OrderSettlementVO> list = query.getResultList();
		OrderSettlementAdapter orderSettlementAdapter = new OrderSettlementAdapter();
		orderSettlementAdapter.setPage( new Page<OrderSettlementVO>(list, total, pageable));
		orderSettlementAdapter.setTotalAmount(amountVo.getTotalAmount());
		orderSettlementAdapter.setTotalSettleCharge(amountVo.getTotalSettleCharge());
	
		return orderSettlementAdapter;
	}

	@Override
	public OrderSettlementAdapter recommendChargeListMonthReport(String time,
			String userName, Pageable pageable, String moblie, String status,Long tenantId) {
		StringBuffer sb = new StringBuffer();
		String totalSql = "SELECT SUM(a.tot)  totalAmount,sum(tc.charge) totalSettleCharge ";
		String sql=" SELECT tc.charge_date chargeDate,	m.`name` ownerName,	m.mobile,	SUM(a.tot) orderAmount,	tc.charge settleCharge,	case Tc.status WHEN 0 THEN '未申请' WHEN 1 THEN '提现审核中' WHEN 2 THEN '已提现' else 'ss' END shareStatus";
		sb.append(" FROM	t_charge tc,	xx_member m,	t_bonus_calc bc,	t_charge tc1,	");
		sb.append(" (SELECT	sum(os.order_settle_amt) AS tot,			os.charge_id FROM t_order_settlement AS os");
		sb.append(" WHERE	os.finish_date IS NOT NULL		GROUP BY			os.charge_id	) a");
		sb.append(" WHERE tc.member_id = m.id AND tc.type = '0' AND bc.charge_id = tc.id AND bc.be_recommend_id = tc1.member_id AND tc1.type = '1' AND a.charge_id = tc1.id ");
		if (StringUtils.isNotBlank(userName)) {
			sb.append(" and m.name like '%").append(userName).append("%'");
		}
		if (StringUtils.isNotBlank(status)) {
			sb.append(" and tc.status =").append(status).append("");
		}
		if (StringUtils.isNotBlank(moblie)) {
			sb.append(" and m.mobile like '%").append(moblie).append("%'");
		}
		
		if (StringUtils.isNotBlank(time)) {
			sb.append(" and tc.charge_date ='").append(time).append("'");
		}
		if(tenantId != null){
			sb.append(" and tc.tenant_id = ").append(tenantId);
		}
		
		StringBuffer totalSqls = new StringBuffer(totalSql);
		totalSqls.append(sb.toString());
		Query query = entityManager.createNativeQuery(totalSqls.toString()).setFlushMode(FlushModeType.COMMIT);
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(OrderSettlementVO.class));
		
		OrderSettlementVO amountVo = (OrderSettlementVO) query.getResultList().get(0);
		sb.append(" GROUP BY  m.id ");
		sb.insert(0, sql);
		//OrderSettlementVO amountVo = queryOrderSettlementTotalAmount(time, userName, pageable);
		
		
		//int total = entityManager.createNativeQuery(sb.toString()).setFlushMode(FlushModeType.COMMIT).getResultList().size();
		

		int total = entityManager.createNativeQuery(sb.toString()).setFlushMode(FlushModeType.COMMIT).getResultList().size();
		
		
		 query = entityManager.createNativeQuery(sb.toString()).setFlushMode(FlushModeType.COMMIT);
		if (pageable != null) {
			int totalPages = (int) Math.ceil((double) total / (double) pageable.getPageSize());
			if (totalPages < pageable.getPageNumber()) {
				pageable.setPageNumber(totalPages);
			}
			query.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(OrderSettlementVO.class));
		List<OrderSettlementVO> list = query.getResultList();
		OrderSettlementAdapter orderSettlementAdapter = new OrderSettlementAdapter();
		orderSettlementAdapter.setPage( new Page<OrderSettlementVO>(list, total, pageable));
		orderSettlementAdapter.setTotalAmount(amountVo.getTotalAmount());
		orderSettlementAdapter.setTotalSettleCharge(amountVo.getTotalSettleCharge());
	
		return orderSettlementAdapter;
	}

}
