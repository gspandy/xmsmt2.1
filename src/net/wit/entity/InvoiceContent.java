/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Entity - 广告内容
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_invoice_content")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_invoice_content_sequence")
public class InvoiceContent extends BaseEntity {

	private static final long serialVersionUID = -1307743303786909390L;

	private Tenant tenant;
	
	private String content;

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}