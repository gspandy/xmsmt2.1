/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: SmsService.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月12日
 */
package net.wit.sms.service;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.annotation.Resource;

import jodd.http.HttpBrowser;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import net.wit.mobile.bo.AuthCode;
import net.wit.mobile.cache.CacheUtil;
import net.wit.mobile.controller.LoginController;
import net.wit.mobile.dao.IAuthCodeDao;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @author: huiyang.yu
 * @version Revision: 0.0.1
 * @Date: 2015年9月12日
 */
@Service
public class SmsService {
	
	private static final String TEST_CODE_PARAM = "TEST_CODE";
	
	private Logger log = LoggerFactory.getLogger(SmsService.class);
	@Resource(name = "authCodeDaoImpl")
	private IAuthCodeDao iAuthCodeDao;
	
	/**
	 * 保存验证码
	 * @author: huiyang.yu
	 * @param obj
	 */
	public void saveCode(AuthCode obj){
		 AuthCode code = iAuthCodeDao.get(obj.getTel());
		if (code == null) {
			 iAuthCodeDao.add(obj);
		}else {
			iAuthCodeDao.update(obj);
		}
	}
	
	/**
	 * 获取验证码
	 * @author: huiyang.yu
	 * @param obj
	 */
	public boolean isInvalidCode(AuthCode obj){
		 AuthCode code = iAuthCodeDao.get(obj.getTel());
		//用户攻击
		if (code == null) {
			return true;
		}
		
		//判断验证码是否正确
		if (!code.getCode().equals(obj.getCode())) {
			return true;
		}
		
		//判断验证码是否超时
		long timeSpace = getValidTimeSpace(code);;
		if (timeSpace > 5) {
			return true;
		}
		iAuthCodeDao.remove(code.getTel());
		return false;
	}
	
	
	
	/**
	 * 获取手机验证码发送的时间间隔
	 * @author: weihuang.peng
	 * @param mobile
	 * @return
	 */
	public long getValidTimeSpace(AuthCode sysValid) {
		if (sysValid == null) {
			return Long.MAX_VALUE;
		}
		return getTimeSpace(sysValid.getCurrentTimeMillis());
	}
	
	/**
	 * 获取时间戳间隔
	 * @author: weihuang.peng
	 * @param oldTimeMillis
	 * @return
	 */
	public long getTimeSpace(Long oldTimeMillis) {
		return (System.currentTimeMillis() - oldTimeMillis) / (1000 * 60);
	}


    public boolean getAuthCodeYP(String tel, String content){
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
        String text = String.format("【指帮内购】您的"+content+"是%s。如非本人操作，请忽略本短信", code.toString());
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
        saveCode(obj);
        return true;
    }

	/**
	 * 获取验证码（云片）
	 * @author: huiyang.yu
	 * @param tel
	 */
	public boolean getAuthCodeYP(String tel){
		//生成6位随机数
		char[] codeSeq = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		Random random = new Random();
		StringBuilder code = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			String randomCode = String.valueOf(codeSeq[random.nextInt(codeSeq.length)]);// random.nextInt(10));
			code.append(randomCode);
		}
		log.info("验证码发送:"+tel+"验证码内容"+code.toString());
		//发送短信
		String apikey = "265f06dc6dda683b7fc8341fbd5e8bcc";
		HttpBrowser browser = new HttpBrowser();
		HttpRequest request = HttpRequest.post("http://yunpian.com/v1/sms/send.json");
		String text = String.format("【指帮内购】您本次操作的验证码为%s，该验证码5分钟内有效。", code.toString());
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
		saveCode(obj);
		return true;
	}
	
	/**
	 * 验证验证码是否通过,通过返回 true
	 * @author:huiyang.yu
	 * @param tel
	 * @param code
	 * @return
	 */
	@RequestMapping("/validCode")
	public boolean validCode(String tel, String code) {
		AuthCode obj = new AuthCode();
		obj.setCode(code);
		obj.setTel(tel);
		String testCode = CacheUtil.getParamValueByName(TEST_CODE_PARAM);
		if(!testCode.equals("0")){
			if(code.equals(testCode)) return true;
		}
		if (!isInvalidCode(obj)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 短信畅卓
	 * @author: huiyang.yu
	 * @param tel  如果是多个 "," 分隔
	 * @param text
	 */
	public boolean getAuthCodeCZ(String tel) {
	
		String username = CacheUtil.getParamValueByName("CZ_SM_USERNAME");
		String password = CacheUtil.getParamValueByName("CZ_SM_PASSWORD");
		//生成6位随机数
		char[] codeSeq = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		Random random = new Random();
		StringBuilder code = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			String randomCode = String.valueOf(codeSeq[random.nextInt(codeSeq.length)]);// random.nextInt(10));
			code.append(randomCode);
		}
		//生成文本内容
		String content = "指帮内购验证码"+ code.toString() +"【指帮内购】";
		//发送短信
		HttpBrowser browser = new HttpBrowser();
		HttpRequest request = HttpRequest.post("http://sms.chanzor.com:8001/sms.aspx?action=send");
		request.form("userid", "");
		request.form("account", username);
		request.form("password", password);
		request.form("mobile", tel);
		request.form("sendTime", "");
		request.form("content", content);
		System.out.println(request.url());
		
		HttpResponse resp;
		try {
			resp = browser.sendRequest(request);
		} catch (Exception e1) {
			try {
				resp = browser.sendRequest(request);
			} catch (Exception e) {
				resp = browser.sendRequest(request);
			}
		}
		
		//处理返回结果
		String msg ="";
		try {
			msg = new String(resp.body().getBytes("iso-8859-1"),"utf-8");
			System.out.println(msg);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			 return false;
		}
	    try {
			 Document  document = DocumentHelper.parseText(msg);  
			 Element returnsms = document.getRootElement();
			 Element returnstatus = returnsms.element("returnstatus");
			 if (returnstatus.getText().equals("Faild")) {
				 return false;
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			return false;
		} 
	    
		//保存验证码到缓存数据库中
		AuthCode obj = new AuthCode();
		obj.setCode(code.toString());
		obj.setTel(tel);
		obj.setCurrentTimeMillis(System.currentTimeMillis());
		saveCode(obj);
		return true;
	}
	
	public boolean sendMarketingSms(String tel ,String content) {
		String username = CacheUtil.getParamValueByName("CZ_YXSM_USERNAME");
		String password = CacheUtil.getParamValueByName("CZ_YXSM_PASSWORD");
		//生成6位随机数

		//生成文本内容
		//尊称+内容+回复TD退订+签名;
//		String content = "指帮内购验证码"+ code.toString() +"【速卖通】";
		//发送短信
		HttpBrowser browser = new HttpBrowser();
		HttpRequest request = HttpRequest.post("http://sms.chanzor.com:8001/sms.aspx?action=send");
		request.form("userid", "");
		request.form("account", username);
		request.form("password", password);
		request.form("mobile", tel);
		request.form("sendTime", "");
		request.form("content", content);
		System.out.println(request.url());
		
		HttpResponse resp;
		try {
			resp = browser.sendRequest(request);
		} catch (Exception e1) {
			try {
				resp = browser.sendRequest(request);
			} catch (Exception e) {
				resp = browser.sendRequest(request);
			}
		}
		
		//处理返回结果
		String msg ="";
		try {
			msg = new String(resp.body().getBytes("iso-8859-1"),"utf-8");
			System.out.println(msg);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			 return false;
		}
	    try {
			 Document  document = DocumentHelper.parseText(msg);  
			 Element returnsms = document.getRootElement();
			 Element returnstatus = returnsms.element("returnstatus");
			 if (returnstatus.getText().equals("Faild")) {
				 return false;
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			return false;
		} 
		return true;
	}
}
