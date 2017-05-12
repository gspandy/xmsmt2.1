package net.wit.mobile.controller;

import java.util.Date;
import java.util.List;

import net.wit.entity.OrderSettlement;
import net.wit.entity.Tenant;
import net.wit.entity.TicketApplyCondition;
import net.wit.service.BatchJobService;
import net.wit.service.BonusCalcService;
import net.wit.service.OrderSettlementService;
import net.wit.service.TenantService;
import net.wit.service.TicketApplyConditionService;
import net.wit.service.TicketApplyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping(value = "/batchJob")
public class BatchJobController extends BaseController {

    
    @Autowired
    private BatchJobService batchJobService;
    
    @Autowired
    private TenantService tenantService;
    
    @Autowired
    private BonusCalcService bonusCalcService;
    
    @Autowired
    private TicketApplyConditionService ticketApplyConditionService;
    
    @Autowired
    private TicketApplyService ticketApplyService;
    
    

    
    private Logger log = LoggerFactory.getLogger(BatchJobController.class);
    
    
    /**
     * 订单结算批
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/orderSettle")
    public void orderSettle() throws Exception{
    	try {
    		Date batchDate = new Date();
    		if(log.isInfoEnabled()) log.info("月结算批 开始执行，当前时间============"+batchDate);
    		batchJobService.orderSettle(batchDate);
    		List<Tenant> list = tenantService.findAll();
    		for (Tenant tenant : list) {
    			bonusCalcService.totalBonusSubmit(batchDate, tenant);
			}
    		if(log.isInfoEnabled()) log.info("月结算批 结束，当前时间============"+new Date());
    	} catch (Exception e) {
    		log.error(e.getMessage());
    	}
    }
    
    
    
    
    
    /**
     * 订单完成批
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/orderComplete")
    public void orderComplete() throws Exception{
    	try {
    		Date batchDate = new Date();
    		if(log.isInfoEnabled()) log.info("订单完成批 开始执行，当前时间============"+batchDate);
    		batchJobService.orderComplete(batchDate);
    		if(log.isInfoEnabled()) log.info("订单完成批 结束，当前时间============"+new Date());
    	} catch (Exception e) {
    		log.error(e.getMessage());
    	}
    }
    
    /**
     * 取消退货批
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/returnCancel")
    public void returnCancel() throws Exception{
    	try {
    		Date batchDate = new Date();
    		if(log.isInfoEnabled()) log.info("订单取消退货批 开始执行，当前时间============"+batchDate);
    		batchJobService.returnCancel(batchDate);
    		if(log.isInfoEnabled()) log.info("订单取消退货批 结束，当前时间============"+new Date());
    	} catch (Exception e) {
    		log.error(e.getMessage());
    	}
    }
    /**
     * 订单支付过期
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/releaseStock")
    public void releaseStock() throws Exception{
    	try {
    		Date batchDate = new Date();
    		if(log.isInfoEnabled()) log.info("订单过期批 开始执行，当前时间============"+batchDate);
    		batchJobService.orderExpire(batchDate);
    		if(log.isInfoEnabled()) log.info("订单过期批 结束，当前时间============"+new Date());
    	} catch (Exception e) {
    		log.error(e.getMessage());
    	}
    }
    /**
     * 订单自动收货批
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/takeDelivery")
    public void takeDelivery() throws Exception{
    	try {
    		Date batchDate = new Date();
    		if(log.isInfoEnabled()) log.info("订单自动收货 开始执行，当前时间============"+batchDate);
    		batchJobService.orderAccept(batchDate);
    		if(log.isInfoEnabled()) log.info("订单自动收货 结束，当前时间============"+new Date());
    	} catch (Exception e) {
    		log.error(e.getMessage());
    	}
    }
    
    
    
    
    @ResponseBody
    @RequestMapping(value = "/calculateRent")
    public void calculateRent() throws Exception{
    	try {
    		Date batchDate = new Date();
    		if(log.isInfoEnabled()) log.info("收租计算 开始执行，当前时间============"+batchDate);
    		batchJobService.calculateRent(batchDate);
    		if(log.isInfoEnabled()) log.info("收租计算 收货 结束，当前时间============"+new Date());
    	} catch (Exception e) {
    		log.error(e.getMessage());
    	}
    }
    
    /**券券申请过期批*/
    @ResponseBody
    @RequestMapping(value = "/ticketApplyAutoReject")
    public void ticketApplyAutoReject() throws Exception{
    	try {
    		Date batchDate = new Date();
    		if(log.isInfoEnabled()) log.info("券券申请过期批 开始执行，当前时间============"+batchDate);
    		List<Tenant> list =tenantService.findAll();
    		for (Tenant tenant : list) {
    			TicketApplyCondition tac = ticketApplyConditionService.getTicketApplyConditionByTenant(tenant);
    			if(tac ==null) continue;
    			ticketApplyService.batchRejectApply(tenant, tac.getAutoRejectDays());
			}
    		if(log.isInfoEnabled()) log.info("券券申请过期批 结束，当前时间============"+new Date());
    	} catch (Exception e) {
    		log.error(e.getMessage());
    	}
    }
    
}
