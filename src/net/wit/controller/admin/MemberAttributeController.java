package net.wit.controller.admin;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.BaseEntity;
import net.wit.entity.MemberAttribute;
import net.wit.service.MemberAttributeService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminMemberAttributeController")
@RequestMapping({"/admin/member_attribute"})
public class MemberAttributeController extends BaseController
{

  @Resource(name="memberAttributeServiceImpl")
  private MemberAttributeService memberAttributeService;

  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String add(ModelMap model, RedirectAttributes redirectAttributes)
  {
    if (this.memberAttributeService.count() - 8L >= 10L) {
      addFlashMessage(redirectAttributes, Message.warn("admin.memberAttribute.addCountNotAllowed", new Object[] { Integer.valueOf(10) }));
    }
    return "/admin/member_attribute/add";
  }

  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String save(MemberAttribute memberAttribute, RedirectAttributes redirectAttributes)
  {
    if (!(isValid(memberAttribute, new Class[] { BaseEntity.Save.class }))) {
      return "/admin/common/error";
    }
    if ((memberAttribute.getType() == MemberAttribute.Type.select) || (memberAttribute.getType() == MemberAttribute.Type.checkbox)) {
      List options = memberAttribute.getOptions();
      if (options != null) {
        for (Iterator iterator = options.iterator(); iterator.hasNext(); ) {
          String option = (String)iterator.next();
          if (StringUtils.isEmpty(option)) {
            iterator.remove();
          }
        }
      }
//      if ((options != null) && (!(options.isEmpty()))) break;// TODO
      return "/admin/common/error";
    }
    if (memberAttribute.getType() == MemberAttribute.Type.text)
      memberAttribute.setOptions(null);
    else {
      return "/admin/common/error";
    }
    Integer propertyIndex = this.memberAttributeService.findUnusedPropertyIndex();
    if (propertyIndex == null) {
      return "/admin/common/error";
    }
    memberAttribute.setPropertyIndex(propertyIndex);
    this.memberAttributeService.save(memberAttribute);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model)
  {
    model.addAttribute("memberAttribute", this.memberAttributeService.find(id));
    return "/admin/member_attribute/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(MemberAttribute memberAttribute, RedirectAttributes redirectAttributes)
  {
    if (!(isValid(memberAttribute, new Class[0]))) {
      return "/admin/common/error";
    }
    MemberAttribute pMemberAttribute = (MemberAttribute)this.memberAttributeService.find(memberAttribute.getId());
    if (pMemberAttribute == null) {
      return "/admin/common/error";
    }
    if ((pMemberAttribute.getType() == MemberAttribute.Type.select) || (pMemberAttribute.getType() == MemberAttribute.Type.checkbox)) {
      List options = memberAttribute.getOptions();
      if (options != null) {
        for (Iterator iterator = options.iterator(); iterator.hasNext(); ) {
          String option = (String)iterator.next();
          if (StringUtils.isEmpty(option)) {
            iterator.remove();
          }
        }
      }
//      if ((options != null) && (!(options.isEmpty()))) break;// TODO
      return "/admin/common/error";
    }

    memberAttribute.setOptions(null);

    label142: this.memberAttributeService.update(memberAttribute, new String[] { "type", "propertyIndex" });
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
    model.addAttribute("page", this.memberAttributeService.findPage(pageable));
    return "/admin/member_attribute/list";
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long[] ids)
  {
    this.memberAttributeService.delete(ids);
    return SUCCESS_MESSAGE;
  }
}