/**
 * 版权所有：福建顶点软件股份有限公司
 * Copyright 2012 Fujian Nengneng Information Technology Co., Ltd.
 * All right reserved. 
 *====================================================
 * 文件名称: HibernateProxySerializer.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2012-11-21			chenxy(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.util;

import java.lang.reflect.Type;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @ClassName: HibernateProxySerializer
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author chenxy
 * @date 2012-11-21 下午8:38:47
 */
public class HibernateProxySerializer implements JsonSerializer<HibernateProxy> {

	public JsonElement serialize(HibernateProxy proxy, Type type, JsonSerializationContext context) {
		return context.serialize(proxy.getHibernateLazyInitializer().getImplementation(), Hibernate.getClass(proxy));
	}

}
