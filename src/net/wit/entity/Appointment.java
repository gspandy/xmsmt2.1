/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity - 预约时间
 * 
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_appointment")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_appointment_sequence")
public class Appointment extends BaseEntity {

	private static final long serialVersionUID = -2534598654855727296L;
	
	/** 描述 */
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}