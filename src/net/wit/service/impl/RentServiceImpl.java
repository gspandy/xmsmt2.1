package net.wit.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.RentDao;
import net.wit.entity.Member;
import net.wit.entity.Owner;
import net.wit.entity.Rent;
import net.wit.entity.Tenant;
import net.wit.entity.TenantShopkeeper;
import net.wit.mobile.cache.CacheUtil;
import net.wit.mobile.service.impl.PushService;
import net.wit.mobile.util.ShortUUID;
import net.wit.mobile.util.rong.models.SystemMessage;
import net.wit.service.OwnerServerice;
import net.wit.service.RentService;
import net.wit.service.TenantShopkeeperService;
import net.wit.util.BizException;
import net.wit.util.BussConst;
import net.wit.util.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 */
@Service("rentServiceImpl")
public class RentServiceImpl extends BaseServiceImpl<Rent, Long> implements RentService {

	@Resource(name = "rentDaoImpl")
	private RentDao rentDao;

	@Resource(name = "rentDaoImpl")
	public void setBaseDao(RentDao rentDao) {
		super.setBaseDao(rentDao);
	}
	
	@Autowired
	private PushService pushService;
	
	@Autowired
	private TenantShopkeeperService tenantShopkeeperService;
	
	@Autowired
	private OwnerServerice ownerServerice;
	
	
	public Page<Rent> findPage(Long memberId, String rentDate,Rent.Status status,Pageable pageable){
		return rentDao.findPage(memberId, rentDate, status, pageable);
	}
	
	
	public void charge(Long id) {
		Rent rent = this.find(id);
		rent.setStatus(Rent.Status.charged);
		rentDao.merge(rent);
	}
	@Override
	public int calNextRentDate(Long memberId)throws BizException{
		int count = rentDao.countRentCharged(memberId, new Date());
		Date date = new Date();
		if(count == 0){
			return 0;
		}else{
			Calendar c = Calendar.getInstance();
			c.setTime(DateUtil.addMonth(date, count));
			Date nextDate = DateUtil.setBeginDayOfMonth(c);
			try {
				return DateUtil.differDay(nextDate, date);
			} catch (ParseException e) {
				return 0;
			}
		}
	}
	@Override
	@Transactional
	public void freePeriodRent(Long memberId,Long tenantId)throws BizException{
		List<Rent> list = rentDao.findRentInfo(memberId,null);
		if(list != null && list.size() > 0){//有交租记录，不再交租。
			return;
		}
		//店主试用期
		int freePeriod = Integer.parseInt(CacheUtil.getParamValueByName(BussConst.PARAM_SHOPKEEPER_FREE_PERIOD));
//		String beginRent = CacheUtil.getParamValueByName(BussConst.PARAM_BEGIN_RENT);
//		BigDecimal rent = new BigDecimal(beginRent);//租金
		Date date = new Date();
		String txNo = ShortUUID.genId();
		Member member = new Member();
		Tenant tenant = new Tenant();
		member.setId(memberId);
		tenant.setId(tenantId);
		for(int i= 0;i < freePeriod;i++){
			Rent entity = new Rent();
			entity.setMember(member);
			entity.setRent(BigDecimal.ZERO);
			entity.setRentDate(DateUtil.changeDateToStr(DateUtil.addMonth(date, i), DateUtil.LINK_DISPLAY_DATE_MONTH));
			entity.setStatus(Rent.Status.system);
			entity.setTenant(tenant);
			entity.setTxNo(txNo);
			rentDao.persist(entity);
		}
	}
	@Override
	public boolean isFreezed(Long memberId)throws BizException{
		int date = Integer.parseInt(CacheUtil.getParamValueByName(BussConst.PARAM_RENT_END_DATE));
		Calendar c=Calendar.getInstance();
		int nowDate = c.get(Calendar.DATE);
		
		List<Rent> list = rentDao.findRentList(memberId, c.getTime(), Rent.Status.notCharge);
		if(list.size() > 1){
			return true;
		}else if(list.size() == 1 && date < nowDate){
			return true;
		}else{
			return false;
		}
	}
	public Rent LastChargeRent(Long memberId){
		return rentDao.getLastChargedRent(memberId);
	}
	
	@Override
	public List<Rent> rentInfo(Long memberId)throws BizException{
		return rentDao.findRentInfo(memberId, null);
	}
	@Override
	@Transactional
	public String payRent(Long memberId,BigDecimal rent,int amount)throws BizException{
		String beginRent = CacheUtil.getParamValueByName(BussConst.PARAM_BEGIN_RENT);
		BigDecimal perRent = new BigDecimal(beginRent);//租金
		if(perRent.multiply(new BigDecimal(amount)).compareTo(rent) != 0){
			throw new BizException("交租金额不正确");
		}
		Date date = new Date();
		TenantShopkeeper tenantShopkeeper = tenantShopkeeperService.getTenantByShopKeeper(memberId);
		String txNo = ShortUUID.genId();
		List<Rent> list = rentDao.findRentList(memberId,Rent.Status.notCharge);
		Member member = new Member();
		Tenant tenant = new Tenant();
		member.setId(memberId);
		tenant.setId(tenantShopkeeper.getTenant().getId());
		if(list != null && list.size() > 0){//存在未交租记录
			for(Rent r : list){
				if(amount == 0){
					break;
				}
//				r.setStatus(Rent.Status.charged);
				r.setTxNo(txNo);
				this.update(r);
				amount--;
				date = DateUtil.changeStrToDate(r.getRentDate(), DateUtil.LINK_DISPLAY_DATE_MONTH);
			}
			if(amount > 0){
				for(int i= 0;i < amount;i++){
					Rent entity = new Rent();
					entity.setMember(member);
					entity.setRent(perRent);
					entity.setRentDate(DateUtil.changeDateToStr(DateUtil.addMonth(date, i+1), DateUtil.LINK_DISPLAY_DATE_MONTH));
					entity.setStatus(Rent.Status.notCharge);
					entity.setTenant(tenant);
					entity.setTxNo(txNo);
					this.save(entity);
//					rentDao.persist(entity);
				}
			}
		}else{
			Rent lastRent = rentDao.getLastChargedRent(memberId);
			Date startDate = new Date();
			if(lastRent != null && StringUtils.isNotBlank(lastRent.getRentDate())){
				startDate = DateUtil.changeStrToDate(lastRent.getRentDate(), DateUtil.LINK_DISPLAY_DATE_MONTH);
			}
			if(amount > 0){
				for(int i= 0;i < amount;i++){
					Rent entity = new Rent();
					entity.setMember(member);
					entity.setRent(perRent);
					entity.setRentDate(DateUtil.changeDateToStr(
							DateUtil.addMonth(startDate, i+1),
							DateUtil.LINK_DISPLAY_DATE_MONTH));
					entity.setStatus(Rent.Status.notCharge);
					entity.setTenant(tenant);
					entity.setTxNo(txNo);
					rentDao.persist(entity);
				}
			}
		}
		return txNo;
	}
	@Transactional
	public void adviceBack(String txNo)throws BizException{
		List<Rent> list = rentDao.findByTxNo(txNo);
		String num = String.valueOf(list.size());
		Tenant tenant = new Tenant();
		Member member = new Member();
		Boolean isPublish = false;
		BigDecimal totalRent = BigDecimal.ZERO;
		if( list.size() > 0){
			for(Rent entity : list){ //修改状态
				if(!entity.getStatus().equals(Rent.Status.charged)){
					entity.setStatus(Rent.Status.charged);
					rentDao.persist(entity);
					isPublish = true;
					tenant = entity.getTenant();
					member= entity.getMember();
					totalRent = totalRent.add(entity.getRent());
				}
			}
			if(totalRent.compareTo(BigDecimal.ZERO) > 0){
				Owner owner = ownerServerice.getOwner(member.getId());
				owner.setTotalRent(owner.getTotalRent().add(totalRent));
				owner.setTotalAmt(owner.getTotalAmt().subtract(totalRent));
				ownerServerice.update(owner);
			}
			
			if(isPublish) //发消息
			pushService.publishSystemMessage(tenant, member, SystemMessage.shopKeeperRentPaymentMsg(num));
		}
	}
	
	
}