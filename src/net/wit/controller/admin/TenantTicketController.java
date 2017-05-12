/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantTicketController.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月14日
 */
package net.wit.controller.admin;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.wit.FileInfo;
import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.Pic;
import net.wit.entity.PlatParam;
import net.wit.entity.TenantTicket;
import net.wit.service.AdminService;
import net.wit.service.FileService;
import net.wit.service.PicService;
import net.wit.service.TenantCategoryService;
import net.wit.service.TenantTicketService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月14日
 */
@Controller("tenantTicketController")
@RequestMapping({ "/admin/tenant_ticket" })
public class TenantTicketController extends BaseController{
	
	@Resource(name = "tenantTicketServiceImpl")
	private TenantTicketService tenantTicketService;
	
	@Resource(name = "adminServiceImpl")
	private AdminService adminService;
	
    @Resource(name="fileServiceImpl")
	private FileService fileService;
    
    @Resource(name="picServiceImpl")
    private PicService picService;

	//查询并输出页面信息
	@RequestMapping(value={"/tenantTicketEdits"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String tenantTicketEdits(ModelMap model)
	  {
		Admin admin = adminService.getCurrent();
		Long tenantId = admin.getTenant().getId();
		model.addAttribute("platParam", tenantTicketService.findTenantTicketByTenantId(tenantId));
		return "/admin/tenant_ticket/tenantTicketEdits";
	  }
	
	@RequestMapping(value = { "/saveTenantTicket" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String saveTenantTicket(TenantTicket tenantTicket, MultipartFile file , MultipartFile imageFile ,MultipartFile tailImageFile,MultipartFile tailExpiredImageFile,RedirectAttributes redirectAttributes) {
		Admin admin = adminService.getCurrent();
		Long tenantId = admin.getTenant().getId();
		if ((imageFile != null) && (!(imageFile.isEmpty()))) {
	        if (!(this.fileService.isValid(FileInfo.FileType.image, imageFile))) {
	          addFlashMessage(redirectAttributes, Message.error("图片类型无效", new Object[0]));
	          return "redirect:tenantTicketEdits.jhtml";
	        }     
	      }
		if ((file != null) && (!(file.isEmpty()))) {
	        if (!(this.fileService.isValid(FileInfo.FileType.image, file))) {
	          addFlashMessage(redirectAttributes, Message.error("图片类型无效", new Object[0]));
	          return "redirect:tenantTicketEdits.jhtml";
	        }     
	      }
		if ((tailImageFile != null) && (!(tailImageFile.isEmpty()))) {
	        if (!(this.fileService.isValid(FileInfo.FileType.image, tailImageFile))) {
	          addFlashMessage(redirectAttributes, Message.error("图片类型无效", new Object[0]));
	          return "redirect:tenantTicketEdits.jhtml";
	        }     
	      }
		if ((tailExpiredImageFile != null) && (!(tailExpiredImageFile.isEmpty()))) {
	        if (!(this.fileService.isValid(FileInfo.FileType.image, tailExpiredImageFile))) {
	          addFlashMessage(redirectAttributes, Message.error("图片类型无效", new Object[0]));
	          return "redirect:tenantTicketEdits.jhtml";
	        }     
	      }
		Pic effectiveImage = this.picService.uploadPic(tenantId, "effective", file);
	    Pic expiryImage = this.picService.uploadPic(tenantId, "expiry", imageFile);
	    Pic tailImage = this.picService.uploadPic(tenantId, "tail", tailImageFile);
	    Pic tailExpiredImage = this.picService.uploadPic(tenantId, "tailExpired", tailExpiredImageFile);
	    tenantTicket.setTanentId(tenantId);
	    if(effectiveImage!=null)
	    {
	    	tenantTicket.setEffectiveImage(effectiveImage.getLargeUrl());
	    }
	    if(expiryImage!=null)
	    {
	    	tenantTicket.setExpiryImage(expiryImage.getLargeUrl());
	    }
	    if(tailImage!=null)
	    {
	    	tenantTicket.setTailImage(tailImage.getLargeUrl());
	    }
	    if(tailExpiredImage!=null)
	    {
	    	tenantTicket.setTailExpiredImage(tailExpiredImage.getLargeUrl());
	    }
		tenantTicketService.update(tenantTicket);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:tenantTicketEdits.jhtml";
	}
}
