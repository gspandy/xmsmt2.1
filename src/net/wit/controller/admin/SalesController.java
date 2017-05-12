package net.wit.controller.admin;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Resource;
import net.wit.service.OrderService;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("adminSalesController")
@RequestMapping({"/admin/sales"})
public class SalesController extends BaseController
{
  private static final int MAX_SIZE = 12;

  @Resource(name="orderServiceImpl")
  private OrderService orderService;

  @RequestMapping(value={"/view"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String view(Type type, Date beginDate, Date endDate, Model model)
  {
    if (type == null) {
      type = Type.month;
    }
    if (beginDate == null) {
      beginDate = DateUtils.addMonths(new Date(), -11);
    }
    if (endDate == null) {
      endDate = new Date();
    }
    Map salesAmountMap = new LinkedHashMap();
    Map salesVolumeMap = new LinkedHashMap();
    Calendar beginCalendar = DateUtils.toCalendar(beginDate);
    Calendar endCalendar = DateUtils.toCalendar(endDate);
    int beginYear = beginCalendar.get(1);
    int endYear = endCalendar.get(1);
    int beginMonth = beginCalendar.get(2);
    int endMonth = endCalendar.get(2);
    for (int year = beginYear; year <= endYear; ++year) {
      if (salesAmountMap.size() >= 12) {
        break;
      }
      Calendar calendar = Calendar.getInstance();
      calendar.set(1, year);
      if (type == Type.year) {
        calendar.set(2, calendar.getActualMinimum(2));
        calendar.set(5, calendar.getActualMinimum(5));
        calendar.set(11, calendar.getActualMinimum(11));
        calendar.set(12, calendar.getActualMinimum(12));
        calendar.set(13, calendar.getActualMinimum(13));
        Date begin = calendar.getTime();
        calendar.set(2, calendar.getActualMaximum(2));
        calendar.set(5, calendar.getActualMaximum(5));
        calendar.set(11, calendar.getActualMaximum(11));
        calendar.set(12, calendar.getActualMaximum(12));
        calendar.set(13, calendar.getActualMaximum(13));
        Date end = calendar.getTime();
        BigDecimal salesAmount = this.orderService.getSalesAmount(begin, end);
        Integer salesVolume = this.orderService.getSalesVolume(begin, end);
        salesAmountMap.put(begin, (salesAmount != null) ? salesAmount : BigDecimal.ZERO);
        salesVolumeMap.put(begin, Integer.valueOf((salesVolume != null) ? salesVolume.intValue() : 0));
      } else {
        for (int month = (year == beginYear) ? beginMonth : calendar.getActualMinimum(2); month <= ((year == endYear) ? endMonth : calendar.getActualMaximum(2)); ++month) {
          if (salesAmountMap.size() >= 12) {
            break;
          }
          calendar.set(2, month);
          calendar.set(5, calendar.getActualMinimum(5));
          calendar.set(11, calendar.getActualMinimum(11));
          calendar.set(12, calendar.getActualMinimum(12));
          calendar.set(13, calendar.getActualMinimum(13));
          Date begin = calendar.getTime();
          calendar.set(5, calendar.getActualMaximum(5));
          calendar.set(11, calendar.getActualMaximum(11));
          calendar.set(12, calendar.getActualMaximum(12));
          calendar.set(13, calendar.getActualMaximum(13));
          Date end = calendar.getTime();
          BigDecimal salesAmount = this.orderService.getSalesAmount(begin, end);
          Integer salesVolume = this.orderService.getSalesVolume(begin, end);
          salesAmountMap.put(begin, (salesAmount != null) ? salesAmount : BigDecimal.ZERO);
          salesVolumeMap.put(begin, Integer.valueOf((salesVolume != null) ? salesVolume.intValue() : 0));
        }
      }
    }
    model.addAttribute("types", Type.values());
    model.addAttribute("type", type);
    model.addAttribute("beginDate", beginDate);
    model.addAttribute("endDate", endDate);
    model.addAttribute("salesAmountMap", salesAmountMap);
    model.addAttribute("salesVolumeMap", salesVolumeMap);
    return "/admin/sales/view";
  }

  public static enum Type
  {
    year, month;
  }
}