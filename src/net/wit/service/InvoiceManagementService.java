package net.wit.service;

import java.util.Date;
import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.InvoiceManagement;
import net.wit.entity.InvoiceManagement.InvoiceStat;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.Order.ShippingStatus;
import net.wit.entity.Tenant;

public interface InvoiceManagementService extends BaseService<InvoiceManagement, Long> {
	
	/**
	 * 根据企业编号分页查询出其所有的发票管理对象
	 * @param tenant 企业对象
	 * @param pageable 分页对象
	 * @return
	 */
	Page<InvoiceManagement> findInvoiceManagementsPage(Tenant tenant, Pageable pageable);
	
	/**
	 * 根据企业编号查询出所有的发票
	 * @param tenant 企业对象
	 * @return
	 */
	List<InvoiceManagement> findInvoiceManagementList(Tenant tenant);

	/**
	 * 根据不同的筛选条件查询发票信息
	 * @param tenant
	 * @param orderStatuses
	 * @param paymentStatuses
	 * @param shippingStatuses
	 * @param invoiceStat
	 * @param firstDayOfMonth
	 * @param lastDayOfMonth
	 * @param pageable
	 * @return
	 */
	Page<InvoiceManagement> findPageByCriteria(Tenant tenant, List<OrderStatus> orderStatuses,
			List<PaymentStatus> paymentStatuses,
			List<ShippingStatus> shippingStatuses,
			List<InvoiceStat> invoiceStat, Date firstDayOfMonth,
			Date lastDayOfMonth, Pageable pageable);

	
	
}
