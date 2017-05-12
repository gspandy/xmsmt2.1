package net.wit.controller.admin;

import java.math.BigDecimal;
import java.util.HashSet;

import javax.annotation.Resource;

import net.wit.FileInfo;
import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Area;
import net.wit.entity.Community;
import net.wit.entity.Location;
import net.wit.entity.ProductImage;
import net.wit.entity.Tag;
import net.wit.service.AreaService;
import net.wit.service.CommunityService;
import net.wit.service.FileService;
import net.wit.service.ProductImageService;
import net.wit.service.TagService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminCommunityController")
@RequestMapping({"/admin/community"})
public class CommunityController extends BaseController
{

  @Resource(name="communityServiceImpl")
  private CommunityService communityService;

  @Resource(name="areaServiceImpl")
  private AreaService areaService;

  @Resource(name="fileServiceImpl")
  private FileService fileService;

  @Resource(name="productImageServiceImpl")
  private ProductImageService productImageService;

  @Resource(name="tagServiceImpl")
  private TagService tagService;

  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String add(ModelMap model)
  {
    model.addAttribute("tags", this.tagService.findList(Tag.Type.community));
    model.addAttribute("community", new Community());
    return "/admin/community/add";
  }

  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String save(Community community, Long[] tagIds, Long areaId, String locationX, String locationY, MultipartFile file, RedirectAttributes redirectAttributes)
  {
    if ((StringUtils.isNotEmpty(locationY)) && (StringUtils.isNotEmpty(locationX))) {
      Location location = new Location();
      BigDecimal x = new BigDecimal(locationX);
      BigDecimal y = new BigDecimal(locationY);
      location.setX(x);
      location.setY(y);
      community.setLocation(location);
    }
    if ((file != null) && (!(file.isEmpty()))) {
      if (!(this.fileService.isValid(FileInfo.FileType.image, file))) {
        addFlashMessage(redirectAttributes, Message.error("无效的文件类型", new Object[0]));
        return "redirect:add.jhtml";
      }
      ProductImage img = new ProductImage();
      img.setFile(file);
      this.productImageService.build(img);
      community.setImage(img.getThumbnail());
    }
    else
    {
      community.setImage(null);
    }
    community.setArea((Area)this.areaService.find(areaId));
    if (!(isValid(community, new Class[0]))) {
      return "/admin/common/error";
    }
    community.setTags(new HashSet(this.tagService.findList(tagIds)));
    this.communityService.save(community);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model)
  {
    model.addAttribute("tags", this.tagService.findList(Tag.Type.community));
    model.addAttribute("community", this.communityService.find(id));
    return "/admin/community/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(Community community, Long[] tagIds, Long areaId, String locationX, String locationY, MultipartFile file, RedirectAttributes redirectAttributes)
  {
    if ((StringUtils.isNotEmpty(locationY)) && (StringUtils.isNotEmpty(locationX))) {
      Location location = new Location();
      BigDecimal x = new BigDecimal(locationX);
      BigDecimal y = new BigDecimal(locationY);
      location.setX(x);
      location.setY(y);
      community.setLocation(location);
    }
    if ((file != null) && (!(file.isEmpty()))) {
      if (!(this.fileService.isValid(FileInfo.FileType.image, file))) {
        addFlashMessage(redirectAttributes, Message.error("无效的文件类型", new Object[0]));
        return "redirect:edit.jhtml";
      }
      ProductImage img = new ProductImage();
      img.setFile(file);
      this.productImageService.build(img);
      community.setImage(img.getThumbnail());
    }

    community.setArea((Area)this.areaService.find(areaId));
    if (!(isValid(community, new Class[0]))) {
      return "/admin/common/error";
    }
    community.setTags(new HashSet(this.tagService.findList(tagIds)));
    this.communityService.update(community, new String[] { "hits", "pageNumber" });
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
    model.addAttribute("page", this.communityService.findPage(pageable));
    return "/admin/community/list";
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long[] ids)
  {
    this.communityService.delete(ids);
    return SUCCESS_MESSAGE;
  }
}