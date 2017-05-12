package net.wit.service;

import net.wit.entity.MessageDetails;
import net.wit.entity.MessageDetails.MessageType;
import net.wit.entity.Order;
import net.wit.entity.Tenant;

/**
 * @ClassName：MessageDetailsService @Description：
 * @author：Chenlf
 * @date：2015年9月13日 下午10:44:35
 */
public interface MessageDetailsService extends BaseService<MessageDetails, Long> {

	/**
	 * @Title：pushMessage @Description：
	 * @param order
	 * @param b
	 * @param tenant
	 * @param string
	 * @param order2
	 * @param c void
	 */
	void pushMessage(MessageType messageType, boolean level, Tenant tenant, String messageContent, Order order, boolean messageStat);

}
