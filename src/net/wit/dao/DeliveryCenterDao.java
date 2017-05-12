/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.math.BigDecimal;
import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Area;
import net.wit.entity.Community;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.Location;
import net.wit.entity.Member;
import net.wit.entity.Tenant;

/**
 * Dao - 发货点
 * @author rsico Team
 * @version 3.0
 */
public interface DeliveryCenterDao extends BaseDao<DeliveryCenter, Long> {

	/**
	 * 查找默认发货点
	 * @return 默认发货点，若不存在则返回null
	 */
	DeliveryCenter findDefault();

	/**
	 * 查找默认发货点
	 * @return 默认发货点，若不存在则返回null
	 */
	DeliveryCenter findDefault(Tenant tenant);

	List<DeliveryCenter> findMyAll(Member member);

	List<DeliveryCenter> findList(Area area, Community community);

	List<DeliveryCenter> findList(Area area, Location location, BigDecimal distatce);

	public Object findPage(Member member, Pageable pageable);

	/**
	 * @Title：findPage @Description：
	 * @param tenant
	 * @param pageable
	 * @return Page<DeliveryCenter>
	 */
	Page<DeliveryCenter> findPage(Tenant tenant, Pageable pageable);
}