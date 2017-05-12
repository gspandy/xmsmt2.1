package net.wit.service.impl;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.wit.dao.AuthorityDao;
import net.wit.entity.Authority;
import net.wit.entity.Role;
import net.wit.enums.AuthorityGroup;
import net.wit.service.AuthorityService;

/**
 * @ClassName：AuthorityServiceImpl @Description：
 * @author：Chenlf
 * @date：2015年9月9日 上午8:06:57
 */
@Service("authorityServiceImpl")
public class AuthorityServiceImpl extends BaseServiceImpl<Authority, Long>implements AuthorityService {

	@Resource(name = "authorityDaoImpl")
	private AuthorityDao authorityDao;

	@Resource(name = "authorityDaoImpl")
	public void setBaseDao(AuthorityDao authorityDao) {
		super.setBaseDao(authorityDao);
	}

	public List<Authority> findRoots() {
		return authorityDao.findRoots();
	}

	public List<Authority> findTree() {
		return authorityDao.findChildren(null, null);
	}

	public List<Authority> findChildren(Authority authority) {
		return authorityDao.findChildren(authority, null);
	}

	public List<Authority> findList(AuthorityGroup group, Set<Role> roles) {
		return authorityDao.findList(group, roles);
	}

}
