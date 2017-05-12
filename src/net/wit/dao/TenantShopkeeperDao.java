package net.wit.dao;


import java.util.Date;
import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.entity.TenantShopkeeper;
import net.wit.entity.TenantShopkeeper.InvitedType;
import net.wit.entity.TenantShopkeeper.IsShopkeeper;
import net.wit.entity.VipLevel;

public interface TenantShopkeeperDao  extends BaseDao<TenantShopkeeper, Long>{

	/**
	 *获取企业的VIP店主
	 * @param tenantId
	 * @param isShopkeeper
	 * @return
	 */
	public List<TenantShopkeeper> findByTenantIdAndIsShopkeeper(Long tenantId,IsShopkeeper isShopkeeper);
	
	/**
	 * 获取企业的邀请有资格成为店主或者已经是店主
	 * @param tenantId
	 * @param memberId
	 * @return
	 */
	public List<TenantShopkeeper> findByTenantIdAndMember(Long tenantId,Long memberId);
	
	/**
	 * 根据企业ID+用户id+推荐人ID
	 * @param tenantId
	 * @param memberId
	 * @param recommandId
	 * @return
	 */
	public TenantShopkeeper findByTenantIdAndMemberAndRecommand(Long tenantId,Long memberId,Long recommandId);
	
	/**
	 * 查询该会员是否为店主
	 * @param memeberId
	 * @param isShopkeeper
	 * @return
	 */
	public List<TenantShopkeeper> findByMemeberIdAndIsShopkeeper(Long memeberId,IsShopkeeper isShopkeeper );
	/**
	 * 查询该店主的下属店主;
	 * @param tenantId
	 * @param recommendId
	 * @return
	 */
	public List<TenantShopkeeper> findByTenantIdAndRecommendId(Long tenantId,Long recommendId);
	/**
	 * 查询该企业所有店主店主;
	 * @param tenantId
	 * @param recommendId
	 * @return
	 */
	public List<TenantShopkeeper> findShopKeeperByTenantId(Long tenantId);

    /**
     * 查询店主;
     * @param memberId
     * @param recommendId
     * @return
     */
    public List<TenantShopkeeper> findShopKeeperByMemberId(Long memberId);
	
	/**
	 * 查询会员在TenantShopkeeper中的记录
	 * 用于判断该用户是不是店主或者该用户邀请函记录
	 * @param memeberId
	 * @return
	 */
	public List<TenantShopkeeper> findTenantShopkeeperByMemeberId(Long memberId);

	/**
	 * 获取店长的邀请的店长信息
	 * @param recommendMemberId
	 * @return
	 */
    public List<TenantShopkeeper> getInvationsByRecommendMemberId(Long recommendMemberId);
	
	
	/**
	 * 根据shopKeeperId 获取Tenant
	 * @param memberId
	 * @return
	 */
	public TenantShopkeeper findShopKeeperByShopKeeperId(Long memberId);
	
	
	/**
	 * 删除邀请函
	 * @param member
	 */
	public void deleteOtherShopKeeper(Member member);

    public int deleteShopKeeperByMemberIdAndTenantId(String memberId, String tenantId);
    /**
     * 根据企业查找店长信息 分页查询
     * @param tenant
     * @param isShopkeeper
     * @param invitedType
     * @param pageable
     * @return
     */
    public Page<TenantShopkeeper> findPage(Tenant tenant,IsShopkeeper isShopkeeper,InvitedType invitedType,Pageable pageable);
    
    Page<TenantShopkeeper> findPageSearch(String name,Tenant tenant,Pageable pageable,String mobile);
    /**
     * 获取该店长在时间内成功邀请的店长
     * @param recommendMember
     * @param beginDate
     * @param endDate
     * @return
     */
    public List<TenantShopkeeper> findTenantShopkeepersByRecommand(
			Member recommendMember, Date beginDate, Date endDate);

    public List<TenantShopkeeper> findTenantShopkeeper(Tenant tenant,
			Member member, Member recommendMember, IsShopkeeper isShopkeeper,
			InvitedType invitedType);

    /**
     * 获取店长的已经邀请人数
     * @param recommendMemberId
     * @return
     */
	public Long getInvationsCount(Long recommendMemberId);

	public Long countByVipLevel(VipLevel vipLevel);
}
