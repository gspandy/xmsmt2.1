package net.wit.controller.admin;

import java.util.List;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Role;
import net.wit.entity.Tenant;
import net.wit.entity.TenantSm;
import net.wit.service.RoleService;
import net.wit.service.TenantService;
import net.wit.service.TenantSmService;
import net.wit.util.ExcelUtil;

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
 * Time: 下午7:16
 * To change this template use File | Settings | File Templates.
 */
@Controller("adminSmController")
@RequestMapping({"/admin/sm"})
public class TenantSmController extends BaseController {

    private static final String PATH = "/admin/tenant/smm";

    @Resource(name="roleServiceImpl")
    private RoleService roleService;

    @Resource(name="tenantSmServiceImpl")
    private TenantSmService tenantSmService;
    
    @Resource(name="tenantServiceImpl")
    private TenantService tenantService;

    @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String list(Pageable pageable, ModelMap model)
    {
    	 String searchValue = null;
 		try {
 			searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
 			pageable.setSearchValue(searchValue);
 		} catch (Exception localException) {
 		}
        model.addAttribute("page", this.tenantSmService.findPage(pageable));
        return PATH + "List";
    }

    @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String add(ModelMap model)
    {
    	List<Tenant> list=this.tenantService.findAll();
 		model.addAttribute("tenantList", list);
        return PATH + "Add";
    }

    @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public Message delete(Long[] ids)
    {
        if (ids != null) {

            this.tenantSmService.delete(ids);
        }
        return SUCCESS_MESSAGE;
    }

    @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String save(TenantSm tenantSm,Long tenantId, RedirectAttributes redirectAttributes)
    {

    	Tenant tenant=new Tenant();
    	tenant.setId(tenantId);
        //tenantSm.setTenant(ExcelUtil.getTenant());
    	tenantSm.setTenant(tenant);
        this.tenantSmService.save(tenantSm);
        addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return "redirect:list.jhtml";
    }

    @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String edit(Long id, ModelMap model)
    {
        model.addAttribute("tenantSm", this.tenantSmService.find(id));
        return PATH + "Edit";
    }

    @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String update(TenantSm tenantSm, RedirectAttributes redirectAttributes)
    {

        TenantSm Sm = (TenantSm)this.tenantSmService.find(tenantSm.getId());
        if ((Sm == null)) {
            return "/admin/common/error";
        }
        tenantSm.setTenant(Sm.getTenant());
        this.tenantSmService.update(tenantSm);
        addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);

        return "redirect:list.jhtml";
    }
}
