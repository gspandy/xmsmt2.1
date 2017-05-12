package net.wit.service;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.OwnerIncome;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-15
 * Time: 下午7:09
 * To change this template use File | Settings | File Templates.
 */
public interface OwnerIncomeService extends BaseService<OwnerIncome, Long> {

    public Page<OwnerIncome> getOwnerIncomeByMemberId(Long memberId, Pageable pageable);
}
