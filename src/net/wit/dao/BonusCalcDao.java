package net.wit.dao;

import java.util.Date;
import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.BonusCalc;
import net.wit.entity.Tenant;

public interface BonusCalcDao extends BaseDao<BonusCalc, Long>{

	public Page<BonusCalc> getBonusCalcList(Long memberId,Pageable pageable);
	/**
	 * 通过提现id查询列表
	 * @param chargeId
	 * @param pageable
	 * @return
	 */
	public Page<BonusCalc> findByChargeId(Long chargeId, Pageable pageable);
	
	public void addBonusCalc(BonusCalc bonusCalc);
	/**
	 * 根据时间查出所有未加总和没有结算的奖金明细
	 * @param bonusTime
	 * @return
	 */
	public List<BonusCalc> findBonusByTenant(Tenant tenant);
	
}
