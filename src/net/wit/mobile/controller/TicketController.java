package net.wit.mobile.controller;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jodd.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.wit.Setting;
import net.wit.entity.Member;
import net.wit.entity.ShareSet;
import net.wit.entity.Tenant;
import net.wit.entity.TenantShopkeeper;
import net.wit.entity.TenantTicket;
import net.wit.entity.Ticket;
import net.wit.entity.TicketApply;
import net.wit.entity.TicketApply.ApplyType;
import net.wit.entity.TicketApplyCondition;
import net.wit.entity.TicketCache;
import net.wit.entity.TicketSet;
import net.wit.mobile.bo.NToken;
import net.wit.mobile.service.INTokenBS;
import net.wit.mobile.service.impl.PushService;
import net.wit.mobile.util.CollectionsUtils;
import net.wit.mobile.util.rong.models.SystemMessage;
import net.wit.service.MemberService;
import net.wit.service.MemberTenantService;
import net.wit.service.PicService;
import net.wit.service.TenantService;
import net.wit.service.TenantShopkeeperService;
import net.wit.service.TenantTicketService;
import net.wit.service.TicketApplyConditionService;
import net.wit.service.TicketApplyService;
import net.wit.service.TicketCacheService;
import net.wit.service.TicketService;
import net.wit.service.TicketSetService;
import net.wit.service.impl.ShareSetService;
import net.wit.util.BizException;
import net.wit.util.CacheUtil;
import net.wit.util.DateUtil;
import net.wit.util.SettingUtils;
import net.wit.vo.SystemMessageVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA. User: ab Date: 15-9-13 Time: 下午1:56 To change
 * this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping(value = "/ticket")
public class TicketController extends BaseController {

	@Autowired
	private TicketSetService ticketSetService;
	
	@Autowired
	private INTokenBS inTokenBS;

	@Autowired
	private TicketService ticketService;

	@Autowired
	private TenantService tenantService;

	@Autowired
	private TenantTicketService tenantTicketService;

	@Autowired
	private TicketCacheService ticketCacheService;

	@Autowired
	private PicService picService;

	@Autowired
	private ShareSetService shareSetService;

	@Autowired
	private TicketApplyService ticketApplyService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private PushService pushService;
	
	@Autowired
	private MemberTenantService memberTenantService;
	
	@Autowired
	private TenantShopkeeperService tenantShopkeeperService;
	
	@Autowired
	private TicketApplyConditionService ticketApplyConditionService;

	private Logger log = LoggerFactory.getLogger(TicketController.class);

	private static final String SHARE_TICKET_URL = "${ticketId}";

	/**
	 * 分享页领券
	 * 
	 * @param response
	 * @param ticketId
	 * @param tel
	 * @throws Exception
	 */
	@RequestMapping(value = "/receiveTicket")
	public void receiveTicket(HttpServletResponse response,
			@RequestParam("ticketId") String ticketId,
			@RequestParam("tel") String tel) throws Exception {
		try {
			JSONObject resultValue = new JSONObject();
			if (!ticketService.isTicketNoUse(new Long(ticketId))) {
				throw new Exception("已被领取");
			}
			resultValue.put("newMember",
					ticketService.shareTicket(new Long(ticketId), tel) + "");
			this.handleJsonResponse(response, true, "成功领取,快去下载APP消费吧!",
					resultValue);

		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			log.error(e.getMessage());
		}
	}

	/**
	 * 发现中券领取动作
	 * 
	 * @param response
	 * @param ticketId
	 * @param token
	 * @throws Exception
	 */
	@RequestMapping(value = "/findTicketsReceive")
	public void findTicketsReceive(HttpServletResponse response,
			@RequestParam("ticketId") String ticketId,
			@RequestParam("token") String token) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));

			} else {
				JSONObject resultValue = new JSONObject();
				Long ticketLong = Long.parseLong(ticketId);
				Ticket ticket = ticketService.find(ticketLong);
				if (!ticketService.isTicketNoUse(ticketLong)) {
					throw new Exception("已被领取");
				}
				NToken nToken = inTokenBS.get(token);
				Member member = new Member();
				Long memberId = new Long(nToken.getMemberId());

				Long count = ticketService.countTicketByMemberId(memberId,
						ticket.getShopkeeper().getId(), ticket.getTenant()
								.getId(), Ticket.Status.recevied);
				if (count > 0) {
					throw new Exception("我分享的券你还没用哦! 使用后才可以再领取哦!");
				}
				member.setId(memberId);
				ticketService.updateTicket(new Long(ticketId), member,
						Ticket.Status.recevied);
				memberTenantService.addMemberTenant(member, ticket.getTenant().getId());
				this.handleJsonResponse(response, true, "", resultValue);

			}

		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			log.error(e.getMessage());
		}
	}

	/**
	 * VIP - 分享券中的查询
	 * 
	 * @param response
	 * @param token
	 * @param pageSize
	 * @param pageNo
	 * @throws Exception
	 */
	@RequestMapping(value = "/getInviteTicket")
	public void getInviteTicket(HttpServletResponse response,
			@RequestParam("token") String token,
			@RequestParam("pageSize") String pageSize,
			@RequestParam("pageNo") String pageNo) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));

			} else {
				JSONArray jsonArray = new JSONArray();
				JSONObject resultValue = new JSONObject();
				List<Ticket.Status> status = new ArrayList<Ticket.Status>();
				NToken nToken = inTokenBS.get(token);
				Long shopkeeperId = new Long(nToken.getMemberId());
				Long tenantId = new Long(nToken.getTenantId());
				status.add(Ticket.Status.nouse);
				List<Ticket> list = ticketService.getTicketByStatus(
						shopkeeperId, tenantId, status,
						Integer.parseInt(pageSize), Integer.parseInt(pageNo));
				if (CollectionsUtils.isNotEmpty(list)) {
					Tenant prevTenant = null;
					TenantTicket tenantTicket = null;
					for (Ticket t : list) {
						Tenant tenant = t.getTenant();
						if (tenant != prevTenant) {
							tenantTicket = tenantTicketService
									.findTenantTicketByTenantId(t.getTenant()
											.getId());
							prevTenant = tenant;
						}
						if (tenantTicket != null) {
							resultValue.put("ticketId", t.getId());
							resultValue.put("remaining", DateUtil.differDay(
									t.getExpiryDate(), new Date()));
							resultValue.put("logo",
									tenantTicket.getEffectiveImage());
							resultValue.put("expiryLogo",
									tenantTicket.getExpiryImage()); // 内购券失效logo
																	// tailExpiredImage
							resultValue.put("tailImage",
									tenantTicket.getTailImage()); // 增加内购券尾图
							resultValue.put("tailExpiredImage",
									tenantTicket.getTailExpiredImage());
							resultValue.put("content",
									tenantTicket.getContent());
							resultValue.put("tenantId", t.getTenant().getId()
									+ "");
							resultValue.put("tenantName", t.getTenant()
									.getShortName() + "");
							resultValue.put("shopKeeperId", t.getShopkeeper()
									.getId() + "");
							String shopKeeperName = t.getShopkeeper().getName()
									+ "";
							if (StringUtil.isEmpty(shopKeeperName))
								shopKeeperName = t.getShopkeeper()
										.getNickName();
							if (StringUtil.isEmpty(shopKeeperName))
								shopKeeperName = "指帮用户";
							resultValue.put("shopKeeperName", shopKeeperName
									+ "");
							// String headImg = t.getShopkeeper().getHeadImg();
							// if(StringUtil.isEmpty(headImg))
							// headImg
							// =picService.getDefaultHeadImage(t.getShopkeeper());
							// resultValue.put("shopKeeperlogo", headImg+"");
							// String shareImage =
							// t.getTenant().getShareImage();
							Setting setting = SettingUtils.get();
							String siteUrl = setting.getSiteUrl();
							// String shareContent =
							// CacheUtil.getParamValueByName("SHOPKEEPER_SHARE_CONTEXT");
							String shareHttp = siteUrl
									+ CacheUtil
											.getParamValueByName("SHOPKEEPER_SHARE_URL");
							// String sharetTitle =
							// CacheUtil.getParamValueByName("SHARE_TICKET_TITLE");

							ShareSet ticketTitleShareSet = shareSetService
									.getTicketShareTitleByTenant(tenant);
							ShareSet tiketContentShareSet = shareSetService
									.getTicketShareContentByTenant(tenant);
							String shareContent = tiketContentShareSet == null ? ""
									: tiketContentShareSet.getContent();
							String sharetTitle = ticketTitleShareSet == null ? ""
									: ticketTitleShareSet.getContent();
							shareHttp = shareHttp.replace(SHARE_TICKET_URL,
									t.getId() + "");
							resultValue.put("sharetTitle", sharetTitle);
							resultValue.put("shareHttp", shareHttp);
							resultValue.put("shareContent", shareContent);
							jsonArray.add(resultValue);
						}
					}
				}
				this.handleJsonResponse(response, true, "成功领取", jsonArray);
			}

		} catch (Exception e) {
			this.handleJsonResponse(response, false, "券券查询失败!");
			log.error(e.getMessage());
		}
	}

	/**
	 * 店主 - 历史券券
	 * 
	 * @param response
	 * @param token
	 * @param pageSize
	 * @param pageNo
	 * @throws Exception
	 */
	@RequestMapping(value = "/getTicketHistory")
	public void getTicketHistory(HttpServletResponse response,
			@RequestParam("token") String token,
			@RequestParam("pageSize") String pageSize,
			@RequestParam("pageNo") String pageNo) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));

			} else {
				JSONArray jsonArray = new JSONArray();
				JSONObject resultValue = new JSONObject();
				List<Ticket.Status> status = new ArrayList<Ticket.Status>();
				NToken nToken = inTokenBS.get(token);
				Long shopkeeperId = new Long(nToken.getMemberId());
				Long tenantId = new Long(nToken.getTenantId());
				status.add(Ticket.Status.recevied);
				status.add(Ticket.Status.used);
				List<Ticket> list = ticketService.getTicketByStatus(
						shopkeeperId, tenantId, status,
						Integer.parseInt(pageSize), Integer.parseInt(pageNo));
				if (list != null) {
					Tenant prevTenant = null;
					TenantTicket tenantTicket = null;

					for (Ticket t : list) {
						Tenant tenant = t.getTenant();
						if (tenant != prevTenant) {
							tenantTicket = tenantTicketService
									.findTenantTicketByTenantId(t.getTenant()
											.getId());
							prevTenant = tenant;
						}
						if (tenantTicket != null) {
							resultValue.put("ticketId", t.getId());
							resultValue.put("remaining", DateUtil.differDay(
									t.getExpiryDate(), new Date()));
							resultValue.put("logo",
									tenantTicket.getEffectiveImage());
							resultValue.put("expiryLogo",
									tenantTicket.getExpiryImage()); // 内购券失效logo
																	// tailExpiredImage
							resultValue.put("tailImage",
									tenantTicket.getTailImage()); // 增加内购券尾图
							resultValue.put("tailExpiredImage",
									tenantTicket.getTailExpiredImage());
							resultValue.put("content",
									tenantTicket.getContent());
							resultValue.put("status", t.getStatus());
							resultValue.put("receiver", t.getMember()
									.getMobile()); // 领券人
							jsonArray.add(resultValue);
						}
					}
					this.handleJsonResponse(response, true, "", jsonArray);
				} else {
					this.handleJsonResponse(response, true,
							"您还没有分享过去券券哦,快去分享赚取分享佣金吧!", jsonArray);
				}
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, "历史券券查询失败!");
			log.error(e.getMessage() + "历史券券查询失败!");
		}
	}

	/**
	 * 我的券券
	 * 
	 * @param response
	 * @param token
	 * @param pageSize
	 * @param pageNo
	 * @throws Exception
	 */
	@RequestMapping(value = "/getMyTicket")
	public void getMyTicket(HttpServletResponse response,
			@RequestParam("token") String token,
			@RequestParam("pageSize") String pageSize,
			@RequestParam("pageNo") String pageNo) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));

			} else {
				JSONArray jsonArray = new JSONArray();
				JSONObject resultValue = new JSONObject();
				List<Ticket.Status> status = new ArrayList<Ticket.Status>();
				NToken nToken = inTokenBS.get(token);
				Long memberId = new Long(nToken.getMemberId());

				status.add(Ticket.Status.recevied);
				status.add(Ticket.Status.used);
				status.add(Ticket.Status.expired);
				List<Ticket> list = ticketService.getMyTicketPagket(memberId,
						status, Integer.parseInt(pageSize),
						Integer.parseInt(pageNo));
				if (CollectionsUtils.isNotEmpty(list)) {
					Tenant prevTenant = null;
					TenantTicket tenantTicket = null;
					for (Ticket t : list) {
						Member shopkeeper = t.getShopkeeper();
						Tenant tenant = t.getTenant();
						if (tenant != prevTenant) {
							tenantTicket = tenantTicketService
									.findTenantTicketByTenantId(t.getTenant()
											.getId());
							prevTenant = tenant;
						}
						if (tenantTicket != null) {
							resultValue.put("ticketId", t.getId());
							resultValue.put("remaining", DateUtil.differDay(
									t.getExpiryDate(), new Date()));
							resultValue.put("logo",
									tenantTicket.getEffectiveImage());
							resultValue.put("expiryLogo",
									tenantTicket.getExpiryImage()); // 内购券失效logo
																	// tailExpiredImage
							resultValue.put("tailImage",
									tenantTicket.getTailImage()); // 增加内购券尾图
							resultValue.put("tailExpiredImage",
									tenantTicket.getTailExpiredImage());
							resultValue.put("content",
									tenantTicket.getContent());
							resultValue.put("status", t.getStatus());
							resultValue.put("tenantId", tenant.getId());
							resultValue.put("tenantShortName",
									tenant.getShortName() + "");
							resultValue.put("recommend",
									shopkeeper.getNickName() + "");
							String recommandName = shopkeeper.getName();
							if (StringUtils.isEmpty(recommandName)
									|| recommandName == null) {
								recommandName = shopkeeper.getNickName();
							}
							if (StringUtils.isEmpty(recommandName)
									|| recommandName == null)
								recommandName = "指帮用户";

							resultValue
									.put("recommendName", recommandName + "");
							resultValue.put("shopkeeperMobile",
									shopkeeper.getMobile());
							if(t.getStatus().ordinal()>Ticket.Status.recevied.ordinal()){
								Member member = new Member();
								member.setId(memberId);
								Long tenantId=tenant.getId();
								if(ticketApplyService.checkMemberCanApply(member, shopkeeper,tenantId)){
									resultValue.put("isApply", "1");
								}else{
									resultValue.put("isApply", "0");
								}
							}
							jsonArray.add(resultValue);
						}
					}
					this.handleJsonResponse(response, true, "", jsonArray);
				} else {
					this.handleJsonResponse(response, true, "券券为空,想办法领取吧!",
							jsonArray);
				}

			}

		} catch (Exception e) {
			this.handleJsonResponse(response, false, "券券查询失败!问问客服姐姐!");
			log.error(e.getMessage() + "券券查询失败!问问客服姐姐!");
		}
	}

	/**
	 * 发现券的查询
	 * 
	 * @param response
	 * @param token
	 * @throws Exception
	 */
	@RequestMapping(value = "/findTickets")
	public void findTickets(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));

			} else {
				JSONArray jsonArray = new JSONArray();
				JSONObject resultValue = new JSONObject();
				NToken nToken = inTokenBS.get(token);
				Long memberId = new Long(nToken.getMemberId());
				// 判断是否有券
				boolean isExTicketCahce = ticketCacheService
						.isTicketCacheExist(memberId);
				boolean isExistTicket = ticketService.isExistTicket(memberId);
				if (!isExistTicket && !isExTicketCahce) {// 判断 券表和券缓冲表
															// 券均为空才生成发现中的生成券
					List<Tenant> list = tenantService.getTenantAll();
					if (CollectionsUtils.isNotEmpty(list)) {// 根据企业生成
						for (Tenant tenant : list) {
							if (tenant.getFirstRentFreePeriod() == Tenant.IS_DISCOVER) { // 企业是发现企业才可以在发现中发券给新用户!
								Ticket ticket = new Ticket();
								Member shopkeeper = new Member();
								shopkeeper.setId(memberId);
								ticket.setExpiryDate(DateUtil
										.getLastDateOfMonth());
								// ticket.setMember(member);
								// ticket.setRemark(remark);
								ticket.setShopkeeper(shopkeeper);
								ticket.setStatus(Ticket.Status.nouse);
								ticket.setTenant(tenant);
								ticketService.save(ticket);
							}
						}
					}
				}
				// 查询可领券
				List<Ticket> list = ticketService.getTicketByShopkeeperId(
						memberId, Ticket.Status.nouse);
				if (CollectionsUtils.isNotEmpty(list)) {
					Map<Long, TenantTicket> ttMap = new HashMap<Long, TenantTicket>();
					TenantTicket tenantTicket = null;
					for (Ticket t : list) {
						// 获取企业券信息
						if (ttMap.get(t.getTenant().getId()) == null) {
							tenantTicket = tenantTicketService
									.findTenantTicketByTenantId(t.getTenant()
											.getId());
							ttMap.put(t.getTenant().getId(), tenantTicket);
						} else {
							tenantTicket = ttMap.get(t.getTenant().getId());
						}
						if (tenantTicket != null) {
							resultValue.put("ticketId", t.getId());
							resultValue.put("remaining", DateUtil.differDay(
									t.getExpiryDate(), new Date()));
							resultValue.put("logo",
									tenantTicket.getEffectiveImage());
							resultValue.put("expiryLogo",
									tenantTicket.getExpiryImage()); // 内购券失效logo
							resultValue.put("tailImage",
									tenantTicket.getTailImage()); // 增加内购券尾图
							resultValue.put("tailExpiredImage",
									tenantTicket.getTailExpiredImage()); // 失效尾图
																			// tailExpiredImage
							resultValue.put("content",
									tenantTicket.getContent());
							resultValue.put("status", t.getStatus());
							jsonArray.add(resultValue);
						}
					}
				}
				this.handleJsonResponse(response, true, "", jsonArray);
			}

		} catch (Exception e) {
			this.handleJsonResponse(response, false, "券券发现失败!问问客服姐姐!");
			log.error(e.getMessage() + "券券发现失败!问问客服姐姐!");
		}
	}

	/**
	 * 获取买家内购店的可用券数
	 * 
	 * @param response
	 * @param token
	 * @param tenantId
	 * @throws Exception
	 */
	@RequestMapping(value = "/getTicketCount")
	public void getTicketCount(HttpServletResponse response,
			@RequestParam("token") String token,
			@RequestParam("tenantId") String tenantId) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));

			} else {
				JSONObject resultValue = new JSONObject();
				NToken nToken = inTokenBS.get(token);
				Long memberId = new Long(nToken.getMemberId());

				Long count = ticketService.countTicketByMemberId(memberId,
						null, new Long(tenantId), Ticket.Status.recevied);

				resultValue.put("count", count);
				this.handleJsonResponse(response, true, "", resultValue);
			}

		} catch (Exception e) {
			this.handleJsonResponse(response, false, "券券为零,去问问客服姐姐");
			log.error(e.getMessage() + "券券失败!问问客服姐姐!");
		}
	}

	/**
	 * 结算页面获取可用券
	 * 
	 * @param response
	 * @param token
	 * @param tenantId
	 * @throws Exception
	 */
	@RequestMapping(value = "/getTicketSettle")
	public void getTicketSettle(HttpServletResponse response,
			@RequestParam("token") String token,
			@RequestParam("tenantId") String tenantId) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));

			} else {
				JSONArray jsonArray = new JSONArray();
				JSONObject resultValue = new JSONObject();
				NToken nToken = inTokenBS.get(token);
				Long memberId = new Long(nToken.getMemberId());

				List<Ticket> list = ticketService.getTicketSettle(memberId,
						new Long(tenantId), Ticket.Status.recevied);
				if (CollectionsUtils.isNotEmpty(list)) {
					for (Ticket t : list) {
						Member shopkeeper = t.getShopkeeper();
						resultValue.put("ticketId", t.getId());
						resultValue
								.put("shopKeeperId", shopkeeper.getId() + "");
						String name = shopkeeper.getName();
						if (StringUtils.isEmpty(name)) {
							name = shopkeeper.getNickName();
						}
						if (StringUtils.isEmpty(name)) {
							name = "指帮用户";
						}
						resultValue.put("shopkeeperName", name);
						jsonArray.add(resultValue);
					}
					this.handleJsonResponse(response, true, "", jsonArray);
				} else {
					this.handleJsonResponse(response, true, "没有券券用啦,快去打电话求券吧!",
							jsonArray);
				}

			}

		} catch (Exception e) {
			this.handleJsonResponse(response, false, "没有券券用啦,快去打电话求券吧!");
			log.error(e.getMessage() + "券券失败!问问客服姐姐!");
		}
	}

	/**
	 * 店主查询未领取券数
	 * 
	 * @param response
	 * @param token
	 * @throws Exception
	 */
	@RequestMapping(value = "/getshopkeeperNoUseCount")
	public void getshopkeeperNoUseCount(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));

			} else {
				JSONObject resultValue = new JSONObject();
				NToken nToken = inTokenBS.get(token);
				Long shopkeeperId = new Long(nToken.getMemberId());
				TenantShopkeeper ts = tenantShopkeeperService.findShopKeeperByMemberId(shopkeeperId);
				TicketSet newShopKeeperticketSet = this.ticketSetService.getNewShopKeeperTicketSetByTenantId(ts.getTenant().getId());
				TicketSet applyticketSet = this.ticketSetService.getApplyTicketSetByTenantId(ts.getTenant().getId());
				String picUrl =  picService.getTickShareImageByTenant(ts.getTenant());
				Long count = ticketCacheService.getshopkeeperNoUseCount(
						shopkeeperId, TicketCache.TICKETCACHE_NORECEIVESTATUS);
				resultValue.put("applyCount", applyticketSet.getSendNum()+"");
				resultValue.put("newShopKeeper", newShopKeeperticketSet.getSendNum()+"");
				resultValue.put("picUrl", String.valueOf(picUrl));
				resultValue.put("count", String.valueOf(count));
				this.handleJsonResponse(response, true, "", resultValue);
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, "没有找到可领用的券!");
			log.error("没有找到可领用的券!");
		}
	}

	/**
	 * 店主领券
	 * 
	 * @param response
	 * @param token
	 * @throws Exception
	 */
	@RequestMapping(value = "/getshopkeeperNoUse")
	public void getshopkeeperNoUse(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));

			} else {
				JSONObject resultValue = new JSONObject();
				NToken nToken = inTokenBS.get(token);
				Long shopkeeperId = new Long(nToken.getMemberId());

				ticketCacheService.toReceiveTicket(shopkeeperId);

				this.handleJsonResponse(response, true, "领取成功", resultValue);
			}

		} catch (Exception e) {
			this.handleJsonResponse(response, false, "领取失败!");
			log.error(e.getMessage());
		}
	}

	/**
	 * 买家向店主申请券
	 * @param response
	 * @param token
	 * @param tenantId
	 * @param ownerId
	 * @throws Exception
	 */
	@RequestMapping(value = "/applyTicketByBuyer")
	public void applyTicketByBuyer(HttpServletResponse response,
			@RequestParam("token") String token,
			@RequestParam("tenantId") String tenantId,
			@RequestParam("ticketId") String ticketId) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				try {
					Ticket ticket = ticketService.find(Long.parseLong(ticketId));
					if(ticket == null) throw new Exception("申请的券为无效券");
					NToken nToken = inTokenBS.get(token);
					Member member = new Member();
					member.setId(new Long(nToken.getMemberId()));
					Member owner =ticket.getShopkeeper();
					Tenant tenant = tenantService.find(new Long(tenantId));					
					boolean bool = this.ticketApplyService.buyerTicketApply(
							member, owner, tenant);
					if (bool) {
						member = memberService.find(member.getId());
						String name = "";
						if (StringUtil.isEmpty(member.getName())) {
							name = member.getMobile();
						} else {
							name = member.getName();
						}
						pushService.publishSystemMessage(tenant, member,
								SystemMessage.shopKeeperTicketApplyMsg(name));
						this.handleJsonResponse(response, true,
								"券券申请成功,着急就打个电话催催他？");
					} else {
						this.handleJsonResponse(response, false,
								"券券申请失败，您已经跟他申请过了哦，着急就打个电话催催他？");
					}
				} catch (BizException e) {
					throw new Exception(e.getMessage());
				}
			}

		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			log.error(e.getMessage());
		}
	}
/**
 * 店主处理券的申请
 * @param response
 * @param applyId
 * @param applyStatus
 * @throws Exception
 */
	@RequestMapping(value = "/processTicketApply")
	public void processTicketApply(HttpServletResponse response,@RequestParam("token") String token,
			@RequestParam("applyId") String applyId,
			@RequestParam("applyStatus") String applyStatus) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
			Long[] ids = new Long[1];
			ids[0] = Long.parseLong(applyId);
			List<SystemMessageVO> list;
			if (Integer.parseInt(applyStatus) == TicketApply.ApplyStatus.confirmed
					.ordinal()) {
				try {
					NToken nToken = inTokenBS.get(token);
					TicketApply ticketApply = ticketApplyService.find(Long.parseLong(applyId));
					if(!ticketApply.getOwner().getId().equals(new Long(nToken.getMemberId()))){
						throw new Exception("用户信息与申请不一致！ 非法请求！");				
					}
					list = ticketApplyService.processTicketApplys(ids,
							TicketApply.ApplyStatus.confirmed);
					if (list.size() > 0) {
						for (SystemMessageVO vo : list) {
							pushService.publishSystemMessage(vo);
						}
						this.handleJsonResponse(response, true, "券券分享成功！");
					} else {
						this.handleJsonResponse(response, false,
								"您已经没有可以分享的券券了，快去试试手气申请券券");
					}
				} catch (BizException e) {
					throw new Exception(e.getMessage());
				}
			} else {
				list = ticketApplyService.processTicketApplys(ids,
						TicketApply.ApplyStatus.rejected);
				if (list.size() > 0) {
					for (SystemMessageVO vo : list) {
						pushService.publishSystemMessage(vo);
					}
					this.handleJsonResponse(response, true, "您好友的券券申请，已经被您拒绝了！");
				} else {
					this.handleJsonResponse(response, false, "您好友的券券申请，拒绝失败了！");
				}
			}
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			log.error(e.getMessage());
		}
	}
	
	/**
	 * VIP申请券券；
	 * @param response
	 * @param token
	 * @param tenantId
	 * @throws Exception
	 */
	@RequestMapping(value = "/ownerApplyTicket")
	public void ownerApplyTicket(HttpServletResponse response,
			@RequestParam("token") String token,@RequestParam("tenantId") String tenantId) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				NToken nToken = inTokenBS.get(token);
				Member owner = new Member();
				owner.setId(Long.parseLong(nToken.getMemberId()));
				Tenant tenant = new Tenant();
				tenant.setId(Long.parseLong(tenantId));
				if(ticketApplyService.ownerTicketApply(owner, tenant)){
					this.handleJsonResponse(response, true, "券券申请成功，请等待客服处理");
				}else{
					this.handleJsonResponse(response, false, "券券申请失败");
				}
			}
		}catch(Exception e){
			this.handleJsonResponse(response, false,e.getMessage());
			log.error(e.getMessage());
		}
	}
	
	
	

	/**
	 * 获取我的好友券券申请列表
	 * 
	 * @param response
	 * @param token
	 * @throws Exception
	 */
	@RequestMapping(value = "/getTicketApplyList")
	public void getTicketApplyList(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				JSONArray results = new JSONArray();
				NToken nToken = inTokenBS.get(token);
				Member owner = new Member();
				owner.setId(new Long(nToken.getMemberId()));
				List<TicketApply> list = ticketApplyService
						.getTicketApplyByOwner(owner,ApplyType.memberApplyToShopKeeper);
				for (TicketApply ticketApply : list) {
					JSONObject js = new JSONObject();
					Member  member = ticketApply.getMember();
					js.put("applyId", ticketApply.getId().toString());
					js.put("applyDate", DateUtil.changeDateToStr(ticketApply.getCreateDate(), DateUtil.DOT_DISPLAY_DATE));
					if(StringUtil.isEmpty(member.getNickName())){
						js.put("nickName", "指帮用户");
					}else{
						js.put("nickName",member.getNickName());
					}
					js.put("mobile",member.getMobile());
					if(StringUtil.isEmpty(member.getHeadImg())){
						js.put("headImg", picService.getDefaultHeadImage(member));
					}else js.put("headImg", member.getHeadImg());
					results.add(js);
				}
				this.handleJsonResponse(response, true, "",results);
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			log.error(e.getMessage());
		}
	}
	
	
	
	
	
	@RequestMapping(value = "/getOwnerTicketApplyPage")
	public void getOwnerTicketApplyPage(HttpServletResponse response,
			@RequestParam("token") String token,@RequestParam("tenantId")String tenantId)throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				JSONObject result = new JSONObject();
				NToken nToken = inTokenBS.get(token);
				Member owner = new Member();
				owner.setId(new Long(nToken.getMemberId()));
				Tenant tenant = new Tenant();
				tenant.setId(new Long(tenantId));
				TicketApplyCondition tac = ticketApplyConditionService.getTicketApplyConditionByTenant(tenant);
				if(tac==null){
					result.put("autoRejectDays", 0);
					result.put("invations", 0);
					result.put("orders", 0);
					result.put("canApplyTimes", 0);
					result.put("buttonStr", "未开通券券申请");
				}else{
					int autoRejectDays = tac.getAutoRejectDays();
					int invations = tac.getInvations();
					int orders = tac.getTicketUsedTimes();
					int canApplyTimes = this.ticketApplyService.getOwnerCanApplyTimes(owner, tenant);
					String buttonStr = "";
					if(canApplyTimes>0){
						buttonStr = "立即申请（"+canApplyTimes+"）";
					}else{
						int appledTimes = this.ticketApplyService.getTicketApplyByOwner(owner, ApplyType.shopkeeperApply).size(); //获取VIP向企业正在申请的条数
						buttonStr = appledTimes>0?"等待客服发券":"您当前无申请资格";
					}
					result.put("autoRejectDays", autoRejectDays);
					result.put("invations", invations);
					result.put("orders", orders);
					result.put("canApplyTimes", canApplyTimes);
					result.put("buttonStr", buttonStr);
				}
				this.handleJsonResponse(response, true, "",result);
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			log.error(e.getMessage());
		}
	}
}
