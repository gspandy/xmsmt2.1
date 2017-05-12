package net.wit.controller.admin;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.wit.Message;
import net.wit.entity.ProductCategoryTenant;
import net.wit.entity.Tenant;
import net.wit.service.AdminService;
import net.wit.service.MemberService;
import net.wit.service.ProductCategoryService;
import net.wit.service.ProductCategoryTenantService;
import net.wit.service.ProductService;
import net.wit.service.TagService;

/**
 * Controller - 商家商品分类
 * @author rsico Team
 * @version 3.0
 */
@Controller("adminTenantProductCategoryController")
@RequestMapping("/admin/product_category_tenant")
public class ProductCategoryTenantController extends BaseController {

	@Resource(name = "productCategoryTenantServiceImpl")
	private ProductCategoryTenantService productCategoryTenantService;

	@Resource(name = "tagServiceImpl")
	private TagService tagService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap model) {
		Tenant tenant = adminService.getCurrent().getTenant();
		model.addAttribute("productCategoryTree", productCategoryTenantService.findTree(tenant));
		return "/admin/product_category_tenant/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(ProductCategoryTenant productCategoryTenant, Long parentId, RedirectAttributes redirectAttributes) {
		Tenant tenant = adminService.getCurrent().getTenant();

		productCategoryTenant.setParent(productCategoryTenantService.find(parentId));
		productCategoryTenant.setTreePath(null);
		productCategoryTenant.setGrade(null);
		productCategoryTenant.setChildren(null);
		productCategoryTenant.setTenant(tenant);
		productCategoryTenantService.save(productCategoryTenant);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		Tenant tenant = adminService.getCurrent().getTenant();
		ProductCategoryTenant productCategoryTenant = productCategoryTenantService.find(id);
		model.addAttribute("productCategoryTree", productCategoryTenantService.findTree(tenant));
		model.addAttribute("productCategory", productCategoryTenant);
		model.addAttribute("children", productCategoryTenantService.findChildren(productCategoryTenant, tenant));
		return "/admin/product_category_tenant/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(ProductCategoryTenant productCategoryTenant, Long parentId, Long[] tagIds, Long[] brandIds, RedirectAttributes redirectAttributes) {
		Tenant tenant = adminService.getCurrent().getTenant();
		productCategoryTenant.setParent(productCategoryTenantService.find(parentId));
		if (productCategoryTenant.getParent() != null) {
			ProductCategoryTenant parent = productCategoryTenant.getParent();
			if (parent.equals(productCategoryTenant)) {
				return ERROR_VIEW;
			}
			List<ProductCategoryTenant> children = productCategoryTenantService.findChildren(parent, tenant);
			if (children != null && children.contains(parent)) {
				return ERROR_VIEW;
			}
		}
		productCategoryTenantService.update(productCategoryTenant, "treePath", "grade", "children", "parameterGroups", "attributes", "promotions", "tenant");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(ModelMap model) {
		Tenant tenant = adminService.getCurrent().getTenant();
		model.addAttribute("productCategoryTree", productCategoryTenantService.findTree(tenant));
		return "/admin/product_category_tenant/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long id) {
		ProductCategoryTenant productCategoryTenant = productCategoryTenantService.find(id);
		if (productCategoryTenant == null) {
			return ERROR_MESSAGE;
		}
		Set<ProductCategoryTenant> children = productCategoryTenant.getChildren();
		if (children != null && !children.isEmpty()) {
			return Message.error("admin.productCategory.deleteExistChildrenNotAllowed");
		}
		productCategoryTenantService.delete(id);
		return SUCCESS_MESSAGE;
	}

}