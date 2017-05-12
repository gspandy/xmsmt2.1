package net.wit.controller.admin;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.AdPosition;
import net.wit.entity.AdPositionTenant;
import net.wit.service.AdPositionService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminAdPositionController")
@RequestMapping({"/admin/ad_position"})
public class AdPositionController extends BaseController
{

  @Resource(name="adPositionServiceImpl")
  private AdPositionService adPositionService;

  @RequestMapping(value={"/agree"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String agree(Long adId, Long adPositionTenantId, RedirectAttributes redirectAttributes)
  {
    try
    {
      AdPositionTenant adPositionTenant = this.adPositionService.findTenant(adPositionTenantId);
      adPositionTenant.setStatus(AdPositionTenant.Status.success);
      this.adPositionService.saveTenant(adPositionTenant);
      addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
      return "redirect:edit.jhtml?id=" + adId;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "redirect:edit.jhtml?id=" + adId;
  }

  @RequestMapping(value={"/disagree"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String disagree(Long adId, Long adPositionTenantId, RedirectAttributes redirectAttributes)
  {
    AdPositionTenant adPositionTenant = this.adPositionService.findTenant(adPositionTenantId);
    adPositionTenant.setStatus(AdPositionTenant.Status.fail);
    this.adPositionService.saveTenant(adPositionTenant);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:edit.jhtml?id=" + adId;
  }

  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String add(ModelMap model)
  {
    return "/admin/ad_position/add";
  }

  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String save(AdPosition adPosition, RedirectAttributes redirectAttributes)
  {
    if (!(isValid(adPosition, new Class[0]))) {
      return "/admin/common/error";
    }
    adPosition.setAds(null);
    this.adPositionService.save(adPosition);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model)
  {
    AdPosition adPosition = (AdPosition)this.adPositionService.find(id);
    model.addAttribute("adPosition", adPosition);
    if ((adPosition.getType() != null) && (adPosition.getType().equals(AdPosition.Type.spread))) {
      return "/admin/ad_position/spreadEdit";
    }
    return "/admin/ad_position/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(AdPosition adPosition, RedirectAttributes redirectAttributes)
  {
    if (!(isValid(adPosition, new Class[0]))) {
      return "/admin/common/error";
    }
    this.adPositionService.update(adPosition, new String[] { "ads" });
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
    model.addAttribute("page", this.adPositionService.findPage(pageable));
    return "/admin/ad_position/list";
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long[] ids)
  {
    this.adPositionService.delete(ids);
    return SUCCESS_MESSAGE;
  }
}