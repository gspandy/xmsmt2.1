/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.GameDao;
import net.wit.entity.Game;
import net.wit.entity.Member;
import net.wit.exception.BalanceNotEnoughException;
import net.wit.service.GameService;
import net.wit.service.MemberService;
import net.wit.webservice.TerminalXmlService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 手机快充
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("gameServiceImpl")
public class GameServiceImpl extends BaseServiceImpl<Game, Long> implements GameService {

	@Resource(name = "gameDaoImpl")
	private GameDao gameDao;
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "gameDaoImpl")
	public void setBaseDao(GameDao gameDao) {
		super.setBaseDao(gameDao);
	}


	@Transactional(readOnly = true)
	public Page<Game> findPage(Member member, Pageable pageable) {
		return gameDao.findPage(member, pageable);
	}
	

	public void fill(Game game) {
	  ResourceBundle bundle=PropertyResourceBundle.getBundle("config");
	  game.setTermNo(bundle.getString("dPosTermNo"));
	  game.setMerNo(bundle.getString("dPosMerNo"));
	  SimpleDateFormat ReqTime =new SimpleDateFormat("yyyyMMddHHmmss");
	  game.setReqTime(ReqTime.format(new Date()));
	  game.setRespCode("01");
	  game.setRespMsg("提交成功");
	  gameDao.persist(game);
	  if ("01".equals(game.getRespCode())) {
		  try {
		    memberService.update(game.getMember(),null, new BigDecimal(0).subtract(game.getFee()),"游戏直充",null);
		  } catch (BalanceNotEnoughException e){
			  game.setRespCode("32");
			  game.setRespMsg("账户余额不足");  
		  } catch (Exception e) {
			  game.setRespCode("9999");
			  game.setRespMsg("未知异常");
		  }
	  }
	  
	  if ("01".equals(game.getRespCode())) {
		  TerminalXmlService dPos = new TerminalXmlService();
          dPos.directCharge(game);
		  //mobile.setRespCode("00");
		  //mobile.setRespMsg("提交成功");
    	  if (!"0".equals(game.getRespCode())) {
    		  try {
    		    memberService.Refunds(game.getMember(),null, game.getFee(),"游戏直充失败，退回款项",null);
    		  } catch (Exception e) {
    			  game.setRespCode("9999");
    			  game.setRespMsg("未知异常，款项待退");
    		  }
    	  } else {
    		  game.setRespCode("01");
    		  game.setRespMsg("提交成功");
    	  }
    	  
	  }
	  
	  gameDao.merge(game);

	}
	
	public void notify(Game game) {
		  TerminalXmlService dPos = new TerminalXmlService();
          dPos.directOrderStateQuery(game);
		  if ("0".equals(game.getRetCode()) || "1909".equals(game.getRetCode()) || "1900".equals(game.getRetCode()) || "1910".equals(game.getRetCode()) ) {
			  SimpleDateFormat retTime =new SimpleDateFormat("yyyyMMddHHmmss");
			  game.setRetTime(retTime.format(new Date()));
			  gameDao.merge(game);
		  }
	}
}

