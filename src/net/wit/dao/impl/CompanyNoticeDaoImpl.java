package net.wit.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.CompanyNoticeDao;
import net.wit.entity.CompanyNotice;
import net.wit.entity.Tenant;

@Repository("companyNoticeDaoImpl")
public class CompanyNoticeDaoImpl extends BaseDaoImpl<CompanyNotice, Long> implements CompanyNoticeDao{

	@Override
	public Page<CompanyNotice> PageALL(Tenant tenant,Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<CompanyNotice> criteriaQuery = criteriaBuilder.createQuery(CompanyNotice.class);
		Root<CompanyNotice> root = criteriaQuery.from(CompanyNotice.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);	 
	}

}
