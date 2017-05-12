package net.wit.service;

import java.util.List;
import java.util.Set;

import net.wit.entity.Authority;
import net.wit.entity.Role;
import net.wit.enums.AuthorityGroup;

/**
 * @ClassName：AuthorityService @Description：权限
 * @author：Chenlf
 * @date：2015年9月9日 上午8:06:22
 */
public interface AuthorityService extends BaseService<Authority, Long> {

	/**
	 * @Title：findRoots @Description：
	 * @return Object
	 */
	List<Authority> findRoots();

	List<Authority> findTree();

	/**
	 * @Title：findChildren @Description：
	 * @param authority
	 * @return Object
	 */
	List<Authority> findChildren(Authority authority);

	/**
	 * @Title：findList @Description：
	 * @param group
	 * @param set
	 * @return List<Authority>
	 */
	List<Authority> findList(AuthorityGroup group, Set<Role> set);

}
