package net.wit.controller.admin;

import java.util.List;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.RentFree;
import net.wit.entity.Tenant;
import net.wit.service.AdminService;
import net.wit.service.RentFreeService;
import net.wit.service.TenantService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminRentFreeController")
@RequestMapping({ "/admin/rent_free" })
public class RentFreeController extends BaseController {

	@Resource(name = "rentFreeServiceImpl")
	private RentFreeService rentFreeService;
	
	@Resource(name = "adminServiceImpl")
	private AdminService adminService;
	
	 @Resource(name="tenantServiceImpl")
	  private TenantService tenantService;

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		 String searchValue = null;
			try {
				searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
				pageable.setSearchValue(searchValue);
			} catch (Exception localException) {
			}
        List<Tenant> tenantList=tenantService.findAll();
        Page<RentFree> freeList=this.rentFreeService.findPage(pageable);
        for(Tenant tenant:tenantList){
        	for(int i=0;i<freeList.getContent().size();i++){
        		 if(freeList.getContent().get(i).getTenantId()==tenant.getId()){
        			 freeList.getContent().get(i).setTenantName(tenant.getName());
        		}
        	}
        }
		model.addAttribute("page",freeList);
		return "/admin/rent_free/list";
	}

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model) {
		//model.addAttribute("roles", this.rentFreeService.findAll());
		model.addAttribute("tenant",this.tenantService.findAll());
		return "/admin/rent_free/add";
	}
	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(long id,ModelMap model) {
		//model.addAttribute("roles", this.rentFreeService.findAll());
		RentFree free=this.rentFreeService.find(id);
		free.setTenantName(this.tenantService.find(free.getTenantId()).getName());
		model.addAttribute("tenant",this.tenantService.findAll());
		model.addAttribute("free",free);
		return "/admin/rent_free/edit";
	}
	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(RentFree rentFree, RedirectAttributes redirectAttributes) {
//		Admin admin = adminService.getCurrent();
//		rentFree.setTenantId(admin.getTenantId());// 取内存中当前登录者的所属企业ID
//		rentFree.setStartMonth(startMonth);
//		rentFree.setEndMonth(endMonth);
		this.rentFreeService.update(rentFree);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

}