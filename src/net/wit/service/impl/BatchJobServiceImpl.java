package net.wit.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.LockModeType;

import net.wit.dao.BatchJobDao;
import net.wit.dao.ChargeDao;
import net.wit.dao.OrderSettlementDao;
import net.wit.dao.OwnerDao;
import net.wit.dao.OwnerIncomeDao;
import net.wit.dao.RentDao;
import net.wit.entity.Admin;
import net.wit.entity.BonusCalc;
import net.wit.entity.Charge;
import net.wit.entity.Charge.Status;
import net.wit.entity.Member;
import net.wit.entity.Order;
import net.wit.entity.OrderSettlement;
import net.wit.entity.Owner;
import net.wit.entity.OwnerIncome;
import net.wit.entity.Rent;
import net.wit.entity.Returns;
import net.wit.entity.Tenant;
import net.wit.entity.TenantBonusSet;
import net.wit.entity.TenantShopkeeper;
import net.wit.entity.TicketSet;
import net.wit.mobile.cache.CacheUtil;
import net.wit.service.BatchJobService;
import net.wit.service.BonusCalcService;
import net.wit.service.ChargeService;
import net.wit.service.OrderService;
import net.wit.service.OrderSettlementService;
import net.wit.service.OwnerServerice;
import net.wit.service.RentService;
import net.wit.service.ReturnsService;
import net.wit.service.TenantBonusSetService;
import net.wit.service.TenantService;
import net.wit.service.TenantShopkeeperService;
import net.wit.service.TicketCacheService;
import net.wit.service.TicketSetService;
import net.wit.util.BizException;
import net.wit.util.BussConst;
import net.wit.util.Constants;
import net.wit.util.DateUtil;
import net.wit.util.TicketUtil;
import net.wit.vo.OrderSettlementVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("batchJobImpl")
public class BatchJobServiceImpl implements BatchJobService{
	private Logger log = LoggerFactory.getLogger(BatchJobServiceImpl.class);
	
	@Autowired
	private OrderSettlementDao orderSettlementDao;
	
	@Autowired
	private OrderSettlementService orderSettlementService;
	
	@Autowired
	private TicketSetService ticketSetService;
	
	@Autowired
	private TicketCacheService ticketCacheService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private BatchJobDao batchJobDao;
	
	@Autowired
	private OwnerServerice ownerServerice;
	
	@Autowired
	private OwnerIncomeDao ownerIncomeDao;
	
	@Autowired
	private OwnerDao ownerDao;
	
	@Autowired
	private BonusCalcService bonusCalcService;
	
	@Autowired
	private TenantBonusSetService tenantBonusSetService;
	
	@Autowired
	private TenantShopkeeperService tenantShopkeeperService;
	
	@Autowired
	private ChargeDao chargeDao;
	
	@Autowired
	private RentDao rentDao;
	
	@Autowired
	private ChargeService chargeService;
	
	@Autowired
	private RentService rentService;
	
	@Autowired
	private ReturnsService returnsService;
	
	@Autowired
	private TenantService tenantService;
	
	
	@Transactional
	public void createCashInfo(OrderSettlementVO vo,Date date)throws Exception{
		
		Long ownerId = vo.getOwnerId().longValue();
		TenantShopkeeper tenantShopkeeper = tenantShopkeeperService.getTenantByShopKeeper(ownerId);
		Long tenantId = tenantShopkeeper.getTenant().getId();
		Charge charge = new Charge();
		Member member = new Member();
		Tenant tenant = new Tenant();
		String strDate = DateUtil.changeDateToStr(date, DateUtil.LINK_DISPLAY_DATE_MONTH);
		charge.setCharge(vo.getSettleCharge());
		charge.setChargeDate(strDate);
//		charge.setCreateDate(createDate);
		member.setId(vo.getOwnerId().longValue());
		charge.setMember(member);
//		charge.setModifyDate(modifyDate);
		charge.setStatus(Status.notReceive);
		tenant.setId(tenantId);
		charge.setTenant(tenant);
		chargeDao.persist(charge);
		
		batchJobDao.batchUpateChargeId(charge.getId(), ownerId, date);
		
	}
	
	
	@Transactional
	public void updateOwner(List<OrderSettlement> list,Date date){
		Long ownerId = list.get(0).getOwner().getId();
		BigDecimal total = BigDecimal.ZERO;
		for(OrderSettlement os : list){
			OwnerIncome entity = new OwnerIncome();
			total = total.add(os.getSettleCharge());
			entity.setContent(date+"分享佣金收入");
			entity.setIncomeAmt(os.getSettleCharge());
			entity.setIncomeDate(new Date());
			entity.setIncomeType(BussConst.INCOME_TYPE_CHARGE);
			entity.setMemberId(os.getOwner().getId());
			entity.setOrderId(os.getOrder().getId());
			ownerIncomeDao.persist(entity);
		}
		
		//分享佣金收入
		List<Owner> ownerList = ownerDao.getOwnerByMemberId(ownerId);
		Owner owner = ownerList.get(0);
		ownerDao.lock(owner, LockModeType.PESSIMISTIC_WRITE);
		owner.setTotalCharge(total);
		//租金收取
		String beginRent = CacheUtil.getParamValueByName(BussConst.PARAM_BEGIN_RENT);
		BigDecimal rent = new BigDecimal(beginRent);
		if(total.compareTo(rent) >= 0){
			owner.setTotalRent(rent);
			
			OwnerIncome entity = new OwnerIncome();
			entity.setContent(date+"租金收取");
			entity.setIncomeAmt(rent);
			entity.setIncomeDate(new Date());
			entity.setIncomeType(BussConst.INCOME_TYPE_RENT);
			entity.setMemberId(ownerId);
			ownerIncomeDao.persist(entity);
		}
		owner.setAccountBalance(owner.getAccountBalance().add(total).subtract(rent));
		
		ownerDao.merge(owner);
		
	}
	
	public void calculateRent(Date batchDate){
		if(log.isInfoEnabled()) log.info("租金生成,当前跑批日期为:"+batchDate);
//		Integer freePeriod = Integer.valueOf(CacheUtil.getParamValueByName(BussConst.PARAM_SHOPKEEPER_FREE_PERIOD));
//		Calendar cal=Calendar.getInstance();
		//收租月
		String rentDate = DateUtil.changeDateToStr(DateUtil.addMonth(batchDate, -1), DateUtil.LINK_DISPLAY_DATE_MONTH);//起租月
		String beginRent = CacheUtil.getParamValueByName(BussConst.PARAM_BEGIN_RENT);
		BigDecimal rent = new BigDecimal(beginRent);//租金
		Rent.Status status = Rent.Status.notCharge;
		if(rent.compareTo(BigDecimal.ZERO) == 0){
			status = Rent.Status.system;
		}
		List<TenantShopkeeper> list = batchJobDao.findShopKeeper(batchDate);
		
		if(list != null){
			for(TenantShopkeeper ts : list){
				try {
					//判断是否在试用期
//					Date startDate = DateUtil.addMonth(ts.getOpenDate(), freePeriod);//起租月
//					cal.setTime(startDate);
//					if(batchDate.before(DateUtil.setLastDayOfMonth(cal)))//跑批日期早于起租月最后一天不收租
//						continue;
					
					if(log.isDebugEnabled())log.debug("收租轮循==========店主id："+ts.getMember().getId());
					//生成租金记录
					Rent entity = new Rent();
					Member member = new Member();
					Tenant tenant = new Tenant();
					member.setId(ts.getMember().getId());
					entity.setMember(member);
					entity.setRent(rent);
					entity.setRentDate(rentDate);
					entity.setStatus(status);
					tenant.setId(ts.getTenant().getId());
					entity.setTenant(tenant);
					rentService.save(entity);
				} catch (Exception e) {
					log.error("租金生成异常：===店主Id:"+ts.getMember().getId()+e.getMessage());
				}
			}
		}
	}

//	public void batchSendTicket(Date batchDate){
//		if(log.isInfoEnabled()) log.info("内购券发放批,当前跑批日期为:"
//				+DateUtil.changeDateToStr(batchDate, DateUtil.LINK_DISPLAY_DATE_FULL));
//		//获取券发放设置
//		List<TicketSet> list = ticketSetService.getTicketSet(null, 
//				TicketSet.SYSTEM_SEND_TYPE, Constants.YES_FLAG);
//		if(list != null){
//			for(TicketSet set : list){
//				try {
//					ticketCacheService.batchGenTicketCacheByTicketSet(set);
//				} catch (Exception e) {
//					log.error("内购券发放批异常：===TenantId:"+set.getTenantId()+e.getMessage());
//				}
//			}
//		}
//		
//	}
	
	
	public void batchSendTicket(Date batchDate){
		if(log.isInfoEnabled()) log.info("内购券发放批,当前跑批日期为:"
				+DateUtil.changeDateToStr(batchDate, DateUtil.LINK_DISPLAY_DATE_FULL));
		//获取券发放设置
		List<Tenant> list = tenantService.findAll();
		if(list != null){
			for(Tenant tenant : list){
				try {
					ticketCacheService.batchGenTicketCacheByTicketSet(tenant);
				} catch (Exception e) {
					log.error("内购券发放批异常：===TenantId:"+tenant.getId()+e.getMessage());
				}
			}
		}
		
	}
	@Transactional
	public void releaseTicket(Date batchDate){
		if(log.isInfoEnabled()) log.info("内购券发放失效处理,当前跑批日期为:"+batchDate);
		try {
			batchJobDao.batchUpateTicketCache(batchDate);
		} catch (Exception e) {
			log.error("内购券发放失效处理批异常：==="+e.getMessage());
		}
		if(log.isInfoEnabled()) log.info("内购券失效处理,当前跑批日期为:"+batchDate);
		try {
			batchJobDao.batchUpateTicket(batchDate);
		} catch (Exception e) {
			log.error("内购券发放失效处理：==="+e.getMessage());
		}
	}
	
	/*
	@Transactional
	public void orderSettle(Date batchDate){
		if(log.isInfoEnabled()) log.info("订单结算批处理,当前跑批日期为:"+batchDate);
		try {
			//跑批月份：跑上个月 ，结算上个月已完成订单
			 Calendar c = Calendar.getInstance();
			 c.add(Calendar.MONTH, -1);//获取上个月时间
			
			 List<OrderSettlementVO> list = batchJobDao.getSettlementCharge(c.getTime());
			 Map<Long,TenantBonusSet> map = new HashMap<Long,TenantBonusSet>();
			 Map<Long,List<BonusCalc>> calcMap = new HashMap<Long,List<BonusCalc>>();
			 Map<Long,Long> exceptionMap = new HashMap<Long, Long>();
			 Map<Long,Long> doneMap = new HashMap<Long, Long>();
			 if(list != null){
				 for(OrderSettlementVO vo : list){
					Long shopKeeperId = vo.getOwnerId().longValue();
					Long tenantId = vo.getTenant().longValue();
					try {
//						TenantShopkeeper tenantShopkeeper = tenantShopkeeperService.getTenantByShopKeeper(shopKeeperId);
//						Long tenantId = tenantShopkeeper.getTenant().getId();
						TenantBonusSet tenantBonusSet = map.get(tenantId);
						if(tenantBonusSet == null){
							tenantBonusSet = tenantBonusSetService.getRegularTenantBonusSetByTenantId(tenantId);
							if(tenantBonusSet == null){
								log.error("企业奖金参数未设置========"+tenantId);
								throw new BizException("企业奖金参数未设置");
							}
							map.put(tenantId, tenantBonusSet);
						}
						BigDecimal relateiveRate = new BigDecimal(tenantBonusSet.getRelativeSellBonusRate());
						BigDecimal sellRate = new BigDecimal(tenantBonusSet.getTenantSellBonusRate());
						if(vo.getOrderSettleAmt().compareTo(BigDecimal.ZERO) > 0){//结算金额大于0
							if( sellRate.compareTo(BigDecimal.ZERO) > 0){//店主奖金
								BonusCalc entity = new BonusCalc();
								Member member = new Member();
								Tenant tenant = new Tenant();
								entity.setBonus(vo.getOrderSettleAmt().divide(sellRate));
								entity.setBonusTime(c.getTime());//TODO
								member.setId(shopKeeperId);
								entity.setMember(member);
								tenant.setId(tenantId);
								entity.setTenant(tenant);
								entity.setType(BonusCalc.Type.owner);
								entity.setStatus(BonusCalc.Status.notReceive);

								if(calcMap.get(shopKeeperId) != null){
									calcMap.get(shopKeeperId).add(entity);
								}else{
									List<BonusCalc> calcList = new ArrayList<BonusCalc>();
									calcList.add(entity);
									calcMap.put(shopKeeperId, calcList);
								}
							}
							
							if(vo.getRecommandId() != null){
//								if(tenantShopkeeper.getRecommendMember() != null
//										&& tenantShopkeeper.getRecommendMember().getId() != null){
								if(relateiveRate.compareTo(BigDecimal.ZERO) > 0){//推荐人奖金
//									Long recommendMemberId = tenantShopkeeper.getRecommendMember().getId();
									Long recommendMemberId = vo.getRecommandId().longValue();
//									TenantShopkeeper relateive = tenantShopkeeperService.getTenantByShopKeeper(tenantShopkeeper.getRecommendMember().getId());
									BonusCalc relateiveEntity = new BonusCalc();
									Member relMember = new Member();
									Member member = new Member();
									Tenant relTenant = new Tenant();
									relateiveEntity.setBonus(vo.getOrderSettleAmt().divide(relateiveRate));
									relateiveEntity.setBonusTime(c.getTime());//TODO
									member.setId(recommendMemberId);
//									relateiveEntity.setMember(tenantShopkeeper.getRecommendMember());
									relateiveEntity.setMember(member);
									
									relMember.setId(shopKeeperId);
									relateiveEntity.setBeRecommend(relMember);
									
									relTenant.setId(tenantId);
									relateiveEntity.setTenant(relTenant);
									relateiveEntity.setType(BonusCalc.Type.relateive);

									if(calcMap.get(recommendMemberId) != null){
										calcMap.get(recommendMemberId).add(relateiveEntity);
									}else{
										List<BonusCalc> calcList = new ArrayList<BonusCalc>();
										calcList.add(relateiveEntity);
										calcMap.put(recommendMemberId, calcList);
									}
								}
							}
						}
						
					} catch (Exception e) {
						exceptionMap.put(shopKeeperId, shopKeeperId);
						log.error("奖金封装异常==========店主id："+shopKeeperId+"======="+e.getMessage());
					}
				
				 }
				 for(OrderSettlementVO vo : list){
					 try {
						 Long shopKeeperId = vo.getOwnerId().longValue();
						 
						 if(exceptionMap.get(shopKeeperId) != null 
								 || doneMap.get(shopKeeperId) != null ){//封装有异常不处理
							 continue;
						 }
						 
						 doneMap.put(shopKeeperId, shopKeeperId);
						 List<BonusCalc> bonusCalcList = calcMap.get(shopKeeperId);
						 chargeService.orderSettleSubmit(vo, bonusCalcList, c.getTime());
						 if(vo.getRecommandId() != null){
							 Long recommandId = vo.getRecommandId().longValue();
							 if(exceptionMap.get(recommandId) != null
									 || doneMap.get(recommandId) != null ){//封装有异常不处理
								 continue;
							 }
							 List<BonusCalc> recommandBclcList = calcMap.get(recommandId);
							 chargeService.orderSettleSubmit(vo, recommandBclcList, c.getTime());
							 doneMap.put(recommandId, recommandId);
						 }
						 
					} catch (Exception e) {
						log.error("结算轮循处理异常：=========店主Id:"+vo.getMemberId()+e.getMessage());
					}
					
				 }
			 }
		} catch (Exception e) {
			log.error("订单结算失败=========："+e.getMessage());
		}
		if(log.isInfoEnabled()) log.info("订单结算批处理完成===============");
	}
	
	
	*/
	@Transactional
	public void orderSettle(Date batchDate){
		if(log.isInfoEnabled()) log.info("订单结算批处理,当前跑批日期为:"+batchDate);
		try {
			//跑批月份：跑上个月 ，结算上个月已完成订单
			 Calendar c = Calendar.getInstance();
			 c.add(Calendar.MONTH, -1);//获取上个月时间
			 List<OrderSettlementVO> list = batchJobDao.getSettlementCharge(c.getTime());
			 Map<Long,TenantBonusSet> tenantBonusSetMap = new HashMap<Long,TenantBonusSet>();
			 if(list != null && list.size()>0){
				 for(OrderSettlementVO vo : list){
					Long tenantId = vo.getTenant().longValue();
						TenantBonusSet tenantBonusSet = tenantBonusSetMap.get(tenantId);
						if(tenantBonusSet == null){
							tenantBonusSet = tenantBonusSetService.getRegularTenantBonusSetByTenantId(tenantId);
							if(tenantBonusSet == null){
								log.error("企业奖金参数未设置========"+tenantId);
								throw new BizException("企业奖金参数未设置");
							}
							tenantBonusSetMap.put(tenantId, tenantBonusSet);
						}
				 }
				 for(OrderSettlementVO vo : list){
					 try {
						 Long ownerId = vo.getOwnerId().longValue();
						 Owner owner = ownerServerice.getOwner(ownerId);  //查询账户信息
						 if(owner == null){  //如果为空 
							 owner = new Owner(ownerId);
						 }
						 chargeService.orderSettleSubmit(vo,c.getTime(),tenantBonusSetMap.get(vo.getTenant().longValue()),owner);
					} catch (Exception e) {
						log.error("结算轮循处理异常：=========店主Id:"+vo.getMemberId()+e.getMessage());
					}
				 }
			 }
		} catch (Exception e) {
			log.error("订单结算失败=========："+e.getMessage());
		}
		if(log.isInfoEnabled()) log.info("订单结算批处理完成===============");
	}
	
	
	
	
	
	
	public void orderExpire(Date date){
		if(log.isInfoEnabled())log.info("订单支付过期处理，当前跑批时间：========"
				+DateUtil.changeDateToStr(date, DateUtil.LINK_DISPLAY_DATE_FULL));
		List<Order> list = batchJobDao.findExpireOrder(date);
		Admin operator = new Admin();
		operator.setUsername("auto");
		for(Order order : list){
			try {
				//订单取消
				orderService.cancel(order, operator);
			} catch (Exception e) {
				log.error("订单支付过期处理失败=========订单号："+order.getId()+"======="+e.getMessage());
			}
		}
		
	}
	
	public void orderComplete(Date date){
		if(log.isInfoEnabled()) log.info("订单完成批处理开始,当前跑批日期为:=========="
				+DateUtil.changeDateToStr(date, DateUtil.LINK_DISPLAY_DATE_FULL));
		Date dueDate = DateUtil.addDay(date, 
				-Integer.parseInt(CacheUtil.getParamValueByName(BussConst.PARAM_FORCE_RETURN_DATE)));
		List<Order> list = batchJobDao.findOrderAccepted(dueDate);
		Admin operator = new Admin();
		operator.setUsername("auto");
		for(Order order : list){
			try {
				//订单完成
				orderService.complete(order, operator);
			} catch (Exception e) {
				log.error("订单完成处理失败=========订单号："+order.getId()+"======="+e.getMessage());
			}
		}
	}
	public void returnCancel(Date date){
		if(log.isInfoEnabled()) log.info("订单取消退货批处理开始,当前跑批日期为:=========="
				+DateUtil.changeDateToStr(date, DateUtil.LINK_DISPLAY_DATE_FULL));
		//退货过期时间
		Date dueDate = DateUtil.addDay(date, 
				-Integer.parseInt(CacheUtil.getParamValueByName(BussConst.PARAM_RETURN_APP_DATE)));
		List<Order> list = batchJobDao.findOrderReturn(dueDate);
		Admin operator = new Admin();
		operator.setUsername("auto");
		
		for(Order order : list){
			try {
				Set<Returns> set  = order.getReturns();
				if(set.size()>0){
					for (Returns returns : set) {
						returnsService.reject(returns);
					}
				}
				//订单完成
				orderService.complete(order, operator);
			} catch (Exception e) {
				log.error("订单取消退货失败=========订单号："+order.getId()+"======="+e.getMessage());
			}
		}
	}
	public void orderAccept(Date date){
		if(log.isInfoEnabled()) log.info("订单自动收货批处理开始,当前跑批日期为:=========="
				+DateUtil.changeDateToStr(date, DateUtil.LINK_DISPLAY_DATE_FULL));
		int day = Integer.parseInt(CacheUtil.getParamValueByName(BussConst.PARAM_AUTO_RECEIPT_DATE));
		Date dueDate = DateUtil.addDay(date,-day);
		List<Order> list = batchJobDao.findOrderShipped(dueDate);
		for(Order order : list){
			try {
				orderService.sign(order);
			} catch (Exception e) {
				log.error("订单自动收货失败=========订单号："+order.getId()+"======="+e.getMessage());
			}
		}
		//延长收货自动收货
		day = day + Integer.parseInt(CacheUtil.getParamValueByName(BussConst.PARAM_RECEIPT_DELAY));
		dueDate = DateUtil.addDay(dueDate,-day);
		List<Order> extendlist = batchJobDao.findOrderShippedExtend(dueDate);
		for(Order order : extendlist){
			try {
				orderService.sign(order);
			} catch (Exception e) {
				log.error("订单自动收货失败=========订单号："+order.getId()+"======="+e.getMessage());
			}
		}
	}
	
	public List<Order> findOrderAccepted(Date dueDate){
		return batchJobDao.findOrderAccepted(dueDate);
	}
	
}
