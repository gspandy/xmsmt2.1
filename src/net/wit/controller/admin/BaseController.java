package net.wit.controller.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validator;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

import net.wit.DateEditor;
import net.wit.Message;
import net.wit.Setting;
import net.wit.entity.Log;
import net.wit.template.directive.FlashMessageDirective;
import net.wit.util.SettingUtils;
import net.wit.util.SpringUtils;

public class BaseController {
	protected static final String ERROR_VIEW = "/admin/common/error";

	protected static final Message ERROR_MESSAGE = Message.error("admin.message.error", new Object[0]);

	protected static final Message NOT_SELECTED = Message.error("未选中记录请重新操作！", new Object[0]);

	protected static final Message NOT_BANK = Message.error("银行卡绑定失败！", new Object[0]);

	protected static final Message DELETE_ERROR_MESSAGE = Message.error("选择的部分商品已有订单无法删除！请重新选择！", new Object[0]);
	
	protected static final Message NOT_CASH_PWD = Message.error("支付密码输入错误请重新操作！", new Object[0]);

	protected static final Message SUCCESS_MESSAGE = Message.success("admin.message.success", new Object[0]);

	private static final String CONSTRAINT_VIOLATIONS_ATTRIBUTE_NAME = "constraintViolations";

	@Resource(name = "validator")
	private Validator validator;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.registerCustomEditor(Date.class, new DateEditor(true));
	}

	protected boolean isValid(Object target, Class<?>[] groups) {
		Set constraintViolations = this.validator.validate(target, groups);
		if (constraintViolations.isEmpty()) {
			return true;
		}
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		requestAttributes.setAttribute("constraintViolations", constraintViolations, 0);
		return false;
	}

	protected boolean isValid(Class<?> type, String property, Object value, Class<?>[] groups) {
		Set constraintViolations = this.validator.validateValue(type, property, value, groups);
		if (constraintViolations.isEmpty()) {
			return true;
		}
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		requestAttributes.setAttribute("constraintViolations", constraintViolations, 0);
		return false;
	}

	protected String currency(BigDecimal amount, boolean showSign, boolean showUnit) {
		Setting setting = SettingUtils.get();
		String price = setting.setScale(amount).toString();
		if (showSign) {
			price = setting.getCurrencySign() + price;
		}
		if (showUnit) {
			price = price + setting.getCurrencyUnit();
		}
		return price;
	}

	protected String message(String code, Object[] args) {
		return SpringUtils.getMessage(code, args);
	}

	protected void addFlashMessage(RedirectAttributes redirectAttributes, Message message) {
		if ((redirectAttributes != null) && (message != null))
			redirectAttributes.addFlashAttribute(FlashMessageDirective.FLASH_MESSAGE_ATTRIBUTE_NAME, message);
	}

	protected void addLog(String content) {
		if (content != null) {
			RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
			requestAttributes.setAttribute(Log.LOG_CONTENT_ATTRIBUTE_NAME, content, 0);
		}
	}

	protected void responseJson(HttpServletResponse response, Object object, Class<?> clazz, String... properties) {
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			SimplePropertyPreFilter filter = null;
			if (clazz != null && properties != null) {
				filter = new SimplePropertyPreFilter(clazz, properties);
			}
			out.write(JSON.toJSONString(object, filter));
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
}