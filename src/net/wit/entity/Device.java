package net.wit.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

/**
 * Entity - 设备
 * @author rsico Team
 * @version 3.0
 */
//@Entity
//@Table(name = "xx_device")
//@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_device_sequence")
public class Device extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4452073095788053870L;

	/** 设备id */
	private String equipId;

	/** 门店 */
	private DeliveryCenter deliveryCenter;
	
	/** 激活码 */
	private String verification;

	/**
	 * 获取设备id
	 * @return 设备id
	 */
	@Length(max = 255)
	public String getEquipId() {
		return equipId;
	}

	public void setEquipId(String equipId) {
		this.equipId = equipId;
	}

	/**
	 * 获取门店
	 * @return 门店
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public DeliveryCenter getDeliveryCenter() {
		return deliveryCenter;
	}

	public void setDeliveryCenter(DeliveryCenter deliveryCenter) {
		this.deliveryCenter = deliveryCenter;
	}

	/**
	 * 获取激活码
	 * @return 激活码
	 */
	@Length(max = 255)
	public String getVerification() {
		return verification;
	}

	public void setVerification(String verification) {
		this.verification = verification;
	}
	
}
