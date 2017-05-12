package net.wit.controller.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import net.wit.Filter;
import net.wit.Message;
import net.wit.Template;
import net.wit.entity.ProductChannel;
import net.wit.service.ArticleCategoryService;
import net.wit.service.ProductCategoryService;
import net.wit.service.ProductChannelService;
import net.wit.service.TemplateService;
import net.wit.service.TenantCategoryService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminProductChannelController")
@RequestMapping({"/admin/product_channel"})
public class ProductChannelController extends BaseController
{

  @Resource(name="productChannelServiceImpl")
  private ProductChannelService productChannelService;

  @Resource(name="productCategoryServiceImpl")
  private ProductCategoryService productCategoryService;

  @Resource(name="tenantCategoryServiceImpl")
  private TenantCategoryService tenantCategoryService;

  @Resource(name="templateServiceImpl")
  private TemplateService templateService;

  @Resource(name="articleCategoryServiceImpl")
  private ArticleCategoryService articleCategoryService;

  @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String list(ModelMap model)
  {
    List<ProductChannel> channels = this.productChannelService.findAll();
    for (ProductChannel c : channels) {
      c.setTemplate(this.templateService.get(c.getTemplateId()));
    }
    model.addAttribute("productChannels", channels);
    return "/admin/product_channel/list";
  }

  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String add(ModelMap model)
  {
    model.addAttribute("templates", this.templateService.getList(Template.Type.channel));
    return "/admin/product_channel/add";
  }

  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String save(ProductChannel productChannel, Long[] productCategoryIds, Long[] tenantCategoryIds, Long[] articleCategoryIds, RedirectAttributes redirectAttributes)
  {
    productChannel.setProductCategorys(new HashSet(this.productCategoryService.findList(productCategoryIds)));
    productChannel.setTenantCategorys(new HashSet(this.tenantCategoryService.findList(tenantCategoryIds)));
    productChannel.setArticleCategories(new HashSet(this.articleCategoryService.findList(articleCategoryIds)));
    if (!(isValid(productChannel, new Class[0])))
      return "/admin/common/error";
    try
    {
      this.productChannelService.save(productChannel);
    } catch (Exception e) {
      e.printStackTrace();
    }
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model)
  {
    ProductChannel productChannel = (ProductChannel)this.productChannelService.find(id);
    model.addAttribute("productChannel", productChannel);
    model.addAttribute("templates", this.templateService.getList(Template.Type.channel));
    return "/admin/product_channel/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(ProductChannel productChannel, Long[] productCategoryIds, Long[] tenantCategoryIds, Long[] articleCategoryIds, RedirectAttributes redirectAttributes)
  {
    productChannel.setProductCategorys(new HashSet(this.productCategoryService.findList(productCategoryIds)));
    productChannel.setTenantCategorys(new HashSet(this.tenantCategoryService.findList(tenantCategoryIds)));
    productChannel.setArticleCategories(new HashSet(this.articleCategoryService.findList(articleCategoryIds)));
    if (!(isValid(productChannel, new Class[0])))
      return "/admin/common/error";
    try
    {
      this.productChannelService.update(productChannel);
    } catch (Exception e) {
      e.printStackTrace();
    }

    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long id)
  {
    ProductChannel productChannel = (ProductChannel)this.productChannelService.find(id);
    if (productChannel == null) {
      return ERROR_MESSAGE;
    }
    this.productChannelService.delete(id);
    return SUCCESS_MESSAGE;
  }

  @RequestMapping(value={"/search"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public List<ProductChannel> search(String name) {
    List filters = new ArrayList();
    int limit = 10000;
    if (StringUtils.isNotBlank(name)) {
      filters.add(new Filter("name", Filter.Operator.like, "%" + name + "%"));
      limit = 100;
    }
    return this.productChannelService.findList(Integer.valueOf(limit), filters, null);
  }
}