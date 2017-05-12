/*
 * Copyright 2005-2013 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.wit.job;

import java.util.Date;

import javax.annotation.Resource;

import net.wit.service.BatchJobService;
import net.wit.service.OrderService;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Job - 内购券
 * 
 * @author Teddy
 * @version 1.0
 */
@Component("ticketJob")
@Lazy(false)
public class TicketJob {

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;
	
	@Resource(name = "batchJobImpl")
	private BatchJobService batchJobService;

	/**
	 * 内购券定额发放
	 */
	@Scheduled(cron = "${job.ticket_send_ticket.cron}")
	public void sendTicket() {
		batchJobService.batchSendTicket(new Date());
	}
	
	/**
	 * 释放过期内购券
	 */
	@Scheduled(cron = "${job.ticket_release_ticket.cron}")
	public void releaseTicket() {
		batchJobService.releaseTicket(new Date());
	}

}