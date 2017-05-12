/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.controller.common;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.wit.Setting;
import net.wit.entity.Area;
import net.wit.entity.ProductCategory;
import net.wit.service.AreaService;
import net.wit.service.CaptchaService;
import net.wit.service.MemberService;
import net.wit.service.ProductCategoryService;
import net.wit.service.RSAService;
import net.wit.util.QRBarCodeUtil;
import net.wit.util.SettingUtils;

/**
 * Controller - 共用
 * @author rsico Team
 * @version 3.0
 */
@Controller("commonCommonController")
@RequestMapping("/common")
public class CommonController {

	/** 二维码 */
	public final static String QRCODE = "qrcode";

	/** 一维码 */
	public final static String BARCODE = "qrcode";

	@Resource(name = "rsaServiceImpl")
	private RSAService rsaService;

	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	@Resource(name = "captchaServiceImpl")
	private CaptchaService captchaService;

	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	/** 网站关闭 */
	@RequestMapping("/site_close")
	public String siteClose() {
		Setting setting = SettingUtils.get();
		if (setting.getIsSiteEnabled()) {
			return "redirect:/";
		} else {
			return "/common/site_close";
		}
	}

	/** 公钥 */
	@RequestMapping(value = "/public_key", method = RequestMethod.GET)
	public @ResponseBody
	Map<String, String> publicKey(HttpServletRequest request) {
		RSAPublicKey publicKey = rsaService.generateKey(request);
		Map<String, String> data = new HashMap<String, String>();
		data.put("modulus", Base64.encodeBase64String(publicKey.getModulus().toByteArray()));
		data.put("exponent", Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray()));
		return data;
	}

	/** 地区 */
	@RequestMapping(value = "/area", method = RequestMethod.GET)
	public @ResponseBody
	Map<Long, String> area(Long parentId) {
		List<Area> areas = new ArrayList<Area>();
		Area parent = areaService.find(parentId);
		if (parent != null) {
			areas = new ArrayList<Area>(parent.getChildren());
		} else {
			areas = areaService.findRoots();
		}
		Map<Long, String> options = new HashMap<Long, String>();
		for (Area area : areas) {
			options.put(area.getId(), area.getName());
		}
		return options;
	}

	/** 商品分类 */
	@RequestMapping(value = "/productCategory", method = RequestMethod.GET)
	public @ResponseBody
	Map<Long, String> productCategory(Long parentId) {
		List<ProductCategory> productCategorys = new ArrayList<ProductCategory>();
		ProductCategory parent = productCategoryService.find(parentId);
		if (parent != null) {
			productCategorys = new ArrayList<ProductCategory>(parent.getChildren());
		} else {
			productCategorys = productCategoryService.findRoots();
		}
		Map<Long, String> options = new HashMap<Long, String>();
		for (ProductCategory productCategory : productCategorys) {
			options.put(productCategory.getId(), productCategory.getName());
		}
		return options;
	}

	/** 地区 */
	@RequestMapping(value = "/area_key", method = RequestMethod.GET)
	public @ResponseBody
	Area area_key(Long id) {
		return areaService.find(id);
	}

	/** 验证码 */
	@RequestMapping(value = "/captcha", method = RequestMethod.GET)
	public void image(String captchaId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (StringUtils.isEmpty(captchaId)) {
			captchaId = request.getSession().getId();
		}
		String pragma = new StringBuffer().append("yB").append("-").append("der").append("ewoP").reverse().toString();
		String value = new StringBuffer().append("ten").append(".").append("xxp").append("ohs").reverse().toString();
		response.addHeader(pragma, value);
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Cache-Control", "no-store");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");

		ServletOutputStream servletOutputStream = null;
		try {
			servletOutputStream = response.getOutputStream();
			BufferedImage bufferedImage = captchaService.buildImage(captchaId);
			ImageIO.write(bufferedImage, "jpg", servletOutputStream);
			servletOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(servletOutputStream);
		}
	}

	/** 一维码 */
	@RequestMapping(value = "/qbarcode", method = RequestMethod.GET)
	public void qbarcode(String contents, String codetype, Integer width, Integer height, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (StringUtils.isEmpty(contents)) {
			return;
		}
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Cache-Control", "no-store");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		ServletOutputStream servletOutputStream = null;
		try {
			servletOutputStream = response.getOutputStream();
			String codepath = System.getProperty("java.io.tmpdir") + "/code_" + UUID.randomUUID() + ".jpg";
			if (QRCODE.equals(codetype)) {
				if (width == null) {
					width = 200;
				}
				if (height == null) {
					height = 200;
				}
				QRBarCodeUtil.encodeQRCode(contents, codepath, width, height);
			} else {
				if (width == null) {
					width = 300;
				}
				if (height == null) {
					height = 48;
				}
				QRBarCodeUtil.encodeBarCode(contents, codepath, width, height, 30);
			}
			BufferedImage bufferedImage = ImageIO.read(new FileInputStream(codepath));
			ImageIO.write(bufferedImage, "jpg", servletOutputStream);
			servletOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(servletOutputStream);
		}
	}

	/** 错误提示 */
	@RequestMapping("/error")
	public String error() {
		return "/common/error";
	}

	/** 资源不存在 */
	@RequestMapping("/resource_not_found")
	public String resourceNotFound() {
		return "/common/resource_not_found";
	}

}