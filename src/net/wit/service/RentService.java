package net.wit.service;

import java.math.BigDecimal;
import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Rent;
import net.wit.util.BizException;

/**
 * 
 */
public interface RentService extends BaseService<Rent, Long> {
	/**
	 * 查询租金列表
	 * @param memberId
	 * @param rentDate
	 * @param status
	 * @param pageable
	 * @return
	 */
	public Page<Rent> findPage(Long memberId, String rentDate,Rent.Status status,Pageable pageable);
	/**
	 * 租金收取
	 * @param id
	 */
	public void charge(Long id);
	/**
	 * 试用期交租
	 * @param memberId
	 * @throws BizException 
	 */
	public void freePeriodRent(Long memberId,Long tenantId) throws BizException;
	/**
	 * 店主是否冻结
	 * @param memberId
	 * @return
	 * @throws BizException
	 */
	public boolean isFreezed(Long memberId) throws BizException;
	/**
	 * 距离下一交租日天数
	 * @param memberId
	 * @return
	 * @throws BizException
	 */
	public int calNextRentDate(Long memberId) throws BizException;
	/**
	 * 交租信息
	 * @param memberId
	 * @return
	 * @throws BizException
	 */
	public List<Rent> rentInfo(Long memberId) throws BizException;
	/**
	 * 交租服务
	 * @param memberId
	 * @param rent
	 * @param amount
	 * @throws BizException
	 */
	public String payRent(Long memberId, BigDecimal rent, int amount)
			throws BizException;
	/**
	 * 最近一条已交租记录
	 * @param memberId
	 * @return
	 */
	public Rent LastChargeRent(Long memberId);
	/**
	 * 支付完成回调
	 * @param txNo
	 * @throws BizException
	 */
	public void adviceBack(String txNo)throws BizException;
}