/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSetDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.dao.impl;

import org.springframework.stereotype.Repository;

import net.wit.dao.PicTypeDao;
import net.wit.entity.PicType;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月7日
 */
@Repository("picTypeDaoImpl")
public class PicTypeDaoImpl extends BaseDaoImpl<PicType, Long> implements PicTypeDao{

}
