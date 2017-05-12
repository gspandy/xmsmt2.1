package net.wit.controller.admin;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Area;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.Location;
import net.wit.service.AdminService;
import net.wit.service.AreaService;
import net.wit.service.DeliveryCenterService;
import net.wit.service.TenantService;

@Controller("deliveryCenterController")
@RequestMapping({ "/admin/delivery_center" })
public class DeliveryCenterController extends BaseController {

	@Resource(name = "deliveryCenterServiceImpl")
	private DeliveryCenterService deliveryCenterService;

	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	@Resource(name = "tenantServiceImpl")
	private TenantService tenantService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add() {
		return "/admin/delivery_center/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(DeliveryCenter deliveryCenter, Long areaId, String locationX, String locationY, RedirectAttributes redirectAttributes) {
		if ((StringUtils.isNotEmpty(locationY)) && (StringUtils.isNotEmpty(locationX))) {
			Location location = new Location();
			BigDecimal x = new BigDecimal(locationX);
			BigDecimal y = new BigDecimal(locationY);
			location.setX(x);
			location.setY(y);
			deliveryCenter.setLocation(location);
		}
		deliveryCenter.setArea(this.areaService.find(areaId));
		if (!(isValid(deliveryCenter, new Class[0]))) {
			return "/admin/common/error";
		}
		deliveryCenter.setCommunity(null);
		deliveryCenter.setAreaName(deliveryCenter.getAreaName());
		deliveryCenter.setTenant(adminService.getCurrent().getTenant());
		try {
			this.deliveryCenterService.save(deliveryCenter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, Model model) {
		model.addAttribute("deliveryCenter", this.deliveryCenterService.find(id));
		return "/admin/delivery_center/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(DeliveryCenter deliveryCenter, Long areaId, String locationX, String locationY, RedirectAttributes redirectAttributes) {
		if ((StringUtils.isNotEmpty(locationY)) && (StringUtils.isNotEmpty(locationX))) {
			Location location = new Location();
			BigDecimal x = new BigDecimal(locationX);
			BigDecimal y = new BigDecimal(locationY);
			location.setX(x);
			location.setY(y);
			deliveryCenter.setLocation(location);
		}
		deliveryCenter.setArea(this.areaService.find(areaId));
		if (!(isValid(deliveryCenter, new Class[0]))) {
			return "/admin/common/error";
		}
		DeliveryCenter dc = this.deliveryCenterService.find(deliveryCenter.getId());
		deliveryCenter.setTenant(dc.getTenant());
		this.deliveryCenterService.update(deliveryCenter, new String[] { "areaName", "tenant" });
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Model model, Pageable pageable) {
		 String searchValue = null;
			try {
				searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
				pageable.setSearchValue(searchValue);
			} catch (Exception localException) {
			}
		model.addAttribute("page", this.deliveryCenterService.findPage(adminService.getCurrent().getTenant(), pageable));
		return "/admin/delivery_center/list";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.deliveryCenterService.delete(ids);
		return SUCCESS_MESSAGE;
	}
}