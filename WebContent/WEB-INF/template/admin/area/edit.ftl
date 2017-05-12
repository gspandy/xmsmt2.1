<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.area.edit")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	
	[@flash_message /]
	
	// 表单验证
	$inputForm.validate({
		rules: {
			name: "required",
			code: "required",
			order: "digits"
		}
	});
	
});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; ${message("admin.area.edit")}
	</div>
	<form id="inputForm" action="update.jhtml" method="post">
		<input type="hidden" name="id" value="${area.id}" />
		<table class="input">
			<tr>
				<th>
					编码:
				</th>
				<td>
					${area.id}
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.area.parent")}:
				</th>
				<td>
					[#if area.parent??]${area.parent.name}[#else]${message("admin.area.root")}[/#if]
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Area.name")}:
				</th>
				<td>
					<input type="text" name="name" class="text" value="${area.name}" maxlength="100" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>行政编码:
				</th>
				<td>
					<input type="text" name="code" class="text" value="${area.code}" maxlength="6" />
				</td>
			</tr>
			<tr>
				<th>
					邮政编码:
				</th>
				<td>
					<input type="text" name="zipCode" class="text" value="${area.zipCode}" maxlength="6" />
				</td>
			</tr>
			<tr>
				<th>
					电话区号:
				</th>
				<td>
					<input type="text" name="telCode" class="text" value="${area.telCode}" maxlength="4" />
				</td>
			</tr>
			<tr>
				<th>
					状态:
				</th>
				<td>
					<select id="status" name="status">
						<option value="none"[#if "none" == area.status] selected="selected"[/#if]>待开通</option>
						<option value="enabled"[#if "enabled" == area.status] selected="selected"[/#if]>已开通</option>
						<option value="disabled"[#if "disabled" == area.status] selected="selected"[/#if]>已关闭</option>
					</select>
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.common.order")}:
				</th>
				<td>
					<input type="text" name="order" class="text" value="${area.order}" maxlength="9" />
				</td>
			</tr>
			<tr>
				<th>
					&nbsp;
				</th>
				<td>
					<input type="submit" class="button" value="${message("admin.common.submit")}" />
					<input type="button" class="button" value="${message("admin.common.back")}" onclick="location.href='list.jhtml[#if area.parent??]?parentId=${area.parent.id}[/#if]'" />
				</td>
			</tr>
		</table>
	</form>
</body>
</html>