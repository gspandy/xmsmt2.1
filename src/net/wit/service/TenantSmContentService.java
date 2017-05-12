package net.wit.service;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Tenant;
import net.wit.entity.TenantSmContent;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-8
 * Time: 下午11:51
 * To change this template use File | Settings | File Templates.
 */
public interface TenantSmContentService extends BaseService<TenantSmContent, Long> {
	
	Page<TenantSmContent> findPage(Tenant tenant, Pageable pageable);

}
