package net.wit.mobile.controller;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javassist.bytecode.Descriptor.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.wit.entity.Member;
import net.wit.entity.MemberBank;
import net.wit.entity.Pic;
import net.wit.mobile.bo.NToken;
import net.wit.mobile.service.INTokenBS;
import net.wit.plugin.PaymentPlugin;
import net.wit.plugin.unionpay.UnionpayPlugin;
import net.wit.service.MemberBankService;
import net.wit.service.MemberService;
import net.wit.service.PicService;
import net.wit.service.PluginService;
import net.wit.service.YeePayService;
import net.wit.service.impl.ZGTService;
import net.wit.util.CacheUtil;
import net.wit.util.ExcelUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.ld.slf4j.Logger;
import cn.ld.slf4j.LoggerFactory;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-13
 * Time: 下午1:56
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/account")
public class AccountController extends BaseController {
	
	private Logger log = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private INTokenBS inTokenBS;

    @Autowired
    private MemberService memberService;
    
    @Autowired
    private YeePayService yeePayService;

    @Autowired
    private PicService picService;
    
    @Autowired
    private PluginService pluginService;

    @Autowired
    private MemberBankService memberBankService;

    @RequestMapping(value = "/uploadImage")
        public void uploadImage(HttpServletResponse response,
                          @RequestParam("token") String token, @RequestParam("file") MultipartFile file) throws Exception{
            try {
                if (!inTokenBS.isVaild(token)) {
                    this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));

                } else {
                    NToken nToken = inTokenBS.get(token);
                    Pic pic = picService.uploadImage(nToken, file);
                    JSONObject resultValue = new JSONObject();
                    String url = "";
                    if (pic != null) {
                          url = pic.getSmallUrl();
                    }
                    resultValue.put("picId", pic.getId()+"");
                    resultValue.put("imageUrl", url);
                    this.handleJsonResponse(response, true, "", resultValue);
                }


            } catch (Exception e) {
                this.handleJsonResponse(response, false, e.getMessage());
                e.printStackTrace();
            }
        }

    @RequestMapping(value = "/getBankInfo")
    public void getBankInfo(HttpServletResponse response
    		,@RequestParam("token") String token
                      ) throws Exception{
        try {
            if (!inTokenBS.isVaild(token)) {
                this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));

            } else {
                NToken nToken = inTokenBS.get(token);
                JSONObject resultValue = new JSONObject();
                String memberId = nToken.getMemberId();
                Member member=new Member();
                member.setId(new Long(memberId));
                MemberBank memberBank=memberBankService.findMember(member);
                if(memberBank!=null){
                	resultValue.put("cardNo", memberBank.getCardNo());
                    resultValue.put("depositBank", memberBank.getDepositBank());
                    resultValue.put("depositUser", memberBank.getDepositUser());
                    resultValue.put("bankId", memberBank.getId());
                    this.handleJsonResponse(response, true, "", resultValue);
                }else{
                	this.handleJsonResponse(response, false, "您没有添加银行卡");
                }
                
            }
        } catch (Exception e) {
            this.handleJsonResponse(response, false, "银行卡信息获取失败");
            e.printStackTrace();
        }
    }
    @RequestMapping(value = "/getMeInfo")
    public void getMeInfo(HttpServletResponse response,
    		@RequestParam("token") String token) throws Exception{
    	try {
    		if (!inTokenBS.isVaild(token)) {
    			this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));

    		} else {
    			NToken nToken = inTokenBS.get(token);
    			String memberId = nToken.getMemberId();
    			JSONObject resultValue = new JSONObject();
    			Member member = memberService.find(new Long(memberId));

    			resultValue.put("tel", ExcelUtil.convertNull(member.getMobile()));
    			resultValue.put("sex", ExcelUtil.convertNull(member.getGender().name()));
    			resultValue.put("realName", ExcelUtil.convertNull(member.getName()));
    			resultValue.put("nickName", ExcelUtil.convertNull(member.getNickName()));
    			resultValue.put("photo", ExcelUtil.convertNull(member.getHeadImg()));//todo

    			this.handleJsonResponse(response, true, "", resultValue);
    		}
    	} catch (Exception e) {
    		this.handleJsonResponse(response, false, e.getMessage());
    		e.printStackTrace();
    	}
    }
    
    /**
     * 绑定银行卡(注册易宝子账户)
     */
    @RequestMapping(value = "/registerAccount")
    public void registerAccount(HttpServletResponse response,
    		@RequestParam("name") String name, @RequestParam("idcard") String idcard,
    		@RequestParam("bankname") String bankname, @RequestParam("bankaccountnumber") String bankaccountnumber,
    		@RequestParam("bankprovince") String bankprovince,@RequestParam("bankcity") String bankcity,
    		@RequestParam("token") String token
    		) throws Exception {
    	try {
    		log.info("请求参数为："
    				+ "[name:" + name + ",idcard:" + idcard + ",bankname:" + bankname + ",bankaccountnumber:" 
    				+ bankaccountnumber + ",bankprovince:" + bankprovince + ",bankcity:" + bankcity +"]");
    		
            if (!inTokenBS.isVaild(token)) {
                this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
            } else {
                NToken nToken = inTokenBS.get(token);
                String memberId = nToken.getMemberId();
    			// 客户端传来的数据
                Map<String, String> params = new HashMap<String, String>();
                params.put("memberId", memberId);// 会员ID
                params.put("customertype", "PERSON");// 会员类别-个人
            	params.put("name", name);// 真实姓名
            	params.put("idcard", idcard);// 身份证号
            	params.put("bankname", bankname);// 开户银行
            	params.put("bankaccountnumber", bankaccountnumber);// 银行卡号（储蓄卡）
            	params.put("bankprovince", bankprovince);// 省份
            	params.put("bankcity", bankcity);// 城市
            	
            	yeePayService.registerAccount(params);
                this.handleJsonResponse(response, true, "银行卡绑定成功");
            }
        } catch (Exception e) {
            this.handleJsonResponse(response, false, e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 绑定和修改支付宝
     */
    @RequestMapping(value = "/registerZhiFuBao")
    public void registerZhiFuBao(HttpServletResponse response, @RequestParam("zhiFuBaoNo") String zhiFuBaoNo,
    		@RequestParam("depositUser") String depositUser,@RequestParam("token") String token
             ) throws Exception {
    	try {
    		log.info("请求参数为："
    				+ "zhiFuBao_no:" + zhiFuBaoNo + ",deposit_user:" + depositUser +"]");
    		
           if (!inTokenBS.isVaild(token)) {
                this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
            } else {
                NToken nToken = inTokenBS.get(token);
                String memberId = nToken.getMemberId();
                Member member=new Member();
                member.setId(new Long(memberId));
                MemberBank memberBank=null;
                memberBank=memberBankService.findMember(member);
                if (memberBank != null) {
                	memberBank.setCardNo(zhiFuBaoNo);
                	memberBank.setDepositUser(depositUser);
             		this.memberBankService.update(memberBank);
             		this.handleJsonResponse(response, true, "支付宝修改成功");
             	} else {
             		memberBank=new MemberBank();
             		memberBank.setMember(member);
             		memberBank.setCardNo(zhiFuBaoNo);
                	memberBank.setDepositUser(depositUser);
        			memberBank.setType(MemberBank.Type.debit);// 保存为借记卡
        			memberBank.setValidity(new Date());
        			this.memberBankService.save(memberBank);
        			this.handleJsonResponse(response, true, "支付宝绑定成功");
             	}
                
           }
        } catch (Exception e) {
            this.handleJsonResponse(response, false, e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 更换银行卡(修改易宝子账户)
     */
    @RequestMapping(value = "/updateAccount")
    public void updateAccount(HttpServletResponse response,
    		@RequestParam("bankname") String bankname, @RequestParam("bankaccountnumber") String bankaccountnumber,
    		@RequestParam("bankprovince") String bankprovince,@RequestParam("bankcity") String bankcity,@RequestParam("name") String name,
    		@RequestParam("token") String token) throws Exception {
    	try {
            if (!inTokenBS.isVaild(token)) {
                this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
            } else {
                NToken nToken = inTokenBS.get(token);
                String memberId = nToken.getMemberId();
                // 参数校验
            	Map<String, String> params = new HashMap<String, String>();
                PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin("yeepayPlugin");
                String accountNotify = paymentPlugin.getAttribute("accountNotify");
            	params.put("callbackurl", accountNotify);// 后台通知地址
            	params.put("memberId", memberId);
            	params.put("accountname", name);
            	params.put("bankprovince", bankprovince);
            	params.put("bankcity", bankcity);
            	params.put("customertype", "PERSON");
            	params.put("bankname", bankname);// 开户银行
            	params.put("bankaccountnumber", bankaccountnumber);// 银行卡号（储蓄卡）
            	yeePayService.updateAccount(params);
                this.handleJsonResponse(response, true, "银行卡绑定成功");
            }
        } catch (Exception e) {
            this.handleJsonResponse(response, false, e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 修改银行卡成功的通知URL
     */
    @RequestMapping(value = "/accountNotify")
    public void accountNotify(HttpServletResponse response,HttpServletRequest request) throws Exception {
    	try {
    		String data				   = request.getParameter("data");
    		Map<String, String> result = ZGTService.decryptPayCallbackData(data);
    		UnionpayPlugin unionpayPlugin=new UnionpayPlugin();
    		String code = result.get("code");// 返回码
    		String requestid = result.get("requestid");// 返回码
    		
    			if(unionpayPlugin.BankVerifyNotify(null, request)){
    				if ("1".equals(code)) {// 修改成功
    				  MemberBank memberBank =	memberBankService.findBank(requestid);
    				  memberBank.setRequestId(null);
    				  memberBankService.delete(memberBank.getBankId());
    				  memberBankService.update(memberBank);
    				} else {// 修改失败
    				  MemberBank memberBank =	memberBankService.findBank(requestid);
    				  memberBankService.delete(memberBank);
    	    		}
    			}
    		
        } catch (Exception e) {
//            this.handleJsonResponse(response, false, e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * 提现管理首页
     * 判断是否已经绑定银行卡和设置提现密码
     * @param response
     * @param token
     * @throws Exception
     */
    @RequestMapping(value = "/cashManage")
    public void cashManage(HttpServletResponse response,
    		@RequestParam("token") String token) throws Exception {
    	try {
            if (!inTokenBS.isVaild(token)) {
                this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
            } else {
            	String bankFlag = null;
            	String bankaccountnumber = null;
            	String pwdFlag = null;
                NToken nToken = inTokenBS.get(token);
                String memberId = nToken.getMemberId();
                
                JSONObject resultValue = new JSONObject();
                Member member = this.memberService.find(new Long(memberId));
                Set<MemberBank> memberBanks = member.getMemberBanks();
                MemberBank memberBank = null;
                for (MemberBank bank:memberBanks) {
                	memberBank = bank;
                }
                if (memberBank != null) {
                	bankFlag = "1";
                	//String str2=memberBank.getCardNo().substring(memberBank.getCardNo().length()-4,memberBank.getCardNo().length());
                	bankaccountnumber = "已绑定支付宝";
                	if (member.getCashPwd()!=null&&!(member.getCashPwd().equals(""))) {// 已绑定提现密码
                		pwdFlag = "1";
                	}
                	else{
                		pwdFlag = "0";
                	}
                } else {
                	bankFlag = "0";
                	pwdFlag = "0";
                }
            	resultValue.put("bankFlag", ExcelUtil.convertNull(bankFlag));// 是否已绑定银行卡：0-未绑定；1-已绑定
            	resultValue.put("bankaccountnumber", ExcelUtil.convertNull(bankaccountnumber));// 绑定的银行卡尾号
            	resultValue.put("pwdFlag", ExcelUtil.convertNull(pwdFlag));// 是否设置提现密码：0-未设置；1-已设置
                this.handleJsonResponse(response, true, "",resultValue);
            }
        } catch (Exception e) {
            this.handleJsonResponse(response, false, e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * 设置提现密码
     * @param response
     * @param familyType
     * @param familyName
     * @param cashPwd
     * @param token
     * @throws Exception
     */
      @RequestMapping(value = "/optionsCashPwd")
      public void optionsCashPwd(HttpServletResponse response,@RequestParam("familyType")String familyType,
      		@RequestParam("familyName")String familyName,@RequestParam("cashPwd")String cashPwd
      		,@RequestParam("token") String token
      		) throws Exception{
      	try {
      		if (!inTokenBS.isVaild(token)) {
      			this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
  
      		} else {
      			NToken nToken = inTokenBS.get(token);
      			String memberId = nToken.getMemberId();
      			JSONObject resultValue = new JSONObject();
      			Member member = memberService.find(new Long(memberId));
      			member.setFamilyType(familyType);
      			member.setFamilyName(familyName);
      			member.setCashPwd(cashPwd);
      			memberService.update(member);
      			this.handleJsonResponse(response, true, "提现密码设置成功");
      		}
      	} catch (Exception e) {
      		this.handleJsonResponse(response, false, "提现密码设置失败");
      		e.printStackTrace();
      	}
      }
      /**
       * 
       * 修改提现密码
       * @param response
       * @param oldCashPwd
       * @param newCashPwd
       * @param token
       * @throws Exception
       */
      @RequestMapping(value = "/updateCashPwd")
      public void updateCashPwd(HttpServletResponse response,@RequestParam("oldCashPwd")String oldCashPwd,
      		@RequestParam("newCashPwd")String newCashPwd
      		,@RequestParam("token") String token
      		) throws Exception{
      	try {
      		if (!inTokenBS.isVaild(token)) {
      			this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
  
      		} else {
      			NToken nToken = inTokenBS.get(token);
      			String memberId = nToken.getMemberId();
      			JSONObject resultValue = new JSONObject();
      			Member member = memberService.find(new Long(memberId));
      			if(member.getCashPwd().equals(oldCashPwd))
      			{
      				member.setCashPwd(newCashPwd);
      				memberService.update(member);
      				this.handleJsonResponse(response, true, "提现密码修改成功");
      			}else{
      				this.handleJsonResponse(response, false, "当前提现密码错误请重新输入");		
      			}
      			
      		}
      	} catch (Exception e) {
      		this.handleJsonResponse(response, false, "提现密码修改失败");
      		e.printStackTrace();
      	}
      }
      /**
       * 
       * 忘记提现密码修改
       * @param response
       * @param familyName
       * @param newCashPwd
       * @param token
       * @throws Exception
       */
      @RequestMapping(value = "/forgetCashPwd")
      public void forgetCashPwd(HttpServletResponse response,
      		@RequestParam("familyName")String familyName,
      		@RequestParam("newCashPwd")String newCashPwd
      		,@RequestParam("token") String token
      		) throws Exception{
      	try {
      		if (!inTokenBS.isVaild(token)) {
      			this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
  
      		} else {
      			NToken nToken = inTokenBS.get(token);
      			String memberId = nToken.getMemberId();
      			JSONObject resultValue = new JSONObject();
      			Member member = memberService.find(new Long(memberId));
      			if(member.getFamilyName().equals(familyName))
      			{
      				member.setCashPwd(newCashPwd);
      				memberService.update(member);
      				this.handleJsonResponse(response, true, "提现密码修改成功");
      			}else{
      				this.handleJsonResponse(response, false, "当前提现密码错误请重新输入");		
      			}
      			
      		}
      	} catch (Exception e) {
      		this.handleJsonResponse(response, false, "提现密码修改失败");
      		e.printStackTrace();
      	}
      }
      /**
       * 
       * 修改用户信息
       * @param response
       * @param familyName
       * @param newCashPwd
       * @param token
       * @throws Exception
       */
      @RequestMapping(value = "/updateMember")
      public void updateMember(HttpServletResponse response,
      		@RequestParam("nickName")String nickName,
      		@RequestParam("gender")String gender,
      		@RequestParam("mobile")String mobile
      		,@RequestParam("token") String token
      		) throws Exception{
      	try {
      		if (!inTokenBS.isVaild(token)) {
      			this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
  
      		} else {
      			NToken nToken = inTokenBS.get(token);
      			String memberId = nToken.getMemberId();
      			JSONObject resultValue = new JSONObject();
      			Member member = memberService.find(new Long(memberId));
      			if(mobile!=null&&!(mobile.equals(""))){
      				member.setMobile(mobile);
      			}
      			if(nickName!=null&&!(nickName.equals(""))){
      				member.setNickName(nickName);
      			}
      			if(gender!=null&&!(gender.equals(""))){
      				if(gender.equals("male")){
      					member.setGender(Member.Gender.male);
      				}
      				if(gender.equals("female")){
      					member.setGender(Member.Gender.female);
      				}
      			}
      			memberService.update(member);
      			this.handleJsonResponse(response, true, "用户信息更新成功");		
      			}
      	} catch (Exception e) {
      		this.handleJsonResponse(response, false, "用户信息更新失败");
      		e.printStackTrace();
      	}
      }
   
}
