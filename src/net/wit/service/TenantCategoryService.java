/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import java.util.List;

import net.wit.entity.ProductChannel;
import net.wit.entity.TenantCategory;

/**
 * Service - 企业分类
 * @author rsico Team
 * @version 3.0
 */
public interface TenantCategoryService extends BaseService<TenantCategory, Long> {

	/**
	 * 查找顶级商品分类
	 * @return 顶级商品分类
	 */
	List<TenantCategory> findRoots();

	/**
	 * 查找顶级商品分类
	 * @param count 数量
	 * @return 顶级商品分类
	 */
	List<TenantCategory> findRoots(Integer count);

	/**
	 * 查找顶级商品分类(缓存)
	 * @param count 数量
	 * @param cacheRegion 缓存区域
	 * @return 顶级商品分类(缓存)
	 */
	List<TenantCategory> findRoots(Integer count, String cacheRegion);

	/**
	 * 查找上级商品分类
	 * @param productCategory 商品分类
	 * @return 上级商品分类
	 */
	List<TenantCategory> findParents(TenantCategory tenantCategory);

	/**
	 * 查找上级商品分类
	 * @param productChannel
	 * @param productCategory 商品分类
	 * @param count 数量
	 * @return 上级商品分类
	 */
	List<TenantCategory> findParents(ProductChannel productChannel, TenantCategory tenantCategory, Integer count);

	/**
	 * 查找上级商品分类(缓存)
	 * @param productChannel
	 * @param productCategory 商品分类
	 * @param count 数量
	 * @param cacheRegion 缓存区域
	 * @return 上级商品分类(缓存)
	 */
	List<TenantCategory> findParents(ProductChannel productChannel, TenantCategory tenantCategory, Integer count, String cacheRegion);

	/**
	 * 查找商品分类树
	 * @return 商品分类树
	 */
	List<TenantCategory> findTree();

	/**
	 * 查找下级商品分类
	 * @param productCategory 商品分类
	 * @return 下级商品分类
	 */
	List<TenantCategory> findChildren(TenantCategory tenantCategory);

	/**
	 * 查找下级商品分类
	 * @param productCategory 商品分类
	 * @param count 数量
	 * @return 下级商品分类
	 */
	List<TenantCategory> findChildren(TenantCategory tenantCategory, Integer count);

	/**
	 * 查找下级商品分类(缓存)
	 * @param productCategory 商品分类
	 * @param count 数量
	 * @param cacheRegion 缓存区域
	 * @return 下级商品分类(缓存)
	 */
	List<TenantCategory> findChildren(TenantCategory tenantCategory, Integer count, String cacheRegion);

	List<TenantCategory> findParents(TenantCategory tenantCategory, Integer count);

}