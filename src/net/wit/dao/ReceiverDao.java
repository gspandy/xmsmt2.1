/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Member;
import net.wit.entity.Receiver;

/**
 * Dao - 收货地址
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface ReceiverDao extends BaseDao<Receiver, Long> {

	/**
	 * 查找默认收货地址
	 * 
	 * @param member
	 *            会员
	 * @return 默认收货地址，若不存在则返回最新收货地址
	 */
	Receiver findDefault(Member member);

	/**
	 * 查找收货地址分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 收货地址分页
	 */
	Page<Receiver> findPage(Member member, Pageable pageable);

	/**
	 * 根据会员查找收货地址
	 * @param member
	 *            会员
	 * @return 收货地址
	 */
	List<Receiver> findList(Member member);
}