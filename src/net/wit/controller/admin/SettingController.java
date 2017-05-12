package net.wit.controller.admin;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.mail.MessagingException;

import net.wit.FileInfo;
import net.wit.Message;
import net.wit.Setting;
import net.wit.service.CacheService;
import net.wit.service.FileService;
import net.wit.service.MailService;
import net.wit.service.StaticService;
import net.wit.util.SettingUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sun.mail.smtp.SMTPSendFailedException;
import com.sun.mail.smtp.SMTPSenderFailedException;

@Controller("adminstingController")
@RequestMapping({"/admin/setting"})
public class SettingController extends BaseController
{

  @javax.annotation.Resource(name="fileServiceImpl")
  private FileService fileService;

  @javax.annotation.Resource(name="mailServiceImpl")
  private MailService mailService;

  @javax.annotation.Resource(name="cacheServiceImpl")
  private CacheService cacheService;

  @javax.annotation.Resource(name="staticServiceImpl")
  private StaticService staticService;

  @RequestMapping(value={"/mail_test"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message mailTest(String smtpFromMail, String smtpHost, Integer smtpPort, String smtpUsername, String smtpPassword, String toMail)
  {
    if (StringUtils.isEmpty(toMail)) {
      return ERROR_MESSAGE;
    }
    Setting setting = SettingUtils.get();
    if (StringUtils.isEmpty(smtpPassword))
      smtpPassword = setting.getSmtpPassword();
    try
    {
      if ((!(isValid(Setting.class, "smtpFromMail", smtpFromMail, new Class[0]))) || (!(isValid(Setting.class, "smtpHost", smtpHost, new Class[0]))) || (!(isValid(Setting.class, "smtpPort", smtpPort, new Class[0]))) || (!(isValid(Setting.class, "smtpUsername", smtpUsername, new Class[0])))) {
        return ERROR_MESSAGE;
      }
      this.mailService.sendTestMail(smtpFromMail, smtpHost, smtpPort, smtpUsername, smtpPassword, toMail);
    } catch (MailSendException e) {
      Exception[] messageExceptions = e.getMessageExceptions();
      if (messageExceptions != null)
      {
        Exception[] arrayOfException1;
        int j = (arrayOfException1 = messageExceptions).length; for (int i = 0; i < j; ++i) { Exception nextException;
          Exception exception = arrayOfException1[i];
          if (exception instanceof SMTPSendFailedException) {
            SMTPSendFailedException smtpSendFailedException = (SMTPSendFailedException)exception;
            nextException = smtpSendFailedException.getNextException();
            if (!(nextException instanceof SMTPSenderFailedException)) continue;
            return Message.error("admin.setting.mailTestSenderFailed", new Object[0]);
          }
          if (exception instanceof MessagingException) {
            MessagingException messagingException = (MessagingException)exception;
            nextException = messagingException.getNextException();
            if (nextException instanceof UnknownHostException)
              return Message.error("admin.setting.mailTestUnknownHost", new Object[0]);
            if (nextException instanceof ConnectException) {
              return Message.error("admin.setting.mailTestConnect", new Object[0]);
            }
          }
        }
      }
      return Message.error("admin.setting.mailTestError", new Object[0]);
    } catch (MailAuthenticationException e) {
      return Message.error("admin.setting.mailTestAuthentication", new Object[0]);
    } catch (Exception e) {
      return Message.error("admin.setting.mailTestError", new Object[0]);
    }
    return Message.success("admin.setting.mailTestSuccess", new Object[0]);
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(ModelMap model)
  {
    model.addAttribute("watermarkPositions", Setting.WatermarkPosition.values());
    model.addAttribute("roundTypes", Setting.RoundType.values());
    model.addAttribute("captchaTypes", Setting.CaptchaType.values());
    model.addAttribute("accountLockTypes", Setting.AccountLockType.values());
    model.addAttribute("stockAllocationTimes", Setting.StockAllocationTime.values());
    model.addAttribute("reviewAuthorities", Setting.ReviewAuthority.values());
    model.addAttribute("consultationAuthorities", Setting.ConsultationAuthority.values());
    return "/admin/setting/edit";
  }

  @RequestMapping(value={"/update"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String update(Setting setting, MultipartFile watermarkImageFile, RedirectAttributes redirectAttributes)
  {
    if (!(isValid(setting, new Class[0]))) {
      return "/admin/common/error";
    }
    if ((setting.getUsernameMinLength().intValue() > setting.getUsernameMaxLength().intValue()) || (setting.getPasswordMinLength().intValue() > setting.getPasswordMinLength().intValue())) {
      return "/admin/common/error";
    }
    Setting srcSetting = SettingUtils.get();
    if (StringUtils.isEmpty(setting.getSmtpPassword())) {
      setting.setSmtpPassword(srcSetting.getSmtpPassword());
    }
    if ((watermarkImageFile != null) && (!(watermarkImageFile.isEmpty()))) {
      if (!(this.fileService.isValid(FileInfo.FileType.image, watermarkImageFile))) {
        addFlashMessage(redirectAttributes, Message.error("admin.upload.invalid", new Object[0]));
        return "redirect:edit.jhtml";
      }
      String watermarkImage = this.fileService.uploadLocal(FileInfo.FileType.image, watermarkImageFile);
      setting.setWatermarkImage(watermarkImage);
    } else {
      setting.setWatermarkImage(srcSetting.getWatermarkImage());
    }
    setting.setCnzzSiteId(srcSetting.getCnzzSiteId());
    setting.setCnzzPassword(srcSetting.getCnzzPassword());
    SettingUtils.set(setting);
    this.cacheService.clear();
    this.staticService.buildIndex();
    this.staticService.buildOther();

    OutputStream outputStream = null;
    try {
      org.springframework.core.io.Resource resource = new ClassPathResource("/smt.properties");
      Properties properties = PropertiesLoaderUtils.loadProperties(resource);
      String templateUpdateDelay = properties.getProperty("template.update_delay");
      String messageCacheSeconds = properties.getProperty("message.cache_seconds");
      if (setting.getIsDevelopmentEnabled().booleanValue()) {
        if ((templateUpdateDelay.equals("0")) && (messageCacheSeconds.equals("0"))) {
//        	break ;// TODO
        }
        outputStream = new FileOutputStream(resource.getFile());
        properties.setProperty("template.update_delay", "0");
        properties.setProperty("message.cache_seconds", "0");
        properties.store(outputStream, "rsico PROPERTIES"); {
//        	break;// TODO
        }
      }

      if ((!(templateUpdateDelay.equals("0"))) && (!(messageCacheSeconds.equals("0")))) {
//    	  break ;// TODO
      }
      outputStream = new FileOutputStream(resource.getFile());
      properties.setProperty("template.update_delay", "3600");
      properties.setProperty("message.cache_seconds", "3600");
      properties.store(outputStream, "rsico PROPERTIES");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(outputStream);
    }
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:edit.jhtml";
  }
}