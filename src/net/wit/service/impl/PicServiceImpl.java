/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import net.wit.Principal;
import net.wit.Setting;
import net.wit.dao.AdminDao;
import net.wit.dao.MemberDao;
import net.wit.dao.PicDao;
import net.wit.dao.ProductDao;
import net.wit.entity.Admin;
import net.wit.entity.Member;
import net.wit.entity.Member.Gender;
import net.wit.entity.Pic;
import net.wit.entity.ProductImage;
import net.wit.entity.Role;
import net.wit.entity.Tenant;
import net.wit.mobile.bo.NToken;
import net.wit.mobile.dao.INtokenDao;
import net.wit.mobile.util.ShortUUID;
import net.wit.plugin.StoragePlugin;
import net.wit.service.AdminService;
import net.wit.service.PicService;
import net.wit.util.FreemarkerUtils;
import net.wit.util.SettingUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service - 管理员
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("picServiceImpl")
public class PicServiceImpl extends BaseServiceImpl<Pic, Long> implements
		PicService {

	@Resource(name = "picDaoImpl")
	public void setBaseDao(PicDao picDao) {
		super.setBaseDao(picDao);
	}

	@Resource(name = "picDaoImpl")
	private PicDao picDao;

	@Autowired
	private MemberDao memberDao;

	@Autowired
	private INtokenDao tokenDao;

	@Resource
	private List<StoragePlugin> storagePlugins;

	/** 目标扩展名 */
	private static final String DEST_EXTENSION = "png";
	/** 目标文件类型 */
	private static final String DEST_CONTENT_TYPE = "image/jpeg";


	public static final String SHAREIMAGE = "shareImage";
	
	public static final String TICKETSHARE = "ticket_share";

	public static final String TENANTINVITATION = "tenant_invitation";

	public static final String SHOPKEEPINVITATION = "shopkeep_invitation";

	public static final String picFemaleSufferFix = "/pic/default_head_female.png";
	
	public static final String picMaleSufferFix = "/pic/default_head_male.png";
	
	public static final String picMemberOpenShopFix = "/pic/member_open_shopkeeper.png";

	public Pic uploadImage(NToken nToken, MultipartFile file) {
		if (file != null && !file.isEmpty()) {
			try {
				Pic pic = new Pic();
				pic.setPicType("headImg");
				pic.setUploadDate(new Date());
				Setting setting = SettingUtils.get();
				Map<String, Object> model = new HashMap<String, Object>();
				String uploadPath = FreemarkerUtils.process(
						setting.getImageUploadPath(), model);
				String uuid = ShortUUID.genId();
				String largePath = uploadPath + uuid + "-large."
						+ DEST_EXTENSION;
				String mediumPath = uploadPath + uuid + "-medium."
						+ DEST_EXTENSION;
				String thumbnailPath = uploadPath + uuid + "-thumbnail."
						+ DEST_EXTENSION;
				Collections.sort(storagePlugins);
				for (StoragePlugin storagePlugin : storagePlugins) {
					if (storagePlugin.getIsEnabled()) {
						File tempFile = new File(
								System.getProperty("java.io.tmpdir")
										+ "/upload_" + ShortUUID.genId()
										+ ".tmp");
						if (!tempFile.getParentFile().exists()) {
							tempFile.getParentFile().mkdirs();
						}
						file.transferTo(tempFile);
						this.addTask(pic, largePath, mediumPath, thumbnailPath,
								tempFile);
						pic.setLargeUrl(storagePlugin.getUrl(largePath));
						pic.setMediumUrl(storagePlugin.getUrl(mediumPath));
						pic.setSmallUrl(storagePlugin.getUrl(thumbnailPath));
						this.save(pic);

						Member member = memberDao.find(new Long(nToken
								.getMemberId()));
						member.setHeadImg(pic.getSmallUrl());
						memberDao.persist(member);

						nToken.setHeadImg(pic.getSmallUrl());
						tokenDao.update(nToken);

						return pic;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public Pic uploadPic(Long tenantId, String picType, MultipartFile file) {
		if (file != null && !file.isEmpty()) {
			try {
				Pic pic = new Pic();
				pic.setTenantId(tenantId);
				pic.setPicType(picType);
				pic.setUploadDate(new Date());
				Setting setting = SettingUtils.get();
				Map<String, Object> model = new HashMap<String, Object>();
				String uploadPath = FreemarkerUtils.process(
						setting.getImageUploadPath(), model);
				String uuid = ShortUUID.genId();
				String largePath = uploadPath + uuid + "-large."
						+ DEST_EXTENSION;
				String mediumPath = uploadPath + uuid + "-medium."
						+ DEST_EXTENSION;
				String thumbnailPath = uploadPath + uuid + "-thumbnail."
						+ DEST_EXTENSION;
				Collections.sort(storagePlugins);
				for (StoragePlugin storagePlugin : storagePlugins) {
					if (storagePlugin.getIsEnabled()) {
						File tempFile = new File(
								System.getProperty("java.io.tmpdir")
										+ "/upload_" + ShortUUID.genId()
										+ ".tmp");
						if (!tempFile.getParentFile().exists()) {
							tempFile.getParentFile().mkdirs();
						}
						file.transferTo(tempFile);
						this.addTask(pic, largePath, mediumPath, thumbnailPath,
								tempFile);
						pic.setLargeUrl(storagePlugin.getUrl(largePath));
						pic.setMediumUrl(storagePlugin.getUrl(mediumPath));
						pic.setSmallUrl(storagePlugin.getUrl(thumbnailPath));
						this.save(pic);
						return pic;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	private void addTask(Pic pic, String largePath, String mediumPath,
			String thumbnailPath, File tempFile) {
		try {
			Collections.sort(storagePlugins);
			for (StoragePlugin storagePlugin : storagePlugins) {
				if (storagePlugin.getIsEnabled()) {
					Setting setting = SettingUtils.get();
					String tempPath = System.getProperty("java.io.tmpdir");

					// File watermarkFile = new
					// File(servletContext.getRealPath(setting.getWatermarkImage()));
					File largeTempFile = new File(tempPath + "/upload_"
							+ UUID.randomUUID() + "." + DEST_EXTENSION);
					File mediumTempFile = new File(tempPath + "/upload_"
							+ UUID.randomUUID() + "." + DEST_EXTENSION);
					File thumbnailTempFile = new File(tempPath + "/upload_"
							+ UUID.randomUUID() + "." + DEST_EXTENSION);
					try {
						BufferedImage bi = ImageIO.read(tempFile);
						int resouceHeight = bi.getHeight();
						int resouceWidht = bi.getWidth();
						double scale = (double) resouceHeight
								/ (double) resouceWidht;
						// int largeWidth = setting.getLargeProductImageWidth();
						// int largeHeight =(int)
						// ((double)setting.getLargeProductImageHeight()*scale);
						int mediumWidth = setting.getMediumProductImageWidth();
						int mediumHeight = (int) ((double) setting
								.getMediumProductImageHeight() * scale);
						int thumbnailWidth = setting
								.getThumbnailProductImageWidth();
						int thumbnailHeight = (int) ((double) setting
								.getThumbnailProductImageHeight() * scale);
						pic.setLargeHeight(resouceHeight);
						pic.setLargeWidth(resouceWidht);
						pic.setMediumWidth(mediumWidth);
						pic.setMediumHeight(mediumHeight);
						pic.setThumbnailWidth(thumbnailWidth);
						pic.setThumbnailHeight(thumbnailHeight);
						Thumbnails.of(bi).scale(1.0)
								.outputFormat(DEST_EXTENSION)
								.toFile(largeTempFile);
						Thumbnails.of(bi).forceSize(mediumWidth, mediumHeight)
								.outputFormat(DEST_EXTENSION)
								.toFile(mediumTempFile);
						Thumbnails.of(bi)
								.forceSize(thumbnailWidth, thumbnailHeight)
								.outputFormat(DEST_EXTENSION)
								.toFile(thumbnailTempFile);
						storagePlugin.upload(largePath, largeTempFile,
								DEST_CONTENT_TYPE);
						storagePlugin.upload(mediumPath, mediumTempFile,
								DEST_CONTENT_TYPE);
						storagePlugin.upload(thumbnailPath, thumbnailTempFile,
								DEST_CONTENT_TYPE);
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						FileUtils.deleteQuietly(tempFile);
						FileUtils.deleteQuietly(largeTempFile);
						FileUtils.deleteQuietly(mediumTempFile);
						FileUtils.deleteQuietly(thumbnailTempFile);
					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Pic getPicByPicId(Long picId) {
		return this.find(picId);
	}

	public String getDefaultHeadImage(Member member) {
		String sufferfix = "";
		if (member.getGender() == null) {
			member.setGender(Gender.male); // 如果性别为空,默认为男性
		}
		if (member.getGender().ordinal() == Member.Gender.male.ordinal()) {
			sufferfix = picMaleSufferFix;
		} else {
			sufferfix = picFemaleSufferFix;
		}
		Setting se = SettingUtils.get();
		String siteUrl = se.getSiteUrl();
		if (siteUrl.endsWith("/"))
			siteUrl = siteUrl.substring(0, siteUrl.length() - 1);
		return siteUrl + sufferfix;
	}
	
	@Override
	public String getMemberOpenShopImage() {
		String sufferfix = picMemberOpenShopFix;
		Setting se = SettingUtils.get();
		String siteUrl = se.getSiteUrl();
		if (siteUrl.endsWith("/"))
			siteUrl = siteUrl.substring(0, siteUrl.length() - 1);
		return siteUrl + sufferfix;
	}

	public String getTickShareImageByTenant(Tenant tenant) {
		List<Pic> list;
		String shareImageUrl = "";
		list = this.picDao.getPicByTenantAndType(tenant, TICKETSHARE);
		if (list.size() > 0)
			shareImageUrl = list.get(0).getLargeUrl();
		return shareImageUrl;
	}

	public String getTenantInviteImage(Tenant tenant) {
		List<Pic> list;
		String shareImageUrl = "";
		list = this.picDao.getPicByTenantAndType(tenant, TENANTINVITATION);
		if (list.size() > 0)
			shareImageUrl = list.get(0).getLargeUrl();
		return shareImageUrl;
	}

	@Override
	public String getMemberInvitesImage(Tenant tenant) {
		List<Pic> list;
		String shareImageUrl = "";
		list = this.picDao.getPicByTenantAndType(tenant, SHAREIMAGE);
		if (list.size() > 0)
			shareImageUrl = list.get(0).getLargeUrl();
		return shareImageUrl;
	}

	/**
	 * 店主邀请分享页图片
	 * 
	 * @param tenant
	 * @return
	 */
	public String getShopKeeperInviteImage(Tenant tenant) {
		List<Pic> list;
		String shareImageUrl = "";
		list = this.picDao.getPicByTenantAndType(tenant, SHOPKEEPINVITATION);
		if (list.size() > 0)
			shareImageUrl = list.get(0).getLargeUrl();
		return shareImageUrl;
	}

	@Override
	public Map<String, Pic> findMap(Tenant tenant) {
		List<Pic> ticketList = picDao
				.getPicByTenantAndType(tenant, TICKETSHARE);// 获取券分享图片信息
		List<Pic> tenantList = picDao.getPicByTenantAndType(tenant,
				TENANTINVITATION);// 企业邀请图片信息
		List<Pic> shopkeepList = picDao.getPicByTenantAndType(tenant,
				SHOPKEEPINVITATION);// 店主邀请图片信息
		Map<String, Pic> map = new HashMap<String, Pic>();
		if (ticketList.size() == 0) {
			map.put("ticketShare", new Pic());
		} else {
			map.put("ticketShare", ticketList.get(0));
		}
		if (tenantList.size() == 0) {
			map.put("tenantInvitation", new Pic());
		} else {
			map.put("tenantInvitation", tenantList.get(0));
		}
		if (shopkeepList.size() == 0) {
			map.put("shopkeepInvitation", new Pic());
		} else {
			map.put("shopkeepInvitation", shopkeepList.get(0));
		}
		return map;
	}

}