/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: orderStatusConfig.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月18日
 */
package net.wit.entity;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 订单状态配置表
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月18日
 */
@Entity
@Table(name = "t_order_status_config")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_order_status_config_sequence")
public class OrderStatusConfig extends BaseEntity{
	private static final long serialVersionUID = 1L;
	private String orderStatus;
	private String paymentStatus;
	private String shippingStatus;
	private String mobileStatus;
	private String mobileShow;
	private String mobileButton;
	private String pcShow;
	private String pcButton;
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public String getShippingStatus() {
		return shippingStatus;
	}
	public void setShippingStatus(String shippingStatus) {
		this.shippingStatus = shippingStatus;
	}
	public String getMobileShow() {
		return mobileShow;
	}
	public void setMobileShow(String mobileShow) {
		this.mobileShow = mobileShow;
	}
	public String getMobileButton() {
		return mobileButton;
	}
	public void setMobileButton(String mobileButton) {
		this.mobileButton = mobileButton;
	}
	public String getPcShow() {
		return pcShow;
	}
	public void setPcShow(String pcShow) {
		this.pcShow = pcShow;
	}
	public String getPcButton() {
		return pcButton;
	}
	public void setPcButton(String pcButton) {
		this.pcButton = pcButton;
	}
	public String getMobileStatus() {
		return mobileStatus;
	}
	public void setMobileStatus(String mobileStatus) {
		this.mobileStatus = mobileStatus;
	}

}
