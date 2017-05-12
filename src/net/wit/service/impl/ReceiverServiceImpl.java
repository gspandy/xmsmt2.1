/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.ReceiverDao;
import net.wit.entity.Member;
import net.wit.entity.Receiver;
import net.wit.service.ReceiverService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 收货地址
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("receiverServiceImpl")
public class ReceiverServiceImpl extends BaseServiceImpl<Receiver, Long> implements ReceiverService {

	@Resource(name = "receiverDaoImpl")
	private ReceiverDao receiverDao;

	@Resource(name = "receiverDaoImpl")
	public void setBaseDao(ReceiverDao receiverDao) {
		super.setBaseDao(receiverDao);
	}

	@Transactional(readOnly = true)
	public Receiver findDefault(Member member) {
		return receiverDao.findDefault(member);
	}

	@Transactional(readOnly = true)
	public Page<Receiver> findPage(Member member, Pageable pageable) {
		return receiverDao.findPage(member, pageable);
	}

	@Transactional(readOnly = true)
	public List<Receiver> findList(Member member){
		return receiverDao.findList(member);
	}

	
	@Override
	public void addAddress(Receiver receiver) {
	   List<Receiver> receiverList=this.findList(receiver.getMember());
	   if(receiverList.size()==0)//是否为第一条地址
	   {
		   receiver.setIsDefault(true);
	   }else{
		   if(receiver.getIsDefault())//当前地址是否为默认
		   {
			   for(Receiver newReceiver:receiverList){
				   if(receiver.getId()!=newReceiver.getId()){
					   if(newReceiver.getIsDefault())//是否已经有默认地址
						  {
							  newReceiver.setIsDefault(false);
							  this.update(newReceiver);//修改默认地址
						  }
				    }	  
				  } 
		   }
		  
	   }
	   if(receiver.getId()!=null){
		   this.update(receiver);
	   }else if(receiver.getId()==null){
		   this.save(receiver);
	   }
	   
	   
	}
}