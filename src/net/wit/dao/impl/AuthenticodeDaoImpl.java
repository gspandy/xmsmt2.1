package net.wit.dao.impl;

import java.util.Collections;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.AuthenticodeDao;
import net.wit.entity.Authenticode;
import net.wit.entity.Authenticode.Status;
import net.wit.entity.Member;
import net.wit.entity.Tenant;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: www.insuper.com
 * </p>
 * <p>
 * Company: www.insuper.com
 * </p>
 * @author chenqifu
 * @version 1.0 Apr 3, 2013
 */

@Repository("authenticodeDaoImpl")
public class AuthenticodeDaoImpl extends BaseDaoImpl<Authenticode, Long> implements AuthenticodeDao {

	public Authenticode findBySn(String sn) {
		if (sn == null) {
			return null;
		}
		String jpql = "select authenticode from Authenticode authenticode where authenticode.sn = :sn";
		try {
			return entityManager.createQuery(jpql, Authenticode.class).setFlushMode(FlushModeType.COMMIT).setParameter("sn", sn).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Authenticode findByTenant(Tenant tenant) {
		if (tenant == null) {
			return null;
		}
		String jpql = "select authenticode from Authenticode authenticode where authenticode.tenant = :tenant";
		try {
			return entityManager.createQuery(jpql, Authenticode.class).setFlushMode(FlushModeType.COMMIT).setParameter("tenant", tenant).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}

	}

	public Page<Authenticode> findPage(Tenant tenant, Pageable pageable) {
		if (tenant == null) {
			return new Page<Authenticode>(Collections.<Authenticode> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Authenticode> criteriaQuery = criteriaBuilder.createQuery(Authenticode.class);
		Root<Authenticode> root = criteriaQuery.from(Authenticode.class);
		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.get("tenant"), tenant));
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Authenticode> findPage(Member member, List<Status> status, Pageable pageable) {
		if (member == null) {
			return new Page<Authenticode>(Collections.<Authenticode> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Authenticode> criteriaQuery = criteriaBuilder.createQuery(Authenticode.class);
		Root<Authenticode> root = criteriaQuery.from(Authenticode.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Member> get("member"), member));
		restrictions = criteriaBuilder.and(restrictions, root.get("status").in(status));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public List<Authenticode> findByMember(Member member, Tenant tenant, List<Status> status) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Authenticode> criteriaQuery = criteriaBuilder.createQuery(Authenticode.class);
		Root<Authenticode> root = criteriaQuery.from(Authenticode.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (member != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Member> get("member"), member));
		}
		if (tenant != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Tenant> get("tenant"), tenant));
		}
		if (status != null && status.size() > 0) {
			restrictions = criteriaBuilder.and(restrictions, root.get("status").in(status));
		}
		criteriaQuery.where(restrictions);
		return entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT).getResultList();
	}

	public List<Authenticode> findByMember(Member member) {
		if (member == null) {
			return null;
		}
		String jpql = "select authenticode from Authenticode authenticode where (authenticode.member = :member and authenticode.tenant is not null)";
		try {
			return  entityManager.createQuery(jpql, Authenticode.class).setFlushMode(FlushModeType.COMMIT).setParameter("member", member).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

}
