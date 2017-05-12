/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.util.Collections;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.GameDao;
import net.wit.entity.Game;
import net.wit.entity.Member;

import org.springframework.stereotype.Repository;

/**
 * Dao - 游戏快充
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("gameDaoImpl")
public class GameDaoImpl extends BaseDaoImpl<Game, Long> implements GameDao {

	public Page<Game> findPage(Member member, Pageable pageable) {
		if (member == null) {
			return new Page<Game>(Collections.<Game> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Game> criteriaQuery = criteriaBuilder.createQuery(Game.class);
		Root<Game> root = criteriaQuery.from(Game.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
}