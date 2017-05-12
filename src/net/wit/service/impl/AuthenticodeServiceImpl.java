package net.wit.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.AuthenticodeDao;
import net.wit.entity.Authenticode;
import net.wit.entity.Member;
import net.wit.entity.Order;
import net.wit.entity.Tenant;
import net.wit.entity.Authenticode.Status;
import net.wit.service.AuthenticodeService;
import net.wit.service.MemberService;
import net.wit.service.SmsSendService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("authenticodeServiceImpl")
public class AuthenticodeServiceImpl extends BaseServiceImpl<Authenticode, Long> implements AuthenticodeService {

	@Resource(name = "authenticodeDaoImpl")
	private AuthenticodeDao authenticodeDao;

	@Resource(name = "smsSendServiceImpl")
	private SmsSendService smsSendService;
	
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	
	@Resource(name = "authenticodeDaoImpl")
	public void setBaseDao(AuthenticodeDao authenticodeDao) {
		super.setBaseDao(authenticodeDao);
	}

	@Transactional(readOnly = true)
	public Authenticode findBySn(String sn) {
		return authenticodeDao.findBySn(sn);
	}

	@Transactional(readOnly = true)
	public Authenticode findByTenant(Tenant tenant) {
		return authenticodeDao.findByTenant(tenant);
	}

	@Transactional(readOnly = true)
	public Page<Authenticode> findPage(Tenant tenant, Pageable pageable) {
		return authenticodeDao.findPage(tenant, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Authenticode> findPage(Member member, List<Status> status, Pageable pageable) {
		return authenticodeDao.findPage(member, status, pageable);
	}

	@Transactional(readOnly = true)
	public List<Authenticode> findByMember(Member member, Tenant tenant, List<Status> status) {
		return authenticodeDao.findByMember(member, tenant, status);
	}
	@Transactional(readOnly = true)
	public List<Authenticode> findByMember(Member member) {
		return authenticodeDao.findByMember(member);
	}

	@Transactional
	public Authenticode update(Authenticode authenticode) {
		return super.update(authenticode);
	}

	@Transactional
	public void entrust(Tenant tenant, Long[] authids) {
		ResourceBundle bundle=PropertyResourceBundle.getBundle("config");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss"); 
		boolean flag = false;
		Order order = new Order();
		for (Long id : authids) {
			Authenticode auth = authenticodeDao.find(id);
			if(auth.getTenant()==null){
				auth.setTenant(tenant);
				authenticodeDao.persist(auth);
				flag = true;
				order = auth.getOrderItem().getOrder();
			}
		}
		if(flag){
			smsSendService.sysSend(tenant.getTelephone(),"您有一笔安装订单  生成于"+sdf.format(new Date())+" 客户姓名"+order.getConsignee()+" 客户地址："+order.getAddress()+"，联系方式：客户电话"+order.getPhone()+"。【"+bundle.getString("signature")+"】");
		}
	}


}
