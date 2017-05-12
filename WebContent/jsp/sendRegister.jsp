<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" import="java.util.HashMap" import="net.wit.service.impl.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%! String formatString(String text){
		return text==null ? "" : text.trim();
	}
%>
<%
	request.setCharacterEncoding("UTF-8");

	String requestid               = formatString(request.getParameter("requestid"));
	String bindmobile              = formatString(request.getParameter("bindmobile"));
	String customertype            = formatString(request.getParameter("customertype"));
	String signedname              = formatString(request.getParameter("signedname"));
	String linkman                 = formatString(request.getParameter("linkman"));
	String idcard                  = formatString(request.getParameter("idcard"));
	String businesslicence         = formatString(request.getParameter("businesslicence"));
	String legalperson             = formatString(request.getParameter("legalperson"));
	String minsettleamount         = formatString(request.getParameter("minsettleamount"));
	String riskreserveday          = formatString(request.getParameter("riskreserveday"));
	String bankaccountnumber       = formatString(request.getParameter("bankaccountnumber"));
	String bankname                = formatString(request.getParameter("bankname"));
	String accountname             = formatString(request.getParameter("accountname"));
	String bankaccounttype         = formatString(request.getParameter("bankaccounttype"));
	String bankprovince            = formatString(request.getParameter("bankprovince"));
	String bankcity                = formatString(request.getParameter("bankcity"));
	String manualsettle            = formatString(request.getParameter("manualsettle"));

	Map<String, String> registerParams = new HashMap<String, String>();
    registerParams.put("requestid", 		requestid);
    registerParams.put("bindmobile", 		bindmobile);
    registerParams.put("customertype", 		customertype);
    registerParams.put("signedname", 		signedname);
    registerParams.put("linkman", 			linkman);
    registerParams.put("idcard", 			idcard);
    registerParams.put("businesslicence", 	businesslicence);
    registerParams.put("legalperson", 		legalperson);
    registerParams.put("minsettleamount", 	minsettleamount);
    registerParams.put("riskreserveday", 	riskreserveday);
    registerParams.put("bankaccountnumber", bankaccountnumber);
    registerParams.put("bankname", 			bankname);
    registerParams.put("accountname", 		accountname);
    registerParams.put("bankaccounttype", 	bankaccounttype);
    registerParams.put("bankprovince", 		bankprovince);
    registerParams.put("bankcity", 			bankcity);                  
    registerParams.put("manualsettle", 		manualsettle);                  
    
	Map<String, String> registerResult = ZGTService.registerAccount(registerParams);
	String customernumber		= formatString(registerResult.get("customernumber"));
	String requestidFromYeepay	= formatString(registerResult.get("requestid"));
	String code 				= formatString(registerResult.get("code"));
	String ledgerno				= formatString(registerResult.get("ledgerno"));
	String hmac					= formatString(registerResult.get("hmac"));
	String msg					= formatString(registerResult.get("msg"));
	String customError			= formatString(registerResult.get("customError"));

	if(!"1".equals(code) || !"".equals(customError)) {
		out.println("<br>customError : " + customError);
		out.println("<br><br>code : " + code);
		out.println("<br><br>msg  : " + msg);
		return;
	}
%>
<html>
<head>
<meta charset="UTF-8">
<title>子账户商户编号注册结果</title>
</head>
	<body>
		<br /> <br /> <br />
		<table width="60%" border="0" align="center" cellpadding="10" cellspacing="0" 
							style="word-break:break-all; border:solid 1px #107929">
			<tr>
		  		<th align="center" height="30" colspan="5" bgcolor="#6BBE18">
					子账户商户编号注册结果
				</th>
		  	</tr>

			<tr >
				<td width="25%" align="left">&nbsp;主账户商户编号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="45"  align="left"> <%=customernumber%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">customernumber</td> 
			</tr>

			<tr >
				<td width="25%" align="left">&nbsp;注册请求号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="45"  align="left"> <%=requestidFromYeepay%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">requestid</td> 
			</tr>

			<tr >
				<td width="25%" align="left">&nbsp;返回码</td>
				<td width="5%"  align="center"> : </td> 
				<td width="45"  align="left"> <%=code%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">code</td> 
			</tr>

			<tr >
				<td width="25%" align="left">&nbsp;子账户商户编号</td>
				<td width="5%"  align="center"> : </td> 
				<th width="45"  align="left"> <%=ledgerno%> </th>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">ledgerno</td> 
			</tr>

		</table>

	</body>
</html>
