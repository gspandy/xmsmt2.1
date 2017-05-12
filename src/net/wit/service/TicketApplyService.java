package net.wit.service;

import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.entity.TicketApply;
import net.wit.entity.TicketApply.ApplyStatus;
import net.wit.entity.TicketApply.ApplyType;
import net.wit.util.BizException;
import net.wit.vo.SystemMessageVO;

public interface TicketApplyService extends BaseService<TicketApply, Long> {

	/**
	 * 处理券券申请, 企业后台申请,店长申请均可使用.
	 * 店长申请返回 list 为空判断没有可以分享的券
	 * @param applyIds
	 * @param applyStatus
	 * @return list;
	 * @throws BizException 
	 */
	public List<SystemMessageVO> processTicketApplys(Long[] applyIds,
			ApplyStatus applyStatus) throws BizException;

	/**
	 * 判断店主当前可以申请的次数
	 * @param owner
	 * @return
	 */
	public int getOwnerCanApplyTimes(Member owner, Tenant tenant);
	/**
	 * 判断会员是否还可以向该店主申请券
	 * @param member
	 * @param owner
	 * @return
	 */
	public boolean checkMemberCanApply(Member member,Member owner,Long tenantId);
	
	/**
	 * 买家向店长申请券
	 * @param member
	 * @param owner
	 * @param tenant
	 * @return
	 */
	public boolean buyerTicketApply(Member member, Member owner, Tenant tenant)
			throws BizException;
	
	
	/**
	 * 店长向企业申请券
	 * @param owner
	 * @return
	 * @throws BizException 
	 */
	public boolean ownerTicketApply(Member owner,Tenant tenant) throws BizException;
	
	/**
	 * 获取店长的券券申请列表
	 * @param owner
	 * @return
	 */
	public List<TicketApply> getTicketApplyByOwner(Member owner,ApplyType applyType);
	
	/**
	 * 内购券申请页面使用查询
	 * @param tenant
	 * @param applyType
	 * @param status
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	public Page<TicketApply> getTicketApplyByTenant(Tenant tenant,ApplyType applyType ,ApplyStatus status ,int pageSize,int pageNo);
	/**券券申请过期批*/
	public void batchRejectApply(Tenant tenant,int autoRejectDays);

	/**
	 * 根据筛选条件查询店长申请内购券列表
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


}
