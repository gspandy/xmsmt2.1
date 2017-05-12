/**
 *====================================================
 * 文件名称: TradeApply.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年8月15日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

/**
 * @ClassName: TradeApply
 * @Description: 申请信息
 * @author Administrator
 * @date 2014年8月15日 上午10:16:06
 */
@Entity
@Table(name = "xx_trade_apply")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_trade_apply_sequence")
public class TradeApply extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/** 申请类型 */
	public static enum Type {
		/** 退款申请 */
		refund,
		/** 退货申请 */
		returns,
		/** 换货申请 */
		change
	}

	/** 申请状态 */
	public static enum Status {
		/** 提交 */
		submited,
		/** 通过 */
		passed,
		/** 拒绝 */
		rejected
	}

	/** 类型 */
	private Type type;

	/** 状态 */
	private Status status;

	/** 配送方式 */
	private ShippingMethod shippingMethod;

	/** 退款金额 */
	private BigDecimal refundAmount;

	/** 运单号 */
	private String trackingNo;

	/** 物流公司 */
	private DeliveryCorp deliveryCorp;

	/** 物流公司名称 */
	private String deliveryCorpName;

	/** 备注 */
	private String memo;

	/** 反馈 */
	private Status remark;

	/** 子订单 */
	private Trade trade;

	/**
	 * 获取申请类型
	 * @return 申请类型
	 */
	public Type getType() {
		return type;
	}

	/**
	 * 设置申请类型
	 * @param type 申请类型
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * 获取申请状态
	 * @return 申请状态
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * 设置申请状态
	 * @param status 申请状态
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * 获取备注
	 * @return 备注
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * 设置备注
	 * @param memo 备注
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * 获取反馈
	 * @return 反馈
	 */
	public Status getRemark() {
		return remark;
	}

	/**
	 * 设置反馈
	 * @param remark 反馈
	 */
	public void setRemark(Status remark) {
		this.remark = remark;
	}

	/**
	 * 获取子订单
	 * @return 子订单
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trades", nullable = false, updatable = false)
	public Trade getTrade() {
		return trade;
	}

	/**
	 * 设置子订单
	 * @param trade 子订单
	 */
	public void setTrade(Trade trade) {
		this.trade = trade;
	}

	/**
	 * 获取配送方式
	 * @return 配送方式
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public ShippingMethod getShippingMethod() {
		return shippingMethod;
	}

	/**
	 * 设置配送方式
	 * @param shippingMethod 配送方式
	 */
	public void setShippingMethod(ShippingMethod shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

	/**
	 * 获取退款金额
	 * @return 退款金额
	 */
	@Digits(integer = 12, fraction = 3)
	@Column(precision = 21, scale = 6)
	public BigDecimal getRefundAmount() {
		return refundAmount;
	}

	/**
	 * 设置退款金额
	 * @param refundAmount 退款金额
	 */
	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}

	/**
	 * 获取运单号
	 * @return 运单号
	 */
	public String getTrackingNo() {
		return trackingNo;
	}

	/**
	 * 设置运单号
	 * @param trackingNo 运单号
	 */
	public void setTrackingNo(String trackingNo) {
		this.trackingNo = trackingNo;
	}

	/**
	 * 获取运单号
	 * @return 运单号
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public DeliveryCorp getDeliveryCorp() {
		return deliveryCorp;
	}

	/**
	 * 设置物流公司
	 * @param deliveryCorp 物流公司
	 */
	public void setDeliveryCorp(DeliveryCorp deliveryCorp) {
		this.deliveryCorp = deliveryCorp;
	}

	/**
	 * 获取物流公司名称
	 * @return 物流公司名称
	 */
	public String getDeliveryCorpName() {
		return deliveryCorpName;
	}

	/**
	 * 设置物流公司名称
	 * @param deliveryCorpName 物流公司名称
	 */
	public void setDeliveryCorpName(String deliveryCorpName) {
		this.deliveryCorpName = deliveryCorpName;
	}

}
