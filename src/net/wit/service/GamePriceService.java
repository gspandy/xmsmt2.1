/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;


import java.util.List;

import net.wit.entity.GamePrice;

/**
 * Service - 手机快充价格表
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface GamePriceService extends BaseService<GamePrice, Long> {

	List<GamePrice> findbyCardId(int cardId);
	List<GamePrice> findbyTypeId(int typeId);
}