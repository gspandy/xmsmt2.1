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
 * Entity - 日销售指标
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_sale_target_day")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_sale_target_day_sequence")
public class SaleTargetDay extends BaseEntity {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2297710568411503503L;

	/** 年销售指标 */
	private SaleTargetYear saleTargetYear;
	
	/** 年销售指标 */
	private SaleTargetMonth saleTargetMonth;
	
	/** 日期 */
	private String day;
	
	/** 日指标 */
	private BigDecimal dayAmount;

	/**
	 * 获取日销售指标
	 * @return 日销售指标
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public SaleTargetYear getSaleTargetYear() {
		return saleTargetYear;
	}

	public void setSaleTargetYear(SaleTargetYear saleTargetYear) {
		this.saleTargetYear = saleTargetYear;
	}

	/**
	 * 获取月销售指标
	 * @return 月销售指标
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public SaleTargetMonth getSaleTargetMonth() {
		return saleTargetMonth;
	}

	public void setSaleTargetMonth(SaleTargetMonth saleTargetMonth) {
		this.saleTargetMonth = saleTargetMonth;
	}

	/**
	 * 获取日期
	 * @return 日期
	 */
	@Length(max = 20)
	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	/**
	 * 获取日指标
	 * @return 日指标
	 */
	@Min(0)
	@Digits(integer = 8, fraction = 2)
	@Column(precision = 10, scale = 2)
	public BigDecimal getDayAmount() {
		return dayAmount;
	}

	public void setDayAmount(BigDecimal dayAmount) {
		this.dayAmount = dayAmount;
	}
	
}
