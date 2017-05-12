package net.wit.controller.admin;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Refunds;
import net.wit.exception.OrderException;
import net.wit.service.AdminService;
import net.wit.service.RefundsService;

@Controller("adminRefundsController")
@RequestMapping({ "/admin/refunds" })
public class RefundsController extends BaseController {

	@Resource(name = "refundsServiceImpl")
	private RefundsService refundsService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@RequestMapping(value = { "/view" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String view(Long id, ModelMap model) {
		model.addAttribute("refunds", this.refundsService.find(id));
		return "/admin/refunds/view";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		 String searchValue = null;
			try {
				searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
				pageable.setSearchValue(searchValue);
			} catch (Exception localException) {
			}
		model.addAttribute("page", this.refundsService.findPage(adminService.getCurrent().getTenant(), pageable));
		return "/admin/refunds/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.refundsService.delete(ids);
		return SUCCESS_MESSAGE;
	}

	/**
	 * 财务拒绝
	 */
	@RequestMapping(value = "/financeRefuse", method = RequestMethod.POST)
	public String financeRefuse(Long refundId, String operateMemo, RedirectAttributes redirectAttributes) {
		Refunds fRefund = refundsService.find(refundId);
		if (fRefund == null) {
			return ERROR_VIEW;
		}
		try {
			refundsService.reject(fRefund, operateMemo, adminService.getCurrentUsername());
			addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		} catch (OrderException e) {
			addFlashMessage(redirectAttributes, Message.error(e.getMessage()));
		}

		return "redirect:list.jhtml";
	}

	/**
	 * 财务通过
	 */
	@RequestMapping(value = "/financePass", method = RequestMethod.POST)
	public String financePass(Long refundId, String operateMemo, RedirectAttributes redirectAttributes) {
		Refunds fRefund = refundsService.find(refundId);
		if (fRefund == null) {
			return ERROR_VIEW;
		}
		try {
			refundsService.agree(fRefund, operateMemo, adminService.getCurrentUsername());
			addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		} catch (OrderException e) {
			addFlashMessage(redirectAttributes, Message.error(e.getMessage()));
		}
		return "redirect:list.jhtml";
	}
}