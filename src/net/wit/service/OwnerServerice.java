package net.wit.service;

import net.wit.entity.Owner;
import net.wit.entity.OwnerCashDetail;
import net.wit.entity.OwnerIncome;
import net.wit.util.BizException;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-12
 * Time: 下午8:27
 * To change this template use File | Settings | File Templates.
 */
public interface OwnerServerice extends BaseService<Owner, Long> {

    public List<Owner> getOwnerByMemberId(Long memberId);

    public String getBalanceWaitChargeByMemberId(Long memberId);
    
    public Owner getOwner(Long memberId);
    /**
     * 提现申请
     * @param ownerCashDetail  提现明细
     * @throws BizException
     */
    public  void accountCash(OwnerCashDetail ownerCashDetail)throws BizException;
    /**
     * 提现完成
     * @param cashrequestid 提现申请Id
     * @param status 提现状态
     * @throws BizException
     */
    public  void accountCashDone(String cashrequestid,String status)throws BizException;
    /**
     * 账户收入
     * @param ownerIncome
     * @throws BizException
     */
    public void transferAccount(OwnerIncome ownerIncome)throws BizException;
    
}
