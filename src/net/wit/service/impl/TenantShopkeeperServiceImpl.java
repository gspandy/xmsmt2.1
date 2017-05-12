/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import jodd.jtx.meta.Transaction;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.TenantShopkeeperDao;
import net.wit.entity.*;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.TenantShopkeeper.InvitedType;
import net.wit.entity.TenantShopkeeper.IsShopkeeper;
import net.wit.mobile.service.impl.PushService;
import net.wit.mobile.util.rong.models.SystemMessage;
import net.wit.service.MemberService;
import net.wit.service.MemberTenantService;
import net.wit.service.OrderService;
import net.wit.service.OwnerServerice;
import net.wit.service.RentService;
import net.wit.service.TenantSellConditionService;
import net.wit.service.TenantShopkeeperService;
import net.wit.service.TicketCacheService;
import net.wit.service.TicketSetService;
import net.wit.service.VipLevelService;
import net.wit.util.BizException;
import net.wit.util.DateUtil;
import net.wit.vo.SystemMessageVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sun.nio.cs.ext.Big5;

/**
 * Service - 管理员
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("tenantShopkeeperServiceImpl")
public class TenantShopkeeperServiceImpl extends
		BaseServiceImpl<TenantShopkeeper, Long> implements
		TenantShopkeeperService {

	private Logger log = LoggerFactory.getLogger(TenantShopkeeperServiceImpl.class);
	
	private static final String NOTQUALIFIEDEXCEPTION = "该用户暂无资格申请为该企业店长,请完成一笔订单吧";

	private static final String ALREADYSHOPKEEPEREXCEPTION = "只能成为一家内购店的店长哟!";
	
	@Autowired
	private PushService pushService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private TenantSellConditionService tenantSellCondition;

	@Autowired
	private MemberTenantService memberTenantService;
	
	@Autowired
	private VipLevelService vipLevelService;
	
	@Autowired
	private OwnerServerice ownerServerice;
	
	@Autowired
	private RentService rentService;

	@Resource(name = "tenantShopkeeperDaoImpl")
	private TenantShopkeeperDao tenantShopkeeperDao;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "ticketSetServiceImpl")
	private TicketSetService ticketSetService;

	@Resource(name = "ticketCacheServiceImpl")
	private TicketCacheService ticketCacheService;

	@Resource(name = "tenantShopkeeperDaoImpl")
	public void setBaseDao(TenantShopkeeperDao tenantShopkeeperDaoImpl) {
		super.setBaseDao(tenantShopkeeperDaoImpl);
	}
	
	@Override
	public void beCapableShopkeeper(Tenant tenant,Member member) throws BizException{
		List<TenantShopkeeper> ts = tenantShopkeeperDao.findTenantShopkeeper(tenant,member,null,null,null);
		boolean isShopKeeper = false;
		for (TenantShopkeeper tenantShopkeeper : ts) {
			if(tenantShopkeeper.getIsShopkeeper().ordinal()==1) isShopKeeper = true;
		}
		if(isShopKeeper) {
			return;
		}else{
			TenantSellCondition  tenantSellConditon  = tenantSellCondition.getRegularTenantSellConditionByTenantId(tenant.getId());
			if(tenantSellConditon==null) return ;
			else{
				VipLevel defaultVipLevel = vipLevelService.getDefaultVipLevel(tenant);
				int tradeNum = tenantSellConditon.getTradeNum();
				if(tradeNum<=0) return;
				List<OrderStatus> orderStatuses= new ArrayList<OrderStatus>();
				orderStatuses.add(OrderStatus.confirmed);
				orderStatuses.add(OrderStatus.completed);
				List<PaymentStatus> paymentStatuses= new ArrayList<PaymentStatus>();
				paymentStatuses.add(PaymentStatus.paid);
				List<Order> list = orderService.getHistoryOrderByTenant(tenant, member, orderStatuses, paymentStatuses);
				BigDecimal orderAmt = BigDecimal.ZERO;
				BigDecimal orderAmtCondition = new BigDecimal(tradeNum);
				for (Order order : list) {
					orderAmt = orderAmt.add(order.getAmountPaid());
				}
				if(orderAmt.compareTo(orderAmtCondition)>=0){
					HashSet<Long> set = new HashSet<Long>();
					for (Order order : list) {
						TenantShopkeeper tenantShopkeeper;
						Member owner = order.getOwner();
						if(set.contains(owner.getId()))continue;
						else set.add(owner.getId());
						if(owner.getId().equals(member.getId())){//用自己券 发现中的券;
							tenantShopkeeper = new TenantShopkeeper(tenant,member,IsShopkeeper.canApply,defaultVipLevel);  //有资格申请的成为店主
						}else{
							tenantShopkeeper = new TenantShopkeeper(tenant,member,owner,IsShopkeeper.capable,defaultVipLevel);  //有资格的被邀请成为店主；
						}
						if(!isExistInvitation(tenantShopkeeper,ts))	this.save(tenantShopkeeper);
					}
				}else return ;
			}
		}
	}
	//判断是否已经存在一样的邀请函
	private Boolean isExistInvitation(TenantShopkeeper ts ,List<TenantShopkeeper> list){
		Boolean returnValue = false;
		for (TenantShopkeeper tenantShopkeeper : list) {
				if(ts.getTenant().getId().equals(tenantShopkeeper.getTenant().getId())){
					if(ts.getMember().getId().equals(tenantShopkeeper.getMember().getId())){
						if(ts.getIsShopkeeper().equals(tenantShopkeeper.getIsShopkeeper())){
							if(ts.getRecommendMember()==null && tenantShopkeeper.getRecommendMember()==null 
									&& ts.getIsShopkeeper().equals(TenantShopkeeper.IsShopkeeper.canApply
											)&&tenantShopkeeper.getIsShopkeeper().equals(TenantShopkeeper.IsShopkeeper.canApply)) return true;
							else if(ts.getRecommendMember().getId().equals(tenantShopkeeper.getRecommendMember().getId())) return true;
						}
					}
				}
		}
		return returnValue;
	}
	
	@Override
	public TenantShopkeeper getShopkeeperByTenantAndMember(Tenant tenant, Member member){
		List<TenantShopkeeper> list = this.tenantShopkeeperDao.findTenantShopkeeper(tenant, member, null, IsShopkeeper.yes, null);
		if(list.size()==0)return null;
		else return list.get(0);
	}
	
	@Override
	public List<TenantShopkeeper> getCanApplyByMember(Tenant tenant,Member member){
		return this.tenantShopkeeperDao.findTenantShopkeeper(tenant, member, null, IsShopkeeper.canApply, InvitedType.tenant);
	}
	@Override
	public List<TenantShopkeeper> getCapableByRecommand(Tenant tenant,Member recommand){
		return this.tenantShopkeeperDao.findTenantShopkeeper(tenant, null, recommand, IsShopkeeper.capable, InvitedType.shopkeeper);
	}
	
	@Override
	public List<TenantShopkeeper> getInvitedByRecommand(Tenant tenant,Member recommand){
		return this.tenantShopkeeperDao.findTenantShopkeeper(tenant, null, recommand, IsShopkeeper.no, InvitedType.shopkeeper);
	}
	
	@Override
	public List<TenantShopkeeper> getTenantCapableByMember(Tenant tenant,Member member){
		return this.tenantShopkeeperDao.findTenantShopkeeper(tenant, member, null, IsShopkeeper.capable, InvitedType.tenant);
	}
	@Override
	public List<TenantShopkeeper> getCapableByMember(Tenant tenant,Member member){
		return this.tenantShopkeeperDao.findTenantShopkeeper(tenant, member, null, IsShopkeeper.capable, InvitedType.shopkeeper);
	}
	
	@Override
	public List<TenantShopkeeper> getInvationByTenantMember(Tenant tenant,Member member){
		return this.tenantShopkeeperDao.findTenantShopkeeper(tenant, member, null, IsShopkeeper.no, InvitedType.shopkeeper);
	}
	
	@Override
	public List<TenantShopkeeper> getInvationByMember(Member member){
		return this.tenantShopkeeperDao.findTenantShopkeeper(null, member, null, IsShopkeeper.no, InvitedType.shopkeeper);
	}
	@Override
	public Integer getInvationsByRecommand(Member recommand){
		Long id = recommand.getId();
		Long long1= this.tenantShopkeeperDao.getInvationsCount(id);
		return Integer.valueOf(String.valueOf(long1));
	}
	

	/**
	 * 店主邀请好友开通店长 /或者通过店主的内购券购买具备成为店主资格
	 * 
	 * @param tenantId
	 * @param memberId
	 * @param recommandMemberId
	 * @return
	 * @throws BizException 
	 */
	public Long inviteBeShopkeeper(Long tenantId, Long memberId,
			Long recommandMemberId) throws BizException {
		TenantShopkeeper ts = tenantShopkeeperDao
				.findByTenantIdAndMemberAndRecommand(tenantId, memberId,
						recommandMemberId);
		if (ts != null)
			return ts.getId(); // 已经邀请
		Tenant tenant = new Tenant();
		tenant.setId(tenantId);
		VipLevel defaultVipLevel = vipLevelService.getDefaultVipLevel(tenant);
		Member member = new Member();
		member.setId(memberId);
		Member recommandMember = new Member();
		TenantShopkeeper tenantShopkeeper;
		if(memberId!=recommandMemberId){
			recommandMember.setId(recommandMemberId);
			tenantShopkeeper = new TenantShopkeeper(tenant,
					member, recommandMember,IsShopkeeper.no,defaultVipLevel);
			this.save(tenantShopkeeper);
			return tenantShopkeeper.getId();
		}else{
			return this.inviteBeShopkeeper(tenantId,memberId);
		}
	}

	/**
	 * 企业邀请开通店长
	 * 
	 * @param tenantId
	 * @param memberId
	 * @return
	 * @throws BizException 
	 */
	public Long inviteBeShopkeeper(Long tenantId, Long memberId) throws BizException {
		Member member = new Member();
		member.setId(memberId);
		List<TenantShopkeeper> ts = tenantShopkeeperDao.findTenantShopkeeper(null, member, null, IsShopkeeper.yes, null);
		if(ts.size()>0) throw new BizException("您已经是内购店店长啦!");
		TenantShopkeeper tenantShopkeeper = new TenantShopkeeper();
		Tenant tenant = new Tenant();
		tenant.setId(tenantId);
		VipLevel defaultVipLevel = vipLevelService.getDefaultVipLevel(tenant);
		tenantShopkeeper.setTenant(tenant);
		this.save(new TenantShopkeeper(tenant, member,IsShopkeeper.no,defaultVipLevel));
		memberTenantService.addMemberTenant(member, tenantId);
		return tenantShopkeeper.getId();
	}

	/**
	 * 成为店主 无推荐人
	 * 
	 * @param tenantId
	 * @param memberId
	 * @return
	 * @throws BizException
	 */
	public void beShopKeeper(Long tenantId, Member member) throws BizException {
		this.beShopKeeper(tenantId, member, null);
	}

	/**
	 * 成为店主有推荐人
	 * 
	 * @param tenantId
	 * @param memberId
	 * @param recommandId
	 * @throws BizException
	 */
	@Transactional
	public void beShopKeeper(Long tenantId, Member member, Long recommandId)
			throws BizException {
		// 判断是不是已经成为店主
		Long memberId = member.getId();
		Member recommandMember =null;
		boolean vipUpdate=false;
		VipLevel updateVipLevel = null;
		Integer invitedNum =null;
		List<TenantShopkeeper> list = tenantShopkeeperDao
				.findByMemeberIdAndIsShopkeeper(memberId,
						TenantShopkeeper.IsShopkeeper.yes);
		if (list.size() == 0) { // 还不是店主
			TenantShopkeeper tenantShopkeeper = tenantShopkeeperDao
					.findByTenantIdAndMemberAndRecommand(tenantId, memberId,
							recommandId);
			if (tenantShopkeeper == null)
				throw new BizException(NOTQUALIFIEDEXCEPTION); // 无资格成为该企业的店主;
			else {
				tenantShopkeeper
						.setIsShopkeeper(TenantShopkeeper.IsShopkeeper.yes);
				tenantShopkeeper.setOpenDate(new Date());
				// Member memberBo = memberService.find(memberId);
				// CacheUtil.getParamValueByName("TOKEN_VALUE")
				if (recommandId != null) {  //有推荐人
					recommandMember= new Member();
					recommandMember.setId(recommandId);
					TenantShopkeeper recmmandShopKeeper = this.getTenantByShopKeeper(recommandId);
					Long count= this.tenantShopkeeperDao.getInvationsCount(recommandId);
					invitedNum = Integer.valueOf(count.toString())+1;
					VipLevel currentVipLevel = recmmandShopKeeper.getVipLevel();
					updateVipLevel = this.vipLevelService.checkIsUpdateLevel(currentVipLevel, invitedNum); //判断是否需要升级vip等级
					if(!currentVipLevel.getId().equals(updateVipLevel.getId())){  //判断返回的vip等级是否和现在的vip一致 如果不一致就认定为升级；
						recmmandShopKeeper.setVipLevel(updateVipLevel);
						this.update(recmmandShopKeeper); //推荐的人的店长信息，更新新的vipLevel；
						vipUpdate = true;
					}
					tenantShopkeeper.setRecommendMember(recommandMember);
				}
				super.update(tenantShopkeeper); // 成为店主
				try{
					rentService.freePeriodRent(memberId,tenantShopkeeper.getTenant().getId()); //试用期交租
				}catch(BizException e){
					log.error(e.getMessage());
					throw new BizException(e.getMessage());
				}
				TicketSet ts = ticketSetService
						.getNewShopKeeperTicketSetByTenantId(tenantId); // 根据设置发放
				this.ownerServerice.save(new Owner(memberId));  //初始化余额表
				if (ts != null && ts.getUseFlag().equals(TicketSet.FLAG_USE))
					ticketCacheService.newShopKeeperSendTicketCache(ts,
							memberId);
				memberService.update(member);
				Tenant tenant = tenantShopkeeper.getTenant();
				
				pushService.publishSystemMessage(tenant, member, SystemMessage.shopKeeperOpenMsg(tenant.getShortName()));  
				if(recommandMember!=null){//给推荐人发消息
					pushService.publishSystemMessage(tenant, recommandMember, SystemMessage.shopKeeperInvationMsg(member.getName(), tenant.getShortName()));
					if(vipUpdate)  //vip等级升级了。给升级的店主发消息
						pushService.publishSystemMessage(tenant, recommandMember, SystemMessage.shopKeeperVipLevelUpMsg(invitedNum.toString(), updateVipLevel.getLevelName()));
				}

			}
		} else {
			throw new BizException(ALREADYSHOPKEEPEREXCEPTION);
		}
	}

	// 根据企业ID及推荐查找店主信息
	public List<TenantShopkeeper> findShopKeeperByTenantId(Long tenantId) {

		return this.tenantShopkeeperDao.findShopKeeperByTenantId(tenantId);

	}

	// 根据会员ID及推荐查找店主信息
	public TenantShopkeeper findShopKeeperByMemberId(Long memberId) {
		List<TenantShopkeeper> tenantShopkeeperList = this.tenantShopkeeperDao
				.findByMemeberIdAndIsShopkeeper(memberId,
						TenantShopkeeper.IsShopkeeper.yes);
		if (tenantShopkeeperList == null) {
			return null;
		}
		TenantShopkeeper ts = null;
		for (TenantShopkeeper tenantShopkeeper : tenantShopkeeperList) {
			if (TenantShopkeeper.IsShopkeeper.yes.equals(tenantShopkeeper
					.getIsShopkeeper())) {
				ts = tenantShopkeeper;
			}
		}
		return ts;

	}

	@Override
	public String getMemeberLoginTyper(Long memberId) {
		List<TenantShopkeeper> list = tenantShopkeeperDao
				.findTenantShopkeeperByMemeberId(memberId);
		if (list.size() == 0) {
			return "0"; // 普通买家
		} else {
			String type = "0";
			for (TenantShopkeeper tenantShopkeeper : list) {
				if (TenantShopkeeper.IsShopkeeper.yes.equals(tenantShopkeeper
						.getIsShopkeeper())) {
					type =  "1"; // 已经是店 
					return type;
				}else if(TenantShopkeeper.IsShopkeeper.no.equals(tenantShopkeeper
						.getIsShopkeeper())){
					type =  "2";// 有资格成为店主
					continue;
				}
			}
			return type;
		}
	}

    public List<TenantShopkeeper> getInvationsByRecommendMemberId(Long recommendMemberId) {
          return tenantShopkeeperDao.getInvationsByRecommendMemberId(recommendMemberId);
    }

	@Override
	public List<TenantShopkeeper> findInvationsByMemberId(Long memberId) {
		List<TenantShopkeeper> list = tenantShopkeeperDao
				.findTenantShopkeeperByMemeberId(memberId);
		if (list.size() == 0) {
			return null; // 普通买家
		} else {
			for (TenantShopkeeper tenantShopkeeper : list) {
				if (TenantShopkeeper.IsShopkeeper.yes.equals(tenantShopkeeper
						.getIsShopkeeper())) {
					return new ArrayList<TenantShopkeeper>();
				}
			}
			return list;
		}
	}

	@Override
	public List<TenantShopkeeper> findInvationsByMemberId(Long memberId,
			Long tenantId) throws BizException {
		List<TenantShopkeeper> list = tenantShopkeeperDao
				.findByTenantIdAndMember(tenantId, memberId);
		if (list.size() == 0) {
			throw new BizException("您还没有资格成为店长哦!!");
		} else {
			for (TenantShopkeeper tenantShopkeeper : list) {
				if (TenantShopkeeper.IsShopkeeper.yes.equals(tenantShopkeeper
						.getIsShopkeeper())) {
					String tenantName = tenantShopkeeper.getTenant().getShortName();
					throw new BizException("您已经是"+tenantName+"店长啦 ! 多邀请朋友,赚取分享佣金吧!");
				}
			}
			return list;
		}
	}

	/**
	 * 
	 * @param shopKeeperId
	 *            推荐人
	 * @param member
	 *            被邀请人
	 * @throws BizException 
	 */
	public void inviteShopkeeper(Long shopKeeperId, Member member) throws BizException {
		TenantShopkeeper ts = tenantShopkeeperDao
				.findShopKeeperByShopKeeperId(shopKeeperId);
		Long id =this.inviteBeShopkeeper(ts.getTenant().getId(), member.getId(),
				shopKeeperId);
		memberTenantService.addMemberTenant(member, ts.getTenant().getId());
		if(id==null) throw new BizException("您已经接受过该店长的邀请啦!快去开通店长赚取分享佣金吧!");
		
	}

	@Override
	public TenantShopkeeper getTenantByShopKeeper(Long shopKeeperId) throws BizException {
		TenantShopkeeper  ts =  tenantShopkeeperDao.findShopKeeperByShopKeeperId(shopKeeperId);
		if(ts ==null) throw new BizException("您还不是店长哦,不能进来哟!");
		return ts;
	}


	@Override
	public void deleteShopKeeperWithNo(Member member) {
		this.tenantShopkeeperDao.deleteOtherShopKeeper(member);
	}

    public int deleteShopKeeperByMemberIdAndTenantId(String memberId, String tenantId) {
        return tenantShopkeeperDao.deleteShopKeeperByMemberIdAndTenantId(memberId, tenantId);
    }

	
	@Override
	public Page<TenantShopkeeper> findPage(Tenant tenant,IsShopkeeper isShopkeeper,InvitedType invitedType,Pageable pageable){
		return tenantShopkeeperDao.findPage(tenant,isShopkeeper,invitedType,pageable);
	}

	@Override
	public Page<TenantShopkeeper> findPageSearch(String name,Tenant tenant, Pageable pageable,String mobile) {
		return tenantShopkeeperDao.findPageSearch(name,tenant,pageable,mobile);
	}

	@Override
	public List<TenantShopkeeper> getInvitationsThisMonth(Member owner) {
		List<TenantShopkeeper> list  = this.tenantShopkeeperDao.findTenantShopkeepersByRecommand(owner,DateUtil.getFirstDateOfMonth(),DateUtil.getNextMonthFirstDate());
		return list;
	}

	@Override
	public List<SystemMessageVO> processApply(Long[] ids, Boolean isPass) {
		List<SystemMessageVO> voList = new ArrayList<SystemMessageVO>();
		List<TenantShopkeeper> list = 	this.findList(ids);
		for (TenantShopkeeper tenantShopkeeper : list) {
			if(!tenantShopkeeper.getIsShopkeeper().equals(IsShopkeeper.capable)) continue; //如果提交的申请不为 capable 不作处理；
			SystemMessageVO vo = new SystemMessageVO();
			Tenant tenant = tenantShopkeeper.getTenant();
			Member member = tenantShopkeeper.getMember();
			vo.setTenant(tenant);
			vo.setMember(member);
			if(isPass){
				if(this.findShopKeeperByMemberId(tenantShopkeeper.getMember().getId())==null){
					vo.setMessage(SystemMessage.buyerApplyToShopKeeperPass(tenant.getShortName()));
					tenantShopkeeper.setIsShopkeeper(TenantShopkeeper.IsShopkeeper.no);
					this.update(tenantShopkeeper);
				}
			}else{
				vo.setMessage(SystemMessage.buyerApplyToShopKeeperReject(tenant.getShortName()));
				tenantShopkeeper.setIsShopkeeper(TenantShopkeeper.IsShopkeeper.canApply);
				this.update(tenantShopkeeper);
			}
			voList.add(vo);
		}
		return voList;
	}
	@Override
	public Long countByVipLevel(VipLevel vipLevel){
		return tenantShopkeeperDao.countByVipLevel(vipLevel);
	}
	
}