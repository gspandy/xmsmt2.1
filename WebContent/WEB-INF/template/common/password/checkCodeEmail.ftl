<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("shop.register.mailTitle")}[#if systemShowPowered] - Powered By rsico[/#if]</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
</head>
<body>
	<p>${username}:</p>
	<p> 您好，此邮件为系统发送邮件，请勿回复。</p>
	<p> 您正在使用${setting.siteName}找回密码功能，请在找回密码页面输入以下验证码以便您找回密码[#if safeKey.expire??](${message("shop.password.expire")}: ${safeKey.expire?string("yyyy-MM-dd HH:mm")})[/#if]</p>
	<p>
		验证码:${safeKey.value}
	</p>
	<p>${setting.siteName}</p>
	<p>${.now?string("yyyy-MM-dd")}</p>
</body>
</html>