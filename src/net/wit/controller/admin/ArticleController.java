package net.wit.controller.admin;

import java.util.HashSet;

import javax.annotation.Resource;

import net.wit.FileInfo;
import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Area;
import net.wit.entity.Article;
import net.wit.entity.ArticleCategory;
import net.wit.entity.ProductImage;
import net.wit.entity.Tag;
import net.wit.service.AreaService;
import net.wit.service.ArticleCategoryService;
import net.wit.service.ArticleService;
import net.wit.service.FileService;
import net.wit.service.ProductImageService;
import net.wit.service.TagService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminArticleController")
@RequestMapping({"/admin/article"})
public class ArticleController extends BaseController
{

  @Resource(name="articleServiceImpl")
  private ArticleService articleService;

  @Resource(name="articleCategoryServiceImpl")
  private ArticleCategoryService articleCategoryService;

  @Resource(name="tagServiceImpl")
  private TagService tagService;

  @Resource(name="areaServiceImpl")
  private AreaService areaService;

  @Resource(name="fileServiceImpl")
  private FileService fileService;

  @Resource(name="productImageServiceImpl")
  private ProductImageService productImageService;

  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String add(ModelMap model)
  {
    model.addAttribute("articleCategoryTree", this.articleCategoryService.findTree());
    model.addAttribute("tags", this.tagService.findList(Tag.Type.article));
    return "/admin/article/add";
  }

  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String save(Article article, Long areaId, Long articleCategoryId, Long[] tagIds, MultipartFile file, RedirectAttributes redirectAttributes)
  {
    if ((file != null) && (!(file.isEmpty()))) {
      if (!(this.fileService.isValid(FileInfo.FileType.image, file))) {
        addFlashMessage(redirectAttributes, Message.error("无效的文件类型", new Object[0]));
        return "redirect:add.jhtml";
      }
      ProductImage img = new ProductImage();
      img.setFile(file);
      this.productImageService.build(img);
      article.setImage(img.getThumbnail());
    }

    article.setArticleCategory((ArticleCategory)this.articleCategoryService.find(articleCategoryId));
    article.setTags(new HashSet(this.tagService.findList(tagIds)));
    article.setArea((Area)this.areaService.find(areaId));
    if (!(isValid(article, new Class[0]))) {
      return "/admin/common/error";
    }
    article.setHits(Long.valueOf(0L));
    article.setPageNumber(null);
    this.articleService.save(article);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model)
  {
    model.addAttribute("articleCategoryTree", this.articleCategoryService.findTree());
    model.addAttribute("tags", this.tagService.findList(Tag.Type.article));
    model.addAttribute("article", this.articleService.find(id));
    return "/admin/article/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(Article article, Long areaId, Long articleCategoryId, Long[] tagIds, MultipartFile file, RedirectAttributes redirectAttributes)
  {
    if ((file != null) && (!(file.isEmpty()))) {
      if (!(this.fileService.isValid(FileInfo.FileType.image, file))) {
        addFlashMessage(redirectAttributes, Message.error("无效的文件类型", new Object[0]));
        return "redirect:edit.jhtml";
      }
      ProductImage img = new ProductImage();
      img.setFile(file);
      this.productImageService.build(img);
      article.setImage(img.getThumbnail());
    }

    article.setArticleCategory((ArticleCategory)this.articleCategoryService.find(articleCategoryId));
    article.setTags(new HashSet(this.tagService.findList(tagIds)));
    article.setArea((Area)this.areaService.find(areaId));
    if (!(isValid(article, new Class[0]))) {
      return "/admin/common/error";
    }
    Article art = (Article)this.articleService.find(article.getId());
    article.setTenant(art.getTenant());
    this.articleService.update(article, new String[] { "hits", "pageNumber" });
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
    model.addAttribute("page", this.articleService.findPage(pageable));
    return "/admin/article/list";
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long[] ids)
  {
    this.articleService.delete(ids);
    return SUCCESS_MESSAGE;
  }
}