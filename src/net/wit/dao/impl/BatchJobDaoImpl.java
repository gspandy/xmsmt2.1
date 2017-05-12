/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.Query;

import net.wit.dao.BatchJobDao;
import net.wit.entity.Deposit;
import net.wit.entity.Member;
import net.wit.entity.Order;
import net.wit.entity.OrderSettlement;
import net.wit.entity.Returns;
import net.wit.entity.TenantShopkeeper;
import net.wit.entity.TenantShopkeeper.IsShopkeeper;
import net.wit.entity.Ticket;
import net.wit.entity.TicketCache;
import net.wit.util.Constants;
import net.wit.util.DateUtil;
import net.wit.util.TicketUtil;
import net.wit.vo.OrderSettlementVO;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

/**
 * Dao - 批处理
 * @author rsico Team
 * @version 3.0
 */
@Repository("batchJobDaoImpl")
public class BatchJobDaoImpl extends BaseDaoImpl<Object, Long> implements BatchJobDao{

	public List<Deposit> findList(Member member, Date beginTime, Date endTime) {
		StringBuffer hql = new StringBuffer("from Deposit  d where 1=1");
		if (member != null) {
			hql.append(" and d.member.id=" + member.getId());
		}
		if (beginTime != null) {
			hql.append(" and d.createDate >='" + DateUtil.changeDateToStr(beginTime,DateUtil.LINK_DISPLAY_DATE_FULL)+"'");
		}
		if (endTime != null) {
			hql.append(" and d.createDate<='" +  DateUtil.changeDateToStr(endTime,DateUtil.LINK_DISPLAY_DATE_FULL)+"'");
		}
		hql.append(" order by d.createDate desc");
		return entityManager.createQuery(hql.toString(), Deposit.class).setFlushMode(FlushModeType.COMMIT).getResultList();
	}
	
	public void batchUpateTicket(Date date)throws Exception{
		String strDate = DateUtil.changeDateToStr(date, DateUtil.LINK_DISPLAY_DATE);
		
		StringBuffer sb = new StringBuffer();
		sb.append(" update t_ticket set ");
		sb.append(" status = '").append(Ticket.Status.expired.ordinal()+"',");
//		sb.append(" where status in (").append(Ticket.Status.nouse.ordinal()).append(",")
//		  .append(Ticket.Status.recevied.ordinal()).append(")");
//		sb.append(" and date_format(expiry_date,'%Y-%m-%d') ").append(" < '").append(strDate+"'");
		sb.append(" modify_date = sysdate() ");
		sb.append(" where date_format(expiry_date,'%Y-%m-%d') ").append(" < '").append(strDate+"'");
		Query query = entityManager.createNativeQuery(sb.toString());
		query.executeUpdate();
	}
	
	public void batchUpateSettlenment()throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append(" update t_order_settlement set ");
		sb.append(" order_settle_amt = order_amount - order_return_amt,");
		sb.append(" settle_charge = order_charge - return_charge,");
		sb.append(" status = '").append(OrderSettlement.SettlementStatus.settlement.ordinal()).append("',");
		sb.append(" modify_date = sysdate() ");
		sb.append(" where status = '").append(OrderSettlement.SettlementStatus.complete.ordinal()).append("'");
		Query query = entityManager.createNativeQuery(sb.toString());
		query.executeUpdate();
	}
	
	public void batchUpateTicketCache(Date date)throws Exception{
		String strDate = DateUtil.changeDateToStr(date, DateUtil.LINK_DISPLAY_DATE);
		StringBuffer sb = new StringBuffer();
		sb.append(" update t_ticket_cache set ");
		sb.append(" receive_status = '").append(TicketCache.TICKETCACHE_EXPIREDSTATUS+"',");
		sb.append(" modify_date = sysdate() ");
//		sb.append(" where receive_status = '").append(TicketUtil.TICKETCACHE_NORECEIVESTATUS+"'");
//		sb.append(" and date_format(expiry_date,'%Y-%m-%d') ").append(" < '").append(strDate+"'");
		sb.append(" where date_format(expiry_date,'%Y-%m-%d') ").append(" < '").append(strDate+"'");
		
		Query query = entityManager.createNativeQuery(sb.toString());
		query.executeUpdate();
	}
	public List<OrderSettlement> findPlanFinishOrder(Date date) {
		String strDate = DateUtil.changeDateToStr(date, DateUtil.LINK_DISPLAY_DATE);
		StringBuffer hql = new StringBuffer("from OrderSettlement  os where 1=1");
		hql.append(" and date_format(planFinishDate,'%Y-%m-%d') < '").append(strDate+"'");
		return entityManager.createQuery(hql.toString(), OrderSettlement.class).
				setFlushMode(FlushModeType.COMMIT).getResultList();
	}
	
	public List<OrderSettlementVO> getSettlementCharge(Date date){
		
		String strDate = DateUtil.changeDateToStr(date, DateUtil.LINK_DISPLAY_DATE_MONTH);
		StringBuffer sb = new StringBuffer();
		sb.append("select sum(os.order_settle_amt) as orderSettleAmt,sum(os.settle_charge) as settleCharge,");
		sb.append(" os.owner_id as ownerId,t.recommend_member_id as recommandId,t.tenant_id as tenant");
		sb.append(" from t_order_settlement os,t_tenant_shopkeeper t ");
		sb.append(" where os.owner_id = t.member_id and date_format(finish_Date,'%Y-%m') = '"+strDate+"'");		
		sb.append(" and status = '"+OrderSettlement.SettlementStatus.complete.ordinal()+"'");
		sb.append(" and t.is_shopkeeper = 1");
		sb.append(" and (invalid = '"+Constants.NO_FLAG+"' or invalid is null)");
		sb.append(" group by owner_id");
	
		Query query = entityManager.createNativeQuery(sb.toString());
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(OrderSettlementVO.class));
		@SuppressWarnings("unchecked")
		List<OrderSettlementVO> list = query.getResultList();
		return list;
			
	}
	
	public void batchUpateChargeId(Long chargeId,Long ownerId,Date date)throws Exception{
		String strDate = DateUtil.changeDateToStr(date, DateUtil.LINK_DISPLAY_DATE_MONTH);
		StringBuffer sb = new StringBuffer();
		sb.append(" update t_order_settlement set ");
		sb.append(" charge_id = ").append(chargeId).append(",");
		sb.append("  status = '"+OrderSettlement.SettlementStatus.settlement.ordinal()+"',");
		sb.append(" modify_date = sysdate() ");
		sb.append(" where date_format(finish_Date,'%Y-%m') = '"+strDate+"'");		
		sb.append(" and status = '"+OrderSettlement.SettlementStatus.complete.ordinal()+"'");
		sb.append(" and owner_id = ").append(ownerId);
		Query query = entityManager.createNativeQuery(sb.toString());
		query.executeUpdate();
	}
	
	public List<Long> getOwnerIdList(Date date) {
		String strDate = DateUtil.changeDateToStr(date, DateUtil.LINK_DISPLAY_DATE_MONTH);
		StringBuffer sb = new StringBuffer();
		sb.append(" select owner_id as ownerId from t_order_settlement os");
		sb.append(" where date_format(finish_Date,'%Y-%m') = '"+strDate+"'");		
		sb.append(" and status = '"+OrderSettlement.SettlementStatus.settlement.ordinal()+"'");
		sb.append(" group by owner_id");
	
		Query query = entityManager.createNativeQuery(sb.toString());
		@SuppressWarnings("unchecked")
		List<Long> list = query.getResultList();
		return list;
	}
	
	public List<Order> findExpireOrder(Date date) {
		StringBuffer hql = new StringBuffer("from Order order where 1=1");
		hql.append(" and orderStatus = :orderStatus");
		hql.append(" and paymentStatus = :paymentStatus");
		hql.append(" and expire is not null and expire <= :expire");
		return entityManager.createQuery(hql.toString(), Order.class)
				.setFlushMode(FlushModeType.COMMIT)
				.setParameter("orderStatus",Order.OrderStatus.confirmed)
				.setParameter("paymentStatus", Order.PaymentStatus.unpaid)
				.setParameter("expire", date)
				.getResultList();
	}
	
	public List<Order> findOrderAccepted(Date dueDate) {
		StringBuffer hql = new StringBuffer("from Order order where 1=1");
		hql.append(" and shippingStatus = :shippingStatus");
		hql.append(" and orderStatus = :orderStatus");
		hql.append(" and modifyDate is not null and modifyDate <= :modifyDate");
		return entityManager.createQuery(hql.toString(), Order.class)
				.setFlushMode(FlushModeType.COMMIT)
				.setParameter("shippingStatus", Order.ShippingStatus.accept)
				.setParameter("orderStatus", Order.OrderStatus.confirmed)
				.setParameter("modifyDate", dueDate)
				.getResultList();
	}
	
	public List<Order> findOrderReturn(Date dueDate) {
		StringBuffer hql = new StringBuffer("select o from Order o,Returns re where 1=1");
		hql.append(" and o.id = re.order");
		hql.append(" and o.orderStatus = :orderStatus");
		hql.append(" and o.shippingStatus = :shippingStatus");
		hql.append(" and re.orderStat = :orderStat");
		hql.append(" and re.modifyDate <= :modifyDate");
		return entityManager.createQuery(hql.toString(), Order.class)
				.setFlushMode(FlushModeType.COMMIT)
				.setParameter("orderStatus", Order.OrderStatus.confirmed)
				.setParameter("shippingStatus", Order.ShippingStatus.apply)
				.setParameter("orderStat", Returns.ReturnStatus.agree)
				.setParameter("modifyDate", dueDate)
				.getResultList();
	}
	
	public List<Order> findOrderShipped(Date dueDate) {
		StringBuffer sb = new StringBuffer("select o from Order o,Shipping s where o.id = s.order");
		sb.append(" and o.shippingStatus = :shippingStatus");
		sb.append(" and s.createDate <= :createDate");
		sb.append(" and (o.isExtend is null or o.isExtend = 0)");//TODO isExtend is null
		
		return entityManager.createQuery(sb.toString(), Order.class)
				.setFlushMode(FlushModeType.COMMIT)
				.setParameter("shippingStatus", Order.ShippingStatus.shipped)
				.setParameter("createDate", dueDate)
//				.setParameter("isExtend", isExtend)
				.getResultList();
	}
	@Override
	public List<Order> findOrderShippedExtend(Date dueDate) {
		StringBuffer sb = new StringBuffer("select o from Order o,Shipping s where o.id = s.order");
		sb.append(" and o.shippingStatus = :shippingStatus");
		sb.append(" and s.createDate <= :createDate");
		sb.append(" and o.isExtend = :isExtend");//TODO isExtend is null
		
		return entityManager.createQuery(sb.toString(), Order.class)
				.setFlushMode(FlushModeType.COMMIT)
				.setParameter("shippingStatus", Order.ShippingStatus.shipped)
				.setParameter("createDate", dueDate)
				.setParameter("isExtend", true)
				.getResultList();
	}
	
	public List<TenantShopkeeper> findShopKeeper(Date date) {
		
		String strDate = DateUtil.changeDateToStr(date, DateUtil.LINK_DISPLAY_DATE_MONTH);
		StringBuffer hql = new StringBuffer("select s from TenantShopkeeper s where 1=1");
		hql.append(" and not exists (select r.member from Rent r where s.member = r.member ");
//		hql.append(" and r.status in (").append(Rent.Status.charged.ordinal()).append(",")
//		.append(Rent.Status.system.ordinal()).append(")");
		hql.append(" and r.rentDate = '"+strDate+"')");
		hql.append(" and s.isShopkeeper = '"+IsShopkeeper.yes.ordinal()+"'");
		return entityManager.createQuery(hql.toString(), TenantShopkeeper.class).
				setFlushMode(FlushModeType.COMMIT).getResultList();
    }
	
}