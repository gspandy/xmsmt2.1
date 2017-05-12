/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import javax.persistence.*;

/**
 * Entity - 管理员
 * 
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_tenant_sm")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_tenant_sm_sequence")
public class TenantSm extends BaseEntity {

	private static final long serialVersionUID = -7519486823153844426L;

    private Tenant tenant;
	private Integer smsCount;
	private Integer leftCount;
	private Integer usedCount;
	
	private String remark;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tenant_id")
    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Integer getSmsCount() {
		return smsCount;
	}

	public void setSmsCount(Integer smsCount) {
		this.smsCount = smsCount;
	}

	public Integer getLeftCount() {
		return leftCount;
	}

	public void setLeftCount(Integer leftCount) {
		this.leftCount = leftCount;
	}

	public Integer getUsedCount() {
		return usedCount;
	}

	public void setUsedCount(Integer usedCount) {
		this.usedCount = usedCount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}