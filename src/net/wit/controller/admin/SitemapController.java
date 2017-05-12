package net.wit.controller.admin;

import javax.annotation.Resource;
import net.wit.Template;
import net.wit.service.StaticService;
import net.wit.service.TemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminSitemapController")
@RequestMapping({"/admin/sitemap"})
public class SitemapController extends BaseController
{

  @Resource(name="templateServiceImpl")
  private TemplateService templateService;

  @Resource(name="staticServiceImpl")
  private StaticService staticService;

  @RequestMapping(value={"/build"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String build(ModelMap model)
  {
    Template sitemapIndexTemplate = this.templateService.get("sitemapIndex");
    model.addAttribute("sitemapIndexPath", sitemapIndexTemplate.getStaticPath());
    return "/admin/sitemap/build";
  }

  @RequestMapping(value={"/build"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String build(RedirectAttributes redirectAttributes)
  {
    this.staticService.buildSitemap();
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:build.jhtml";
  }
}