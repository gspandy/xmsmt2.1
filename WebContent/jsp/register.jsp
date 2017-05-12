<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Date, java.text.SimpleDateFormat"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	SimpleDateFormat dateFormat		= new SimpleDateFormat("yyMMdd_HHmmssSSS");
	String requestid				= "ZGTREGISTER" + dateFormat.format(new Date());
%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
	<title>掌柜通注册接口</title>
</head>
	<body>
		<table width="80%" border="0" align="center" cellpadding="5" cellspacing="0" style="border:solid 1px #107929">
			<tr>
		  		<th align="center" height="30" colspan="5" bgcolor="#6BBE18">
					子账户商户编号注册
				</th>
		  	</tr> 

			<form method="POST" action="../jsp/sendRegister.jsp" target="_blank" accept-charset="UTF-8">
				<tr >
					<td width="20%" align="left">&nbsp;注册请求号</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="requestid" value="<%=requestid%>"/>
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">requestid</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;绑定手机号</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="bindmobile" value="12345678901" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">bindmobile</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;注册类型</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input type="radio" name="customertype" id="PERSON" value="PERSON" checked />
						<label for="PERSON">个人</label>
						<input type="radio" name="customertype" id="ENTERPRISE" value="ENTERPRISE"/>
						<label for="ENTERPRISE">企业</label>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">customertype</td> 
				</tr> 


				<tr >
					<td width="20%" align="left">&nbsp;签约名</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="signedname" value="掌柜通测试" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">signedname</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;联系人姓名</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="linkman" value="张三" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">linkman</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;个人身份证号</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="idcard" value="123456789012345678" />
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">idcard</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;</td>
					<td width="5%"  align="center">&nbsp;</td> 
					<td width="55%" align="left"> 
						<span style="font-size:12px; color:#FF0000; font-weight:100;"> 
							「个人身份证」在「注册类型」为「个人」时必填！
						</span>
					</td>
					<td width="5%"  align="center">&nbsp;</td> 
					<td width="15%" align="left">&nbsp;</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;企业营业执照号</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="businesslicence" value="" />
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">businesslicence</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;</td>
					<td width="5%"  align="center">&nbsp;</td> 
					<td width="55%" align="left"> 
						<span style="font-size:12px; color:#FF0000; font-weight:100;"> 
							「企业营业执照」在「注册类型」为「企业」时必填！
						</span>
					</td>
					<td width="5%"  align="center">&nbsp;</td> 
					<td width="15%" align="left">&nbsp;</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;姓名</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="legalperson" value="张三" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">legalperson</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;</td>
					<td width="5%"  align="center">&nbsp;</td> 
					<td width="55%" align="left"> 
						<span style="font-size:12px; color:#FF0000; font-weight:100;"> 
							「姓名」在「注册类型」为「企业」时，填写企业法人姓名；为「个人」时填写身份证姓名！
						</span>
					</td>
					<td width="5%"  align="center">&nbsp;</td> 
					<td width="15%" align="left">&nbsp;</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;起结金额</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="minsettleamount" value="1" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">minsettleamount</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;结算周期</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="riskreserveday" value="1" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">riskreserveday</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;银行卡号</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="bankaccountnumber" value="1234567890123456" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">bankaccountnumber</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;银行卡开户行</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="bankname" value="招商银行股份有限公司杭州分行" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">bankname</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;银行卡开户名</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="accountname" value="张三" />
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
						<input type="radio" name="bankaccounttype" id="PublicCash" value="PublicCash"/>
						<label for="PublicCash">对公</label>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">bankaccounttype</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;银行卡开户省</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="bankprovince" value="浙江" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">bankprovince</td> 
				</tr>

				<tr >
					<td width="20%" align="left">&nbsp;银行卡开户市</td>
					<td width="5%"  align="center"> : &nbsp;</td> 
					<td width="55%" align="left"> 
						<input size="70" type="text" name="bankcity" value="杭州" />
						<span style="color:#FF0000;font-weight:100;">*</span>
					</td>
					<td width="5%"  align="center"> - </td> 
					<td width="15%" align="left">bankcity</td> 
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
					<td width="20%" align="left">&nbsp;</td>
					<td width="5%"  align="center">&nbsp;</td> 
					<td width="55%" align="left"> 
						<input type="submit" value="提交注册信息" />
					</td>
					<td width="5%"  align="center">&nbsp;</td> 
					<td width="15%" align="left">&nbsp;</td> 
				</tr>

			</form>
		</table>
</body>
</html>
