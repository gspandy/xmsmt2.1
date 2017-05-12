package net.wit.controller.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.wit.Message;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Product;
import net.wit.entity.ProductCategory;
import net.wit.entity.Specification;
import net.wit.entity.SpecificationValue;
import net.wit.entity.Tenant;
import net.wit.service.AdminService;
import net.wit.service.ProductCategoryService;
import net.wit.service.SpecificationService;
import net.wit.service.TenantService;
import net.wit.util.WebUtils;

@Controller("adminSpecificationController")
@RequestMapping({ "/admin/specification" })
public class SpecificationController extends BaseController {

	@Resource(name = "specificationServiceImpl")
	private SpecificationService specificationService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@Resource(name = "tenantServiceImpl")
	private TenantService tenantService;

	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model, HttpServletRequest request) {
		Tenant tenant = adminService.getCurrent().getTenant();
		model.addAttribute("types", Specification.Type.values());
		if (tenant != null) {
			model.addAttribute("productCategories", tenant.getProductCategories());
		} else {
			model.addAttribute("productCategoryTree", productCategoryService.findTree());
		}
		model.addAttribute("tenant", tenant);
		String currentProductCategoryId = WebUtils.getCookie(request, "specificationPreProductCategoryId");
		model.addAttribute("currentProductCategoryId", currentProductCategoryId);
		return "/admin/specification/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(Specification specification, Long productCategoryId, RedirectAttributes redirectAttributes) {
		Tenant tenant = adminService.getCurrent().getTenant();
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		for (Iterator iterator = specification.getSpecificationValues().iterator(); iterator.hasNext();) {
			SpecificationValue specificationValue = (SpecificationValue) iterator.next();
			if ((specificationValue == null) || (specificationValue.getName() == null)) {
				iterator.remove();
			} else {
				if (specification.getType() == Specification.Type.text) {
					specificationValue.setImage(null);
				}
				specificationValue.setSpecification(specification);
			}
		}
		specification.setTenant(tenant);
		specification.setProductCategory(productCategory);
		if (!(isValid(specification, new Class[0]))) {
			return "/admin/common/error";
		}
		specification.setProducts(null);
		this.specificationService.save(specification);
		if (adminService.getCurrent().getTenant() == null) {
			List<Tenant> tenants = tenantService.findList(productCategory);
			for (Tenant t : tenants) {
				Specification specification2 = new Specification();
				BeanUtils.copyProperties(specification, specification2, new String[] { "id", "products", "specificationValues" });
				specification2.setTenant(t);
				specification2.setProductCategory(productCategory);
				List<SpecificationValue> specificationValues = specification2.getSpecificationValues();
				for (Iterator iterator = specification.getSpecificationValues().iterator(); iterator.hasNext();) {
					SpecificationValue specificationValue = (SpecificationValue) iterator.next();
					SpecificationValue specificationValue2 = new SpecificationValue();
					BeanUtils.copyProperties(specificationValue, specificationValue2, new String[] { "id", "products" });
					if ((specificationValue == null) || (specificationValue.getName() == null)) {
						iterator.remove();
					} else {
						if (specification.getType() == Specification.Type.text) {
							specificationValue2.setImage(null);
						}
						specificationValue2.setSpecification(specification2);
					}
					specificationValue2.setProducts(new HashSet<Product>());
					specificationValues.add(specificationValue2);
				}
				specificationService.save(specification2);
			}
		}
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		Tenant tenant = adminService.getCurrent().getTenant();
		model.addAttribute("types", Specification.Type.values());
		if (tenant != null) {
			model.addAttribute("productCategories", tenant.getProductCategories());
		} else {
			model.addAttribute("productCategoryTree", productCategoryService.findTree());
		}
		model.addAttribute("tenant", tenant);
		model.addAttribute("specification", this.specificationService.find(id));
		return "/admin/specification/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(Specification specification, Long productCategoryId, RedirectAttributes redirectAttributes) {
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		for (Iterator iterator = specification.getSpecificationValues().iterator(); iterator.hasNext();) {
			SpecificationValue specificationValue = (SpecificationValue) iterator.next();
			if ((specificationValue == null) || (specificationValue.getName() == null)) {
				iterator.remove();
			} else {
				if (specification.getType() == Specification.Type.text) {
					specificationValue.setImage(null);
				}
				specificationValue.setSpecification(specification);
			}
		}
		specification.setProductCategory(productCategory);
		if (!(isValid(specification, new Class[0]))) {
			return "/admin/common/error";
		}
		this.specificationService.update(specification, new String[] { "products", "tenant" });
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Long id, Pageable pageable, ModelMap model) {
		String searchValue = null;
		try {
			searchValue = new String(pageable.getSearchValue().getBytes("ISO-8859-1"), "UTF-8");
			pageable.setSearchValue(searchValue);
		} catch (Exception localException) {
		}
		Tenant tenant = adminService.getCurrent().getTenant();
		ProductCategory productCategory = productCategoryService.find(id);
		model.addAttribute("id", id);
		Page<Specification> page = specificationService.findPage(productCategory, tenant, pageable);
		model.addAttribute("page", page);
		return "/admin/specification/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		if (ids != null) {
			for (Long id : ids) {
				Specification specification = this.specificationService.find(id);
				if ((specification != null) && (specification.getProducts() != null) && (!(specification.getProducts().isEmpty()))) {
					return Message.error("admin.specification.deleteExistProductNotAllowed", new Object[] { specification.getName() });
				}
			}
			this.specificationService.delete(ids);
		}
		return SUCCESS_MESSAGE;
	}

	/**
	 * 树结构
	 */
	@RequestMapping(value = "/tree", method = RequestMethod.GET)
	public void tree(HttpServletResponse response, Pageable pageable, ModelMap model) {
		String searchValue = null;
		try {
			searchValue = new String(pageable.getSearchValue().getBytes("ISO-8859-1"), "UTF-8");
			pageable.setSearchValue(searchValue);
		} catch (Exception localException) {
		}
		Tenant tenant = adminService.getCurrent().getTenant();
		List<ProductCategory> resultList = new ArrayList<ProductCategory>();
		if (tenant != null) { // 企业级进入

			List<ProductCategory> result = tenant.getProductCategories();
			for (ProductCategory productCategory : result) {
				resultList.add(productCategory);
				if (productCategory.getParent() != null && !resultList.contains(productCategory.getParent())) {
					resultList.add((productCategory.getParent()));
				}
			}
			responseJson(response, resultList, ProductCategory.class, new String[] { "id", "pId", "name", "open" });
		} else { // 平台用户
			List<ProductCategory> result = productCategoryService.findAll();
			responseJson(response, result, ProductCategory.class, new String[] { "id", "pId", "name", "open" });

		}
	}
}