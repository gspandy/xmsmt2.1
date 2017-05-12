package net.wit.controller.admin;

import javax.annotation.Resource;

import net.wit.Pageable;
import net.wit.service.BonusCalcService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("bonusCalcController")
@RequestMapping({"/admin/bonusCalc"})
public class BonusCalcController extends BaseController{
	@Resource(name="bonusCalcServiceImpl")
	  private BonusCalcService bonusCalcService;
	
	/**奖金详情列表
	 * @param pageable
	 * @param model
	 * @return
	 */
	@RequestMapping("/view")
	public String list(String chargeId,Pageable pageable, Model model){
		if(chargeId == null || "".equals(chargeId)){
			chargeId = "1";
		}
		model.addAttribute("page", this.bonusCalcService.findByChargeId(Long.valueOf(chargeId), pageable));
		return "admin/charge/view";
	}
}
