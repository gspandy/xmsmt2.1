/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: VersionUpdateServiceImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月11日
 */
package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.dao.VipLevelDao;
import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.entity.TenantShopkeeper;
import net.wit.entity.VipLevel;
import net.wit.mobile.controller.RentController;
import net.wit.service.TenantShopkeeperService;
import net.wit.service.VipLevelService;
import net.wit.util.BizException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月11日
 */
@Service("vipLevelServiceImpl")
public class VipLevelServiceImpl extends BaseServiceImpl<VipLevel, Long> implements VipLevelService{
	
	private Logger log = LoggerFactory.getLogger(VipLevelServiceImpl.class);
	
	@Resource(name = "vipLevelDaoImpl")
	private VipLevelDao vipLevelDao;
	
	@Resource(name = "vipLevelDaoImpl")
	public void setBaseDao(VipLevelDao vipLevelDao) {
		super.setBaseDao(vipLevelDao);;
	}
	
	@Autowired
	private TenantShopkeeperService tenantShopKeeperService;

	@Override
	public VipLevel getDefaultVipLevel(Tenant tenant) throws BizException {
		List<VipLevel> list = vipLevelDao.queryLevel(tenant, null, true,null);
		if(list.size()==0) throw new BizException("未设置默认vip等级！");
		else return list.get(0);
	}

	@Override
	public VipLevel checkIsUpdateLevel(VipLevel currentVipLevel, Integer invitedNum) {
		Tenant tenant = currentVipLevel.getTenant();
		List<VipLevel> list = vipLevelDao.queryLevel(tenant, null, null,null);
		for (VipLevel vipLevel : list) {
			if(invitedNum==vipLevel.getInviteCondition()&&vipLevel.getLevel()>currentVipLevel.getLevel())return vipLevel;
		}
		return currentVipLevel;
	}
	
	@Override
	public VipLevel getVipLevelByMemeber(Member member){
		TenantShopkeeper ts = tenantShopKeeperService.findShopKeeperByMemberId(member.getId());
		if(ts==null) return null;
		else return ts.getVipLevel();
	}
	
	@Override
	public List<VipLevel> getVipLevelByTenant(Tenant tenant){
		List<VipLevel> list = vipLevelDao.queryLevel(tenant, null, null,null);
		return list;
	}
	
	@Override
	public VipLevel getVipLevelByLevel(Tenant tenant,Integer level){
		return vipLevelDao.queryLevel(tenant,level);
	}

	@Override
	public VipLevel getNextLevelOfCurrent(TenantShopkeeper tenantShopkeeper){
		VipLevel nextLevel = null;
		VipLevel currentLevel = tenantShopkeeper.getVipLevel();
		Integer nextLevelInt = currentLevel.getLevel()+1;
		List<VipLevel> levelList = this.vipLevelDao.queryLevel(currentLevel.getTenant(), null, null,nextLevelInt);
		if(levelList.size()>0){
			nextLevel = levelList.get(0);
		}
		return nextLevel;
	}
	
	@Override
	public VipLevel getNextLevelOfCurrent(VipLevel vipLevel){
		VipLevel nextLevel = null;
		Integer nextLevelInt = vipLevel.getLevel()+1;
		List<VipLevel> levelList = this.vipLevelDao.queryLevel(vipLevel.getTenant(), null, null,nextLevelInt);
		if(levelList.size()>0){
			nextLevel = levelList.get(0);
		}
		return nextLevel;
	}
	@Override
	@Transactional
	public void creatNew(VipLevel vipLevel) throws BizException{
		try {
			save(vipLevel);
			vipLevelDao.batchUpateVipLevel(vipLevel);
		} catch (Exception e) {
			log.error("创建vip等级异常："+e.getMessage());
			throw new BizException("创建失败");
		}
		
	}
	@Override
	@Transactional
	public void updateLevel(VipLevel vipLevel) throws BizException{
		try {
			update(vipLevel);
			vipLevelDao.batchUpateVipLevel(vipLevel);
		} catch (Exception e) {
			log.error("修改等级异常："+e.getMessage());
			throw new BizException("修改失败");
		}
		
	}
	
	@Override
	public VipLevel getLastLevel(Tenant tenant){
		return vipLevelDao.getLastLevel(tenant);
	}
	
	@Override
	public void batchUpateVipLevel(VipLevel level)throws Exception{
		vipLevelDao.batchUpateVipLevel(level);
	}
	
}
