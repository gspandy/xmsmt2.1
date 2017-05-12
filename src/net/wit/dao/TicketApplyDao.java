package net.wit.dao;

import java.util.Date;
import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.entity.TicketApply;
import net.wit.entity.TicketApply.ApplyStatus;
import net.wit.entity.TicketApply.ApplyType;

public interface TicketApplyDao extends BaseDao<TicketApply, Long>{

	/**
	 * 查询店长在申请状态 内购券申请记录
	 * @param owner
	 * @return
	 */
	public List<TicketApply> queryTicketApplyByOwner(Member owner,ApplyType applyType);

	/**
	 * 查询会员与店长是否有
	 * @param owner
	 * @param member
	 * @return
	 */
	public List<TicketApply> queryTicketApplyByMemberOwner(Member owner, Member member);

	/**
	 * 查询店长时间范围内向企业申请券的记录,不包含被拒绝的申请；
	 * @param owner
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public List<TicketApply> queryTicketApplyByOwner(Member owner, Date beginDate,
			Date endDate);

	public List<TicketApply> queryTicketApply(ApplyType applyType, ApplyStatus status,Tenant tenant);

	/**
	 * 根据筛选条件查询VIP申请内购券列表
	 * @param tenant
	 * @param applyStatus
	 * @param applyType
	 * @param mobile
	 * @param pageable
	 * @return
	 */
	public Page<TicketApply> findPageByCriteria(Tenant tenant,
			List<ApplyStatus> applyStatus, List<ApplyType> applyType,
			String mobile, Pageable pageable);

	public List<TicketApply> queryTicketApply(ApplyType applyType, ApplyStatus status,
			Tenant tenant, Member owner, Member member);

}
