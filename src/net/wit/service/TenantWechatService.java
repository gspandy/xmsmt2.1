package net.wit.service;

import net.wit.entity.Member;
import net.wit.entity.TenantWechat;

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

public interface TenantWechatService extends BaseService<TenantWechat, Long> {


	/**
	 * 保存商家微信公众号，更新member
	 * @param name
	 * @return
	 */
	public void save(TenantWechat tenantWechat, Member member);

}
