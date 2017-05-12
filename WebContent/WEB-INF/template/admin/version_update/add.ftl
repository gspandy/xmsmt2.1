<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.product.add")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/editor/kindeditor.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<style type="text/css">
	.specificationSelect {
		height: 100px;
		padding: 5px;
		overflow-y: scroll;
		border: 1px solid #cccccc;
	}
	
	.specificationSelect li {
		float: left;
		min-width: 150px;
		_width: 200px;
	}
</style>
<script type="text/javascript">
$().ready(function() {
	var $inputForm = $("#inputForm");
	// 表单验证
	$inputForm.validate({
		rules: {
 			articleCategoryId: "required",
			"versionNo": {
 				required: true
 			},
 			"versionType": {
 				required: true
 			},
 			"versionDownloadUrl": {
 				required: true
 			},
 		}
	});
});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo;版本更新设置
	</div>
	<form id="inputForm" action="save.jhtml" method="post" enctype="multipart/form-data">
		<table class="input tabContent">
			<tr>
				<th>
					<span class="requiredField">*</span>版本号：
				</th>
				<td>
				  <input type="text" name="versionNo" class="text" maxlength="20" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>手机端：
				</th>
				<td>
					<select name="versionType" style="width:100px">
						<option value="0">安卓版本</option>
						<option value="1">IOS 版本</option>
					</select>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>更新地址：
				</th>
				<td>
					<input type="text" name="versionDownloadUrl" class="text" maxlength="300" />
				</td>
			</tr>
			<tr>
				<th>
					 更新内容：
				</th>
				<td>
					<input type="text" name="versionContent" class="text" maxlength="500" />
				</td>
			</tr>
			<tr>
				<th>
					 默认强制更新：
				</th>
				<td>
					<select name="defaultUpdate" style="width:100px">
						<option value="0" selected="selected">不强制更新</option>
						<option value="1">强制更新</option>
					</select>
				</td>
			</tr>
		</table>
		<table class="input">
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