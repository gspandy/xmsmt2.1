package net.wit.service;

import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.entity.TenantShopkeeper;
import net.wit.entity.TenantShopkeeper.InvitedType;
import net.wit.entity.TenantShopkeeper.IsShopkeeper;
import net.wit.entity.VipLevel;
import net.wit.util.BizException;
import net.wit.vo.SystemMessageVO;

public interface TenantShopkeeperService extends BaseService<TenantShopkeeper, Long>{

	
  /**
   * 企业邀请开通店长,无推荐人
   * @param tenantId
   * @param memberId
   * @return
   * @throws BizException
   */
	public Long inviteBeShopkeeper(Long tenantId, Long memberId) throws BizException;
	/**
	 * 成为店主无推荐人
	 * @param tenantId
	 * @param memberId
	 */
	public void beShopKeeper(Long tenantId,Member member) throws BizException;
	
    /**
     * 成为店主有邀请人
     * @param tenantId
     * @param memberId
     * @param recommandId
     * @throws BizException
     */
    public void beShopKeeper(Long tenantId,Member memberId,Long recommandId) throws BizException;
	
	public List<TenantShopkeeper> findShopKeeperByTenantId(Long tenantId);
    /**
     * 根据用户ID获取
     * @param memberId
     * @return
     */
    public TenantShopkeeper findShopKeeperByMemberId(Long memberId);
	
    public List<TenantShopkeeper> findInvationsByMemberId(Long memberId);

    public List<TenantShopkeeper> getInvationsByRecommendMemberId(Long recommendMemberId);
    
    /**获取用户+该企业的邀请函
     * return null : 没资格或者已经是店主
     *  list.size() == 0 说明没有邀请人
     *  list.size()>0 说明有邀请人,由用户选择;
     * */
	public List<TenantShopkeeper> findInvationsByMemberId(Long memberId,
			Long tenantId) throws BizException;
	/**
	 * 根据用户id 获取该用户的登录类型
	 * @param memberId
	 * @return
	 */
	public String getMemeberLoginTyper(Long memberId);
	
	/**
	 * 店主邀请函领取
	 * @param shopKeeperId 店主ID
	 * @param member  被邀请人对象;
	 */
	public void inviteShopkeeper(Long shopKeeperId, Member member) throws BizException;
	
	/**
	 * 根据店主ID获取企业店对象;
	 * @param shopKeeperId
	 * @throws BizException 
	 */
	public TenantShopkeeper getTenantByShopKeeper(Long shopKeeperId) throws BizException;
	
	/**
	 * 删除该用户的邀请函即 isShopKeeper = 0的记录
	 * @param member
	 */
	public void deleteShopKeeperWithNo(Member member);
	
	/**
	 * 签收后获得开通店长邀请
	 * @param tenantId
	 * @param memberId
	 * @param recommandMemberId
	 * @return
	 */
	public Long inviteBeShopkeeper(Long tenantId, Long memberId,
			Long recommandMemberId) throws BizException;



    public int deleteShopKeeperByMemberIdAndTenantId(String memberId, String tenantId);
    
    /**
     * 查找店长信息（店长审批可以调用传入 ）
     * @param tenant
     * @param isShopkeeper
     * @param invitedType 
     * @param pageable
     * @return
     */
    public Page<TenantShopkeeper> findPage(Tenant tenant,IsShopkeeper isShopkeeper,InvitedType invitedType,Pageable pageable);
	  /**
     * 根据条件查询店主信息 
     * @param tenant
     * @return
     */
	Page<TenantShopkeeper> findPageSearch(String name,Tenant tenant,Pageable pageable,String mobile);
	
	/**
	 * 获取本月成功邀请店长的个数
	 * @param owner
	 * @return
	 */
	public List<TenantShopkeeper> getInvitationsThisMonth(Member owner);
	
	
	/**
	 * 成为有资格成为店主的人
	 * @param tenant
	 * @param member
	 */
	public void beCapableShopkeeper(Tenant tenant,Member member) throws BizException;
	
	/**
	 * 买家端
	 * 会员申请成店长
	 * @param tenant
	 * @param member
	 * @return
	 */
	public List<TenantShopkeeper> getCanApplyByMember(Tenant tenant, Member member);
	
	/**
	 * 店长端
	 * 获取店长可以邀请的会员
	 * @param tenant
	 * @param recommand
	 * @return
	 */
	public List<TenantShopkeeper> getCapableByRecommand(Tenant tenant, Member recommand);
	/**
	 * 买家端
	 * 获取买家可以被谁邀请的列表
	 * @param tenant
	 * @param member
	 * @return
	 */
	public List<TenantShopkeeper> getCapableByMember(Tenant tenant, Member member);
	
	/**
	 * 获取买家申请店长在企业后台审核中的记录
	 * @param tenant
	 * @param member
	 * @return
	 */
	public List<TenantShopkeeper> getTenantCapableByMember(Tenant tenant, Member member);
	/**
	 * 获取该会员在这个企业的邀请函
	 * @param tenant
	 * @param member
	 * @return
	 */
	public List<TenantShopkeeper> getInvationByTenantMember(Tenant tenant,
			Member member);
	/**
	 * 获取已经受邀的邀请函
	 * @param tenant
	 * @param recommand
	 * @return
	 */
	public List<TenantShopkeeper> getInvitedByRecommand(Tenant tenant, Member recommand);
	
	/**
	 * 获取会员的邀请函列表
	 * @param member
	 * @return
	 */
	public List<TenantShopkeeper> getInvationByMember(Member member);

	/**
	 * 根据企业及会员获取该会员在这个企业的店主信息;
	 * @param tenant
	 * @param member
	 * @return
	 */
	public TenantShopkeeper getShopkeeperByTenantAndMember(Tenant tenant, Member member);
	
	/**
	 * 后台审核店主/vip 申请处理方法
	 * @param ids
	 * @param isPass
	 * @return
	 */
	public List<SystemMessageVO> processApply(Long ids[],Boolean isPass);
	
	/**
	 * 根据推荐人获取已经邀请的总数；
	 * @param recommand
	 * @return
	 */
	public Integer getInvationsByRecommand(Member recommand);
	
	public Long countByVipLevel(VipLevel vipLevel);
	
}
