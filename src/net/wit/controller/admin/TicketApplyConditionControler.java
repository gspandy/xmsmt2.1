package net.wit.controller.admin;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import net.wit.entity.Admin;
import net.wit.entity.TicketApplyCondition;
import net.wit.entity.TicketSet;
import net.wit.service.AdminService;
import net.wit.service.RoleService;
import net.wit.service.TicketApplyConditionService;
import net.wit.service.TicketSetService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller("ticketApplyConditionControler")
@RequestMapping({"/admin/ticket_apply"})
public class TicketApplyConditionControler extends BaseController{

	 @Resource(name="roleServiceImpl")
	  private RoleService roleService;
	  
	 @Resource(name="adminServiceImpl")
	 private AdminService adminService;
	  
	 @Resource(name="ticketApplyConditionServiceImpl")
	 private TicketApplyConditionService ticketApplyConditionService;
	 
	 @RequestMapping(value={"/ticketApplyList"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String add(ModelMap model)
	  {
		Admin admin = adminService.getCurrent();
		TicketApplyCondition ticketApply=ticketApplyConditionService.getTicketApplyConditionByTenant(admin.getTenant());
		model.addAttribute("ticketApply", ticketApply);
	    return "/admin/ticket_apply/ticketApplyList";
	  }
	 @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String save(TicketApplyCondition ticketApply, RedirectAttributes redirectAttributes)
	  { 
		  Admin admin = adminService.getCurrent();
		  ticketApply.setTenant(admin.getTenant());
		  ticketApplyConditionService.update(ticketApply);
		  addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
	      return "redirect:ticketApplyList.jhtml";
	  }
}
