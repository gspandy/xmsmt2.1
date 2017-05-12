/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Entity - 管理员
 * 
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_tenant_shopkeeper")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_tenant_shopkeeper_sequence")
public class TenantShopkeeper extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	public enum IsShopkeeper{
		/**
		 * 还不是店主 表示为邀请函
		 */
		no,
		/**
		 * 已经是店主
		 */
		yes,
		/**
		 * 可以被邀请
		 */
		capable,
		/**   1.下载APP获取券，邀请类型为tenant，购买商品达到企业的要求可以申请成为店主,
		 *    2.被邀请进来，购买商品达到企业的要求状态为capable，邀请类型为shopkeeper
		 * 可以申请
		 */
		canApply
	}
	
	/**
	 *邀请类型
	 * @author dnzx
	 *
	 */
	public enum InvitedType{
		tenant,
		shopkeeper
	}

    private Tenant tenant;
    private Member member;
    private Member recommendMember;
	private IsShopkeeper isShopkeeper;
	private InvitedType invitedType;
	private Date openDate;
	private Date qualifyDate;
	private VipLevel vipLevel;




	/**
	 * 成为有资格的会员
	 * @param tenant
	 * @param member
	 * @param recommendMember
	 */
	public TenantShopkeeper(Tenant tenant,Member member,Member recommendMember,IsShopkeeper isShopkeeper,VipLevel vipLevel){
		this.tenant = tenant;
		this.member = member;
		this.recommendMember = recommendMember;
		this.isShopkeeper =isShopkeeper;
		this.invitedType = InvitedType.shopkeeper;
		this.qualifyDate = new Date();
		this.vipLevel = vipLevel;
	}
	
	
	/**
	 * 无推荐人的邀请人
	 */
	
	public TenantShopkeeper(Tenant tenant,Member member,IsShopkeeper isShopkeeper,VipLevel vipLevel){
		this.tenant = tenant;
		this.member = member;
		this.isShopkeeper =isShopkeeper;
		this.invitedType = InvitedType.tenant;
		this.qualifyDate = new Date();
		this.vipLevel = vipLevel;
	}
	
	
	public TenantShopkeeper(){
		
	}
	

    @JsonProperty
    @JoinColumn(name = "tenant_id")
    @ManyToOne(fetch = FetchType.LAZY)
    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    @JsonProperty
    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    @JsonProperty
    @JoinColumn(name = "recommend_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    public Member getRecommendMember() {
        return recommendMember;
    }

    public void setRecommendMember(Member recommendMember) {
        this.recommendMember = recommendMember;
    }

	public IsShopkeeper getIsShopkeeper() {
		return isShopkeeper;
	}
	public void setIsShopkeeper(IsShopkeeper isShopkeeper) {
		this.isShopkeeper = isShopkeeper;
	}
	public Date getOpenDate() {
		return openDate;
	}
	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}
	public Date getQualifyDate() {
		return qualifyDate;
	}
	public void setQualifyDate(Date qualifyDate) {
		this.qualifyDate = qualifyDate;
	}


	public InvitedType getInvitedType() {
		return invitedType;
	}


	public void setInvitedType(InvitedType invitedType) {
		this.invitedType = invitedType;
	}
	
	@JsonProperty
    @JoinColumn(name = "vip_level")
	@ManyToOne(fetch = FetchType.LAZY)
	public VipLevel getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(VipLevel vipLevel) {
		this.vipLevel = vipLevel;
	}
}