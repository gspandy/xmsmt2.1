/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity - 序列号
 * 
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_sn")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_sn_sequence")
public class Sn extends BaseEntity {

	private static final long serialVersionUID = -2330598144835706164L;

	/**
	 * 类型
	 */
	public enum Type {

		/** 商品 */
		product,

		/** 订单 */
		order,

		/** 收款单 */
		payment,

		/** 退款单 */
		refunds,

		/** 发货单 */
		shipping,

		/** 退货单 */
		returns,

		/** 付款单 */
		credit,
		
		/** 系统跟踪号 */
		epaybank,

		/** 手机快充 */
		mobile,
		
		/** 游戏直充 */
		game,
		
		/** 子订单号 */
		trade,
		
		/** 缴费 */
		application,
		
		/** 随机数 */
		random,
		
		/** 易宝 - 注册请求号 */
		yeepayRegister,
		
		/** 易宝 - 账户修改请求号 */
		yeepayUpdate,
		
		/** 易宝 - 订单支付请求号 */
		yeepayPayment,
		
		/** 易宝 - 分账号 */
		yeepayDivide,
		
		/** 易宝 - 退款请求号 */
		yeepayRefund,
		
		/** 易宝 - 提现请求号 */
		yeepayCash,
		
		/** 易宝 - 转账请求号 */
		yeepayTransfer
		
	}

	/** 类型 */
	private Type type;

	/** 末值 */
	private Long lastValue;

	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	@Column(nullable = false, updatable = false, unique = true)
	public Type getType() {
		return type;
	}

	/**
	 * 设置类型
	 * 
	 * @param type
	 *            类型
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * 获取末值
	 * 
	 * @return 末值
	 */
	@Column(nullable = false)
	public Long getLastValue() {
		return lastValue;
	}

	/**
	 * 设置末值
	 * 
	 * @param lastValue
	 *            末值
	 */
	public void setLastValue(Long lastValue) {
		this.lastValue = lastValue;
	}

}