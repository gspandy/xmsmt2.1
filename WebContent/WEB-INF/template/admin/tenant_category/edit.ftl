<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.tenantCategory.edit")} - Powered By rsico</title>
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
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; ${message("admin.tenantCategory.edit")}
	</div>
	<form id="inputForm" action="update.jhtml" method="post">
		<input type="hidden" name="id" value="${tenantCategory.id}" />
		<table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>${message("TenantCategory.name")}:
				</th>
				<td>
					<input type="text" id="name" name="name" class="text" value="${tenantCategory.name}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("TenantCategory.parent")}:
				</th>
				<td>
					<select name="parentId">
						<option value="">${message("admin.tenantCategory.root")}</option>
						[#list tenantCategoryTree as category]
							[#if category != tenantCategory && !children?seq_contains(category)]
								<option value="${category.id}"[#if category == tenantCategory.parent] selected="selected"[/#if]>
									[#if category.grade != 0]
										[#list 1..category.grade as i]
											&nbsp;&nbsp;
										[/#list]
									[/#if]
									${category.name}
								</option>
							[/#if]
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					缩例图:
				</th>
				<td>
					<span class="fieldSet">
						<input type="text" name="image" class="text" value="${tenantCategory.image}" maxlength="200" title="缩例图" />
						<input type="button" id="browserButton" class="button" value="${message("admin.browser.select")}" />
						[#if tenantCategory.image??]
							<a href="${tenantCategory.image}" target="_blank">${message("admin.common.view")}</a>
						[/#if]
					</span>
				</td>
			</tr>
			<tr>
				<th>
					${message("TenantCategory.seoTitle")}:
				</th>
				<td>
					<input type="text" name="seoTitle" class="text" value="${tenantCategory.seoTitle}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("TenantCategory.seoKeywords")}:
				</th>
				<td>
					<input type="text" name="seoKeywords" class="text" value="${tenantCategory.seoKeywords}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("TenantCategory.seoDescription")}:
				</th>
				<td>
					<input type="text" name="seoDescription" class="text" value="${tenantCategory.seoDescription}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.common.order")}:
				</th>
				<td>
					<input type="text" name="order" class="text" value="${tenantCategory.order}" maxlength="9" />
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