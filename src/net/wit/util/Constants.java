package net.wit.util;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;

/**
 * 常量
 * @author Administrator
 */
public final class Constants {

	public static final String PASSWORD_INIT = "123456"; // 初始密码

	public static final String TOP_ZONE = "000000000000000000"; // 顶级区域

	public static final String PAGESIZE = "10"; // 分页每页大小

	public static final String PAGESIZE_PHONE = "10"; // 手机分页每页大小

	public static final String PARENT_ID = "-1";// 顶级父类id

	// ************流水号工具start****************
	public static final String SNO_INIT = "900"; // 初始化流水号
	// ************流水号工具end****************

	// ************日期格式化start***************
	public static final SimpleDateFormat DEFAULTDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static final SimpleDateFormat YMDHMFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public static final SimpleDateFormat YMDFORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public static final SimpleDateFormat YMFORMAT = new SimpleDateFormat("yyyy-MM");

	public static final SimpleDateFormat YFORMAT = new SimpleDateFormat("yyyy");

	public static final DecimalFormat INTFORMT = new DecimalFormat("0");

	public static final DecimalFormat ONEDEFORMT = new DecimalFormat("0.0");

	public static final DecimalFormat THREEDEFORMT = new DecimalFormat("0.000");

	public static final String emptyDate = "1970-01-01 00:00:00";

	/**
	 * 每个月天数 30天
	 */
	public static final int DAYSOFMONTH = 30;

	public static final int DAY_TY = 3;

	// ************日期格式化end***************

	public static final String THUMB = "thumb"; // 缩略图名称

	public static final int THUMB_WIDTH = 120; // 缩略图宽度

	public static final int THUMB_HEIGHT = 120; // 缩略图高度

	public static final String SUFFIX = ".jpg"; // 缩略图高度

	/**
	 * 是否标识-是
	 */
	public static final String YES_FLAG = "1"; //

	/**
	 * 是否标识-否
	 */
	public static final String NO_FLAG = "0"; //

	/**
	 * 原图地址转化成缩略图地址
	 * @param url 原图地址 如 WEB-INF\resource\2013\9\137816948156838910.png
	 * @return 缩略图地址 如 WEB-INF\resource\2013\9\137816948156838910_thumb.png
	 */
	public static String convertToThumbUrl(String url) {
		if (StringUtils.isNotEmpty(url)) {
			String thumbTarget = url.substring(url.indexOf("."));// 取后缀
			url = url.substring(0, url.indexOf("."));// 取.之前的
			url += "_" + Constants.THUMB + thumbTarget;// 加入thumb拼接
			return url;
		}
		return "";
	}

	public static final double DISTANCE = 500000; // 默认距离

	// *************资源位置start*******************
	public static String getSeparator() {
		return SEPARATOR;
	}

	public static final String SEPARATOR = System.getProperty("file.separator");

	public static final String OSNAME = System.getProperty("os.name");

	private static final String SECURITY_RES = "WEB-INF" + SEPARATOR + "resource";

	private static final String SECURITY_UPLOAD = "upload" + SEPARATOR;

	private static final String SECURITY_TEMP = SECURITY_RES + SEPARATOR + "temp" + SEPARATOR;

	private static final String SECURITY_ATTACH = SECURITY_RES + SEPARATOR + "attach" + SEPARATOR;

	private static final String SECURITY_AUTHORIZE = SECURITY_RES + SEPARATOR + "authorize" + SEPARATOR;

	public static final String IMAGE_DEFAULT = SECURITY_RES + SEPARATOR + "default.jpg"; // 默认图片路径

	public static String getSecurityRes() {
		return SECURITY_RES;
	}

	public static String getSecurityUpload() {
		return SECURITY_UPLOAD;
	}

	public static String getSecurityTemp() {
		return SECURITY_TEMP;
	}

	public static String getSecurityAttach() {
		return SECURITY_ATTACH;
	}

	public static String getSecurityAuthorize() {
		return SECURITY_AUTHORIZE;
	}

	// *************资源位置end*******************

	// *********获取属性文件Start**********
	private static String CACHEPREFIX;

	public static String getCachePrifix() {
		if (CACHEPREFIX != null) {
			return CACHEPREFIX;
		} else {
			String cachePrefix = "SUPERVISE_MEM";
			try {
				Properties p = getProperties("config/jdbc.properties");
				cachePrefix = p.getProperty("mem_cache_prefix");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return cachePrefix;
		}
	}

	private static Properties getProperties(String url) throws Exception {
		InputStream in = new ClassPathResource(url).getInputStream();
		Properties p = new Properties();
		p.load(in);
		return p;
	}

	// *********获取属性文件end**********

	// *********日期方法start************
	private static Date MINDATE;

	private static Date MAXDATE;

	public static Date getMinDate() {
		if (MINDATE != null) {
			return MINDATE;
		} else {
			Date minDate = null;
			try {
				minDate = DEFAULTDATEFORMAT.parse("1970-01-01 00:00:00");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return minDate;
		}
	}

	public static Date getMaxDate() {
		if (MAXDATE != null) {
			return MAXDATE;
		} else {
			Date maxDate = null;
			try {
				maxDate = DEFAULTDATEFORMAT.parse("9999-12-31 00:00:00");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return maxDate;
		}
	}

	public static Date getFirstDayOfMth(Calendar calendar) {
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date fistDate = calendar.getTime();
		String dateTemp = Constants.YMDFORMAT.format(fistDate);
		try {
			fistDate = DEFAULTDATEFORMAT.parse(dateTemp + " 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return fistDate;
	}

	public static Date getLastDayOfMth(Calendar calendar) {
		int value = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, value);
		Date lastDate = calendar.getTime();
		String dateTemp = Constants.YMDFORMAT.format(lastDate);
		try {
			lastDate = DEFAULTDATEFORMAT.parse(dateTemp + " 23:59:59");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return lastDate;
	}

}
