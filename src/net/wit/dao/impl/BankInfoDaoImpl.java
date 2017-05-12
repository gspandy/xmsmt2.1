/**
 *====================================================
 * 文件名称: BankInfoDaoImpl.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年9月4日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.wit.dao.BankInfoDao;
import net.wit.entity.BankInfo;
import net.wit.entity.Member;
import net.wit.entity.MemberBank;

/**
 * @ClassName: BankInfoDaoImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年9月4日 下午5:33:14
 */
@Repository("bankInfoDaoImpl")
public class BankInfoDaoImpl extends BaseDaoImpl<BankInfo, Long> implements BankInfoDao {

	public List<BankInfo> findListByNo(String cardNo) {
		if (cardNo == null) {
			return new ArrayList<BankInfo>();
		}
		if (cardNo.length() > 6) {
			cardNo = cardNo.substring(0, 6);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<BankInfo> criteriaQuery = criteriaBuilder.createQuery(BankInfo.class);
		Root<BankInfo> root = criteriaQuery.from(BankInfo.class);
		criteriaQuery.where(criteriaBuilder.like(root.<String> get("cardNoHead"), cardNo + "%"));
		criteriaQuery.select(root);
		return super.findList(criteriaQuery, null, null, null, null);
	}

}
