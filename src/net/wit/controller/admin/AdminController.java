package net.wit.controller.admin;

import java.util.HashSet;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.BaseEntity;
import net.wit.service.AdminService;
import net.wit.service.RoleService;

@Controller("adminAdminController")
@RequestMapping({ "/admin/admin" })
public class AdminController extends BaseController {

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@Resource(name = "roleServiceImpl")
	private RoleService roleService;

	@RequestMapping(value = { "/check_username" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public boolean checkUsername(String username) {
		if (StringUtils.isEmpty(username)) {
			return false;
		}

		return (!this.adminService.usernameExists(username));
	}

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model) {
		Admin admin = adminService.getCurrent();
		model.addAttribute("roles", this.roleService.findList(admin.getTenant()));
		return "/admin/admin/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(Admin admin, Long[] roleIds, RedirectAttributes redirectAttributes) {
		admin.setRoles(new HashSet(this.roleService.findList(roleIds)));
		if (!(isValid(admin, new Class[] { BaseEntity.Save.class }))) {
			return "/admin/common/error";
		}
		if (this.adminService.usernameExists(admin.getUsername())) {
			return "/admin/common/error";
		}
		admin.setPassword(DigestUtils.md5Hex(admin.getPassword()));
		admin.setIsLocked(Boolean.valueOf(false));
		admin.setLoginFailureCount(Integer.valueOf(0));
		admin.setLockedDate(null);
		admin.setLoginDate(null);
		admin.setLoginIp(null);
		admin.setOrders(null);
		Admin adminTemp = adminService.getCurrent();
		admin.setTenant(adminTemp.getTenant());// 取内存中当前登录者的所属企业ID
		this.adminService.save(admin);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		Admin admin = adminService.getCurrent();
		model.addAttribute("roles", this.roleService.findList(admin.getTenant()));
		model.addAttribute("admin", this.adminService.find(id));
		return "/admin/admin/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(Admin admin, Long[] roleIds, RedirectAttributes redirectAttributes) {
		admin.setRoles(new HashSet(this.roleService.findList(roleIds)));
		if (!(isValid(admin, new Class[0]))) {
			return "/admin/common/error";
		}
		Admin pAdmin = adminService.find(admin.getId());
		if (pAdmin == null) {
			return "/admin/common/error";
		}
		if (StringUtils.isNotEmpty(admin.getPassword()))
			admin.setPassword(DigestUtils.md5Hex(admin.getPassword()));
		else {
			admin.setPassword(pAdmin.getPassword());
		}
		if ((pAdmin.getIsLocked().booleanValue()) && (!(admin.getIsLocked().booleanValue()))) {
			admin.setLoginFailureCount(Integer.valueOf(0));
			admin.setLockedDate(null);
		} else {
			admin.setIsLocked(pAdmin.getIsLocked());
			admin.setLoginFailureCount(pAdmin.getLoginFailureCount());
			admin.setLockedDate(pAdmin.getLockedDate());
		}
		this.adminService.update(admin, new String[] { "username", "loginDate", "loginIp", "orders", "tenant" });
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		 String searchValue = null;
			try {
				searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
				pageable.setSearchValue(searchValue);
			} catch (Exception localException) {
			}
		Admin admin = adminService.getCurrent();
		model.addAttribute("page", this.adminService.findPage(admin.getTenant(), pageable));
		return "/admin/admin/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		if (ids.length >= this.adminService.count()) {
			return Message.error("admin.common.deleteAllNotAllowed", new Object[0]);
		}
		this.adminService.delete(ids);
		return SUCCESS_MESSAGE;
	}
}