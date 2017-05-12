package net.wit.mobile.bo;

import java.math.BigDecimal;
import com.google.gson.annotations.Expose;

public class ProductListBo {


	@Expose
	private String id;

	/** 名称 */
	@Expose
	private String name;

	/** 全称 */
	@Expose
	private String fullName;


	/** 内购价 */
	@Expose
	private BigDecimal price;

	/** 市场价 */
	@Expose
	private BigDecimal marketPrice;

	/** 价格类型 */
	private int priceType;

	/** 电商价 */
	private BigDecimal ePrice;

	/** 商品佣金 */
	private BigDecimal rent;

	/** 展示图片 */
	@Expose
	private String image;
	/**
	 * 显示图片高
	 */
	
	private int imageHeight;
	/**
	 * 显示图片宽
	 */
	private int imageWidth;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getMarketPrice() {
		return marketPrice;
	}
	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}
	public int getPriceType() {
		return priceType;
	}
	public void setPriceType(int priceType) {
		this.priceType = priceType;
	}
	public BigDecimal getePrice() {
		return ePrice;
	}
	public void setePrice(BigDecimal ePrice) {
		this.ePrice = ePrice;
	}
	public BigDecimal getRent() {
		return rent;
	}
	public void setRent(BigDecimal rent) {
		this.rent = rent;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getImageHeight() {
		return imageHeight;
	}
	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}
	public int getImageWidth() {
		return imageWidth;
	}
	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}
}
