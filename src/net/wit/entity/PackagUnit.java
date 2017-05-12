/**
 *====================================================
 * 文件名称: PackagUnit.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年5月15日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @ClassName: PackagUnit
 * @Description: 商品包装单位
 * @author Administrator
 * @date 2014年5月15日 上午11:05:51
 */
@Entity
@Table(name = "xx_packag_unit")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_packag_unit_sequence")
public class PackagUnit extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/** 包装单位名称 */
	private String name;

	/** 换算系数 */
	private BigDecimal coefficient;

	/** 商品批发价 */
	private BigDecimal wholePrice;

	/** 销售价 */
	private BigDecimal price;

	/** 商品条形码 */
	private String barcode;

	/** 所属商品 */
	private Product product;

	/** 购物车项 */
	private Set<CartItem> cartItems = new HashSet<CartItem>();

	/**
	 * 获取包装单位名称
	 * @return 包装单位名称
	 */
	@NotNull
	@JsonProperty
	public String getName() {
		return name;
	}

	/**
	 * 设置包装单位名称
	 * @param name 包装单位名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取换算系数
	 * @return 换算系数
	 */
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getCoefficient() {
		return coefficient;
	}

	/**
	 * 设置换算系数
	 * @param coefficient 换算系数
	 */
	public void setCoefficient(BigDecimal coefficient) {
		this.coefficient = coefficient;
	}

	/**
	 * 获取商品销售价
	 * @return 商品销售价
	 */
	@Min(0)
	@JsonProperty
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * 设置商品销售价
	 * @param wholePrice 商品销售价
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	/**
	 * 获取商品批发价
	 * @return 商品批发价
	 */
	@Min(0)
	@JsonProperty
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getWholePrice() {
		return wholePrice;
	}

	/**
	 * 设置商品批发价
	 * @param wholePrice 商品批发价
	 */
	public void setWholePrice(BigDecimal wholePrice) {
		this.wholePrice = wholePrice;
	}

	/**
	 * 获取所属商品包装条形码
	 * @return 所属商品包装条形码
	 */
	@Column
	public String getBarcode() {
		return barcode;
	}

	/**
	 * 设置所属包装条形码
	 * @param product 所属包装条形码
	 */
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	/**
	 * 获取所属商品
	 * @return 所属商品
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public Product getProduct() {
		return product;
	}

	/**
	 * 设置所属商品
	 * @param product 所属商品
	 */
	public void setProduct(Product product) {
		this.product = product;
		if (wholePrice == null) {
			wholePrice = calculateWholePrice();
		}
		if (price == null) {
			price = calculatePrice();
		}
	}

	/**
	 * 获取所属商品
	 * @return 所属商品
	 */
	@OneToMany(mappedBy = "packagUnit", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<CartItem> getCartItems() {
		return cartItems;
	}

	/**
	 * 设置所属购物车项
	 * @param product 所属购物车项
	 */
	public void setCartItems(Set<CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	/**
	 * 持久化前处理
	 */
	@PrePersist
	public void prePersist() {
		if (wholePrice == null) {
			wholePrice = calculateWholePrice();
		}
		if (price == null) {
			price = calculatePrice();
		}
	}

	/**
	 * 更新前处理
	 */
	@PreUpdate
	public void preUpdate() {
		if (wholePrice == null) {
			wholePrice = calculateWholePrice();
		}
		if (price == null) {
			price = calculatePrice();
		}
	}

	/** 计算包装批发价格 */
	public BigDecimal calculateWholePrice() {
		return coefficient.multiply(this.getProduct().getWholePrice());
	}

	/** 计算包装销售价格 */
	public BigDecimal calculatePrice() {
		return coefficient.multiply(this.getProduct().getPrice());
	}

}
