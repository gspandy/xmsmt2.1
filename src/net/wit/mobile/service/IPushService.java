/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: IPushService.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	12 Sep,2015
 */
package net.wit.mobile.service;

import java.util.List;

import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.mobile.bo.NToken;
import net.wit.mobile.util.rong.models.SystemMessage;
import net.wit.mobile.util.rong.models.TxtMessage;
import net.wit.vo.SystemMessageVO;

/**
 * 
 * @author: weihuang.peng
 * @version Revision: 0.0.1
 * @Date: 12 Sep,2015
 */
public interface IPushService {
	
	/**
	 * 获取融云token令牌
	 * @author: weihuang.peng
	 * @param nToken nToken对象
	 * @return
	 */
	public String getToken(NToken nToken);
	
	/**
	 * 获取融云token令牌
	 * @author: weihuang.peng
	 * @param id 用户ID
	 * @param username 用户名称
	 * @param portraitUri 头像
	 * @return
	 */
	public String getToken(String id, String username, String portraitUri);
	
	/**
	 * 发布消息（一对多）
	 * @author: weihuang.peng
	 * @param fromUserId 发送消息的用户ID
	 * @param toUserIds 接收消息的用户ID列表
	 * @param msg 文本消息对象
	 * @return
	 */
	public boolean publishMessage(String fromUserId, List<String> toUserIds, TxtMessage msg);
	
	
	
	/**
	 * 刷新用户token信息;
	 * @param fromUserId
	 * @param userName
	 * @param portraitUri
	 * @return
	 */
	public boolean refreshToken(String fromUserId,String userName,String portraitUri);

	
	
	/**
	 * 企业公告
	 * @param fromUserId
	 * @param toUserIds
	 * @param title
	 * @param content
	 * @return
	 */
	public boolean publishNoticeMessage(String fromUserId,List<String> toUserIds,String title,String content);
   /**
    * 系统消息
    * @param tenant
    * @param member
    * @param message
    * @return
    */
	public boolean publishSystemMessage(Tenant tenant, Member member,
			SystemMessage message);

	/**
	 * 系统消息 by VO
	 * @param vo
	 * @return
	 */
	public boolean publishSystemMessage(SystemMessageVO vo);
}
