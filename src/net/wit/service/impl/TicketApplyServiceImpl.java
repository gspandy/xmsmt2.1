package net.wit.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import net.wit.entity.Member;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.Tenant;
import net.wit.entity.TenantShopkeeper;
import net.wit.entity.Ticket.Status;
import net.wit.entity.TicketApply;
import net.wit.entity.TicketApply.ApplyStatus;
import net.wit.entity.TicketApply.ApplyType;
import net.wit.entity.TicketApplyCondition;
import net.wit.entity.TicketCache;
import net.wit.entity.TicketSet;
import net.wit.mobile.util.rong.models.SystemMessage;
import net.wit.service.OrderService;
import net.wit.service.TenantShopkeeperService;
import net.wit.service.TicketApplyConditionService;
import net.wit.service.TicketApplyService;
import net.wit.service.TicketCacheService;
import net.wit.service.TicketSetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.wit.Filter;
import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.TicketApplyDao;
import net.wit.entity.Ticket;
import net.wit.service.TicketService;
import net.wit.util.BizException;
import net.wit.util.DateUtil;
import net.wit.vo.SystemMessageVO;

/**
 * @ClassName：TicketServiceImpl @Description：
 * @author：Chenlf
 * @date：2015年9月11日 下午6:22:39
 */
@Service("ticketApplyServiceImpl")
public class TicketApplyServiceImpl extends BaseServiceImpl<TicketApply, Long>implements TicketApplyService {


	@Resource(name = "ticketApplyDaoImpl")
	private TicketApplyDao ticketApplyDao;
	
	@Resource(name = "ticketApplyDaoImpl")
	public void setBaseDao(TicketApplyDao ticketApplyDao) {
		super.setBaseDao(ticketApplyDao);
	}

	@Autowired
	private TicketCacheService  ticketCacheService;
	
	@Autowired
	private TicketService  ticketService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private TenantShopkeeperService shopkeeperService;
	
	@Autowired
	private TicketSetService ticketSetService;
	
	@Autowired
	private TicketApplyConditionService ticketApplyConditionService;


	@Override
	public List<TicketApply> getTicketApplyByOwner(Member owner ,ApplyType applyType){
		return this.ticketApplyDao.queryTicketApplyByOwner(owner,applyType);
	}
	@Override
	public Page<TicketApply> getTicketApplyByTenant(Tenant tenant,ApplyType applyType ,ApplyStatus status ,int pageSize,int pageNo){
		List<Filter> filters = new ArrayList<Filter>();
		List<Order> orders = new ArrayList<Order>();
		Filter tenantFilter = new Filter("tenant", Filter.Operator.eq,tenant);   //根据企业查询
		if(applyType==null){
			Filter defaultapplyType = new Filter("applyType", Filter.Operator.in,ApplyType.shopkeeperApply);   //店主申请
			Filter defaultapplyType2 = new Filter("applyType", Filter.Operator.in,ApplyType.memberApplyToTenant);   //会员向企业申请
			filters.add(defaultapplyType);
			filters.add(defaultapplyType2);
		}else{
			Filter applyTypeFilter = new Filter("applyType", Filter.Operator.eq,applyType);   //申请类型;
			filters.add(applyTypeFilter);
		}
		Filter applyStatusFilter = new Filter("applyStatus", Filter.Operator.eq, status);  //申请状态
		filters.add(tenantFilter);
		filters.add(applyStatusFilter);
		orders.add(Order.desc("modifyDate"));
		Pageable pageable = new Pageable();
		pageable.setOrders(orders);
		pageable.setFilters(filters);
		pageable.setPageSize(pageSize);
		pageable.setPageNumber(pageNo);
		return this.findPage(pageable);
	}
	
	/**
	 * 
	 * @param applyIds
	 * @param applyStatus
	 * @return
	 * @throws BizException 
	 */
	@Override
	public List<SystemMessageVO> processTicketApplys(Long[] applyIds,ApplyStatus applyStatus) throws BizException{
		List<TicketApply> list = this.findList(applyIds);
		HashMap<Long,TicketSet> map = new HashMap<Long,TicketSet>();
		List<SystemMessageVO> voList = new ArrayList<SystemMessageVO>();
		for (TicketApply ticketApply : list) {
			Tenant tenant  = ticketApply.getTenant();
			String tenantName = tenant.getShortName();
			Member owner = ticketApply.getOwner();
			SystemMessageVO vo = new SystemMessageVO();
			vo.setTenant(tenant);
			if(ticketApply.getApplyStatus().equals(ApplyStatus.apply)){ //被拒绝
				if(applyStatus.equals(ApplyStatus.confirmed)){  //同意申请
					if(ticketApply.getApplyType().equals(ApplyType.memberApplyToShopKeeper)){  //买家申请
						vo.setMember(ticketApply.getMember());
						vo.setMessage(SystemMessage.buyerTicketApplyConfirmed(owner.getName(), tenantName));
						List<Ticket> ticketList  = ticketService.getTicketByShopkeeperId(owner.getId(), Ticket.Status.nouse);//获取店主未被领取的有效券
						Boolean ticketShare = false;
						for (Ticket ticket : ticketList) {
							ticketShare = ticketService.ticketApplyConfirmed(ticket, ticketApply.getMember());  
							if(ticketShare) break;//分享成功
						}
						if(ticketShare){
							voList.add(vo);  //有券分享
							ticketApply.setApplyStatus(applyStatus);    //更新券券申请状态
							this.update(ticketApply);
						}
					}else if(ticketApply.getApplyType().equals(ApplyType.memberApplyToTenant)){
						vo.setMember(ticketApply.getMember());
						vo.setMessage(SystemMessage.buyerApplyTicketToTenant(tenantName));
						Ticket ticket = new Ticket(owner,ticketApply.getMember(),tenant,Status.recevied);
						ticketService.save(ticket);
						ticketApply.setApplyStatus(applyStatus);
						this.update(ticketApply);
					}else{  //店长申请
						if(!map.containsKey(tenant.getId())){
							List<TicketSet> ticketSets =  ticketSetService.getTicketSet(tenant.getId(), TicketSet.APPLY_SEND_TYPE, TicketSet.FLAG_USE);  //获取店长申请发放张数；
							if(ticketSets.size()==0) throw new BizException("请先去 内购券发放设置 里设置每次申请发放张数！"); //没有设置前台抛出异常；
							map.put(tenant.getId(), ticketSets.get(0));
							TicketSet ts = map.get(tenant.getId());
							int ticketNum =ts.getSendNum();  //获取店长申请发放张数；
							vo.setMember(owner);
							vo.setMessage(SystemMessage.shopKeeperTicketApplyConfirmed(tenantName,String.valueOf(ticketNum)));
							ticketCacheService.save( new TicketCache(owner.getId(),ts));
							voList.add(vo);
							ticketApply.setApplyStatus(applyStatus);    //更新券券申请状态
							this.update(ticketApply);
						}
						else{
							TicketSet ts = map.get(tenant.getId());
							int ticketNum =ts.getSendNum();  //获取店长申请发放张数；
							vo.setMember(owner);
							vo.setMessage(SystemMessage.shopKeeperTicketApplyConfirmed(tenantName,String.valueOf(ticketNum)));
							ticketCacheService.save( new TicketCache(owner.getId(),ts));
							voList.add(vo);
							ticketApply.setApplyStatus(applyStatus);    //更新券券申请状态
							this.update(ticketApply);
						}
					}
				}else if(applyStatus.equals(ApplyStatus.rejected)){  //被拒绝
					if(ticketApply.getApplyType().equals(ApplyType.shopkeeperApply)){  //买家
						vo.setMember(ticketApply.getMember());
						vo.setMessage(SystemMessage.buyerTicketApplyRejected(owner.getName(), tenantName));
						voList.add(vo);
					}else if(ticketApply.getApplyType().equals(ApplyType.memberApplyToTenant)){
						vo.setMember(ticketApply.getMember());
						vo.setMessage(SystemMessage.buyerApplyTicketRejectByTenant(tenantName));
					}else{  //店长申请
						vo.setMember(owner);
						vo.setMessage(SystemMessage.shopKeeperTicketApplyRejected(tenantName));
						voList.add(vo);
					}
					ticketApply.setApplyStatus(applyStatus);    //更新券券申请状态
					this.update(ticketApply);
				}
			}
		}
		return voList;
	}
	

	/**
	 * 判断条件
	 * 已经有申请状态返回false;
	 * 有一张该店主未使用的券,返回false;
	 */
	@Override
	public boolean checkMemberCanApply(Member member,Member owner,Long tenantId){
		Boolean bool = true;
		List<TicketApply> list  = this.ticketApplyDao.queryTicketApplyByMemberOwner(owner, member); //  是否存在有等待店主审批的申请;
		if(list.size()>0) return false;
		Long count = ticketService.countTicketByMemberId(member.getId(), owner.getId(), tenantId, Ticket.Status.recevied);  //查询该用户是否存在申请店长的未使用的券
		if(count.compareTo(new Long(0))>0) return false;
		return bool;
	}
	
	@Override
	public int getOwnerCanApplyTimes(Member owner,Tenant tenant){
		int ordersCondition = 0;
		int invationsCondtion = 0;
		int applyTimes =0;
		int canApplyTimes = 0;
		TicketApplyCondition tac = ticketApplyConditionService.getTicketApplyConditionByTenant(tenant);
		if(tac!=null){
			if(tac.getInvations()!=null&&tac.getInvations()>0){
				List<TenantShopkeeper> shopKeeperList = this.shopkeeperService.getInvitationsThisMonth(owner);
				invationsCondtion = shopKeeperList.size()/tac.getInvations();
			}
			if(tac.getTicketUsedTimes()!=null &&tac.getTicketUsedTimes()>0){
				List<net.wit.entity.Order> orderList = this.orderService.orderTimesWithMonth(owner, PaymentStatus.paid);
				ordersCondition = orderList.size()/tac.getTicketUsedTimes();
			}
			List<TicketApply> list  = this.ticketApplyDao.queryTicketApplyByOwner(owner, DateUtil.getFirstDateOfMonth(), DateUtil.getNextMonthFirstDate());		
			applyTimes = list.size();
			if(invationsCondtion>ordersCondition){
				if(invationsCondtion>applyTimes)canApplyTimes = invationsCondtion - applyTimes;
			}else{
				if(ordersCondition>applyTimes)canApplyTimes = ordersCondition - applyTimes;
			}
		}
		return canApplyTimes;
	}
	
	/**
	 * 买家向店主申请券券
	 * @param member
	 * @param owner
	 * @param tenant
	 * @return
	 * @throws BizException
	 */
	@Override
	public boolean buyerTicketApply(Member member,Member owner,Tenant tenant) throws BizException{
		Boolean bool = true;
		if(!this.checkMemberCanApply(member, owner,tenant.getId())) return false;  //判断是否可以申请
		List<TicketApply> list = 
					ticketApplyDao.queryTicketApply(ApplyType.memberApplyToTenant, ApplyStatus.apply, tenant, owner, member);
		if(list != null && list.size() > 0){
			return false; 
		}
		TicketApply ticketApply = new TicketApply();
		
		if(member.getId().equals(owner.getId())) {
			TenantShopkeeper ts = shopkeeperService.getShopkeeperByTenantAndMember(tenant,member);
			if(ts==null ) //不是该企业店主
				ticketApply.setApplyType(ApplyType.memberApplyToTenant);// 买家想企业申请券;
			else
				throw new BizException("您已经是"+tenant.getShortName()+"的店长,不能向自己申请券券哦.去向企业申请吧!"); 
		}
		else ticketApply.setApplyType(ApplyType.memberApplyToShopKeeper);
		ticketApply.setApplyStatus(TicketApply.ApplyStatus.apply);
		ticketApply.setMember(member);
		ticketApply.setOwner(owner);
		ticketApply.setTenant(tenant);
		try {
			this.save(ticketApply);
			return bool;
		} catch (Exception e) {
			throw new BizException("申请券券失败,请联系平台客服姐姐!");
		}

	}
	
	/**
	 * 店主向企业申请
	 * @throws BizException 
	 */
	public boolean ownerTicketApply(Member owner,Tenant tenant) throws BizException{
		Boolean bool = true;
		if(!(this.getOwnerCanApplyTimes(owner, tenant)>0))return false;
		TicketApply ticketApply = new TicketApply();
		ticketApply.setApplyType(ApplyType.shopkeeperApply);
		ticketApply.setApplyStatus(TicketApply.ApplyStatus.apply);
		ticketApply.setOwner(owner);
		ticketApply.setMember(owner);
		ticketApply.setTenant(tenant);
		try {
			this.save(ticketApply);
			return bool;
		} catch (Exception e) {
			throw new BizException("申请券券失败,请联系平台客服姐姐!");
		}
	}
	@Override
	public void batchRejectApply(Tenant tenant, int autoRejectDays) {
		List<TicketApply> list  = this.ticketApplyDao.queryTicketApply(ApplyType.shopkeeperApply, TicketApply.ApplyStatus.apply, tenant);
		Date now = new Date();
		for (TicketApply ticketApply : list) {
			Date createDate = ticketApply.getCreateDate();
			if(now.compareTo(DateUtil.addDay(createDate, autoRejectDays))>0){
				ticketApply.setApplyStatus(ApplyStatus.rejected);
				this.update(ticketApply);
			}
		}
		
	}
	
	/**
	 * 根据筛选条件查询店长申请内购券列表
	 */
	public Page<TicketApply> findPageByCriteria(Tenant tenant, List<ApplyStatus> applyStatus, List<ApplyType> applyType,
							         String mobile, Pageable pageable) {
		return ticketApplyDao.findPageByCriteria(tenant, applyStatus, applyType, mobile, pageable);
	}


	
}
