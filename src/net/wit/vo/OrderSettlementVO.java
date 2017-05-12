package net.wit.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import net.wit.entity.OrderSettlement;
import net.wit.entity.OrderSettlement.SettlementStatus;

/**
 */
public class OrderSettlementVO {

	//	m.name ownerName, m.mobile, o.sn, ts.order_amount orderAmount, 
	//ts.order_settle_amt orderSettleAmt, ts.settle_charge settleCharge,  ts.status, p.payment_date paymentDate
	private BigInteger id;
	private BigInteger orderId;
	private BigInteger tenant;
//	private Long orderId;
	/** 下单结算金额=下单金额-退货金额 */
	private BigDecimal orderSettleAmt;

	/** 结算佣金=下单佣金-退货佣金 */
	private BigDecimal settleCharge;

	private BigInteger memberId;
	
	private BigInteger ownerId;
	private BigDecimal orderAmount;
	private Integer status;
	private Date paymentDate;
	private String ownerName;
	private String mobile;
	private String recommendMobile;
	private String sn;
	private String recommendName;//推荐人
	private BigInteger recommandId;  //推荐人ID
	private String shareStatus;
	private String chargeDate;
	
	private BigDecimal recommonAmount;  //推荐奖金
	
	private Integer bonusLevel;
	
	private String statusName;
	
	 public String getShareStatus() {
		return shareStatus;
	}

	public void setShareStatus(String shareStatus) {
		this.shareStatus = shareStatus;
	}

	public String getChargeDate() {
		return chargeDate;
	}

	public void setChargeDate(String chargeDate) {
		this.chargeDate = chargeDate;
	}

	private BigDecimal totalAmount=BigDecimal.ZERO;//订单金额
	    private BigDecimal totalSettleCharge=BigDecimal.ZERO;
	    

	public BigDecimal getTotalAmount() {
			return totalAmount;
		}

		public void setTotalAmount(BigDecimal totalAmount) {
			this.totalAmount = totalAmount;
		}

		public BigDecimal getTotalSettleCharge() {
			return totalSettleCharge;
		}

		public void setTotalSettleCharge(BigDecimal totalSettleCharge) {
			this.totalSettleCharge = totalSettleCharge;
		}

	public String getRecommendMobile() {
		return recommendMobile;
	}

	public void setRecommendMobile(String recommendMobile) {
		this.recommendMobile = recommendMobile;
	}

	public BigDecimal getRecommonAmount() {
		return recommonAmount;
	}

	public void setRecommonAmount(BigDecimal recommonAmount) {
		this.recommonAmount = recommonAmount;
	}

	public BigInteger getTenant() {
		return tenant;
	}

	public void setTenant(BigInteger tenant) {
		this.tenant = tenant;
	}

	public String getRecommendName() {
		return recommendName;
	}

	public void setRecommendName(String recommendName) {
		this.recommendName = recommendName;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

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

	public BigInteger getOrderId() {
		return orderId;
	}

	public void setOrderId(BigInteger orderId) {
		this.orderId = orderId;
	}

	public BigInteger getMemberId() {
		return memberId;
	}

	public void setMemberId(BigInteger memberId) {
		this.memberId = memberId;
	}

	public BigInteger getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(BigInteger ownerId) {
		this.ownerId = ownerId;
	}



	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
	}



	
	public String getStatus() {
		if(this.status ==OrderSettlement.SettlementStatus.settlement.ordinal()) return "已结算";
		if(this.status ==OrderSettlement.SettlementStatus.complete.ordinal()) return "可结算";
		if(this.status ==OrderSettlement.SettlementStatus.cancel.ordinal())return "已取消";
		if(this.status ==OrderSettlement.SettlementStatus.recevied.ordinal())return "已发放";
		if(this.status ==OrderSettlement.SettlementStatus.uncomplete.ordinal())return"未结算";
		else return "";
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public BigInteger getRecommandId() {
		return recommandId;
	}

	public void setRecommandId(BigInteger recommandId) {
		this.recommandId = recommandId;
	}

	public Integer getBonusLevel() {
		return bonusLevel;
	}

	public void setBonusLevel(Integer bonusLevel) {
		this.bonusLevel = bonusLevel;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

}