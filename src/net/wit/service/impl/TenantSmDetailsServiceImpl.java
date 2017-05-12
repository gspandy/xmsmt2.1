package net.wit.service.impl;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.TenantSmDao;
import net.wit.dao.TenantSmDetailsDao;
import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.entity.TenantSm;
import net.wit.entity.TenantSmContent;
import net.wit.entity.TenantSmDetails;
import net.wit.service.MemberService;
import net.wit.service.TenantSmContentService;
import net.wit.service.TenantSmDetailsService;
import net.wit.sms.service.SmsService;
import net.wit.util.ExcelUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-8
 * Time: 下午11:50
 * To change this template use File | Settings | File Templates.
 */
@Service("tenantSmDetailsServiceImpl")
public class TenantSmDetailsServiceImpl extends BaseServiceImpl<TenantSmDetails, Long> implements TenantSmDetailsService {

    @Resource(name = "tenantSmDaoImpl")
    private TenantSmDao tenantSmDao;
    @Resource(name = "tenantSmDetailsDaoImpl")
    TenantSmDetailsDao tenantSmDetailsDao;

    @Resource(name = "tenantSmDetailsDaoImpl")
    public void setBaseDao(TenantSmDetailsDao tenantSmDetailsDao) {
        super.setBaseDao(tenantSmDetailsDao);
    }
    
    @Autowired
    SmsService smsService;

    @Autowired
    private TenantSmContentService tenantSmContentService;

    @Autowired
    private MemberService memberService;

    public void save(TenantSmDetails tenantSmDetails) {
        super.save(tenantSmDetails);
    }

    public void reqShopkeeper(String memberIds, String smContentIds, TenantSm tenantSm) {
        String [] mIds = memberIds.split(",");
        //Todo smsService;
        
        Long contentId = new Long(smContentIds);
        TenantSmContent tenantSmContent = tenantSmContentService.find(contentId);
        StringBuilder sbTel = new StringBuilder();
        String content = "";
        String split = "";
        for (String id : mIds) {
            TenantSmDetails tenantSmDetails = new TenantSmDetails();
            Member member = new Member();
            member.setId(new Long(id));
            tenantSmDetails.setMember(member);
            tenantSmDetails.setTenantSmContent(tenantSmContent);
            tenantSmDetails.setSendDate(new Date());
            tenantSmDetails.setTenant(ExcelUtil.getTenant());

            super.save(tenantSmDetails);

            member = memberService.find(new Long(id));
            sbTel.append(split).append(member.getMobile());
            content = tenantSmContent.getContent();
            split = ",";
        }

        //发短信
        try {

            smsService.sendMarketingSms(sbTel.toString(), content);
            tenantSm.setLeftCount(tenantSm.getLeftCount() - mIds.length);
            tenantSm.setUsedCount(tenantSm.getUsedCount() + mIds.length);
            tenantSm.setSmsCount(tenantSm.getLeftCount() + tenantSm.getUsedCount());
            tenantSmDao.persist(tenantSm);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	public Page<TenantSmDetails> findPage(Tenant tenant, Pageable pageable) {
		return this.tenantSmDetailsDao.findPage(tenant,pageable);
	}
}
