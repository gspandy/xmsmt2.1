package net.wit.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.wit.Filter;
import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.TicketCacheDao;
import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.entity.TenantShopkeeper;
import net.wit.entity.TenantShopkeeper.IsShopkeeper;
import net.wit.entity.Ticket;
import net.wit.entity.TicketCache;
import net.wit.entity.TicketSet;
import net.wit.mobile.service.impl.PushService;
import net.wit.mobile.util.rong.models.SystemMessage;
import net.wit.service.MemberService;
import net.wit.service.TenantService;
import net.wit.service.TenantShopkeeperService;
import net.wit.service.TicketCacheService;
import net.wit.service.TicketService;
import net.wit.vo.TicketCacheVO;

@Service("ticketCacheServiceImpl")
public class TicketCacheServiceImpl extends BaseServiceImpl<TicketCache, Long>
		implements TicketCacheService {

	@Resource(name = "ticketCacheDaoImpl")
	private TicketCacheDao ticketCacheDao;

	@Resource(name = "tenantShopkeeperServiceImpl")
	private TenantShopkeeperService tenantShopkeeperService;
	
	@Resource(name = "ticketServiceImpl")
	private TicketService ticketService;
	
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "ticketCacheDaoImpl")
	public void setBaseDao(TicketCacheDao ticketCacheDao) {
		super.setBaseDao(ticketCacheDao);
	}
	@Autowired
	private TenantService tenantService;
	
	@Autowired
	private PushService pushService;
	
	private Logger log = LoggerFactory.getLogger(TicketCacheServiceImpl.class);

	public List<TicketCache> getNoReceiveTickCacheByMemberId(Long memberId) {
		return ticketCacheDao.getTicketCacheByTenantId(memberId,
				TicketCache.TICKETCACHE_NORECEIVESTATUS);
	}
	
	public Long getshopkeeperNoUseCount(Long memberId,String receiveStatus){
		return ticketCacheDao.getshopkeeperNoUseCount(memberId, receiveStatus);
	}

	public List<TicketCache> getReceivedTickCacheByMemberId(Long memberId) {
		return ticketCacheDao.getTicketCacheByTenantId(memberId,
				TicketCache.TICKETCACHE_RECEIVEDSTATUS);
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void batchGenTicketCacheByTicketSet(TicketSet ticketSet) {
		if (ticketSet.getSendType().equals(
				TicketSet.SYSTEM_SEND_TYPE)) {
			List<Filter> filters = new ArrayList<Filter>();
			List<Order> orders = new ArrayList<Order>();
			Tenant tenant = new Tenant();
			tenant.setId(ticketSet.getTenantId());
			Filter filter = new Filter("tenant", Filter.Operator.eq,tenant);
			Filter filter1 = new Filter("isShopkeeper", Filter.Operator.eq,IsShopkeeper.yes);
			filters.add(filter);
			filters.add(filter1);
			orders.add(Order.asc("member"));
			Long count = tenantShopkeeperService.count(filter);
			Pageable pageable = new Pageable();
			pageable.setOrders(orders);
			pageable.setFilters(filters);
			int pageSize = 100;
			pageable.setPageSize(pageSize);
			int totalPages = (int) Math.ceil((double) count
					/ (double) pageable.getPageSize());
			for (int i = 0; i < totalPages; i++) {
				pageable.setPageNumber(i);
				List<TenantShopkeeper> list = this.tenantShopkeeperService
						.findPage(pageable).getContent();
				List<TicketCache> ticketList = new ArrayList<TicketCache>();
				for (TenantShopkeeper tenantShopkeeper : list) {
					TicketCache tc ;
					if(ticketSet.getSendType().equals(TicketSet.SYSTEM_SEND_TYPE)){
						tc= new TicketCache(  //根据店长等级发券
								tenantShopkeeper.getMember().getId(),tenantShopkeeper.getVipLevel() ,ticketSet.getSendType());

					}else{
						tc= new TicketCache(tenantShopkeeper.getMember().getId(),ticketSet);
					}
					ticketList.add(tc);
				}
				//单独事务提交
//				TicketCacheService ticketCacheService = (TicketCacheService) ApplicationContextUtil
//						.getBean("ticketCacheServiceImpl");
				try{
					batchSubmitTicketCache(ticketList);
				}catch(Exception e){
					log.error(e.getMessage());
				}
			}
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void batchGenTicketCacheByTicketSet(Tenant tenant) {
			List<Filter> filters = new ArrayList<Filter>();
			List<Order> orders = new ArrayList<Order>();
			Filter filter = new Filter("tenant", Filter.Operator.eq,tenant);
			Filter filter1 = new Filter("isShopkeeper", Filter.Operator.eq,IsShopkeeper.yes);
			filters.add(filter);
			filters.add(filter1);
			orders.add(Order.asc("member"));
			Long count = tenantShopkeeperService.count(filter);
			Pageable pageable = new Pageable();
			pageable.setOrders(orders);
			pageable.setFilters(filters);
			int pageSize = 100;
			pageable.setPageSize(pageSize);
			int totalPages = (int) Math.ceil((double) count
					/ (double) pageable.getPageSize());
			for (int i = 0; i < totalPages; i++) {
				pageable.setPageNumber(i);
				List<TenantShopkeeper> list = this.tenantShopkeeperService
						.findPage(pageable).getContent();
				List<TicketCache> ticketList = new ArrayList<TicketCache>();
				for (TenantShopkeeper tenantShopkeeper : list) {
					TicketCache tc= new TicketCache(  //根据店长等级发券
						tenantShopkeeper.getMember().getId(),tenantShopkeeper.getVipLevel() ,TicketSet.SYSTEM_SEND_TYPE);
					ticketList.add(tc);
				}
				//单独事务提交
//				TicketCacheService ticketCacheService = (TicketCacheService) ApplicationContextUtil
//						.getBean("ticketCacheServiceImpl");
				try{
					batchSubmitTicketCache(ticketList);
				}catch(Exception e){
					log.error(e.getMessage());
				}
			}
		}
	
	
	

	public void newShopKeeperSendTicketCache(TicketSet ticketSet, Long memberId) {
		TicketCache tc = new TicketCache(memberId,ticketSet);
		this.save(tc);
	}

	public void batchSaveTicketCacheByMemberListOnManual(
			List<Long> memberIdList, Long tenantId, int num) {
		Tenant tenant = tenantService.find(tenantId);
		for (Long memberId : memberIdList) {
			TicketCache tc = new TicketCache(memberId, tenantId, num);
			this.save(tc);
		}
		for(Long memberId : memberIdList){
			Member member = new Member();
			member.setId(memberId);
			pushService.publishSystemMessage(tenant, member, SystemMessage.shopKeeperTicketMsg(tenant.getShortName(), String.valueOf(num)));
		}
	}

	public void sendTicketCacheToAllShopKeeperOnManual(Long tenantId, int num) {
		List<Filter> filters = new ArrayList<Filter>();
		List<Order> orders = new ArrayList<Order>();
		Filter filter = new Filter("tenantId", Filter.Operator.eq, tenantId);
		filters.add(filter);
		orders.add(Order.asc("memberId"));
		Long count = tenantShopkeeperService.count(filter);
		Pageable pageable = new Pageable();
		pageable.setOrders(orders);
		pageable.setFilters(filters);
		int pageSize = 100;
		pageable.setPageSize(pageSize);
		int totalPages = (int) Math.ceil((double) count
				/ (double) pageable.getPageSize());
		for (int i = 0; i < totalPages; i++) {
			pageable.setPageNumber(i);
			List<TenantShopkeeper> list = this.tenantShopkeeperService
					.findPage(pageable).getContent();
			List<TicketCache> ticketList = new ArrayList<TicketCache>();
			for (TenantShopkeeper tenantShopkeeper : list) {
				TicketCache tc = new TicketCache(
						tenantShopkeeper.getMember().getId(), tenantId, num);
				ticketList.add(tc);
			}
			try{
				batchSubmitTicketCache(ticketList);
			}catch(Exception e){
				log.error("批量发券失败!"+e.getMessage());
			}

		}
	}
/**
 * 批量提交券cache
 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public void batchSubmitTicketCache(List<TicketCache> list) {
		Tenant tenant = null;
		for (TicketCache ticketCache : list) {
				this.ticketCacheDao.persist(ticketCache);
		}
		for(TicketCache ticketCache : list){
			if(tenant==null){
				tenant= tenantService.find(ticketCache.getTenantId());
			}
			Member member = new Member();
			member.setId(ticketCache.getMemberId());
			pushService.publishSystemMessage(tenant, member, SystemMessage.shopKeeperTicketMsg(tenant.getShortName(), ticketCache.getNum().toString()));
		}
	}
  /**
   * 领券
   */
	@Override
	public void toReceiveTicket(Long memberId) {
		List<TicketCache> list = this.getNoReceiveTickCacheByMemberId(memberId);
		for (TicketCache ticketCache : list) {
			List<Ticket> tickList = new ArrayList<Ticket>();
			int ticketNum = ticketCache.getNum();
			for (int i = 0; i < ticketNum; i++) {
				Member member = new Member();
				Tenant tenant = new Tenant();
				tenant.setId(ticketCache.getTenantId());
				member.setId(ticketCache.getMemberId());
				Ticket ticket  = new Ticket(member,tenant,ticketCache.getExpiryDate());
				tickList.add(ticket);
			}
			ticketService.batchSubmitTicket(tickList);
			ticketCache.setReceiveStatus("1");
			this.update(ticketCache);
		}
	}
	@Override
    public boolean isTicketCacheExist(Long memberId){
    	List<TicketCache> list = this.ticketCacheDao.getTicketCacheByMember(memberId);
    	if(list.size()>0) return true;
    	else return false;
    }
	@Override
	public Page<TicketCacheVO> findTickeCacheByPage(Long tenantId,Date startDate,Date endDate,String sendModel,String name,Pageable pageable) {
		List<Member> members =null;
		//若name 有值，先使用模糊查询，确定数量 
		if(name!=null && !"".equals(name)){
			 members = memberService.findListByIdAndName(null, name);
		 }
		Page<TicketCache> ticketCachePage = ticketCacheDao.findTickeCacheByPage(tenantId, startDate, endDate, sendModel, members, pageable);
		 
		 List<TicketCacheVO> list=new ArrayList<TicketCacheVO>();
		 List<TicketCache> content = ticketCachePage.getContent();
		 if(content!=null && content.size()>0){
			 for (TicketCache ticketCache : content) {
				 TicketCacheVO ticketCacheVO = new TicketCacheVO();
				ticketCacheVO.setId(BigInteger.valueOf(ticketCache.getId()));
				ticketCacheVO.setModel(ticketCache.getModel());
				ticketCacheVO.setTenantId(BigInteger.valueOf(ticketCache.getTenantId()));
				ticketCacheVO.setTicketNum(ticketCache.getNum());
				ticketCacheVO.setCreateDate(ticketCache.getCreateDate());
				
				if(ticketCache.getMemberId()!=null && !"".equals(ticketCache.getMemberId())){
					members = memberService.findListByIdAndName(ticketCache.getMemberId(), name);
					Member member=null;
					if(members!=null && members.size()>0){
						member=members.get(0);
					}
					if(member!=null && !"".equals(member)){
						ticketCacheVO.setMemberId(BigInteger.valueOf(member.getId()));
						ticketCacheVO.setName(member.getName());
						ticketCacheVO.setMobile(member.getMobile());
						list.add(ticketCacheVO);
					}
				}
			}
		 }
		 return new Page<>(list, ticketCachePage.getTotal(), ticketCachePage.getPageable());
	}
	@Override
	public List<TicketCacheVO> findTickeCacheByList(Long tenantId,Date startDate,Date endDate,String sendModel,String name) {
		List<Member> members =null;
		//若name 有值，先使用模糊查询，确定数量 
		if(name!=null && !"".equals(name)){
			members = memberService.findListByIdAndName(null, name);
		}
		List<TicketCache> ticketCacheList = ticketCacheDao.findTickeCacheByList(tenantId, startDate, endDate, sendModel, members);
		
		List<TicketCacheVO> list=new ArrayList<TicketCacheVO>();
		if(ticketCacheList!=null && ticketCacheList.size()>0){
			for (TicketCache ticketCache : ticketCacheList) {
				TicketCacheVO ticketCacheVO = new TicketCacheVO();
				ticketCacheVO.setId(BigInteger.valueOf(ticketCache.getId()));
				ticketCacheVO.setModel(ticketCache.getModel());
				ticketCacheVO.setTenantId(BigInteger.valueOf(ticketCache.getTenantId()));
				ticketCacheVO.setTicketNum(ticketCache.getNum());
				ticketCacheVO.setCreateDate(ticketCache.getCreateDate());
				
				if(ticketCache.getMemberId()!=null && !"".equals(ticketCache.getMemberId())){
					members = memberService.findListByIdAndName(ticketCache.getMemberId(), name);
					Member member=null;
					if(members!=null && members.size()>0){
						member=members.get(0);
					}
					if(member!=null && !"".equals(member)){
						ticketCacheVO.setMemberId(BigInteger.valueOf(member.getId()));
						ticketCacheVO.setName(member.getName());
						ticketCacheVO.setMobile(member.getMobile());
						list.add(ticketCacheVO);
					}
				}
			}
		}
		return list;
	}
}
