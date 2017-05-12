package net.wit.mobile.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jodd.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.wit.entity.Rent;
import net.wit.entity.TenantShopkeeper;
import net.wit.mobile.bo.NToken;
import net.wit.mobile.cache.CacheUtil;
import net.wit.mobile.service.INTokenBS;
import net.wit.plugin.PaymentPlugin;
import net.wit.plugin.alipayMobile.util.AlipaySubmit;
import net.wit.service.PluginService;
import net.wit.service.RentService;
import net.wit.service.TenantShopkeeperService;
import net.wit.util.BizException;
import net.wit.util.DateUtil;
import net.wit.util.ExcelUtil;

/**
 * Created with IntelliJ IDEA. User: ab Date: 15-9-13 Time: 下午1:56 To change
 * this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping(value = "/rent")
public class RentController extends BaseController {

	@Autowired
	private INTokenBS inTokenBS;

	@Autowired
	private TenantShopkeeperService tenantShopkeeperService;

	@Autowired
	private RentService rentService;
	
	@Autowired
	private PluginService pluginService;

	private Logger log = LoggerFactory.getLogger(RentController.class);

	/**
	 * 店主- 获取下个交租日剩余天数
	 * 
	 * @param response
	 * @param token
	 * @throws Exception
	 */
	@RequestMapping(value = "/getNextRentDays")
	public void getNextRentDays(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				JSONObject resultValue = new JSONObject();
				NToken nToken = inTokenBS.get(token);
				Long shopkeeperId = new Long(nToken.getMemberId()); // 获取店主ID
				int nextDays = rentService.calNextRentDate(shopkeeperId); // 根据店主id		
				TenantShopkeeper ts = tenantShopkeeperService.getTenantByShopKeeper(shopkeeperId);
				String opendate = DateUtil.changeDateToStr(ts.getOpenDate(), DateUtil.DOT_DISPLAY_DATE);
				String rentPeriod = "";
				try{
					Rent rent = rentService.LastChargeRent(shopkeeperId);
					if(rent==null){
						rentPeriod = "未有缴费记录!";    //交租
					}else{
						String lastDate  =rent.getRentDate();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
						Date date = sdf.parse(lastDate);
						lastDate =  DateUtil.changeDateToStr(DateUtil.getLastDayOfMonth(date), DateUtil.DOT_DISPLAY_DATE);
						rentPeriod = opendate+"至"+lastDate;
					}
				}catch(Exception e){
					rentPeriod = "未有缴费记录!";
				}
				resultValue.put("nextDays", nextDays+"");
				resultValue.put("rentPeriod", rentPeriod);
				this.handleJsonResponse(response, true, "", resultValue);
			}

		} catch (Exception e) {
			if (e.getClass().equals(BizException.class)) {
				this.handleJsonResponse(response, false, e.getMessage());
				log.error("getNextRentDays :" + e.getMessage());
			} else {
				this.handleJsonResponse(response, false, "查询失败");
				log.error("getNextRentDays :" + e.getMessage());
			}
		}
	}

	/**
	 * 店主- 交租
	 * 
	 * @param response
	 * @param token
	 * @param rent
	 * @param amount
	 * @throws Exception
	 */
	@RequestMapping(value = "/payRent")
	public void payRent(HttpServletResponse response,
			@RequestParam("token") String token,
			@RequestParam("amount") String amount) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)){
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
				NToken nToken = inTokenBS.get(token);
				Long memberId = new Long(nToken.getMemberId()); // 获取店主ID
				BigDecimal platfromRent  = new BigDecimal(CacheUtil.getParamValueByName("PLATFROM_RENT"));
				BigDecimal rent  = platfromRent.multiply( new BigDecimal(amount));
				String txNo = rentService.payRent(memberId, rent,
						Integer.parseInt(amount));
				
				// 调用支付接口
				JSONObject resultValue = new JSONObject();
				Map<String, String> params = new HashMap<String, String>();
				PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin("alipayMobilePlugin");
				String rentNotifyUrl = paymentPlugin.getAttribute("rentNotifyUrl");
				
				params.put("amount", rent.setScale(2, BigDecimal.ROUND_HALF_UP).toString());// 订单金额
				params.put("notify_url", rentNotifyUrl);// 后台通知地址
				params.put("orderNo", txNo);// 订单号 TODO
				params.put("subject", "速卖通费用商品");// 商品名称
				params.put("partner", CacheUtil.getParamValueByName("PARTNER".toUpperCase()));// 获取平台的支付宝PID
				params.put("privateKey", CacheUtil.getParamValueByName("PRIVATE_KEY".toUpperCase()));// 获取平台的支付宝私钥
				params.put("seller_id", CacheUtil.getParamValueByName("SELLER_ID".toUpperCase()));// 支付宝账号（邮箱）

				Map<String, String> result = AlipaySubmit.buildRequest(params);// 调用支付宝进行支付

				resultValue.put("resultStr", ExcelUtil.convertNull(result.get("resultStr")));
				// 返回给客户端的值
				log.info("resultStr:" + result.get("resultStr"));
				this.handleJsonResponse(response, true, "", resultValue);
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, "缴费失败! 请联系平台客服咨询");  //交租
		}
	}

	private Boolean checkRent(BigDecimal rents, int amount) {
		try {
			BigDecimal rent = rents.divide(new BigDecimal(amount));
			if (rent.equals(new BigDecimal("30")))
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}

	}

//	/**
//	 * 店主- 租金流水
//	 * 
//	 * @param response
//	 * @param token
//	 * @throws Exception
//	 */
//	@RequestMapping(value = "/rentDetails")
//	public void rentDetails(HttpServletResponse response,
//			@RequestParam("token") String token) throws Exception {
//		try {
//			if (!inTokenBS.isVaild(token))
//				this.handleJsonTokenResponse(response, false,
//						CacheUtil.getParamValueByName("TOKEN_INVALID"));
//			else {
//				NToken nToken = inTokenBS.get(token);
//				Long memberId = new Long(nToken.getMemberId()); // 获取店主ID
//				List<Rent> list = rentService.rentInfo(memberId);
//				JSONArray results = new JSONArray();
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
//				for (Rent rent : list) {
//					JSONObject jso = new JSONObject();
//					Date date = sdf.parse(rent.getRentDate());
//					String rentDate = DateUtil.changeDateToStr(date,
//							DateUtil.CN_DISPLAY_DATE_MONTH); // 店租月
//					String payRentDate = DateUtil.changeDateToStr(
//							rent.getModifyDate(), DateUtil.CN_DISPLAY_DATE); // 交租日
//					jso.put("rentDate", rentDate);
//					BigDecimal rentDecimal = new BigDecimal(CacheUtil.getParamValueByName("PLATFROM_RENT"));
//					if (rent.getStatus().equals(Rent.Status.notCharge)) {
//						jso.put("rentStat", "未交租");
//						jso.put("actual", "" + rent.getRent() + "");
//						jso.put("system", "0.00");
//						jso.put("rent", rentDecimal+"");
//						jso.put("tips", "截止该月10日未交租,账户冻结不可提现");
//					} else if (rent.getStatus().equals(Rent.Status.charged)) {
//						jso.put("rentStat", "已交租");
//						jso.put("actual", "" + rent.getRent() + "");
//						jso.put("system", "0.00");
//						jso.put("rent", rentDecimal+"");
//						jso.put("tips", "交租时间 " + payRentDate);
//					} else if (rent.getStatus().equals(Rent.Status.system)) {
//						jso.put("rentStat", "系统免租");
//						jso.put("actual", "0.00");
//						jso.put("system", "" + rent.getRent() + "");
//						jso.put("rent", rentDecimal+"");
//						jso.put("tips", "交租时间 " + payRentDate);
//					}
//					results.add(jso);
//				}
//				this.handleJsonResponse(response, true, "", results);
//			}
//		} catch (Exception e) {
//			if (e.getClass().equals(BizException.class)) {
//				this.handleJsonResponse(response, false, e.getMessage());
//				log.error("payRentDetails :" + e.getMessage());
//			} else {
//				this.handleJsonResponse(response, false, "查询失败");
//				log.error("payRentDetails :" + e.getMessage());
//			}
//		}
//	}
	
	
	
	/**
	 * 店主- 租金流水
	 * 
	 * @param response
	 * @param token
	 * @throws Exception
	 */
	@RequestMapping(value = "/rentDetails")
	public void rentDetails(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {
			if (!inTokenBS.isVaild(token))
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			else {
				NToken nToken = inTokenBS.get(token);
				Long memberId = new Long(nToken.getMemberId()); // 获取店主ID
				List<Rent> list = rentService.rentInfo(memberId);
				JSONArray results = new JSONArray();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
				for (Rent rent : list) {
					JSONObject jso = new JSONObject();
					Date date = sdf.parse(rent.getRentDate());
					String rentDate = DateUtil.changeDateToStr(date,
							DateUtil.CN_DISPLAY_DATE_MONTH); // 店租月
					String payRentDate = DateUtil.changeDateToStr(
							rent.getModifyDate(), DateUtil.CN_DISPLAY_DATE); // 交租日
					jso.put("rentDate", rentDate);
					BigDecimal rentDecimal = new BigDecimal(CacheUtil.getParamValueByName("PLATFROM_RENT"));
					if (rent.getStatus().equals(Rent.Status.notCharge)) {
						jso.put("rentStat", "未缴费");
						jso.put("actual", "" + rent.getRent() + "");
						jso.put("system", "0.00");
						jso.put("rent", rentDecimal+"");
						jso.put("tips", "截止该月10日未缴费,账户冻结不可提现");
					} else if (rent.getStatus().equals(Rent.Status.charged)) {
						jso.put("rentStat", "已缴费");
						jso.put("actual", "" + rent.getRent() + "");
						jso.put("system", "0.00");
						jso.put("rent", rentDecimal+"");
						jso.put("tips", "缴费时间 " + payRentDate);
					} else if (rent.getStatus().equals(Rent.Status.system)) {
						jso.put("rentStat", "系统缴费");
						jso.put("actual", "0.00");
						jso.put("system", "" + rent.getRent() + "");
						jso.put("rent", rentDecimal+"");
						jso.put("tips", "缴费时间 " + payRentDate);
					}
					results.add(jso);
				}
				this.handleJsonResponse(response, true, "", results);
			}
		} catch (Exception e) {
			if (e.getClass().equals(BizException.class)) {
				this.handleJsonResponse(response, false, e.getMessage());
				log.error("payRentDetails :" + e.getMessage());
			} else {
				this.handleJsonResponse(response, false, "查询失败");
				log.error("payRentDetails :" + e.getMessage());
			}
		}
	}
	
	

	/**
	 * 店主- 交租明细
	 * 
	 * @param response
	 * @param token
	 * @throws Exception
	 */
	@RequestMapping(value = "/payRentDetails")
	public void payRentDetails(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {
			if (!inTokenBS.isVaild(token))
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));
			else {
				NToken nToken = inTokenBS.get(token);
				Long memberId = new Long(nToken.getMemberId()); // 获取店主ID
				List<Rent> list = rentService.rentInfo(memberId);
				String preTxNo = "";
				List<JSONObject> jsList = new ArrayList<JSONObject>();
				JSONArray jsa = new JSONArray();
				BigDecimal rentDecimal = new BigDecimal(CacheUtil.getParamValueByName("PLATFROM_RENT"));
				for (Rent rent : list) {
					if(!rent.getStatus().equals(Rent.Status.charged)) continue;
					if (StringUtil.isEmpty(rent.getTxNo())||rent.getStatus().equals(Rent.Status.system))
						continue; // 交易编号为空 即未交租状态 跳过.
					if (!preTxNo.equals(rent.getTxNo())) {
						preTxNo = rent.getTxNo();
						JSONObject js = new JSONObject();
						String payRentDate = DateUtil.changeDateToStr(
								rent.getModifyDate(), DateUtil.CN_DISPLAY_DATE_FULL);
						js.put("payRentDate", payRentDate);
						js.put("months", 1+"");
						js.put("rent", rentDecimal+"");
						jsList.add(js);
					} else {
						int index = jsList.size() - 1;
						int months =1+ Integer.parseInt(String.valueOf(jsList.get(index).get("months")));
						BigDecimal rents = new BigDecimal(String.valueOf(jsList.get(index).get("rent"))).add(rentDecimal);
						jsList.get(index).put("months", months+"");
						jsList.get(index).put("rent", rents+"");
					}
				}
				for (JSONObject object : jsList) {
					jsa.add(object);
				}
				this.handleJsonResponse(response, true, "", jsa);
			}
		} catch (Exception e) {
			if (e.getClass().equals(BizException.class)) {
				this.handleJsonResponse(response, false, e.getMessage());
				log.error("payRentDetails :" + e.getMessage());
			} else {
				this.handleJsonResponse(response, false, "查询失败");
				log.error("payRentDetails :" + e.getMessage());
			}
		}
	}
}
