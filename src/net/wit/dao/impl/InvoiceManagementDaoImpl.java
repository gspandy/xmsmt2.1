package net.wit.dao.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.InvoiceManagementDao;
import net.wit.entity.InvoiceManagement;
import net.wit.entity.InvoiceManagement.InvoiceStat;
import net.wit.entity.Order;
import net.wit.entity.OrderItem;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.Order.ShippingStatus;
import net.wit.entity.Tenant;

/**
 * 发票管理
 * @author zhang_yu
 *
 */
@Repository("invoiceManagementDaoImpl")
public class InvoiceManagementDaoImpl extends BaseDaoImpl<InvoiceManagement, Long> implements InvoiceManagementDao {
	
	/**
	 * 根据企业编号分页查询出其所有的发票管理
	 */
	public Page<InvoiceManagement> findInvoiceManagementsPage(Tenant tenant, Pageable pageable) {
		
		if (tenant == null) {
			return new Page<InvoiceManagement>(Collections.<InvoiceManagement> emptyList(), 0, pageable);
		}
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<InvoiceManagement> criteriaQuery = criteriaBuilder.createQuery(InvoiceManagement.class);
		Root<InvoiceManagement> root = criteriaQuery.from(InvoiceManagement.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
        if(pageable.getSearchProperty()!=null&&pageable.getSearchProperty().equals("sn")){
        	 restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("order").<String>get("sn"), pageable.getSearchValue()));
        	 pageable.setSearchProperty(null);
        	 pageable.setSearchValue(null);
		}
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		criteriaQuery.where(restrictions);
		Page<InvoiceManagement> invoiceManagementPages = super.findPage(criteriaQuery, pageable);
		return invoiceManagementPages;
		
	}

	/**
	 * 根据企业编号查询出所有的发票
	 */
	public List<InvoiceManagement> findInvoiceManagementList(Tenant tenant) {
		
		if (null != tenant) {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<InvoiceManagement> criteriaQuery = criteriaBuilder.createQuery(InvoiceManagement.class);
			Root<InvoiceManagement> root = criteriaQuery.from(InvoiceManagement.class);
			criteriaQuery.select(root);
			criteriaQuery.where(criteriaBuilder.equal(root.get("tenant"), tenant));
			List<InvoiceManagement> invoiceManagementList = super.findList(criteriaQuery);
			return invoiceManagementList;
		}
		return null;
	}

	public Page<InvoiceManagement> findPageByCriteria(Tenant tenant,
			List<OrderStatus> orderStatuses,
			List<PaymentStatus> paymentStatuses,
			List<ShippingStatus> shippingStatuses,
			List<InvoiceStat> invoiceStat, Date firstDayOfMonth,
			Date lastDayOfMonth, Pageable pageable) {
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<InvoiceManagement> criteriaQuery = criteriaBuilder.createQuery(InvoiceManagement.class);
		Root<InvoiceManagement> root = criteriaQuery.from(InvoiceManagement.class);
		criteriaQuery.select(root);
		Predicate restrictions = createQuery(orderStatuses, paymentStatuses, shippingStatuses, criteriaBuilder, invoiceStat, root);
		if (tenant != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		if(firstDayOfMonth!=null)
		{
			restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), firstDayOfMonth));
		}
		if(lastDayOfMonth!=null)
		{
			restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.lessThanOrEqualTo(root.<Date> get("createDate"), lastDayOfMonth));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	protected Predicate createQuery(List<OrderStatus> orderStatuses,
			List<PaymentStatus> paymentStatuses,
			List<ShippingStatus> shippingStatuses,
			CriteriaBuilder criteriaBuilder, List<InvoiceStat> invoiceStat,
			Root<InvoiceManagement> root) {
		
		Predicate restrictions = criteriaBuilder.conjunction();
		if (orderStatuses != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("order").<Order.OrderStatus>get("orderStatus"), orderStatuses));
		}
		if (paymentStatuses != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("order").<Order.OrderStatus>get("paymentStatus"), paymentStatuses));
		}
		if (shippingStatuses != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("order").<Order.OrderStatus>get("shippingStatus"), shippingStatuses));
		}
		if (invoiceStat != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("invoiceStat"), invoiceStat));
		}
		return restrictions;
		
	}

}
