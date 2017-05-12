package net.wit.service.impl;

import javax.annotation.Resource;

import jodd.jtx.meta.Transaction;

import org.springframework.stereotype.Service;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.InvoiceContentDao;
import net.wit.entity.InvoiceContent;
import net.wit.entity.Tenant;
import net.wit.service.InvoiceContentService;

@Service("invoiceContentServiceImpl")
public class InvoiceContentServiceImpl extends BaseServiceImpl<InvoiceContent, Long> implements InvoiceContentService {
	
	@Resource(name = "invoiceContentDaoImpl")
	private InvoiceContentDao invoiceContentDao;
	
	@Resource(name = "invoiceContentDaoImpl")
	public void setBaseDao(InvoiceContentDao invoiceContentDao) {
		super.setBaseDao(invoiceContentDao);
	}
	
	/**
	 * 根据企业编号分页查询出其所有的发票内容
	 */
	@Transaction(readOnly = true)
	public Page<InvoiceContent> findInvoiceContentsPage(Tenant tenant, Pageable pageable) {
		
		return invoiceContentDao.findInvoiceContentsPage(tenant, pageable);
		
	}
	
}
