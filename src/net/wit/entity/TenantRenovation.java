/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * Entity - 管理员
 * 
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_tenant_renovation")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_tenant_renovation_sequence")
public class TenantRenovation extends BaseEntity {

	private static final long serialVersionUID = -7519486823153844426L;

	private Long tenantId;
	/**
	 * picId 是图片URL
	 */
	private String picId;     
	private Integer bannerNum;
	private String skipType;
    private Product product;
	private String url;
	
	private String remark;

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	
	public String getPicId() {
		return picId;
	}

	public void setPicId(String picId) {
		this.picId = picId;
	}

	public Integer getBannerNum() {
		return bannerNum;
	}

	public void setBannerNum(Integer bannerNum) {
		this.bannerNum = bannerNum;
	}

	public String getSkipType() {
		return skipType;
	}

	public void setSkipType(String skipType) {
		this.skipType = skipType;
	}

    @JsonProperty
    @JoinColumn(name = "product_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action=NotFoundAction.IGNORE)
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}


	
}