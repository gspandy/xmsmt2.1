package net.wit.dao.impl;

import java.util.Collections;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.StockDao;
import net.wit.entity.Area;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.Product;
import net.wit.entity.Stock;
import net.wit.entity.Tenant;

import org.springframework.stereotype.Repository;

@Repository("stockDaoImpl")
public class StockDaoImpl extends BaseDaoImpl<Stock, Long> implements StockDao {

	public Page<Stock> findPage(Tenant tenant, Pageable pageable) {
		if (tenant == null) {
			return new Page<Stock>(Collections.<Stock> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Stock> criteriaQuery = criteriaBuilder.createQuery(Stock.class);
		Root<Stock> root = criteriaQuery.from(Stock.class);
		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.get("tenant"), tenant));
		return super.findPage(criteriaQuery, pageable);
	}

	public Stock findStock(Tenant tenant, Product product, DeliveryCenter deliveryCenter) {
		String jpql = "select stock from Stock stock where stock.tenant = :tenant and stock.product = :product and stock.deliveryCenter = :deliveryCenter";
		try {
			TypedQuery<Stock> typedQuery = entityManager.createQuery(jpql, Stock.class);
			typedQuery.setFlushMode(FlushModeType.COMMIT);
			typedQuery.setParameter("tenant", tenant);
			typedQuery.setParameter("product", product);
			typedQuery.setParameter("deliveryCenter", deliveryCenter);
			return typedQuery.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Stock find(Area area, Product product, Integer quantity) {
		if (area == null) {
			return null;
		}
		String sql = "select s.* from xx_stock s " +
				"left join xx_tenant t on s.tenant = t.id " +
				"left join xx_delivery_center d on s.delivery_center = d.id " +
				"left join xx_area a on d.area = a.id " +
				"left join xx_tenant_tag ta on t.id = ta.tenants " +
				"where t.member is not null " +
				"and d.tenant is not null and d.tenant <> 1 "+
				"and ta.tags = 9 and (d.area = '" + area.getId() +
				"' or a.parent = '" + area.getId() +
				"' ) and s.product = '" + product.getId() +
				"' and (s.stock - s.allocated_stock > " + quantity +
				" or s.stock - s.allocated_stock = " + quantity +
				" ) order by s.stock - s.allocated_stock desc;";
		try {
			Query query = entityManager.createNativeQuery(sql, Stock.class);
			List<Stock> list = query.getResultList();
			if(list == null || list.size() == 0){
				return null;
			}
			return list.get(0);
		} catch (NoResultException e) {
			return null;
		}
	}
}
