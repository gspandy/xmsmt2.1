package net.wit.controller.admin;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.Setting;
import net.wit.dao.TenantSmContentDao;
import net.wit.entity.Role;
import net.wit.entity.TenantSmContent;
import net.wit.service.AdminService;
import net.wit.service.RoleService;
import net.wit.service.TenantSmContentService;
import net.wit.util.CacheUtil;
import net.wit.util.ExcelUtil;
import net.wit.util.SettingUtils;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-8
 * Time: 下午9:13
 * To change this template use File | Settings | File Templates.
 */
@Controller("adminSmContentController")
@RequestMapping({"/admin/smContent"})
public class TenantSmContentController extends BaseController {

    @Resource(name="roleServiceImpl")
    private RoleService roleService;

    @Resource(name="tenantSmContentServiceImpl")
    private TenantSmContentService tenantSmContentService;
    
    @Resource(name="adminServiceImpl")
    private AdminService adminService;

    private static final String PATH = "/admin/tenant/smContent";

    @RequestMapping(value={"/listSel"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String listSel(Pageable pageable, ModelMap model)
    {
    	 String searchValue = null;
 		try {
 			searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
 			pageable.setSearchValue(searchValue);
 		} catch (Exception localException) {
 		}
        model.addAttribute("page", this.tenantSmContentService.findPage(adminService.getCurrent().getTenant(),pageable));
        return PATH + "ListSel";
    }

    @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String list(Pageable pageable, ModelMap model)
    {
    	 String searchValue = null;
 		try {
 			searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
 			pageable.setSearchValue(searchValue);
 		} catch (Exception localException) {
 		}
        model.addAttribute("page", this.tenantSmContentService.findPage(adminService.getCurrent().getTenant(),pageable));
        return PATH + "List";
    }

    @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String add(ModelMap model)
    {

        Setting setting = SettingUtils.get();
        String siteUrl = setting.getSiteUrl();
        String shareHttp = siteUrl+CacheUtil.getParamValueByName("TENANT_INVATE_URL");
        shareHttp = shareHttp.replace("${tenantId}", ExcelUtil.getTenant().getId()+"");
        model.addAttribute("reqUrl", shareHttp);

        return PATH + "Add";
    }

    @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public Message delete(Long[] ids)
    {
        if (ids != null) {

            this.tenantSmContentService.delete(ids);
        }
        return SUCCESS_MESSAGE;
    }

    @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String save(TenantSmContent tenantSmContent, RedirectAttributes redirectAttributes)
    {

        tenantSmContent.setTenant(ExcelUtil.getTenant());
        tenantSmContent.setUpdateDate(new Date());
        this.tenantSmContentService.save(tenantSmContent);
        addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return "redirect:list.jhtml";
    }

    @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String edit(Long id, ModelMap model)
    {
        model.addAttribute("tenantSmContent", this.tenantSmContentService.find(id));
        Setting setting = SettingUtils.get();
        String siteUrl = setting.getSiteUrl();
        String shareHttp = siteUrl+CacheUtil.getParamValueByName("TENANT_INVATE_URL");
        shareHttp = shareHttp.replace("${tenantId}", ExcelUtil.getTenant().getId()+"");
        model.addAttribute("reqUrl", shareHttp);

        return PATH + "Edit";
    }

    @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String update(TenantSmContent tenantSmContent, RedirectAttributes redirectAttributes)
    {

        TenantSmContent smContent = (TenantSmContent)this.tenantSmContentService.find(tenantSmContent.getId());
        if ((smContent == null)) {
            return "/admin/common/error";
        }
        smContent.setContent(tenantSmContent.getContent());
        this.tenantSmContentService.update(smContent);
        addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);

        return "redirect:list.jhtml";
    }

}
