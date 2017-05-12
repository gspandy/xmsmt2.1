/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Game;
import net.wit.entity.Member;

/**
 * Dao - 付款单
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface GameDao extends BaseDao<Game, Long> {

	Page<Game> findPage(Member member, Pageable pageable);
}