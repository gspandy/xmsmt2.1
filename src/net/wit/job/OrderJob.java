/*
 * Copyright 2005-2013 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.wit.job;

import java.util.Date;

import javax.annotation.Resource;

import jodd.http.HttpBrowser;
import jodd.http.HttpRequest;
import net.wit.mobile.cache.CacheUtil;
import net.wit.service.BatchJobService;
import net.wit.service.OrderService;
import net.wit.util.BussConst;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Job - 订单
 * 
 * @author Teddy
 * @version 1.0
 */
@Component("orderJob")
@Lazy(false)
public class OrderJob {

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;
	
	@Resource(name = "batchJobImpl")
	private BatchJobService batchJobService;
	
	private Logger log = LoggerFactory.getLogger(OrderJob.class);
	
	/**
	 * 释放过期订单库存（已下单未支付的订单）
	 */
	@Scheduled(cron = "${job.order_release_stock.cron}")
	public void releaseStock() {
		Date batchDate = new Date();
		if(log.isInfoEnabled()) log.info("订单过期批开启，当前时间============"+batchDate);
		String uri = CacheUtil.getParamValueByName(BussConst.PARAM_SERVICE_URL);
		String destination = uri + "/releaseStock.mobile";
		HttpBrowser browser = new HttpBrowser();
		HttpRequest request = HttpRequest.post(destination);
		browser.sendRequest(request);
	}
	
	/**
	 * 订单完成（已确认收货）
	 */
	@Scheduled(cron = "${job.order_complete.cron}")
	public void orderComplete() {
		Date batchDate = new Date();
		if(log.isInfoEnabled()) log.info("订单完成批开启，当前时间============"+batchDate);
		String uri = CacheUtil.getParamValueByName(BussConst.PARAM_SERVICE_URL);
		String destination = uri + "/orderComplete.mobile";
		HttpBrowser browser = new HttpBrowser();
		HttpRequest request = HttpRequest.post(destination);
		browser.sendRequest(request);
	}
	
	/**
	 * 订单自动收货（到了收货期买家仍未确认收货的订单）
	 */
	@Scheduled(cron = "${job.order_take_delivery.cron}")
	public void takeDelivery() {
		Date batchDate = new Date();
		if(log.isInfoEnabled()) log.info("订单自动收货批开启，当前时间============"+batchDate);
		String uri = CacheUtil.getParamValueByName(BussConst.PARAM_SERVICE_URL);
		String destination = uri + "/takeDelivery.mobile";
		HttpBrowser browser = new HttpBrowser();
		HttpRequest request = HttpRequest.post(destination);
		browser.sendRequest(request);
	}
	
	/**
	 * 订单自动结算
	 */
	@Scheduled(cron = "${job.order_settle.cron}")
	public void orderSettle() {
		Date batchDate = new Date();
		if(log.isInfoEnabled()) log.info("订单结算批开启，当前时间============"+batchDate);
		String uri = CacheUtil.getParamValueByName(BussConst.PARAM_SERVICE_URL);
		String destination = uri + "/orderSettle.mobile";
		HttpBrowser browser = new HttpBrowser();
		HttpRequest request = HttpRequest.post(destination);
		browser.sendRequest(request);
	}
	/**
	 * 订单退货过期自动完成
	 */
	@Scheduled(cron = "${job.return_cancel.cron}")
	public void returnCancel() {
		Date batchDate = new Date();
		if(log.isInfoEnabled()) log.info("订单取消退货批开启，当前时间============"+batchDate);
		String uri = CacheUtil.getParamValueByName(BussConst.PARAM_SERVICE_URL);
		String destination = uri + "/returnCancel.mobile";
		HttpBrowser browser = new HttpBrowser();
		HttpRequest request = HttpRequest.post(destination);
		browser.sendRequest(request);
	}

}