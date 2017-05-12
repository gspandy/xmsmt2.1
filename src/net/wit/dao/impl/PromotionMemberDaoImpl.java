/**
 *====================================================
 * 文件名称: PromotionMemberDaoImpl.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年4月28日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.dao.impl;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.PromotionMemberDao;
import net.wit.entity.Member;
import net.wit.entity.Promotion;
import net.wit.entity.PromotionMember;
import net.wit.entity.Promotion.Classify;
import net.wit.entity.PromotionMember.Status;

import org.springframework.stereotype.Repository;

/**
 * @ClassName: PromotionMemberDaoImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年4月28日 上午10:36:32
 */
@Repository("promotionMemberDao")
public class PromotionMemberDaoImpl extends BaseDaoImpl<PromotionMember, Long> implements PromotionMemberDao {

	public Page<PromotionMember> findPage(Promotion promotion, Status status, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<PromotionMember> criteriaQuery = criteriaBuilder.createQuery(PromotionMember.class);
		Root<PromotionMember> root = criteriaQuery.from(PromotionMember.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (status != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), status));
		}
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Promotion> get("promotion"), promotion));

		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<PromotionMember> findJoinAuctionPage(Pageable pageable, Member member, Classify classify) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<PromotionMember> criteriaQuery = criteriaBuilder.createQuery(PromotionMember.class);
		Root<PromotionMember> root = criteriaQuery.from(PromotionMember.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member").get("id"), member.getId()));
		if (classify != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("promotion").get("classify"), classify));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
}
