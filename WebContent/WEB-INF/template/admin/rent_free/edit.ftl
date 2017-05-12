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
<script type="text/javascript" src="${base}/resources/admin/datePicker/WdatePicker.js"></script>
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
	var $browserButton = $("#browserButton");
	
	[@flash_message /]
	$browserButton.browser();

	// 表单验证
	$inputForm.validate({
		rules: {
			name: "required",
			order: "digits"
		}
	});
	
});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 免租设置
	</div>
	<form id="inputForm" action="save.jhtml" method="post">
		<table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>企业:
				</th>
				<td>
				       <input type="hidden" name="id" value="${free.id}"/>
						<select name="tenantId" style="width:100px">
						<option value="${free.tenantId}">${free.tenantName}</option>
						[#list tenant as tenants]
							<option value="${tenants.id}">
								${tenants.name}
							</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					起始时间:
				</th>
				<td>
						<input id="startMonth" type="text" value="${free.startMonth}" name="startMonth" class="text" maxlength="200" readonly onfocus="WdatePicker({dateFmt: 'yyyy-MM-dd', maxDate: '#F{$dp.$D(\'endMonth\')}'});" />
				</td>
			</tr>
			<tr>
				<th>
					结束时间:
				</th>
				<td>
					<input id="endMonth" type="text" name="endMonth" value="${free.endMonth}" class="text" maxlength="200" readonly onfocus="WdatePicker({dateFmt: 'yyyy-MM-dd', minDate: '#F{$dp.$D(\'startMonth\')}'});" />
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