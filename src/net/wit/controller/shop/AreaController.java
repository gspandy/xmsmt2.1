/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.controller.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.wit.Message;
import net.wit.entity.Area;
import net.wit.entity.Member;
import net.wit.service.AreaService;
import net.wit.util.WebUtils;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller - 地区
 * 
 * @author rsico Team
 * @version 3.0
 */
@Controller("shopAreaController")
@RequestMapping("/area")
public class AreaController extends BaseController {

	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Long parentId, ModelMap model) {
		model.addAttribute("parent", areaService.find(parentId));
		return "/admin/area/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(Area area, Long parentId, RedirectAttributes redirectAttributes) {
		area.setParent(areaService.find(parentId));
		if (!isValid(area)) {
			return ERROR_VIEW;
		}
		area.setFullName(null);
		area.setTreePath(null);
		area.setChildren(null);
		area.setMembers(null);
		area.setReceivers(null);
		area.setOrders(null);
		area.setDeliveryCenters(null);
		areaService.save(area);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		model.addAttribute("area", areaService.find(id));
		return "/admin/area/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(Area area, RedirectAttributes redirectAttributes) {
		if (!isValid(area)) {
			return ERROR_VIEW;
		}
		areaService.update(area, "fullName", "treePath", "parent", "children", "members", "receivers", "orders", "deliveryCenters");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Long parentId, ModelMap model) {
		Area parent = areaService.find(parentId);
		if (parent != null) {
			model.addAttribute("parent", parent);
			model.addAttribute("areas", new ArrayList<Area>(parent.getChildren()));
		} else {
			model.addAttribute("areas", areaService.findRoots());
		}
		return "/admin/area/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody
	Message delete(Long id) {
		areaService.delete(id);
		return SUCCESS_MESSAGE;
	}
	
	/**
	 * 获取省
	 */
	@RequestMapping(value = "/getProvince", method = RequestMethod.GET)
	@ResponseBody
	public Area getProvince(Long id) {
		Area area = areaService.find(id);
		Area parent = area.getParent();
		return parent;
	}
	
	/**
	 * 获取城市
	 */
	@RequestMapping(value = "/getCitys", method = RequestMethod.GET)
	@ResponseBody
	public Set<Area> getCitys(Long id) {
		Area area = areaService.find(id);
		Set<Area> citys = area.getChildren();
		return citys;
	}
	
	
	/**
	 * 获取
	 */
	@RequestMapping(value = "/getbyid", method = RequestMethod.GET)
	@ResponseBody
	public Area getArea(Long id) {
		Area area = areaService.find(id);
		return area;
	}
	/**
	 * 获取全部省
	 */
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	@ResponseBody
	public List<Area> chooseProvince() {
		List<Area> provinces = areaService.findRoots();
		return provinces;
	}
	/**
	 * 获取当前session area
	 */
	@RequestMapping(value = "/current", method = RequestMethod.GET)
	@ResponseBody
	public Area getCurrent(HttpServletRequest request,HttpServletResponse response) {
		Area area = areaService.getCurrent();
		request.getSession().setAttribute(Member.AREA_ATTRIBUTE_NAME,area);
		return area;
	}
	/**
	 * 跟新session area
	 */
	@RequestMapping(value = "/update_current", method = RequestMethod.GET)
	public String updateCurrent(Long id,HttpServletRequest request,HttpServletResponse response) {
		if(id==null){
			return ERROR_VIEW;
		}
		Area area = areaService.find(id);
		request.getSession().setAttribute(Member.AREA_ATTRIBUTE_NAME,area);
		WebUtils.addCookie(request, response, Area.AREA_NAME, area.getName());
		WebUtils.addCookie(request, response, Area.AREA_ID,area.getId().toString());
		return "redirect://index.jhtml";
	}

	
}