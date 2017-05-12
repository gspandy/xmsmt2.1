package net.wit.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entity - 随手记
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_note")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_note_sequence")
public class Note extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6263609532782803614L;

	/**
	 * 类型
	 */
	public enum Type {

		/** 文本 */
		text,

		/** 图片 */
		picture
	}
	
	/**
	 * 状态
	 */
	public enum Status {

		/** 已发送 */
		send,

		/** 已接收 */
		receive,

		/** 已回复 */
		reply
	}
	
	/** 门店 */
	private DeliveryCenter deliveryCenter;
	
	/** 主题 */
	private String title;
	
	/** 图片路径 */
	private String url;
	
	/** 类型 */
	private Type type;
	
	/** 状态 */
	private Status status;

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
	 * 获取主题
	 * @return 主题
	 */
	@JsonProperty
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 获取图片路径
	 * @return 图片路径
	 */
	@JsonProperty
	@Length(max = 255)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * 获取类型
	 * @return 类型
	 */
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	/**
	 * 获取状态
	 * @return 状态
	 */
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
}
