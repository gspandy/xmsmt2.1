<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.productCategory.edit")} - Powered By rsico</title>
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
	
	var $selectedbrandvalue = $("#selectedbrandvalue");
	var $selectedbrand = $("#selectedbrand");
	var $selectedAllbrand = $("#selectedAllbrand");
	var $selectedCleanbrand = $("#selectedCleanbrand");
	
	[@flash_message /]
	$browserButton.browser();
	
	function queryLoadBrand(brandName, checked) {
		$.ajax({
			url: "${base}/admin/brand/search.jhtml",
			type: "POST",
			data: {name:brandName},
			dataType: "json",
			cache: false,
			success: function(message) {
				if (message.length > 1) {
					checked = "";
				}
				var $brandIds =$("input[name='brandIds']");
				var $brandstd = $(".brands td");
				for (var i = 0; i < message.length; i++) {
					var flag = true;
					$brandIds.each(function() {
						var $this = $(this);
						if ($this.val() == message[i].id) {
							flag = false;
						}
					});
					if (flag) {
						$brandstd.append("<label><input type='checkbox' name='brandIds' value='" + message[i].id + "' " + checked + " />" + message[i].name  + "</label>");
					}
				}
			}
		});
	}
	
	$selectedbrand.click(function() {
		if ($selectedbrandvalue.val().replace(/\s/g,"") == "") {
			return;
		} else {
			queryLoadBrand($selectedbrandvalue.val(), "checked='checked'");
		}
	});
	$selectedAllbrand.click(function() {
		queryLoadBrand("", "");
	});
	$selectedCleanbrand.click(function() {
		var $label =$(".brands td label");
		$label.each(function() {
			var $this = $(this);
			if(!$this.find("input")[0].checked) {
				$this.remove();
			}
		});
	});
	
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
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; ${message("admin.productCategory.edit")}
	</div>
	<form id="inputForm" action="update.jhtml" method="post">
		<input type="hidden" name="id" value="${productCategory.id}" />
		<table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>${message("ProductCategory.name")}:
				</th>
				<td>
					<input type="text" id="name" name="name" class="text" value="${productCategory.name}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("ProductCategory.parent")}:
				</th>
				<td>
					[#if children??&&children?has_content]
					<input type="hidden" name="parentId" value="[#if productCategory.parent??]${productCategory.parent.id}[/#if]" />
					${(productCategory.parent.name)!}
					[#else]
					<select name="parentId">
						<option value="">${message("admin.productCategory.root")}</option>
						[#list productCategoryTree as category]
							[#if category != productCategory && !children?seq_contains(category)]
								<option value="${category.id}"[#if category == productCategory.parent] selected="selected"[/#if]>
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
					[/#if]
				</td>
			</tr>
			<tr>
				<th>
					品牌搜索:
				</th>
				<td>
					<span class="fieldSet">
						<input type="text" id="selectedbrandvalue" class="text" maxlength="200" title="搜索结果并入品牌筛选中" />
						<input type="button" id="selectedbrand" class="button" value="查询" />
						<input type="button" id="selectedAllbrand" class="button" value="全部" />
						<input type="button" id="selectedCleanbrand" class="button" value="清除" />
					</span>
				</td>
			</tr>
			<tr class="brands">
				<th>
					${message("ProductCategory.brands")}:
				</th>
				<td>
					[#list productCategory.brands as brand]
						<label>
							<input type="checkbox" name="brandIds" value="${brand.id}" checked="checked" />${brand.name}
						</label>
					[/#list]
				</td>
			</tr>
			<tr>
				<th>
					缩例图:
				</th>
				<td>
					<span class="fieldSet">
						<input type="text" name="image" class="text" value="${productCategory.image}" maxlength="200" title="${message("admin.product.imageTitle")}" />
						<input type="button" id="browserButton" class="button" value="${message("admin.browser.select")}" />
						[#if productCategory.image??]
							<a href="${productCategory.image}" target="_blank">${message("admin.common.view")}</a>
						[/#if]
					</span>
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.tags")}:
				</th>
				<td>
					[#list tags as tag]
						<label>
							<input type="checkbox" name="tagIds" value="${tag.id}"[#if productCategory.tags?seq_contains(tag)] checked="checked"[/#if] />${tag.name}
						</label>
					[/#list]
				</td>
			</tr>
			<tr>
				<th>
					${message("ProductCategory.seoTitle")}:
				</th>
				<td>
					<input type="text" name="seoTitle" class="text" value="${productCategory.seoTitle}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("ProductCategory.seoKeywords")}:
				</th>
				<td>
					<input type="text" name="seoKeywords" class="text" value="${productCategory.seoKeywords}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("ProductCategory.seoDescription")}:
				</th>
				<td>
					<input type="text" name="seoDescription" class="text" value="${productCategory.seoDescription}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.common.order")}:
				</th>
				<td>
					<input type="text" name="order" class="text" value="${productCategory.order}" maxlength="9" />
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