/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import net.wit.Setting;
import net.wit.entity.ProductNotify;
import net.wit.entity.SafeKey;
import net.wit.service.MailService;
import net.wit.service.TemplateService;
import net.wit.util.SettingUtils;
import net.wit.util.SpringUtils;

import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Service - 邮件
 * @author rsico Team
 * @version 3.0
 */
@Service("mailServiceImpl")
public class MailServiceImpl implements MailService {

	@Resource(name = "freeMarkerConfigurer")
	private FreeMarkerConfigurer freeMarkerConfigurer;

	@Resource(name = "javaMailSender")
	private JavaMailSenderImpl javaMailSender;

	@Resource(name = "taskExecutor")
	private TaskExecutor taskExecutor;

	@Resource(name = "templateServiceImpl")
	private TemplateService templateService;

	/**
	 * 添加邮件发送任务
	 * @param mimeMessage MimeMessage
	 */
	private void addSendTask(final MimeMessage mimeMessage) {
		try {
			taskExecutor.execute(new Runnable() {
				public void run() {
					javaMailSender.send(mimeMessage);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(String smtpFromMail, String smtpHost, Integer smtpPort, String smtpUsername, String smtpPassword, String toMail, String subject, String templatePath, Map<String, Object> model, boolean async) {
		Assert.hasText(smtpFromMail);
		Assert.hasText(smtpHost);
		Assert.notNull(smtpPort);
		Assert.hasText(smtpUsername);
		Assert.hasText(smtpPassword);
		Assert.hasText(toMail);
		Assert.hasText(subject);
		Assert.hasText(templatePath);
		try {
			Setting setting = SettingUtils.get();
			Configuration configuration = freeMarkerConfigurer.getConfiguration();
			Template template = configuration.getTemplate(templatePath);
			String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
			javaMailSender.setHost(smtpHost);
			javaMailSender.setPort(smtpPort);
			javaMailSender.setUsername(smtpUsername);
			javaMailSender.setPassword(smtpPassword);
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "utf-8");
			mimeMessageHelper.setFrom(MimeUtility.encodeWord(setting.getSiteName()) + " <" + smtpFromMail + ">");
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setTo(toMail);
			mimeMessageHelper.setText(text, true);
			if (async) {
				addSendTask(mimeMessage);
			} else {
				javaMailSender.send(mimeMessage);
			}
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public void send(String toMail, String subject, String templatePath, Map<String, Object> model, boolean async) {
		Setting setting = SettingUtils.get();
		send(setting.getSmtpFromMail(), setting.getSmtpHost(), setting.getSmtpPort(), setting.getSmtpUsername(), setting.getSmtpPassword(), toMail, subject, templatePath, model, async);
	}

	public void send(String toMail, String subject, String templatePath, Map<String, Object> model) {
		Setting setting = SettingUtils.get();
		send(setting.getSmtpFromMail(), setting.getSmtpHost(), setting.getSmtpPort(), setting.getSmtpUsername(), setting.getSmtpPassword(), toMail, subject, templatePath, model, true);
	}

	public void send(String toMail, String subject, String templatePath) {
		Setting setting = SettingUtils.get();
		send(setting.getSmtpFromMail(), setting.getSmtpHost(), setting.getSmtpPort(), setting.getSmtpUsername(), setting.getSmtpPassword(), toMail, subject, templatePath, null, true);
	}

	public void sendTestMail(String smtpFromMail, String smtpHost, Integer smtpPort, String smtpUsername, String smtpPassword, String toMail) {
		Setting setting = SettingUtils.get();
		String subject = SpringUtils.getMessage("admin.setting.testMailSubject", setting.getSiteName());
		net.wit.Template testMailTemplate = templateService.get("testMail");
		send(smtpFromMail, smtpHost, smtpPort, smtpUsername, smtpPassword, toMail, subject, testMailTemplate.getTemplatePath(), null, false);
	}

	public void sendFindPasswordMail(String toMail, String username, SafeKey safeKey) {
		Setting setting = SettingUtils.get();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("username", username);
		model.put("safeKey", safeKey);
		String subject = SpringUtils.getMessage("shop.password.mailSubject", setting.getSiteName());
		net.wit.Template findPasswordMailTemplate = templateService.get("findPasswordMail");
		send(toMail, subject, findPasswordMailTemplate.getTemplatePath(), model);
	}

	public void sendFindPaymentPasswordMail(String toMail, String username, SafeKey safeKey) {
		Setting setting = SettingUtils.get();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("username", username);
		model.put("safeKey", safeKey);
		String subject = SpringUtils.getMessage("shop.password.mailSubject", setting.getSiteName());
		net.wit.Template findPasswordMailTemplate = templateService.get("findPaymentPasswordMail");
		send(toMail, subject, findPasswordMailTemplate.getTemplatePath(), model);
	}

	public void sendBindMail(String toMail, String username, SafeKey safeKey) {
		Setting setting = SettingUtils.get();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("username", username);
		model.put("safeKey", safeKey);
		String subject = setting.getSiteName() + "--账户绑定缴活";
		net.wit.Template bindMailTemplate = templateService.get("bindMail");
		send(toMail, subject, bindMailTemplate.getTemplatePath(), model);
	}

	public void sendRegisterMail(String toMail, String username, SafeKey safeKey) {
		Setting setting = SettingUtils.get();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("username", username);
		model.put("safeKey", safeKey);
		String subject = setting.getSiteName() + "--邮箱验证码";
		net.wit.Template bindMailTemplate = templateService.get("registerMail");
		send(toMail, subject, bindMailTemplate.getTemplatePath(), model);
	}

	public void sendGetBackPasswordMail(String toMail, String username, SafeKey safeKey) {
		Setting setting = SettingUtils.get();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("username", username);
		model.put("safeKey", safeKey);
		String subject = setting.getSiteName() + "--找回密码验证码";
		net.wit.Template bindMailTemplate = templateService.get("getBackPasswordMail");
		send(toMail, subject, bindMailTemplate.getTemplatePath(), model);
	}

	public void sendRegisterSucessMail(String toMail, String username, SafeKey safeKey) {
		Setting setting = SettingUtils.get();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("username", username);
		model.put("safeKey", safeKey);
		String subject = setting.getSiteName() + "--注册成功";
		net.wit.Template bindMailTemplate = templateService.get("registerSuccessMail");
		send(toMail, subject, bindMailTemplate.getTemplatePath(), model);
	}

	public void sendMailInvaild(String toMail, String username, SafeKey safeKey) {
		Setting setting = SettingUtils.get();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("username", username);
		model.put("safeKey", safeKey);
		String subject = setting.getSiteName() + "--修改绑定邮箱";
		net.wit.Template bindMailTemplate = templateService.get("MailInvaild");
		send(toMail, subject, bindMailTemplate.getTemplatePath(), model);
	}

	public void sendProductNotifyMail(ProductNotify productNotify) {
		Setting setting = SettingUtils.get();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("productNotify", productNotify);
		String subject = SpringUtils.getMessage("admin.productNotify.mailSubject", setting.getSiteName());
		net.wit.Template productNotifyMailTemplate = templateService.get("productNotifyMail");
		send(productNotify.getEmail(), subject, productNotifyMailTemplate.getTemplatePath(), model);
	}

	public void sendCheckCodeByMail(String toMail, String username, SafeKey safeKey) {
		Setting setting = SettingUtils.get();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("username", username);
		model.put("safeKey", safeKey);
		String subject = setting.getSiteName() + "--验证码";
		net.wit.Template bindMailTemplate = templateService.get("findPasswordCheckCodeMail");
		send(toMail, subject, bindMailTemplate.getTemplatePath(), model);
	}

	public void sendCheckCodeByMailCommon(String toMail, String username, SafeKey safeKey) {
		Setting setting = SettingUtils.get();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("username", username);
		model.put("safeKey", safeKey);
		String subject = setting.getSiteName() + "--验证码";
		net.wit.Template bindMailTemplate = templateService.get("findPasswordCheckCodeMailCommon");
		send(toMail, subject, bindMailTemplate.getTemplatePath(), model);
	}

	public void b2cSendFindPaymentPasswordMail(String email, String username, SafeKey safeKey) {
		Setting setting = SettingUtils.get();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("username", username);
		model.put("safeKey", safeKey);
		String subject = SpringUtils.getMessage("shop.password.mailSubject", setting.getSiteName());
		net.wit.Template findPasswordMailTemplate = templateService.get("b2cFindPaymentPasswordMail");
		send(email, subject, findPasswordMailTemplate.getTemplatePath(), model);

	}

	public void sendB2cBindMail(String email, String username, SafeKey safeKey) {
		Setting setting = SettingUtils.get();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("username", username);
		model.put("safeKey", safeKey);
		String subject = setting.getSiteName() + "--账户绑定缴活";
		net.wit.Template bindMailTemplate = templateService.get("b2cBindMail");
		send(email, subject, bindMailTemplate.getTemplatePath(), model);
	}

	public void sendCheckCodeByMailBind(String toMail, String username, SafeKey safeKey) {
		Setting setting = SettingUtils.get();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("username", username);
		model.put("safeKey", safeKey);
		String subject = setting.getSiteName() + "--邮箱绑定";
		net.wit.Template bindMailTemplate = templateService.get("apiBindMail");
		send(toMail, subject, bindMailTemplate.getTemplatePath(), model);
	}

}