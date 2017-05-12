/**
 *====================================================
 * 文件名称: TenantDomain.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年4月24日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.domain;

import net.wit.entity.Tenant;

/**
 * @ClassName: TenantDomain
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年4月24日 下午2:46:37
 */
public interface TenantDomain {

	/**
	 * 判断该购买商家购买是否走库存
	 * @param tenant
	 * @return
	 */
	public boolean repository(Tenant tenant);

}
