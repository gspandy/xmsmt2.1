package net.wit.service;

import java.util.Date;
import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.entity.Ticket;
import net.wit.entity.Ticket.Status;

/**
 * @ClassName：TicketService
 * @Description：
 * @author：Chenlf 
 * @date：2015年9月11日 下午6:22:12 
 */
public interface TicketService extends BaseService<Ticket,Long> {

	/**条件查询内购券信息
	 * @param member 会员
	 * @param ticketStatusParam	内购券状态
	 * @param ticketModifyDate  内购券领取日期
	 * @return
	 */
	public List<Ticket> findList(Long tenantId,Member member, String ticketStatusParam, Date firstDayOfMonth, Date lastDayOfMonth);
	
	/**条件查询内购券信息
	 * @param member 会员
	 * @param ticketStatusParam	内购券状态
	 * @param ticketModifyDate  内购券领取日期
	 * @param pageable
	 * @return
	 */
	public Page<Ticket> findPage(Long tenantId,Member member, String ticketStatusParam, Date firstDayOfMonth, Date lastDayOfMonth,Pageable pageable);

    public boolean isTicketNoUse(Long ticketId);

	/**
	 * 根据用户ID获取未分享出去的券.
	 * @param memberId
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
    public List<Ticket> getTicketByStatus(Long shopkeeperId,Long tenantId ,List<Ticket.Status> status,int pageSize,int pageNo);
	
	
	/**
	 * 单独事务批量提交ticket
	 * @param list
	 */
	public void batchSubmitTicket(List<Ticket> list);

	public void updateTicket(Long ticketId, Member member,  Ticket.Status status);

    public int shareTicket(Long ticketId, String tel)throws Exception;
    
    public boolean isExistTicket(Long memberId);
    
    public Long countTicketByMemberId(Long memberId,Long shopKeeperId,Long tenantId,Ticket.Status status);
    
    public List<Ticket> getMyTicketByStatus(Long memberId,List<Ticket.Status> status,int pageSize,int pageNo);
    
    public List<Ticket> getTicketByShopkeeperId(Long shopkeeperId,Ticket.Status status);
    
    public List<Ticket> getTicketSettle(Long memberId,Long tenantId,Ticket.Status status);

	public List<Ticket> getMyTicketPagket(Long memberId, List<Status> status,
			int pageSize, int pageNo);
	
	public List<Tenant> getTicketTenant(Member member);

	public Boolean ticketApplyConfirmed(Ticket ticket, Member member);
}
