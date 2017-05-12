package net.wit.controller.admin;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Area;
import net.wit.entity.BaseEntity;
import net.wit.entity.MobilePrice;
import net.wit.service.AreaService;
import net.wit.service.MobilePriceService;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("mobilePriceController")
@RequestMapping({"/admin/mobile/price"})
public class MobilePriceController extends BaseController
{

  @Resource(name="mobilePriceServiceImpl")
  private MobilePriceService mobilePriceService;

  @Resource(name="areaServiceImpl")
  private AreaService areaService;

  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String add(ModelMap model)
  {
    MobilePrice mobilePrice = new MobilePrice();
    model.addAttribute("mobilePrice", mobilePrice);
    return "/admin/mobile/price/add";
  }

  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String save(MobilePrice mobilePrice, Long areaId, RedirectAttributes redirectAttributes)
  {
    mobilePrice.setArea((Area)this.areaService.find(areaId));
    if (!(isValid(mobilePrice, new Class[] { BaseEntity.Save.class }))) {
      return "/admin/common/error";
    }
    this.mobilePriceService.save(mobilePrice);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model)
  {
    model.addAttribute("mobilePrice", this.mobilePriceService.find(id));
    return "/admin/mobile/price/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(MobilePrice mobilePrice, Long areaId, RedirectAttributes redirectAttributes)
  {
    mobilePrice.setArea((Area)this.areaService.find(areaId));
    if (!(isValid(mobilePrice, new Class[0]))) {
      return "/admin/common/error";
    }
    MobilePrice pMobilePrice = (MobilePrice)this.mobilePriceService.find(mobilePrice.getId());
    if (pMobilePrice == null) {
      return "/admin/common/error";
    }
    BeanUtils.copyProperties(mobilePrice, pMobilePrice, new String[] { "createDate", "modifyDate" });
    this.mobilePriceService.update(pMobilePrice);
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
    model.addAttribute("page", this.mobilePriceService.findPage(pageable));
    return "/admin/mobile/price/list";
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long[] ids)
  {
    if (ids.length >= this.mobilePriceService.count()) {
      return Message.error("admin.common.deleteAllNotAllowed", new Object[0]);
    }
    this.mobilePriceService.delete(ids);
    return SUCCESS_MESSAGE;
  }
}