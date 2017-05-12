package net.wit.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.TradeDao;
import net.wit.entity.Member;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.Order.ShippingStatus;
import net.wit.entity.Trade;
import net.wit.service.TradeService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: www.insuper.com
 * </p>
 * <p>
 * Company: www.insuper.com
 * </p>
 * @author liumx
 * @version 1.0
 * @date 2013年7月2日15:46:16
 */
@Service("tradeServiceImpl")
public class TradeServiceImpl extends BaseServiceImpl<Trade, Long> implements TradeService {

	@Resource(name = "tradeDaoImpl")
	private TradeDao tradeDao;

	@Resource(name = "tradeDaoImpl")
	public void setBaseDao(TradeDao tradeDao) {
		super.setBaseDao(tradeDao);
	}

	@Transactional(readOnly = true)
	public Trade findBySn(String sn) {
		return tradeDao.findBySn(sn);
	}

	@Transactional(readOnly = true)
	public Page<Trade> findPage(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable) {
		return tradeDao.findPage(member, orderStatus, paymentStatus, shippingStatus, hasExpired, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Trade> findPage(Member member, Date beginDate, Date endDate, Pageable pageable) {
		return tradeDao.findPage(member, beginDate, endDate, pageable);
	}
	
	@Transactional(readOnly = true)
	public Page<Trade> findPage(Member member, Pageable pageable) {
		return tradeDao.findPage(member,pageable);
	}
	
	public long countTenant(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired) {
		return tradeDao.countTenant(member, orderStatus, paymentStatus, shippingStatus, hasExpired);
	}
}
