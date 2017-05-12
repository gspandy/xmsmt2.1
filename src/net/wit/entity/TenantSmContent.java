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
@Table(name = "t_tenant_sm_content")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_tenant_sm_content_sequence")
public class TenantSmContent extends BaseEntity {

	private static final long serialVersionUID = -7519486823153844426L;

//	private Integer id;// 主键
	private Tenant tenant;// 企业ID
	private String content;// 短信内容
	private Date updateDate;// 维护时间

//	public Integer getId() {
//		return id;
//	}
//
//	public void setId(Integer id) {
//		this.id = id;
//	}


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tenant_id")
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

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
}