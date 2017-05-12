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
			"name": {
 				required: true
 			},
 			"tenantType": {
 				required: true
 			},
 			"shortName": {
 				required: true
 			},
 			"address": {
 				required: true
 			},
 			"linkman": {
				required: true
 			},
 			"telephone": {
 				required: true
 			}
 		}
 	});

});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 帮助说明
	</div>
		<form id="inputForm" action="saveDesc.jhtml" method="post" enctype="multipart/form-data">
		   <table class="input">
		   [#list platParam as platParam]
		     [#if platParam.type==3]
			<tr>
				<th>
					${platParam.paramCnName}: 
				</th>
				<td>
				    <input type="hidden" name="id" class="text" value="${platParam.id}"/>
					 [#if platParam.paramValue==null]
				      <textarea rows="5" cols="30" class="text"  name="paramValue">${platParam.defaultValue}</textarea>
				    [#else]
				       <textarea rows="5" cols="30" class="text"  name="paramValue">${platParam.paramValue}</textarea>
					[/#if]
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
				</td>
			</tr>
		</table>
		</form>
		</form>
</body>
</html>
