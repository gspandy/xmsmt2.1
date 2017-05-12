/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import javax.annotation.Resource;

import net.wit.dao.PaymentMethodDao;
import net.wit.entity.PaymentMethod;
import net.wit.service.PaymentMethodService;

import org.springframework.stereotype.Service;

/**
 * Service - 支付方式
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("paymentMethodServiceImpl")
public class PaymentMethodServiceImpl extends BaseServiceImpl<PaymentMethod, Long> implements PaymentMethodService {

	@Resource(name = "paymentMethodDaoImpl")
	public void setBaseDao(PaymentMethodDao paymentMethodDao) {
		super.setBaseDao(paymentMethodDao);
	}

}