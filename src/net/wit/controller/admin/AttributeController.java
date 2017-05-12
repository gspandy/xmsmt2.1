/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.controller.admin;

import java.util.Iterator;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Attribute;
import net.wit.entity.BaseEntity.Save;
import net.wit.entity.Product;
import net.wit.service.AttributeService;
import net.wit.service.ProductCategoryService;

/**
 * Controller - 属性
 * @author rsico Team
 * @version 3.0
 */
@Controller("adminAttributeController")
@RequestMapping("/admin/attribute")
public class AttributeController extends BaseController {

	@Resource(name = "attributeServiceImpl")
	private AttributeService attributeService;

	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap model) {
		model.addAttribute("productCategoryTree", productCategoryService.findTree());
		model.addAttribute("attributeValuePropertyCount", Product.ATTRIBUTE_VALUE_PROPERTY_COUNT);
		return "/admin/attribute/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(Attribute attribute, Long productCategoryId, RedirectAttributes redirectAttributes) {
		for (Iterator<String> iterator = attribute.getOptions().iterator(); iterator.hasNext();) {
			String option = iterator.next();
			if (StringUtils.isEmpty(option)) {
				iterator.remove();
			}
		}
		attribute.setProductCategory(productCategoryService.find(productCategoryId));
		if (attribute.getProductCategory().getAttributes().size() >= Product.ATTRIBUTE_VALUE_PROPERTY_COUNT) {
			addFlashMessage(redirectAttributes, Message.error("admin.attribute.addCountNotAllowed", Product.ATTRIBUTE_VALUE_PROPERTY_COUNT));
		} else {
			attribute.setPropertyIndex(null);
			attributeService.save(attribute);
			addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		}
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		model.addAttribute("productCategoryTree", productCategoryService.findTree());
		model.addAttribute("attributeValuePropertyCount", Product.ATTRIBUTE_VALUE_PROPERTY_COUNT);
		model.addAttribute("attribute", attributeService.find(id));
		return "/admin/attribute/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(Attribute attribute, RedirectAttributes redirectAttributes) {
		for (Iterator<String> iterator = attribute.getOptions().iterator(); iterator.hasNext();) {
			String option = iterator.next();
			if (StringUtils.isEmpty(option)) {
				iterator.remove();
			}
		}
		attributeService.update(attribute, "propertyIndex", "productCategory");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		 String searchValue = null;
			try {
				searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
				pageable.setSearchValue(searchValue);
			} catch (Exception localException) {
			}
		model.addAttribute("page", attributeService.findPage(pageable));
		return "/admin/attribute/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long[] ids) {
		attributeService.delete(ids);
		return SUCCESS_MESSAGE;
	}

}