package net.wit.mobile.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import jodd.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.wit.Setting;
import net.wit.entity.Member;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.Owner;
import net.wit.entity.ShareSet;
import net.wit.entity.Tenant;
import net.wit.entity.TenantBonusSet;
import net.wit.entity.TenantSellCondition;
import net.wit.entity.TenantShopkeeper;
import net.wit.entity.TenantShopkeeper.InvitedType;
import net.wit.entity.TenantShopkeeper.IsShopkeeper;
import net.wit.entity.TicketApplyCondition;
import net.wit.entity.TicketSet;
import net.wit.mobile.bo.NToken;
import net.wit.mobile.service.INTokenBS;
import net.wit.mobile.service.impl.PushService;
import net.wit.mobile.util.rong.models.SystemMessage;
import net.wit.service.MemberService;
import net.wit.service.OrderService;
import net.wit.service.OwnerServerice;
import net.wit.service.PicService;
import net.wit.service.TenantBonusSetService;
import net.wit.service.TenantSellConditionService;
import net.wit.service.TenantService;
import net.wit.service.TenantShopkeeperService;
import net.wit.service.TicketApplyConditionService;
import net.wit.service.TicketSetService;
import net.wit.service.impl.ShareSetService;
import net.wit.util.BizException;
import net.wit.util.CacheUtil;
import net.wit.util.DateUtil;
import net.wit.util.ExcelUtil;
import net.wit.util.SettingUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 邀请函控制类
 * 
 * @author dnzx
 *
 */
@Controller
@RequestMapping(value = "/invitation")
public class Invitation extends BaseController {

	@Autowired
	private INTokenBS inTokenBS;

	@Autowired
	private MemberService memberService;

	@Autowired
	private OwnerServerice ownerService;

	@Autowired
	private TenantShopkeeperService tenantShopkeeperService;

	@Autowired
	private TenantService tenantService;

	@Autowired
	private PicService picService;

	@Autowired
	private TenantSellConditionService conditionService;

	@Autowired
	private TicketSetService ticketSetService;

	@Autowired
	private TicketApplyConditionService ticketApplyConditionService;

	@Autowired
	private TenantBonusSetService tenantBonusSetService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private PushService pushService;

	private static final String TENANTID = "${tenantId}";

	private static final String SHOPKEEPERID = "${shopKeeperId}";
	// /**
	// * 获取已经该店主已经邀请的店主
	// */
	// @RequestMapping(value = "/getRecommandByMe")
	// public void getMemberBanks(HttpServletResponse response,
	// @RequestParam("token") String token) throws Exception{
	// NToken nToken = inTokenBS.get(token);
	// String memberId = nToken.getMemberId();
	// }
	private Logger log = LoggerFactory.getLogger(Invitation.class);

	@Autowired
	private ShareSetService shareSetService;

	/** 店长邀请 */
	@RequestMapping(value = "/shopKeeperInviteAction")
	public void shopKeeperInviteAction(HttpServletResponse response,
			@RequestParam("token") String token,
			@RequestParam("tenantShopKeeperId") String tenantShopKeeperId)
			throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				NToken nToken = inTokenBS.get(token);
				Long memberId = Long.parseLong(nToken.getMemberId());
				Long tenantShopKeeperLongId = Long
						.parseLong(tenantShopKeeperId);
				TenantShopkeeper ts = tenantShopkeeperService
						.find(tenantShopKeeperLongId);
				Member recommand = ts.getRecommendMember();
				if (recommand.getId().equals(memberId)
						&& ts.getInvitedType().equals(InvitedType.shopkeeper)
						&& ts.getIsShopkeeper().equals(IsShopkeeper.capable)) {
					ts.setIsShopkeeper(IsShopkeeper.no);
					try {
						tenantShopkeeperService.update(ts);
						String name = ExcelUtil.getMemberName(recommand);
						String tenantName = ts.getTenant().getShortName();
						pushService.publishSystemMessage(ts.getTenant(),
								ts.getMember(),
								SystemMessage.buyerBeInvited(name, tenantName));
						this.handleJsonResponse(response, true, "邀请成功");
					} catch (Exception e) {
						throw new Exception("邀请函发送失败了! 请联系系统客服");
					}
				} else {
					throw new Exception("无效邀请函!");
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			this.handleJsonResponse(response, false, "可邀请列表获取失败! 请电话联系客服");
		}
	}

	/**
	 * 店长获取全部可以邀请的好友
	 */
	/**
	 * 店长获取全部可以邀请的好友
	 */
	@RequestMapping(value = "/shopKeeperCanInvites")
	public void shopKeeperCanInvites(HttpServletResponse response,
			@RequestParam("token") String token,
			@RequestParam("tenantId") String tenantId) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				NToken nToken = inTokenBS.get(token);
				Long memberId = Long.parseLong(nToken.getMemberId());
				Member member = new Member();
				member.setId(memberId);
				Long tenantLongId = Long.parseLong(tenantId);
				Tenant tenant = tenantService.find(tenantLongId);
				JSONObject result = new JSONObject();
				// 获取图片
				String picUrl = tenant.getOpenShopImage();
				result.put("picUrl", picUrl);
				// 奖金比例
				TenantBonusSet tbs = tenantBonusSetService
						.getRegularTenantBonusSetByTenantId(tenantLongId);
				if(tbs==null){
					int rate = tbs.getRelativeSellBonusRate();
					result.put("rate", rate + "%");
				}else{
					result.put("rate", "");
				}

				// 获取权益
				TicketApplyCondition tac = ticketApplyConditionService
						.getTicketApplyConditionByTenant(tenant);
				int applyCondition = tac.getInvations();
				List<TicketSet> ticketSetList = ticketSetService.getTicketSet(
						tenantLongId, TicketSet.APPLY_SEND_TYPE,
						TicketSet.FLAG_USE);
				int ticketNum;
				if (ticketSetList.size() > 0)
					ticketNum = ticketSetList.get(0).getSendNum();
				else
					ticketNum = 0;
				result.put("ticketNum", ticketNum);
				result.put("applyCondition", applyCondition);
				// 条件
				TenantSellCondition tsc = conditionService
						.getRegularTenantSellConditionByTenantId(tenantLongId);
				int orderAmtCondition = tsc.getTradeNum();
				result.put("orderAmtCondition", orderAmtCondition);
				// 可邀请好友列表
				List<TenantShopkeeper> list = tenantShopkeeperService
						.getCapableByRecommand(tenant, member);
				List<OrderStatus> orderStatuses = new ArrayList<OrderStatus>();
				orderStatuses.add(OrderStatus.confirmed);
				orderStatuses.add(OrderStatus.completed);
				List<PaymentStatus> paymentStatuses = new ArrayList<PaymentStatus>();
				paymentStatuses.add(PaymentStatus.paid);
				JSONArray jsa = new JSONArray();
				for (TenantShopkeeper tenantShopkeeper : list) {
					JSONObject jso = new JSONObject();
					Member buyer = memberService.find(tenantShopkeeper
							.getMember().getId());
					String headImage = buyer.getHeadImg();
					if (StringUtil.isEmpty(headImage)) {
						headImage = picService.getDefaultHeadImage(buyer);
					}
					jso.put("headImage", headImage);
					jso.put("name", ExcelUtil.getMemberName(buyer));
					jso.put("mobile", buyer.getMobile());
					BigDecimal orderAmt = orderService
							.getHistoryOrderAmtByTenant(tenant, buyer,
									orderStatuses, paymentStatuses);
					jso.put("orderAmt", ExcelUtil.BigDemcialToString(orderAmt));
					jso.put("tenantShopKeeperId", tenantShopkeeper.getId() + "");
					jsa.add(jso);
				}
				result.put("canInviteList", jsa);
				JSONArray jsarray = new JSONArray();
				List<TenantShopkeeper> hasInvitedList = tenantShopkeeperService
						.getInvitedByRecommand(tenant, member);
				for (TenantShopkeeper tenantShopkeeper : hasInvitedList) {
					JSONObject jso = new JSONObject();
					Member buyer = memberService.find(tenantShopkeeper
							.getMember().getId());
					String headImage = buyer.getHeadImg();
					if (StringUtil.isEmpty(headImage)) {
						headImage = picService.getDefaultHeadImage(buyer);
					}
					jso.put("headImage", headImage);
					jso.put("name", ExcelUtil.getMemberName(buyer));
					jso.put("mobile", buyer.getMobile());
					BigDecimal orderAmt = orderService
							.getHistoryOrderAmtByTenant(tenant, buyer,
									orderStatuses, paymentStatuses);
					jso.put("orderAmt", ExcelUtil.BigDemcialToString(orderAmt));
					jso.put("tenantShopKeeperId", tenantShopkeeper.getId() + "");
					jsarray.add(jso);
				}
				result.put("hasInvitedList", jsarray);
				this.handleJsonResponse(response, true, "", result);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			this.handleJsonResponse(response, false, "可邀请列表获取失败! 请电话联系客服");
		}
	}

	/**
	 * 买家邀请函列表
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/memberInvites")
	public void memberInvites(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				NToken nToken = inTokenBS.get(token);
				Long memberId = Long.parseLong(nToken.getMemberId());
				JSONObject result = new JSONObject();
				// 获取买家邀请函列表图片
				String picUrl = picService.getMemberOpenShopImage();
				result.put("picUrl", picUrl);
				// 获取开店条件
				// String tenantName = tenant.getShortName();
				// String condition =
				// conditionService.getRegularTenantSellConditionByTenantId(new
				// Long(tenantId)).getTradeNum().toString();
				// result.put("tenantName", tenantName);
				// result.put("condition", condition);
				// 邀请函列表
				Member member = new Member();
				member.setId(memberId);
				TenantShopkeeper ts = tenantShopkeeperService
						.findShopKeeperByMemberId(memberId);
				if (ts == null) {
					result.put("isShopkeeper", "0");
					result.put("shopName", "");
				} else {
					result.put("isShopkeeper", "1");
					result.put("shopName", ts.getTenant().getName());
				}
				List<TenantShopkeeper> list = tenantShopkeeperService
						.getInvationByMember(member);
				JSONArray jsa = new JSONArray();
				for (TenantShopkeeper tenantShopkeeper : list) {
					JSONObject resultValue = new JSONObject();
					Member recommendMember = tenantShopkeeper
							.getRecommendMember();
					String headImage = recommendMember.getHeadImg();
					if (StringUtil.isEmpty(headImage)) {
						headImage = picService
								.getDefaultHeadImage(recommendMember);
					}
					resultValue.put("headImage", headImage + "");
					resultValue.put("mobile", recommendMember.getMobile() + "");
					resultValue.put("recommandName", tenantShopkeeper
							.getRecommendMember() == null ? ""
							: tenantShopkeeper.getRecommendMember().getName()
									+ "");
					resultValue.put("tenantId", tenantShopkeeper.getTenant()
							.getId() == null ? "" : tenantShopkeeper
							.getTenant().getId() + "");
					resultValue.put("tenantName", tenantShopkeeper.getTenant()
							.getShortName() == null ? "" : tenantShopkeeper
							.getTenant().getShortName() + "");
					resultValue.put("logo", tenantShopkeeper.getTenant()
							.getLogo() == null ? "" : tenantShopkeeper
							.getTenant().getLogo() + "");
					resultValue.put("invationImage", tenantShopkeeper
							.getTenant().getInvationImage() == null ? ""
							: tenantShopkeeper.getTenant().getInvationImage()
									+ "");
					resultValue.put("recommandId", tenantShopkeeper
							.getRecommendMember() == null ? ""
							: tenantShopkeeper.getRecommendMember().getId()
									+ "");

					resultValue.put("tenantShopkeeperId", tenantShopkeeper
							.getId() == null ? "" : tenantShopkeeper.getId()
							+ "");
					jsa.add(resultValue);
				}
				result.put("list", jsa);
				this.handleJsonResponse(response, true, "", result);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			this.handleJsonResponse(response, false, "受邀列表查询失败哟!快去联系客服咨询!");
		}
	}

	/**
	 * 店主邀请函
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/shopKeeperInvite")
	public void shopKeeperInvite(HttpServletResponse response,
			@RequestParam("shopKeeperId") String shopKeeperId,
			@RequestParam("tel") String tel) throws Exception {
		try {
			Member member = memberService.findByTel(tel);
			if (member == null) {
				member = new Member();
				member.setMobile(tel);
				memberService.save(member);
			}
			try {
				tenantShopkeeperService.inviteShopkeeper(
						new Long(shopKeeperId), member);
				this.handleJsonResponse(response, true, "成功受邀,快去开通店长赚取分享佣金吧!");
			} catch (BizException e) {
				log.info(tel + "用户已经接受过该店长邀请! 店长——ID" + shopKeeperId);
				this.handleJsonResponse(response, false, e.getMessage());
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			this.handleJsonResponse(response, false, "受邀失败哟!快去联系客服咨询!");
		}
	}

	/**
	 * 企业邀请函
	 */
	@RequestMapping(value = "/tenantInvite")
	public void tenantInvite(HttpServletResponse response,
			@RequestParam("tenantId") String tenantId,
			@RequestParam("tel") String tel) throws Exception {
		try {
			Member member = memberService.findByTel(tel);
			if (member == null) {
				member = new Member();
				member.setMobile(tel);
				memberService.save(member);
			}
			try {
				tenantShopkeeperService.inviteBeShopkeeper(new Long(tenantId),
						member.getId());
				this.handleJsonResponse(response, true, "成功受邀,快去成为店长赚取分享佣金吧!");
			} catch (BizException e) {
				this.handleJsonResponse(response, false, e.getMessage());
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			this.handleJsonResponse(response, false, "受邀失败哟!快去联系客服咨询!");
		}

	}

	/**
	 * 邀请函
	 */
	@RequestMapping(value = "/getInvitations")
	public void getInvitations(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {

			JSONArray jsonArray = new JSONArray();
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {

				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();
				if (StringUtils.isEmpty(memberId)) {
					throw new Exception("会员ID不能为空");
				}
				Member member = memberService.find(new Long(memberId));
				if (member != null) {

					List<TenantShopkeeper> tenantShopkeeperList = tenantShopkeeperService
							.findInvationsByMemberId(member.getId());
					if (tenantShopkeeperList != null) {
						if (tenantShopkeeperList.size() == 0) {
							throw new Exception("已是店长");
						}
						JSONObject resultValue;
						for (TenantShopkeeper tenantShopkeeper : tenantShopkeeperList) {
							resultValue = new JSONObject();
							resultValue
									.put("tenantId", tenantShopkeeper
											.getTenant().getId() == null ? ""
											: tenantShopkeeper.getTenant()
													.getId() + "");
							resultValue.put("tenantName", tenantShopkeeper
									.getTenant().getShortName() == null ? ""
									: tenantShopkeeper.getTenant()
											.getShortName() + "");
							resultValue.put("logo", tenantShopkeeper
									.getTenant().getLogo() == null ? ""
									: tenantShopkeeper.getTenant().getLogo()
											+ "");
							resultValue.put("invationImage",
									tenantShopkeeper.getTenant()
											.getInvationImage() == null ? ""
											: tenantShopkeeper.getTenant()
													.getInvationImage() + "");
							resultValue
									.put("recommandId", tenantShopkeeper
											.getRecommendMember() == null ? ""
											: tenantShopkeeper
													.getRecommendMember()
													.getId()
													+ "");
							resultValue
									.put("recommandName", tenantShopkeeper
											.getRecommendMember() == null ? ""
											: tenantShopkeeper
													.getRecommendMember()
													.getName()
													+ "");
							resultValue.put("tenantShopkeeperId",
									tenantShopkeeper.getId() == null ? ""
											: tenantShopkeeper.getId() + "");

							jsonArray.add(resultValue);
						}
					}
				}

				this.handleJsonArrayResponse(response, true, "", jsonArray);
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 成为店主
	 */
	@RequestMapping(value = "/becameShopKeeper")
	public void becameShopKeeper(HttpServletResponse response,
			@RequestParam("token") String token,
			@RequestParam("tenantId") String tenantId,
			@RequestParam("recommandId") String recommandId,
			@RequestParam("realName") String realName) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				NToken nToken = inTokenBS.get(token);
				Member member = memberService.find(new Long(nToken
						.getMemberId()));
				member.setName(realName);
				inTokenBS.becameShopKeeper(nToken, member, tenantId,
						recommandId);
				tenantShopkeeperService.deleteShopKeeperWithNo(member);
				Tenant tenant = tenantService.find(new Long(tenantId));
				this.handleJsonResponse(response, true,
						"恭喜您已经成为" + tenant.getName() + "的店长");
			}
		} catch (BizException bize) {
			this.handleJsonResponse(response, false, bize.getMessage());
		} catch (Exception e) {
			this.handleJsonResponse(response, false, "成为店长失败,请咨询客服!");
			e.printStackTrace();
		}
	}

	// /**
	// * 开通店长资格审核
	// */
	// @RequestMapping(value = "/checkBeShopkeeper")
	// public void checkShopKeeper(HttpServletResponse response,
	// @RequestParam("token") String token,
	// @RequestParam("tenantId") String tenantId)
	// throws Exception {
	//
	// try {
	// JSONObject jo = new JSONObject();
	// JSONArray jsa = new JSONArray();
	// if (!inTokenBS.isVaild(token)) {
	// this.handleJsonTokenResponse(response, false,
	// CacheUtil.getParamValueByName("TOKEN_INVALID"));
	// } else {
	// NToken nToken = inTokenBS.get(token);
	// Member member = memberService.find(new Long(nToken
	// .getMemberId()));
	// Long memberId =member.getId();
	// try {
	// List<TenantShopkeeper> list = tenantShopkeeperService.
	// findInvationsByMemberId(memberId, Long.valueOf(tenantId));
	// if(list==null){
	// this.handleJsonResponse(response, false, "您还没有资格成为店长");
	// }else{
	// String totalCharge ;
	// Owner ow =ownerService.getOwner(memberId);
	// if(ow ==null)totalCharge = "0";
	// else totalCharge =String.valueOf(ow.getTotalCharge()) ;
	// jo.put("totalCharge", totalCharge);
	// jo.put("tenantId", tenantId);
	//
	// for (TenantShopkeeper tenantShopkeeper : list) {
	// Member recommendMember = tenantShopkeeper.getRecommendMember();
	// if(recommendMember!=null){
	// JSONObject jsonObject = new JSONObject();
	// jsonObject.put("recommandId", String.valueOf(recommendMember.getId()));
	// jsonObject.put("recomamndName", recommendMember.getName());
	// jsa.add(jsonObject);
	// }
	// }
	// jo.put("recommandMan", jsa);
	// this.handleJsonResponse(response, true, "恭喜您可以成为店长啦! 快快开店赚取分享佣金吧!",jo);
	// }
	// } catch (BizException e) {
	// this.handleJsonResponse(response, false, e.getMessage());
	// }
	//
	// }
	// } catch (Exception e) {
	// this.handleJsonResponse(response, false, e.getMessage());
	// e.printStackTrace();
	// }
	// }
	/**
	 * 获取开店条件
	 * 
	 * @param response
	 * @param tenantId
	 * @throws Exception
	 */
	@RequestMapping(value = "/getOpenCondition")
	public void checkShopKeeper(HttpServletResponse response,
			@RequestParam("tenantId") String tenantId) throws Exception {
		try {
			JSONObject jo = new JSONObject();
			Long tenantLongId = new Long(tenantId);
			Tenant tenant = tenantService.find(tenantLongId);
			TenantSellCondition tec = conditionService
					.getRegularTenantSellConditionByTenantId(tenantLongId);
			jo.put("condition",
					"在" + tenant.getShortName() + "内购店累计购买" + tec.getTradeNum()
							+ "元的商品");
			this.handleJsonResponse(response, true, "", jo);
		} catch (Exception e) {
			this.handleJsonResponse(response, false, "该企业尚未设定开店条件!");
			log.error(e.getMessage());
		}
	}

	/**
	 * 开通店长资格审核
	 */

	@RequestMapping(value = "/checkBeShopkeeper")
	public void checkShopKeeper(HttpServletResponse response,
			@RequestParam("token") String token,
			@RequestParam("tenantId") String tenantId) throws Exception {
		try {
			JSONObject jo = new JSONObject();
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {

				NToken nToken = inTokenBS.get(token);
				Long memberId = new Long(nToken.getMemberId());
				Member member = memberService.find(memberId);
				try {
					TenantSellCondition tsc = conditionService
							.getRegularTenantSellConditionByTenantId(Long
									.parseLong(tenantId));
					jo.put("condition", tsc.getTradeNum() + "");
					TenantShopkeeper ts = tenantShopkeeperService
							.findShopKeeperByMemberId(memberId);
					if (ts != null) { // 已经是店长了
						jo.put("totalCharge", "0");
						jo.put("canApplyId", "");
						this.handleJsonResponse(response, false, "您已经是"
								+ ts.getTenant().getShortName() + "的店长", jo);
					} else {
						Tenant tenant = new Tenant();
						tenant.setId(Long.parseLong(tenantId));
						List<TenantShopkeeper> list = tenantShopkeeperService
								.getCanApplyByMember(tenant, member);
						if (list.size() > 0) {
							String totalCharge;
							Owner ow = ownerService.getOwner(memberId);
							if (ow == null)
								totalCharge = "0";
							else
								totalCharge = String.valueOf(ow
										.getTotalCharge());
							jo.put("totalCharge", totalCharge);
							jo.put("canApplyId", list.get(0).getId());
							this.handleJsonResponse(response, true,
									"恭喜您可以申请成为店长啦! 快快去申请开店赚取分享佣金吧!", jo);
						} else {
							jo.put("totalCharge", "0");
							jo.put("canApplyId", "");
							List<TenantShopkeeper> tenantCapable = tenantShopkeeperService
									.getTenantCapableByMember(tenant, member);
							List<TenantShopkeeper> tenantInvitations = tenantShopkeeperService
									.getInvationByTenantMember(tenant, member);
							if (tenantCapable.size() > 0) {
								this.handleJsonResponse(response, false,
										"您的店长申请正在审核中,请耐心等待", jo);
							} else if (tenantInvitations.size() > 0) {
								this.handleJsonResponse(response, false,
										"您的好友已经发邀请函给您啦，快去查看吧", jo);
							} else {
								List<TenantShopkeeper> recommandList = tenantShopkeeperService
										.getCapableByMember(tenant, member);
								if (recommandList.size() == 0) {
									this.handleJsonResponse(response, false,
											"您还不具店长资格", jo);
								} else {
									this.handleJsonResponse(response, false,
											"打电话给分享您券券的好友,让他们邀请你成为店长", jo);
								}
							}
						}
					}
				} catch (Exception e) {
					this.handleJsonResponse(response, false, e.getMessage());
				}
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 申请开店
	 * 
	 * @param response
	 * @param token
	 * @param canApplyId
	 * @throws Exception
	 */
	@RequestMapping(value = "/applyToShopKeeper")
	public void applyToShopKeeper(HttpServletResponse response,
			@RequestParam("token") String token,
			@RequestParam("canApplyId") String canApplyId) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				TenantShopkeeper ts = tenantShopkeeperService.find(Long
						.parseLong(canApplyId));
				NToken nToken = inTokenBS.get(token);
				Long memberId = new Long(nToken.getMemberId());
				if (ts.getMember().getId().equals(memberId)
						&& ts.getInvitedType().equals(InvitedType.tenant)
						&& ts.getIsShopkeeper().equals(IsShopkeeper.canApply)) {
					ts.setIsShopkeeper(IsShopkeeper.capable);
					try {
						tenantShopkeeperService.update(ts);
						this.handleJsonResponse(response, true,
								"恭喜您申请成功,请耐心等待后台审核!");
					} catch (Exception e) {
						throw new Exception("店长申请失败请联系客服姐姐!");
					}
				} else {
					throw new Exception("无效的店长申请!");
				}
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			log.error(e.getMessage());
		}
	}

	@RequestMapping(value = "/findInvationsByMemberId")
	public void findInvationsByMemberId(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));

			} else {
				NToken nToken = inTokenBS.get(token);
				String tenantId = nToken.getTenantId();
				Tenant tenant = tenantService.find(new Long(tenantId));
				String openShopImage = tenant.getOpenShopImage();
				String memberId = nToken.getMemberId();
				List<TenantShopkeeper> tenantShopkeeperList = tenantShopkeeperService
						.getInvationsByRecommendMemberId(new Long(memberId));

				JSONArray jsonArray = new JSONArray();
				JSONObject resultValue;
				JSONObject result = new JSONObject();
				Setting setting = SettingUtils.get();
				String siteUrl = setting.getSiteUrl();
				String shareHttp = siteUrl
						+ CacheUtil
								.getParamValueByName("SHOPKEEPER_INVATE_URL");
				ShareSet invitationTitleShareSet = shareSetService
						.getInvitaionShareTitleByTenant(tenant);
				ShareSet invitationContentShareSet = shareSetService
						.getInvitaionShareContentByTenant(tenant);
				String shareContent = invitationContentShareSet == null ? ""
						: invitationContentShareSet.getContent();
				String sharetTitle = invitationTitleShareSet == null ? ""
						: invitationTitleShareSet.getContent();
				shareHttp = shareHttp.replace(TENANTID, tenantId + "");
				shareHttp = shareHttp.replace(SHOPKEEPERID, memberId);
				result.put("openShopImage", openShopImage);
				result.put("shareHttp", shareHttp);
				result.put("shareContent", shareContent);
				result.put("shareImage", tenant.getLogo());
				result.put("shareTitle", sharetTitle);
				if (CollectionUtils.isNotEmpty(tenantShopkeeperList)) {
					Member member;
					String name;
					for (TenantShopkeeper tenantShopkeeper : tenantShopkeeperList) {
						member = tenantShopkeeper.getMember();
						if (member == null) {
							continue;
						}
						resultValue = new JSONObject();
						name = member.getName();
						if (StringUtils.isEmpty(name)) {
							name = member.getNickName();
						}
						resultValue.put("openDate", DateUtil.changeDateToStr(
								tenantShopkeeper.getOpenDate(),
								DateUtil.DOT_DISPLAY_DATE));
						resultValue.put("name", ExcelUtil.convertNull(name));
						if (StringUtil.isEmpty(member.getHeadImg())) {
							resultValue.put("headImg", ExcelUtil
									.convertNull(picService
											.getDefaultHeadImage(member)));
						} else {
							resultValue.put("headImg",
									ExcelUtil.convertNull(member.getHeadImg()));
						}
						resultValue.put("mobile",
								ExcelUtil.convertNull(member.getMobile()));

						jsonArray.add(resultValue);
					}
				}
				result.put("invations", jsonArray);
				this.handleJsonResponse(response, true, "", result);
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}

}
