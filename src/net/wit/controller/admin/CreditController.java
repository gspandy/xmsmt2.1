package net.wit.controller.admin;

import java.util.Date;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import net.wit.Message;
import net.wit.Pageable;
import net.wit.Setting;
import net.wit.entity.Admin;
import net.wit.entity.Credit;
import net.wit.entity.Sn;
import net.wit.service.AdminService;
import net.wit.service.CreditService;
import net.wit.service.PayBankService;
import net.wit.service.SmsSendService;
import net.wit.service.SnService;
import net.wit.util.SettingUtils;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminCreditController")
@RequestMapping({"/admin/credit"})
public class CreditController extends BaseController
{

  @Resource(name="creditServiceImpl")
  private CreditService creditService;

  @Resource(name="adminServiceImpl")
  private AdminService adminService;

  @Resource(name="payBankServiceImpl")
  private PayBankService payBankService;

  @Resource(name="snServiceImpl")
  private SnService snService;

  @Resource(name="smsSendServiceImpl")
  private SmsSendService smsSendService;

  @RequestMapping(value={"/view"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String view(Long id, ModelMap model)
  {
    model.addAttribute("credit", this.creditService.find(id));
    return "/admin/credit/view";
  }

  @RequestMapping(value={"/success"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String success(Long id, RedirectAttributes redirectAttributes, ModelMap model)
  {
    Credit credit = (Credit)this.creditService.find(id);
    credit.setCreditDate(new Date());
    Admin admin = this.adminService.getCurrent();
    credit.setOperator(admin.getName());
    credit.setMemo("线下支付");
    credit.setStatus(Credit.Status.success);
    this.creditService.save(credit);
    model.addAttribute("credit", credit);
    return "/admin/credit/view";
  }

  @RequestMapping(value={"/paybank"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String payBank(Long id, RedirectAttributes redirectAttributes, ModelMap model)
  {
    Setting setting = SettingUtils.get();
    ResourceBundle bundle = PropertyResourceBundle.getBundle("config");

    Credit credit = (Credit)this.creditService.find(id);
    credit.setCreditDate(new Date());
    Admin admin = this.adminService.getCurrent();
    credit.setOperator(admin.getName());
    Message msg = null;
    if (Credit.Status.wait.equals(credit.getStatus())) {
      if (credit.getMemo() != null) {
        credit.setSn(this.snService.generate(Sn.Type.credit));
      }
      msg = this.payBankService.payToBank(credit, this.snService.generate(Sn.Type.epaybank), "1");
      if (credit.getStatus() == Credit.Status.failure)
        this.smsSendService.sysSend(credit.getMobile(), "您在" + setting.getSiteName() + "申请的银行汇款" + credit.getSn() + "(尾号" + credit.getAccount().substring(credit.getAccount().length() - 4, credit.getAccount().length()) + ",金额" + credit.getAccount() + "元)汇款失败，请重新提交汇款申请。【" + bundle.getString("signature") + "】");
    }
    else {
      msg = Message.error("您当前单据不在提交状态不能完成支付。", new Object[0]);
    }
    model.addAttribute("credit", credit);
    if (msg != null) {
      addFlashMessage(redirectAttributes, msg);
    }
    return "/admin/credit/view";
  }

  @RequestMapping(value={"/checkbank"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String checkBank(Long id, RedirectAttributes redirectAttributes, ModelMap model)
  {
    Setting setting = SettingUtils.get();
    Credit credit = (Credit)this.creditService.find(id);
    credit.setCreditDate(new Date());
    Admin admin = this.adminService.getCurrent();
    credit.setOperator(admin.getName());
    Message msg = null;
    if (Credit.Status.wait_success.equals(credit.getStatus())) {
      msg = this.payBankService.checkBank(credit, this.snService.generate(Sn.Type.epaybank));
      if ((credit.getStatus() == Credit.Status.failure) || (credit.getStatus() == Credit.Status.wait_failure)) {
        this.smsSendService.sysSend(credit.getMobile(), "您在" + setting.getSiteName() + "申请的银行汇款" + credit.getSn() + "(尾号" + credit.getAccount().substring(credit.getAccount().length() - 4, credit.getAccount().length()) + ",金额" + credit.getAccount() + "元)汇款失败，请重新提交汇款申请。【" + setting.getSiteName() + "】");
      }
      if (credit.getStatus() == Credit.Status.wait_failure)
        this.smsSendService.sysSend("13860431130", "付款单" + credit.getSn() + "(尾号" + credit.getAccount().substring(credit.getAccount().length() - 4, credit.getAccount().length()) + ",金额" + credit.getAccount() + "元)退款失败，请人工处理。【" + setting.getSiteName() + "】");
    }
    else {
      msg = Message.error("您当前单据不在提交银行状态不能完成检测", new Object[0]);
    }
    model.addAttribute("credit", credit);
    if (msg != null) {
      addFlashMessage(redirectAttributes, msg);
    }
    return "/admin/credit/view";
  }

  @RequestMapping(value={"/failure"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String failure(Long id, RedirectAttributes redirectAttributes, ModelMap model)
  {
    Credit credit = (Credit)this.creditService.find(id);
    credit.setCreditDate(new Date());
    Admin admin = this.adminService.getCurrent();
    credit.setOperator(admin.getName());
    Message msg = null;
    if (Credit.Status.wait_failure.equals(credit.getStatus())) {
      credit.setMemo("手工退款成功");
      credit.setStatus(Credit.Status.failure);
      try {
        this.creditService.saveAndRefunds(credit);
      } catch (Exception e) {
        return "/admin/common/error";
      }
    } else {
      msg = Message.error("您当前单据不在待退款状态，不能操作手工退款", new Object[0]);
    }
    model.addAttribute("credit", credit);
    if (msg != null) {
      addFlashMessage(redirectAttributes, msg);
    }
    return "/admin/credit/view";
  }

  @RequestMapping(value={"/cancel"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String cancel(Long id, RedirectAttributes redirectAttributes, ModelMap model)
  {
    Credit credit = (Credit)this.creditService.find(id);
    credit.setCreditDate(new Date());
    Admin admin = this.adminService.getCurrent();
    credit.setOperator(admin.getName());
    Message msg = null;
    if (Credit.Status.wait.equals(credit.getStatus())) {
      credit.setMemo("撤消退款成功");
      credit.setStatus(Credit.Status.failure);
      try {
        this.creditService.saveAndRefunds(credit);
      } catch (Exception e) {
        return "/admin/common/error";
      }
    } else {
      msg = Message.error("您当前单据不在待支付状态，不能操作手工撤消", new Object[0]);
    }
    model.addAttribute("credit", credit);
    if (msg != null) {
      addFlashMessage(redirectAttributes, msg);
    }
    return "/admin/credit/view";
  }

  @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String list(Pageable pageable, ModelMap model)
  {
	  String searchValue = null;
		try {
			searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
			pageable.setSearchValue(searchValue);
		} catch (Exception localException) {
		}
    model.addAttribute("page", this.creditService.findPage(pageable));
    return "/admin/credit/list";
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long[] ids)
  {
    if (ids != null) {
      for (Long id : ids) {
        Credit credit = (Credit)this.creditService.find(id);
        if ((credit != null) && (credit.getExpire() != null) && (!(credit.hasExpired()))) {
          return Message.error("admin.payment.deleteUnexpiredNotAllowed", new Object[0]);
        }
      }
      this.creditService.delete(ids);
    }
    return SUCCESS_MESSAGE;
  }
}