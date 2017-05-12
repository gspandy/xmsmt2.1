package net.wit.service;

import net.wit.entity.BindUser;
import net.wit.entity.Member;
import net.wit.entity.MemberRank;
import net.wit.entity.BindUser.Type;

/**
 * Service - 绑定登录
 * 
 * @author mayt
 * @version 3.0
 */
public interface BindUserService extends BaseService<BindUser,String>{

	/**
	 * 根据用户名查找绑定登录会员
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 绑定登录会员，若不存在则返回null
	 */
	BindUser findByUsername(String username,Type type);
	/**
	 * 根据用户名查找绑定登录会员
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 绑定登录会员，若不存在则返回null
	 */
	BindUser findByMember(Member member,Type type);
	/**
	 * 根据用户名和密码向 绑定用户表 和 会员表 添加记录
	 * @param username
	 * @param password
	 * @param email
	 * @param point
	 * @param ip
	 * @param memberRank
	 * @return
	 */
	BindUser createBinduserAndMember(String username, String password,
			String areaCode,long point,String ip,MemberRank memberRank,Type type);
	/**
	 * 对已存在会员进行第三方账号绑定
	 * @param username
	 * @param member
	 * @return
	 */
	BindUser createBinduserWithMember(String username,String password,Member member,Type type);
	/**
	 * 修改binduser和member表对应记录的密码
	 * @param username
	 * @param password
	 */
	void setPasswords(String username,String password,Type type);
}
