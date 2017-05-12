package net.wit.service.impl;

import javax.annotation.Resource;

import net.wit.dao.MemberDao;
import net.wit.dao.TenantWechatDao;
import net.wit.entity.Member;
import net.wit.entity.TenantWechat;
import net.wit.service.TenantWechatService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: www.insuper.com
 * </p>
 * <p>
 * Company: www.insuper.com
 * </p>
 * @author liumx
 * @version 1.0
 * @date 2013年7月2日15:46:16
 */
@Service("tenantWechatServiceImpl")
public class TenantWechatServiceImpl extends BaseServiceImpl<TenantWechat, Long> implements TenantWechatService {

	@Resource(name = "memberDaoImpl")
	private MemberDao memberDao;
	
	@Resource(name = "tenantWechatDaoImpl")
	private TenantWechatDao tenantWechatDao;
	
	@Transactional
	public void save(TenantWechat tenantWechat, Member member) {
		tenantWechatDao.merge(tenantWechat);
		member.getTenant().setTenantWechat(tenantWechat);
		memberDao.merge(member);
	}

}
