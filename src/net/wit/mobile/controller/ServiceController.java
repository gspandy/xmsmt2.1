package net.wit.mobile.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.bytecode.Descriptor.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jodd.util.URLDecoder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Admin;
import net.wit.entity.CustomService;
import net.wit.entity.Member;
import net.wit.entity.MemberBank;
import net.wit.entity.Pic;
import net.wit.entity.Product;
import net.wit.entity.Tenant;
import net.wit.mobile.bo.NToken;
import net.wit.mobile.service.INTokenBS;
import net.wit.plugin.PaymentPlugin;
import net.wit.plugin.unionpay.UnionpayPlugin;
import net.wit.service.CustomServiceService;
import net.wit.service.MemberBankService;
import net.wit.service.MemberService;
import net.wit.service.PicService;
import net.wit.service.PluginService;
import net.wit.service.TenantService;
import net.wit.service.TicketService;
import net.wit.service.YeePayService;
import net.wit.service.impl.ZGTService;
import net.wit.util.CacheUtil;
import net.wit.util.ExcelUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.ld.slf4j.Logger;
import cn.ld.slf4j.LoggerFactory;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created with IntelliJ IDEA. User: ab Date: 15-9-13 Time: 下午1:56 To change
 * this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/customService")
public class ServiceController extends BaseController {

	private Logger log = LoggerFactory.getLogger(ServiceController.class);

	public static final String SERVICE_PREFIX = "SERVICE_";

	@Autowired
	private MemberService memberService;

	@Autowired
	private TenantService tenantService;

	@Autowired
	private CustomServiceService customServiceService;

	@Autowired
	private TicketService ticketService;

	@Autowired
	private INTokenBS inTokenBS;
	
	
	@RequestMapping(value = "/getHotLine")
	public void getHotLine(HttpServletResponse response) throws Exception {
		try {
			final String HOTLINE_KEYS = "PLATFROM_HOTLINE";
			String tel = CacheUtil.getParamValueByName(HOTLINE_KEYS);
			JSONObject json = new JSONObject();
			json.put("tel", tel);
			this.handleJsonResponse(response, true, "",json);
			// log.info(arg0);
		} catch (Exception e) {
			this.handleJsonResponse(response, false, "未设置客户热线电话");
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/getTenants")
	public void getTenants(HttpServletResponse response) throws Exception {
		try {
			List<Tenant> list = tenantService.getTenantAll();
			JSONArray jsa = new JSONArray();
			for (Tenant tenant : list) {
				JSONObject js = new JSONObject();
				js.put("tenantName", tenant.getShortName()); // 返回企业名称
				js.put("tenantId", tenant.getId()); // 返回企业ID
				js.put("logUrl", tenant.getLogo()); // 返回企业logo
				jsa.add(js);
			}
			this.handleJsonResponse(response, true, "", jsa);
			// log.info(arg0);
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/getTenantsByTicket")
	public void getTenantsByTicket(HttpServletResponse response,
			@RequestParam("token") String token) throws Exception {
		try {
			if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false,
						CacheUtil.getParamValueByName("TOKEN_INVALID"));

			} else {
				NToken nToken = inTokenBS.get(token);
				String memberId = nToken.getMemberId();
				Member member = memberService.find(new Long(memberId));
				List<Tenant> list = ticketService.getTicketTenant(member);
				JSONArray jsa = new JSONArray();
				for (Tenant tenant : list) {
					JSONObject js = new JSONObject();
					js.put("tenantName", tenant.getShortName()); // 返回企业名称
					js.put("tenantId", tenant.getId()); // 返回企业ID
					js.put("logUrl", tenant.getLogo()); // 返回企业logo
					jsa.add(js);
				}
				this.handleJsonResponse(response, true, "", jsa);
				// log.info(arg0);
			}
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/getTenantService")
	public void getTenantService(HttpServletResponse response,
			@RequestParam("tenantId") String tenantId) throws Exception {
		try {
			List<CustomService> list = customServiceService.findALL(new Long(
					tenantId));
			JSONArray jsa = new JSONArray();
			for (CustomService customService : list) {
				if (!customService.getOnlines().equals(
						CustomService.ONLINE_STATE))
					continue; // 不在线状态不显示
				JSONObject js = new JSONObject();
				String serviceId = SERVICE_PREFIX + ""
						+ customService.getAdmin().getId();
				String headImage = customService.getServiceImg();
				String serviceName = customService.getServiceName();
				String serviceTel = customService.getServiceTel();
				js.put("serviceId", serviceId);
				js.put("headImage", headImage);
				js.put("serviceName", serviceName);
				js.put("serviceTel", serviceTel);
				jsa.add(js);
			}
			log.info("tenantId:" + tenantId + " 客服列表信息:" + jsa.toString() + "");
			if (jsa.isEmpty()) {
				throw new Exception("抱歉 ! 目前没有在线客服");
			} else
				this.handleJsonResponse(response, true, "", jsa);
		} catch (Exception e) {
			this.handleJsonResponse(response, false, e.getMessage());
			e.printStackTrace();
		}
	}

}
