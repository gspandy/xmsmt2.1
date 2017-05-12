/**
 *====================================================
 * 文件名称: SettingConstant.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年8月19日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.constant;

import java.math.BigDecimal;

/**
 * @ClassName: SettingConstant
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年8月19日 下午3:19:46
 */
public class SettingConstant {

	/** 订单积分抵扣上限 */
	public static BigDecimal maxCouponPointPer = new BigDecimal("0.1");

	/** 积分兑换现金比例 */
	public static BigDecimal amountPointScale = new BigDecimal("0.01");

	/** 特权系数比例 */
	public static BigDecimal amountPrivilegeScale = new BigDecimal("0.01");

	/** 自动确认收货时限 */
	public static int autoTradeAcceptDateLimit = 10;

	/** 诚信等级算法参数-20 */
	public static BigDecimal scoreParams20 = new BigDecimal(20);

	/** 诚信等级算法参数-5 */
	public static BigDecimal scoreParams5 = new BigDecimal(5);

	/** 诚信等级算法参数-1 */
	public static BigDecimal scoreParams1 = new BigDecimal(1);

}
