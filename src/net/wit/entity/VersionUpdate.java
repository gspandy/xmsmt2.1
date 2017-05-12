/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: VersionUpdate.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月11日
 */
package net.wit.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月11日
 */
@Entity
@Table(name = "t_version_update")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_version_update_sequence")
public class VersionUpdate extends BaseEntity{
	private static final long serialVersionUID = 1L;
	private String versionType;
	private String versionNo;
	private String versionContent;
	private String versionDownloadUrl;
	private Date versionDate;
	private String defaultUpdate;
	public String getVersionType() {
		return versionType;
	}
	public void setVersionType(String versionType) {
		this.versionType = versionType;
	}
	public String getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}
	public String getVersionContent() {
		return versionContent;
	}
	public void setVersionContent(String versionContent) {
		this.versionContent = versionContent;
	}
	public String getVersionDownloadUrl() {
		return versionDownloadUrl;
	}
	public void setVersionDownloadUrl(String versionDownloadUrl) {
		this.versionDownloadUrl = versionDownloadUrl;
	}
	public Date getVersionDate() {
		return versionDate;
	}
	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}
	public String getDefaultUpdate() {
		return defaultUpdate;
	}
	public void setDefaultUpdate(String defaultUpdate) {
		this.defaultUpdate = defaultUpdate;
	}
}
