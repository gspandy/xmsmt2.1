<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.productCategory.add")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<style type="text/css">
.productCategorys label {
	width: 150px;
	display: block;
	float: left;
	padding-right: 6px;
}
.tenantCategorys label {
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
	
	var $selectedProductCategoryvalue = $("#selectedProductCategoryvalue");
	var $selectedProductCategory = $("#selectedProductCategory");
	var $selectedAllProductCategory = $("#selectedAllProductCategory");
	var $selectedCleanProductCategory = $("#selectedCleanProductCategory");
	
	var $selectedTenantCategoryvalue = $("#selectedTenantCategoryvalue");
	var $selectedTenantCategory = $("#selectedTenantCategory");
	var $selectedAllTenantCategory = $("#selectedAllTenantCategory");
	var $selectedCleanTenantCategory = $("#selectedCleanTenantCategory");
	
	var $selectedArticleCategoryvalue = $("#selectedArticleCategoryvalue");
	var $selectedArticleCategory = $("#selectedArticleCategory");
	var $selectedAllArticleCategory = $("#selectedAllArticleCategory");
	var $selectedCleanArticleCategory = $("#selectedCleanArticleCategory");
	
	[@flash_message /]
	$browserButton.browser();
	
	function queryLoadProductCategory(productCategoryName, checked) {
		$.ajax({
			url: "${base}/admin/product_category/search.jhtml",
			type: "POST",
			data: {name:productCategoryName},
			dataType: "json",
			cache: false,
			success: function(message) {
				if (message.length > 1) {
					checked = "";
				}
				var $productCategoryIds =$("input[name='productCategoryIds']");
				var $productCategorystd = $(".productCategorys td");
				for (var i = 0; i < message.length; i++) {
					var flag = true;
					$productCategoryIds.each(function() {
						var $this = $(this);
						if ($this.val() == message[i].id) {
							flag = false;
						}
					});
					if (flag) {
						$productCategorystd.append("<label><input type='checkbox' name='productCategoryIds' value='" + message[i].id + "' " + checked + " />" + message[i].name  + "</label>");
					}
				}
			}
		});
	}
	$selectedProductCategory.click(function() {
		if ($selectedProductCategoryvalue.val().replace(/\s/g,"") == "") {
			return;
		} else {
			queryLoadProductCategory($selectedProductCategoryvalue.val(), "checked='checked'");
		}
	});
	$selectedAllProductCategory.click(function() {
		queryLoadProductCategory("", "");
	});
	$selectedCleanProductCategory.click(function() {
		var $label =$(".productCategorys td label");
		$label.each(function() {
			var $this = $(this);
			if(!$this.find("input")[0].checked) {
				$this.remove();
			}
		});
	});
	
	function queryLoadTenantCategory(tenantCategoryName, checked) {
		$.ajax({
			url: "${base}/admin/tenant_category/search.jhtml",
			type: "POST",
			data: {name:tenantCategoryName},
			dataType: "json",
			cache: false,
			success: function(message) {
				if (message.length > 1) {
					checked = "";
				}
				var $tenantCategoryIds =$("input[name='tenantCategoryIds']");
				var $tenantCategorystd = $(".tenantCategorys td");
				for (var i = 0; i < message.length; i++) {
					var flag = true;
					$tenantCategoryIds.each(function() {
						var $this = $(this);
						if ($this.val() == message[i].id) {
							flag = false;
						}
					});
					if (flag) {
						$tenantCategorystd.append("<label><input type='checkbox' name='tenantCategoryIds' value='" + message[i].id + "' " + checked + " />" + message[i].name  + "</label>");
					}
				}
			}
		});
	}
	$selectedTenantCategory.click(function() {
		if ($selectedTenantCategoryvalue.val().replace(/\s/g,"") == "") {
			return;
		} else {
			queryLoadTenantCategory($selectedTenantCategoryvalue.val(), "checked='checked'");
		}
	});
	$selectedAllTenantCategory.click(function() {
		queryLoadTenantCategory("", "");
	});
	$selectedCleanTenantCategory.click(function() {
		var $label =$(".tenantCategorys td label");
		$label.each(function() {
			var $this = $(this);
			if(!$this.find("input")[0].checked) {
				$this.remove();
			}
		});
	});
	
	function queryLoadArticleCategory(articleCategoryName, checked) {
		$.ajax({
			url: "${base}/admin/article_category/search.jhtml",
			type: "POST",
			data: {name:articleCategoryName},
			dataType: "json",
			cache: false,
			success: function(message) {
				if (message.length > 1) {
					checked = "";
				}
				var $articleCategoryIds =$("input[name='articleCategoryIds']");
				var $articleCategorystd = $(".articleCategorys td");
				for (var i = 0; i < message.length; i++) {
					var flag = true;
					$articleCategoryIds.each(function() {
						var $this = $(this);
						if ($this.val() == message[i].id) {
							flag = false;
						}
					});
					if (flag) {
						$articleCategorystd.append("<label><input type='checkbox' name='articleCategoryIds' value='" + message[i].id + "' " + checked + " />" + message[i].name  + "</label>");
					}
				}
			}
		});
	}
	$selectedArticleCategory.click(function() {
		if ($selectedArticleCategoryvalue.val().replace(/\s/g,"") == "") {
			return;
		} else {
			queryLoadArticleCategory($selectedArticleCategoryvalue.val(), "checked='checked'");
		}
	});
	$selectedAllArticleCategory.click(function() {
		queryLoadArticleCategory("", "");
	});
	$selectedCleanArticleCategory.click(function() {
		var $label =$(".ArticleCategorys td label");
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
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 分类频道添加
	</div>
	<form id="inputForm" action="save.jhtml" method="post">
		<table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>名称:
				</th>
				<td>
					<input type="text" id="name" name="name" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					主题图片:
				</th>
				<td>
					<span class="fieldSet">
						<input type="text" name="image" class="text" maxlength="200" title="${message("admin.product.imageTitle")}" />
						<input type="button" id="browserButton" class="button" value="${message("admin.browser.select")}" />
					</span>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>频道描述:
				</th>
				<td>
					<textarea name="description" class="text"></textarea>
				</td>
			</tr>
			<tr>
				<th>
					模版选择:
				</th>
				<td>
					<select name="templateId">
						<option value="" selected="selected">---选择模版---</option>
						[#list templates as template]
							<option value="${template.id}">${template.name}</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					商品分类搜索:
				</th>
				<td>
					<span class="fieldSet">
						<input type="text" id="selectedProductCategoryvalue" class="text" maxlength="200" title="搜索结果并入品牌筛选中" />
						<input type="button" id="selectedProductCategory" class="button" value="查询" />
						<input type="button" id="selectedAllProductCategory" class="button" value="全部" />
						<input type="button" id="selectedCleanProductCategory" class="button" value="清除" />
					</span>
				</td>
			</tr>
			<tr class="productCategorys">
				<th>
					商品分类筛选:
				</th>
				<td></td>
			</tr>
			<tr>
				<th>
					商家分类搜索:
				</th>
				<td>
					<span class="fieldSet">
						<input type="text" id="selectedTenantCategoryvalue" class="text" maxlength="200" title="搜索结果并入品牌筛选中" />
						<input type="button" id="selectedTenantCategory" class="button" value="查询" />
						<input type="button" id="selectedAllTenantCategory" class="button" value="全部" />
						<input type="button" id="selectedCleanTenantCategory" class="button" value="清除" />
					</span>
				</td>
			</tr>
			<tr class="tenantCategorys">
				<th>
					商家分类筛选:
				</th>
				<td></td>
			</tr>
			<tr>
				<th>
					文章分类搜索:
				</th>
				<td>
					<span class="fieldSet">
						<input type="text" id="selectedArticleCategoryvalue" class="text" maxlength="200" title="搜索结果并入品牌筛选中" />
						<input type="button" id="selectedArticleCategory" class="button" value="查询" />
						<input type="button" id="selectedAllArticleCategory" class="button" value="全部" />
						<input type="button" id="selectedCleanArticleCategory" class="button" value="清除" />
					</span>
				</td>
			</tr>
			<tr class="articleCategorys">
				<th>
					文章分类筛选:
				</th>
				<td></td>
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