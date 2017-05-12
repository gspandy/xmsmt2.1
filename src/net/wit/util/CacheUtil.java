package net.wit.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.wit.mobile.service.IParamService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: ab
 * Date: 15-9-5
 * Time: 下午6:27
 * To change this template use File | Settings | File Templates.
 */
public class CacheUtil {

    private static Log log = LogFactory.getLog(CacheUtil.class);

    private static final String PATH = "/ehcache.xml";

    private static URL url;

    private static CacheManager manager;

    public static CacheManager getCacheManager() {
        if (manager == null) {
            url = CacheUtil.class.getResource(PATH);
            manager = CacheManager.create(url);
        }
        return manager;
    }

    public static Cache getParamCache() {
        return getCacheManager().getCache("paramCache");
    }

    public static String getParamValueByName(String name) {
        String comment = "";
        Element element = getParamCache().get(name);
        if (element == null) {
            IParamService iParamService = (IParamService) SpringUtils.getBean("paramService");
            if (iParamService != null) {
                comment = iParamService.getParamValueByName(name);
                if (StringUtils.isNotEmpty(comment)) {
                    getParamCache().put(new Element(name, comment));
                }
            }
        }  else {
            comment = (String) element.getObjectValue();
        }

        return comment;
    }

    public static void refreshParamByValue(String value) {
        getParamCache().remove(value);
    }
}
