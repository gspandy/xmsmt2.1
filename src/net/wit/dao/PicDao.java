/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantSellConditionDao.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月8日
 */
package net.wit.dao;

import java.util.List;

import net.wit.entity.Pic;
import net.wit.entity.Tenant;


/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月8日
 */
public interface PicDao extends BaseDao<Pic, Long>{

	public List<Pic> getDefaultHeadImage(String picName);
	/**
	 * 根据企业及图片类型获取PIC
	 * @param tenant
	 * @param picType
	 * @return
	 */
	public List<Pic> getPicByTenantAndType(Tenant tenant,String picType);
}
