/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.MobileDao;
import net.wit.entity.Member;
import net.wit.entity.Mobile;
import net.wit.exception.BalanceNotEnoughException;
import net.wit.service.MemberService;
import net.wit.service.MobileService;
import net.wit.webservice.ChongzhiService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 手机快充
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("mobileServiceImpl")
public class MobileServiceImpl extends BaseServiceImpl<Mobile, Long> implements MobileService {

	@Resource(name = "mobileDaoImpl")
	private MobileDao mobileDao;
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "mobileDaoImpl")
	public void setBaseDao(MobileDao mobileDao) {
		super.setBaseDao(mobileDao);
	}


	@Transactional(readOnly = true)
	public Page<Mobile> findPage(Member member, Pageable pageable) {
		return mobileDao.findPage(member, pageable);
	}
	

	public void fill(Mobile mobile) {
	  ResourceBundle bundle=PropertyResourceBundle.getBundle("config");
	  mobile.setTermNo(bundle.getString("dPosTermNo"));
	  mobile.setMerNo(bundle.getString("dPosMerNo"));
	  SimpleDateFormat ReqTime =new SimpleDateFormat("yyyyMMddHHmmss");
	  mobile.setReqTime(ReqTime.format(new Date()));
	  mobile.setRespCode("01");
	  mobile.setRespMsg("提交成功");
 	  mobileDao.persist(mobile);
	  if ("01".equals(mobile.getRespCode())) {
		  try {
		    memberService.update(mobile.getMember(),null, new BigDecimal(0).subtract(mobile.getFee()),"手机快充",null);
		  } catch (BalanceNotEnoughException e){
			  mobile.setRespCode("32");
			  mobile.setRespMsg("账户余额不足");  
		  } catch (Exception e) {
			  mobile.setRespCode("99");
			  mobile.setRespMsg("未知异常");
		  }
	  }
	  
	  if ("01".equals(mobile.getRespCode())) {
          ChongzhiService dPos = new ChongzhiService();
          dPos.fill(mobile);
		  //mobile.setRespCode("00");
		  //mobile.setRespMsg("提交成功");
    	  if (!"00".equals(mobile.getRespCode())) {
    		  try {
    		    memberService.Refunds(mobile.getMember(),null, mobile.getFee(),"手机快充失败，退回款项",null);
    		  } catch (Exception e) {
    			  mobile.setRespCode("99");
    			  mobile.setRespMsg("未知异常，款项待退");
    		  }
    	  } else {
    		  mobile.setRespCode("01");
			  mobile.setRespMsg("提交成功");
    	  }
    	  
	  }
	  
	  mobileDao.merge(mobile);

	}
	
	public void notify(Mobile mobile) {
		  SimpleDateFormat ReqTime =new SimpleDateFormat("yyyyMMddHHmmss");
		  mobile.setReqTime(ReqTime.format(new Date()));
          ChongzhiService dPos = new ChongzhiService();
          dPos.notify(mobile);
		  if ("1".equals(mobile.getRetCode()) || "2".equals(mobile.getRetCode()) || "3".equals(mobile.getRetCode()) || "4".equals(mobile.getRetCode()) ) {
			  SimpleDateFormat retTime =new SimpleDateFormat("yyyyMMddHHmmss");
			  mobile.setRetTime(retTime.format(new Date()));
		 	  mobileDao.merge(mobile);
		  }
	}
}

