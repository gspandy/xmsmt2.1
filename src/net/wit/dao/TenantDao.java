package net.wit.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.wit.Filter;
import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Area;
import net.wit.entity.Community;
import net.wit.entity.Location;
import net.wit.entity.Member;
import net.wit.entity.ProductCategory;
import net.wit.entity.ProductCategoryTenant;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.entity.Tenant.Status;
import net.wit.entity.TenantCategory;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: www.insuper.com
 * </p>
 * <p>
 * Company: www.insuper.com
 * </p>
 * @author liumx
 * @version 3.0 2013年7月1日16:37:52
 */

public interface TenantDao extends BaseDao<Tenant, Long> {
	/**
	 * 查找企业
	 * @param code 企业编码
	 * @return 企业
	 */
	Tenant findByCode(String code);

	/**
	 * 查找企业
	 * @param area 城市
	 * @param name 企业名称
	 * @param tag 标签
	 * @param count 数量
	 * @return 企业
	 */
	List<Tenant> findList(Area area, String name, Tag tag, Integer count);

	/**
	 * 查找企业
	 * @param area 城市
	 * @param location 经纬度
	 * @param distatce 距离
	 * @param tags 标签
	 * @param count 数量
	 * @return 企业
	 */
	List<Tenant> findList(Area area, Location location, BigDecimal distatce, Tag tag, Integer count);

	/**
	 * 查找最新加入企业
	 * @param tag 标签
	 * @param count 数量
	 * @return 企业
	 */
	List<Tenant> findNew(List<Tag> tags, Integer count);

	/**
	 * 查找企业
	 * @param tenantCategory 企业分类
	 * @param tags 标签
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @return 企业
	 */
	List<Tenant> findList(TenantCategory tenantCategory, List<Tag> tags, Integer count, List<Filter> filters, List<Order> orders);

	/**
	 * 查找企业
	 * @param tenantCategory 企业分类
	 * @param tags 所属地区
	 * @param area 小区
	 * @param community 是否查询周边
	 * @param periferal 标签
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @return 企业
	 */
	List<Tenant> findList(TenantCategory tenantCategory, List<Tag> tags, Area area, Community community, Boolean periferal, Integer count, List<Filter> filters, List<Order> orders);

	/**
	 * 查找企业
	 * @param tenantCategory 企业分类
	 * @param beginDate 起始日期
	 * @param endDate 结束日期
	 * @param first 起始记录
	 * @param count 数量
	 * @return 企业
	 */
	List<Tenant> findList(TenantCategory tenantCategory, Date beginDate, Date endDate, Integer first, Integer count);

	/**
	 * 查找企业分页
	 * @param tenantCategory 企业分类
	 * @param tags 所属地区
	 * @param area 小区
	 * @param community 是否查询周边
	 * @param periferal 标签
	 * @param pageable 分页信息
	 * @return 企业分页
	 */
	Page<Tenant> findPage(Set<TenantCategory> tenantCategorys, List<Tag> tags, Area area, Community community, Boolean periferal, Location location, BigDecimal distance, Pageable pageable);

	/**
	 * 查找企业分页
	 * @param tenantCategory 企业分类
	 * @param tags 标签
	 * @param pageable 分页信息
	 * @return 企业分页
	 */
	Page<Tenant> findPage(TenantCategory tenantCategory, List<Tag> tags, Pageable pageable);

	/**
	 * 查找企业
	 * @param area 城市
	 * @param name 企业名称
	 * @param tags 标签
	 * @param count 数量
	 * @return 企业
	 */
	Page<Tenant> findPage(Area area, List<Tag> tags, Pageable pageable);

	Page<Tenant> findAgency(Member member, Status status, Pageable pageable);

	long count(Member member, Date startTime, Date endTime, Status status);

	List<ProductCategoryTenant> findRoots(Tenant tenant, Integer count);

	Page<Tenant> mobileFindPage(Set<TenantCategory> tenantCategorys, List<Tag> tags, Area area, Community community, Boolean periferal, Location location, BigDecimal distatce, Pageable pageable);

	Page<Tenant> findPage(Set<TenantCategory> tenantCategorys, List<Tag> tags, Area area, Community community, Boolean periferal, Pageable pageable);

	List<Tenant> tenantSelect(String q, Boolean b, int i);

	List<Tenant> findNewest(List<Tag> tags, Integer count);

	Page<Tenant> findPage(Member member, Pageable pageable);

	public List<Tenant> getTenantAll();

	/**
	 * @Title：findList @Description：
	 * @param productCategory
	 * @return List<Tenant>
	 */
	List<Tenant> findList(ProductCategory productCategory);
}
