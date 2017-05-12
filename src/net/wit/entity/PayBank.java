package net.wit.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * <p>Title: 银企互联 </p>
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
@Table(name = "xx_pay_bank")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_pay_bank_sequence")
public class PayBank extends BaseEntity{

	private static final long serialVersionUID = -245735963597251149L;
	
	/**
	 *交易代码
	 */
	private String tranCode;
	
	/**
	 * 终端号
	 */
	private String termNo;
	
	/**
	 * 商户号
	 */
	private String merNo;
	
	/**
	 * 系统跟踪号
	 */
	private String trackNo;

	/**
	 * 请求时间
	 */
	private String reqTime;

	/**
	 * 付款卡号
	 */
	private String payCardNo;

	/**
	 * 付款人银行账号
	 */
	private String payBankAccNo;

	/**
	 * 支付密码
	 */
	private String payPass;

	/**
	 * 支付类型
	 */
	private String payType;

	/**
	 * 支付方式
	 */
	private String payWay;

	/**
	 * 定向标志
	 */
	private String toFlag;

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 交易金额
	 */
	private BigDecimal amount;

	/**
	 * 交易日期
	 */
	private String tradeDate;

	/**
	 * 收款方卡号
	 */
	private String recCardNo;

	/**
	 * 收款人银行编码
	 */
	private String bankCode;

	/**
	 * 收款方开户行行号
	 */
	private String depositBankNo;

	/**
	 * 收款人银行账户类型
	 */
	private String bankAccType;

	/**
	 * 收款人银行账户属性
	 */
	private String bankAccProp;

	/**
	 * 收款人银行账号
	 */
	private String bankAccNo;

	/**
	 * 收款人银行账
	 */
	private String bankAccName;

	/**
	 * 收款人证件类型
	 */
	private String idType;

	/**
	 * 收款人证件号码
	 */
	private String idNo;

	/**
	 * 收款人手机号码
	 */
	private String mobNo;

	/**
	 * 摘要
	 */
	private String summary;

	/**
	 * 响应码
	 */
	private String respCode;
	
	/**
	 * 响应信息
	 */
	private String respMsg;
	
	/**
	 * 完成时间
	 */
	private String finishTime;
	
	/**
	 * 交易结果码
	 */
	private String retCode;
	
	/**
	 * 交易错误信息
	 */
	private String retMsg;
	
	/**
	 * 业务参考号
	 */
	private String busiRefNo;

	/**
	 * 交易后余额
	 */
	private BigDecimal balance;
	
	/**
	 * 交易手续费
	 */
	private BigDecimal serviceFee;
	
	/**
	 * 绑定的会员
	 */
	private Member member;


	@Column(nullable = false)
	public String getTranCode() {
		return tranCode;
	}

	public void setTranCode(String tranCode) {
		this.tranCode = tranCode;
	}

	@Column(nullable = false)
	public String getTermNo() {
		return termNo;
	}

	public void setTermNo(String termNo) {
		this.termNo = termNo;
	}

	@Column(nullable = false)
	public String getMerNo() {
		return merNo;
	}

	public void setMerNo(String merNo) {
		this.merNo = merNo;
	}

	@Column(nullable = false)
	public String getTrackNo() {
		return trackNo;
	}

	public void setTrackNo(String trackNo) {
		this.trackNo = trackNo;
	}

	@Column(nullable = false)
	public String getReqTime() {
		return reqTime;
	}

	public void setReqTime(String reqTime) {
		this.reqTime = reqTime;
	}

	@Column(nullable = false)
	public String getPayCardNo() {
		return payCardNo;
	}

	public void setPayCardNo(String payCardNo) {
		this.payCardNo = payCardNo;
	}

	public String getPayBankAccNo() {
		return payBankAccNo;
	}

	public void setPayBankAccNo(String payBankAccNo) {
		this.payBankAccNo = payBankAccNo;
	}

	@Column(nullable = false)
	public String getPayPass() {
		return payPass;
	}

	public void setPayPass(String payPass) {
		this.payPass = payPass;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getPayWay() {
		return payWay;
	}

	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}

	public String getToFlag() {
		return toFlag;
	}

	public void setToFlag(String toFlag) {
		this.toFlag = toFlag;
	}

	@Column(nullable = false)
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(nullable = false)
	public String getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getRecCardNo() {
		return recCardNo;
	}

	public void setRecCardNo(String recCardNo) {
		this.recCardNo = recCardNo;
	}

	@Column(nullable = false)
	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getDepositBankNo() {
		return depositBankNo;
	}

	public void setDepositBankNo(String depositBankNo) {
		this.depositBankNo = depositBankNo;
	}

	public String getBankAccType() {
		return bankAccType;
	}

	public void setBankAccType(String bankAccType) {
		this.bankAccType = bankAccType;
	}

	public String getBankAccProp() {
		return bankAccProp;
	}

	public void setBankAccProp(String bankAccProp) {
		this.bankAccProp = bankAccProp;
	}

	@Column(nullable = false)
	public String getBankAccNo() {
		return bankAccNo;
	}

	public void setBankAccNo(String bankAccNo) {
		this.bankAccNo = bankAccNo;
	}

	@Column(nullable = false)
	public String getBankAccName() {
		return bankAccName;
	}

	public void setBankAccName(String bankAccName) {
		this.bankAccName = bankAccName;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getMobNo() {
		return mobNo;
	}

	public void setMobNo(String mobNo) {
		this.mobNo = mobNo;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public String getRespMsg() {
		return respMsg;
	}

	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}

	public String getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}

	public String getRetCode() {
		return retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	public String getRetMsg() {
		return retMsg;
	}

	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}

	public String getBusiRefNo() {
		return busiRefNo;
	}

	public void setBusiRefNo(String busiRefNo) {
		this.busiRefNo = busiRefNo;
	}

	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(BigDecimal serviceFee) {
		this.serviceFee = serviceFee;
	}

	@NotNull
	@ManyToOne
	@JoinColumn(nullable=false)
	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}
	
	
}
