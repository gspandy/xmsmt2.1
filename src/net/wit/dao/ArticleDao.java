/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import net.wit.Filter;
import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Area;
import net.wit.entity.Article;
import net.wit.entity.ArticleCategory;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;

/**
 * Dao - 文章
 * @author rsico Team
 * @version 3.0
 */
public interface ArticleDao extends BaseDao<Article, Long> {

	/**
	 * 查找文章
	 * @param articleCategory 文章分类
	 * @param tags 标签
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @return 文章
	 */
	List<Article> findList(ArticleCategory articleCategory, List<Tag> tags, Integer count, List<Filter> filters, List<Order> orders);

	/**
	 * 查找文章
	 * @param articleCategory 文章分类
	 * @param tags 标签
	 * @param tenant 商铺
	 * @param area 标签
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @return 文章
	 */
	List<Article> findList(ArticleCategory articleCategory, List<Tag> tags, Tenant tenant, Area area, Integer count, List<Filter> filters, List<Order> orders);

	/**
	 * 查找文章
	 * @param articleCategory 文章分类
	 * @param beginDate 起始日期
	 * @param endDate 结束日期
	 * @param first 起始记录
	 * @param count 数量
	 * @return 文章
	 */
	List<Article> findList(ArticleCategory articleCategory, Date beginDate, Date endDate, Integer first, Integer count);

	/**
	 * 查找文章分页
	 * @param articleCategory 文章分类
	 * @param tags 标签
	 * @param pageable 分页信息
	 * @return 文章分页
	 */
	Page<Article> findPage(Set<ArticleCategory> articleCategorys, List<Tag> tags, Area area, Pageable pageable);

	/**
	 * 查找文章分页
	 * @param Tenant 文章企业
	 * @param pageable 分页信息
	 * @return 文章分页
	 */
	Page<Article> findMyPage(Tenant tenant, Pageable pageable);
}