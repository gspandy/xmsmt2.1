/**
 *====================================================
 * 文件名称: MemberBankServiceImpl.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年7月30日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.dao.MemberBankDao;
import net.wit.entity.Member;
import net.wit.entity.MemberBank;
import net.wit.service.AdminService;
import net.wit.service.MemberBankService;

import org.springframework.stereotype.Service;

/**
 * @ClassName: MemberBankServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年7月30日 上午9:06:30
 */
@Service("memberBankServiceImpl")
public class MemberBankServiceImpl extends BaseServiceImpl<MemberBank, Long> implements MemberBankService {

	@Resource
	private MemberBankDao memberBankDao;
	
	 @Resource(name="adminServiceImpl")
	  private AdminService adminService;

	@Resource(name = "memberBankDaoImpl")
	public void setBaseDao(MemberBankDao memberBankDao) {
		super.setBaseDao(memberBankDao);
	}

	public List<MemberBank> findListByMember(Member member) {
		return memberBankDao.findListByMember(member);
	}

	@Override
	public MemberBank getMemberBankByTenantId(Long tenantId, MemberBank.Type type) {
		 List<MemberBank> list=memberBankDao.getMemberBankByTenantId(tenantId);
		 MemberBank memberBank = new MemberBank();
		 if(list != null && list.size() > 0) {
			 for (int i = 0; i < list.size(); i++) {
				 if (list.get(i) != null) {
					 if (type.equals(list.get(i).getType())) {
						 memberBank = list.get(i);
						 return memberBank;
					 }
				 }
			 }
		 }
		 memberBank.setDepositUser(adminService.getCurrent().getTenant().getName());
		 return memberBank;
	}

	
	@Override
	public MemberBank findMember(Member member) {
		
		return memberBankDao.findMember(member);
	}

	@Override
	public MemberBank findBank(String requestid) {
		
		return memberBankDao.findBank(requestid);
	}

}
