/**
 *====================================================
 * 文件名称: MemberBankDao.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年7月30日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.dao;

import java.util.List;

import net.wit.entity.Member;
import net.wit.entity.MemberBank;
import net.wit.entity.TenantTicket;

/**
 * @ClassName: MemberBankDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年7月30日 上午9:01:13
 */
public interface MemberBankDao extends BaseDao<MemberBank, Long> {

	List<MemberBank> findListByMember(Member member);
	
	MemberBank findMember(Member member);
	
	MemberBank findBank(String requestid);
	
    List<MemberBank> getMemberBankByTenantId(Long tenantId);

}
