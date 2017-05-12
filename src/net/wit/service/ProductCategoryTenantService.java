/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import java.util.List;

import net.wit.entity.ProductCategoryTenant;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;

/**
 * Service - 商家商品分类
 * @author rsico Team
 * @version 3.0
 */
public interface ProductCategoryTenantService extends BaseService<ProductCategoryTenant, Long> {

	/**
	 * 查找顶级商品分类
	 * @return 顶级商品分类
	 */
	List<ProductCategoryTenant> findRoots(Tenant tenant);

	/**
	 * 查找顶级商品分类
	 * @param count 数量
	 * @param tenant 商家
	 * @return 顶级商品分类
	 */
	List<ProductCategoryTenant> findRoots(Integer count, Tenant tenant);

	/**
	 * 查找顶级商品分类
	 * @param count 数量
	 * @param tenant 商铺
	 * @return 顶级商品分类
	 */
	List<ProductCategoryTenant> findRoots(Tag tag, Integer count, Tenant tenant);

	/**
	 * 查找顶级商品分类(缓存)
	 * @param count 数量
	 * @param tenant 商铺
	 * @param cacheRegion 缓存区域
	 * @return 顶级商品分类(缓存)
	 */
	List<ProductCategoryTenant> findRoots(Integer count, Tenant tenant, String cacheRegion);

	/**
	 * 查找顶级商品分类(缓存)
	 * @param count 数量
	 * @param tenant 商家
	 * @param cacheRegion 缓存区域
	 * @return 顶级商品分类(缓存)
	 */
	List<ProductCategoryTenant> findRoots(Tag tag, Integer count, Tenant tenant, String cacheRegion);

	/**
	 * 查找上级商品分类
	 * @param ProductCategoryTenant 商品分类
	 * @return 上级商品分类
	 */
	List<ProductCategoryTenant> findParents(ProductCategoryTenant productCategoryTenant, Tenant tenant);

	/**
	 * 查找上级商品分类
	 * @param ProductCategoryTenant 商品分类
	 * @param count 数量
	 * @param tenant 商铺
	 * @return 上级商品分类
	 */
	List<ProductCategoryTenant> findParents(ProductCategoryTenant productCategoryTenant, Integer count, Tenant tenant);

	/**
	 * 查找上级商品分类(缓存)
	 * @param ProductCategoryTenant 商品分类
	 * @param count 数量
	 * @param tenant 商铺
	 * @param cacheRegion 缓存区域
	 * @return 上级商品分类(缓存)
	 */
	List<ProductCategoryTenant> findParents(ProductCategoryTenant productCategoryTenant, Integer count, Tenant tenant, String cacheRegion);

	/**
	 * 查找商品分类树
	 * @return 商品分类树
	 */
	List<ProductCategoryTenant> findTree(Tenant tenant);

	/**
	 * 查找下级商品分类
	 * @param ProductCategoryTenant 商品分类
	 * @return 下级商品分类
	 */
	List<ProductCategoryTenant> findChildren(ProductCategoryTenant productCategoryTenant, Tenant tenant);

	/**
	 * 查找下级商品分类
	 * @param ProductCategoryTenant 商品分类
	 * @param count 数量
	 * @param tenant
	 * @return 下级商品分类
	 */
	List<ProductCategoryTenant> findChildren(ProductCategoryTenant productCategoryTenant, Integer count, Tenant tenant);

	/**
	 * 查找下级商品分类(缓存)
	 * @param ProductCategoryTenant 商品分类
	 * @param count 数量
	 * @param tenant 商铺
	 * @param cacheRegion 缓存区域
	 * @return 下级商品分类(缓存)
	 */
	List<ProductCategoryTenant> findChildren(ProductCategoryTenant productCategoryTenant, Integer count, Tenant tenant, String cacheRegion);

	/**
	 * @Title：findList @Description：店铺分类
	 * @param tenant 商家
	 * @param count 数量
	 * @return List<ProductCategoryTenant>
	 */
	List<ProductCategoryTenant> findList(Tenant tenant, Integer count);

}