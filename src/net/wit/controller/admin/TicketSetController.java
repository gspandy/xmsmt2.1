package net.wit.controller.admin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.BaseEntity;
import net.wit.entity.TicketSet;
import net.wit.service.AdminService;
import net.wit.service.RoleService;
import net.wit.service.TicketSetService;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminTicketSetController")
@RequestMapping({"/admin/ticket_set"})
public class TicketSetController extends BaseController
{

  @Resource(name="roleServiceImpl")
  private RoleService roleService;
  
  @Resource(name="adminServiceImpl")
  private AdminService adminService;
  
  @Resource(name="ticketSetServiceImpl")
  private TicketSetService ticketSetService;
  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String add(ModelMap model)
  {
    model.addAttribute("roles", this.roleService.findAll());
    TicketSet showTicket=new TicketSet();
    List<TicketSet> list=ticketSetService.findAll();
    Admin admin = adminService.getCurrent();
    Boolean type1=true;
    Boolean type2=true;
    Boolean type3=true;
    for(TicketSet ticket:list){
		  if(ticket.getTenantId()==admin.getTenant().getId()){
			 
			  if(ticket.getSendType().equals("0")){
				  model.addAttribute("ticket", ticket);
				  type1=false;
			  }
			  if(ticket.getSendType().equals("1")){
				  model.addAttribute("newticket", ticket);
				  type2=false;
			  } 
			  if(ticket.getSendType().equals("2")){
				  model.addAttribute("ticketApply", ticket);
				  type3=false;
			  }
		  }
	  }
	TicketSet ticket=new TicketSet();
	ticket.setSendNum(30);
	TicketSet ticketApply=new TicketSet();
	ticketApply.setSendNum(5);
    if(type1){
			model.addAttribute("ticket", ticket);
	  }
    if(type2){
			  model.addAttribute("newticket", ticket);
		  } 
    if(type3){
			  model.addAttribute("ticketApply", ticketApply);
		  }
    return "/admin/ticket_set/add";
  }

  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String save(TicketSet ticketSet, Integer newSendNum,Integer applySendNum,Integer newEffectiveDays,Long[] roleIds, RedirectAttributes redirectAttributes)
  {
	  TicketSet newTicketSet=new TicketSet();
	  TicketSet applyTicketSet=new TicketSet();
	  Calendar c = Calendar.getInstance();
	  Admin admin = adminService.getCurrent();
	  List<TicketSet> list=ticketSetService.findAll();
	  Boolean type=false;
	  for(TicketSet ticket:list){
		  if(ticket.getTenantId()==admin.getTenant().getId()){
			  type=true;
			  if(ticket.getSendType().equals("0")){
				  ticketSet.setId(ticket.getId());
			  }else if(ticket.getSendType().equals("1")){
				  newTicketSet.setId(ticket.getId());
			  }else if(ticket.getSendType().equals("2")){
				  applyTicketSet.setId(ticket.getId());
			  }
		  }
	  }
	  ticketSet.setTenantId(admin.getTenant().getId());
	  ticketSet.setUseFlag("1");
	  ticketSet.setSendType("0");
	  
	  newTicketSet.setUseFlag("1");
	  newTicketSet.setSendType("1");
	  newTicketSet.setSendNum(newSendNum);
	  newTicketSet.setTenantId(admin.getTenant().getId());
	  applyTicketSet.setUseFlag("1");
	  applyTicketSet.setSendType("2");
	  applyTicketSet.setSendNum(applySendNum);
	  applyTicketSet.setTenantId(admin.getTenant().getId());
	  if(type){ 
		  this.ticketSetService.update(ticketSet);
		  this.ticketSetService.update(newTicketSet);
		  this.ticketSetService.update(applyTicketSet);
	  }else{
		  this.ticketSetService.save(ticketSet);
	      this.ticketSetService.save(newTicketSet);
	      this.ticketSetService.save(applyTicketSet);
	  }
	  addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
      return "redirect:add.jhtml";
  }
}