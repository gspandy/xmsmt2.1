package net.wit.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.LockModeType;

import net.wit.dao.OwnerCashDetailDao;
import net.wit.dao.OwnerDao;
import net.wit.dao.OwnerIncomeDao;
import net.wit.dao.RentDao;
import net.wit.entity.Owner;
import net.wit.entity.OwnerCashDetail;
import net.wit.entity.OwnerIncome;
import net.wit.service.OwnerServerice;
import net.wit.util.BizException;
import net.wit.util.BussConst;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-12
 * Time: 下午8:27
 * To change this template use File | Settings | File Templates.
 */
@Service("ownerServericeImpl")
public class OwnerServericeImpl extends BaseServiceImpl<Owner, Long>implements OwnerServerice {

//    @Autowired
//    private OwnerDao ownerDao;
	@Resource(name = "ownerDaoImpl")
	private OwnerDao ownerDao;

	@Resource(name = "ownerDaoImpl")
	public void setBaseDao(OwnerDao ownerDao) {
		super.setBaseDao(ownerDao);
	}
    
    @Autowired
    private OwnerCashDetailDao ownerCashDetailDao;
    
    @Autowired
    private OwnerIncomeDao ownerIncomeDao;

    public Owner getOwner(Long memberId) {
        List<Owner> list = ownerDao.getOwnerByMemberId(memberId);
        if(list.size() == 0) return null;
        else return list.get(0);
    }
    public String getBalanceWaitChargeByMemberId(Long memberId) {
          return null;
    }

    public List<Owner> getOwnerByMemberId(Long memberId) {
        return  ownerDao.getOwnerByMemberId(memberId);
    }
    
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public void transferAccount(OwnerIncome ownerIncome)throws BizException{
    	Long memberId = ownerIncome.getMemberId();
    	String incomeType = ownerIncome.getIncomeType();
    	BigDecimal amount = ownerIncome.getIncomeAmt();
    	Owner owner = getOwner(memberId);
		if(owner == null){
			throw new BizException("店长信息不存在："+memberId);
		}
    	ownerDao.lock(owner, LockModeType.PESSIMISTIC_WRITE);
    	if(BussConst.INCOME_TYPE_CHARGE.equals(incomeType)){
    		owner.setTotalCharge(owner.getTotalCharge().add(amount));
    	}else
    		if(BussConst.INCOME_TYPE_BONUS.equals(incomeType)){
    			owner.setTotalBonus(owner.getTotalBonus().add(amount));
    		}else
    			if(BussConst.INCOME_TYPE_RENT.equals(incomeType)){
    				owner.setTotalRent(owner.getTotalRent().subtract(amount));
    			}else
    				if(BussConst.INCOME_TYPE_RECOMMEND.equals(incomeType)){
    					owner.setTotalBonus(owner.getTotalBonus().add(amount));
    				}else{
    					throw new BizException("收入类型未知："+incomeType);
    				}
    	ownerDao.merge(owner);
    	//保存提现记录
    	ownerIncome.setIncomeDate(new Date());
    	ownerIncome.setIncomeType(ownerIncome.getIncomeType());
    	ownerIncome.setMemberId(ownerIncome.getMemberId());
    	ownerIncomeDao.persist(ownerIncome);
    } 
    
    
    @Transactional
    public synchronized void accountCash(OwnerCashDetail ownerCashDetail)throws BizException{
    	BigDecimal amount = ownerCashDetail.getCashAmt();
    	Owner owner = getOwner(ownerCashDetail.getMemberId());
    	if(amount.compareTo(owner.getAccountBalance()) > 0){
    		throw new BizException("账户余额不足");
    	}
    	//扣减账户金额
    	ownerDao.lock(owner, LockModeType.PESSIMISTIC_WRITE);
    	owner.setAccountBalance(owner.getAccountBalance().subtract(ownerCashDetail.getCashAmt()));
    	ownerDao.merge(owner);
    	//保存提现记录
    	ownerCashDetail.setCashDate(new Date());
    	ownerCashDetail.setStatus(BussConst.CASH_TYPE_APPLYING);
    	ownerCashDetailDao.persist(ownerCashDetail);
    }  
    
    @Transactional
    public synchronized void accountCashDone(String cashrequestid,String status)throws BizException{
    	OwnerCashDetail ownerCashDetail = ownerCashDetailDao.findByCashRequestId(cashrequestid);
    	if(BussConst.CASH_TYPE_FAIL.equals(status)){
    		Owner owner = getOwner(ownerCashDetail.getMemberId());
        	ownerDao.lock(owner, LockModeType.PESSIMISTIC_WRITE);
        	owner.setAccountBalance(owner.getAccountBalance().add(ownerCashDetail.getCashAmt()));
        	ownerDao.merge(owner);
    	}
    	ownerCashDetail.setStatus(status);
    	ownerCashDetailDao.merge(ownerCashDetail);
    }  

}
