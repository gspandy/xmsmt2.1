package net.wit.job;

import java.util.Date;

import javax.annotation.Resource;

import net.wit.service.BatchJobService;
import net.wit.service.OrderService;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Job - 资金
 * 
 * @author Teddy
 * @version 1.0
 */
@Component("fundJob")
@Lazy(false)
public class FundJob {

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;
	
	@Resource(name = "batchJobImpl")
	private BatchJobService batchJobService;

	/**
	 * 店主佣金计算
	 */
//	@Scheduled(cron = "${job.fund_calculate_charge.cron}")
	public void calculateCharge() {
//		batchJobService.calculateCharge();
	}
	
	/**
	 * 店主租金计算
	 */
//	@Scheduled(cron = "${job.fund_calculate_rent.cron}")
	public void calculateRent() {
		batchJobService.calculateRent(new Date());
	}
	
	/**
	 * 店主奖金计算
	 */
//	@Scheduled(cron = "${job.fund_calculate_bonus.cron}")
	public void calculateBonus() {
		orderService.releaseStock();
	}

}