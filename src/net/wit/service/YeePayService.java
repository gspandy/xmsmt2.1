package net.wit.service;

import java.util.Map;

import net.wit.entity.BaseEntity;
import net.wit.entity.Refunds;
import net.wit.util.BizException;

/**
 * Service - 易宝支付Service
 * @author Teddy
 * @version 1.0
 */
public interface YeePayService extends BaseService<BaseEntity, Long> {
	
	/**
	 * 注册子账户接口
	 */
	public Map<String, String> registerAccount(Map<String, String> params) throws BizException;
	
	/**
	 * 修改子账户接口
	 */
	public Map<String, String> updateAccount(Map<String, String> params) throws BizException;
	
	/**
	 * 订单支付
	 */
	public Map<String, String> orderPayment(Map<String, String> params) throws BizException;
	
	/**
	 * 分账接口
	 */
	public Map<String, String> divide(Map<String, String> params) throws BizException;
	
	/**
	 * 退款接口
	 */
	public Map<String, String> refund(Map<String, String> params,Refunds refunds) throws BizException;
	
	/**
	 * 提现接口
	 */
	public Map<String, String> cashTransfer(Map<String, String> params) throws BizException;
	
	/**
	 * 转账接口
	 */
	public Map<String, String> transfer(Map<String, String> params) throws BizException;
	
	/**
	 * 订单查询接口
	 */
	public Map<String, String> paymentQuery(Map<String, String> params) throws BizException;

}