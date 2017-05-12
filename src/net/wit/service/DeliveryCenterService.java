/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.Member;
import net.wit.entity.Tenant;

/**
 * Service - 发货点
 * @author rsico Team
 * @version 3.0
 */
public interface DeliveryCenterService extends BaseService<DeliveryCenter, Long> {

	/**
	 * 查找默认发货点
	 * @return 默认发货点，若不存在则返回null
	 */
	DeliveryCenter findDefault();

	DeliveryCenter findDefault(Tenant tenant);

	List<DeliveryCenter> findMyAll(Member member);

	public Object findPage(Member member, Pageable pageable);

	/**
	 * @Title：findPage @Description：
	 * @param tenant
	 * @param pageable
	 * @return Object
	 */
	Page<DeliveryCenter> findPage(Tenant tenant, Pageable pageable);

}