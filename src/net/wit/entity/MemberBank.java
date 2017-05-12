/**
 *====================================================
 * 文件名称: MemberBank.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年7月29日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @ClassName: MemberBank
 * @Description: 会员银行卡
 * @author Administrator
 * @date 2014年7月29日 下午3:15:46
 */
@Entity
@Table(name = "xx_member_bank")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_member_bank_sequence")
public class MemberBank extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/** 银行卡类型 */
	public static enum Type {
		/** 信用卡 */
		credit,
		/** 借记卡 */
		debit
	}

	/** 银行卡类型 */
	private Type type;

	/** 卡号 */
	private String cardNo;

	/** 有效期 */
	private Date validity;

	/** 还款日期--信用卡 */
	private Integer repaymentDay;

	/** 开户行 */
	private String depositBank;
	
	
	
	/** 开户名 */
	private String depositUser;
	
	/** 会员信息 */
	private Member member;
	
	/** 企业信息 */
	private Tenant tenant;
	
	private String bankProvince;// 开户省
	
	private String bankCity;// 开户市
	
	private String requestId;//修改状态请求号(null:修改成功状态，非空：正在修改状态)
	
	private Long bankId;//原银行卡记录id

	/**
	 * 银行卡类型
	 * @return 银行卡类型
	 */
	@Column(nullable = false)
	public Type getType() {
		return type;
	}

	/**
	 * 银行卡类型
	 * @param type 银行卡类型
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * 卡号
	 * @return 卡号
	 */
	@NotBlank
	@Column(nullable = false, length = 64)
	public String getCardNo() {
		return cardNo;
	}

	/**
	 * 卡号
	 * @param type 卡号
	 */
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	/**
	 * 有效期
	 * @return 有效期
	 */
	public Date getValidity() {
		return validity;
	}

	/**
	 * 有效期
	 * @param validity 有效期
	 */
	public void setValidity(Date validity) {
		this.validity = validity;
	}

	/**
	 * 信用卡还款日期
	 * @return 信用卡还款日期
	 */
	public Integer getRepaymentDay() {
		return repaymentDay;
	}

	/**
	 * 还款日期--信用卡
	 * @param repaymentDay 还款日期--信用卡
	 */
	public void setRepaymentDay(Integer repaymentDay) {
		this.repaymentDay = repaymentDay;
	}

	/**
	 * 开户行
	 * @return 开户行
	 */
	@NotBlank
	@Column(nullable = false, length = 100)
	public String getDepositBank() {
		return depositBank;
	}

	/**
	 * 开户行
	 * @param depositBank 开户行
	 */
	public void setDepositBank(String depositBank) {
		this.depositBank = depositBank;
	}

	/**
	 * 会员信息
	 * @return 会员信息
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public Member getMember() {
		return member;
	}

	/**
	 * 会员信息
	 * @param member 会员信息
	 */
	public void setMember(Member member) {
		this.member = member;
	}
	/**
	 * 开户行
	 * @return 开户行
	 */
	@NotBlank
	@JsonProperty
	@Column(nullable = false, length = 100)
	public String getDepositUser() {
		return depositUser;
	}

	public void setDepositUser(String depositUser) {
		this.depositUser = depositUser;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public String getBankProvince() {
		return bankProvince;
	}

	public void setBankProvince(String bankProvince) {
		this.bankProvince = bankProvince;
	}

	public String getBankCity() {
		return bankCity;
	}

	public void setBankCity(String bankCity) {
		this.bankCity = bankCity;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Long getBankId() {
		return bankId;
	}

	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}
    
}
