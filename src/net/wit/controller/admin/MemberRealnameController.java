package net.wit.controller.admin;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Idcard;
import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.service.IdcardService;
import net.wit.service.MemberService;
import net.wit.service.TenantService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminMemberRealnameController")
@RequestMapping({"/admin/member_realname"})
public class MemberRealnameController extends BaseController
{

  @Resource(name="idcardServiceImpl")
  private IdcardService idcardService;

  @Resource(name="memberServiceImpl")
  private MemberService memberService;

  @Resource(name="tenantServiceImpl")
  private TenantService tenantService;

  @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String list(Pageable pageable, ModelMap model)
  {
	  String searchValue = null;
		try {
			searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
			pageable.setSearchValue(searchValue);
		} catch (Exception localException) {
		}
    model.addAttribute("page", this.idcardService.findMemberPage(pageable));
    return "/admin/mamber_realname/list";
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model, RedirectAttributes redirectAttributes)
  {
    Member member = (Member)this.memberService.find(id);
    if ((member == null) || (member.getIdcard() == null)) {
      if (member.getTenant() != null) {
        Tenant tenant = member.getTenant();
        if ((tenant != null) && (((Tenant.Status.wait == tenant.getStatus()) || (Tenant.Status.none == tenant.getStatus())))) {
          tenant.setStatus(Tenant.Status.fail);
          this.tenantService.save(tenant);
        }
      }
      addFlashMessage(redirectAttributes, Message.error("该商户未提交身份认证信息！", new Object[0]));
      return "redirect:list.jhtml";
    }
    model.addAttribute("member", member);
    return "/admin/mamber_realname/edit";
  }

  @RequestMapping(value={"/certification"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String certification(Long id, Idcard.AuthStatus status, RedirectAttributes redirectAttributes)
  {
    Member member = (Member)this.memberService.find(id);
    Idcard idcard = member.getIdcard();
    if (idcard == null) {
      addFlashMessage(redirectAttributes, Message.error("参数错误", new Object[0]));
    }
    idcard.setAuthStatus(status);
    idcard = (Idcard)this.idcardService.update(idcard);
    if (Idcard.AuthStatus.fail == status) {
      Tenant tenant = member.getTenant();
      if (tenant != null) {
        tenant.setStatus(Tenant.Status.fail);
        this.tenantService.save(tenant);
      }
    }
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }
}