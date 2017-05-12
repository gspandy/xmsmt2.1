package net.wit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import sun.misc.BASE64Encoder;

/**
 * @author dingzy
 * 
 * @create 2014年9月24日18:14:51
 * 
 * @description 物业云部分使用到的一些方法工具
 * 
 */
public class PropertyUtil {
	 	private static final long ONE_MINUTE = 60000L;
	    private static final long ONE_HOUR = 3600000L;
	    private static final long ONE_DAY = 86400000L;
	    private static final long ONE_WEEK = 604800000L;

	    private static final String ONE_SECOND_AGO = "秒前";
	    private static final String ONE_MINUTE_AGO = "分钟前";
	    private static final String ONE_HOUR_AGO = "小时前";
	    private static final String ONE_DAY_AGO = "天前";
	    private static final String ONE_MONTH_AGO = "月前";
	    private static final String ONE_YEAR_AGO = "年前";
	/**
	 * @descrption 根据HttpServletRequest对象获取MultipartFile集合
	 * @author dingzy
	 * @param request
	 * @param maxLength
	 *            <strong>文件</strong>最大限制
	 * @param allowExtName
	 *            不允许上传的<strong>文件</strong>扩展名
	 * @return MultipartFile集合
	 * @param attrName 表单中文件控件的name属性
	 */
	public static List<MultipartFile> getFileSet(HttpServletRequest request,
			long maxLength, String[] allowExtName, String attrName) {
		MultipartHttpServletRequest multipartRequest = null;
		try {
			multipartRequest = (MultipartHttpServletRequest) request;
		} catch (Exception e) {
			return new LinkedList<MultipartFile>();
		}

		List<MultipartFile> files = new LinkedList<MultipartFile>();
		files = multipartRequest.getFiles(attrName);
		// 移除不符合条件的
		for (int i = 0; i < files.size(); i++) {
			if (!validateFile(files.get(i), maxLength, allowExtName)) {
				files.remove(files.get(i));
				if (files.size() == 0) {
					return files;
				}
			}
		}
		return files;
	}

	/**
	 * @descrption 保存<strong>文件</strong>
	 * @author dingzy
	 * @param file
	 *            MultipartFile对象
	 * @param fileName
	 *            要存储为的文件名，不需要后缀名
	 * @param path
	 *            保存路径，如“D:\\File\\”
	 * @return 保存的全路径 如“D:\\File\\2345678.txt”
	 * @throws Exception
	 *             <strong>文件</strong>保存失败
	 */
	public static String uploadFile(MultipartFile file, String fileName, String path)
			throws Exception {

		String filename = file.getOriginalFilename();
		String extName = filename.substring(filename.lastIndexOf("."))
				.toLowerCase();
		String lastFileName = fileName + extName;
		if (!path.endsWith(File.separator)) {
			path = path + File.separator;
		}
		File temp = new File(path);
		if (!temp.isDirectory()) {
			temp.mkdir();
		}
		// 图片存储的全路径
		String fileFullPath = path + lastFileName;
		FileCopyUtils.copy(file.getBytes(), new File(fileFullPath));
		return fileFullPath;
	}

	/**
	 * @descrption 验证<strong>文件</strong>格式，这里主要验证后缀名
	 * @author dingzy
	 * @param file
	 *            MultipartFile对象
	 * @param maxLength
	 *            <strong>文件</strong>最大限制
	 * @param allowExtName
	 *            不允许上传的<strong>文件</strong>扩展名
	 * @return <strong>文件</strong>格式是否合法
	 */
	private static boolean validateFile(MultipartFile file, long maxLength,
			String[] allowExtName) {
		if (file.getSize() < 0 || file.getSize() > maxLength)
			return false;
		String filename = file.getOriginalFilename();

		// 处理不选择<strong>文件</strong>点击上传时，也会有MultipartFile对象，在此进行过滤
		if (filename == "") {
			return false;
		}
		String extName = filename.substring(filename.lastIndexOf("."))
				.toLowerCase();
		if (allowExtName == null || allowExtName.length == 0
				|| Arrays.binarySearch(allowExtName, extName) != -1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 将MultipartFile文件转成base64
	 * @param multipartFile
	 * @return
	 * @throws Exception
	 */
	public static String encodeBase64MultipartFile(MultipartFile multipartFile) throws Exception {
		File file = new File(multipartFile.getName());
		multipartFile.transferTo(file);
		return encodeBase64File(file);
	}

	/**
	 * 将文件转成base64 字符串
	 * @param file
	 * @return *
	 * @throws Exception
	 */
	public static String encodeBase64File(File file) throws Exception {
		FileInputStream fileinputFile = new FileInputStream(file);
		byte[] buffer = new byte[(int) file.length()];
		fileinputFile.read(buffer);
		fileinputFile.close();
		return new BASE64Encoder().encode(buffer);

	}

	/**
	 * 将绝对时间转化成相对时间 如2014-10-11 17:02:19 -> 19秒前
	 * @param dateStr yyyy-MM-dd HH:m:s格式的时间
	 * @return 成功返回时间，失败返回null
	 */
	public static String format(String dateStr) {
    	try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:m:s");
			Date date = format.parse(dateStr);
			
			long delta = new Date().getTime() - date.getTime();
			if (delta < 1L * ONE_MINUTE) {
			    long seconds = toSeconds(delta);
			    return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
			}
			if (delta < 45L * ONE_MINUTE) {
			    long minutes = toMinutes(delta);
			    return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
			}
			if (delta < 24L * ONE_HOUR) {
			    long hours = toHours(delta);
			    return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
			}
			if (delta < 48L * ONE_HOUR) {
			    return "昨天";
			}
			if (delta < 30L * ONE_DAY) {
			    long days = toDays(delta);
			    return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
			}
			if (delta < 12L * 4L * ONE_WEEK) {
			    long months = toMonths(delta);
			    return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
			} else {
			    long years = toYears(delta);
			    return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }

    /**
     * 获取网卡的mac地址
     * @param inetAddress 通过InetAddress.getLocalHost()获取
     * @return 本机mac地址
     * @throws Exception
     */
    public static String getLocalMac(InetAddress inetAddress) throws Exception {
		//获取网卡，获取地址
		byte[] mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
		System.out.println("mac数组长度："+mac.length);
		StringBuffer sb = new StringBuffer("");
		for(int i=0; i<mac.length; i++) {
			if(i!=0) {
				sb.append("-");
			}
			//字节转换为整数
			int temp = mac[i]&0xff;
			String str = Integer.toHexString(temp);
			if(str.length()==1) {
				sb.append("0"+str);
			}else {
				sb.append(str);
			}
		}
		return sb.toString().toUpperCase();
	}
    
    /**
     * 获取本机mac地址
     * @return
     * @throws Exception
     */
    public static String getLocalMac() throws Exception {
    	return getLocalMac(InetAddress.getLocalHost());
    }
    
    /**
     * 获取本机mac地址
     * @return
     * @throws Exception
     */
    public static String getHoseAddress() throws Exception {
    	return InetAddress.getLocalHost().getHostAddress();
    }
    
    /**
	 * 以map形式返回配置文件的参数
	 * @param name
	 * @return
	 */
	public static Map<String, String> getProperties(String name){
		ResourceBundle rb = ResourceBundle.getBundle(name);
		Enumeration<String> em = rb.getKeys();
		// 存放属性文件的键值对
		Map<String, String> map = new HashMap<String, String>();
		// 根据key取值
		while (em.hasMoreElements()) {
			String key = em.nextElement();
			String value = rb.getString(key);
			map.put(key, value);
		}
		return map;
	}
}

