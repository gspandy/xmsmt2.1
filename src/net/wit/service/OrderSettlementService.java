package net.wit.service;

import java.math.BigDecimal;
import java.util.List;

import net.sf.json.JSONArray;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Charge;
import net.wit.entity.Member;
import net.wit.entity.OrderSettlement;
import net.wit.entity.Tenant;
import net.wit.entity.VipLevel;
import net.wit.vo.OrderSettlementAdapter;
import net.wit.vo.OrderSettlementVO;

/**
 * @ClassName：OrderSettlementService @Description：
 * @author：Chenlf
 * @date：2015年9月11日 下午10:59:40
 */
public interface OrderSettlementService extends BaseService<OrderSettlement, Long> {
	/**
	 * @return 
	 * 
	 */
	public BigDecimal getSettlementCharge(Member owner);
	
	public List<OrderSettlement> getSettlementDone();
	
	public void orderSettlement(OrderSettlement orderSettlement);
	
	/**
	 * 获取佣金流水 返回已经完成和结算的订单
	 * @param orderSettlement
	 */
	public Page<OrderSettlement> getOrderSettlementStream (Member owner,int pageNo,int pageSize);
	/**
	 * 获取店主的待结佣金
	 */
	public BigDecimal getOwnerSettleCharge(Member owner);
	/**
	 * 未完成的佣金流水列表
	 */
	public Page<OrderSettlement> getOrderSettlementUncomplete (Member owner,int pageNo,int pageSize);
	/**
	 * 通过提现id查询列表
	 * @param chargeId
	 * @param pageable
	 * @return
	 */
	public Page<OrderSettlement> findByChargeId(Long chargeId,Pageable pageable);
   /**
    * 店主预收奖金计算(含未完成及已完成)
    * @param member
    * @param tenant
    * @return
    */
	public BigDecimal getPendingOrderBonus(Member member, Tenant tenant);
   /**
    * 店主预收奖金明细(含已完成订单, 未完成订单)
    * @param member
    * @param tenant
    * @return
    */
	public JSONArray getPendingOrderSettleInfo(Member member, Tenant tenant);
	
	/**
	 * 获取订单的当前奖金
	 * @param orderSettlement
	 * @return
	 */
	public BigDecimal getOrderBonus(OrderSettlement orderSettlement,Tenant tenant);


	public OrderSettlementAdapter queryOrderSettlementsByCondition(String time,String userName,
																	String status,Pageable pageable,String ids,Long tenantId);
	public OrderSettlementAdapter queryOrderRecommonSettlementsByCondition(String time,String userName,
			String status,Pageable pageable,String ids,Long tenantId);
	/**
	 * 批量修改ordersettlement 为已经发放
	 * @param list
	 */
	public void batchUpateOrderSettlementToRece(List<Charge> list)throws Exception;
	/**
	 * 根据vip等级获取订单的销售奖金
	 * @param orderSettlement
	 * @param vipLevel
	 * @return
	 */
	public BigDecimal getOrderBonus(OrderSettlement orderSettlement, VipLevel vipLevel);

	/**
	 * 根据vip等级获取订单的销售奖金
	 * @param member
	 * @param vipLevel
	 * @return
	 */
	public BigDecimal getPendingOrderBonus(Member member, VipLevel vipLevel);
	/**
	 * 获取预结算订单根据vip等级计算；
	 * @param member
	 * @param tenant
	 * @param vipLevel
	 * @return
	 */
	public JSONArray getPendingOrderSettleInfo(Member member, Tenant tenant,
			VipLevel vipLevel);
		
		
		public OrderSettlementAdapter queryShareChargeMonthReportByCondition(
			String time, String username, Pageable pageable, Object object, String moblie, String status,Long tenantId);

	public OrderSettlementAdapter recommendChargeListMonthReport(String time,
			String username, Pageable pageable, Object object, String moblie, String status,Long tenantId);	
}
