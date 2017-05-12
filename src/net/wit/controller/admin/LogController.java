package net.wit.controller.admin;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.service.LogService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("adminLogController")
@RequestMapping({"/admin/log"})
public class LogController extends BaseController
{

  @Resource(name="logServiceImpl")
  private LogService logService;

  @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String list(Pageable pageable, ModelMap model)
  {
	  String searchValue = null;
		try {
			searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
			pageable.setSearchValue(searchValue);
		} catch (Exception localException) {
		}
    model.addAttribute("page", this.logService.findPage(pageable));
    return "/admin/log/list";
  }

  @RequestMapping(value={"/view"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String view(Long id, ModelMap model)
  {
    model.addAttribute("log", this.logService.find(id));
    return "/admin/log/view";
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long[] ids)
  {
    this.logService.delete(ids);
    return SUCCESS_MESSAGE;
  }

  @RequestMapping(value={"/clear"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message clear()
  {
    this.logService.clear();
    return SUCCESS_MESSAGE;
  }
}