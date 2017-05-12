package net.wit.controller.admin;

import java.math.BigDecimal;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.MemberRank;
import net.wit.service.MemberRankService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminMemberRankController")
@RequestMapping({"/admin/member_rank"})
public class MemberRankController extends BaseController
{

  @Resource(name="memberRankServiceImpl")
  private MemberRankService memberRankService;

  @RequestMapping(value={"/check_name"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public boolean checkName(String previousName, String name)
  {
    if (StringUtils.isEmpty(name)) {
      return false;
    }

    return (!(this.memberRankService.nameUnique(previousName, name)));
  }

  @RequestMapping(value={"/check_amount"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public boolean checkAmount(BigDecimal previousAmount, BigDecimal amount)
  {
    if (amount == null) {
      return false;
    }

    return (!(this.memberRankService.amountUnique(previousAmount, amount)));
  }

  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String add(ModelMap model)
  {
    return "/admin/member_rank/add";
  }

  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String save(MemberRank memberRank, RedirectAttributes redirectAttributes)
  {
    if (!(isValid(memberRank, new Class[0]))) {
      return "/admin/common/error";
    }
    if (this.memberRankService.nameExists(memberRank.getName())) {
      return "/admin/common/error";
    }
    if (memberRank.getIsSpecial().booleanValue())
      memberRank.setAmount(null);
    else if ((memberRank.getAmount() == null) || (this.memberRankService.amountExists(memberRank.getAmount()))) {
      return "/admin/common/error";
    }
    memberRank.setMembers(null);
    memberRank.setPromotions(null);
    this.memberRankService.save(memberRank);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model)
  {
    model.addAttribute("memberRank", this.memberRankService.find(id));
    return "/admin/member_rank/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(MemberRank memberRank, RedirectAttributes redirectAttributes)
  {
    if (!(isValid(memberRank, new Class[0]))) {
      return "/admin/common/error";
    }
    MemberRank pMemberRank = (MemberRank)this.memberRankService.find(memberRank.getId());
    if (pMemberRank == null) {
      return "/admin/common/error";
    }
    if (!(this.memberRankService.nameUnique(pMemberRank.getName(), memberRank.getName()))) {
      return "/admin/common/error";
    }
    if (pMemberRank.getIsDefault().booleanValue()) {
      memberRank.setIsDefault(Boolean.valueOf(true));
    }
    if (memberRank.getIsSpecial().booleanValue())
      memberRank.setAmount(null);
    else if ((memberRank.getAmount() == null) || (!(this.memberRankService.amountUnique(pMemberRank.getAmount(), memberRank.getAmount())))) {
      return "/admin/common/error";
    }
    this.memberRankService.update(memberRank, new String[] { "members", "promotions" });
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
    model.addAttribute("page", this.memberRankService.findPage(pageable));
    return "/admin/member_rank/list";
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long[] ids)
  {
    if (ids != null) {
      for (Long id : ids) {
        MemberRank memberRank = (MemberRank)this.memberRankService.find(id);
        if ((memberRank != null) && (memberRank.getMembers() != null) && (!(memberRank.getMembers().isEmpty()))) {
          return Message.error("admin.memberRank.deleteExistNotAllowed", new Object[] { memberRank.getName() });
        }
      }
      long totalCount = this.memberRankService.count();
      if (ids.length >= totalCount) {
        return Message.error("admin.common.deleteAllNotAllowed", new Object[0]);
      }
      this.memberRankService.delete(ids);
    }
    return SUCCESS_MESSAGE;
  }
}