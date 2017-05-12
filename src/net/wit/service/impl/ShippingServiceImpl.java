/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.Setting;
import net.wit.dao.OrderDao;
import net.wit.dao.ShippingDao;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.DeliveryCorp;
import net.wit.entity.Order;
import net.wit.entity.OrderItem;
import net.wit.entity.Returns;
import net.wit.entity.Shipping;
import net.wit.entity.ShippingItem;
import net.wit.entity.ShippingMethod;
import net.wit.entity.Sn;
import net.wit.entity.Tenant;
import net.wit.mobile.service.impl.PushService;
import net.wit.mobile.util.rong.models.SystemMessage;
import net.wit.service.DeliveryCenterService;
import net.wit.service.DeliveryCorpService;
import net.wit.service.OrderService;
import net.wit.service.ShippingMethodService;
import net.wit.service.ShippingService;
import net.wit.service.SnService;
import net.wit.util.SettingUtils;

/**
 * Service - 发货单
 * @author rsico Team
 * @version 3.0
 */
@Service("shippingServiceImpl")
public class ShippingServiceImpl extends BaseServiceImpl<Shipping, Long>implements ShippingService {

	@Resource(name = "shippingDaoImpl")
	private ShippingDao shippingDao;

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;

	@Resource(name = "shippingMethodServiceImpl")
	private ShippingMethodService shippingMethodService;

	@Resource(name = "deliveryCenterServiceImpl")
	private DeliveryCenterService deliveryCenterService;

	@Resource(name = "deliveryCorpServiceImpl")
	private DeliveryCorpService deliveryCorpService;

	@Resource(name = "snServiceImpl")
	private SnService snService;
	
	@Autowired
	private PushService pushService;

	@Resource(name = "orderDaoImpl")
	private OrderDao orderDao;

	@Resource(name = "shippingDaoImpl")
	public void setBaseDao(ShippingDao shippingDao) {
		super.setBaseDao(shippingDao);
	}

	@Transactional(readOnly = true)
	public Shipping findBySn(String sn) {
		return shippingDao.findBySn(sn);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
//	@Cacheable("shipping")
	public Map<String, Object> query(Shipping shipping) {
		Setting setting = SettingUtils.get();
		Map<String, Object> data = new HashMap<String, Object>();
		if (shipping != null && StringUtils.isNotEmpty(setting.getKuaidi100Key()) && StringUtils.isNotEmpty(shipping.getDeliveryCorpCode()) && StringUtils.isNotEmpty(shipping.getTrackingNo())) {
			try {
				ObjectMapper mapper = new ObjectMapper();
//				URL url = new URL("http://api.kuaidi100.com/api?id=" + setting.getKuaidi100Key() + "&com=" + shipping.getDeliveryCorpCode() + "&nu=" + shipping.getTrackingNo() + "&show=0&muti=1&order=desc");
				URL url = new URL("http://api.kuaidi100.com/api?id=bede29d816bf5cf2&com=" + shipping.getDeliveryCorpCode() + "&nu=" + shipping.getTrackingNo() + "&show=0&muti=1&order=desc");
				data = mapper.readValue(url, Map.class);
				// 
				if (!"1".equals(data.get("status"))) {
					URL urlHttp = new URL("http://www.kuaidi100.com/applyurl?key=bede29d816bf5cf2&com=" + shipping.getDeliveryCorpCode() + "&nu=" + shipping.getTrackingNo());
					
					URLConnection con = urlHttp.openConnection();
		            con.setAllowUserInteraction(false);
		            InputStream urlStream = urlHttp.openStream();
		            byte b[] = new byte[10000];
		            int numRead = urlStream.read(b);
		            String content = new String(b, 0, numRead);
		            while (numRead != -1)
		            {
		                numRead = urlStream.read(b);
		                if (numRead != -1)
		                {
		                    // String newContent = new String(b, 0, numRead);
		                    String newContent = new String(b, 0, numRead, "UTF-8");
		                    content += newContent;
		                }
		            }
		            urlStream.close();
					data.put("status", "3");
					data.put("data", content);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Map<String, Object> queryReturns(Returns returns) {
		Setting setting = SettingUtils.get();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			ObjectMapper mapper = new ObjectMapper();
//			URL url = new URL("http://api.kuaidi100.com/api?id=" + setting.getKuaidi100Key() + "&com=" + returns.getDeliveryCorp() + "&nu=" + returns.getTrackingNo() + "&show=0&muti=1&order=desc");
			URL url = new URL("http://api.kuaidi100.com/api?id=bede29d816bf5cf2&com=" + returns.getDeliveryCorp() + "&nu=" + returns.getTrackingNo() + "&show=0&muti=1&order=desc");
			data = mapper.readValue(url, Map.class);
			
			// 
			if (!"1".equals(data.get("status"))) {
				URL urlHttp = new URL("http://www.kuaidi100.com/applyurl?key=bede29d816bf5cf2&com=" + returns.getDeliveryCorp() + "&nu=" + returns.getTrackingNo());
				
				URLConnection con = urlHttp.openConnection();
	            con.setAllowUserInteraction(false);
	            InputStream urlStream = urlHttp.openStream();
	            byte b[] = new byte[10000];
	            int numRead = urlStream.read(b);
	            String content = new String(b, 0, numRead);
	            while (numRead != -1)
	            {
	                numRead = urlStream.read(b);
	                if (numRead != -1)
	                {
	                    // String newContent = new String(b, 0, numRead);
	                    String newContent = new String(b, 0, numRead, "UTF-8");
	                    content += newContent;
	                }
	            }
	            urlStream.close();
				data.put("status", "3");
				data.put("context", content);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public Page<Shipping> findPage(Tenant tenant, Pageable pageable) {
		return shippingDao.findPage(tenant, pageable);
	}

	public String importData(List<String> titles, List<Object[]> shippings, Tenant tenant) {
		StringBuffer errorMsg = new StringBuffer("");
		int successCount = 0;
		int errorCount = 0;
		for (Object[] objects : shippings) {
			try {
				// 校验订单
				if (!validateShipping(objects)) {
					errorMsg.append("序号:" + objects[0] + "数据不正确!");
					errorCount++;
					continue;
				}
				String orderSn = (String) objects[1];
				Order order = orderDao.findBySn(orderSn);
				if (order == null) {
					errorMsg.append("序号:" + objects[0] + "订单为空! ");
					errorCount++;
					continue;
				}
				if (order.getShippings().size() > 0) {
					errorCount++;
					continue;
				}
				if (!tenant.equals(order.getTenant())) {
					errorMsg.append("序号:" + objects[0] + "非法订单! ");
					errorCount++;
					continue;
				}
				Shipping shipping = new Shipping();
				for (OrderItem orderItem : order.getOrderItems()) {
					ShippingItem shippingItem = new ShippingItem();
					shippingItem.setQuantity(orderItem.getQuantity());
					shippingItem.setName(orderItem.getFullName());
					shippingItem.setShipping(shipping);
					shippingItem.setSn(orderItem.getSn());
					shipping.getShippingItems().add(shippingItem);

				}
				shipping.setOrder(order);
				ShippingMethod shippingMethod = this.shippingMethodService.findByName((String) objects[2]);
				if (shippingMethod == null) {
					errorMsg.append("序号:" + objects[0] + "配送方式填写不正确! ");
					errorCount++;
					continue;
				}
				shipping.setShippingMethod(shippingMethod.getName());
				DeliveryCorp deliveryCorp = this.deliveryCorpService.findByName((String) objects[3]);
				if (deliveryCorp == null) {
					errorMsg.append("序号:" + objects[0] + "物流公司填写不正确! ");
					errorCount++;
					continue;
				}
				if (objects[4] == "") {
					errorMsg.append("序号:" + objects[0] + "物流单号填写不正确! ");
					errorCount++;
					continue;

				}
				shipping.setSn(snService.generate(Sn.Type.shipping));
				shipping.setTrackingNo((String) objects[4]);
				shipping.setDeliveryCorp(deliveryCorp.getName());
				shipping.setDeliveryCorpUrl(deliveryCorp.getUrl());
				shipping.setDeliveryCorpCode(deliveryCorp.getCode());
				shipping.setArea((order.getArea() != null) ? order.getArea().getFullName() : "");
				DeliveryCenter deliveryCenter = this.deliveryCenterService.findDefault(tenant);
				if (deliveryCenter == null) {
					errorMsg.append("序号:" + objects[0] + "发货点填写不正确! ");
					errorCount++;
					continue;
				}
				shipping.setDeliveryCenter(deliveryCenter);
				shipping.setOrder(order);
				shipping.setAddress(order.getAddress());
				shipping.setConsignee(order.getConsignee());
				shipping.setZipCode(order.getZipCode());
				shipping.setPhone(order.getPhone());
				shipping.setOperator(tenant.getName());
				orderService.shipping(order, shipping, null);
				successCount++;
			} catch (Exception e) {
				errorCount++;
				errorMsg.append("序号:" + objects[0] + "导入错误! ");
			}

		}
		return "成功导入:" + successCount + "条;失败导入:" + errorCount + "条;" + errorMsg.toString();
	}
	
	/**
	 * 导入已发货订单信息（用于批量发货）
	 * @param titles
	 * @param shippings
	 * @param tenant
	 * @return
	 */
	public String importShipped(List<String> titles, List<Object[]> shippings, Tenant tenant) {
		StringBuffer errorMsg = new StringBuffer("");
		int successCount = 0;
		int errorCount = 0;
		for (Object[] objects : shippings) {
			try {
				// 校验订单
				if (!validateShipping(objects)) {
					errorMsg.append("序号:" + objects[0] + "数据不正确!");
					errorCount++;
					continue;
				}
				String orderSn = (String) objects[1];
				Order order = orderDao.findBySn(orderSn);
				if (order == null) {
					errorMsg.append("序号:" + objects[0] + "订单为空! ");
					errorCount++;
					continue;
				}
				if (order.getShippings().size() > 0) {
					errorCount++;
					continue;
				}
				if (!tenant.equals(order.getTenant())) {
					errorMsg.append("序号:" + objects[0] + "非法订单! ");
					errorCount++;
					continue;
				}
				Shipping shipping = new Shipping();
				for (OrderItem orderItem : order.getOrderItems()) {
					ShippingItem shippingItem = new ShippingItem();
					shippingItem.setQuantity(orderItem.getQuantity());
					shippingItem.setName(orderItem.getFullName());
					shippingItem.setShipping(shipping);
					shippingItem.setSn(orderItem.getSn());
					shipping.getShippingItems().add(shippingItem);

				}
				shipping.setOrder(order);
				ShippingMethod shippingMethod = this.shippingMethodService.findByName((String) objects[8]);// 配送方式
				if (shippingMethod == null) {
					errorMsg.append("序号:" + objects[0] + "配送方式填写不正确! ");
					errorCount++;
					continue;
				}
				shipping.setShippingMethod(shippingMethod.getName());
				DeliveryCorp deliveryCorp = this.deliveryCorpService.findByName((String) objects[9]);
				if (deliveryCorp == null) {
					errorMsg.append("序号:" + objects[0] + "物流公司填写不正确! ");
					errorCount++;
					continue;
				}
				if (objects[4] == "") {
					errorMsg.append("序号:" + objects[0] + "物流单号填写不正确! ");
					errorCount++;
					continue;

				}
				shipping.setSn(snService.generate(Sn.Type.shipping));
				shipping.setTrackingNo((String) objects[10]);
				shipping.setDeliveryCorp(deliveryCorp.getName());
				shipping.setDeliveryCorpUrl(deliveryCorp.getUrl());
				shipping.setDeliveryCorpCode(deliveryCorp.getCode());
				shipping.setArea((order.getArea() != null) ? order.getArea().getFullName() : "");
				DeliveryCenter deliveryCenter = this.deliveryCenterService.findDefault(tenant);
				if (deliveryCenter == null) {
					errorMsg.append("序号:" + objects[0] + "发货点填写不正确! ");
					errorCount++;
					continue;
				}
				shipping.setDeliveryCenter(deliveryCenter);
				shipping.setOrder(order);
				shipping.setAddress(order.getAddress());
				shipping.setConsignee(order.getConsignee());
				shipping.setZipCode(order.getZipCode());
				shipping.setPhone(order.getPhone());
				shipping.setOperator(tenant.getName());
				orderService.shipping(order, shipping, null);
				successCount++;
				// 给会员发送消息
				pushService.publishSystemMessage(order.getTenant(), order.getMember(), 
						SystemMessage.buyerOrderDeliverMsg(order.getTenant().getShortName(), order.getSn()));
			} catch (Exception e) {
				errorCount++;
				errorMsg.append("序号:" + objects[0] + "导入错误! ");
			}

		}
		return "成功导入:" + successCount + "条;失败导入:" + errorCount + "条;" + errorMsg.toString();
	}

	/**
	 * @Title：validateShipping @Description：
	 * @param objects void
	 */
	private boolean validateShipping(Object[] objects) {
		return true;
	}

}