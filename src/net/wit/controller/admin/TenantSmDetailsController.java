package net.wit.controller.admin;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.Role;
import net.wit.entity.TenantSmDetails;
import net.wit.service.AdminService;
import net.wit.service.RoleService;
import net.wit.service.TenantSmDetailsService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-10
 * Time: 上午12:18
 * To change this template use File | Settings | File Templates.
 */
@Controller("adminSmDetailsController")
@RequestMapping({"/admin/smDetails"})
public class TenantSmDetailsController extends BaseController {

    private static final String PATH = "/admin/tenant/smDetails";

    @Resource(name="roleServiceImpl")
    private RoleService roleService;
    
    @Resource(name="adminServiceImpl")
    private AdminService adminService;

    @Resource(name="tenantSmDetailsServiceImpl")
    private TenantSmDetailsService tenantSmDetailsService;

    @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String list(Pageable pageable, ModelMap model)
    {
    	 String searchValue = null;
 		try {
 			searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
 			pageable.setSearchValue(searchValue);
 		} catch (Exception localException) {
 		}
 		Admin admin = adminService.getCurrent();
        model.addAttribute("page", this.tenantSmDetailsService.findPage(admin.getTenant(), pageable));
        return PATH + "List";
}

    @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String add()
    {
        return PATH + "Add";
    }

    @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public Message delete(Long[] ids)
    {
        if (ids != null) {

            this.tenantSmDetailsService.delete(ids);
        }
        return SUCCESS_MESSAGE;
    }

    @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String save(TenantSmDetails tenantSmDetails, RedirectAttributes redirectAttributes)
    {

        this.tenantSmDetailsService.save(tenantSmDetails);
        addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return "redirect:list.jhtml";
    }

    @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String edit(Long id, ModelMap model)
    {
        model.addAttribute("tenantSmDetails", this.tenantSmDetailsService.find(id));
        return PATH + "Edit";
    }

    @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String update(TenantSmDetails tenantSmDetails, RedirectAttributes redirectAttributes)
    {

        TenantSmDetails smDetails = (TenantSmDetails)this.tenantSmDetailsService.find(tenantSmDetails.getId());
        if ((smDetails == null)) {
            return "/admin/common/error";
        }
        this.tenantSmDetailsService.update(tenantSmDetails);
        addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);

        return "redirect:list.jhtml";
    }
}
