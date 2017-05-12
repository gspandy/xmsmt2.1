package net.wit.service;

import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Authenticode;
import net.wit.entity.Authenticode.Status;
import net.wit.entity.Member;
import net.wit.entity.Tenant;

/**
 * <p>
 * Title:接口类 - 企业
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

public interface AuthenticodeService extends BaseService<Authenticode, Long> {

	/**
	 * 根据验证码查找订单
	 * @param code 验证码(忽略大小写)
	 * @return 若不存在则返回null
	 */
	Authenticode findBySn(String sn);

	/**
	 * 根据商家查找
	 * @param tenant
	 * @return
	 */
	Authenticode findByTenant(Tenant tenant);

	/**
	 * 分页
	 * @param tenant
	 * @param pageable
	 * @return
	 */
	Page<Authenticode> findPage(Tenant tenant, Pageable pageable);

	public Page<Authenticode> findPage(Member member, List<Status> status, Pageable pageable);

	/**
	 * 根据商家查找
	 * @param member
	 * @return
	 */
	List<Authenticode> findByMember(Member member, Tenant tenant, List<Status> status);
	List<Authenticode> findByMember(Member member);

	public void entrust(Tenant tenant, Long[] authids);

}
