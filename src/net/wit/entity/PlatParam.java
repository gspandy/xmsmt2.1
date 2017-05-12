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
 * Entity - 管理员
 * 
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_plat_param")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_plat_param_sequence")
public class PlatParam extends BaseEntity {

	private static final long serialVersionUID = -7519486823153844426L;

//	private Integer id;// 主键
	private String paramCnName;
	private String paramName;
	private String paramValue;
	private String defaultValue;
	private Integer type;
	public String getParamCnName() {
		return paramCnName;
	}

	public void setParamCnName(String paramCnName) {
		this.paramCnName = paramCnName;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
    
}