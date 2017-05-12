<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>编辑店长等级</title>
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
			inviteCondition: {
				required: true,digits:true
			},
			bonusLevel: {
				required: true,digits:true
			},
			ticketNum: {
				required: true,digits:true
			}
		}
	});
	
});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; ${message("admin.productCategory.edit")}
	</div>
	<form id="inputForm" action="update.jhtml" method="post">
		<input type="hidden" name="id" value="${vipLevel.id}" />
		<table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>等级名称:
				</th>
				<td>
					${vipLevel.levelName}
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>邀请店长人数:
				</th>
				<td>
					<input type="text" name="inviteCondition" class="text" value="${vipLevel.inviteCondition}"
						style="width:100px;" maxlength="4" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>邀请奖金比例:
				</th>
				<td>
					<input type="text" name="bonusLevel" class="text" maxlength="2"  value="${vipLevel.bonusLevel}"
						style="width:60px"/>&nbsp;<font size="2px"><strong>%</strong></font>
				</td>
			</tr>
			<tr class="brands">
				<th>
					<span class="requiredField">*</span>每月固定发放内购券:
				</th>
				<td>
					<input type="text" name="ticketNum" value="${vipLevel.ticketNum}"
						class="text" style="width:100px"/>
				</td>
			</tr>
			<tr>
				<th>
					&nbsp;
				</th>
				<td>
					<input type="submit" class="button" value="${message("admin.common.submit")}" />
					<input type="button" class="button" value="${message("admin.common.back")}" onclick="location.href='list.jhtml'" />
				</td>
			</tr>
		</table>
	</form>
</body>
</html>