package net.wit.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Length;

/**
 * Entity - 年销售指标
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_sale_target_year")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_sale_target_year_sequence")
public class SaleTargetYear extends BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1819153140374948190L;

	/**
	 * 目标类型
	 */
	public enum Type {

		/** 没有下级目标 */
		no,

		/** 有下级目标 */
		yes
	}
	
	/** 门店 */
	private DeliveryCenter deliveryCenter;

	/** 年份 */
	private String year;
	
	/** 年指标 */
	private BigDecimal yearAmount;

	/** 目标类型 */
	private Type targetType;

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
	 * 获取年份
	 * @return 年份
	 */
	@Length(max = 20)
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * 获取年指标
	 * @return 年指标
	 */
	@Min(0)
	@Digits(integer = 8, fraction = 2)
	@Column(precision = 10, scale = 2)
	public BigDecimal getYearAmount() {
		return yearAmount;
	}

	public void setYearAmount(BigDecimal yearAmount) {
		this.yearAmount = yearAmount;
	}

	/**
	 * 获取目标类型
	 * @return 目标类型
	 */
	public Type getTargetType() {
		return targetType;
	}

	public void setTargetType(Type targetType) {
		this.targetType = targetType;
	}

}
