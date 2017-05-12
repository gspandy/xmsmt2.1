package net.wit.controller.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.Converter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import net.wit.ExcelView;
import net.wit.Message;
import net.wit.Pageable;
import net.wit.SMTExcelView;
import net.wit.entity.Shipping;
import net.wit.entity.Tenant;
import net.wit.service.AdminService;
import net.wit.service.ShippingService;
import net.wit.util.DateUtil;
import net.wit.util.ExcelUtil;

@Controller("adminShippingController")
@RequestMapping({ "/admin/shipping" })
public class ShippingController extends BaseController {

	@Resource(name = "shippingServiceImpl")
	private ShippingService shippingService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@RequestMapping(value = { "/view" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String view(Long id, ModelMap model) {
		model.addAttribute("shipping", this.shippingService.find(id));
		return "/admin/shipping/view";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		 String searchValue = null;
			try {
				searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
				pageable.setSearchValue(searchValue);
			} catch (Exception localException) {
			}
		model.addAttribute("page", this.shippingService.findPage(adminService.getCurrent().getTenant(), pageable));
		return "/admin/shipping/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.shippingService.delete(ids);
		return SUCCESS_MESSAGE;
	}

	/**
	 * 批量发货
	 * @Title：importData @Description：
	 * @param file
	 * @return
	 * @throws IOException ModelAndView
	 */
	@RequestMapping(value = "/importData", method = RequestMethod.POST)
	public ModelAndView importData(MultipartFile file) throws IOException {
		String msg = "";
		ModelAndView mav = new ModelAndView("/admin/shipping/msg");
		Tenant tenant = adminService.getCurrent().getTenant();
		if (tenant == null) {
			msg = "平台用户无法发货!";
			return getMav(mav, msg);
		}

		String realFileName = file.getOriginalFilename();
		String fileType = ExcelUtil.getFileType(realFileName);

		if (!"xls".equals(fileType)) {
			msg = ("导入文件的格式不对，系统只支持Excel（xls文件格式）文件的导入");
			return getMav(mav, msg);
		}

		// 读Excel
		List<String> titles = new ArrayList<String>();
		List<Object[]> shippings = new ArrayList<Object[]>();
		ExcelUtil.readExcel(file.getInputStream(), titles, shippings);

		try {
//			msg = "导入结果:" + shippingService.importData(titles, shippings, tenant);
			msg = "导入结果:" + shippingService.importShipped(titles, shippings, tenant);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getMav(mav, msg);
	}

	private ModelAndView getMav(ModelAndView mav, String msg) {
		if (mav == null) {
			return new ModelAndView();
		}
		return mav.addObject("msg", msg);
	}

}