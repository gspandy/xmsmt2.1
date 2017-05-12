/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.controller.api;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.wit.Filter;
import net.wit.Order;
import net.wit.entity.Area;
import net.wit.entity.Community;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.entity.TenantCategory;
import net.wit.service.AreaService;
import net.wit.service.CommunityService;
import net.wit.service.TagService;
import net.wit.service.TenantCategoryService;
import net.wit.service.TenantService;
import net.wit.util.JsonUtils;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
 
/**
 * Controller - 商家API
 * 
 * @author rsico Team
 * @version 3.0
 */
@Controller("apiTenantController")
@RequestMapping("/api/tenant")
public class TenantController extends BaseController  {
	
	@Resource(name = "tenantServiceImpl")
	private TenantService tenantService;
	@Resource(name = "tenantCategoryServiceImpl")
	private TenantCategoryService tenantCategoryService;
	@Resource(name = "tagServiceImpl")
	private TagService tagService;
	@Resource(name = "areaServiceImpl")
	private AreaService areaService;
	@Resource(name = "communityServiceImpl")
	private CommunityService communityService;
	
	/**
	 * 获取商家分类根结点
	 */
	@RequestMapping(value = "/getTenantCategoryRoots",method = RequestMethod.GET)
	public @ResponseBody List<TenantCategory> getTenantCategoryRoots(Integer count) {
		List<TenantCategory> tenantCategories;
		tenantCategories = tenantCategoryService.findRoots(count);
		return tenantCategories;
	}
	
	/**
	 * 获取商家分类子结点
	 */
	@RequestMapping(value = "/getTenantCategoryChildrens",method = RequestMethod.GET)
	public @ResponseBody List<TenantCategory> getTenantCategoryChildrens(Long id,Integer count) {
		List<TenantCategory> tenantCategories;
		TenantCategory tenantCategory=tenantCategoryService.find(id);
		tenantCategories = tenantCategoryService.findChildren(tenantCategory,count);
		return tenantCategories;
	}
	
	/**
	 * 获取商家分类父结点
	 */
	@RequestMapping(value = "/getTenantCategoryParents",method = RequestMethod.GET)
	public @ResponseBody List<TenantCategory> getTenantCategoryParents(Long id,Integer count) {
		List<TenantCategory> tenantCategories;
		TenantCategory tenantCategory=tenantCategoryService.find(id);
		tenantCategories = tenantCategoryService.findParents(tenantCategory,count);
		return tenantCategories;
	}
	
	
	/**
	 * 获取商家
	 */
	@RequestMapping(value = "/getTenants",method = RequestMethod.GET)
	public @ResponseBody List<Tenant> getTenants(Long tenantCategoryId,String areaCode,String communityCode,Long[] tagIds,Boolean periferal,Integer count) {
		TenantCategory tenantCategory = tenantCategoryService.find(tenantCategoryId);
		List<Tag> tags = tagService.findList(tagIds);
		Area area = areaService.findByCode(areaCode);
		Community community = communityService.findbyCode(communityCode);
		List<Filter> filters =new ArrayList<Filter>();
		List<Order> orders = new ArrayList<Order>();
		List<Tenant> tenants;
		if ((tenantCategoryId != null && tenantCategory == null) || (tagIds != null && tags.isEmpty()) || (areaCode != null && area == null)) {
			tenants = new ArrayList<Tenant>();
		} else {
			tenants = tenantService.findList(tenantCategory,tags,area,community,periferal, count, filters, orders);
		}
		return tenants;
	}
	
	@RequestMapping(value = "/test",method = RequestMethod.GET)
	public @ResponseBody List<Tenant> test() {
		Area area = areaService.find(Long.decode("1159"));
		Tag tag = tagService.find(Long.decode("8"));
		return tenantService.findList(area,"店铺",tag,5);
		
	}
		
}