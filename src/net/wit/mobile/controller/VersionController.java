/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: versionUpdateController.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月17日
 */
package net.wit.mobile.controller;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.wit.entity.VersionUpdate;
import net.wit.service.VersionUpdateService;
import net.wit.util.CacheUtil;
import net.wit.util.JsonUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 客户端版本类
 * @author Teddy
 */
@Controller
@RequestMapping(value = "/versionUpdate")
public class VersionController extends BaseController{
	
	@Autowired
	private VersionUpdateService versionUpdateService;
	
	/**
     * 返回客户端版本更新信息
     */
    @RequestMapping(value = "/clientVersion")
    public void clientVersion(HttpServletResponse response
    		,@RequestParam("versionType") String versionType
    		) throws Exception {
    	try {
	            	  JSONObject resultValue = new JSONObject();
	            	  VersionUpdate  versionUpdat= versionUpdateService.versionUpdate(versionType);
	            	  if(versionUpdat==null){
	            		  this.handleJsonResponse(response, false,"获取版本失败" ); 
	            	  }else{
	            		  resultValue.put("versionNo", versionUpdat.getVersionNo());
	            		  resultValue.put("versionDownloadUrl",versionUpdat.getVersionDownloadUrl());
	            		  resultValue.put("defaultUpdate", versionUpdat.getDefaultUpdate());
	            		  this.handleJsonResponse(response, true,"",resultValue ); 
	            	  }	     
        } catch (Exception e) {
            this.handleJsonResponse(response, false, "客户端版本查询失败");
            e.printStackTrace();
        }
    }
    
}
