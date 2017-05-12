<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.admin.add")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $areaId = $("#areaId");
	[@flash_message /]
	
		// 地区选择
	$areaId.lSelect({
		url: "${base}/common/area.jhtml"
	});

	// 表单验证
	$inputForm.validate({
		rules: {
			areaId: "required",
			providers: "required",
			denomination: "required",
			price: "required",
			cost: "required"
		}
	});
	
});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 手机快充价格表
	</div>
	<form id="inputForm" action="update.jhtml" method="post">
		<input type="hidden" name="id" value="${(mobilePrice.id)!}" />
		<ul id="tab" class="tab">
			<li>
				<label>手机快充价格表</label>
			</li>
		</ul>
		<table class="input tabContent">
			<tr>
				<th>
					<span class="requiredField">*</span>区域:
				</th>
				<td>
					<span class="fieldSet">
						<input type="hidden" id="areaId" name="areaId" value="${(mobilePrice.area.id)!}" treePath="${(mobilePrice.area.treePath)!}" />
					</span>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>运营商:
				</th>
				<td>
									<select name="providers">
										<option value="">--请选择--</option>
										<option value="1" [#if (1 == (mobilePrice.providers)!)] selected[/#if] >中国移动</option>
										<option value="2" [#if (2 == (mobilePrice.providers)!)] selected[/#if] >中国联通</option>
										<option value="3" [#if (3 == (mobilePrice.providers)!)] selected[/#if] >中国电信</option>
									</select>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>面值:
				</th>
				<td>
					<input type="text" name="denomination" class="text"  value="${(mobilePrice.denomination)!}" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>进价:
				</th>
				<td>
					<input type="text" name="cost" class="text"  value="${(mobilePrice.cost)!}" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>售价:
				</th>
				<td>
					<input type="text" name="price" class="text"  value="${(mobilePrice.price)!}" />
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