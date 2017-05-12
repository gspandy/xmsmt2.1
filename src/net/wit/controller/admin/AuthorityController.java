package net.wit.controller.admin;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.map.LinkedMap;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.wit.controller.shop.BaseController;
import net.wit.entity.Authority;
import net.wit.enums.AuthorityGroup;
import net.wit.service.AdminService;
import net.wit.service.AuthorityService;

/**
 * @ClassName：AuthorityController @Description：
 * @author：Chenlf
 * @date：2015年9月9日 上午8:10:24
 */
@Controller("adminAuthorityController")
@RequestMapping(value = "/admin/authority")
public class AuthorityController extends BaseController {

	@Resource(name = "authorityServiceImpl")
	private AuthorityService authorityService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {
		Map<AuthorityGroup, List<Authority>> mapResult = new LinkedMap();
		AuthorityGroup[] authorityGroups = AuthorityGroup.values();
		for (AuthorityGroup group : authorityGroups) {
			List<Authority> authoritys = authorityService.findList(group, null);
			mapResult.put(group, authoritys);
		}
		model.addAttribute("authMap", mapResult);
		return "/admin/authority/list";
	}

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model) {
		model.addAttribute("authorityTree", this.authorityService.findTree());
		model.addAttribute("groups", AuthorityGroup.values());
		return "/admin/authority/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(Authority authority, Long parentId, RedirectAttributes redirectAttributes) {
		authority.setParent(this.authorityService.find(parentId));
		if (!(isValid(authority, new Class[0]))) {
			return "/admin/authority/add";
		}
		this.authorityService.save(authority);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		Authority authority = authorityService.find(id);
		model.addAttribute("authority", authority);
		model.addAttribute("authorityTree", this.authorityService.findTree());
		model.addAttribute("children", this.authorityService.findChildren(authority));
		model.addAttribute("groups", AuthorityGroup.values());
		return "/admin/authority/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(Authority authority, Long parentId, RedirectAttributes redirectAttributes) {
		authority.setParent(this.authorityService.find(parentId));
		if (!(isValid(authority, new Class[0]))) {
			return "/admin/authority/edit";
		}
		if (authority.getParent() != null) {
			Authority parent = authority.getParent();
			if (parent.equals(authority)) {
				return "/admin/authority/edit";
			}
			List children = this.authorityService.findChildren(parent);
			if ((children != null) && (children.contains(parent))) {
				return "/admin/authority/edit";
			}
		}
		this.authorityService.update(authority);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}
}
