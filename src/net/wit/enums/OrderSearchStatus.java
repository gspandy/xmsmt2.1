package net.wit.enums;

/**
 * @ClassName：OrderSearchStatus
 * @Description：
 * @author：Chenlf 
 * @date：2015年10月18日 下午5:15:38 
 */
public enum OrderSearchStatus {
	/** 待付款 */
	waitPay, 
	/** 待发货 */
	waitShipping, 
	/** 已发货 */
	shippinged, 
	/** 退货中 */
	returning, 
	/** 退款中 */
	refunding, 
	/** 已退款 */
	refunded, 
	/** 已签收 */
	signed, 
	/** 交易关闭 */
	cancel, 
	/** 交易成功 */
	complete

}
