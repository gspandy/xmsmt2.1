/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;



import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.TenantShopkeeperDao;
import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.entity.TenantShopkeeper;
import net.wit.entity.TenantShopkeeper.InvitedType;
import net.wit.entity.TenantShopkeeper.IsShopkeeper;
import net.wit.entity.VipLevel;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * Dao - 广告
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("tenantShopkeeperDaoImpl")
public class TenantShopkeeperDaoImpl extends BaseDaoImpl<TenantShopkeeper, Long> implements TenantShopkeeperDao {

	public List<TenantShopkeeper> findByTenantIdAndMember(Long tenantId,Long memberId){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TenantShopkeeper> criteriaQuery = criteriaBuilder.createQuery(TenantShopkeeper.class);
		Root<TenantShopkeeper> root = criteriaQuery.from(TenantShopkeeper.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
        Tenant tenant = new Tenant();
        tenant.setId(tenantId);
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
        Member member = new Member();
        member.setId(memberId);
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		criteriaQuery.where(restrictions);
		List<TenantShopkeeper> list = super.findList(criteriaQuery);
		return list;
	}
	
	public TenantShopkeeper findByTenantIdAndMemberAndRecommand(Long tenantId,Long memberId,Long recommandId){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TenantShopkeeper> criteriaQuery = criteriaBuilder.createQuery(TenantShopkeeper.class);
		Root<TenantShopkeeper> root = criteriaQuery.from(TenantShopkeeper.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
        Tenant tenant = new Tenant();
        tenant.setId(tenantId);
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
        Member member = new Member();
        member.setId(memberId);
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		
		if(recommandId!=null){
	        Member recommendMember = new Member();
	        recommendMember.setId(recommandId);
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("recommendMember"), recommendMember));
			criteriaQuery.where(restrictions);
			List<TenantShopkeeper> list = super.findList(criteriaQuery);
			if(list.size()==0) return null;
			else return list.get(0);
		}else{
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNull((root.get("recommendMember"))));
			criteriaQuery.where(restrictions);
			List<TenantShopkeeper> list = super.findList(criteriaQuery);
			if(list.size()>0) {
				return list.get(0);
			}
			else{
				return null;
			}
		}
	}
	
	public List<TenantShopkeeper> findByTenantIdAndIsShopkeeper(Long tenantId,IsShopkeeper isShopkeeper){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TenantShopkeeper> criteriaQuery = criteriaBuilder.createQuery(TenantShopkeeper.class);
		Root<TenantShopkeeper> root = criteriaQuery.from(TenantShopkeeper.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
        Tenant tenant = new Tenant();
        tenant.setId(tenantId);
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isShopkeeper"), isShopkeeper));
//		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("memberId"), memberId));
//		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("recommendMemberId"), recommandId));
		criteriaQuery.where(restrictions);
		List<TenantShopkeeper> list = super.findList(criteriaQuery);
		return list;
	}
	
	public List<TenantShopkeeper> findByMemeberIdAndIsShopkeeper(Long memeberId,IsShopkeeper isShopkeeper){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TenantShopkeeper> criteriaQuery = criteriaBuilder.createQuery(TenantShopkeeper.class);
		Root<TenantShopkeeper> root = criteriaQuery.from(TenantShopkeeper.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
        Member member = new Member();
        member.setId(memeberId);
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isShopkeeper"), isShopkeeper));
//		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("memberId"), memberId));
//		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("recommendMemberId"), recommandId));
		criteriaQuery.where(restrictions);
		List<TenantShopkeeper> list = super.findList(criteriaQuery);
		return list;
	}

    public List<TenantShopkeeper> getInvationsByRecommendMemberId(Long recommendMemberId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TenantShopkeeper> criteriaQuery = criteriaBuilder.createQuery(TenantShopkeeper.class);
        Root<TenantShopkeeper> root = criteriaQuery.from(TenantShopkeeper.class);
        criteriaQuery.select(root);
        Predicate restrictions = criteriaBuilder.conjunction();
        Member recommendMember = new Member();
        recommendMember.setId(recommendMemberId);
        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("recommendMember"), recommendMember));
        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isShopkeeper"), IsShopkeeper.yes));
        criteriaQuery.where(restrictions);
        List<TenantShopkeeper> list = super.findList(criteriaQuery);
        return list;
    }
    
    @Override
    public Long getInvationsCount(Long recommendMemberId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TenantShopkeeper> criteriaQuery = criteriaBuilder.createQuery(TenantShopkeeper.class);
        Root<TenantShopkeeper> root = criteriaQuery.from(TenantShopkeeper.class);
        criteriaQuery.select(root);
        Predicate restrictions = criteriaBuilder.conjunction();
        Member recommendMember = new Member();
        recommendMember.setId(recommendMemberId);
        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("recommendMember"), recommendMember));
        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isShopkeeper"), IsShopkeeper.yes));
        criteriaQuery.where(restrictions);
        Long count = super.count(criteriaQuery, null);
        return count;
    }
    
	
	public List<TenantShopkeeper> findTenantShopkeeperByMemeberId(Long memberId){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TenantShopkeeper> criteriaQuery = criteriaBuilder.createQuery(TenantShopkeeper.class);
		Root<TenantShopkeeper> root = criteriaQuery.from(TenantShopkeeper.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
        Member member = new Member();
        member.setId(memberId);
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		criteriaQuery.where(restrictions);
		List<TenantShopkeeper> list = super.findList(criteriaQuery);
		return list;
	}
	
	public List<TenantShopkeeper> findByTenantIdAndRecommendId(Long tenantId,Long recommendId){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TenantShopkeeper> criteriaQuery = criteriaBuilder.createQuery(TenantShopkeeper.class);
		Root<TenantShopkeeper> root = criteriaQuery.from(TenantShopkeeper.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenantId"), tenantId));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("recommendId"), recommendId));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isShopkeeper"), TenantShopkeeper.IsShopkeeper.yes));
//		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("memberId"), memberId));
//		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("recommendMemberId"), recommandId));
		criteriaQuery.where(restrictions);
		List<TenantShopkeeper> list = super.findList(criteriaQuery);
		return list;
	}
	public List<TenantShopkeeper> findShopKeeperByTenantId(Long tenantId){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TenantShopkeeper> criteriaQuery = criteriaBuilder.createQuery(TenantShopkeeper.class);
		Root<TenantShopkeeper> root = criteriaQuery.from(TenantShopkeeper.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		Tenant tenant = new Tenant();
		tenant.setId(tenantId);
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isShopkeeper"), TenantShopkeeper.IsShopkeeper.yes));
		criteriaQuery.where(restrictions);
		List<TenantShopkeeper> list = super.findList(criteriaQuery);
		return list;
	}
    public List<TenantShopkeeper> findShopKeeperByMemberId(Long memberId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TenantShopkeeper> criteriaQuery = criteriaBuilder.createQuery(TenantShopkeeper.class);
        Root<TenantShopkeeper> root = criteriaQuery.from(TenantShopkeeper.class);
        criteriaQuery.select(root);
        Predicate restrictions = criteriaBuilder.conjunction();
        Member member = new Member();
        member.setId(memberId);
        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
        criteriaQuery.where(restrictions);
        List<TenantShopkeeper> list = super.findList(criteriaQuery);
        return list;
    }
    
    public TenantShopkeeper findShopKeeperByShopKeeperId(Long memberId){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TenantShopkeeper> criteriaQuery = criteriaBuilder.createQuery(TenantShopkeeper.class);
        Root<TenantShopkeeper> root = criteriaQuery.from(TenantShopkeeper.class);
        criteriaQuery.select(root);
        Predicate restrictions = criteriaBuilder.conjunction();
        Member member = new Member();
        member.setId(memberId);
        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isShopkeeper"), TenantShopkeeper.IsShopkeeper.yes));
        criteriaQuery.where(restrictions);
        List<TenantShopkeeper> list = super.findList(criteriaQuery);
        if(list.size()==0) return null;
        else return list.get(0);
    }
    
    public void deleteOtherShopKeeper(Member member){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TenantShopkeeper> criteriaQuery = criteriaBuilder.createQuery(TenantShopkeeper.class);
        Root<TenantShopkeeper> root = criteriaQuery.from(TenantShopkeeper.class);
        criteriaQuery.select(root);
        Predicate restrictions = criteriaBuilder.conjunction();
        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.notEqual(root.get("isShopkeeper"), TenantShopkeeper.IsShopkeeper.yes));
        criteriaQuery.where(restrictions);
        List<TenantShopkeeper> list = super.findList(criteriaQuery);
        for (TenantShopkeeper tenantShopkeeper : list) {
        	 super.remove(tenantShopkeeper);
		}
    }


    public int deleteShopKeeperByMemberIdAndTenantId(String memberId, String tenantId) {
        int rs = 0;
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TenantShopkeeper> criteriaQuery = criteriaBuilder.createQuery(TenantShopkeeper.class);
        Root<TenantShopkeeper> root = criteriaQuery.from(TenantShopkeeper.class);
        criteriaQuery.select(root);
        Predicate restrictions = criteriaBuilder.conjunction();
        Member member = new Member();
        member.setId(new Long(memberId));
        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
        Tenant tenant = new Tenant();
        tenant.setId(new Long(tenantId));
        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
        criteriaQuery.where(restrictions);
        List<TenantShopkeeper> list = super.findList(criteriaQuery);
        if (CollectionUtils.isNotEmpty(list)) {
            for (TenantShopkeeper tenantShopkeeper : list) {
                super.remove(tenantShopkeeper);
                rs ++;
            }
        }

        return rs;
    }


	@Override
	public Page<TenantShopkeeper> findPage(Tenant tenant,IsShopkeeper isShopkeeper,InvitedType invitedType,Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TenantShopkeeper> criteriaQuery = criteriaBuilder.createQuery(TenantShopkeeper.class);
		Root<TenantShopkeeper> root = criteriaQuery.from(TenantShopkeeper.class);
		Predicate restriction = criteriaBuilder.conjunction();
		if(tenant!=null){
			restriction = criteriaBuilder.and(restriction, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		if(invitedType!=null){
			restriction = criteriaBuilder.and(restriction, criteriaBuilder.equal(root.get("invitedType"), invitedType.ordinal()));
		}
	    restriction = criteriaBuilder.and(restriction, criteriaBuilder.equal(root.get("isShopkeeper"), isShopkeeper));
	    
		criteriaQuery.select(root);
		criteriaQuery.where(restriction);
		return super.findPage(criteriaQuery, pageable);
	}


	@Override
	public Page<TenantShopkeeper> findPageSearch(String name,Tenant tenant, Pageable pageable,String mobile) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TenantShopkeeper> criteriaQuery = criteriaBuilder.createQuery(TenantShopkeeper.class);
		Root<TenantShopkeeper> root = criteriaQuery.from(TenantShopkeeper.class);
		Predicate restriction = criteriaBuilder.conjunction();
		if(StringUtils.isNotEmpty(name)){
			restriction = criteriaBuilder.and(restriction, criteriaBuilder.like(root.get("member").<String> get("name"),  "%" + name + "%"));
		}
		if(tenant!=null){
			restriction = criteriaBuilder.and(restriction, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		if(StringUtils.isNotEmpty(mobile)){
			restriction = criteriaBuilder.and(restriction, criteriaBuilder.equal(root.get("member").<String> get("mobile"),mobile));
		}
	    restriction = criteriaBuilder.and(restriction, criteriaBuilder.equal(root.get("isShopkeeper"), TenantShopkeeper.IsShopkeeper.yes));
		criteriaQuery.select(root);
		criteriaQuery.where(restriction);
		return super.findPage(criteriaQuery, pageable);
	}
	@Override
	public List<TenantShopkeeper> findTenantShopkeeper(Tenant tenant , Member member,Member recommendMember, IsShopkeeper isShopkeeper,InvitedType invitedType){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TenantShopkeeper> criteriaQuery = criteriaBuilder.createQuery(TenantShopkeeper.class);
		Root<TenantShopkeeper> root = criteriaQuery.from(TenantShopkeeper.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if(tenant!=null){
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		if(member!=null){
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		}
		if(recommendMember!=null){
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("recommendMember"), recommendMember));
		}
		if(isShopkeeper!=null){
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isShopkeeper"), isShopkeeper));
		}
		if(invitedType!=null){
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("invitedType"), invitedType));
		}
			criteriaQuery.where(restrictions);
			return super.findList(criteriaQuery);
	}
	
	
	@Override
	public List<TenantShopkeeper> findTenantShopkeepersByRecommand(Member recommendMember,Date beginDate,Date endDate){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TenantShopkeeper> criteriaQuery = criteriaBuilder.createQuery(TenantShopkeeper.class);
		Root<TenantShopkeeper> root = criteriaQuery.from(TenantShopkeeper.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("openDate"), beginDate));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThan(root.<Date> get("openDate"), endDate));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isShopkeeper"),IsShopkeeper.yes));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("recommendMember"), recommendMember));
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery);
	}
	
	@Override
	public Long countByVipLevel(VipLevel vipLevel){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TenantShopkeeper> criteriaQuery = criteriaBuilder.createQuery(TenantShopkeeper.class);
		Root<TenantShopkeeper> root = criteriaQuery.from(TenantShopkeeper.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("vipLevel"), vipLevel));
		criteriaQuery.where(restrictions);
		return super.count(criteriaQuery, null);
	}

}