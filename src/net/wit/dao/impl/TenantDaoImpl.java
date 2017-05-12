package net.wit.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import net.wit.Filter;
import net.wit.Order;
import net.wit.Order.Direction;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.DeliveryCenterDao;
import net.wit.dao.TenantDao;
import net.wit.entity.Area;
import net.wit.entity.Community;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.Location;
import net.wit.entity.Member;
import net.wit.entity.Product;
import net.wit.entity.ProductCategory;
import net.wit.entity.ProductCategoryTenant;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.entity.TenantTicket;
import net.wit.entity.Tenant.Status;
import net.wit.entity.TenantCategory;

import org.apache.commons.lang.StringUtils;
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

@Repository("tenantDaoImpl")
public class TenantDaoImpl extends BaseDaoImpl<Tenant, Long>implements TenantDao {
	@Resource(name = "deliveryCenterDaoImpl")
	private DeliveryCenterDao deliveryCenterDao;

	private static final Pattern pattern = Pattern.compile("\\d*");

	public Tenant findByCode(String code) {
		if (code == null) {
			return null;
		}
		String jpql = "select tenant from Tenant tenant where tenant.code = :code";
		try {
			return entityManager.createQuery(jpql, Tenant.class).setFlushMode(FlushModeType.COMMIT).setParameter("code", code).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Tenant> findList(Area area, String name, Tag tag, Integer count) {
		// String jpql =
		// "select tenant from Tenant tenant,IN(tenant.tags) tags where tags=:tag and tenant.area=:area and tenant.name like :name order by tenant.score desc";
		String sql = "select * from xx_tenant t1 left join xx_tenant_tag t2 on t1.id = t2.tenants where 1=1";
		if (StringUtils.isNotEmpty(name)) {
			sql = sql + " and t1.name like '%" + name + "%'";
		}
		if (area != null) {
			sql = sql + " and t1.area='" + area.getId() + "'";
		}
		if (tag != null) {
			sql = sql + " and t2.tags = " + tag.getId() + " and t1.status = 2";
		}
		try {
			Query query = entityManager.createNativeQuery(sql, Tenant.class);
			// TypedQuery<Tenant> query = entityManager.createQuery(jpql,
			// Tenant.class).setFlushMode(FlushModeType.COMMIT).setParameter("name",
			// "%"+name+"%").setParameter("area", area).setParameter("tag",
			// tag);
			if (count != null) {
				query.setMaxResults(count);
			}
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Tenant> findList(Area area, Location location, BigDecimal distatce, Tag tag, Integer count) {
		List<Tenant> tenants = new ArrayList<Tenant>();
		Integer i = Integer.decode("0");
		List<DeliveryCenter> dc = deliveryCenterDao.findList(area, location, distatce);
		for (Iterator<DeliveryCenter> it = dc.iterator(); it.hasNext();) {
			DeliveryCenter d = it.next();
			if (d.getTenant() != null && d.getTenant().getTags() != null && d.getTenant().getTags().contains(tag)) {
				Tenant tenant = d.getTenant();
				tenant.setDistatce(d.getDistatce(location));
				tenants.add(tenant);
				i++;
				if (i.compareTo(count) <= 0) {
					break;
				}
			}
		}
		return tenants;
	}

	public List<Tenant> findNew(List<Tag> tags, Integer count) {
		String sql = "select * from xx_tenant t1 left join xx_tenant_tag t2 on t1.id = t2.tenants where 1=1";
		String t = null;
		for (Tag tag : tags) {
			if (tag != null) {
				if (t == null) {
					t = " and t2.tags in (" + tag.getId();
				} else {
					t = t + "," + tag.getId();
				}
			}
		}
		if (t != null) {
			t = t + " )";
			sql = sql + t;
		}
		sql = sql + " and t1.status = 2 order by create_date desc";
		try {
			Query query = entityManager.createNativeQuery(sql, Tenant.class);
			if (count != null) {
				query.setMaxResults(count);
			}
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Tenant> findList(TenantCategory tenantCategory, List<Tag> tags, Integer count, List<Filter> filters, List<Order> orders) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tenant> criteriaQuery = criteriaBuilder.createQuery(Tenant.class);
		Root<Tenant> root = criteriaQuery.from(Tenant.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (tenantCategory != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.equal(root.get("tenantCategory"), tenantCategory),
					criteriaBuilder.like(root.get("tenantCategory").<String> get("treePath"), "%" + TenantCategory.TREE_PATH_SEPARATOR + tenantCategory.getId() + TenantCategory.TREE_PATH_SEPARATOR + "%")));
		}
		if (tags != null && !tags.isEmpty()) {
			Subquery<Tenant> subquery = criteriaQuery.subquery(Tenant.class);
			Root<Tenant> subqueryRoot = subquery.from(Tenant.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("tags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery, null, count, filters, orders);
	}

	public List<Tenant> findList(TenantCategory tenantCategory, List<Tag> tags, Area area, Community community, Boolean periferal, Integer count, List<Filter> filters, List<Order> orders) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tenant> criteriaQuery = criteriaBuilder.createQuery(Tenant.class);
		Root<Tenant> root = criteriaQuery.from(Tenant.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (tenantCategory != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.equal(root.get("tenantCategory"), tenantCategory),
					criteriaBuilder.like(root.get("tenantCategory").<String> get("treePath"), "%" + TenantCategory.TREE_PATH_SEPARATOR + tenantCategory.getId() + TenantCategory.TREE_PATH_SEPARATOR + "%")));
		}
		if (tags != null && !tags.isEmpty()) {
			Subquery<Tenant> subquery = criteriaQuery.subquery(Tenant.class);
			Root<Tenant> subqueryRoot = subquery.from(Tenant.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("tags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Status> get("status"), Status.success));
		List<Long> tenants = new ArrayList<Long>();
		List<DeliveryCenter> dlvs = new ArrayList<DeliveryCenter>();
		if ((periferal != null) && (periferal)) {
			dlvs = deliveryCenterDao.findList(area, community.getLocation(), new BigDecimal(6));
		} else {
			dlvs = deliveryCenterDao.findList(area, community);
		}
		for (Iterator<DeliveryCenter> it = dlvs.iterator(); it.hasNext();) {
			DeliveryCenter dc = it.next();
			tenants.add(dc.getTenant().getId());
		}
		if (tenants.size() > 0) {
			restrictions = criteriaBuilder.and(restrictions, root.get("id").in(tenants));
		} else {
			return new ArrayList<Tenant>();
		}
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery, null, count, filters, orders);
	}

	public List<Tenant> findList(TenantCategory tenantCategory, Date beginDate, Date endDate, Integer first, Integer count) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tenant> criteriaQuery = criteriaBuilder.createQuery(Tenant.class);
		Root<Tenant> root = criteriaQuery.from(Tenant.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (tenantCategory != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.equal(root.get("tenantCategory"), tenantCategory),
					criteriaBuilder.like(root.get("tenantCategory").<String> get("treePath"), "%" + TenantCategory.TREE_PATH_SEPARATOR + tenantCategory.getId() + TenantCategory.TREE_PATH_SEPARATOR + "%")));
		}
		if (beginDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), beginDate));
		}
		if (endDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<Date> get("createDate"), endDate));
		}
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery, first, count, null, null);
	}

	public Page<Tenant> findPage(TenantCategory tenantCategory, List<Tag> tags, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tenant> criteriaQuery = criteriaBuilder.createQuery(Tenant.class);
		Root<Tenant> root = criteriaQuery.from(Tenant.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (tenantCategory != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.equal(root.get("tenantCategory"), tenantCategory),
					criteriaBuilder.like(root.get("tenantCategory").<String> get("treePath"), "%" + TenantCategory.TREE_PATH_SEPARATOR + tenantCategory.getId() + TenantCategory.TREE_PATH_SEPARATOR + "%")));
		}
		if (tags != null && !tags.isEmpty()) {
			Subquery<Tenant> subquery = criteriaQuery.subquery(Tenant.class);
			Root<Tenant> subqueryRoot = subquery.from(Tenant.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("tags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Tenant> findPage(Set<TenantCategory> tenantCategorys, List<Tag> tags, Area area, Community community, Boolean periferal, Location location, BigDecimal distatce, Pageable pageable) {
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Tenant> criteriaQuery = criteriaBuilder.createQuery(Tenant.class);
			Root<Tenant> root = criteriaQuery.from(Tenant.class);
			criteriaQuery.select(root);
			Predicate restrictions = criteriaBuilder.conjunction();
			if (tenantCategorys != null && tenantCategorys.size() > 0) {
				Predicate categoryPredicate = criteriaBuilder.conjunction();
				Predicate categoryPredicate1 = null;
				for (TenantCategory tenantCategory : tenantCategorys) {
					String categoryTreePath = TenantCategory.TREE_PATH_SEPARATOR + tenantCategory.getId() + TenantCategory.TREE_PATH_SEPARATOR;
					if (categoryPredicate1 == null) {
						categoryPredicate = criteriaBuilder.or(criteriaBuilder.equal(root.get("tenantCategory"), tenantCategory), criteriaBuilder.like(root.get("tenantCategory").<String> get("treePath"), "%" + categoryTreePath + "%"));
					} else {
						categoryPredicate = criteriaBuilder.or(categoryPredicate1,
								criteriaBuilder.or(criteriaBuilder.equal(root.get("tenantCategory"), tenantCategory), criteriaBuilder.like(root.get("tenantCategory").<String> get("treePath"), "%" + categoryTreePath + "%")));
					}
					categoryPredicate1 = criteriaBuilder.or(criteriaBuilder.equal(root.get("tenantCategory"), tenantCategory), criteriaBuilder.like(root.get("tenantCategory").<String> get("treePath"), "%" + categoryTreePath + "%"));
				}
				restrictions = criteriaBuilder.and(restrictions, categoryPredicate);
			}
			if (tags != null && !tags.isEmpty()) {
				Subquery<Tenant> subquery = criteriaQuery.subquery(Tenant.class);
				Root<Tenant> subqueryRoot = subquery.from(Tenant.class);
				subquery.select(subqueryRoot);
				subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("tags").in(tags));
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
			}
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Status> get("status"), Status.success));
			List<Long> tenants = new ArrayList<Long>();
			List<DeliveryCenter> dlvs = new ArrayList<DeliveryCenter>();
			if ((periferal != null) && periferal) {
				if (location == null && community != null) {
					dlvs = deliveryCenterDao.findList(community.getArea(), community.getLocation(), new BigDecimal(6));
				} else if (location.isExists()) {
					if (distatce == null) {
						distatce = new BigDecimal(6);
					} else {

					}
					dlvs = deliveryCenterDao.findList(area, location, distatce);
				}
			} else {
				dlvs = deliveryCenterDao.findList(area, community);
			}

			for (Iterator<DeliveryCenter> it = dlvs.iterator(); it.hasNext();) {
				DeliveryCenter dc = it.next();
				tenants.add(dc.getTenant().getId());
			}
			if (tenants.size() > 0) {
				restrictions = criteriaBuilder.and(restrictions, root.get("id").in(tenants));
			} else {
				return new Page<Tenant>(Collections.<Tenant> emptyList(), 0, pageable);
			}
			criteriaQuery.where(restrictions);
			return super.findPage(criteriaQuery, pageable);
		} catch (Exception e) {
			e.printStackTrace();
			return new Page<Tenant>();
		}
	}

	public Page<Tenant> findPage(Area area, List<Tag> tags, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tenant> criteriaQuery = criteriaBuilder.createQuery(Tenant.class);
		Root<Tenant> root = criteriaQuery.from(Tenant.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (area != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Area> get("area"), area));
		}
		if (tags != null && !tags.isEmpty()) {
			Subquery<Tenant> subquery = criteriaQuery.subquery(Tenant.class);
			Root<Tenant> subqueryRoot = subquery.from(Tenant.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("tags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Tenant> findAgency(Member member, Status status, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tenant> criteriaQuery = criteriaBuilder.createQuery(Tenant.class);
		Root<Tenant> root = criteriaQuery.from(Tenant.class);
		Predicate restriction = criteriaBuilder.conjunction();
		restriction = criteriaBuilder.and(restriction, criteriaBuilder.equal(root.get("member").get("member"), member));
		if (status != null) {
			restriction = criteriaBuilder.and(restriction, criteriaBuilder.equal(root.get("status"), status));
		}
		criteriaQuery.select(root);
		criteriaQuery.where(restriction);
		return super.findPage(criteriaQuery, pageable);
	}

	public long count(Member member, Date startTime, Date endTime, Status status) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tenant> criteriaQuery = criteriaBuilder.createQuery(Tenant.class);
		Root<Tenant> root = criteriaQuery.from(Tenant.class);
		Predicate restriction = criteriaBuilder.conjunction();
		restriction = criteriaBuilder.and(restriction, criteriaBuilder.equal(root.get("member").get("member"), member));
		if (status != null) {
			restriction = criteriaBuilder.and(restriction, criteriaBuilder.equal(root.get("status"), status));
		}
		if (startTime != null) {
			restriction = criteriaBuilder.and(restriction, criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), startTime));
		}
		if (endTime != null) {
			restriction = criteriaBuilder.and(restriction, criteriaBuilder.lessThanOrEqualTo(root.<Date> get("createDate"), endTime));
		}
		criteriaQuery.select(root);
		criteriaQuery.where(restriction);
		try {
			return super.count(criteriaQuery, null);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public List<ProductCategoryTenant> findRoots(Tenant tenant, Integer count) {
		String jpql = "select productCategoryTenant from ProductCategoryTenant productCategoryTenant where productCategoryTenant.parent is null and productCategoryTenant.tenant= (:tenant) order by productCategoryTenant.order asc";
		TypedQuery<ProductCategoryTenant> query = entityManager.createQuery(jpql, ProductCategoryTenant.class).setFlushMode(FlushModeType.COMMIT).setParameter("tenant", tenant);
		if (count != null) {
			query.setMaxResults(count);
		}
		return query.getResultList();
	}

	public Page<Tenant> mobileFindPage(Set<TenantCategory> tenantCategorys, List<Tag> tags, Area area, Community community, Boolean periferal, Location location, BigDecimal distatce, Pageable pageable) {
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Tenant> criteriaQuery = criteriaBuilder.createQuery(Tenant.class);
			Root<Tenant> root = criteriaQuery.from(Tenant.class);
			criteriaQuery.select(root);
			Predicate restrictions = criteriaBuilder.conjunction();
			if (tenantCategorys != null && tenantCategorys.size() > 0) {
				Predicate categoryPredicate = criteriaBuilder.conjunction();
				Predicate categoryPredicate1 = null;
				for (TenantCategory tenantCategory : tenantCategorys) {
					String categoryTreePath = TenantCategory.TREE_PATH_SEPARATOR + tenantCategory.getId() + TenantCategory.TREE_PATH_SEPARATOR;
					if (categoryPredicate1 == null) {
						categoryPredicate = criteriaBuilder.or(criteriaBuilder.equal(root.get("tenantCategory"), tenantCategory), criteriaBuilder.like(root.get("tenantCategory").<String> get("treePath"), "%" + categoryTreePath + "%"));
					} else {
						categoryPredicate = criteriaBuilder.or(categoryPredicate1,
								criteriaBuilder.or(criteriaBuilder.equal(root.get("tenantCategory"), tenantCategory), criteriaBuilder.like(root.get("tenantCategory").<String> get("treePath"), "%" + categoryTreePath + "%")));
					}
					categoryPredicate1 = criteriaBuilder.or(criteriaBuilder.equal(root.get("tenantCategory"), tenantCategory), criteriaBuilder.like(root.get("tenantCategory").<String> get("treePath"), "%" + categoryTreePath + "%"));
				}
				restrictions = criteriaBuilder.and(restrictions, categoryPredicate);
			}
			if (tags != null && !tags.isEmpty()) {
				Subquery<Tenant> subquery = criteriaQuery.subquery(Tenant.class);
				Root<Tenant> subqueryRoot = subquery.from(Tenant.class);
				subquery.select(subqueryRoot);
				subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("tags").in(tags));
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
			}
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Status> get("status"), Status.success));
			List<Long> tenants = new ArrayList<Long>();
			List<DeliveryCenter> dlvs = new ArrayList<DeliveryCenter>();
			if ((periferal != null) && periferal) {
				if (location == null && community != null) {
					dlvs = deliveryCenterDao.findList(community.getArea(), community.getLocation(), new BigDecimal(6));
				} else if (location.isExists()) {
					if (distatce == null) {
						distatce = new BigDecimal(6);
					} else {

					}
					dlvs = deliveryCenterDao.findList(area, location, distatce);
				}
			} else {
				dlvs = deliveryCenterDao.findList(area, community);
			}

			for (Iterator<DeliveryCenter> it = dlvs.iterator(); it.hasNext();) {
				DeliveryCenter dc = it.next();
				tenants.add(dc.getTenant().getId());
			}
			if (tenants.size() > 0) {
				restrictions = criteriaBuilder.and(restrictions, root.get("id").in(tenants));
			} else {
				return new Page<Tenant>(Collections.<Tenant> emptyList(), 0, pageable);
			}
			criteriaQuery.where(restrictions);
			return super.findPage(criteriaQuery, pageable);
		} catch (Exception e) {
			e.printStackTrace();
			return new Page<Tenant>();
		}
	}

	public Page<Tenant> findPage(Set<TenantCategory> tenantCategorys, List<Tag> tags, Area area, Community community, Boolean periferal, Pageable pageable) {
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Tenant> criteriaQuery = criteriaBuilder.createQuery(Tenant.class);
			Root<Tenant> root = criteriaQuery.from(Tenant.class);
			criteriaQuery.select(root);
			Predicate restrictions = criteriaBuilder.conjunction();
			if (tenantCategorys != null && tenantCategorys.size() > 0) {
				Predicate categoryPredicate = criteriaBuilder.conjunction();
				Predicate categoryPredicate1 = null;
				for (TenantCategory tenantCategory : tenantCategorys) {
					String categoryTreePath = TenantCategory.TREE_PATH_SEPARATOR + tenantCategory.getId() + TenantCategory.TREE_PATH_SEPARATOR;
					if (categoryPredicate1 == null) {
						categoryPredicate = criteriaBuilder.or(criteriaBuilder.equal(root.get("tenantCategory"), tenantCategory), criteriaBuilder.like(root.get("tenantCategory").<String> get("treePath"), "%" + categoryTreePath + "%"));
					} else {
						categoryPredicate = criteriaBuilder.or(categoryPredicate1,
								criteriaBuilder.or(criteriaBuilder.equal(root.get("tenantCategory"), tenantCategory), criteriaBuilder.like(root.get("tenantCategory").<String> get("treePath"), "%" + categoryTreePath + "%")));
					}
					categoryPredicate1 = criteriaBuilder.or(criteriaBuilder.equal(root.get("tenantCategory"), tenantCategory), criteriaBuilder.like(root.get("tenantCategory").<String> get("treePath"), "%" + categoryTreePath + "%"));
				}
				restrictions = criteriaBuilder.and(restrictions, categoryPredicate);
			}
			if (tags != null && !tags.isEmpty()) {
				Subquery<Tenant> subquery = criteriaQuery.subquery(Tenant.class);
				Root<Tenant> subqueryRoot = subquery.from(Tenant.class);
				subquery.select(subqueryRoot);
				subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("tags").in(tags));
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
			}
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Status> get("status"), Status.success));
			List<Long> tenants = new ArrayList<Long>();
			List<DeliveryCenter> dlvs = new ArrayList<DeliveryCenter>();
			if ((periferal != null) && periferal && community != null) {
				dlvs = deliveryCenterDao.findList(community.getArea(), community.getLocation(), new BigDecimal(6));
			} else {
				dlvs = deliveryCenterDao.findList(area, community);
			}

			for (Iterator<DeliveryCenter> it = dlvs.iterator(); it.hasNext();) {
				DeliveryCenter dc = it.next();
				tenants.add(dc.getTenant().getId());
			}
			if (tenants.size() > 0) {
				restrictions = criteriaBuilder.and(restrictions, root.get("id").in(tenants));
			} else {
				return new Page<Tenant>(Collections.<Tenant> emptyList(), 0, pageable);
			}
			criteriaQuery.where(restrictions);
			return super.findPage(criteriaQuery, pageable);
		} catch (Exception e) {
			e.printStackTrace();
			return new Page<Tenant>();
		}
	}

	public List<Tenant> tenantSelect(String keyword, Boolean isGift, int count) {
		if (StringUtils.isEmpty(keyword)) {
			return Collections.<Tenant> emptyList();
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tenant> criteriaQuery = criteriaBuilder.createQuery(Tenant.class);
		Root<Tenant> root = criteriaQuery.from(Tenant.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (pattern.matcher(keyword).matches()) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.like(root.<String> get("name"), "%" + keyword + "%"), criteriaBuilder.like(root.<String> get("shortName"), "%" + keyword + "%")));
		} else {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.like(root.<String> get("name"), "%" + keyword + "%"), criteriaBuilder.like(root.<String> get("shortName"), "%" + keyword + "%")));
		}
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery, null, count, null, null);
	}

	public List<Tenant> findNewest(List<Tag> tags, Integer count) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tenant> criteriaQuery = criteriaBuilder.createQuery(Tenant.class);
		Root<Tenant> root = criteriaQuery.from(Tenant.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (tags != null) {
			Subquery<Tenant> subquery = criteriaQuery.subquery(Tenant.class);
			Root<Tenant> subqueryRoot = subquery.from(Tenant.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("unionTags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		criteriaQuery.where(restrictions);
		List<Order> orders = new ArrayList<Order>();
		orders.add(new Order("modifyDate", Direction.desc));
		return super.findList(criteriaQuery, null, count, null, orders);
	}

	public Page<Tenant> findPage(Member member, Pageable pageable) {
		if (member == null) {
			return new Page<Tenant>(Collections.<Tenant> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tenant> criteriaQuery = criteriaBuilder.createQuery(Tenant.class);
		Root<Tenant> root = criteriaQuery.from(Tenant.class);
		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.join("favoriteMembers"), member));

		return super.findPage(criteriaQuery, pageable);
	}

	public List<Tenant> getTenantAll() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tenant> criteriaQuery = criteriaBuilder.createQuery(Tenant.class);
		Root<Tenant> root = criteriaQuery.from(Tenant.class);
		criteriaQuery.select(root);
		// Predicate restrictions = criteriaBuilder.conjunction();
		// restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tanentId"), tenantId));
		// criteriaQuery.where(restrictions);
		List<Tenant> list = super.findList(criteriaQuery);
		return list;
	}

	public List<Tenant> findList(ProductCategory productCategory) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tenant> criteriaQuery = criteriaBuilder.createQuery(Tenant.class);
		Root<Tenant> root = criteriaQuery.from(Tenant.class);
		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.join("productCategories"), productCategory));
		return super.findList(criteriaQuery);
	}
}
