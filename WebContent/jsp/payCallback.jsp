<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page import="net.wit.service.impl.*" import="java.util.Map"%>
<%! String formatString(String text) {
		return (text == null) ? "" : text.trim();
	}
%>
<%
	String data			= formatString(request.getParameter("data"));

	if("".equals(data)) {
		out.println("ONEKEY  一键支付成功");
		return;
	}
	
	Map<String, String> result = ZGTService.decryptPayCallbackData(data);
	String customernumber      = formatString(result.get("customernumber"));
	String requestid           = formatString(result.get("requestid"));
	String code                = formatString(result.get("code"));
	String notifytype          = formatString(result.get("notifytype"));
	String externalid          = formatString(result.get("externalid"));
	String amount              = formatString(result.get("amount"));
	String cardno              = formatString(result.get("cardno"));
	String bankcode            = formatString(result.get("bankcode"));
	String cardtype            = formatString(result.get("cardtype"));
	String msg	               = formatString(result.get("msg"));
	String hmac                = formatString(result.get("hmac"));
	String customError         = formatString(result.get("customError"));

	
	if(!"1".equals(code) || !"".equals(customError)) {
		out.println("<br>customError : " + customError);
		out.println("<br><br>code : " + code);
		out.println("<br><br>msg  : " + msg);
		return;
	} else if(notifytype.equals("SERVER")) {
		out.println("SUCCESS");
		return;
	}
%>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>通知-订单支付成功</title>
	</head>
	<body>	
		<br /> <br />
		<table width="70%" border="0" align="center" cellpadding="5" cellspacing="0" style="border:solid 1px #107929">
			<tr>
		  		<th align="center" height="30" colspan="5" bgcolor="#6BBE18">
					通知-订单支付成功
				</th>
		  	</tr>

			<tr>
				<td width="15%" align="left">&nbsp;商户编号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=customernumber%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">customernumber</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;返回码</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=code%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">code</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;通知类型</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=notifytype%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">notifytype</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;商户订单号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=requestid%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">requestid</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;交易流水号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=externalid%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">externalid</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;订单金额</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=amount%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">amount</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;银行卡号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=cardno%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">cardno</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;银行编码</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=bankcode%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">bankcode</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;银行卡类型</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=cardtype%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">cardtype</td> 
			</tr>

	</body>

</html>
