package net.wit.controller.admin;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.DeliveryCorp;
import net.wit.entity.ShippingMethod;
import net.wit.service.DeliveryCorpService;
import net.wit.service.ShippingMethodService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminShippingMethodController")
@RequestMapping({"/admin/shipping_method"})
public class ShippingMethodController extends BaseController
{

  @Resource(name="shippingMethodServiceImpl")
  private ShippingMethodService shippingMethodService;

  @Resource(name="deliveryCorpServiceImpl")
  private DeliveryCorpService deliveryCorpService;

  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String add(ModelMap model)
  {
    model.addAttribute("deliveryCorps", this.deliveryCorpService.findAll());
    return "/admin/shipping_method/add";
  }

  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String save(ShippingMethod shippingMethod, Long defaultDeliveryCorpId, RedirectAttributes redirectAttributes)
  {
    shippingMethod.setDefaultDeliveryCorp((DeliveryCorp)this.deliveryCorpService.find(defaultDeliveryCorpId));
    if (!(isValid(shippingMethod, new Class[0]))) {
      return "/admin/common/error";
    }
    shippingMethod.setPaymentMethods(null);
    shippingMethod.setOrders(null);
    this.shippingMethodService.save(shippingMethod);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model)
  {
    model.addAttribute("deliveryCorps", this.deliveryCorpService.findAll());
    model.addAttribute("shippingMethod", this.shippingMethodService.find(id));
    return "/admin/shipping_method/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(ShippingMethod shippingMethod, Long defaultDeliveryCorpId, RedirectAttributes redirectAttributes)
  {
    shippingMethod.setDefaultDeliveryCorp((DeliveryCorp)this.deliveryCorpService.find(defaultDeliveryCorpId));
    if (!(isValid(shippingMethod, new Class[0]))) {
      return "/admin/common/error";
    }
    this.shippingMethodService.update(shippingMethod, new String[] { "paymentMethods", "orders" });
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
    model.addAttribute("page", this.shippingMethodService.findPage(pageable));
    return "/admin/shipping_method/list";
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long[] ids)
  {
    if (ids.length >= this.shippingMethodService.count()) {
      return Message.error("admin.common.deleteAllNotAllowed", new Object[0]);
    }
    this.shippingMethodService.delete(ids);
    return SUCCESS_MESSAGE;
  }
}