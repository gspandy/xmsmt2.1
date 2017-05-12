/**
 *====================================================
 * 文件名称: PackagUnitDaoImpl.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年5月21日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.dao.impl;

import org.springframework.stereotype.Repository;

import net.wit.dao.PackagUnitDao;
import net.wit.entity.PackagUnit;

/**
 * @ClassName: PackagUnitDaoImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年5月21日 上午11:03:37
 */
@Repository("packagUnitDaoImpl")
public class PackagUnitDaoImpl extends BaseDaoImpl<PackagUnit, Long> implements PackagUnitDao {

}
