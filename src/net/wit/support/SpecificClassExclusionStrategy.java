/**
 *====================================================
 * 文件名称: SpecificClassExclusionStrategy.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014-4-12			chenxy(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 */
package net.wit.support;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * @ClassName: SpecificClassExclusionStrategy
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author chenxy
 * @date 2014-4-12 下午6:03:03
 */
public class SpecificClassExclusionStrategy implements ExclusionStrategy {

	protected static final Logger logger = LoggerFactory.getLogger(SpecificClassExclusionStrategy.class);

	private static final Set<Class<?>> BASE_FIELD_TYPE = new HashSet<Class<?>>();

	static {
		BASE_FIELD_TYPE.add(Long.class);
		BASE_FIELD_TYPE.add(Integer.class);
		BASE_FIELD_TYPE.add(Short.class);
		BASE_FIELD_TYPE.add(Boolean.class);
		BASE_FIELD_TYPE.add(Float.class);
		BASE_FIELD_TYPE.add(BigDecimal.class);
		BASE_FIELD_TYPE.add(String.class);
		BASE_FIELD_TYPE.add(Date.class);
	}

	private final Set<Class<?>> excludedThisClasses = new HashSet<Class<?>>();

	private final Set<Class<?>> excludedDeclaredFieldClass = new HashSet<Class<?>>();

	private final Set<String> excludedFieldNames = new HashSet<String>();

	private final Map<String, Set<Class<?>>> excludedDeclaringFieldClasses = new HashMap<String, Set<Class<?>>>();

	public SpecificClassExclusionStrategy(final Class<?>[] excludedThisClasses, final String[] excludedFieldNames, final Class<?>[] excludedDeclaredFieldClass,
			final Map<String, Set<Class<?>>> excludedDeclaringFieldClasses) {
		if (excludedThisClasses != null) {
			this.excludedThisClasses.addAll(Arrays.asList(excludedThisClasses));
		}
		if (excludedDeclaredFieldClass != null) {
			this.excludedDeclaredFieldClass.addAll(Arrays.asList(excludedDeclaredFieldClass));
		}
		if (excludedFieldNames != null) {
			this.excludedFieldNames.addAll(Arrays.asList(excludedFieldNames));
		}
		if (excludedDeclaringFieldClasses != null) {
			this.excludedDeclaringFieldClasses.putAll(excludedDeclaringFieldClasses);
		}
	}

	public boolean shouldSkipClass(Class<?> clazz) {
		return excludedThisClasses.contains(clazz);
	}

	public boolean shouldSkipField(FieldAttributes f) {
		if (BASE_FIELD_TYPE.contains(f.getDeclaredClass())) {
			return false;
		}
		if (excludedDeclaredFieldClass.contains(f.getDeclaredClass())) {
			return true;
		}
		if (excludedFieldNames.contains(f.getName())) {
			return true;
		}
		Set<Class<?>> declaringClasses = excludedDeclaringFieldClasses.get(f.getName());
		if (declaringClasses == null) {
			return false;
		}
		if (declaringClasses.contains(f.getDeclaringClass())) {
			return true;
		}
		return false;
	}
}
