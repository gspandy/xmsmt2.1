/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;


import net.wit.dao.IdcardDao;
import net.wit.entity.Idcard;

import org.springframework.stereotype.Repository;

/**
 * Dao - 实名认证
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("idcardDaoImpl")
public class IdcardDaoImpl extends BaseDaoImpl<Idcard, Long> implements IdcardDao {

}