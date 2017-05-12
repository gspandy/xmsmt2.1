package net.wit.mobile.controller;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.wit.entity.Order;
import net.wit.entity.Returns;
import net.wit.entity.Shipping;
import net.wit.mobile.service.INTokenBS;
import net.wit.service.OrderService;
import net.wit.service.ReturnsService;
import net.wit.service.ShippingService;
import net.wit.util.CacheUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.ld.slf4j.Logger;
import cn.ld.slf4j.LoggerFactory;

@Controller("mobileShippingController")
@RequestMapping(value = "/shipping")
public class ShippingController extends BaseController {

	private Logger log = LoggerFactory.getLogger(ShippingController.class);
	
    @Autowired
    private INTokenBS inTokenBS;
    @Autowired
    private ShippingService shippingService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ReturnsService returnsService;
  
    /**
     * 查看订单的物流信息
     */
    @RequestMapping(value = "/query")
    public void query(
    		@RequestParam("token") String token, 
    		HttpServletResponse response, 
    		@RequestParam("orderId") String orderId) throws Exception {
    	try {
    		if (!inTokenBS.isVaild(token)) {
    			this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
    		} else {
    			log.info("请求参数为：" + "[orderId:" + orderId +"]");
    			Order order = this.orderService.find(new Long(orderId));
    			Set<Shipping> shippings = order.getShippings();
    			if (shippings != null && shippings.iterator() != null && shippings.iterator().hasNext()) {
    				Shipping shipping = shippings.iterator().next();
    				Map<String, Object> result = this.shippingService.query(shipping);
    				
    				JSONObject resultValue = new JSONObject();
    				resultValue.put("shippingTrack", result);// 返回物流信息
    				resultValue.put("trackingNo", shipping.getTrackingNo());// 返回物流信息
    				this.handleJsonResponse(response, true, "物流信息查看成功",resultValue);
    			} else {
    				this.handleJsonResponse(response, true, "物流信息查看失败");
    			}
    		}
    	} catch (Exception e) {
    		this.handleJsonResponse(response, false, e.getMessage());
    		e.printStackTrace();
    	}
    	
    }
    
    /**
     * 查看退货单的物流信息
     */
    @RequestMapping(value = "/queryReturns")
    public void queryReturns(@RequestParam("token") String token, HttpServletResponse response, 
    		@RequestParam("returnsId") String returnsId) throws Exception {
    	try {
    		if (!inTokenBS.isVaild(token)) {
    			this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
    		} else {
    			log.info("请求参数为：" + "[returnsId:" + returnsId +"]");
    			Returns returns = this.returnsService.find(new Long(returnsId));
    			if (returns != null) {
    				Map<String, Object> result = this.shippingService.queryReturns(returns);
    				JSONObject resultValue = new JSONObject();
    				resultValue.put("shippingTrack", result);// 返回物流信息
    				this.handleJsonResponse(response, true, "物流信息查看成功",resultValue);
    			} else {
    				this.handleJsonResponse(response, true, "物流信息查看失败");
    			}
    		}
    	} catch (Exception e) {
    		this.handleJsonResponse(response, false, e.getMessage());
    		e.printStackTrace();
    	}
    	
    }

}
