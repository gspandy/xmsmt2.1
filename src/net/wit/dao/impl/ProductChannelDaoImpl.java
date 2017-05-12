/**
 *====================================================
 * 文件名称: ProductChannelDaoImpl.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年6月13日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.dao.impl;

import org.springframework.stereotype.Repository;

import net.wit.dao.ProductChannelDao;
import net.wit.entity.ProductChannel;

/**
 * @ClassName: ProductChannelDaoImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年6月13日 上午9:11:55
 */
@Repository("productChannelDaoImpl")
public class ProductChannelDaoImpl extends BaseDaoImpl<ProductChannel, Long> implements ProductChannelDao {

}
