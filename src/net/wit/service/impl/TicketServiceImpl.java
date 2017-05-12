package net.wit.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.mobile.service.impl.PushService;
import net.wit.mobile.util.rong.models.SystemMessage;
import net.wit.service.MemberService;
import net.wit.service.MemberTenantService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.wit.Filter;
import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.TicketDao;
import net.wit.entity.Ticket;
import net.wit.service.TicketService;

/**
 * @ClassName：TicketServiceImpl @Description：
 * @author：Chenlf
 * @date：2015年9月11日 下午6:22:39
 */
@Service("ticketServiceImpl")
public class TicketServiceImpl extends BaseServiceImpl<Ticket, Long>implements TicketService {


    @Autowired
    private MemberService memberService;
	
    @Autowired
    private MemberTenantService memberTenantService;
	@Resource(name = "ticketDaoImpl")
	private TicketDao ticketDao;
	
	@Resource(name = "ticketDaoImpl")
	public void setBaseDao(TicketDao ticketDao) {
		super.setBaseDao(ticketDao);
	}

	@Autowired
	private PushService pushService;

	@Override
    public void updateTicket(Long ticketId, Member member,  Ticket.Status status) {
        Ticket ticket = ticketDao.find(ticketId);
        ticket.setMember(member);
        ticket.setStatus(status);
        
        super.update(ticket);
//        super.save(ticket);
        if(status.equals(Ticket.Status.recevied)){ //如果修改为已经领取 发消息给店主
        	String tenantName =ticket.getTenant().getShortName();
        	String mobile = "";
        	if(member.getMobile()==null){
        		member = memberService.find(member.getId());
        		mobile= member.getMobile();
        	}else mobile = member.getMobile();
        	pushService.publishSystemMessage(ticket.getTenant(), ticket.getShopkeeper(), SystemMessage.shopKeeperTicketBeRecevied(tenantName,mobile));
        }
    }
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public Boolean ticketApplyConfirmed(Ticket ticket,Member member){
		if(ticket.getStatus().equals(Ticket.Status.nouse)){
			ticket.setMember(member);
			ticket.setStatus(Ticket.Status.recevied);
			 super.update(ticket);
			 return true;
		}else return false;
	}

	@Override
	public boolean isTicketNoUse(Long ticketId) {
        Ticket ticket = ticketDao.find(ticketId);
        if (ticket != null) {
            if (Ticket.Status.nouse.name().equals(ticket.getStatus().name())) {
                return  true;
            }
        }
        return false;
    }
	
	@Override
    public int shareTicket(Long ticketId, String tel) throws Exception{
        int flag = -1;
        Member member = memberService.findByTel(tel);
        if (member == null) {
            //注册
            Member newMember = new Member();
            newMember.setMobile(tel);
            memberService.save(newMember);
            memberTenantService.addMemberTenant(newMember, this.find(ticketId).getTenant().getId());
            updateTicket(ticketId, newMember,Ticket.Status.recevied);
            flag = 0;
        } else {
        	Ticket ticket = this.find(ticketId);
        	 List<Ticket> count = ticketDao.countTicketByMemberIdAndShopKeeperId(member.getId(),ticket.getShopkeeper().getId() 
        			 ,ticket.getTenant().getId(),Ticket.Status.recevied);
             if(count.size() > 0){
             	throw new Exception("我分享的券你还没用哦! 使用后才可以再领取哦!");
             }
            updateTicket(ticketId, member,Ticket.Status.recevied);
            flag = 1;
        }
        return flag;
    }
	
	public List<Ticket> getTicketByStatus(Long shopkeeperId,Long tenantId,List<Ticket.Status> status,int pageSize,int pageNo){
		List<Filter> filters = new ArrayList<Filter>();
		List<Order> orders = new ArrayList<Order>();
		Member shopkeeper = new Member();
		Tenant tenant = new Tenant();
		tenant.setId(tenantId);
		shopkeeper.setId(shopkeeperId);
		Filter filter = new Filter("shopkeeper", Filter.Operator.eq,shopkeeper);
		Filter filter2 = new Filter("tenant", Filter.Operator.eq,tenant);
		Filter filter1 = new Filter("status", Filter.Operator.in,status);
		filters.add(filter);
		filters.add(filter2);
		filters.add(filter1);
		orders.add(Order.asc("status"));
		orders.add(Order.asc("shopkeeper"));
		Pageable pageable = new Pageable();
		pageable.setOrders(orders);
		pageable.setFilters(filters);
		pageable.setPageSize(pageSize);
		pageable.setPageNumber(pageNo);
		List<Ticket> list =this.findPage(pageable).getContent();
		return list;
	}
	
	public List<Ticket> getMyTicketByStatus(Long memberId,List<Ticket.Status> status,int pageSize,int pageNo){
		List<Filter> filters = new ArrayList<Filter>();
		List<Order> orders = new ArrayList<Order>();
		Member member = new Member();
		member.setId(memberId);
		Filter filter = new Filter("member", Filter.Operator.eq,member);
		Filter filter1 = new Filter("status", Filter.Operator.in,status);
		filters.add(filter);
		filters.add(filter1);
		orders.add(Order.asc("status"));
		orders.add(Order.asc("tenant"));
		orders.add(Order.asc("shopkeeper"));
		Pageable pageable = new Pageable();
		pageable.setOrders(orders);
		pageable.setFilters(filters);
		pageable.setPageSize(pageSize);
		pageable.setPageNumber(pageNo);
		List<Ticket> list =this.findPage(pageable).getContent();
		return list;
	}
	@Override
	public List<Ticket> getMyTicketPagket(Long memberId,List<Ticket.Status> status,int pageSize,int pageNo){
		List<Ticket> list = this.getMyTicketByStatus(memberId, status, pageSize, pageNo);
		List<Ticket>returnList = new ArrayList<Ticket>();
		HashMap<String,Ticket.Status> mapStatus = new HashMap<String,Ticket.Status>();
		HashMap<String,Integer> mapIndex = new HashMap<String,Integer>();
		int i=0;
		for (Ticket ticket : list) {
			String key = ticket.getTenant().getId()+"&"+ticket.getShopkeeper().getId();
			if(!mapStatus.containsKey(key)){ //如果不存在
				mapStatus.put(key, ticket.getStatus());
				mapIndex.put(key, i);
				returnList.add(ticket);
				i++;
			}else{ //如果已经存在,
				if(!mapStatus.get(key).equals(Ticket.Status.recevied)){ //存在的状态不为有效
					if(ticket.getStatus().equals(Ticket.Status.recevied)){ //当前ticket 状态为有效替换原有的
						int index = mapIndex.get(key);
						returnList.remove(index);
						returnList.add(index, ticket);
					}
				}
			}
		}
		return returnList;
	}
	
	
	public List<Ticket> getTicketHasShared(Long memberId,int pageSize,int pageNo){
		List<Filter> filters = new ArrayList<Filter>();
		List<Order> orders = new ArrayList<Order>();
		Member member = new Member();
		member.setId(memberId);
		Filter filter = new Filter("member", Filter.Operator.eq,member);
		Filter filter1 = new Filter("status", Filter.Operator.ne,Ticket.Status.nouse);
		filters.add(filter);
		filters.add(filter1);
		orders.add(Order.asc("id"));
		Pageable pageable = new Pageable();
		pageable.setOrders(orders);
		pageable.setFilters(filters);
		pageable.setPageSize(pageSize);
		pageable.setPageNumber(pageNo);
		List<Ticket> list =this.findPage(pageable).getContent();
		return list;
	}
	
	public boolean isExistTicket(Long memberId){
		return ticketDao.isExistTicket(memberId);
	}
	
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public void batchSubmitTicket(List<Ticket> list) {
		for (Ticket ticket : list) {
			this.ticketDao.persist(ticket);
		}
	}
	
	public Long countTicketByMemberId(Long memberId,Long shopKeeperId,Long tenantId,Ticket.Status status){
		List<Ticket> list =  ticketDao.countTicketByMemberIdAndShopKeeperId(memberId, shopKeeperId, tenantId, status);
		Long count = new Long(list.size());
		return count;
	}
	
	public List<Ticket> getTicketByShopkeeperId(Long shopkeeperId,Ticket.Status status){
		return ticketDao.getTicketByShopkeeperId(shopkeeperId, status);
	}
	
	public List<Ticket> getTicketSettle(Long memberId,Long tenantId,Ticket.Status status){
		return ticketDao.getTicketByTenant(memberId, tenantId, status);
	}
	public List<Tenant> getTicketTenant(Member member){
		List<Ticket> ticketList = ticketDao.queryTenantByTickets(member);
		List<Tenant> list = new ArrayList<Tenant>();
		for (Ticket ticket : ticketList) {
			list.add(ticket.getTenant());
		}
		return list;
		
	}
	@Override
	public Page<Ticket> findPage(Long tenantId,Member member, String ticketStatusParam,  Date firstDayOfMonth, Date lastDayOfMonth, Pageable pageable) {
		return ticketDao.findPage(tenantId,member, ticketStatusParam, firstDayOfMonth,lastDayOfMonth, pageable);
	}
	
	@Override
	public List<Ticket> findList(Long tenantId,Member member, String ticketStatusParam,  Date firstDayOfMonth, Date lastDayOfMonth) {
		return ticketDao.findList(tenantId,member, ticketStatusParam, firstDayOfMonth,lastDayOfMonth);
	}

}
