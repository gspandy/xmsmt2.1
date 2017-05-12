package net.wit.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.BonusCalcDao;
import net.wit.entity.BonusCalc;
import net.wit.entity.Charge;
import net.wit.entity.Owner;
import net.wit.entity.Tenant;
import net.wit.service.BonusCalcService;
import net.wit.service.ChargeService;
import net.wit.service.OwnerServerice;
import net.wit.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 */
@Service("bonusCalcServiceImpl")
public class BonusCalcServiceImpl extends BaseServiceImpl<BonusCalc, Long>implements BonusCalcService {

	@Resource(name="bonusCalcDaoImpl")
	private BonusCalcDao bonusCalcDao;
	
	@Resource(name = "bonusCalcDaoImpl")
	public void setBaseDao(BonusCalcDao bonusCalcDao) {
		super.setBaseDao(bonusCalcDao);
	}
	
	@Autowired
	private ChargeService chargeService;
	
	
	@Autowired
	private OwnerServerice ownerServerice;
	
	@Override
	public Page<BonusCalc> getBonusCalcList(Long memberId, int pageNo,
			int pageSize) {
		 Pageable pageable=new Pageable();
		 pageable.setPageSize(pageSize);
		 pageable.setPageNumber(pageNo);
		 return bonusCalcDao.getBonusCalcList(memberId, pageable);
	}
	public Page<BonusCalc> findByChargeId(Long chargeId, Pageable pageable){
		return bonusCalcDao.findByChargeId(chargeId, pageable);
	}
	
	@Override
	@Transactional
	public void totalBonusSubmit(Date bonusTime, Tenant tenant){
		 Calendar c = Calendar.getInstance();
		 c.add(Calendar.MONTH, -1);//获取上个月时间
			String strDate = DateUtil.changeDateToStr(c.getTime(),
					DateUtil.LINK_DISPLAY_DATE_MONTH);
		List<BonusCalc> list  = bonusCalcDao.findBonusByTenant(tenant);
		if(list.size()==0) return;
		HashMap<Long, Charge> map = new HashMap<Long, Charge>();
		HashMap<Long, List<BonusCalc>> bonusCalcMap = new HashMap<Long , List<BonusCalc>>();			
		Charge charge;
		for (BonusCalc bonusCalc : list) {   //加总奖金数据
			Long memberId = bonusCalc.getMember().getId();
			if(!map.containsKey(memberId)){
				charge = new Charge();
				charge.setCharge(bonusCalc.getBonus());
				charge.setMember(bonusCalc.getMember());
				charge.setStatus(Charge.Status.notReceive);
				charge.setType(Charge.Type.bonus);
				charge.setTenant(bonusCalc.getTenant());
				charge.setChargeDate(strDate);
				map.put(memberId, charge);
			}else{
				charge = map.get(memberId);
				charge.setCharge(charge.getCharge().add(bonusCalc.getBonus()));
			}
			if(!bonusCalcMap.containsKey(memberId)){
				List<BonusCalc> bonusCalcList = new ArrayList<BonusCalc>();
				bonusCalcList.add(bonusCalc);
				bonusCalcMap.put(memberId, bonusCalcList);
			}else{
				bonusCalcMap.get(memberId).add(bonusCalc);
			}
		}
		Set<Long> set = map.keySet();  //循环保存奖金 ,并修改奖金明细状态
		for (Long keyLong : set) {
				Owner owner = ownerServerice.getOwner(keyLong);
				charge = map.get(keyLong);
				owner.setTotalBonus(owner.getTotalBonus().add(charge.getCharge()));  //账户表 奖金加总 
				owner.setTotalAmt(owner.getTotalAmt().add(charge.getCharge())); //账户表总收入加总
				List<BonusCalc> boncalcList = bonusCalcMap.get(keyLong);
				chargeService.submitBonusCharge(owner,charge,boncalcList);
		}
	}

}
