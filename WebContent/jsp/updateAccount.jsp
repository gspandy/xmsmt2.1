<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Date, java.text.SimpleDateFormat"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	SimpleDateFormat dateFormat		= new SimpleDateFormat("yyMMdd_HHmmssSSS");
	String requestid				= "ZGTUPDATEACCOUNT" + dateFormat.format(new Date());
%>
<html>
<head>
	<meta charset="UTF-8" />
	<title>账户结算信息修改接口</title>
</head>
	<body>
		<br><br>
		<table width="80%" border="0" align="center" cellpadding="5" cellspacing="0" style="border:solid 1px #107929">
			<tr>
		  		<th align="center" height="30" colspan="5" bgcolor="#6BBE18">
					账户结算信息修改接口
				</th>
		  	</tr> 

			<form method="POST" action="sendUpdateAccount.jsp" target="_blank" accept-charset="UTF-8">
				<tr >
					<td width="20%" align="left">&nbsp;修改请求号</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="requestid" value="<%=requestid%>"/>
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">requestid</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;子账户商编</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="ledgerno" placeholder="若不填写，则修改主商编的账户信息。" value=""/>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">ledgerno</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;银行卡号</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="bankaccountnumber" placeholder="当银行卡号、开户名有变动时，为降低风险，需易宝工作人员审核，审核时间：1-3个工作日。" value="" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">bankaccountnumber</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;开户行</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="bankname" value="" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">bankname</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;开户名</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="accountname" placeholder="当银行卡号、开户名有变动时，为降低风险，需易宝工作人员审核，审核时间：1-3个工作日。" value="" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">accountname</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;银行卡类型</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input type="radio" name="bankaccounttype" id="PrivateCash" value="PrivateCash" checked />
						<label for="PrivateCash">对私</label>
						<input type="radio" name="bankaccounttype" id="PublishCash" value="PublishCash"/>
						<label for="PublishCash">对公</label>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">bankaccounttype</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;银行卡开户省</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="bankprovince" value="" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">bankprovince</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;银行卡开户市</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="bankcity" value="" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">bankcity</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;起结金额</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="minsettleamount" value="" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">minsettleamount</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;结算周期</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="riskreserveday" value="" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">riskreserveday</td> 
				</tr>
				
				<tr >
					<td width="20%" align="left">&nbsp;是否自助结算</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input type="radio" name="manualsettle" id="N" value="N" checked />
						<label for="N">否</label>
						<input type="radio" name="manualsettle" id="Y" value="Y"/>
						<label for="Y">是</label>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">manualsettle</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;后台回调地址</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="callbackurl" value="" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">callbackurl</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;绑定手机号</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="bindmobile" value="" />
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">bindmobile</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;</td>
					<td width="5%"  align="center">&nbsp;</td> 
					<td width="55%" align="left"> 
						<input type="submit" value="submit" />
					</td>
					<td width="5%"  align="center">&nbsp;</td> 
					<td width="15%" align="left">&nbsp;</td> 
				</tr>

			</form>
		</table>
</body>
</html>
