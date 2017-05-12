<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.promotion.edit")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.autocomplete.js"></script>
<script type="text/javascript" src="${base}/resources/admin/editor/kindeditor.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/admin/datePicker/WdatePicker.js"></script>
<style type="text/css">
.memberRank label, .productCategory label, .brand label, .coupon label {
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
	var $productTable = $("#productTable");
	var $productSelect = $("#productSelect");
	var $deleteProduct = $("a.deleteProduct");
	var $productTitle = $("#productTitle");
	var $giftTable = $("#giftTable");
	var $giftSelect = $("#giftSelect");
	var $deleteGift = $("a.deleteGift");
	var $giftTitle = $("#giftTitle");
	var productIds = [#if promotion.promotionProducts?has_content][[#list promotion.promotionProducts as promoteproduct]${promoteproduct.product.id}[#if promoteproduct_has_next], [/#if][/#list]][#else]new Array()[/#if];
	var promoteproductIndex = ${promotion.promotionProducts?size};
	var giftIds = [#if promotion.giftItems?has_content][[#list promotion.giftItems as giftItem]${giftItem.gift.id}[#if giftItem_has_next], [/#if][/#list]][#else]new Array()[/#if];
	var giftItemIndex = ${promotion.giftItems?size};
	[@flash_message /]
	// 商品选择
	$productSelect.autocomplete("product_select.jhtml", {
		dataType: "json",
		max: 20,
		width: 600,
		scrollHeight: 300,
		parse: function(data) {
			return $.map(data, function(item) {
				return {
					data: item,
					value: item.fullName
				}
			});
		},
		formatItem: function(item) {
			if ($.inArray(item.id, productIds) < 0) {
				return '<span title="' + item.fullName + '">' + item.fullName.substring(0, 50) + '<\/span>';
			} else {
				return false;
			}
		}
	}).result(function(event, item) {
		[@compress single_line = true]
			var trHtml = 
			'<tr class="productTr">
				<th>
					<input type="hidden" name="promotionProducts[' + promoteproductIndex + '].id" \/>
					<input type="hidden" name="promotionProducts[' + promoteproductIndex + '].product.id" value="' + item.id + '" \/>
					&nbsp;
				<\/th>
				<td>
					<span title="' + item.fullName + '">' + item.fullName.substring(0, 50) + '<\/span>
				<\/td>
				<td>
					<input type="text" name="promotionProducts[' + promoteproductIndex + '].quantity" style="width:50px" value="1" \/>
				<\/td>
				<td>
					<input type="text" name="promotionProducts[' + promoteproductIndex + '].price" style="width:50px" value="' + item.price + '" \/>
				<\/td>
				<td>
					<a href="${base}' + item.path + '" target="_blank">[${message("admin.common.view")}]<\/a>
					<a href="javascript:;" class="deleteProduct">[${message("admin.common.delete")}]<\/a>
				<\/td>
			<\/tr>';
		[/@compress]
		$productTitle.show();
		$productTable.append(trHtml);
		productIds.push(item.id);
		promoteproductIndex++;
	});
	// 删除商品
	$deleteProduct.live("click", function() {
		var $this = $(this);
		$.dialog({
			type: "warn",
			content: "${message("admin.dialog.deleteConfirm")}",
			onOk: function() {
				var id = parseInt($this.closest("tr").find("input:hidden")[1].value);
				productIds = $.grep(productIds, function(n, i) {
					return n != id;
				});
				$this.closest("tr").remove();
				if ($productTable.find("tr.productTr").size() <= 0) {
					$productTitle.hide();
				}
			}
		});
	});
	// 赠品选择
	$giftSelect.autocomplete("gift_select.jhtml", {
		dataType: "json",
		max: 20,
		width: 600,
		scrollHeight: 300,
		parse: function(data) {
			return $.map(data, function(item) {
				return {
					data: item,
					value: item.fullName
				}
			});
		},
		formatItem: function(item) {
			if ($.inArray(item.id, giftIds) < 0) {
				return '<span title="' + item.fullName + '">' + item.fullName.substring(0, 50) + '<\/span>';
			} else {
				return false;
			}
		}
	}).result(function(event, item) {
		[@compress single_line = true]
			var trHtml = 
			'<tr class="giftTr">
				<th>
					<input type="hidden" name="giftItems[' + giftItemIndex + '].gift.id" class="giftId" value="' + item.id + '" \/>&nbsp;
				<\/th>
				<td>
					<span title="' + item.fullName + '">' + item.fullName.substring(0, 50) + '<\/span>
				<\/td>
				<td>
					<input type="text" name="giftItems[' + giftItemIndex + '].quantity" class="text giftItemQuantity" value="1" maxlength="9" style="width: 30px;" \/>
				<\/td>
				<td>
					<a href="${base}' + item.path + '" target="_blank">[${message("admin.common.view")}]<\/a>
					<a href="javascript:;" class="deleteGift">[${message("admin.common.delete")}]<\/a>
				<\/td>
			<\/tr>';
		[/@compress]
		$giftTitle.show();
		$giftTable.append(trHtml);
		giftIds.push(item.id);
		giftItemIndex ++;
	});
	// 删除赠品
	$deleteGift.live("click", function() {
		var $this = $(this);
		$.dialog({
			type: "warn",
			content: "${message("admin.dialog.deleteConfirm")}",
			onOk: function() {
				var id = parseInt($this.closest("tr").find("input.giftId").val());
				giftIds = $.grep(giftIds, function(n, i) {
					return n != id;
				});
				$this.closest("tr").remove();
				if ($giftTable.find("tr.giftTr").size() <= 0) {
					$giftTitle.hide();
				}
			}
		});
	});
	$.validator.addMethod("compare", 
		function(value, element, param) {
			var parameterValue = $(param).val();
			if ($.trim(parameterValue) == "" || $.trim(value) == "") {
				return true;
			}
			try {
				return parseFloat(parameterValue) <= parseFloat(value);
			} catch(e) {
				return false;
			}
		},
		"${message("admin.promotion.compare")}"
	);
	$.validator.addClassRules({
		giftItemQuantity: {
			required: true,
			integer: true,
			min: 1
		}
	});
	// 表单验证
	$inputForm.validate({
		rules: {
			name: "required",
			title: "required",
			minimumQuantity: "digits",
			maximumQuantity: {
				digits: true,
				compare: "#minimumQuantity"
			},
			pointExpression: {
				remote: {
					url: "check_point_expression.jhtml",
					cache: false
				}
			},
			order: "digits"
		}
	});
});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; ${message("admin.promotion.groupon.edit")}
	</div>
	<form id="inputForm" action="update.jhtml" method="post">
		<input type="hidden" name="id" value="${promotion.id}" />
		<input type="hidden" name="type" value="${promotion.type}" />
		<input type="hidden" name="classify" value="${promotion.classify}" />
		<input type="hidden" name="status" value="${promotion.status}" />
		<ul id="tab" class="tab">
			<li>
				<input type="button" value="${message("admin.promotion.base")}" />
			</li>
		</ul>
		<div class="tabContent">
			<table class="input">
				<tr>
					<th>
						<span class="requiredField">*</span>类型:
					</th>
					<td>
						${message("Promotion.Classify." + promotion.classify)}
					</td>
				</tr>
				<tr>
					<th>
						<span class="requiredField">*</span>名称:
					</th>
					<td>
						<input type="text" name="name" class="text requiredField" value="${promotion.name}" maxlength="200" />
					</td>
				</tr>
				<tr>
					<th>
						<span class="requiredField">*</span>标题:
					</th>
					<td>
						<textarea name="title" class="text requiredField">${promotion.title}</textarea>
					</td>
				</tr>
				<tr>
					<th>
						<span class="requiredField">*</span>开团起始时间:
					</th>
					<td>
						<input type="text" id="beginDate" name="beginDate" class="text Wdate requiredField" value="[#if promotion.beginDate??]${promotion.beginDate?string("yyyy-MM-dd HH:mm:ss")}[/#if]" onfocus="WdatePicker({dateFmt: 'yyyy-MM-dd HH:mm:ss', maxDate: '#F{$dp.$D(\'endDate\')}'});" />
					</td>
				</tr>
				<tr>
					<th>
						<span class="requiredField">*</span>团购结束时间:
					</th>
					<td>
						<input type="text" id="endDate" name="endDate" class="text Wdate requiredField" value="[#if promotion.endDate??]${promotion.endDate?string("yyyy-MM-dd HH:mm:ss")}[/#if]" onfocus="WdatePicker({dateFmt: 'yyyy-MM-dd HH:mm:ss', minDate: '#F{$dp.$D(\'beginDate\')}'});" />
					</td>
				</tr>
				<tr>
					<th>
						<span class="requiredField">*</span>开团最低人数:
					</th>
					<td>
						<input type="text" id="minimumQuantity" name="minimumQuantity" class="text requiredField" value="${promotion.minimumQuantity}" maxlength="9" title="满足开团的最低订购数量" />
					</td>
				</tr>
				<tr>
					<th>
						团购人数上限:
					</th>
					<td>
						<input type="text" name="maximumQuantity" class="text" value="${promotion.maximumQuantity}" maxlength="9" title="未填写,则无上限要求"/>
					</td>
				</tr>
				<tr>
					<th>
						附赠积分计算:
					</th>
					<td>
						<input type="text" name="pointExpression" class="text" value="${promotion.pointExpression}" maxlength="255" title="可用变量：point" />
					</td>
				</tr>
				<tr>
					<th>
						排序:
					</th>
					<td>
						<input type="text" name="order" class="text" value="${promotion.order}" maxlength="9" />
					</td>
				</tr>
				<tr>
					<th>
						配送方式:
					</th>
					<td>
						<select name="shippingMethod.id" style="width:190px;">
							[#list shippingMethods as shippingMethod]
								<option value="${shippingMethod.id}" [#if promotion.shippingMethod?? && promotion.shippingMethod.id == shippingMethod.id]selected="selected"[/#if]>${shippingMethod.name}</option>
							[/#list]
						</select>
					</td>
				</tr>
				[#if promotion.brokerage??]
				<tr>
					<th>
						分享佣金比率:
					</th>
					<td>
						<input type="hidden" name="brokerage" value="${promotion.brokerage}" />${promotion.brokerage}
					</td>
				</tr>
				[/#if]
				[#if promotion.tenant??]
				<tr>
					<th>
						开团商家:
					</th>
					<td>
						<input type="hidden" name="tenant.id" value="${promotion.tenant.id}" />${promotion.tenant.name}
					</td>
				</tr>
				[/#if]
				[#if promotion.member??]
				<tr>
					<th>
						申请会员:
					</th>
					<td>
						<input type="hidden" name="member.id" value="${promotion.member.id}" />${promotion.member.username}
					</td>
				</tr>
				[/#if]
			</table>
			<table id="productTable" class="input">
				<tr>
					<th>
						${message("Promotion.products")}:
					</th>
					<td  colspan="4">
						<input type="text" id="productSelect" name="productSelect" class="text" maxlength="200" title="${message("admin.promotion.productSelectTitle")}" />
					</td>
				</tr>
				<tr id="productTitle" class="title[#if !promotion.promotionProducts?has_content] hidden[/#if]">
					<th>
						&nbsp;
					</th>
					<td>
						${message("Product.name")}
					</td>
					<td>
						数量
					</td>
					<td>
						官网价格
					</td>
					<td>
						${message("admin.common.handle")}
					</td>
				</tr>
				[#list promotion.promotionProducts as promoteproduct]
					<tr class="productTr">
						<th>
							<input type="hidden" name="promotionProducts[${promoteproduct_index}].id" value="${promoteproduct.id}" />
							<input type="hidden" name="promotionProducts[${promoteproduct_index}].product.id" value="${promoteproduct.product.id}" />
							&nbsp;
						</th>
						<td>
							<span title="${promoteproduct.product.fullName}">${abbreviate(promoteproduct.product.fullName, 50)}</span>
						</td>
						<td>
							<input type="text" name="promotionProducts[${promoteproduct_index}].quantity" style="width:50px" value="${promoteproduct.quantity}" \/>
						</td>
						<td>
							<input type="text" name="promotionProducts[${promoteproduct_index}].price" style="width:50px" value="${promoteproduct.price}" \/>
						</td>
						<td>
							<a href="${base}${promoteproduct.product.path}" target="_blank">[${message("admin.common.view")}]</a>
							<a href="javascript:;" class="deleteProduct">[${message("admin.common.delete")}]</a>
						</td>
					</tr>
				[/#list]
			</table>
		</div>
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