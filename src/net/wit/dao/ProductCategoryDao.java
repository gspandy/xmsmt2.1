/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.List;

import net.wit.entity.ProductCategory;
import net.wit.entity.ProductChannel;
import net.wit.entity.Tag;

/**
 * Dao - 商品分类
 * @author rsico Team
 * @version 3.0
 */
public interface ProductCategoryDao extends BaseDao<ProductCategory, Long> {

	/**
	 * 查找顶级商品分类
	 * @param count 数量
	 * @return 顶级商品分类
	 */
	List<ProductCategory> findRoots(Integer count);

	/**
	 * 查找上级商品分类
	 * @param productCategory 商品分类
	 * @param count 数量
	 * @return 上级商品分类
	 */
	List<ProductCategory> findParents(ProductCategory productCategory, Integer count);

	/**
	 * 查找下级商品分类
	 * @param productCategory 商品分类
	 * @param count 数量
	 * @return 下级商品分类
	 */
	List<ProductCategory> findChildren(ProductCategory productCategory, Integer count, List<Tag> tags);

	/**
	 * 查找顶级商品分类
	 * @param count 数量
	 * @return 顶级商品分类
	 */
	List<ProductCategory> findRoots(Tag tag, Integer count);

	List<ProductCategory> findParentsByChannel(ProductChannel productChannel, ProductCategory productCategory, Integer count);

	List<ProductCategory> findRootsByChannel(ProductChannel productChannel, Integer count);
	List<ProductCategory> findRootsByChannel(ProductChannel productChannel, Tag tags, Integer count);
}