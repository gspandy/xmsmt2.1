/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.MemberDao;
import net.wit.dao.MemberTenantDao;
import net.wit.entity.*;
import net.wit.entity.Order;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.PaymentStatus;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Dao - 会员
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("memberDaoImpl")
public class MemberDaoImpl extends BaseDaoImpl<Member, Long> implements MemberDao {

    @Autowired
    private MemberTenantDao memberTanentDao;

	public boolean usernameExists(String username) {
		if (username == null) {
			return false;
		}
		String jpql = "select count(*) from Member members where lower(members.username) = lower(:username)";
		Long count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT).setParameter("username", username).getSingleResult();
		return count > 0;
	}

	public boolean emailExists(String email) {
		if (email == null) {
			return false;
		}
		String jpql = "select count(*) from Member members where lower(members.email) = lower(:email)";
		Long count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT).setParameter("email", email).getSingleResult();
		return count > 0;
	}
	public boolean mobileExists(String mobile) {
		if (mobile == null) {
			return false;
		}
		String jpql = "select count(*) from Member members where members.mobile = :mobile";
		Long count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT).setParameter("mobile", mobile).getSingleResult();
		return count > 0;
	}

	public Member findByUsername(String username) {
		if (username == null) {
			return null;
		}
		try {
			String jpql = "select members from Member members where lower(members.username) = lower(:username)";
			return entityManager.createQuery(jpql, Member.class).setFlushMode(FlushModeType.COMMIT).setParameter("username", username).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Member> findListByEmail(String email) {
		if (email == null) {
			return Collections.<Member> emptyList();
		}
		String jpql = "select members from Member members where lower(members.email) = lower(:email)";
		return entityManager.createQuery(jpql, Member.class).setFlushMode(FlushModeType.COMMIT).setParameter("email", email).getResultList();
	}

	public List<Object[]> findPurchaseList(Date beginDate, Date endDate, Integer count) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);
		Root<Member> member = criteriaQuery.from(Member.class);
		Join<Product, Order> orders = member.join("orders");
		criteriaQuery.multiselect(member.get("id"), member.get("username"), member.get("email"), member.get("point"), member.get("amount"), member.get("balance"), criteriaBuilder.sum(orders.<BigDecimal> get("amountPaid")));
		Predicate restrictions = criteriaBuilder.conjunction();
		if (beginDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(orders.<Date> get("createDate"), beginDate));
		}
		if (endDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(orders.<Date> get("createDate"), endDate));
		}
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(orders.get("orderStatus"), OrderStatus.completed), criteriaBuilder.equal(orders.get("paymentStatus"), PaymentStatus.paid));
		criteriaQuery.where(restrictions);
		criteriaQuery.groupBy(member.get("id"), member.get("username"), member.get("email"), member.get("point"), member.get("amount"), member.get("balance"));
		criteriaQuery.orderBy(criteriaBuilder.desc(criteriaBuilder.sum(orders.<BigDecimal> get("amountPaid"))));
		TypedQuery<Object[]> query = entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT);
		if (count != null && count >= 0) {
			query.setMaxResults(count);
		}
		return query.getResultList();
	}
    public 	Page<Member> findPage(Member member, Pageable pageable) {
		if (member == null) {
			return new Page<Member>(Collections.<Member> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
		Root<Member> root = criteriaQuery.from(Member.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), member.getTenant()));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
    }
    //in条件查询
    public 	Page<Member> findPage(List<Long> memberID,String mobile, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
		Root<Member> root = criteriaQuery.from(Member.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if(mobile!=null){
		restrictions =criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("mobile"), mobile));
		}
		if(memberID.size()==0){
			return new Page<Member>();
		}
		restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.in(root.get("id")).value(memberID));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
    }
    
    public 	List<Member> findList(Member member) {
    	if (member == null) {
    		return new ArrayList<Member>(Collections.<Member> emptyList());
    	}
    	CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    	CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
    	Root<Member> root = criteriaQuery.from(Member.class);
    	criteriaQuery.select(root);
    	Predicate restrictions = criteriaBuilder.conjunction();
    	restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
    	criteriaQuery.where(restrictions);
    	return super.findList(criteriaQuery, null, null, null, null);
    }

    public 	Page<Member> findPage(Tenant tenant, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
		Root<Member> root = criteriaQuery.from(Member.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
	    if (tenant != null) {
	    	restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
    }

	public Page<Member> findRealnameMemberPage(Pageable pageable){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
		Root<Member> root = criteriaQuery.from(Member.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(root.get("idcard")), criteriaBuilder.equal(root.get("idcard").get("authStatus"), Idcard.AuthStatus.wait));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Member findByTel(String mobile) {
		if (mobile == null) {
			return null;
		}
		try {
			String jpql = "select members from Member members where lower(members.mobile) = lower(:mobile)";
			return entityManager.createQuery(jpql, Member.class).setFlushMode(FlushModeType.COMMIT).setParameter("mobile", mobile).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Member findByEmail(String email) {
		if (email == null) {
			return null;
		}
		try {
			String jpql = "select members from Member members where lower(members.email) = lower(:email)";
			return entityManager.createQuery(jpql, Member.class).setFlushMode(FlushModeType.COMMIT).setParameter("email", email).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public boolean emailExistsWithoutUser(String email,Member member) {
		if (email == null) {
			return false;
		}
		String jpql = "select count(*) from Member members where lower(members.email) = lower(:email) and members.id != :id";
		Long count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT).setParameter("email", email).setParameter("id", member.getId()).getSingleResult();
		return count > 0;
	}
	
	public boolean mobileExistsWithoutUser(String mobile,Member member) {
		if (mobile == null) {
			return false;
		}
		String jpql = "select count(*) from Member members where members.mobile = :mobile and members.id != :id";
		Long count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT).setParameter("mobile", mobile).setParameter("id", member.getId()).getSingleResult();
		return count > 0;
	}
	
	public Member findByBindTel(String mobile) {
		if (mobile == null) {
			return null;
		}
		try {
			String jpql = "select members from Member members where lower(members.mobile) = lower(:mobile) and members.bindMobile = 1";
			return entityManager.createQuery(jpql, Member.class).setFlushMode(FlushModeType.COMMIT).setParameter("mobile", mobile).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Member findByBindEmail(String email) {
		if (email == null) {
			return null;
		}
		try {
			String jpql = "select members from Member members where lower(members.email) = lower(:email) and members.bindEmail = 1";
			return entityManager.createQuery(jpql, Member.class).setFlushMode(FlushModeType.COMMIT).setParameter("email", email).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Page<Member> findFavoritePage(Member member, Pageable pageable) {
		if (member == null) {
			return new Page<Member>(Collections.<Member> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
		Root<Member> root = criteriaQuery.from(Member.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions,criteriaBuilder.equal(root.join("favoriteTenants"), member.getTenant()));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
	
	public void addMember(Member member){
		entityManager.persist(member);
	}
	

    public int addMembers(List<Object[]> addMembers, String comId) {
        int cnt = 0;
        if (CollectionUtils.isEmpty(addMembers)) {
            return cnt;
        }
        try {
            for (Object[] objects : addMembers) {
                Member member = new Member();
                member.setMobile((String)objects[0]);
                member.setName((String)objects[1]);
                member.setIdcarNo((String) objects[2]);
                member.setComId(new Long(comId));
                member.setEmail((String) objects[3]);

                entityManager.persist(member);

                MemberTenant memberTanent = new MemberTenant();
                memberTanent.setMemberId(member.getId());
                memberTanent.setTenantId(new Long(comId));
                memberTanent.setRelativeDate(new Date());
                memberTanent.setPhone(member.getMobile());

                memberTanentDao.addMemberTanent(memberTanent);

                cnt ++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cnt;
    }

    public boolean validateMember(String mobile, String comId) {
        boolean flag = false;
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(comId)) {
            return flag;
        }
        try {
            String jpql = "select members from Member members where lower(members.mobile) = lower(:mobile) and lower(members.comId) = lower(:comId)";
            List<Member> members = entityManager.createQuery(jpql, Member.class).setFlushMode(FlushModeType.COMMIT).setParameter("mobile", mobile).setParameter("comId", comId).getResultList();
            if (CollectionUtils.isNotEmpty(members)) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

	
	@Override
	public List<Member> findList(List<Long> id) {
		if (id == null) {
    		return new ArrayList<Member>(Collections.<Member> emptyList());
    	}
    	CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    	CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
    	Root<Member> root = criteriaQuery.from(Member.class);
    	criteriaQuery.select(root);
    	Predicate restrictions = criteriaBuilder.conjunction();
    	restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.in(root.get("id")).value(id));
    	criteriaQuery.where(restrictions);
    	return super.findList(criteriaQuery, null, null, null, null);
	}
	
	public List<Member> findMemberList(boolean isShop) {
    	CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    	CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
    	Root<Member> root = criteriaQuery.from(Member.class);
    	criteriaQuery.select(root);
    	Predicate restrictions = criteriaBuilder.conjunction();
    	restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isShop"), isShop));
    	criteriaQuery.where(restrictions);
    	return super.findList(criteriaQuery, null, null, null, null);
	}

	@Override
	public long count(Tenant tenant) {
		if (tenant == null) {
			String jpql = "select count(*) from Member";
			Long count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT).getSingleResult();
			return count;
		}
		String jpql = "select count(*) from MemberTenant memberTenant where memberTenant.tenantId = :tenantId";
		Long count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT).setParameter("tenantId", tenant.getId()).getSingleResult();
		return count;
	}

	@Override
	public Page<Member> findPage(List<Long> id, Pageable pageable,
			Boolean isRegister) {
		if (id.size()==0) {
    		return new Page<Member>();
    	}
    	CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    	CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
    	Root<Member> root = criteriaQuery.from(Member.class);
    	criteriaQuery.select(root);
    	Predicate restrictions = criteriaBuilder.conjunction();
    	restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.in(root.get("id")).value(id));
    	if(isRegister!=null&&isRegister){
    		restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.isNotNull(root.get("loginDate")));
    	}
    	if(isRegister!=null && !isRegister){
    		restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.isNull(root.get("loginDate")));
    	}
    	criteriaQuery.where(restrictions);
    	return super.findPage(criteriaQuery, pageable);
	}

	@Override
	public Page<Member> findPage(Tenant tenant, Pageable pageable,
			Boolean isRegister) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
		Root<Member> root = criteriaQuery.from(Member.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
	    if (tenant != null) {
	    	restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		if(isRegister!=null&&isRegister){
    		restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.isNotNull(root.get("loginDate")));
    	}
    	if(isRegister!=null && !isRegister){
    		restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.isNull(root.get("loginDate")));
    	}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
	@Override
	public List<Member> findListByIdAndName(Long id,String name) {
    	CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    	CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
    	Root<Member> root = criteriaQuery.from(Member.class);
    	criteriaQuery.select(root);
    	Predicate restrictions = criteriaBuilder.conjunction();
    	if(id!=null && !"".equals(id)){
    		restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.in(root.get("id")).value(id));
    	}
    	if(name!=null && !"".equals(name)){
    		restrictions = criteriaBuilder.and(criteriaBuilder.like(root.<String> get("name"), "%" + name + "%"));
    	}
    	criteriaQuery.where(restrictions);
    	return super.findList(criteriaQuery, null, null, null, null);
	}
}