package net.wit.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * <p>Title: 游戏快充 </p>
 *
 * <p> Description: </p>
 * <p>Copyright: www.rsico.cn</p>
 *
 * <p>Company: www.rsico.cn</p>
 *
 * @author chenqifu
 * @version 1.0
 * @2013-7-31
 */

@Entity
@Table(name = "xx_rebate")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_rebate_sequence")
public class Rebate extends BaseEntity{

	private static final long serialVersionUID = -541766724349671149L;
	
	/**
	 * 绑定状态
	 */
	public enum Status {

		/** 没入账 */
		none,

		/** 已入账*/
		success
	}
	
	/**
	 * 所属日期
	 */
	private Date rebateDate;
	
	/**
	 * 会员数1
	 */
	private int lv1MemberCount;

	/**
	 * 会员数2
	 */
	private int lv2MemberCount;
	
	/**
	 * 营业额1
	 */
	private BigDecimal lv1Amount;

	/**
	 * 反利1
	 */
	private BigDecimal lv1Rebate;

	/**
	 * 营业额2
	 */
	private BigDecimal lv2Amount;

	/**
	 * 反利2
	 */
	private BigDecimal lv2Rebate;

	/**
	 * 是否入账
	 */
	private Status status;
	
	/** 会员 */
	private Member member;

	public Date getRebateDate() {
		return rebateDate;
	}

	public void setRebateDate(Date rebateDate) {
		this.rebateDate = rebateDate;
	}

	public int getLv1MemberCount() {
		return lv1MemberCount;
	}

	public void setLv1MemberCount(int lv1MemberCount) {
		this.lv1MemberCount = lv1MemberCount;
	}

	public int getLv2MemberCount() {
		return lv2MemberCount;
	}

	public void setLv2MemberCount(int lv2MemberCount) {
		this.lv2MemberCount = lv2MemberCount;
	}

	public BigDecimal getLv1Amount() {
		return lv1Amount;
	}

	public void setLv1Amount(BigDecimal lv1Amount) {
		this.lv1Amount = lv1Amount;
	}

	public BigDecimal getLv1Rebate() {
		return lv1Rebate;
	}

	public void setLv1Rebate(BigDecimal lv1Rebate) {
		this.lv1Rebate = lv1Rebate;
	}

	public BigDecimal getLv2Amount() {
		return lv2Amount;
	}

	public void setLv2Amount(BigDecimal lv2Amount) {
		this.lv2Amount = lv2Amount;
	}

	public BigDecimal getLv2Rebate() {
		return lv2Rebate;
	}

	public void setLv2Rebate(BigDecimal lv2Rebate) {
		this.lv2Rebate = lv2Rebate;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	@Transient
	public BigDecimal getTotalRebate() {
		return getLv1Rebate().add(getLv2Rebate());
	}
	
}
