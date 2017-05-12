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
 * Entity - 月销售指标
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_sale_target_month")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_sale_target_month_sequence")
public class SaleTargetMonth extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3629882071415561116L;

	/**
	 * 目标类型
	 */
	public enum Type {

		/** 没有下级目标 */
		no,

		/** 有下级目标 */
		yes
	}
	
	/** 年销售指标 */
	private SaleTargetYear saleTargetYear;
	
	/** 月份 */
	private String month;

	/** 月指标 */
	private BigDecimal monthAmount;
	
	/** 目标类型 */
	private Type targetType;

	/**
	 * 获取年销售指标
	 * @return 年销售指标
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public SaleTargetYear getSaleTargetYear() {
		return saleTargetYear;
	}

	public void setSaleTargetYear(SaleTargetYear saleTargetYear) {
		this.saleTargetYear = saleTargetYear;
	}
	
	/**
	 * 获取月份
	 * @return 月份
	 */
	@Length(max = 20)
	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	/**
	 * 获取月指标
	 * @return 月指标
	 */
	@Min(0)
	@Digits(integer = 8, fraction = 2)
	@Column(precision = 10, scale = 2)
	public BigDecimal getMonthAmount() {
		return monthAmount;
	}

	public void setMonthAmount(BigDecimal monthAmount) {
		this.monthAmount = monthAmount;
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
