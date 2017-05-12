package net.wit.service;

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
 * Title:接口类 - 企业
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
 * @version 1.0
 * @date 2013年7月2日15:46:16
 */

public interface TenantService extends BaseService<Tenant, Long> {

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
	 * @param tags 标签
	 * @param count 数量
	 * @return 企业
	 */
	List<Tenant> findList(Area area, String name, Tag tag, Integer count);

	/**
	 * 查找企业
	 * @param area 城市
	 * @param tags 标签
	 * @param pageable 分页数量
	 * @return 企业
	 */
	Page<Tenant> findPage(Area area, List<Tag> tags, Pageable pageable);

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
	 * 查找文章
	 * @param tenantCategory 文章分类
	 * @param tags 标签
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @return 仅包含已发布文章
	 */
	List<Tenant> findList(TenantCategory tenantCategory, List<Tag> tags, Integer count, List<Filter> filters, List<Order> orders);

	/**
	 * 查找文章
	 * @param tenantCategory 文章分类
	 * @param tags 标签 所属地区
	 * @param area 小区
	 * @param community 是否查询周边
	 * @param periferal
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @return 仅包含已发布文章
	 */
	List<Tenant> findList(TenantCategory tenantCategory, List<Tag> tags, Area area, Community community, Boolean periferal, Integer count, List<Filter> filters, List<Order> orders);

	/**
	 * 查找文章(缓存)
	 * @param tenantCategory 文章分类
	 * @param tags 标签 所属地区
	 * @param area 小区
	 * @param community 是否查询周边
	 * @param periferal
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @param cacheRegion 缓存区域
	 * @return 仅包含已发布文章
	 */
	List<Tenant> findList(TenantCategory tenantCategory, List<Tag> tags, Area area, Community community, Boolean periferal, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion);

	/**
	 * 查找文章(缓存)
	 * @param tenantCategory 文章分类
	 * @param tags 标签
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @param cacheRegion 缓存区域
	 * @return 仅包含已发布文章
	 */
	List<Tenant> findList(TenantCategory tenantCategory, List<Tag> tags, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion);

	/**
	 * 查找文章
	 * @param tenantCategory 文章分类
	 * @param beginDate 起始日期
	 * @param endDate 结束日期
	 * @param first 起始记录
	 * @param count 数量
	 * @return 仅包含已发布文章
	 */
	List<Tenant> findList(TenantCategory tenantCategory, Date beginDate, Date endDate, Integer first, Integer count);

	/**
	 * 查找文章分页
	 * @param tenantCategory 文章分类
	 * @param tags 标签
	 * @param pageable 分页信息
	 * @return 仅包含已发布文章
	 */
	Page<Tenant> findPage(TenantCategory tenantCategory, List<Tag> tags, Pageable pageable);

	/**
	 * 查找文章分页
	 * @param tenantCategory 文章分类
	 * @param tags 标签 所属地区
	 * @param area 小区
	 * @param community 是否查询周边
	 * @param periferal
	 * @param pageable 分页信息
	 * @return 仅包含已发布文章
	 */
	Page<Tenant> findPage(Set<TenantCategory> tenantCategorys, List<Tag> tags, Area area, Community community, Boolean periferal, Location location, BigDecimal distatce, Pageable pageable);

	Page<Tenant> mobileFindPage(Set<TenantCategory> tenantCategorys, List<Tag> tags, Area area, Community community, Boolean periferal, Location location, BigDecimal distatce, Pageable pageable);

	/**
	 * 保存企业，更新member
	 * @param name
	 * @return
	 */
	public void save(Tenant tenant, Member member);

	/**
	 * 查找最新加入企业
	 * @param tag 标签
	 * @param count 数量
	 * @return 企业
	 */
	public List<Tenant> findNew(List<Tag> tags, Integer count);

	/**
	 * 查找代理企业
	 * @param member 销售员
	 * @param status 状态
	 * @param pageable 分页
	 * @return 企业
	 */
	public Page<Tenant> findAgency(Member member, Status status, Pageable pageable);

	public long count(Member member, Date startTime, Date endTime, Status status);

	/**
	 * 查找顶级商家商品分类
	 * @return 顶级商家商品分类
	 */
	public List<ProductCategoryTenant> findRoots(Tenant tenant, Integer count);

	/**
	 * 计算商家距离经纬度的距离
	 * @param tenant
	 * @param location
	 * @return
	 */
	public BigDecimal calculateDistance(Tenant tenant, Location location);

	Page<Tenant> findPage(Set<TenantCategory> tenantCategorys, List<Tag> tags, Area area, Community community, Boolean periferal, Pageable pageable);

	List<Tenant> tenantSelect(String q, Boolean b, int i);

	List<Tenant> findNewest(List<Tag> tags, Integer count);

	/**
	 * 查找关注商家分页
	 * @param member 会员
	 * @param pageable 分页信息
	 * @return 收藏商品分页
	 */
	Page<Tenant> findPage(Member member, Pageable pageable);

	public List<Tenant> getTenantAll();

	/**
	 * @Title：findList @Description：
	 * @param productCategory
	 * @return List<Tenant>
	 */
	List<Tenant> findList(ProductCategory productCategory);
}
