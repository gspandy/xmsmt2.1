/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: SmsController.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月12日
 */
package net.wit.sms.controller;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import jodd.http.HttpBrowser;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import net.wit.Message;
import net.wit.controller.api.BaseController;
import net.wit.mobile.bo.AuthCode;
import net.wit.sms.service.SmsService;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 
 * @author: huiyang.yu
 * @version Revision: 0.0.1
 * @Date: 2015年9月12日
 */
//@Controller("smsController")
//@RequestMapping("/api/sms")
public class SmsController extends BaseController {
	
	@Autowired
	private SmsService smsService;
	
	
	/**
	 * 1.14 获取验证码(云片)
	 * @author:huiyang.yu
	 * @param mobile
	 * @return
	 */
	@RequestMapping("/getAuthCode")
	public @ResponseBody Message getAuthCode(String tel) {
		//生成6位随机数
		char[] codeSeq = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		Random random = new Random();
		StringBuilder code = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			String randomCode = String.valueOf(codeSeq[random.nextInt(codeSeq.length)]);// random.nextInt(10));
			code.append(randomCode);
		}
		//发送短信
		String apikey = "265f06dc6dda683b7fc8341fbd5e8bcc";
		HttpBrowser browser = new HttpBrowser();
		HttpRequest request = HttpRequest.post("http://yunpian.com/v1/sms/send.json");
		String text = String.format("【指帮内购】您的验证码是%s。如非本人操作，请忽略本短信", code.toString());
		request.form("apikey", apikey);
		request.form("text", text);
		request.form("mobile", tel);
		HttpResponse resp = browser.sendRequest(request);
		//处理返回结果
		String msg ="";
		try {
			msg = new String(resp.body().getBytes("iso-8859-1"),"Utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println(msg);
		
		//保存验证码到缓存数据库中
		AuthCode obj = new AuthCode();
		obj.setCode(code.toString());
		obj.setTel(tel);
		obj.setCurrentTimeMillis(System.currentTimeMillis());
		smsService.saveCode(obj);
		
		
		return Message.success("验证码发送成功");
	}
	
	/**
	 * 验证验证码是否通过
	 * @author:huiyang.yu
	 * @param tel
	 * @param code
	 * @return
	 */
	@RequestMapping("/validCode")
	public @ResponseBody Message validCode(String tel, String code) {
		AuthCode obj = new AuthCode();
		obj.setCode(code);
		obj.setTel(tel);
		
		if (!smsService.isInvalidCode(obj)) {
			return Message.success("成功");
		}
		return Message.error("验证码错误");
	}
	
	
	/**
	 * 1.14 获取验证码(畅卓)
	 * @author: huiyang.yu
	 * @param tel
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/getAuthCodeCZ") 
	public @ResponseBody Message getAuthCodeCZ(String tel) {
		
		String username = "lcs9078";
		String password = "152677";
		//生成6位随机数
		char[] codeSeq = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		Random random = new Random();
		StringBuilder code = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			String randomCode = String.valueOf(codeSeq[random.nextInt(codeSeq.length)]);// random.nextInt(10));
			code.append(randomCode);
		}
		//生成文本内容
		String content = "指帮内购验证码"+ code.toString() +"【速卖通】";
		//发送短信
		HttpBrowser browser = new HttpBrowser();
		HttpRequest request = HttpRequest.get("http://sms.chanzor.com:8001/sms.aspx?action=send");
		request.query("userid", "");
		request.query("account", username);
		request.query("password", password);
		request.query("mobile", tel);
		request.query("sendTime", "");
		request.query("content", content);
		System.out.println(request.url());
		HttpResponse resp = browser.sendRequest(request);
		
		//处理返回结果
		String msg ="";
		try {
			msg = new String(resp.body().getBytes("iso-8859-1"),"utf-8");
			System.out.println(msg);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	    try {
			 Document  document = DocumentHelper.parseText(msg);  
			 Element returnsms = document.getRootElement();
			 Element returnstatus = returnsms.element("returnstatus");
			 Element message = returnsms.element("message");
			 if (returnstatus.getText().equals("Faild")) {
				 return Message.error("验证码发送失败,错误原因：" + message.getText());
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} 
	    
		//保存验证码到缓存数据库中
		AuthCode obj = new AuthCode();
		obj.setCode(code.toString());
		obj.setTel(tel);
		obj.setCurrentTimeMillis(System.currentTimeMillis());
		smsService.saveCode(obj);
		
		return Message.success("验证码发送成功");
	}
}
