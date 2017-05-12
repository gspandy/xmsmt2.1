package net.wit.controller.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import net.wit.Filter;
import net.wit.Message;
import net.wit.entity.ArticleCategory;
import net.wit.service.ArticleCategoryService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminArticleCategoryController")
@RequestMapping({"/admin/article_category"})
public class ArticleCategoryController extends BaseController
{

  @Resource(name="articleCategoryServiceImpl")
  private ArticleCategoryService articleCategoryService;

  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String add(ModelMap model)
  {
    model.addAttribute("articleCategoryTree", this.articleCategoryService.findTree());
    return "/admin/article_category/add";
  }

  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String save(ArticleCategory articleCategory, Long parentId, RedirectAttributes redirectAttributes)
  {
    articleCategory.setParent((ArticleCategory)this.articleCategoryService.find(parentId));
    if (!(isValid(articleCategory, new Class[0]))) {
      return "/admin/common/error";
    }
    articleCategory.setTreePath(null);
    articleCategory.setGrade(null);
    articleCategory.setChildren(null);
    articleCategory.setArticles(null);
    this.articleCategoryService.save(articleCategory);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model)
  {
    ArticleCategory articleCategory = (ArticleCategory)this.articleCategoryService.find(id);
    model.addAttribute("articleCategoryTree", this.articleCategoryService.findTree());
    model.addAttribute("articleCategory", articleCategory);
    model.addAttribute("children", this.articleCategoryService.findChildren(articleCategory));
    return "/admin/article_category/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(ArticleCategory articleCategory, Long parentId, RedirectAttributes redirectAttributes)
  {
    articleCategory.setParent((ArticleCategory)this.articleCategoryService.find(parentId));
    if (!(isValid(articleCategory, new Class[0]))) {
      return "/admin/common/error";
    }
    if (articleCategory.getParent() != null) {
      ArticleCategory parent = articleCategory.getParent();
      if (parent.equals(articleCategory)) {
        return "/admin/common/error";
      }
      List children = this.articleCategoryService.findChildren(parent);
      if ((children != null) && (children.contains(parent))) {
        return "/admin/common/error";
      }
    }
    this.articleCategoryService.update(articleCategory, new String[] { "treePath", "grade", "children", "articles" });
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String list(ModelMap model)
  {
    model.addAttribute("articleCategoryTree", this.articleCategoryService.findTree());
    return "/admin/article_category/list";
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long id)
  {
    ArticleCategory articleCategory = (ArticleCategory)this.articleCategoryService.find(id);
    if (articleCategory == null) {
      return ERROR_MESSAGE;
    }
    Set children = articleCategory.getChildren();
    if ((children != null) && (!(children.isEmpty()))) {
      return Message.error("admin.articleCategory.deleteExistChildrenNotAllowed", new Object[0]);
    }
    Set articles = articleCategory.getArticles();
    if ((articles != null) && (!(articles.isEmpty()))) {
      return Message.error("admin.articleCategory.deleteExistArticleNotAllowed", new Object[0]);
    }
    this.articleCategoryService.delete(id);
    return SUCCESS_MESSAGE;
  }

  @RequestMapping(value={"/search"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public List<ArticleCategory> search(String name) {
    List filters = new ArrayList();
    int limit = 10000;
    if (StringUtils.isNotBlank(name)) {
      filters.add(new Filter("name", Filter.Operator.like, "%" + name + "%"));
      limit = 100;
    }
    return this.articleCategoryService.findList(Integer.valueOf(limit), filters, null);
  }
}