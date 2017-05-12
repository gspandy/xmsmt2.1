package net.wit.controller.admin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.wit.FileInfo;
import net.wit.Message;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.CustomService;
import net.wit.entity.DeliveryTemplate;
import net.wit.entity.Pic;
import net.wit.entity.Refunds;
import net.wit.entity.Role;
import net.wit.exception.TenantException;
import net.wit.mobile.controller.ServiceController;
import net.wit.mobile.service.impl.PushService;
import net.wit.service.AdminService;
import net.wit.service.CustomServiceService;
import net.wit.service.FileService;
import net.wit.service.PicService;
import net.wit.service.RoleService;
import net.wit.util.BizException;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("customServiceController")
@RequestMapping({"/admin/customService"})
public class CustomServiceController extends BaseController{
	
	  @Resource(name="customServiceServiceImpl")
	  private CustomServiceService customServiceService;
	
	  @Resource(name="adminServiceImpl")
	  private AdminService adminService;
	  
	  @Resource(name="fileServiceImpl")
	  private FileService fileService;
	  
	  @Resource(name="picServiceImpl")
	  private PicService picService;
	  
	  @Autowired
	  private PushService pushService;
	  
	  @Resource(name="roleServiceImpl")
	  private RoleService roleService;
	
	 @RequestMapping(value={"/serviceList"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String  confirm(Pageable pageable, ModelMap model) throws BizException, TenantException
	  {
		 String searchValue = null;
			try {
				searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
				pageable.setSearchValue(searchValue);
			} catch (Exception localException) {
			}
		 model.addAttribute("page", this.customServiceService.findPage(adminService.getCurrent().getTenant(), pageable));
		return "/admin/CustomService/serviceList";
		  
	  }
  
	 @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String add(Pageable pageable)
	  {
	    return "/admin/CustomService/serviceAdd";
	  }
	 @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String save(CustomService customService,Long adminId,String username,String password,MultipartFile file, RedirectAttributes redirectAttributes)
	  {
		 if ((file != null) && (!(file.isEmpty()))) {
		        if (!(this.fileService.isValid(FileInfo.FileType.image, file))) {
		          addFlashMessage(redirectAttributes, Message.error("无效的文件类型", new Object[0]));
		          return "redirect:add.jhtml";
		        }     
		      }
		 Admin admin=adminService.getCurrent();
		 Admin newAdmin=new Admin();
		 Pic pic = this.picService.uploadPic(admin.getTenant().getId(), "ticket_share", file);
		 if(customService.getId()==null){
			 Role role=roleService.findCustomService(admin.getTenant(), "客服");
			 if(role==null){
				 addFlashMessage(redirectAttributes, Message.error("该企业还未创建客服角色！请先创建角色！", new Object[0]));
		         return "redirect:add.jhtml";
			 }
			 Set<Role> roles=new HashSet<Role>();
			 roles.add(role);
			 newAdmin.setUsername(username);
			 newAdmin.setPassword(DigestUtils.md5Hex(password));
			 newAdmin.setTenant(admin.getTenant());
			 newAdmin.setEmail("55555@qq.com");
			 newAdmin.setIsEnabled(true);
			 newAdmin.setIsLocked(false);
			 newAdmin.setLoginFailureCount(0);
			 newAdmin.setName("客服");
			 newAdmin.setRoles(roles);
			 try{
				  adminService.save(newAdmin);
			    }catch (Exception e) {
			    	 addFlashMessage(redirectAttributes, Message.error("用户无法保存！请输入新的用户名！", new Object[0]));
			          return "redirect:add.jhtml";
			   }
			 String userId = ServiceController.SERVICE_PREFIX+newAdmin.getId();
			 String img;
			 if(pic==null){
				 img=customService.getServiceImg();
			 }else{
				 img=pic.getSmallUrl();
			 }
			 String token = null;
			 while(token==null){
				 token = pushService.getToken(userId + "", customService.getServiceName(), img);
			 }
			 customService.setToken(token);
		 }
		 else{
			 newAdmin.setId(adminId);
		 }
		 
		 if(pic!=null){
			 customService.setServiceImg(pic.getSmallUrl());
		 }
		 customService.setTenantId(admin.getTenant().getId());
		 customService.setAdmin(newAdmin);
		 customServiceService.update(customService);
	    //this.deliveryTemplateService.save(deliveryTemplate);
	    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
	    return "redirect:serviceList.jhtml";
	  }
	 @RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
		public String edit(Long id, Model model) {
			model.addAttribute("customService", this.customServiceService.find(id));
			return "/admin/CustomService/serviceEdit";
		}
	 @RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
		@ResponseBody
		public Message delete(Long[] ids) {
		    List<CustomService> list=customServiceService.findList(ids);
			this.customServiceService.delete(ids);
			for(int i=0;i<list.size();i++){
				adminService.delete(list.get(i).getAdmin().getId());
			}
			return SUCCESS_MESSAGE;
		}
}
