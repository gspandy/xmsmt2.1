<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Date, java.text.SimpleDateFormat"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	SimpleDateFormat dateFormat		= new SimpleDateFormat("yyMMdd_HHmmssSSS");
	String requestid				= "ZGTREFUND" + dateFormat.format(new Date());
%><html>
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
	<title>订单退款接口</title>
</head>
	<body>
		<table width="80%" border="0" align="center" cellpadding="5" cellspacing="0" style="border:solid 1px #107929">
			<tr>
		  		<th align="center" height="30" colspan="5" bgcolor="#6BBE18">
					请输入退款请求参数	
				</th>
		  	</tr> 

			<form method="post" action="../jsp/sendRefund.jsp" target="_blank" accept-charset="UTF-8">
				<tr >
					<td width="20%" align="left">&nbsp;退款请求号</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="requestid" value="<%=requestid%>"/>
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">requestid</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;商户订单号</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="orderrequestid" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">orderrequestid</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;退款金额</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="amount" value="" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">amount</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;子账户退款详情</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="divideinfo" value="" />
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">divideinfo</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;退款确认</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="confirm" readonly="readonly" value="1" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">confirm</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;退款备注</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="memo" value="" />
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">memo</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;</td>
					<td width="5%"  align="center">&nbsp;</td> 
					<td width="55%" align="left"> 
						<input type="submit" value="提交退款" />
					</td>
					<td width="5%"  align="center">&nbsp;</td> 
					<td width="15%" align="left">&nbsp;</td> 
				</tr>

			</form>
		</table>
</body>
</html>
