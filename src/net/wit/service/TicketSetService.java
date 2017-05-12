/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSetService.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.service;
import java.util.List;

import net.wit.entity.TicketSet;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月7日
 */
public interface TicketSetService extends BaseService<TicketSet, Long>{
	/**
	 * 获取企业的内购券定额发放设置
	 * @param tenantId
	 * @return
	 */
	public TicketSet getRegularTicketSetByTenantId(Long tenantId);
	
	
	
	/**
	 * 获取企业新店主内购券发放设置
	 * @param tenantId
	 * @return
	 */
	public TicketSet getNewShopKeeperTicketSetByTenantId(Long tenantId);
	/**
	 * 根据条件获取券发放设置
	 * @param tenantId
	 * @param sendType
	 * @param useFlag
	 * @return
	 */
	public List<TicketSet> getTicketSet(Long tenantId,String sendType,String useFlag);



	public TicketSet getApplyTicketSetByTenantId(Long tenantId);

}
