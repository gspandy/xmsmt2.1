/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;


import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.CreditDao;
import net.wit.dao.PayBankDao;
import net.wit.entity.Credit;
import net.wit.entity.Member;
import net.wit.entity.PayBank;
import net.wit.service.MemberService;
import net.wit.service.PayBankService;
import net.wit.webservice.EpayDsfService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 付款单
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("payBankServiceImpl")
public class PayBankServiceImpl extends BaseServiceImpl<PayBank, Long> implements PayBankService {

	@Resource(name = "creditDaoImpl")
	private CreditDao creditDao;
	@Resource(name = "payBankDaoImpl")
	private PayBankDao payBankDao;
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "payBankDaoImpl")
	public void setBaseDao(PayBankDao payBankDao) {
		super.setBaseDao(payBankDao);
	}


	@Transactional(readOnly = true)
	public Page<PayBank> findPage(Member member, Pageable pageable) {
		return payBankDao.findPage(member, pageable);
	}
	

	public Message payToBank(Credit credit,String trackNo,String flag) {
	  PayBank payBank = new PayBank();
	  payBank.setAmount(credit.getAmount());
	  if (credit.getType()==Credit.Type.cash) {
	     payBank.setBankAccType("00");
	     payBank.setSummary("往 来结算");
	  } else {
		 payBank.setBankAccType("02");
	     payBank.setSummary("往来结算");
	  }
	  payBank.setOrderNo(credit.getSn());
	  payBank.setBankCode(credit.getBank());
	  payBank.setBankAccProp("0");
	  payBank.setBankAccNo(credit.getAccount());
	  payBank.setBankAccName(credit.getPayer());
	  payBank.setMobNo(credit.getMobile());
	  payBank.setTrackNo(trackNo);
	  payBank.setMember(credit.getMember());
	  
	  EpayDsfService epay = new EpayDsfService();
	  epay.payToBank(payBank);
	  
	  
	  if ("0000".equals(payBank.getRespCode()) && "0000".equals(payBank.getRetCode()) ) {
		  credit.setCreditDate(new Date());
		  credit.setStatus(Credit.Status.success);
	  } else 
      if ("2300".equals(payBank.getRespCode()) ) {
		  credit.setMemo("已经请交银行处理，请注意资金变动");  
		  credit.setStatus(Credit.Status.wait_success);
	  }  else 
	  if ("2104".equals(payBank.getRespCode()) && (flag=="1") ) {
		  credit.setMemo("本日周末,付款到银行业务暂停!");  
		  //return Message.error("本日周末,付款到银行业务暂停!");  
	  }  else 
	  if ("2399".equals(payBank.getRespCode()) && (flag=="1") ) {
		  credit.setMemo("代收:银行交易失败");  
		  //return Message.error("代收:银行交易失败");  
	  }	 else
	  if ("2109".equals(payBank.getRespCode()) && (flag=="1") ) {
		  credit.setMemo("账户余额不足不能完成扣款");  
		  //return Message.error("账户余额不足不能完成扣款");  
	  } else 
	  if ("2200".equals(payBank.getRespCode()) && (flag=="1") ) {
		  credit.setMemo("等待易票联支付受理");  
		  //return Message.error("等待易票联支付受理");  
	  } else 
	  if ("9999".equals(payBank.getRespCode()) ) {
		  if (flag=="1") {
			 //return Message.error("未知异常,请稍候重试");  
			 credit.setMemo("未知异常,请稍候重试");  
		  } else {
		     credit.setCreditDate(new Date());
		     credit.setStatus(Credit.Status.wait_failure);
		     credit.setMemo("未知异常，请人工退款");  
		  }
	  } else {
		  credit.setCreditDate(new Date());
		  credit.setStatus(Credit.Status.failure);
		  try {
		    memberService.Refunds(credit.getMember(),null, credit.getEffectiveAmount(),"付款失败退回",null);
		  } catch (Exception e) {
			credit.setStatus(Credit.Status.wait_failure);
			credit.setMemo("退款失败，请人工退款");  
		  }
	  }
		  
	  payBankDao.persist(payBank);
	  creditDao.merge(credit);
	  if ( ("0000".equals(payBank.getRespCode()) && "0000".equals(payBank.getRetCode()) ) || ("2300".equals(payBank.getRespCode())) ) {
	     return Message.success(payBank.getRespMsg());
	  } else {
	 	  if ("0000".equals(payBank.getRespCode()) )  {
		     return Message.error(payBank.getRetMsg());  
		  } else {
			 return Message.error(payBank.getRespMsg());    
		  }
	  }
	}
	
	public Message checkBank(Credit credit,String trackNo) {
		  PayBank payBank = payBankDao.findbyOrderNo(credit.getSn());
		  if (payBank==null) {
			  return Message.error("没有找到支付记录");	
		  }
		  String tkNo = payBank.getTrackNo();
		  String reqTime = payBank.getReqTime();
		  EpayDsfService epay = new EpayDsfService();
		  Message msg = epay.queryToBank(payBank);
		  if (msg.getType()==Message.Type.success) {
			  payBank.setTrackNo(tkNo);
			  payBank.setReqTime(reqTime);
			  SimpleDateFormat finishTime =new SimpleDateFormat("yyyyMMddHHmmss");
			  payBank.setFinishTime(finishTime.format(new Date()));
			  if ("1".equals(payBank.getRetCode())) {
			     credit.setStatus(Credit.Status.success);
				 creditDao.merge(credit);
				 payBankDao.merge(payBank);
				 return Message.success(payBank.getRetMsg());
			  } else
			  if ("2".equals(payBank.getRetCode())) {
				  credit.setStatus(Credit.Status.failure);
				  try {
				    memberService.Refunds(credit.getMember(),null, credit.getEffectiveAmount(),"付款失败退回",null);
				  } catch (Exception e) {
					credit.setStatus(Credit.Status.wait_failure);
					credit.setMemo("退款失败，请人工退款");  
				  }
				 creditDao.merge(credit);
				 payBankDao.merge(payBank);
				 return Message.success(payBank.getRetMsg());
			  } else
			  if ("4".equals(payBank.getRetCode())) {
			  	 return Message.success(payBank.getRetMsg());
			  } else
			  if ("0".equals(payBank.getRetCode())) {
				 credit.setStatus(Credit.Status.wait_failure);
				 credit.setMemo("退款失败，请人工退款");  
				 creditDao.merge(credit);
				 payBankDao.merge(payBank);
				 return Message.success(payBank.getRetMsg());
			  } else {
				 return Message.error(payBank.getRetMsg());				  
			  }
				  
		  } else {
			  return msg;
		  }
		
	}
}

