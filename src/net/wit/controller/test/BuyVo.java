package net.wit.controller.test;

/**
 * @ClassName：BuyVo @Description：
 * @author：Chenlf
 * @date：2015年9月11日 下午5:08:50
 */
public class BuyVo {

	private Long productId;

	private Integer quantity;

	/**
	 * 获取 quantity
	 * @return quantity
	 */
	public Integer getQuantity() {
		return quantity;
	}

	/**
	 * 设置 quantity
	 * @param quantity quantity
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	/**
	 * 获取 productId
	 * @return productId
	 */
	public Long getProductId() {
		return productId;
	}

	/**
	 * 设置 productId
	 * @param productId productId
	 */
	public void setProductId(Long productId) {
		this.productId = productId;
	}

}
