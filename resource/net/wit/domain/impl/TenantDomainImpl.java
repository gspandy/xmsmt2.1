/**
 *====================================================
 * 文件名称: TenantDomainImpl.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年4月24日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.domain.impl;

import org.springframework.stereotype.Service;

import net.wit.domain.TenantDomain;
import net.wit.entity.Cart;
import net.wit.entity.Tenant;

/**
 * @ClassName: TenantDomainImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年4月24日 下午2:47:12
 */
@Service("tenantDomain")
public class TenantDomainImpl implements TenantDomain {

	public static boolean supportWholePrice(Cart cart) {
		if (cart == null) {
			return false;
		}
		if (cart.getMember() == null) {
			return false;
		}
		if (cart.getMember().getTenant() == null) {
			return false;
		}
		if (Tenant.Status.success == cart.getMember().getTenant().getStatus()) {
			return true;
		}
		return false;
	}

	public boolean repository(Tenant tenant) {
		if (tenant == null) {
			return false;
		}
		if (Tenant.Status.success.equals(tenant.getStatus())) {
			return true;
		}
		return false;
	}

}
