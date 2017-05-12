/**
 *====================================================
 * 文件名称: TradeFollowUp.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年8月15日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @ClassName: TradeFollowUp
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年8月15日 下午2:23:42
 */
@Entity
@Table(name = "xx_trade_followup")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_trade_followup_sequence")
public class TradeFollowup extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/** 备注 */
	private String memo;

	/** 子订单 */
	private Trade trade;

	/**
	 * 获取备注
	 * @return 备注
	 */
	@Column
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
	 * 获取子订单
	 * @return 子订单
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
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
}
