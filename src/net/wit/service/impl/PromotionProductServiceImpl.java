/**
 *====================================================
 * 文件名称: PromotionGrouponServiceImpl.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年4月25日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.wit.Filter;
import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.PromotionProductDao;
import net.wit.entity.Promotion;
import net.wit.entity.Promotion.Type;
import net.wit.entity.Area;
import net.wit.entity.PromotionProduct;
import net.wit.entity.Tenant;
import net.wit.service.PromotionProductService;
import net.wit.service.impl.support.PromotionBaseServiceImpl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * @ClassName: PromotionGrouponServiceImpl
 * @Description: 团购促销
 * @author Administrator
 * @date 2014年4月25日 下午2:58:43
 */
@Service("promotionProductService")
public class PromotionProductServiceImpl extends PromotionBaseServiceImpl implements PromotionProductService {

	@Resource(name = "promotionProductDaoImpl")
	private PromotionProductDao promotionProductDao;

	public Page<PromotionProduct> findProductPage(Pageable pageable, Tenant tenant, net.wit.entity.Promotion.Status status) {
		return promotionProductDao.findProductPage(pageable, tenant, status);
	}

	public List<PromotionProduct> findList(Promotion promotion, Type type, Area area, Integer count, List<Filter> filters, List<Order> orders) {
		return promotionProductDao.findList(promotion, type, area, count, filters, orders);
	}

	@Cacheable("promotionProduct")
	public List<PromotionProduct> findList(Promotion promotion, Type type, Area area, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion) {
		return promotionProductDao.findList(promotion, type, area, count, filters, orders);
	}

	@Transactional
	@CacheEvict(value = { "promotionProduct" }, allEntries = true)
	public void save(PromotionProduct promotionProduct) {
		Assert.notNull(promotionProduct);
		promotionProductDao.persist(promotionProduct);
	}

	@Transactional
	@CacheEvict(value = { "promotionProduct" }, allEntries = true)
	public PromotionProduct update(PromotionProduct promotionProduct) {
		Assert.notNull(promotionProduct);
		PromotionProduct pProduct = promotionProductDao.merge(promotionProduct);
		return pProduct;
	}

	@Transactional
	@CacheEvict(value = { "promotionProduct" }, allEntries = true)
	public void delete(Long id) {
		promotionProductDao.remove(promotionProductDao.find(id));
	}

	@Transactional
	@CacheEvict(value = { "promotionProduct" }, allEntries = true)
	public void delete(Long... ids) {
		for (Long id : ids) {
			promotionProductDao.remove(promotionProductDao.find(id));
		}
	}

	@Transactional
	@CacheEvict(value = { "promotionProduct" }, allEntries = true)
	public void delete(PromotionProduct promotionProduct) {
		promotionProductDao.remove(promotionProduct);
	}

}
