package net.wit.controller.admin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Tenant;
import net.wit.service.AdminService;
import net.wit.service.ProductService;
import net.wit.util.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller("productSaleController")
@RequestMapping({"/admin/productSale"})
public class ProductSaleController extends BaseController
{

  @Resource(name="productServiceImpl")
  private ProductService productService;
  
  @Resource(name = "adminServiceImpl")
	private AdminService adminService;

  @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String list(Pageable pageable, Model model,Date pageDate)
  {
	  Tenant tenant = adminService.getCurrent().getTenant();
	  Date endDate = new Date();
	  Date beginDate = pageDate;
	  Calendar cal = Calendar.getInstance();
	  if(beginDate != null){
		  cal.setTime(beginDate);
		  endDate = DateUtil.setLastDayOfMonth(cal);
	  }else{
		  beginDate = DateUtil.setBeginDayOfMonth(cal);
		  endDate = DateUtil.setLastDayOfMonth(cal);
	  }
	  Page<Object[]> page = productService.findSalesPage(pageable, beginDate, endDate, tenant);
	  List<Object[]> list = page.getContent();
	  List<Object[]> newlist = new ArrayList<Object[]>();
	  
	  if(list != null){
		  for(int i=0;i<list.size();i++){
			  Object[] ob = list.get(i);
			  String fullName = (String) ob[1];
			  String specifications = "";
			  try {
				  if(StringUtils.isNotEmpty(fullName)){
					  specifications = fullName.substring(fullName.indexOf("[")+1, fullName.indexOf("]"));
				  }
				 
			} catch (Exception e) {
				// TODO: handle exception
			}
			  ob[1] = specifications;
			  newlist.add(ob);
		  }
	  }
	  model.addAttribute("page", new Page<Object[]>(newlist, page.getTotal(), page.getPageable()));
	  return "/admin/product_sell/list";
  }
}