package net.wit.service;

import java.util.Date;
import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.BonusCalc;
import net.wit.entity.Tenant;

/**
 * 
 */
public interface BonusCalcService extends BaseService<BonusCalc, Long> {
	/**
	 * 查询邀请店的奖金明细
	 */
	 public Page<BonusCalc> getBonusCalcList(Long memberId,int pageNo,int pageSize);
	 /**
	  * 通过提现id查询列表
	  * @param chargeId
	  * @param pageable
	  * @return
	  */
	 public Page<BonusCalc> findByChargeId(Long chargeId, Pageable pageable);
	/**
	 * 加总为结算奖金明细
	 * @param bonusTime
	 * @param tenant
	 */
	 public void totalBonusSubmit(Date bonusTime, Tenant tenant);
}
