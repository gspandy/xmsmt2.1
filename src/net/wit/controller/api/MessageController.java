/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.controller.api;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.wit.Message;
import net.wit.entity.Member;
import net.wit.service.MemberService;
import net.wit.service.MessageService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
 
/**
 * Controller - 信息API
 * 
 * @author rsico Team
 * @version 3.0
 */
@Controller("apiMessageController")
@RequestMapping("/api/message")
public class MessageController extends BaseController  {
	
	@Resource(name = "messageServiceImpl")
	MessageService messageService;
	@Resource(name = "memberServiceImpl")
	MemberService memberService;
	
	/**
	 * 获取商家分类父结点
	 */
	@RequestMapping(value = "/send",method = RequestMethod.POST)
	public @ResponseBody 
	Message send(String username,String title,String content,HttpServletRequest request) {
		Member receiver = null;
		if (StringUtils.isNotEmpty(username)) {
			receiver = memberService.findByUsername(username);
			if (receiver == null) {
				return Message.error("无效用户");
			}
		}
		net.wit.entity.Message message = new net.wit.entity.Message();
		message.setTitle(title);
		message.setContent(content);
		message.setIp(request.getRemoteAddr());
		message.setIsDraft(false);
		message.setSenderRead(true);
		message.setReceiverRead(false);
		message.setSenderDelete(false);
		message.setReceiverDelete(false);
		message.setSender(null);
		message.setReceiver(receiver);
		message.setForMessage(null);
		message.setReplyMessages(null);
		messageService.save(message);
		return Message.success("发送成功");
	}
	
}