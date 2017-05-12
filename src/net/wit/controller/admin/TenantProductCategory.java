package net.wit.controller.admin;

import java.util.List;

import javax.annotation.Resource;

import net.wit.Pageable;
import net.wit.entity.ProductCategory;
import net.wit.entity.Tenant;
import net.wit.service.ProductCategoryService;
import net.wit.service.SpecificationService;
import net.wit.service.TenantService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller("tenantProductCategory")
@RequestMapping({"/admin/tenant_product_category"})
public class TenantProductCategory extends BaseController{
	
	  @Resource(name="tenantServiceImpl")
	  private TenantService tenantService;
	  
	  @Resource(name="productCategoryServiceImpl")
	  private ProductCategoryService productCategoryService;
	  
	  @Resource(name="specificationServiceImpl")
	  private SpecificationService specificationService;

	@RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String list(Pageable pageable, ModelMap model)
	  {
		String searchValue = null;
		try {
			searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
			pageable.setSearchValue(searchValue);
		} catch (Exception localException) {
		}
	    model.addAttribute("page", this.tenantService.findPage(pageable));
	    return "/admin/tenant_product_category/list";
	  }
	
	 @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String edit(Long id, ModelMap model)
	  {
	    Tenant tenant = (Tenant)this.tenantService.find(id);
	    List<ProductCategory>  list=this.productCategoryService.findAll();
	    for(int i = 0;i<list.size();i++)
	    {
	    	 for(ProductCategory productCategory:tenant.getProductCategories())
	 	    {
	 	    	if(list.get(i).getId()==productCategory.getId())
	 	    	{
	 	    		list.get(i).setIsChoice("1");
	 	    	}
	 	    }
	    }
	   
	    model.addAttribute("tenant",tenant);
	    model.addAttribute("productCategoryList", list);
	    return "admin/tenant_product_category/edit";
	  }
	 @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String save(Long[] ids,Long tenantId,RedirectAttributes redirectAttributes) throws Exception
	  {
		  Tenant tenant = tenantService.find(tenantId);
		  List<ProductCategory> list=this.productCategoryService.findList(ids);
		  tenant.setProductCategories(list);
		  tenantService.update(tenant);
		  specificationService.initTenantSpecification(tenant);
		  addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		  return "redirect:list.jhtml";  
	  }
	  
	  
}
