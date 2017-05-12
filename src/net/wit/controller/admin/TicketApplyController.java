package net.wit.controller.admin;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.wit.Pageable;
import net.wit.entity.Tenant;
import net.wit.entity.TicketApply;
import net.wit.entity.TicketApply.ApplyStatus;
import net.wit.mobile.service.impl.PushService;
import net.wit.service.AdminService;
import net.wit.service.TicketApplyService;
import net.wit.util.BizException;
import net.wit.vo.SystemMessageVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("adminTicketApplyController")
@RequestMapping({ "admin/ticketApply" })
public class TicketApplyController {

	@Resource(name = "ticketApplyServiceImpl")
	private TicketApplyService ticketApplyService;

	@Autowired
	private PushService pushService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	/**
	 * VIP查询内购券列表
	 */
	@RequestMapping(value = { "/vip_list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String vipContent(String applyStatusParam, String applyTypeParam,
			String mobile, Pageable pageable, Model model) {

		if (null == applyTypeParam) {
			applyTypeParam = "shopkeeperApply";
		}
		
		String searchValue = null;
		Tenant tenantSearch = null;
		try {
			searchValue = new String(pageable.getSearchValue().getBytes(
					"ISO-8859-1"), "UTF-8");
			pageable.setSearchValue(searchValue);
			if (pageable.getSearchProperty().equals("tenant")) {
				pageable.setSearchProperty(null);
				tenantSearch = new Tenant();
				tenantSearch.setShortName(pageable.getSearchValue());
			}

		} catch (Exception localException) {
		}

		Tenant tenant = adminService.getCurrent().getTenant();
		model.addAttribute("applyStatusParam", applyStatusParam);
		model.addAttribute("mobile", mobile);
		model.addAttribute("tenant", tenant);

		if (tenantSearch == null) {
			List<TicketApply.ApplyStatus> applyStatus = null;
			if (null != applyStatusParam && !"".equals(applyStatusParam)) {
				applyStatus = new ArrayList<TicketApply.ApplyStatus>();
			}
			List<TicketApply.ApplyType> applyType = null;
			if (null != applyTypeParam && !"".equals(applyTypeParam)) {
				applyType = new ArrayList<TicketApply.ApplyType>();
			}
			this.convertStat(applyStatusParam, applyStatus);
			this.convertStatus(applyTypeParam, applyType);
			model.addAttribute("page", ticketApplyService.findPageByCriteria(
					tenant, applyStatus, applyType, mobile, pageable));
		}
		return "admin/ticket_apply/vip_list";

	}
	
	/**
	 * 会员查询内购券列表
	 */
	@RequestMapping(value = { "/member_list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String memberContent(String applyStatusParam, String applyTypeParam,
			String mobile, Pageable pageable, Model model) {
		
		if (null == applyTypeParam) {
			applyTypeParam = "memberApplyToTenant";
		}
		
		String searchValue = null;
		Tenant tenantSearch = null;
		try {
			searchValue = new String(pageable.getSearchValue().getBytes(
					"ISO-8859-1"), "UTF-8");
			pageable.setSearchValue(searchValue);
			if (pageable.getSearchProperty().equals("tenant")) {
				pageable.setSearchProperty(null);
				tenantSearch = new Tenant();
				tenantSearch.setShortName(pageable.getSearchValue());
			}
			
		} catch (Exception localException) {
		}
		
		Tenant tenant = adminService.getCurrent().getTenant();
		model.addAttribute("applyStatusParam", applyStatusParam);
		model.addAttribute("mobile", mobile);
		model.addAttribute("tenant", tenant);
		
		if (tenantSearch == null) {
			List<TicketApply.ApplyStatus> applyStatus = null;
			if (null != applyStatusParam && !"".equals(applyStatusParam)) {
				applyStatus = new ArrayList<TicketApply.ApplyStatus>();
			}
			List<TicketApply.ApplyType> applyType = null;
			if (null != applyTypeParam && !"".equals(applyTypeParam)) {
				applyType = new ArrayList<TicketApply.ApplyType>();
			}
			this.convertStat(applyStatusParam, applyStatus);
			this.convertStatus(applyTypeParam, applyType);
			model.addAttribute("page", ticketApplyService.findPageByCriteria(
					tenant, applyStatus, applyType, mobile, pageable));
		}
		return "admin/ticket_apply/member_list";
		
	}

	/**
	 * 同意申请内购券
	 */
	@RequestMapping(value = { "/agree" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String agree(Long ids[], String applyTypeParam) throws BizException {

		ApplyStatus applyStatus = ApplyStatus.confirmed;
		List<SystemMessageVO> systemMessageVOs = ticketApplyService.processTicketApplys(ids, applyStatus);
		for (SystemMessageVO systemMessageVO : systemMessageVOs) {
			 pushService.publishSystemMessage(systemMessageVO);
		}
		
		if ("shopkeeperApply".equals(applyTypeParam)) {
			return "redirect:/admin/ticketApply/vip_list.jhtml";
		}else{
			return "redirect:/admin/ticketApply/member_list.jhtml";
		}

	}

	/**
	 * 拒绝申请内购券
	 */
	@RequestMapping(value = { "/refuse" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String refuse(Long ids[], String applyTypeParam) throws BizException {
		
		ApplyStatus applyStatus = ApplyStatus.rejected;
		List<SystemMessageVO> systemMessageVOs = ticketApplyService.processTicketApplys(ids, applyStatus);
		for (SystemMessageVO systemMessageVO : systemMessageVOs) {
			 pushService.publishSystemMessage(systemMessageVO);
		}
		
		if ("shopkeeperApply".equals(applyTypeParam)) {
			return "redirect:/admin/ticketApply/vip_list.jhtml";
		}else{
			return "redirect:/admin/ticketApply/member_list.jhtml";
		}

	}

	/**
	 * 申请类型转换
	 */
	private void convertStatus(String applyTypeParam,
			List<TicketApply.ApplyType> applyType) {

		if (null != applyTypeParam && !"".equals(applyTypeParam)) {
			if ("shopkeeperApply".equals(applyTypeParam)) {
				applyType.add(TicketApply.ApplyType.shopkeeperApply);
			}
			if ("memberApplyToTenant".equals(applyTypeParam)) {
				applyType.add(TicketApply.ApplyType.memberApplyToTenant);
			}
		}

	}

	/**
	 * 内购券状态转换
	 */
	private void convertStat(String applyStatusParam,
			List<TicketApply.ApplyStatus> applyStatus) {

		if (null != applyStatusParam && !"".equals(applyStatusParam)) {
			if ("apply".equals(applyStatusParam)) {
				applyStatus.add(TicketApply.ApplyStatus.apply);
			}
			if ("confirmed".equals(applyStatusParam)) {
				applyStatus.add(TicketApply.ApplyStatus.confirmed);
			}
			if ("rejected".equals(applyStatusParam)) {
				applyStatus.add(TicketApply.ApplyStatus.rejected);
			}
		}

	}

}
