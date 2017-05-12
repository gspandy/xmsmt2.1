/**
 * 版权所有：福建顶点软件股份有限公司
 * Copyright 2011 Fujian Apex Software Shares Co., Ltd.
 * All right reserved. 
 *====================================================
 * 文件名称: GsonUtil.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2011-8-18			chenxy(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.util;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * 包含操作 {@code JSON} 数据的常用方法的工具类。
 * <p>
 * 该工具类使用的 {@code JSON} 转换引擎是 <a href="http://code.google.com/p/google-gson/" mce_href="http://code.google.com/p/google-gson/" target="_blank">
 * {@code Google Gson}</a>。下面是工具类的使用案例：
 * </p>
 * 
 * <pre>
 * public class User {  
 *     {@literal @SerializedName("pwd")}  
 *     private String password;  
 *     {@literal @Expose}  
 *     {@literal @SerializedName("uname")}  
 *     private String username;  
 *     {@literal @Expose}  
 *     {@literal @Since(1.1)}  
 *     private String gender;  
 *     {@literal @Expose}  
 *     {@literal @Since(1.0)}  
 *     private String sex;  
 *       
 *     public User() {}  
 *     public User(String username, String password, String gender) {  
 *         // user constructor code... ... ...  
 *     }  
 *       
 *     public String getUsername()  
 *     ... ... ...  
 * }  
 * List<User> userList = new LinkedList<User>();  
 * User jack = new User("Jack", "123456", "Male");  
 * User marry = new User("Marry", "888888", "Female");  
 * userList.add(jack);  
 * userList.add(marry);  
 * Type targetType = new TypeToken<List<User>>(){}.getType();  
 * String sUserList1 = JSONUtils.toJson(userList, targetType);  
 * sUserList1 ----> [{"uname":"jack","gender":"Male","sex":"Male"},{"uname":"marry","gender":"Female","sex":"Female"}]  
 * String sUserList2 = JSONUtils.toJson(userList, targetType, false);  
 * sUserList2 ----> [{"uname":"jack","pwd":"123456","gender":"Male","sex":"Male"},{"uname":"marry","pwd":"888888","gender":"Female","sex":"Female"}]  
 * String sUserList3 = JSONUtils.toJson(userList, targetType, 1.0d, true);  
 * sUserList3 ----> [{"uname":"jack","sex":"Male"},{"uname":"marry","sex":"Female"}]
 * </pre>
 * @author:chenxy
 * @date 2011-8-24 上午10:16:31
 * @since JDK Version 1.5
 */
public class GsonUtil {
	
	private static final HibernateProxySerializer hibernateProxySerializer = new HibernateProxySerializer();

	/** 空的 {@code JSON} 数据 - <code>"{}"</code>。 */
	public static final String EMPTY_JSON = "{}";

	/** 空的 {@code JSON} 数组(集合)数据 - {@code "[]"}。 */
	public static final String EMPTY_JSON_ARRAY = "[]";

	/** 默认的 {@code JSON} 日期/时间字段的格式化模式。 */
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

	/** {@code Google Gson} 的 {@literal @Since} 注解常用的版本号常量 - {@code 1.0}。 */
	public static final Double SINCE_VERSION_10 = 1.0d;

	/** {@code Google Gson} 的 {@literal @Since} 注解常用的版本号常量 - {@code 1.1}。 */
	public static final Double SINCE_VERSION_11 = 1.1d;

	/** {@code Google Gson} 的 {@literal @Since} 注解常用的版本号常量 - {@code 1.2}。 */
	public static final Double SINCE_VERSION_12 = 1.2d;

	/** {@code Google Gson} 的 {@literal @Since} 注解常用的版本号常量 - {@code 2.0}。 */
	public static final Double SINCE_VERSION_20 = 2.0d;

	/** {@code Google Gson} 的 {@literal @Since} 注解常用的版本号常量 - {@code 2.1}。 */
	public static final Double SINCE_VERSION_21 = 2.1d;

	/** {@code Google Gson} 的 {@literal @Since} 注解常用的版本号常量 - {@code 2.2}。 */
	public static final Double SINCE_VERSION_22 = 2.2d;

	/** {@code Google Gson} 的 {@literal @Since} 注解常用的版本号常量 - {@code 3.0}。 */
	public static final Double SINCE_VERSION_30 = 3.0d;

	/** {@code Google Gson} 的 {@literal @Since} 注解常用的版本号常量 - {@code 3.1}。 */
	public static final Double SINCE_VERSION_31 = 3.1d;

	/** {@code Google Gson} 的 {@literal @Since} 注解常用的版本号常量 - {@code 3.2}。 */
	public static final Double SINCE_VERSION_32 = 3.2d;

	/**
	 * 将给定的目标对象根据指定的条件参数转换成 {@code JSON} 格式的字符串。
	 * <p />
	 * <strong>该方法转换发生错误时，不会抛出任何异常。若发生错误时，曾通对象返回 <code>"{}"</code>； 集合或数组对象返回 <code>"[]"</code></strong>
	 * @param target 目标对象
	 * @param targetType 目标对象的类型
	 * @param isSerializeNulls 是否序列化 {@code null} 值字段
	 * @param version 字段的版本号注解
	 * @param datePattern 日期字段的格式化模式
	 * @param excludesFieldsWithoutExpose 是否排除未标注 {@literal @Expose} 注解的字段
	 * @param exclusionStrategy 序列化策略
	 * @return 目标对象的 {@code JSON} 格式的字符串。
	 */
	public static String toJson(Object target, Type targetType, boolean isSerializeNulls, Double version, String datePattern, boolean excludesFieldsWithoutExpose,
			ExclusionStrategy... exclusionStrategy) {
		if (target == null) {
			return EMPTY_JSON;
		}
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeHierarchyAdapter(org.hibernate.proxy.HibernateProxy.class, hibernateProxySerializer);
		builder.excludeFieldsWithModifiers(Modifier.STATIC);
		if (isSerializeNulls) {
			builder.serializeNulls();
		}
		if (version != null) {
			builder.setVersion(version.doubleValue());
		}
		if (StringUtils.isBlank(datePattern)) {
			datePattern = DEFAULT_DATE_PATTERN;
		}
		builder.setDateFormat(datePattern);
		if (excludesFieldsWithoutExpose) {
			builder.excludeFieldsWithoutExposeAnnotation();
		}
		if (exclusionStrategy != null) {
			builder.setExclusionStrategies(exclusionStrategy);
		}
		String result = null;
		Gson gson = builder.create();
		try {
			if (targetType != null) {
				result = gson.toJson(target, targetType);
			} else {
				result = gson.toJson(target);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (target instanceof Collection || target instanceof Iterator || target instanceof Enumeration || target.getClass().isArray()) {
				result = EMPTY_JSON_ARRAY;
			} else {
				result = EMPTY_JSON;
			}
		}
		return result;
	}

	/**
	 * 将给定的目标对象转换成 {@code JSON} 格式的字符串。<strong>此方法只用来转换普通的 {@code JavaBean} 对象。</strong>
	 * <ul>
	 * <li>该方法只会转换标有 {@literal @Expose} 注解的字段；</li>
	 * <li>该方法不会转换 {@code null} 值字段；</li>
	 * <li>该方法会转换所有未标注或已标注 {@literal @Since} 的字段；</li>
	 * <li>该方法转换时使用默认的 日期/时间 格式化模式 - {@code yyyy-MM-dd HH:mm:ss SSS}；</li>
	 * </ul>
	 * @param target 要转换成 {@code JSON} 的目标对象。
	 * @return 目标对象的 {@code JSON} 格式的字符串。
	 */
	public static String toJson(Object target) {
		return toJson(target, null, false, null, null, true, (ExclusionStrategy[]) null);
	}

	/**
	 * 将给定的目标对象转换成 {@code JSON} 格式的字符串。<strong>此方法只用来转换普通的 {@code JavaBean} 对象。</strong>
	 * <ul>
	 * <li>该方法只会转换标有 {@literal @Expose} 注解的字段；</li>
	 * <li>该方法不会转换 {@code null} 值字段；</li>
	 * <li>该方法会转换所有未标注或已标注 {@literal @Since} 的字段；</li>
	 * <li>该方法转换时使用默认的 日期/时间 格式化模式 - {@code yyyy-MM-dd HH:mm:ss SSS}；</li>
	 * <li>该方法转换时使用序列化策略{@literal exclusionStrategy}</li>
	 * </ul>
	 * @param target 要转换成 {@code JSON} 的目标对象
	 * @param exclusionStrategy 序列化策略
	 * @return 目标对象的 {@code JSON} 格式的字符串。
	 */
	public static String toJson(Object target, ExclusionStrategy... exclusionStrategy) {
		return toJson(target, null, false, null, null, true, exclusionStrategy);
	}

	/**
	 * 将给定的目标对象转换成 {@code JSON} 格式的字符串。<strong>此方法只用来转换普通的 {@code JavaBean} 对象。</strong>
	 * <ul>
	 * <li>该方法只会转换标有 {@literal @Expose} 注解的字段；</li>
	 * <li>该方法不会转换 {@code null} 值字段；</li>
	 * <li>该方法会转换所有未标注或已标注 {@literal @Since} 的字段；</li>
	 * </ul>
	 * @param target 要转换成 {@code JSON} 的目标对象。
	 * @param datePattern 日期字段的格式化模式。
	 * @return 目标对象的 {@code JSON} 格式的字符串。
	 */
	public static String toJson(Object target, String datePattern) {
		return toJson(target, null, false, null, datePattern, true, (ExclusionStrategy[]) null);
	}

	/**
	 * 将给定的目标对象转换成 {@code JSON} 格式的字符串。<strong>此方法只用来转换普通的 {@code JavaBean} 对象。</strong>
	 * <ul>
	 * <li>该方法只会转换标有 {@literal @Expose} 注解的字段；</li>
	 * <li>该方法不会转换 {@code null} 值字段；</li>
	 * <li>该方法会转换所有未标注或已标注 {@literal @Since} 的字段；</li>
	 * </ul>
	 * @param target 要转换成 {@code JSON} 的目标对象。
	 * @param datePattern 日期字段的格式化模式。
	 * @return 目标对象的 {@code JSON} 格式的字符串。
	 */
	public static String toJson(Object target, String datePattern, boolean excludesFieldsWithoutExpose) {
		return toJson(target, null, false, null, datePattern, excludesFieldsWithoutExpose, (ExclusionStrategy[]) null);
	}

	/**
	 * 将给定的目标对象转换成 {@code JSON} 格式的字符串。<strong>此方法只用来转换普通的 {@code JavaBean} 对象。</strong>
	 * <ul>
	 * <li>该方法只会转换标有 {@literal @Expose} 注解的字段；</li>
	 * <li>该方法不会转换 {@code null} 值字段；</li>
	 * <li>该方法转换时使用默认的 日期/时间 格式化模式 - {@code yyyy-MM-dd HH:mm:ss SSS}；</li>
	 * </ul>
	 * @param target 要转换成 {@code JSON} 的目标对象。
	 * @param version 字段的版本号注解({@literal @Since})。
	 * @return 目标对象的 {@code JSON} 格式的字符串。
	 */
	public static String toJson(Object target, Double version) {
		return toJson(target, null, false, version, null, true, (ExclusionStrategy[]) null);
	}

	/**
	 * 将给定的目标对象转换成 {@code JSON} 格式的字符串。<strong>此方法只用来转换普通的 {@code JavaBean} 对象。</strong>
	 * <ul>
	 * <li>该方法只会转换标有 {@literal @Expose} 注解的字段；</li>
	 * <li>该方法不会转换 {@code null} 值字段；</li>
	 * <li>该方法转换时使用默认的 日期/时间 格式化模式 - {@code yyyy-MM-dd HH:mm:ss SSS}；</li>
	 * </ul>
	 * @param target 要转换成 {@code JSON} 的目标对象。
	 * @param version 字段的版本号注解({@literal @Since})。
	 * @return 目标对象的 {@code JSON} 格式的字符串。
	 */
	public static String toJson(Object target, Double version, ExclusionStrategy... exclusionStrategy) {
		return toJson(target, null, false, version, null, true, (ExclusionStrategy[]) exclusionStrategy);
	}

	/**
	 * 将给定的目标对象转换成 {@code JSON} 格式的字符串。<strong>此方法只用来转换普通的 {@code JavaBean} 对象。</strong>
	 * <ul>
	 * <li>该方法不会转换 {@code null} 值字段；</li>
	 * <li>该方法会转换所有未标注或已标注 {@literal @Since} 的字段；</li>
	 * <li>该方法转换时使用默认的 日期/时间 格式化模式 - {@code yyyy-MM-dd HH:mm:ss SSS}；</li>
	 * </ul>
	 * @param target 要转换成 {@code JSON} 的目标对象。
	 * @param excludesFieldsWithoutExpose 是否排除未标注 {@literal @Expose} 注解的字段。
	 * @return 目标对象的 {@code JSON} 格式的字符串。
	 */
	public static String toJson(Object target, boolean excludesFieldsWithoutExpose) {
		return toJson(target, null, false, null, null, excludesFieldsWithoutExpose, (ExclusionStrategy[]) null);
	}

	/** @see GsonUtil#toJson(Object, boolean) */
	public static String toJson(Object target, boolean excludesFieldsWithoutExpose, ExclusionStrategy... exclusionStrategy) {
		return toJson(target, null, false, null, null, excludesFieldsWithoutExpose, (ExclusionStrategy[]) exclusionStrategy);
	}

	/**
	 * 将给定的目标对象转换成 {@code JSON} 格式的字符串。<strong>此方法只用来转换普通的 {@code JavaBean} 对象。</strong>
	 * <ul>
	 * <li>该方法不会转换 {@code null} 值字段；</li>
	 * <li>该方法转换时使用默认的 日期/时间 格式化模式 - {@code yyyy-MM-dd HH:mm:ss SSS}；</li>
	 * </ul>
	 * @param target 要转换成 {@code JSON} 的目标对象。
	 * @param version 字段的版本号注解({@literal @Since})。
	 * @param excludesFieldsWithoutExpose 是否排除未标注 {@literal @Expose} 注解的字段。
	 * @return 目标对象的 {@code JSON} 格式的字符串。
	 */
	public static String toJson(Object target, Double version, String datePattern, boolean excludesFieldsWithoutExpose) {
		return toJson(target, null, false, version, datePattern, excludesFieldsWithoutExpose, (ExclusionStrategy[]) null);
	}

	/**
	 * 将给定的目标对象转换成 {@code JSON} 格式的字符串。<strong>此方法只用来转换普通的 {@code JavaBean} 对象。</strong>
	 * <ul>
	 * <li>该方法不会转换 {@code null} 值字段；</li>
	 * <li>该方法转换时使用默认的 日期/时间 格式化模式 - {@code yyyy-MM-dd HH:mm:ss SSS}；</li>
	 * </ul>
	 * @param target 要转换成 {@code JSON} 的目标对象。
	 * @param version 字段的版本号注解({@literal @Since})。
	 * @param excludesFieldsWithoutExpose 是否排除未标注 {@literal @Expose} 注解的字段。
	 * @return 目标对象的 {@code JSON} 格式的字符串。
	 */
	public static String toJson(Object target, Double version, boolean excludesFieldsWithoutExpose) {
		return toJson(target, null, false, version, null, excludesFieldsWithoutExpose, (ExclusionStrategy[]) null);
	}

	/**
	 * 将给定的目标对象转换成 {@code JSON} 格式的字符串。<strong>此方法只用来转换普通的 {@code JavaBean} 对象。</strong>
	 * <ul>
	 * <li>该方法不会转换 {@code null} 值字段；</li>
	 * <li>该方法转换时使用默认的 日期/时间 格式化模式 - {@code yyyy-MM-dd HH:mm:ss SSS}；</li>
	 * </ul>
	 * @param target 要转换成 {@code JSON} 的目标对象。
	 * @param version 字段的版本号注解({@literal @Since})。
	 * @param excludesFieldsWithoutExpose 是否排除未标注 {@literal @Expose} 注解的字段
	 * @param exclusionStrategy 序列化策略
	 * @return 目标对象的 {@code JSON} 格式的字符串。
	 */
	public static String toJson(Object target, Double version, boolean excludesFieldsWithoutExpose, ExclusionStrategy... exclusionStrategy) {
		return toJson(target, null, false, version, null, excludesFieldsWithoutExpose, exclusionStrategy);
	}

	/**
	 * 将给定的目标对象转换成 {@code JSON} 格式的字符串。<strong>此方法通常用来转换使用泛型的对象。</strong>
	 * <ul>
	 * <li>该方法只会转换标有 {@literal @Expose} 注解的字段；</li>
	 * <li>该方法不会转换 {@code null} 值字段；</li>
	 * <li>该方法会转换所有未标注或已标注 {@literal @Since} 的字段；</li>
	 * <li>该方法转换时使用默认的 日期/时间 格式化模式 - {@code yyyy-MM-dd HH:mm:ss SSSS}；</li>
	 * </ul>
	 * @param target 要转换成 {@code JSON} 的目标对象。
	 * @param targetType 目标对象的类型。
	 * @return 目标对象的 {@code JSON} 格式的字符串。
	 */
	public static String toJson(Object target, Type targetType) {
		return toJson(target, targetType, false, null, null, true, (ExclusionStrategy[]) null);
	}

	/**
	 * 将给定的目标对象转换成 {@code JSON} 格式的字符串。<strong>此方法通常用来转换使用泛型的对象。</strong>
	 * <ul>
	 * <li>该方法只会转换标有 {@literal @Expose} 注解的字段；</li>
	 * <li>该方法不会转换 {@code null} 值字段；</li>
	 * <li>该方法转换时使用默认的 日期/时间 格式化模式 - {@code yyyy-MM-dd HH:mm:ss SSSS}；</li>
	 * </ul>
	 * @param target 要转换成 {@code JSON} 的目标对象。
	 * @param targetType 目标对象的类型。
	 * @param version 字段的版本号注解({@literal @Since})。
	 * @return 目标对象的 {@code JSON} 格式的字符串。
	 */
	public static String toJson(Object target, Type targetType, Double version) {
		return toJson(target, targetType, false, version, null, true, (ExclusionStrategy[]) null);
	}

	/**
	 * 将给定的目标对象转换成 {@code JSON} 格式的字符串。<strong>此方法通常用来转换使用泛型的对象。</strong>
	 * <ul>
	 * <li>该方法不会转换 {@code null} 值字段；</li>
	 * <li>该方法会转换所有未标注或已标注 {@literal @Since} 的字段；</li>
	 * <li>该方法转换时使用默认的 日期/时间 格式化模式 - {@code yyyy-MM-dd HH:mm:ss SSS}；</li>
	 * </ul>
	 * @param target 要转换成 {@code JSON} 的目标对象。
	 * @param targetType 目标对象的类型。
	 * @param excludesFieldsWithoutExpose 是否排除未标注 {@literal @Expose} 注解的字段。
	 * @return 目标对象的 {@code JSON} 格式的字符串。
	 */
	public static String toJson(Object target, Type targetType, boolean excludesFieldsWithoutExpose) {
		return toJson(target, targetType, false, null, null, excludesFieldsWithoutExpose, (ExclusionStrategy[]) null);
	}

	/**
	 * 将给定的目标对象转换成 {@code JSON} 格式的字符串。<strong>此方法通常用来转换使用泛型的对象。</strong>
	 * <ul>
	 * <li>该方法不会转换 {@code null} 值字段；</li>
	 * <li>该方法转换时使用默认的 日期/时间 格式化模式 - {@code yyyy-MM-dd HH:mm:ss SSS}；</li>
	 * </ul>
	 * @param target 要转换成 {@code JSON} 的目标对象。
	 * @param targetType 目标对象的类型。
	 * @param version 字段的版本号注解({@literal @Since})。
	 * @param excludesFieldsWithoutExpose 是否排除未标注 {@literal @Expose} 注解的字段。
	 * @return 目标对象的 {@code JSON} 格式的字符串。
	 */
	public static String toJson(Object target, Type targetType, Double version, boolean excludesFieldsWithoutExpose) {
		return toJson(target, targetType, false, version, null, excludesFieldsWithoutExpose, (ExclusionStrategy[]) null);
	}

	/**
	 * 将给定的 {@code JSON} 字符串转换成指定的类型对象。
	 * @param <T> 要转换的目标类型。
	 * @param json 给定的 {@code JSON} 字符串。
	 * @param token {@code com.google.gson.reflect.TypeToken} 的类型指示类对象。
	 * @param datePattern 日期格式模式。
	 * @return 给定的 {@code JSON} 字符串表示的指定的类型对象。
	 */
	public static <T> T fromJson(String json, TypeToken<T> token, String datePattern) {
		if (StringUtils.isBlank(json)) {
			return null;
		}
		GsonBuilder builder = new GsonBuilder();
		if (StringUtils.isBlank(datePattern)) {
			datePattern = DEFAULT_DATE_PATTERN;
		}
		Gson gson = builder.setDateFormat(datePattern).create();
		try {
			return gson.fromJson(json, token.getType());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将给定的 {@code JSON} 字符串转换成指定的类型对象。
	 * @param <T> 要转换的目标类型。
	 * @param json 给定的 {@code JSON} 字符串。
	 * @param token {@code com.google.gson.reflect.TypeToken} 的类型指示类对象。
	 * @return 给定的 {@code JSON} 字符串表示的指定的类型对象。
	 */
	public static <T> T fromJson(String json, TypeToken<T> token) {
		return fromJson(json, token, null);
	}

	/**
	 * 将给定的 {@code JSON} 字符串转换成指定的类型对象。<strong>此方法通常用来转换普通的 {@code JavaBean} 对象。</strong>
	 * @param <T> 要转换的目标类型。
	 * @param json 给定的 {@code JSON} 字符串。
	 * @param clazz 要转换的目标类。
	 * @param datePattern 日期格式模式。
	 * @return 给定的 {@code JSON} 字符串表示的指定的类型对象。
	 */
	public static <T> T fromJson(String json, Class<T> clazz, String datePattern) {
		if (StringUtils.isBlank(json)) {
			return null;
		}
		GsonBuilder builder = new GsonBuilder();
		if (StringUtils.isBlank(datePattern)) {
			datePattern = DEFAULT_DATE_PATTERN;
		}
		Gson gson = builder.setDateFormat(datePattern).create();
		try {
			return gson.fromJson(json, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将给定的 {@code JSON} 字符串转换成指定的类型对象。<strong>此方法通常用来转换普通的 {@code JavaBean} 对象。</strong>
	 * @param <T> 要转换的目标类型。
	 * @param json 给定的 {@code JSON} 字符串。
	 * @param clazz 要转换的目标类。
	 * @return 给定的 {@code JSON} 字符串表示的指定的类型对象。
	 */
	public static <T> T fromJson(String json, Class<T> clazz) {
		return fromJson(json, clazz, null);
	}
}
