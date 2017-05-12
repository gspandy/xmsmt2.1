package net.wit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import jodd.jtx.meta.Transaction;

import org.springframework.stereotype.Service;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.GoodsDao;
import net.wit.dao.InvoiceManagementDao;
import net.wit.entity.InvoiceManagement;
import net.wit.entity.InvoiceManagement.InvoiceStat;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.Order.ShippingStatus;
import net.wit.entity.Tenant;
import net.wit.service.InvoiceManagementService;

@Service("invoiceManagementServiceImpl")
public class InvoiceManagementServiceImpl extends
		BaseServiceImpl<InvoiceManagement, Long> implements
		InvoiceManagementService {

	@Resource(name = "invoiceManagementDaoImpl")
	private InvoiceManagementDao invoiceManagementDao;

	@Resource(name = "invoiceManagementDaoImpl")
	public void setBaseDao(InvoiceManagementDao invoiceManagementDao) {
		super.setBaseDao(invoiceManagementDao);
	}

	/**
	 * 根据企业编号分页查询出其所有的发票管理
	 */
	@Transaction(readOnly = true)
	public Page<InvoiceManagement> findInvoiceManagementsPage(Tenant tenant,
			Pageable pageable) {

		return invoiceManagementDao
				.findInvoiceManagementsPage(tenant, pageable);

	}

	/**
	 * 根据企业编号查询出所有的发票
	 */
	public List<InvoiceManagement> findInvoiceManagementList(Tenant tenant) {
		return invoiceManagementDao.findInvoiceManagementList(tenant);
	}

	/**
	 * 根据不同的筛选条件查询发票信息
	 */
	public Page<InvoiceManagement> findPageByCriteria(Tenant tenant,
			List<OrderStatus> orderStatuses,
			List<PaymentStatus> paymentStatuses,
			List<ShippingStatus> shippingStatuses,
			List<InvoiceStat> invoiceStat, Date firstDayOfMonth,
			Date lastDayOfMonth, Pageable pageable) {
		return invoiceManagementDao.findPageByCriteria(tenant, orderStatuses,
				paymentStatuses, shippingStatuses, invoiceStat,
				firstDayOfMonth, lastDayOfMonth, pageable);
	}

}
