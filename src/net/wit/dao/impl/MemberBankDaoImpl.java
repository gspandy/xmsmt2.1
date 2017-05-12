/**
 *====================================================
 * 文件名称: MemberBankDaoImpl.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年7月30日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.wit.dao.MemberBankDao;
import net.wit.entity.Member;
import net.wit.entity.MemberBank;
import net.wit.entity.Tenant;
import net.wit.entity.TenantTicket;

/**
 * @ClassName: MemberBankDaoImpl
 * @Description: 会员银行卡
 * @author Administrator
 * @date 2014年7月30日 上午9:03:54
 */
@Repository("memberBankDaoImpl")
public class MemberBankDaoImpl extends BaseDaoImpl<MemberBank, Long> implements MemberBankDao {

	public List<MemberBank> findListByMember(Member member) {
		if (member == null) {
			return new ArrayList<MemberBank>();
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<MemberBank> criteriaQuery = criteriaBuilder.createQuery(MemberBank.class);
		Root<MemberBank> root = criteriaQuery.from(MemberBank.class);
		criteriaQuery.where(criteriaBuilder.equal(root.get("member"), member));
		criteriaQuery.select(root);
		return super.findList(criteriaQuery, null, null, null, null);
	}

	@Override
	public List<MemberBank> getMemberBankByTenantId(Long tenantId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<MemberBank> criteriaQuery = criteriaBuilder.createQuery(MemberBank.class);
		Root<MemberBank> root = criteriaQuery.from(MemberBank.class);
		Tenant tenant=new Tenant();
		tenant.setId(tenantId);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		criteriaQuery.where(restrictions);
		List<MemberBank> list = super.findList(criteriaQuery);
		return list;
	}

	@Override
	public MemberBank findMember(Member member) {
		if (member == null) {
			return null;
		}
		try {
			String jpql = "select memberBank from MemberBank memberBank where MemberBank.member = :member";
			return entityManager.createQuery(jpql, MemberBank.class).setFlushMode(FlushModeType.COMMIT).setParameter("member", member).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public MemberBank findBank(String requestid) {
		if (requestid == null) {
			return null;
		}
		try {
			String jpql = "select memberBank from MemberBank memberBank where MemberBank.requestid = :requestid";
			return entityManager.createQuery(jpql, MemberBank.class).setFlushMode(FlushModeType.COMMIT).setParameter("requestid", requestid).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
