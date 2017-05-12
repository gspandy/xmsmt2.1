package net.wit.mobile.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import jodd.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.wit.entity.Member;
import net.wit.entity.MemberBank;
import net.wit.entity.TenantShopkeeper;
import net.wit.mobile.bo.NToken;
import net.wit.mobile.service.INTokenBS;
import net.wit.mobile.service.IPushService;
import net.wit.mobile.util.rong.models.TxtMessage;
import net.wit.service.MemberService;
import net.wit.service.PicService;
import net.wit.service.TenantShopkeeperService;
import net.wit.sms.service.SmsService;
import net.wit.util.CacheUtil;
import net.wit.util.ExcelUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/login")
public class LoginController extends BaseController {

	@Autowired
	private PicService picService;

	@Autowired
	private IPushService pushService;

	@Autowired
	private INTokenBS inTokenBS;

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private SmsService smsService;

	@Autowired
	private TenantShopkeeperService tenantShopkeeperService;

	private Logger log = LoggerFactory.getLogger(LoginController.class);


    @RequestMapping(value = "/validateFamily")
        public void validateFamily(HttpServletResponse response,
                          @RequestParam("token") String token, @RequestParam("familyType") String familyType,@RequestParam("familyName") String familyName) throws Exception{
            try {
                if (!inTokenBS.isVaild(token)) {
                    this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));

                } else {
                    NToken nToken = inTokenBS.get(token);
                    String memberId = nToken.getMemberId();
                    JSONObject resultValue = new JSONObject();
                    Member member = memberService.find(new Long(memberId));
                    if (StringUtils.isEmpty(familyType)) {
                        familyType = "";
                    }
                    if (StringUtils.isEmpty(familyName)) {
                        familyName = "";
                    }
                    if (familyType.equals(member.getFamilyType()) && familyName.equals(member.getFamilyName())) {
                        this.handleJsonResponse(response, true, "验证通过");
                    } else {
                        this.handleJsonResponse(response, true, "验证不通过");
                    }
                }


            } catch (Exception e) {
                this.handleJsonResponse(response, false, e.getMessage());
                e.printStackTrace();
            }
        }

	@RequestMapping(value = "/getMemberBanks")
	public void getMemberBanks(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));

			} else {
				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();
				JSONArray jsonArray = new JSONArray();
				JSONObject resultValue = new JSONObject();
				Member member = memberService.find(new Long(memberId));
				Set<MemberBank> memberBanks = member.getMemberBanks();
				if (CollectionUtils.isEmpty(memberBanks)) {
					memberBanks = new HashSet<MemberBank>();
				}
				for (MemberBank memberBank : memberBanks) {
					resultValue.put("type",
							ExcelUtil.convertNull(memberBank.getType().name()));
					resultValue.put("cardNo",
							ExcelUtil.convertNull(memberBank.getCardNo()));
					resultValue.put("depositBank",
							ExcelUtil.convertNull(memberBank.getDepositBank()));
				}
				this.handleJsonResponse(response, true, "", resultValue);
			}

		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/updatePayPswd")
	public void updatePayPswd(HttpServletResponse response,
			@RequestParam("token") String token,
			@RequestParam("payPswd") String payPswd) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));

			} else {
				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();
				memberService.updatePayPswd(new Long(memberId), payPswd);
				this.handleJsonResponse(response, true, "修改成功");
			}

		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/updatePswd")
	public void updatePswd(HttpServletResponse response,
			@RequestParam("token") String token,
			@RequestParam("pswd") String pswd) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));

			} else {
				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();
				memberService.updatePswd(new Long(memberId), pswd);
				this.handleJsonResponse(response, true, "修改成功");
			}

		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}

	/* 不适用 */
	/**
	 * @RequestMapping(value = "/getMemberInfoByTel") public void
	 *                       getMemberInfoByTel(HttpServletResponse response,
	 * @RequestParam("tel") String tel) throws Exception { try { Member member =
	 *                      memberService.findByTel(tel); if (member == null) {
	 *                      member = new Member(); } JSONObject resultValue =
	 *                      new JSONObject(); resultValue.put("nickName",
	 *                      member.getName()); resultValue.put("sex",
	 *                      member.getGender() == null ? "" :
	 *                      member.getGender());
	 *                      this.handleJsonResponse(response, true, null,
	 *                      resultValue); } catch (Exception e) {
	 *                      this.handleJsonResponse(response, false,
	 *                      e.getMessage()); } }
	 */

	/**
	 * 获取验证码方法;
	 * 
	 * @param response
	 * @param tel
	 * @throws Exception
	 */
	@RequestMapping(value = "/getAuthCode")
	public void getAuthCode(HttpServletResponse response,
			@RequestParam("tel") String tel) throws Exception {
		try {
			
			JSONObject resultValue = new JSONObject();
			smsService.getAuthCodeYP(tel);//发送短信
			Member member = memberService.findByTel(tel);
			if (member == null) { // 判断用户是否已经存在;
				resultValue.put("isNew", "true"); // 不存在告诉前端第一次登陆;
			} else {
				resultValue.put("isNew", "false"); // 存在不是第一次登陆;
			}
			this.handleJsonResponse(response, true, "", resultValue);
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
		}
	}

	/**
	 * 获取邀请函
	 * 
	 * @param response
	 * @param token
	 * @throws Exception
	 */

//	@RequestMapping(value = "/getInvitations")
//	public void getInvitations(HttpServletResponse response,
//			@RequestParam("token") String token) throws Exception {
//		try {
//
//			JSONArray jsonArray = new JSONArray();
//			if (!inTokenBS.isVaild(token)) {
//				this.handleJsonTokenResponse(response, false,
//						CacheUtil.getParamValueByName("TOKEN_INVALID"));
//			} else {
//
//				NToken nToken = inTokenBS.get(token);
//				String memberId = nToken.getMemberId();
//				if (StringUtils.isEmpty(memberId)) {
//					throw new Exception("会员ID不能为空");
//				}
//				Member member = memberService.find(new Long(memberId));
//				if (member != null) {
//
//					List<TenantShopkeeper> tenantShopkeeperList = tenantShopkeeperService
//							.findInvationsByMemberId(member.getId());
//					if (tenantShopkeeperList != null) {
//						if (tenantShopkeeperList.size() == 0) {
//							throw new Exception("已是店主");
//						}
//						JSONObject resultValue;
//						for (TenantShopkeeper tenantShopkeeper : tenantShopkeeperList) {
//							resultValue = new JSONObject();
//							resultValue
//									.put("tenantId", tenantShopkeeper
//											.getTenant().getId() == null ? ""
//											: tenantShopkeeper.getTenant()
//													.getId() + "");
//							resultValue.put("tenantName", tenantShopkeeper
//									.getTenant().getShortName() == null ? ""
//									: tenantShopkeeper.getTenant().getShortName()
//											+ "");
//							resultValue.put("logo", tenantShopkeeper
//									.getTenant().getLogo() == null ? ""
//									: tenantShopkeeper.getTenant().getLogo()
//											+ "");
//							resultValue.put("invationImage",
//									tenantShopkeeper.getTenant()
//											.getInvationImage() == null ? ""
//											: tenantShopkeeper.getTenant()
//													.getInvationImage() + "");
//							resultValue
//									.put("recommandId", tenantShopkeeper
//											.getRecommendMember() == null ? ""
//											: tenantShopkeeper
//													.getRecommendMember()
//													.getId()
//													+ "");
//							resultValue
//							.put("recommandName", tenantShopkeeper
//									.getRecommendMember() == null ? ""
//									: tenantShopkeeper
//											.getRecommendMember()
//											.getName()
//											+ "");
//							resultValue.put("tenantShopkeeperId",
//									tenantShopkeeper.getId() == null ? ""
//											: tenantShopkeeper.getId() + "");
//
//							jsonArray.add(resultValue);
//						}
//					}
//				}
//				log.info(jsonArray.toString()+"111111111111111111111111111111111111111111111");
//				this.handleJsonArrayResponse(response, true, "", jsonArray);
//			}
//		} catch (Exception e) {
//			this.handleJsonResponse(response, false, e.getMessage());
//			e.printStackTrace();
//		}
//	}

	@RequestMapping(value = "/logout")
	public void logout(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {
			boolean flag = inTokenBS.logout(token);
			if (flag) {
				this.handleJsonResponse(response, true, "注销成功");
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
		}
	}
	/**
	 * 测试发送消息
	 * @param response
	 * @param token
	 * @throws Exception
	 */
	@RequestMapping(value = "/sendMessage")
	public void sendMessage(HttpServletResponse response) throws Exception {
		try {
			pushService.getToken("1", "admin", "http://zb.z8ls.com/pic/default_head_female.png");
			List<String> toUserIds = new ArrayList<String>();
			toUserIds.add("25");
			toUserIds.add("26");
	String newStr=new String("哈哈哈哈".getBytes("gbk"));
	JSONObject  js = new JSONObject();
	js.put("content", "哈哈哈哈");
			TxtMessage txt = new TxtMessage( js.toString());
			pushService.publishMessage("1", toUserIds, txt);
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
		}
	}

	@RequestMapping(value = "/getRongyunToken")
	public void getRongyunToken(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				JSONObject result = new JSONObject();
				NToken nToken = inTokenBS.get(token);
				String rongyunToken = pushService.getToken(nToken); // 获取融云token;
				result.put("rongyunToken", rongyunToken);
				this.handleJsonResponse(response, true, "", result);
			}

		} catch (Exception e) {
			e.printStackTrace();
			this.handleJsonResponse(response, false, e.getMessage());
		}
	}

	/**
	 * 手机端登陆
	 * 
	 * @param response
	 * @param tel
	 * @param authcode
	 * @param nickName
	 * @param sex
	 * @throws Exception
	 */
	@RequestMapping(value = "/login")
	public void login(HttpServletResponse response,
			@RequestParam("tel") String tel,
			@RequestParam("authcode") String authcode,
			@RequestParam("nickName") String nickName,
			@RequestParam("sex") String sex) throws Exception {
		try {
				log.info("登录验证码校验 "+tel+" 验证码:"+authcode+" 用户昵称:"+nickName+"性别:"+sex);
//			 验证验证码
			if (!smsService.validCode(tel, authcode)) {
				throw new Exception("验证码错误!");
			}
			Member member = memberService.findByTel(tel);
			JSONObject result = new JSONObject();
			if (member == null) {
				try{
					String loginType = "0";
					// 注册新的
					member = new Member();
					member.setMobile(tel);
					member.setNickName(nickName);
					member.setGender(Member.Gender.valueOf(sex));
					member.setLoginDate(new Date()) ; 
					String headImage = picService.getDefaultHeadImage(member);
					member.setHeadImg(headImage);
					NToken nToken =new NToken(tel, nickName, headImage);
					result.put(TOKEN, inTokenBS.registerMemberWithToken(member,
							nToken));
					result.put("loginType", loginType); // 登录类型
					result.put("nickName", nickName); // 返回昵称
					result.put("headImage", headImage); // 返回头像
					result.put("sex", member.getGender().name());
					result.put("realName", "");
					result.put("tenantName", "");
					result.put("tenantId", "");
					result.put("tel", tel);
					result.put("isShopKeeper", "0");
					String rongyunToken = "";
					try{
						 rongyunToken =	pushService.getToken(nToken); //获取融云token;
						 if(StringUtil.isEmpty(rongyunToken)) rongyunToken =	pushService.getToken(nToken); //获取融云token;
						 if(StringUtil.isEmpty(rongyunToken)) rongyunToken =	pushService.getToken(nToken); //获取融云token;
						 if(StringUtil.isEmpty(rongyunToken)) rongyunToken =	pushService.getToken(nToken); //获取融云token;
					} catch (Exception e) {
						 rongyunToken =	""; //获取融云token;
					}
					result.put("rongyunToken", rongyunToken);
					log.error("登录日志"+result.toString());
					this.handleJsonResponse(response, true, "", result);
				}catch(Exception e){
					e.printStackTrace();
					throw new Exception("登录失败!");
				}
				
			}
			// 普通、有资格、店主
			else {
				try {
					if(member.getLoginDate()==null){
						member.setLoginDate(new Date());
						memberService.update(member);   //第一次登陆
					}
					String ntoken ;
					String loginType = tenantShopkeeperService
							.getMemeberLoginTyper(member.getId());
					if (loginType.equals("1")) {
						TenantShopkeeper tenantShopkeeper = tenantShopkeeperService
								.findShopKeeperByMemberId(member.getId());
						ntoken = inTokenBS.createToken(member, tenantShopkeeper);
						result.put(TOKEN,ntoken);
						result.put("tenantName",  String.valueOf(tenantShopkeeper.getTenant().getShortName()));
						result.put("tenantId", String.valueOf(tenantShopkeeper.getTenant().getId()));
						result.put("isShopKeeper", "1");
					} else {
						ntoken= inTokenBS.createNoShopToken(member);
						result.put(TOKEN, ntoken);
						result.put("tenantId", "");
						result.put("isShopKeeper", "0");
						result.put("tenantName",  "");
					}
					result.put("loginType", loginType); // 登录类型
					if (StringUtils.isEmpty(member.getNickName()))
						nickName = "指帮用户";
					else
						nickName = member.getNickName();
					result.put("nickName", nickName); // 返回昵称
					String headImage;
					if (StringUtils.isEmpty(member.getHeadImg()))
						headImage = picService.getDefaultHeadImage(member);
					else
						headImage = member.getHeadImg();
					result.put("headImage", headImage); // 返回头像
					result.put("sex", member.getGender().name());
					result.put("tel", member.getMobile());
					String realName = member.getName() ==null?"":member.getName();
					result.put("realName",realName);
					NToken token =inTokenBS.get(ntoken);
					String rongyunToken = "";
					try{
						 rongyunToken =	pushService.getToken(token); //获取融云token;
						 if(StringUtil.isEmpty(rongyunToken)) rongyunToken =	pushService.getToken(token); //获取融云token;
						 if(StringUtil.isEmpty(rongyunToken)) rongyunToken =	pushService.getToken(token); //获取融云token;
						 if(StringUtil.isEmpty(rongyunToken)) rongyunToken =	pushService.getToken(token); //获取融云token;
					} catch (Exception e) {
						 rongyunToken =	""; //获取融云token;
					}
					result.put("rongyunToken", rongyunToken);
					log.error("登录日志"+result.toString());
					this.handleJsonResponse(response, true, "", result);
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("登录失败!");
				}
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
		}
	}
	
	
	@RequestMapping(value = "/login2")
	public void login(HttpServletResponse response,@RequestParam("tel") String tel,@RequestParam("password") String password) throws Exception {
		try {
			String tel1  ="13600670002";  //建发美酒汇
			String tel2  ="13600670003"; //乔丹
			String tel3 ="13600670004"; //匹克
			String tel4  ="13600670005"; //港发
			String tel5  ="13600670006"; //酩酊
			String tel6  ="13600670007"; //媄购汇
			String tel7="13600670008";//泽露


			if(tel.equals(tel1)||tel.equals(tel2)||tel.equals(tel3)||tel.equals(tel4)||tel.equals(tel5)||tel.equals(tel6)||tel.equals(tel7)){
				if(!StringUtil.toLowerCase(password).equals("e10adc3949ba59abbe56e057f20f883e")) throw new Exception("密码错误!");
				else{
					Member member = memberService.findByTel(tel);
					String loginType = "0";
					String ntoken= inTokenBS.createNoShopToken(member);
					String nickName;
					JSONObject result = new JSONObject();
					result.put(TOKEN, ntoken);
					result.put("tenantId", "");
					result.put("isShopKeeper", "0");
					result.put("tenantName",  "");
					result.put("loginType", loginType); // 登录类型
					if (StringUtils.isEmpty(member.getNickName()))
						nickName = "指帮用户";
					else
						nickName = member.getNickName();
					result.put("nickName", nickName); // 返回昵称
					String headImage;
					if (StringUtils.isEmpty(member.getHeadImg()))
						headImage = picService.getDefaultHeadImage(member);
					else
						headImage = member.getHeadImg();
					result.put("headImage", headImage); // 返回头像
					result.put("sex", member.getGender().name());
					result.put("tel", member.getMobile());
					String realName = member.getName() ==null?"":member.getName();
					result.put("realName",realName);
					NToken token =inTokenBS.get(ntoken);
					try{
						String rongyunToken =	pushService.getToken(token); //获取融云token;
						result.put("rongyunToken", rongyunToken);
					} catch (Exception e) {
						String rongyunToken =	""; //获取融云token;
						result.put("rongyunToken", rongyunToken);
					}
					log.info(result.toString()+"111111111111111111111111111111111111111111111");
					this.handleJsonResponse(response, true, "", result);
				}
			}else{
				throw new Exception("账号错误!");
			}
				
			

		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
		}
	}

}
