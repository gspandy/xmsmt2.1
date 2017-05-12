/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import net.wit.dao.AppointmentDao;
import net.wit.entity.Appointment;

import org.springframework.stereotype.Repository;

/**
 * Dao - 预约时间
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("appointmentDaoImpl")
public class AppointmentDaoImpl extends BaseDaoImpl<Appointment, Long> implements AppointmentDao {


}