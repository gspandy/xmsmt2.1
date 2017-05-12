package net.wit.service.impl;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import net.wit.Template;
import net.wit.dao.SmsSendDao;
import net.wit.entity.Member;
import net.wit.entity.SmsSend;
import net.wit.entity.SmsSend.Status;
import net.wit.entity.SmsSend.Type;
import net.wit.service.MemberService;
import net.wit.service.SmsSendService;
import net.wit.service.TemplateService;
import net.wit.webservice.SmsClient;

import org.jsoup.helper.StringUtil;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;

@Service("smsSendServiceImpl")
public class SmsSendServiceImpl extends BaseServiceImpl<SmsSend, String> implements SmsSendService {

	@Resource(name = "smsSendDaoImpl")
	private SmsSendDao smsSendDao;

	@Resource(name = "taskExecutor")
	private TaskExecutor taskExecutor;

	@Resource(name = "smsSendDaoImpl")
	public void setBaseDao(SmsSendDao smsSendDao) {
		super.setBaseDao(smsSendDao);
	}

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "templateServiceImpl")
	private TemplateService templateService;

	@Resource(name = "freeMarkerConfigurer")
	private FreeMarkerConfigurer freeMarkerConfigurer;

	private static SmsClient client = null;

	public synchronized SmsClient getClient() {
		ResourceBundle bundle = PropertyResourceBundle.getBundle("config");
		if (client == null) {
			try {
				client = new SmsClient(bundle.getString("softwareSerialNo"), bundle.getString("password"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return client;
	}

	/** 发送验证合法短信 */
	private SmsSend send(SmsSend smsSend) {
		String r = "-1";
		client = getClient();
		try {
			String content = URLEncoder.encode(smsSend.getContent(), "utf8");
			r = client.mdSmsSend_u(smsSend.getMobiles(), content, "", "", "");
		} catch (Exception e) {
			r = "-9999";
		}
		if (r.startsWith("-") || r.equals("")) {
			smsSend.setStatus(Status.Error);
			smsSend.setDescr("短信失败，错误码=" + r);
		} else {
			smsSend.setStatus(Status.send);
			smsSend.setDescr("短信发送成功");
		}
		super.save(smsSend);
		return smsSend;
	}

	private void saveIllegalSMS(SmsSend smsSend, String message) {
		if (smsSend == null) {
			return;
		}
		smsSend.setStatus(Status.Error);
		smsSend.setDescr(message);
		super.save(smsSend);
	}

	private void putSendPool(final SmsSend smsSend) {
		try {
			taskExecutor.execute(new Runnable() {
				public void run() {
					send(smsSend);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private SmsSend validate(String mobile, String content, Type type) throws IllegalArgumentException {
		if (StringUtil.isBlank(mobile)) {
			throw new java.lang.IllegalArgumentException("接收号码为空");
		}
		BigDecimal amount = new BigDecimal(1 * 1.00 / 10.00);
		SmsSend smsSend = null;
		switch (type) {
		case member:
			Member member = memberService.getCurrent();
			if (amount.compareTo(member.getBalance()) > 0) {
				throw new java.lang.IllegalArgumentException("账户余额不足");
			}
			smsSend = new SmsSend();
			smsSend.setMember(member);
			smsSend.setType(Type.member);
			smsSend.setFee(amount);
			break;
		case system:
			smsSend = new SmsSend();
			smsSend.setType(Type.system);
			smsSend.setFee(amount);
			break;
		}
		smsSend.setMobiles(mobile);
		smsSend.setContent(content);
		smsSend.setPriority(1);
		smsSend.setCharset("GBK");
		smsSend.setCount(1);
		return smsSend;
	}

	/** 发送会员短信 */
	public String sendByMember(String mobile, String content) {
		SmsSend smsSend = null;
		try {
			smsSend = validate(mobile, content, Type.member);
			send(smsSend);
			return smsSend.getDescr();
		} catch (IllegalArgumentException e) {
			saveIllegalSMS(smsSend, e.getMessage());
			return "error";
		} catch (Exception e) {
			return "error";
		}
	}

	/** 异步线程发送(暂不考虑线程池堆满情况处理) */
	public void putMemberSendPool(final String mobile, final String content) {
		SmsSend smsSend = null;
		try {
			smsSend = validate(mobile, content, Type.member);
			putSendPool(smsSend);
		} catch (IllegalArgumentException e) {
			saveIllegalSMS(smsSend, e.getMessage());
		} catch (Exception e) {
		}
	}

	/** 发送系统短信 */
	public String sysSend(String mobile, String content) {
		SmsSend smsSend = null;
		try {
			smsSend = validate(mobile, content, Type.system);
			send(smsSend);
			return smsSend.getDescr();
		} catch (IllegalArgumentException e) {
			saveIllegalSMS(smsSend, e.getMessage());
			return "error";
		} catch (Exception e) {
			return "error";
		}
	}

	/** 异步线程发送(暂不考虑线程池堆满情况处理) */
	public void putSystemSendPool(final String mobile, final String content) {
		SmsSend smsSend = null;
		try {
			smsSend = validate(mobile, content, Type.system);
			putSendPool(smsSend);
		} catch (IllegalArgumentException e) {
			saveIllegalSMS(smsSend, e.getMessage());
		} catch (Exception e) {
		}
	}

	public void sendTemplateNoticePool(String mobile, String tempalte, Map<String, String> model) {
		try {
			Template template = templateService.get(tempalte);
			Configuration configuration = freeMarkerConfigurer.getConfiguration();
			freemarker.template.Template templateMarker = configuration.getTemplate(template.getTemplatePath());
			String text = FreeMarkerTemplateUtils.processTemplateIntoString(templateMarker, model);
			putSystemSendPool(mobile, text);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}