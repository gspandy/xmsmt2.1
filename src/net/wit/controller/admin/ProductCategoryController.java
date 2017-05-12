package net.wit.controller.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.wit.Filter;
import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.ProductCategory;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.service.AdminService;
import net.wit.service.BrandService;
import net.wit.service.ProductCategoryService;
import net.wit.service.TagService;

@Controller("adminProductCategoryController")
@RequestMapping({ "/admin/product_category" })
public class ProductCategoryController extends BaseController {

	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;

	@Resource(name = "brandServiceImpl")
	private BrandService brandService;

	@Resource(name = "tagServiceImpl")
	private TagService tagService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model) {
		model.addAttribute("productCategoryTree", this.productCategoryService.findTree());
		model.addAttribute("brands", this.brandService.findList(adminService.getCurrent().getTenant()));
		model.addAttribute("tags", this.tagService.findList(Tag.Type.productCategory));
		return "/admin/product_category/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(ProductCategory productCategory, Long parentId, Long[] tagIds, Long[] brandIds, RedirectAttributes redirectAttributes) {
		productCategory.setParent((ProductCategory) this.productCategoryService.find(parentId));
		productCategory.setBrands(new HashSet(this.brandService.findList(brandIds)));
		if (!(isValid(productCategory, new Class[0]))) {
			return "/admin/common/error";
		}
		productCategory.setTags(new HashSet(this.tagService.findList(tagIds)));
		productCategory.setTreePath(null);
		productCategory.setGrade(null);
		productCategory.setChildren(null);
		productCategory.setProducts(null);
		productCategory.setParameterGroups(null);
		productCategory.setAttributes(null);
		productCategory.setPromotions(null);
		this.productCategoryService.save(productCategory);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		ProductCategory productCategory = (ProductCategory) this.productCategoryService.find(id);
		model.addAttribute("productCategoryTree", this.productCategoryService.findTree());
		model.addAttribute("brands", this.brandService.findList(adminService.getCurrent().getTenant()));
		model.addAttribute("productCategory", productCategory);
		model.addAttribute("children", this.productCategoryService.findChildren(productCategory));
		model.addAttribute("tags", this.tagService.findList(Tag.Type.productCategory));
		return "/admin/product_category/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(ProductCategory productCategory, Long parentId, Long[] tagIds, Long[] brandIds, RedirectAttributes redirectAttributes) {
		productCategory.setParent((ProductCategory) this.productCategoryService.find(parentId));
		productCategory.setBrands(new HashSet(this.brandService.findList(brandIds)));
		productCategory.setTags(new HashSet(this.tagService.findList(tagIds)));
		if (!(isValid(productCategory, new Class[0]))) {
			return "/admin/common/error";
		}
		if (productCategory.getParent() != null) {
			ProductCategory parent = productCategory.getParent();
			if (parent.equals(productCategory)) {
				return "/admin/common/error";
			}
			List children = this.productCategoryService.findChildren(parent);
			if ((children != null) && (children.contains(parent))) {
				return "/admin/common/error";
			}
		}
		this.productCategoryService.update(productCategory, new String[] { "treePath", "grade", "children", "products", "parameterGroups", "attributes", "promotions" });
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(ModelMap model) {
		model.addAttribute("productCategoryTree", this.productCategoryService.findTree());
		return "/admin/product_category/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long id) {
		ProductCategory productCategory = (ProductCategory) this.productCategoryService.find(id);
		if (productCategory == null) {
			return ERROR_MESSAGE;
		}
		Set children = productCategory.getChildren();
		if ((children != null) && (!(children.isEmpty()))) {
			return Message.error("admin.productCategory.deleteExistChildrenNotAllowed", new Object[0]);
		}
		Set products = productCategory.getProducts();
		if ((products != null) && (!(products.isEmpty()))) {
			return Message.error("admin.productCategory.deleteExistProductNotAllowed", new Object[0]);
		}
		this.productCategoryService.delete(id);
		return SUCCESS_MESSAGE;
	}

	@RequestMapping(value = { "/search" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public List<ProductCategory> search(String name) {
		List filters = new ArrayList();
		int limit = 10000;
		if (StringUtils.isNotBlank(name)) {
			filters.add(new Filter("name", Filter.Operator.like, "%" + name + "%"));
			limit = 100;
		}
		return this.productCategoryService.findList(Integer.valueOf(limit), filters, null);
	}
	
}