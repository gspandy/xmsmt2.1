/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import java.util.List;

import net.wit.entity.ProductCategory;
import net.wit.entity.ProductChannel;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;

/**
 * Service - 商品分类
 * @author rsico Team
 * @version 3.0
 */
public interface ProductCategoryService extends BaseService<ProductCategory, Long> {

	/**
	 * 查找顶级商品分类
	 * @return 顶级商品分类
	 */
	List<ProductCategory> findRoots();

	/**
	 * 查找顶级商品分类
	 * @param count 数量
	 * @return 顶级商品分类
	 */
	List<ProductCategory> findRoots(Integer count);

	/**
	 * 查找顶级商品分类
	 * @param count 数量
	 * @return 顶级商品分类
	 */
	List<ProductCategory> findRoots(Tag tag, Integer count);

	/**
	 * 查找顶级商品分类(缓存)
	 * @param count 数量
	 * @param cacheRegion 缓存区域
	 * @return 顶级商品分类(缓存)
	 */
	List<ProductCategory> findRoots(Integer count, String cacheRegion);

	/**
	 * 查找顶级商品分类(缓存)
	 * @param count 数量
	 * @param cacheRegion 缓存区域
	 * @return 顶级商品分类(缓存)
	 */
	List<ProductCategory> findRoots(Tag tag, Integer count, String cacheRegion);

	/**
	 * 查找上级商品分类
	 * @param productCategory 商品分类
	 * @return 上级商品分类
	 */
	List<ProductCategory> findParents(ProductCategory productCategory);

	/**
	 * 查找上级商品分类
	 * @param productCategory 商品分类
	 * @param count 数量
	 * @return 上级商品分类
	 */
	List<ProductCategory> findParents(ProductCategory productCategory, Integer count);

	/**
	 * 查找上级商品分类(缓存)
	 * @param productCategory 商品分类
	 * @param count 数量
	 * @param cacheRegion 缓存区域
	 * @return 上级商品分类(缓存)
	 */
	List<ProductCategory> findParents(ProductCategory productCategory, Integer count, String cacheRegion);

	/**
	 * 查找商品分类树
	 * @return 商品分类树
	 */
	List<ProductCategory> findTree();

	/**
	 * 查找下级商品分类
	 * @param productCategory 商品分类
	 * @return 下级商品分类
	 */
	List<ProductCategory> findChildren(ProductCategory productCategory);

	/**
	 * 查找下级商品分类
	 * @param productCategory 商品分类
	 * @param count 数量
	 * @param cacheRegion
	 * @param tags biaoqian
	 * @return 下级商品分类
	 */
	List<ProductCategory> findChildren(ProductCategory productCategory, Integer count, List<Tag> tags);

	/**
	 * 查找下级商品分类(缓存)
	 * @param productCategory 商品分类
	 * @param count 数量
	 * @param cacheRegion 缓存区域
	 * @return 下级商品分类(缓存)
	 */
	List<ProductCategory> findChildren(ProductCategory productCategory, Integer count, List<Tag> tags, String cacheRegion);

	List<ProductCategory> findParentsByChannel(ProductChannel productChannel, ProductCategory productCategory, Integer count, String cacheRegion);

	List<ProductCategory> findParentsByChannel(ProductChannel productChannel, ProductCategory productCategory, Integer count);

	List<ProductCategory> findRootsByChannel(ProductChannel productChannel, Integer count);

	List<ProductCategory> findRootsByChannel(ProductChannel productChannel, Integer count, String cacheRegion);
	
	List<ProductCategory> findRootsByChannel(ProductChannel productChannel, Tag tags, Integer count, String cacheRegion);

	List<ProductCategory> findRootsByChannel(ProductChannel productChannel, Tag tags, Integer count);
}