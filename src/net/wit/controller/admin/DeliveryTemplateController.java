package net.wit.controller.admin;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.DeliveryTemplate;
import net.wit.service.DeliveryTemplateService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminDeliveryTemplateController")
@RequestMapping({"/admin/delivery_template"})
public class DeliveryTemplateController extends BaseController
{

  @Resource(name="deliveryTemplateServiceImpl")
  private DeliveryTemplateService deliveryTemplateService;

  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String add(Pageable pageable)
  {
    return "/admin/delivery_template/add";
  }

  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String save(DeliveryTemplate deliveryTemplate, RedirectAttributes redirectAttributes)
  {
    if (!(isValid(deliveryTemplate, new Class[0]))) {
      return "/admin/common/error";
    }
    this.deliveryTemplateService.save(deliveryTemplate);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String eidt(Long id, Model model)
  {
    model.addAttribute("deliveryTemplate", this.deliveryTemplateService.find(id));
    return "/admin/delivery_template/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String udpate(DeliveryTemplate deliveryTemplate, RedirectAttributes redirectAttributes)
  {
//    if (!(isValid(deliveryTemplate, new Class[0]))) {
//      return "/admin/common/error";
//    }
    this.deliveryTemplateService.update(deliveryTemplate);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String list(Pageable pageable, Model model)
  {
	  String searchValue = null;
		try {
			searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
			pageable.setSearchValue(searchValue);
		} catch (Exception localException) {
		}
    model.addAttribute("page", this.deliveryTemplateService.findPage(pageable));
    return "/admin/delivery_template/list";
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long[] ids)
  {
    this.deliveryTemplateService.delete(ids);
    return SUCCESS_MESSAGE;
  }
}