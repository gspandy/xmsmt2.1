package net.wit.dao;

import java.util.List;
import java.util.Set;

import net.wit.entity.Authority;
import net.wit.entity.Role;
import net.wit.enums.AuthorityGroup;

/**
 * @ClassName：AuthorityDao @Description：
 * @author：Chenlf
 * @date：2015年9月9日 上午8:07:52
 */
public interface AuthorityDao extends BaseDao<Authority, Long> {

	/**
	 * @Title：findRoots @Description：
	 * @return List<Authority>
	 */
	List<Authority> findRoots();

	/**
	 * @Title：findChildren @Description：
	 * @param object
	 * @param object2
	 * @param object3
	 * @return List<Authority>
	 */
	List<Authority> findChildren(Authority authority, Integer count);

	/**
	 * @Title：findList @Description：
	 * @param group
	 * @param roles
	 * @return List<Authority>
	 */
	List<Authority> findList(AuthorityGroup group, Set<Role> roles);

}
