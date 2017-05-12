package net.wit.controller.admin;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Consultation;
import net.wit.service.ConsultationService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminConsultationController")
@RequestMapping({"/admin/consultation"})
public class ConsultationController extends BaseController
{

  @Resource(name="consultationServiceImpl")
  private ConsultationService consultationService;

  @RequestMapping(value={"/reply"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String reply(Long id, ModelMap model)
  {
    model.addAttribute("consultation", this.consultationService.find(id));
    return "/admin/consultation/reply";
  }

  @RequestMapping(value={"/reply"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String reply(Long id, String content, HttpServletRequest request, RedirectAttributes redirectAttributes)
  {
    if (!(isValid(Consultation.class, "content", content, new Class[0]))) {
      return "/admin/common/error";
    }
    Consultation consultation = (Consultation)this.consultationService.find(id);
    if (consultation == null) {
      return "/admin/common/error";
    }
    Consultation replyConsultation = new Consultation();
    replyConsultation.setContent(content);
    replyConsultation.setIp(request.getRemoteAddr());
    this.consultationService.reply(consultation, replyConsultation);

    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:reply.jhtml?id=" + id;
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model)
  {
    model.addAttribute("consultation", this.consultationService.find(id));
    return "/admin/consultation/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(Long id, @RequestParam(defaultValue="false") Boolean isShow, RedirectAttributes redirectAttributes)
  {
    Consultation consultation = (Consultation)this.consultationService.find(id);
    if (consultation == null) {
      return "/admin/common/error";
    }
    if (isShow != consultation.getIsShow()) {
      consultation.setIsShow(isShow);
      this.consultationService.update(consultation);
    }
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
    model.addAttribute("page", this.consultationService.findPage(null, null, null, pageable));
    return "/admin/consultation/list";
  }

  @RequestMapping(value={"/delete_reply"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message deleteReply(Long id)
  {
    Consultation consultation = (Consultation)this.consultationService.find(id);
    if ((consultation == null) || (consultation.getForConsultation() == null)) {
      return ERROR_MESSAGE;
    }
    this.consultationService.delete(consultation);
    return SUCCESS_MESSAGE;
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long[] ids)
  {
    if (ids != null) {
      this.consultationService.delete(ids);
    }
    return SUCCESS_MESSAGE;
  }
}