/**
 * 
 */
package net.wit.service.impl;

import javax.annotation.Resource;

import net.wit.dao.TradeFollowupDao;
import net.wit.entity.TradeFollowup;
import net.wit.service.TradeFollowupService;

import org.springframework.stereotype.Service;

/**
 * @author Administrator
 *
 */
@Service("tradeFollowupServiceImpl")
public class TradeFollowupServiceImpl extends BaseServiceImpl<TradeFollowup, Long> implements TradeFollowupService {

	@Resource(name = "tradeFollowupDaoImpl")
	private TradeFollowupDao tradeFollowupDao;

	@Resource(name = "tradeFollowupDaoImpl")
	public void setBaseDao(TradeFollowupDao tradeFollowupDao) {
		super.setBaseDao(tradeFollowupDao);
	}

}
