/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;

import net.wit.dao.GamePriceDao;
import net.wit.entity.GamePrice;

import org.springframework.stereotype.Repository;

/**
 * Dao - 手机快充价格
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("gamePriceDaoImpl")
public class GamePriceDaoImpl extends BaseDaoImpl<GamePrice, Long> implements GamePriceDao {

	public List<GamePrice> findByCardId(int cardId) {
		String jpql = "select gamePrice from GamePrice gamePrice where gamePrice.cardId=:cardId order by gamePrice.seqNo asc";
		TypedQuery<GamePrice> query = entityManager.createQuery(jpql, GamePrice.class).setFlushMode(FlushModeType.COMMIT).setParameter("cardId", cardId);
		return query.getResultList();
	}
	public List<GamePrice> findByTypeId(int typeId) {
		String jpql = "select gamePrice from GamePrice gamePrice where gamePrice.typeId=:typeId order by gamePrice.seqNo asc";
		TypedQuery<GamePrice> query = entityManager.createQuery(jpql, GamePrice.class).setFlushMode(FlushModeType.COMMIT).setParameter("typeId", typeId);
		return query.getResultList();
	}
}