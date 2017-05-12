/**
 *====================================================
 * 文件名称: BankInfoServiceImpl.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年9月4日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.dao.BankInfoDao;
import net.wit.dao.OrderDao;
import net.wit.entity.BankInfo;
import net.wit.service.BankInfoService;

import org.springframework.stereotype.Service;

/**
 * @ClassName: BankInfoServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年9月4日 下午5:39:26
 */
@Service("bankInfoServiceImpl")
public class BankInfoServiceImpl extends BaseServiceImpl<BankInfo, Long> implements BankInfoService {

	@Resource
	private BankInfoDao bankInfoDao;

	@Resource(name = "bankInfoDaoImpl")
	public void setBaseDao(BankInfoDao bankInfoDao) {
		super.setBaseDao(bankInfoDao);
	}

	public List<BankInfo> findListByNo(String cardNo) {
		return bankInfoDao.findListByNo(cardNo);
	}

}
