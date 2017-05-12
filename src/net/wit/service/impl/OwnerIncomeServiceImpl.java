package net.wit.service.impl;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.OwnerIncomeDao;
import net.wit.entity.OwnerIncome;
import net.wit.service.OwnerIncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-15
 * Time: 下午7:10
 * To change this template use File | Settings | File Templates.
 */
@Service("ownerIncomeServiceImpl")
public class OwnerIncomeServiceImpl extends BaseServiceImpl<OwnerIncome, Long>implements OwnerIncomeService {


    @Autowired
    private OwnerIncomeDao ownerIncomeDao;

    public Page<OwnerIncome> getOwnerIncomeByMemberId(Long memberId, Pageable pageable) {
        return ownerIncomeDao.getOwnerIncomeByMemberId(memberId, pageable);
    }
}
