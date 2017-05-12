/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: versionUpdateController.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月11日
 */
package net.wit.controller.admin;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.Tag;
import net.wit.entity.TicketSet;
import net.wit.entity.Product.PriceType;
import net.wit.entity.VersionUpdate;
import net.wit.service.TenantCategoryService;
import net.wit.service.VersionUpdateService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月11日
 */
@Controller("versionUpdateController")
@RequestMapping({"/admin/version_update"})
public class VersionUpdateController extends BaseController{

	@Resource(name="versionUpdateServiceImpl")
	  private VersionUpdateService versionUpdateService;
	  
	 @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String list(Pageable pageable, ModelMap model)
	  {
		 String searchValue = null;
			try {
				searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
				pageable.setSearchValue(searchValue);
			} catch (Exception localException) {
			}
	    model.addAttribute("page", this.versionUpdateService.findPage(pageable));
	    return "/admin/version_update/list";
	  }
	 //时间段查询
	 @RequestMapping(value={"/search"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String search(Date startTime,Date endTime,Pageable pageable, ModelMap model)
	  {
	    model.addAttribute("page", this.versionUpdateService.timeSearch(startTime, endTime, pageable));
	    model.addAttribute("startTime",startTime );
	    model.addAttribute("endTime",endTime );
	    return "/admin/version_update/list";
	  }
	 
	 @RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
		public String add(ModelMap model) {
		return "/admin/version_update/add";
		}
	 @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String save(VersionUpdate versionUpdate,RedirectAttributes redirectAttributes)
	  {
		 if(versionUpdate==null)
		 {
			  addFlashMessage(redirectAttributes, ERROR_MESSAGE);
		      return "redirect:list.jhtml";
		 }
		 versionUpdate.setVersionDate(new Date());
		 versionUpdateService.save(versionUpdate);
		  addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
	      return "redirect:list.jhtml";
	  }
}
