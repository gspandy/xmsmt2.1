package net.wit.controller.admin;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.wit.Filter;
import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Brand;
import net.wit.entity.Tenant;
import net.wit.service.AdminService;
import net.wit.service.BrandService;
import net.wit.service.TenantService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminBrandController")
@RequestMapping({ "/admin/brand" })
public class BrandController extends BaseController {

	@Resource(name = "brandServiceImpl")
	private BrandService brandService;

	@Resource(name = "tenantServiceImpl")
	private TenantService tenantService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model) {
		model.addAttribute("types", Brand.Type.values());
		return "/admin/brand/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(Brand brand, Long tenantId, RedirectAttributes redirectAttributes) {
		if (!(isValid(brand, new Class[0]))) {
			return "/admin/common/error";
		}
		if (brand.getType() == Brand.Type.text)
			brand.setLogo(null);
		else if (StringUtils.isEmpty(brand.getLogo())) {
			return "/admin/common/error";
		}
		Tenant tenant = tenantService.find(tenantId);
		if (tenant != null) {
			brand.setTenant(tenant);
		} else {
			brand.setTenant(adminService.getCurrent().getTenant());
		}
		brand.setProducts(null);
		brand.setProductCategories(null);
		brand.setPromotions(null);
		this.brandService.save(brand);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		model.addAttribute("types", Brand.Type.values());
		model.addAttribute("brand", this.brandService.find(id));
		return "/admin/brand/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(Brand brand, RedirectAttributes redirectAttributes) {
		if (!(isValid(brand, new Class[0]))) {
			return "/admin/common/error";
		}
		if (brand.getType() == Brand.Type.text)
			brand.setLogo(null);
		else if (StringUtils.isEmpty(brand.getLogo())) {
			return "/admin/common/error";
		}
		this.brandService.update(brand, new String[] { "products", "productCategories", "promotions", "tenant" });
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
		model.addAttribute("page", this.brandService.findPage(adminService.getCurrent().getTenant(), pageable));
		return "/admin/brand/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.brandService.delete(ids);
		return SUCCESS_MESSAGE;
	}

	@RequestMapping(value = { "/search" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public List<Brand> search(String name) {
		List filters = new ArrayList();
		int limit = 10000;
		if (StringUtils.isNotBlank(name)) {
			filters.add(new Filter("name", Filter.Operator.like, "%" + name + "%"));
			limit = 100;
		}
		return this.brandService.findList(Integer.valueOf(0), Integer.valueOf(limit), filters, null);
	}
}