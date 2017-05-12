/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Member;
import net.wit.entity.Mobile;

/**
 * Dao - 付款单
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface MobileDao extends BaseDao<Mobile, Long> {

	Page<Mobile> findPage(Member member, Pageable pageable);
}