package net.wit.controller.admin;

import java.util.Iterator;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Parameter;
import net.wit.entity.ParameterGroup;
import net.wit.entity.ProductCategory;
import net.wit.service.ParameterGroupService;
import net.wit.service.ProductCategoryService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminParameterGroupController")
@RequestMapping({"/admin/parameter_group"})
public class ParameterGroupController extends BaseController
{

  @Resource(name="parameterGroupServiceImpl")
  private ParameterGroupService parameterGroupService;

  @Resource(name="productCategoryServiceImpl")
  private ProductCategoryService productCategoryService;

  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String add(ModelMap model)
  {
    model.addAttribute("productCategoryTree", this.productCategoryService.findTree());
    return "/admin/parameter_group/add";
  }

  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String save(ParameterGroup parameterGroup, Long productCategoryId, RedirectAttributes redirectAttributes)
  {
    for (Iterator iterator = parameterGroup.getParameters().iterator(); iterator.hasNext(); ) {
      Parameter parameter = (Parameter)iterator.next();
      if ((parameter == null) || (parameter.getName() == null))
        iterator.remove();
      else {
        parameter.setParameterGroup(parameterGroup);
      }
    }
    parameterGroup.setProductCategory((ProductCategory)this.productCategoryService.find(productCategoryId));
    if (!(isValid(parameterGroup, new Class[0]))) {
      return "/admin/common/error";
    }
    this.parameterGroupService.save(parameterGroup);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model)
  {
    model.addAttribute("parameterGroup", this.parameterGroupService.find(id));
    model.addAttribute("productCategoryTree", this.productCategoryService.findTree());
    return "/admin/parameter_group/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(ParameterGroup parameterGroup, Long productCategoryId, RedirectAttributes redirectAttributes)
  {
    for (Iterator iterator = parameterGroup.getParameters().iterator(); iterator.hasNext(); ) {
      Parameter parameter = (Parameter)iterator.next();
      if ((parameter == null) || (parameter.getName() == null))
        iterator.remove();
      else {
        parameter.setParameterGroup(parameterGroup);
      }
    }
    parameterGroup.setProductCategory((ProductCategory)this.productCategoryService.find(productCategoryId));
    if (!(isValid(parameterGroup, new Class[0]))) {
      return "/admin/common/error";
    }
    this.parameterGroupService.update(parameterGroup);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
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
    model.addAttribute("page", this.parameterGroupService.findPage(pageable));
    return "/admin/parameter_group/list";
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long[] ids)
  {
    this.parameterGroupService.delete(ids);
    return SUCCESS_MESSAGE;
  }
}