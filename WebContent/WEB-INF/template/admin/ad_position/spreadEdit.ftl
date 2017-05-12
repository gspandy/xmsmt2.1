<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.adPosition.edit")} - Powered By rsico</title>
<meta name="Author" content="rsico Team" />
<meta name="Copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.autocomplete.js"></script>
<script type="text/javascript" src="${base}/resources/admin/editor/kindeditor.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/admin/datePicker/WdatePicker.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	
	[@flash_message /]

	// 表单验证
	$inputForm.validate({
		rules: {
			name: "required",
			width: {
				required: true,
				min: 1
			},
			height: {
				required: true,
				min: 1
			},
			template: "required"
		}
	});
	
});

	function agree(id){
		$("#adPositionTenantId").val(id);
		$("#submitForm").submit();
	}
	
	function disagree(id){
		$("#adPositionTenantId").val(id);
		$("#submitForm").attr("action","disagree.jhtml");
		$("#submitForm").submit();
	}
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; ${message("admin.adPosition.edit")}
	</div>
	<form id="submitForm" action="agree.jhtml" method="post">
		<input type="hidden" name="adPositionTenantId" id="adPositionTenantId"/>
		<input type="hidden" name="adId" value="${adPosition.id}" />
	</form>
	<form id="inputForm" action="update.jhtml" method="post">
		<input type="hidden" name="id" value="${adPosition.id}" />
		<input type="hidden" name="type" value="${adPosition.type}" />
		<ul id="tab" class="tab">
			<li>
				<input type="button" value="基本信息" />
			</li>
			<li>
				<input type="button" value="商家信息" />
			</li>
		</ul>
		<div class="tabContent">
		<table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>${message("AdPosition.name")}:
				</th>
				<td>
					<input type="text" name="name" class="text" value="${adPosition.name}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("AdPosition.width")}:
				</th>
				<td>
					<input type="text" name="width" class="text" value="${adPosition.width}" maxlength="9" title="${message("admin.adPosition.widthTitle")}" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("AdPosition.height")}:
				</th>
				<td>
					<input type="text" name="height" class="text" value="${adPosition.height}" maxlength="9" title="${message("admin.adPosition.heightTitle")}" />
				</td>
			</tr>
			<tr>
				<th>
					${message("AdPosition.description")}:
				</th>
				<td>
					<input type="text" name="description" class="text" value="${adPosition.description}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					推广信息:
				</th>
				<td>
					<textarea name="spreadmemo" class="text" style="width: 60%; height: 100px;">${adPosition.spreadmemo}</textarea>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("AdPosition.template")}:
				</th>
				<td>
					<textarea name="template" class="text" style="width: 98%; height: 300px;">${adPosition.template?html}</textarea>
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
		</div>
		<div class="tabContent">
			<table id="listTable" class="list">
				<tr>
					<th width="20%">
						<span>商家</span>
					</th>
					<th width="15%">
						<span>申请备注</span>
					</th>
					<th width="15%">
						<span>状态</span>
					</th>
					<th width="15%">
						<span>操作</span>
					</th>
				</tr>
				[#list adPosition.adPositionTenants as adPositionTenant]
					<td>
						${adPositionTenant.tenant.name}
					</td>
					<td>
						${adPositionTenant.memo}
					</td>
					<td>
						${message("admin.adPositionTenant.Status." + adPositionTenant.status)}
					</td>
					<td>
						[#if adPositionTenant.status=="submit"]
							<input type="button" value="同意" onclick="agree('${adPositionTenant.id}')"/>
							<input type="button" value="拒绝" onclick="disagree('${adPositionTenant.id}')"/>
						[/#if]
					</td>
				[/#list]
			</table>
		</div>
	</form>
</body>
</html>