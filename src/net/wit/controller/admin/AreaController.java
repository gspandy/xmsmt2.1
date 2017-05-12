package net.wit.controller.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.entity.Area;
import net.wit.service.AreaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminAreaController")
@RequestMapping({"/admin/area"})
public class AreaController extends BaseController
{

  @Resource(name="areaServiceImpl")
  private AreaService areaService;

  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String add(Long parentId, ModelMap model)
  {
    model.addAttribute("parent", this.areaService.find(parentId));
    return "/admin/area/add";
  }

  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String save(Area area, Long parentId, RedirectAttributes redirectAttributes)
  {
    area.setParent((Area)this.areaService.find(parentId));
    if (!(isValid(area, new Class[0]))) {
      return "/admin/common/error";
    }
    area.setFullName(null);
    area.setTreePath(null);
    area.setChildren(null);
    area.setMembers(null);
    area.setReceivers(null);
    area.setOrders(null);
    area.setDeliveryCenters(null);
    this.areaService.save(area);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model)
  {
    model.addAttribute("area", this.areaService.find(id));
    return "/admin/area/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(Area area, RedirectAttributes redirectAttributes)
  {
    if (!(isValid(area, new Class[0]))) {
      return "/admin/common/error";
    }
    this.areaService.update(area, new String[] { "fullName", "treePath", "parent", "children", "members", "receivers", "orders", "deliveryCenters" });
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String list(Long parentId, ModelMap model)
  {
    Area parent = (Area)this.areaService.find(parentId);
    if (parent != null) {
      model.addAttribute("parent", parent);
      model.addAttribute("areas", new ArrayList(parent.getChildren()));
    } else {
      model.addAttribute("areas", this.areaService.findRoots());
    }
    return "/admin/area/list";
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long id)
  {
    this.areaService.delete(id);
    return SUCCESS_MESSAGE;
  }

  @RequestMapping(value={"/getProvince"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Area getProvince(Long id)
  {
    Area area = (Area)this.areaService.find(id);
    Area parent = area.getParent();
    return parent;
  }

  @RequestMapping(value={"/getCitys"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Set<Area> getCitys(Long id)
  {
    Area area = (Area)this.areaService.find(id);
    Set citys = area.getChildren();
    return citys;
  }

  @RequestMapping(value={"/chooseProvince"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public List<Area> chooseProvince()
  {
    List provinces = this.areaService.findRoots();
    return provinces;
  }
}