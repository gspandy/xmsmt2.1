package net.wit.mobile.controller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.BonusCalc;
import net.wit.entity.Charge;
import net.wit.entity.Member;
import net.wit.entity.MemberBank;
import net.wit.entity.OrderSettlement;
import net.wit.entity.Owner;
import net.wit.entity.OwnerCashDetail;
import net.wit.entity.OwnerIncome;
import net.wit.entity.Tenant;
import net.wit.entity.TenantShopkeeper;
import net.wit.entity.VipLevel;
import net.wit.mobile.bo.NToken;
import net.wit.mobile.bo.OwnerCashDetailBo;
import net.wit.mobile.service.INTokenBS;
import net.wit.mobile.service.impl.PushService;
import net.wit.mobile.util.rong.models.SystemMessage;
import net.wit.service.BonusCalcService;
import net.wit.service.ChargeService;
import net.wit.service.MemberBankService;
import net.wit.service.MemberService;
import net.wit.service.OrderSettlementService;
import net.wit.service.OwnerCashDetailService;
import net.wit.service.OwnerIncomeService;
import net.wit.service.OwnerServerice;
import net.wit.service.RentService;
import net.wit.service.TenantBonusSetService;
import net.wit.service.TenantShopkeeperService;
import net.wit.service.VipLevelService;
import net.wit.util.CacheUtil;
import net.wit.util.DateUtil;
import net.wit.util.ExcelUtil;
import net.wit.util.JsonUtils;
import net.wit.Filter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: ab Date: 15-9-12 Time: 下午8:15 To change
 * this template use File | Settings | File Templates.
 */
@Controller("ownerController")
@RequestMapping({ "/owner" })
public class OwnerController extends BaseController {

	@Autowired
	private INTokenBS inTokenBS;

	@Autowired
	private OwnerServerice ownerServerice;
	
	@Autowired
	private OrderSettlementService orderSettlementService;

    @Autowired
    private OwnerIncomeService ownerIncomeService;
    
    @Autowired
    private OwnerCashDetailService ownerCashDetailService;
    
    @Autowired
    private ChargeService chargeService;
    
    @Autowired
    private RentService rentService;
    
    @Autowired
    private MemberService memberService;
    
    @Autowired
    private BonusCalcService bonusCalcService;
    
    @Autowired
    private PushService pushService;
    
    @Autowired
    private TenantBonusSetService tenantBonusSetService;
    
    @Autowired
    private MemberBankService memberBankService;
    
    @Autowired
    private VipLevelService vipLevelService;
    
    @Autowired
    private TenantShopkeeperService tenantShopkeeperService;

	
	private Logger log = LoggerFactory.getLogger(OwnerController.class);
	
//	@RequestMapping(value = "/token")
//	public void registerToken(HttpServletResponse response) throws Exception {
//		try {
//
//			JSONObject resultValue = new JSONObject();
//			NToken nToken = new NToken("", "", "", "", "", "", "","");
//			nToken.setMemberId("17");
//			resultValue.put(TOKEN, inTokenBS.registerToken(nToken));
//			this.handleJsonResponse(response, true, "", resultValue);
//
//		} catch (Exception e) {
//			this.handleJsonResponse(response, false, e.getMessage());
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * 提现列表页提现记录
	 * @param response
	 * @param token
	 * @throws Exception
	 */
	@RequestMapping(value = "/withdrawChargeList")
	public void withdrawChargeList(HttpServletResponse response,
			@RequestParam("token") String token)throws Exception {
		try {
			 if(!inTokenBS.isVaild(token)) 
					this.handleJsonTokenResponse(response, false,
							CacheUtil.getParamValueByName("TOKEN_INVALID"));
			else{
				JSONArray results = new JSONArray();
				NToken nToken = inTokenBS.get(token);
				Long memberId =Long.parseLong(nToken.getMemberId());	
			    boolean isFreezed = rentService.isFreezed(memberId);
			    List<Charge> list = chargeService.findNotReceiveList(memberId);
			    for (Charge charge : list) {
			    	JSONObject js = new JSONObject();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
					Date date =sdf.parse(charge.getChargeDate());
					String chargeDate =  DateUtil.changeDateToStr(date, DateUtil.DOT_DISPLAY_DATE_MONTH);    	
			    	if(charge.getType().equals(Charge.Type.bonus)){
			    		js.put("chargeDate&Type", chargeDate+"邀请奖金");   
			    	}else{
			    		js.put("chargeDate&Type", chargeDate+"分享佣金");   
			    	}
			    	js.put("charge", ""+charge.getCharge());
			    	js.put("chargeId", ""+charge.getId());
			    	if(isFreezed&&charge.getStatus().equals(Charge.Status.notReceive)){
				    	js.put("buttonStr", "提现冻结");
				    	js.put("buttonType", "0");
			    	}else if(charge.getStatus().equals(Charge.Status.notReceive)){
			    		js.put("buttonStr", "提现申请");
			    		js.put("buttonType", "1");
			    	}else{
			    		js.put("buttonStr", "提现审核中");
			    		js.put("buttonType", "0");
			    	}
			    	results.add(js);
				}
			    this.handleJsonResponse(response, true, "", results);
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, "提现查询失败!请联系平台客服");
			log.error(e.getMessage());
		}
	}
	
	
	/**
	 * 提现列表页提现记录
	 * @param response
	 * @param token
	 * @throws Exception
	 */
	@RequestMapping(value = "/withdrawnList")
	public void withdrawnList(HttpServletResponse response,
			@RequestParam("token") String token,
			@RequestParam("pageNumber") String pageNumber,
			@RequestParam("pageSize") String pageSize)throws Exception {
		try {
			 if(!inTokenBS.isVaild(token)) 
					this.handleJsonTokenResponse(response, false,
							CacheUtil.getParamValueByName("TOKEN_INVALID"));
			else{
				JSONObject results = new JSONObject();
				NToken nToken = inTokenBS.get(token);
				Long memberId =Long.parseLong(nToken.getMemberId());	
				Pageable pageable =new Pageable();
				pageable.setPageNumber(Integer.parseInt(pageNumber));
				pageable.setPageSize(Integer.parseInt(pageSize));
				Page<Charge>page= chargeService.findPageReceived(memberId, pageable);
			    BigDecimal sumReceviedCharge =  chargeService.sumReceived(memberId);
			    if(sumReceviedCharge==null){
			    	results.put("totalCharge", "0.00");
			    }else{
			    	results.put("totalCharge", ""+sumReceviedCharge);
			    }
			    
			    List<Charge> list = page.getContent();
			    JSONArray chargeArray = new JSONArray();
			    for (Charge charge : list) {
			    	JSONObject js = new JSONObject();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
					Date date =sdf.parse(charge.getChargeDate());
					String chargeDate =  DateUtil.changeDateToStr(date, DateUtil.DOT_DISPLAY_DATE_MONTH);    	
			    	if(charge.getType().equals(Charge.Type.bonus)){
			    		js.put("chargeDate&Type", chargeDate+"邀请奖金");   
			    	}else{
			    		js.put("chargeDate&Type", chargeDate+"分享佣金");   
			    	}
			    	js.put("charge", ""+charge.getCharge());
			    	js.put("chargeId", ""+charge.getId());
			    	js.put("withdrawnDate", DateUtil.changeDateToStr(charge.getModifyDate(), DateUtil.DOT_DISPLAY_DATE_FULL) );
			    	chargeArray.add(js);
				}
			    results.put("chargeList", chargeArray);
			    this.handleJsonResponse(response, true, "", results);
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, "提现查询失败!请联系平台客服");
			log.error(e.getMessage());
		}
	}
	
	@RequestMapping(value = "/withdrawSubmit")
	public void withdrawSubmit(HttpServletResponse response,
			@RequestParam("token") String token,
			@RequestParam("chargeId") String chargeId,
			@RequestParam("password") String password)throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			}else{
				NToken nToken = inTokenBS.get(token);
				Long memberId =Long.parseLong(nToken.getMemberId());	
				Member member = new Member();
				member.setId(memberId);
				MemberBank memberBank = memberBankService.findMember(member);
				if(memberBank==null) throw new Exception("请先到提现管理中绑定提现的支付宝账号！");
				if(!memberBank.getType().equals(MemberBank.Type.debit))throw new Exception("请先到提现管理中绑定提现的支付宝账号！");
				if(password.equals(memberService.find(memberId).getCashPwd())){
					chargeService.chargeCash(Long.parseLong(chargeId));
					this.handleJsonResponse(response, true, "提现申请成功! 请耐心等待财务审核放款!");
				}else{
					throw new Exception("提现密码错误!");
				}
				
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			log.error( e.getMessage());
		}
	}
	
	
	@RequestMapping(value = "/withdrawCheck")
	public void withdrawCheck(HttpServletResponse response,
			@RequestParam("token") String token)throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			}else{
				NToken nToken = inTokenBS.get(token);
				Long memberId =Long.parseLong(nToken.getMemberId());	
				Member member = new Member();
				member.setId(memberId);
				MemberBank memberBank = memberBankService.findMember(member);
				if(memberBank==null) throw new Exception("请先到提现管理中绑定提现的支付宝账号！");
				if(!memberBank.getType().equals(MemberBank.Type.debit))throw new Exception("请先到提现管理中绑定提现的支付宝账号！");
				this.handleJsonResponse(response, true, "");
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			log.error( e.getMessage());
		}
	}
	

	@RequestMapping(value = "/balanceCurrentCharge")
	public void balanceCurrentCharge(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {

			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {

				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();
				List<Owner> ownerList = ownerServerice
						.getOwnerByMemberId(new Long(memberId));
				JSONObject resultValue = new JSONObject();
				BigDecimal totalCharge = BigDecimal.ZERO;
				if (CollectionUtils.isNotEmpty(ownerList)) {
					Owner owner = ownerList.get(0);
					totalCharge = owner.getTotalCharge();
					if (totalCharge == null) {
						totalCharge = BigDecimal.ZERO;
					}
				}

				resultValue.put("totalCharge",
						ExcelUtil.convertNull(totalCharge));

				this.handleJsonResponse(response, true, "", resultValue);
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping(value = "/pendingBonusDetails")
	public void pendingBonusDetails(HttpServletResponse response,
			@RequestParam("token") String token)throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();
				Member owner = new Member();
				owner.setId(Long.parseLong(memberId));
				String tenantId = nToken.getTenantId();
				Tenant tenant  = new Tenant();
				tenant.setId(Long.parseLong(tenantId));
				VipLevel vipLevel = vipLevelService.getVipLevelByMemeber(owner);

				if(vipLevel!=null){
					JSONObject jso = new JSONObject();
	//				BigDecimal pendingBonus = orderSettlementService.getPendingOrderBonus(owner, tenant);
					BigDecimal pendingBonus = orderSettlementService.getPendingOrderBonus(owner, vipLevel);
	//				String bonusPercent  = tenantBonusSetService.getBonusPercent(Long.parseLong(tenantId));
					String bonusPercent  = tenantBonusSetService.getBonusPercent(vipLevel);
	//				JSONArray jsa =orderSettlementService.getPendingOrderSettleInfo(owner, tenant);
					VipLevel nextVipLevel =vipLevelService.getNextLevelOfCurrent(vipLevel);
					Integer invitedCount = tenantShopkeeperService.getInvationsByRecommand(owner);
					if(nextVipLevel==null){
						jso.put("nextVipLevel", "");
						jso.put("nextInviteCount", "");
						jso.put("nextBonusPercent", "");
						jso.put("nextLevelBonus", "");
						jso.put("disparity", "");
					}else{
						Integer nextInviteCount = nextVipLevel.getInviteCondition();
						BigDecimal nextLevelBonus = orderSettlementService.getPendingOrderBonus(owner, nextVipLevel);
						String nextBonusPercent  = tenantBonusSetService.getBonusPercent(nextVipLevel);
						jso.put("nextVipLevel", nextVipLevel.getLevelName());
						jso.put("nextInviteCount", nextInviteCount+"");
						jso.put("nextBonusPercent", nextBonusPercent);
						jso.put("nextLevelBonus",nextLevelBonus+"");
						jso.put("disparity", (nextInviteCount-invitedCount)+"");
					}
					JSONArray jsa =orderSettlementService.getPendingOrderSettleInfo(owner, tenant,vipLevel);
					jso.put("vipLevel", vipLevel.getLevelName());
					if(vipLevel.getInviteCondition()==null)
						jso.put("inviteCount","0");
					else
						jso.put("inviteCount",vipLevel.getInviteCondition()+"");
					jso.put("invitedCount", invitedCount+"");
					jso.put("pendingBonus", pendingBonus);
					jso.put("bonusPercent", bonusPercent);
					jso.put("bonusDetails", jsa);
					this.handleJsonResponse(response, true, "",jso);
				}else{
					throw new Exception("预收邀请奖金收入查询失败! 请联系平台客服");
				}
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			log.error(e.getMessage());
		}
	}
	

	/**
	 * 佣金收入明细 (查询订单已经完成和已经结算的记录)
	 * @param response
	 * @param token
	 * @param pageSize
	 * @param pageNo
	 * @throws Exception
	 */
	
	@RequestMapping(value = "/chargeDetail")
	public void chargeDetail(HttpServletResponse response,
			@RequestParam("token") String token,
			@RequestParam("pageSize") String pageSize,
			@RequestParam("pageNo") String pageNo) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();
				Member owner = new Member();
				owner.setId(Long.parseLong(memberId));
				Page<OrderSettlement> page = orderSettlementService.getOrderSettlementStream(owner, Integer.parseInt(pageNo), Integer.parseInt(pageSize));
				List<OrderSettlement> list = page.getContent();
				JSONArray results = new JSONArray();
				for (OrderSettlement orderSettlement : list) {
					JSONObject js  = new JSONObject();
					String chargeDate = DateUtil.changeDateToStr(
							orderSettlement.getFinishDate(), DateUtil.DOT_DISPLAY_DATE);
					js.put("chargeDate", chargeDate);  //订单完成日
					js.put("orderSn", orderSettlement.getOrder().getSn()); //订单编号
					js.put("buyerNickName", "买家(昵称:"+orderSettlement.getMember().getNickName()+")"); //买家
					js.put("charge", "+"+orderSettlement.getSettleCharge()); //佣金金额;
					results.add(js);
				}
				this.handleJsonArrayResponse(response, true, "", results);
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, "分享佣金收入查询失败! 请联系平台客服");
			log.error(e.getMessage());
		}
	}
	
	/**
	 * 奖金收入(查询所有未发放和已经发放的奖金明细记录)
	 * @param response
	 * @param token
	 * @param pageSize
	 * @param pageNo
	 * @throws Exception
	 */
	@RequestMapping(value = "/bonusDetail")
	public void bonusDetail(HttpServletResponse response,
			@RequestParam("token") String token,
			@RequestParam("pageSize") String pageSize,
			@RequestParam("pageNo") String pageNo) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();
				JSONArray results = new JSONArray();
				 Page<BonusCalc> page = bonusCalcService.getBonusCalcList(Long.parseLong(memberId), Integer.parseInt(pageNo), Integer.parseInt(pageSize));
				 List<BonusCalc> list = page.getContent();
				 for (BonusCalc bonusCalc : list) {
						JSONObject js  = new JSONObject();
						String bonusTime = DateUtil.changeDateToStr(
								bonusCalc.getBonusTime(), DateUtil.DOT_DISPLAY_DATE);
						js.put("bonusTime", bonusTime.substring(0, 7));  //奖金生成月
						String name = bonusCalc.getBeRecommend().getName(); //被推荐人
						js.put("shopKeeperName", "邀请奖金 (店长:"+name+")"); //邀请店主名称
						js.put("bonus", "+"+bonusCalc.getBonus()); //奖金金额;
						js.put("levelName", bonusCalc.getVipLevelName()); //vip 等级名称
						js.put("bonusPercent", bonusCalc.getBonusPercent()+"%"); //奖金比例;
						results.add(js);
				}
				 this.handleJsonArrayResponse(response, true, "", results);
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, "分享佣金收入查询失败! 请联系平台客服");
			log.error(e.getMessage());
		}
	}
	/**已经	弃用*/
	@RequestMapping(value = "/balanceDetail")
	public void balanceDetail(HttpServletResponse response,
			@RequestParam("token") String token,
			@RequestParam("pageSize") String pageSize,
			@RequestParam("pageNo") String pageNo) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));

			} else {
				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();

                Pageable pageable = new Pageable();
                pageable.setPageSize(new Integer(pageSize));
                pageable.setPageNumber(new Integer(pageNo));

                List<Order> orderList = pageable.getOrders();
                orderList.add(new Order("incomeDate", Order.Direction.desc));

				Page<OwnerIncome> page = ownerIncomeService
						.getOwnerIncomeByMemberId(new Long(memberId), pageable);

                JSONArray jsonArray = new JSONArray();
                JSONObject resultValue;
				BigDecimal incomeAmt;
                String amt = "";
                String content = "";
                String inComeType = "";
				for(OwnerIncome ownerIncome : page.getContent()) {
                    resultValue = new JSONObject();
                    resultValue.put("incomeDate", ExcelUtil
                            .convertNull(DateUtil.changeDateToStr(
                                    ownerIncome.getIncomeDate(),
                                    DateUtil.CN_DISPLAY_DATE)));
                    incomeAmt = ownerIncome.getIncomeAmt();
                    if (incomeAmt == null) {
                        incomeAmt = BigDecimal.ZERO;
                    }
                    inComeType = ownerIncome.getIncomeType();
                    if ("1".equals(inComeType)) {
                        content = "佣金收入";
                    } else if ("2".equals(inComeType)) {
                        content = "奖金收入";
                    } else if ("3".equals(inComeType)) {
                        content = "租金收取";
                        amt = incomeAmt.multiply(new BigDecimal("-1")) + "";
                    }
                    if (BigDecimal.ZERO.compareTo(incomeAmt) < 0 && !"3".equals(inComeType)) {
                        amt = "+" + incomeAmt;
                    }
                    resultValue.put("content", content);

                    resultValue.put("incomeAmt", amt);

                    jsonArray.add(resultValue);
                }

				this.handleJsonArrayResponse(response, true, "", jsonArray);
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}

/**
 * 店主首页 显示待结算佣金和可提现申请的(佣金+奖金)
 * @param response
 * @param token
 * @throws Exception
 */
	@RequestMapping(value = "/balanceStatistics")
	public void balanceStatistics(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {

			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();
				String tenantId = nToken.getTenantId();
				Member member =new Member();
				member.setId(new Long(memberId));
				Tenant tenant  = new Tenant();
				tenant.setId(Long.parseLong(tenantId));
				JSONObject resultValue = new JSONObject();
				BigDecimal totalAmt = orderSettlementService.getOwnerSettleCharge(member);  //获取待结算佣金
				if(totalAmt==null)totalAmt = BigDecimal.ZERO;
				TenantShopkeeper ts = tenantShopkeeperService.findShopKeeperByMemberId(new Long(memberId));
				BigDecimal accountBalance  = orderSettlementService.getPendingOrderBonus(member, ts.getVipLevel()); // 获取店主待结算奖金
				if(accountBalance==null)accountBalance = BigDecimal.ZERO;
				resultValue.put("totalAmt",	totalAmt);
				resultValue.put("accountBalance",	accountBalance);
				this.handleJsonResponse(response, true, "", resultValue);
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}
	/** 废弃
	@RequestMapping(value = "/balanceStatistics")
	public void balanceStatistics(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {

			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {

				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();
				Member member =new Member();
				member.setId(new Long(memberId));
				JSONObject resultValue = new JSONObject();
				BigDecimal totalAmt = BigDecimal.ZERO;
				totalAmt = orderSettlementService.getOwnerSettleCharge(member);  //获取待结算佣金
				BigDecimal accountBalance = BigDecimal.ZERO;
				List<Charge> list = chargeService.findChargeInfo(new Long(memberId), Charge.Status.notReceive);
				for (Charge charge : list) {
					accountBalance = accountBalance.add(charge.getCharge());
				}
				if(totalAmt==null)totalAmt= BigDecimal.ZERO;
				resultValue.put("totalAmt", totalAmt);
				if(accountBalance==null)accountBalance = BigDecimal.ZERO;
				resultValue.put("accountBalance",	accountBalance);
				this.handleJsonResponse(response, true, "", resultValue);
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}
	 */

	@RequestMapping(value = "/balanceMoneyTotal")
	public void balanceMoneyTotal(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {

			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();
				List<Owner> ownerList = ownerServerice
						.getOwnerByMemberId(new Long(memberId));
				JSONObject resultValue = new JSONObject();
				BigDecimal totalAmt = BigDecimal.ZERO;
				BigDecimal totalCharge = BigDecimal.ZERO;
				BigDecimal totalBonus = BigDecimal.ZERO;
				if (CollectionUtils.isNotEmpty(ownerList)) {
					Owner owner = ownerList.get(0);
					totalAmt = owner.getTotalAmt();
					if (totalAmt == null) {
						totalAmt = BigDecimal.ZERO;
					}
					totalCharge = owner.getTotalCharge();
					if (totalCharge == null) {
						totalCharge = BigDecimal.ZERO;
					}
					totalBonus = owner.getTotalBonus();
					if (totalBonus == null) {
						totalBonus = BigDecimal.ZERO;
					}
				}

				resultValue.put("totalAmt", ExcelUtil.convertNull(totalAmt));
				resultValue.put("totalCharge",
						ExcelUtil.convertNull(totalCharge));
				resultValue
						.put("totalBonus", ExcelUtil.convertNull(totalBonus));

				this.handleJsonResponse(response, true, "", resultValue);;
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping(value = "/chargeInOrder")
	public void chargeInOrder(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try{
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				JSONObject resultValue = new JSONObject();
				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();
				Member member = new Member();
				member.setId(new Long(memberId));
				BigDecimal charge = orderSettlementService.getOwnerSettleCharge(member)==null?new BigDecimal(0):
					orderSettlementService.getOwnerSettleCharge(member);
				resultValue.put("orderCharge", String.valueOf(charge));
				log.info("chargeInOrder"+String.valueOf(charge));
				this.handleJsonResponse(response, true, "", resultValue);
			}
		}catch(Exception e){
			log.error(e.getMessage());
			this.handleJsonResponse(response, false, "获取待结算分享佣金失败!");
		}
	}
	/***
	 * 
	 * 返回账户余额
	 * @param response
	 * @param token
	 * @throws Exception
	 */
	@RequestMapping(value = "/accountBalance")
	public void accountBalance(HttpServletResponse response,@RequestParam("token") String token
			) throws Exception {
		try{
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				JSONObject resultValue = new JSONObject();
				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();
				Owner  owner=ownerServerice.getOwner(new Long(memberId));
				if(owner==null){
					resultValue.put("accountBalance", String.valueOf(0));
					this.handleJsonResponse(response, true, "余额为0", resultValue);
				}else{
					resultValue.put("accountBalance", String.valueOf(owner.getAccountBalance()));
					this.handleJsonResponse(response, true, "", resultValue);
				}
				
			}
		}catch(Exception e){
			log.error(e.getMessage());
			this.handleJsonResponse(response, false, "获取账户余额失败!");
		}
	}
	/***
	 * 
	 * 返回提现记录
	 * @param response
	 * @param token
	 * @throws Exception
	 */
	@RequestMapping(value = "/cashDetail")
	public void cashDetail(HttpServletResponse response
			,@RequestParam("token") String token
			) throws Exception {
		try{
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				JSONObject resultValue = new JSONObject();
				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();
				List<OwnerCashDetail>  ownerCashDetailList=ownerCashDetailService.getOwnerCashDetailByMemberId(new Long(memberId));
				List<OwnerCashDetailBo> ownerCashDetailBoList=new ArrayList<OwnerCashDetailBo>();
				for(OwnerCashDetail ownerCashDetail:ownerCashDetailList)
				{
					OwnerCashDetailBo ownerCashDetailBo=new OwnerCashDetailBo();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
					ownerCashDetailBo.setId(String.valueOf(ownerCashDetail.getId()));
					ownerCashDetailBo.setCashAmt(String.valueOf(ownerCashDetail.getCashAmt()));
					ownerCashDetailBo.setCashDate(String.valueOf(sdf.format(ownerCashDetail.getCashDate())));
					ownerCashDetailBo.setMemberBankId(String.valueOf(ownerCashDetail.getMemberBankId()));
					ownerCashDetailBo.setMemberId(String.valueOf(ownerCashDetail.getMemberId()));
					ownerCashDetailBoList.add(ownerCashDetailBo);
					
				}
				resultValue.put("ownerCashDetail",JsonUtils.toJson(ownerCashDetailBoList));
				this.handleJsonResponse(response, true, "", resultValue);
			}
		}catch(Exception e){
			log.error(e.getMessage());
			this.handleJsonResponse(response, false, "提现记录查询失败!");
		}
	}
	
	
	/***
	 * 
	 * 根据企业获取企业的店长等级
	 * @param response
	 * @param token
	 * @throws Exception
	 */
	@RequestMapping(value = "/getVipLevelByTenant")
	public void getVipLevelByTenant(HttpServletResponse response
			,@RequestParam("tenantId") String tenantId
			) throws Exception {
		try{
			JSONArray results = new JSONArray();
			Tenant tenant = new Tenant();
			tenant.setId(Long.parseLong(tenantId));
			List<VipLevel> list = vipLevelService.getVipLevelByTenant(tenant);
			for (VipLevel vipLevel : list) {
				JSONObject result = new JSONObject();
				String bonusPercent  = tenantBonusSetService.getBonusPercent(vipLevel);
				result.put("levelName", vipLevel.getLevelName());
				result.put("bonusLevel", bonusPercent);
				result.put("ticketNum", vipLevel.getTicketNum());
				result.put("condition", vipLevel.getInviteCondition());
				result.put("isDefault", vipLevel.getIsDefault()+"");
				results.add(result);
			}
			this.handleJsonArrayResponse(response, true, "", results);
		}catch(Exception e){
			log.error(e.getMessage());
			this.handleJsonResponse(response, false, "vip等级查询失败!");
		}
	}
	
	
	
	/***
	 * 
	 * 根据企业获取企业的店长等级
	 * 
	 * @param response
	 * @param token
	 * @throws Exception
	 */
	@RequestMapping(value = "/getVipLevelByMember")
	public void getVipLevelByMember(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();
				Member owner = new Member();
				owner.setId(Long.parseLong(memberId));
				VipLevel vipLevel = vipLevelService.getVipLevelByMemeber(owner);
				if (vipLevel != null) {
					JSONObject jso = new JSONObject();
					String bonusPercent = tenantBonusSetService
							.getBonusPercent(vipLevel);
					VipLevel nextVipLevel = vipLevelService
							.getNextLevelOfCurrent(vipLevel);
					Integer invitedCount = tenantShopkeeperService
							.getInvationsByRecommand(owner);
					if (nextVipLevel == null) {
						jso.put("nextVipLevel", "");
						jso.put("nextInviteCount", "");
						jso.put("nextBonusPercent", "");
						jso.put("disparity", "");
					} else {
						Integer nextInviteCount = nextVipLevel
								.getInviteCondition();
						String nextBonusPercent = tenantBonusSetService
								.getBonusPercent(nextVipLevel);
						jso.put("nextVipLevel", nextVipLevel.getLevelName());
						jso.put("nextInviteCount", nextInviteCount + "");
						jso.put("nextBonusPercent", nextBonusPercent);
						jso.put("disparity", (nextInviteCount - invitedCount)
								+ "");
					}
					jso.put("vipLevel", vipLevel.getLevelName());
					if(vipLevel.getInviteCondition()==null)
						jso.put("inviteCount","0");
					else
						jso.put("inviteCount",vipLevel.getInviteCondition()+"");
					jso.put("invitedCount", invitedCount + "");
					jso.put("bonusPercent", bonusPercent);
					this.handleJsonResponse(response, true, "", jso);
				} else {
					this.handleJsonResponse(response, false, "该企业尚未设置店长等级");
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			this.handleJsonResponse(response, false, "vip等级查询失败!");
		}
	}
	
	
	/***
	 * 
	 * 返回提现记录
	 * @param response
	 * @param token
	 * @throws Exception
	 */
	@RequestMapping(value = "/testmsm")
	public void testmsm(HttpServletResponse response
			) throws Exception {
		try{
			Tenant tenant = new Tenant();
			tenant.setId(Long.parseLong("14"));
			Member member = new Member();
			member.setId(Long.parseLong("179"));
			pushService.publishSystemMessage(tenant, member, SystemMessage.shopKeeperRentPaymentMsg("3"));
		}catch(Exception e){
			log.error(e.getMessage());
			this.handleJsonResponse(response, false, "提现记录查询失败!");
		}
	}
	
	
	
	
}
