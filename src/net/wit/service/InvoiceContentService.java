package net.wit.service;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.InvoiceContent;
import net.wit.entity.Tenant;

public interface InvoiceContentService extends BaseService<InvoiceContent, Long> {
	
	/**
	 * 根据企业编号分页查询出其所有的发票内容对象
	 * @param tenant 企业对象
	 * @param pageable 分页对象
	 * @return
	 */
	public Page<InvoiceContent> findInvoiceContentsPage(Tenant tenant, Pageable pageable);
	
}
