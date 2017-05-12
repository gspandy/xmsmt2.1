package net.wit.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import net.wit.dao.AreaDao;
import net.wit.dao.BindUserDao;
import net.wit.dao.MemberDao;
import net.wit.dao.TenantDao;
import net.wit.entity.Area;
import net.wit.entity.BindUser;
import net.wit.entity.Member;
import net.wit.entity.MemberRank;
import net.wit.entity.Tenant;
import net.wit.entity.BindUser.Type;
import net.wit.service.BindUserService;
import net.wit.support.EntitySupport;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

/**
 * Service - 绑定登录
 * 
 * @author mayt
 * @version 3.0
 */
@Service("bindUserServiceImpl")
public class BindUserServiceImpl extends BaseServiceImpl<BindUser, String> implements BindUserService {

	
	@Resource(name = "tenantDaoImpl")
	private TenantDao tenantDao;
	@Resource(name = "bindUserDaoImpl")
	private BindUserDao bindUserDao;
	@Resource(name = "memberDaoImpl")
	private MemberDao memberDao;
	@Resource(name = "areaDaoImpl")
	private AreaDao areaDao;
	
	@Resource(name = "bindUserDaoImpl")
	public void setBaseDao(BindUserDao bindUserDao) {
		super.setBaseDao(bindUserDao);
	}

	public BindUser findByUsername(String username,Type type) {
		return bindUserDao.findByUsername(username,type);
	}

	public BindUser findByMember(Member member,Type type) {
		return bindUserDao.findByMember(member,type);
	}

	public BindUser createBinduserAndMember(String username, String password,
			String areaCode,long point,String ip,MemberRank memberRank,Type type) {
		Member member = new Member();
		member.setUsername(username.toLowerCase());
		member.setPassword(DigestUtils.md5Hex(password));
		member.setEmail(username.toLowerCase()+"@"+type.name()+".com");
		member.setPoint(point);
		member.setAmount(new BigDecimal(0));
		member.setBalance(new BigDecimal(0));
		member.setIsEnabled(true);
		member.setIsLocked(false);
		member.setLoginFailureCount(0);
		member.setLockedDate(null);
		member.setRegisterIp(ip);
		member.setLoginIp(ip);
		member.setName(username);
		member.setLoginDate(new Date());
		member.setFavoriteProducts(null);
		member.setMemberRank(memberRank);
		Tenant  tenant = EntitySupport.createInitTenant();
		Area area = areaDao.findByCode(areaCode);
		if (area!=null) {
		   tenant.setArea(area);
		}
		tenant.setTenantType(Tenant.TenantType.individual);
		tenant.setAddress(member.getAddress());
		tenant.setLinkman(member.getName());
		tenant.setTelephone(member.getMobile());
		tenant.setName(member.getName()+"的店铺");
	    tenant.setShortName(member.getName());
		tenantDao.persist(tenant);
		member.setTenant(tenant);
		memberDao.persist(member);
		BindUser bindUser = new BindUser();
		bindUser.setMember(member);
		bindUser.setUsername(username.toLowerCase());
		bindUser.setPassword(password);
		bindUser.setType(type);
		bindUserDao.persist(bindUser);
		return bindUser;
	}
	public BindUser createBinduserWithMember(String username,String password,Member member,Type type){
		BindUser bindUser = new BindUser();
		bindUser.setMember(member);
		bindUser.setUsername(username.toLowerCase());
		bindUser.setPassword(password);
		bindUser.setType(type);
		bindUserDao.persist(bindUser);
		return bindUser;
	}
	public void setPasswords(String username,String password,Type type){
		if(username!=null&&!"".equals(username)&&password!=null){
			BindUser bindUser = bindUserDao.findByUsername(username,type);
			if(bindUser!=null){
				bindUser.setPassword(password);
				bindUserDao.merge(bindUser);
				Member member = bindUser.getMember();
				if(member!=null){
					member.setPassword(DigestUtils.md5Hex(password));
					memberDao.merge(member);
				}
			}
		}
		
	}
}
