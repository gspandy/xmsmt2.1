/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.Area;
import net.wit.entity.Deposit;
import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.util.TupleTwo;

/**
 * Service - 会员
 * @author rsico Team
 * @version 3.0
 */
public interface MemberService extends BaseService<Member, Long> {

	/**
	 * 判断用户名是否存在
	 * @param username 用户名(忽略大小写)
	 * @return 用户名是否存在
	 */
	boolean usernameExists(String username);

	/**
	 * 判断用户名是否禁用
	 * @param username 用户名(忽略大小写)
	 * @return 用户名是否禁用
	 */
	boolean usernameDisabled(String username);

	/**
	 * 判断E-mail是否存在
	 * @param email E-mail(忽略大小写)
	 * @return E-mail是否存在
	 */
	boolean emailExists(String email);

	/**
	 * 判断mobile是否存在
	 * @param mobile mobile
	 * @return mobile是否存在
	 */
	boolean mobileExists(String mobile);

	/**
	 * 判断E-mail是否唯一
	 * @param previousEmail 修改前E-mail(忽略大小写)
	 * @param currentEmail 当前E-mail(忽略大小写)
	 * @return E-mail是否唯一
	 */
	boolean emailUnique(String previousEmail, String currentEmail);

	/**
	 * 保存会员
	 * @param member 会员
	 * @param operator 操作员
	 */
	void save(Member member, Admin operator);

	/**
	 * 更新会员
	 * @param member 会员
	 * @param modifyPoint 修改积分
	 * @param modifyBalance 修改余额
	 * @param depositMemo 修改余额备注
	 * @param operator 操作员
	 */
	public Deposit update(Member member, Integer modifyPoint, BigDecimal modifyBalance, String depositMemo, Admin operator) throws Exception;

	/**
	 * 充值
	 * @param member 会员
	 * @param modifyPoint 修改积分
	 * @param modifyBalance 修改余额
	 * @param depositMemo 修改余额备注
	 * @param operator 操作员
	 */
	void Recharge(Member member, Integer modifyPoint, BigDecimal modifyBalance, String depositMemo, Admin operator) throws Exception;

	/**
	 * 退款
	 * @param member 会员
	 * @param modifyPoint 修改积分
	 * @param modifyBalance 修改余额
	 * @param depositMemo 修改余额备注
	 * @param operator 操作员
	 */
	void Refunds(Member member, Integer modifyPoint, BigDecimal modifyBalance, String depositMemo, Admin operator) throws Exception;

	/**
	 * 返利
	 * @param member 会员
	 * @param modifyPoint 修改积分
	 * @param modifyBalance 修改余额
	 * @param depositMemo 修改余额备注
	 * @param operator 操作员
	 */
	void Profit(Member member, Integer modifyPoint, BigDecimal modifyBalance, String depositMemo, Admin operator) throws Exception;

	/**
	 * 分红
	 * @param member 会员
	 * @param modifyPoint 修改积分
	 * @param modifyBalance 修改余额
	 * @param depositMemo 修改余额备注
	 * @param operator 操作员
	 */
	void Rebate(Member member, Integer modifyPoint, BigDecimal modifyBalance, String depositMemo, Admin operator) throws Exception;

	/**
	 * 根据用户名查找会员
	 * @param username 用户名(忽略大小写)
	 * @return 会员，若不存在则返回null
	 */
	Member findByUsername(String username);

	/**
	 * 根据E-mail查找会员
	 * @param email E-mail(忽略大小写)
	 * @return 会员，若不存在则返回null
	 */
	List<Member> findListByEmail(String email);

	/**
	 * 查找会员消费信息
	 * @param beginDate 起始日期
	 * @param endDate 结束日期
	 * @param count 数量
	 * @return 会员消费信息
	 */
	List<Object[]> findPurchaseList(Date beginDate, Date endDate, Integer count);

	/**
	 * 判断会员是否登录
	 * @return 会员是否登录
	 */
	boolean isAuthenticated();

	/**
	 * 获取当前登录会员
	 * @return 当前登录会员，若不存在则返回null
	 */
	Member getCurrent();

	/**
	 * 获取当前登录区域
	 * @return 当前登录区域，若不存在则返回null
	 */

	Area getCurrentArea();

	/**
	 * 获取当前登录用户名
	 * @return 当前登录用户名，若不存在则返回null
	 */
	String getCurrentUsername();

	String getToken(Member member);

	/**
	 * 当前会员的发展会员-分页
	 * @param member
	 * @param pageable
	 * @return
	 */
	Page<Member> findPage(Member member, Pageable pageable);
	
	Page<Member> findPage(List<Long> memberId, String mobile,Pageable pageable);

	/**
	 * 当前会员的发展会员-列表
	 * @param member
	 * @return
	 */
	List<Member> findList(Member member);
	
	List<Member> findList(List<Long> id);

	List<Member> findList(Tenant tenant);

	Page<Member> findPage(Tenant tenant, Pageable pageable);
	
	Page<Member> findPage(List<Long> id,Pageable pageable,Boolean isRegister);
	
	Page<Member> findPage(Tenant tenant, Pageable pageable,Boolean isRegister);

	/**
	 * 根据手机号查找会员
	 * @param mobile 手机号
	 * @return 会员，若不存在则返回null
	 */
	Member findByTel(String mobile);

	Member findByEmail(String email);






	void upgrade(Member member);


	/**
	 * 判断E-mail是否存在
	 * @param email E-mail(忽略大小写)
	 * @return E-mail是否存在
	 */
	boolean emailExistsWithoutUser(String email,Member member);

	/**
	 * 判断mobile是否存在
	 * @param mobile mobile(忽略大小写)
	 * @return mobile是否存在
	 */
	boolean mobileExistsWithoutUser(String mobile,Member member);
	
	/**
	 * 根据手机号查找会员
	 * @param mobile 手机号
	 * @return 会员，若不存在则返回null
	 */
	Member findByBindTel(String mobile);

	/**
	 * 根据邮箱查找会员
	 * @param email 邮箱
	 * @return 会员，若不存在则返回null
	 */
	Member findByBindEmail(String email);
	
	/**
	 * 查找收藏商家分页
	 * @param member 会员
	 * @param pageable 分页信息
	 * @return 收藏商家分页
	 */
	Page<Member> findFavoritePage(Member member, Pageable pageable);

    public String importMembers(List<String> titles, List<Object[]> members, String comId, Map<String, Integer> count) throws Exception;

    public void updatePswd(Long memberId, String pswd);

    public void updatePayPswd(Long memberId, String payPswd);
    
    public List<Member> findMemberList(boolean isShop);
    
    public long count(Tenant tenant);
    
    /**
     * 根据id 和 姓名 查询会员
     * @param id
     * @param name
     * @return
     */
    public List<Member> findListByIdAndName(Long id,String name);
}