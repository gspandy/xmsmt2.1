package net.wit.controller.admin;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Member;
import net.wit.entity.Order;
import net.wit.entity.Tenant;
import net.wit.entity.VipReport;
import net.wit.service.AdminService;
import net.wit.service.MemberService;
import net.wit.service.OrderService;
import net.wit.service.VipReportService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * vip(店主)统计报表
 * @author Administrator
 */
@Controller("vipReportController")
@RequestMapping({ "/admin/vipReport" })
public class VipReportController extends BaseController {
	
	@Resource(name = "vipReportServiceImpl")
	private VipReportService vipReportService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;
	
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	
	@Resource(name = "orderServiceImpl")
	private OrderService orderService;
	
	/**
	 * Vip 销售量统计列表
	 * @param pageable
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/volumeList" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String volumeList(Pageable pageable, ModelMap model, Date beginDate, Date endDate) {
		try {
			Tenant tenant = adminService.getCurrent().getTenant();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String strBeginDate = "";
			String strEndDate = "";
			if (beginDate != null && !"".equals(beginDate)) {
				strBeginDate = formatter.format(beginDate);
			}
			if (endDate != null && !"".equals(endDate)) {
				strEndDate = formatter.format(endDate);
			}
			Page<VipReport> page = this.vipReportService.findVolumePageTenant(tenant, 
					beginDate, endDate, pageable);
			model.addAttribute("beginDate", strBeginDate);
			model.addAttribute("endDate", strEndDate);
			model.addAttribute("tenant", tenant);
			model.addAttribute("page", page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/admin/vip_report/volumeList";
	}
	
	/**
	 * Vip 销售额统计列表
	 * @param pageable
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/amountList" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String amountList(Pageable pageable, ModelMap model, Date beginDate, Date endDate) {
		try {
			Tenant tenant = adminService.getCurrent().getTenant();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String strBeginDate = "";
			String strEndDate = "";
			if (beginDate != null && !"".equals(beginDate)) {
				strBeginDate = formatter.format(beginDate);
			}
			if (endDate != null && !"".equals(endDate)) {
				strEndDate = formatter.format(endDate);
			}
			
			Page<VipReport> page = this.vipReportService.findAmountPageTenant(tenant, beginDate, endDate, pageable);
			
			model.addAttribute("beginDate", strBeginDate);
			model.addAttribute("endDate", strEndDate);
			model.addAttribute("tenant", tenant);
			model.addAttribute("page", page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/admin/vip_report/amountList";
	}

	/**
	 * 查看订单明细
	 * @param id vipId（店主ID）
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/volumeView" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String volumeView(Long id, ModelMap model, Pageable pageable, Date beginDate, Date endDate) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String strBeginDate = "";
			String strEndDate = "";
			if (beginDate != null && !"".equals(beginDate)) {
				strBeginDate = formatter.format(beginDate);
			}
			if (endDate != null && !"".equals(endDate)) {
				strEndDate = formatter.format(endDate);
			}
			
			Member vipMember = this.memberService.find(id);
			Page<Order> page = orderService.findPage(vipMember, beginDate, endDate, pageable);
			model.addAttribute("member", vipMember);
			model.addAttribute("beginDate", strBeginDate);
			model.addAttribute("endDate", strEndDate);
			model.addAttribute("page", page);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return "/admin/vip_report/volumeView";
	}
	
	/**
	 * 查看订单明细
	 * @param id vipId（店主ID）
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/amountView" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String amountView(Long id, ModelMap model, Pageable pageable, Date beginDate, Date endDate) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String strBeginDate = "";
			String strEndDate = "";
			if (beginDate != null && !"".equals(beginDate)) {
				strBeginDate = formatter.format(beginDate);
			}
			if (endDate != null && !"".equals(endDate)) {
				strEndDate = formatter.format(endDate);
			}
			
			Member vipMember = this.memberService.find(id);
			Page<Order> page = orderService.findPage(vipMember, beginDate, endDate, pageable);
			model.addAttribute("member", vipMember);
			model.addAttribute("beginDate", strBeginDate);
			model.addAttribute("endDate", strEndDate);
			model.addAttribute("page", page);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return "/admin/vip_report/amountView";
	}

//	/**
//	 * 批量导出
//	 * @Title：exportExcel @Description：
//	 * @param ids
//	 * @param model
//	 * @return ModelAndView
//	 */
//	@RequestMapping(value = "/exportExcel", method = RequestMethod.POST)
//	public ModelAndView exportExcel(Long[] ids, HttpServletRequest request,
//			HttpServletResponse response) {
//		List<Order> orders = orderService.findList(ids);
//		List<Order> data = new ArrayList<Order>();
//		Tenant tenant = adminService.getCurrent().getTenant();
//		for (Order order : orders) {
//			if (order.getTenant().equals(tenant)) {
//				data.add(order);
//			}
//		}
//		return new ModelAndView(new SMTExcelView("订单"
//				+ DateUtil.changeDateToStr(new Date(),
//						DateUtil.DB_STORE_DATE_FULL) + ".xls", data));
//	}
	
}