package net.wit.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.wit.dao.MessageDetailsDao;
import net.wit.entity.MessageDetails;
import net.wit.entity.MessageDetails.MessageType;
import net.wit.entity.Order;
import net.wit.entity.Tenant;
import net.wit.service.MessageDetailsService;

/**
 * @ClassName：MessageDetailsServiceImpl @Description：
 * @author：Chenlf
 * @date：2015年9月13日 下午10:45:25
 */
@Service("messageDetailsServiceImpl")
public class MessageDetailsServiceImpl extends BaseServiceImpl<MessageDetails, Long>implements MessageDetailsService {

	@Resource(name = "messageDetailsDaoImpl")
	public void setBaseDao(MessageDetailsDao messageDetailsDao) {
		super.setBaseDao(messageDetailsDao);
	}

	@Resource(name = "messageDetailsDaoImpl")
	private MessageDetailsDao messageDetailsDao;

	public void pushMessage(MessageType messageType, boolean level, Tenant tenant, String messageContent, Order order, boolean messageStat) {
		MessageDetails messageDetails = new MessageDetails();
		messageDetails.setType(messageType);
		messageDetails.setLevel(true);
		messageDetails.setTenant(tenant);
		messageDetails.setMessageContent(messageContent);
		messageDetails.setOrder(order);
		messageDetails.setMessageStat(messageStat);
		messageDetailsDao.persist(messageDetails);
	}

}
