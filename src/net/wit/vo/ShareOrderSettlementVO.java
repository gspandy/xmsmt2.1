package net.wit.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import net.wit.entity.OrderSettlement;

/**
 */
public class ShareOrderSettlementVO {


//	private Long orderId;
	/** 下单结算金额=下单金额-退货金额 */
	private BigDecimal orderSettleAmt;

	/** 结算佣金=下单佣金-退货佣金 */
	private BigDecimal settleCharge;
	
	
	private Date payDate;
	
	private String ownerName;
	
	private String inviteName;
	
	private String ownerMobile;
	
	private String orderSn;
	
	private BigInteger chargeId;
	
	private OrderSettlement.SettlementStatus status;
	

	public BigDecimal getOrderSettleAmt() {
		return orderSettleAmt;
	}

	public void setOrderSettleAmt(BigDecimal orderSettleAmt) {
		this.orderSettleAmt = orderSettleAmt;
	}

	public BigDecimal getSettleCharge() {
		return settleCharge;
	}

	public void setSettleCharge(BigDecimal settleCharge) {
		this.settleCharge = settleCharge;
	}

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerMobile() {
		return ownerMobile;
	}

	public void setOwnerMobile(String ownerMobile) {
		this.ownerMobile = ownerMobile;
	}

	public String getOrderSn() {
		return orderSn;
	}

	public void setOrderSn(String orderSn) {
		this.orderSn = orderSn;
	}

	public BigInteger getChargeId() {
		return chargeId;
	}

	public void setChargeId(BigInteger chargeId) {
		this.chargeId = chargeId;
	}

	public OrderSettlement.SettlementStatus getStatus() {
		return status;
	}

	public void setStatus(OrderSettlement.SettlementStatus status) {
		this.status = status;
	}
	
	public String getOrderSettlementStatus(){
		if(this.chargeId==null && this.status.equals(OrderSettlement.SettlementStatus.settlement)) return "可结算";
		if(this.chargeId==null && this.status.equals(OrderSettlement.SettlementStatus.complete)) return "可结算";
		if(this.chargeId==null && this.status.equals(OrderSettlement.SettlementStatus.cancel)) return "已取消";
		if(this.chargeId!=null && this.status.equals(OrderSettlement.SettlementStatus.settlement)) return "已发放";
		if(this.chargeId==null && this.status.equals(OrderSettlement.SettlementStatus.uncomplete))return"未结算";
		else return "未结算";
	}

	public String getInviteName() {
		return inviteName;
	}

	public void setInviteName(String inviteName) {
		this.inviteName = inviteName;
	}

	


	



}