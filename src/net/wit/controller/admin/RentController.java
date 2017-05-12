package net.wit.controller.admin;

import javax.annotation.Resource;

import net.wit.Pageable;
import net.wit.entity.Rent.Status;
import net.wit.service.RentService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("adminRentController")
@RequestMapping({"/admin/rent_list"})
public class RentController extends BaseController{

	  @Resource(name="rentServiceImpl")
	  private RentService rentService;
	  
	  /**查询租金管理列表
	 * @param memberId 店主ID
	 * @param rentDate 租金月份
	 * @param status 状态
	 * @param pageable 分页
	 * @param model
	 * @return
	 */
	  @RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	  public String list(Long memberId,String rentDate,Status status,Pageable pageable,Model model){
		  model.addAttribute("page", this.rentService.findPage(memberId, rentDate, status, pageable));
		  return "admin/rent/list";
	  }
}
