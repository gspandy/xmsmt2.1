package net.wit.interceptor;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.LinkedMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import net.wit.entity.Authority;
import net.wit.enums.AuthorityGroup;
import net.wit.service.AdminService;
import net.wit.service.AuthorityService;

/**
 * @ClassName：AuthInterceptor @Description：
 * @author：Chenlf
 * @date：2015年9月9日 下午2:00:20
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {

	@Resource(name = "authorityServiceImpl")
	private AuthorityService authorityService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if (modelAndView != null) {
			Map<AuthorityGroup, List<Authority>> mapResult = new LinkedMap();
			AuthorityGroup[] authorityGroups = AuthorityGroup.values();
			for (AuthorityGroup group : authorityGroups) {
				List<Authority> authoritys = authorityService.findList(group, adminService.getCurrent().getRoles());
				mapResult.put(group, authoritys);
			}
			modelAndView.addObject("mapResult", mapResult);
		}
		super.postHandle(request, response, handler, modelAndView);
	}

}
