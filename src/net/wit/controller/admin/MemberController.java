package net.wit.controller.admin;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.wit.*;
import net.wit.Message;
import net.wit.entity.*;
import net.wit.service.*;
import net.wit.util.ExcelUtil;
import net.wit.util.JsonUtils;
import net.wit.util.SettingUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminMemberController")
@RequestMapping({"/admin/member"})
public class MemberController extends BaseController
{

  @Resource(name="memberServiceImpl")
  private MemberService memberService;

  @Resource(name="memberRankServiceImpl")
  private MemberRankService memberRankService;

  @Resource(name="memberAttributeServiceImpl")
  private MemberAttributeService memberAttributeService;

  @Resource(name="areaServiceImpl")
  private AreaService areaService;

  @Resource(name="tenantServiceImpl")
  private TenantService tenantService;

  @Resource(name="adminServiceImpl")
  private AdminService adminService;

  @Resource(name="tenantSmServiceImpl")
  private TenantSmService tenantSmService;
    
  @Resource(name="memberTenantServiceImpl")
  private MemberTenantService memberTenantService;

  @Resource(name="tenantSmDetailsServiceImpl")
  private TenantSmDetailsService tenantSmDetailsService;

    @RequestMapping(value={"/importData"}, method={RequestMethod.POST})
    public ModelAndView importData(MultipartFile file) throws IOException {
        String msg = "";
        ModelAndView mav = new ModelAndView("/admin/member/msg");

        String realFileName = file.getOriginalFilename();
        String fileType = ExcelUtil.getFileType(realFileName);

        if (!"xls".equals(fileType)) {
            msg = ("导入文件的格式不对，系统只支持Excel（xls文件格式）文件的导入");
            return getMav(mav, msg);
        }

        //读Excel
        List<String> titles = new ArrayList<String>();
        List<Object[]> members = new ArrayList<Object[]>();
        ExcelUtil.readExcel(file.getInputStream(), titles, members);

        String comId = ExcelUtil.getTenant().getId() + "";

        if(StringUtils.isEmpty(comId)) {
            msg = "企业ID不存在";
            return getMav(mav, msg);
        }
        Map<String, Integer> count = new HashMap<String, Integer>();
        try {

            msg = "导入结果:" + memberService.importMembers(titles, members, comId, count);
        } catch (Exception e) {
            msg = "导入异常，但成功了"+(count.get("cnt") == null ? "0" : count.get("cnt"))+"条";
            e.printStackTrace();
        }

        return getMav(mav, msg);
    }

    public ModelAndView getMav(ModelAndView mav, String msg) {
        if (mav == null) {
            return new ModelAndView();
        }
        return mav.addObject("msg", msg);
    }

    @RequestMapping(value={"/reqShopkeeper"}, method={RequestMethod.POST})
    @ResponseBody
    public Message reqShopkeeper(String memberIds, String smContentIds) throws IOException {
        String msg = "邀请失败";
        if(StringUtils.isNotEmpty(memberIds)) {
            memberIds = memberIds.substring(0, memberIds.length() - 1);

            Long tenantId = ExcelUtil.getTenant().getId();

            List<TenantSm> tenantSms = tenantSmService.getSmLeftByTenantId(tenantId);
            if (CollectionUtils.isEmpty(tenantSms)) {
                msg = "请增加短信记录";
                return Message.success(msg);
            }

            if (tenantSms.size() != 1) {
                msg = "短信记录不唯一";
                return Message.success(msg);
            }

            TenantSm tenantSm = tenantSms.get(0);
            Integer leftCnt = tenantSm.getLeftCount();
            if (leftCnt == null) {
                leftCnt = 0;
            }

            int cnt = memberIds.split(",").length;
            if (cnt > leftCnt) {
                msg = "短信剩余条数为"+leftCnt+"不足";
                return Message.success(msg);
            }
            tenantSmDetailsService.reqShopkeeper(memberIds, smContentIds,  tenantSm);
            msg = "邀请短信发送成功";
        }
        return Message.success(msg);
    }

  @RequestMapping(value={"/check_username"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public boolean checkUsername(String username)
  {
    if (StringUtils.isEmpty(username)) {
      return false;
    }

    return ((this.memberService.usernameDisabled(username)) || (this.memberService.usernameExists(username)));
  }

  @RequestMapping(value={"/check_email"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public boolean checkEmail(String previousEmail, String email)
  {
    if (StringUtils.isEmpty(email)) {
      return false;
    }

    return (!(this.memberService.emailUnique(previousEmail, email)));
  }

  @RequestMapping(value={"/view"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String view(Long id, ModelMap model)
  {
    model.addAttribute("genders", Member.Gender.values());
    model.addAttribute("memberAttributes", this.memberAttributeService.findList());
    model.addAttribute("member", this.memberService.find(id));
    return "/admin/member/view";
  }

  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String add(ModelMap model)
  {
    model.addAttribute("genders", Member.Gender.values());
    model.addAttribute("memberRanks", this.memberRankService.findAll());
    model.addAttribute("memberAttributes", this.memberAttributeService.findList());
    return "/admin/member/add";
  }

  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String save(Member member, Long memberRankId, HttpServletRequest request, RedirectAttributes redirectAttributes)
  {
    member.setMemberRank((MemberRank)this.memberRankService.find(memberRankId));
    member.removeAttributeValue();
    for (MemberAttribute memberAttribute : this.memberAttributeService.findList()) {
      String parameter = request.getParameter("memberAttribute_" + memberAttribute.getId());
      if ((memberAttribute.getType() == MemberAttribute.Type.name) || (memberAttribute.getType() == MemberAttribute.Type.address) || (memberAttribute.getType() == MemberAttribute.Type.zipCode) || (memberAttribute.getType() == MemberAttribute.Type.phone) || (memberAttribute.getType() == MemberAttribute.Type.mobile) || (memberAttribute.getType() == MemberAttribute.Type.text) || (memberAttribute.getType() == MemberAttribute.Type.select)) {
        if ((memberAttribute.getIsRequired().booleanValue()) && (StringUtils.isEmpty(parameter))) {
          return "/admin/common/error";
        }
        member.setAttributeValue(memberAttribute, parameter);
      } else if (memberAttribute.getType() == MemberAttribute.Type.gender) {
        Member.Gender gender = (StringUtils.isNotEmpty(parameter)) ? Member.Gender.valueOf(parameter) : null;
        if ((memberAttribute.getIsRequired().booleanValue()) && (gender == null)) {
          return "/admin/common/error";
        }
        member.setGender(gender); } else {
        if (memberAttribute.getType() == MemberAttribute.Type.birth)
          try {
            Date birth = (StringUtils.isNotEmpty(parameter)) ? DateUtils.parseDate(parameter, CommonAttributes.DATE_PATTERNS) : null;
            if ((memberAttribute.getIsRequired().booleanValue()) && (birth == null)) {
              return "/admin/common/error";
            }
            member.setBirth(birth);
          } catch (ParseException e) {
            return "/admin/common/error";
          }
        if (memberAttribute.getType() == MemberAttribute.Type.area) {
          Area area = (StringUtils.isNotEmpty(parameter)) ? (Area)this.areaService.find(Long.valueOf(parameter)) : null;
          if (area != null) {
            member.setArea(area);
            continue; } if (!(memberAttribute.getIsRequired().booleanValue())) continue;
          return "/admin/common/error";
        }
        if (memberAttribute.getType() == MemberAttribute.Type.checkbox) {
          String[] parameterValues = request.getParameterValues("memberAttribute_" + memberAttribute.getId());
          List options = (parameterValues != null) ? Arrays.asList(parameterValues) : null;
          if ((memberAttribute.getIsRequired().booleanValue()) && (((options == null) || (options.isEmpty())))) {
            return "/admin/common/error";
          }
          member.setAttributeValue(memberAttribute, options); }
      }
    }
    Setting setting = SettingUtils.get();
    if ((member.getUsername().length() < setting.getUsernameMinLength().intValue()) || (member.getUsername().length() > setting.getUsernameMaxLength().intValue())) {
      return "/admin/common/error";
    }
    if ((member.getPassword().length() < setting.getPasswordMinLength().intValue()) || (member.getPassword().length() > setting.getPasswordMaxLength().intValue())) {
      return "/admin/common/error";
    }
    if ((this.memberService.usernameDisabled(member.getUsername())) || (this.memberService.usernameExists(member.getUsername()))) {
      return "/admin/common/error";
    }
    if ((!(setting.getIsDuplicateEmail().booleanValue())) && (this.memberService.emailExists(member.getEmail()))) {
      return "/admin/common/error";
    }

    member.setUsername(member.getUsername().toLowerCase());
    member.setPassword(DigestUtils.md5Hex(member.getPassword()));
    member.setTenant((Tenant)this.tenantService.find(Long.valueOf(1L)));
    member.setAmount(new BigDecimal(0));
    member.setIsLocked(Boolean.valueOf(false));
    member.setLoginFailureCount(Integer.valueOf(0));
    member.setLockedDate(null);
    member.setRegisterIp(request.getRemoteAddr());
    member.setLoginIp(null);
    member.setLoginDate(null);
    member.setSafeKey(null);
    member.setCart(null);
    member.setOrders(null);
    member.setDeposits(null);
    member.setPayments(null);
    if (member.getPaymentPassword() == null) {
      member.setPaymentPassword(member.getPassword());
    }
    member.setCouponCodes(null);
    member.setReceivers(null);
    member.setReviews(null);
    member.setConsultations(null);
    member.setFavoriteProducts(null);
    member.setProductNotifies(null);
    member.setInMessages(null);
    member.setOutMessages(null);
    if (member.getBindEmail() == null) {
      member.setBindEmail(Member.BindStatus.none);
    }
    if (member.getBindMobile() == null) {
      member.setBindMobile(Member.BindStatus.none);
    }
    member.setProfitAmount(BigDecimal.ZERO);
    member.setRebateAmount(BigDecimal.ZERO);
    member.setFreezeBalance(BigDecimal.ZERO);
    member.setPrivilege(Integer.valueOf(0));
    member.setTotalScore(Long.valueOf(0L));

    if (!(isValid(member, new Class[] { BaseEntity.Save.class }))) {
      return "/admin/common/error";	
    }
    this.memberService.save(member, this.adminService.getCurrent());
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model)
  {
    model.addAttribute("genders", Member.Gender.values());
    model.addAttribute("memberRanks", this.memberRankService.findAll());
    model.addAttribute("memberAttributes", this.memberAttributeService.findList());
    model.addAttribute("member", this.memberService.find(id));
    return "/admin/member/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(Member member, Long memberRankId, Integer modifyPoint, BigDecimal modifyBalance, String depositMemo, HttpServletRequest request, RedirectAttributes redirectAttributes)
  {
    member.setMemberRank((MemberRank)this.memberRankService.find(memberRankId));
    Setting setting = SettingUtils.get();
    if ((member.getPassword() != null) && (((member.getPassword().length() < setting.getPasswordMinLength().intValue()) || (member.getPassword().length() > setting.getPasswordMaxLength().intValue())))) {
      return "/admin/common/error";
    }
    Member pMember = (Member)this.memberService.find(member.getId());
    if (pMember == null) {
      return "/admin/common/error";
    }
    member.removeAttributeValue();
    for (MemberAttribute memberAttribute : this.memberAttributeService.findList()) {
      String parameter = request.getParameter("memberAttribute_" + memberAttribute.getId());
      if ((memberAttribute.getType() == MemberAttribute.Type.name) || (memberAttribute.getType() == MemberAttribute.Type.address) || (memberAttribute.getType() == MemberAttribute.Type.zipCode) || (memberAttribute.getType() == MemberAttribute.Type.phone) || (memberAttribute.getType() == MemberAttribute.Type.text) || (memberAttribute.getType() == MemberAttribute.Type.select)) {
        member.setAttributeValue(memberAttribute, parameter);
      } else if (memberAttribute.getType() == MemberAttribute.Type.gender) {
        Member.Gender gender = (StringUtils.isNotEmpty(parameter)) ? Member.Gender.valueOf(parameter) : null;
        if ((memberAttribute.getIsRequired().booleanValue()) && (gender == null)) {
          return "/admin/common/error";
        }
        member.setGender(gender); } else {
        if (memberAttribute.getType() == MemberAttribute.Type.birth)
          try {
            Date birth = (StringUtils.isNotEmpty(parameter)) ? DateUtils.parseDate(parameter, CommonAttributes.DATE_PATTERNS) : null;
            if ((memberAttribute.getIsRequired().booleanValue()) && (birth == null)) {
              return "/admin/common/error";
            }
            member.setBirth(birth);
          } catch (ParseException e) {
            return "/admin/common/error";
          }
        if (memberAttribute.getType() == MemberAttribute.Type.area) {
          Area area = (StringUtils.isNotEmpty(parameter)) ? (Area)this.areaService.find(Long.valueOf(parameter)) : null;
          if (area != null) {
            member.setArea(area);
            continue; } if (!(memberAttribute.getIsRequired().booleanValue())) continue;
          return "/admin/common/error";
        }
        if (memberAttribute.getType() == MemberAttribute.Type.checkbox) {
          String[] parameterValues = request.getParameterValues("memberAttribute_" + memberAttribute.getId());
          List options = (parameterValues != null) ? Arrays.asList(parameterValues) : null;
          if ((memberAttribute.getIsRequired().booleanValue()) && (((options == null) || (options.isEmpty())))) {
            return "/admin/common/error";
          }
          member.setAttributeValue(memberAttribute, options); }
      }
    }
    if (StringUtils.isEmpty(member.getPassword()))
      member.setPassword(pMember.getPassword());
    else {
      member.setPassword(DigestUtils.md5Hex(member.getPassword()));
    }
    if ((pMember.getIsLocked().booleanValue()) && (!(member.getIsLocked().booleanValue()))) {
      member.setLoginFailureCount(Integer.valueOf(0));
      member.setLockedDate(null);
    } else {
      member.setIsLocked(pMember.getIsLocked());
      member.setLoginFailureCount(pMember.getLoginFailureCount());
      member.setLockedDate(pMember.getLockedDate());
    }
    member.setTenant(pMember.getTenant());
    BeanUtils.copyProperties(member, pMember, new String[] { "username", "member", "bindEmail", "bindMobile", "mobile", "email", "idcard", "point", "amount", "profitAmount", "rebateAmount", "balance", "registerIp", "loginIp", "loginDate", "safeKey", "cart", "orders", "deposits", "payments", "couponCodes", "receivers", "reviews", "consultations", 
      "favoriteProducts", "productNotifies", "inMessages", "outMessages", "totalScore", "freezeBalance", "privilege" });
    try {
      this.memberService.update(pMember, modifyPoint, modifyBalance, depositMemo, this.adminService.getCurrent());
    } catch (Exception e) {
      return "/admin/common/error";
    }
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:list.jhtml";
  }

  @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String list(Pageable pageable,Boolean isRegister, ModelMap model)
  {
	  String searchValue = null;
		try {
			searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
			pageable.setSearchValue(searchValue);
		} catch (Exception localException) {
		}
    Admin admin = this.adminService.getCurrent();
    model.addAttribute("memberRanks", this.memberRankService.findAll());
    model.addAttribute("memberAttributes", this.memberAttributeService.findAll());
    if(admin.getTenant()!=null){
    	 model.addAttribute("page", this.memberTenantService.getMemberByTenant(admin.getTenant(),pageable,isRegister));
    }else{
    	 model.addAttribute("page", this.memberService.findPage(admin.getTenant(),pageable,isRegister));
    }
    model.addAttribute("isRegister", isRegister);
    return "/admin/member/list";
  }

  @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message delete(Long[] ids)
  {
    if (ids != null) {
      for (Long id : ids) {
        Member member = (Member)this.memberService.find(id);
        if ((member != null) && (member.getBalance().compareTo(new BigDecimal(0)) > 0)) {
          return Message.error("admin.member.deleteExistDepositNotAllowed", new Object[] { member.getUsername() });
        }
      }
      this.memberService.delete(ids);
    }
    return SUCCESS_MESSAGE;
  }

  @RequestMapping(value={"/upgrade"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String upgrade(Long id)
  {
    Member member = (Member)this.memberService.find(id);
    this.memberService.upgrade(member);
    return "redirect:list.jhtml";
  }
}