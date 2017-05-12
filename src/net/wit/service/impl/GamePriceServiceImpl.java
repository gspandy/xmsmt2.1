/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.dao.GamePriceDao;
import net.wit.entity.GamePrice;
import net.wit.service.GamePriceService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 地区
 * 
 * @author rsico++ Team
 * @version 3.0
 */
@Service("gamePriceServiceImpl")
public class GamePriceServiceImpl extends BaseServiceImpl<GamePrice, Long> implements GamePriceService {

	@Resource(name = "gamePriceDaoImpl")
	private GamePriceDao gamePriceDao;

	@Resource(name = "gamePriceDaoImpl")
	public void setBaseDao(GamePriceDao gamePriceDao) {
		super.setBaseDao(gamePriceDao);
	}

	@Override
	@Transactional
	@CacheEvict(value = "gamePrice", allEntries = true)
	public void save(GamePrice gamePrice) {
		super.save(gamePrice);
	}

	@Override
	@Transactional
	@CacheEvict(value = "gamePrice", allEntries = true)
	public GamePrice update(GamePrice gamePrice) {
		return super.update(gamePrice);
	}

	@Override
	@Transactional
	@CacheEvict(value = "gamePrice", allEntries = true)
	public GamePrice update(GamePrice gamePrice, String... ignoreProperties) {
		return super.update(gamePrice, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = "gamePrice", allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = "gamePrice", allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = "gamePrice", allEntries = true)
	public void delete(GamePrice gamePrice) {
		super.delete(gamePrice);
	}
	
	public List<GamePrice> findbyCardId(int cardId) {
       return gamePriceDao.findByCardId(cardId);
	}
	
	public List<GamePrice> findbyTypeId(int typeId) {
	       return gamePriceDao.findByTypeId(typeId);
		}
	
	
}