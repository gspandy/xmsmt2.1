package net.wit.controller.admin;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.wit.entity.Article;
import net.wit.entity.ArticleCategory;
import net.wit.entity.Product;
import net.wit.entity.ProductCategory;
import net.wit.service.ArticleCategoryService;
import net.wit.service.ArticleService;
import net.wit.service.ProductCategoryService;
import net.wit.service.ProductService;
import net.wit.service.StaticService;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("adminStaticController")
@RequestMapping({"/admin/static"})
public class StaticController extends BaseController
{

  @Resource(name="articleServiceImpl")
  private ArticleService articleService;

  @Resource(name="articleCategoryServiceImpl")
  private ArticleCategoryService articleCategoryService;

  @Resource(name="productServiceImpl")
  private ProductService productService;

  @Resource(name="productCategoryServiceImpl")
  private ProductCategoryService productCategoryService;

  @Resource(name="staticServiceImpl")
  private StaticService staticService;

  @RequestMapping(value={"/build"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String build(ModelMap model)
  {
    model.addAttribute("buildTypes", BuildType.values());
    model.addAttribute("defaultBeginDate", DateUtils.addDays(new Date(), -7));
    model.addAttribute("defaultEndDate", new Date());
    model.addAttribute("articleCategoryTree", this.articleCategoryService.findChildren(null, null));
    model.addAttribute("productCategoryTree", this.productCategoryService.findChildren(null, null, null));
    return "/admin/static/build";
  }

  @RequestMapping(value={"/build"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Map<String, Object> build(BuildType buildType, Long articleCategoryId, Long productCategoryId, Date beginDate, Date endDate, Integer first, Integer count)
  {
    Calendar calendar;
    long startTime = System.currentTimeMillis();
    if (beginDate != null) {
      calendar = DateUtils.toCalendar(beginDate);
      calendar.set(11, calendar.getActualMinimum(11));
      calendar.set(12, calendar.getActualMinimum(12));
      calendar.set(13, calendar.getActualMinimum(13));
      beginDate = calendar.getTime();
    }
    if (endDate != null) {
      calendar = DateUtils.toCalendar(endDate);
      calendar.set(11, calendar.getActualMaximum(11));
      calendar.set(12, calendar.getActualMaximum(12));
      calendar.set(13, calendar.getActualMaximum(13));
      endDate = calendar.getTime();
    }
    if ((first == null) || (first.intValue() < 0)) {
      first = Integer.valueOf(0);
    }
    if ((count == null) || (count.intValue() <= 0)) {
      count = Integer.valueOf(50);
    }
    int buildCount = 0;
    boolean isCompleted = true;
    if (buildType == BuildType.index) {
      buildCount = this.staticService.buildIndex();
    } else if (buildType == BuildType.article) {
      ArticleCategory articleCategory = (ArticleCategory)this.articleCategoryService.find(articleCategoryId);
      List<Article> articles = this.articleService.findList(articleCategory, beginDate, endDate, first, count);
      for (Article article : articles) {
        buildCount += this.staticService.build(article);
      }
      first = Integer.valueOf(first.intValue() + articles.size());
      if (articles.size() == count.intValue())
        isCompleted = false;
    }
    else if (buildType == BuildType.product) {
      ProductCategory productCategory = (ProductCategory)this.productCategoryService.find(productCategoryId);
      List<Product> products = this.productService.findList(productCategory, beginDate, endDate, first, count);
      for (Product product : products) {
        buildCount += this.staticService.build(product);
      }
      first = Integer.valueOf(first.intValue() + products.size());
      if (products.size() == count.intValue())
        isCompleted = false;
    }
    else if (buildType == BuildType.other) {
      buildCount = this.staticService.buildOther();
    }
    long endTime = System.currentTimeMillis();
    Map map = new HashMap();
    map.put("first", first);
    map.put("buildCount", Integer.valueOf(buildCount));
    map.put("buildTime", Long.valueOf(endTime - startTime));
    map.put("isCompleted", Boolean.valueOf(isCompleted));
    return map;
  }

  public static enum BuildType
  {
    index, article, product, other;
  }
}