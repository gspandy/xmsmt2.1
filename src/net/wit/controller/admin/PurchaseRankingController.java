package net.wit.controller.admin;

import java.util.Calendar;
import java.util.Date;
import javax.annotation.Resource;
import net.wit.service.MemberService;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("adminPurchaseRankingController")
@RequestMapping({"/admin/purchase_ranking"})
public class PurchaseRankingController extends BaseController
{
  private static final int DEFAULT_COUNT = 20;

  @Resource(name="memberServiceImpl")
  private MemberService memberService;

  @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String list(Date beginDate, Date endDate, Integer count, Model model)
  {
    Calendar calendar;
    if (beginDate != null) {
      calendar = DateUtils.toCalendar(beginDate);
      calendar.set(11, calendar.getActualMinimum(11));
      calendar.set(12, calendar.getActualMinimum(12));
      calendar.set(13, calendar.getActualMinimum(13));
      beginDate = calendar.getTime();
    }
    if (endDate != null) {
      calendar = DateUtils.toCalendar(endDate);
      calendar.set(11, calendar.getActualMaximum(11));
      calendar.set(12, calendar.getActualMaximum(12));
      calendar.set(13, calendar.getActualMaximum(13));
      endDate = calendar.getTime();
    }
    if ((count == null) || (count.intValue() <= 0)) {
      count = Integer.valueOf(20);
    }
    model.addAttribute("beginDate", beginDate);
    model.addAttribute("endDate", endDate);
    model.addAttribute("count", count);
    model.addAttribute("data", this.memberService.findPurchaseList(beginDate, endDate, count));
    return "/admin/purchase_ranking/list";
  }
}