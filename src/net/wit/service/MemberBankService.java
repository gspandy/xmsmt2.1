/**
 *====================================================
 * 文件名称: MemberBankService.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年7月30日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.service;

import java.util.List;

import net.wit.entity.Member;
import net.wit.entity.MemberBank;

/**
 * @ClassName: MemberBankService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年7月30日 上午9:05:40
 */
public interface MemberBankService extends BaseService<MemberBank, Long> {

	List<MemberBank> findListByMember(Member member);
	
	MemberBank getMemberBankByTenantId (Long tenantId, MemberBank.Type type);
	
	MemberBank findMember(Member member);
	
	MemberBank findBank(String requestid);

}
