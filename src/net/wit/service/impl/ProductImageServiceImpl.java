/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;
import net.wit.Setting;
import net.wit.entity.ProductImage;
import net.wit.entity.TenantSellCondition;
import net.wit.mobile.util.ShortUUID;
import net.wit.plugin.StoragePlugin;
import net.wit.service.ProductImageService;
import net.wit.util.FreemarkerUtils;
import net.wit.util.SettingUtils;



/**
 * Service - 商品图片
 * 
 * @author rsico Team
 * @version 3.0
 */
@Service("productImageServiceImpl")
public class ProductImageServiceImpl extends BaseServiceImpl<ProductImage, Long>  implements ProductImageService, ServletContextAware {

	/** 目标扩展名 */
	private static final String DEST_EXTENSION = "png";
	/** 目标文件类型 */
	private static final String DEST_CONTENT_TYPE = "image/jpeg";

	/** servletContext */
	private ServletContext servletContext;

	@Resource(name = "taskExecutor")
	private TaskExecutor taskExecutor;
	@Resource
	private List<StoragePlugin> storagePlugins;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * 添加图片处理任务
	 * 
	 * @param sourcePath
	 *            原图片上传路径
	 * @param largePath
	 *            图片文件(大)上传路径
	 * @param mediumPath
	 *            图片文件(小)上传路径
	 * @param thumbnailPath
	 *            图片文件(缩略)上传路径
	 * @param tempFile
	 *            原临时文件
	 * @param contentType
	 *            原文件类型
	 */
	private void addTask(final ProductImage productImage,   final String sourcePath, final String largePath, final String mediumPath, final String thumbnailPath, final File tempFile, final String contentType) {
		try {
			taskExecutor.execute(new Runnable() {
				public void run() {
					Collections.sort(storagePlugins);
					for (StoragePlugin storagePlugin : storagePlugins) {
						if (storagePlugin.getIsEnabled()) {
							Setting setting = SettingUtils.get();
							String tempPath = System.getProperty("java.io.tmpdir");
							
							//File watermarkFile = new File(servletContext.getRealPath(setting.getWatermarkImage()));
							File largeTempFile = new File(tempPath + "/upload_" + UUID.randomUUID() + "." + DEST_EXTENSION);
							File mediumTempFile = new File(tempPath + "/upload_" + UUID.randomUUID() + "." + DEST_EXTENSION);
							File thumbnailTempFile = new File(tempPath + "/upload_" + UUID.randomUUID() + "." + DEST_EXTENSION);
							try {
								BufferedImage bi = ImageIO.read(tempFile);
								
								int resouceHeight = bi.getHeight();
								int resouceWidht = bi.getWidth();
								double scale = (double)resouceHeight/(double)resouceWidht;
								productImage.setSourceHeight(resouceHeight);
								productImage.setSourceWidth(resouceWidht);
								int largeWidth = setting.getLargeProductImageWidth();
								int largeHeight =(int) ((double)setting.getLargeProductImageHeight()*scale);
								int mediumWidth = setting.getMediumProductImageWidth();
								int mediumHeight =(int) ((double)setting.getMediumProductImageHeight()*scale);
								int thumbnailWidth = setting.getThumbnailProductImageWidth();
								int thumbnailHeight = (int)((double)setting.getThumbnailProductImageHeight()*scale);
								productImage.setLargeHeight(largeHeight);
								productImage.setLargeWidth(largeWidth);
								productImage.setMediumWidth(mediumWidth);
								productImage.setMediumHeight(mediumHeight);
								productImage.setThumbnailWidth(thumbnailWidth);
								productImage.setThumbnailHeight(thumbnailHeight);
								Thumbnails.of(bi).forceSize(largeWidth, largeHeight).outputFormat(DEST_EXTENSION).toFile(largeTempFile);
								Thumbnails.of(bi).forceSize(mediumWidth, mediumHeight).outputFormat(DEST_EXTENSION).toFile(mediumTempFile);
								Thumbnails.of(bi).forceSize(thumbnailWidth, thumbnailHeight).outputFormat(DEST_EXTENSION).toFile(thumbnailTempFile);
								
//								ImageUtils.zoom(tempFile, largeTempFile, setting.getLargeProductImageWidth(), setting.getLargeProductImageHeight());
//								ImageUtils.addWatermark(largeTempFile, largeTempFile, watermarkFile, setting.getWatermarkPosition(), setting.getWatermarkAlpha());
//								ImageUtils.zoom(tempFile, mediumTempFile, setting.getMediumProductImageWidth(), setting.getMediumProductImageHeight());
//								ImageUtils.addWatermark(mediumTempFile, mediumTempFile, watermarkFile, setting.getWatermarkPosition(), setting.getWatermarkAlpha());
//								ImageUtils.zoom(tempFile, thumbnailTempFile, setting.getThumbnailProductImageWidth(), setting.getThumbnailProductImageHeight());
								storagePlugin.upload(sourcePath, tempFile, contentType);
								storagePlugin.upload(largePath, largeTempFile, DEST_CONTENT_TYPE);
								storagePlugin.upload(mediumPath, mediumTempFile, DEST_CONTENT_TYPE);
								storagePlugin.upload(thumbnailPath, thumbnailTempFile, DEST_CONTENT_TYPE);
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
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void build(ProductImage productImage) {
		MultipartFile multipartFile = productImage.getFile();
		if (multipartFile != null && !multipartFile.isEmpty()) {
			try {
				Setting setting = SettingUtils.get();
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("uuid", UUID.randomUUID().toString());
				String uploadPath = FreemarkerUtils.process(setting.getImageUploadPath(), model);
				String uuid = UUID.randomUUID().toString();
				String sourcePath = uploadPath + uuid + "-source." + FilenameUtils.getExtension(multipartFile.getOriginalFilename());
				String largePath = uploadPath + uuid + "-large." + DEST_EXTENSION;
				String mediumPath = uploadPath + uuid + "-medium." + DEST_EXTENSION;
				String thumbnailPath = uploadPath + uuid + "-thumbnail." + DEST_EXTENSION;

				Collections.sort(storagePlugins);
				for (StoragePlugin storagePlugin : storagePlugins) {
					if (storagePlugin.getIsEnabled()) {
						File tempFile = new File(System.getProperty("java.io.tmpdir") + "/upload_" + UUID.randomUUID() + ".tmp");
						if (!tempFile.getParentFile().exists()) {
							tempFile.getParentFile().mkdirs();
						}
						multipartFile.transferTo(tempFile);
						addTask(productImage,sourcePath, largePath, mediumPath, thumbnailPath, tempFile, multipartFile.getContentType());
						productImage.setSource(storagePlugin.getUrl(sourcePath));
						productImage.setLarge(storagePlugin.getUrl(largePath));
						productImage.setMedium(storagePlugin.getUrl(mediumPath));
						productImage.setThumbnail(storagePlugin.getUrl(thumbnailPath));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
//	private void calcSmallZoomSize(BufferedImage bufferImage, int zoom) {
//		
//		double orgWidth = (double) bufferImage.getWidth();
//		double orgHeight = (double) bufferImage.getHeight();
//		if (orgWidth <= zoom && orgHeight <= zoom) {
//			zoomWidth = (int) orgWidth;
//			zoomHeight = (int) orgHeight;
//			return;
//		}
//		if (orgWidth >= orgHeight) {
//			zoomWidth = LONG_SIDE_PX;
//			double scale = orgHeight / orgWidth;
//			zoomHeight = (int) (scale * LONG_SIDE_PX);
//		} else {
//			zoomHeight = LONG_SIDE_PX;
//			double scale = orgWidth / orgHeight;
//			zoomWidth = (int) (scale * LONG_SIDE_PX);
//		}
//	}
	
//	public String savePhoto(MultipartFile file) {
//			InputStream ins = file.getInputStream();
//			BufferedImage bi = ImageIO.read(ins);
//
//			Thumbnails.of(bi).scale(1f).outputFormat("png").toFile(realPath + "_big");
//			Thumbnails.of(bi).forceSize(zoomWidth, zoomHeight)	.outputFormat("png").toFile(realPath + "_small");
//			Thumbnails.of(bi).forceSize(arg0, arg1)
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			return "";
//		}
//		return id;
//	}

}