/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.List;

import net.wit.entity.GamePrice;

/**
 * Dao - 游戏直充价格
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface GamePriceDao extends BaseDao<GamePrice, Long> {

	/**
	 * 查找游戏价格
	 * 
	 * @param cardId
	 *            游戏类型
	 * @return 价格列表
	 */
	
    List<GamePrice> findByCardId(int cardId);
	/**
	 * 查找游戏类型
	 * 
	 * @param typeId
	 *            游戏类型
	 * @return 类列表
	 */
	
    List<GamePrice> findByTypeId(int typeId);
}