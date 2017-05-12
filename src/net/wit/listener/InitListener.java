/*
 * 
 * 
 * 
 */
package net.wit.listener;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import net.wit.service.CacheService;
import net.wit.service.StaticService;

/**
 * Listener - 初始化
 */
@Component("initListener")
public class InitListener implements ServletContextAware, ApplicationListener<ContextRefreshedEvent> {

	/** logger */
	private static final Logger logger = Logger.getLogger(InitListener.class.getName());

	/** servletContext */
	private ServletContext servletContext;

	@Value("${system.name}")
	private String systemName;

	@Value("${system.version}")
	private String systemVersion;

	@Resource(name = "staticServiceImpl")
	private StaticService staticService;

	@Resource(name = "cacheServiceImpl")
	private CacheService cacheService;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		if (servletContext != null && contextRefreshedEvent.getApplicationContext().getParent() == null) {
			String info = "S|t|a|t|i|i|n|g|  " + systemName + " | " + systemVersion;
			logger.info(info.replace("|", ""));

			cacheService.clear();
			staticService.buildOther();

		}
	}

}