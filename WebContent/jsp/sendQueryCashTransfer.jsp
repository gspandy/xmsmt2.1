<%@ page language="java" contentType="text/html; charset=UTF-8" 
	pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" import="java.util.HashMap" import="net.wit.service.impl.*"%>
<!DOCTYPE html>
<%! String formatString(String text){
		return text==null ? "" : text.trim();
	}
%>
<%
	request.setCharacterEncoding("UTF-8");

	String cashrequestid		= formatString(request.getParameter("cashrequestid"));	

	Map<String, String> result	= ZGTService.queryCashTransfer(cashrequestid);
	String customernumber		= formatString(result.get("customernumber"));
	String cashrequestidYeepay 	= formatString(result.get("cashrequestid"));
	String code             	= formatString(result.get("code"));
	String ledgerno         	= formatString(result.get("ledgerno"));
	String amount           	= formatString(result.get("amount"));
	String status           	= formatString(result.get("status"));
	String lastno           	= formatString(result.get("lastno"));
	String hmac					= formatString(result.get("hmac"));
	String msg					= formatString(result.get("msg"));
	String desc 	          	= formatString(result.get("desc"));
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
<title>提现查询接口</title>
</head>
	<body>
		<br /> <br />
		<table width="70%" border="0" align="center" cellpadding="5" cellspacing="0" 
							style="word-break:break-all; border:solid 1px #107929">
			<tr>
		  		<th align="center" height="30" colspan="5" bgcolor="#6BBE18">
					提现查询接口
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
				<td width="15%" align="left">&nbsp;提现请求号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=cashrequestidYeepay%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">cashrequestid</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;返回码</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=code%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">code</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;子账户商户编号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=ledgerno%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">ledgerno</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;提现金额</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=amount%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">amount</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;提现状态</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=status%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">status</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;银行卡后四位</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=lastno%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">lastno</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;描述</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=desc%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">desc</td> 
			</tr>

		</table>

	</body>
</html>
