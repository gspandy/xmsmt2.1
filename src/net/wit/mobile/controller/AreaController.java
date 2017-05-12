/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: AreaController.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月16日
 */
package net.wit.mobile.controller;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.wit.entity.Area;
import net.wit.service.AreaService;
import net.wit.util.CacheUtil;
import net.wit.util.JsonUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月16日
 */
@Controller
@RequestMapping(value = "/area")
public class AreaController extends BaseController{
	
	@Autowired
	private AreaService   areaService;
	
	 /**
     * 返回省份列表信息
     */
    @RequestMapping(value = "/provinceList")
    public void provinceList(HttpServletResponse response) throws Exception {
    	try {
	            
	            	  JSONObject resultValue = new JSONObject();
	            	  List<Area> provinceList=areaService.findRoots();
	            	  resultValue.put("product", JsonUtils.toJson(provinceList));
	            	  this.handleJsonResponse(response, true,"",resultValue );
        } catch (Exception e) {
            this.handleJsonResponse(response, false, "获取省份列表失败");
            e.printStackTrace();
        }
    }
    /**
     * 返回地区列表信息
     */
    @RequestMapping(value = "/cityList")
    public void cityList(HttpServletResponse response, @RequestParam("provinceId") String provinceId
    		) throws Exception {
    	try {
	            if(provinceId!=null&&!(provinceId.equals(""))){
	            	
	            	 JSONObject resultValue = new JSONObject();
	            	  Area area=areaService.find(Long.parseLong(provinceId));
	            	  Set<Area> areaSet=area.getChildren();
	            	  resultValue.put("provinceList", JsonUtils.toJson(areaSet));
	            	  this.handleJsonResponse(response, true,"",resultValue );
	            }else{
	            	this.handleJsonResponse(response, false, "请选择省份信息");
	            }
	            
        } catch (Exception e) {
            this.handleJsonResponse(response, false, "获取市列表失败");
            e.printStackTrace();
        }
    }

}
