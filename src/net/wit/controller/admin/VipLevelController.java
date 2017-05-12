package net.wit.controller.admin;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.entity.Tenant;
import net.wit.entity.VipLevel;
import net.wit.service.AdminService;
import net.wit.service.TenantShopkeeperService;
import net.wit.service.VipLevelService;
import net.wit.util.BizException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 
 */
@Controller("vipLevelController")
@RequestMapping("/admin/vipLevel")
public class VipLevelController extends BaseController {

	@Resource(name = "vipLevelServiceImpl")
	private VipLevelService vipLevelService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;
	
	@Resource(name = "tenantShopkeeperServiceImpl")
	private TenantShopkeeperService tenantShopkeeperService;

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(ModelMap model,RedirectAttributes redirectAttributes) {
		Tenant tenant = adminService.getCurrent().getTenant();
		if(tenant == null){
			return "/admin/vipmanage/list";
		}
		model.addAttribute("vipLevel", vipLevelService.getVipLevelByTenant(tenant));
		return "/admin/vipmanage/list";
	}
	
	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model) {
		return "/admin/vipmanage/add";
	}
	
	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(VipLevel vipLevel, RedirectAttributes redirectAttributes) {
		Tenant tenant = adminService.getCurrent().getTenant();
		VipLevel lastVipLevel = vipLevelService.getLastLevel(tenant);
		if(lastVipLevel != null && !vipLevel.getLevel().equals(lastVipLevel.getLevel() + 1)){
			addFlashMessage(redirectAttributes,Message.error("只能逐级添加", new Object[0]));
			return "redirect:add.jhtml";
		}
//		VipLevel exist =  vipLevelService.getVipLevelByLevel(tenant, vipLevel.getLevel());
//		if(exist != null){
//			addFlashMessage(redirectAttributes,Message.error("已存在的等级，不能重复添加", new Object[0]));
//			return "redirect:add.jhtml";
//		}
		vipLevel.setTenant(tenant);
		vipLevel.setLevelName(vipLevel.getLevelName()+vipLevel.getLevel());
		if(vipLevel.getLevel() == 1){
			vipLevel.setIsDefault(true);
		}else{
			vipLevel.setIsDefault(false);
		}
		try {
			vipLevelService.creatNew(vipLevel);
		} catch (BizException e) {
			addFlashMessage(redirectAttributes,Message.error(e.getMessage(), new Object[0]));
			return "redirect:add.jhtml";
		}
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}
	
	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		VipLevel vipLevel = vipLevelService.find(id);
		model.addAttribute("vipLevel", vipLevel);
		return "/admin/vipmanage/edit";
	}
	
	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(VipLevel vipLevel, RedirectAttributes redirectAttributes) {
		VipLevel level = vipLevelService.find(vipLevel.getId());
		level.setBonusLevel(vipLevel.getBonusLevel());
		
		level.setTicketNum(vipLevel.getTicketNum());
		if(!vipLevel.getInviteCondition().equals(level.getInviteCondition())){
			level.setInviteCondition(vipLevel.getInviteCondition());
			try {
				vipLevelService.updateLevel(level);
			} catch (BizException e) {
				addFlashMessage(redirectAttributes,Message.error(e.getMessage(), new Object[0]));
				return "redirect:edit.jhtml";
			}
		}else{
			level.setInviteCondition(vipLevel.getInviteCondition());
			vipLevelService.update(level);
		}
		
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}
	
	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long id) {
		VipLevel vipLevel = vipLevelService.find(id);
		if (vipLevel == null) {
			return ERROR_MESSAGE;
		}
		if(vipLevel.getIsDefault()){
			return Message.error("默认等级不能删除", new Object[0]);
		}
		Tenant tenant = adminService.getCurrent().getTenant();
		VipLevel lastVipLevel = vipLevelService.getLastLevel(tenant);
		if(!vipLevel.getLevel().equals(lastVipLevel.getLevel())){
			return Message.error("只能从最高等级开始删除", new Object[0]);
		}
		Long count = tenantShopkeeperService.countByVipLevel(vipLevel);
		if(count > 0){
			return Message.error("存在该店长等级，无法删除", new Object[0]);
		}
		vipLevelService.delete(id);
		return SUCCESS_MESSAGE;
	}

}