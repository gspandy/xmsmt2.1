/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Ad;
import net.wit.entity.Article;
import net.wit.entity.Tenant;

/**
 * Dao - 广告
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface AdDao extends BaseDao<Ad, Long> {
	/**
	 * 查找广告分页
	 * 
	 * @param Tenant
	 *            文章企业
	 * @param pageable
	 *            分页信息
	 * @return 广告分页
	 */
	Page<Ad> findMyPage(Tenant tenant,Pageable pageable);
}