/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantSellConditionDao.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月8日
 */
package net.wit.dao;

import java.util.Date;
import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Rent;


/**
 * 
 */
public interface RentDao extends BaseDao<Rent, Long>{
	
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
	 * 查询未交租记录
	 * @param memberId
	 * @param date
	 * @param status
	 * @return
	 */
	public List<Rent> findRentList(Long memberId, Date date,Rent.Status status);
	/**
	 * 统计已交租
	 * @param memberId
	 * @param date
	 * @return
	 */
	public int countRentCharged(Long memberId, Date date);
	
	public List<Rent> findRentInfo(Long memberId,Rent.Status status);
	/**
	 * 获取最近一条已交租记录
	 * @param memberId
	 * @return
	 */
	public Rent getLastChargedRent(Long memberId);
	/**
	 * 通过交易编号查询
	 * @param txNo
	 * @return
	 */
	public List<Rent> findByTxNo(String txNo);
	
	public List<Rent> findRentList(Long memberId, Rent.Status status);
	
}
