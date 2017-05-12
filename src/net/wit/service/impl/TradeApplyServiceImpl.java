/**
 *====================================================
 * 文件名称: TradeApplyServiceImpl.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年8月19日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import net.wit.dao.RefundsDao;
import net.wit.dao.ReturnsDao;
import net.wit.dao.TradeApplyDao;
import net.wit.dao.TradeDao;
import net.wit.entity.Order.ShippingStatus;
import net.wit.entity.Member;
import net.wit.entity.OrderItem;
import net.wit.entity.Refunds;
import net.wit.entity.Returns;
import net.wit.entity.ReturnsItem;
import net.wit.entity.Shipping;
import net.wit.entity.Trade;
import net.wit.entity.TradeApply;
import net.wit.service.TradeApplyService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName: TradeApplyServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年8月19日 下午1:45:50
 */
@Service("tradeApplyServiceImpl")
public class TradeApplyServiceImpl extends BaseServiceImpl<TradeApply, Long> implements TradeApplyService {

	@Resource(name = "tradeApplyDaoImpl")
	private TradeApplyDao tradeApplyDao;
	
	@Resource(name = "tradeDaoImpl")
	private TradeDao tradeDao;
	
	@Resource(name = "refundsDaoImpl")
	private RefundsDao refundsDao;
	
	@Resource(name = "returnsDaoImpl")
	private ReturnsDao returnsDao;

	@Resource(name = "tradeApplyDaoImpl")
	public void setBaseDao(TradeApplyDao tradeApplyDao) {
		super.setBaseDao(tradeApplyDao);
	}

	@Transactional
	public void rejected(TradeApply apply) {
		Trade trade = apply.getTrade();
		if(trade.getShippingStatus()==ShippingStatus.partialShipment){
			trade.setShippingStatus(ShippingStatus.partialShipment);
		}else{
			trade.setShippingStatus(ShippingStatus.shipped);
		}
		tradeDao.persist(trade);
		apply.setStatus(TradeApply.Status.rejected);
		tradeApplyDao.persist(apply);
	}

	@Transactional
	public void refunds(Trade trade,Refunds refunds) {
		trade.setShippingStatus(ShippingStatus.shipped);
		TradeApply apply = trade.getSubmitApply();
		tradeDao.persist(trade);
		apply.setStatus(TradeApply.Status.passed);
		tradeApplyDao.persist(apply);
		refundsDao.persist(refunds);
	}

	@Transactional
	public void change(TradeApply apply) {
		Trade trade = apply.getTrade();
		if(trade.getShippingStatus()==ShippingStatus.partialShipment){
			trade.setShippingStatus(ShippingStatus.partialShipment);
		}else{
			trade.setShippingStatus(ShippingStatus.shipped);
		}
		tradeDao.persist(trade);
		apply.setStatus(TradeApply.Status.passed);
		tradeApplyDao.persist(apply);
	}

	@Transactional
	public void returns(Member member,Trade trade,Returns returns,Refunds refunds) {
		TradeApply apply = trade.getSubmitApply();
		//生成退货单
		returns.setOrder(trade.getOrder());
		returns.setTrade(trade);
		returns.setShipper(trade.getOrder().getConsignee());
		Shipping shipping = trade.getShippings().iterator().next();
		for (OrderItem orderItem : trade.getOrderItems()) {
			ReturnsItem returnsItem = new ReturnsItem();
			returnsItem.setSn(orderItem.getSn());
			returnsItem.setName(orderItem.getName());
			returnsItem.setQuantity(orderItem.getShippedQuantity());
			returnsItem.setReturns(returns);
			returns.getReturnsItems().add(returnsItem);
		}
		returns.setDeliveryCenter(shipping.getDeliveryCenter());
		returns.setDeliveryCorp(trade.getSubmitApply().getDeliveryCorpName());
		returns.setArea(member.getTenant().getArea().getName());
		returns.setAddress(member.getTenant().getAddress());
		returns.setPhone(member.getTenant().getTelephone());
		returns.setTrackingNo(apply.getTrackingNo());
		returns.setZipCode(member.getTenant().getZipcode()==null?"000000":member.getTenant().getZipcode());
		returns.setFreight(new BigDecimal(0));
		if(trade.getSubmitApply().getShippingMethod()!=null){
			returns.setShippingMethod(trade.getSubmitApply().getShippingMethod().getName());
		}
		returnsDao.persist(returns);
		//生成退款单
		refunds.setOrder(trade.getOrder());
		refunds.setMethod(Refunds.Method.online);
		refunds.setAmount(trade.getSubmitApply().getRefundAmount());
		refunds.setOperator(member.getTenant().getName());
		refundsDao.persist(refunds);
		
		trade.setShippingStatus(ShippingStatus.returned);
		tradeDao.persist(trade);
		
		apply.setStatus(TradeApply.Status.passed);
		tradeApplyDao.persist(apply);
	}

}
