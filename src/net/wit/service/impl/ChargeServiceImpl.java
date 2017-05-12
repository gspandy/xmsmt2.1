/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.BatchJobDao;
import net.wit.dao.BonusCalcDao;
import net.wit.dao.ChargeDao;
import net.wit.entity.BonusCalc;
import net.wit.entity.Charge;
import net.wit.entity.Charge.Status;
import net.wit.entity.Member;
import net.wit.entity.Owner;
import net.wit.entity.Tenant;
import net.wit.entity.TenantBonusSet;
import net.wit.entity.VipLevel;
import net.wit.mobile.service.impl.PushService;
import net.wit.mobile.util.ShortUUID;
import net.wit.mobile.util.rong.models.SystemMessage;
import net.wit.service.BonusCalcService;
import net.wit.service.ChargeService;
import net.wit.service.OrderSettlementService;
import net.wit.service.OwnerServerice;
import net.wit.service.TenantBonusSetService;
import net.wit.service.VipLevelService;
import net.wit.util.BizException;
import net.wit.util.DateUtil;
import net.wit.vo.OrderSettlementVO;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 */
@Service("chargeServiceImpl")
public class ChargeServiceImpl extends BaseServiceImpl<Charge, Long>implements ChargeService {
	
	private Logger log = LoggerFactory.getLogger(ChargeServiceImpl.class);
	
	@Resource(name = "chargeDaoImpl")
	private ChargeDao chargeDao;

	@Resource(name = "chargeDaoImpl")
	public void setBaseDao(ChargeDao chargeDao) {
		super.setBaseDao(chargeDao);
	}
	
	@Autowired
	private PushService pushService;
	
	@Autowired
	private OwnerServerice ownerServerice;
	
	@Autowired
	private BonusCalcService bonusCalcService;
	
	@Autowired
	private BatchJobDao batchJobDao;
	
	@Autowired
	private BonusCalcDao bonusCalcDao;
	
	@Autowired
	private OrderSettlementService orderSettlementService;
	
	@Autowired
	private TenantBonusSetService bonusSetService;
	
	@Autowired VipLevelService vipLevelService;
	
	public Page<Charge> findPage(Charge charge,Pageable pageable){
		if(charge == null){
			charge = new Charge();
		}
		return chargeDao.findPage(charge, charge.getMember(), charge.getTenant(), pageable);
	} 
	
	public Page<Charge> findPageNotReceive(Long memberId,Pageable pageable){
		List<Charge.Status> status = new ArrayList<Charge.Status>();
		status.add(Charge.Status.notReceive);
		status.add(Charge.Status.receiving);
		return chargeDao.findPageByParas(memberId, status, null, pageable);
	} 
	public List<Charge> findNotReceiveList(Long memberId){
		List<Charge.Status> status = new ArrayList<Charge.Status>();
		status.add(Charge.Status.notReceive);
		status.add(Charge.Status.receiving);
		return chargeDao.findChargeInfo(memberId, status);
	} 
	
	public Page<Charge> findPageReceived(Long memberId,Pageable pageable){
		List<Charge.Status> status = new ArrayList<Charge.Status>();
		status.add(Charge.Status.received);
		return chargeDao.findPageByParas(memberId, status, null, pageable);
	} 
	public List<Charge> findChargeInfo(Long memberId,Charge.Status status){
		List<Charge.Status> statusList = new ArrayList<Charge.Status>();
		statusList.add(status);
		return chargeDao.findChargeInfo(memberId, statusList);
	}
	
	@Transactional
	public void chargeCash(Long id)throws BizException{
		Charge charge = this.find(id);
		if(charge == null){
			throw new BizException("提现记录不存在");
		}
		String txNo = ShortUUID.genId();
		charge.setStatus(Charge.Status.receiving);
		charge.setTxNo(txNo);
		chargeDao.merge(charge);
	}
	@Transactional
	public void cashAgree(List<Long> list)throws BizException{
		if(list != null){
			for(Long id : list){
				Charge charge = this.find(id);
				if(charge == null){
					throw new BizException("提现记录不存在");
				}
				charge.setStatus(Charge.Status.received);
				chargeDao.merge(charge);
			}
		}
	}
	
	@Transactional
	public String cashAdviceBack(String result)throws BizException{
		//格式：流水号1^收款方帐号1^真实姓名^付款金额1^备注说明1|流水号2^收款方帐号2^真实姓名^付款金额2^备注说明2....
		List<Charge> list = null;
		Tenant tenant =new Tenant();
		Member member = new Member();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		String returnCode = "success";
		try {
			if(StringUtils.isNotBlank(result)){
				String[] resultDetail = result.split("\\|");
				for(int i = 0;i < resultDetail.length ; i++){
					String detail = resultDetail[i];
					String[] s = detail.split("\\^");
					String txNo = s[0];
					list = chargeDao.findChargeInfo(txNo);
					if(list != null){
						for(Charge c : list){
							c.setStatus(Charge.Status.received);
							this.update(c);
						}
						orderSettlementService.batchUpateOrderSettlementToRece(list);
					}
				}
			}
			
			return returnCode;
		} catch (Exception e) {
			log.error("提现回调服务异常============"+e.getMessage());
			returnCode = "fail";
			return returnCode;
		}finally{
			if("success".equals(returnCode) && list != null){
				for(Charge c : list){
					tenant = c.getTenant();
					member = c.getMember();
					try {
						Date date = sdf.parse(c.getChargeDate());
						String bonusDate = DateUtil.changeDateToStr(date, DateUtil.CN_DISPLAY_DATE_MONTH);
						if(c.getType().equals(Charge.Type.bonus)){
							pushService.publishSystemMessage(tenant, member, SystemMessage.shopKeeperBonusPaymentMsg(bonusDate, c.getCharge()+""));  //奖金发放消息推送
						}else{
							pushService.publishSystemMessage(tenant, member, SystemMessage.shopKeeperChargePaymentMsg(bonusDate, c.getCharge()+""));//佣金发放消息推送
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
					}
				}
			}
		}
		
	}
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
	public void orderSettleSubmit(OrderSettlementVO vo, Date date,
			TenantBonusSet set, Owner owner) throws Exception {
		String strDate = DateUtil.changeDateToStr(date,
				DateUtil.LINK_DISPLAY_DATE_MONTH);
		Long shopKeeperId = vo.getOwnerId().longValue();
		Integer tenantSellBonusRate = set.getTenantSellBonusRate();
		Integer relativeSellBonusRate = set.getRelativeSellBonusRate();
		Tenant tenant = new Tenant();   //订单所属企业
		tenant.setId(vo.getTenant().longValue());
		Member ownerMember = new Member(); // 本店店主
		ownerMember.setId(vo.getOwnerId().longValue());
		Member recommendMember = new Member(); // 推荐店主;
		recommendMember.setId(vo.getRecommandId().longValue());
		BigDecimal settleCharge = vo.getSettleCharge();   //结算佣金
		BigDecimal orderSettleAmt = vo.getOrderSettleAmt();  //结算订单金额
		// 佣金计算
		if (settleCharge.compareTo(BigDecimal.ZERO) > 0) {
			Charge charge = new Charge();
			charge.setCharge(settleCharge);
			charge.setChargeDate(strDate);
			charge.setMember(ownerMember);
			charge.setTenant(tenant);
			charge.setStatus(Status.notReceive);
			charge.setType(Charge.Type.charge);
			this.save(charge);
			owner.setTotalCharge(owner.getTotalCharge().add(settleCharge)); // 佣金加总
			owner.setTotalAmt(owner.getTotalAmt().add(settleCharge));  //总收入加总
			batchJobDao.batchUpateChargeId(charge.getId(), shopKeeperId, date);
			ownerServerice.update(owner);
		}

		//奖金计算
		if (orderSettleAmt.compareTo(BigDecimal.ZERO) > 0) {
			if (tenantSellBonusRate != null && tenantSellBonusRate > 0) {  //本店奖金计算
				
				BigDecimal tenantSellBonus = bonusSetService.calcBonus(orderSettleAmt, set);
				BonusCalc tenantSellBc = new BonusCalc();
				tenantSellBc.setBonus(tenantSellBonus);  //设置奖金金额
				tenantSellBc.setTenant(tenant);  //设置所属企业
				tenantSellBc.setStatus(BonusCalc.Status.notSettle); //设置该奖金未加总
				tenantSellBc.setBonusTime(date);  //设置该奖金结算时间
				tenantSellBc.setMember(ownerMember);   //设置奖金归属人
				tenantSellBc.setType(BonusCalc.Type.owner);//本店奖金
				bonusCalcService.save(tenantSellBc);
			}
			if (relativeSellBonusRate != null && relativeSellBonusRate > 0) {  //推荐人奖金计算
				VipLevel vipLevel = vipLevelService.getVipLevelByMemeber(recommendMember);
				if(vipLevel!=null){
//					BigDecimal relativeSellBonus =bonusSetService.calcReBonus(orderSettleAmt, set);
					BigDecimal relativeSellBonus =bonusSetService.calcReBonus(orderSettleAmt, vipLevel);
					BonusCalc reTenantSellBc = new BonusCalc();
					reTenantSellBc.setBonus(relativeSellBonus);  //设置奖金金额
					reTenantSellBc.setTenant(tenant);  //设置所属企业
					reTenantSellBc.setStatus(BonusCalc.Status.notSettle); //设置该奖金未加总
					reTenantSellBc.setBonusTime(date);  //设置该奖金结算时间
					reTenantSellBc.setMember(recommendMember);   //设置奖金归属人 设置为推荐的奖金
					reTenantSellBc.setBeRecommend(ownerMember); //被推荐人为本店店主
					reTenantSellBc.setType(BonusCalc.Type.relateive);//本店奖金
					reTenantSellBc.setBonusPercent(vipLevel.getBonusLevel());
					reTenantSellBc.setVipLevelName(vipLevel.getLevelName());
					bonusCalcService.save(reTenantSellBc);
				}
			}
		}
	}
	public BigDecimal sumReceived(Long memberId) {
		return chargeDao.sumReceived(memberId);
	}
	
	public boolean updateTicketInvalid(Member shopkeeper){
		return chargeDao.updateTicketInvalid(shopkeeper);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
	public void submitBonusCharge(Owner owner  , Charge charge ,List<BonusCalc> boncalcList ){
		ownerServerice.update(owner);
		this.save(charge);
		for (BonusCalc bonusCalc : boncalcList) {
			bonusCalc.setChargeId(charge.getId());
			bonusCalc.setStatus(BonusCalc.Status.notReceive);
			bonusCalcService.update(bonusCalc);
		}
	}

}