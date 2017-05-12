/**
 *====================================================
 * 文件名称: Bonus.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年5月15日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
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
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;

/**
 * @ClassName: Bonus
 * @Description: 商品提成方式
 * @author Administrator
 * @date 2014年5月15日 上午11:05:41
 */
@Entity
@Table(name = "xx_bonus")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_bonus_sequence")
public class Bonus extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/** 提成角色 */
	public enum RoleType {

		/** 业务员 */
		salesman

	}

	/** 提成角色 */
	private RoleType roleType;

	/** 提成表达式 */
	private String expression;

	/** 商品提成金额 */
	private BigDecimal amount;

	/** 所属商品 */
	private Product product;

	/**
	 * 获取提成角色类型
	 * @return 提成角色类型
	 */
	@NotNull
	public RoleType getRoleType() {
		return roleType;
	}

	/**
	 * 设置提成角色类型
	 * @param bonusExpression 提成角色类型
	 */
	public void setRoleType(RoleType roleType) {
		this.roleType = roleType;
	}

	/**
	 * 获取提成计算方式
	 * @return 提成计算方式
	 */
	@NotNull
	public String getExpression() {
		return expression;
	}

	/**
	 * 设置提成计算方式
	 * @param expression 提成计算方式
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

	/**
	 * 获取单品提成金额
	 * @return 单品提成金额
	 */
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * 设置单品提成金额
	 * @param amount 单品提成金额
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * 获取商品
	 * @return 商品
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public Product getProduct() {
		return product;
	}

	/**
	 * 设置商品
	 * @param product 商品
	 */
	public void setProduct(Product product) {
		this.product = product;
		if (this.amount == null) {
			BigDecimal result = BigDecimal.ZERO;
			if (StringUtils.isNotBlank(this.expression) && this.expression.endsWith("%")) {
				result = new BigDecimal(this.expression.replace("%", "")).divide(BigDecimal.TEN).divide(BigDecimal.TEN).multiply(getProduct().getWholePrice());
			} else {
				result = new BigDecimal(this.expression);
			}
			this.amount = result;
		}
	}

}
