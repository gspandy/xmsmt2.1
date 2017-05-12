/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import net.wit.Message;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Credit;
import net.wit.entity.Member;
import net.wit.entity.PayBank;

/**
 * Service - 收款单
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface PayBankService extends BaseService<PayBank, Long> {

	Page<PayBank> findPage(Member member, Pageable pageable);
	
	Message payToBank(Credit credit,String trackNo,String flag);
	Message checkBank(Credit credit,String trackNo);
}