<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.product.edit")} - Powered By rsico</title>
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
	var $productCategoryId = $("#productCategoryId");
	var $isMemberPrice = $("#isMemberPrice");
	var $memberPriceTr = $("#memberPriceTr");
	var $memberPrice = $("#memberPriceTr input");
	var $browserButton = $("#browserButton");
	var $productImageTable = $("#productImageTable");
	var $addProductImage = $("#addProductImage");
	var $deleteProductImage = $("a.deleteProductImage");
	var $parameterTable = $("#parameterTable");
	var $specificationIds = $("#specificationSelect :checkbox");
	var $specificationProductTable = $("#specificationProductTable");
	var $addSpecificationProduct = $("#addSpecificationProduct");
	var $deleteSpecificationProduct = $("a.deleteSpecificationProduct");
	var productImageIndex = ${(product.productImages?size)!"0"};
	var previousProductCategoryId = "${product.productCategory.id}";
	
	var $addProductBonus = $("#addProductBonus");
	var $productBonusTable = $("#productBonusTable");
	var $deleteProductBonus = $("a.deleteProductBonus");
	var $addProductPackagUnit = $("#addProductPackagUnit");
	var $productPackagUnitTable = $("#productPackagUnitTable");
	
	var productBonusIndex = ${(product.bonuses?size)!"0"};
	var productPackagUnitIndex =${(product.packagUnits?size)!"0"};
	[@flash_message /]
	
	$browserButton.browser();
	
	// 会员价
	$isMemberPrice.click(function() {
		if ($(this).prop("checked")) {
			$memberPriceTr.show();
			$memberPrice.prop("disabled", false);
		} else {
			$memberPriceTr.hide();
			$memberPrice.prop("disabled", true);
		}
	});
	
	// 增加商品图片
	$addProductImage.click(function() {
		[@compress single_line = true]
			var trHtml = 
			'<tr>
				<td>
					<input type="file" name="productImages[' + productImageIndex + '].file" class="productImageFile" \/>
				<\/td>
				<td>
					<input type="text" name="productImages[' + productImageIndex + '].title" class="text" maxlength="200" \/>
				<\/td>
				<td>
					<input type="text" name="productImages[' + productImageIndex + '].order" class="text productImageOrder" maxlength="9" style="width: 50px;" \/>
				<\/td>
				<td>
					<a href="javascript:;" class="deleteProductImage">[${message("admin.common.delete")}]<\/a>
				<\/td>
			<\/tr>';
		[/@compress]
		$productImageTable.append(trHtml);
		productImageIndex ++;
	});
	
	// 删除商品图片
	$deleteProductImage.live("click", function() {
		var $this = $(this);
		$.dialog({
			type: "warn",
			content: "${message("admin.dialog.deleteConfirm")}",
			onOk: function() {
				$this.closest("tr").remove();
			}
		});
	});
	
	// 修改商品分类
	$productCategoryId.change(function() {
		var hasValue = false;
		if (hasValue) {
			$.dialog({
				type: "warn",
				content: "${message("admin.product.productCategoryChangeConfirm")}",
				width: 450,
				onOk: function() {
					loadParameter();
					loadSpecification();
					previousProductCategoryId = $productCategoryId.val();
				},
				onCancel: function() {
					$productCategoryId.val(previousProductCategoryId);
				}
			});
		} else {
			loadParameter();
			loadSpecification();
			previousProductCategoryId = $productCategoryId.val();
		}
	});
	
	// 加载参数
	function loadParameter() {
		$.ajax({
			url: "parameter_groups.jhtml",
			type: "GET",
			data: {id: $productCategoryId.val()},
			dataType: "json",
			beforeSend: function() {
				$parameterTable.empty();
			},
			success: function(data) {
				var trHtml = "";
				$.each(data, function(i, parameterGroup) {
					trHtml += '<tr><td style="text-align: right;"><strong>' + parameterGroup.name + ':<\/strong><\/td><td>&nbsp;<\/td><\/tr>';
					$.each(parameterGroup.parameters, function(i, parameter) {
						[@compress single_line = true]
							trHtml += 
							'<tr>
								<th>' + parameter.name + ': <\/th>
								<td>
									<input type="text" name="parameter_' + parameter.id + '" class="text" maxlength="200" \/>
								<\/td>
							<\/tr>';
						[/@compress]
					});
				});
				$parameterTable.append(trHtml);
			}
		});
	}
	
	
	// 加载规格
	function loadSpecification() {
		$.ajax({
			url: "specifications.jhtml",
			type: "GET",
			data: {id: $productCategoryId.val()},
			dataType: "json",
			success: function(data) {
				var liHtml = "";
				$.each(data, function(i, specification) {
					liHtml += '<li><label><input name="specificationIds" value="'+specification.id+'" type="checkbox">'+specification.name;
					if(specification.memo!=null&&specification.memo!=""){
						liHtml+='<span class="gray">'+specification.memo+'</span>';
					}
					liHtml+='</label></li>';
				});
				[@compress single_line = true]
					liHtml;
				[/@compress]	
				$("#specificationSelect").find("ul").html(liHtml);
				
			}
		});
	}
	
	// 修改商品规格
	$specificationIds.click(function() {
		if ($specificationIds.filter(":checked").size() == 0) {
			$specificationProductTable.find("tr:gt(1)").remove();
		}
		var $this = $(this);
		if ($this.prop("checked")) {
			$specificationProductTable.find("td.specification_" + $this.val()).show().find("select").prop("disabled", false);
			$specificationProductTable.find("td.stock_" + $this.val()).show();
			$specificationProductTable.find("td.price_" + $this.val()).show();
			$specificationProductTable.find("td.rent_" + $this.val()).show();
		} else {
			$specificationProductTable.find("td.specification_" + $this.val()).hide().find("select").prop("disabled", true);
			$specificationProductTable.find("td.stock_" + $this.val()).hide();
			$specificationProductTable.find("td.price_" + $this.val()).hide();
			$specificationProductTable.find("td.rent_" + $this.val()).hide();
		}
	});
	
	// 增加规格商品
	$addSpecificationProduct.click(function() {
		if ($specificationIds.filter(":checked").size() == 0) {
			$.message("warn", "${message("admin.product.specificationRequired")}");
			return false;
		}
		if ($specificationProductTable.find("tr:gt(1)").size() == 0) {
			$tr = $specificationProductTable.find("tr:eq(1)").clone().show().appendTo($specificationProductTable);
			$tr.find("td:first").text("${message("admin.product.currentSpecification")}");
			$tr.find("td:last").text("-");
		} else {
			$specificationProductTable.find("tr:eq(1)").clone().show().appendTo($specificationProductTable);
		}
	});
	
	// 删除规格商品
	$deleteSpecificationProduct.live("click", function() {
		var $this = $(this);
		$.dialog({
			type: "warn",
			content: "${message("admin.dialog.deleteConfirm")}",
			onOk: function() {
				$this.closest("tr").remove();
			}
		});
	});
	
	$.validator.addClassRules({
		memberPrice: {
			min: 0,
			decimal: {
				integer: 12,
				fraction: ${setting.priceScale}
			}
		},
		productImageFile: {
			required: true,
			extension: "${setting.uploadImageExtension}"
		},
		productImageOrder: {
			digits: true
		},
		perexpression : {
			pattern : /^\d+(.{1}\d+)?$|^\d+(.{1}\d+)?%$/
		},
		valueRequired : {
			required: true
		}
	});
	
	// 表单验证
	$inputForm.validate({
		rules: {
			productCategoryId: "required",
			name: "required",
			sn: {
				pattern: /^[0-9a-zA-Z_-]+$/,
				remote: {
					url: "check_sn.jhtml?previousSn=${product.sn}",
					cache: false
				}
			},
			price: {
				required: true,
				min: 0,
				decimal: {
					integer: 12,
					fraction: ${setting.priceScale}
				}
			},
			cost: {
				min: 0,
				decimal: {
					integer: 12,
					fraction: ${setting.priceScale}
				}
			},
			marketPrice: {
				required: true,
				min: 0,
				decimal: {
					integer: 12,
					fraction: ${setting.priceScale}
				}
			},
			rent: {
				required: true,
				min: 0,
				decimal: {
					integer: 12,
					fraction: ${setting.priceScale}
				}
			},
			weight: "digits",
			stock: "digits",
			point: "digits",
			rentPen:{
				number:true,
				min:0.01,
				max:100,
			}
		},
		messages: {
			sn: {
				pattern: "${message("admin.validate.illegal")}",
				remote: "${message("admin.validate.exist")}"
			}
		},
		submitHandler: function(form) {
			var expressions = $("input[name^='bonuses'][name$='expression']");
			var exprecheck = /^\d+(.{1}\d+)?$|^\d+(.{1}\d+)?%$/;
			for (var i = 0; i < expressions.length; i++) {
				if(!exprecheck.test(expressions[i].value)) {
					return false;
				}
			}
			if ($specificationIds.filter(":checked").size() > 0 && $specificationProductTable.find("tr:gt(1)").size() == 0) {
				$.message("warn", "${message("admin.product.specificationProductRequired")}");
				return false;
			} else {
				var isRepeats = false;
				var parameters = new Array();
				$specificationProductTable.find("tr:gt(1)").each(function() {
					var parameter = $(this).find("select").serialize();
					if ($.inArray(parameter, parameters) >= 0) {
						$.message("warn", "${message("admin.product.specificationValueRepeat")}");
						isRepeats = true;
						return false;
					} else {
						parameters.push(parameter);
					}
				});
				if (!isRepeats) {
					$specificationProductTable.find("tr:eq(1)").find("select").prop("disabled", true);
					addCookie("previousProductCategoryId", $productCategoryId.val(), {expires: 24 * 60 * 60});
					form.submit();
				}
			}
		}
	});
	var $ratio=$("#ratio");
	var $culc=$("#culc");
	var $rentPen=$("input[name='rentPen']");
	$culc.click(function(){
		if($ratio.val()==""){
			$.message("warn","请先输入分享佣金比例!");
			return false;
		}
		var $rentss= $specificationProductTable.find("input[name^='rent_']");
		$rentss.each(function(){
			var _this=$(this);
			var price=_this.closest("tr").find("input[name^='price_']").val();
			if(price!=""&&(/^[0-9]+.?[0-9]*$/.test(price))){
				_this.val(parseInt(parseFloat(price)*parseFloat($rentPen.val())/100));
			}
		});
	});
});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; ${message("admin.product.edit")}
	</div>
	<form id="inputForm" action="update.jhtml" method="post" enctype="multipart/form-data">
		<input type="hidden" name="id" value="${product.id}" />
		<ul id="tab" class="tab">
			<li>
				<input type="button" value="${message("admin.product.base")}" />
			</li>
			<li>
				<input type="button" value="商品主图" />
			</li>
			<li>
				<input type="button" value="详情介绍" />
			</li>
			<li>
				<input type="button" value="${message("admin.product.parameter")}" />
			</li>
			<li>
				<input type="button" value="${message("规格库存")}" />
			</li>
		</ul>
		<table class="input tabContent">
			[#if product.specifications?has_content]
				<tr>
					<th>
						${message("Product.specifications")}:
					</th>
					<td>
						[#list product.specificationValues as specificationValue]
							${specificationValue.name}
						[/#list]
					</td>
				</tr>
			[/#if]
			[#if product.validPromotions?has_content]
				<tr>
					<th>
						${message("Product.promotions")}:
					</th>
					<td>
						[#list product.validPromotions as promotion]
							<p>
								${promotion.name}
								[#if promotion.beginDate?? || promotion.endDate??]
									[${promotion.beginDate} ~ ${promotion.endDate}]
								[/#if]
							</p>
						[/#list]
					</td>
				</tr>
			[/#if]
			<tr>
				<th>
					${message("Product.productCategory")}:
				</th>
				<td>
					<select id="productCategoryId" name="productCategoryId">
						[#list productCategorys as productCategory]
							[#if productCategory.grade == 1]
							<option value="${productCategory.id}"[#if productCategory == product.productCategory] selected="selected"[/#if]>
								${productCategory.name}
							</option>
								[/#if]
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					自定义分类:
				</th>
				<td>
					<select id="productCategoryTenantId" name="productCategoryTenantId">
						[#list productCategoryTenantTree as productCategory]
							<option value="${productCategory.id}"[#if productCategory == product.productCategoryTenant] selected="selected"[/#if]>
								[#if productCategory.grade != 0]
									[#list 1..productCategory.grade as i]
										&nbsp;&nbsp;
									[/#list]
								[/#if]
								${productCategory.name}
							</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Product.name")}:
				</th>
				<td>
					<input type="text" name="name" class="text" value="${product.name}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.sn")}:
				</th>
				<td>
					<input type="text" name="sn" class="text" value="${product.sn}" maxlength="100" title="${message("admin.product.snTitle")}" />
				</td>
			</tr>
			<tr style="display:none;"><!--已被注释-->
				<th>
					<span class="requiredField">*</span>内购价:
				</th>
				<td>
					<input type="text" name="price" class="text" value="${product.price}" maxlength="16" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>电商价类型:
				</th>
				<td>
					<select name="priceType">
						<option value="">请选择。。</option>
						[#list priceTypes as priceType]
							<option value="${priceType}" [#if product.priceType==priceType]selected[/#if]>
								${message("Product.PriceType."+priceType)}
							</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					电商价:
				</th>
				<td>
					<input type="text" name="ePrice" class="text" maxlength="16" value="${product.ePrice}" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Product.marketPrice")}:
				</th>
				<td>
					<input type="text" name="marketPrice" class="text" value="${product.marketPrice}" maxlength="16"  />
				</td>
			</tr>
			<!--<tr>
				<th>
					<span class="requiredField">*</span>分享佣金:
				</th>
				<td>
					<input type="text" name="rent" class="text" maxlength="16" value="${product.rent}"/>
				</td>
			</tr>-->
			<tr>
				<th>
					${message("Product.image")}:
				</th>
				<td>
					<span class="fieldSet">
						<input type="text" name="image" class="text" value="${product.image}" maxlength="200" title="${message("admin.product.imageTitle")}" />
						<input type="button" id="browserButton" class="button" value="${message("admin.browser.select")}" />
						[#if product.image??]
							<a href="${product.image}" target="_blank">${message("admin.common.view")}</a>
						[/#if]
					</span>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Product.unit")}:
				</th>
				<td>
					<input type="text" name="unit" class="text valueRequired" maxlength="200" value="${product.unit}" />
				</td>
			</tr>
			<!--<tr>
				<th>
					${message("Product.weight")}:
				</th>
				<td>
					<input type="text" name="weight" class="text" value="${product.weight}" maxlength="9" title="${message("admin.product.weightTitle")}" />
				</td>
			</tr>
			<tr>
				<th>
					条形码:
				</th>
				<td>
					<input type="text" name="barcode" class="text" value="${product.barcode}" maxlength="128" title="条形码" />
				</td>
			</tr>
			
			<tr>
				<th>
					${message("Product.stock")}:
				</th>
				<td>
					<input type="text" name="stock" class="text" value="${product.stock}" maxlength="9" title="${message("admin.product.stockTitle")}" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.stockMemo")}:
				</th>
				<td>
					<input type="text" name="stockMemo" class="text" value="${product.stockMemo}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.point")}:
				</th>
				<td>
					<input type="text" name="point" class="text" value="${product.point}" maxlength="9" title="${message("admin.product.pointTitle")}" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.tags")}:
				</th>
				<td>
					[#list tags as tag]
						<label>
							<input type="checkbox" name="tagIds" value="${tag.id}"[#if product.tags?seq_contains(tag)] checked="checked"[/#if] />${tag.name}
						</label>
					[/#list]
				</td>
			</tr>-->
			<tr>
				<th>
					${message("Product.brand")}:
				</th>
				<td>
					<select name="brandId">
						<option value="">${message("admin.common.choose")}</option>
						[#list brands as brand]
							<option value="${brand.id}"[#if brand == product.brand] selected="selected"[/#if]>
								${brand.name}
							</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.common.setting")}:
				</th>
				<td>
					<label>
						<input type="checkbox" name="isMarketable" value="true"[#if product.isMarketable] checked="checked"[/#if] />${message("Product.isMarketable")}
						<input type="hidden" name="_isMarketable" value="false" />
					</label>
					<label>
						<input type="checkbox" name="isList" value="true"[#if product.isList] checked="checked"[/#if] />${message("Product.isList")}
						<input type="hidden" name="_isList" value="false" />
					</label>
					<label style="display:none;"><!--已被注释-->
						<input type="checkbox" name="isTop" value="true"[#if product.isTop] checked="checked"[/#if] />${message("Product.isTop")}
						<input type="hidden" name="_isTop" value="false" />
					</label>
					<label>
						<input type="checkbox" name="isGift" value="true"[#if product.isGift] checked="checked"[/#if] />是否推荐
						<input type="hidden" name="_isGift" value="false" />
					</label>
				</td>
			</tr>
			<!--<tr>
				<th>
					${message("Product.memo")}:
				</th>
				<td>
					<input type="text" name="memo" class="text" value="${product.memo}" maxlength="200" />
				</td>
			</tr>-->
			<tr>
				<th>
					排序:
				</th>
				<td>
					<input type="text" name="sort" class="text" value="${product.sort}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.keyword")}:
				</th>
				<td>
					<input type="text" name="keyword" class="text" value="${product.keyword}" maxlength="200" title="${message("admin.product.keywordTitle")}" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.seoTitle")}:
				</th>
				<td>
					<input type="text" name="seoTitle" class="text" value="${product.seoTitle}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.seoKeywords")}:
				</th>
				<td>
					<input type="text" name="seoKeywords" class="text" value="${product.seoKeywords}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.seoDescription")}:
				</th>
				<td>
					<input type="text" name="seoDescription" class="text" value="${product.seoDescription}" maxlength="200" />
				</td>
			</tr>
			
		</table>
		<table class="input tabContent">
			<tr>
				<td>
					<textarea id="editor" name="introduction" class="editor" style="width: 100%;">${product.introduction?html}</textarea>
				</td>
			</tr>
		</table>
		<table id="productImageTable" class="input tabContent">
			<tr>
				<td colspan="4">
					<a href="javascript:;" id="addProductImage" class="button">${message("admin.product.addProductImage")}</a>
				</td>
			</tr>
			<tr class="title">
				<th>
					${message("ProductImage.file")}
				</th>
				<th>
					${message("ProductImage.title")}
				</th>
				<th>
					${message("admin.common.order")}
				</th>
				<th>
					${message("admin.common.delete")}
				</th>
			</tr>
			[#list product.productImages as productImage]
				<tr>
					<td>
						<input type="hidden" name="productImages[${productImage_index}].source" value="${productImage.source}" />
						<input type="hidden" name="productImages[${productImage_index}].large" value="${productImage.large}" />
						<input type="hidden" name="productImages[${productImage_index}].medium" value="${productImage.medium}" />
						<input type="hidden" name="productImages[${productImage_index}].thumbnail" value="${productImage.thumbnail}" />
						
						<input type="hidden" name="productImages[${productImage_index}].sourceWidth" value="${productImage.sourceWidth}" />
						<input type="hidden" name="productImages[${productImage_index}].sourceHeight" value="${productImage.sourceHeight}" />
						<input type="hidden" name="productImages[${productImage_index}].largeWidth" value="${productImage.largeWidth}" />
						<input type="hidden" name="productImages[${productImage_index}].largeHeight" value="${productImage.largeHeight}" />
						<input type="hidden" name="productImages[${productImage_index}].mediumWidth" value="${productImage.mediumWidth}" />
						<input type="hidden" name="productImages[${productImage_index}].mediumHeight" value="${productImage.mediumHeight}" />
						<input type="hidden" name="productImages[${productImage_index}].thumbnailWidth" value="${productImage.thumbnailWidth}" />
						<input type="hidden" name="productImages[${productImage_index}].thumbnailHeight" value="${productImage.thumbnailHeight}" />
						
						
						<input type="file" name="productImages[${productImage_index}].file" class="productImageFile ignore" />
						<a href="${productImage.large}" target="_blank">${message("admin.common.view")}</a>
					</td>
					<td>
						<input type="text" name="productImages[${productImage_index}].title" class="text" maxlength="200" value="${productImage.title}" />
					</td>
					<td>
						<input type="text" name="productImages[${productImage_index}].order" class="text productImageOrder" value="${productImage.order}" maxlength="9" style="width: 50px;" />
					</td>
					<td>
						<a href="javascript:;" class="deleteProductImage">[${message("admin.common.delete")}]</a>
					</td>
				</tr>
			[/#list]
		</table>
		<table id="parameterTable" class="input tabContent">
			[#list product.productCategory.parameterGroups as parameterGroup]
				<tr>
					<td style="text-align: right; padding-right: 10px;">
						<strong>${parameterGroup.name}:</strong>
					</td>
					<td>
						&nbsp;
					</td>
				</tr>
				[#list parameterGroup.parameters as parameter]
					<tr>
						<th>${parameter.name}:</th>
						<td>
							<input type="text" name="parameter_${parameter.id}" class="text" value="${product.parameterValue.get(parameter)}" maxlength="200" />
						</td>
					</tr>
				[/#list]
			[/#list]
		</table>
		<table class="input tabContent">
			<tr class="title">
				<th>
					${message("admin.product.selectSpecification")}:
				</th>
			</tr>
			<tr>
				<td>
					<div id="specificationSelect" class="specificationSelect">
						<ul>
							[#list specifications as specification]
								<li>
									<label>
										<input type="checkbox" name="specificationIds" value="${specification.id}"[#if product.specifications?seq_contains(specification)] checked="checked"[/#if] />${specification.name}
										[#if specification.memo??]
											<span class="gray">[${specification.memo}]</span>
										[/#if]
									</label>
								</li>
							[/#list]
						</ul>
					</div>
				</td>
			</tr>
			<tr>
				<td>
					<a href="javascript:;" id="addSpecificationProduct" class="button">${message("admin.product.addSpecificationProduct")}</a>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					分享佣金比例：<input type="text" class="text" name="rentPen"  id="ratio" style="width:50px"/>%
					<a href="javascript:;" id="culc" class="button" style="float:none;">计算</a>
				</td>
			</tr>
			<tr>
				<td>
					<table id="specificationProductTable" class="input">
						<tr class="title">
							<td width="60">
								&nbsp;
							</td>
							[#list specifications as specification]
								<td class="specification_${specification.id}[#if !product.specifications?seq_contains(specification)] hidden[/#if]">
									${specification.name}
									[#if specification.memo??]
										<span class="gray">[${specification.memo}]</span>
									[/#if]
								</td>
							[/#list]
							<td>
								库存
							</td>
							<td>
								内购价
							</td>
							<td>
								分享佣金
							</td>
							
							<td>
								${message("admin.common.handle")}
							</td>
						</tr>
						<tr class="hidden">
							<td>
								&nbsp;
							</td>
							[#list specifications as specification]
								<td class="specification_${specification.id}[#if !product.specifications?seq_contains(specification)] hidden[/#if]">
									<select name="specification_${specification.id}"[#if !product.specifications?seq_contains(specification)] disabled="disabled"[/#if]>
										[#list specification.specificationValues as specificationValue]
											<option value="${specificationValue.id}">${specificationValue.name}</option>
										[/#list]
									</select>
								</td>
								[#if !specification_has_next]
							<td class="stock_${specification.id} [#if !product.specifications?seq_contains(specification)] hidden[/#if]">
								<input type="text" name="stock_${specification.id}" value="${product.stock}" />
							</td>
							<td class="price_${specification.id} [#if !product.specifications?seq_contains(specification)] hidden[/#if]">
								<input type="text" name="price_${specification.id}" value="${product.price}"/>
							</td>
							<td class="rent_${specification.id} [#if !product.specifications?seq_contains(specification)] hidden[/#if]">
								<input type="text" name="rent_${specification.id}" value="${product.rent}"/>
							</td>
								[/#if]
							[/#list]
							<td>
								<a href="javascript:;" class="deleteSpecificationProduct">[${message("admin.common.delete")}]</a>
							</td>
						</tr>
						[#if product.specifications?has_content]
							<tr>
								<td>
									${message("admin.product.currentSpecification")}
									<input type="hidden" name="specificationProductIds" value="${product.id}" />
								</td>
								[#list specifications as specification]
									<td class="specification_${specification.id}[#if !product.specifications?seq_contains(specification)] hidden[/#if]">
										<select name="specification_${specification.id}"[#if !product.specifications?seq_contains(specification)] disabled="disabled"[/#if]>
											[#list specification.specificationValues as specificationValue]
												<option value="${specificationValue.id}"[#if product.specificationValues?seq_contains(specificationValue)] selected="selected"[/#if]>${specificationValue.name}</option>
											[/#list]
										</select>
									</td>
								[#if !specification_has_next]
							<td class="stock_${specification.id} }[#if !product.specifications?seq_contains(specification)] hidden[/#if]">
								<input type="text" name="stock_${specification.id}" value="${product.stock}" />
							</td>
							<td class="price_${specification.id} [#if !product.specifications?seq_contains(specification)] hidden[/#if]">
								<input type="text" name="price_${specification.id}" value="${product.price}"/>
							</td>
							<td class="rent_${specification.id} [#if !product.specifications?seq_contains(specification)] hidden[/#if]">
								<input type="text" name="rent_${specification.id}" value="${product.rent}"/>
							</td>
								[/#if]
								[/#list]
								
								<td>
									-
								</td>
							</tr>
						[/#if]
						[#list product.siblings as specificationProduct]
							<tr>
								<td>
									&nbsp;
									<input type="hidden" name="specificationProductIds" value="${specificationProduct.id}" />
								</td>
								[#list specifications as specification]
									<td class="specification_${specification.id}[#if !specificationProduct.specifications?seq_contains(specification)] hidden[/#if]">
										<select name="specification_${specification.id}"[#if !specificationProduct.specifications?seq_contains(specification)] disabled="disabled"[/#if]>
											[#list specification.specificationValues as specificationValue]
												<option value="${specificationValue.id}"[#if specificationProduct.specificationValues?seq_contains(specificationValue)] selected="selected"[/#if]>${specificationValue.name}</option>
											[/#list]
										</select>
									</td>
								[#if !specification_has_next]
							<td class="stock_${specification.id} [#if !specificationProduct.specifications?seq_contains(specification)] hidden[/#if]">
								<input type="text" name="stock_${specification.id}" value="${specificationProduct.stock}" />
							</td>
							<td class="price_${specification.id} [#if !specificationProduct.specifications?seq_contains(specification)] hidden[/#if]">
								<input type="text" name="price_${specification.id}" value="${specificationProduct.price}"/>
							</td>
							<td class="rent_${specification.id} [#if !specificationProduct.specifications?seq_contains(specification)] hidden[/#if]">
								<input type="text" name="rent_${specification.id}" value="${specificationProduct.rent}"/>
							</td>
								[/#if]
								[/#list]
								
								
								<td>
									<a href="javascript:;" class="deleteSpecificationProduct">[${message("admin.common.delete")}]</a>
									<a href="edit.jhtml?id=${specificationProduct.id}">[${message("admin.common.edit")}]</a>
								</td>
							</tr>
						[/#list]
					</table>
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