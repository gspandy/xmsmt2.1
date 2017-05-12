<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" import="java.util.HashMap" import="net.wit.service.impl.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%! String formatString(String text){
		return text==null ? "" : text.trim();
	}
%>
<%
	request.setCharacterEncoding("UTF-8");

	String requestid         	= formatString(request.getParameter("requestid"));
	String ledgerno          	= formatString(request.getParameter("ledgerno"));
	String bankaccountnumber 	= formatString(request.getParameter("bankaccountnumber"));
	String bankname          	= formatString(request.getParameter("bankname"));
	String accountname       	= formatString(request.getParameter("accountname"));
	String bankaccounttype   	= formatString(request.getParameter("bankaccounttype"));
	String bankprovince      	= formatString(request.getParameter("bankprovince"));
	String bankcity          	= formatString(request.getParameter("bankcity"));
	String minsettleamount   	= formatString(request.getParameter("minsettleamount"));
	String riskreserveday    	= formatString(request.getParameter("riskreserveday"));
	String manualsettle      	= formatString(request.getParameter("manualsettle"));
	String callbackurl      	= formatString(request.getParameter("callbackurl"));
	String bindmobile 	     	= formatString(request.getParameter("bindmobile"));

	Map<String, String> params	= new HashMap<String, String>();
    params.put("requestid", 		requestid);
    params.put("ledgerno", 			ledgerno);
    params.put("bankaccountnumber", bankaccountnumber);
    params.put("bankname", 			bankname);
    params.put("accountname", 		accountname);
    params.put("bankaccounttype", 	bankaccounttype);
    params.put("bankprovince", 		bankprovince);
    params.put("bankcity", 			bankcity);                  
    params.put("manualsettle", 		manualsettle);                  
    params.put("minsettleamount", 	minsettleamount);
    params.put("riskreserveday", 	riskreserveday);
    params.put("callbackurl", 		callbackurl);                  
    params.put("bindmobile", 		bindmobile);                  
    
	Map<String, String> result	= ZGTService.updateAccount(params);
	String customernumber		= formatString(result.get("customernumber"));
	String requestidFromYeepay	= formatString(result.get("requestid"));
	String code 				= formatString(result.get("code"));
	String hmac					= formatString(result.get("hmac"));
	String msg					= formatString(result.get("msg"));
	String customError			= formatString(result.get("customError"));

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
<title>账户结算信息修改接口</title>
</head>
	<body>
		<br /> <br /> <br />
		<table width="60%" border="0" align="center" cellpadding="10" cellspacing="0" 
							style="word-break:break-all; border:solid 1px #107929">
			<tr>
		  		<th align="center" height="30" colspan="5" bgcolor="#6BBE18">
					账户结算信息修改接口
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

		</table>

	</body>
</html>
