package net.wit.mobile.controller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.wit.entity.TenantRenovation;
import net.wit.entity.TenantShopkeeper;
import net.wit.mobile.bo.NToken;
import net.wit.mobile.service.INTokenBS;
import net.wit.service.TenantRenovationService;
import net.wit.service.TenantShopkeeperService;
import net.wit.sms.service.SmsService;
import net.wit.util.CacheUtil;
import net.wit.util.ExcelUtil;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-13
 * Time: 下午1:56
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping(value = "/shopKeeper")
public class ShopKeeperController extends BaseController {

    @Autowired
    private INTokenBS inTokenBS;

    @Autowired
    private TenantRenovationService tenantRenovationService;
    
    @Autowired
    private TenantShopkeeperService tenantShopkeeperService;

    @Autowired
    private SmsService smsService;
/**
 * 
 * @param response
 * @param token
 * @throws Exception
 */
    @RequestMapping(value = "/getBanner")
        public void getBanner(HttpServletResponse response,
                          @RequestParam("token") String token) throws Exception{
            try {
                if (!inTokenBS.isVaild(token)) {
                    this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));

                } else {
                	Long memberId = new Long(inTokenBS.get(token).getMemberId());
                	TenantShopkeeper ts = tenantShopkeeperService.getTenantByShopKeeper(memberId);
                	
                    JSONArray jsonArray = new JSONArray();
                    JSONObject resultValue = new JSONObject();
                    List<TenantRenovation> tenantRenovationList = tenantRenovationService.getTenantRenovationByTenantId(ts.getTenant().getId());
                    if (CollectionUtils.isNotEmpty(tenantRenovationList)) {
                        for (TenantRenovation tenantRenovation : tenantRenovationList) {
                        	resultValue.put("picUrl", ExcelUtil.convertNull(tenantRenovation.getPicId()));
                        	if(tenantRenovation.getProduct()!=null){
                                resultValue.put("type", ExcelUtil.convertNull(tenantRenovation.getSkipType()));
                                resultValue.put("goodId", ExcelUtil.convertNull(tenantRenovation.getProduct().getId()));
                                resultValue.put("url", ExcelUtil.convertNull(tenantRenovation.getUrl()));
                        	}else{
                                resultValue.put("type", ExcelUtil.convertNull(tenantRenovation.getSkipType()));
                                resultValue.put("goodId", "");
                                resultValue.put("url", ExcelUtil.convertNull(tenantRenovation.getUrl()));
                        	}
                            
                            jsonArray.add(resultValue);
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
     * banner 跳转
     * @param response
     * @param token
     * @param tenantId
     * @throws Exception
     */
    @RequestMapping(value = "/getBannerWithTenant")
    public void getBanner(HttpServletResponse response,
                      @RequestParam("token") String token,@RequestParam("tenantId") String tenantId) throws Exception{
        try {
            if (!inTokenBS.isVaild(token)) {
                this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));

            } else {
//            	Long memberId = new Long(inTokenBS.get(token).getMemberId());
//            	TenantShopkeeper ts = tenantShopkeeperService.getTenantByShopKeeper(memberId);
            	
                JSONArray jsonArray = new JSONArray();
                JSONObject resultValue = new JSONObject();
                List<TenantRenovation> tenantRenovationList = tenantRenovationService.getTenantRenovationByTenantId(new Long(tenantId));
                if (CollectionUtils.isNotEmpty(tenantRenovationList)) {
                    for (TenantRenovation tenantRenovation : tenantRenovationList) {

                    	resultValue.put("picUrl", ExcelUtil.convertNull(tenantRenovation.getPicId()));
                    	if(tenantRenovation.getProduct()!=null){
                            resultValue.put("type", ExcelUtil.convertNull(tenantRenovation.getSkipType()));
                            resultValue.put("goodId", ExcelUtil.convertNull(tenantRenovation.getProduct().getId()));
                            resultValue.put("url", ExcelUtil.convertNull(tenantRenovation.getUrl()));
                    	}else{
                            resultValue.put("type", ExcelUtil.convertNull(tenantRenovation.getSkipType()));
                            resultValue.put("goodId", "");
                            resultValue.put("url", ExcelUtil.convertNull(tenantRenovation.getUrl()));
                    	}
                        
                        jsonArray.add(resultValue);
                    }
                }
                this.handleJsonArrayResponse(response, true, "", jsonArray);
            }


        } catch (Exception e) {
            this.handleJsonResponse(response, false, e.getMessage());
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/getUnbundlingAuthCode")
        public void getUnbundlingAuthCode(HttpServletResponse response,
                          @RequestParam("token") String token) throws Exception{
            try {
                if (!inTokenBS.isVaild(token)) {
                    this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));

                } else {
                    NToken nToken = inTokenBS.get(token);
                    String tel = nToken.getTel();
                    String content = "店长解绑验证码";
                    if (smsService.getAuthCodeCZ(tel)) {
                        this.handleJsonResponse(response, true, "获取验证码成功");
                    }
                }
            } catch (Exception e) {
                this.handleJsonResponse(response, false, e.getMessage());
                e.printStackTrace();
            }
        }
/**
 * 解绑
 * @param response
 * @param token
 * @param authCode
 * @throws Exception
 */
    @RequestMapping(value = "/unbundling")
        public void unbundling(HttpServletResponse response,
                          @RequestParam("token") String token, @RequestParam("authCode") String authCode) throws Exception{
            try {
                if (!inTokenBS.isVaild(token)) {
                    this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));

                } else {
                    NToken nToken = inTokenBS.get(token);
                    String tel = nToken.getTel();

                    if (smsService.validCode(tel, authCode)) {
                        String memberId = nToken.getMemberId();
                        String tenantId = nToken.getTenantId();
                        tenantShopkeeperService.deleteShopKeeperByMemberIdAndTenantId(memberId, tenantId);
                        this.handleJsonResponse(response, true, "解绑成功");
                    }
                }
            } catch (Exception e) {
                this.handleJsonResponse(response, false, e.getMessage());
                e.printStackTrace();
            }
        }
}
