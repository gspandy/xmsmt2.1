package net.wit.controller.admin;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.Returns;
import net.wit.entity.Returns.ReturnStatus;
import net.wit.exception.OrderException;
import net.wit.service.AdminService;
import net.wit.service.ReturnsService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminReturnsController")
@RequestMapping({ "/admin/returns" })
public class ReturnsController extends BaseController {

	@Resource(name = "returnsServiceImpl")
	private ReturnsService returnsService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@RequestMapping(value = { "/view" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String view(Long id, ModelMap model) {
		model.addAttribute("returns", returnsService.find(id));
		return "/admin/returns/view";
	}

	@RequestMapping(value = { "/agree" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String agree(Long id, RedirectAttributes redirectAttributes) {
		Returns returns = returnsService.find(id);
		returns.setOrderStat(ReturnStatus.agree);
		try {
			returnsService.agreeReturn(returns);
			addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		} catch (OrderException e) {
			addFlashMessage(redirectAttributes, ERROR_MESSAGE);
		}
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/reject" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String reject(Long id, RedirectAttributes redirectAttributes) {
		Returns returns = returnsService.find(id);
		returns.setOrderStat(ReturnStatus.reject);
		returnsService.reject(returns);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(ReturnStatus returnStatus, Pageable pageable, ModelMap model) {
		 String searchValue = null;
			try {
				searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
				pageable.setSearchValue(searchValue);
			} catch (Exception localException) {
			}
		Admin admin = adminService.getCurrent();
		model.addAttribute("page", returnsService.findPage(admin.getTenant(), returnStatus, pageable));
		return "/admin/returns/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.returnsService.delete(ids);
		return SUCCESS_MESSAGE;
	}
	
	/**
	 * 退货单确认收货（弃用）
	 * @param id
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = { "/returns" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String returns(Long id, RedirectAttributes redirectAttributes) {
		Returns returns = this.returnsService.find(id);
		try {
			this.returnsService.returns(returns, null);
			addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		} catch (OrderException e) {
			e.printStackTrace();
			addFlashMessage(redirectAttributes, ERROR_MESSAGE);
		}
		return "redirect:list.jhtml";
	}
	
}