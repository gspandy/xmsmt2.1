package net.wit.vo;

import java.math.BigDecimal;
import java.util.Date;

import net.wit.entity.BonusCalc.Status;
import net.wit.entity.BonusCalc.Type;


/**
 */
public class AccountTransactionVO {
	/**
	 * 店主Id
	 */
	private Long memberId;
	private Long tenantId;
	/**
	 * 金额
	 */
	private BigDecimal amount;
	/**
	 * 
	 */
	private Date time;
	private Type type;
	
	private Status status;
	
	public enum Type {
		/** 本店销售奖金*/
		owner, /** 关联点销售奖金*/
		relateive

	}
	
	public enum Status {
		/** 未领取*/
		notReceiving , /** 审核中*/
		receiving,/** 已领取*/
		received,/** 退回*/
		returned
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}