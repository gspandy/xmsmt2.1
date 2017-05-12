<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>券券申请条件设置</title>
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
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo;券券申请条件设置
	</div>
	<form id="inputForm" action="save.jhtml" method="post">
	    <input type="hidden" name="id" value="${ticketApply.id}"/>
	    <table class="input">
	       <tr>
	          <td> 
	             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<strong><font size="4">券券申请条件</font></strong>
	             <p><font color="red">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;说明：每个店长满足以下条件之一，即可申请一次券券</font></p>
	          </td>
	       </tr>
	       <tr>
	       <td>
	        <table class="input">
			     <tr>
	                <th>
					      每月成功交易次数:
		            </th>
	                <td>
	                  <input type="text" name="ticketUsedTimes" class="text" value="${ticketApply.ticketUsedTimes}" maxlength="300" /> 次
	                </td>
	              </tr>
	            </table>
	          </td>
	       </tr>
	       <tr>
	         <td>
	        <table class="input">
			     <tr>
	          <th>
					      每月成功邀请店长人数:
		      </th>
	          <td>
	                  <input type="text" name="invations" class="text"  value="${ticketApply.invations}" maxlength="300" /> 人
	          </td>
	            </tr>
	            </table>
	          </td>
	       </tr>
	       <tr>
	            <td style="padding: 0px;"> 
	             <div style="margin-left:auto;margin-right:auto;padding:0; width:100%;height:1px;background-color:#303030;overflow:hidden;"></div>
	           </td>
	       </tr>
	       <tr>
	        <td>
	        <table class="input">
			     <tr>
	          <th>
					      申请自动过期天数:
		      </th>
	          <td>
	                  <input type="text" name="autoRejectDays" value="${ticketApply.autoRejectDays}" class="text" maxlength="300" /> 天
	          </td>
	           </tr>
	            </table>
	          </td>
	       </tr>
	      <tr>
	         <td>
	            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" class="button" style="width:100px;" value="保存" />
	         </td>
	      </tr>
	    </table>
		
	</form>
</body>
</html>