package net.wit.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.BonusCalcDao;
import net.wit.entity.BonusCalc;
import net.wit.entity.Member;
import net.wit.entity.Tenant;

@Repository("bonusCalcDaoImpl")
public class BonusCalcDaoImpl extends BaseDaoImpl<BonusCalc, Long> implements BonusCalcDao{

	@Override
	public Page<BonusCalc> getBonusCalcList(Long memberId, Pageable pageable) {
		Member member = new Member();
		member.setId(memberId);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<BonusCalc> criteriaQuery = criteriaBuilder.createQuery(BonusCalc.class);
		Root<BonusCalc> root = criteriaQuery.from(BonusCalc.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"),member));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
	
	public Page<BonusCalc> findByChargeId(Long chargeId, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<BonusCalc> criteriaQuery = criteriaBuilder.createQuery(BonusCalc.class);
		Root<BonusCalc> root = criteriaQuery.from(BonusCalc.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("chargeId"),chargeId));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
	
	public void addBonusCalc(BonusCalc bonusCalc){
		entityManager.persist(bonusCalc);
	}
	@Override
	public List<BonusCalc> findBonusByTenant(Tenant tenant){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<BonusCalc> criteriaQuery = criteriaBuilder.createQuery(BonusCalc.class);
		Root<BonusCalc> root = criteriaQuery.from(BonusCalc.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNull(root.get("chargeId")));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), BonusCalc.Status.notSettle));
//		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("bonusTime"), bonusTime));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery);
	}
	
}
