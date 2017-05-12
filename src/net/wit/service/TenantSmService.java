package net.wit.service;

import net.wit.entity.TenantSm;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-10
 * Time: 下午7:19
 * To change this template use File | Settings | File Templates.
 */
public interface TenantSmService extends BaseService<TenantSm, Long> {
    public List<TenantSm> getSmLeftByTenantId(Long tenantId);
}
