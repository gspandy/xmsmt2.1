package net.wit.dao;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.InvoiceContent;
import net.wit.entity.Tenant;

public interface InvoiceContentDao extends BaseDao<InvoiceContent, Long> {
	
	/**
	 * 根据企业编号分页查询出其所有的发票
	 * @param tenant 企业对象
	 * @param pageable  分页对象
	 * @return
	 */
	Page<InvoiceContent> findInvoiceContentsPage(Tenant tenant, Pageable pageable);
	
}
