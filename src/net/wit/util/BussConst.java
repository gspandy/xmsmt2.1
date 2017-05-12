package net.wit.util;

import java.math.BigDecimal;

/**
 *业务常量类
 */
public class BussConst {
	/***************账户交易*************/
	/**
	 * 账户交易类型-收入
	 */
	public static final String TRAN_TYPE_INCOME = "0"; // 
	/**
	 * 账户交易类型-提现
	 */
	public static final String TRAN_TYPE_CASH = "1"; // 
	/**
	 *  提现状态-成功
	 */
	public static final String CASH_TYPE_SUCCESS = "1"; // 
	/**
	 *  提现状态-处理中
	 */
	public static final String CASH_TYPE_APPLYING = "0"; // 
	/**
	 * 提现状态-失败
	 */
	public static final String CASH_TYPE_FAIL = "2"; // 
	/**
	 * 收支类型-佣金
	 */
	public static final String INCOME_TYPE_CHARGE = "1"; // 
	/**
	 * 收入类型-奖金
	 */
	public static final String INCOME_TYPE_BONUS = "2"; // 
	
	/**
	 * 收入类型-租金
	 */
	public static final String INCOME_TYPE_RENT = "3"; // 
	/**
	 * 收入类型-推荐奖励
	 */
	public static final String INCOME_TYPE_RECOMMEND = "4"; // 
	
	/***************账户交易*************/
	/***************平台参数*************/
	/**
	 * 平台参数-平台起租金额
	 */
	public static final String PARAM_BEGIN_RENT = "PLATFROM_RENT"; // 
	/**
	 * 平台参数-无理由退货期
	 */
	public static final String PARAM_FORCE_RETURN_DATE = "FORCE_RETURN_DATE"; // 
	/**
	 * 平台参数-店主试用期
	 */
	public static final String PARAM_SHOPKEEPER_FREE_PERIOD = "SHOPKEEPER_FREE_PERIOD"; // 
	/**
	 * 平台参数-服务url
	 */
	public static final String PARAM_SERVICE_URL = "SERVICE_URL"; // 
	/**
	 *  平台参数-退货单填写期
	 */
	public static final String PARAM_RETURN_APP_DATE = "RETURN_APP_DATE"; // 
	/**
	 * 平台参数-交租截止日
	 */
	public static final String PARAM_RENT_END_DATE = "RENT_END_DATE"; // 
	/**
	 * 平台参数-自动收货期
	 */
	public static final String PARAM_AUTO_RECEIPT_DATE = "AUTO_RECEIPT_DATE"; // 
	/**
	 * 平台参数-延长收货期
	 */
	public static final String PARAM_RECEIPT_DELAY = "RECEIPT_DELAY"; // 
	/***************平台参数*************/
	/***************奖金参数*************/
	/**
	 * 本店销售额奖金比例基数
	 */
	public static final BigDecimal TENANT_SELL_BONUS_RATE = new BigDecimal(100); // 
	/**
	 * 关联店销售额奖金比例基数
	 */
	public static final BigDecimal RELATIVE_SELL_BONUS_RATE = new BigDecimal(500); // 
	/***************奖金参数*************/
}
