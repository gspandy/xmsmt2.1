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
@Table(name = "xx_game")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_game_sequence")
public class Game extends BaseEntity{

	private static final long serialVersionUID = -141736723497251149L;
	
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
	 * 账号
	 */
	private String account;

	/**
	 * 服务
	 */
	private String server;

	/**
	 * 区域
	 */
	private String area;

	/**
	 * 游戏类型
	 */
	private int cardId;

	/**
	 * 面值
	 */
	private int priceId;

	/**
	 * 数量
	 */
	private int chargeCount;
	
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

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAccount() {
		return account;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public int getCardId() {
		return cardId;
	}

	public void setCardId(int cardId) {
		this.cardId = cardId;
	}

	public int getPriceId() {
		return priceId;
	}

	public void setPriceId(int priceId) {
		this.priceId = priceId;
	}

	public int getChargeCount() {
		return chargeCount;
	}

	public void setChargeCount(int chargeCount) {
		this.chargeCount = chargeCount;
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
