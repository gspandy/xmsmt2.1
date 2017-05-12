package net.wit.controller.admin;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.map.LinkedMap;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.Authority;
import net.wit.entity.Role;
import net.wit.entity.Tenant;
import net.wit.enums.AuthorityGroup;
import net.wit.service.AdminService;
import net.wit.service.AuthorityService;
import net.wit.service.RoleService;
import net.wit.service.TenantService;

@Controller("adminRoleController")
@RequestMapping({ "/admin/role" })
public class RoleController extends BaseController {

	@Resource(name = "roleServiceImpl")
	private RoleService roleService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@Resource(name = "authorityServiceImpl")
	private AuthorityService authorityService;

	@Resource(name = "tenantServiceImpl")
	private TenantService tenantService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(Model model) {
		AuthorityGroup[] groups = AuthorityGroup.values();
		Admin admin = adminService.getCurrent();
		Set<Role> roles = admin.getRoles();
		Role role = null;
		for (Role r : roles) {
			if (r.getIsSuper()) {
				role = r;
				break;
			}
		}
		if (role != null) {
			if (role.getTenant() == null) {
				List<Tenant> all = tenantService.findAll();
				model.addAttribute("tenants", all);
				model.addAttribute("allow", true);
			} else {
				model.addAttribute("tenant", role.getTenant());
				model.addAttribute("allow", false);
			}
		}
		Map<AuthorityGroup, List<Authority>> map = new LinkedMap();
		for (AuthorityGroup g : groups) {
			map.put(g, authorityService.findList(g, admin.getRoles()));
		}
		model.addAttribute("map", map);
		return "/admin/role/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(Role role, Long tenantId, Long[] authorityIds, RedirectAttributes redirectAttributes) {
		Tenant tenant = tenantService.find(tenantId);
		List<Authority> list = authorityService.findList(authorityIds);
		role.setAdmins(null);
		role.setTenant(tenant);
		role.setAuthorities(list);
		if (!(isValid(role, new Class[0]))) {
			return "/admin/common/error";
		}
		this.roleService.save(role);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		model.addAttribute("role", this.roleService.find(id));
		Admin admin = adminService.getCurrent();
		Map<AuthorityGroup, List<Authority>> map = new LinkedMap();
		for (AuthorityGroup g : AuthorityGroup.values()) {
			map.put(g, authorityService.findList(g, admin.getRoles()));
		}
		model.addAttribute("map", map);
		return "/admin/role/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(Role role, Long[] authorityIds, RedirectAttributes redirectAttributes) {
		Role pRole = roleService.find(role.getId());
		if (pRole == null) {
			return ERROR_VIEW;
		}
		if (pRole.getTenant() == null && pRole.getIsSuper()) {
			addFlashMessage(redirectAttributes, Message.warn("系统超级管理员角色不能被编辑"));
			return "redirect:list.jhtml";
		}
		BeanUtils.copyProperties(role, pRole, new String[] { "admins", "isSuper", "tenant" });
		List<Authority> list = authorityService.findList(authorityIds);
		pRole.setAuthorities(list);
		if (!(isValid(role, new Class[0]))) {
			return "/admin/common/error";
		}
		this.roleService.save(pRole);
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
		Set<Role> roles = admin.getRoles();
		Role role = null;
		for (Role r : roles) {
			if (r.getIsSuper()) {
				role = r;
				break;
			}
		}
		if (role != null) {
			model.addAttribute("page", this.roleService.findPage(role.getTenant(), pageable));
		} else {
		}
		return "/admin/role/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		if (ids != null) {
			for (Long id : ids) {
				Role role = (Role) this.roleService.find(id);
				if ((role != null) && ((((role.getAdmins() != null) && (!(role.getAdmins().isEmpty())))))) {
					return Message.error("admin.role.deleteExistNotAllowed", new Object[] { role.getName() });
				}
			}
			this.roleService.delete(ids);
		}
		return SUCCESS_MESSAGE;
	}
}