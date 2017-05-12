package net.wit.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.stereotype.Repository;

import net.wit.dao.AuthorityDao;
import net.wit.entity.Authority;
import net.wit.entity.Product;
import net.wit.entity.Role;
import net.wit.enums.AuthorityGroup;

/**
 * @ClassName：AuthorityDaoImpl @Description：
 * @author：Chenlf
 * @date：2015年9月9日 上午8:08:20
 */
@Repository("authorityDaoImpl")
public class AuthorityDaoImpl extends BaseDaoImpl<Authority, Long>implements AuthorityDao {

	public List<Authority> findRoots() {
		String jpql = "select at from Authority at where at.parent is null";
		return entityManager.createQuery(jpql, Authority.class).setFlushMode(FlushModeType.COMMIT).getResultList();
	}

	public List<Authority> findChildren(Authority authority, Integer count) {
		TypedQuery<Authority> query;
		if (authority != null) {
			String jpql = "select at from Authority at  where at.treePath like :treePath and at.grade=:grade  order by at.order asc";
			query = entityManager.createQuery(jpql, Authority.class).setFlushMode(FlushModeType.COMMIT).setParameter("treePath", "%" + Authority.TREE_PATH_SEPARATOR + authority.getId() + Authority.TREE_PATH_SEPARATOR + "%").setParameter("grade",
					authority.getGrade() + 1);
		} else {
			String jpql = "select at from Authority at order by at.order asc";
			query = entityManager.createQuery(jpql, Authority.class).setFlushMode(FlushModeType.COMMIT);
		}
		if (count != null) {
			query.setMaxResults(count);
		}
		return sort(query.getResultList(), authority);
	}

	private List<Authority> sort(List<Authority> authoritys, Authority parent) {
		List<Authority> result = new ArrayList<Authority>();
		if (authoritys != null) {
			for (Authority authority : authoritys) {
				if ((authority.getParent() != null && authority.getParent().equals(parent)) || (authority.getParent() == null && parent == null)) {
					result.add(authority);
					result.addAll(sort(authoritys, authority));
				}
			}
		}
		return result;
	}

	public List<Authority> findList(AuthorityGroup group, Set<Role> roles) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Authority> createQuery = criteriaBuilder.createQuery(Authority.class);
		Root<Authority> root = createQuery.from(Authority.class);
		createQuery.select(root);
		Predicate conjunction = criteriaBuilder.conjunction();
		if (group != null) {
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.equal(root.get("authorityGroup"), group));
		}
		if (roles != null && !roles.isEmpty()) {
			Subquery<Authority> tagsSubquery = createQuery.subquery(Authority.class);
			Root<Authority> tagsSubqueryRoot = tagsSubquery.from(Authority.class);
			tagsSubquery.select(tagsSubqueryRoot);
			tagsSubquery.where(criteriaBuilder.equal(tagsSubqueryRoot, root), tagsSubqueryRoot.join("roles").in(roles));
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.exists(tagsSubquery));
		}
		createQuery.where(conjunction);
		return super.findList(createQuery, null, null, null, null);
	}

}
