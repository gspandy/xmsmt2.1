package net.wit.controller.admin;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.wit.Pageable;
import net.wit.entity.Member;
import net.wit.service.MemberService;
import net.wit.service.MessageService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminMessageController")
@RequestMapping({"/admin/message"})
public class MessageController extends BaseController
{

  @Resource(name="messageServiceImpl")
  MessageService messageService;

  @Resource(name="memberServiceImpl")
  MemberService memberService;

  @RequestMapping(value={"/check_username"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public boolean checkUsername(String username)
  {
    return (!(this.memberService.usernameExists(username)));
  }

  @RequestMapping(value={"/send"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String send(Long draftMessageId, Model model)
  {
    net.wit.entity.Message draftMessage = (net.wit.entity.Message)this.messageService.find(draftMessageId);
    if ((draftMessage != null) && (draftMessage.getIsDraft().booleanValue()) && (draftMessage.getSender() == null)) {
      model.addAttribute("draftMessage", draftMessage);
    }
    return "admin/message/send";
  }

  @RequestMapping(value={"/send"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String send(Long draftMessageId, String username, String title, String content, @RequestParam(defaultValue="false") Boolean isDraft, HttpServletRequest request, RedirectAttributes redirectAttributes)
  {
    if (!(isValid(net.wit.entity.Message.class, "content", content, new Class[0]))) {
      return "/admin/common/error";
    }
    net.wit.entity.Message draftMessage = (net.wit.entity.Message)this.messageService.find(draftMessageId);
    if ((draftMessage != null) && (draftMessage.getIsDraft().booleanValue()) && (draftMessage.getSender() == null)) {
      this.messageService.delete(draftMessage);
    }
    Member receiver = null;
    if (StringUtils.isNotEmpty(username)) {
      receiver = this.memberService.findByUsername(username);
      if (receiver == null) {
        return "/admin/common/error";
      }
    }
    net.wit.entity.Message message = new net.wit.entity.Message();
    message.setTitle(title);
    message.setContent(content);
    message.setIp(request.getRemoteAddr());
    message.setIsDraft(isDraft);
    message.setSenderRead(Boolean.valueOf(true));
    message.setReceiverRead(Boolean.valueOf(false));
    message.setSenderDelete(Boolean.valueOf(false));
    message.setReceiverDelete(Boolean.valueOf(false));
    message.setSender(null);
    message.setReceiver(receiver);
    message.setForMessage(null);
    message.setReplyMessages(null);
    this.messageService.save(message);
    if (isDraft.booleanValue()) {
      addFlashMessage(redirectAttributes, net.wit.Message.success("admin.message.saveDraftSuccess", new Object[0]));
      return "redirect:draft.jhtml";
    }
    addFlashMessage(redirectAttributes, net.wit.Message.success("admin.message.sendSuccess", new Object[0]));
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/view"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String view(Long id, Model model)
  {
    net.wit.entity.Message message = (net.wit.entity.Message)this.messageService.find(id);
    if ((message == null) || (message.getIsDraft().booleanValue()) || (message.getForMessage() != null)) {
      return "/admin/common/error";
    }
    if (((message.getSender() != null) && (message.getReceiver() != null)) || ((message.getReceiver() == null) && (message.getReceiverDelete().booleanValue())) || ((message.getSender() == null) && (message.getSenderDelete().booleanValue()))) {
      return "/admin/common/error";
    }
    if (message.getReceiver() == null)
      message.setReceiverRead(Boolean.valueOf(true));
    else {
      message.setSenderRead(Boolean.valueOf(true));
    }
    this.messageService.update(message);
    model.addAttribute("adminMessage", message);
    return "/admin/message/view";
  }

  @RequestMapping(value={"/reply"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String reply(Long id, String content, HttpServletRequest request, RedirectAttributes redirectAttributes)
  {
    if (!(isValid(net.wit.entity.Message.class, "content", content, new Class[0]))) {
      return "/admin/common/error";
    }
    net.wit.entity.Message forMessage = (net.wit.entity.Message)this.messageService.find(id);
    if ((forMessage == null) || (forMessage.getIsDraft().booleanValue()) || (forMessage.getForMessage() != null)) {
      return "/admin/common/error";
    }
    if (((forMessage.getSender() != null) && (forMessage.getReceiver() != null)) || ((forMessage.getReceiver() == null) && (forMessage.getReceiverDelete().booleanValue())) || ((forMessage.getSender() == null) && (forMessage.getSenderDelete().booleanValue()))) {
      return "/admin/common/error";
    }
    net.wit.entity.Message message = new net.wit.entity.Message();
    message.setTitle("reply: " + forMessage.getTitle());
    message.setContent(content);
    message.setIp(request.getRemoteAddr());
    message.setIsDraft(Boolean.valueOf(false));
    message.setSenderRead(Boolean.valueOf(true));
    message.setReceiverRead(Boolean.valueOf(false));
    message.setSenderDelete(Boolean.valueOf(false));
    message.setReceiverDelete(Boolean.valueOf(false));
    message.setSender(null);
    message.setReceiver((forMessage.getReceiver() == null) ? forMessage.getSender() : forMessage.getReceiver());
    if (((forMessage.getReceiver() == null) && (!(forMessage.getSenderDelete().booleanValue()))) || ((forMessage.getSender() == null) && (!(forMessage.getReceiverDelete().booleanValue())))) {
      message.setForMessage(forMessage);
    }
    message.setReplyMessages(null);
    this.messageService.save(message);

    if (forMessage.getSender() == null) {
      forMessage.setSenderRead(Boolean.valueOf(true));
      forMessage.setReceiverRead(Boolean.valueOf(false));
    } else {
      forMessage.setSenderRead(Boolean.valueOf(false));
      forMessage.setReceiverRead(Boolean.valueOf(true));
    }
    this.messageService.update(forMessage);

    if (((forMessage.getReceiver() == null) && (!(forMessage.getSenderDelete().booleanValue()))) || ((forMessage.getSender() == null) && (!(forMessage.getReceiverDelete().booleanValue())))) {
      addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
      return "redirect:view.jhtml?id=" + forMessage.getId();
    }
    addFlashMessage(redirectAttributes, net.wit.Message.success("admin.message.replySuccess", new Object[0]));
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String list(Pageable pageable, Model model)
  {
	  String searchValue = null;
		try {
			searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
			pageable.setSearchValue(searchValue);
		} catch (Exception localException) {
		}
    model.addAttribute("page", this.messageService.findPage(null, pageable));
    return "/admin/message/list";
  }

  @RequestMapping(value={"/draft"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String draft(Pageable pageable, Model model)
  {
    model.addAttribute("page", this.messageService.findDraftPage(null, pageable));
    return "/admin/message/draft";
  }

  @RequestMapping(value={"delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public net.wit.Message delete(Long[] ids)
  {
    if (ids != null) {
      for (Long id : ids) {
        this.messageService.delete(id, null);
      }
    }
    return SUCCESS_MESSAGE;
  }
}