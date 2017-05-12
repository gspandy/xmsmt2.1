package net.wit.dao.impl;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import net.wit.dao.BindUserDao;
import net.wit.entity.BindUser;
import net.wit.entity.Member;
import net.wit.entity.BindUser.Type;

import org.springframework.stereotype.Repository;

/**
 * Dao - 绑定登录
 * 
 * @author mayt
 * @version 3.0
 */
@Repository("bindUserDaoImpl")
public class BindUserDaoImpl extends BaseDaoImpl<BindUser, String> implements BindUserDao{

	public BindUser findByUsername(String username,Type type) {
		if (username == null) {
			return null;
		}
		try {
			String jpql = null;
			if (type==Type._xsm) {
			   jpql = "select bindUsers from BindUser bindUsers where bindUsers.username = :username and bindUsers.type=0";
			}else if (type==Type._wx) {
			   jpql = "select bindUsers from BindUser bindUsers where bindUsers.username = :username and bindUsers.type=2";
			} else {
			   jpql = "select bindUsers from BindUser bindUsers where bindUsers.username = :username and bindUsers.type=1";
				
			}
			
			return entityManager.createQuery(jpql, BindUser.class).setFlushMode(FlushModeType.COMMIT).setParameter("username", username.toUpperCase()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public BindUser findByMember(Member member,Type type) {
		if (member == null) {
			return null;
		}
		try {
		    String jpql = "select bindUsers from BindUser bindUsers where bindUsers.member = :member and bindUsers.type=:type";
			return entityManager.createQuery(jpql, BindUser.class).setFlushMode(FlushModeType.COMMIT).setParameter("member",member).setParameter("type",type).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
