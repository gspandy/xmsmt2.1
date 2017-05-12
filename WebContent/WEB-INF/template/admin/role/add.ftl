<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>商家档案 - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<style type="text/css">
.authorities label {
	min-width: 120px;
	_width: 120px;
	display: block;
	float: left;
	padding-right: 4px;
	_white-space: nowrap;
}
</style>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $selectAll = $("#inputForm .selectAll");
	
	[@flash_message /]
	
	$selectAll.click(function() {
		var $this = $(this);
		var $thisCheckbox = $this.closest("tr").find(":checkbox");
		if ($thisCheckbox.filter(":checked").size() > 0) {
			$thisCheckbox.prop("checked", false);
		} else {
			$thisCheckbox.prop("checked", true);
		}
		return false;
	});
	
	// 表单验证
	$inputForm.validate({
		rules: {
			name: "required",
			authorities: "required"
		}
	});
	
});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo;商家档案
	</div>
	<form id="inputForm" action="save.jhtml" method="post">
		<table class="input">
		[#if allow]
			<tr>
				<th>
					<span class="requiredField">*</span>商家:
				</th>
				<td>
					<select name="tenantId">
							<option value="">【系统】角色</option>
						[#list tenants as tenant]
							<option value="${tenant.id}">${tenant.name}</option>
						[/#list]
					</select>
				</td>
			</tr>
			[#else]
				<input type="hidden" name="tenantId" value="${tenant.id}" />
			[/#if]
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Role.name")}:
				</th>
				<td>
					<input type="text" name="name" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Role.description")}:
				</th>
				<td>
					<input type="text" name="description" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<td colspan="2">
					&nbsp;
				</td>
			</tr>
				[#list map.keySet() as key]
				[#if map.get(key)??&&map.get(key)?has_content]
			<tr class="authorities">
				<th>
					<a href="javascript:;" class="selectAll" title="${message("admin.role.selectAll")}">${message("Authority.AuthorityGroup."+key)}</a>
				</th>
				<td>
					<span class="fieldSet">
						[#list map.get(key) as authority]
						<label>
							<input type="checkbox" name="authorityIds" value="${authority.id}" />${authority.name}
						</label>
						[/#list]
					</span>
				</td>
			</tr>
			[/#if]
			[/#list]
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