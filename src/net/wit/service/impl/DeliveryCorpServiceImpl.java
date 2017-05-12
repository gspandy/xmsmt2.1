/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.wit.dao.DeliveryCorpDao;
import net.wit.entity.DeliveryCorp;
import net.wit.service.DeliveryCorpService;

/**
 * Service - 物流公司
 * @author rsico Team
 * @version 3.0
 */
@Service("deliveryCorpServiceImpl")
public class DeliveryCorpServiceImpl extends BaseServiceImpl<DeliveryCorp, Long>implements DeliveryCorpService {

	@Resource(name = "deliveryCorpDaoImpl")
	private DeliveryCorpDao deliveryCorpDao;

	@Resource(name = "deliveryCorpDaoImpl")
	public void setBaseDao(DeliveryCorpDao deliveryCorpDao) {
		super.setBaseDao(deliveryCorpDao);
	}

	public DeliveryCorp findByName(String name) {
		return deliveryCorpDao.findByName(name);
	}

}