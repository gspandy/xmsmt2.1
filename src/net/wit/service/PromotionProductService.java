/**
 *====================================================
 * 文件名称: PromotionGrouponService.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年4月25日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.service;

import java.util.List;

import net.wit.Filter;
import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Area;
import net.wit.entity.Promotion;
import net.wit.entity.Promotion.Type;
import net.wit.entity.PromotionProduct;
import net.wit.entity.Tenant;

/**
 * @ClassName: PromotionGrouponService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年4月25日 下午2:58:29
 */
public interface PromotionProductService extends BaseService<Promotion, Long> {

	public Page<PromotionProduct> findProductPage(Pageable pageable, Tenant tenant, net.wit.entity.Promotion.Status status);

	public List<PromotionProduct> findList(Promotion promotion, Type type, Area area, Integer count, List<Filter> filters, List<Order> orders);

	public List<PromotionProduct> findList(Promotion promotion, Type type,  Area area,Integer count, List<Filter> filters, List<Order> orders, String cacheRegion);

}
