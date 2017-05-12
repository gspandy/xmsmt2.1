package net.wit.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * <p>Title: 手机快充 </p>
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
@Table(name = "xx_mobile")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_mobile_sequence")
public class Mobile extends BaseEntity{

	private static final long serialVersionUID = -245736763497251149L;
	
	/**
	 * 终端号
	 */
	private String termNo;
	
	/**
	 * 商户号
	 */
	private String merNo;
	
	/**
	 * 请求时间
	 */
	private String reqTime;

	/**
	 * 订单号
	 */
	private String sn;

	/**
	 * 交易金额
	 */
	private BigDecimal amount;

	/**
	 * 支付费用
	 */
	private BigDecimal fee;

	/**
	 * 成本金额
	 */
	private BigDecimal cost;

	/**
	 * 收款人手机号码
	 */
	private String mobile;

	/**
	 * 预留1
	 */
	private String param1;

	/**
	 * 预留2
	 */
	private String param2;
	/**
	 * 响应码
	 */
	private String respCode;
	
	/**
	 * 响应信息
	 */
	private String respMsg;
			
	/**
	 * 应答时间
	 */
	private String retTime;
	/**
	 * 交易结果码
	 */
	private String retCode;
	
	/**
	 * 交易错误信息
	 */
	private String retMsg;
	
	/**
	 * 易票联受理号
	 */
	private String busiRefNo;
	/**
	 * 绑定的会员
	 */
	private Member member;


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
	public String getReqTime() {
		return reqTime;
	}

	public void setReqTime(String reqTime) {
		this.reqTime = reqTime;
	}

	@Column(nullable = false)
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
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

	public String getRetCode() {
		return retCode;
	}

	public String getRetTime() {
		return retTime;
	}

	public void setRetTime(String retTime) {
		this.retTime = retTime;
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

	@NotNull
	@ManyToOne
	@JoinColumn(nullable=false)
	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}
	
	@Transient
	public BigDecimal getPrfAmount() {
		return getAmount().subtract(getFee());
	}
	
}
