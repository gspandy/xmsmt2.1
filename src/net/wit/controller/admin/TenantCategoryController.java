package net.wit.controller.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import net.wit.Filter;
import net.wit.Filter.Operator;
import net.wit.Message;
import net.wit.entity.TenantCategory;
import net.wit.service.TenantCategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminTenantCategoryController")
@RequestMapping({"/admin/tenant_category"})
public class TenantCategoryController extends BaseController
{

  @Resource(name="tenantCategoryServiceImpl")
  private TenantCategoryService tenantCategoryService;

  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String add(ModelMap model)
  {
    model.addAttribute("tenantCategoryTree", this.tenantCategoryService.findTree());
    return "/admin/tenant_category/add";
  }

  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String save(TenantCategory tenantCategory, Long parentId, Long[] brandIds, RedirectAttributes redirectAttributes)
  {
    tenantCategory.setParent((TenantCategory)this.tenantCategoryService.find(parentId));
    if (!(isValid(tenantCategory, new Class[0]))) {
      return "/admin/common/error";
    }
    tenantCategory.setTreePath(null);
    tenantCategory.setGrade(null);
    tenantCategory.setChildren(null);
    this.tenantCategoryService.save(tenantCategory);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model)
  {
    TenantCategory tenantCategory = (TenantCategory)this.tenantCategoryService.find(id);
    model.addAttribute("tenantCategoryTree", this.tenantCategoryService.findTree());
    model.addAttribute("tenantCategory", tenantCategory);
    model.addAttribute("children", this.tenantCategoryService.findChildren(tenantCategory));
    return "/admin/tenant_category/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(TenantCategory tenantCategory, Long parentId, Long[] brandIds, RedirectAttributes redirectAttributes)
  {
    tenantCategory.setParent((TenantCategory)this.tenantCategoryService.find(parentId));
    if (!(isValid(tenantCategory, new Class[0]))) {
      return "/admin/common/error";
    }
    if (tenantCategory.getParent() != null) {
      TenantCategory parent = tenantCategory.getParent();
      if (parent.equals(tenantCategory)) {
        return "/admin/common/error";
      }
      List children = this.tenantCategoryService.findChildren(parent);
      if ((children != null) && (children.contains(parent))) {
        return "/admin/common/error";
      }
    }
    this.tenantCategoryService.update(tenantCategory, new String[] { "treePath", "grade", "children", "tenants" });
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String list(ModelMap model)
  {
    model.addAttribute("tenantCategoryTree", this.tenantCategoryService.findTree());
    return "/admin/tenant_category/list";
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long id)
  {
    TenantCategory tenantCategory = (TenantCategory)this.tenantCategoryService.find(id);
    if (tenantCategory == null) {
      return ERROR_MESSAGE;
    }
    Set children = tenantCategory.getChildren();
    if ((children != null) && (!(children.isEmpty()))) {
      return Message.error("admin.tenantCategory.deleteExistChildrenNotAllowed", new Object[0]);
    }
    Set tenants = tenantCategory.getTenants();
    if ((tenants != null) && (!(tenants.isEmpty()))) {
      return Message.error("admin.tenantCategory.deleteExistProductNotAllowed", new Object[0]);
    }
    this.tenantCategoryService.delete(id);
    return SUCCESS_MESSAGE;
  }

  @RequestMapping(value={"/search"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public List<TenantCategory> search(String name) {
    List filters = new ArrayList();
    int limit = 10000;
    if (StringUtils.isNotBlank(name)) {
      filters.add(new Filter("name", Filter.Operator.like, "%" + name + "%"));
      limit = 100;
    }
    return this.tenantCategoryService.findList(Integer.valueOf(limit), filters, null);
  }
}