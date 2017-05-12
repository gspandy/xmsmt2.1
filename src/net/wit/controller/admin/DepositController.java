package net.wit.controller.admin;

import javax.annotation.Resource;

import net.wit.Pageable;
import net.wit.entity.Member;
import net.wit.service.DepositService;
import net.wit.service.MemberService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("adminDepositController")
@RequestMapping({"/admin/deposit"})
public class DepositController extends BaseController
{

  @Resource(name="depositServiceImpl")
  private DepositService depositService;

  @Resource(name="memberServiceImpl")
  private MemberService memberService;

  @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String list(Long memberId, Pageable pageable, ModelMap model)
  {
	  String searchValue = null;
		try {
			searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
			pageable.setSearchValue(searchValue);
		} catch (Exception localException) {
		}
    Member member = (Member)this.memberService.find(memberId);
    if (member != null) {
      model.addAttribute("member", member);
      model.addAttribute("page", this.depositService.findPage(member, pageable));
    } else {
      model.addAttribute("page", this.depositService.findPage(pageable));
    }
    return "/admin/deposit/list";
  }
}