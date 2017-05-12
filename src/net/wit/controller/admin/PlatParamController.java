/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: PlatParamController.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月10日
 */
package net.wit.controller.admin;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.wit.entity.Admin;
import net.wit.entity.PlatParam;
import net.wit.entity.TicketCache;
import net.wit.service.OrderService;
import net.wit.service.PlatParamService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月10日
 */
@Controller("platParamController")
@RequestMapping({"/admin/plat_param"})
public class PlatParamController extends BaseController{

	@Resource(name="platParamServiceImpl")
	 private PlatParamService platParamService;
	
	@RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String edit(ModelMap model)
	  {
		List<PlatParam> platParam=platParamService.findAll();
		model.addAttribute("platParam", platParam);
		model.addAttribute("platParams", platParam);
	    return "/admin/plat_param/edit";
	  }
	@RequestMapping(value={"/edits"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String edits(ModelMap model)
	  {
		List<PlatParam> platParam=platParamService.findAll();
		model.addAttribute("platParam", platParam);
	    return "/admin/plat_param/edits";
	  }
	@RequestMapping(value={"/editts"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String editts(ModelMap model)
	  {
		List<PlatParam> platParam=platParamService.findAll();
		model.addAttribute("platParam", platParam);
	    return "/admin/plat_param/editts";
	  }
	@RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String save(Long[] id,String[] paramValue,RedirectAttributes redirectAttributes)
	  {
		  List<PlatParam> list=platParamService.findAll();
		
			  for(int j=0;j<id.length;j++)
			  {
				  for(PlatParam platParam:list)
				  {
				     if(platParam.getId()==id[j])
				     {
				    	 platParam.setParamValue(paramValue[j]);
				    	 this.platParamService.update(platParam);
				     }
				  }
			  }
			  //this.platParamService.update(platParam);
	    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
	    return "redirect:edit.jhtml";
	  }
	@RequestMapping(value={"/saveDate"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String saveDate(Long[] ids,String[] paramValues,RedirectAttributes redirectAttributes)
	  {
		  List<PlatParam> list=platParamService.findAll();
		
			  for(int j=0;j<ids.length;j++)
			  {
				  for(PlatParam platParam:list)
				  {
				     if(platParam.getId()==ids[j])
				     {
				    	 platParam.setParamValue(paramValues[j]);
				    	 this.platParamService.update(platParam);
				     }
				  }
			  }
			  //this.platParamService.update(platParam);
	    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
	    return "redirect:edit.jhtml";
	  }
	@RequestMapping(value={"/saveDesc"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String saveDesc(Long[] id,String[] paramValue,RedirectAttributes redirectAttributes)
	  {
		  List<PlatParam> list=platParamService.findAll();
		
			  for(int j=0;j<id.length;j++)
			  {
				  for(PlatParam platParam:list)
				  {
				     if(platParam.getId()==id[j])
				     {
				    	 platParam.setParamValue(paramValue[j]);
				    	 this.platParamService.update(platParam);
				     }
				  }
			  }
			  //this.platParamService.update(platParam);
	    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
	    return "redirect:edits.jhtml";
	  }
	@RequestMapping(value={"/saveUrl"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String saveUrl(Long[] id,String[] paramValue,RedirectAttributes redirectAttributes)
	  {
		  List<PlatParam> list=platParamService.findAll();
		
			  for(int j=0;j<id.length;j++)
			  {
				  for(PlatParam platParam:list)
				  {
				     if(platParam.getId()==id[j])
				     {
				    	 platParam.setParamValue(paramValue[j]);
				    	 this.platParamService.update(platParam);
				     }
				  }
			  }
			  //this.platParamService.update(platParam);
	    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
	    return "redirect:editts.jhtml";
	  }
}
