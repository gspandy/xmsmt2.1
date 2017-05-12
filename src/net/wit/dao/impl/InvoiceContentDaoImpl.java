package net.wit.dao.impl;

import java.util.Collections;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.InvoiceContentDao;
import net.wit.entity.InvoiceContent;
import net.wit.entity.Tenant;

/**
 * 发票内容
 * @author zhang_yu
 *
 */
@Repository("invoiceContentDaoImpl")
public class InvoiceContentDaoImpl extends BaseDaoImpl<InvoiceContent, Long> implements InvoiceContentDao {
	
	/**
	 * 根据企业编号分页查询出其所有的发票内容
	 */
	public Page<InvoiceContent> findInvoiceContentsPage(Tenant tenant, Pageable pageable) {
		
		if (null == tenant) {
			return new Page<InvoiceContent>(Collections.<InvoiceContent> emptyList(), 0, pageable);
		}
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<InvoiceContent> criteriaQuery = criteriaBuilder.createQuery(InvoiceContent.class);
		Root<InvoiceContent> root = criteriaQuery.from(InvoiceContent.class);
		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.get("tenant"), tenant));
		Page<InvoiceContent> invoiceContentPages = super.findPage(criteriaQuery, pageable);
		return invoiceContentPages;
		
	}

	

}
