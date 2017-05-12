package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.dao.ShareSetDao;
import net.wit.dao.TenantTicketDao;
import net.wit.entity.ShareSet;
import net.wit.entity.Tenant;
import net.wit.entity.TenantTicket;
import net.wit.service.TenantTicketService;

import org.springframework.stereotype.Service;

/**
 * @ClassName：TicketServiceImpl @Description：
 * @author：Chenlf
 * @date：2015年9月11日 下午6:22:39
 */
@Service("shareSetServiceImpl")
public class ShareSetServiceImpl extends BaseServiceImpl<ShareSet, Long>implements ShareSetService {

	@Resource(name = "shareSetDaoImpl")
	private ShareSetDao shareSetDao;
	
	@Resource(name = "shareSetDaoImpl")
	public void setBaseDao(ShareSetDao shareSetDao) {
		super.setBaseDao(shareSetDao);
	}
	
  @Override
  public List<ShareSet> getShareSetByTenant(Tenant tenant){
	  List<ShareSet> list=shareSetDao.queryShareSetByTenant(tenant, null);
	  Boolean ticketShareTitle=true;
	  Boolean invitationShareTitle=true;
	  Boolean ticketShare=true;
	  Boolean invitaionShare=true;
	  for(ShareSet set:list){
		  if(set.getType()==ShareSet.Type.invitationShareTitle){
			  invitationShareTitle=false;
		  }
		  if(set.getType()==ShareSet.Type.ticketShareTitle){
			  ticketShareTitle=false;
		  }
		  if(set.getType()==ShareSet.Type.invitaionShare){
			  ticketShare=false;
		  }
		  if(set.getType()==ShareSet.Type.invitaionShare){
			  invitaionShare=false;
		  }
	  }
	  if(ticketShareTitle){
		  ShareSet set=new ShareSet();
		  set.setType(ShareSet.Type.ticketShareTitle);
		  list.add(set);
	  }
	  if(invitationShareTitle){
		  ShareSet set=new ShareSet();
		  set.setType(ShareSet.Type.invitationShareTitle);
		  list.add(set);
	  }
	  if(ticketShare){
		  ShareSet set=new ShareSet();
		  set.setType(ShareSet.Type.ticketShare);
		  list.add(set);
	  }
	  if(invitaionShare){
		  ShareSet set=new ShareSet();
		  set.setType(ShareSet.Type.invitaionShare);
		  list.add(set);
	  }
	  return list;
  }
  
  @Override
  public List<ShareSet> getShareSetByTenant(Tenant tenant,ShareSet.Type type){
	  return shareSetDao.queryShareSetByTenant(tenant, type);
  }
  @Override
  public ShareSet getTicketShareTitleByTenant(Tenant tenant){
	  List<ShareSet> list = this.getShareSetByTenant(tenant,ShareSet.Type.ticketShareTitle);
	  if(list.size()==0) return null;
	  return list.get(0);	  
  }
  @Override
  public ShareSet getInvitaionShareTitleByTenant(Tenant tenant){
	  List<ShareSet> list = this.getShareSetByTenant(tenant,ShareSet.Type.invitationShareTitle);
	  if(list.size()==0) return null;
	  return list.get(0);	  
  }
  @Override
  public ShareSet getTicketShareContentByTenant(Tenant tenant){
	  List<ShareSet> list = this.getShareSetByTenant(tenant,ShareSet.Type.ticketShare);
	  if(list.size()==0) return null;
	  return list.get(0);	  
  }
  @Override
  public ShareSet getInvitaionShareContentByTenant(Tenant tenant){
	  List<ShareSet> list = this.getShareSetByTenant(tenant,ShareSet.Type.invitaionShare);
	  if(list.size()==0) return null;
	  return list.get(0);	  
  }
	
}
