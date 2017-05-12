package net.wit.controller.admin;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.BaseEntity;
import net.wit.entity.Tag;
import net.wit.service.TagService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminTagController")
@RequestMapping({"/admin/tag"})
public class TagController extends BaseController
{

  @Resource(name="tagServiceImpl")
  private TagService tagService;

  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String add(ModelMap model)
  {
    model.addAttribute("types", Tag.Type.values());
    return "/admin/tag/add";
  }

  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String save(Tag tag, RedirectAttributes redirectAttributes)
  {
    if (!(isValid(tag, new Class[] { BaseEntity.Save.class }))) {
      return "/admin/common/error";
    }
    tag.setArticles(null);
    tag.setProducts(null);
    this.tagService.save(tag);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model)
  {
    model.addAttribute("types", Tag.Type.values());
    model.addAttribute("tag", this.tagService.find(id));
    return "/admin/tag/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(Tag tag, RedirectAttributes redirectAttributes)
  {
    if (!(isValid(tag, new Class[0]))) {
      return "/admin/common/error";
    }
    this.tagService.update(tag, new String[] { "type", "articles", "products" });
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
    model.addAttribute("page", this.tagService.findPage(pageable));
    return "/admin/tag/list";
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long[] ids)
  {
    this.tagService.delete(ids);
    return SUCCESS_MESSAGE;
  }
}