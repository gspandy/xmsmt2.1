<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.tenantCategory.add")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<style type="text/css">
.brands label {
	width: 150px;
	display: block;
	float: left;
	padding-right: 6px;
}
</style>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	[@flash_message /]
	// 表单验证
	$inputForm.validate({
		rules: {
			sendDate: "required",
			order: "digits"
		}
	});
	
});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo;定额发放设置
	</div>
	<form id="inputForm" action="save.jhtml" method="post">
	    <table class="input">
	      <tr >
	        <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font size="4"><strong>定额发放</strong></font></td>
	      </tr>
	      
	     <!--  <tr>
	        <td>
	        <table class="input">
			<tr>
			<th>
					<span class="requiredField">*</span>发放张数:
				</th>
				<td>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<select name="sendNum" style="width:80px;" >
					<option value="${ticket.sendNum}" selected="selected">${ticket.sendNum}</option>
						<option value="1"  >1</option>
						<option value="2"  >2</option>
						<option value="3"  >3</option>
						<option value="4"  >4</option>
						<option value="5"  >5</option>
						<option value="10" >10</option>
						<option value="15" >15</option>
						<option value="30" >30</option>
					</select>张&nbsp;&nbsp;&nbsp;<font color="red">每月1号企业固定发放给每位店长会员的内购券数量设定</font>
				</td>
			</tr>	-->
		</table>
	        </td>
	      </tr>
	       <tr>
	        <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font size="4"><strong>店长会员首次发放内购券数量</strong></font></td>
	      </tr>
	       <tr>
	        <td>
	        <table class="input">
			<tr>
			<th>
					<span class="requiredField">*</span>发放张数:
				</th>
				<td>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<select name="newSendNum" style="width:80px;" >
					    <option value="${newticket.sendNum}" selected="selected">${newticket.sendNum}</option>
						<option value="1"  >1</option>
						<option value="2"  >2</option>
						<option value="3"  >3</option>
						<option value="4"  >4</option>
						<option value="5"  >5</option>
						<option value="10" >10</option>
						<option value="15" >15</option>
						<option value="30" >30</option>
					</select>张&nbsp;&nbsp;&nbsp;<font color="red">企业给新店长会员发放的内购券数量设定</font>
				</td>
			</tr>	
		</table>
	        </td>
	      </tr>
	      <tr >
	        <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font size="4"><strong>店长申请券券每次发放数量</strong></font></td>
	       </tr>
	       <tr>
	        <td>
	        <table class="input">
			   <tr>
		     	<th>
					  <span class="requiredField">*</span>发放张数:
				    </th>
				   <td>
				       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					   <select name="applySendNum" style="width:80px;" >
					   <option value="${ticketApply.sendNum}" selected="selected">${ticketApply.sendNum}</option>
						  <option value="1"  >1</option>
						  <option value="2"  >2</option>
						   <option value="3"  >3</option>
						   <option value="4"  >4</option>
						   <option value="5"  >5</option>
					   </select>张&nbsp;&nbsp;&nbsp;<font color="red">企业给申请券券的店长的券券发放数量</font>
				 </td>
			 </tr>	
		    </table>
	        </td>
	      </tr>
	      <tr>
	         <td>
	         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" class="button" style="width:100px;" value="保存" />
	         </td>
	      </tr>
	    </table>
		
	</form>
</body>
</html>