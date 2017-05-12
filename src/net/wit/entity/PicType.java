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
@Table(name = "t_pic_type")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_pic_type_sequence")
public class PicType extends BaseEntity {

	private static final long serialVersionUID = -7519486823153844426L;

	private String imageType;
	private String imageTypeName;
	
	private String remark;

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public String getImageTypeName() {
		return imageTypeName;
	}

	public void setImageTypeName(String imageTypeName) {
		this.imageTypeName = imageTypeName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}