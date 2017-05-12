package net.wit.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.wit.vo.OrderSettlementAdapter;
import net.wit.vo.OrderSettlementVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import net.sf.json.JSONArray;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.OrderSettlementDao;
import net.wit.entity.Charge;
import net.wit.entity.Member;
import net.wit.entity.Order;
import net.wit.entity.OrderSettlement;
import net.wit.entity.Tenant;
import net.wit.entity.TenantBonusSet;
import net.wit.entity.VipLevel;
import net.wit.service.OrderSettlementService;
import net.wit.service.TenantBonusSetService;
import net.wit.util.DateUtil;

/**
 * @ClassName：OrderSettlementServiceImpl @Description：
 * @author：Chenlf
 * @date：2015年9月11日 下午11:00:19
 */
@Service("orderSettlementServiceImpl")
public class OrderSettlementServiceImpl extends BaseServiceImpl<OrderSettlement, Long>implements OrderSettlementService {
	@Resource(name = "orderSettlementDaoImpl")
	public void setBaseDao(OrderSettlementDao orderSettlementDao) {
		super.setBaseDao(orderSettlementDao);
	}
	
	@Resource(name="orderSettlementDaoImpl")
	private OrderSettlementDao orderSettlementDao;
	
	@Autowired
	private TenantBonusSetService tenantBonusSetService;
	
	@Transactional
	public void orderSettlement(OrderSettlement orderSettlement){
		if(orderSettlement == null){
			return;
		}
		orderSettlement.setOrderSettleAmt(orderSettlement.getOrderAmount()
				.subtract(orderSettlement.getOrderReturnAmt()));//订单结算金额 = 下单金额-退货金额
		orderSettlement.setSettleCharge(orderSettlement.getOrderCharge()
				.subtract(orderSettlement.getReturnCharge()));//结算佣金 = 下单佣金-退货佣金
		orderSettlement.setStatus(OrderSettlement.SettlementStatus.settlement);
		orderSettlementDao.merge(orderSettlement);
	}
	public void orderFinish(OrderSettlement orderSettlement){
		
	}
	public BigDecimal getSettlementCharge(Member owner){
		return orderSettlementDao.getSettlementCharge(owner);
	}
	
	public List<OrderSettlement> getSettlementDone(){
		return orderSettlementDao.getSettlementDone();
	}
	@Override
	public Page<OrderSettlement> getOrderSettlementStream(Member owner,
			int pageNo, int pageSize) {
		 Pageable pageable=new Pageable();
		 pageable.setPageSize(pageSize);
		 pageable.setPageNumber(pageNo);
		 return  orderSettlementDao.getOrderSettlementStream(owner,pageable);
	
	}
	@Override
	public BigDecimal getOwnerSettleCharge(Member owner) {
		return orderSettlementDao.getOwnerSettleCharge(owner);
	}
	@Override
	public Page<OrderSettlement> getOrderSettlementUncomplete(Member owner,
			int pageNo, int pageSize) {
		 Pageable pageable=new Pageable();
		 pageable.setPageSize(pageSize);
		 pageable.setPageNumber(pageNo);
		 return  orderSettlementDao.getOrderSettlementUncomplete(owner, pageable); 
	}
	public Page<OrderSettlement> findByChargeId(Long chargeId,Pageable pageable){
		return orderSettlementDao.findByChargeId(chargeId, pageable);
	}

	@Override
	public BigDecimal getPendingOrderBonus(Member member,Tenant tenant){
		TenantBonusSet tbs = tenantBonusSetService.getRegularTenantBonusSetByTenantId(tenant.getId());
		if(tbs==null) return BigDecimal.ZERO;
		BigDecimal orderAmt = this.orderSettlementDao.getPendingorderSettleAmt(member);
		if(orderAmt==null) return BigDecimal.ZERO;
		return tenantBonusSetService.calcReBonus(orderAmt, tbs);
	}
	
	@Override
	public BigDecimal getPendingOrderBonus(Member member,VipLevel vipLevel){
		if(vipLevel==null) return BigDecimal.ZERO;
		BigDecimal orderAmt = this.orderSettlementDao.getPendingorderSettleAmt(member);
		if(orderAmt==null) return BigDecimal.ZERO;
		return tenantBonusSetService.calcReBonus(orderAmt, vipLevel);
	}
	
	@Override
	public JSONArray getPendingOrderSettleInfo(Member member,Tenant tenant){
		TenantBonusSet tbs = tenantBonusSetService.getRegularTenantBonusSetByTenantId(tenant.getId());
//		Integer sellBonusRate = tbs.getRelativeSellBonusRate();
		List<OrderSettlement> list  = this.orderSettlementDao.getPendingOrderSettlements(member);
		JSONArray array = new JSONArray();
		for (OrderSettlement orderSettlement : list) {
			JSONObject jsonObject = new JSONObject();
			BigDecimal orderAmt = orderSettlement.getOrderSettleAmt();
			Order order = orderSettlement.getOrder();
			BigDecimal orderBonus =  tenantBonusSetService.calcReBonus(orderAmt, tbs);
			jsonObject.put("orderAmt", orderAmt);
			jsonObject.put("orderBonus", orderBonus);
			jsonObject.put("orderSn", order.getSn());
			jsonObject.put("orderStatus",orderSettlement.getOrder().getOrderStatusName() );
			Date orderModifyDate = orderSettlement.getOrder().getModifyDate();
			jsonObject.put("orderDate", DateUtil.changeDateToStr(orderSettlement.getCreateDate(), DateUtil.LINK_DISPLAY_DATE_MINUTE));
			jsonObject.put("orderShortDate", DateUtil.changeDateToStr(orderModifyDate, DateUtil.DOT_DISPLAY_DATE));
			jsonObject.put("member", orderSettlement.getOwner().getName());
			jsonObject.put("tel", orderSettlement.getMember().getMobile());
			array.add(jsonObject);
		}
		return array;
	}
	
	
	@Override
	public JSONArray getPendingOrderSettleInfo(Member member,Tenant tenant,VipLevel vipLevel){
//		TenantBonusSet tbs = tenantBonusSetService.getRegularTenantBonusSetByTenantId(tenant.getId());
//		Integer sellBonusRate = tbs.getRelativeSellBonusRate();
		List<OrderSettlement> list  = this.orderSettlementDao.getPendingOrderSettlements(member);
		JSONArray array = new JSONArray();
		for (OrderSettlement orderSettlement : list) {
			JSONObject jsonObject = new JSONObject();
			BigDecimal orderAmt = orderSettlement.getOrderSettleAmt();
			Order order = orderSettlement.getOrder();
			BigDecimal orderBonus =  tenantBonusSetService.calcReBonus(orderAmt, vipLevel);
			jsonObject.put("orderAmt", orderAmt);
			jsonObject.put("orderBonus", orderBonus);
			jsonObject.put("orderSn", order.getSn());
			jsonObject.put("orderStatus",orderSettlement.getOrder().getOrderStatusName() );
			Date orderModifyDate = orderSettlement.getOrder().getModifyDate();
			jsonObject.put("orderDate", DateUtil.changeDateToStr(orderSettlement.getCreateDate(), DateUtil.LINK_DISPLAY_DATE_MINUTE));
			jsonObject.put("orderShortDate", DateUtil.changeDateToStr(orderModifyDate, DateUtil.DOT_DISPLAY_DATE));
			jsonObject.put("member", orderSettlement.getOwner().getName());
			jsonObject.put("tel", orderSettlement.getMember().getMobile());
			array.add(jsonObject);
		}
		return array;
	}
	
	
	
	@Override
	public BigDecimal getOrderBonus(OrderSettlement orderSettlement,Tenant tenant){
		TenantBonusSet tbs = tenantBonusSetService.getRegularTenantBonusSetByTenantId(tenant.getId());
		BigDecimal orderAmt = orderSettlement.getOrderSettleAmt();
		return tenantBonusSetService.calcReBonus(orderAmt, tbs);
	}
	
	@Override
	public BigDecimal getOrderBonus(OrderSettlement orderSettlement,VipLevel vipLevel){
		BigDecimal orderAmt = orderSettlement.getOrderSettleAmt();
		return tenantBonusSetService.calcReBonus(orderAmt, vipLevel);
	}



	@Override
	public OrderSettlementAdapter queryOrderSettlementsByCondition(String time,String userName,
																	String status,Pageable pageable,String ids,Long tenantId) {
		return orderSettlementDao.queryOrderSettlementsByCondition(time,userName,status,pageable,ids,tenantId);
	}
	@Override
	public OrderSettlementAdapter queryOrderRecommonSettlementsByCondition(String time,String userName,
			String status,Pageable pageable,String ids,Long tenantId) {
		return orderSettlementDao.queryOrderRecommonSettlementsByCondition(time,userName,status,pageable,ids,tenantId);
	}
	
	@Override
	public void batchUpateOrderSettlementToRece(List<Charge> list) throws Exception{
		this.orderSettlementDao.batchUpateOrderSettlementRecevied(list);
	}
	@Override
	public OrderSettlementAdapter queryShareChargeMonthReportByCondition(
			String time, String username, Pageable pageable, Object object, String moblie, String status,Long tenantId) {
		// TODO Auto-generated method stub
		return orderSettlementDao.queryShareChargeMonthReportByCondition(time,username,pageable,moblie,status,tenantId);
	}
	@Override
	public OrderSettlementAdapter recommendChargeListMonthReport(String time,
			String username, Pageable pageable, Object object, String moblie, String status,Long tenantId) {
		// TODO Auto-generated method stub
		return orderSettlementDao.recommendChargeListMonthReport(time,username,pageable,moblie,status,tenantId);
	}

}
