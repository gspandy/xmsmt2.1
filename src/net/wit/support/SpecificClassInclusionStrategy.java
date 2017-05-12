/**
 *====================================================
 * 文件名称: SpecificClassInclusionStrategy.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年8月20日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.support;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * @ClassName: SpecificClassInclusionStrategy
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年8月20日 上午11:03:08
 */
public class SpecificClassInclusionStrategy implements ExclusionStrategy {

	private final Set<String> includedThisFieldNames = new HashSet<String>();

	public SpecificClassInclusionStrategy(Set<String> includedThisFieldNames) {
		this.includedThisFieldNames.addAll(includedThisFieldNames);
	}

	public boolean shouldSkipClass(Class<?> arg0) {
		return false;
	}

	public boolean shouldSkipField(FieldAttributes f) {
		if (includedThisFieldNames.contains(f.getName())) {
			return false;
		}
		return true;
	}

}
