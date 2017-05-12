<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@page import="java.util.UUID"%>
<%@page import="java.security.interfaces.RSAPublicKey"%>
<%@page import="org.apache.commons.lang.ArrayUtils"%>
<%@page import="org.apache.commons.codec.binary.Base64"%>
<%@page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="net.wit.Setting"%>
<%@page import="net.wit.util.SettingUtils"%>
<%@page import="net.wit.util.SpringUtils"%>
<%@page import="net.wit.Setting.CaptchaType"%>
<%@page import="net.wit.Setting.AccountLockType"%>
<%@page import="net.wit.service.RSAService"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%
response.setHeader("Pragma","No-cache"); 
response.setHeader("Cache-Control","no-cache"); 
response.setDateHeader("Expires", 0);
String base = request.getContextPath();
String captchaId = UUID.randomUUID().toString();
ApplicationContext applicationContext = SpringUtils.getApplicationContext();
Setting setting = SettingUtils.get();
String certtext = setting.getCerttext();
String siteName = setting.getSiteName();
if (applicationContext != null) {
%>
<shiro:authenticated>
<%
response.sendRedirect(base + "/admin/common/main.jhtml");
%>
</shiro:authenticated>
<%
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />

<%
if (applicationContext != null) {
	RSAService rsaService = SpringUtils.getBean("rsaServiceImpl", RSAService.class);
	RSAPublicKey publicKey = rsaService.generateKey(request);
	String modulus = Base64.encodeBase64String(publicKey.getModulus().toByteArray());
	String exponent = Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray());
	
	String message = null;
	String loginFailure = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
	if (loginFailure != null) {
		if (loginFailure.equals("org.apache.shiro.authc.pam.UnsupportedTokenException")) {
			message = "admin.captcha.invalid";
		} else if (loginFailure.equals("org.apache.shiro.authc.UnknownAccountException")) {
			message = "admin.login.unknownAccount";
		} else if (loginFailure.equals("org.apache.shiro.authc.DisabledAccountException")) {
			message = "admin.login.disabledAccount";
		} else if (loginFailure.equals("org.apache.shiro.authc.LockedAccountException")) {
			message = "admin.login.lockedAccount";
		} else if (loginFailure.equals("org.apache.shiro.authc.IncorrectCredentialsException")) {
			if (ArrayUtils.contains(setting.getAccountLockTypes(), AccountLockType.admin)) {
				message = "admin.login.accountLockCount";
			} else {
				message = "admin.login.incorrectCredentials";
			}
		} else if (loginFailure.equals("org.apache.shiro.authc.AuthenticationException")) {
			message = "admin.login.authentication";
		}
	}
%>
<title><%=SpringUtils.getMessage("admin.login.title")%> - Powered By z8ls</title>
<meta http-equiv="expires" content="0" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="<%=base%>/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<link href="<%=base%>/resources/admin/css/login.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="<%=base%>/resources/admin/css/zhibang.css" />
<script src="<%=base%>/resources/admin/js/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="<%=base%>/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="<%=base%>/resources/admin/js/jsbn.js"></script>
<script type="text/javascript" src="<%=base%>/resources/admin/js/prng4.js"></script>
<script type="text/javascript" src="<%=base%>/resources/admin/js/rng.js"></script>
<script type="text/javascript" src="<%=base%>/resources/admin/js/rsa.js"></script>
<script type="text/javascript" src="<%=base%>/resources/admin/js/base64.js"></script>
<script type="text/javascript" src="<%=base%>/resources/admin/js/common.js"></script>
<script type="text/javascript">
	$().ready( function() {
		
		var $loginForm = $("#loginForm");
		var $enPassword = $("#enPassword");
		var $username = $("#username");
		var $password = $("#password");
		var $captcha = $("#captcha");
		var $captchaImage = $("#captchaImage");
		var $isRememberUsername = $("#isRememberUsername");
		
		// 记住用户名
		if(getCookie("adminUsername") != null) {
			$isRememberUsername.prop("checked", true);
			$username.val(getCookie("adminUsername"));
			$password.focus();
		} else {
			$isRememberUsername.prop("checked", false);
			$username.focus();
		}
		
		// 更换验证码
		$captchaImage.click( function() {
			$captchaImage.attr("src", "<%=base%>/admin/common/captcha.jhtml?captchaId=<%=captchaId%>&timestamp=" + (new Date()).valueOf());
		});
		
		// 表单验证、记住用户名
		$loginForm.submit( function() {
			if ($username.val() == "") {
				$.message("warn", "<%=SpringUtils.getMessage("admin.login.usernameRequired")%>");
				return false;
			}
			if ($password.val() == "") {
				$.message("warn", "<%=SpringUtils.getMessage("admin.login.passwordRequired")%>");
				return false;
			}
			
			
			if ($isRememberUsername.prop("checked")) {
				addCookie("adminUsername", $username.val(), {expires: 7 * 24 * 60 * 60});
			} else {
				removeCookie("adminUsername");
			}
			
			var rsaKey = new RSAKey();
			rsaKey.setPublic(b64tohex("<%=modulus%>"), b64tohex("<%=exponent%>"));
			var enPassword = hex2b64(rsaKey.encrypt($password.val()));
			$enPassword.val(enPassword);
		});
		
		<%if (message != null) {%>
			$.message("error", "<%=SpringUtils.getMessage(message, setting.getAccountLockCount())%>");
		<%}%>
	});
</script>
<%} else {%>
<title>提示信息 - Powered By WIT</title>
<meta http-equiv="expires" content="0" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta name="author" content="WIT Team" />
<meta name="copyright" content="WIT" />
<link href="<%=base%>/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<link href="<%=base%>/resources/admin/css/login.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="<%=base%>/static/css/cloud-admin.css">
<%}%>
<style>
.username{
border:1px solid #cccccc;
box-shadow: none;
background-color: #FFFCFC;
display: block;
width: 100%;
height: 50px;
box-sizing: border-box;
background-image: url(../resources/admin/images/icon01.png);
background-position: 20px center;
background-repeat: no-repeat;
padding-left: 50px;
margin-top: 10%;
}
.password{
border:1px solid #cccccc;
box-shadow: none;
background-color: #FFFCFC;
display: block;
width: 100%;
height: 50px;
box-sizing: border-box;
background-image: url(../resources/admin/images/icon02.png);
background-position: 20px center;
background-repeat: no-repeat;
padding-left: 50px;
margin-top: 10%;
}
</style>
</head>
<body class="login">
            <header>
				<div class="container">
					<div class="row">
						<div class="col-md-4 col-md-offset-4">
							<div id="logo">
								<a href="#"><img src="<%=base%>/resources/admin/images/logo.png"  alt="logo name" /></a>
							
							</div>
							
						</div>
					</div>
				</div>
				<!--/NAV-BAR -->
			</header>
<!-- <header class="header"> -->
<!-- 	<div class="container"> -->
<%-- 			<div class="logo"><a href="#"><img src="<%=base%>/resources/admin/images/logo.png"/></a></div> --%>
<!--             <div class="Userib"><a href="#">使用帮肋</a></div> -->
<!--     </div> -->
<!-- </header> -->
	<%if (applicationContext != null) {%>
	

	
<div class="banner" style="position:relative" >	
		<div class="login_main" style="position:absolute">
		<div ></div>
		<div style="position:absolute">
			<div class="login_middle_main loginform" style="padding-top: 2%;position:absolute;margin-left:10%">
				<div class="login-Float-div" style="width:340px;margin-left:70%;position:absolute;">
						<form id="loginForm" action="login.jsp" method="post">
							<input type="hidden" id="enPassword" name="enPassword" />
							<%if (ArrayUtils.contains(setting.getCaptchaTypes(), CaptchaType.adminLogin)) {%>
								<input type="hidden" style="position:absolute;" name="captchaId" value="<%=captchaId%>" />
							<%}%>
							<table class="loginTable" style="width:290px;position:absolute;">
								
								<tr>
								
 									<th > 
									<%=SpringUtils.getMessage("admin.login.username")%>: 
								    </th> 
								    
								    </tr>
								    <td></td>
								<tr>
									<th>
										<input type="text" id="username" name="username" style="position:absolute;margin-top:2%" class="username" maxlength="20"  />
									</th>
								</tr>
								<tr>
									<th style="padding: 60px 0px 0px 0px;style="position:absolute;"">
										<%=SpringUtils.getMessage("admin.login.password")%>:
									</th>
								</tr>
								<tr>	
									<td>
										<input type="password" id="password" style="position:absolute;margin-top:2%" class="password" maxlength="20" autocomplete="off" />
									</td>
								</tr>
								<%if (ArrayUtils.contains(setting.getCaptchaTypes(), CaptchaType.adminLogin)) {%>
									<tr>
										<th style="padding:80px 0 0 0">
											<%=SpringUtils.getMessage("admin.captcha.name")%>:
										</th>
										<td>
											<input type="text"style="margin-left:10%;height:30px;position:absolute" id="captcha" name="captcha" class="text captcha" maxlength="4" autocomplete="off" /><img id="captchaImage"  class="captchaImage" src="<%=base%>/admin/common/captcha.jhtml?captchaId=<%=captchaId%>" title="<%=SpringUtils.getMessage("admin.captcha.imageTitle")%>" />
										</td>
									</tr>
								<%}%>
								<tr>

									
									<tr>
										<th style="padding:10px 0 0 0;float:left;position:absolute">
											<input type="checkbox" id="isRememberUsername" value="true" />
										<font style="font-size:14px">	<%=SpringUtils.getMessage("admin.login.rememberUsername")%></font>
										</th>
<!-- 										<th> -->
<!-- 										<span style="float:right;font-size: 14px;margin-right: 6%;"><a href="#">无法登录?</a></span> -->
<!-- 								        </th> -->
									</tr>
										
									
								</tr>
								<tr>

									<td style="position:absolute;margin-top: 20%;">
										<input type="submit" class="submitButton" style="position:absolute;" value="登录" />
										
									</td>
								</tr>
							</table>
						</form>
				</div>
			</div>
		</div>
		<div class="login_footer">
			<div class="powered" style="margin-top: 35%;">
            	<%=siteName %> 版权所有 2002-2013 All Rights Reserved  备案：<%=certtext %>
            </div>
		</div>		
	</div>
</div>
	<%} else {%>
		<fieldset>
			<legend>系统出现异常</legend>
			<p>请检查WIT程序是否已正确安装 [<a href="<%=base%>/install/">点击此处进行安装</a>]</p>
			<p>
				<strong>提示: WIT安装完成后必须重新启动WEB服务器</strong>
			</p>
		</fieldset>
	<%}%>
</body>
</html>