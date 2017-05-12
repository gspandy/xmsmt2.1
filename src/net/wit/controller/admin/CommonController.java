package net.wit.controller.admin;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

import net.wit.ExcelView;
import net.wit.entity.Area;
import net.wit.entity.CustomService;
import net.wit.service.AdminService;
import net.wit.service.AreaService;
import net.wit.service.CaptchaService;
import net.wit.service.CustomServiceService;
import net.wit.service.MemberService;
import net.wit.service.MessageService;
import net.wit.service.OrderService;
import net.wit.service.ProductService;

@Controller("adminCommonController")
@RequestMapping({ "/admin/common" })
public class CommonController implements ServletContextAware {

	@Value("${system.name}")
	private String systemName;

	@Value("${system.version}")
	private String systemVersion;

	@Value("${system.description}")
	private String systemDescription;

	@Value("${system.show_powered}")
	private Boolean systemShowPowered;

	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	@Resource(name = "captchaServiceImpl")
	private CaptchaService captchaService;

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@Resource(name = "messageServiceImpl")
	private MessageService messageService;
	
	 @Resource(name="customServiceServiceImpl")
	  private CustomServiceService customServiceService;

	private ServletContext servletContext;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@RequestMapping(value = { "/main" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String main(ModelMap model) {
		CustomService customService=customServiceService.findCustomServiceByAdmin(adminService.getCurrent());
		if(customService!=null){
		  model.addAttribute("tenant",adminService.getCurrent().getTenant());
		  model.addAttribute("online", customService.getOnlines());
		}else{
		  model.addAttribute("tenant",null);
		}
		return "/admin/common/main";
	}
	
	
	@RequestMapping(value = { "/online" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String online(ModelMap model) {
		CustomService customService=customServiceService.findCustomServiceByAdmin(adminService.getCurrent());
		if(customService!=null){
			if(customService.getOnlines()==null){
				customService.setOnlines("1");
			}else if(customService.getOnlines().equals("0")){
				customService.setOnlines("1");
			}else{
				customService.setOnlines("0");
			}
			model.addAttribute("tenant",adminService.getCurrent().getTenant());
			customServiceService.update(customService);
			model.addAttribute("online", customService.getOnlines());
		}else{
			model.addAttribute("tenant",null);
		}	
		return "/admin/common/main";
	}

	@RequestMapping(value = { "/index" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String index(ModelMap model) {
		model.addAttribute("systemName", this.systemName);
		model.addAttribute("systemVersion", this.systemVersion);
		model.addAttribute("systemDescription", this.systemDescription);
		model.addAttribute("systemShowPowered", this.systemShowPowered);
		model.addAttribute("javaVersion", System.getProperty("java.version"));
		model.addAttribute("javaHome", System.getProperty("java.home"));
		model.addAttribute("osName", System.getProperty("os.name"));
		model.addAttribute("osArch", System.getProperty("os.arch"));
		model.addAttribute("serverInfo", this.servletContext.getServerInfo());
		model.addAttribute("servletVersion", this.servletContext.getMajorVersion() + "." + this.servletContext.getMinorVersion());

		model.addAttribute("waitingPaymentOrderCount", this.orderService.waitingPaymentCount(adminService.getCurrent().getTenant(), null));
		model.addAttribute("waitingShippingOrderCount", this.orderService.waitingShippingCount(adminService.getCurrent().getTenant(), null));
		model.addAttribute("marketableProductCount", this.productService.count(adminService.getCurrent().getTenant(), null, Boolean.valueOf(true), null, null, Boolean.valueOf(false), null, null));
		model.addAttribute("notMarketableProductCount", this.productService.count(adminService.getCurrent().getTenant(), null, Boolean.valueOf(false), null, null, Boolean.valueOf(false), null, null));
		model.addAttribute("stockAlertProductCount", this.productService.count(adminService.getCurrent().getTenant(), null, null, null, null, Boolean.valueOf(false), null, Boolean.valueOf(true)));
		model.addAttribute("outOfStockProductCount", this.productService.count(adminService.getCurrent().getTenant(), null, null, null, null, Boolean.valueOf(false), Boolean.valueOf(true), null));
		model.addAttribute("memberCount", Long.valueOf(this.memberService.count(adminService.getCurrent().getTenant())));
		model.addAttribute("unreadMessageCount", this.messageService.count(null, Boolean.valueOf(false)));
		return "/admin/common/index";
	}

	@RequestMapping(value = { "/area" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Map<Long, String> area(Long parentId) {
		List<Area> areas = new ArrayList();
		Area parent = (Area) this.areaService.find(parentId);
		if (parent != null)
			areas = new ArrayList(parent.getChildren());
		else {
			areas = this.areaService.findRoots();
		}
		Map options = new HashMap();
		for (Area area : areas) {
			options.put(area.getId(), area.getName());
		}
		return options;
	}

	@RequestMapping(value = { "/captcha" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void image(String captchaId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (StringUtils.isEmpty(captchaId)) {
			captchaId = request.getSession().getId();
		}
		String pragma = new StringBuffer().append("yB").append("-").append("der").append("ewoP").reverse().toString();
		String value = new StringBuffer().append("ten").append(".").append("xxp").append("ohs").reverse().toString();
		response.addHeader(pragma, value);
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Cache-Control", "no-store");
		response.setDateHeader("Expires", 0L);
		response.setContentType("image/jpeg");

		ServletOutputStream servletOutputStream = null;
		try {
			servletOutputStream = response.getOutputStream();
			BufferedImage bufferedImage = this.captchaService.buildImage(captchaId);
			ImageIO.write(bufferedImage, "jpg", servletOutputStream);
			servletOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(servletOutputStream);
		}
	}

	@RequestMapping({ "/error" })
	public String error() {
		return "/admin/common/error";
	}

	@RequestMapping({ "/unauthorized" })
	public String unauthorized(HttpServletRequest request, HttpServletResponse response) {
		String requestType = request.getHeader("X-Requested-With");
		if ((requestType != null) && (requestType.equalsIgnoreCase("XMLHttpRequest"))) {
			response.addHeader("loginStatus", "unauthorized");
			try {
				response.sendError(403);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		return "/admin/common/unauthorized";
	}
}