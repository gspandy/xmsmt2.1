/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.util.Date;

import javax.persistence.*;

/**
 * Entity - 管理员
 * 
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_tenant_sm_details")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_tenant_sm_details_sequence")
public class TenantSmDetails extends BaseEntity {

	private static final long serialVersionUID = -7519486823153844426L;

//	private Integer id;// 主键
	
    private Tenant tenant;
    private Member member;
	private Date sendDate;// 发送时间
	
    private TenantSmContent tenantSmContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tenant_id")
    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="content_id")
    public TenantSmContent getTenantSmContent() {
        return tenantSmContent;
    }

    public void setTenantSmContent(TenantSmContent tenantSmContent) {
        this.tenantSmContent = tenantSmContent;
    }

    public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

}