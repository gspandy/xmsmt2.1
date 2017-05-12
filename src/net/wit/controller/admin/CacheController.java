package net.wit.controller.admin;

import javax.annotation.Resource;

import net.wit.service.CacheService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminCacheController")
@RequestMapping({"/admin/cache"})
public class CacheController extends BaseController
{

  @Resource(name="cacheServiceImpl")
  private CacheService cacheService;

  @RequestMapping(value={"/clear"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String clear(ModelMap model)
  {
    Long totalMemory = null;
    Long maxMemory = null;
    Long freeMemory = null;
    try {
      totalMemory = Long.valueOf(Runtime.getRuntime().totalMemory() / 1024L / 1024L);
      maxMemory = Long.valueOf(Runtime.getRuntime().maxMemory() / 1024L / 1024L);
      freeMemory = Long.valueOf(Runtime.getRuntime().freeMemory() / 1024L / 1024L);
    } catch (Exception localException) {
    }
    model.addAttribute("totalMemory", totalMemory);
    model.addAttribute("maxMemory", maxMemory);
    model.addAttribute("freeMemory", freeMemory);
    model.addAttribute("cacheSize", Integer.valueOf(this.cacheService.getCacheSize()));
    model.addAttribute("diskStorePath", this.cacheService.getDiskStorePath());
    return "/admin/cache/clear";
  }

  @RequestMapping(value={"/clear"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String clear(RedirectAttributes redirectAttributes)
  {
    this.cacheService.clear();
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:clear.jhtml";
  }
}