/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import javax.annotation.Resource;

import net.wit.dao.CartItemDao;
import net.wit.entity.CartItem;
import net.wit.service.CartItemService;

import org.springframework.stereotype.Service;

/**
 * Service - 购物车项
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("cartItemServiceImpl")
public class CartItemServiceImpl extends BaseServiceImpl<CartItem, Long> implements CartItemService {

	@Resource(name = "cartItemDaoImpl")
	public void setBaseDao(CartItemDao cartItemDao) {
		super.setBaseDao(cartItemDao);
	}

}