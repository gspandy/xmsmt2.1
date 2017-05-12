package net.wit.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件操作工具
 * @author Administrator
 */
public class FileUtil {

	protected static Log log = LogFactory.getLog(FileUtil.class);

	private static final int BUFFER_SIZE = 16 * 1024;

	/**
	 * 缓存操作文件拷贝 由原至目标
	 * @param
	 */
	public static boolean copyFile(File src, File dst) {
		try {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = new BufferedInputStream(new FileInputStream(src), BUFFER_SIZE);
				out = new BufferedOutputStream(new FileOutputStream(dst), BUFFER_SIZE);
				byte[] buffer = new byte[BUFFER_SIZE];
				while (in.read(buffer) > 0) {
					out.write(buffer);
				}
			} finally {
				if (null != in) {
					in.close();
				}
				if (null != out) {
					out.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 解压缩文件
	 * @author Ryan.xu
	 * @param args
	 */
	public static List<String> Extract(String sZipPathFile, String sDestPath) {
		// int iCompressLevel; // 压缩比 取值范围为0~9
		// boolean bOverWrite = true; // 是否覆盖同名文件 取值范围为True和False
		// ArrayList allFiles = new ArrayList();
		// String sErrorMessage;
		List<String> allFileName = new ArrayList<String>();
		FileInputStream fins = null;
		ZipInputStream zins = null;
		try {
			// 先指定压缩档的位置和档名，建立FileInputStream对象
			fins = new FileInputStream(sZipPathFile);
			// 将fins传入ZipInputStream中
			zins = new ZipInputStream(fins);
			ZipEntry ze = null;
			byte ch[] = new byte[256];
			while ((ze = zins.getNextEntry()) != null) {
				File zfile = new File(sDestPath + ze.getName());
				if (zfile.exists()) {
					continue;
				}
				File fpath = new File(zfile.getParentFile().getPath());
				if (ze.isDirectory()) {
					if (!zfile.exists())
						zfile.mkdirs();
					zins.closeEntry();
				} else {
					if (!fpath.exists())
						fpath.mkdirs();
					FileOutputStream fouts = new FileOutputStream(zfile);
					try {
						int i;
						allFileName.add(zfile.getAbsolutePath());
						while ((i = zins.read(ch)) != -1)
							fouts.write(ch, 0, i);
						zins.closeEntry();
					} finally {
						fouts.close();
					}
				}
			}
		} catch (Exception e) {
		} finally {
			if (null != fins) {
				try {
					fins.close();
				} catch (IOException e) {
				}
			}
			if (null != zins) {
				try {
					zins.close();
				} catch (IOException e) {
				}
			}
		}
		// allFiles.clear();
		return allFileName;
	}

	public static String getType(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	public static void write2File(final String organTreeFilePath, final String content) {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(organTreeFilePath)), "UTF-8"));
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static long uniqueID = System.currentTimeMillis();// length=13

	private static Random oRandom = new Random();

	/**
	 * 18位唯一值序列号
	 * @return
	 */
	public static synchronized String getUniqueID() {
		uniqueID++;
		// wanhm 2010-3-23 return (uniqueID*1000+oRandom.nextInt(9999999));
		return String.valueOf((uniqueID * 100000 + oRandom.nextInt(99999)));// 这样可以从主键中反馈出时间来，否则按上面的是反馈不出来的。
	}

	/**
	 * 上传单个图片至服务器（并生成一张缩略图）
	 * @param MultipartFile multifile 文件 String basePath 服务器的基路径
	 * @return TupleTwo<String, String> null表无上传文件或者上传失败 TupleTwo<String, String> 参数1表结果文件路径 参数2表原始文件名
	 */
	public TupleTwo<String, String> uploadImage(MultipartFile file, String basePath) {
		if (null == file || !StringUtils.hasText(file.getOriginalFilename()))
			return null;

		basePath = buildUploadPath(basePath);
		final String orginalFile = file.getOriginalFilename();
		final String suffix = orginalFile.substring(orginalFile.indexOf("."));
		// 判断是否图片格式
		if (!isImage(suffix)) {
			return null;
		}
		String target = basePath + this.getUniqueID();
		// String thumb_target = target + "_" + Constants.THUMB + suffix;
		target += suffix;
		try {
			InputStream inputStream = file.getInputStream();
			OutputStream outputStream = new FileOutputStream(target);
			byte[] buffer = file.getBytes();
			int bytesum = 0;
			int byteread = 0;
			while ((byteread = inputStream.read(buffer)) != -1) {
				bytesum += byteread;
				outputStream.write(buffer, 0, byteread);
				outputStream.flush();
			}
			outputStream.close();
			inputStream.close();

			// 生成一张缩略图
			// ImageUtil.zoomImageW(target, thumb_target, Constants.THUMB_WIDTH, suffix.substring(1));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return new TupleTwo<String, String>(target.substring(getWebRoot().length()), file.getOriginalFilename());
	}

	/**
	 * 手机上传图片（通过数据流）
	 * @param request
	 * @return
	 */
	public static String uploadImage_phone(HttpServletRequest request) {

		try {

			// 输入流
			InputStream in = request.getInputStream();

			String contrastTime = Constants.YMFORMAT.format(new Date());
			String basePath = getWebRoot() + Constants.getSecurityUpload() + contrastTime.split("-")[0] + Constants.getSeparator() + contrastTime.split("-")[1] + Constants.getSeparator();
			File uploadFile = new File(basePath);
			if (!uploadFile.exists()) {
				uploadFile.mkdirs();
			}

			final String fileName = getUniqueID();
			final String target = basePath + fileName + Constants.SUFFIX;
			File file = new File(target);
			FileOutputStream out = new FileOutputStream(file);

			byte[] buffer = new byte[1024];
			int length = -1;
			/* 从文件读取数据至缓冲区 */
			while ((length = in.read(buffer)) != -1) {
				/* 将资料写入FileOutputStream中 */
				out.write(buffer, 0, length);
			}
			in.close();
			out.flush();
			out.close();

			// 生成一张缩略图
			// final String thumb_target = basePath + fileName + "_" + Constants.THUMB + Constants.SUFFIX;
			// ImageUtil.zoomImageW(target, thumb_target, Constants.THUMB_WIDTH, Constants.SUFFIX.substring(1));

			String dbpath = (Constants.getSecurityUpload() + contrastTime.split("-")[0] + Constants.getSeparator() + contrastTime.split("-")[1] + Constants.getSeparator() + fileName + Constants.SUFFIX);
			return dbpath;

		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 删除存在服务器的图片文件（包括缩略图）
	 * @param imgUrl 如 WEB-INF\resource\137601741510084568.png
	 */
	public static void deleteImage(String imgUrl) {
		String thumbUrl = imgUrl;
		if (org.apache.commons.lang.StringUtils.isNotEmpty(thumbUrl)) {
			String thumbTarget = thumbUrl.substring(thumbUrl.indexOf("."));// 取后缀
			thumbUrl = thumbUrl.substring(0, thumbUrl.indexOf("."));// 取.之前的
			thumbUrl += "_" + Constants.THUMB + thumbTarget;// 加入thumb拼接

			File fileThumb = new File(getWebRoot() + thumbUrl);
			File file = new File(getWebRoot() + imgUrl);
			if (file.exists()) {
				file.delete();
			}
			if (fileThumb.exists()) {
				fileThumb.delete();
			}
		}
	}

	private static String getWebRoot() {
		return ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/");
	}

	/**
	 * 通过判断图片的宽度和高度来确定是否是图片
	 * @param formatName 文件后缀
	 * @return
	 */
	private boolean isImage(String formatName) {

		if (StringUtils.isEmpty(formatName)) {
			return false;
		}
		Pattern pattern = Pattern.compile("^(.jpg|.jpeg|.png|.gif|.bmp)$");
		Matcher matcher = pattern.matcher(formatName.toLowerCase());
		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	// 硬盘上创建文件夹，并返回文件路径
	private String buildUploadPath(String basePath) {
		String contrastTime = Constants.YMFORMAT.format(new Date());
		String year = contrastTime.split("-")[0] + Constants.getSeparator() + contrastTime.split("-")[1] + Constants.getSeparator();
		if (basePath.charAt(basePath.length() - 1) != File.separatorChar) {
			basePath += Constants.getSeparator() + year;
		} else {
			basePath += year;
		}
		File uploadDir = new File(basePath);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}

		return basePath;
	}

	static class Tester {
		public static void main(String[] args) {
			System.out.println(getType("\\WEB-INF\\resource\\upload\\zz."));
		}
	}
}
