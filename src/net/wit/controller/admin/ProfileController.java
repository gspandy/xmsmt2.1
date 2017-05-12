package net.wit.controller.admin;

import javax.annotation.Resource;
import net.wit.entity.Admin;
import net.wit.service.AdminService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminProfileController")
@RequestMapping({"/admin/profile"})
public class ProfileController extends BaseController
{

  @Resource(name="adminServiceImpl")
  private AdminService adminService;

  @RequestMapping(value={"/check_current_password"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public boolean checkCurrentPassword(String currentPassword)
  {
    if (StringUtils.isEmpty(currentPassword)) {
      return false;
    }
    Admin admin = this.adminService.getCurrent();

    return ((StringUtils.equals(DigestUtils.md5Hex(currentPassword), admin.getPassword())));
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(ModelMap model)
  {
    model.addAttribute("admin", this.adminService.getCurrent());
    return "/admin/profile/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(String currentPassword, String password, String email, RedirectAttributes redirectAttributes)
  {
    if (!(isValid(Admin.class, "email", email, new Class[0]))) {
      return "/admin/common/error";
    }
    Admin pAdmin = this.adminService.getCurrent();
    if ((StringUtils.isNotEmpty(currentPassword)) && (StringUtils.isNotEmpty(password))) {
      if (!(isValid(Admin.class, "password", password, new Class[0]))) {
        return "/admin/common/error";
      }
      if (!(StringUtils.equals(DigestUtils.md5Hex(currentPassword), pAdmin.getPassword()))) {
        return "/admin/common/error";
      }
      pAdmin.setPassword(DigestUtils.md5Hex(password));
    }
    pAdmin.setEmail(email);
    this.adminService.update(pAdmin);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:edit.jhtml";
  }
}