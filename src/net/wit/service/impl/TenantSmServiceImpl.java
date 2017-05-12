package net.wit.service.impl;

import net.wit.dao.TenantSmDao;
import net.wit.entity.TenantSm;
import net.wit.entity.TenantSm;
import net.wit.service.TenantSmService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-10
 * Time: 下午7:19
 * To change this template use File | Settings | File Templates.
 */
@Service("tenantSmServiceImpl")
public class TenantSmServiceImpl extends BaseServiceImpl<TenantSm, Long> implements TenantSmService {

    @Resource(name = "tenantSmDaoImpl")
    private TenantSmDao tenantSmDao;

    @Resource(name = "tenantSmDaoImpl")
    public void setBaseDao(TenantSmDao tenantSmDao) {
        super.setBaseDao(tenantSmDao);
    }

    public List<TenantSm> getSmLeftByTenantId(Long tenantId) {
        return tenantSmDao.getSmLeftByTenantId(tenantId);
    }
}
