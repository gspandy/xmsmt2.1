/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import net.wit.dao.CartItemDao;
import net.wit.entity.CartItem;

import org.springframework.stereotype.Repository;

/**
 * Dao - 购物车项
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("cartItemDaoImpl")
public class CartItemDaoImpl extends BaseDaoImpl<CartItem, Long> implements CartItemDao {

}