/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.LockModeType;
import javax.servlet.http.HttpServletRequest;

import net.wit.Filter;
import net.wit.Filter.Operator;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.Principal;
import net.wit.Setting;
import net.wit.dao.AreaDao;
import net.wit.dao.DeliveryCenterDao;
import net.wit.dao.DepositDao;
import net.wit.dao.MemberDao;
import net.wit.dao.MemberRankDao;
import net.wit.dao.MemberTenantDao;
import net.wit.dao.TenantDao;
import net.wit.entity.Admin;
import net.wit.entity.Area;
import net.wit.entity.Deposit;
import net.wit.entity.Member;
import net.wit.entity.MemberTenant;
import net.wit.entity.Tenant;
import net.wit.entity.Tenant.TenantType;
import net.wit.exception.BalanceNotEnoughException;
import net.wit.mobile.cache.CacheUtil;
import net.wit.service.MemberService;
import net.wit.service.MemberTenantService;
import net.wit.service.TenantCategoryService;
import net.wit.support.EntitySupport;
import net.wit.util.SettingUtils;
import net.wit.util.SpringUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Service - 会员
 * @author rsico Team
 * @version 3.0
 */
@Service("memberServiceImpl")
public class MemberServiceImpl extends BaseServiceImpl<Member, Long>implements MemberService {
	
	private Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);

	@Resource(name = "memberDaoImpl")
	private MemberDao memberDao;

	@Resource(name = "depositDaoImpl")
	private DepositDao depositDao;

	@Resource(name = "tenantDaoImpl")
	private TenantDao tenantDao;

	@Resource(name = "areaDaoImpl")
	private AreaDao areaDao;

	@Resource(name = "deliveryCenterDaoImpl")
	private DeliveryCenterDao deliveryCenterDao;

	@Resource(name = "memberRankDaoImpl")
	private MemberRankDao memberRankDao;

	@Resource(name = "tenantCategoryServiceImpl")
	private TenantCategoryService tenantCategoryService;

	@Resource(name = "memberTanentImpl")
	private MemberTenantDao memberTanentDao;

	@Autowired
	private MemberTenantService memberTenantService;

	@Resource(name = "memberDaoImpl")
	public void setBaseDao(MemberDao memberDao) {
		super.setBaseDao(memberDao);
	}

	@Transactional(readOnly = true)
	public boolean usernameExists(String username) {
		return memberDao.usernameExists(username);
	}

	@Transactional(readOnly = true)
	public boolean usernameDisabled(String username) {
		Assert.hasText(username);
		Setting setting = SettingUtils.get();
		if (setting.getDisabledUsernames() != null) {
			for (String disabledUsername : setting.getDisabledUsernames()) {
				if (StringUtils.containsIgnoreCase(username, disabledUsername)) {
					return true;
				}
			}
		}
		return false;
	}

	@Transactional(readOnly = true)
	public boolean emailExists(String email) {
		return memberDao.emailExists(email);
	}

	@Transactional(readOnly = true)
	public boolean mobileExists(String mobile) {
		return memberDao.mobileExists(mobile);
	}

	@Transactional(readOnly = true)
	public boolean emailUnique(String previousEmail, String currentEmail) {
		if (StringUtils.equalsIgnoreCase(previousEmail, currentEmail)) {
			return true;
		} else {
			if (memberDao.emailExists(currentEmail)) {
				return false;
			} else {
				return true;
			}
		}
	}

	@Override
	public void save(Member member) {
		Assert.notNull(member);
		memberDao.persist(member);
	}

	public void save(Member member, Admin operator) {
		Assert.notNull(member);
		memberDao.persist(member);
		if (member.getBalance().compareTo(new BigDecimal(0)) > 0) {
			Deposit deposit = new Deposit();
			deposit.setType(Deposit.Type.Recharge);
			deposit.setCredit(member.getBalance());
			deposit.setDebit(new BigDecimal(0));
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername() : null);
			deposit.setMember(member);
			depositDao.persist(deposit);
		}
	}

	public Deposit update(Member member, Integer modifyPoint, BigDecimal modifyBalance, String depositMemo, Admin operator) throws Exception {
		Assert.notNull(member);
		memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);
		if (modifyPoint != null && modifyPoint != 0 && member.getPoint() + modifyPoint >= 0) {
			member.setPoint(member.getPoint() + modifyPoint);
		}
		Deposit deposit = null;
		if (modifyBalance != null && modifyBalance.compareTo(new BigDecimal(0)) != 0) {
			if (member.getBalance().add(modifyBalance).compareTo(new BigDecimal(0)) < 0) {
				throw new BalanceNotEnoughException("balance.not.enough");
			}
			member.setBalance(member.getBalance().add(modifyBalance));
			deposit = new Deposit();
			if (modifyBalance.compareTo(new BigDecimal(0)) > 0) {
				deposit.setType(Deposit.Type.Credit);
				deposit.setCredit(modifyBalance);
				deposit.setDebit(new BigDecimal(0));
			} else {
				deposit.setType(Deposit.Type.Payment);
				deposit.setCredit(new BigDecimal(0));
				deposit.setDebit(modifyBalance.abs());
			}
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername() : null);
			deposit.setMemo(depositMemo);
			deposit.setMember(member);
			depositDao.persist(deposit);
		}
		memberDao.merge(member);
		return deposit;
	}

	public void Recharge(Member member, Integer modifyPoint, BigDecimal modifyBalance, String depositMemo, Admin operator) throws Exception {
		Assert.notNull(member);

		memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);

		if (modifyPoint != null && modifyPoint != 0 && member.getPoint() + modifyPoint >= 0) {
			member.setPoint(member.getPoint() + modifyPoint);
		}

		if (modifyBalance != null && modifyBalance.compareTo(new BigDecimal(0)) != 0) {
			if (member.getBalance().add(modifyBalance).compareTo(new BigDecimal(0)) < 0) {
				throw new BalanceNotEnoughException("balance.not.enough");
			}
			member.setBalance(member.getBalance().add(modifyBalance));
			Deposit deposit = new Deposit();
			deposit.setType(Deposit.Type.Recharge);
			deposit.setCredit(modifyBalance);
			deposit.setDebit(new BigDecimal(0));
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername() : null);
			deposit.setMemo(depositMemo);
			deposit.setMember(member);
			depositDao.persist(deposit);
		}
		memberDao.merge(member);
	}

	public void Refunds(Member member, Integer modifyPoint, BigDecimal modifyBalance, String depositMemo, Admin operator) throws Exception {
		Assert.notNull(member);

		memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);

		if (modifyPoint != null && modifyPoint != 0 && member.getPoint() + modifyPoint >= 0) {
			member.setPoint(member.getPoint() + modifyPoint);
		}

		if (modifyBalance != null && modifyBalance.compareTo(new BigDecimal(0)) != 0) {
			if (member.getBalance().add(modifyBalance).compareTo(new BigDecimal(0)) < 0) {
				throw new BalanceNotEnoughException("balance.not.enough");
			}
			member.setBalance(member.getBalance().add(modifyBalance));
			Deposit deposit = new Deposit();
			deposit.setType(Deposit.Type.Refunds);
			deposit.setCredit(modifyBalance);
			deposit.setDebit(new BigDecimal(0));
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername() : null);
			deposit.setMemo(depositMemo);
			deposit.setMember(member);
			depositDao.persist(deposit);
		}
		memberDao.merge(member);
	}

	public void Profit(Member member, Integer modifyPoint, BigDecimal modifyBalance, String depositMemo, Admin operator) throws Exception {
		Assert.notNull(member);

		memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);

		if (modifyPoint != null && modifyPoint != 0 && member.getPoint() + modifyPoint >= 0) {
			member.setPoint(member.getPoint() + modifyPoint);
		}

		if (modifyBalance != null && modifyBalance.compareTo(new BigDecimal(0)) != 0) {
			if (member.getBalance().add(modifyBalance).compareTo(new BigDecimal(0)) < 0) {
				throw new BalanceNotEnoughException("balance.not.enough");
			}
			member.setBalance(member.getBalance().add(modifyBalance));
			Deposit deposit = new Deposit();
			deposit.setType(Deposit.Type.Profit);
			deposit.setCredit(modifyBalance);
			deposit.setDebit(new BigDecimal(0));
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername() : null);
			deposit.setMemo(depositMemo);
			deposit.setMember(member);
			depositDao.persist(deposit);
		}
		memberDao.merge(member);
	}

	public void Rebate(Member member, Integer modifyPoint, BigDecimal modifyBalance, String depositMemo, Admin operator) throws Exception {
		Assert.notNull(member);

		memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);

		if (modifyPoint != null && modifyPoint != 0 && member.getPoint() + modifyPoint >= 0) {
			member.setPoint(member.getPoint() + modifyPoint);
		}

		if (modifyBalance != null && modifyBalance.compareTo(new BigDecimal(0)) != 0) {
			if (member.getBalance().add(modifyBalance).compareTo(new BigDecimal(0)) < 0) {
				throw new BalanceNotEnoughException("balance.not.enough");
			}
			member.setBalance(member.getBalance().add(modifyBalance));
			Deposit deposit = new Deposit();
			deposit.setType(Deposit.Type.Rebate);
			deposit.setCredit(modifyBalance);
			deposit.setDebit(new BigDecimal(0));
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername() : null);
			deposit.setMemo(depositMemo);
			deposit.setMember(member);
			depositDao.persist(deposit);
		}
		memberDao.merge(member);
	}

	@Transactional(readOnly = true)
	public Member findByUsername(String username) {
		return memberDao.findByUsername(username);
	}

	@Transactional(readOnly = true)
	public List<Member> findListByEmail(String email) {
		return memberDao.findListByEmail(email);
	}

	@Transactional(readOnly = true)
	public List<Object[]> findPurchaseList(Date beginDate, Date endDate, Integer count) {
		return memberDao.findPurchaseList(beginDate, endDate, count);
	}

	@Transactional(readOnly = true)
	public boolean isAuthenticated() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			Principal principal = (Principal) request.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
			if (principal != null) {
				return true;
			}
		}
		return false;
	}

	@Transactional(readOnly = true)
	public Member getCurrent() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			Principal principal = (Principal) request.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
			if (principal != null) {
				return memberDao.find(principal.getId());
			}
		}
		return null;
	}

	@Transactional(readOnly = true)
	public Area getCurrentArea() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			Area area = (Area) request.getSession().getAttribute(Member.AREA_ATTRIBUTE_NAME);
			if (area != null) {
				return areaDao.find(area.getId());
			}
		}
		return null;
	}

	@Transactional(readOnly = true)
	public String getCurrentUsername() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			Principal principal = (Principal) request.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
			if (principal != null) {
				return principal.getUsername();
			}
		}
		return null;
	}

	public String getToken(Member member) {
		Tenant tenant = member.getTenant();
		if (tenant == null) {
			return "error";
		}

		String memberName = member.getName();
		if (memberName == null) {
			memberName = member.getUsername();
		}
		String memberAddress = member.getAddress();
		if (memberAddress == null) {
			memberAddress = "无";
		}
		String memberMobile = member.getMobile();
		if (memberMobile == null) {
			memberMobile = "无";
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String shopAddress = tenant.getAddress();
		String shopArea = null;
		if (tenant.getArea() != null) {
			if (tenant.getArea().getCode() == null) {
				shopArea = tenant.getArea().getId().toString();
			} else {
				shopArea = tenant.getArea().getCode();
			}
			shopAddress = tenant.getArea().getFullName() + shopAddress;
		} else {
			shopArea = "#";
		}

		if (tenant.getLicenseCode() == null) {
			tenant.setLicenseCode("无");
		}

		if (shopAddress == null) {
			shopAddress = "无";
		}

		if (tenant.getLinkman() == null) {
			tenant.setLinkman("无");
		}

		if (tenant.getTelephone() == null) {
			tenant.setTelephone("无");
		}

		String token = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<token code=\"0000\" msg=\"success\"> <userInfo>" + "<userId>" + String.format("%036d", member.getId()) + "</userId>" + "<account>" + member.getUsername() + "</account>" + "<username>"
				+ memberName + "</username>" + "<tenantId>1" + String.format("%08d", tenant.getId()) + "</tenantId>" + "<tenantName>" + tenant.getName() + "</tenantName>" + "<shopId>1" + String.format("%08d", tenant.getId()) + "0001</shopId>"
				+ "<shopName>" + tenant.getShortName() + "</shopName>" + "<address>" + shopAddress + "</address>" + "<xsmCode>" + member.getUsername() + "</xsmCode>" + "<xsmAlias>" + member.getUsername() + "</xsmAlias>" + "<xsmPWD>" + member.getPassword()
				+ "</xsmPWD>" + "<licenseCode>" + tenant.getLicenseCode() + "</licenseCode>" + "<legal>" + tenant.getLinkman() + "</legal>" + "<idCard>无</idCard>" + "<mobile>" + tenant.getTelephone() + "</mobile>" + "<regionId>" + shopArea + "</regionId>"
				+ "<lDate>" + sdf.format(new java.util.Date()) + "</lDate>" + "<online>1</online>" + "</userInfo></token>";
		return token;
	}

	public Page<Member> findPage(Member member, Pageable pageable) {
		return memberDao.findPage(member, pageable);
	}

	public Page<Member> findPage(List<Long> memberId, String mobile, Pageable pageable) {
		return memberDao.findPage(memberId, mobile, pageable);
	}

	public List<Member> findList(Member member) {
		return memberDao.findList(member);
	}

	public List<Member> findList(Tenant tenant) {
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new Filter("tenant", Operator.eq, tenant));
		return memberDao.findList(0, null, filters, null);
	}

	public Page<Member> findPage(Tenant tenant, Pageable pageable) {
		return memberDao.findPage(tenant, pageable);
	}

	public Member findByTel(String mobile) {

		return memberDao.findByTel(mobile);
	}

	public Member findByEmail(String email) {
		return memberDao.findByEmail(email);
	}

	// 生成用户账号--迭代
	private String generateAcctName(String username) {
		Member member = memberDao.findByUsername(username);
		if (member == null) {
			return username;
		} else {
			username = String.valueOf(SpringUtils.getIdentifyingCode());
			return generateAcctName(username);
		}
	}

	private String getJsessionid(HttpResponse httpResponse) {
		String JSESSIONID = "";
		Header[] heard = httpResponse.getHeaders("Set-Cookie");
		for (int i = 0; i < heard.length; i++) {
			if (heard[i].getValue().contains("JSESSIONID")) {
				JSESSIONID = heard[i].getValue();
				break;
			}
		}
		return JSESSIONID;
	}

	@Transactional
	public void upgrade(Member member) {
		Tenant tenant = EntitySupport.createInitTenant();
		tenant.setMember(member);
		tenant.setCode(member.getUsername());
		tenant.setName((member.getName() == null ? member.getUsername() : member.getName()) + "的店铺");
		tenant.setShortName(member.getUsername());
		tenant.setTenantType(TenantType.personal);
		tenant.setTenantCategory(tenantCategoryService.findFirst());
		tenant.setScore(0f);
		tenant.setTotalScore(0L);
		tenant.setScoreCount(0L);
		tenant.setHits(0L);
		tenant.setWeekHits(0L);
		tenant.setMonthHits(0L);
		tenant.setArea(member.getArea());
		tenantDao.persist(tenant);
		member.setTenant(tenant);
		memberDao.persist(member);
	}

	@Transactional(readOnly = true)
	public boolean emailExistsWithoutUser(String email, Member member) {
		return memberDao.emailExistsWithoutUser(email, member);
	}

	@Transactional(readOnly = true)
	public boolean mobileExistsWithoutUser(String mobile, Member member) {
		return memberDao.mobileExistsWithoutUser(mobile, member);
	}

	public Member findByBindTel(String mobile) {

		return memberDao.findByBindTel(mobile);
	}

	public Member findByBindEmail(String email) {
		return memberDao.findByBindEmail(email);
	}

	@Transactional(readOnly = true)
	public Page<Member> findFavoritePage(Member member, Pageable pageable) {
		return memberDao.findFavoritePage(member, pageable);
	}

	public String importMembers(List<String> titles, List<Object[]> members, String comId, Map<String, Integer> count) throws Exception {
		String msg = "";

		// 验证标题
		msg = validateTitles(titles);
		if (StringUtils.isNotEmpty(msg)) {
			return msg;
		}

		String impMax = CacheUtil.getParamValueByName("MEMBER_IMP_MAX");
		if (StringUtils.isEmpty(impMax)) {
			impMax = "10000";
		}

		List<Object[]> addMembers = new ArrayList<Object[]>();
		// 验证数据
		msg = validateData(members, impMax, comId, addMembers);
		if (StringUtils.isNotEmpty(msg)) {
			return msg;
		}

		// 一次记录数
		int pageSize = 1000;
		int totalCnt = addMembers.size();
		int totalPage = totalCnt % pageSize == 0 ? totalCnt / pageSize : totalCnt / pageSize + 1;

		int cnt = 0;
		List<Object[]> memberList;
		for (int i = 1; i <= totalPage; i++) {
			try {
				int start = new Integer(pageSize) * (new Integer(i) - 1);
				int end = i * new Integer(pageSize) > totalCnt ? totalCnt : i * new Integer(pageSize);
				memberList = new ArrayList<Object[]>();
				for (int j = start; j < end;) {
					memberList.add(addMembers.get(j));
					j++;
				}
//				cnt = cnt + addMembers(memberList, comId);
				for (Object[] objects : memberList) {
		        	try {
		        		memberTenantService.importSubmit(objects, comId);
		        		 cnt ++;
					} catch (Exception e) {
						log.error("保存异常====="+e.getMessage());
					}
		        }
			} catch (Exception e) {
				log.error("导入异常======"+e.getMessage());
			}
			count.put("cnt", cnt);
		}
		msg = cnt + "";

		return msg;
	}
	
	private String validateTitles(List<String> titles) {
		return "";
	}

	private String validateData(List<Object[]> members, String impMax, String comId, List<Object[]> addMembers) {
		if (members.size() > new Integer(impMax)) {
			return "导入数据不能超过" + impMax + "条";
		}
		if (CollectionUtils.isNotEmpty(members)) {

			String mobile;
			for (Object[] objects : members) {
				mobile = (String) objects[0];
				if (validateMember(mobile, comId)) {
					continue;
				}
				addMembers.add(objects);
			}
		}
		return "";
	}

	public boolean validateMember(String mobile, String comId) {
		return memberDao.validateMember(mobile, comId);
	}

	@Override
	public List<Member> findList(List<Long> id) {

		return memberDao.findList(id);
	}

	public void updatePayPswd(Long memberId, String payPswd) {
		Member member = find(memberId);
		member.setPaymentPassword(payPswd);
		save(member);
	}

	public void updatePswd(Long memberId, String pswd) {
		Member member = find(memberId);
		member.setPassword(pswd);
		save(member);
	}

	public List<Member> findMemberList(boolean isShop) {
		return memberDao.findMemberList(isShop);
	}

	@Override
	public long count(Tenant tenant) {
		// TODO Auto-generated method stub
		return memberDao.count(tenant);
	}

	@Override
	public Page<Member> findPage(Tenant tenant, Pageable pageable,
			Boolean isRegister) {
		return memberDao.findPage(tenant, pageable, isRegister);
	}

	@Override
	public Page<Member> findPage(List<Long> id, Pageable pageable,
			Boolean isRegister) {
		return memberDao.findPage(id, pageable, isRegister);
	}

	@Override
	public List<Member> findListByIdAndName(Long id, String name) {
		return memberDao.findListByIdAndName(id, name);
	}

}