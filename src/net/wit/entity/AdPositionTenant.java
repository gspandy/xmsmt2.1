/**
 *====================================================
 * 文件名称: AdPartakeTenant.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年8月14日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @ClassName: AdPartakeTenant
 * @Description: 商家申请广告位信息
 * @author Administrator
 * @date 2014年8月14日 上午11:26:48
 */
@Entity
@Table(name = "xx_ad_position_tenant")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_ad_position_tenant_sequence")
public class AdPositionTenant extends BaseEntity {

	private static final long serialVersionUID = -5932290235960967369L;

	/** 申请状态 */
	public static enum Status {
		/** 提交申请 */
		submit,
		/** 确认通过 */
		success,
		/** 申请拒绝 */
		fail
	}

	/** 申请状态 */
	private Status status;

	/** 申请备注 */
	private String memo;

	/** 操作员 */
	private String operator;

	/** 审批备注 */
	private String remark;

	/** 广告位 */
	private AdPosition adPosition;

	/** 申请商家 */
	private Tenant tenant;

	/**
	 * 获取申请状态
	 * @return 申请状态
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * 申请状态
	 * @param status 申请状态
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * 获取申请备注
	 * @return 申请备注
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * 申请备注
	 * @param memo 申请备注
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * 获取操作员
	 * @return 操作员
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * 操作员
	 * @param operator 操作员
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * 获取审批备注
	 * @return 审批备注
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * 审批备注
	 * @param remark 审批备注
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * 获取广告位
	 * @return 广告位
	 */
	public AdPosition getAdPosition() {
		return adPosition;
	}

	/**
	 * 广告位
	 * @param adPosition 广告位
	 */
	public void setAdPosition(AdPosition adPosition) {
		this.adPosition = adPosition;
	}

	/**
	 * 获取申请商家
	 * @return 申请商家
	 */
	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	public Tenant getTenant() {
		return tenant;
	}

	/**
	 * 申请商家
	 * @param tenant 申请商家
	 */
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

}
