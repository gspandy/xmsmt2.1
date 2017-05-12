package net.wit.controller.admin;

import java.util.HashSet;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.PaymentMethod;
import net.wit.service.PaymentMethodService;
import net.wit.service.ShippingMethodService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminPaymentMethodController")
@RequestMapping({"/admin/payment_method"})
public class PaymentMethodController extends BaseController
{

  @Resource(name="paymentMethodServiceImpl")
  private PaymentMethodService paymentMethodService;

  @Resource(name="shippingMethodServiceImpl")
  private ShippingMethodService shippingMethodService;

  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String add(ModelMap model)
  {
    model.addAttribute("methods", PaymentMethod.Method.values());
    model.addAttribute("shippingMethods", this.shippingMethodService.findAll());
    return "/admin/payment_method/add";
  }

  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String save(PaymentMethod paymentMethod, Long[] shippingMethodIds, RedirectAttributes redirectAttributes)
  {
    paymentMethod.setShippingMethods(new HashSet(this.shippingMethodService.findList(shippingMethodIds)));
    if (!(isValid(paymentMethod, new Class[0]))) {
      return "/admin/common/error";
    }
    paymentMethod.setOrders(null);
    this.paymentMethodService.save(paymentMethod);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model)
  {
    model.addAttribute("methods", PaymentMethod.Method.values());
    model.addAttribute("shippingMethods", this.shippingMethodService.findAll());
    model.addAttribute("paymentMethod", this.paymentMethodService.find(id));
    return "/admin/payment_method/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(PaymentMethod paymentMethod, Long[] shippingMethodIds, RedirectAttributes redirectAttributes)
  {
    paymentMethod.setShippingMethods(new HashSet(this.shippingMethodService.findList(shippingMethodIds)));
    if (!(isValid(paymentMethod, new Class[0]))) {
      return "/admin/common/error";
    }
    this.paymentMethodService.update(paymentMethod, new String[] { "orders" });
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
    model.addAttribute("page", this.paymentMethodService.findPage(pageable));
    return "/admin/payment_method/list";
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long[] ids)
  {
    if (ids.length >= this.paymentMethodService.count()) {
      return Message.error("admin.common.deleteAllNotAllowed", new Object[0]);
    }
    this.paymentMethodService.delete(ids);
    return SUCCESS_MESSAGE;
  }
}