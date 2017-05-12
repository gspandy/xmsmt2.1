package net.wit.controller.admin;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.wit.ExcelView;
import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Coupon;
import net.wit.service.AdminService;
import net.wit.service.CouponCodeService;
import net.wit.service.CouponService;
import net.wit.util.FreemarkerUtils;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminCouponController")
@RequestMapping({"/admin/coupon"})
public class CouponController extends BaseController
{

  @Resource(name="couponServiceImpl")
  private CouponService couponService;

  @Resource(name="couponCodeServiceImpl")
  private CouponCodeService couponCodeService;

  @Resource(name="adminServiceImpl")
  private AdminService adminService;

  @RequestMapping(value={"/check_price_expression"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public boolean checkPriceExpression(String priceExpression)
  {
    if (StringUtils.isEmpty(priceExpression))
      return false;
    try
    {
      Map model = new HashMap();
      model.put("quantity", Integer.valueOf(111));
      model.put("price", new BigDecimal(9.99D));
      new BigDecimal(FreemarkerUtils.process("#{(" + priceExpression + ");M50}", model));
      return true; } catch (Exception e) {
    }
    return false;
  }

  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String add(ModelMap model)
  {
    return "/admin/coupon/add";
  }

  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String save(Coupon coupon, RedirectAttributes redirectAttributes)
  {
    if (!(isValid(coupon, new Class[0]))) {
      return "/admin/common/error";
    }
    if ((coupon.getBeginDate() != null) && (coupon.getEndDate() != null) && (coupon.getBeginDate().after(coupon.getEndDate()))) {
      return "/admin/common/error";
    }
    if ((coupon.getMinimumQuantity() != null) && (coupon.getMaximumQuantity() != null) && (coupon.getMinimumQuantity().intValue() > coupon.getMaximumQuantity().intValue())) {
      return "/admin/common/error";
    }
    if ((coupon.getMinimumPrice() != null) && (coupon.getMaximumPrice() != null) && (coupon.getMinimumPrice().compareTo(coupon.getMaximumPrice()) > 0)) {
      return "/admin/common/error";
    }
    if (StringUtils.isNotEmpty(coupon.getPriceExpression())) {
      try {
        Map model = new HashMap();
        model.put("quantity", Integer.valueOf(111));
        model.put("price", new BigDecimal(9.99D));
        new BigDecimal(FreemarkerUtils.process("#{(" + coupon.getPriceExpression() + ");M50}", model));
      } catch (Exception e) {
        return "/admin/common/error";
      }
    }
    if ((coupon.getIsExchange().booleanValue()) && (coupon.getPoint() == null)) {
      return "/admin/common/error";
    }
    if (!(coupon.getIsExchange().booleanValue())) {
      coupon.setPoint(null);
    }
    coupon.setCouponCodes(null);
    coupon.setPromotions(null);
    coupon.setOrders(null);
    this.couponService.save(coupon);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model)
  {
    model.addAttribute("coupon", this.couponService.find(id));
    return "/admin/coupon/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(Coupon coupon, RedirectAttributes redirectAttributes)
  {
    if (!(isValid(coupon, new Class[0]))) {
      return "/admin/common/error";
    }
    if ((coupon.getBeginDate() != null) && (coupon.getEndDate() != null) && (coupon.getBeginDate().after(coupon.getEndDate()))) {
      return "/admin/common/error";
    }
    if ((coupon.getMinimumQuantity() != null) && (coupon.getMaximumQuantity() != null) && (coupon.getMinimumQuantity().intValue() > coupon.getMaximumQuantity().intValue())) {
      return "/admin/common/error";
    }
    if ((coupon.getMinimumPrice() != null) && (coupon.getMaximumPrice() != null) && (coupon.getMinimumPrice().compareTo(coupon.getMaximumPrice()) > 0)) {
      return "/admin/common/error";
    }
    if (StringUtils.isNotEmpty(coupon.getPriceExpression())) {
      try {
        Map model = new HashMap();
        model.put("quantity", Integer.valueOf(111));
        model.put("price", new BigDecimal(9.99D));
        new BigDecimal(FreemarkerUtils.process("#{(" + coupon.getPriceExpression() + ");M50}", model));
      } catch (Exception e) {
        return "/admin/common/error";
      }
    }
    if ((coupon.getIsExchange().booleanValue()) && (coupon.getPoint() == null)) {
      return "/admin/common/error";
    }
    if (!(coupon.getIsExchange().booleanValue())) {
      coupon.setPoint(null);
    }
    this.couponService.update(coupon, new String[] { "couponCodes", "promotions", "orders" });
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String list(Pageable pageable, ModelMap model)
  {
	  String searchValue = null;
		try {
			searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
			pageable.setSearchValue(searchValue);
		} catch (Exception localException) {
		}
    model.addAttribute("page", this.couponService.findPage(pageable));
    return "/admin/coupon/list";
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long[] ids)
  {
    this.couponService.delete(ids);
    return SUCCESS_MESSAGE;
  }

  @RequestMapping(value={"/build"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String build(Long id, ModelMap model)
  {
    Coupon coupon = (Coupon)this.couponService.find(id);
    model.addAttribute("coupon", coupon);
    model.addAttribute("totalCount", this.couponCodeService.count(coupon, null, null, null, null));
    model.addAttribute("usedCount", this.couponCodeService.count(coupon, null, null, null, Boolean.valueOf(true)));
    return "/admin/coupon/build";
  }

  @RequestMapping(value={"/download"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public ModelAndView download(Long id, Integer count, ModelMap model)
  {
    if ((count == null) || (count.intValue() <= 0)) {
      count = Integer.valueOf(50);
    }
    Coupon coupon = (Coupon)this.couponService.find(id);
    List data = this.couponCodeService.build(coupon, null, count);
    String filename = "coupon_code_" + new SimpleDateFormat("yyyyMM").format(new Date()) + ".xls";
    String[] contents = new String[4];
    contents[0] = message("admin.coupon.type", new Object[0]) + ": " + coupon.getName();
    contents[1] = message("admin.coupon.count", new Object[0]) + ": " + count;
    contents[2] = message("admin.coupon.operator", new Object[0]) + ": " + this.adminService.getCurrentUsername();
    contents[3] = message("admin.coupon.date", new Object[0]) + ": " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    return new ModelAndView(new ExcelView(filename, null, new String[] { "code" }, new String[] { message("admin.coupon.title", new Object[0]) }, null, null, data, contents), model);
  }
}