package net.wit.dao;

import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.BaseDao;
import net.wit.entity.Member;
import net.wit.entity.MemberTenant;

public interface MemberTenantDao extends BaseDao<MemberTenant, Long>{

	/**
	 * 新增企业与会员关系
	 * @param memberTenant
	 * @return
	 */
	public boolean addMemberTanent(MemberTenant memberTenant);
	
	/**
	 * 判断企业与会员关系是否存在?
	 * @param tenantId
	 * @param memberId
	 * @return
	 */
	public boolean getExistByTenantAndMember(Long tenantId ,Long memberId);
	
	public List<MemberTenant> getMemberTenantByTenant(Long tenantId);

    public void addMember(Member member);
}
