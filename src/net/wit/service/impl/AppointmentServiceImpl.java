/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import javax.annotation.Resource;

import net.wit.dao.AppointmentDao;
import net.wit.entity.Appointment;
import net.wit.service.AppointmentService;

import org.springframework.stereotype.Service;

/**
 * Service - 预约时间
 * @author rsico Team
 * @version 3.0
 */
@Service("appointmentServiceImpl")
public class AppointmentServiceImpl extends BaseServiceImpl<Appointment, Long> implements AppointmentService {

	@Resource(name = "appointmentDaoImpl")
	public void setBaseDao(AppointmentDao appointmentDao) {
		super.setBaseDao(appointmentDao);
	}

	public Appointment findDefault() {
		Appointment appointment = super.find(1L);
		if (appointment == null) {
			appointment = super.findFirst();
		}
		return appointment;
	}
}