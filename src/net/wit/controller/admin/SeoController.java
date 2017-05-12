package net.wit.controller.admin;

import javax.annotation.Resource;

import net.wit.Pageable;
import net.wit.entity.Seo;
import net.wit.service.SeoService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminSeoController")
@RequestMapping({"/admin/seo"})
public class SeoController extends BaseController
{

  @Resource(name="seoServiceImpl")
  private SeoService seoService;

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model)
  {
    model.addAttribute("seo", this.seoService.find(id));
    return "/admin/seo/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(Seo seo, RedirectAttributes redirectAttributes)
  {
    if (!(isValid(seo, new Class[0]))) {
      return "/admin/common/error";
    }
    this.seoService.update(seo, new String[] { "type" });
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
    model.addAttribute("page", this.seoService.findPage(pageable));
    return "/admin/seo/list";
  }
}