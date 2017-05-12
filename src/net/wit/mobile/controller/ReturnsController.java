package net.wit.mobile.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.Order;
import net.wit.entity.OrderItem;
import net.wit.entity.Refunds;
import net.wit.entity.Returns;
import net.wit.mobile.service.INTokenBS;
import net.wit.mobile.service.impl.PushService;
import net.wit.mobile.util.rong.models.SystemMessage;
import net.wit.service.DeliveryCenterService;
import net.wit.service.OrderItemService;
import net.wit.service.OrderService;
import net.wit.service.RefundsService;
import net.wit.service.ReturnsService;
import net.wit.support.ReturnItemVo;
import net.wit.util.CacheUtil;
import net.wit.util.ExcelUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.ld.slf4j.Logger;
import cn.ld.slf4j.LoggerFactory;

@Controller("mobielReturnController")
@RequestMapping(value = "/returns")
public class ReturnsController extends BaseController {

	private Logger log = LoggerFactory.getLogger(ReturnsController.class);

	@Autowired
	private INTokenBS inTokenBS;
	@Autowired
	private ReturnsService returnsService;
	@Autowired
	private RefundsService refundsService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderItemService orderItemService;
	@Autowired
	private DeliveryCenterService deliveryCenterService;
	@Autowired
	private PushService pushService;
	
	private static final String splitChar= "\\|";
	
	 /**
     * 买家退货申请
     */
    @RequestMapping(value = "/applyReturns")
    public void applyReturns(HttpServletResponse response, 
    		@RequestParam("token") String token, String memo ,String reason,
    		String quantity, String trackingNo, String orderItemId, 
    		@RequestParam("orderNo") String orderNo, String picId1, String picId2, String picId3) 
    		throws Exception {
    	try {
    		if (!inTokenBS.isVaild(token)) {
    			this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
    		} else {
    			log.info("请求参数为：" + "[token:" + token + ",orderNo:" + orderNo +"]");
	    		if(orderNo != null && !"".equals(orderNo)) {
	    			String[] orderItemIds = orderItemId.split(splitChar);
	        		String[] quantitys = quantity.split(splitChar);
	    			Order order = this.orderService.findBySn(orderNo);
	    			List<ReturnItemVo> returnVos = new ArrayList();
	    			OrderItem orderItem = null;
	    			ReturnItemVo returnItemVo = null;
	    			for (int i = 0, length = orderItemIds.length; i < length; i++) {
	        			orderItem = this.orderItemService.find(new Long(orderItemIds[i]));
	        			returnItemVo = new ReturnItemVo();
	        			returnItemVo.setOrderItem(orderItem);
	        			returnItemVo.setQuantity(new Integer(quantitys[i]));
	        			returnVos.add(returnItemVo);
	        		}
	    			Returns returns = this.returnsService.apply(returnVos, order, memo, reason, picId1, picId2, picId3);
	    			this.handleJsonResponse(response, true,"退货申请成功");
	    		}
    		}
    	} catch (Exception e) {
    		this.handleJsonResponse(response, false, "无法退货");
    		e.printStackTrace();
    	}
    }
    
    /**
     * 买家填写物流单
     */
    @RequestMapping(value = "/submitShipping")
    public void submitShipping(HttpServletResponse response, @RequestParam("token") String token, 
    		@RequestParam("shippingCompany") String shippingCompany, @RequestParam("returnsId") String returnsId,
    		@RequestParam("trackingNo") String trackingNo) throws Exception {
    	try {
    		if (!inTokenBS.isVaild(token)) {
    			this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
    		} else {
    			log.info("请求参数为：" + "[token:" + token + ",shippingCompany:" + shippingCompany 
    					+ ",trackingNo:" + trackingNo +"]");
	    		if(returnsId != null && !"".equals(returnsId)) {// 订单号不为空
//	    			Order order = this.orderService.findBySn(orderNo);
//	    			Set<Returns> returnsList = order.getReturns();
	    			
	    			Returns returns = this.returnsService.find(new Long(returnsId));
	    			
//	    			if (returnsList != null && returnsList.iterator() != null && returnsList.iterator().hasNext()) {
//	    				returns = returnsList.iterator().next();
//	    				// 设置物流公司
//	    			}
	    			
	    			this.returnsService.edit(returns, trackingNo);
	    			
	    			// 给会员发送消息
					pushService.publishSystemMessage(returns.getOrder().getTenant(), returns.getOrder().getMember(), 
							SystemMessage.buyerOrderReturnMsg(returns.getOrder().getTenant().getShortName(), returns.getOrder().getSn()));
	    			this.handleJsonResponse(response, true,"快递单号提交成功");
	    		}
    		}
    	} catch (Exception e) {
    		this.handleJsonResponse(response, false, "无法提交快递单号");
    		e.printStackTrace();
    	}
    }
    
    
    /**
     * 退款申请
     */
    @RequestMapping(value = "/applyRefunds")
    public void applyRefunds(HttpServletResponse response, 
    		@RequestParam("token") String token, String memo,
    		@RequestParam("orderNo") String orderNo) throws Exception {
    	try {
    		if (!inTokenBS.isVaild(token)) {
    			this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
    		} else {
    			log.info("请求参数为：" + "[token:" + token + ",orderNo:" + orderNo +"]");
	    		if(orderNo != null && !"".equals(orderNo)) {
	    			Order order = this.orderService.findBySn(orderNo);
	    			this.refundsService.apply(order, memo);
	    			
	    			// 给会员发送消息
					pushService.publishSystemMessage(order.getTenant(), order.getMember(), 
							SystemMessage.buyerOrderRefundMsg(order.getTenant().getShortName(), order.getSn()));
	    			this.handleJsonResponse(response, true,"退款申请成功");
	    		}
    		}
    	} catch (Exception e) {
    		this.handleJsonResponse(response, false, "无法退款");
    		e.printStackTrace();
    	}
    }

    /**
     * 退货单详情
     * @param response
     * @param token
     * @param orderNo
     * @throws Exception
     */
    @RequestMapping(value = "/returnsDetail")
    public void returnsDetail(HttpServletResponse response, @RequestParam("token") String token, 
    		@RequestParam("orderNo") String orderNo) throws Exception {
    	try {
    		if (!inTokenBS.isVaild(token)) {
    			this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
    		} else {
    			log.info("请求参数为：" + "[token:" + token + ",orderNo:" + orderNo +"]");
	    		if(orderNo != null && !"".equals(orderNo)) {// 订单号不为空
	    			Order order = this.orderService.findBySn(orderNo);
	    			// 获取企业默认收货地址
	    			DeliveryCenter deliveryCenter = this.deliveryCenterService.findDefault(order.getTenant());
	    			
	    			// 获取退货单详情
	    			Set<Returns> returnsSet = order.getReturns();
	    			Returns returns = null;
	    			if (returnsSet != null && returnsSet.iterator() != null && returnsSet.iterator().hasNext()) {
	    				returns = returnsSet.iterator().next();
	    			}
	    			JSONObject resultValue = new JSONObject();
	    			resultValue.put("address", ExcelUtil.convertNull(deliveryCenter.getAreaName() + deliveryCenter.getAddress()));// 收货地址
	    			resultValue.put("phone", ExcelUtil.convertNull(deliveryCenter.getPhone()));// 联系电话
	    			resultValue.put("contact", ExcelUtil.convertNull(deliveryCenter.getContact()));// 联系人
	    			resultValue.put("reason", ExcelUtil.convertNull(returns.getReason()));// 原因
	    			resultValue.put("amount", ExcelUtil.convertNull(returns.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP)));// 退款金额
	    			resultValue.put("RefundableAmount", ExcelUtil.convertNull(returns.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP)));// 应退金额
	    			resultValue.put("oldAmount", ExcelUtil.convertNull(returns.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP)));// 原路退回金额
	    			resultValue.put("trackingNo", ExcelUtil.convertNull(returns.getTrackingNo()));// 物流单号
	    			resultValue.put("returnsId", ExcelUtil.convertNull(returns.getId()));// 退款单ID
	    			resultValue.put("tenantName", ExcelUtil.convertNull(order.getTenant().getShortName()));// 店铺名称
	    			this.handleJsonResponse(response, true,"获取退货单成功", resultValue);
	    		} else {
	    			this.handleJsonResponse(response, false, "订单号为空！");
	    		}
    		}
    	} catch (Exception e) {
    		this.handleJsonResponse(response, false, "获取企业收货地址失败");
    		e.printStackTrace();
    	}
    	
    }
    
    /**
     * 退款单详情
     * @param response
     * @param token
     * @param orderNo
     * @throws Exception
     */
    @RequestMapping(value = "/refundsDetail")
    public void refundsDetail(HttpServletResponse response, @RequestParam("token") String token, 
    		@RequestParam("orderNo") String orderNo) throws Exception {
    	try {
    		if (!inTokenBS.isVaild(token)) {
    			this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
    		} else {
    			log.info("请求参数为：" + "[token:" + token + ",orderNo:" + orderNo +"]");
	    		if(orderNo != null && !"".equals(orderNo)) {// 订单号不为空
	    			Order order = this.orderService.findBySn(orderNo);
	    			// 获取退款单详情
	    			Set<Refunds> refundsSet = order.getRefunds();
	    			Refunds refunds = null;
	    			if (refundsSet != null && refundsSet.iterator() != null && refundsSet.iterator().hasNext()) {
	    				refunds = refundsSet.iterator().next();
	    			}
	    			JSONObject resultValue = new JSONObject();
	    			resultValue.put("memo", ExcelUtil.convertNull(refunds.getMemo()));// 原因
	    			resultValue.put("amount", ExcelUtil.convertNull(refunds.getAmount().setScale(2,BigDecimal.ROUND_HALF_UP)));// 退款金额
	    			resultValue.put("RefundableAmount", ExcelUtil.convertNull(refunds.getAmount().setScale(2,BigDecimal.ROUND_HALF_UP)));// 应退金额
	    			resultValue.put("oldAmount", ExcelUtil.convertNull(refunds.getAmount().setScale(2,BigDecimal.ROUND_HALF_UP)));// 原路退回金额
	    			resultValue.put("refundsId", ExcelUtil.convertNull(refunds.getId()));// 退款单ID
	    			resultValue.put("tenantName", ExcelUtil.convertNull(order.getTenant().getShortName()));// 店铺名称
	    			resultValue.put("status", ExcelUtil.convertNull(refunds.getStatus()));// 退款单ID
	    			this.handleJsonResponse(response, true,"获取退款单成功", resultValue);
	    		} else {
	    			this.handleJsonResponse(response, false, "订单号为空！");
	    		}
    		}
    	} catch (Exception e) {
    		this.handleJsonResponse(response, false, "获取退款单失败");
    		e.printStackTrace();
    	}
    	
    }
    
}
