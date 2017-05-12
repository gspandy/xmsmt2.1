/**
 *====================================================
 * 文件名称: ProductChannelServiceImpl.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年6月13日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.wit.dao.ProductChannelDao;
import net.wit.entity.ProductChannel;
import net.wit.service.ProductChannelService;

/**
 * @ClassName: ProductChannelServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年6月13日 上午9:18:54
 */
@Service("productChannelServiceImpl")
public class ProductChannelServiceImpl extends BaseServiceImpl<ProductChannel, Long> implements ProductChannelService {

	@Resource(name = "productChannelDaoImpl")
	private ProductChannelDao productChannelDao;

	@Resource(name = "productChannelDaoImpl")
	public void setBaseDao(ProductChannelDao productChannelDao) {
		super.setBaseDao(productChannelDao);
	}

}
