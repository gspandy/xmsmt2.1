/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;



import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;




/**
 * Entity - 广告内容
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_invoice_management")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_invoice_management_sequence")
public class InvoiceManagement extends BaseEntity {

	private static final long serialVersionUID = -1307743303786909390L;
	
	/** 开票状态 */
	public enum InvoiceStat {
		/** 未开票 */
		no, /** 已开票 */
		yes, /** 取消开票 */
		cancel
	}
	private Order order;
	
	private Tenant tenant;
	
	private InvoiceContent invoiceContent;
	
	private BigDecimal invoiceValue;
	
	private String invoiceTitle;
	
	private InvoiceStat invoiceStat;
	
	private Date invoiceDate;
	
	private String remark;
	/**
	 * 获取 Order
	 * @return Order
	 */
	@JoinColumn(name = "order_id")
	@OneToOne(fetch = FetchType.LAZY)
	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
	
	/**
	 * 获取 tenant
	 * @return tenant
	 */
	@JoinColumn(name = "tenant_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
	/**
	 * 获取 InvoiceContent
	 * @return InvoiceContent
	 */
	@JoinColumn(name = "content_id")
	@ManyToOne(fetch = FetchType.LAZY)
	public InvoiceContent getInvoiceContent() {
		return invoiceContent;
	}

	public void setInvoiceContent(InvoiceContent invoiceContent) {
		this.invoiceContent = invoiceContent;
	}

	public BigDecimal getInvoiceValue() {
		return invoiceValue;
	}

	public void setInvoiceValue(BigDecimal invoiceValue) {
		this.invoiceValue = invoiceValue;
	}

	public String getInvoiceTitle() {
		return invoiceTitle;
	}

	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}
	public InvoiceStat getInvoiceStat() {
		return invoiceStat;
	}

	public void setInvoiceStat(InvoiceStat invoiceStat) {
		this.invoiceStat = invoiceStat;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}