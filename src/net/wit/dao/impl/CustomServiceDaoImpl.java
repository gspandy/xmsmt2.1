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
import net.wit.dao.CustomServiceDao;
import net.wit.entity.Admin;
import net.wit.entity.CustomService;
import net.wit.entity.Tenant;
import net.wit.entity.TenantShopkeeper;
import net.wit.entity.Ticket;

@Repository("customServiceDaoImpl")
public class CustomServiceDaoImpl extends BaseDaoImpl<CustomService, Long> implements CustomServiceDao{

	@Override
	public List<CustomService> findALL(long tenantId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<CustomService> criteriaQuery = criteriaBuilder.createQuery(CustomService.class);
		Root<CustomService> root = criteriaQuery.from(CustomService.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenantId"), tenantId));
		criteriaQuery.where(restrictions);
		List<CustomService> list = super.findList(criteriaQuery);
		if(list.size()==0)
		{
			return new ArrayList<CustomService>();
		}
		return list;
	}

	@Override
	public CustomService getCustomServiceByAdmin(Admin admin) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<CustomService> criteriaQuery = criteriaBuilder.createQuery(CustomService.class);
		Root<CustomService> root = criteriaQuery.from(CustomService.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("admin"), admin));
		criteriaQuery.where(restrictions);
		List<CustomService> list = super.findList(criteriaQuery);
		if(list.size()==0)
		{
			return null;
		}
		return list.get(0);
	}

	@Override
	public Page<CustomService> findPage(Tenant tenant, Pageable pageable) {
		if(tenant==null){
			return new Page<CustomService>();
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<CustomService> criteriaQuery = criteriaBuilder.createQuery(CustomService.class);
		Root<CustomService> root = criteriaQuery.from(CustomService.class);
		Predicate restriction = criteriaBuilder.conjunction();
		restriction = criteriaBuilder.and(restriction, criteriaBuilder.equal(root.get("tenantId"), tenant.getId()));
		criteriaQuery.select(root);
		criteriaQuery.where(restriction);
		return super.findPage(criteriaQuery, pageable);	}

}
