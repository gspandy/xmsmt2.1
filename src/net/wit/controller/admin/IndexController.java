package net.wit.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.wit.entity.Article;
import net.wit.entity.Product;
import net.wit.service.ArticleService;
import net.wit.service.ProductService;
import net.wit.service.SearchService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("adminIndexController")
@RequestMapping({"/admin/index"})
public class IndexController extends BaseController
{

  @Resource(name="articleServiceImpl")
  private ArticleService articleService;

  @Resource(name="productServiceImpl")
  private ProductService productService;

  @Resource(name="searchServiceImpl")
  private SearchService searchService;

  @RequestMapping(value={"/build"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String build(ModelMap model)
  {
    model.addAttribute("buildTypes", BuildType.values());
    return "/admin/index/build";
  }

  @RequestMapping(value={"/build"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Map<String, Object> build(BuildType buildType, Boolean isPurge, Integer first, Integer count)
  {
    long startTime = System.currentTimeMillis();
    if ((first == null) || (first.intValue() < 0)) {
      first = Integer.valueOf(0);
    }
    if ((count == null) || (count.intValue() <= 0)) {
      count = Integer.valueOf(50);
    }
    int buildCount = 0;
    boolean isCompleted = true;
    if (buildType == BuildType.article) {
      if ((first.intValue() == 0) && (isPurge != null) && (isPurge.booleanValue())) {
        this.searchService.purge(Article.class);
      }
      List<Article> articles = this.articleService.findList(null, null, null, first, count);
      for (Article article : articles) {
        this.searchService.index(article);
        ++buildCount;
      }
      first = Integer.valueOf(first.intValue() + articles.size());
      if (articles.size() == count.intValue())
        isCompleted = false;
    }
    else if (buildType == BuildType.product) {
      if ((first.intValue() == 0) && (isPurge != null) && (isPurge.booleanValue())) {
        this.searchService.purge(Product.class);
      }
      List<Product> products = this.productService.findList(null, null, null, first, count);
      for (Product product : products) {
        this.searchService.index(product);
        ++buildCount;
      }
      first = Integer.valueOf(first.intValue() + products.size());
      if (products.size() == count.intValue()) {
        isCompleted = false;
      }
    }
    long endTime = System.currentTimeMillis();
    Object map = new HashMap();
    ((Map)map).put("first", first);
    ((Map)map).put("buildCount", Integer.valueOf(buildCount));
    ((Map)map).put("buildTime", Long.valueOf(endTime - startTime));
    ((Map)map).put("isCompleted", Boolean.valueOf(isCompleted));
    return ((Map<String, Object>)map);
  }

  public static enum BuildType
  {
    article, product;
  }
}