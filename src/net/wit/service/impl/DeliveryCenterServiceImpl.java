/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.DeliveryCenterDao;
import net.wit.dao.DeviceDao;
import net.wit.dao.MemberDao;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.service.DeliveryCenterService;

/**
 * Service - 广告
 * @author rsico Team
 * @version 3.0
 */
@Service("deliveryCenterServiceImpl")
public class DeliveryCenterServiceImpl extends BaseServiceImpl<DeliveryCenter, Long>implements DeliveryCenterService {

	@Resource(name = "deliveryCenterDaoImpl")
	private DeliveryCenterDao deliveryCenterDao;


	@Resource(name = "deviceDaoImpl")
	private DeviceDao deviceDao;

	@Resource(name = "memberDaoImpl")
	private MemberDao memberDao;

	@Resource(name = "deliveryCenterDaoImpl")
	public void setBaseDao(DeliveryCenterDao DeliveryCenterDao) {
		super.setBaseDao(DeliveryCenterDao);
	}

	@Transactional(readOnly = true)
	public DeliveryCenter findDefault() {
		return deliveryCenterDao.findDefault();
	}

	@Transactional(readOnly = true)
	public DeliveryCenter findDefault(Tenant tennat) {
		return deliveryCenterDao.findDefault(tennat);
	}

	@Transactional(readOnly = true)
	public List<DeliveryCenter> findMyAll(Member member) {
		return deliveryCenterDao.findMyAll(member);
	}

	public Object findPage(Member member, Pageable pageable) {
		return deliveryCenterDao.findPage(member, pageable);
	}

	public Page<DeliveryCenter> findPage(Tenant tenant, Pageable pageable) {
		return deliveryCenterDao.findPage(tenant, pageable);
	}
}