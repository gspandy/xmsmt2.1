/**
 *====================================================
 * 文件名称: PackagUnitServiceImpl.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年5月21日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.service.impl;

import javax.annotation.Resource;

import net.wit.dao.PackagUnitDao;
import net.wit.entity.PackagUnit;
import net.wit.service.PackagUnitService;

import org.springframework.stereotype.Service;

/**
 * @ClassName: PackagUnitServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年5月21日 上午11:05:20
 */
@Service("packagUnitServiceImpl")
public class PackagUnitServiceImpl extends BaseServiceImpl<PackagUnit, Long> implements PackagUnitService {

	@Resource(name = "packagUnitDaoImpl")
	public void setBaseDao(PackagUnitDao packagUnitDao) {
		super.setBaseDao(packagUnitDao);
	}

}
